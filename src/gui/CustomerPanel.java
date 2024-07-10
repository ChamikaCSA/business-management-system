package gui;

import services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class CustomerPanel extends JPanel {
    private final CustomerService customerService;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerPanel(JFrame menuFrame, CustomerService customerService) {
        this.customerService = customerService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

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

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(customerTable);
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
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        customerService.getCustomerRegistry().forEach((id, customer) -> tableModel.addRow(new Object[]{id, customer.getName(), customer.getEmail()}));
    }
}
