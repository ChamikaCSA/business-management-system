package gui;

import services.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class SupplierPanel extends JPanel {
    private final SupplierService supplierService;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierPanel(JFrame menuFrame, SupplierService supplierService) {
        this.supplierService = supplierService;

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

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        supplierTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(supplierTable);
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
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        supplierService.getSupplierRegistry().forEach((id, supplier) -> tableModel.addRow(new Object[]{id, supplier.getName(), supplier.getEmail()}));
    }
}
