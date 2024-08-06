package views;

import models.Invoice;
import models.Item;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class InvoiceDetailsDialog extends JDialog {

    public InvoiceDetailsDialog(JFrame parentFrame, Invoice invoice) {
        initialize(parentFrame, invoice);
    }

    private void initialize(JFrame parentFrame, Invoice invoice) {
        setTitle("Invoice Details");
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel dateLabel = new JLabel("Date");
        JLabel dateValueLabel = new JLabel(invoice.getDate().toString());

        JLabel invoiceIdLabel = new JLabel("Invoice ID");
        JLabel invoiceIdValueLabel = new JLabel(invoice.getId());

        JLabel customerLabel = new JLabel("Customer");
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));

        JLabel customerIdLabel = new JLabel(invoice.getCustomer().getId());
        JLabel customerNameLabel = new JLabel(invoice.getCustomer().getName());
        JLabel customerEmailLabel = new JLabel(invoice.getCustomer().getEmail());

        customerPanel.add(customerLabel);
        customerPanel.add(customerIdLabel);
        customerPanel.add(customerNameLabel);
        customerPanel.add(customerEmailLabel);

        JLabel itemLabel = new JLabel("Items");
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));

        for (Item item : invoice.getItemsMap().keySet()) {
            String itemId = item.getId();
            String itemName = item.getName();
            String itemPrice = String.valueOf(item.getPrice());

            JLabel itemIdLabel = new JLabel(itemId);
            JLabel itemNameLabel = new JLabel(itemName);
            JLabel itemPriceLabel = new JLabel("$ " + itemPrice);
            JLabel itemQuantityLabel = new JLabel("x " + invoice.getItemsMap().get(item));

            itemPanel.add(itemIdLabel);
            itemPanel.add(itemNameLabel);
            itemPanel.add(itemPriceLabel);
            itemPanel.add(itemQuantityLabel);
            itemPanel.add(Box.createVerticalStrut(10));
        }

        JLabel totalAmountLabel = new JLabel("Total Amount");
        JLabel totalAmountValueLabel = new JLabel(String.valueOf(invoice.getTotalAmount()));

        JButton downloadButton = new JButton("Download");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(dateLabel, gbc);

        gbc.gridx++;
        add(dateValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(invoiceIdLabel, gbc);

        gbc.gridx++;
        add(invoiceIdValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(customerLabel, gbc);

        gbc.gridx++;
        add(customerPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(itemLabel, gbc);

        gbc.gridx++;
        add(itemPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(totalAmountLabel, gbc);

        gbc.gridx++;
        add(totalAmountValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(downloadButton, gbc);

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Invoice as PDF");
                fileChooser.setSelectedFile(new File(invoice.getId() + ".pdf"));
                int userSelection = fileChooser.showSaveDialog(parentFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!filePath.endsWith(".pdf")) {
                        filePath += ".pdf";
                    }
                    try {
                        createPdf(filePath, invoice);
                        JOptionPane.showMessageDialog(parentFrame, "PDF saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } catch (FileNotFoundException | DocumentException ex) {
                        JOptionPane.showMessageDialog(parentFrame, "Error saving PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void createPdf(String filePath, Invoice invoice) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font textFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph title = new Paragraph("Invoice Details", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingBefore(10f);
        detailsTable.setSpacingAfter(10f);

        detailsTable.setHeaderRows(1);

        addCellToTable(detailsTable, "Date", textFont, true);
        addCellToTable(detailsTable, invoice.getDate().toString(), textFont, false);

        addCellToTable(detailsTable, "Invoice ID", textFont, true);
        addCellToTable(detailsTable, invoice.getId(), textFont, false);

        addCellToTable(detailsTable, "Customer ID", textFont, true);
        addCellToTable(detailsTable, invoice.getCustomer().getId(), textFont, false);

        addCellToTable(detailsTable, "Customer Name", textFont, true);
        addCellToTable(detailsTable, invoice.getCustomer().getName(), textFont, false);

        addCellToTable(detailsTable, "Customer Email", textFont, true);
        addCellToTable(detailsTable, invoice.getCustomer().getEmail(), textFont, false);

        document.add(detailsTable);

        document.add(new Paragraph("Items", subtitleFont));

        PdfPTable itemTable = new PdfPTable(4);
        itemTable.setWidthPercentage(100);
        itemTable.setSpacingBefore(10f);
        itemTable.setSpacingAfter(10f);
        itemTable.setHeaderRows(1);

        addCellToTable(itemTable, "Item ID", textFont, true);
        addCellToTable(itemTable, "Item Name", textFont, true);
        addCellToTable(itemTable, "Item Price", textFont, true);
        addCellToTable(itemTable, "Quantity", textFont, true);

        for (Item item : invoice.getItemsMap().keySet()) {
            addCellToTable(itemTable, item.getId(), textFont, false);
            addCellToTable(itemTable, item.getName(), textFont, false);
            addCellToTable(itemTable, "$ " + String.format("%.2f", item.getPrice()), textFont, false);
            addCellToTable(itemTable, String.valueOf(invoice.getItemsMap().get(item)), textFont, false);
        }

        document.add(itemTable);

        document.add(new Paragraph(" "));

        Paragraph totalAmount = new Paragraph("Total Amount: $" + String.format("%.2f", invoice.getTotalAmount()), subtitleFont);
        totalAmount.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalAmount);

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
