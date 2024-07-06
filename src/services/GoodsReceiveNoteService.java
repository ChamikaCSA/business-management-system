package services;

import entities.GoodsReceiveNote;
import entities.Item;
import entities.Supplier;
import utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class GoodsReceiveNoteService {
    private final Map<String, GoodsReceiveNote> goodsReceiveNoteRegistry = new HashMap<>();

    public GoodsReceiveNoteService() {
        String sql = "SELECT * FROM GoodsReceiveNotes";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                GoodsReceiveNote grn = new GoodsReceiveNote();
                grn.setId(rs.getString("id"));
                grn.setReceiveDate(rs.getDate("receiveDate"));
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

    public void registerGoodsReceiveNote(GoodsReceiveNote grn) {
        String sql = "INSERT INTO GoodsReceiveNotes (id, supplierId, itemId, receiveDate, quantity) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grn.getId());
            stmt.setString(2, grn.getSupplier().getId());
            stmt.setString(3, grn.getItem().getId());
            stmt.setDate(4, new java.sql.Date(grn.getReceiveDate().getTime()));
            stmt.setInt(5, grn.getQuantity());
            stmt.executeUpdate();

            goodsReceiveNoteRegistry.put(grn.getId(), grn);
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
                    grn.setReceiveDate(rs.getDate("receiveDate"));
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

    public Map<String, GoodsReceiveNote> getGoodsReceiveNotes() {
        return goodsReceiveNoteRegistry;
    }
}
