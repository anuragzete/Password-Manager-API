package com.project.passwordManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private MongoCollection<Document> userCollection;

    @Override
    public void init() throws ServletException {
        try {
            userCollection = MongoDBConnection.getDatabase().getCollection("userPasswords");
            System.out.println("AuthServlet initialized with shared MongoDB connection.");
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MongoDB collections", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Action parameter is required").toString());
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(request.getInputStream()));

            switch (action) {
                case "signup":
                    handleSignup(jsonObject, response);
                    break;
                case "signin":
                    handleSignin(jsonObject, response);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(new JSONObject().put("error", "Invalid action").toString());
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    private void handleSignup(JSONObject jsonObject, HttpServletResponse response) throws IOException {
        String username = jsonObject.optString("username", "").trim();
        String password = jsonObject.optString("encryptedPassword", "").trim();

        if (username.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Username and password are required").toString());
            return;
        }

        if (userCollection.find(Filters.eq("username", username)).first() != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(new JSONObject().put("error", "User already exists").toString());
            return;
        }

        Document newUser = new Document("username", username)
                .append("password", password)
                .append("passwordCounter", 0)
                .append("passwords", new ArrayList<>());

        userCollection.insertOne(newUser);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(new JSONObject().put("status", "success")
                .put("userId", newUser.getObjectId("_id").toString())
                .toString());
    }

    private void handleSignin(JSONObject jsonObject, HttpServletResponse response) throws IOException {
        String username = jsonObject.optString("username", "").trim();
        String password = jsonObject.optString("encryptedPassword", "").trim();

        if (username.isEmpty() || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Username and password are required").toString());
            return;
        }

        Document userDoc = userCollection.find(Filters.eq("username", username))
                .projection(Projections.include("_id", "password"))
                .first();

        if (userDoc != null && password.equals(userDoc.getString("password"))) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(new JSONObject()
                    .put("status", "success")
                    .put("userId", userDoc.getObjectId("_id").toString())
                    .toString());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new JSONObject().put("error", "Invalid credentials").toString());
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthServlet destroyed.");
    }
}
