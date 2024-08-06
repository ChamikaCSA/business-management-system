package controllers;

import models.ScaleLicense;
import utils.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ScaleLicenseService {
    private final NavigableMap<String, ScaleLicense> scaleLicenseRegistry = new TreeMap<>();

    public ScaleLicenseService() {
        updateRegistry();
    }

    public void updateRegistry() {
        scaleLicenseRegistry.clear();

        String sql = "SELECT * FROM ScaleLicenses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ScaleLicense scaleLicense = new ScaleLicense();
                scaleLicense.setId(rs.getString("id"));
                scaleLicense.setLicenseType(rs.getString("licenseType"));
                scaleLicense.setIssuedDate(rs.getDate("issuedDate"));
                scaleLicense.setExpirationDate(rs.getDate("expirationDate"));

                CustomerService customerService = new CustomerService();
                scaleLicense.setCustomer(customerService.getCustomerById(rs.getString("customerId")));

                scaleLicense.setStatus(rs.getString("status"));

                scaleLicenseRegistry.put(scaleLicense.getId(), scaleLicense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, ScaleLicense> getScaleLicenseRegistry() {
        updateRegistry();
        return scaleLicenseRegistry;
    }

    public void insertScaleLicense(ScaleLicense scaleLicense) {
        String sql = "INSERT INTO ScaleLicenses (id, licenseType, issuedDate, expirationDate, customerId, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, scaleLicense.getId());
            stmt.setString(2, scaleLicense.getLicenseType());
            stmt.setDate(3, new Date(scaleLicense.getIssuedDate().getTime()));
            stmt.setDate(4, new Date(scaleLicense.getExpirationDate().getTime()));
            stmt.setString(5, scaleLicense.getCustomer().getId());
            stmt.setString(6, scaleLicense.getStatus());
            stmt.executeUpdate();

            scaleLicenseRegistry.put(scaleLicense.getId(), scaleLicense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void renewScaleLicense(ScaleLicense expiredScaleLicense, ScaleLicense newScaleLicense) {
        String sql = "UPDATE ScaleLicenses SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "Expired");
            stmt.setString(2, expiredScaleLicense.getId());
            stmt.executeUpdate();

            insertScaleLicense(newScaleLicense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateScaleLicense(ScaleLicense scaleLicense) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE ScaleLicenses SET licenseType = ?, issuedDate = ?, expirationDate = ?, customerId = ?, status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, scaleLicense.getLicenseType());
            stmt.setDate(2, new Date(scaleLicense.getIssuedDate().getTime()));
            stmt.setDate(3, new Date(scaleLicense.getExpirationDate().getTime()));
            stmt.setString(4, scaleLicense.getCustomer().getId());
            stmt.setString(5, scaleLicense.getStatus());
            stmt.setString(6, scaleLicense.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScaleLicense(String scaleLicenseId) {
        String sql = "DELETE FROM ScaleLicenses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, scaleLicenseId);
            stmt.executeUpdate();

            scaleLicenseRegistry.remove(scaleLicenseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScaleLicensesByCustomer(String customerId) {
        String sql = "DELETE FROM ScaleLicenses WHERE customerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            stmt.executeUpdate();

            scaleLicenseRegistry.entrySet().removeIf(entry -> entry.getValue().getCustomer().getId().equals(customerId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ScaleLicense getScaleLicenseById(String id) {
        return scaleLicenseRegistry.get(id);
    }

    public int getScaleLicenseCount() {
        if (scaleLicenseRegistry.isEmpty()) {
            return 0;
        }
        String lastId = scaleLicenseRegistry.lastKey();
        return Integer.parseInt(lastId.substring(lastId.lastIndexOf("-") + 1));
    }
}
