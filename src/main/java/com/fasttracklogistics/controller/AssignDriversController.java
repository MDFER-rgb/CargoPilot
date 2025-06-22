// src/main/java/com/fasttracklogistics/controller/AssignDriversController.java (MODIFIED)
package com.fasttracklogistics.controller;

import com.fasttracklogistics.dao.DeliveryDAO;
import com.fasttracklogistics.dao.DeliveryDAOImpl;
import com.fasttracklogistics.dao.DeliveryPersonnelDAO;
import com.fasttracklogistics.dao.DeliveryPersonnelDAOImpl;
import com.fasttracklogistics.model.Delivery;
import com.fasttracklogistics.model.DeliveryPersonnel;
import com.fasttracklogistics.view.AssignDriversPanel;

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Added for notification messages
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing the assignment of drivers to shipments.
 * This class handles the logic for linking unassigned shipments with available personnel,
 * updating statuses, and displaying the results.
 */
public class AssignDriversController {

    private AssignDriversPanel view;
    private ShipmentDAO shipmentDAO;
    private DeliveryDAO deliveryDAO;
    private DeliveryPersonnelDAO personnelDAO;
    private NotificationController notificationController; // Inject NotificationController

    public AssignDriversController(AssignDriversPanel view, NotificationController notificationController) {
        this.view = view;
        this.shipmentDAO = new ShipmentDAOImpl();
        this.deliveryDAO = new DeliveryDAOImpl();
        this.personnelDAO = new DeliveryPersonnelDAOImpl();
        this.notificationController = notificationController; // Initialize NotificationController

        // Attach action listeners to buttons
        this.view.getAssignButton().addActionListener(e -> assignDriver());
        this.view.getRefreshUnassignedButton().addActionListener(e -> loadUnassignedShipments());
        this.view.getRefreshPersonnelButton().addActionListener(e -> loadAvailablePersonnel());
        this.view.getRefreshAssignedButton().addActionListener(e -> loadAssignedDeliveries());

        // Load initial data for all tables
        loadAllData();
    }

    /**
     * Loads all necessary data (unassigned shipments, available personnel, assigned deliveries)
     * and updates all tables in the view.
     */
    private void loadAllData() {
        loadUnassignedShipments();
        loadAvailablePersonnel();
        loadAssignedDeliveries();
    }

