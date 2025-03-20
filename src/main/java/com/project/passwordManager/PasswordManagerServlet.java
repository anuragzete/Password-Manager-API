package com.project.passwordManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/passwords")
public class PasswordManagerServlet extends HttpServlet {

    private MongoCollection<Document> passwordCollection;
    private MongoCollection<Document> userCollection;

    @Override
    public void init() throws ServletException {
        try {
            // ✅ Use the shared MongoDB connection
            passwordCollection = MongoDBConnection.getDatabase().getCollection("passwords");
            userCollection = MongoDBConnection.getDatabase().getCollection("users");

            System.out.println("✅ PasswordManagerServlet initialized with shared MongoDB connection.");
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MongoDB collections", e);
        }
    }

    // 💾 Handle Adding Passwords
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try (BufferedReader reader = request.getReader()) {
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonInput.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonInput.toString());

            String userId = jsonObject.getString("user_id");
            String siteName = jsonObject.getString("site_name");
            String username = jsonObject.getString("username");
            String encryptedPassword = jsonObject.getString("password");

            // ✅ Create password document
            Document passwordDoc = new Document("userId", userId)
                    .append("site", siteName)
                    .append("username", username)
                    .append("password", encryptedPassword)
                    .append("createdAt", System.currentTimeMillis());

            // ✅ Insert password document into collection
            passwordCollection.insertOne(passwordDoc);

            // ✅ Update user's password counter
            Bson filter = Filters.eq("userId", userId);
            Bson update = Updates.inc("passwordCounter", 1);
            userCollection.updateOne(filter, update, new com.mongodb.client.model.UpdateOptions().upsert(true));

            // ✅ Send success response
            response.getWriter().write(new JSONObject().put("status", "success").toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    // 🔍 Retrieve Passwords
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String userId = request.getParameter("user_id");
        if (userId == null) {
            response.getWriter().write(new JSONObject().put("error", "User ID required").toString());
            return;
        }

        JSONArray passwordsArray = new JSONArray();

        try {
            // ✅ Find all passwords for the given user
            for (Document doc : passwordCollection.find(Filters.eq("userId", userId))) {
                JSONObject obj = new JSONObject();
                obj.put("site_name", doc.getString("site"));
                obj.put("username", doc.getString("username"));
                obj.put("password", doc.getString("password"));
                obj.put("createdAt", doc.getLong("createdAt"));
                passwordsArray.put(obj);
            }

            JSONObject result = new JSONObject();
            result.put("passwords", passwordsArray);
            response.getWriter().write(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    @Override
    public void destroy() {
        // ✅ No need to close MongoDB connection here since it's handled by the singleton class
        System.out.println("🛑 PasswordManagerServlet destroyed.");
    }
}
