
// src/main/java/com/fasttracklogistics/view/ManageDeliveryPersonnelPanel.java

package com.fasttracklogistics.view;

import com.fasttracklogistics.model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * JPanel for managing delivery personnel records (Add, Update, Remove).
 * This class represents the 'View' component for delivery personnel management.
 */
public class ManageDeliveryPersonnelPanel extends JPanel {

    private JTextField personnelIdField;
    private JTextField employeeIdField;
    private JTextField nameField;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextField vehicleTypeField;
    private JTextField licenseNumberField; // NEW FIELD
    private JComboBox<String> availabilityStatusComboBox;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton; // NEW: Refresh button for personnel

    private JTable personnelTable;
    private DefaultTableModel tableModel;

    public ManageDeliveryPersonnelPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Input Form Panel ---
        JPanel inputFormPanel = new JPanel(new GridBagLayout());
        inputFormPanel.setBorder(BorderFactory.createTitledBorder("Delivery Personnel Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Personnel ID (read-only for display/selection)
        gbc.gridx = 0; gbc.gridy = 0; inputFormPanel.add(new JLabel("Personnel ID (Auto):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        personnelIdField = new JTextField(20);
        personnelIdField.setEditable(false); // ID is auto-generated or selected
        inputFormPanel.add(personnelIdField, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Row 1: Employee ID
        gbc.gridx = 0; gbc.gridy = 1; inputFormPanel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; employeeIdField = new JTextField(20); inputFormPanel.add(employeeIdField, gbc);

        // Row 2: Name
        gbc.gridx = 0; gbc.gridy = 2; inputFormPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; nameField = new JTextField(20); inputFormPanel.add(nameField, gbc);

        // Row 3: Contact Number & Email
        gbc.gridx = 0; gbc.gridy = 3; inputFormPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; contactNumberField = new JTextField(20); inputFormPanel.add(contactNumberField, gbc);
        gbc.gridx = 2; gbc.gridy = 3; inputFormPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; emailField = new JTextField(20); inputFormPanel.add(emailField, gbc);

        // Row 4: Vehicle Type & License Number (NEW ROW)
        gbc.gridx = 0; gbc.gridy = 4; inputFormPanel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; vehicleTypeField = new JTextField(20); inputFormPanel.add(vehicleTypeField, gbc);
        gbc.gridx = 2; gbc.gridy = 4; inputFormPanel.add(new JLabel("License Number:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4; licenseNumberField = new JTextField(20); inputFormPanel.add(licenseNumberField, gbc); // NEW FIELD

        // Row 5: Availability Status (MOVED TO NEW ROW)
        gbc.gridx = 0; gbc.gridy = 5; inputFormPanel.add(new JLabel("Availability Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 3; // Take more space
        String[] statuses = {"Available", "On Route", "Off Duty"};
        availabilityStatusComboBox = new JComboBox<>(statuses);
        inputFormPanel.add(availabilityStatusComboBox, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        add(inputFormPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Holds buttons and table) ---
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Personnel");
        updateButton = new JButton("Update Personnel");
        deleteButton = new JButton("Delete Personnel");
        clearButton = new JButton("Clear Form");
        refreshButton = new JButton("Refresh Personnel"); // NEW: Refresh button for personnel

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton); // Add refresh button to the panel
        mainContentPanel.add(buttonPanel, BorderLayout.NORTH);

        // --- Personnel Table ---
        String[] columnNames = {"Personnel ID", "Employee ID", "Name", "Contact Number", "Email", "Vehicle Type", "License Number", "Availability Status"}; // UPDATED Column Names
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        personnelTable = new JTable(tableModel);
        personnelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personnelTable.getTableHeader().setReorderingAllowed(false);
        personnelTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(personnelTable);
        mainContentPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER); // Add the mainContentPanel to the CENTER of the primary JPanel


        // Add a ListSelectionListener to the table to populate form fields on row selection
        personnelTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && personnelTable.getSelectedRow() != -1) {
                // The controller will handle populating the form
                // The controller listens to this event and calls populateForm() after fetching data.
            }
        });
    }

    // --- Getters for Form Fields ---
    public String getPersonnelId() {
        return personnelIdField.getText().trim();
    }

    public String getEmployeeId() {
        return employeeIdField.getText().trim();
    }

    public String getName() {
        return nameField.getText().trim();
    }

    public String getContactNumber() {
        return contactNumberField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getVehicleType() {
        return vehicleTypeField.getText().trim();
    }

    public String getLicenseNumber() { // NEW GETTER
        return licenseNumberField.getText().trim();
    }

    public String getAvailabilityStatus() {
        return (String) availabilityStatusComboBox.getSelectedItem();
    }

    public JTable getPersonnelTable() {
        return personnelTable;
    }

    // --- Methods to interact with the View from Controller ---

    /**
     * Clears all input fields in the form.
     */
    public void clearForm() {
        personnelIdField.setText("");
        employeeIdField.setText("");
        nameField.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        vehicleTypeField.setText("");
        licenseNumberField.setText(""); // NEW
        availabilityStatusComboBox.setSelectedIndex(0); // Set to "Available"
        personnelTable.clearSelection();
    }

    /**
     * Populates the form fields with data from a given DeliveryPersonnel object.
     * @param personnel The DeliveryPersonnel object to display.
     */
    public void populateForm(DeliveryPersonnel personnel) {
        if (personnel != null) {
            personnelIdField.setText(personnel.getPersonnelId());
            employeeIdField.setText(personnel.getEmployeeId());
            nameField.setText(personnel.getName());
            contactNumberField.setText(personnel.getContactNumber());
            emailField.setText(personnel.getEmail());
            vehicleTypeField.setText(personnel.getVehicleType());
            licenseNumberField.setText(personnel.getLicenseNumber()); // NEW
            availabilityStatusComboBox.setSelectedItem(personnel.getAvailabilityStatus());
        } else {
            clearForm();
        }
    }

    /**
     * Displays a list of delivery personnel in the table.
     * @param personnelList The list of DeliveryPersonnel objects to display.
     */
    public void displayPersonnel(List<DeliveryPersonnel> personnelList) {
        tableModel.setRowCount(0); // Clear existing data
        for (DeliveryPersonnel personnel : personnelList) {
            Vector<Object> row = new Vector<>();
            row.add(personnel.getPersonnelId());
            row.add(personnel.getEmployeeId());
            row.add(personnel.getName());
            row.add(personnel.getContactNumber());
            row.add(personnel.getEmail());
            row.add(personnel.getVehicleType());
            row.add(personnel.getLicenseNumber()); // NEW
            row.add(personnel.getAvailabilityStatus());
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

    public JButton getRefreshButton() { // NEW: Getter for the new refresh button
        return refreshButton;
    }
}
