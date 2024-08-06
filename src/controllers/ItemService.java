package controllers;

import models.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.*;

public class ItemService {
    private final NavigableMap<String, Item> itemRegistry = new TreeMap<>();

    private static final int LOW_STOCK_THRESHOLD = 10;

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
            report.append(item.getName())
                    .append(" : ")
                    .append(item.getQuantity())
                    .append("\n");
        }
        report.deleteCharAt(report.length() - 1);
        return report.toString();
    }

    public boolean hasGoodsReceiveNotes(String itemId) {
        String sql = "SELECT COUNT(*) FROM GoodsReceiveNotes WHERE itemId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemId);
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

    public boolean hasInvoiceItems(String itemId) {
        String sql = "SELECT COUNT(*) FROM InvoiceItems WHERE itemId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemId);
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

    public int getItemCount() {
        if (itemRegistry.isEmpty()) {
            return 0;
        }
        String lastId = itemRegistry.lastKey();
        return Integer.parseInt(lastId.substring(lastId.lastIndexOf("-") + 1));
    }

    public List<String> getLowStockItems() {
        List<String> lowStockItems = new ArrayList<>();
        for (Item item : itemRegistry.values()) {
            if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
                lowStockItems.add(item.getName() + " : " + item.getQuantity());
            }
        }
        return lowStockItems;
    }
}
