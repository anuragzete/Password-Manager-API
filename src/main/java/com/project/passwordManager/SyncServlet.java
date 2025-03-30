package com.project.passwordManager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/sync")
public class SyncServlet extends HttpServlet {

    private MongoCollection<Document> userCollection;

    @Override
    public void init() throws ServletException {
        try {
            userCollection = MongoDBConnection.getDatabase().getCollection("userPasswords");
            System.out.println("SyncServlet initialized.");
        } catch (Exception e) {
            throw new ServletException("Failed to initialize MongoDB connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try (BufferedReader reader = request.getReader()) {
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonInput.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonInput.toString());
            String userId = jsonObject.getString("user_id");

            JSONArray localPasswords = jsonObject.getJSONArray("passwords");

            Document userDoc = userCollection.find(Filters.eq("_id", userId)).first();

            if (userDoc != null) {
                JSONArray serverPasswords = new JSONArray(userDoc.get("passwords").toString());

                // Merge local and server passwords (conflict resolution)
                JSONArray mergedPasswords = mergePasswords(localPasswords, serverPasswords);

                // Update the database with the merged data
                Document updateDoc = new Document("passwords", mergedPasswords.toList());
                userCollection.updateOne(Filters.eq("_id", userId), new Document("$set", updateDoc));

                response.getWriter().write(new JSONObject().put("status", "Sync successful").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(new JSONObject().put("error", "User not found").toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject().put("error", e.getMessage()).toString());
        }
    }

    private JSONArray mergePasswords(JSONArray local, JSONArray server) {
        // Merging logic: Avoid duplicates, prioritize recent updates
        for (int i = 0; i < local.length(); i++) {
            JSONObject localPwd = local.getJSONObject(i);
            boolean exists = false;

            for (int j = 0; j < server.length(); j++) {
                JSONObject serverPwd = server.getJSONObject(j);

                if (localPwd.getString("site_name").equals(serverPwd.getString("site_name"))) {
                    // Conflict resolution: Prioritize latest password
                    serverPwd.put("password", localPwd.getString("password"));
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                server.put(localPwd);
            }
        }
        return server;
    }

    @Override
    public void destroy() {
        System.out.println("SyncServlet destroyed.");
    }
}
