package views;

import models.Item;
import controllers.ItemService;
import utils.Generator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ItemDialog extends JDialog {
    private final ItemService itemService;

    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    private String itemId;

    public ItemDialog(JFrame parentFrame, String title, ItemService itemService) {
        super(parentFrame, title, true);
        this.itemService = itemService;

        initialize(parentFrame);
    }

    public ItemDialog(JFrame parentFrame, String title, ItemService itemService, String itemId) {
        super(parentFrame, title, true);
        this.itemService = itemService;
        this.itemId = itemId;

        initialize(parentFrame);
        loadItemData();
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        add(nameField, gbc);

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !nameField.getText().isEmpty()) {
                    priceField.requestFocus();
                }
            }
        });

        JLabel priceLabel = new JLabel("Price:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(priceLabel, gbc);

        priceField = new JTextField(20);
        gbc.gridx = 1;
        add(priceField, gbc);

        priceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !priceField.getText().isEmpty()) {
                    quantityField.requestFocus();
                }
            }
        });

        JLabel quantityLabel = new JLabel("Quantity:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(quantityLabel, gbc);

        quantityField = new JTextField(20);
        gbc.gridx = 1;
        add(quantityField, gbc);

        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !quantityField.getText().isEmpty()) {
                    saveItem();
                }
            }
        });

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveItem());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void loadItemData() {
        Item item = itemService.getItemById(itemId);
        nameField.setText(item.getName());
        priceField.setText(String.valueOf(item.getPrice()));
        quantityField.setText(String.valueOf(item.getQuantity()));
    }

    private void saveItem() {
        String name = nameField.getText().trim();
        double price;
        int quantity;

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (itemId == null) {
            itemId = Generator.generateId("ITEM", itemService.getItemCount() + 1);
            Item item = new Item(itemId, name, price, quantity);
            itemService.insertItem(item);
        } else {
            Item item = new Item(itemId, name, price, quantity);
            itemService.updateItem(item);
        }

        dispose();
    }
}
