
// src/main/java/com/fasttracklogistics/model/Shipment.java

package com.fasttracklogistics.model;

import java.time.LocalDateTime;

/**
 * Represents a Shipment entity in the FastTrack Logistics system.
 */
public class Shipment {
    private String shipmentId;
    private String trackingNumber;
    private String senderName;
    private String senderAddress;
    private String senderContact;
    private String receiverName;
    private String receiverAddress;
    private String receiverContact;
    private String packageContents;
    private String packageType;
    private double weightKg;
    private String dimensionsCm;
    private String currentLocation;
    private String route;
    private String currentStatus;
    private boolean isUrgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Shipment() {
        // Default constructor
    }

    public Shipment(String shipmentId, String trackingNumber, String senderName, String senderAddress, String senderContact,
                    String receiverName, String receiverAddress, String receiverContact, String packageContents,
                    String packageType,
                    double weightKg, String dimensionsCm, String currentLocation,
                    String route,
                    String currentStatus, boolean isUrgent,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.shipmentId = shipmentId;
        this.trackingNumber = trackingNumber;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.senderContact = senderContact;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.receiverContact = receiverContact;
        this.packageContents = packageContents;
        this.packageType = packageType;
        this.weightKg = weightKg;
        this.dimensionsCm = dimensionsCm;
        this.currentLocation = currentLocation;
        this.route = route;
        this.currentStatus = currentStatus;
        this.isUrgent = isUrgent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getShipmentId() {
        return shipmentId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getSenderContact() {
        return senderContact;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getReceiverContact() {
        return receiverContact;
    }

    public String getPackageContents() {
        return packageContents;
    }

    public String getPackageType() {
        return packageType;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getDimensionsCm() {
        return dimensionsCm;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getRoute() {
        return route;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setSenderContact(String senderContact) {
        this.senderContact = senderContact;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setReceiverContact(String receiverContact) {
        this.receiverContact = receiverContact;
    }

    public void setPackageContents(String packageContents) {
        this.packageContents = packageContents;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public void setDimensionsCm(String dimensionsCm) {
        this.dimensionsCm = dimensionsCm;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId='" + shipmentId + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", packageType='" + packageType + '\'' +
                ", route='" + route + '\'' +
                ", isUrgent=" + isUrgent +
                '}';
    }
}
