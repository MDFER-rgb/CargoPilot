
// src/main/java/com/fasttracklogistics/dao/DeliveryPersonnelDAO.java

package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.DeliveryPersonnel;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for Data Access Operations related to Delivery Personnel.
 * Defines the contract for CRUD operations on DeliveryPersonnel objects.
 */
public interface DeliveryPersonnelDAO {
    /**
     * Inserts a new delivery personnel record into the database.
     * @param personnel The DeliveryPersonnel object to insert.
     * @return true if the insertion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean insertPersonnel(DeliveryPersonnel personnel) throws SQLException;

    /**
     * Updates an existing delivery personnel record in the database.
     * @param personnel The DeliveryPersonnel object with updated details.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updatePersonnel(DeliveryPersonnel personnel) throws SQLException;

    /**
     * Deletes a delivery personnel record from the database by its ID.
     * @param personnelId The ID of the personnel to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean deletePersonnel(String personnelId) throws SQLException;

    /**
     * Finds a delivery personnel record by its unique personnel ID.
     * @param personnelId The ID of the personnel to find.
     * @return The DeliveryPersonnel object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    DeliveryPersonnel findPersonnelById(String personnelId) throws SQLException;

    /**
     * Finds a delivery personnel record by its unique employee ID.
     * @param employeeId The employee ID of the personnel to find.
     * @return The DeliveryPersonnel object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    DeliveryPersonnel findPersonnelByEmployeeId(String employeeId) throws SQLException;

    /**
     * Retrieves all delivery personnel records from the database.
     * @return A List of all DeliveryPersonnel objects.
     * @throws SQLException If a database access error occurs.
     */
    List<DeliveryPersonnel> findAllPersonnel() throws SQLException;

    /**
     * Retrieves all available delivery personnel records from the database.
     * @return A List of available DeliveryPersonnel objects.
     * @throws SQLException If a database access error occurs.
     */
    List<DeliveryPersonnel> findAvailablePersonnel() throws SQLException;
}

