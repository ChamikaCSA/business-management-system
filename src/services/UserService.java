package services;

import entities.Invoice;
import entities.User;
import utils.DBConnection;
import utils.ReportGenerator;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserService {
    private final Map<String, User> userRegistry = new HashMap<>();
    private final InvoiceService invoiceService;

    public UserService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO Users (id, name, email, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE Users SET name = ?, email = ?, type = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getType());
            stmt.setString(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String userId) {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewReports() {
        // Example using ReportGenerator class
        String reportData = "Sample report data";
        ReportGenerator.generateReport(reportData);
    }

    public void nextMonthSalesForecast() {
        // Example sales forecasting logic
        List<Invoice> invoices = invoiceService.getInvoiceRegistry().values().stream()
                .toList();
        double totalSales = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        System.out.println(STR."Next Month Sales Forecast: \{totalSales * 1.1}"); // Assume a 10% growth
    }

    public void incomeAndSalesAnalysis() {
        // Example income and sales analysis logic
        List<Invoice> invoices = invoiceService.getInvoiceRegistry().values().stream()
                .toList();
        double totalIncome = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        System.out.println(STR."Total Income: \{totalIncome}");
        // Additional logic as needed
    }

    public double viewIncomeDetails() {
        List<Invoice> invoices = invoiceService.getInvoiceRegistry().values().stream()
                .toList();
        return invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
    }

    public void renewScaleLicense(String scaleId) {
        // Placeholder for renewing scale license logic
        System.out.println(STR."Renewing scale license for scale ID: \{scaleId}");
        // Additional logic for renewing the license
    }

    public User getUserById(String userId) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, User> getUserRegistry() {
        if (userRegistry.isEmpty()) {
            String sql = "SELECT * FROM Users";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setType(rs.getString("type"));
                    userRegistry.put(user.getId(), user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userRegistry;
    }
}
