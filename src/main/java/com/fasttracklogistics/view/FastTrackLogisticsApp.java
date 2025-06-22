// src/main/java/com/fasttracklogistics/view/FastTrackLogisticsApp.java (MODIFIED to include Reports Panel)
package com.fasttracklogistics.view;

import com.fasttracklogistics.controller.ShipmentController;
import com.fasttracklogistics.controller.DeliveryPersonnelController;
import com.fasttracklogistics.controller.ScheduleDeliveryController;
import com.fasttracklogistics.controller.NotificationController;
import com.fasttracklogistics.controller.TrackShipmentsController;
import com.fasttracklogistics.controller.AssignDriversController;
import com.fasttracklogistics.controller.ReportController; // NEW: Import ReportController
import com.fasttracklogistics.service.ReportService; // NEW: Import ReportService
import com.fasttracklogistics.dao.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Main application frame for the FastTrack Logistics Management System.
 * This class serves as the entry point and holds the main navigation
 * between different functional panels (Views).
 */
public class FastTrackLogisticsApp extends JFrame {

    private JTabbedPane tabbedPane;

    public FastTrackLogisticsApp() {
        setTitle("FastTrack Logistics Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialize the database connection and create tables on startup
        DatabaseConnection.initializeDatabase();

        // Set FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set FlatLaf LookAndFeel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                System.err.println("Failed to set System LookAndFeel: " + ex.getMessage());
            }
        }


        // Create a tabbed pane to hold different functional panels
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // --- Instantiate Notification Panels and Controller FIRST ---
        CustomerNotificationPanel customerNotificationPanel = new CustomerNotificationPanel();
        PersonnelNotificationPanel personnelNotificationPanel = new PersonnelNotificationPanel();
        NotificationController notificationController = new NotificationController(customerNotificationPanel, personnelNotificationPanel);


        // --- Add Shipment Management Panel ---
        ManageShipmentsPanel manageShipmentsPanel = new ManageShipmentsPanel();
        new ShipmentController(manageShipmentsPanel, notificationController);
        tabbedPane.addTab("Manage Shipments", manageShipmentsPanel);
        tabbedPane.setMnemonicAt(0, java.awt.event.KeyEvent.VK_S);

        // --- Add Delivery Personnel Management Panel ---
        ManageDeliveryPersonnelPanel managePersonnelPanel = new ManageDeliveryPersonnelPanel();
        new DeliveryPersonnelController(managePersonnelPanel);
        tabbedPane.addTab("Manage Personnel", managePersonnelPanel);
        tabbedPane.setMnemonicAt(1, java.awt.event.KeyEvent.VK_P);

        // --- Add Schedule Deliveries Panel ---
        ScheduleDeliveriesPanel scheduleDeliveriesPanel = new ScheduleDeliveriesPanel();
        new ScheduleDeliveryController(scheduleDeliveriesPanel, notificationController);
        tabbedPane.addTab("Schedule Deliveries", scheduleDeliveriesPanel);
        tabbedPane.setMnemonicAt(2, java.awt.event.KeyEvent.VK_D);


        // --- Add Track Shipment Progress Panel ---
        TrackShipmentsPanel trackShipmentsPanel = new TrackShipmentsPanel();
        new TrackShipmentsController(trackShipmentsPanel);
        tabbedPane.addTab("Track Shipments", trackShipmentsPanel);
        tabbedPane.setMnemonicAt(3, java.awt.event.KeyEvent.VK_T);

        // --- Add Assign Drivers Panel ---
        AssignDriversPanel assignDriversPanel = new AssignDriversPanel();
        new AssignDriversController(assignDriversPanel, notificationController);
        tabbedPane.addTab("Assign Drivers", assignDriversPanel);
        tabbedPane.setMnemonicAt(4, java.awt.event.KeyEvent.VK_A);

        // --- Add Monthly Reports Panel (Implemented) ---
        ReportsPanel reportsPanel = new ReportsPanel();
        ReportService reportService = new ReportService(); // Instantiate the service
        new ReportController(reportsPanel, reportService); // Pass view and service
        tabbedPane.addTab("Reports", reportsPanel);
        tabbedPane.setMnemonicAt(5, java.awt.event.KeyEvent.VK_R);


        // --- Add Customer Notifications Panel ---
        tabbedPane.addTab("Customer Notifications", customerNotificationPanel);
        tabbedPane.setMnemonicAt(6, java.awt.event.KeyEvent.VK_C);
        // --- Add Personnel Notifications Panel ---
        tabbedPane.addTab("Personnel Notifications", personnelNotificationPanel);
        tabbedPane.setMnemonicAt(7, java.awt.event.KeyEvent.VK_N);

        // Add a window listener to ensure resources are closed on application exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Perform any cleanup if necessary before exiting
                // For this app, closing resources is handled within DAOs after each operation.
                // However, if you had global resources (e.g., connection pools), you'd close them here.
                System.out.println("Application closing. Goodbye!");
            }
        });
    }

    public static void main(String[] args) {
        // Ensure Swing UI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            FastTrackLogisticsApp app = new FastTrackLogisticsApp();
            app.setVisible(true);
        });
    }
}