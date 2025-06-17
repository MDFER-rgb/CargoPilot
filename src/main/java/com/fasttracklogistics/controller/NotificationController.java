// src/main/java/com/fasttracklogistics/controller/NotificationController.java (UPDATED)
package com.fasttracklogistics.controller;

import com.fasttracklogistics.dao.NotificationDAO;
import com.fasttracklogistics.dao.NotificationDAOImpl;
import com.fasttracklogistics.model.Notification;
import com.fasttracklogistics.view.CustomerNotificationPanel;
import com.fasttracklogistics.view.PersonnelNotificationPanel;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing notifications sent to customers and delivery personnel.
 * This class handles the logic for generating, sending (simulated), and logging notifications.
 */
public class NotificationController {

    private CustomerNotificationPanel customerView;
    private PersonnelNotificationPanel personnelView;
    private NotificationDAO notificationDAO;

    // Formatter for display timestamps - already exists in views, removed here to avoid duplication.

    public NotificationController(CustomerNotificationPanel customerView, PersonnelNotificationPanel personnelView) {
        this.customerView = customerView;
        this.personnelView = personnelView;
        this.notificationDAO = new NotificationDAOImpl();

        // Attach action listeners to refresh buttons
        this.customerView.getRefreshButton().addActionListener(e -> loadCustomerNotifications());
        // For personnel notifications, listen to both refresh button and urgent filter checkbox
        this.personnelView.getRefreshButton().addActionListener(e -> loadPersonnelNotifications());
        this.personnelView.getUrgentFilterCheckBox().addActionListener(e -> loadPersonnelNotifications());


        // Load initial data for both panels
        loadCustomerNotifications();
        loadPersonnelNotifications();
    }

    /**
     * Sends (logs and stores) a notification intended for a customer.
     * The `recipientId` for CUSTOMER type notifications will be the `shipmentId`.
     * @param shipmentId The ID of the shipment related to the notification (used as recipientId).
     * @param message The message content for the notification.
     * @param isUrgent Indicates if the notification is urgent (this flag is stored but might not affect customer display directly).
     */
    public void sendCustomerNotification(String shipmentId, String message, boolean isUrgent) {
        Notification notification = new Notification();
        notification.setRecipientType("CUSTOMER");
        notification.setRecipientId(shipmentId); // Recipient ID for customer is the shipment ID
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setStatus("SENT"); // Simulate immediate sending for now
        notification.setUrgent(isUrgent); // Set urgency

        try {
            boolean success = notificationDAO.insertNotification(notification);
            if (success) {
                System.out.println("Customer notification generated and logged: " + message);
                // After successful insert, reload the customer notifications to update the table
                loadCustomerNotifications();
            } else {
                System.err.println("Failed to insert customer notification into DB.");
            }
        } catch (SQLException ex) {
            System.err.println("Database error sending customer notification: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends (logs and stores) a notification intended for delivery personnel.
     * The `recipientId` for PERSONNEL type notifications can be the `personnelId`
     * or the `relatedEntityId` if it's a general broadcast (e.g., for urgent shipment).
     * @param personnelId The ID of the personnel (can be null if it's a general urgent broadcast).
     * @param relatedEntityId The ID of the related entity (e.g., shipmentId for urgent shipment).
     * @param message The message content for the notification.
     * @param isUrgent Indicates if the notification is urgent.
     */
    public void sendPersonnelNotification(String personnelId, String relatedEntityId, String message, boolean isUrgent) {
        Notification notification = new Notification();
        notification.setRecipientType("PERSONNEL");
        // For personnel notifications, recipientId can be the personnel's ID or the related entity's ID if it's a general broadcast
        notification.setRecipientId(personnelId != null ? personnelId : relatedEntityId);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setStatus("SENT"); // Simulate immediate sending for now
        notification.setUrgent(isUrgent); // Set urgency

        try {
            boolean success = notificationDAO.insertNotification(notification);
            if (success) {
                System.out.println("Personnel notification generated and logged: " + message);
                // After successful insert, reload the personnel notifications to update the table
                loadPersonnelNotifications();
            } else {
                System.err.println("Failed to insert personnel notification into DB.");
            }
        } catch (SQLException ex) {
            System.err.println("Database error sending personnel notification: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads and displays all customer notifications from the database.
     */
    public void loadCustomerNotifications() {
        try {
            List<Notification> notifications = notificationDAO.findNotificationsByRecipientType("CUSTOMER");
            customerView.displayNotifications(notifications);
        } catch (SQLException ex) {
            customerView.showErrorMessage("Error loading customer notifications: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads all personnel notifications from the database and displays them in the personnel notifications panel.
     * This method now also supports filtering by urgent status.
     */
    public void loadPersonnelNotifications() {
        try {
            List<Notification> notifications;
            // Apply filter based on checkbox state
            if (personnelView.getUrgentFilterCheckBox().isSelected()) {
                notifications = notificationDAO.findNotificationsByUrgency(true); // Fetch only urgent
            } else {
                notifications = notificationDAO.findNotificationsByRecipientType("PERSONNEL"); // Fetch all personnel notifications
            }
            personnelView.displayNotifications(notifications);
        } catch (SQLException ex) {
            personnelView.showErrorMessage("Error loading personnel notifications: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Refreshes all notifications in both panels.
     */
    public void refreshAllNotifications() {
        loadCustomerNotifications();
        loadPersonnelNotifications();
    }
}