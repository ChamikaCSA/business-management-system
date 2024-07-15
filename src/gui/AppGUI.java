package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import services.*;
import utils.Validation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppGUI extends JFrame {
    private final CustomerService customerService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final ScaleLicenseService scaleLicenseService;
    private final SupplierService supplierService;
    private final UserService userService;

    private static JTabbedPane tabbedPane;

    private JPanel loginPanel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JToggleButton showPasswordToggle;
    private JButton forgotPasswordButton;

    private static final Logger LOGGER = Logger.getLogger(AppGUI.class.getName());

    public static final String FONT_NAME = "Segoe UI";

    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    public static final Color PRIMARY_COLOR = new Color(0, 122, 255);
    public static final Color PRIMARY_COLOR_HOVER = new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 200);
    public static final Color SECONDARY_COLOR = new Color(188, 60, 144);
    public static final Color SECONDARY_COLOR_HOVER = new Color(SECONDARY_COLOR.getRed(), SECONDARY_COLOR.getGreen(), SECONDARY_COLOR.getBlue(), 200);
    public static final Color TERTIARY_COLOR = new Color(255, 0, 122);
    public static final Color TERTIARY_COLOR_HOVER = new Color(TERTIARY_COLOR.getRed(), TERTIARY_COLOR.getGreen(), TERTIARY_COLOR.getBlue(), 200);
    public static final Color QUATERNARY_COLOR = new Color(122, 255, 0);
    public static final Color QUATERNARY_COLOR_HOVER = new Color(QUATERNARY_COLOR.getRed(), QUATERNARY_COLOR.getGreen(), QUATERNARY_COLOR.getBlue(), 200);

    public AppGUI() {
        customerService = new CustomerService();
        goodsReceiveNoteService = new GoodsReceiveNoteService();
        itemService = new ItemService();
        invoiceService = new InvoiceService(customerService, itemService);
        paymentService = new PaymentService(invoiceService);
        scaleLicenseService = new ScaleLicenseService();
        supplierService = new SupplierService();
        userService = new UserService();

        initialize();
    }

    public static JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppGUI::new);
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

        loginPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Business Management System");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

        emailField = new JTextField(20);
        emailField.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        emailField.setToolTipText("Enter your email address");
        emailField.setPreferredSize(new Dimension(300, 30));
        emailField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
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

        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        forgotPasswordButton.setForeground(PRIMARY_COLOR);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setBorder(BorderFactory.createEmptyBorder());
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel forgotPasswordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        forgotPasswordPanel.setBackground(BACKGROUND_COLOR);
        forgotPasswordPanel.add(forgotPasswordButton);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(BACKGROUND_COLOR);
        loginButton.setPreferredSize(new Dimension(150, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        clearButton.setBackground(SECONDARY_COLOR);
        clearButton.setForeground(BACKGROUND_COLOR);
        clearButton.setPreferredSize(new Dimension(150, 45));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new BorderLayout(10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(loginButton, BorderLayout.CENTER);
        buttonPanel.add(clearButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridy++;
        loginPanel.add(subtitleLabel, gbc);

        gbc.gridy++;
        loginPanel.add(progressBar, gbc);

        gbc.gridy++;
        loginPanel.add(statusLabel, gbc);

        gbc.gridy++;
        loginPanel.add(emailLabel, gbc);

        gbc.gridy++;
        loginPanel.add(emailField, gbc);

        gbc.gridy++;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        loginPanel.add(passwordPanel, gbc);

        gbc.gridy++;
        loginPanel.add(forgotPasswordPanel, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(buttonPanel, gbc);

        add(loginPanel, BorderLayout.CENTER);

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
                    clearStatus();
                    String email = emailField.getText().trim();
                    if (email.isEmpty()) {
                        setStatus("Please enter your email.", Color.RED);
                    } else if (!Validation.isValidEmail(email)) {
                        setStatus("Invalid email format.", Color.RED);
                    } else {
                        passwordField.requestFocus();
                    }
                }
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                passwordPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR_HOVER, 1));
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

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
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

        forgotPasswordButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                forgotPasswordButton.setForeground(PRIMARY_COLOR_HOVER);
            }

            public void mouseExited(MouseEvent evt) {
                forgotPasswordButton.setForeground(PRIMARY_COLOR);
            }
        });

        forgotPasswordButton.addActionListener(_ -> {
            clearStatus();
            String email = emailField.getText().trim();
            openForgotPassword(email);
        });

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(PRIMARY_COLOR_HOVER);
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(PRIMARY_COLOR);
            }
        });

        loginButton.addActionListener(_ -> handleLogin());

        clearButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                clearButton.setBackground(SECONDARY_COLOR_HOVER);
                }

            public void mouseExited(MouseEvent evt) {
                clearButton.setBackground(SECONDARY_COLOR);
            }
        });

        clearButton.addActionListener(_ -> {
            clearStatus();
            emailField.setText("");
            passwordField.setText("");
            emailField.requestFocus();
        });

        setVisible(true);
    }

    public void handleLogin() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            private String userType;
            @Override
            protected Boolean doInBackground() {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    setStatus("Please enter your email and password.", Color.RED);
                    return false;
                } else if (!Validation.isValidEmail(email)) {
                    setStatus("Invalid email format.", Color.RED);
                    return false;
                }

                try {
                    userType = userService.authenticateUser(email, password);
                    if (userType == null) {
                        setStatus("Invalid email or password.", Color.RED);
                        return false;
                    } else {
                        setStatus("Logging in...", Color.BLACK);
                        return true;
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "An error occurred during login", ex);
                    setStatus("An error occurred: " + ex.getMessage(), Color.RED);
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean loginSuccess = get();
                    if (loginSuccess) {
                        setStatus("Login successful.", Color.GREEN);
                        openMenu(userType);
                    }
                } catch (Exception ex) {
                    setStatus("An error occurred: " + ex.getMessage(), Color.RED);
                } finally {
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(false);
                }
            }
        };

        clearStatus();
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        worker.execute();
    }

    private void openMenu(String userType) {
        JFrame menuFrame = new JFrame("Menu");
        menuFrame.setSize(1000, 800);
        menuFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        menuFrame.setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("User Dashboard", new WorkerMenuPanel(menuFrame, customerService, goodsReceiveNoteService, invoiceService,
                itemService, paymentService, scaleLicenseService, supplierService, userService));
        if (userType.equalsIgnoreCase("admin")) {
            tabbedPane.addTab("Admin Dashboard", new AdminMenuPanel(menuFrame, invoiceService, itemService));
            tabbedPane.addTab("Reports and Analysis", new ReportAnalysisPanel(menuFrame, invoiceService, itemService));
        }
        tabbedPane.addTab("Items", new ItemPanel(menuFrame, itemService));
        tabbedPane.addTab("Suppliers", new SupplierPanel(menuFrame, supplierService));
        tabbedPane.addTab("Customers", new CustomerPanel(menuFrame, customerService));
        if (userType.equalsIgnoreCase("admin")) {
            tabbedPane.addTab("Users", new UserPanel(menuFrame, userService));
        }
        tabbedPane.addTab("Goods Receive Notes", new GoodsReceiveNotePanel(menuFrame, goodsReceiveNoteService, itemService, supplierService));
        tabbedPane.addTab("Invoices", new InvoicePanel(menuFrame, invoiceService, customerService, itemService, paymentService));
        tabbedPane.addTab("Payments", new PaymentPanel(menuFrame, paymentService, invoiceService));

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

    private void openForgotPassword(String email) {
        ForgotPasswordPanel forgotPasswordPanel = new ForgotPasswordPanel(loginPanel, userService, email);
        add(forgotPasswordPanel, BorderLayout.CENTER);

        forgotPasswordPanel.setVisible(true);
        loginPanel.setVisible(false);

        revalidate();
        repaint();
    }

    private void setStatus(String message, Color color) {
        statusLabel.setForeground(color);
        statusLabel.setText(message);
    }

    private void clearStatus() {
        statusLabel.setText(" ");
    }
}
