package gui;

import entities.Supplier;
import services.SupplierService;
import utils.Generator;
import utils.Validation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SupplierDialog extends JDialog {
    private final SupplierService supplierService;

    private JTextField nameField;
    private JTextField emailField;
    private String supplierId;

    public SupplierDialog(JFrame parentFrame, String title, SupplierService supplierService) {
        super(parentFrame, title, true);
        this.supplierService = supplierService;

        initialize(parentFrame);
    }

    public SupplierDialog(JFrame parentFrame, String title, SupplierService supplierService, String supplierId) {
        super(parentFrame, title, true);
        this.supplierService = supplierService;
        this.supplierId = supplierId;

        initialize(parentFrame);
        loadSupplierData();
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
                    emailField.requestFocus();
                }
            }
        });

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !emailField.getText().isEmpty()) {
                    saveSupplier();
                }
            }
        });

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveSupplier());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void loadSupplierData() {
        Supplier supplier = supplierService.getSupplierById(supplierId);
        nameField.setText(supplier.getName());
        emailField.setText(supplier.getEmail());
    }

    private void saveSupplier() {
        String name = nameField.getText();
        String email = emailField.getText();

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

        if (supplierId == null) {
            supplierId = Generator.generateId("SUP", supplierService.getSupplierRegistry().size() + 1);
            Supplier supplier = new Supplier(supplierId, name, email);
            supplierService.insertSupplier(supplier);
        } else {
            Supplier supplier = new Supplier(supplierId, name, email);
            supplierService.updateSupplier(supplier);
        }

        dispose();
    }
}
