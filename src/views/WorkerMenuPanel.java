package views;

import models.*;
import controllers.*;
import utils.EmailSender;
import utils.Generator;

import static views.AppGUI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class WorkerMenuPanel extends JPanel {
    private final CustomerService customerService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final ScaleLicenseService scaleLicenseService;
    private final SupplierService supplierService;
    private final UserService userService;

    private final JFrame menuFrame;

    private static final Logger LOGGER = Logger.getLogger(AdminMenuPanel.class.getName());

    public WorkerMenuPanel(JFrame menuFrame, CustomerService customerService, GoodsReceiveNoteService goodsReceiveNoteService,
                          InvoiceService invoiceService, ItemService itemService, PaymentService paymentService, ScaleLicenseService scaleLicenseService,
                          SupplierService supplierService, UserService userService) {
        this.menuFrame = menuFrame;
        this.customerService = customerService;
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.invoiceService = invoiceService;
        this.itemService = itemService;
        this.paymentService = paymentService;
        this.scaleLicenseService = scaleLicenseService;
        this.supplierService = supplierService;
        this.userService = userService;

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("User Dashboard");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(TEXT_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addSection(mainPanel, gbc, "Inventory Management", 0);
        addButton(mainPanel, gbc, 0, 1, "Manage Items", "icon.png", "view and manage items in the inventory.", this::viewItems);
        addButton(mainPanel, gbc, 1, 1, "Maintain Stock", "icon.png", "maintain stock levels for items.", this::maintainStock);
        addButton(mainPanel, gbc, 2, 1, "Create Goods Receive Note", "icon.png", "create a new goods receive note for a supplier.", this::createGoodsReceiveNote);

        addSection(mainPanel, gbc, "Finance Management", 2);
        addButton(mainPanel, gbc, 0, 3, "Create Invoice", "icon.png", "create a new invoice for a customer.", this::createInvoice);
        addButton(mainPanel, gbc, 1, 3, "Manage Payments", "icon.png", "view and manage payments.", this::managePayments);
        addButton(mainPanel, gbc, 2, 3, "View Income Details", "icon.png", "view income details within a specified date range.", this::viewIncomeDetails);

        addSection(mainPanel, gbc, "Supplier and Customer Management", 4);
        addButton(mainPanel, gbc, 0, 5, "Manage Suppliers", "icon.png", "view and manage suppliers.", this::viewSuppliers);
        addButton(mainPanel, gbc, 1, 5, "Manage Customers", "icon.png", "view and manage customers.", this::viewCustomers);

        addSection(mainPanel, gbc, "Operations Management", 6);
        addButton(mainPanel, gbc, 0, 7, "Renew Scale License", "icon.png", "renew scale license for weighing equipment.", this::renewScaleLicense);
        addButton(mainPanel, gbc, 1, 7, "Send Email", "icon.png", "send email notifications to customers or suppliers.", this::sendEmail);

        add(titleLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addSection(JPanel panel, GridBagConstraints gbc, String title, int y) {
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        sectionLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(sectionLabel, gbc);
        gbc.gridwidth = 1;
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, int x, int y, String text, String iconPath, String toolTip, Runnable action) {
        gbc.gridx = x;
        gbc.gridy = y;

        JButton button = new JButton(text);
        button.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        button.setIcon(new ImageIcon("/icons/" + iconPath));
        button.setToolTipText("Click to" + toolTip);
        button.addActionListener(_ -> action.run());
        button.setPreferredSize(new Dimension(270, 70));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(BACKGROUND_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        panel.add(button, gbc);
    }

    private void viewItems() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Items"));
    }

    private void maintainStock() {
        JComboBox<Item> itemComboBox = new JComboBox<>(itemService.getItemRegistry().values().toArray(new Item[0]));
        itemComboBox.setSelectedIndex(-1);

        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerNumberModel);
        quantitySpinner.setEnabled(false);

        itemComboBox.addActionListener(_ -> {
            Item selectedItem = (Item) itemComboBox.getSelectedItem();
            if (selectedItem != null) {
                quantitySpinner.setValue(selectedItem.getQuantity());
                quantitySpinner.setEnabled(true);
            } else {
                quantitySpinner.setValue(0);
                quantitySpinner.setEnabled(false);
            }
        });

        while (true) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Select Item:"));
            panel.add(itemComboBox);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantitySpinner);

            int result = JOptionPane.showConfirmDialog(null, panel, "Maintain Stock",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            Item selectedItem = (Item) itemComboBox.getSelectedItem();
            int quantity = (int) quantitySpinner.getValue();

            if (selectedItem == null) {
                JOptionPane.showMessageDialog(null, "No item selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (quantity == 0) {
                JOptionPane.showMessageDialog(null, "Quantity cannot be zero.", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            selectedItem.setQuantity(quantity);
            itemService.updateItem(selectedItem);
            JOptionPane.showMessageDialog(null, "Stock updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void createGoodsReceiveNote() {
        GoodsReceiveNoteDialog goodsReceiveNoteDialog = new GoodsReceiveNoteDialog(menuFrame, "Create Goods Receive Note", goodsReceiveNoteService, itemService, supplierService);
        goodsReceiveNoteDialog.setVisible(true);
    }

    private void createInvoice() {
        InvoiceDialog invoiceDialog = new InvoiceDialog(menuFrame, "Create Invoice", invoiceService, customerService, itemService, paymentService, scaleLicenseService);
        invoiceDialog.setVisible(true);
    }

    public void managePayments() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Payments"));
    }

    private void viewIncomeDetails() {
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        endDateSpinner.setEditor(endDateEditor);

        while (true) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Start Date:"));
            panel.add(startDateSpinner);
            panel.add(new JLabel("End Date:"));
            panel.add(endDateSpinner);

            int result = JOptionPane.showConfirmDialog(null, panel, "View Income Details",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();

            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(null, "Start date cannot be after end date.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            double totalIncome = invoiceService.calculateTotalIncome(startDate, endDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            JOptionPane.showMessageDialog(null, "Total income between " + startDateStr + " and " + endDateStr + " : $" + totalIncome, "Income Details", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void viewSuppliers() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Suppliers"));
    }

    private void viewCustomers() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Customers"));
    }

    private void renewScaleLicense() {
        JComboBox<ScaleLicense> licenseComboBox = new JComboBox<>(scaleLicenseService.getScaleLicenseRegistry().values().toArray(new ScaleLicense[0]));
        licenseComboBox.setSelectedIndex(-1);

        JSpinner expiryDateField = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor expiryDateEditor = new JSpinner.DateEditor(expiryDateField, "yyyy-MM-dd");
        expiryDateField.setEditor(expiryDateEditor);

        while (true) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Select License:"));
            panel.add(licenseComboBox);
            panel.add(new JLabel("Expiry Date:"));
            panel.add(expiryDateField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Renew Scale License",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            ScaleLicense selectedLicense = (ScaleLicense) licenseComboBox.getSelectedItem();
            Date expiryDate = (Date) expiryDateField.getValue();

            if (selectedLicense == null) {
                JOptionPane.showMessageDialog(null, "No license selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (expiryDate.before(new Date())) {
                JOptionPane.showMessageDialog(null, "Expiry date cannot be in the past.", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String licenseId = Generator.generateDatedId("SL", scaleLicenseService.getScaleLicenseCount() + 1);

            ScaleLicense renewedLicense = new ScaleLicense(licenseId, selectedLicense.getLicenseType(), new Date(), expiryDate, selectedLicense.getCustomer(), "active");
            scaleLicenseService.renewScaleLicense(selectedLicense, renewedLicense);

            System.out.println(selectedLicense);

            JOptionPane.showMessageDialog(null, "License renewed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void sendEmail() {
        JComboBox<String> recipientTypeComboBox = new JComboBox<>(new String[]{"Customer", "Supplier", "User"});
        recipientTypeComboBox.setSelectedIndex(-1);

        JComboBox<Customer> customerComboBox = new JComboBox<>(customerService.getCustomerRegistry().values().toArray(new Customer[0]));
        customerComboBox.setSelectedIndex(-1);

        JComboBox<Supplier> supplierComboBox = new JComboBox<>(supplierService.getSupplierRegistry().values().toArray(new Supplier[0]));
        supplierComboBox.setSelectedIndex(-1);

        JComboBox<User> userComboBox = new JComboBox<>(userService.getUserRegistry().values().toArray(new User[0]));
        userComboBox.setSelectedIndex(-1);

        JTextField emailSubjectField = new JTextField();
        JTextArea emailContentArea = new JTextArea(10, 30);

        JTextField attachmentField = new JTextField();
        JButton attachmentButton = new JButton("Select Attachment");

        JPanel attachmentPanel = new JPanel();
        attachmentPanel.setLayout(new BoxLayout(attachmentPanel, BoxLayout.X_AXIS));
        attachmentPanel.add(attachmentField);
        attachmentPanel.add(attachmentButton);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select Recipient Type:"));
        panel.add(recipientTypeComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Send Email",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String recipientType = (String) recipientTypeComboBox.getSelectedItem();

        while (true) {
            JPanel emailPanel = new JPanel();
            emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));

            if ("Customer".equals(recipientType)) {
                emailPanel.add(createLeftAlignedLabelPanel("Select Customer:"));
                emailPanel.add(customerComboBox);
            } else if ("Supplier".equals(recipientType)) {
                emailPanel.add(createLeftAlignedLabelPanel("Select Supplier:"));
                emailPanel.add(supplierComboBox);
            } else if ("User".equals(recipientType)) {
                emailPanel.add(createLeftAlignedLabelPanel("Select User:"));
                emailPanel.add(userComboBox);
            }

            emailPanel.add(createLeftAlignedLabelPanel("Email Subject:"));
            emailPanel.add(emailSubjectField);
            emailPanel.add(createLeftAlignedLabelPanel("Email Content:"));
            emailPanel.add(new JScrollPane(emailContentArea));
            emailPanel.add(createLeftAlignedLabelPanel("Attachment:"));
            emailPanel.add(attachmentPanel);

            attachmentButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    attachmentField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });

            int emailResult = JOptionPane.showConfirmDialog(null, emailPanel, "Send Email",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (emailResult != JOptionPane.OK_OPTION) {
                return;
            }

            if ("Customer".equals(recipientType)) {
                if (customerComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "No customer selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            } else if ("Supplier".equals(recipientType)) {
                if (supplierComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "No supplier selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            }   else if ("User".equals(recipientType)) {
                if (userComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "No user selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            }

            String emailSubject = emailSubjectField.getText();
            String emailContent = emailContentArea.getText();
            String attachmentPath = attachmentField.getText();

            if (emailSubject.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email subject cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (emailContent.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email content cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String recipientEmail = "";
            if ("Customer".equals(recipientType)) {
                Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
                recipientEmail = selectedCustomer.getEmail();
            } else if ("Supplier".equals(recipientType)) {
                Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
                recipientEmail = selectedSupplier.getEmail();
            } else if ("User".equals(recipientType)) {
                User selectedUser = (User) userComboBox.getSelectedItem();
                recipientEmail = selectedUser.getEmail();
            }

            EmailSender.sendEmail(recipientEmail, emailSubject, emailContent, attachmentPath, getParent());
            break;
        }
    }

    private JPanel createLeftAlignedLabelPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        return panel;
    }
}
