package gui;

import services.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SupplierPanel extends JPanel {
    private final SupplierService supplierService;

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public SupplierPanel(JFrame menuFrame, SupplierService supplierService) {
        this.supplierService = supplierService;

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

        supplierTable = new JTable();
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

        supplierTable.setModel(tableModel);
        supplierTable.setFillsViewportHeight(true);
        supplierTable.setRowHeight(30);

        sorter = new TableRowSorter<>(tableModel);
        supplierTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(supplierTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadSuppliers();

        addButton.addActionListener(_ -> {
            SupplierDialog supplierDialog = new SupplierDialog(parentFrame, "Add Supplier", supplierService);
            supplierDialog.setVisible(true);
            loadSuppliers();
        });

        editButton.addActionListener(_ -> {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a supplier to edit", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String supplierId = (String) tableModel.getValueAt(selectedRow, 0);
            SupplierDialog supplierDialog = new SupplierDialog(parentFrame, "Edit Supplier", supplierService, supplierId);
            supplierDialog.setVisible(true);
            loadSuppliers();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = supplierTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a supplier to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String supplierId = (String) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this supplier?", "Warning", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                supplierService.deleteSupplier(supplierId);
                loadSuppliers();
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

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        supplierService.getSupplierRegistry().forEach((id, supplier) -> tableModel.addRow(new Object[]{id, supplier.getName(), supplier.getEmail()}));
    }
}
