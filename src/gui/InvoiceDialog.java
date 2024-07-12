package gui;

import entities.Customer;
import entities.Invoice;
import entities.Item;
import services.CustomerService;
import services.InvoiceService;
import services.ItemService;
import services.PaymentService;
import utils.EmailSender;
import utils.IDGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvoiceDialog extends JDialog {
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final PaymentService paymentService;

    private JComboBox<Customer> customerComboBox;
    private JComboBox<Item> itemComboBox;
    private JTextField quantityField;
    private final Map<Item, Integer> items = new HashMap<>();

    public InvoiceDialog(JFrame parentFrame, String title, InvoiceService invoiceService, CustomerService customerService, ItemService itemService, PaymentService paymentService) {
        super(parentFrame, title, true);
        this.invoiceService = invoiceService;
        this.customerService = customerService;
        this.itemService = itemService;
        this.paymentService = paymentService;

        initialize(parentFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel customerLabel = new JLabel("Customer:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(customerLabel, gbc);

        customerComboBox = new JComboBox<>(customerService.getCustomerRegistry().values().toArray(new Customer[0]));
        gbc.gridx = 1;
        add(customerComboBox, gbc);

        JPanel itemsPanel = new JPanel();
        JTable itemsTable = new JTable();
        DefaultTableModel itemsTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 2, 3 -> Integer.class;
                    default -> String.class;
                };
            }
        };
        itemsTable.setModel(itemsTableModel);
        itemsTable.setFillsViewportHeight(true);
        itemsTable.setRowHeight(30);

        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsPanel.add(itemsScrollPane);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(itemsPanel, gbc);

        JLabel itemLabel = new JLabel("Item:");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(itemLabel, gbc);

        itemComboBox = new JComboBox<>(itemService.getItemRegistry().values().toArray(new Item[0]));
        gbc.gridx = 1;
        add(itemComboBox, gbc);

        JLabel quantityLabel = new JLabel("Quantity:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(quantityLabel, gbc);

        quantityField = new JTextField(10);
        gbc.gridx = 1;
        add(quantityField, gbc);

        JPanel itemButtonPanel = new JPanel();

        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(_ -> addItem(itemsTableModel));

        JButton removeItemButton = new JButton("Remove Item");
        removeItemButton.addActionListener(_ -> removeItem(itemsTableModel, itemsTable));

        itemButtonPanel.add(addItemButton);
        itemButtonPanel.add(removeItemButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(itemButtonPanel, gbc);

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveInvoice());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void addItem(DefaultTableModel tableModel) {
        Item item = (Item) itemComboBox.getSelectedItem();

        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (item.getQuantity() < quantity) {
            JOptionPane.showMessageDialog(this, "Not enough stock.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (items.containsKey(item)) {
            items.put(item, items.get(item) + quantity);
        } else {
            items.put(item, quantity);
        }
        tableModel.addRow(new Object[]{item.getId(), item.getName(), item.getPrice(), quantity});

        itemComboBox.setSelectedIndex(-1);
        quantityField.setText("");

        itemComboBox.requestFocus();
    }

    private void removeItem(DefaultTableModel tableModel, JTable itemsTable) {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item item = itemService.getItemById((String) tableModel.getValueAt(selectedRow, 0));
        items.remove(item);
        tableModel.removeRow(selectedRow);
    }

    private void saveInvoice() {
        Customer customer = (Customer) customerComboBox.getSelectedItem();

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceCount = invoiceService.getInvoiceRegistry().size() + 1;
        String invoiceId = IDGenerator.generateDatedId("INV", invoiceCount);
        Date date = new Date();
        double totalAmount = calculateTotalAmount(items);

        Invoice invoice = new Invoice(invoiceId, customer, items, date, totalAmount);
        invoiceService.insertInvoice(invoice);

        PaymentDialog paymentDialog = new PaymentDialog((JFrame) getParent(), "Payment", invoice, paymentService);
        paymentDialog.setVisible(true);

        sendInvoice(invoice);
        dispose();
    }

    private double calculateTotalAmount(Map<Item, Integer> itemsMap) {
        double totalAmount = 0;
        for (Map.Entry<Item, Integer> entry : itemsMap.entrySet()) {
            totalAmount += entry.getKey().getPrice() * entry.getValue();
        }
        return totalAmount;
    }

    private void sendInvoice(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice ID: ").append(invoice.getId()).append("\n");
        sb.append("Date: ").append(invoice.getDate()).append("\n");
        sb.append("Customer: ").append(invoice.getCustomer().getName()).append("\n\n");
        sb.append("Items:\n");
        for (Map.Entry<Item, Integer> entry : invoice.getItemsMap().entrySet()) {
            sb.append(entry.getKey().getName()).append(" x").append(entry.getValue()).append("\n");
        }
        sb.append("\nTotal Amount: ").append(invoice.getTotalAmount());

        EmailSender.sendEmail(invoice.getCustomer().getEmail(), "Invoice", sb.toString(), getParent());
    }
}
