package views;

import models.GoodsReceiveNote;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class GoodsReceiveNoteDetailsDialog extends JDialog {

    public GoodsReceiveNoteDetailsDialog(JFrame parentFrame, GoodsReceiveNote goodsReceiveNote) {
        initialize(parentFrame, goodsReceiveNote);
    }

    private void initialize(JFrame parentFrame, GoodsReceiveNote goodsReceiveNote) {
        setTitle("Goods Receive Note Details");
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel receivedDateLabel = new JLabel("Date");
        JLabel receivedDateValueLabel = new JLabel(goodsReceiveNote.getReceivedDate().toString());

        JLabel grnIdLabel = new JLabel("Goods Receive Note ID");
        JLabel grnIdValueLabel = new JLabel(goodsReceiveNote.getId());

        JLabel supplierLabel = new JLabel("Supplier");
        JPanel supplierPanel = new JPanel();
        supplierPanel.setLayout(new BoxLayout(supplierPanel, BoxLayout.Y_AXIS));

        JLabel supplierIdLabel = new JLabel(goodsReceiveNote.getSupplier().getId());
        JLabel supplierNameLabel = new JLabel(goodsReceiveNote.getSupplier().getName());
        JLabel supplierEmailLabel = new JLabel(goodsReceiveNote.getSupplier().getEmail());

        supplierPanel.add(supplierLabel);
        supplierPanel.add(supplierIdLabel);
        supplierPanel.add(supplierNameLabel);
        supplierPanel.add(supplierEmailLabel);

        JLabel itemLabel = new JLabel("Item");
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        JLabel itemIdLabel = new JLabel(goodsReceiveNote.getItem().getId());
        JLabel itemNameLabel = new JLabel(goodsReceiveNote.getItem().getName());
        JLabel itemPriceLabel = new JLabel("$ " + goodsReceiveNote.getItem().getPrice());
        JLabel itemQuantityLabel = new JLabel("x " + goodsReceiveNote.getQuantity());

        itemPanel.add(itemIdLabel);
        itemPanel.add(itemNameLabel);
        itemPanel.add(itemPriceLabel);
        itemPanel.add(itemQuantityLabel);
        itemPanel.add(Box.createVerticalStrut(10));

        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Goods Receive Note as PDF");
                fileChooser.setSelectedFile(new File(goodsReceiveNote.getId() + ".pdf"));
                int userSelection = fileChooser.showSaveDialog(parentFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!filePath.endsWith(".pdf")) {
                        filePath += ".pdf";
                    }
                    try {
                        createPdf(filePath, goodsReceiveNote);
                        JOptionPane.showMessageDialog(parentFrame, "PDF saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (FileNotFoundException | DocumentException ex) {
                        JOptionPane.showMessageDialog(parentFrame, "Error saving PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(receivedDateLabel, gbc);

        gbc.gridx++;
        add(receivedDateValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(grnIdLabel, gbc);

        gbc.gridx++;
        add(grnIdValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(supplierLabel, gbc);

        gbc.gridx++;
        add(supplierPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(itemLabel, gbc);

        gbc.gridx++;
        add(itemPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(downloadButton, gbc);

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void createPdf(String filePath, GoodsReceiveNote goodsReceiveNote) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph title = new Paragraph("Goods Receive Note", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingBefore(10f);
        detailsTable.setSpacingAfter(10f);

        detailsTable.setHeaderRows(1);

        addCellToTable(detailsTable, "Date", textFont, true);
        addCellToTable(detailsTable, goodsReceiveNote.getReceivedDate().toString(), textFont, false);

        addCellToTable(detailsTable, "Goods Receive Note ID", textFont, true);
        addCellToTable(detailsTable, goodsReceiveNote.getId(), textFont, false);

        addCellToTable(detailsTable, "Supplier ID", textFont, true);
        addCellToTable(detailsTable, goodsReceiveNote.getSupplier().getId(), textFont, false);

        addCellToTable(detailsTable, "Supplier Name", textFont, true);
        addCellToTable(detailsTable, goodsReceiveNote.getSupplier().getName(), textFont, false);

        addCellToTable(detailsTable, "Supplier Email", textFont, true);
        addCellToTable(detailsTable, goodsReceiveNote.getSupplier().getEmail(), textFont, false);

        document.add(detailsTable);

        document.add(new Paragraph("Item Details", subtitleFont));

        PdfPTable itemTable = new PdfPTable(4);
        itemTable.setWidthPercentage(100);
        itemTable.setSpacingBefore(10f);
        itemTable.setSpacingAfter(10f);
        itemTable.setHeaderRows(1);

        addCellToTable(itemTable, "Item ID", textFont, true);
        addCellToTable(itemTable, "Item Name", textFont, true);
        addCellToTable(itemTable, "Item Price", textFont, true);
        addCellToTable(itemTable, "Quantity", textFont, true);

        addCellToTable(itemTable, goodsReceiveNote.getItem().getId(), textFont, false);
        addCellToTable(itemTable, goodsReceiveNote.getItem().getName(), textFont, false);
        addCellToTable(itemTable, "$ " + goodsReceiveNote.getItem().getPrice(), textFont, false);
        addCellToTable(itemTable, String.valueOf(goodsReceiveNote.getQuantity()), textFont, false);

        document.add(itemTable);

        document.add(new Paragraph(" "));
        document.close();
    }

    private void addCellToTable(PdfPTable table, String text, Font font, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setPadding(10);
        cell.setBorderWidth(1);
        cell.setBorderColor(BaseColor.GRAY);

        if (isHeader) {
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else {
            cell.setBackgroundColor(BaseColor.WHITE);
        }

        table.addCell(cell);
    }
}
