package GUI;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import services.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AppGUI extends JFrame {
    private final CustomerService customerService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final ScaleLicenseService scaleLicenseService;
    private final SupplierService supplierService;
    private final UserService userService;

    private static final Logger LOGGER = Logger.getLogger(AppGUI.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private JTextField emailField;
    private JPasswordField passwordField;
    private JToggleButton showPasswordToggle;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public AppGUI() {
        customerService = new CustomerService();
        goodsReceiveNoteService = new GoodsReceiveNoteService();
        invoiceService = new InvoiceService();
        itemService = new ItemService();
        scaleLicenseService = new ScaleLicenseService();
        supplierService = new SupplierService();
        userService = new UserService();

        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            LOGGER.warning("Failed to apply FlatLaf Look and Feel: " + e.getMessage());
        }

        setTitle("Business Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Business Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        emailField.setToolTipText("Enter your email address");
        emailField.setPreferredSize(new Dimension(300, 30));
        emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setToolTipText("Enter your password");
        passwordField.setPreferredSize(new Dimension(300, 30));
        passwordField.setBorder(BorderFactory.createEmptyBorder());

        ImageIcon showIcon = new ImageIcon("icons/eye.png");
        ImageIcon hideIcon = new ImageIcon("icons/hidden.png");

        showPasswordToggle = new JToggleButton();
        showPasswordToggle.setIcon(showIcon);
        showPasswordToggle.setPreferredSize(new Dimension(20, 20));
        showPasswordToggle.setBorder(BorderFactory.createEmptyBorder());
        showPasswordToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordToggle.setBackground(Color.WHITE);
        showPasswordToggle.setFocusPainted(false);
        showPasswordToggle.setToolTipText("Show/Hide Password");
        showPasswordToggle.setVisible(false);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setPreferredSize(new Dimension(300, 30));
        passwordPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordToggle, BorderLayout.EAST);

        JButton loginButton = new JButton("Login");
        JButton clearButton = new JButton("Clear");

        loginButton.setBackground(new Color(34, 140, 220));
        loginButton.setForeground(new Color(245, 245, 245));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(150, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);

        clearButton.setBackground(new Color(181, 43, 121));
        clearButton.setForeground(new Color(245, 245, 245));
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        clearButton.setPreferredSize(new Dimension(150, 45));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setFocusPainted(false);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        loginPanel.add(subtitleLabel, gbc);

        gbc.gridy = 4;
        loginPanel.add(emailLabel, gbc);

        gbc.gridy = 5;
        loginPanel.add(emailField, gbc);

        gbc.gridy = 6;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy = 7;
        loginPanel.add(passwordPanel, gbc);


        JPanel buttonPanel = new JPanel(new BorderLayout(10 , 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(loginButton, BorderLayout.CENTER);
        buttonPanel.add(clearButton, BorderLayout.EAST);

        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(buttonPanel, gbc);

        gbc.gridy = 2;
        loginPanel.add(progressBar, gbc);

        gbc.gridy = 3;
        loginPanel.add(statusLabel, gbc);

        add(loginPanel, BorderLayout.CENTER);

        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                emailField.setBorder(BorderFactory.createLineBorder(new Color(77, 163, 228), 1));
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                if (!emailField.getText().isEmpty() && isInvalidEmail(emailField.getText().trim())) {
                    setStatus("Invalid email format.", Color.RED);
                }
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(77, 163, 228), 1));
                showPasswordToggle.setVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                passwordPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                if (new String(passwordField.getPassword()).isEmpty()) {
                    showPasswordToggle.setVisible(false);
                }
            }
        });

        showPasswordToggle.addActionListener(_ -> {
            if (showPasswordToggle.isSelected()) {
                passwordField.setEchoChar((char) 0);
                showPasswordToggle.setIcon(hideIcon);
            } else {
                passwordField.setEchoChar('•');
                showPasswordToggle.setIcon(showIcon);
            }
        });

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(77, 163, 228));
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(34, 140, 240));
            }
        });

        loginButton.addActionListener(_ -> {
            clearStatus();
            handleLogin();
        });

        clearButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                clearButton.setBackground(new Color(210, 53, 141));
            }

            public void mouseExited(MouseEvent evt) {
                clearButton.setBackground(new Color(181, 43, 121));
            }
        });

        clearButton.addActionListener(_ -> {
            emailField.setText("");
            passwordField.setText("");
            clearStatus();
            emailField.requestFocus();
        });

        setVisible(true);
    }

    private void handleLogin() {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() && password.isEmpty()) {
            setStatus("Email and password must not be empty.", Color.RED);
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
            return;
        } else if (!email.isEmpty() && (isInvalidEmail(email))) {
            setStatus("Invalid email format.", Color.RED);
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
            return;
        } else if (email.isEmpty() || password.isEmpty()) {
            setStatus("Email and password must not be empty.", Color.RED);
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
            return;
        }

        try {
            String userType = userService.authenticateUser(email, password);

            if (userType != null) {
                setStatus("Login successful.", Color.GREEN);
                openMenu(userType);
            } else {
                setStatus("Invalid username or password. Please try again.", Color.RED);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "An error occurred during login", ex);
            setStatus(STR."An error occurred: \{ex.getMessage()}", Color.RED);
        } finally {
            progressBar.setVisible(false);
            progressBar.setIndeterminate(false);
        }
    }

    private void openMenu(String userType) {
        JFrame menuFrame = new JFrame("Menu");
        menuFrame.setSize(1000, 800);
        menuFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        menuFrame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        if (userType.equalsIgnoreCase("admin")) {
            tabbedPane.addTab("Admin Dashboard", new AdminMenuPanel(menuFrame, customerService, goodsReceiveNoteService, invoiceService,
                    itemService, scaleLicenseService, supplierService, userService));
            tabbedPane.addTab("Reports", createReportsPanel());
        } else if (userType.equalsIgnoreCase("worker")) {
            tabbedPane.addTab("Worker Dashboard", new WorkerMenuPanel(menuFrame, customerService, goodsReceiveNoteService, invoiceService,
                    itemService, scaleLicenseService, supplierService));
            tabbedPane.addTab("Tasks", createTasksPanel());
        }

        menuFrame.add(tabbedPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(_ -> {
            menuFrame.dispose();

            emailField.setText("");
            passwordField.setText("");
            clearStatus();
            emailField.requestFocus();
            setVisible(true);
        });
        toolBar.add(logoutButton);

        menuFrame.add(toolBar, BorderLayout.NORTH);

        menuFrame.setVisible(true);
        setVisible(false);
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setText("Reports content goes here...");
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea taskArea = new JTextArea();
        taskArea.setEditable(false);
        taskArea.setText("Tasks content goes here...");
        panel.add(new JScrollPane(taskArea), BorderLayout.CENTER);
        return panel;
    }

    private void setStatus(String message, Color color) {
        statusLabel.setForeground(color);
        statusLabel.setText(message);
    }

    private void clearStatus() {
        statusLabel.setText(" ");
    }

    private boolean isInvalidEmail(String email) {
        return !EMAIL_PATTERN.matcher(email).matches();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppGUI::new);
    }
}
