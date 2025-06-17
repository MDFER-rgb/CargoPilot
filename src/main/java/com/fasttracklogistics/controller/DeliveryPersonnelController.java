
// src/main/java/com/fasttracklogistics/controller/DeliveryPersonnelController.java

package com.fasttracklogistics.controller;

import com.fasttracklogistics.dao.DeliveryPersonnelDAO;
import com.fasttracklogistics.dao.DeliveryPersonnelDAOImpl;
import com.fasttracklogistics.model.DeliveryPersonnel;
import com.fasttracklogistics.view.ManageDeliveryPersonnelPanel;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for managing delivery personnel operations.
 * This class handles user interactions from ManageDeliveryPersonnelPanel,
 * performs validation, and interacts with the DeliveryPersonnelDAO.
 */
public class DeliveryPersonnelController {

    private ManageDeliveryPersonnelPanel view;
    private DeliveryPersonnelDAO personnelDAO;

    public DeliveryPersonnelController(ManageDeliveryPersonnelPanel view) {
        this.view = view;
        this.personnelDAO = new DeliveryPersonnelDAOImpl();

        // Attach action listeners to buttons
        this.view.getAddButton().addActionListener(e -> addPersonnel());
        this.view.getUpdateButton().addActionListener(e -> updatePersonnel());
        this.view.getDeleteButton().addActionListener(e -> deletePersonnel());
        this.view.getClearButton().addActionListener(e -> clearForm());
        this.view.getRefreshButton().addActionListener(e -> loadPersonnel());

        // Attach a listener to the table for row selection
        this.view.getPersonnelTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && view.getPersonnelTable().getSelectedRow() != -1) {
                    loadSelectedPersonnelToForm();
                }
            }
        });

        // Load initial data into the table when the controller is initialized
        loadPersonnel();
    }

    /**
     * Loads all delivery personnel records from the database
     * and displays them in the view's table.
     */
    public void loadPersonnel() {
        try {
            List<DeliveryPersonnel> personnelList = personnelDAO.findAllPersonnel();
            view.displayPersonnel(personnelList);
        } catch (SQLException ex) {
            view.showErrorMessage("Error loading personnel: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles adding a new delivery personnel based on user input from the form.
     */
    private void addPersonnel() {
        // Retrieve data from view
        String employeeId = view.getEmployeeId();
        String name = view.getName();
        String contactNumber = view.getContactNumber();
        String email = view.getEmail();
        String vehicleType = view.getVehicleType();
        String licenseNumber = view.getLicenseNumber();
        String availabilityStatus = view.getAvailabilityStatus();

        // Basic validation
        if (employeeId.isEmpty() || name.isEmpty() || contactNumber.isEmpty() || email.isEmpty()) {
            view.showErrorMessage("Please fill in all required fields (Employee ID, Name, Contact, Email).");
            return;
        }

        // Create a new DeliveryPersonnel object (ID will be set by DAO)
        DeliveryPersonnel newPersonnel = new DeliveryPersonnel();
        newPersonnel.setEmployeeId(employeeId);
        newPersonnel.setName(name);
        newPersonnel.setContactNumber(contactNumber);
        newPersonnel.setEmail(email);
        newPersonnel.setVehicleType(vehicleType);
        newPersonnel.setLicenseNumber(licenseNumber);
        newPersonnel.setAvailabilityStatus(availabilityStatus);

        try {
            // Check if employee ID already exists
            if (personnelDAO.findPersonnelByEmployeeId(employeeId) != null) {
                view.showErrorMessage("Employee ID already exists. Please use a unique Employee ID.");
                return;
            }

            boolean success = personnelDAO.insertPersonnel(newPersonnel);
            if (success) {
                view.showMessage("Delivery personnel added successfully!");
                clearForm();
                loadPersonnel(); // Refresh table
            } else {
                view.showErrorMessage("Failed to add delivery personnel.");
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error adding personnel: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles updating an existing delivery personnel based on user input from the form.
     */
    private void updatePersonnel() {
        String personnelId = view.getPersonnelId();
        if (personnelId.isEmpty()) {
            view.showErrorMessage("Please select a personnel to update from the table.");
            return;
        }

        // Retrieve updated data from view
        String employeeId = view.getEmployeeId();
        String name = view.getName();
        String contactNumber = view.getContactNumber();
        String email = view.getEmail();
        String vehicleType = view.getVehicleType();
        String licenseNumber = view.getLicenseNumber();
        String availabilityStatus = view.getAvailabilityStatus();

        // Basic validation
        if (employeeId.isEmpty() || name.isEmpty() || contactNumber.isEmpty() || email.isEmpty()) {
            view.showErrorMessage("Please ensure all required fields are filled.");
            return;
        }

        // Create DeliveryPersonnel object with updated data
        DeliveryPersonnel updatedPersonnel = new DeliveryPersonnel();
        updatedPersonnel.setPersonnelId(personnelId);
        updatedPersonnel.setEmployeeId(employeeId);
        updatedPersonnel.setName(name);
        updatedPersonnel.setContactNumber(contactNumber);
        updatedPersonnel.setEmail(email);
        updatedPersonnel.setVehicleType(vehicleType);
        updatedPersonnel.setLicenseNumber(licenseNumber);
        updatedPersonnel.setAvailabilityStatus(availabilityStatus);

        try {
            // Check if employee ID is being changed to an existing one (that is not this personnel's own)
            DeliveryPersonnel existingByEmployeeId = personnelDAO.findPersonnelByEmployeeId(employeeId);
            if (existingByEmployeeId != null && !existingByEmployeeId.getPersonnelId().equals(personnelId)) {
                view.showErrorMessage("Another personnel already uses this Employee ID. Please use a unique Employee ID.");
                return;
            }

            boolean success = personnelDAO.updatePersonnel(updatedPersonnel);
            if (success) {
                view.showMessage("Delivery personnel updated successfully!");
                clearForm();
                loadPersonnel(); // Refresh table
            } else {
                view.showErrorMessage("Failed to update delivery personnel. Personnel ID might not exist.");
            }
        } catch (SQLException ex) {
            view.showErrorMessage("Database error updating personnel: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Handles deleting a delivery personnel based on the selected row in the table.
     */
    private void deletePersonnel() {
        String personnelId = view.getPersonnelId();
        if (personnelId.isEmpty()) {
            view.showErrorMessage("Please select a personnel to delete from the table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete personnel with ID: " + personnelId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = personnelDAO.deletePersonnel(personnelId);
                if (success) {
                    view.showMessage("Delivery personnel deleted successfully!");
                    clearForm();
                    loadPersonnel(); // Refresh table
                } else {
                    view.showErrorMessage("Failed to delete personnel. Personnel ID might not exist.");
                }
            } catch (SQLException ex) {
                view.showErrorMessage("Database error deleting personnel: " + ex.getMessage());
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
     * Loads the details of the selected delivery personnel from the table into the form fields.
     */
    private void loadSelectedPersonnelToForm() {
        int selectedRow = view.getPersonnelTable().getSelectedRow();
        if (selectedRow >= 0) {
            String personnelId = (String) view.getPersonnelTable().getModel().getValueAt(selectedRow, 0);
            try {
                DeliveryPersonnel selectedPersonnel = personnelDAO.findPersonnelById(personnelId);
                if (selectedPersonnel != null) {
                    view.populateForm(selectedPersonnel);
                } else {
                    view.showErrorMessage("Could not find selected personnel in database.");
                    clearForm();
                }
            } catch (SQLException ex) {
                view.showErrorMessage("Error loading personnel details: " + ex.getMessage());
                ex.printStackTrace();
                clearForm();
            }
        }
    }
}
