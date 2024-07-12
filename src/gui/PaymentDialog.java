package gui;

import entities.Invoice;
import entities.Payment;
import services.CustomerService;
import services.InvoiceService;
import services.ItemService;
import services.PaymentService;
import utils.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class PaymentDialog extends JDialog {
    private final PaymentService paymentService;
    private Invoice invoice;

    private JComboBox<String> paymentMethodComboBox;
    private String paymentId;
    private Double amount;
    private Date paymentDate;

    public PaymentDialog(JFrame parentFrame, String title, Invoice invoice, PaymentService paymentService) {
        super(parentFrame, title, true);
        this.invoice = invoice;
        this.paymentService = paymentService;

        initialize(parentFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(paymentMethodLabel, gbc);

        paymentMethodComboBox = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Cheque"});
        gbc.gridx = 1;
        add(paymentMethodComboBox, gbc);

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> savePayment());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void savePayment() {
        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();

        if (paymentMethod == null) {
            JOptionPane.showMessageDialog(this, "Please select a payment method.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentCount = paymentService.getPaymentRegistry().size() + 1;
        paymentId = IDGenerator.generateDatedId("PAY", paymentCount);
        amount = invoice.getTotalAmount();
        paymentDate = new Date();

        Payment payment = new Payment(paymentId, invoice, amount, paymentDate, paymentMethod);
        paymentService.insertPayment(payment);

        dispose();
    }
}
