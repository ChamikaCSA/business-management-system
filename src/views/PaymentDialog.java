package views;

import models.Invoice;
import models.Payment;
import controllers.PaymentService;
import utils.Generator;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class PaymentDialog extends JDialog {
    private final PaymentService paymentService;
    private final Invoice invoice;

    private JRadioButton cashRadioButton;
    private JRadioButton creditCardRadioButton;
    private JRadioButton debitCardRadioButton;
    private JRadioButton bankTransferRadioButton;
    private JRadioButton chequeRadioButton;

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
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel paymentMethodLabel = new JLabel("Payment Method:");

        cashRadioButton = new JRadioButton("Cash");
        creditCardRadioButton = new JRadioButton("Credit Card");
        debitCardRadioButton = new JRadioButton("Debit Card");
        bankTransferRadioButton = new JRadioButton("Bank Transfer");
        chequeRadioButton = new JRadioButton("Cheque");

        ButtonGroup paymentMethodGroup = new ButtonGroup();
        paymentMethodGroup.add(cashRadioButton);
        paymentMethodGroup.add(creditCardRadioButton);
        paymentMethodGroup.add(debitCardRadioButton);
        paymentMethodGroup.add(bankTransferRadioButton);
        paymentMethodGroup.add(chequeRadioButton);

        JPanel radioPanel = new JPanel(new GridLayout(0, 3));
        radioPanel.add(cashRadioButton);
        radioPanel.add(creditCardRadioButton);
        radioPanel.add(debitCardRadioButton);
        radioPanel.add(bankTransferRadioButton);
        radioPanel.add(chequeRadioButton);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(paymentMethodLabel, gbc);

        gbc.gridy++;
        add(radioPanel, gbc);

        gbc.gridy++;
        add(buttonPanel, gbc);

        saveButton.addActionListener(_ -> savePayment());
        cancelButton.addActionListener(_ -> dispose());

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void savePayment() {
        String paymentMethod = null;
        if (cashRadioButton.isSelected()) {
            paymentMethod = "Cash";
        } else if (creditCardRadioButton.isSelected()) {
            paymentMethod = "Credit Card";
        } else if (debitCardRadioButton.isSelected()) {
            paymentMethod = "Debit Card";
        } else if (bankTransferRadioButton.isSelected()) {
            paymentMethod = "Bank Transfer";
        } else if (chequeRadioButton.isSelected()) {
            paymentMethod = "Cheque";
        }

        if (paymentMethod == null) {
            JOptionPane.showMessageDialog(this, "Please select a payment method.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentId = Generator.generateDatedId("PAY", paymentService.getPaymentCount() + 1);
        Double amount = invoice.getTotalAmount();
        Date paymentDate = new Date();

        Payment payment = new Payment(paymentId, invoice, amount, paymentDate, paymentMethod);
        paymentService.insertPayment(payment);

        dispose();
    }
}
