package com.Sharvari.elearningPlatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    //create database in MySQL first
    private static final String URL      = "jdbc:mysql://localhost:3306/elearning_platform";
    private static final String USER     = "root";
    private static final String PASSWORD = "S..";


    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected.");
            }
        } catch (SQLException e) {
            System.err.println("❌ DB connection failed: " + e.getMessage());
            System.err.println("Make sure MySQL is running and credentials are correct.");
            System.exit(1);
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
