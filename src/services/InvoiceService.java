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
    private final Map<String, Invoice> invoiceRegistry = new HashMap<>();

    public InvoiceService() {
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

    public double calculateTotalIncome(Date startDate, Date endDate) {
        double totalIncome = 0.0;
        for (Invoice invoice : invoiceRegistry.values()) {
            if (!invoice.getDate().before(startDate) && !invoice.getDate().after(endDate)) {
                totalIncome += invoice.getTotalAmount();
            }
        }
        return totalIncome;
    }

    public String generateMonthlySalesReport() {
        StringBuilder report = new StringBuilder();
        Map<String, Double> salesByMonth = new HashMap<>();

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        for (Invoice invoice : invoiceRegistry.values()) {
            String month = monthFormat.format(invoice.getDate());
            salesByMonth.put(month, salesByMonth.getOrDefault(month, 0.0) + invoice.getTotalAmount());
        }

        for (Map.Entry<String, Double> entry : salesByMonth.entrySet()) {
            report.append(entry.getKey()).append(": $").append(entry.getValue()).append("\n");
        }
        return report.toString();
    }

    public double forecastNextMonthSales() {
        double totalSales = 0.0;
        int monthsCounted = 0;

        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        Map<String, Double> monthlySales = new HashMap<>();

        for (Invoice invoice : invoiceRegistry.values()) {
            String month = monthFormat.format(invoice.getDate());
            monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + invoice.getTotalAmount());
        }

        for (Double sales : monthlySales.values()) {
            totalSales += sales;
            monthsCounted++;
        }

        return monthsCounted > 0 ? totalSales / monthsCounted : 0.0;
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
        return STR."Total Income: $\{totalIncome}\nTotal Sales: \{totalSales}\nAverage Sale: $\{averageSale}\nHighest Sale: $\{highestSale}\nLowest Sale: $\{lowestSale}";
    }
}
