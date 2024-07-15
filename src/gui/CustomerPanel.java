package gui;

import services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CustomerPanel extends JPanel {
    private final CustomerService customerService;

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public CustomerPanel(JFrame menuFrame, CustomerService customerService) {
        this.customerService = customerService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        JLabel searchLabel = new JLabel(" Search: ");
        searchField = new JTextField(15);

        JToolBar toolBar = new JToolBar();
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        customerTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        customerTable.setModel(tableModel);
        customerTable.setFillsViewportHeight(true);
        customerTable.setRowHeight(30);

        sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(customerTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadCustomers();

        addButton.addActionListener(_ -> {
            CustomerDialog customerDialog = new CustomerDialog(parentFrame, "Add Customer", customerService);
            customerDialog.setVisible(true);
            loadCustomers();
        });

        editButton.addActionListener(_ -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a customer to edit", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String customerId = (String) tableModel.getValueAt(selectedRow, 0);
            CustomerDialog customerDialog = new CustomerDialog(parentFrame, "Edit Customer", customerService, customerId);
            customerDialog.setVisible(true);
            loadCustomers();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a customer to delete", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String customerId = (String) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this customer?", "Delete Customer", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                customerService.deleteCustomer(customerId);
                loadCustomers();
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
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        customerService.getCustomerRegistry().forEach((id, customer) -> tableModel.addRow(new Object[]{id, customer.getName(), customer.getEmail()}));
    }
}
