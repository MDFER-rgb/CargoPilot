

// src/main/java/com/fasttracklogistics/controller/TrackShipmentsController.java

package com.fasttracklogistics.controller;

import com.fasttracklogistics.dao.ShipmentDAO;
import com.fasttracklogistics.dao.ShipmentDAOImpl;
import com.fasttracklogistics.dao.DeliveryDAO;
import com.fasttracklogistics.dao.DeliveryDAOImpl;
import com.fasttracklogistics.dao.DeliveryPersonnelDAO; // NEW import for personnel
import com.fasttracklogistics.dao.DeliveryPersonnelDAOImpl; // NEW import for personnel

import com.fasttracklogistics.model.Shipment;
import com.fasttracklogistics.model.Delivery;
import com.fasttracklogistics.model.DeliveryPersonnel; // NEW import for personnel
import com.fasttracklogistics.view.TrackShipmentsPanel;

import java.sql.SQLException;

/**
 * Controller for managing the "Track Shipments" functionality.
 * This class handles user input for tracking numbers and retrieves/displays
 * shipment and delivery details.
 */
public class TrackShipmentsController {

    private TrackShipmentsPanel view;
    private ShipmentDAO shipmentDAO;
    private DeliveryDAO deliveryDAO;
    private DeliveryPersonnelDAO personnelDAO; // NEW DAO instance

    public TrackShipmentsController(TrackShipmentsPanel view) {
        this.view = view;
        this.shipmentDAO = new ShipmentDAOImpl();
        this.deliveryDAO = new DeliveryDAOImpl();
        this.personnelDAO = new DeliveryPersonnelDAOImpl(); // Initialize personnel DAO

        // Attach action listeners
        this.view.getTrackButton().addActionListener(e -> trackShipment());
        this.view.getClearButton().addActionListener(e -> clearForm());
    }

    /**
     * Handles tracking a shipment based on the entered tracking number.
     * Fetches shipment, delivery, and personnel details and displays them.
     */
    private void trackShipment() {
        String trackingNumber = view.getTrackingNumberInput();

        if (trackingNumber.isEmpty()) {
            view.showErrorMessage("Please enter a tracking number.");
            view.clearDetails(); // Clear any previous details if input is empty
            return;
        }

        try {
            Shipment shipment = shipmentDAO.findShipmentByTrackingNumber(trackingNumber);

            if (shipment != null) {
                // Found the shipment, now fetch associated delivery and personnel (if any)
                Delivery delivery = deliveryDAO.findDeliveryByShipmentId(shipment.getShipmentId());
                DeliveryPersonnel assignedPersonnel = null;
                if (delivery != null && delivery.getPersonnelId() != null) {
                    assignedPersonnel = personnelDAO.findPersonnelById(delivery.getPersonnelId());
                }
                view.displayShipmentDetails(shipment, delivery, assignedPersonnel);
            } else {
                view.showErrorMessage("No shipment found with tracking number: " + trackingNumber);
                view.clearDetails(); // Clear display if not found
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error while tracking shipment: " + ex.getMessage());
            ex.printStackTrace();
            view.clearDetails(); // Clear display on error
        }
    }

    /**
     * Clears all display fields in the tracking panel.
     */
    private void clearForm() {
        view.clearDetails();
    }
}