package gui;

import entities.Customer;
import services.CustomerService;
import utils.Generator;
import utils.Validation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
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
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

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
                    saveCustomer();
                }
            }
        });

        saveButton.addActionListener(_ -> saveCustomer());

        cancelButton.addActionListener(_ -> dispose());

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
            customerId = Generator.generateId("CUST", customerCount);
            Customer customer = new Customer(customerId, name, email);
            customerService.insertCustomer(customer);

        } else {
            Customer customer = new Customer(customerId, name, email);
            customerService.updateCustomer(customer);
        }

        dispose();
    }
}
