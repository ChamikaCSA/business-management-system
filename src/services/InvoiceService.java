package services;

import entities.Customer;
import entities.Invoice;
import entities.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class InvoiceService {
    private final CustomerService customerService;
    private final ItemService itemService;

    private final Map<String, Invoice> invoiceRegistry = new TreeMap<>();

    public InvoiceService(CustomerService customerService, ItemService itemService) {
        this.customerService = customerService;
        this.itemService = itemService;

        updateRegistry();
    }

    public void updateRegistry() {
        invoiceRegistry.clear();
        Map<String, Customer> customerRegistry = customerService.getCustomerRegistry();
        Map<String, Item> itemRegistry = itemService.getItemRegistry();
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
                invoice.setCustomer(customerRegistry.get(customerId));

                Map<Item, Integer> itemsMap = getInvoiceItems(invoice.getId(), itemRegistry);
                invoice.setItemsMap(itemsMap);

                invoiceRegistry.put(invoice.getId(), invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Invoice> getInvoiceRegistry() {
        updateRegistry();
        return invoiceRegistry;
    }

    public void insertInvoice(Invoice invoice) {
        String sql = "INSERT INTO Invoices (id, customerId, date, totalAmount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoice.getId());
            stmt.setString(2, invoice.getCustomer().getId());
            stmt.setDate(3, new java.sql.Date(invoice.getDate().getTime()));
            stmt.setDouble(4, invoice.getTotalAmount());
            stmt.executeUpdate();

            insertInvoiceItemsAndUpdateStock(invoice);

            invoiceRegistry.put(invoice.getId(), invoice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertInvoiceItemsAndUpdateStock(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO InvoiceItems (invoiceId, itemId, quantity) VALUES (?, ?, ?)";
        String updateStockSql = "UPDATE Items SET quantity = quantity - ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
            for (Map.Entry<Item, Integer> entry : invoice.getItemsMap().entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();

                stmt.setString(1, invoice.getId());
                stmt.setString(2, item.getId());
                stmt.setInt(3, quantity);
                stmt.executeUpdate();

                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, item.getId());
                updateStmt.executeUpdate();
            }
        }
    }

    public void updateInvoice(Invoice selectedInvoice) {
        String sql = "UPDATE Invoices SET customerId = ?, date = ?, totalAmount = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, selectedInvoice.getCustomer().getId());
            stmt.setDate(2, new java.sql.Date(selectedInvoice.getDate().getTime()));
            stmt.setDouble(3, selectedInvoice.getTotalAmount());
            stmt.setString(4, selectedInvoice.getId());
            stmt.executeUpdate();

            updateInvoiceItemsAndUpdateStock(selectedInvoice);

            invoiceRegistry.put(selectedInvoice.getId(), selectedInvoice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateInvoiceItemsAndUpdateStock(Invoice invoice) throws SQLException {
        String deleteSql = "DELETE FROM InvoiceItems WHERE invoiceId = ?";
        String insertSql = "INSERT INTO InvoiceItems (invoiceId, itemId, quantity) VALUES (?, ?, ?)";
        String updateStockSql = "UPDATE Items SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
            deleteStmt.setString(1, invoice.getId());
            deleteStmt.executeUpdate();

            for (Map.Entry<Item, Integer> entry : invoice.getItemsMap().entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();

                insertStmt.setString(1, invoice.getId());
                insertStmt.setString(2, item.getId());
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, item.getId());
                updateStmt.executeUpdate();
            }
        }
    }

    public void deleteInvoice(String invoiceId) {
        try {
            deleteInvoiceItemsAndUpdateStock(invoiceId);

            String sql = "DELETE FROM Invoices WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, invoiceId);
                stmt.executeUpdate();

                invoiceRegistry.remove(invoiceId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteInvoiceItemsAndUpdateStock(String invoiceId) throws SQLException {
        String deleteSql = "DELETE FROM InvoiceItems WHERE invoiceId = ?";
        String updateStockSql = "UPDATE Items SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
            deleteStmt.setString(1, invoiceId);
            deleteStmt.executeUpdate();

            Map<Item, Integer> items = getInvoiceItems(invoiceId, itemService.getItemRegistry());
            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();

                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, item.getId());
                updateStmt.executeUpdate();
            }
        }
    }

    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRegistry.get(invoiceId);
    }

    private static Map<Item, Integer> getInvoiceItems(String invoiceId, Map<String, Item> itemsRegistry) {
        Map<Item, Integer> items = new HashMap<>();
        String sql = "SELECT itemId, quantity FROM InvoiceItems WHERE invoiceId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String itemId = rs.getString("itemId");
                    Item item = itemsRegistry.get(itemId);
                    int quantity = rs.getInt("quantity");
                    items.put(item, quantity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public double calculateTotalIncome(Date startDate, Date endDate) {
        double totalIncome = 0.0;
        for (Invoice invoice : invoiceRegistry.values()) {
            if (!invoice.getDate().before(startDate) && !invoice.getDate().after(endDate)) {
                totalIncome += invoice.getTotalAmount();
            }
        }
        return totalIncome;
    }

    public String generateIncomeAndSalesAnalysis() {
        double totalIncome = 0.0;
        int totalSales = 0;
        double highestSale = 0.0;
        double lowestSale = Double.MAX_VALUE;

        for (Invoice invoice : invoiceRegistry.values()) {
            double amount = invoice.getTotalAmount();
            totalIncome += amount;
            totalSales++;
            if (amount > highestSale) {
                highestSale = amount;
            }
            if (amount < lowestSale) {
                lowestSale = amount;
            }
        }

        double averageSale = totalSales > 0 ? totalIncome / totalSales : 0.0;
        return "Total Income: $" + totalIncome + "\nTotal Sales: " + totalSales + "\nAverage Sale: $" + averageSale + "\nHighest Sale: $" + highestSale + "\nLowest Sale: $" + lowestSale;
    }

    public List<Double> getMonthlySalesData() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        Map<String, Double> monthlySales = new TreeMap<>();
        String currentMonth = monthFormat.format(new Date());

        for (Invoice invoice : invoiceRegistry.values()) {
            String month = monthFormat.format(invoice.getDate());
            if (!month.equals(currentMonth)) {
                monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + invoice.getTotalAmount());
            }
        }

        return new ArrayList<>(monthlySales.values());
    }
}
