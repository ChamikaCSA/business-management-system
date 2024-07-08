package services;

import entities.Customer;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {
    private final Map<String, Customer> customerRegistry = new HashMap<>();

    public CustomerService() {
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

    public void registerCustomer(Customer customer) {
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

    public Customer getCustomerById(String id) {
        Customer customer = customerRegistry.get(id);
        if (customer == null) {
            String sql = "SELECT * FROM Customers WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    customer = new Customer();
                    customer.setId(rs.getString("id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customerRegistry.put(id, customer);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return customer;
    }

    public Map<String, Customer> getCustomerRegistry() {
        return customerRegistry;
    }

    public void sendEmail(Customer selectedCustomer, String emailSubject, String emailContent) {
        System.out.println("Sending email to " + selectedCustomer.getEmail());
        System.out.println("Subject: " + emailSubject);
        System.out.println("Content: " + emailContent);
    }
}
