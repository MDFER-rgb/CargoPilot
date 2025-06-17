// src/main/java/com/fasttracklogistics/dao/NotificationDAOImpl.java (UPDATED)
package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs

/**
 * JDBC implementation of the NotificationDAO interface.
 * Handles database operations for Notification entities.
 */
public class NotificationDAOImpl implements NotificationDAO {

    /**
     * Inserts a new notification record into the database.
     * A unique ID is generated for the new notification.
     * @param notification The Notification object to insert.
     * @return true if the notification was successfully inserted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean insertNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO Notifications (notification_id, recipient_type, recipient_id, message, timestamp, status, is_urgent) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Generate a unique ID for the notification if not already set
        if (notification.getNotificationId() == null || notification.getNotificationId().isEmpty()) {
            notification.setNotificationId("NOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        // Set timestamp if not already set
        if (notification.getTimestamp() == null) {
            notification.setTimestamp(LocalDateTime.now());
        }
        // Set default status if not already set
        if (notification.getStatus() == null || notification.getStatus().isEmpty()) {
            notification.setStatus("GENERATED");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notification.getNotificationId());
            stmt.setString(2, notification.getRecipientType());
            stmt.setString(3, notification.getRecipientId());
            stmt.setString(4, notification.getMessage());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setString(6, notification.getStatus());
            stmt.setBoolean(7, notification.isUrgent()); // Set is_urgent

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves a notification record by its ID.
     * @param notificationId The ID of the notification to retrieve.
     * @return The Notification object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Notification findNotificationById(String notificationId) throws SQLException {
        String sql = "SELECT * FROM Notifications WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notificationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing notification record in the database.
     * @param notification The Notification object with updated information.
     * @return true if the notification was successfully updated, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean updateNotification(Notification notification) throws SQLException {
        String sql = "UPDATE Notifications SET recipient_type = ?, recipient_id = ?, message = ?, timestamp = ?, status = ?, is_urgent = ? WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notification.getRecipientType());
            stmt.setString(2, notification.getRecipientId());
            stmt.setString(3, notification.getMessage());
            stmt.setTimestamp(4, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setString(5, notification.getStatus());
            stmt.setBoolean(6, notification.isUrgent()); // Update is_urgent
            stmt.setString(7, notification.getNotificationId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes a notification record from the database by its ID.
     * @param notificationId The ID of the notification to delete.
     * @return true if the notification was successfully deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean deleteNotification(String notificationId) throws SQLException {
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notificationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves all notification records from the database.
     * @return A list of all Notification objects.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Notification> findAllNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications ORDER BY timestamp DESC"; // Order by most recent
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        }
        return notifications;
    }

    /**
     * Retrieves notifications for a specific recipient type.
     * @param recipientType The type of recipient (e.g., "CUSTOMER", "PERSONNEL").
     * @return A list of Notification objects for the specified recipient type.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Notification> findNotificationsByRecipientType(String recipientType) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE recipient_type = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipientType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    /**
     * Retrieves notifications for a specific recipient ID.
     * @param recipientId The ID of the recipient (e.g., shipmentId, personnelId).
     * @return A list of Notification objects for the given recipient ID.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Notification> findNotificationsByRecipientId(String recipientId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE recipient_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    /**
     * Retrieves notifications based on whether they are urgent or not.
     * @param isUrgent true to get urgent notifications, false for non-urgent.
     * @return A list of Notification objects matching the urgency status.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<Notification> findNotificationsByUrgency(boolean isUrgent) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE is_urgent = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isUrgent);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    /**
     * Helper method to map a ResultSet row to a Notification object.
     * @param rs The ResultSet containing notification data.
     * @return A Notification object.
     * @throws SQLException If a database access error occurs.
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getString("notification_id"));
        notification.setRecipientType(rs.getString("recipient_type"));
        notification.setRecipientId(rs.getString("recipient_id"));
        notification.setMessage(rs.getString("message"));
        notification.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        notification.setStatus(rs.getString("status"));
        notification.setUrgent(rs.getBoolean("is_urgent")); // Get is_urgent
        return notification;
    }
}