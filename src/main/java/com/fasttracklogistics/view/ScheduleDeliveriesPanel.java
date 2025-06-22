// src/main/java/com/fasttracklogistics/view/ScheduleDeliveriesPanel.java (MODIFIED)

package com.fasttracklogistics.view;

import com.fasttracklogistics.model.Delivery;
import com.fasttracklogistics.model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Import for parsing exception
import java.util.List;
import java.util.Vector;

/**
 * JPanel for scheduling deliveries.
 * This panel allows users to schedule, update, and delete delivery records,
 * linking shipments to personnel and managing delivery status.
 */
public class ScheduleDeliveriesPanel extends JPanel {

    private JTextField deliveryIdField;
    private JTextField shipmentIdDisplayField; // Displays selected shipment ID
    private JTextField trackingNumberDisplayField; // Displays selected tracking number
    private JTextField receiverAddressDisplayField; // Displays selected receiver address
    private JComboBox<DeliveryPersonnel> assignedPersonnelComboBox; // Personnel selection
    private JTextField scheduledDateField; // Replaced JDatePicker with JTextField
    private JTextField scheduledTimeSlotField;
    private JTextField estimatedArrivalTimeField;
    private JTextField actualDeliveryDateField;
    private JComboBox<String> deliveryStatusComboBox;
    private JTextArea delayReasonArea;

    private JButton scheduleButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshShipmentsButton; // Refresh for unscheduled shipments
    private JButton refreshDeliveriesButton; // Refresh for scheduled deliveries

    private JTable unscheduledShipmentsTable;
    private DefaultTableModel unscheduledShipmentsTableModel;
    private JTable deliveriesTable;
    private DefaultTableModel deliveriesTableModel;

    // Moved to be an instance variable to be accessible by inner class
    private final String[] unscheduledShipmentColumns = {"Shipment ID", "Tracking No.", "Sender Name", "Receiver Name", "Receiver Address", "Package Type", "Urgent"};

