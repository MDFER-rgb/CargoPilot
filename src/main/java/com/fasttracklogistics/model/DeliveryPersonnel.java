

// src/main/java/com/fasttracklogistics/model/DeliveryPersonnel.java

package com.fasttracklogistics.model;

import java.util.UUID;

/**
 * Represents a Delivery Personnel (Driver/Courier) entity.
 */
public class DeliveryPersonnel {
    private String personnelId;
    private String employeeId;
    private String name;
    private String contactNumber;
    private String email;
    private String vehicleType;
    private String licenseNumber;
    private String availabilityStatus; // e.g., 'Available', 'On Route', 'Off Duty'

    // Constructors
    public DeliveryPersonnel() {
        // Default constructor
    }

    public DeliveryPersonnel(String personnelId, String employeeId, String name,
                             String contactNumber, String email, String vehicleType,
                             String licenseNumber,
                             String availabilityStatus) {
        this.personnelId = personnelId;
        this.employeeId = employeeId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.availabilityStatus = availabilityStatus;
    }

    // Getters
    public String getPersonnelId() {
        return personnelId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    // Setters
    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        // Updated toString to be more descriptive for display in UI components
        return name + " (EmpID: " + employeeId + " - " + availabilityStatus + ")";
    }
}

