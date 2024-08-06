package controllers;

import models.Customer;
import utils.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {
    private final NavigableMap<String, Customer> customerRegistry = new TreeMap<>();

    public CustomerService() {
        updateRegistry();
    }

    public void updateRegistry() {
        customerRegistry.clear();
        String sql = "SELECT * FROM Customers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getString("id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customerRegistry.put(customer.getId(), customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Customer> getCustomerRegistry() {
        updateRegistry();
        return customerRegistry;
    }

    public void insertCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (id, name, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getId());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.executeUpdate();

            customerRegistry.put(customer.getId(), customer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getId());
            stmt.executeUpdate();

            customerRegistry.put(customer.getId(), customer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(String id) {
        String sql = "DELETE FROM Customers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();

            customerRegistry.remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Customer getCustomerById(String id) {
        return customerRegistry.get(id);
    }


    public boolean hasInvoices(String customerId) {
        String sql = "SELECT COUNT(*) FROM Invoices WHERE customerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCustomerCount() {
        if (customerRegistry.isEmpty()) {
            return 0;
        }
        String lastId = customerRegistry.lastKey();
        return Integer.parseInt(lastId.substring(lastId.lastIndexOf("-") + 1));
    }
}
