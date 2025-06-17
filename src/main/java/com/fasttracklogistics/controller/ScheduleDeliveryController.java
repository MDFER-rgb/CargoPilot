// src/main/java/com/fasttracklogistics/controller/ScheduleDeliveryController.java (NEW FILE)
package com.fasttracklogistics.controller;

import com.fasttracklogistics.dao.DeliveryDAO;
import com.fasttracklogistics.dao.DeliveryDAOImpl;
import com.fasttracklogistics.dao.DeliveryPersonnelDAO;
import com.fasttracklogistics.dao.DeliveryPersonnelDAOImpl;
import com.fasttracklogistics.dao.ShipmentDAO;
import com.fasttracklogistics.dao.ShipmentDAOImpl;
import com.fasttracklogistics.model.Delivery;
import com.fasttracklogistics.model.DeliveryPersonnel;
import com.fasttracklogistics.model.Shipment;
import com.fasttracklogistics.view.ScheduleDeliveriesPanel;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing delivery scheduling operations.
 * This class handles user interactions from ScheduleDeliveriesPanel,
 * performs validation, and interacts with DAOs for Deliveries, Shipments, and DeliveryPersonnel.
 * It also coordinates with the NotificationController for sending alerts.
 */
public class ScheduleDeliveryController {

    private ScheduleDeliveriesPanel view;
    private ShipmentDAO shipmentDAO;
    private DeliveryDAO deliveryDAO;
    private DeliveryPersonnelDAO personnelDAO;
    private NotificationController notificationController; // Injected NotificationController

