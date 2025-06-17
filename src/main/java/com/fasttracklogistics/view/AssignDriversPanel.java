// src/main/java/com/fasttracklogistics/view/AssignDriversPanel.java

package com.fasttracklogistics.view;

import com.fasttracklogistics.model.Shipment;
import com.fasttracklogistics.model.Delivery;
import com.fasttracklogistics.model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

/**
 * JPanel for assigning drivers to shipments.
 * This panel displays unassigned shipments, available personnel, and assigned deliveries,
 * facilitating the allocation of drivers.
 */
public class AssignDriversPanel extends JPanel {

    private JTable unassignedShipmentsTable;
    private DefaultTableModel unassignedShipmentsTableModel;
    private JTable availablePersonnelTable;
    private DefaultTableModel availablePersonnelTableModel;
    private JTable assignedDeliveriesTable;
    private DefaultTableModel assignedDeliveriesTableModel;

    private JButton assignButton;
    private JButton refreshUnassignedButton;
    private JButton refreshPersonnelButton;
    private JButton refreshAssignedButton;

    public AssignDriversPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Assign Drivers to Shipments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // --- Split Pane for Unassigned Shipments and Personnel ---
        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplitPane.setResizeWeight(0.5); // Equal size initially

        // Panel for Unassigned Shipments
        JPanel unassignedShipmentsPanel = new JPanel(new BorderLayout(5, 5));
        unassignedShipmentsPanel.setBorder(BorderFactory.createTitledBorder("Unassigned Shipments"));
        String[] unassignedShipmentColumns = {"Shipment ID", "Tracking No.", "Receiver Address", "Package Type", "Urgent"};
        unassignedShipmentsTableModel = new DefaultTableModel(unassignedShipmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == unassignedShipmentColumns.length - 1) { // "Urgent" column
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        unassignedShipmentsTable = new JTable(unassignedShipmentsTableModel);
        unassignedShipmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unassignedShipmentsTable.setAutoCreateRowSorter(true);
        unassignedShipmentsPanel.add(new JScrollPane(unassignedShipmentsTable), BorderLayout.CENTER);
        refreshUnassignedButton = new JButton("Refresh Unassigned");
        JPanel unassignedButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        unassignedButtonPanel.add(refreshUnassignedButton);
        unassignedShipmentsPanel.add(unassignedButtonPanel, BorderLayout.SOUTH);
        topSplitPane.setLeftComponent(unassignedShipmentsPanel);

        // Panel for Available Personnel
        JPanel availablePersonnelPanel = new JPanel(new BorderLayout(5, 5));
        availablePersonnelPanel.setBorder(BorderFactory.createTitledBorder("Available Personnel"));
        String[] availablePersonnelColumns = {"Personnel ID", "Employee ID", "Name", "Vehicle Type", "Status"};
        availablePersonnelTableModel = new DefaultTableModel(availablePersonnelColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availablePersonnelTable = new JTable(availablePersonnelTableModel);
        availablePersonnelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availablePersonnelTable.setAutoCreateRowSorter(true);
        availablePersonnelPanel.add(new JScrollPane(availablePersonnelTable), BorderLayout.CENTER);
        refreshPersonnelButton = new JButton("Refresh Personnel");
        JPanel personnelButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        personnelButtonPanel.add(refreshPersonnelButton);
        availablePersonnelPanel.add(personnelButtonPanel, BorderLayout.SOUTH);
        topSplitPane.setRightComponent(availablePersonnelPanel);

        // --- Assign Button ---
        JPanel assignButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        assignButton = new JButton("Assign Driver to Selected Shipment");
        assignButtonPanel.add(assignButton);

        // --- Assigned Deliveries Panel ---
        JPanel assignedDeliveriesPanel = new JPanel(new BorderLayout(5, 5));
        assignedDeliveriesPanel.setBorder(BorderFactory.createTitledBorder("Assigned Deliveries"));
        String[] assignedDeliveryColumns = {"Delivery ID", "Shipment Tracking No.", "Assigned Personnel", "Scheduled Date", "Status"};
        assignedDeliveriesTableModel = new DefaultTableModel(assignedDeliveryColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignedDeliveriesTable = new JTable(assignedDeliveriesTableModel);
        assignedDeliveriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignedDeliveriesTable.setAutoCreateRowSorter(true);
        assignedDeliveriesPanel.add(new JScrollPane(assignedDeliveriesTable), BorderLayout.CENTER);
        refreshAssignedButton = new JButton("Refresh Assigned Deliveries");
        JPanel assignedButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        assignedButtonPanel.add(refreshAssignedButton);
        assignedDeliveriesPanel.add(assignedButtonPanel, BorderLayout.SOUTH);


        // --- Main Layout ---
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setTopComponent(topSplitPane);
        mainSplitPane.setBottomComponent(assignedDeliveriesPanel);
        mainSplitPane.setResizeWeight(0.5); // Equal size initially

        add(mainSplitPane, BorderLayout.CENTER);
        add(assignButtonPanel, BorderLayout.SOUTH); // Place assign button at the bottom

        // Initial setup for the split pane divider location
        SwingUtilities.invokeLater(() -> topSplitPane.setDividerLocation(0.5));
        SwingUtilities.invokeLater(() -> mainSplitPane.setDividerLocation(0.5));
    }

    // --- Display Methods ---

    /**
     * Displays a list of unassigned shipments in the 'Unassigned Shipments' table.
     * @param shipments A list of Shipment objects that are currently unassigned.
     */
    public void displayUnassignedShipments(List<Shipment> shipments) {
        unassignedShipmentsTableModel.setRowCount(0); // Clear existing data
        for (Shipment shipment : shipments) {
            Vector<Object> row = new Vector<>();
            row.add(shipment.getShipmentId());
            row.add(shipment.getTrackingNumber());
            row.add(shipment.getReceiverAddress());
            row.add(shipment.getPackageType());
            row.add(shipment.isUrgent()); // Display urgent status
            unassignedShipmentsTableModel.addRow(row);
        }
    }

    /**
     * Displays a list of available personnel in the 'Available Personnel' table.
     * @param personnelList A list of DeliveryPersonnel objects that are available.
     */
    public void displayAvailablePersonnel(List<DeliveryPersonnel> personnelList) {
        availablePersonnelTableModel.setRowCount(0); // Clear existing data
        for (DeliveryPersonnel personnel : personnelList) {
            Vector<Object> row = new Vector<>();
            row.add(personnel.getPersonnelId());
            row.add(personnel.getEmployeeId());
            row.add(personnel.getName());
            row.add(personnel.getVehicleType());
            row.add(personnel.getAvailabilityStatus());
            availablePersonnelTableModel.addRow(row);
        }
    }

    /**
     * Displays a list of assigned deliveries in the 'Assigned Deliveries' table.
     * @param deliveryDetails A list of Object arrays, where each array contains Delivery, Shipment, and DeliveryPersonnel.
     * Expected format: {Delivery, Shipment, DeliveryPersonnel (can be null if unassigned delivery is shown here)}.
     */
    public void displayAssignedDeliveries(List<Object[]> deliveryDetails) {
        assignedDeliveriesTableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Object[] rowData : deliveryDetails) {
            Delivery delivery = (Delivery) rowData[0];
            Shipment shipment = (Shipment) rowData[1]; // Will not be null here
            DeliveryPersonnel personnel = (DeliveryPersonnel) rowData[2]; // Can be null

            Vector<Object> row = new Vector<>();
            row.add(delivery.getDeliveryId());
            row.add(shipment != null ? shipment.getTrackingNumber() : "N/A"); // Should not be N/A for assigned
            row.add(personnel != null ? personnel.getName() + " (" + personnel.getEmployeeId() + ")" : "Unassigned");
            row.add(delivery.getScheduledDate() != null ? delivery.getScheduledDate().format(dateFormatter) : "N/A");
            row.add(delivery.getDeliveryStatus());
            assignedDeliveriesTableModel.addRow(row);
        }
    }

    // --- Getters for selected items ---

    /**
     * Returns the Shipment ID of the currently selected unassigned shipment.
     * @return The shipment ID string, or null if no row is selected.
     */
    public String getSelectedUnassignedShipmentId() {
        int selectedRow = unassignedShipmentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) unassignedShipmentsTableModel.getValueAt(selectedRow, 0); // Shipment ID column
        }
        return null;
    }

    /**
     * Returns the Personnel ID of the currently selected available personnel.
     * @return The personnel ID string, or null if no row is selected.
     */
    public String getSelectedAvailablePersonnelId() {
        int selectedRow = availablePersonnelTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) availablePersonnelTableModel.getValueAt(selectedRow, 0); // Personnel ID column
        }
        return null;
    }

    /**
     * Returns the Delivery ID of the currently selected assigned delivery.
     * @return The delivery ID string, or null if no row is selected.
     */
    public String getSelectedAssignedDeliveryId() {
        int selectedRow = assignedDeliveriesTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) assignedDeliveriesTableModel.getValueAt(selectedRow, 0); // Delivery ID column
        }
        return null;
    }


    // --- UI Utility Methods ---

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
    public JButton getAssignButton() {
        return assignButton;
    }

    public JButton getRefreshUnassignedButton() {
        return refreshUnassignedButton;
    }

    public JButton getRefreshPersonnelButton() {
        return refreshPersonnelButton;
    }

    public JButton getRefreshAssignedButton() {
        return refreshAssignedButton;
    }

    // --- Table Getters for Controller to attach Listeners ---
    public JTable getUnassignedShipmentsTable() {
        return unassignedShipmentsTable;
    }

    public JTable getAvailablePersonnelTable() {
        return availablePersonnelTable;
    }

    public JTable getAssignedDeliveriesTable() {
        return assignedDeliveriesTable;
    }
}