package services;

import entities.Supplier;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SupplierService {
    private final Map<String, Supplier> supplierRegistry = new TreeMap<>();

    public SupplierService() {
        updateRegistry();
    }

    public void updateRegistry() {
        supplierRegistry.clear();
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

    public Map<String, Supplier> getSupplierRegistry() {
        updateRegistry();
        return supplierRegistry;
    }

    public void insertSupplier(Supplier supplier) {
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

    public void updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getId());
            stmt.executeUpdate();

            supplierRegistry.put(supplier.getId(), supplier);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSupplier(String id) {
        String sql = "DELETE FROM Suppliers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();

            supplierRegistry.remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Supplier getSupplierById(String id) {
        return supplierRegistry.get(id);
    }

    public void sendEmail(Supplier selectedSupplier, String emailSubject, String emailContent) {
        System.out.println("Sending email to " + selectedSupplier.getEmail());
        System.out.println("Subject: " + emailSubject);
        System.out.println("Content: " + emailContent);
    }
}
