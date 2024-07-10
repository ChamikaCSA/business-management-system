package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import entities.*;
import services.*;
import utils.EmailSender;
import utils.IDGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;
import java.util.logging.Logger;

public class WorkerMenuPanel extends JPanel {
    private final CustomerService customerService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final ScaleLicenseService scaleLicenseService;
    private final SupplierService supplierService;

    private final JFrame menuFrame;

    private static final Logger LOGGER = Logger.getLogger(AdminMenuPanel.class.getName());

    public WorkerMenuPanel(JFrame menuFrame, CustomerService customerService, GoodsReceiveNoteService goodsReceiveNoteService,
                          InvoiceService invoiceService, ItemService itemService, ScaleLicenseService scaleLicenseService,
                          SupplierService supplierService) {
        this.menuFrame = menuFrame;
        this.customerService = customerService;
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.invoiceService = invoiceService;
        this.itemService = itemService;
        this.scaleLicenseService = scaleLicenseService;
        this.supplierService = supplierService;

        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            LOGGER.warning(STR."Failed to apply FlatLaf Look and Feel: \{e.getMessage()}");
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("User Dashboard");
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
        mainPanel.add(inventoryLabel, createGBC(0));

        addButton(mainPanel, gbc, 0, 1, "Manage Items", "icon.png", "view and manage items in the inventory.", this::viewItems);
        addButton(mainPanel, gbc, 1, 1, "Create Goods Receive Notes", "icon.png", "create a new goods receive note for a supplier.", this::createGoodsReceiveNote);

        JLabel FinanceLabel = new JLabel("Finance Management");
        FinanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(FinanceLabel, createGBC(2));

        addButton(mainPanel, gbc, 0, 3, "Create Invoice", "icon.png", "create a new invoice for a customer.", this::createInvoice);
        addButton(mainPanel, gbc, 1, 3, "Manage Payments", "icon.png", "manage payments for an invoice.", this::managePayments);
        addButton(mainPanel, gbc, 2, 3, "View Income Details", "icon.png", "view income details within a specified date range.", this::viewIncomeDetails);

        JLabel SupplierCustomerLabel = new JLabel("Supplier and Customer Management");
        SupplierCustomerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(SupplierCustomerLabel, createGBC(4));

        addButton(mainPanel, gbc, 0, 5, "Manage Suppliers", "icon.png", "view and manage suppliers.", this::viewSuppliers);
        addButton(mainPanel, gbc, 1, 5, "Manage Customers", "icon.png", "view and manage customers.", this::viewCustomers);

        JLabel operationsLabel = new JLabel("Operations Management");
        operationsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(operationsLabel, createGBC(6));

        addButton(mainPanel, gbc, 0, 7, "Renew Scale License", "icon.png", "renew scale license for weighing equipment.", this::renewScaleLicense);
        addButton(mainPanel, gbc, 1, 7, "Send Email", "icon.png", "send email notifications to customers or suppliers.", this::sendEmail);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, int x, int y, String text, String iconPath, String toolTip, Runnable action) {
        gbc.gridx = x;
        gbc.gridy = y;

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setIcon(new ImageIcon(STR."/icons/\{iconPath}"));
        button.setToolTipText(STR."Click to \{toolTip}");
        button.addActionListener(_ -> action.run());
        button.setPreferredSize(new Dimension(240, 40));

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

    private GridBagConstraints createGBC(int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        gbc.ipadx = 20;
        gbc.ipady = 20;
        return gbc;
    }

    private void viewItems() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Items"));
    }

    private void createGoodsReceiveNote() {
        GoodsReceiveNoteDialog goodsReceiveNoteDialog = new GoodsReceiveNoteDialog(menuFrame, "Create Goods Receive Note", goodsReceiveNoteService, itemService, supplierService);
        goodsReceiveNoteDialog.setVisible(true);
    }

    private void createInvoice() {
        InvoiceDialog invoiceDialog = new InvoiceDialog(menuFrame, "Create Invoice", invoiceService, customerService, itemService);
        invoiceDialog.setVisible(true);
    }