    // Define DateTimeFormatter for consistent input/output for date and datetime fields
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public ScheduleDeliveriesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Unscheduled Shipments Table ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Unscheduled Shipments (Select to Schedule)"));
        // Using the instance variable now
        unscheduledShipmentsTableModel = new DefaultTableModel(unscheduledShipmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == unscheduledShipmentColumns.length - 1) { // "Urgent" column
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        unscheduledShipmentsTable = new JTable(unscheduledShipmentsTableModel);
        unscheduledShipmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unscheduledShipmentsTable.setAutoCreateRowSorter(true);
        unscheduledShipmentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && unscheduledShipmentsTable.getSelectedRow() != -1) {
                populateFormFromUnscheduledShipmentSelection();
            }
        });
        topPanel.add(new JScrollPane(unscheduledShipmentsTable), BorderLayout.CENTER);
        refreshShipmentsButton = new JButton("Refresh Unscheduled Shipments");
        JPanel refreshShipmentsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshShipmentsButtonPanel.add(refreshShipmentsButton);
        topPanel.add(refreshShipmentsButtonPanel, BorderLayout.SOUTH);


        // --- Middle Panel: Delivery Details Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Delivery Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Delivery ID (auto-generated)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Delivery ID (Auto):"), gbc);
        gbc.gridx = 1; deliveryIdField = new JTextField(15); deliveryIdField.setEditable(false); formPanel.add(deliveryIdField, gbc);

        // Selected Shipment Info (from unscheduled table)
        gbc.gridx = 2; gbc.gridy = row; formPanel.add(new JLabel("Shipment ID:"), gbc);
        gbc.gridx = 3; shipmentIdDisplayField = new JTextField(15); shipmentIdDisplayField.setEditable(false); formPanel.add(shipmentIdDisplayField, gbc);
        gbc.gridx = 4; gbc.gridy = row++; formPanel.add(new JLabel("Tracking No:"), gbc);
        gbc.gridx = 5; trackingNumberDisplayField = new JTextField(15); trackingNumberDisplayField.setEditable(false); formPanel.add(trackingNumberDisplayField, gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Receiver Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; receiverAddressDisplayField = new JTextField(30); receiverAddressDisplayField.setEditable(false); formPanel.add(receiverAddressDisplayField, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 4; gbc.gridy = row++; formPanel.add(new JLabel("Assigned Personnel:"), gbc);
        gbc.gridx = 5; assignedPersonnelComboBox = new JComboBox<>(); assignedPersonnelComboBox.setPreferredSize(new Dimension(150, assignedPersonnelComboBox.getPreferredSize().height)); formPanel.add(assignedPersonnelComboBox, gbc);


        // Scheduled Date (JTextField)
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Scheduled Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; scheduledDateField = new JTextField(15); formPanel.add(scheduledDateField, gbc);

        // Scheduled Time Slot
        gbc.gridx = 2; gbc.gridy = row; formPanel.add(new JLabel("Scheduled Time Slot:"), gbc);
        gbc.gridx = 3; scheduledTimeSlotField = new JTextField(15); formPanel.add(scheduledTimeSlotField, gbc);

        // Estimated Arrival Time
        gbc.gridx = 4; gbc.gridy = row++; formPanel.add(new JLabel("Estimated Arrival Time (YYYY-MM-DD HH:MM):"), gbc);
        gbc.gridx = 5; estimatedArrivalTimeField = new JTextField(15); formPanel.add(estimatedArrivalTimeField, gbc);

        // Actual Delivery Date
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Actual Delivery Date (Auto-Filled on 'Delivered'):"), gbc);
        gbc.gridx = 1; actualDeliveryDateField = new JTextField(15); actualDeliveryDateField.setEditable(false); formPanel.add(actualDeliveryDateField, gbc);

        // Delivery Status
        gbc.gridx = 2; gbc.gridy = row; formPanel.add(new JLabel("Delivery Status:"), gbc);
        gbc.gridx = 3;
        String[] deliveryStatuses = {"Scheduled", "Assigned", "Picked Up", "En Route", "Delayed", "Delivered", "Cancelled"};
        deliveryStatusComboBox = new JComboBox<>(deliveryStatuses);
        formPanel.add(deliveryStatusComboBox, gbc);

        // Delay Reason
        gbc.gridx = 4; gbc.gridy = row++; formPanel.add(new JLabel("Delay Reason:"), gbc);
        gbc.gridx = 5; delayReasonArea = new JTextArea(3, 15); delayReasonArea.setLineWrap(true); delayReasonArea.setWrapStyleWord(true);
        JScrollPane delayScrollPane = new JScrollPane(delayReasonArea);
        formPanel.add(delayScrollPane, gbc);

        // --- Create a separate panel for buttons and add it below the form ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        scheduleButton = new JButton("Schedule Delivery");
        updateButton = new JButton("Update Delivery");
        deleteButton = new JButton("Delete Delivery");
        clearButton = new JButton("Clear Form");
        buttonPanel.add(scheduleButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // --- Container to hold form and buttons stacked vertically ---
        JPanel formAndButtonsContainer = new JPanel(new BorderLayout());
        formAndButtonsContainer.add(formPanel, BorderLayout.CENTER); // Form takes center
        formAndButtonsContainer.add(buttonPanel, BorderLayout.SOUTH); // Buttons at the bottom of this container


        // --- Bottom Panel: Scheduled Deliveries Table ---
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Scheduled Deliveries"));
        String[] deliveryColumns = {"Delivery ID", "Shipment Tracking No.", "Assigned Personnel", "Scheduled Date", "Time Slot", "ETA", "Actual Delivery Date", "Status", "Delay Reason"};
        deliveriesTableModel = new DefaultTableModel(deliveryColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deliveriesTable = new JTable(deliveriesTableModel);
        deliveriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deliveriesTable.setAutoCreateRowSorter(true);
        deliveriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && deliveriesTable.getSelectedRow() != -1) {
                // Controller handles populating from selected delivery
            }
        });
        bottomPanel.add(new JScrollPane(deliveriesTable), BorderLayout.CENTER);
        refreshDeliveriesButton = new JButton("Refresh Scheduled Deliveries");
        JPanel refreshDeliveriesButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshDeliveriesButtonPanel.add(refreshDeliveriesButton);
        bottomPanel.add(refreshDeliveriesButtonPanel, BorderLayout.SOUTH);


        // --- Main Layout using JSplitPane ---
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setTopComponent(topPanel);

        JSplitPane formAndBottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        formAndBottomSplitPane.setTopComponent(formAndButtonsContainer); // Now contains form + buttons
        formAndBottomSplitPane.setBottomComponent(bottomPanel);
        formAndBottomSplitPane.setResizeWeight(0.5); // Give equal weight to form/buttons and bottom table

        mainSplitPane.setBottomComponent(formAndBottomSplitPane);
        mainSplitPane.setResizeWeight(0.3); // Give more space to form and assigned deliveries

        add(mainSplitPane, BorderLayout.CENTER);

        // Initial setup for the split pane dividers
        SwingUtilities.invokeLater(() -> mainSplitPane.setDividerLocation(0.3));
        SwingUtilities.invokeLater(() -> formAndBottomSplitPane.setDividerLocation(0.5));
    }

    // --- Helper to populate form from Unscheduled Shipments Table Selection ---
    public void populateFormFromUnscheduledShipmentSelection() { // Changed to public
        int selectedRow = unscheduledShipmentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String shipmentId = (String) unscheduledShipmentsTableModel.getValueAt(selectedRow, 0);
            String trackingNumber = (String) unscheduledShipmentsTableModel.getValueAt(selectedRow, 1);
            String receiverAddress = (String) unscheduledShipmentsTableModel.getValueAt(selectedRow, 4); // Index for Receiver Address

            shipmentIdDisplayField.setText(shipmentId);
            trackingNumberDisplayField.setText(trackingNumber);
            receiverAddressDisplayField.setText(receiverAddress);

            // Clear other fields related to existing delivery as this is for scheduling new
            deliveryIdField.setText("");
            assignedPersonnelComboBox.setSelectedIndex(-1); // No personnel assigned initially
            scheduledDateField.setText(""); // Clear the text field
            scheduledTimeSlotField.setText("");
            estimatedArrivalTimeField.setText("");
            actualDeliveryDateField.setText("");
            deliveryStatusComboBox.setSelectedItem("Scheduled");
            delayReasonArea.setText("");
        }
    }


    // --- Getters for Form Fields ---
    public String getDeliveryId() {
        return deliveryIdField.getText().trim();
    }

    public String getSelectedShipmentIdFromForm() {
        return shipmentIdDisplayField.getText().trim();
    }

    public DeliveryPersonnel getSelectedPersonnel() {
        // Cast to DeliveryPersonnel to get the full object
        return (DeliveryPersonnel) assignedPersonnelComboBox.getSelectedItem();
    }

    public LocalDate getScheduledDate() {
        String dateText = scheduledDateField.getText().trim();
        if (dateText.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateText, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            showErrorMessage("Invalid Scheduled Date format. Please use `YYYY-MM-DD`.");
            return null;
        }
    }

    public String getScheduledTimeSlot() {
        return scheduledTimeSlotField.getText().trim();
    }

    public LocalDateTime getEstimatedArrivalTime() {
        String etaText = estimatedArrivalTimeField.getText().trim();
        if (etaText.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(etaText, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            showErrorMessage("Invalid Estimated Arrival Time format. Use `YYYY-MM-DD HH:MM` (e.g., 2025-07-20 14:30)");
            return null;
        }
    }

    public String getDeliveryStatus() {
        return (String) deliveryStatusComboBox.getSelectedItem();
    }

    public String getDelayReason() {
        return delayReasonArea.getText().trim();
    }

    // --- Methods to interact with the View from Controller ---

    /**
     * Clears all input fields and table selections in the form.
     */
    public void clearForm() {
        deliveryIdField.setText("");
        shipmentIdDisplayField.setText("");
        trackingNumberDisplayField.setText("");
        receiverAddressDisplayField.setText("");
        assignedPersonnelComboBox.setSelectedIndex(-1); // Clears selection
        scheduledDateField.setText(""); // Clear the text field
        scheduledTimeSlotField.setText("");
        estimatedArrivalTimeField.setText("");
        actualDeliveryDateField.setText("");
        deliveryStatusComboBox.setSelectedItem("Scheduled"); // Default status
        delayReasonArea.setText("");
        unscheduledShipmentsTable.clearSelection();
        deliveriesTable.clearSelection();
    }

    /**
     * Populates the form fields with data from a given Delivery, Shipment, and DeliveryPersonnel.
     * This is used when a row is selected in the scheduled deliveries table.
     * @param delivery The Delivery object to display.
     * @param shipment The associated Shipment object.
     * @param personnel The assigned DeliveryPersonnel object (can be null).
     */
    public void populateForm(Delivery delivery, Shipment shipment, DeliveryPersonnel personnel) {
        if (delivery != null && shipment != null) {
            deliveryIdField.setText(delivery.getDeliveryId());
            shipmentIdDisplayField.setText(shipment.getShipmentId());
            trackingNumberDisplayField.setText(shipment.getTrackingNumber());
            receiverAddressDisplayField.setText(shipment.getReceiverAddress());

            // Select personnel in combo box
            if (personnel != null) {
                // Ensure the exact personnel object is in the combo box model before setting selected item
                boolean found = false;
                for (int i = 0; i < assignedPersonnelComboBox.getItemCount(); i++) {
                    DeliveryPersonnel item = assignedPersonnelComboBox.getItemAt(i);
                    if (item != null && item.getPersonnelId().equals(personnel.getPersonnelId())) {
                        assignedPersonnelComboBox.setSelectedItem(item);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    assignedPersonnelComboBox.setSelectedIndex(-1); // Not found, clear selection
                }
            } else {
                assignedPersonnelComboBox.setSelectedIndex(-1);
            }

            // Set date using JTextField
            if (delivery.getScheduledDate() != null) {
                scheduledDateField.setText(delivery.getScheduledDate().format(DATE_FORMATTER));
            } else {
                scheduledDateField.setText("");
            }

            scheduledTimeSlotField.setText(delivery.getScheduledTimeSlot());
            estimatedArrivalTimeField.setText(delivery.getEstimatedArrivalTime() != null ?
                    delivery.getEstimatedArrivalTime().format(DATETIME_FORMATTER) : "");
            actualDeliveryDateField.setText(delivery.getActualDeliveryDate() != null ?
                    delivery.getActualDeliveryDate().format(DATETIME_FORMATTER) : "");
            deliveryStatusComboBox.setSelectedItem(delivery.getDeliveryStatus());
            delayReasonArea.setText(delivery.getDelayReason());
        } else {
            clearForm();
        }
    }

    /**
     * Displays a list of unscheduled shipments in the top table.
     * @param shipments The list of Shipment objects to display.
     */
    public void displayUnscheduledShipments(List<Shipment> shipments) {
        unscheduledShipmentsTableModel.setRowCount(0); // Clear existing data
        for (Shipment shipment : shipments) {
            Vector<Object> row = new Vector<>();
            row.add(shipment.getShipmentId());
            row.add(shipment.getTrackingNumber());
            row.add(shipment.getSenderName());
            row.add(shipment.getReceiverName());
            row.add(shipment.getReceiverAddress());
            row.add(shipment.getPackageType());
            row.add(shipment.isUrgent());
            unscheduledShipmentsTableModel.addRow(row);
        }
    }

    /**
     * Displays a list of scheduled deliveries in the bottom table.
     * @param deliveryDetails A list of Object arrays, each containing {Delivery, Shipment, DeliveryPersonnel}.
     */
    public void displayDeliveries(List<Object[]> deliveryDetails) {
        deliveriesTableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // Not used directly in display
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


        for (Object[] rowData : deliveryDetails) {
            Delivery delivery = (Delivery) rowData[0];
            Shipment shipment = (Shipment) rowData[1];
            DeliveryPersonnel personnel = (DeliveryPersonnel) rowData[2];

            Vector<Object> row = new Vector<>();
            row.add(delivery.getDeliveryId());
            row.add(shipment != null ? shipment.getTrackingNumber() : "N/A");
            row.add(personnel != null ? personnel.getName() : "Unassigned");
            row.add(delivery.getScheduledDate() != null ? delivery.getScheduledDate().format(dateFormatter) : "N/A");
            row.add(delivery.getScheduledTimeSlot());
            row.add(delivery.getEstimatedArrivalTime() != null ? delivery.getEstimatedArrivalTime().format(dateTimeFormatter) : "N/A");
            row.add(delivery.getActualDeliveryDate() != null ? delivery.getActualDeliveryDate().format(dateTimeFormatter) : "N/A");
            row.add(delivery.getDeliveryStatus());
            row.add(delivery.getDelayReason());
            deliveriesTableModel.addRow(row);
        }
    }

    /**
     * Populates the assigned personnel combo box with a list of DeliveryPersonnel objects.
     * @param personnelList The list of DeliveryPersonnel objects to populate the combo box with.
     */
    public void populatePersonnelComboBox(List<DeliveryPersonnel> personnelList) {
        assignedPersonnelComboBox.removeAllItems();
        // Add a null item to represent "Unassigned" or no selection
        assignedPersonnelComboBox.addItem(null); // This displays as empty or "null" depending on JComboBox renderer
        for (DeliveryPersonnel personnel : personnelList) {
            assignedPersonnelComboBox.addItem(personnel);
        }
        // Set a custom renderer to display personnel name instead of object's toString()
        assignedPersonnelComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DeliveryPersonnel) {
                    setText(((DeliveryPersonnel) value).getName());
                } else if (value == null) {
                    setText("-- Select Personnel --"); // Custom text for null/no selection
                }
                return this;
            }
        });
        assignedPersonnelComboBox.setSelectedIndex(0); // Select the "-- Select Personnel --" by default
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
    public JButton getScheduleButton() {
        return scheduleButton;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JButton getRefreshShipmentsButton() {
        return refreshShipmentsButton;
    }

    public JButton getRefreshDeliveriesButton() {
        return refreshDeliveriesButton;
    }

    // --- Table Getters for Controller to attach Listeners ---
    public JTable getUnscheduledShipmentsTable() {
        return unscheduledShipmentsTable;
    }

    public JTable getDeliveriesTable() {
        return deliveriesTable;
    }
}