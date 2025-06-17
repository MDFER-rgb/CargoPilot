
// src/main/java/com/fasttracklogistics/view/TrackShipmentsPanel.java

package com.fasttracklogistics.view;

import com.fasttracklogistics.model.Shipment;
import com.fasttracklogistics.model.Delivery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * JPanel for tracking shipment progress.
 * This view allows users to enter a tracking number and display detailed shipment information.
 */
public class TrackShipmentsPanel extends JPanel {

    private JTextField trackingNumberInputField;
    private JButton trackButton;
    private JButton clearButton; // NEW: Clear button for tracking panel

    // Labels for displaying shipment details
    private JLabel shipmentIdLabel;
    private JLabel senderNameLabel;
    private JLabel senderAddressLabel;
    private JLabel senderContactLabel;
    private JLabel receiverNameLabel;
    private JLabel receiverAddressLabel;
    private JLabel receiverContactLabel;
    private JLabel packageContentsLabel;
    private JLabel packageTypeLabel;
    private JLabel weightKgLabel;
    private JLabel dimensionsCmLabel;
    private JLabel currentLocationLabel;
    private JLabel routeLabel;
    private JLabel currentStatusLabel;
    private JLabel isUrgentLabel; // NEW: Label for urgent status
    private JLabel scheduledDateLabel;
    private JLabel scheduledTimeSlotLabel;
    private JLabel estimatedArrivalTimeLabel;
    private JLabel actualDeliveryDateLabel;
    private JLabel deliveryStatusLabel;
    private JLabel delayReasonLabel;
    private JLabel assignedPersonnelLabel; // To show assigned driver's name/ID
    private JLabel createdAtLabel;
    private JLabel updatedAtLabel;

    public TrackShipmentsPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Input Panel for Tracking Number ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.add(new JLabel("Enter Tracking Number:"));
        trackingNumberInputField = new JTextField(25);
        inputPanel.add(trackingNumberInputField);
        trackButton = new JButton("Track Shipment");
        inputPanel.add(trackButton);
        clearButton = new JButton("Clear"); // NEW clear button
        inputPanel.add(clearButton);
        add(inputPanel, BorderLayout.NORTH);

