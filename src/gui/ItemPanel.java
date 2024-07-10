package gui;

import services.ItemService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class ItemPanel extends JPanel {
    private final ItemService itemService;

    private JTable itemTable;
    private DefaultTableModel tableModel;

    public ItemPanel(JFrame menuFrame, ItemService itemService) {
        this.itemService = itemService;

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

        itemTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 2 -> Double.class;
                    case 3 -> Integer.class;
                    default -> String.class;
                };
            }
        };
        itemTable.setModel(tableModel);
        itemTable.setFillsViewportHeight(true);
        itemTable.setRowHeight(30);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        itemTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        loadItems();

        addButton.addActionListener(_ -> {
            ItemDialog itemDialog = new ItemDialog(parentFrame, "Add Item", itemService);
            itemDialog.setVisible(true);
            loadItems();
        });

        editButton.addActionListener(_ -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select an item to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String itemId = (String) tableModel.getValueAt(selectedRow, 0);
            ItemDialog itemDialog = new ItemDialog(parentFrame, "Edit Item", itemService, itemId);
            itemDialog.setVisible(true);
            loadItems();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select an item to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String itemId = (String) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this item?", "Delete Item", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                itemService.deleteItem(itemId);
                loadItems();
            }
        });
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        itemService.getItemRegistry().values().forEach(item -> tableModel.addRow(new Object[]{item.getId(), item.getName(), item.getPrice(), item.getQuantity()}));
    }
}
