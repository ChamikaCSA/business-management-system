package views;

import controllers.ScaleLicenseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

public class ScaleLicensePanel extends JPanel {
    private final ScaleLicenseService scaleLicenseService;

    private JTable scaleLicenseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> licenseTypeFilter;
    private JComboBox<String> statusFilter;

    public ScaleLicensePanel(JFrame menuFrame, ScaleLicenseService scaleLicenseService) {
        this.scaleLicenseService = scaleLicenseService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton deleteButton = new JButton("Delete");

        JLabel searchLabel = new JLabel(" Search: ");
        searchField = new JTextField(15);

        JLabel licenseTypeLabel = new JLabel("License Type: ");
        licenseTypeFilter = new JComboBox<>(new String[]{"All", "Basic", "Pro", "Enterprise"});

        JLabel statusLabel = new JLabel("Status: ");
        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Expired", "Suspended"});

        JToolBar toolBar = new JToolBar();
        toolBar.setMargin(new Insets(0, 0, 8, 0));

        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        toolBar.addSeparator();
        toolBar.add(licenseTypeLabel);
        toolBar.add(licenseTypeFilter);

        toolBar.addSeparator();
        toolBar.add(statusLabel);
        toolBar.add(statusFilter);

        scaleLicenseTable = new JTable();

        tableModel = new DefaultTableModel(new Object[]{"ID", "License Type", "Issued Date", "Expiration Date", "Customer ID", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 2, 3 -> Date.class;
                    default -> String.class;
                };
            }
        };
        scaleLicenseTable.setModel(tableModel);
        scaleLicenseTable.setFillsViewportHeight(true);
        scaleLicenseTable.setRowHeight(30);

        TableColumnModel columnModel = scaleLicenseTable.getColumnModel();
        columnModel.setColumnMargin(20);

        sorter = new TableRowSorter<>(tableModel);
        scaleLicenseTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(scaleLicenseTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadScaleLicenses();

        deleteButton.addActionListener(e -> {
            int selectedRow = scaleLicenseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a scale license to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String scaleLicenseId = (String) scaleLicenseTable.getValueAt(selectedRow, 0);
            int result = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete the scale license " + scaleLicenseId + "?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                scaleLicenseService.deleteScaleLicense(scaleLicenseId);
                loadScaleLicenses();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText();
                if (searchText.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });

        licenseTypeFilter.addActionListener(_ -> applyFilters());
        statusFilter.addActionListener(_ -> applyFilters());
    }

    private void loadScaleLicenses() {
        tableModel.setRowCount(0);
        scaleLicenseService.getScaleLicenseRegistry().values().forEach(scaleLicense -> tableModel.addRow(new Object[]{
                scaleLicense.getId(),
                scaleLicense.getLicenseType(),
                scaleLicense.getIssuedDate(),
                scaleLicense.getExpirationDate(),
                scaleLicense.getCustomer().getId(),
                scaleLicense.getStatus()
        }));
    }

    private void applyFilters() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean licenseTypeMatch = true;
                boolean statusMatch = true;

                if (licenseTypeFilter.getSelectedIndex() != 0) {
                    String licenseType = (String) entry.getValue(1);
                    licenseTypeMatch = licenseType.equals(licenseTypeFilter.getSelectedItem());
                }
                if (statusFilter.getSelectedIndex() != 0) {
                    String status = (String) entry.getValue(5);
                    statusMatch = status.equals(statusFilter.getSelectedItem());
                }
                return licenseTypeMatch && statusMatch;
            }
        });
    }
}
