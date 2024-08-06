package controllers;

import models.GoodsReceiveNote;
import models.Item;
import models.Supplier;
import utils.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class GoodsReceiveNoteService {
    private final NavigableMap<String, GoodsReceiveNote> goodsReceiveNoteRegistry = new TreeMap<>();

    public GoodsReceiveNoteService() {
        updateRegistry();
    }

    public void updateRegistry() {
        goodsReceiveNoteRegistry.clear();
        String sql = "SELECT * FROM GoodsReceiveNotes";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                GoodsReceiveNote grn = new GoodsReceiveNote();
                grn.setId(rs.getString("id"));
                grn.setReceivedDate(rs.getDate("receiveDate"));
                grn.setQuantity(rs.getInt("quantity"));

                String supplierId = rs.getString("supplierId");
                Supplier supplier = new SupplierService().getSupplierById(supplierId);
                grn.setSupplier(supplier);

                String itemId = rs.getString("itemId");
                Item item = new ItemService().getItemById(itemId);
                grn.setItem(item);

                goodsReceiveNoteRegistry.put(grn.getId(), grn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, GoodsReceiveNote> getGoodsReceiveNotesRegistry() {
        updateRegistry();
        return goodsReceiveNoteRegistry;
    }

    public void insertGoodsReceiveNote(GoodsReceiveNote grn) {
        String sql = "INSERT INTO GoodsReceiveNotes (id, supplierId, itemId, receiveDate, quantity) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grn.getId());
            stmt.setString(2, grn.getSupplier().getId());
            stmt.setString(3, grn.getItem().getId());
            stmt.setDate(4, new java.sql.Date(grn.getReceivedDate().getTime()));
            stmt.setInt(5, grn.getQuantity());
            stmt.executeUpdate();

            updateStock(grn);

            goodsReceiveNoteRegistry.put(grn.getId(), grn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGoodsReceiveNote(GoodsReceiveNote grn) {
        String sql = "UPDATE GoodsReceiveNotes SET supplierId = ?, itemId = ?, receiveDate = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grn.getSupplier().getId());
            stmt.setString(2, grn.getItem().getId());
            stmt.setDate(3, new java.sql.Date(grn.getReceivedDate().getTime()));
            stmt.setInt(4, grn.getQuantity());
            stmt.setString(5, grn.getId());
            stmt.executeUpdate();

            updateStock(grn);

            goodsReceiveNoteRegistry.put(grn.getId(), grn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGoodsReceiveNote(String grnId) {
        String sql = "DELETE FROM GoodsReceiveNotes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grnId);
            stmt.executeUpdate();

            goodsReceiveNoteRegistry.remove(grnId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGoodsReceiveNotesBySupplier(String supplierId) {
        String sql = "DELETE FROM GoodsReceiveNotes WHERE supplierId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplierId);
            stmt.executeUpdate();

            goodsReceiveNoteRegistry.entrySet().removeIf(entry -> entry.getValue().getSupplier().getId().equals(supplierId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateStock(GoodsReceiveNote grn) {
        String sql = "UPDATE Items SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grn.getQuantity());
            stmt.setString(2, grn.getItem().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, GoodsReceiveNote> getGoodsReceiveNoteRegistry() {
        if (goodsReceiveNoteRegistry.isEmpty()) {
            String sql = "SELECT * FROM GoodsReceiveNotes";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    GoodsReceiveNote grn = new GoodsReceiveNote();
                    grn.setId(rs.getString("id"));
                    grn.setReceivedDate(rs.getDate("receiveDate"));
                    grn.setQuantity(rs.getInt("quantity"));

                    String supplierId = rs.getString("supplierId");
                    Supplier supplier = new SupplierService().getSupplierById(supplierId);
                    grn.setSupplier(supplier);

                    String itemId = rs.getString("itemId");
                    Item item = new ItemService().getItemById(itemId);
                    grn.setItem(item);

                    goodsReceiveNoteRegistry.put(grn.getId(), grn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return goodsReceiveNoteRegistry;
    }

    public GoodsReceiveNote getGoodsReceiveNoteById(String id) {
        return goodsReceiveNoteRegistry.get(id);
    }

    public void deleteGoodsReceiveNotesByItem(String itemId) {
        String sql = "DELETE FROM GoodsReceiveNotes WHERE itemId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemId);
            stmt.executeUpdate();

            goodsReceiveNoteRegistry.entrySet().removeIf(entry -> entry.getValue().getItem().getId().equals(itemId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getGoodsReceiveNoteCount() {
        if (goodsReceiveNoteRegistry.isEmpty()) {
            return 0;
        }
        String lastId = goodsReceiveNoteRegistry.lastKey();
        return Integer.parseInt(lastId.substring(lastId.lastIndexOf("-") + 1));
    }
}
