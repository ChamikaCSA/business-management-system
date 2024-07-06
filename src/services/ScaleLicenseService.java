package services;

import entities.ScaleLicense;
import org.w3c.dom.ls.LSOutput;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ScaleLicenseService {
    private final Map<String, ScaleLicense> licenseRegistry = new HashMap<>();

    public ScaleLicenseService() {
        String sql = "SELECT * FROM ScaleLicenses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ScaleLicense scaleLicense = new ScaleLicense();
                scaleLicense.setId(rs.getString("id"));
                scaleLicense.setExpirationDate(rs.getDate("expirationDate"));
                scaleLicense.setScaleType(rs.getString("scaleType"));
                licenseRegistry.put(scaleLicense.getId(), scaleLicense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void renewLicense(ScaleLicense scaleLicense) {
        String sql = "INSERT INTO ScaleLicenses (id, expirationDate, scaleType) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, scaleLicense.getId());
            stmt.setDate(2, new java.sql.Date(scaleLicense.getExpirationDate().getTime()));
            stmt.setString(3, scaleLicense.getScaleType());
            stmt.executeUpdate();

            licenseRegistry.put(scaleLicense.getId(), scaleLicense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, ScaleLicense> getLicenseRegistry() {
        return licenseRegistry;
    }
}
