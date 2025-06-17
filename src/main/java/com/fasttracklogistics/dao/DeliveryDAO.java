

// src/main/java/com/fasttracklogistics/dao/DeliveryDAO.java

package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.Delivery;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Data Access Operations related to Deliveries.
 * Defines the contract for CRUD operations on Delivery objects.
 */
public interface DeliveryDAO {
    /**
     * Inserts a new delivery record into the database.
     * @param delivery The Delivery object to insert.
     * @return true if the insertion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean insertDelivery(Delivery delivery) throws SQLException;

    /**
     * Updates an existing delivery record in the database.
     * @param delivery The Delivery object with updated details.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateDelivery(Delivery delivery) throws SQLException;

    /**
     * Deletes a delivery record from the database by its ID.
     * @param deliveryId The ID of the delivery to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean deleteDelivery(String deliveryId) throws SQLException;

    /**
     * Finds a delivery record by its unique delivery ID.
     * @param deliveryId The ID of the delivery to find.
     * @return The Delivery object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Delivery findDeliveryById(String deliveryId) throws SQLException;

    /**
     * Finds a delivery record by its associated shipment ID.
     * @param shipmentId The ID of the shipment linked to the delivery.
     * @return The Delivery object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Delivery findDeliveryByShipmentId(String shipmentId) throws SQLException;

    /**
     * Retrieves all delivery records from the database.
     * @return A List of all Delivery objects.
     * @throws SQLException If a database access error occurs.
     */
    List<Delivery> findAllDeliveries() throws SQLException;

    /**
     * Retrieves delivery records for a specific personnel.
     * @param personnelId The ID of the personnel.
     * @return A List of Delivery objects assigned to the given personnel.
     * @throws SQLException If a database access error occurs.
     */
    List<Delivery> findDeliveriesByPersonnelId(String personnelId) throws SQLException;
}