    /**
     * Retrieves unassigned shipments from the database and updates the view's table.
     * A shipment is considered "unassigned" if it has no associated delivery record,
     * or if its associated delivery record has a null `personnel_id`.
     */
    public void loadUnassignedShipments() {
        try {
            // Fetch all shipments
            List<Shipment> allShipments = shipmentDAO.findAllShipments();
            List<Shipment> unassignedShipments = new ArrayList<>();

            for (Shipment shipment : allShipments) {
                Delivery delivery = deliveryDAO.findDeliveryByShipmentId(shipment.getShipmentId());
                // If there's no delivery record, or if the delivery record exists but has no personnel assigned
                if (delivery == null || delivery.getPersonnelId() == null || delivery.getPersonnelId().isEmpty()) {
                    unassignedShipments.add(shipment);
                }
            }
            view.displayUnassignedShipments(unassignedShipments);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading unassigned shipments: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves all available delivery personnel from the database and updates the view's table.
     */
    public void loadAvailablePersonnel() {
        try {
            // For now, we load all personnel and let the view filter/display based on availability.
            // Or, we can use personnelDAO.findAvailablePersonnel() if a specific status is 'Available'
            List<DeliveryPersonnel> availablePersonnel = personnelDAO.findAvailablePersonnel(); // Using specific DAO method
            view.displayAvailablePersonnel(availablePersonnel);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading available personnel: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves all assigned deliveries (deliveries with a personnel ID) from the database
     * and updates the view's table, including associated shipment and personnel details.
     */
    public void loadAssignedDeliveries() {
        try {
            List<Delivery> allDeliveries = deliveryDAO.findAllDeliveries();
            List<Object[]> assignedDeliveryDetails = new ArrayList<>();

            for (Delivery delivery : allDeliveries) {
                // Only consider deliveries that have a personnel assigned
                if (delivery.getPersonnelId() != null && !delivery.getPersonnelId().isEmpty()) {
                    Shipment shipment = shipmentDAO.findShipmentById(delivery.getShipmentId());
                    DeliveryPersonnel personnel = personnelDAO.findPersonnelById(delivery.getPersonnelId());
                    if (shipment != null && personnel != null) { // Ensure both are found
                        assignedDeliveryDetails.add(new Object[]{delivery, shipment, personnel});
                    }
                }
            }
            view.displayAssignedDeliveries(assignedDeliveryDetails);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading assigned deliveries: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles the assignment of a selected driver to a selected unassigned shipment.
     * This involves creating or updating a Delivery record and updating related statuses.
     */
    private void assignDriver() {
        String selectedShipmentId = view.getSelectedUnassignedShipmentId();
        String selectedPersonnelId = view.getSelectedAvailablePersonnelId();

        if (selectedShipmentId == null) {
            view.showErrorMessage("Please select an unassigned shipment from the top-left table.");
            return;
        }
        if (selectedPersonnelId == null) {
            view.showErrorMessage("Please select an available personnel from the top-right table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Assign selected driver to selected shipment?", "Confirm Assignment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Shipment shipmentToAssign = shipmentDAO.findShipmentById(selectedShipmentId);
                DeliveryPersonnel personnelToAssign = personnelDAO.findPersonnelById(selectedPersonnelId);

                if (shipmentToAssign == null || personnelToAssign == null) {
                    view.showErrorMessage("Selected shipment or personnel not found. Please refresh and try again.");
                    return;
                }

                // Check if a delivery record already exists for this shipment
                Delivery existingDelivery = deliveryDAO.findDeliveryByShipmentId(shipmentToAssign.getShipmentId());
                boolean success = false;

                if (existingDelivery == null) {
                    // Create a new delivery record if none exists
                    Delivery newDelivery = new Delivery();
                    newDelivery.setDeliveryId("DEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Generate Delivery ID
                    newDelivery.setShipmentId(shipmentToAssign.getShipmentId());
                    newDelivery.setPersonnelId(personnelToAssign.getPersonnelId());
                    newDelivery.setScheduledDate(LocalDate.now()); // Default to today
                    newDelivery.setScheduledTimeSlot("Any Time"); // Default
                    newDelivery.setDeliveryStatus("Assigned"); // Initial status after assignment
                    newDelivery.setEstimatedArrivalTime(LocalDateTime.now().plusHours(24)); // Default 24 hrs
                    // Delay reason defaults to null
                    success = deliveryDAO.insertDelivery(newDelivery);
                } else {
                    // Update existing delivery record if it exists but was unassigned or needed status change
                    existingDelivery.setPersonnelId(personnelToAssign.getPersonnelId());
                    existingDelivery.setDeliveryStatus("Assigned"); // Update status
                    success = deliveryDAO.updateDelivery(existingDelivery);
                }

                if (success) {
                    // Update Shipment status
                    shipmentToAssign.setCurrentStatus("Assigned");
                    shipmentDAO.updateShipment(shipmentToAssign);

                    // Update Personnel status (e.g., to 'On Route' or 'Assigned')
                    personnelToAssign.setAvailabilityStatus("On Route"); // Or 'Assigned'
                    personnelDAO.updatePersonnel(personnelToAssign);

                    view.showMessage("Driver assigned successfully!");

                    // --- Send Notifications on Assignment ---
                    // Notify Customer
                    String customerMsg = String.format("Dear %s, your shipment '%s' is now assigned to our personnel %s for delivery. Status: %s. Est. Delivery: %s.",
                            shipmentToAssign.getReceiverName(), // Using Receiver Name as a placeholder for customer ID/name
                            shipmentToAssign.getTrackingNumber(),
                            personnelToAssign.getName(),
                            shipmentToAssign.getCurrentStatus(),
                            (existingDelivery != null && existingDelivery.getEstimatedArrivalTime() != null) ?
                                    existingDelivery.getEstimatedArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
                    // Pass shipment ID as the recipient ID for customer notifications
                    notificationController.sendCustomerNotification(shipmentToAssign.getShipmentId(), customerMsg, false);

                    // Notify Personnel
                    String personnelMsg = String.format("You have been assigned Shipment ID: %s (Tracking No: %s). Receiver: %s at %s. Status: %s. Please check your schedule.",
                            shipmentToAssign.getShipmentId(),
                            shipmentToAssign.getTrackingNumber(),
                            shipmentToAssign.getReceiverName(),
                            shipmentToAssign.getReceiverAddress(),
                            shipmentToAssign.getCurrentStatus());
                    // Pass personnel ID as the recipient ID for personnel notifications
                    notificationController.sendPersonnelNotification(personnelToAssign.getPersonnelId(), shipmentToAssign.getShipmentId(), personnelMsg, shipmentToAssign.isUrgent());

                    loadAllData(); // Refresh all tables
                } else {
                    view.showErrorMessage("Failed to assign driver. Database error or invalid data.");
                }

            } catch (SQLException ex) {
                view.showErrorMessage("Database error during assignment: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}