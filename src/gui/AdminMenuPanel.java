package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import entities.User;
import services.*;
import utils.IDGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AdminMenuPanel extends JPanel {
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final UserService userService;

    private static final Logger LOGGER = Logger.getLogger(AdminMenuPanel.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public AdminMenuPanel(JFrame menuFrame,
                          InvoiceService invoiceService, ItemService itemService, UserService userService) {
        this.invoiceService = invoiceService;
        this.itemService = itemService;
        this.userService = userService;

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

        JLabel titleLabel = new JLabel("Admin Dashboard");
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

        JLabel reportsLabel = new JLabel("Reports and Analysis");
        reportsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(reportsLabel, createGBC(0));

        addButton(mainPanel, gbc, 0, 1, "View Reports", "icon.png", "view various reports such as sales or stock reports.", this::viewReports);
        addButton(mainPanel, gbc, 1, 1, "Next Month Sales Forecast", "icon.png", "forecast sales for the next month.", this::nextMonthSalesForecast);
        addButton(mainPanel, gbc, 2, 1, "Income and Sales Analysis", "icon.png", "generate income and sales analysis report.", this::incomeAndSalesAnalysis);

        JLabel userManagementLabel = new JLabel("User Management");
        userManagementLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(userManagementLabel, createGBC(2));

        addButton(mainPanel, gbc, 0, 3, "View Users", "icon.png", "view and manage users.", this::viewUsers);
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

    private void viewReports() {
        JComboBox<String> reportComboBox = new JComboBox<>(new String[]{"Sales Report", "Stock Report"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select Report:"));
        panel.add(reportComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "View Reports",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedReport = (String) reportComboBox.getSelectedItem();

        if ("Sales Report".equals(selectedReport)) {
            try {
                String report = invoiceService.generateMonthlySalesReport();
                JOptionPane.showMessageDialog(this, report);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, STR."Error generating sales report: \{e.getMessage()}");
            }
        } else if ("Stock Report".equals(selectedReport)) {
            try {
                String report = itemService.generateStockLevelReport();
                JOptionPane.showMessageDialog(this, report);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, STR."Error generating stock report: \{e.getMessage()}");
            }
        }
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

    private void viewUsers() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Users"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);
            frame.add(new AdminMenuPanel(frame, new InvoiceService(), new ItemService(), new UserService()));
            frame.setVisible(true);
        });
    }
}
