package GUI;

import entities.Item;
import services.ItemService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterItemPanel extends JPanel {
    private ItemService itemService;

    public RegisterItemPanel(ItemService itemService) {
        this.itemService = itemService;

        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();
        add(nameLabel);
        add(nameField);

        JLabel priceLabel = new JLabel("Item Price:");
        JTextField priceField = new JTextField();
        add(priceLabel);
        add(priceField);

        JLabel quantityLabel = new JLabel("Item Quantity:");
        JTextField quantityField = new JTextField();
        add(quantityLabel);
        add(quantityField);

        JButton registerButton = new JButton("Register Item");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = nameField.getText().trim();
                double itemPrice = Double.parseDouble(priceField.getText().trim());
                int itemQuantity = Integer.parseInt(quantityField.getText().trim());

                Item item = new Item();
                item.setName(itemName);
                item.setPrice(itemPrice);
                item.setQuantity(itemQuantity);

                itemService.registerItem(item);
                JOptionPane.showMessageDialog(RegisterItemPanel.this, "Item registered successfully.");
            }
        });
        add(registerButton);
    }
}

