package GUI;

import services.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppGUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Services and other necessary dependencies
    private CustomerService customerService;
    private GoodsReceiveNoteService goodsReceiveNoteService;
    private InvoiceService invoiceService;
    private ItemService itemService;
    private ScaleLicenseService scaleLicenseService;
    private StockService stockService;
    private SupplierService supplierService;
    private UserService userService;

    public AppGUI() {
        // Initialize services
        customerService = new CustomerService();
        goodsReceiveNoteService = new GoodsReceiveNoteService();
        invoiceService = new InvoiceService();
        itemService = new ItemService();
        scaleLicenseService = new ScaleLicenseService();
        stockService = new StockService();
        supplierService = new SupplierService();
        userService = new UserService();

        // Set up JFrame
        setTitle("Inventory Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setLayout(new BorderLayout());

        // Components initialization
        JPanel loginPanel = new JPanel(new GridLayout(3, 1));
        emailField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        loginPanel.add(new JLabel("Enter Email:"));
        loginPanel.add(emailField);
        loginPanel.add(new JLabel("Enter Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.CENTER);

        // Action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                try {
                    String userType = userService.authenticateUser(email, password);

                    if (userType != null) {
                        JOptionPane.showMessageDialog(AppGUI.this, "Login successful.");
                        openMenu(userType);
                    } else {
                        JOptionPane.showMessageDialog(AppGUI.this, "Invalid username or password. Please try again.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AppGUI.this, "An error occurred: " + ex.getMessage());
                }
            }
        });

        setVisible(true);
    }

    private void openMenu(String userType) {
        JFrame menuFrame = new JFrame("Menu");
        menuFrame.setSize(800, 600);
        menuFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        menuFrame.setLocationRelativeTo(null); // Center window

        // Create appropriate menu based on userType
        if (userType.equalsIgnoreCase("admin")) {
            AdminMenuPanel adminMenuPanel = new AdminMenuPanel(menuFrame, customerService, goodsReceiveNoteService, invoiceService,
                    itemService, scaleLicenseService, stockService, supplierService, userService);
            menuFrame.setContentPane(adminMenuPanel);
        } else if (userType.equalsIgnoreCase("worker")) {
            WorkerMenuPanel workerMenuPanel = new WorkerMenuPanel(menuFrame, customerService, goodsReceiveNoteService, invoiceService,
                    itemService, scaleLicenseService, stockService, supplierService, userService);
            menuFrame.setContentPane(workerMenuPanel);
        }

        menuFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppGUI();
            }
        });
    }
}
