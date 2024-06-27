package services;

import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StockService {
    private final Map<String, Integer> stock = new HashMap<>();

    public StockService() {
    }

    public void updateStock(String itemId, int quantity) {
        String sql = "UPDATE Items SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, itemId);
            stmt.executeUpdate();
            int updatedQuantity = stock.getOrDefault(itemId, 0) + quantity;
            stock.put(itemId, updatedQuantity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStockQuantity(String itemId) {
        return stock.getOrDefault(itemId, 0);
    }
}
