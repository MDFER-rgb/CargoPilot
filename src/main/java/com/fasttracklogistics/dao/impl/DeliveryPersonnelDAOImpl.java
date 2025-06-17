
// src/main/java/com/fasttracklogistics/dao/impl/DeliveryPersonnelDAOImpl.java

package com.fasttracklogistics.dao;

import com.fasttracklogistics.model.DeliveryPersonnel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // For generating unique IDs

/**
 * JDBC implementation of the DeliveryPersonnelDAO interface for MySQL.
 */
public class DeliveryPersonnelDAOImpl implements DeliveryPersonnelDAO {

    @Override
    public boolean insertPersonnel(DeliveryPersonnel personnel) throws SQLException {
        String sql = "INSERT INTO DeliveryPersonnel (personnel_id, employee_id, name, contact_number, email, vehicle_type, license_number, availability_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            if (personnel.getPersonnelId() == null || personnel.getPersonnelId().isEmpty()) {
                personnel.setPersonnelId(UUID.randomUUID().toString());
            }
            if (personnel.getEmployeeId() == null || personnel.getEmployeeId().isEmpty()) {
                personnel.setEmployeeId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            if (personnel.getAvailabilityStatus() == null || personnel.getAvailabilityStatus().isEmpty()) {
                personnel.setAvailabilityStatus("Available");
            }

            pstmt.setString(1, personnel.getPersonnelId());
            pstmt.setString(2, personnel.getEmployeeId());
            pstmt.setString(3, personnel.getName());
            pstmt.setString(4, personnel.getContactNumber());
            pstmt.setString(5, personnel.getEmail());
            pstmt.setString(6, personnel.getVehicleType());
            pstmt.setString(7, personnel.getLicenseNumber());
            pstmt.setString(8, personnel.getAvailabilityStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public boolean updatePersonnel(DeliveryPersonnel personnel) throws SQLException {
        String sql = "UPDATE DeliveryPersonnel SET employee_id = ?, name = ?, contact_number = ?, " +
                "email = ?, vehicle_type = ?, license_number = ?, availability_status = ? WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getEmployeeId());
            pstmt.setString(2, personnel.getName());
            pstmt.setString(3, personnel.getContactNumber());
            pstmt.setString(4, personnel.getEmail());
            pstmt.setString(5, personnel.getVehicleType());
            pstmt.setString(6, personnel.getLicenseNumber());
            pstmt.setString(7, personnel.getAvailabilityStatus());
            pstmt.setString(8, personnel.getPersonnelId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public boolean deletePersonnel(String personnelId) throws SQLException {
        String sql = "DELETE FROM DeliveryPersonnel WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, null);
        }
    }

    @Override
    public DeliveryPersonnel findPersonnelById(String personnelId) throws SQLException {
        String sql = "SELECT * FROM DeliveryPersonnel WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPersonnel(rs);
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public DeliveryPersonnel findPersonnelByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT * FROM DeliveryPersonnel WHERE employee_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPersonnel(rs);
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public List<DeliveryPersonnel> findAllPersonnel() throws SQLException {
        List<DeliveryPersonnel> personnelList = new ArrayList<>();
        String sql = "SELECT * FROM DeliveryPersonnel ORDER BY name";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                personnelList.add(mapResultSetToPersonnel(rs));
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return personnelList;
    }

    @Override
    public List<DeliveryPersonnel> findAvailablePersonnel() throws SQLException {
        List<DeliveryPersonnel> personnelList = new ArrayList<>();
        String sql = "SELECT * FROM DeliveryPersonnel WHERE availability_status = 'Available' ORDER BY name";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                personnelList.add(mapResultSetToPersonnel(rs));
            }
        } finally {
            DatabaseConnection.closeResources(conn, pstmt, rs);
        }
        return personnelList;
    }

    /**
     * Helper method to map a ResultSet row to a DeliveryPersonnel object.
     */
    private DeliveryPersonnel mapResultSetToPersonnel(ResultSet rs) throws SQLException {
        DeliveryPersonnel personnel = new DeliveryPersonnel();
        personnel.setPersonnelId(rs.getString("personnel_id"));
        personnel.setEmployeeId(rs.getString("employee_id"));
        personnel.setName(rs.getString("name"));
        personnel.setContactNumber(rs.getString("contact_number"));
        personnel.setEmail(rs.getString("email"));
        personnel.setVehicleType(rs.getString("vehicle_type"));
        personnel.setLicenseNumber(rs.getString("license_number"));
        personnel.setAvailabilityStatus(rs.getString("availability_status"));
        return personnel;
    }
}

