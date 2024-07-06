package services;

import entities.Item;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StockService {
    private final ItemService itemService = new ItemService();
    private final Map<String, Integer> stockRegistry = new HashMap<>();

    public StockService() {
        String sql = "SELECT * FROM Items";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String itemId = rs.getString("id");
                int quantity = rs.getInt("quantity");
                stockRegistry.put(itemId, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(String itemId, int quantity) {
        String sql = "UPDATE Items SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, itemId);
            stmt.executeUpdate();

            int updatedQuantity = stockRegistry.getOrDefault(itemId, 0) + quantity;
            stockRegistry.put(itemId, updatedQuantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStockQuantity(String itemId) {
        return stockRegistry.getOrDefault(itemId, 0);
    }

    public String generateStockLevelReport() {
        StringBuilder report = new StringBuilder();
        for (Item item : itemService.getItemRegistry().values()) {
            report.append("Item: ").append(item.getName())
                    .append(" - Stock: ").append(item.getQuantity())
                    .append("\n");
        }
        return report.toString();
    }

}
