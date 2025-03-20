package com.project.passwordManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.bson.types.ObjectId;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/passwords")
public class PasswordManagerServlet extends HttpServlet {

    private MongoCollection<Document> userCollection;

    @Override
    public void init() throws ServletException {
        try {
            userCollection = MongoDBConnection.getDatabase().getCollection("userPasswords");
            System.out.println("PasswordManagerServlet initialized with shared MongoDB connection.");
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MongoDB collections", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try (BufferedReader reader = request.getReader()) {
            StringBuilder jsonInput = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonInput.append(line);
            }

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(jsonInput.toString());
            } catch (JSONException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new JSONObject().put("error", "Invalid JSON format").toString());
                return;
            }

            String userId = jsonObject.optString("user_id", "").trim();
            String siteName = jsonObject.optString("site_name", "").trim();
            String password = jsonObject.optString("password", "").trim();

            if (userId.isEmpty() || siteName.isEmpty() || password.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new JSONObject().put("error", "Missing parameters").toString());
                return;
            }

            if (!ObjectId.isValid(userId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new JSONObject().put("error", "Invalid User ID").toString());
                return;
            }

            Bson filter = Filters.eq("_id", new ObjectId(userId));

            Bson updateExisting = Updates.set("passwords.$[elem].password", password);
            UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("elem.site", siteName)));

            UpdateResult result = userCollection.updateOne(filter, updateExisting, options);

            if (result.getMatchedCount() == 0) {
                Document newPassword = new Document("site", siteName).append("password", password);
                Bson addNewPassword = Updates.push("passwords", newPassword);

                userCollection.updateOne(filter, addNewPassword, new UpdateOptions().upsert(true));

                response.getWriter().write(new JSONObject().put("status", "New password added").toString());
            } else {
                response.getWriter().write(new JSONObject().put("status", "Password updated").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String userId = request.getParameter("user_id");

        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "User ID required").toString());
            return;
        }

        if (!ObjectId.isValid(userId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("error", "Invalid User ID format").toString());
            return;
        }

        try {
            Bson filter = Filters.eq("_id", new ObjectId(userId));

            Document userDoc = userCollection.find(filter)
                    .projection(Projections.include("passwords"))
                    .first();

            JSONArray passwordsArray = new JSONArray();

            if (userDoc != null && userDoc.containsKey("passwords")) {
                @SuppressWarnings("unchecked")
                List<Document> passwords = (List<Document>) userDoc.get("passwords");

                for (Document passwordDoc : passwords) {
                    JSONObject obj = new JSONObject();
                    obj.put("site_name", passwordDoc.getString("site"));
                    obj.put("password", passwordDoc.getString("password"));
                    passwordsArray.put(obj);
                }
            }

            JSONObject result = new JSONObject();
            result.put("passwords", passwordsArray);
            response.getWriter().write(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }


    @Override
    public void destroy() {
        try {
            MongoDBConnection.close();
            System.out.println("PasswordManagerServlet destroyed and MongoDB connection closed.");
        } catch (Exception e) {
            System.err.println("Failed to close MongoDB connection: " + e.getMessage());
        }
    }
}

