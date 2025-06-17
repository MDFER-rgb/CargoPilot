
// src/main/java/com/fasttracklogistics/model/Delivery.java

package com.fasttracklogistics.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a Delivery record, linking a shipment to personnel and tracking its progress.
 */
public class Delivery {
    private String deliveryId;
    private String shipmentId; // FK to Shipments
    private String personnelId; // FK to DeliveryPersonnel, can be null
    private LocalDate scheduledDate;
    private String scheduledTimeSlot;
    private LocalDateTime actualDeliveryDate;
    private String deliveryStatus; // e.g., 'Scheduled', 'Assigned', 'Picked Up', 'En Route', 'Delivered', 'Delayed'
    private LocalDateTime estimatedArrivalTime;
    private String delayReason;

    // Constructors
    public Delivery() {
        // Default constructor
    }

    public Delivery(String deliveryId, String shipmentId, String personnelId, LocalDate scheduledDate,
                    String scheduledTimeSlot, LocalDateTime actualDeliveryDate, String deliveryStatus,
                    LocalDateTime estimatedArrivalTime, String delayReason) {
        this.deliveryId = deliveryId;
        this.shipmentId = shipmentId;
        this.personnelId = personnelId;
        this.scheduledDate = scheduledDate;
        this.scheduledTimeSlot = scheduledTimeSlot;
        this.actualDeliveryDate = actualDeliveryDate;
        this.deliveryStatus = deliveryStatus;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.delayReason = delayReason;
    }

    // Getters
    public String getDeliveryId() {
        return deliveryId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public String getScheduledTimeSlot() {
        return scheduledTimeSlot;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public String getDelayReason() {
        return delayReason;
    }

    // Setters
    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setScheduledTimeSlot(String scheduledTimeSlot) {
        this.scheduledTimeSlot = scheduledTimeSlot;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId='" + deliveryId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", personnelId='" + personnelId + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                '}';
    }
}

