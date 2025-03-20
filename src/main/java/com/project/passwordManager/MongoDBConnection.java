package com.project.passwordManager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.ServletException;

import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final Properties props = new Properties();

    static {
        try (InputStream input = MongoDBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                if (input != null) {
                    props.load(input);
                    System.out.println("✅ MONGO_URI: " + props.getProperty("MONGO_URI"));
                    System.out.println("✅ DB_NAME: " + props.getProperty("DB_NAME"));

                    String mongoUri = props.getProperty("MONGO_URI");
                    String dbName = props.getProperty("DB_NAME");

                    mongoClient = MongoClients.create(mongoUri);

                    if (mongoClient == null) {
                        throw new ServletException("❌ Failed to connect to MongoDB client.");
                    }

                    database = mongoClient.getDatabase(dbName);

                    if (database == null) {
                        throw new ServletException("❌ Failed to connect to MongoDB database: " + dbName);
                    }

                    System.out.println("✅ Connected to database: " + dbName);

                } else {
                    throw new ServletException("❌ config.properties not found");
                }
            } else {
                System.err.println("❌ config.properties not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize MongoDB connection: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("🛑 MongoDB connection closed.");
        }
    }
}
