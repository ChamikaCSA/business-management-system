package views;

import controllers.PaymentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

public class PaymentPanel extends JPanel {
    private final PaymentService paymentService;

    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> amountFilter;
    private JComboBox<String> paymentMethodFilter;

    public PaymentPanel(JFrame menuFrame, PaymentService paymentService) {
        this.paymentService = paymentService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton deleteButton = new JButton("Delete");

        JLabel searchLabel = new JLabel(" Search: ");
        searchField = new JTextField(15);

        JLabel amountLabel = new JLabel("Amount: ");
        amountFilter = new JComboBox<>(new String[]{"All", "Below $100", "$100 - $500", "$500 - $1000", "$1000 - $5000", "Above $5000"});

        JLabel paymentMethodLabel = new JLabel("Payment Method: ");
        paymentMethodFilter = new JComboBox<>(new String[]{"All", "Cash", "Credit Card", "Debit Card", "Bank Transfer", "Cheque"});

        JToolBar toolBar = new JToolBar();
        toolBar.setMargin(new Insets(0, 0, 8, 0));

        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        toolBar.addSeparator();
        toolBar.add(amountLabel);
        toolBar.add(amountFilter);

        toolBar.addSeparator();
        toolBar.add(paymentMethodLabel);
        toolBar.add(paymentMethodFilter);

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
        paymentTable.setRowHeight(30);

        TableColumnModel columnModel = paymentTable.getColumnModel();
        columnModel.setColumnMargin(20);

        sorter = new TableRowSorter<>(tableModel);
        paymentTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(paymentTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadPayments();

        deleteButton.addActionListener(_ -> {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a payment to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String paymentId = (String) tableModel.getValueAt(selectedRow, 0);
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete payment " + paymentId + "?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                paymentService.deletePayment(paymentId);
                loadPayments();
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

        amountFilter.addActionListener(_ -> applyFilters());
        paymentMethodFilter.addActionListener(_ -> applyFilters());
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

    private void applyFilters() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean amountMatch = true;
                boolean paymentMethodMatch = true;

                if (amountFilter.getSelectedIndex() != 0) {
                    double amount = (double) entry.getValue(2);
                    switch (amountFilter.getSelectedIndex()) {
                        case 1 -> amountMatch = amount < 100;
                        case 2 -> amountMatch = amount >= 100 && amount <= 500;
                        case 3 -> amountMatch = amount >= 500 && amount <= 1000;
                        case 4 -> amountMatch = amount >= 1000 && amount <= 5000;
                        case 5 -> amountMatch = amount > 5000;
                    }
                }
                if (paymentMethodFilter.getSelectedIndex() != 0) {
                    String paymentMethod = (String) entry.getValue(4);
                    paymentMethodMatch = paymentMethod.equals(paymentMethodFilter.getSelectedItem());
                }
                return amountMatch && paymentMethodMatch;
            }
        });
    }
}
