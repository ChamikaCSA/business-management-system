package gui;

import services.GoodsReceiveNoteService;
import services.ItemService;
import services.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

public class GoodsReceiveNotePanel extends JPanel {
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final ItemService itemService;
    private final SupplierService supplierService;

    private JTable grnTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> quantityFilter;


    public GoodsReceiveNotePanel(JFrame menuFrame, GoodsReceiveNoteService goodsReceiveNoteService, ItemService itemService, SupplierService supplierService) {
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.itemService = itemService;
        this.supplierService = supplierService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JButton createButton = new JButton("Create");
        JButton deleteButton = new JButton("Delete");

        JLabel searchLabel = new JLabel(" Search: ");
        searchField = new JTextField(15);

        JLabel quantityLabel = new JLabel("Quantity: ");
        quantityFilter = new JComboBox<>(new String[]{"All", "Below 20", "20 - 100", "100 - 500", "Above 500"});

        JToolBar toolBar = new JToolBar();
        toolBar.add(createButton);
        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        toolBar.addSeparator();
        toolBar.add(quantityLabel);
        toolBar.add(quantityFilter);

        add(toolBar, BorderLayout.NORTH);

        grnTable = new JTable();
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
        grnTable.setModel(tableModel);
        grnTable.setFillsViewportHeight(true);
        grnTable.setRowHeight(30);

        sorter = new TableRowSorter<>(tableModel);
        grnTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(grnTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadGoodsReceiveNotes();

        createButton.addActionListener(_ -> {
            GoodsReceiveNoteDialog goodsReceiveNoteDialog = new GoodsReceiveNoteDialog(parentFrame, "Create Goods Receive Note", goodsReceiveNoteService, itemService, supplierService);
            goodsReceiveNoteDialog.setVisible(true);
            loadGoodsReceiveNotes();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = grnTable.getSelectedRow();
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

        quantityFilter.addActionListener(_ -> applyFilter());
    }

    private void loadGoodsReceiveNotes() {
        tableModel.setRowCount(0);
        goodsReceiveNoteService.getGoodsReceiveNotesRegistry().values().forEach(grn -> tableModel.addRow(new Object[]{grn.getId(), grn.getSupplier().getId(), grn.getItem().getId(), grn.getQuantity(), grn.getReceivedDate()}));
    }

    private void applyFilter() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean quantityMatch = true;

                if (quantityFilter.getSelectedIndex() != 0) {
                    int quantity = (int) entry.getValue(3);
                    switch (quantityFilter.getSelectedIndex()) {
                        case 1 -> quantityMatch = quantity < 20;
                        case 2 -> quantityMatch = quantity >= 20 && quantity <= 100;
                        case 3 -> quantityMatch = quantity >= 100 && quantity <= 500;
                        case 4 -> quantityMatch = quantity > 500;
                    }
                }
                return quantityMatch;
            }
        });
    }
}