        // --- Details Display Panel ---
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Shipment Tracking Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Shipment Details
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Shipment ID:"), gbc);
        gbc.gridx = 1; shipmentIdLabel = new JLabel("N/A"); detailsPanel.add(shipmentIdLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row++; detailsPanel.add(new JLabel("Tracking Number:"), gbc);
        gbc.gridx = 3; detailsPanel.add(new JLabel(""), gbc); // Placeholder as tracking number is input

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Sender Name:"), gbc);
        gbc.gridx = 1; senderNameLabel = new JLabel("N/A"); detailsPanel.add(senderNameLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row; detailsPanel.add(new JLabel("Sender Address:"), gbc);
        gbc.gridx = 3; senderAddressLabel = new JLabel("N/A"); detailsPanel.add(senderAddressLabel, gbc);
        gbc.gridx = 4; gbc.gridy = row++; detailsPanel.add(new JLabel("Sender Contact:"), gbc);
        gbc.gridx = 5; senderContactLabel = new JLabel("N/A"); detailsPanel.add(senderContactLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Receiver Name:"), gbc);
        gbc.gridx = 1; receiverNameLabel = new JLabel("N/A"); detailsPanel.add(receiverNameLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row; detailsPanel.add(new JLabel("Receiver Address:"), gbc);
        gbc.gridx = 3; receiverAddressLabel = new JLabel("N/A"); detailsPanel.add(receiverAddressLabel, gbc);
        gbc.gridx = 4; gbc.gridy = row++; detailsPanel.add(new JLabel("Receiver Contact:"), gbc);
        gbc.gridx = 5; receiverContactLabel = new JLabel("N/A"); detailsPanel.add(receiverContactLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Package Contents:"), gbc);
        gbc.gridx = 1; packageContentsLabel = new JLabel("N/A"); detailsPanel.add(packageContentsLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row; detailsPanel.add(new JLabel("Package Type:"), gbc);
        gbc.gridx = 3; packageTypeLabel = new JLabel("N/A"); detailsPanel.add(packageTypeLabel, gbc);
        gbc.gridx = 4; gbc.gridy = row++; detailsPanel.add(new JLabel("Is Urgent:"), gbc); // NEW
        gbc.gridx = 5; isUrgentLabel = new JLabel("N/A"); detailsPanel.add(isUrgentLabel, gbc); // NEW


        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1; weightKgLabel = new JLabel("N/A"); detailsPanel.add(weightKgLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row; detailsPanel.add(new JLabel("Dimensions (cm):"), gbc);
        gbc.gridx = 3; dimensionsCmLabel = new JLabel("N/A"); detailsPanel.add(dimensionsCmLabel, gbc);
        gbc.gridx = 4; gbc.gridy = row++; detailsPanel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 5; routeLabel = new JLabel("N/A"); detailsPanel.add(routeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Current Location:"), gbc);
        gbc.gridx = 1; currentLocationLabel = new JLabel("N/A"); detailsPanel.add(currentLocationLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row++; detailsPanel.add(new JLabel("Current Status:"), gbc);
        gbc.gridx = 3; currentStatusLabel = new JLabel("N/A"); detailsPanel.add(currentStatusLabel, gbc);

        // Separator for Delivery Details
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = GridBagConstraints.REMAINDER;
        detailsPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Delivery Details
        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Scheduled Date:"), gbc);
        gbc.gridx = 1; scheduledDateLabel = new JLabel("N/A"); detailsPanel.add(scheduledDateLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row++; detailsPanel.add(new JLabel("Scheduled Time Slot:"), gbc);
        gbc.gridx = 3; scheduledTimeSlotLabel = new JLabel("N/A"); detailsPanel.add(scheduledTimeSlotLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Est. Arrival Time:"), gbc);
        gbc.gridx = 1; estimatedArrivalTimeLabel = new JLabel("N/A"); detailsPanel.add(estimatedArrivalTimeLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row++; detailsPanel.add(new JLabel("Actual Delivery Date:"), gbc);
        gbc.gridx = 3; actualDeliveryDateLabel = new JLabel("N/A"); detailsPanel.add(actualDeliveryDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Delivery Status:"), gbc);
        gbc.gridx = 1; deliveryStatusLabel = new JLabel("N/A"); detailsPanel.add(deliveryStatusLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row++; detailsPanel.add(new JLabel("Delay Reason:"), gbc);
        gbc.gridx = 3; delayReasonLabel = new JLabel("N/A"); detailsPanel.add(delayReasonLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; detailsPanel.add(new JLabel("Assigned Personnel:"), gbc);
        gbc.gridx = 1; assignedPersonnelLabel = new JLabel("N/A"); detailsPanel.add(assignedPersonnelLabel, gbc);
        gbc.gridx = 2; gbc.gridy = row++; detailsPanel.add(new JLabel("Created At:"), gbc);
        gbc.gridx = 3; createdAtLabel = new JLabel("N/A"); detailsPanel.add(createdAtLabel, gbc);
        gbc.gridx = 4; gbc.gridy = row++; detailsPanel.add(new JLabel("Last Updated:"), gbc);
        gbc.gridx = 5; updatedAtLabel = new JLabel("N/A"); detailsPanel.add(updatedAtLabel, gbc);


        // Add details panel to a scroll pane in case content is too long
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(detailsScrollPane, BorderLayout.CENTER);

        // Initialize display to "N/A"
        clearDetails();
    }

    /**
     * Retrieves the tracking number entered by the user.
     * @return The tracking number string.
     */
    public String getTrackingNumberInput() {
        return trackingNumberInputField.getText().trim();
    }

    /**
     * Populates the display labels with shipment and delivery details.
     *
     * @param shipment The Shipment object to display.
     * @param delivery The associated Delivery object (can be null).
     * @param personnel The assigned DeliveryPersonnel object (can be null).
     */
    public void displayShipmentDetails(Shipment shipment, Delivery delivery, com.fasttracklogistics.model.DeliveryPersonnel personnel) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (shipment != null) {
            shipmentIdLabel.setText(shipment.getShipmentId());
            senderNameLabel.setText(shipment.getSenderName());
            senderAddressLabel.setText(shipment.getSenderAddress());
            senderContactLabel.setText(shipment.getSenderContact());
            receiverNameLabel.setText(shipment.getReceiverName());
            receiverAddressLabel.setText(shipment.getReceiverAddress());
            receiverContactLabel.setText(shipment.getReceiverContact());
            packageContentsLabel.setText(shipment.getPackageContents());
            packageTypeLabel.setText(shipment.getPackageType());
            weightKgLabel.setText(String.format("%.2f kg", shipment.getWeightKg()));
            dimensionsCmLabel.setText(shipment.getDimensionsCm());
            currentLocationLabel.setText(shipment.getCurrentLocation());
            routeLabel.setText(shipment.getRoute());
            currentStatusLabel.setText(shipment.getCurrentStatus());
            isUrgentLabel.setText(shipment.isUrgent() ? "Yes" : "No"); // Display "Yes" or "No"

            createdAtLabel.setText(shipment.getCreatedAt() != null ? shipment.getCreatedAt().format(dateTimeFormatter) : "N/A");
            updatedAtLabel.setText(shipment.getUpdatedAt() != null ? shipment.getUpdatedAt().format(dateTimeFormatter) : "N/A");

            if (delivery != null) {
                scheduledDateLabel.setText(delivery.getScheduledDate() != null ? delivery.getScheduledDate().format(dateFormatter) : "N/A");
                scheduledTimeSlotLabel.setText(delivery.getScheduledTimeSlot());
                estimatedArrivalTimeLabel.setText(delivery.getEstimatedArrivalTime() != null ? delivery.getEstimatedArrivalTime().format(dateTimeFormatter) : "N/A");
                actualDeliveryDateLabel.setText(delivery.getActualDeliveryDate() != null ? delivery.getActualDeliveryDate().format(dateTimeFormatter) : "N/A");
                deliveryStatusLabel.setText(delivery.getDeliveryStatus());
                delayReasonLabel.setText(delivery.getDelayReason() != null && !delivery.getDelayReason().isEmpty() ? delivery.getDelayReason() : "N/A");
            } else {
                // Clear delivery-specific fields if no delivery is found
                scheduledDateLabel.setText("N/A");
                scheduledTimeSlotLabel.setText("N/A");
                estimatedArrivalTimeLabel.setText("N/A");
                actualDeliveryDateLabel.setText("N/A");
                deliveryStatusLabel.setText("N/A");
                delayReasonLabel.setText("N/A");
            }

            assignedPersonnelLabel.setText(personnel != null ? personnel.getName() + " (EmpID: " + personnel.getEmployeeId() + ")" : "Not Assigned");

        } else {
            clearDetails(); // Clear all fields if shipment is null
        }
    }

    /**
     * Clears all detail display labels and the input field.
     */
    public void clearDetails() {
        trackingNumberInputField.setText("");
        shipmentIdLabel.setText("N/A");
        senderNameLabel.setText("N/A");
        senderAddressLabel.setText("N/A");
        senderContactLabel.setText("N/A");
        receiverNameLabel.setText("N/A");
        receiverAddressLabel.setText("N/A");
        receiverContactLabel.setText("N/A");
        packageContentsLabel.setText("N/A");
        packageTypeLabel.setText("N/A");
        weightKgLabel.setText("N/A");
        dimensionsCmLabel.setText("N/A");
        currentLocationLabel.setText("N/A");
        routeLabel.setText("N/A");
        currentStatusLabel.setText("N/A");
        isUrgentLabel.setText("N/A");
        scheduledDateLabel.setText("N/A");
        scheduledTimeSlotLabel.setText("N/A");
        estimatedArrivalTimeLabel.setText("N/A");
        actualDeliveryDateLabel.setText("N/A");
        deliveryStatusLabel.setText("N/A");
        delayReasonLabel.setText("N/A");
        assignedPersonnelLabel.setText("N/A");
        createdAtLabel.setText("N/A");
        updatedAtLabel.setText("N/A");
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

    // --- Getters for Buttons (to attach listeners in Controller) ---
    public JButton getTrackButton() {
        return trackButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }
}