    public void managePayments() {
        JComboBox<Invoice> invoiceComboBox = new JComboBox<>(invoiceService.getInvoiceRegistry().values().toArray(new Invoice[0]));
        JTextField paymentAmountField = new JTextField();

        while (true) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Select Invoice:"));
            panel.add(invoiceComboBox);
            panel.add(new JLabel("Payment Amount:"));
            panel.add(paymentAmountField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Manage Payments",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            Invoice selectedInvoice = (Invoice) invoiceComboBox.getSelectedItem();
            String paymentAmountStr = paymentAmountField.getText().trim();

            if (selectedInvoice == null) {
                JOptionPane.showMessageDialog(null, "No invoice selected.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (paymentAmountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Payment amount cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try {
                double paymentAmount = Double.parseDouble(paymentAmountStr);

                if (paymentAmount <= 0) {
                    JOptionPane.showMessageDialog(null, "Payment amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                if (paymentAmount > selectedInvoice.getTotalAmount()) {
                    JOptionPane.showMessageDialog(null, "Payment amount cannot exceed total amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                double remainingAmount = selectedInvoice.getTotalAmount() - paymentAmount;
                selectedInvoice.setTotalAmount(remainingAmount);
                invoiceService.updateInvoice(selectedInvoice);

                JOptionPane.showMessageDialog(null, STR."Payment recorded successfully for invoice: \{selectedInvoice.getId()}", "Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input for payment amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            JOptionPane.showMessageDialog(null, STR."Total income within specified date range: $\{totalIncome}", "Income Details", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "No license selected.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (expiryDate.before(new Date())) {
                JOptionPane.showMessageDialog(null, "Expiry date cannot be in the past.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            int licenseCount = scaleLicenseService.getScaleLicenseRegistry().size() + 1;
            String licenseId = IDGenerator.generateDatedId("SL", licenseCount);

            selectedLicense.setId(licenseId);
            selectedLicense.setExpirationDate(expiryDate);
            scaleLicenseService.renewScaleLicense(selectedLicense);

            System.out.println(selectedLicense);

            JOptionPane.showMessageDialog(null, "License renewed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void sendEmail() {
        JComboBox<String> recipientTypeComboBox = new JComboBox<>(new String[]{"Customer", "Supplier"});
        JComboBox<Customer> customerComboBox = new JComboBox<>(customerService.getCustomerRegistry().values().toArray(new Customer[0]));
        JComboBox<Supplier> supplierComboBox = new JComboBox<>(supplierService.getSupplierRegistry().values().toArray(new Supplier[0]));
        JTextField emailSubjectField = new JTextField();
        JTextArea emailContentArea = new JTextArea(10, 30);

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
            }

            emailPanel.add(createLeftAlignedLabelPanel("Email Subject:"));
            emailPanel.add(emailSubjectField);
            emailPanel.add(createLeftAlignedLabelPanel("Email Content:"));
            emailPanel.add(new JScrollPane(emailContentArea));

            int emailResult = JOptionPane.showConfirmDialog(null, emailPanel, "Send Email",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (emailResult != JOptionPane.OK_OPTION) {
                return;
            }

            if ("Customer".equals(recipientType)) {
                if (customerComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "No customer selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            } else if ("Supplier".equals(recipientType)) {
                if (supplierComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "No supplier selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            }
            String emailSubject = emailSubjectField.getText();
            String emailContent = emailContentArea.getText();

            if (emailSubject.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email subject cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (emailContent.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if ("Customer".equals(recipientType)) {
                Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
                EmailSender.sendEmail(selectedCustomer.getEmail(), emailSubject, emailContent);
            } else if ("Supplier".equals(recipientType)) {
                Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
                EmailSender.sendEmail(selectedSupplier.getEmail(), emailSubject, emailContent);
            }
        }
    }

    private JPanel createLeftAlignedLabelPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        return panel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Worker Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.add(new WorkerMenuPanel(frame, new CustomerService(), new GoodsReceiveNoteService(),
                new InvoiceService(), new ItemService(), new ScaleLicenseService(), new SupplierService()));
        frame.setVisible(true);
    }
}
