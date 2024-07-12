package services;

import entities.Invoice;
import entities.Payment;
import utils.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class PaymentService {
    private final InvoiceService invoiceService;

    private final Map<String, Payment> paymentRegistry = new TreeMap<>();

    public PaymentService(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;

        updateRegistry();
    }

    public void updateRegistry() {
        paymentRegistry.clear();
        Map<String, Invoice> invoiceRegistry = invoiceService.getInvoiceRegistry();
        String sql = "SELECT * FROM Payments";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getString("id"));

                String invoiceId = rs.getString("invoiceId");
                payment.setInvoice(invoiceRegistry.get(invoiceId));

                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentDate(rs.getDate("paymentDate"));
                payment.setPaymentMethod(rs.getString("paymentMethod"));

                paymentRegistry.put(payment.getId(), payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Payment> getPaymentRegistry() {
        updateRegistry();
        return paymentRegistry;
    }

    public void insertPayment(Payment payment) {
        String sql = "INSERT INTO Payments (id, invoiceId, amount, paymentDate, paymentMethod) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getId());
            stmt.setString(2, payment.getInvoice().getId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setDate(4, new java.sql.Date(payment.getPaymentDate().getTime()));
            stmt.setString(5, payment.getPaymentMethod());
            stmt.executeUpdate();

            paymentRegistry.put(payment.getId(), payment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayment(Payment payment) {
        String sql = "UPDATE Payments SET invoiceId = ?, amount = ?, paymentDate = ?, paymentMethod = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getInvoice().getId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setDate(3, new java.sql.Date(payment.getPaymentDate().getTime()));
            stmt.setString(4, payment.getPaymentMethod());
            stmt.setString(5, payment.getId());
            stmt.executeUpdate();

            paymentRegistry.put(payment.getId(), payment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePayment(String paymentId) {
        String sql = "DELETE FROM Payments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentId);
            stmt.executeUpdate();

            paymentRegistry.remove(paymentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Payment getPaymentById(String paymentId) {
        return paymentRegistry.get(paymentId);
    }
}
