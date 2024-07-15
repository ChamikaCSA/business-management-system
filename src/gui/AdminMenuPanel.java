package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import services.CustomerService;
import services.InvoiceService;
import services.ItemService;

import static gui.AppGUI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
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
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception e) {
            LOGGER.warning("Failed to apply FlatLaf Look and Feel: " + e.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        gbc.ipady = 20;

        JLabel reportsLabel = new JLabel("Reports and Analysis");
        reportsLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        mainPanel.add(reportsLabel, createGBC(0));

        addButton(mainPanel, gbc, 0, 1, "Stock Report", "icon.png", "generate stock report.", this::viewReport);
        addButton(mainPanel, gbc, 1, 1, "Next Month Sales Forecast", "icon.png", "forecast sales for the next month.", this::nextMonthSalesForecast);
        addButton(mainPanel, gbc, 2, 1, "Income and Sales Analysis", "icon.png", "generate income and sales analysis report.", this::incomeAndSalesAnalysis);

        JLabel userManagementLabel = new JLabel("User Management");
        userManagementLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        mainPanel.add(userManagementLabel, createGBC(2));

        addButton(mainPanel, gbc, 0, 3, "View Users", "icon.png", "view and manage users.", this::viewUsers);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, int x, int y, String text, String iconPath, String toolTip, Runnable action) {
        gbc.gridx = x;
        gbc.gridy = y;

        JButton button = new JButton(text);
        button.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        button.setIcon(new ImageIcon("/icons/" + iconPath));
        button.setToolTipText("Click to " + toolTip);
        button.addActionListener(_ -> action.run());
        button.setPreferredSize(new Dimension(240, 40));

        button.setBackground(SECONDARY_COLOR);
        button.setForeground(BACKGROUND_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR_HOVER);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
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

    private void viewReport() {
        try {
            String report = itemService.generateStockLevelReport();
            JOptionPane.showMessageDialog(this, report, "Stock Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating stock report: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            LOGGER.severe("Error generating stock report: " + e.getMessage());
        }
    }

    private void nextMonthSalesForecast() {
        try {
            List<Double> salesData = invoiceService.getMonthlySalesData();
            if (salesData.size() < 2) {
                throw new Exception("Not enough data to perform forecasting.");
            }

            installPythonPackage("statsmodels");
            String forecast = runPythonScript(salesData);
            JOptionPane.showMessageDialog(this, "Next month's sales forecast is: $" + forecast, "Sales Forecast", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error forecasting sales: " + e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            LOGGER.severe("Error forecasting sales: " + e.getMessage());
        }
    }

    private void installPythonPackage(String packageName) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("pip", "install", packageName);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to install Python package " + packageName);
        }
    }

    private String runPythonScript(List<Double> salesData) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("src/python/forecast.py");
        for (Double data : salesData) {
            command.add(data.toString());
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Script execution failed with exit code " + exitCode);
        }

        return result.toString();
    }

    private void incomeAndSalesAnalysis() {
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("AdminMenuPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.add(new AdminMenuPanel(null, new InvoiceService(new CustomerService(), new ItemService()), new ItemService()));
        frame.setVisible(true);
    }
}
