package views;

import models.GoodsReceiveNote;
import models.Item;
import models.Supplier;
import controllers.GoodsReceiveNoteService;
import controllers.ItemService;
import controllers.SupplierService;
import utils.EmailSender;
import utils.Generator;
import utils.Validation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

public class GoodsReceiveNoteDialog extends JDialog {
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final ItemService itemService;
    private final SupplierService supplierService;

    private JComboBox<Supplier> supplierComboBox;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<Item> itemComboBox;
    private JTextField quantityField;

    private final Supplier newSupplierPlaceholder = new Supplier();

    public GoodsReceiveNoteDialog(JFrame parentFrame, String title, GoodsReceiveNoteService goodsReceiveNoteService, ItemService itemService, SupplierService supplierService) {
        super(parentFrame, title, true);
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.itemService = itemService;
        this.supplierService = supplierService;

        initialize(parentFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel supplierLabel = new JLabel("Supplier:");
        supplierComboBox = new JComboBox<>(supplierService.getSupplierRegistry().values().toArray(new Supplier[0]));
        supplierComboBox.insertItemAt(newSupplierPlaceholder, 0);
        supplierComboBox.setSelectedIndex(0);

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

        JLabel itemLabel = new JLabel("Item:");
        itemComboBox = new JComboBox<>(itemService.getItemRegistry().values().toArray(new Item[0]));
        itemComboBox.setSelectedIndex(-1);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(20);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(supplierLabel, gbc);

        gbc.gridx++;
        add(supplierComboBox, gbc);

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

        gbc.gridx = 0;
        gbc.gridy++;
        add(itemLabel, gbc);

        gbc.gridx++;
        add(itemComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(quantityLabel, gbc);

        gbc.gridx = 1;
        add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        supplierComboBox.addActionListener(_ -> {
            if (supplierComboBox.getSelectedIndex() != 0) {
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

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !nameField.getText().isEmpty()) {
                    emailField.requestFocus();
                }
            }
        });

        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !emailField.getText().isEmpty()) {
                    quantityField.requestFocus();
                }
            }
        });

        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !quantityField.getText().isEmpty()) {
                    saveGoodsReceiveNote();
                }
            }
        });

        saveButton.addActionListener(_ -> saveGoodsReceiveNote());

        cancelButton.addActionListener(_ -> dispose());

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void saveGoodsReceiveNote() {
        Supplier supplier;

        if (supplierComboBox.getSelectedIndex() == 0) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an email", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Validation.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Email is not valid", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String supplierId = Generator.generateId("SUP", supplierService.getSupplierCount() + 1);
            supplier = new Supplier(supplierId, name, email);
            supplierService.insertSupplier(supplier);
        } else {
            supplier = (Supplier) supplierComboBox.getSelectedItem();
        }

        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item item = (Item) itemComboBox.getSelectedItem();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String goodsReceiveNoteId = Generator.generateDatedId("GRN", goodsReceiveNoteService.getGoodsReceiveNoteCount() + 1);
        Date receivedDate = new Date();

        GoodsReceiveNote goodsReceiveNote = new GoodsReceiveNote(goodsReceiveNoteId, supplier, item, receivedDate, quantity);
        goodsReceiveNoteService.insertGoodsReceiveNote(goodsReceiveNote);

        sendGoodsReceiveNote(goodsReceiveNote);
        dispose();
    }

    private void sendGoodsReceiveNote(GoodsReceiveNote goodsReceiveNote) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Goods Receive Note Details</h2>");
        sb.append("<p><strong>Goods Receive Note ID:</strong> ").append(goodsReceiveNote.getId()).append("</p>");
        sb.append("<p><strong>Date:</strong> ").append(goodsReceiveNote.getReceivedDate()).append("</p>");
        sb.append("<p><strong>Supplier:</strong> ").append(goodsReceiveNote.getSupplier().getName()).append("</p>");

        sb.append("<h3>Item Details:</h3>");

        sb.append("<table style='border-collapse: collapse; width: 100%;'>");
        sb.append("<tr style='background-color: #f2f2f2;'><th style='border: 1px solid #ddd; padding: 8px;'>Item Name</th>")
                .append("<th style='border: 1px solid #ddd; padding: 8px;'>Quantity</th>")
                .append("<th style='border: 1px solid #ddd; padding: 8px;'>Price</th></tr>");

        sb.append("<tr>")
                .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(goodsReceiveNote.getItem().getName()).append("</td>")
                .append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(goodsReceiveNote.getQuantity()).append("</td>")
                .append("<td style='border: 1px solid #ddd; padding: 8px;'>$").append(String.format("%.2f", goodsReceiveNote.getItem().getPrice())).append("</td>")
                .append("</tr>");

        sb.append("</table>");
        sb.append("<p>Thank you for your business!</p>");
        sb.append("</body></html>");

        EmailSender.sendEmail(goodsReceiveNote.getSupplier().getEmail(), "Goods Receive Note", sb.toString(), getParent());
    }
}
