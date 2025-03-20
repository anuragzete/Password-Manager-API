package com.project.passwordManager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final Properties props = new Properties();

    static {
        try (InputStream input = MongoDBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath.");
            }

            props.load(input);

            String mongoUri = props.getProperty("MONGO_URI");
            String dbName = props.getProperty("DB_NAME");

            if (mongoUri == null || dbName == null) {
                throw new RuntimeException("MONGO_URI or DB_NAME is missing in config.properties.");
            }

            mongoClient = MongoClients.create(mongoUri);
            database = mongoClient.getDatabase(dbName);

            System.out.println("✅ Connected to MongoDB: " + dbName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize MongoDB connection: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("MongoDB database is not initialized.");
        }
        return database;
    }

    public static synchronized void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("🛑 MongoDB connection closed.");
        }
    }
}

