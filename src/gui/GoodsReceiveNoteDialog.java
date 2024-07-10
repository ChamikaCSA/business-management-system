package gui;

import entities.GoodsReceiveNote;
import entities.Item;
import entities.Supplier;
import services.GoodsReceiveNoteService;
import services.ItemService;
import services.SupplierService;
import utils.EmailSender;
import utils.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class GoodsReceiveNoteDialog extends JDialog {
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final ItemService itemService;
    private final SupplierService supplierService;

    private JComboBox<Item> itemComboBox;
    private JComboBox<Supplier> supplierComboBox;
    private JTextField quantityField;

    public GoodsReceiveNoteDialog(JFrame parentFrame, String title, GoodsReceiveNoteService goodsReceiveNoteService, ItemService itemService, SupplierService supplierService) {
        super(parentFrame, title, true);
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.itemService = itemService;
        this.supplierService = supplierService;

        initialize(parentFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel supplierLabel = new JLabel("Supplier:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(supplierLabel, gbc);

        supplierComboBox = new JComboBox<>(supplierService.getSupplierRegistry().values().toArray(new Supplier[0]));
        gbc.gridx = 1;
        add(supplierComboBox, gbc);

        JLabel itemLabel = new JLabel("Item:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(itemLabel, gbc);

        itemComboBox = new JComboBox<>(itemService.getItemRegistry().values().toArray(new Item[0]));
        gbc.gridx = 1;
        add(itemComboBox, gbc);

        JLabel quantityLabel = new JLabel("Quantity:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(quantityLabel, gbc);

        quantityField = new JTextField(20);
        gbc.gridx = 1;
        add(quantityField, gbc);

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> saveGoodsReceiveNote());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dispose());
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void saveGoodsReceiveNote() {
        Supplier supplier = (Supplier) supplierComboBox.getSelectedItem();
        Item item = (Item) itemComboBox.getSelectedItem();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int grnCount = goodsReceiveNoteService.getGoodsReceiveNoteRegistry().size() + 1;
        String goodsReceiveNoteId = IDGenerator.generateDatedId("GRN", grnCount);
        Date receivedDate = new Date();

        GoodsReceiveNote goodsReceiveNote = new GoodsReceiveNote(goodsReceiveNoteId, supplier, item, receivedDate, quantity);
        goodsReceiveNoteService.insertGoodsReceiveNote(goodsReceiveNote);

        SendGoodsReceiveNote(goodsReceiveNote);
        dispose();
    }

    private void SendGoodsReceiveNote(GoodsReceiveNote goodsReceiveNote) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(goodsReceiveNote.getId()).append("\n");
        sb.append("Supplier: ").append(goodsReceiveNote.getSupplier().getName()).append("\n");
        sb.append("Item: ").append(goodsReceiveNote.getItem().getName()).append("\n");
        sb.append("Quantity: ").append(goodsReceiveNote.getQuantity()).append("\n");
        sb.append("Received Date: ").append(goodsReceiveNote.getReceivedDate()).append("\n");

        EmailSender.sendEmail(goodsReceiveNote.getSupplier().getEmail(), "Goods Receive Note", sb.toString());
        }
}
