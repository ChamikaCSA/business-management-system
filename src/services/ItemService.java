package services;

import entities.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ItemService {
    private final Map<String, Item> itemRegistry = new HashMap<>();

    public void registerItem(Item item) {
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

    public Item getItemById(String id) {
        Item item = itemRegistry.get(id);
        if (item == null) {
            String sql = "SELECT * FROM Items WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    item = new Item();
                    item.setId(rs.getString("id"));
                    item.setName(rs.getString("name"));
                    item.setPrice(rs.getDouble("price"));
                    item.setQuantity(rs.getInt("quantity"));
                    itemRegistry.put(id, item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    public Map<String, Item> getItemRegistry() {
        if (itemRegistry.isEmpty()) {
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
        return itemRegistry;
    }
}
