// src/main/java/com/fasttracklogistics/view/PersonnelNotificationPanel.java (UPDATED - Now uses JTable with urgent filter)
package com.fasttracklogistics.view;

import com.fasttracklogistics.model.Notification; // Import Notification model

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

/**
 * JPanel for displaying delivery personnel notification logs.
 * This class represents a 'View' component for personnel notifications,
 * displaying them in a JTable and allowing filtering by urgent status.
 */
public class PersonnelNotificationPanel extends JPanel {

    private JTable notificationTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JCheckBox urgentFilterCheckBox; // NEW: Checkbox for filtering urgent notifications

    // Formatter for displaying notification timestamps
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PersonnelNotificationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Delivery Personnel Notification Log", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // --- Filter Panel (NEW) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgentFilterCheckBox = new JCheckBox("Show Only Urgent"); // NEW: Checkbox for urgent filter
        filterPanel.add(urgentFilterCheckBox);
        add(filterPanel, BorderLayout.PAGE_START); // Add filter panel at the very top (Page Start == North)


        // --- Notification Table ---
        // Changed column header for clarity: recipientId here can be Personnel ID or related entity ID
        String[] columnNames = {"Notification ID", "Recipient ID (Personnel/Related ID)", "Message", "Timestamp", "Status", "Urgent"}; // ADDED "Urgent" column
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getColumnCount() - 1) { // Last column is "Urgent"
                    return Boolean.class; // This will render as a checkbox
                }
                return super.getColumnClass(columnIndex);
            }
        };
        notificationTable = new JTable(tableModel);
        notificationTable.setFillsViewportHeight(true);
        notificationTable.setAutoCreateRowSorter(true);

        JScrollPane tableScrollPane = new JScrollPane(notificationTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // --- Refresh button for personnel notifications ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshButton = new JButton("Refresh Personnel Notifications");
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Displays a list of personnel notifications in the table.
     * Clears existing content and populates the table with new data.
     * @param notifications The list of Notification objects (recipientType="PERSONNEL") to display.
     */
    public void displayNotifications(List<Notification> notifications) {
        tableModel.setRowCount(0); // Clear existing data

        for (Notification notification : notifications) {
            Vector<Object> row = new Vector<>();
            row.add(notification.getNotificationId());
            row.add(notification.getRecipientId()); // This will be personnel ID or related entity ID
            row.add(notification.getMessage());
            row.add(notification.getTimestamp() != null ? notification.getTimestamp().format(DISPLAY_FORMATTER) : "");
            row.add(notification.getStatus());
            row.add(notification.isUrgent()); // NEW: Add urgent status
            tableModel.addRow(row);
        }
    }

    /**
     * Shows an informational message dialog.
     * @param message The message to display.
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Shows an error message dialog.
     * @param message The error message to display.
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Getter for the refresh button
    public JButton getRefreshButton() {
        return refreshButton;
    }

    // NEW: Getter for the urgent filter checkbox
    public JCheckBox getUrgentFilterCheckBox() {
        return urgentFilterCheckBox;
    }
}