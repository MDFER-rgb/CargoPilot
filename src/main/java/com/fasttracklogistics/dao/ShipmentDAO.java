
// src/main/java/com/fasttracklogistics/dao/ShipmentDAO.java

package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.Shipment;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Data Access Operations related to Shipments.
 * Defines the contract for CRUD operations on Shipment objects.
 */
public interface ShipmentDAO {
    /**
     * Inserts a new shipment record into the database.
     * @param shipment The Shipment object to insert.
     * @return true if the insertion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean insertShipment(Shipment shipment) throws SQLException;

    /**
     * Updates an existing shipment record in the database.
     * @param shipment The Shipment object with updated details.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateShipment(Shipment shipment) throws SQLException;

    /**
     * Deletes a shipment record from the database by its ID.
     * @param shipmentId The ID of the shipment to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean deleteShipment(String shipmentId) throws SQLException;

    /**
     * Finds a shipment record by its unique shipment ID.
     * @param shipmentId The ID of the shipment to find.
     * @return The Shipment object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Shipment findShipmentById(String shipmentId) throws SQLException;

    /**
     * Finds a shipment record by its unique tracking number.
     * @param trackingNumber The tracking number of the shipment to find.
     * @return The Shipment object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Shipment findShipmentByTrackingNumber(String trackingNumber) throws SQLException;

    /**
     * Retrieves all shipment records from the database.
     * @return A List of all Shipment objects.
     * @throws SQLException If a database access error occurs.
     */
    List<Shipment> findAllShipments() throws SQLException;

    /**
     * Retrieves all unscheduled shipment records from the database.
     * A shipment is considered unscheduled if it does not have an associated delivery.
     * @return A List of all unscheduled Shipment objects.
     * @throws SQLException If a database access error occurs.
     */
    List<Shipment> findUnscheduledShipments() throws SQLException;
}
