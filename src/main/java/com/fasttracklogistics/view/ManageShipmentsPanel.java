// src/main/java/com/fasttracklogistics/view/ManageShipmentsPanel.java
package com.fasttracklogistics.view;

import com.fasttracklogistics.model.Delivery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

/**
 * JPanel for managing shipment records (Add, Update, Remove).
 * This class represents the 'View' component for shipment management.
 */
public class ManageShipmentsPanel extends JPanel {

    private JTextField shipmentIdField;
    private JTextField trackingNumberField;
    private JTextField senderNameField;
    private JTextField senderAddressField;
    private JTextField senderContactField;
    private JTextField receiverNameField;
    private JTextField receiverAddressField;
    private JTextField receiverContactField;
    private JTextArea packageContentsArea;
    private JComboBox<String> packageTypeComboBox; // NEW: Package Type ComboBox
    private JTextField weightKgField;
    private JTextField dimensionsCmField;
    private JTextField currentLocationField;
    private JComboBox<String> routeComboBox; // NEW: Route ComboBox
    private JComboBox<String> currentStatusComboBox;
    private JTextField estimatedDeliveryTimeField;
    private JCheckBox isUrgentCheckBox; // Re-added isUrgent checkbox

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;

    private JTable shipmentTable;
    private DefaultTableModel tableModel;

    // Fixed lists for Package Types and Routes
    private final String[] PACKAGE_TYPES = {
            "Documents/Letters", "Parcels", "Cash on Delivery (COD)",
            "Perishable/Temperature-Sensitive", "Fragile/Special Handling Packages",
            "Bulky/Oversized Packages"
    };

    private final String[] ROUTES = {
            "A1 (Colombo ↔ Kandy Road)",
            "A2 (Coastal Road: Colombo ↔ Galle ↔ Matara ↔ Hambantota ↔ Wellawaya)",
            "A3 (Colombo ↔ Negombo ↔ Chilaw ↔ Puttalam)",
            "A4 (High-Level Road: Colombo ↔ Ratnapura ↔ Badulla ↔ Batticaloa)",
            "A5 (Peradeniya ↔ Nuwara Eliya ↔ Badulla ↔ Chenkalady)",
            "A6 (Ambepussa ↔ Kurunegala ↔ Dambulla ↔ Trincomalee)",
            "A9 (Kandy ↔ Dambulla ↔ Vavuniya ↔ Jaffna)"
    };

