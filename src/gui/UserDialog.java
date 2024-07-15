package gui;

import entities.User;
import services.UserService;
import utils.Generator;
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
    private int userCount;
    private String userId;
    private String userEmail;
    private String userPassword;

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
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !nameField.getText().isEmpty()) {
                    emailField.requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String name = nameField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                String count;

                if (userId == null) {
                    userCount = userService.getUserRegistry().size() + 1;
                    count = Generator.generateId("USER", userCount).split("-")[1];
                } else {
                    count = userId.split("-")[1];
                }

                if (name.isEmpty() || type == null) {
                    userEmail = "";
                    if (userId == null) {
                        userPassword = "";
                    }
                } else {
                    userEmail = name.toLowerCase().split(" ")[0] + "." + count + "@example.com";
                    if (userId == null) {
                        userPassword = Generator.generatePassword();
                    }
                }

                emailField.setText(userEmail);
                passwordField.setText(userPassword);
            }
        });

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !emailField.getText().isEmpty()) {
                    saveUser();
                }
            }
        });

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        passwordField = new JTextField(20);
        passwordField.setEditable(false);
        gbc.gridx = 1;
        add(passwordField, gbc);

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
            userId = Generator.generateId("USER", userCount);
            User user = new User(userId, name, email, password, type);
            userService.insertUser(user);
        } else {
            User user = new User(userId, name, email, password, type);
            userService.updateUser(user);
        }

        dispose();
    }
}
