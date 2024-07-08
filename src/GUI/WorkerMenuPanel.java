package GUI;

import entities.*;
import services.*;
import utils.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkerMenuPanel extends JPanel {
    private final CustomerService customerService;
    private final GoodsReceiveNoteService goodsReceiveNoteService;
    private final InvoiceService invoiceService;
    private final ItemService itemService;
    private final ScaleLicenseService scaleLicenseService;
    private final SupplierService supplierService;

    public WorkerMenuPanel(JFrame menuFrame, CustomerService customerService, GoodsReceiveNoteService goodsReceiveNoteService,
                           InvoiceService invoiceService, ItemService itemService, ScaleLicenseService scaleLicenseService,
                           SupplierService supplierService) {
        this.customerService = customerService;
        this.goodsReceiveNoteService = goodsReceiveNoteService;
        this.invoiceService = invoiceService;
        this.itemService = itemService;
        this.scaleLicenseService = scaleLicenseService;
        this.supplierService = supplierService;

        initialize();
    }


    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton registerItemButton = new JButton("Register Item");
        registerItemButton.addActionListener(_ -> registerItem());
        add(registerItemButton, gbc);

        gbc.gridx = 1;
        JButton registerSupplierButton = new JButton("Register Supplier");
        registerSupplierButton.addActionListener(_ -> registerSupplier());
        add(registerSupplierButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JButton registerCustomerButton = new JButton("Register Customer");
        registerCustomerButton.addActionListener(_ -> registerCustomer());
        add(registerCustomerButton, gbc);

        gbc.gridx = 1;
        JButton createInvoiceButton = new JButton("Create Invoice");
        createInvoiceButton.addActionListener(_ -> createInvoice());
        add(createInvoiceButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JButton stockMaintenanceButton = new JButton("Stock Maintenance");
        stockMaintenanceButton.addActionListener(_ -> stockMaintenance());
        add(stockMaintenanceButton, gbc);

        gbc.gridx = 1;
        JButton goodsReceiveNotesButton = new JButton("Goods Receive Notes");
        goodsReceiveNotesButton.addActionListener(_ -> goodsReceiveNotes());
        add(goodsReceiveNotesButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton viewIncomeDetailsButton = new JButton("View Income Details");
        viewIncomeDetailsButton.addActionListener(_ -> viewIncomeDetails());
        add(viewIncomeDetailsButton, gbc);

        gbc.gridx = 1;
        JButton renewScaleLicenseButton = new JButton("Renew Scale License");
        renewScaleLicenseButton.addActionListener(_ -> renewScaleLicense());
        add(renewScaleLicenseButton, gbc);
    }

    private void registerItem() {
        int itemCount = itemService.getItemRegistry().size() + 1;
        String itemId = IDGenerator.generateId("ITEM", itemCount);
        String itemName = JOptionPane.showInputDialog(this, "Enter item name:");
        if (itemName != null && !itemName.isEmpty()) {
            try {
                double itemPrice = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter item price:"));
                int itemQuantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter item quantity:"));

                Item newItem = new Item(itemId, itemName, itemPrice, itemQuantity);
                itemService.registerItem(newItem);
                JOptionPane.showMessageDialog(this, STR."Item registered successfully: \{itemName}");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for price or quantity. Please enter valid numbers.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Item name cannot be empty.");
        }
    }

    private void registerSupplier() {
        int supplierCount = supplierService.getSupplierRegistry().size() + 1;
        String supplierId = IDGenerator.generateId("SUP", supplierCount);
        String supplierName = JOptionPane.showInputDialog(this, "Enter supplier name:");
        if (supplierName != null && !supplierName.isEmpty()) {
            String supplierEmail = JOptionPane.showInputDialog(this, "Enter supplier email:");
            if (supplierEmail != null && !supplierEmail.isEmpty()) {
                Supplier newSupplier = new Supplier(supplierId, supplierName, supplierEmail);
                supplierService.registerSupplier(newSupplier);
                JOptionPane.showMessageDialog(this, STR."Supplier registered successfully: \{supplierName}");
            } else {
                JOptionPane.showMessageDialog(this, "Supplier email cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Supplier name cannot be empty.");
        }
    }

    private void registerCustomer() {
        int customerCount = customerService.getCustomerRegistry().size() + 1;
        String customerId = IDGenerator.generateId("CUST", customerCount);
        String customerName = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (customerName != null && !customerName.isEmpty()) {
            String customerEmail = JOptionPane.showInputDialog(this, "Enter customer email:");
            if (customerEmail != null && !customerEmail.isEmpty()) {
                Customer newCustomer = new Customer(customerId, customerName, customerEmail);
                customerService.registerCustomer(newCustomer);
                JOptionPane.showMessageDialog(this, STR."Customer registered successfully: \{customerName}");
            } else {
                JOptionPane.showMessageDialog(this, "Customer email cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Customer name cannot be empty.");
        }
    }

    private void createInvoice() {
        try {
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
                        int maxQuantity = selectedItem.getQuantity();
                        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity:"));
                        if (quantity > 0 && quantity <= maxQuantity) {
                            selectedItem.setQuantity(quantity);
                            selectedItems.add(selectedItem);
                        } else if (quantity <= 0) {
                            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                        } else {
                            JOptionPane.showMessageDialog(this, STR."Quantity cannot exceed available stock (\{maxQuantity}).");
                        }
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
                if (!selectedItems.isEmpty()) {
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity input. Please enter a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, STR."Error creating invoice: \{e.getMessage()}");
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

        if (selectedStockItem != null) {
            try {
                int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity to add to stock:"));
                if (quantity > 0) {
                    selectedStockItem.setQuantity(selectedStockItem.getQuantity() + quantity);
                    itemService.updateItem(selectedStockItem);
                    JOptionPane.showMessageDialog(this, "Stock quantity updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for quantity. Please enter a valid number.");
            }
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

        if (selectedSupplier != null) {
            Item selectedGRNItem = (Item) JOptionPane.showInputDialog(this,
                    "Select item:",
                    "Goods Receive Notes",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    itemService.getItemRegistry().values().toArray(),
                    null);

            if (selectedGRNItem != null) {
                try {
                    int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter quantity received:"));
                    if (quantity > 0) {
                        GoodsReceiveNote newGRN = new GoodsReceiveNote(grnId, selectedSupplier, selectedGRNItem, new Date(), quantity);
                        goodsReceiveNoteService.registerGoodsReceiveNote(newGRN);
                        selectedGRNItem.setQuantity(selectedGRNItem.getQuantity() + quantity);
                        itemService.updateItem(selectedGRNItem);
                        JOptionPane.showMessageDialog(this, "Goods receive note created successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid input for quantity. Please enter a valid number.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No item selected.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
        }
    }

    private void viewIncomeDetails() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(JOptionPane.showInputDialog(this, "Enter start date (yyyy-MM-dd):"));
            Date endDate = dateFormat.parse(JOptionPane.showInputDialog(this, "Enter end date (yyyy-MM-dd):"));

            double totalIncome = invoiceService.calculateTotalIncome(startDate, endDate);
            JOptionPane.showMessageDialog(this, String.format("Total Income from %s to %s is: $%.2f", dateFormat.format(startDate), dateFormat.format(endDate), totalIncome));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, STR."Error calculating income: \{e.getMessage()}");
        }
    }


    private void renewScaleLicense() {
        int licenseCount = scaleLicenseService.getLicenseRegistry().size() + 1;
        String licenseId = IDGenerator.generateDatedId("SL", licenseCount);
        ScaleLicense[] licenses = scaleLicenseService.getLicenseRegistry().values().toArray(new ScaleLicense[0]);
        ScaleLicense selectedLicense = (ScaleLicense) JOptionPane.showInputDialog(this,
                "Select license to renew:",
                "Renew Scale License",
                JOptionPane.PLAIN_MESSAGE,
                null,
                licenses,
                null);
        if (selectedLicense != null) {
            try {
                selectedLicense = new ScaleLicense(selectedLicense.getId(), selectedLicense.getExpirationDate(), selectedLicense.getScaleType());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date newExpirationDate = dateFormat.parse(JOptionPane.showInputDialog(this, "Enter new expiration date (yyyy-MM-dd):"));
                selectedLicense.setId(licenseId);
                selectedLicense.setExpirationDate(newExpirationDate);
                scaleLicenseService.renewLicense(selectedLicense);
                JOptionPane.showMessageDialog(this, "License renewed successfully.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, STR."Error renewing license: \{e.getMessage()}");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No license selected.");
        }
    }
}


