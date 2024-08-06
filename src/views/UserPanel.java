package views;

import controllers.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UserPanel extends JPanel {
    private final UserService userService;

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> typeFilter;

    public UserPanel(JFrame menuFrame, UserService userService) {
        this.userService = userService;

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

        JLabel typeLabel = new JLabel("Type: ");
        typeFilter = new JComboBox<>(new String[]{"All", "Admin", "Worker"});

        JToolBar toolBar = new JToolBar();
        toolBar.setMargin(new Insets(0, 0, 8, 0));

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);

        toolBar.addSeparator();
        toolBar.add(searchLabel);
        toolBar.add(searchField);

        toolBar.addSeparator();
        toolBar.add(typeLabel);
        toolBar.add(typeFilter);

        userTable = new JTable();
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        userTable.setModel(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(30);

        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.setColumnMargin(20);

        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(userTable);

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadUsers();

        addButton.addActionListener(_ -> {
            UserDialog userDialog = new UserDialog(parentFrame, "Add User", userService);
            userDialog.setVisible(true);
            loadUsers();
        });

        editButton.addActionListener(_ -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a user to edit", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String userId = (String) userTable.getValueAt(selectedRow, 0);
            UserDialog userDialog = new UserDialog(parentFrame, "Edit User", userService, userId);
            userDialog.setVisible(true);
            loadUsers();
        });

        deleteButton.addActionListener(_ -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a user to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String userId = (String) userTable.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to delete this user?", "Warning", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                userService.deleteUser(userId);
                loadUsers();
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

        typeFilter.addActionListener(_ -> {
            String selectedType = (String) typeFilter.getSelectedItem();
            assert selectedType != null;
            if (selectedType.equals("All")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selectedType, 3));
            }
        });
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        userService.getUserRegistry().values().forEach(user -> tableModel.addRow(new Object[]{user.getId(), user.getName(), user.getEmail(), user.getType()}));
    }
}
