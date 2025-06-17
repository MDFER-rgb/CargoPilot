// src/main/java/com/fasttracklogistics/dao/DatabaseConnection.java (UPDATED - Now uses MySQL and handles schema evolution)
package com.fasttracklogistics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for managing database connections.
 * This class handles establishing and closing connections to the MySQL database,
 * and ensures table schema is up-to-date on application startup.
 */
public class DatabaseConnection {

    // Database connection parameters for MySQL
    // IMPORTANT: Replace with your actual MySQL database URL, username, and password
    // ADDED allowPublicKeyRetrieval=true TO ADDRESS "Public Key Retrieval is not allowed" ERROR
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fasttrack_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = ""; // Replace with your MySQL password

    // JDBC Driver name for MySQL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Establishes and returns a connection to the database.
     *
     * @return A valid database Connection object.
     * @throws SQLException If a database access error occurs or the driver cannot be found.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the JDBC driver
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please ensure the driver is in your classpath.");
            throw new SQLException("JDBC Driver not found", e);
        }
        // Establish the connection
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Closes the given Statement, ResultSet, and Connection objects safely.
     *
     * @param conn The Connection to close (can be null).
     * @param stmt The Statement to close (can be null).
     * @param rs   The ResultSet to close (can be null).
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Initializes the database by creating tables if they don't exist,
     * and adding missing columns to existing tables.
     * This method should be called once at application startup.
     */
    public static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();

            // Shipments Table
            // Full DDL including all expected columns for initial creation
            stmt.execute("CREATE TABLE IF NOT EXISTS Shipments (" +
                    "    shipment_id VARCHAR(50) PRIMARY KEY," +
                    "    tracking_number VARCHAR(100) UNIQUE NOT NULL," +
                    "    sender_name VARCHAR(255) NOT NULL," +
                    "    sender_address VARCHAR(255) NOT NULL," +
                    "    sender_contact VARCHAR(20), " +
                    "    receiver_name VARCHAR(255) NOT NULL," +
                    "    receiver_address VARCHAR(255) NOT NULL," +
                    "    receiver_contact VARCHAR(20), " +
                    "    package_contents TEXT," +
                    "    package_type VARCHAR(50)," +
                    "    weight_kg DECIMAL(10, 2)," +
                    "    dimensions_cm VARCHAR(50)," +
                    "    current_location VARCHAR(255)," +
                    "    route VARCHAR(255)," +
                    "    is_urgent BOOLEAN DEFAULT FALSE," +
                    "    current_status VARCHAR(50) NOT NULL DEFAULT 'Pending'," +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ");");

            // Add columns if they don't exist (for schema evolution on existing databases)
            addMissingColumn(stmt, "Shipments", "sender_contact", "VARCHAR(20)");
            addMissingColumn(stmt, "Shipments", "receiver_contact", "VARCHAR(20)");
            addMissingColumn(stmt, "Shipments", "package_contents", "TEXT"); // Added for completeness if it was missing
            addMissingColumn(stmt, "Shipments", "package_type", "VARCHAR(50)");
            addMissingColumn(stmt, "Shipments", "weight_kg", "DECIMAL(10, 2)");
            addMissingColumn(stmt, "Shipments", "dimensions_cm", "VARCHAR(50)");
            addMissingColumn(stmt, "Shipments", "current_location", "VARCHAR(255)");
            addMissingColumn(stmt, "Shipments", "route", "VARCHAR(255)");
            addMissingColumn(stmt, "Shipments", "is_urgent", "BOOLEAN DEFAULT FALSE");


            // DeliveryPersonnel Table
            stmt.execute("CREATE TABLE IF NOT EXISTS DeliveryPersonnel (" +
                    "    personnel_id VARCHAR(50) PRIMARY KEY," +
                    "    employee_id VARCHAR(100) UNIQUE NOT NULL," +
                    "    name VARCHAR(255) NOT NULL," +
                    "    contact_number VARCHAR(20) NOT NULL," +
                    "    email VARCHAR(255)," +
                    "    vehicle_type VARCHAR(50)," +
                    "    license_number VARCHAR(50)," + // Added license_number
                    "    availability_status VARCHAR(50) NOT NULL DEFAULT 'Available'" +
                    ");");
            // Add license_number column if it doesn't exist
            addMissingColumn(stmt, "DeliveryPersonnel", "license_number", "VARCHAR(50)");


            // Deliveries Table
            stmt.execute("CREATE TABLE IF NOT EXISTS Deliveries (" +
                    "    delivery_id VARCHAR(50) PRIMARY KEY," +
                    "    shipment_id VARCHAR(50) UNIQUE NOT NULL," +
                    "    personnel_id VARCHAR(50)," +
                    "    scheduled_date DATE NOT NULL," +
                    "    scheduled_time_slot VARCHAR(50)," +
                    "    actual_delivery_date DATETIME," +
                    "    delivery_status VARCHAR(50) NOT NULL DEFAULT 'Scheduled'," +
                    "    estimated_arrival_time DATETIME," +
                    "    delay_reason TEXT," +
                    "    FOREIGN KEY (shipment_id) REFERENCES Shipments(shipment_id) ON DELETE CASCADE," +
                    "    FOREIGN KEY (personnel_id) REFERENCES DeliveryPersonnel(personnel_id) ON DELETE SET NULL" +
                    ");");

            // Notifications Table
            stmt.execute("CREATE TABLE IF NOT EXISTS Notifications (" +
                    "    notification_id VARCHAR(50) PRIMARY KEY," +
                    "    recipient_type VARCHAR(20) NOT NULL," +
                    "    recipient_id VARCHAR(50) NOT NULL," +
                    "    message TEXT NOT NULL," +
                    "    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "    status VARCHAR(50) DEFAULT 'Generated'," +
                    "    is_urgent BOOLEAN DEFAULT FALSE" + // is_urgent column
                    ");");
            // Add is_urgent column if it doesn't exist
            addMissingColumn(stmt, "Notifications", "is_urgent", "BOOLEAN DEFAULT FALSE");


            System.out.println("Database tables checked/created successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    /**
     * Helper method to add a column to a table if it doesn't already exist.
     * This prevents errors when running the application on an already existing database schema.
     * @param stmt The Statement object.
     * @param tableName The name of the table.
     * @param columnName The name of the column to add.
     * @param columnDefinition The SQL definition of the column (e.g., "VARCHAR(255)").
     */
    private static void addMissingColumn(Statement stmt, String tableName, String columnName, String columnDefinition) {
        try {
            // Check if the column exists by querying INFORMATION_SCHEMA or by attempting to select it
            // For simplicity and cross-DBMS compatibility in basic cases,
            // we'll rely on catching the Duplicate column error (SQLSTATE 42S21 for MySQL)
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
            System.out.println("Column " + columnName + " added to table " + tableName + ".");
        } catch (SQLException e) {
            // Error code 1060 is "Duplicate column name" in MySQL
            // SQLSTATE 42S21 is standard for "Undefined column" but MySQL gives 1060 for "Duplicate column name" on ADD
            if (e.getErrorCode() == 1060 || "42S21".equals(e.getSQLState())) {
                System.out.println("Column " + columnName + " already exists in table " + tableName + ". Skipping.");
            } else {
                System.err.println("Error adding column " + columnName + " to table " + tableName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}