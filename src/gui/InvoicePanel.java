package gui;

import entities.Invoice;
import services.InvoiceService;
import services.CustomerService;
import services.ItemService;
import services.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class InvoicePanel extends JPanel {
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final PaymentService paymentService;

    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> totalAmountFilter;

    public InvoicePanel(JFrame menuFrame, InvoiceService invoiceService, CustomerService customerService, ItemService itemService, PaymentService paymentService) {
        this.invoiceService = invoiceService;
        this.customerService = customerService;
        this.itemService = itemService;
        this.paymentService = paymentService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JButton createButton = new JButton("Create");
        JButton viewButton = new JButton("View");
        JButton deleteButton = new JButton("Delete");

        JLabel searchLabel = new JLabel(" Search: ");
        searchField = new JTextField(15);

        JLabel totalAmountLabel = new JLabel("Total Amount: ");
        totalAmountFilter = new JComboBox<>(new String[]{"All", "Below $100", "$100 - $500", "$500 - $1000", "$1000 - $5000", "Above $5000"});

        JToolBar toolBar = new JToolBar();
        toolBar.add(createButton);
        toolBar.add(viewButton);
        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        toolBar.addSeparator();
        toolBar.add(totalAmountLabel);
        toolBar.add(totalAmountFilter);

        invoiceTable = new JTable();

        tableModel = new DefaultTableModel(new Object[]{"ID", "Customer ID", "Item ID", "Quantity", "Total Amount", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 3 -> Integer.class;
                    case 4 -> Double.class;
                    case 5 -> Date.class;
                    default -> String.class;
                };
            }
        };
        invoiceTable.setModel(tableModel);
        invoiceTable.setFillsViewportHeight(true);

        sorter = new TableRowSorter<>(tableModel);
        invoiceTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadInvoiceData();

        createButton.addActionListener(_ -> {
            InvoiceDialog invoiceDialog = new InvoiceDialog(parentFrame, "Create Invoice", invoiceService, customerService, itemService, paymentService);
            invoiceDialog.setVisible(true);
            loadInvoiceData();
        });

        viewButton.addActionListener(_ -> {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select an invoice to view", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice != null) {
                showInvoiceDetailsDialog(parentFrame, invoice);
            }
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select an invoice to delete", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
            int result = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this invoice?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                invoiceService.deleteInvoice(invoiceId);
                loadInvoiceData();
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

        totalAmountFilter.addActionListener(_ -> applyFilter());

        invoiceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    int selectedRow = invoiceTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
                        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
                        if (invoice != null) {
                            showInvoiceDetailsDialog(parentFrame, invoice);
                        }
                    }
                }
            }
        });
    }

    private void loadInvoiceData() {
        tableModel.setRowCount(0);
        invoiceService.getInvoiceRegistry().values().forEach(invoice -> {
            StringBuilder itemIDs = new StringBuilder("<html>");
            StringBuilder quantities = new StringBuilder("<html>");
            invoice.getItemsMap().forEach((item, quantity) -> {
                itemIDs.append(item.getId()).append("<br>");
                quantities.append(quantity).append("<br>");
            });
            if (itemIDs.length() > 6) itemIDs.setLength(itemIDs.length() - 4);
            if (quantities.length() > 6) quantities.setLength(quantities.length() - 4);
            itemIDs.append("</html>");
            quantities.append("</html>");
            tableModel.addRow(new Object[]{
                    invoice.getId(),
                    invoice.getCustomer().getId(),
                    itemIDs.toString(),
                    quantities.toString(),
                    invoice.getTotalAmount(),
                    invoice.getDate()
            });
        });
        adjustRowHeight(invoiceTable);
    }

    private void adjustRowHeight(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            for (int column = 0; column < table.getColumnCount(); column++) {
                if (tableModel.getRowCount() > row) {
                    Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }
            }
            table.setRowHeight(row, rowHeight);
        }
    }

    private void applyFilter() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean totalAmountMatch = true;

                if (totalAmountFilter.getSelectedIndex() != 0) {
                    double totalAmount = (Double) entry.getValue(4);
                    switch (totalAmountFilter.getSelectedIndex()) {
                        case 1:
                            totalAmountMatch = totalAmount < 100;
                            break;
                        case 2:
                            totalAmountMatch = totalAmount >= 100 && totalAmount <= 500;
                            break;
                        case 3:
                            totalAmountMatch = totalAmount >= 500 && totalAmount <= 1000;
                            break;
                        case 4:
                            totalAmountMatch = totalAmount >= 1000 && totalAmount <= 5000;
                            break;
                        case 5:
                            totalAmountMatch = totalAmount > 5000;
                            break;
                    }
                }
                return totalAmountMatch;
            }
        });
    }

    private void showInvoiceDetailsDialog(JFrame parentFrame,Invoice invoice) {
        InvoiceDetailsDialog invoiceDetailsDialog = new InvoiceDetailsDialog(parentFrame ,invoice);
        invoiceDetailsDialog.setModal(true);
        invoiceDetailsDialog.setVisible(true);
    }
}
