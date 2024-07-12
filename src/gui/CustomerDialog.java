package gui;

import entities.Customer;
import services.CustomerService;
import utils.IDGenerator;
import utils.Validation;

import javax.swing.*;
import java.awt.*;

public class CustomerDialog extends JDialog {
    private final CustomerService customerService;

    private JTextField nameField;
    private JTextField emailField;
    private String customerId;

    public CustomerDialog(JFrame parentFrame, String title, CustomerService customerService) {
        super(parentFrame, title, true);
        this.customerService = customerService;

        initialize(parentFrame);
    }

    public CustomerDialog(JFrame parentFrame, String title, CustomerService customerService, String customerId) {
        super(parentFrame, title, true);
        this.customerService = customerService;
        this.customerId = customerId;

        initialize(parentFrame);
        loadCustomerData();
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveCustomer());
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

    private void loadCustomerData() {
        Customer customer = customerService.getCustomerById(customerId);
        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());
    }

    private void saveCustomer() {
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
            JOptionPane.showMessageDialog(this, "Please enter a valid email", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (customerId == null) {
            int customerCount = customerService.getCustomerRegistry().size() + 1;
            customerId = IDGenerator.generateId("CUST", customerCount);
            Customer customer = new Customer(customerId, name, email);
            customerService.insertCustomer(customer);

        } else {
            Customer customer = new Customer(customerId, name, email);
            customerService.updateCustomer(customer);
        }

        dispose();
    }
}
