package gui;

import services.GoodsReceiveNoteService;
import services.ItemService;
import services.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Date;

public class GoodsReceiveNotePanel extends JPanel {
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final ItemService itemService;
    private final SupplierService supplierService;

    private JTable itemTable;
    private DefaultTableModel tableModel;

    public GoodsReceiveNotePanel(JFrame menuFrame, GoodsReceiveNoteService goodsReceiveNoteService, ItemService itemService, SupplierService supplierService) {
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.itemService = itemService;
        this.supplierService = supplierService;

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

        tableModel = new DefaultTableModel(new Object[]{"ID", "Supplier ID", "Item ID", "Quantity", "Received Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 3 -> Integer.class;
                    case 4 -> Date.class;
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

        loadGoodsReceiveNotes();

        createButton.addActionListener(_ -> {
            GoodsReceiveNoteDialog goodsReceiveNoteDialog = new GoodsReceiveNoteDialog(parentFrame, "Create Goods Receive Note", goodsReceiveNoteService, itemService, supplierService);
            goodsReceiveNoteDialog.setVisible(true);
            loadGoodsReceiveNotes();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = itemTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a note to delete", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String goodsReceiveNoteId = (String) tableModel.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this note?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                goodsReceiveNoteService.deleteGoodsReceiveNoteById(goodsReceiveNoteId);
                loadGoodsReceiveNotes();
            }
        });
    }

    private void loadGoodsReceiveNotes() {
        tableModel.setRowCount(0);
        goodsReceiveNoteService.getGoodsReceiveNotesRegistry().values().forEach(grn -> tableModel.addRow(new Object[]{grn.getId(), grn.getSupplier().getId(), grn.getItem().getId(), grn.getQuantity(), grn.getReceivedDate()}));
    }
}
