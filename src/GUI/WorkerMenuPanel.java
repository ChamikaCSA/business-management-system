package GUI;

import entities.*;
import services.*;
import utils.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkerMenuPanel extends JPanel {
    private CustomerService customerService;
    private GoodsReceiveNoteService goodsReceiveNoteService;
    private InvoiceService invoiceService;
    private ItemService itemService;
    private ScaleLicenseService scaleLicenseService;
    private StockService stockService;
    private SupplierService supplierService;
    private UserService userService;

    public WorkerMenuPanel(JFrame menuFrame, CustomerService customerService, GoodsReceiveNoteService goodsReceiveNoteService,
                           InvoiceService invoiceService, ItemService itemService, ScaleLicenseService scaleLicenseService,
                            StockService stockService, SupplierService supplierService, UserService userService) {
        this.customerService = customerService;
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.invoiceService = invoiceService;
        this.itemService = itemService;
        this.scaleLicenseService = scaleLicenseService;
        this.stockService = stockService;
        this.supplierService = supplierService;
        this.userService = userService;

        initialize();
    }


    private void initialize() {
        setLayout(new GridLayout(4, 2, 10, 10));

        JButton registerItemButton = new JButton("Register Item");
        registerItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerItem();
            }
        });

        JButton registerSupplierButton = new JButton("Register Supplier");
        registerSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerSupplier();
            }
        });

        JButton registerCustomerButton = new JButton("Register Customer");
        registerCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerCustomer();
            }
        });

        JButton createInvoiceButton = new JButton("Create Invoice");
        createInvoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createInvoice();
            }
        });

        JButton stockMaintenanceButton = new JButton("Stock Maintenance");
        stockMaintenanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stockMaintenance();
            }
        });

        JButton goodsReceiveNotesButton = new JButton("Goods Receive Notes");
        goodsReceiveNotesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goodsReceiveNotes();
            }
        });

        JButton viewIncomeDetailsButton = new JButton("View Income Details");
        viewIncomeDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewIncomeDetails();
            }
        });

        JButton renewScaleLicenseButton = new JButton("Renew Scale License");
        renewScaleLicenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renewScaleLicense();
            }
        });

        add(registerItemButton);
        add(registerSupplierButton);
        add(registerCustomerButton);
        add(createInvoiceButton);
        add(stockMaintenanceButton);
        add(goodsReceiveNotesButton);
        add(viewIncomeDetailsButton);
        add(renewScaleLicenseButton);
    }

    private void registerItem() {
        int itemCount = itemService.getItemRegistry().size() + 1;
        String itemId = IDGenerator.generateId("ITEM", itemCount);
        String itemName = JOptionPane.showInputDialog(this, "Enter item name:");
        double itemPrice = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter item price:"));
        int itemQuantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter item quantity:"));
        if (itemName != null && !itemName.isEmpty()) {
            Item newItem = new Item(itemId, itemName, itemPrice, itemQuantity);
            itemService.registerItem(newItem);
            JOptionPane.showMessageDialog(this, "Item registered successfully: " + itemName);
        } else {
            JOptionPane.showMessageDialog(this, "Item name cannot be empty.");
        }
    }

    private void registerSupplier() {
        int supplierCount = supplierService.getSupplierRegistry().size() + 1;
        String supplierId = IDGenerator.generateId("SUP", supplierCount);
        String supplierName = JOptionPane.showInputDialog(this, "Enter supplier name:");
        String supplierEmail = JOptionPane.showInputDialog(this, "Enter supplier email:");
        if (supplierName != null && !supplierName.isEmpty()) {
            Supplier newSupplier = new Supplier(supplierId, supplierName, supplierEmail);
            supplierService.registerSupplier(newSupplier);
            JOptionPane.showMessageDialog(this, "Supplier registered successfully: " + supplierName);
        } else {
            JOptionPane.showMessageDialog(this, "Supplier name cannot be empty.");
        }
    }

    private void registerCustomer() {
        int customerCount = customerService.getCustomerRegistry().size() + 1;
        String customerId = IDGenerator.generateId("CUST", customerCount);
        String customerName = JOptionPane.showInputDialog(this, "Enter customer name:");
        String customerEmail = JOptionPane.showInputDialog(this, "Enter customer email:");
        if (customerName != null && !customerName.isEmpty()) {
            Customer newCustomer = new Customer(customerId, customerName, customerEmail);
            customerService.registerCustomer(newCustomer);
            JOptionPane.showMessageDialog(this, "Customer registered successfully: " + customerName);
        } else {
            JOptionPane.showMessageDialog(this, "Customer name cannot be empty.");
        }
    }

    private void createInvoice() {
        int invoiceCount = invoiceService.getInvoiceRegistry().size() + 1;
        String invoiceId = IDGenerator.generateDatedId("INV", invoiceCount);
        Customer selectedCustomer = (Customer) JOptionPane.showInputDialog(this,
                "Select customer:",
                "Create Invoice",
                JOptionPane.PLAIN_MESSAGE,
                null,
                customerService.getCustomerRegistry().values().toArray(),
                null);

        if (selectedCustomer != null) {
            List<Item> selectedItems = new ArrayList<>();
            Item[] items = itemService.getItemRegistry().values().toArray(new Item[0]);
            while (true) {
                Item selectedItem = (Item) JOptionPane.showInputDialog(this,
                        "Select item:",
                        "Create Invoice",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        items,
                        null);
                if (selectedItem != null) {
                    int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity:"));
                    selectedItem.setQuantity(quantity);
                    selectedItems.add(selectedItem);
                    int addMore = JOptionPane.showConfirmDialog(this,
                            "Do you want to add more items to the invoice?",
                            "Add More Items",
                            JOptionPane.YES_NO_OPTION);
                    if (addMore == JOptionPane.NO_OPTION) {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (selectedItems != null && !selectedItems.isEmpty()) {
                double totalAmount = calculateTotalAmount(selectedItems);
                Date currentDate = new Date();
                Invoice newInvoice = new Invoice(invoiceId, selectedCustomer, selectedItems, currentDate, totalAmount);
                invoiceService.registerInvoice(newInvoice);
                JOptionPane.showMessageDialog(this, STR."Invoice created successfully for customer: \{selectedCustomer.getName()}");
            } else {
                JOptionPane.showMessageDialog(this, "No items selected for invoice creation.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No customer selected.");
        }
    }


    private double calculateTotalAmount(List<Item> items) {
        double total = 0.0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }

    private void stockMaintenance() {
        Item selectedStockItem = (Item) JOptionPane.showInputDialog(this,
                "Select item to update stock quantity:",
                "Stock Maintenance",
                JOptionPane.PLAIN_MESSAGE,
                null,
                itemService.getItemRegistry().values().toArray(),
                null);
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity to add to stock:"));
        if (selectedStockItem != null) {
            selectedStockItem.setQuantity(selectedStockItem.getQuantity() + quantity);
            stockService.updateStock(selectedStockItem.getId(), quantity);
            JOptionPane.showMessageDialog(this, "Stock quantity updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No item selected.");
        }
    }

    private void goodsReceiveNotes() {
        int grnCount = goodsReceiveNoteService.getGoodsReceiveNoteRegistry().size() + 1;
        String grnId = IDGenerator.generateDatedId("GRN", grnCount);
        Supplier selectedSupplier = (Supplier) JOptionPane.showInputDialog(this,
                "Select supplier:",
                "Goods Receive Notes",
                JOptionPane.PLAIN_MESSAGE,
                null,
                supplierService.getSupplierRegistry().values().toArray(),
                null);
        Item selectedGRNItem = (Item) JOptionPane.showInputDialog(this,
                "Select item:",
                "Goods Receive Notes",
                JOptionPane.PLAIN_MESSAGE,
                null,
                itemService.getItemRegistry().values().toArray(),
                null);
        Date receiveDate = new Date();
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity received:"));
        if (selectedSupplier != null && selectedGRNItem != null) {
            GoodsReceiveNote newGRN = new GoodsReceiveNote(grnId, selectedSupplier, selectedGRNItem, receiveDate, quantity);
            goodsReceiveNoteService.registerGoodsReceiveNote(newGRN);
            stockService.updateStock(selectedGRNItem.getId(), quantity);
            JOptionPane.showMessageDialog(this, "Goods receive note created successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Supplier or item not selected.");
        }
    }

    private void viewIncomeDetails() {
        try {
            String startDateStr = JOptionPane.showInputDialog(this, "Enter start date (yyyy-MM-dd):");
            String endDateStr = JOptionPane.showInputDialog(this, "Enter end date (yyyy-MM-dd):");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            double totalIncome = invoiceService.calculateTotalIncome(startDate, endDate);
            JOptionPane.showMessageDialog(this, "Total Income from " + startDateStr + " to " + endDateStr + " is: $" + totalIncome);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating income: " + e.getMessage());
        }
    }


    private void renewScaleLicense() {
        int licenseCount = scaleLicenseService.getLicenseRegistry().size() + 1;
        String licenseId = IDGenerator.generateDatedId("LIC", licenseCount);
        ScaleLicense[] licenses = scaleLicenseService.getLicenseRegistry().values().toArray(new ScaleLicense[0]);
        ScaleLicense selectedLicense = (ScaleLicense) JOptionPane.showInputDialog(this,
                "Select license to renew:",
                "Renew Scale License",
                JOptionPane.PLAIN_MESSAGE,
                null,
                licenses,
                null);
        String newExpirationDateStr = JOptionPane.showInputDialog(this, "Enter new expiration date (yyyy-MM-dd):");

        if (selectedLicense != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date newExpirationDate = dateFormat.parse(newExpirationDateStr);
                selectedLicense.setId(licenseId);
                selectedLicense.setExpirationDate(newExpirationDate);
                scaleLicenseService.renewLicense(selectedLicense);
                JOptionPane.showMessageDialog(this, "License renewed successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error renewing license: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "No license selected.");
        }
    }
}