    public ManageShipmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Input Form Panel ---
        JPanel inputFormPanel = new JPanel(new GridBagLayout());
        inputFormPanel.setBorder(BorderFactory.createTitledBorder("Shipment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Shipment ID (read-only for display/selection)
        gbc.gridx = 0; gbc.gridy = 0; inputFormPanel.add(new JLabel("Shipment ID (Auto):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        shipmentIdField = new JTextField(20);
        shipmentIdField.setEditable(false);
        inputFormPanel.add(shipmentIdField, gbc);
        gbc.gridwidth = 1;

        // Row 1: Tracking Number
        gbc.gridx = 0; gbc.gridy = 1; inputFormPanel.add(new JLabel("Tracking Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; trackingNumberField = new JTextField(20); inputFormPanel.add(trackingNumberField, gbc);

        // Row 2: Sender Name & Address
        gbc.gridx = 0; gbc.gridy = 2; inputFormPanel.add(new JLabel("Sender Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; senderNameField = new JTextField(20); inputFormPanel.add(senderNameField, gbc);
        gbc.gridx = 2; gbc.gridy = 2; inputFormPanel.add(new JLabel("Sender Address:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; senderAddressField = new JTextField(20); inputFormPanel.add(senderAddressField, gbc);

        // Row 3: Sender Contact
        gbc.gridx = 0; gbc.gridy = 3; inputFormPanel.add(new JLabel("Sender Contact:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; senderContactField = new JTextField(20); inputFormPanel.add(senderContactField, gbc);

        // Row 4: Receiver Name & Address
        gbc.gridx = 0; gbc.gridy = 4; inputFormPanel.add(new JLabel("Receiver Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; receiverNameField = new JTextField(20); inputFormPanel.add(receiverNameField, gbc);
        gbc.gridx = 2; gbc.gridy = 4; inputFormPanel.add(new JLabel("Receiver Address:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4; receiverAddressField = new JTextField(20); inputFormPanel.add(receiverAddressField, gbc);

        // Row 5: Receiver Contact
        gbc.gridx = 0; gbc.gridy = 5; inputFormPanel.add(new JLabel("Receiver Contact:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; receiverContactField = new JTextField(20); inputFormPanel.add(receiverContactField, gbc);

        // Row 6: Package Contents & Package Type (NEW)
        gbc.gridx = 0; gbc.gridy = 6; inputFormPanel.add(new JLabel("Package Contents:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 1; gbc.weighty = 0.5; // Reset gridwidth for contents area
        packageContentsArea = new JTextArea(3, 20);
        packageContentsArea.setLineWrap(true);
        packageContentsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(packageContentsArea);
        inputFormPanel.add(scrollPane, gbc);
        gbc.weighty = 0; // Reset weighty
        gbc.gridx = 2; gbc.gridy = 6; inputFormPanel.add(new JLabel("Package Type:"), gbc); // Label for new field
        gbc.gridx = 3; gbc.gridy = 6; packageTypeComboBox = new JComboBox<>(PACKAGE_TYPES); inputFormPanel.add(packageTypeComboBox, gbc); // ComboBox for new field

        // Row 7: Weight & Dimensions
        gbc.gridx = 0; gbc.gridy = 7; inputFormPanel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1; gbc.gridy = 7; weightKgField = new JTextField(20); inputFormPanel.add(weightKgField, gbc);
        gbc.gridx = 2; gbc.gridy = 7; inputFormPanel.add(new JLabel("Dimensions (LxWxH cm):"), gbc);
        gbc.gridx = 3; gbc.gridy = 7; dimensionsCmField = new JTextField(20); inputFormPanel.add(dimensionsCmField, gbc);

        // Row 8: Current Location & Route (NEW)
        gbc.gridx = 0; gbc.gridy = 8; inputFormPanel.add(new JLabel("Current Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8; currentLocationField = new JTextField(20); inputFormPanel.add(currentLocationField, gbc);
        gbc.gridx = 2; gbc.gridy = 8; inputFormPanel.add(new JLabel("Route:"), gbc); // Label for new field
        gbc.gridx = 3; gbc.gridy = 8; routeComboBox = new JComboBox<>(ROUTES); inputFormPanel.add(routeComboBox, gbc); // ComboBox for new field

        // Row 9: Current Status & Is Urgent (Re-added)
        gbc.gridx = 0; gbc.gridy = 9; inputFormPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 9;
        String[] statuses = {"Pending", "Picked Up", "In Transit", "Out for Delivery", "Delivered", "Cancelled"};
        currentStatusComboBox = new JComboBox<>(statuses);
        inputFormPanel.add(currentStatusComboBox, gbc);
        gbc.gridx = 2; gbc.gridy = 9; inputFormPanel.add(new JLabel("Is Urgent:"), gbc);
        gbc.gridx = 3; gbc.gridy = 9; isUrgentCheckBox = new JCheckBox(); inputFormPanel.add(isUrgentCheckBox, gbc);


        // Row 10: Estimated Delivery Time (Read-only, populated from Delivery)
        gbc.gridx = 0; gbc.gridy = 10; inputFormPanel.add(new JLabel("Est. Delivery Time:"), gbc);
        gbc.gridx = 1; gbc.gridy = 10; gbc.gridwidth = 3;
        estimatedDeliveryTimeField = new JTextField(20);
        estimatedDeliveryTimeField.setEditable(false);
        inputFormPanel.add(estimatedDeliveryTimeField, gbc);
        gbc.gridwidth = 1;

        add(inputFormPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Holds buttons and table) ---
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Shipment");
        updateButton = new JButton("Update Shipment");
        deleteButton = new JButton("Delete Shipment");
        clearButton = new JButton("Clear Form");
        refreshButton = new JButton("Refresh Shipments");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        mainContentPanel.add(buttonPanel, BorderLayout.NORTH);

        // --- Shipment Table ---
        String[] columnNames = {"Shipment ID", "Tracking No.", "Sender Name", "Sender Contact",
                "Receiver Name", "Receiver Contact", "Package Type", "Weight (kg)", "Dimensions (cm)",
                "Current Location", "Route", "Is Urgent", "Status", "Est. Delivery Time", "Date Created"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 11) { // "Is Urgent" column
                    return Boolean.class;
                }
                if (columnIndex == 7) { // "Weight (kg)" column
                    return Double.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        shipmentTable = new JTable(tableModel);
        shipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shipmentTable.getTableHeader().setReorderingAllowed(false);
        shipmentTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(shipmentTable);
        mainContentPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);


        // Add a ListSelectionListener to the table to populate form fields on row selection
        shipmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && shipmentTable.getSelectedRow() != -1) {
                // The controller will handle populating the form
            }
        });
    }


    // --- Getters for Form Fields ---
    public String getShipmentId() {
        return shipmentIdField.getText().trim();
    }

    public String getTrackingNumber() {
        return trackingNumberField.getText().trim();
    }

    public String getSenderName() {
        return senderNameField.getText().trim();
    }

    public String getSenderAddress() {
        return senderAddressField.getText().trim();
    }

    public String getSenderContact() {
        return senderContactField.getText().trim();
    }

    public String getReceiverName() {
        return receiverNameField.getText().trim();
    }

    public String getReceiverAddress() {
        return receiverAddressField.getText().trim();
    }

    public String getReceiverContact() {
        return receiverContactField.getText().trim();
    }

    public String getPackageContents() {
        return packageContentsArea.getText().trim();
    }

    public String getPackageType() {
        return (String) packageTypeComboBox.getSelectedItem();
    }

    public String getWeightKg() {
        return weightKgField.getText().trim();
    }

    public String getDimensionsCm() {
        return dimensionsCmField.getText().trim();
    }

    public String getCurrentLocation() {
        return currentLocationField.getText().trim();
    }

    public String getRoute() {
        return (String) routeComboBox.getSelectedItem();
    }

    public String getCurrentStatus() {
        return (String) currentStatusComboBox.getSelectedItem();
    }

    public boolean getIsUrgent() {
        return isUrgentCheckBox.isSelected();
    }

    public JTable getShipmentTable() {
        return shipmentTable;
    }

    // --- Methods to interact with the View from Controller ---

    /**
     * Clears all input fields in the form.
     */
    public void clearForm() {
        shipmentIdField.setText("");
        trackingNumberField.setText("");
        senderNameField.setText("");
        senderAddressField.setText("");
        senderContactField.setText("");
        receiverNameField.setText("");
        receiverAddressField.setText("");
        receiverContactField.setText("");
        packageContentsArea.setText("");
        packageTypeComboBox.setSelectedIndex(0);
        weightKgField.setText("");
        dimensionsCmField.setText("");
        currentLocationField.setText("");
        routeComboBox.setSelectedIndex(0);
        currentStatusComboBox.setSelectedIndex(0);
        estimatedDeliveryTimeField.setText("");
        isUrgentCheckBox.setSelected(false);
        shipmentTable.clearSelection();
    }

    /**
     * Populates the form fields with data from a given Shipment object and its associated Delivery.
     * @param shipment The Shipment object to display.
     * @param delivery The associated Delivery object (can be null if no delivery is scheduled yet).
     */
    public void populateForm(Shipment shipment, Delivery delivery) {
        if (shipment != null) {
            shipmentIdField.setText(shipment.getShipmentId());
            trackingNumberField.setText(shipment.getTrackingNumber());
            senderNameField.setText(shipment.getSenderName());
            senderAddressField.setText(shipment.getSenderAddress());
            senderContactField.setText(shipment.getSenderContact());
            receiverNameField.setText(shipment.getReceiverName());
            receiverAddressField.setText(shipment.getReceiverAddress());
            receiverContactField.setText(shipment.getReceiverContact());
            packageContentsArea.setText(shipment.getPackageContents());
            packageTypeComboBox.setSelectedItem(shipment.getPackageType());
            weightKgField.setText(String.valueOf(shipment.getWeightKg()));
            dimensionsCmField.setText(shipment.getDimensionsCm());
            currentLocationField.setText(shipment.getCurrentLocation());
            routeComboBox.setSelectedItem(shipment.getRoute());
            currentStatusComboBox.setSelectedItem(shipment.getCurrentStatus());
            isUrgentCheckBox.setSelected(shipment.isUrgent());

            // Populate Estimated Delivery Time from Delivery object
            if (delivery != null && delivery.getEstimatedArrivalTime() != null) {
                estimatedDeliveryTimeField.setText(delivery.getEstimatedArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            } else {
                estimatedDeliveryTimeField.setText("N/A");
            }
        } else {
            clearForm();
        }
    }

    /**
     * Displays a list of shipments in the table, including associated delivery details.
     * @param shipmentDetails A list of Object arrays, where each array contains shipment and delivery data.
     * Expected format: {Shipment, Delivery (can be null)}.
     */
    public void displayShipments(List<Object[]> shipmentDetails) {
        tableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter etaFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Object[] rowData : shipmentDetails) {
            Shipment shipment = (Shipment) rowData[0];
            Delivery delivery = (Delivery) rowData[1]; // Can be null

            Vector<Object> row = new Vector<>();
            row.add(shipment.getShipmentId());
            row.add(shipment.getTrackingNumber());
            row.add(shipment.getSenderName());
            row.add(shipment.getSenderContact());
            row.add(shipment.getReceiverName());
            row.add(shipment.getReceiverContact());
            row.add(shipment.getPackageType());
            row.add(shipment.getWeightKg());
            row.add(shipment.getDimensionsCm());
            row.add(shipment.getCurrentLocation());
            row.add(shipment.getRoute());
            row.add(shipment.isUrgent());
            row.add(shipment.getCurrentStatus());
            row.add(delivery != null && delivery.getEstimatedArrivalTime() != null ?
                    delivery.getEstimatedArrivalTime().format(etaFormatter) : "N/A");
            row.add(shipment.getCreatedAt() != null ? shipment.getCreatedAt().toLocalDate().format(dateFormatter) : "");

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

    // --- Getters for Buttons (to attach listeners in Controller) ---
    public JButton getAddButton() {
        return addButton;
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

    public JButton getRefreshButton() {
        return refreshButton;
    }
}