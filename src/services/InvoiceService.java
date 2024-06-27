package services;

import entities.Customer;
import entities.Invoice;
import entities.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.*;

public class InvoiceService {
    private final Map<String, Invoice> invoiceRegistry = new HashMap<>();

    public void registerInvoice(Invoice invoice) {
        String sql = "INSERT INTO Invoices (id, customerId, date, totalAmount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoice.getId());
            stmt.setString(2, invoice.getCustomer().getId());
            stmt.setDate(3, new java.sql.Date(invoice.getDate().getTime()));
            stmt.setDouble(4, invoice.getTotalAmount());
            stmt.executeUpdate();

            saveInvoiceItemsAndUpdateStock(invoice);

            invoiceRegistry.put(invoice.getId(), invoice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveInvoiceItemsAndUpdateStock(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO InvoiceItems (invoiceId, itemId, quantity) VALUES (?, ?, ?)";
        String updateStockSql = "UPDATE Items SET quantity = quantity - ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
            for (Item item : invoice.getItems()) {
                stmt.setString(1, invoice.getId());
                stmt.setString(2, item.getId());
                stmt.setInt(3, item.getQuantity());
                stmt.executeUpdate();

                updateStmt.setInt(1, item.getQuantity());
                updateStmt.setString(2, item.getId());
                updateStmt.executeUpdate();
            }
        }
    }

    public Map<String, Invoice> getInvoiceRegistry() {
        if (invoiceRegistry.isEmpty()) {
            String sql = "SELECT * FROM Invoices";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setId(rs.getString("id"));
                    invoice.setDate(rs.getDate("date"));
                    invoice.setTotalAmount(rs.getDouble("totalAmount"));

                    String customerId = rs.getString("customerId");
                    Customer customer = new CustomerService().getCustomerById(customerId);
                    invoice.setCustomer(customer);

                    List<Item> items = getInvoiceItems(invoice.getId());
                    invoice.setItems(items);

                    invoiceRegistry.put(invoice.getId(), invoice);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return invoiceRegistry;
    }

    private List<Item> getInvoiceItems(String invoiceId) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT itemId FROM InvoiceItems WHERE invoiceId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String itemId = rs.getString("itemId");
                Item item = new ItemService().getItemById(itemId);
                items.add(item);
            }
        }
        return items;
    }
}
