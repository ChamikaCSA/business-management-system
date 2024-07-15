package gui;

import entities.Customer;
import entities.Invoice;
import entities.Item;
import services.CustomerService;
import services.InvoiceService;
import services.ItemService;
import services.PaymentService;
import utils.EmailSender;
import utils.Generator;
import utils.Validation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvoiceDialog extends JDialog {
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final PaymentService paymentService;

    private JComboBox<Customer> customerComboBox;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<Item> itemComboBox;
    private JTextField quantityField;

    private final Map<Item, Integer> items = new HashMap<>();
    private final Customer newCustomerPlaceholder = new Customer();

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
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel customerLabel = new JLabel("Customer:");
        customerComboBox = new JComboBox<>(customerService.getCustomerRegistry().values().toArray(new Customer[0]));
        customerComboBox.insertItemAt(newCustomerPlaceholder, 0);
        customerComboBox.setSelectedIndex(0);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        Dimension placeholderSize = new Dimension(0, nameField.getPreferredSize().height);

        JPanel placeholderPanel1 = new JPanel();
        placeholderPanel1.setPreferredSize(placeholderSize);
        placeholderPanel1.setVisible(false);

        JPanel placeholderPanel2 = new JPanel();
        placeholderPanel2.setPreferredSize(placeholderSize);
        placeholderPanel2.setVisible(false);

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
        JPanel itemsPanel = new JPanel();
        itemsPanel.add(itemsScrollPane);

        JLabel itemLabel = new JLabel("Item:");
        itemComboBox = new JComboBox<>(itemService.getItemRegistry().values().toArray(new Item[0]));
        itemComboBox.setSelectedIndex(-1);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(10);

        JButton addItemButton = new JButton("Add Item");
        addItemButton.setPreferredSize(new Dimension(120, 30));
        JButton removeItemButton = new JButton("Remove Item");
        removeItemButton.setPreferredSize(new Dimension(120, 30));

        JPanel itemButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        itemButtonPanel.add(addItemButton);
        itemButtonPanel.add(removeItemButton);

        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(120, 30));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(customerLabel, gbc);

        gbc.gridx++;
        add(customerComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(nameLabel, gbc);

        gbc.gridx++;
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(emailLabel, gbc);

        gbc.gridx++;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(placeholderPanel1, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(placeholderPanel2, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        add(itemsPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(itemLabel, gbc);

        gbc.gridx++;
        add(itemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(quantityLabel, gbc);

        gbc.gridx++;
        add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(itemButtonPanel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        customerComboBox.addActionListener(_ -> {
            if (customerComboBox.getSelectedIndex() != 0) {
                nameLabel.setVisible(false);
                nameField.setVisible(false);
                emailLabel.setVisible(false);
                emailField.setVisible(false);
                placeholderPanel1.setVisible(true);
                placeholderPanel2.setVisible(true);
            } else {
                nameLabel.setVisible(true);
                nameField.setVisible(true);
                emailLabel.setVisible(true);
                emailField.setVisible(true);
                placeholderPanel1.setVisible(false);
                placeholderPanel2.setVisible(false);
            }
        });

        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    emailField.requestFocus();
                }
            }
        });

        emailField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    itemComboBox.requestFocus();
                }
            }
        });

        itemComboBox.addActionListener(_ -> quantityField.requestFocus());

        quantityField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !quantityField.getText().isEmpty()) {
                    addItem(itemsTableModel);
                }
            }
        });

        addItemButton.addActionListener(_ -> addItem(itemsTableModel));
        removeItemButton.addActionListener(_ -> removeItem(itemsTableModel, itemsTable));

        saveButton.addActionListener(_ -> saveInvoice());
        cancelButton.addActionListener(_ -> dispose());

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
        Customer customer;

        if (customerComboBox.getSelectedIndex() == 0) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an email.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Validation.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Email is not valid.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String customerId = Generator.generateId("CUST", customerService.getCustomerRegistry().size() + 1);
            customer = new Customer(customerId, name, email);
            customerService.insertCustomer(customer);
        } else {
            customer = (Customer) customerComboBox.getSelectedItem();
        }

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceCount = invoiceService.getInvoiceRegistry().size() + 1;
        String invoiceId = Generator.generateDatedId("INV", invoiceCount);
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
