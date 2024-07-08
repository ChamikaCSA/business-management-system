package GUI;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import entities.*;
import services.*;
import utils.IDGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AdminMenuPanel extends JPanel {
    private final CustomerService customerService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final ScaleLicenseService scaleLicenseService;
    private final SupplierService supplierService;
    private final UserService userService;

    private static final Logger LOGGER = Logger.getLogger(AdminMenuPanel.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public AdminMenuPanel(JFrame menuFrame, CustomerService customerService, GoodsReceiveNoteService goodsReceiveNoteService,
                          InvoiceService invoiceService, ItemService itemService, ScaleLicenseService scaleLicenseService,
                          SupplierService supplierService, UserService userService) {
        this.customerService = customerService;
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.invoiceService = invoiceService;
        this.itemService = itemService;
        this.scaleLicenseService = scaleLicenseService;
        this.supplierService = supplierService;
        this.userService = userService;

        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            LOGGER.warning("Failed to apply FlatLaf Look and Feel: " + e.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        gbc.ipady = 20;

        JLabel inventoryLabel = new JLabel("Inventory Management");
        inventoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(inventoryLabel, createGBC(0, 0, 2));

        addButton(mainPanel, gbc, 0, 1, "Register Item", "icon.png", "register a new item in the inventory.", this::registerItem);
        addButton(mainPanel, gbc, 1, 1, "Register Supplier", "icon.png", "add a new supplier to the system.", this::registerSupplier);
        addButton(mainPanel, gbc, 2, 1, "Register Customer", "icon.png", "register a new customer.", this::registerCustomer);
        addButton(mainPanel, gbc, 0, 2, "Create Invoice", "icon.png", "create a new invoice for a customer.", this::createInvoice);
        addButton(mainPanel, gbc, 1, 2, "Stock Maintenance", "icon.png", "manage stock levels", this::stockMaintenance);
        addButton(mainPanel, gbc, 2, 2, "Goods Receive Notes", "icon.png", "record goods received from suppliers.", this::goodsReceiveNotes);

        JLabel reportsLabel = new JLabel("Reports and Analysis");
        reportsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(reportsLabel, createGBC(0, 3, 2));

        addButton(mainPanel, gbc, 0, 4, "View Income Details", "icon.png", "view income details within a specified date range.", this::viewIncomeDetails);
        addButton(mainPanel, gbc, 1, 4, "Renew Scale License", "icon.png", "renew scale license for weighing equipment.", this::renewScaleLicense);
        addButton(mainPanel, gbc, 2, 4, "Send Email", "icon.png", "send email notifications to customers or suppliers.", this::sendEmail);
        addButton(mainPanel, gbc, 0, 5, "View Reports", "icon.png", "view various reports such as sales or stock reports.", this::viewReports);
        addButton(mainPanel, gbc, 1, 5, "Next Month Sales Forecast", "icon.png", "forecast sales for the next month.", this::nextMonthSalesForecast);
        addButton(mainPanel, gbc, 2, 5, "Income and Sales Analysis", "icon.png", "generate income and sales analysis report.", this::incomeAndSalesAnalysis);

        JLabel userManagementLabel = new JLabel("User Management");
        userManagementLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(userManagementLabel, createGBC(0, 6, 2));

        addButton(mainPanel, gbc, 0, 7, "Add User", "icon.png", "add a new user to the system.", this::addUser);
        addButton(mainPanel, gbc, 1, 7, "Update User", "icon.png", "update user information.", this::updateUser);
        addButton(mainPanel, gbc, 2, 7, "Delete User", "icon.png", "delete a user from the system.", this::deleteUser);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, int x, int y, String text, String iconPath, String toolTip, Runnable action) {
        gbc.gridx = x;
        gbc.gridy = y;

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setIcon(new ImageIcon(STR."/icons/\{iconPath}"));
        button.setToolTipText(STR."Click to \{toolTip}");
        button.addActionListener(e -> action.run());
        button.setPreferredSize(new Dimension(200, 100));

        button.setBackground(new Color(34, 140, 240));
        button.setForeground(new Color(245, 245, 245));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(77, 163, 228));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(34, 140, 240));
            }
        });

        panel.add(button, gbc);
    }

    private GridBagConstraints createGBC(int x, int y, int gridWidth) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gridWidth;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        gbc.ipady = 20;
        return gbc;
    }

    private void registerItem() {
        int itemCount = itemService.getItemRegistry().size() + 1;
        String itemId = IDGenerator.generateId("ITEM", itemCount);
        String itemName = JOptionPane.showInputDialog(this, "Enter item name:", "Register Item", JOptionPane.PLAIN_MESSAGE);
        if (itemName != null && !itemName.isEmpty()) {
            try {
                double itemPrice = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter item price:", "Register Item", JOptionPane.PLAIN_MESSAGE));
                int itemQuantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter item quantity:", "Register Item", JOptionPane.PLAIN_MESSAGE));

                Item newItem = new Item(itemId, itemName, itemPrice, itemQuantity);
                itemService.registerItem(newItem);
                JOptionPane.showMessageDialog(this, STR."Item registered successfully: \{itemName}", "Register Item", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for price or quantity. Please enter valid numbers.", "Register Item", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Item name cannot be empty.", "Register Item", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerSupplier() {
        int supplierCount = supplierService.getSupplierRegistry().size() + 1;
        String supplierId = IDGenerator.generateId("SUP", supplierCount);
        String supplierName = JOptionPane.showInputDialog(this, "Enter supplier name:");
        if (supplierName != null && !supplierName.isEmpty()) {
            String supplierEmail = JOptionPane.showInputDialog(this, "Enter supplier email:");
            if (supplierEmail != null && !supplierEmail.isEmpty()) {
                if (isInvalidEmail(supplierEmail)) {
                    JOptionPane.showMessageDialog(this, "Invalid email format. Please enter a valid email address.");
                    return;
                }
                Supplier newSupplier = new Supplier(supplierId, supplierName, supplierEmail);
                supplierService.registerSupplier(newSupplier);
                JOptionPane.showMessageDialog(this, STR."Supplier registered successfully: \{supplierName}");
            } else {
                JOptionPane.showMessageDialog(this, "Supplier email cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Supplier name cannot be empty.");
        }
    }

    private void registerCustomer() {
        int customerCount = customerService.getCustomerRegistry().size() + 1;
        String customerId = IDGenerator.generateId("CUST", customerCount);
        String customerName = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (customerName != null && !customerName.isEmpty()) {
            String customerEmail = JOptionPane.showInputDialog(this, "Enter customer email:");
            if (customerEmail != null && !customerEmail.isEmpty()) {
                if (isInvalidEmail(customerEmail)) {
                    JOptionPane.showMessageDialog(this, "Invalid email format. Please enter a valid email address.");
                    return;
                }
                Customer newCustomer = new Customer(customerId, customerName, customerEmail);
                customerService.registerCustomer(newCustomer);
                JOptionPane.showMessageDialog(this, STR."Customer registered successfully: \{customerName}");
            } else {
                JOptionPane.showMessageDialog(this, "Customer email cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Customer name cannot be empty.");
        }
    }

    private void createInvoice() {
        try {
            int invoiceCount = invoiceService.getInvoiceRegistry().size() + 1;
            String invoiceId = IDGenerator.generateDatedId("INV", invoiceCount);
            Customer selectedCustomer = (Customer) JOptionPane.showInputDialog(this,
                    "Select customer:",
                    "Create Invoice",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    customerService.getCustomerRegistry().values().toArray(),
                    null);

            if (selectedCustomer != null) {
                List<Item> selectedItems = new ArrayList<>();
                Item[] items = itemService.getItemRegistry().values().toArray(new Item[0]);
                while (true) {
                    Item selectedItem = (Item) JOptionPane.showInputDialog(this,
                            "Select item:",
                            "Create Invoice",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            items,
                            null);
                    if (selectedItem != null) {
                        int maxQuantity = selectedItem.getQuantity();
                        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity:"));
                        if (quantity > 0 && quantity <= maxQuantity) {
                            selectedItem.setQuantity(quantity);
                            selectedItems.add(selectedItem);
                        } else if (quantity <= 0) {
                            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                        } else {
                            JOptionPane.showMessageDialog(this, STR."Quantity cannot exceed available stock (\{maxQuantity}).");
                        }
                        int addMore = JOptionPane.showConfirmDialog(this,
                                "Do you want to add more items to the invoice?",
                                "Add More Items",
                                JOptionPane.YES_NO_OPTION);
                        if (addMore == JOptionPane.NO_OPTION) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!selectedItems.isEmpty()) {
                    double totalAmount = calculateTotalAmount(selectedItems);
                    Date currentDate = new Date();
                    Invoice newInvoice = new Invoice(invoiceId, selectedCustomer, selectedItems, currentDate, totalAmount);
                    invoiceService.registerInvoice(newInvoice);
                    JOptionPane.showMessageDialog(this, STR."Invoice created successfully for customer: \{selectedCustomer.getName()}");
                } else {
                    JOptionPane.showMessageDialog(this, "No items selected for invoice creation.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No customer selected.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity input. Please enter a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, STR."Error creating invoice: \{e.getMessage()}");
        }
    }


    private double calculateTotalAmount(List<Item> items) {
        double total = 0.0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    private void stockMaintenance() {
        Item selectedStockItem = (Item) JOptionPane.showInputDialog(this,
                "Select item to update stock quantity:",
                "Stock Maintenance",
                JOptionPane.PLAIN_MESSAGE,
                null,
                itemService.getItemRegistry().values().toArray(),
                null);

        if (selectedStockItem != null) {
            try {
                int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity to add to stock:"));
                if (quantity > 0) {
                    selectedStockItem.setQuantity(selectedStockItem.getQuantity() + quantity);
                    itemService.updateItem(selectedStockItem);
                    JOptionPane.showMessageDialog(this, "Stock quantity updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for quantity. Please enter a valid number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No item selected.");
        }
    }


    private void goodsReceiveNotes() {
        int grnCount = goodsReceiveNoteService.getGoodsReceiveNoteRegistry().size() + 1;
        String grnId = IDGenerator.generateDatedId("GRN", grnCount);
        Supplier selectedSupplier = (Supplier) JOptionPane.showInputDialog(this,
                "Select supplier:",
                "Goods Receive Notes",
                JOptionPane.PLAIN_MESSAGE,
                null,
                supplierService.getSupplierRegistry().values().toArray(),
                null);

        if (selectedSupplier != null) {
            Item selectedGRNItem = (Item) JOptionPane.showInputDialog(this,
                    "Select item:",
                    "Goods Receive Notes",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    itemService.getItemRegistry().values().toArray(),
                    null);

            if (selectedGRNItem != null) {
                try {
                    int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity received:"));
                    if (quantity > 0) {
                        GoodsReceiveNote newGRN = new GoodsReceiveNote(grnId, selectedSupplier, selectedGRNItem, new Date(), quantity);
                        goodsReceiveNoteService.registerGoodsReceiveNote(newGRN);
                        selectedGRNItem.setQuantity(selectedGRNItem.getQuantity() + quantity);
                        itemService.updateItem(selectedGRNItem);
                        JOptionPane.showMessageDialog(this, "Goods receive note created successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input for quantity. Please enter a valid number.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No item selected.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
        }
    }

    private void viewIncomeDetails() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(JOptionPane.showInputDialog(this, "Enter start date (yyyy-MM-dd):"));
            Date endDate = dateFormat.parse(JOptionPane.showInputDialog(this, "Enter end date (yyyy-MM-dd):"));

            double totalIncome = invoiceService.calculateTotalIncome(startDate, endDate);
            JOptionPane.showMessageDialog(this, String.format("Total Income from %s to %s is: $%.2f", dateFormat.format(startDate), dateFormat.format(endDate), totalIncome));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, STR."Error calculating income: \{e.getMessage()}");
        }
    }


    private void renewScaleLicense() {
        int licenseCount = scaleLicenseService.getLicenseRegistry().size() + 1;
        String licenseId = IDGenerator.generateDatedId("SL", licenseCount);
        ScaleLicense[] licenses = scaleLicenseService.getLicenseRegistry().values().toArray(new ScaleLicense[0]);
        ScaleLicense selectedLicense = (ScaleLicense) JOptionPane.showInputDialog(this,
                "Select license to renew:",
                "Renew Scale License",
                JOptionPane.PLAIN_MESSAGE,
                null,
                licenses,
                null);
        if (selectedLicense != null) {
            selectedLicense = new ScaleLicense(selectedLicense.getId(), selectedLicense.getExpirationDate(), selectedLicense.getScaleType());
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date newExpirationDate = dateFormat.parse(JOptionPane.showInputDialog(this, "Enter new expiration date (yyyy-MM-dd):"));
                selectedLicense.setId(licenseId);
                selectedLicense.setExpirationDate(newExpirationDate);
                scaleLicenseService.renewLicense(selectedLicense);
                JOptionPane.showMessageDialog(this, "License renewed successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, STR."Error renewing license: \{e.getMessage()}");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No license selected.");
        }
    }

    private void sendEmail() {
        String emailType = (String) JOptionPane.showInputDialog(this,
                "Select email type:",
                "Send Email",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Customer", "Supplier"},
                null);

        if (emailType != null) {
            if ("Customer".equals(emailType)) {
                sendCustomerEmail();
            } else if ("Supplier".equals(emailType)) {
                sendSupplierEmail();
            }
        }
    }

    private void sendCustomerEmail() {
        Customer selectedCustomer = (Customer) JOptionPane.showInputDialog(this,
                "Select customer to send email:",
                "Send Customer Email",
                JOptionPane.PLAIN_MESSAGE,
                null,
                customerService.getCustomerRegistry().values().toArray(),
                null);

        if (selectedCustomer != null) {
            String emailSubject = JOptionPane.showInputDialog(this, "Enter email subject:");
            if (emailSubject != null && !emailSubject.isEmpty()) {
                String emailContent = JOptionPane.showInputDialog(this, "Enter email content:");
                if (emailContent != null && !emailContent.isEmpty()) {
                    try {
                        customerService.sendEmail(selectedCustomer, emailSubject, emailContent);
                        JOptionPane.showMessageDialog(this, "Email sent successfully to customer: " + selectedCustomer.getName());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, STR."Error sending email: \{e.getMessage()}");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Email content cannot be empty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email subject cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No customer selected.");
        }
    }

    private void sendSupplierEmail() {
        Supplier selectedSupplier = (Supplier) JOptionPane.showInputDialog(this,
                "Select supplier to send email:",
                "Send Supplier Email",
                JOptionPane.PLAIN_MESSAGE,
                null,
                supplierService.getSupplierRegistry().values().toArray(),
                null);

        if (selectedSupplier != null) {
            String emailSubject = JOptionPane.showInputDialog(this, "Enter email subject:");
            if (emailSubject != null && !emailSubject.isEmpty()) {
                String emailContent = JOptionPane.showInputDialog(this, "Enter email content:");
                if (emailContent != null && !emailContent.isEmpty()) {
                    try {
                        supplierService.sendEmail(selectedSupplier, emailSubject, emailContent);
                        JOptionPane.showMessageDialog(this, "Email sent successfully to supplier: " + selectedSupplier.getName());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, STR."Error sending email: \{e.getMessage()}");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Email content cannot be empty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email subject cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
        }
    }

    private void viewReports() {
        String reportType = (String) JOptionPane.showInputDialog(this,
                "Select report type:",
                "View Reports",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Sales Report", "Stock Report"},
                "Sales Report");

        if (reportType != null) {
            if ("Sales Report".equals(reportType)) {
                generateSalesReport();
            } else if ("Stock Report".equals(reportType)) {
                generateStockReport();
            }
        }
    }

    private void generateSalesReport() {
        String report = invoiceService.generateMonthlySalesReport();
        JOptionPane.showMessageDialog(this, report);
    }

    private void generateStockReport() {
        String report = itemService.generateStockLevelReport();
        JOptionPane.showMessageDialog(this, report);
    }

    private void nextMonthSalesForecast() {
        try {
            double forecast = invoiceService.forecastNextMonthSales();
            JOptionPane.showMessageDialog(this, String.format("Next month's sales forecast is: $%.2f", forecast));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, STR."Error forecasting sales: \{e.getMessage()}");
        }
    }

    private void incomeAndSalesAnalysis() {
        try {
            String report = invoiceService.generateIncomeAndSalesAnalysis();
            JOptionPane.showMessageDialog(this, report);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, STR."Error generating income and sales analysis: \{e.getMessage()}");
        }
    }

    private void addUser() {
        int userCount = userService.getUserRegistry().size() + 1;
        String userId = IDGenerator.generateId("USER", userCount);
        String userType = (String) JOptionPane.showInputDialog(this,
                "Select user type:",
                "Add User",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Admin", "Worker"},
                null);
        String userName = JOptionPane.showInputDialog(this, "Enter user name:");
        if (userName != null && !userName.isEmpty()) {
            try {
                String userEmail = JOptionPane.showInputDialog(this, "Enter user email:");
                if (userEmail != null && !userEmail.isEmpty()) {
                    if (isInvalidEmail(userEmail)) {
                        JOptionPane.showMessageDialog(this, "Invalid email format. Please enter a valid email address.");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User email cannot be empty.");
                    return;
                }
                String userPassword = JOptionPane.showInputDialog(this, "Enter user password:");
                User newUser = new User(userId, userType, userName, userEmail, userPassword);
                userService.addUser(newUser);
                JOptionPane.showMessageDialog(this, STR."User added successfully: \{userName}");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, STR."Error adding user: \{e.getMessage()}");
            }
        } else {
            JOptionPane.showMessageDialog(this, "User name cannot be empty.");
        }
    }

    private void updateUser() {
        User[] users = userService.getUserRegistry().values().toArray(new User[0]);
        if (users.length > 0) {
            User selectedUser = (User) JOptionPane.showInputDialog(this,
                    "Select user to update:",
                    "Update User",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    users,
                    null);

            if (selectedUser != null) {
                try {
                    String newUserName = JOptionPane.showInputDialog(this, "Enter new name for user:");
                    if (newUserName != null && !newUserName.isEmpty()) {
                        selectedUser.setName(newUserName);
                        userService.updateUser(selectedUser);
                        JOptionPane.showMessageDialog(this, "User updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "New user name cannot be empty.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, STR."Error updating user: \{e.getMessage()}");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No user selected.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No users available.");
        }
    }

    private void deleteUser() {
        User[] users = userService.getUserRegistry().values().toArray(new User[0]);
        if (users.length > 0) {
            User selectedUser = (User) JOptionPane.showInputDialog(this,
                    "Select user to delete:",
                    "Delete User",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    users,
                    null);

            if (selectedUser != null) {
                int confirmDelete = JOptionPane.showConfirmDialog(this,
                        STR."Are you sure you want to delete user: \{selectedUser.getName()}?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirmDelete == JOptionPane.YES_OPTION) {
                    userService.deleteUser(selectedUser.getId());
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No user selected.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No users available.");
        }
    }

    private boolean isInvalidEmail(String email) {
        return !EMAIL_PATTERN.matcher(email).matches();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);
            frame.add(new AdminMenuPanel(frame, new CustomerService(), new GoodsReceiveNoteService(),
                    new InvoiceService(), new ItemService(), new ScaleLicenseService(), new SupplierService(), new UserService()));
            frame.setVisible(true);
        });
    }
}
