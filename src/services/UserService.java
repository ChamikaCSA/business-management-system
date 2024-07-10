package services;

import entities.User;
import utils.DBConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class UserService {
    private final Map<String, User> userRegistry = new TreeMap<>();

    public UserService() {
        updateRegistry();
    }

    public void updateRegistry() {
        userRegistry.clear();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("type")
                );
                userRegistry.put(user.getId(), user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, User> getUserRegistry() {
        updateRegistry();
        return userRegistry;
    }


    public void insertUser(User user) {
        String sql = "INSERT INTO Users (id, name, email, password, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            String hashedPassword = hashPassword(user.getPassword());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, user.getType());
            stmt.executeUpdate();

            userRegistry.put(user.getId(), user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedBytes = md.digest();

            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(User user) {
        String sql = "UPDATE Users SET name = ?, email = ?, password = ?, type = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getType());
            stmt.setString(5, user.getId());
            stmt.executeUpdate();

            userRegistry.replace(user.getId(), user);
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

            userRegistry.remove(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserById(String userId) {
        return userRegistry.get(userId);
    }

    public String  authenticateUser(String email, String password) {
        String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
