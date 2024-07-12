package gui;

import services.InvoiceService;
import services.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Date;

public class PaymentPanel extends JPanel {
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    public PaymentPanel(JFrame menuFrame, PaymentService paymentService,InvoiceService invoiceService) {
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton deleteButton = new JButton("Delete");
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

        paymentTable = new JTable();

        tableModel = new DefaultTableModel(new Object[]{"ID", "Invoice ID", "Amount", "Payment Date", "Payment Method"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 2 -> Double.class;
                    case 3 -> Date.class;
                    default -> String.class;
                };
            }
        };
        paymentTable.setModel(tableModel);
        paymentTable.setFillsViewportHeight(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        paymentTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        add(scrollPane, BorderLayout.CENTER);

        loadPayments();

        deleteButton.addActionListener(_ -> {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a payment to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String paymentId = (String) tableModel.getValueAt(selectedRow, 0);
            int result = JOptionPane.showConfirmDialog(this, STR."Are you sure you want to delete payment \{paymentId}?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                paymentService.deletePayment(paymentId);
                loadPayments();
            }
        });
    }

    private void loadPayments() {
        tableModel.setRowCount(0);
        paymentService.getPaymentRegistry().values().forEach(payment -> tableModel.addRow(new Object[]{
            payment.getId(),
            payment.getInvoice().getId(),
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getPaymentMethod()
        }));
    }
}
