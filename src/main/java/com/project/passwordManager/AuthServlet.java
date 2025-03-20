package com.project.passwordManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private MongoCollection<Document> userCollection;

    @Override
    public void init() throws ServletException {
        try {
            // ✅ Use shared MongoDB connection
            userCollection = MongoDBConnection.getDatabase().getCollection("users");

            System.out.println("✅ AuthServlet initialized with shared MongoDB connection.");
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MongoDB collections", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String action = request.getParameter("action");  // 'signup' or 'signin'

        try (BufferedReader reader = request.getReader()) {
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonInput.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonInput.toString());

            if ("signup".equals(action)) {
                handleSignup(jsonObject, response);
            } else if ("signin".equals(action)) {
                handleSignin(jsonObject, response);
            } else {
                response.getWriter().write(new JSONObject().put("error", "Invalid action").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    // 🔥 Handle Signup
    private void handleSignup(JSONObject jsonObject, HttpServletResponse response) throws IOException {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        // ✅ Check if user already exists
        if (userCollection.find(Filters.eq("username", username)).first() != null) {
            response.getWriter().write(new JSONObject().put("error", "User already exists").toString());
            return;
        }

        // ✅ Add new user to collection
        Document newUser = new Document("username", username)
                .append("password", password)
                .append("createdAt", System.currentTimeMillis())
                .append("passwordCounter", 0);

        userCollection.insertOne(newUser);

        response.getWriter().write(new JSONObject().put("status", "User registered successfully").toString());
    }

    // 🔥 Handle Signin
    private void handleSignin(JSONObject jsonObject, HttpServletResponse response) throws IOException {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        Document userDoc = userCollection.find(Filters.eq("username", username)).first();

        if (userDoc != null && password.equals(userDoc.getString("password"))) {
            response.getWriter().write(new JSONObject()
                    .put("status", "success")
                    .put("userId", userDoc.getObjectId("_id").toString())
                    .toString());
        } else {
            response.getWriter().write(new JSONObject().put("error", "Invalid credentials").toString());
        }
    }

    @Override
    public void destroy() {
        System.out.println("🛑 AuthServlet destroyed.");
    }
}
