package services;

import entities.Customer;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {
    private final Map<String, Customer> customerRegistry = new TreeMap<>();

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

    public void sendEmail(Customer selectedCustomer, String emailSubject, String emailContent) {
        System.out.println("Sending email to " + selectedCustomer.getEmail());
        System.out.println("Subject: " + emailSubject);
        System.out.println("Content: " + emailContent);
    }
}
