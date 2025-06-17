

// src/main/java/com/fasttracklogistics/dao/impl/DeliveryDAOImpl.java

package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.Delivery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs

/**
 * JDBC implementation of the DeliveryDAO interface for MySQL.
 */
public class DeliveryDAOImpl implements DeliveryDAO {

    @Override
    public boolean insertDelivery(Delivery delivery) throws SQLException {
        String sql = "INSERT INTO Deliveries (delivery_id, shipment_id, personnel_id, scheduled_date, scheduled_time_slot, " +
                "actual_delivery_date, delivery_status, estimated_arrival_time, delay_reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            if (delivery.getDeliveryId() == null || delivery.getDeliveryId().isEmpty()) {
                delivery.setDeliveryId(UUID.randomUUID().toString());
            }
            if (delivery.getDeliveryStatus() == null || delivery.getDeliveryStatus().isEmpty()) {
                delivery.setDeliveryStatus("Scheduled");
            }

            pstmt.setString(1, delivery.getDeliveryId());
            pstmt.setString(2, delivery.getShipmentId());
            pstmt.setString(3, delivery.getPersonnelId());
            pstmt.setDate(4, Date.valueOf(delivery.getScheduledDate()));
            pstmt.setString(5, delivery.getScheduledTimeSlot());
            pstmt.setTimestamp(6, delivery.getActualDeliveryDate() != null ? Timestamp.valueOf(delivery.getActualDeliveryDate()) : null);
            pstmt.setString(7, delivery.getDeliveryStatus());
            pstmt.setTimestamp(8, delivery.getEstimatedArrivalTime() != null ? Timestamp.valueOf(delivery.getEstimatedArrivalTime()) : null);
            pstmt.setString(9, delivery.getDelayReason());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateDelivery(Delivery delivery) throws SQLException {
        String sql = "UPDATE Deliveries SET shipment_id = ?, personnel_id = ?, scheduled_date = ?, scheduled_time_slot = ?, " +
                "actual_delivery_date = ?, delivery_status = ?, estimated_arrival_time = ?, delay_reason = ? " +
                "WHERE delivery_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, delivery.getShipmentId());
            pstmt.setString(2, delivery.getPersonnelId());
            pstmt.setDate(3, Date.valueOf(delivery.getScheduledDate()));
            pstmt.setString(4, delivery.getScheduledTimeSlot());
            pstmt.setTimestamp(5, delivery.getActualDeliveryDate() != null ? Timestamp.valueOf(delivery.getActualDeliveryDate()) : null);
            pstmt.setString(6, delivery.getDeliveryStatus());
            pstmt.setTimestamp(7, delivery.getEstimatedArrivalTime() != null ? Timestamp.valueOf(delivery.getEstimatedArrivalTime()) : null);
            pstmt.setString(8, delivery.getDelayReason());
            pstmt.setString(9, delivery.getDeliveryId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public boolean deleteDelivery(String deliveryId) throws SQLException {
        String sql = "DELETE FROM Deliveries WHERE delivery_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deliveryId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public Delivery findDeliveryById(String deliveryId) throws SQLException {
        String sql = "SELECT * FROM Deliveries WHERE delivery_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deliveryId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDelivery(rs);
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public Delivery findDeliveryByShipmentId(String shipmentId) throws SQLException {
        String sql = "SELECT * FROM Deliveries WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToDelivery(rs);
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public List<Delivery> findAllDeliveries() throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Deliveries ORDER BY scheduled_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                deliveries.add(mapResultSetToDelivery(rs));
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return deliveries;
    }

    @Override
    public List<Delivery> findDeliveriesByPersonnelId(String personnelId) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Deliveries WHERE personnel_id = ? ORDER BY scheduled_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                deliveries.add(mapResultSetToDelivery(rs));
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return deliveries;
    }

    /**
     * Helper method to map a ResultSet row to a Delivery object.
     */
    private Delivery mapResultSetToDelivery(ResultSet rs) throws SQLException {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(rs.getString("delivery_id"));
        delivery.setShipmentId(rs.getString("shipment_id"));
        delivery.setPersonnelId(rs.getString("personnel_id"));
        delivery.setScheduledDate(rs.getDate("scheduled_date") != null ? rs.getDate("scheduled_date").toLocalDate() : null);
        delivery.setScheduledTimeSlot(rs.getString("scheduled_time_slot"));
        delivery.setActualDeliveryDate(rs.getTimestamp("actual_delivery_date") != null ? rs.getTimestamp("actual_delivery_date").toLocalDateTime() : null);
        delivery.setDeliveryStatus(rs.getString("delivery_status"));
        delivery.setEstimatedArrivalTime(rs.getTimestamp("estimated_arrival_time") != null ? rs.getTimestamp("estimated_arrival_time").toLocalDateTime() : null);
        delivery.setDelayReason(rs.getString("delay_reason"));
        return delivery;
    }
}
