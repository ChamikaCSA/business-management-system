package gui;

import entities.Invoice;
import entities.Item;
import services.CustomerService;
import services.InvoiceService;
import services.ItemService;
import services.PaymentService;

import javax.swing.*;
import java.awt.*;

public class InvoiceDetailsDialog extends JDialog {

    public InvoiceDetailsDialog(JFrame parentFrame, Invoice invoice) {

        initialize(parentFrame, invoice);
    }

    private void initialize(JFrame parentFrame, Invoice invoice) {
        setTitle("Invoice Details");
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel invoiceIdLabel = new JLabel("Invoice ID: ");
        JLabel invoiceIdValueLabel = new JLabel(invoice.getId());

        JLabel customerLabel = new JLabel("Customer: ");
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));

        JLabel customerIdLabel = new JLabel(invoice.getCustomer().getId());
        JLabel customerNameLabel = new JLabel(invoice.getCustomer().getName());
        JLabel customerEmailLabel = new JLabel(invoice.getCustomer().getEmail());

        customerPanel.add(customerLabel);
        customerPanel.add(customerIdLabel);
        customerPanel.add(customerNameLabel);
        customerPanel.add(customerEmailLabel);

        JLabel itemLabel = new JLabel("Items: ");
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        for (Item item : invoice.getItemsMap().keySet()) {
            String itemId = item.getId();
            String itemName = item.getName();
            String itemPrice = String.valueOf(item.getPrice());

            JLabel itemIdLabel = new JLabel(itemId);
            JLabel itemNameLabel = new JLabel(itemName);
            JLabel itemPriceLabel = new JLabel("$ " + itemPrice);
            JLabel itemQuantityLabel = new JLabel("x " + invoice.getItemsMap().get(item));

            itemPanel.add(itemIdLabel);
            itemPanel.add(itemNameLabel);
            itemPanel.add(itemPriceLabel);
            itemPanel.add(itemQuantityLabel);
            itemPanel.add(Box.createVerticalStrut(10));
        }

        JLabel totalAmountLabel = new JLabel("Total Amount: ");
        JLabel totalAmountValueLabel = new JLabel(String.valueOf(invoice.getTotalAmount()));

        JLabel dateLabel = new JLabel("Date: ");
        JLabel dateValueLabel = new JLabel(invoice.getDate().toString());

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(invoiceIdLabel, gbc);

        gbc.gridx++;
        add(invoiceIdValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(customerLabel, gbc);

        gbc.gridx++;
        add(customerPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(itemLabel, gbc);

        gbc.gridx++;
        add(itemPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(totalAmountLabel, gbc);

        gbc.gridx++;
        add(totalAmountValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(dateLabel, gbc);

        gbc.gridx++;
        add(dateValueLabel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }


}
