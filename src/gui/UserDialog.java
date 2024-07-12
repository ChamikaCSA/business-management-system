package gui;

import entities.User;
import services.UserService;
import utils.IDGenerator;
import utils.Validation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UserDialog extends JDialog {
    private final UserService userService;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField passwordField;
    private JComboBox<String> typeComboBox;
    private String userId;

    public UserDialog(JFrame parentFrame, String title, UserService userService) {
        super(parentFrame, title, true);
        this.userService = userService;

        initialize(parentFrame);
    }

    public UserDialog(JFrame parentFrame, String title, UserService userService, String userId) {
        super(parentFrame, title, true);
        this.userService = userService;
        this.userId = userId;

        initialize(parentFrame);
        loadUserData();
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(typeLabel, gbc);

        typeComboBox = new JComboBox<>(new String[]{"Admin", "Worker"});
        gbc.gridx = 1;
        add(typeComboBox, gbc);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        passwordField = new JTextField(20);
        passwordField.setEditable(false);
        gbc.gridx = 1;
        add(passwordField, gbc);

        typeComboBox.addActionListener(_ -> {
            if (nameField.getText().isEmpty()) {
                return;
            }
            String name = nameField.getText();
            String type = (String) typeComboBox.getSelectedItem();

            assert type != null;
            String userEmail = STR."\{name.toLowerCase().replace(" ", ".")}.\{type.toLowerCase().replace(" ", ".")}@example.com";
            String userPassword = IDGenerator.generateRandomPassword();

            emailField.setText(userEmail);
            passwordField.setText(userPassword);
        });

        nameField.addKeyListener (new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String name = nameField.getText();
                String type = (String) typeComboBox.getSelectedItem();

                assert type != null;
                String userEmail = STR."\{name.toLowerCase().replace(" ", ".")}.\{type.toLowerCase().replace(" ", ".")}@example.com";
                String userPassword = IDGenerator.generateRandomPassword();

                emailField.setText(userEmail);
                passwordField.setText(userPassword);

            }
        });

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveUser());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void loadUserData() {
        User user = userService.getUserById(userId);
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        passwordField.setText("********");
        typeComboBox.setSelectedItem(user.getType());
    }

    private void saveUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String type = (String) typeComboBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an email.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Validation.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email. Please enter a valid email.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (type == null) {
            JOptionPane.showMessageDialog(this, "Please select a type.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userId == null) {
            int userCount = userService.getUserRegistry().size() + 1;
            userId = IDGenerator.generateId("USER", userCount);
            User user = new User(userId, name, email, password, type);
            userService.insertUser(user);
        } else {
            User user = new User(userId, name, email, password, type);
            userService.updateUser(user);
        }

        dispose();
    }
}
