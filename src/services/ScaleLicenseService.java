package services;

import entities.ScaleLicense;
import utils.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class ScaleLicenseService {
    private final Map<String, ScaleLicense> scaleLicenseRegistry = new TreeMap<>();

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
                scaleLicense.setExpirationDate(rs.getDate("expirationDate"));
                scaleLicense.setScaleType(rs.getString("scaleType"));
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

    public void renewScaleLicense(ScaleLicense scaleLicense) {
        String sql = "INSERT INTO ScaleLicenses (id, expirationDate, scaleType) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, scaleLicense.getId());
            stmt.setDate(2, new java.sql.Date(scaleLicense.getExpirationDate().getTime()));
            stmt.setString(3, scaleLicense.getScaleType());
            stmt.executeUpdate();

            scaleLicenseRegistry.put(scaleLicense.getId(), scaleLicense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScaleLicense(String id) {
        String sql = "DELETE FROM ScaleLicenses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();

            scaleLicenseRegistry.remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ScaleLicense getScaleLicenseById(String id) {
        return scaleLicenseRegistry.get(id);
    }
}
