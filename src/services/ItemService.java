package services;

import entities.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ItemService {
    private final Map<String, Item> itemRegistry = new TreeMap<>();

    public ItemService() {
        updateRegistry();
    }

    public void updateRegistry() {
        itemRegistry.clear();
        String sql = "SELECT * FROM Items";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getString("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setQuantity(rs.getInt("quantity"));
                itemRegistry.put(item.getId(), item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Item> getItemRegistry() {
        updateRegistry();
        return itemRegistry;
    }

    public void insertItem(Item item) {
        String sql = "INSERT INTO Items (id, name, price, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setDouble(3, item.getPrice());
            stmt.setInt(4, item.getQuantity());
            stmt.executeUpdate();

            itemRegistry.put(item.getId(), item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(Item item) {
        String sql = "UPDATE Items SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setInt(3, item.getQuantity());
            stmt.setString(4, item.getId());
            stmt.executeUpdate();

            itemRegistry.put(item.getId(), item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(String id) {
        String sql = "DELETE FROM Items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();

            itemRegistry.remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Item getItemById(String id) {
        return itemRegistry.get(id);
    }

    public String generateStockLevelReport() {
        StringBuilder report = new StringBuilder();
        for (Item item : itemRegistry.values()) {
            report.append("Item: ").append(item.getName())
                    .append(" - Stock: ").append(item.getQuantity())
                    .append("\n");
        }
        return report.toString();
    }
}
