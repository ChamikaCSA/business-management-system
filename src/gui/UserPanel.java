package gui;

import services.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class UserPanel extends JPanel {
    private final UserService userService;

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserPanel(JFrame menuFrame, UserService userService) {
        this.userService = userService;

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

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(userTable);
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
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        userService.getUserRegistry().values().forEach(user -> tableModel.addRow(new Object[]{user.getId(), user.getName(), user.getEmail(), user.getType()}));
    }
}
