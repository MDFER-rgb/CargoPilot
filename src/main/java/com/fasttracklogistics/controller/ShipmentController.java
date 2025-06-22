// src/main/java/com/fasttracklogistics/controller/ShipmentController.java (UPDATED to pass isUrgent to notifications)

package com.fasttracklogistics.controller;

import com.fasttracklogistics.dao.DeliveryDAO;
import com.fasttracklogistics.dao.DeliveryDAOImpl;
import com.fasttracklogistics.model.Delivery;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs
import javax.swing.JOptionPane;

/**
 * Controller for managing shipment operations.
 * This class handles user interactions from ManageShipmentsPanel,
 * performs validation, and interacts with the ShipmentDAO.
 */
public class ShipmentController {

    private ManageShipmentsPanel view;
    private ShipmentDAO shipmentDAO;
    private DeliveryDAO deliveryDAO;
    private NotificationController notificationController;

    public ShipmentController(ManageShipmentsPanel view, NotificationController notificationController) {
        this.view = view;
        this.shipmentDAO = new ShipmentDAOImpl();
        this.deliveryDAO = new DeliveryDAOImpl();
        this.notificationController = notificationController;

        // Attach action listeners to buttons
        this.view.getAddButton().addActionListener(e -> addShipment());
        this.view.getUpdateButton().addActionListener(e -> updateShipment());
        this.view.getDeleteButton().addActionListener(e -> deleteShipment());
        this.view.getClearButton().addActionListener(e -> clearForm());
        this.view.getRefreshButton().addActionListener(e -> loadShipments());

        // Attach a listener to the table for row selection
        this.view.getShipmentTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getShipmentTable().getSelectedRow() != -1) {
                    loadSelectedShipmentToForm();
                }
            }
        });

        // Load initial data into the table when the controller is initialized
        loadShipments();
    }

    /**
     * Loads all shipment records (and their associated delivery details) from the database
     * and displays them in the view's table.
     */
    public void loadShipments() {
        try {
            List<Shipment> shipments = shipmentDAO.findAllShipments();
            List<Object[]> shipmentDetails = new ArrayList<>();

            for (Shipment shipment : shipments) {
                Delivery delivery = deliveryDAO.findDeliveryByShipmentId(shipment.getShipmentId());
                shipmentDetails.add(new Object[]{shipment, delivery});
            }
            view.displayShipments(shipmentDetails);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading shipments: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles adding a new shipment based on user input from the form.
     */
    private void addShipment() {
        // Retrieve data from view
        String trackingNumber = view.getTrackingNumber();
        String senderName = view.getSenderName();
        String senderAddress = view.getSenderAddress();
        String senderContact = view.getSenderContact();
        String receiverName = view.getReceiverName();
        String receiverAddress = view.getReceiverAddress();
        String receiverContact = view.getReceiverContact();
        String packageContents = view.getPackageContents();
        String packageType = view.getPackageType();
        String weightKgStr = view.getWeightKg();
        String dimensionsCm = view.getDimensionsCm();
        String currentLocation = view.getCurrentLocation();
        String route = view.getRoute();
        String currentStatus = view.getCurrentStatus();
        boolean isUrgent = view.getIsUrgent(); // Get isUrgent status from view

        // Basic validation
        if (trackingNumber.isEmpty() || senderName.isEmpty() || senderAddress.isEmpty() ||
                receiverName.isEmpty() || receiverAddress.isEmpty() || weightKgStr.isEmpty() ||
                packageType.isEmpty() || route.isEmpty() || senderContact.isEmpty() || receiverContact.isEmpty() ||
                packageContents.isEmpty() || dimensionsCm.isEmpty() || currentLocation.isEmpty()) {
            view.showErrorMessage("Please fill in all required fields.");
            return;
        }

        double weightKg;
        try {
            weightKg = Double.parseDouble(weightKgStr);
            if (weightKg <= 0) {
                view.showErrorMessage("Weight must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showErrorMessage("Invalid weight format. Please enter a number.");
            return;
        }

        // Create a new Shipment object (ID and dates will be set by DAO/DB)
        Shipment newShipment = new Shipment();
        newShipment.setTrackingNumber(trackingNumber);
        newShipment.setSenderName(senderName);
        newShipment.setSenderAddress(senderAddress);
        newShipment.setSenderContact(senderContact);
        newShipment.setReceiverName(receiverName);
        newShipment.setReceiverAddress(receiverAddress);
        newShipment.setReceiverContact(receiverContact);
        newShipment.setPackageContents(packageContents);
        newShipment.setPackageType(packageType);
        newShipment.setWeightKg(weightKg);
        newShipment.setDimensionsCm(dimensionsCm);
        newShipment.setCurrentLocation(currentLocation);
        newShipment.setRoute(route);
        newShipment.setCurrentStatus(currentStatus);
        newShipment.setUrgent(isUrgent); // Set the urgent status

        try {
            // Check if tracking number already exists
            if (shipmentDAO.findShipmentByTrackingNumber(trackingNumber) != null) {
                view.showErrorMessage("Tracking number already exists. Please use a unique tracking number.");
                return;
            }

            // Generate a unique Shipment ID
            newShipment.setShipmentId("SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // Set initial status if not already set (e.g., from ComboBox)
            if (newShipment.getCurrentStatus() == null || newShipment.getCurrentStatus().isEmpty()) {
                newShipment.setCurrentStatus("Pending");
            }

            boolean success = shipmentDAO.insertShipment(newShipment);
            if (success) {
                view.showMessage("Shipment added successfully!");
                // If urgent, send a notification to personnel
                if (newShipment.isUrgent()) {
                    String notificationMsg = String.format("URGENT: New shipment '%s' (%s) added on route '%s'.",
                            newShipment.getTrackingNumber(), newShipment.getPackageType(), newShipment.getRoute());
                    notificationController.sendPersonnelNotification(null, newShipment.getShipmentId(), notificationMsg, true); // Pass true for urgent
                }
                String customerNotificationMsg = String.format("Your shipment '%s' has been successfully created and is now Pending. Current Location: %s",
                        newShipment.getTrackingNumber(), newShipment.getCurrentLocation());
                notificationController.sendCustomerNotification(newShipment.getShipmentId(), customerNotificationMsg, false); // Pass false for non-urgent


                clearForm();
                loadShipments(); // Refresh table
            } else {
                view.showErrorMessage("Failed to add shipment.");
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error adding shipment: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles updating an existing shipment based on user input from the form.
     */
    private void updateShipment() {
        String shipmentId = view.getShipmentId();
        if (shipmentId.isEmpty()) {
            view.showErrorMessage("Please select a shipment to update from the table.");
            return;
        }

        // Retrieve existing shipment to check for changes
        Shipment originalShipment = null;
        try {
            originalShipment = shipmentDAO.findShipmentById(shipmentId);
        } catch (SQLException ex) {
            view.showErrorMessage("Error retrieving original shipment for update: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
        if (originalShipment == null) {
            view.showErrorMessage("Selected shipment not found for update.");
            return;
        }

        // Retrieve updated data from view
        String trackingNumber = view.getTrackingNumber();
        String senderName = view.getSenderName();
        String senderAddress = view.getSenderAddress();
        String senderContact = view.getSenderContact();
        String receiverName = view.getReceiverName();
        String receiverAddress = view.getReceiverAddress();
        String receiverContact = view.getReceiverContact();
        String packageContents = view.getPackageContents();
        String packageType = view.getPackageType();
        String weightKgStr = view.getWeightKg();
        String dimensionsCm = view.getDimensionsCm();
        String currentLocation = view.getCurrentLocation();
        String route = view.getRoute();
        String currentStatus = view.getCurrentStatus();
        boolean isUrgent = view.getIsUrgent(); // Get isUrgent status from view

        // Basic validation (similar to add, but for update)
        if (trackingNumber.isEmpty() || senderName.isEmpty() || senderAddress.isEmpty() ||
                receiverName.isEmpty() || receiverAddress.isEmpty() || weightKgStr.isEmpty() ||
                packageType.isEmpty() || route.isEmpty() || senderContact.isEmpty() || receiverContact.isEmpty() ||
                packageContents.isEmpty() || dimensionsCm.isEmpty() || currentLocation.isEmpty()) {
            view.showErrorMessage("Please ensure all required fields are filled.");
            return;
        }

        double weightKg;
        try {
            weightKg = Double.parseDouble(weightKgStr);
            if (weightKg <= 0) {
                view.showErrorMessage("Weight must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            view.showErrorMessage("Invalid weight format. Please enter a number.");
            return;
        }

        // Create Shipment object with updated data
        Shipment updatedShipment = new Shipment();
        updatedShipment.setShipmentId(shipmentId);
        updatedShipment.setTrackingNumber(trackingNumber);
        updatedShipment.setSenderName(senderName);
        updatedShipment.setSenderAddress(senderAddress);
        updatedShipment.setSenderContact(senderContact);
        updatedShipment.setReceiverName(receiverName);
        updatedShipment.setReceiverAddress(receiverAddress);
        updatedShipment.setReceiverContact(receiverContact);
        updatedShipment.setPackageContents(packageContents);
        updatedShipment.setPackageType(packageType);
        updatedShipment.setWeightKg(weightKg);
        updatedShipment.setDimensionsCm(dimensionsCm);
        updatedShipment.setCurrentLocation(currentLocation);
        updatedShipment.setRoute(route);
        updatedShipment.setCurrentStatus(currentStatus);
        updatedShipment.setUrgent(isUrgent); // Set the urgent status

        try {
            // Check if tracking number is being changed to an existing one (that is not this shipment's own)
            Shipment existingByTrackingNumber = shipmentDAO.findShipmentByTrackingNumber(trackingNumber);
            if (existingByTrackingNumber != null && !existingByTrackingNumber.getShipmentId().equals(shipmentId)) {
                view.showErrorMessage("Another shipment already uses this tracking number. Please use a unique tracking number.");
                return;
            }

            boolean success = shipmentDAO.updateShipment(updatedShipment);
            if (success) {
                view.showMessage("Shipment updated successfully!");

                // Check for status change and send customer notification
                if (!originalShipment.getCurrentStatus().equals(updatedShipment.getCurrentStatus())) {
                    String customerNotificationMsg = String.format("Your shipment '%s' status has changed to: %s. Current Location: %s",
                            updatedShipment.getTrackingNumber(), updatedShipment.getCurrentStatus(), updatedShipment.getCurrentLocation());
                    notificationController.sendCustomerNotification(updatedShipment.getShipmentId(), customerNotificationMsg, false); // Pass false for non-urgent
                }

                // Check for urgent status change and send personnel notification
                if (updatedShipment.isUrgent() && !originalShipment.isUrgent()) {
                    String personnelNotificationMsg = String.format("URGENT: Shipment '%s' (%s) is now marked urgent. Route: '%s'.",
                            updatedShipment.getTrackingNumber(), updatedShipment.getPackageType(), updatedShipment.getRoute());
                    notificationController.sendPersonnelNotification(null, updatedShipment.getShipmentId(), personnelNotificationMsg, true); // Pass true for urgent
                }


                clearForm();
                loadShipments(); // Refresh table
            } else {
                view.showErrorMessage("Failed to update shipment. Shipment ID might not exist.");
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error updating shipment: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles deleting a shipment based on the selected row in the table.
     */
    private void deleteShipment() {
        String shipmentId = view.getShipmentId();
        if (shipmentId.isEmpty()) {
            view.showErrorMessage("Please select a shipment to delete from the table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete shipment with ID: " + shipmentId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = shipmentDAO.deleteShipment(shipmentId);
                if (success) {
                    view.showMessage("Shipment deleted successfully!");
                    // Optionally, send a cancellation notification to the customer
                    // For now, we'll keep it simple and not send a notification on deletion
                    clearForm();
                    loadShipments(); // Refresh table
                } else {
                    view.showErrorMessage("Failed to delete shipment. Shipment ID not found.");
                }
            } catch (SQLException ex) {
                view.showErrorMessage("Database error deleting shipment: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Clears the input form fields in the view.
     */
    private void clearForm() {
        view.clearForm();
    }

    /**
     * Loads the details of the selected shipment from the table into the form fields.
     */
    private void loadSelectedShipmentToForm() {
        int selectedRow = view.getShipmentTable().getSelectedRow();
        if (selectedRow >= 0) {
            String shipmentId = (String) view.getShipmentTable().getModel().getValueAt(selectedRow, 0);
            try {
                Shipment selectedShipment = shipmentDAO.findShipmentById(shipmentId);
                Delivery associatedDelivery = null;
                if (selectedShipment != null) {
                    associatedDelivery = deliveryDAO.findDeliveryByShipmentId(selectedShipment.getShipmentId());
                }

                if (selectedShipment != null) {
                    // Pass both shipment and its associated delivery to populate the form
                    view.populateForm(selectedShipment, associatedDelivery);
                } else {
                    view.showErrorMessage("Could not find selected shipment in database.");
                    clearForm();
                }
            } catch (SQLException ex) {
                view.showErrorMessage("Error loading shipment details: " + ex.getMessage());
                ex.printStackTrace();
                clearForm();
            }
        }
    }
}