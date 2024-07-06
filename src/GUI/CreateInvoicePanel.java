package GUI;

import entities.Customer;
import entities.Invoice;
import entities.Item;
import services.CustomerService;
import services.InvoiceService;
import services.ItemService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CreateInvoicePanel extends JPanel {
    private ItemService itemService;
    private CustomerService customerService;
    private InvoiceService invoiceService;

    public CreateInvoicePanel(ItemService itemService, CustomerService customerService, InvoiceService invoiceService) {
        this.itemService = itemService;
        this.customerService = customerService;
        this.invoiceService = invoiceService;

        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel customerIdLabel = new JLabel("Customer ID:");
        JTextField customerIdField = new JTextField();
        add(customerIdLabel);
        add(customerIdField);

        JLabel itemIdLabel = new JLabel("Item ID:");
        JTextField itemIdField = new JTextField();
        add(itemIdLabel);
        add(itemIdField);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        add(quantityLabel);
        add(quantityField);

        JButton createInvoiceButton = new JButton("Create Invoice");
        createInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerId = customerIdField.getText().trim();
                String itemId = itemIdField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                Customer customer = customerService.getCustomerById(customerId);
                Item item = itemService.getItemById(itemId);

                if (customer == null) {
                    JOptionPane.showMessageDialog(CreateInvoicePanel.this, "Customer not found.");
                    return;
                }

                if (item == null) {
                    JOptionPane.showMessageDialog(CreateInvoicePanel.this, "Item not found.");
                    return;
                }

                // Create invoice logic here
                // Example: Calculate total amount, set items, etc.

                Invoice invoice = new Invoice();
                // Set invoice details
                invoiceService.registerInvoice(invoice);
                JOptionPane.showMessageDialog(CreateInvoicePanel.this, "Invoice created successfully.");
            }
        });
        add(createInvoiceButton);
    }
}

