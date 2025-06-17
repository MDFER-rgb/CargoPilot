// src/main/java/com/fasttracklogistics/dao/NotificationDAO.java (UPDATED)
package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.Notification;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object (DAO) interface for Notification entities.
 * Defines the contract for CRUD operations on Notification records in the database.
 */
public interface NotificationDAO {
    /**
     * Inserts a new notification record into the database.
     * The notification ID will be auto-generated or set within the implementation.
     * @param notification The Notification object to insert.
     * @return true if the notification was successfully inserted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean insertNotification(Notification notification) throws SQLException;

    /**
     * Retrieves a notification record by its ID.
     * @param notificationId The ID of the notification to retrieve.
     * @return The Notification object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Notification findNotificationById(String notificationId) throws SQLException;

    /**
     * Updates an existing notification record in the database.
     * @param notification The Notification object with updated information.
     * @return true if the notification was successfully updated, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateNotification(Notification notification) throws SQLException;

    /**
     * Deletes a notification record from the database by its ID.
     * @param notificationId The ID of the notification to delete.
     * @return true if the notification was successfully deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean deleteNotification(String notificationId) throws SQLException;

    /**
     * Retrieves all notification records from the database.
     * @return A list of all Notification objects.
     * @throws SQLException If a database access error occurs.
     */
    List<Notification> findAllNotifications() throws SQLException;

    /**
     * Retrieves notifications for a specific recipient type.
     * @param recipientType The type of recipient (e.g., "CUSTOMER", "PERSONNEL").
     * @return A list of Notification objects for the specified recipient type.
     * @throws SQLException If a database access error occurs.
     */
    List<Notification> findNotificationsByRecipientType(String recipientType) throws SQLException;

    /**
     * Retrieves notifications for a specific recipient ID.
     * @param recipientId The ID of the recipient (e.g., shipmentId, personnelId).
     * @return A list of Notification objects for the given recipient ID.
     * @throws SQLException If a database access error occurs.
     */
    List<Notification> findNotificationsByRecipientId(String recipientId) throws SQLException;

    /**
     * Retrieves notifications based on whether they are urgent or not.
     * @param isUrgent true to get urgent notifications, false for non-urgent.
     * @return A list of Notification objects matching the urgency status.
     * @throws SQLException If a database access error occurs.
     */
    List<Notification> findNotificationsByUrgency(boolean isUrgent) throws SQLException;
}