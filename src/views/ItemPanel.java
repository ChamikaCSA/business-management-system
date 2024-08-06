package views;

import controllers.ItemService;
import controllers.GoodsReceiveNoteService;
import controllers.InvoiceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ItemPanel extends JPanel {
    private final ItemService itemService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;

    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> priceFilter;
    private JComboBox<String> quantityFilter;

    public ItemPanel(JFrame menuFrame, ItemService itemService, GoodsReceiveNoteService goodsReceiveNoteService, InvoiceService invoiceService) {
        this.itemService = itemService;
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.invoiceService = invoiceService;

        initialize(menuFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        JLabel searchLabel = new JLabel(" Search: ");
        searchField = new JTextField(15);

        JLabel priceLabel = new JLabel("Price: ");
        priceFilter = new JComboBox<>(new String[]{"All", "Below $50", "$100 - $200", "$200 - $500", "$500 - $1000", "Above $1000"});

        JLabel quantityLabel = new JLabel("Quantity: ");
        quantityFilter = new JComboBox<>(new String[]{"All", "Out of Stock", "Low Stock", "Medium Stock", "High Stock", "Very High Stock"});

        JToolBar toolBar = new JToolBar();
        toolBar.setMargin(new Insets(0, 0, 8, 0));

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        toolBar.addSeparator();
        toolBar.add(priceLabel);
        toolBar.add(priceFilter);

        toolBar.addSeparator();
        toolBar.add(quantityLabel);
        toolBar.add(quantityFilter);

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

        TableColumnModel columnModel = itemTable.getColumnModel();
        columnModel.setColumnMargin(20);

        sorter = new TableRowSorter<>(tableModel);
        itemTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(itemTable);

        add(toolBar, BorderLayout.NORTH);
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

            if (itemService.hasGoodsReceiveNotes(itemId) || itemService.hasInvoiceItems(itemId)) {
                int option = JOptionPane.showConfirmDialog(parentFrame,
                        "This item has associated goods recieve notes and invoices. Are you sure you want to delete this item?",
                        "Delete Item",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
                goodsReceiveNoteService.deleteGoodsReceiveNotesByItem(itemId);
                invoiceService.deleteInvoiceItemsByItem(itemId);
            } else {
                int option = JOptionPane.showConfirmDialog(parentFrame,
                        "Are you sure you want to delete this item?",
                        "Delete Item",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            itemService.deleteItem(itemId);
            loadItems();
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

        priceFilter.addActionListener(_ -> applyFilters());
        quantityFilter.addActionListener(_ -> applyFilters());
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        itemService.getItemRegistry().values().forEach(item -> tableModel.addRow(new Object[]{item.getId(), item.getName(), item.getPrice(), item.getQuantity()}));
    }

    private void applyFilters() {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                boolean priceMatch = true;
                boolean quantityMatch = true;

                if (priceFilter.getSelectedIndex() != 0) {
                    double price = (Double) entry.getValue(2);
                    switch (priceFilter.getSelectedIndex()) {
                        case 1:
                            priceMatch = price < 50;
                            break;
                        case 2:
                            priceMatch = price >= 100 && price <= 200;
                            break;
                        case 3:
                            priceMatch = price >= 200 && price <= 500;
                            break;
                        case 4:
                            priceMatch = price >= 500 && price <= 1000;
                            break;
                        case 5:
                            priceMatch = price > 1000;
                            break;
                    }
                }

                if (quantityFilter.getSelectedIndex() != 0) {
                    int quantity = (Integer) entry.getValue(3);
                    switch (quantityFilter.getSelectedIndex()) {
                        case 1:
                            quantityMatch = quantity == 0;
                            break;
                        case 2:
                            quantityMatch = quantity > 0 && quantity <= 20;
                            break;
                        case 3:
                            quantityMatch = quantity > 20 && quantity <= 100;
                            break;
                        case 4:
                            quantityMatch = quantity > 100 && quantity <= 500;
                            break;
                        case 5:
                            quantityMatch = quantity > 500;
                            break;
                    }
                }
                return priceMatch && quantityMatch;
            }
        });
    }
}
