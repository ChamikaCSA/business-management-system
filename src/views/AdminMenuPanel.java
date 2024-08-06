package views;

import controllers.InvoiceService;
import controllers.ItemService;
import utils.Forecast;

import static views.AppGUI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

public class AdminMenuPanel extends JPanel {
    private final InvoiceService invoiceService;
    private final ItemService itemService;

    private static final Logger LOGGER = Logger.getLogger(AdminMenuPanel.class.getName());

    public AdminMenuPanel(JFrame menuFrame,
                          InvoiceService invoiceService, ItemService itemService) {
        this.invoiceService = invoiceService;
        this.itemService = itemService;

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(TEXT_COLOR);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addSection(mainPanel, gbc, "Reports and Analysis", 0);
        addButton(mainPanel, gbc, 0, 1, "View Stock Report", "icon.png", "view stock level report.", this::viewStockReport);
        addButton(mainPanel, gbc, 1, 1, "Forecast Next Month Sales", "icon.png", "forecast sales for the next month.", this::forecastNextMonthSales);
        addButton(mainPanel, gbc, 2, 1, "Analyse Income and Sales", "icon.png", "analyse income and sales.", this::analyseIncomeAndSales);

        addSection(mainPanel, gbc, "User Management", 2);
        addButton(mainPanel, gbc, 0, 3, "View Users", "icon.png", "view and manage users.", this::viewUsers);

        addLowStockAlerts(mainPanel, gbc, 0, 5);

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

    private void viewStockReport() {
        try {
            String report = itemService.generateStockLevelReport();
            JOptionPane.showMessageDialog(this, report, "Stock Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating stock report: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            LOGGER.severe("Error generating stock report: " + e.getMessage());
        }
    }

    private void forecastNextMonthSales() {
        try {
            List<Double> salesData = invoiceService.getMonthlySalesData();
            if (salesData.size() < 2) {
                throw new Exception("Not enough data to perform forecasting.");
            }

            String forecast = Forecast.generateForecast(salesData);
            forecast = String.format("%.2f", Double.parseDouble(forecast));

            JOptionPane.showMessageDialog(this, "Next month's sales forecast : $" + forecast, "Sales Forecast", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error forecasting sales: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            LOGGER.severe("Error forecasting sales: " + e.getMessage());
        }
    }


    private void analyseIncomeAndSales() {
        try {
            String report = invoiceService.generateIncomeAndSalesAnalysis();
            JOptionPane.showMessageDialog(this, report, "Income and Sales Analysis", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating income and sales analysis: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            LOGGER.severe("Error generating income and sales analysis: " + e.getMessage());
        }
    }

    private void viewUsers() {
        JTabbedPane tabbedPane = AppGUI.getTabbedPane();
        tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Users"));
    }

    private void addLowStockAlerts(JPanel panel, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 3;

        JTextArea alertArea = new JTextArea();
        alertArea.setEditable(false);
        alertArea.setLineWrap(true);
        alertArea.setWrapStyleWord(true);
        alertArea.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        alertArea.setBorder(BorderFactory.createEmptyBorder());

        List<String> lowStockItems = itemService.getLowStockItems();
        if (!lowStockItems.isEmpty()) {
            try {
                StringBuilder alerts = new StringBuilder("Low Stock Items:\n");
                for (String item : lowStockItems) {
                    alerts.append(item).append("\n");
                }
                alerts.deleteCharAt(alerts.length() - 1);
                alertArea.setText(alerts.toString());
            } catch (Exception e) {
                alertArea.setText("Error fetching low stock items: " + e.getMessage());
                LOGGER.severe("Error fetching low stock items: " + e.getMessage());
            }

            JScrollPane scrollPane = new JScrollPane(alertArea);
            scrollPane.setPreferredSize(new Dimension(400, 150));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));

            panel.add(scrollPane, gbc);
        }
        gbc.gridwidth = 1;
    }
}
