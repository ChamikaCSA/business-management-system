package gui;

import entities.User;
import services.UserService;
import utils.EmailSender;
import utils.Generator;
import utils.Validation;

import static gui.AppGUI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class ForgotPasswordPanel extends JPanel {
    private final JPanel loginPanel;
    private final UserService userService;
    private final String email;

    private GridBagConstraints gbc;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel statusLabel;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel codeLabel;
    private JTextField codeField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPasswordField;
    private JPanel buttonPanel;
    private JButton sendButton;
    private JButton verifyButton;
    private JButton submitButton;
    private JButton backButton;
    private JButton loginButton;

    public ForgotPasswordPanel(JPanel loginPanel, UserService userService, String email) {
        this.loginPanel = loginPanel;
        this.userService = userService;
        this.email = email;

        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel("Forgot Password?");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        subtitleLabel = new JLabel("We'll send you a verification code to reset your password.");
        subtitleLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

        emailField = new JTextField(20);
        emailField.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        emailField.setToolTipText("Enter your email address");
        emailField.setPreferredSize(new Dimension(300, 30));
        emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        emailField.setText(email);

        sendButton = new JButton("Send Code");
        sendButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(BACKGROUND_COLOR);
        sendButton.setPreferredSize(new Dimension(150, 45));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setFocusPainted(false);

        backButton = new JButton("Back");
        backButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        backButton.setBackground(SECONDARY_COLOR);
        backButton.setForeground(BACKGROUND_COLOR);
        backButton.setPreferredSize(new Dimension(150, 45));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);

        buttonPanel = new JPanel(new BorderLayout(10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(sendButton, BorderLayout.CENTER);
        buttonPanel.add(backButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy++;
        add(subtitleLabel, gbc);

        gbc.gridy++;
        add(statusLabel, gbc);

        gbc.gridy++;
        add(emailLabel, gbc);

        gbc.gridy++;
        add(emailField, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                emailField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR_HOVER, 1));
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                if (!emailField.getText().isEmpty() && !Validation.isValidEmail(emailField.getText().trim())) {
                    setStatus("Invalid email format.", Color.RED);
                }
            }
        });

        emailField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSend();
                }
            }
        });

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                sendButton.setBackground(PRIMARY_COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                sendButton.setBackground(PRIMARY_COLOR);
            }
        });

        sendButton.addActionListener(_ -> handleSend());

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                backButton.setBackground(SECONDARY_COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                backButton.setBackground(SECONDARY_COLOR);
            }
        });

        backButton.addActionListener(_ -> {
            loginPanel.setVisible(true);
            setVisible(false);

            revalidate();
            repaint();
        });
    }

    private void handleSend() {
        clearStatus();
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            setStatus("Please enter your email.", Color.RED);
        } else if (!Validation.isValidEmail(email)) {
            setStatus("Invalid email format.", Color.RED);
        } else if (userService.getUserByEmail(email) == null) {
            setStatus("Email is not registered.", Color.RED);
        } else {
            sendCode(email);
        }
    }

    private void sendCode(String email) {
        String code = Generator.generateCode();
        System.out.println("Code: " + code);
        EmailSender.sendEmail(email, "Password Reset", "Your password reset code is: " + code + ".\n\nEnter this code to reset your password.", getParent());
        setStatus("A verification code has been sent to your email.", Color.GREEN);

        titleLabel.setText("Verification");

        subtitleLabel.setText("Enter the verification code sent to your email.");

        statusLabel.setText("");

        codeLabel = new JLabel("Verification Code");
        codeLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

        codeField = new JTextField(20);
        codeField.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        codeField.setToolTipText("Enter the verification code");
        codeField.setPreferredSize(new Dimension(300, 30));
        codeField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        verifyButton = new JButton("Verify Code");
        verifyButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        verifyButton.setBackground(PRIMARY_COLOR);
        verifyButton.setForeground(BACKGROUND_COLOR);
        verifyButton.setPreferredSize(new Dimension(150, 45));
        verifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verifyButton.setFocusPainted(false);

        buttonPanel.removeAll();
        buttonPanel.add(verifyButton, BorderLayout.CENTER);
        buttonPanel.add(backButton, BorderLayout.EAST);

        removeAll();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy++;
        add(subtitleLabel, gbc);

        gbc.gridy++;
        add(statusLabel, gbc);

        gbc.gridy++;
        add(codeLabel, gbc);

        gbc.gridy++;
        add(codeField, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        revalidate();
        repaint();

        codeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                codeField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR_HOVER, 1));
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                codeField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            }
        });

        codeField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleVerify(code);
                }
            }
        });

        verifyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                verifyButton.setBackground(PRIMARY_COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                verifyButton.setBackground(PRIMARY_COLOR);
            }
        });

        verifyButton.addActionListener(_ -> handleVerify(code));

        backButton.addActionListener(_ -> {
            removeAll();
            initialize();
            revalidate();
            repaint();
        });
    }

    private void handleVerify(String code) {
        clearStatus();
        String enteredCode = codeField.getText().trim();
        if (enteredCode.isEmpty()) {
            setStatus("Please enter the verification code.", Color.RED);
        } else if (!enteredCode.equals(code)) {
            setStatus("Invalid verification code.", Color.RED);
        } else {
            setStatus("Verification successful.", Color.GREEN);

            titleLabel.setText("Set New Password");

            subtitleLabel.setText("Your new password must be different from your previous password.");

            statusLabel.setText("");

            passwordLabel = new JLabel("New Password");
            passwordLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

            passwordField = new JPasswordField(20);
            passwordField.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
            passwordField.setToolTipText("Enter your new password");
            passwordField.setPreferredSize(new Dimension(300, 30));
            passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            confirmPasswordLabel = new JLabel("Confirm Password");
            confirmPasswordLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

            confirmPasswordField = new JPasswordField(20);
            confirmPasswordField.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
            confirmPasswordField.setToolTipText("Confirm your new password");
            confirmPasswordField.setPreferredSize(new Dimension(300, 30));
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            submitButton = new JButton("Submit");
            submitButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
            submitButton.setBackground(PRIMARY_COLOR);
            submitButton.setForeground(BACKGROUND_COLOR);
            submitButton.setPreferredSize(new Dimension(150, 45));
            submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            submitButton.setFocusPainted(false);

            buttonPanel.removeAll();
            buttonPanel.add(submitButton, BorderLayout.CENTER);
            buttonPanel.add(backButton, BorderLayout.EAST);

            removeAll();

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            add(titleLabel, gbc);

            gbc.gridy++;
            add(subtitleLabel, gbc);

            gbc.gridy++;
            add(statusLabel, gbc);

            gbc.gridy++;
            add(passwordLabel, gbc);

            gbc.gridy++;
            add(passwordField, gbc);

            gbc.gridy++;
            add(confirmPasswordLabel, gbc);

            gbc.gridy++;
            add(confirmPasswordField, gbc);

            gbc.gridy++;
            add(buttonPanel, gbc);

            revalidate();
            repaint();

            passwordField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    passwordField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR_HOVER, 1));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                }
            });

            passwordField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        clearStatus();
                        char[] password = passwordField.getPassword();
                        char[] oldPassword = userService.getUserByEmail(email).getPassword().toCharArray();
                        if (password.length == 0) {
                            setStatus("Please enter your password.", Color.RED);
                        } else if (!Validation.isValidPassword(password)) {
                            setStatus("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.", Color.RED);
                        } else if (String.valueOf(password).equals(String.valueOf(oldPassword))) {
                            setStatus("New password must be different from your previous password.", Color.RED);
                        } else {
                            confirmPasswordField.requestFocus();
                        }
                    }
                }
            });

            confirmPasswordField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    confirmPasswordField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR_HOVER, 1));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                }
            });

            confirmPasswordField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        handleSubmit();
                    }
                }
            });

            submitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    submitButton.setBackground(PRIMARY_COLOR_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    submitButton.setBackground(PRIMARY_COLOR);
                }
            });

            submitButton.addActionListener(_ -> handleSubmit());

            backButton.addActionListener(_ -> {
                removeAll();
                initialize();
                revalidate();
                repaint();
            });
        }
    }

    private void handleSubmit() {
        clearStatus();
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();
        char[] oldPassword = userService.getUserByEmail(email).getPassword().toCharArray();
        if (email.isEmpty()) {
            setStatus("Please enter your email.", Color.RED);
        } else if (!Validation.isValidEmail(email)) {
            setStatus("Invalid email format.", Color.RED);
        } else if (password.length == 0) {
            setStatus("Please enter your password.", Color.RED);
        } else if (!Validation.isValidPassword(password)) {
            setStatus("Password must be at least 8 characters long and contain at least one uppercase letter,\n one lowercase letter, one digit, and one special character.", Color.RED);
        } else if (String.valueOf(password).equals(String.valueOf(oldPassword))) {
            setStatus("New password must be different from your previous password.", Color.RED);
        } else if (confirmPassword.length == 0) {
            setStatus("Please confirm your password.", Color.RED);
        } else if (!String.valueOf(password).equals(String.valueOf(confirmPassword))) {
            setStatus("Passwords do not match.", Color.RED);
        } else {
            changePassword(email, password);
        }
    }

    private void changePassword(String email, char[] password) {
        String newPassword = String.valueOf(password);
        String hashedPassword = userService.hashPassword(newPassword);
        User user = userService.getUserByEmail(email);
        user.setPassword(hashedPassword);
        userService.updateUser(user);
        EmailSender.sendEmail(user.getEmail(), "Password Reset", "Your password has been reset.", getParent());
        setStatus("Password reset successful.", Color.GREEN);

        titleLabel.setText("Success");

        subtitleLabel.setText("Your password has been reset successfully.");

        statusLabel.setText("");

        loginButton = new JButton("Login");
        loginButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(BACKGROUND_COLOR);
        loginButton.setPreferredSize(new Dimension(150, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);

        buttonPanel.removeAll();
        buttonPanel.add(loginButton, BorderLayout.CENTER);

        removeAll();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy++;
        add(subtitleLabel, gbc);

        gbc.gridy++;
        add(statusLabel, gbc);

        gbc.gridy++;
        add(buttonPanel, gbc);

        revalidate();
        repaint();

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                loginButton.setBackground(PRIMARY_COLOR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                loginButton.setBackground(PRIMARY_COLOR);
            }
        });

        loginButton.addActionListener(_ -> {
            loginPanel.setVisible(true);
            setVisible(false);

            revalidate();
            repaint();
        });
    }

    private void setStatus(String message, Color color) {
        String formattedMessage = "<html>" + message.replace("\n", "<br>") + "</html>";
        statusLabel.setText(formattedMessage);
        statusLabel.setForeground(color);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }
}
