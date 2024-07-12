package gui;

import services.InvoiceService;
import services.CustomerService;
import services.ItemService;
import services.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Date;

public class InvoicePanel extends JPanel {
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final PaymentService paymentService;

    private JTable itemTable;
    private DefaultTableModel tableModel;

    public InvoicePanel(JFrame menuFrame, InvoiceService invoiceService, CustomerService customerService, ItemService itemService, PaymentService paymentService) {
        this.invoiceService = invoiceService;
        this.customerService = customerService;
        this.itemService = itemService;
        this.paymentService = paymentService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton createButton = new JButton("Create");
        JButton deleteButton = new JButton("Delete");
        toolBar.add(createButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

        itemTable = new JTable();

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
        itemTable.setModel(tableModel);
        itemTable.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        itemTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        loadInvoiceData();

        createButton.addActionListener(_ -> {
            InvoiceDialog invoiceDialog = new InvoiceDialog(parentFrame, "Create Invoice", invoiceService, customerService, itemService, paymentService);
            invoiceDialog.setVisible(true);
            loadInvoiceData();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = itemTable.getSelectedRow();
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
        adjustRowHeight(itemTable);
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
}
