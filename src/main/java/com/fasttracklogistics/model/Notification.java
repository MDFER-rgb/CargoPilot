

// src/main/java/com/fasttracklogistics/dao/impl/ShipmentDAOImpl.java
// Note: You would typically put DAO implementations in a subpackage like 'impl'
// However, for simplicity and adherence to the exact diagram, I'll place it directly in 'dao'.

package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.Shipment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs

/**
 * JDBC implementation of the ShipmentDAO interface for MySQL.
 */
public class ShipmentDAOImpl implements ShipmentDAO {

    @Override
    public boolean insertShipment(Shipment shipment) throws SQLException {
        String sql = "INSERT INTO Shipments (shipment_id, tracking_number, sender_name, sender_address, sender_contact, " +
                "receiver_name, receiver_address, receiver_contact, package_contents, package_type, weight_kg, dimensions_cm, " +
                "current_location, route, current_status, is_urgent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            // Generate a unique ID if not already set (e.g., for new shipments)
            if (shipment.getShipmentId() == null || shipment.getShipmentId().isEmpty()) {
                shipment.setShipmentId(UUID.randomUUID().toString());
            }
            if (shipment.getTrackingNumber() == null || shipment.getTrackingNumber().isEmpty()) {
                shipment.setTrackingNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            if (shipment.getCurrentStatus() == null || shipment.getCurrentStatus().isEmpty()) {
                shipment.setCurrentStatus("Pending");
            }

            // Set parameters for the prepared statement
            pstmt.setString(1, shipment.getShipmentId());
            pstmt.setString(2, shipment.getTrackingNumber());
            pstmt.setString(3, shipment.getSenderName());
            pstmt.setString(4, shipment.getSenderAddress());
            pstmt.setString(5, shipment.getSenderContact());
            pstmt.setString(6, shipment.getReceiverName());
            pstmt.setString(7, shipment.getReceiverAddress());
            pstmt.setString(8, shipment.getReceiverContact());
            pstmt.setString(9, shipment.getPackageContents());
            pstmt.setString(10, shipment.getPackageType());
            pstmt.setDouble(11, shipment.getWeightKg());
            pstmt.setString(12, shipment.getDimensionsCm());
            pstmt.setString(13, shipment.getCurrentLocation());
            pstmt.setString(14, shipment.getRoute());
            pstmt.setString(15, shipment.getCurrentStatus());
            pstmt.setBoolean(16, shipment.isUrgent());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateShipment(Shipment shipment) throws SQLException {
        String sql = "UPDATE Shipments SET tracking_number = ?, sender_name = ?, sender_address = ?, sender_contact = ?, " +
                "receiver_name = ?, receiver_address = ?, receiver_contact = ?, package_contents = ?, package_type = ?, weight_kg = ?, " +
                "dimensions_cm = ?, current_location = ?, route = ?, current_status = ?, is_urgent = ?, updated_at = CURRENT_TIMESTAMP WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipment.getTrackingNumber());
            pstmt.setString(2, shipment.getSenderName());
            pstmt.setString(3, shipment.getSenderAddress());
            pstmt.setString(4, shipment.getSenderContact());
            pstmt.setString(5, shipment.getReceiverName());
            pstmt.setString(6, shipment.getReceiverAddress());
            pstmt.setString(7, shipment.getReceiverContact());
            pstmt.setString(8, shipment.getPackageContents());
            pstmt.setString(9, shipment.getPackageType());
            pstmt.setDouble(10, shipment.getWeightKg());
            pstmt.setString(11, shipment.getDimensionsCm());
            pstmt.setString(12, shipment.getCurrentLocation());
            pstmt.setString(13, shipment.getRoute());
            pstmt.setString(14, shipment.getCurrentStatus());
            pstmt.setBoolean(15, shipment.isUrgent());
            pstmt.setString(16, shipment.getShipmentId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public boolean deleteShipment(String shipmentId) throws SQLException {
        String sql = "DELETE FROM Shipments WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public Shipment findShipmentById(String shipmentId) throws SQLException {
        String sql = "SELECT * FROM Shipments WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToShipment(rs);
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public Shipment findShipmentByTrackingNumber(String trackingNumber) throws SQLException {
        String sql = "SELECT * FROM Shipments WHERE tracking_number = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingNumber);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToShipment(rs);
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public List<Shipment> findAllShipments() throws SQLException {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT * FROM Shipments ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                shipments.add(mapResultSetToShipment(rs));
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return shipments;
    }

    @Override
    public List<Shipment> findUnscheduledShipments() throws SQLException {
        List<Shipment> shipments = new ArrayList<>();
        // Select shipments that do NOT have a corresponding entry in the Deliveries table
        String sql = "SELECT s.* FROM Shipments s LEFT JOIN Deliveries d ON s.shipment_id = d.shipment_id WHERE d.shipment_id IS NULL ORDER BY s.created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                shipments.add(mapResultSetToShipment(rs));
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return shipments;
    }

    /**
     * Helper method to map a ResultSet row to a Shipment object.
     */
    private Shipment mapResultSetToShipment(ResultSet rs) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setShipmentId(rs.getString("shipment_id"));
        shipment.setTrackingNumber(rs.getString("tracking_number"));
        shipment.setSenderName(rs.getString("sender_name"));
        shipment.setSenderAddress(rs.getString("sender_address"));
        shipment.setSenderContact(rs.getString("sender_contact"));
        shipment.setReceiverName(rs.getString("receiver_name"));
        shipment.setReceiverAddress(rs.getString("receiver_address"));
        shipment.setReceiverContact(rs.getString("receiver_contact"));
        shipment.setPackageContents(rs.getString("package_contents"));
        shipment.setPackageType(rs.getString("package_type"));
        shipment.setWeightKg(rs.getDouble("weight_kg"));
        shipment.setDimensionsCm(rs.getString("dimensions_cm"));
        shipment.setCurrentLocation(rs.getString("current_location"));
        shipment.setRoute(rs.getString("route"));
        shipment.setCurrentStatus(rs.getString("current_status"));
        shipment.setUrgent(rs.getBoolean("is_urgent"));

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            shipment.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            shipment.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        return shipment;
    }
}