    public ScheduleDeliveryController(ScheduleDeliveriesPanel view, NotificationController notificationController) {
        this.view = view;
        this.shipmentDAO = new ShipmentDAOImpl();
        this.deliveryDAO = new DeliveryDAOImpl();
        this.personnelDAO = new DeliveryPersonnelDAOImpl();
        this.notificationController = notificationController; // Initialize NotificationController

        // Attach action listeners to buttons
        this.view.getScheduleButton().addActionListener(e -> scheduleDelivery());
        this.view.getUpdateButton().addActionListener(e -> updateDelivery());
        this.view.getDeleteButton().addActionListener(e -> deleteDelivery());
        this.view.getClearButton().addActionListener(e -> clearForm());
        this.view.getRefreshShipmentsButton().addActionListener(e -> loadUnscheduledShipments());
        this.view.getRefreshDeliveriesButton().addActionListener(e -> loadDeliveries());

        // Attach listeners to tables for row selection
        this.view.getUnscheduledShipmentsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getUnscheduledShipmentsTable().getSelectedRow() != -1) {
                    view.populateFormFromUnscheduledShipmentSelection(); // Helper in view to populate basic shipment data
                }
            }
        });

        this.view.getDeliveriesTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getDeliveriesTable().getSelectedRow() != -1) {
                    loadSelectedDeliveryToForm(); // Populate form from selected scheduled delivery
                }
            }
        });

        // Load initial data
        loadAllData();
    }

    /**
     * Loads all necessary data (unscheduled shipments, all deliveries, available personnel)
     * and updates all tables/combobox in the view.
     */
    private void loadAllData() {
        loadUnscheduledShipments();
        loadDeliveries();
        loadAvailablePersonnel();
    }

    /**
     * Loads unscheduled shipments into the top table.
     */
    public void loadUnscheduledShipments() {
        try {
            List<Shipment> allShipments = shipmentDAO.findAllShipments();
            List<Shipment> unscheduledShipments = new ArrayList<>();
            for (Shipment shipment : allShipments) {
                // A shipment is unscheduled if it has no associated delivery record
                if (deliveryDAO.findDeliveryByShipmentId(shipment.getShipmentId()) == null) {
                    unscheduledShipments.add(shipment);
                }
            }
            view.displayUnscheduledShipments(unscheduledShipments);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading unscheduled shipments: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads all deliveries (scheduled/assigned/etc.) into the bottom table.
     */
    public void loadDeliveries() {
        try {
            List<Delivery> allDeliveries = deliveryDAO.findAllDeliveries();
            List<Object[]> deliveryDetails = new ArrayList<>();
            for (Delivery delivery : allDeliveries) {
                Shipment shipment = shipmentDAO.findShipmentById(delivery.getShipmentId());
                DeliveryPersonnel personnel = null;
                if (delivery.getPersonnelId() != null && !delivery.getPersonnelId().isEmpty()) {
                    personnel = personnelDAO.findPersonnelById(delivery.getPersonnelId());
                }
                deliveryDetails.add(new Object[]{delivery, shipment, personnel});
            }
            view.displayDeliveries(deliveryDetails);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading scheduled deliveries: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads all available personnel into the personnel combo box.
     */
    public void loadAvailablePersonnel() {
        try {
            List<DeliveryPersonnel> personnelList = personnelDAO.findAllPersonnel(); // Get all personnel for selection
            view.populatePersonnelComboBox(personnelList);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading delivery personnel: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     * Handles scheduling a new delivery based on user input.
     */
    private void scheduleDelivery() {
        String shipmentId = view.getSelectedShipmentIdFromForm();
        if (shipmentId.isEmpty()) {
            view.showErrorMessage("Please select a shipment to schedule from the 'Unscheduled Shipments' table.");
            return;
        }

        DeliveryPersonnel selectedPersonnel = view.getSelectedPersonnel();
        LocalDate scheduledDate = view.getScheduledDate();
        String scheduledTimeSlot = view.getScheduledTimeSlot();
        LocalDateTime estimatedArrivalTime = view.getEstimatedArrivalTime();
        String deliveryStatus = view.getDeliveryStatus();
        String delayReason = view.getDelayReason();

        // Basic validation
        if (scheduledDate == null || scheduledTimeSlot.isEmpty() || estimatedArrivalTime == null || deliveryStatus.isEmpty()) {
            view.showErrorMessage("Please fill in all required delivery details (Date, Time Slot, ETA, Status).");
            return;
        }

        // Create new Delivery object
        Delivery newDelivery = new Delivery();
        newDelivery.setDeliveryId("DEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        newDelivery.setShipmentId(shipmentId);
        if (selectedPersonnel != null) {
            newDelivery.setPersonnelId(selectedPersonnel.getPersonnelId());
        }
        newDelivery.setScheduledDate(scheduledDate);
        newDelivery.setScheduledTimeSlot(scheduledTimeSlot);
        newDelivery.setEstimatedArrivalTime(estimatedArrivalTime);
        newDelivery.setDeliveryStatus(deliveryStatus);
        newDelivery.setDelayReason(delayReason);

        try {
            boolean success = deliveryDAO.insertDelivery(newDelivery);
            if (success) {
                view.showMessage("Delivery scheduled successfully!");

                // Update shipment status if needed (e.g., to 'Scheduled' or 'Assigned')
                Shipment shipment = shipmentDAO.findShipmentById(shipmentId);
                if (shipment != null) {
                    if (newDelivery.getPersonnelId() != null && !newDelivery.getPersonnelId().isEmpty()) {
                        shipment.setCurrentStatus("Assigned");
                        // Also update personnel status if assigned directly here
                        if(selectedPersonnel != null) {
                            selectedPersonnel.setAvailabilityStatus("On Route"); // Assuming 'On Route' for assigned delivery
                            personnelDAO.updatePersonnel(selectedPersonnel);
                        }
                    } else {
                        shipment.setCurrentStatus("Scheduled");
                    }
                    shipmentDAO.updateShipment(shipment);

                    // --- Send Customer Notification ---
                    String customerMsg = String.format("Dear %s, your shipment '%s' is now scheduled for delivery on %s between %s. Est. Arrival: %s. Status: %s.",
                            shipment.getReceiverName(), // Using receiver name as a placeholder for customer name
                            shipment.getTrackingNumber(),
                            newDelivery.getScheduledDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            newDelivery.getScheduledTimeSlot(),
                            newDelivery.getEstimatedArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            newDelivery.getDeliveryStatus());
                    notificationController.sendCustomerNotification(shipment.getShipmentId(), customerMsg, false);

                    // --- Send Personnel Notification if assigned ---
                    if (selectedPersonnel != null) {
                        String personnelMsg = String.format("New Delivery Scheduled for you: Shipment '%s' to %s. Date: %s, Time: %s. Est. Arrival: %s.",
                                shipment.getTrackingNumber(),
                                shipment.getReceiverAddress(),
                                newDelivery.getScheduledDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                newDelivery.getScheduledTimeSlot(),
                                newDelivery.getEstimatedArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        notificationController.sendPersonnelNotification(selectedPersonnel.getPersonnelId(), shipment.getShipmentId(), personnelMsg, shipment.isUrgent());
                    }

                }
                clearForm();
                loadAllData(); // Refresh all tables
            } else {
                view.showErrorMessage("Failed to schedule delivery.");
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error scheduling delivery: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles updating an existing delivery based on user input.
     */
    private void updateDelivery() {
        String deliveryId = view.getDeliveryId();
        if (deliveryId.isEmpty()) {
            view.showErrorMessage("Please select a delivery to update from the 'Scheduled Deliveries' table.");
            return;
        }

        Delivery existingDelivery;
        Shipment associatedShipment = null;
        DeliveryPersonnel originalPersonnel = null;
        try {
            existingDelivery = deliveryDAO.findDeliveryById(deliveryId);
            if (existingDelivery == null) {
                view.showErrorMessage("Selected delivery not found for update.");
                return;
            }
            associatedShipment = shipmentDAO.findShipmentById(existingDelivery.getShipmentId());
            if (existingDelivery.getPersonnelId() != null && !existingDelivery.getPersonnelId().isEmpty()) {
                originalPersonnel = personnelDAO.findPersonnelById(existingDelivery.getPersonnelId());
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Error retrieving existing delivery details: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        DeliveryPersonnel newSelectedPersonnel = view.getSelectedPersonnel();
        LocalDate scheduledDate = view.getScheduledDate();
        String scheduledTimeSlot = view.getScheduledTimeSlot();
        LocalDateTime estimatedArrivalTime = view.getEstimatedArrivalTime();
        String newDeliveryStatus = view.getDeliveryStatus();
        String delayReason = view.getDelayReason();

        // Update the existing Delivery object with new values
        if (newSelectedPersonnel != null) {
            existingDelivery.setPersonnelId(newSelectedPersonnel.getPersonnelId());
        } else {
            existingDelivery.setPersonnelId(null); // Unassign personnel
        }
        existingDelivery.setScheduledDate(scheduledDate);
        existingDelivery.setScheduledTimeSlot(scheduledTimeSlot);
        existingDelivery.setEstimatedArrivalTime(estimatedArrivalTime);
        // Only update actual delivery date if status changes to 'Delivered'
        if ("Delivered".equals(newDeliveryStatus) && existingDelivery.getActualDeliveryDate() == null) {
            existingDelivery.setActualDeliveryDate(LocalDateTime.now());
        } else if (!"Delivered".equals(newDeliveryStatus) && existingDelivery.getActualDeliveryDate() != null) {
            // If status changes from Delivered to something else, clear actual delivery date
            existingDelivery.setActualDeliveryDate(null);
        }
        String oldStatus = existingDelivery.getDeliveryStatus();
        existingDelivery.setDeliveryStatus(newDeliveryStatus);
        existingDelivery.setDelayReason(delayReason);

        try {
            boolean success = deliveryDAO.updateDelivery(existingDelivery);
            if (success) {
                view.showMessage("Delivery updated successfully!");

                // Update Shipment status if it changes
                if (associatedShipment != null && !oldStatus.equals(newDeliveryStatus)) {
                    associatedShipment.setCurrentStatus(newDeliveryStatus);
                    shipmentDAO.updateShipment(associatedShipment);

                    // --- Send Customer Notification on Status Change ---
                    String customerMsg = String.format("Dear %s, your shipment '%s' delivery status has changed to: %s. Current Location: %s.",
                            associatedShipment.getReceiverName(),
                            associatedShipment.getTrackingNumber(),
                            newDeliveryStatus,
                            associatedShipment.getCurrentLocation());
                    notificationController.sendCustomerNotification(associatedShipment.getShipmentId(), customerMsg, false);
                }

                // --- Handle Personnel Status and Notifications for assignment/reassignment/unassignment ---
                // If personnel was assigned and is now unassigned
                if (originalPersonnel != null && newSelectedPersonnel == null) {
                    originalPersonnel.setAvailabilityStatus("Available"); // Make original personnel available
                    personnelDAO.updatePersonnel(originalPersonnel);
                    String personnelMsg = String.format("Your assignment for Shipment ID: %s (Tracking No: %s) has been cancelled. You are now Available.",
                            associatedShipment.getShipmentId(), associatedShipment.getTrackingNumber());
                    notificationController.sendPersonnelNotification(originalPersonnel.getPersonnelId(), associatedShipment.getShipmentId(), personnelMsg, false);
                }
                // If a new personnel is assigned or personnel is changed
                else if (newSelectedPersonnel != null && (originalPersonnel == null || !originalPersonnel.getPersonnelId().equals(newSelectedPersonnel.getPersonnelId()))) {
                    if (originalPersonnel != null) { // If there was an old personnel, make them available
                        originalPersonnel.setAvailabilityStatus("Available");
                        personnelDAO.updatePersonnel(originalPersonnel);
                        String oldPersonnelMsg = String.format("Your assignment for Shipment ID: %s (Tracking No: %s) has been reassigned. You are now Available.",
                                associatedShipment.getShipmentId(), associatedShipment.getTrackingNumber());
                        notificationController.sendPersonnelNotification(originalPersonnel.getPersonnelId(), associatedShipment.getShipmentId(), oldPersonnelMsg, false);
                    }
                    newSelectedPersonnel.setAvailabilityStatus("On Route"); // Mark new personnel as on route
                    personnelDAO.updatePersonnel(newSelectedPersonnel);
                    String newPersonnelMsg = String.format("You have been assigned Shipment ID: %s (Tracking No: %s). Receiver: %s at %s. Status: %s. Please check your schedule.",
                            associatedShipment.getShipmentId(),
                            associatedShipment.getTrackingNumber(),
                            associatedShipment.getReceiverName(),
                            associatedShipment.getReceiverAddress(),
                            newDeliveryStatus);
                    notificationController.sendPersonnelNotification(newSelectedPersonnel.getPersonnelId(), associatedShipment.getShipmentId(), newPersonnelMsg, associatedShipment.isUrgent());
                }

                clearForm();
                loadAllData(); // Refresh all tables
            } else {
                view.showErrorMessage("Failed to update delivery. Delivery ID might not exist.");
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error updating delivery: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles deleting an existing delivery.
     */
    private void deleteDelivery() {
        String deliveryId = view.getDeliveryId();
        if (deliveryId.isEmpty()) {
            view.showErrorMessage("Please select a delivery to delete from the 'Scheduled Deliveries' table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete delivery with ID: " + deliveryId + "? This will unassign the shipment.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Delivery deliveryToDelete = deliveryDAO.findDeliveryById(deliveryId);
                if (deliveryToDelete == null) {
                    view.showErrorMessage("Delivery not found.");
                    return;
                }

                boolean success = deliveryDAO.deleteDelivery(deliveryId);
                if (success) {
                    view.showMessage("Delivery deleted successfully!");

                    // Update associated shipment status to 'Pending' (unassigned)
                    Shipment associatedShipment = shipmentDAO.findShipmentById(deliveryToDelete.getShipmentId());
                    if (associatedShipment != null) {
                        associatedShipment.setCurrentStatus("Pending"); // Or 'Unassigned'
                        shipmentDAO.updateShipment(associatedShipment);

                        // --- Send Customer Notification on Cancellation ---
                        String customerMsg = String.format("Dear %s, the scheduled delivery for your shipment '%s' (Tracking No: %s) has been cancelled. It is now Pending for new scheduling.",
                                associatedShipment.getReceiverName(),
                                associatedShipment.getShipmentId(),
                                associatedShipment.getTrackingNumber());
                        notificationController.sendCustomerNotification(associatedShipment.getShipmentId(), customerMsg, false);

                        // --- Send Personnel Notification if it was assigned ---
                        if (deliveryToDelete.getPersonnelId() != null && !deliveryToDelete.getPersonnelId().isEmpty()) {
                            DeliveryPersonnel personnel = personnelDAO.findPersonnelById(deliveryToDelete.getPersonnelId());
                            if (personnel != null) {
                                personnel.setAvailabilityStatus("Available"); // Make personnel available
                                personnelDAO.updatePersonnel(personnel);
                                String personnelMsg = String.format("Your assigned delivery for Shipment ID: %s (Tracking No: %s) has been cancelled. You are now Available.",
                                        associatedShipment.getShipmentId(), associatedShipment.getTrackingNumber());
                                notificationController.sendPersonnelNotification(personnel.getPersonnelId(), associatedShipment.getShipmentId(), personnelMsg, false);
                            }
                        }
                    }
                    clearForm();
                    loadAllData(); // Refresh all tables
                } else {
                    view.showErrorMessage("Failed to delete delivery.");
                }
            } catch (SQLException ex) {
                view.showErrorMessage("Database error deleting delivery: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Clears all input fields and table selections in the view.
     */
    private void clearForm() {
        view.clearForm();
    }

    /**
     * Loads the details of the selected delivery from the `deliveriesTable` into the form fields.
     */
    private void loadSelectedDeliveryToForm() {
        int selectedRow = view.getDeliveriesTable().getSelectedRow();
        if (selectedRow >= 0) {
            String deliveryId = (String) view.getDeliveriesTable().getModel().getValueAt(selectedRow, 0);
            try {
                Delivery selectedDelivery = deliveryDAO.findDeliveryById(deliveryId);
                Shipment associatedShipment = null;
                DeliveryPersonnel assignedPersonnel = null;

                if (selectedDelivery != null) {
                    associatedShipment = shipmentDAO.findShipmentById(selectedDelivery.getShipmentId());
                    if (selectedDelivery.getPersonnelId() != null && !selectedDelivery.getPersonnelId().isEmpty()) {
                        assignedPersonnel = personnelDAO.findPersonnelById(selectedDelivery.getPersonnelId());
                    }
                    view.populateForm(selectedDelivery, associatedShipment, assignedPersonnel);
                } else {
                    view.showErrorMessage("Could not find selected delivery in database.");
                    clearForm();
                }
            } catch (SQLException ex) {
                view.showErrorMessage("Error loading delivery details: " + ex.getMessage());
                ex.printStackTrace();
                clearForm();
            }
        }
    }
}