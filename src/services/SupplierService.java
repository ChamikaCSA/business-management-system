package services;

import entities.Supplier;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SupplierService {
    private final Map<String, Supplier> supplierRegistry = new HashMap<>();

    public SupplierService() {
        String sql = "SELECT * FROM Suppliers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getString("id"));
                supplier.setName(rs.getString("name"));
                supplier.setEmail(rs.getString("email"));
                supplierRegistry.put(supplier.getId(), supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registerSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getId());
            stmt.setString(2, supplier.getName());
            stmt.setString(3, supplier.getEmail());
            stmt.executeUpdate();
            supplierRegistry.put(supplier.getId(), supplier);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Supplier getSupplierById(String id) {
        Supplier supplier = supplierRegistry.get(id);
        if (supplier == null) {
            String sql = "SELECT * FROM Suppliers WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    supplier = new Supplier();
                    supplier.setId(rs.getString("id"));
                    supplier.setName(rs.getString("name"));
                    supplier.setEmail(rs.getString("email"));
                    supplierRegistry.put(id, supplier);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return supplier;
    }

    public Map<String, Supplier> getSupplierRegistry() {
        return supplierRegistry;
    }

    public void sendEmail(Supplier selectedSupplier, String emailSubject, String emailContent) {
        System.out.println("Sending email to " + selectedSupplier.getEmail());
        System.out.println("Subject: " + emailSubject);
        System.out.println("Content: " + emailContent);
    }
}
