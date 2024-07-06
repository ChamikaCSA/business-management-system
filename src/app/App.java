package app;

import GUI.AppGUI;
import entities.*;
import services.*;
import utils.*;

import javax.swing.*;
import java.util.*;

public class App {
    public static void main(String[] args) {
        ItemService itemService = new ItemService();
        SupplierService supplierService = new SupplierService();
        CustomerService customerService = new CustomerService();
        InvoiceService invoiceService = new InvoiceService();
        StockService stockService = new StockService();
        UserService userService = new UserService();
        GoodsReceiveNoteService goodsReceiveNoteService = new GoodsReceiveNoteService();

        Scanner scanner = new Scanner(System.in);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppGUI();
            }
        });

        boolean loggedIn = false;
        String userType  = null;

        while (!loggedIn) {
            try {
                System.out.println("Login to Inventory Management System");
                String email = UserInput.getStringInput(scanner, "Enter email: ");
                String password = UserInput.getStringInput(scanner, "Enter password: ");

                userType = userService.authenticateUser(email, password);

                if (userType != null) {
                    System.out.println("Login successful.");
                    loggedIn = true;

                    if (userType.equalsIgnoreCase("admin")) {
                        adminMenu(scanner, userService, itemService, supplierService, customerService, invoiceService, stockService, goodsReceiveNoteService);
                    } else if (userType.equalsIgnoreCase("worker")) {
                        workerMenu(scanner, userService, itemService, supplierService, customerService, invoiceService, stockService, goodsReceiveNoteService);
                    } else {
                        System.out.println("Unknown user type. Exiting...");
                    }

                } else {
                    System.out.println("Invalid username or password. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Input error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void workerMenu(Scanner scanner, UserService userService, ItemService itemService, SupplierService supplierService,
                                   CustomerService customerService, InvoiceService invoiceService,
                                   StockService stockService, GoodsReceiveNoteService goodsReceiveNoteService) {
        boolean exit = false;

        while (!exit) {
            try {
                System.out.println("\nWorker Menu");
                System.out.println("1. Register Item");
                System.out.println("2. Register Supplier");
                System.out.println("3. Register Customer");
                System.out.println("4. Create Invoice");
                System.out.println("5. Stock Maintenance");
                System.out.println("6. Goods Receive Notes");
                System.out.println("7. View Income Details");
                System.out.println("8. Renew Scale License");
                System.out.println("0. Back");

                int choice = UserInput.getIntInput(scanner, "Select option: ", 0, 8);

                switch (choice) {
                    case 1:
                        registerItem(scanner, itemService);
                        break;
                    case 2:
                        registerSupplier(scanner, supplierService);
                        break;
                    case 3:
                        registerCustomer(scanner, customerService);
                        break;
                    case 4:
                        createInvoice(scanner, itemService, customerService, invoiceService);
                        break;
                    case 5:
                        stockMaintenance(scanner, itemService, stockService);
                        break;
                    case 6:
                        goodsReceiveNotes(scanner, itemService, supplierService, goodsReceiveNoteService, stockService);
                        break;
                    case 7:
                        viewIncomeDetails(userService);
                        break;
                    case 8:
                        renewScaleLicense(scanner, userService);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println(STR."Input error: \{e.getMessage()}");
                scanner.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println(STR."An error occurred: \{e.getMessage()}");
            }
        }
    }

    private static void adminMenu(Scanner scanner, UserService userService, ItemService itemService,
                                  SupplierService supplierService, CustomerService customerService,
                                  InvoiceService invoiceService, StockService stockService, GoodsReceiveNoteService goodsReceiveNoteService) {
        boolean exit = false;

        while (!exit) {
            try {
                System.out.println("\nAdmin Menu");
                System.out.println("1. Register Item");
                System.out.println("2. Register Supplier");
                System.out.println("3. Register Customer");
                System.out.println("4. Create Invoice");
                System.out.println("5. Stock Maintenance");
                System.out.println("6. Goods Receive Notes");
                System.out.println("7. View Income Details");
                System.out.println("8. Renew Scale License");
                System.out.println("9. Add User");
                System.out.println("10. Update User");
                System.out.println("11. Delete User");
                System.out.println("12. View Reports");
                System.out.println("13. Next Month Sales Forecasting");
                System.out.println("14. Income and Sales Analysis");
                System.out.println("0. Back");

                int choice = UserInput.getIntInput(scanner, "Select option: ", 0, 14);

                switch (choice) {
                    case 1:
                        registerItem(scanner, itemService);
                        break;
                    case 2:
                        registerSupplier(scanner, supplierService);
                        break;
                    case 3:
                        registerCustomer(scanner, customerService);
                        break;
                    case 4:
                        createInvoice(scanner, itemService, customerService, invoiceService);
                        break;
                    case 5:
                        stockMaintenance(scanner, itemService, stockService);
                        break;
                    case 6:
                        goodsReceiveNotes(scanner, itemService, supplierService, goodsReceiveNoteService, stockService);
                        break;
                    case 7:
                        viewIncomeDetails(userService);
                        break;
                    case 8:
                        renewScaleLicense(scanner, userService);
                        break;
                    case 9:
                        addUser(scanner, userService);
                        break;
                    case 10:
                        updateUser(scanner, userService);
                        break;
                    case 11:
                        deleteUser(scanner, userService);
                        break;
                    case 12:
                        userService.viewReports();
                        break;
                    case 13:
                        userService.nextMonthSalesForecast();
                        break;
                    case 14:
                        userService.incomeAndSalesAnalysis();
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println(STR."Input error: \{e.getMessage()}");
                scanner.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println(STR."An error occurred: \{e.getMessage()}");
            }
        }
    }

    private static void registerItem(Scanner scanner, ItemService itemService) {
        Item item = new Item();

        int itemCount = itemService.getItemRegistry().size() + 1;
        String itemId = IDGenerator.generateId("ITEM", itemCount);
        item.setId(itemId);

        item.setName(UserInput.getStringInput(scanner, "Enter Item Name: "));
        item.setPrice(UserInput.getDoubleInput(scanner, "Enter Item Price: ", 0, Double.MAX_VALUE));
        item.setQuantity(UserInput.getIntInput(scanner, "Enter Item Quantity: ", 0, Integer.MAX_VALUE));
        itemService.registerItem(item);
        System.out.println("Item registered successfully.");
    }

    private static void registerSupplier(Scanner scanner, SupplierService supplierService) {
        Supplier supplier = new Supplier();

        int supplierCount = supplierService.getSupplierRegistry().size() + 1;
        String supplierId = IDGenerator.generateId("SUP", supplierCount);
        supplier.setId(supplierId);

        supplier.setName(UserInput.getStringInput(scanner, "Enter Supplier Name: "));
        supplier.setEmail(UserInput.getStringInput(scanner, "Enter Supplier Email: ", UserInput.EMAIL_REGEX));
        supplierService.registerSupplier(supplier);
        System.out.println("Supplier registered successfully.");
    }

    private static void registerCustomer(Scanner scanner, CustomerService customerService) {
        Customer customer = new Customer();

        int customerCount = customerService.getCustomerRegistry().size() + 1;
        String customerId = IDGenerator.generateId("CUST", customerCount);
        customer.setId(customerId);

        customer.setName(UserInput.getStringInput(scanner, "Enter Customer Name: "));
        customer.setEmail(UserInput.getStringInput(scanner, "Enter Customer Email: ", UserInput.EMAIL_REGEX));
        customerService.registerCustomer(customer);
        System.out.println("Customer registered successfully.");
    }

    private static void createInvoice(Scanner scanner, ItemService itemService, CustomerService customerService, InvoiceService invoiceService) {
        Invoice invoice = new Invoice();

        int invoiceCount = invoiceService.getInvoiceRegistry().size() + 1;
        String invoiceId = IDGenerator.generateDatedId("INV", invoiceCount);
        invoice.setId(invoiceId);

        String customerId = UserInput.getStringInput(scanner, "Enter Customer ID for Invoice: ");
        Customer invoiceCustomer = customerService.getCustomerById(customerId);
        if (invoiceCustomer == null) {
            System.out.println("Customer not found.");
            return;
        }
        invoice.setCustomer(invoiceCustomer);
        List<Item> items = new ArrayList<>();
        String addMoreItems;
        do {
            String itemId = UserInput.getStringInput(scanner, "Enter Item ID: ");
            Item invoiceItem = itemService.getItemById(itemId);
            if (invoiceItem != null) {
                int quantity = UserInput.getIntInput(scanner, STR."Enter Quantity of \{invoiceItem.getName()}: ", 1, Integer.MAX_VALUE);
                invoiceItem.setQuantity(quantity);
                items.add(invoiceItem);
            } else {
                System.out.println("Item not found.");
            }
            addMoreItems = UserInput.getStringInput(scanner, "Add more items? (yes/no): ");
        } while (addMoreItems.equalsIgnoreCase("yes"));

        double totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        invoice.setItems(items);
        invoice.setDate(new Date());
        invoice.setTotalAmount(totalAmount);
        invoiceService.registerInvoice(invoice);
        System.out.println("Invoice created successfully.");

        String customerEmail = invoice.getCustomer().getEmail();
        String emailSubject = "Invoice Created";
        String emailContent = STR."Dear \{invoice.getCustomer().getName()},\n\nYour invoice details: \{invoice}";
        EmailSender.sendEmail(customerEmail, emailSubject, emailContent);
    }

    private static void stockMaintenance(Scanner scanner, ItemService itemService, StockService stockService) {
        String stockItemId = UserInput.getStringInput(scanner, "Enter Item ID to Update Stock: ");
        Item stockItem = itemService.getItemById(stockItemId);
        if (stockItem == null) {
            System.out.println("Item not found.");
            return;
        }
        int quantity = UserInput.getIntInput(scanner, "Enter Quantity to Add: ", 0, Integer.MAX_VALUE);
        stockService.updateStock(stockItemId, quantity);
        System.out.println("Stock updated successfully.");
    }

    private static void goodsReceiveNotes(Scanner scanner, ItemService itemService, SupplierService supplierService, GoodsReceiveNoteService goodsReceiveNoteService, StockService stockService) {
        GoodsReceiveNote grn = new GoodsReceiveNote();

        int grnCount = goodsReceiveNoteService.getGoodsReceiveNoteRegistry().size() + 1;
        String grnId = IDGenerator.generateDatedId("GRN", grnCount);
        grn.setId(grnId);

        String grnSupplierId = UserInput.getStringInput(scanner, "Enter Supplier ID: ");
        Supplier grnSupplier = supplierService.getSupplierById(grnSupplierId);
        if (grnSupplier == null) {
            System.out.println("Supplier not found.");
            return;
        }
        grn.setSupplier(grnSupplier);
        String grnItemId = UserInput.getStringInput(scanner, "Enter Item ID: ");
        Item grnItem = itemService.getItemById(grnItemId);
        if (grnItem == null) {
            System.out.println("Item not found.");
            return;
        }
        grn.setItem(grnItem);
        grn.setReceiveDate(new Date());
        int receivedQuantity = UserInput.getIntInput(scanner, "Enter Quantity Received: ", 0, Integer.MAX_VALUE);
        grn.setQuantity(receivedQuantity);
        goodsReceiveNoteService.registerGoodsReceiveNote(grn);
        stockService.updateStock(grnItemId, receivedQuantity);
        System.out.println("Goods Receive Note created and stock updated successfully.");

        String supplierEmail = grn.getSupplier().getEmail();
        String grnSubject = "Goods Receive Note Created";
        String grnContent = STR."Dear \{grn.getSupplier().getName()},\n\nYour goods receive note details: \{grn}";
        EmailSender.sendEmail(supplierEmail, grnSubject, grnContent);
    }

    private static void viewIncomeDetails(UserService userService) {
        double totalIncome = userService.viewIncomeDetails();
        System.out.println(STR."Total Income: \{totalIncome}");
    }

    private static void renewScaleLicense(Scanner scanner, UserService userService) {
        String scaleId = UserInput.getStringInput(scanner, "Enter Scale ID to Renew License: ");
        userService.renewScaleLicense(scaleId);
    }

    private static void addUser(Scanner scanner, UserService userService) {
        User user = new User();

        int userCount = userService.getUserRegistry().size() + 1;
        String userId = IDGenerator.generateId("USER", userCount);
        user.setId(userId);

        user.setName(UserInput.getStringInput(scanner, "Enter User Name: "));
        user.setEmail(UserInput.getStringInput(scanner, "Enter User Email: ", UserInput.EMAIL_REGEX));
        user.setPassword(UserInput.getStringInput(scanner, "Enter User Password: "));
        String userType = UserInput.getStringInput(scanner, "Enter User Type (worker/admin): ");
        if (!userType.equalsIgnoreCase("worker") && !userType.equalsIgnoreCase("admin")) {
            System.out.println("Invalid user type. Must be 'worker' or 'admin'.");
            return;
        }
        user.setType(userType);
        userService.addUser(user);
        System.out.println("User added successfully.");
    }

    private static void updateUser(Scanner scanner, UserService userService) {
        String updateUserId = UserInput.getStringInput(scanner, "Enter User ID to Update: ");
        if (userService.getUserById(updateUserId) == null) {
            System.out.println("User not found.");
            return;
        }
        String updateUserName = UserInput.getStringInput(scanner, "Enter New User Name: ");
        String updateUserEmail = UserInput.getStringInput(scanner, "Enter New User Email: ", UserInput.EMAIL_REGEX);
        String updateUserPassword = UserInput.getStringInput(scanner, "Enter New User Password: ");
        String updateUserType = UserInput.getStringInput(scanner, "Enter User Type (worker/admin): ");
        if (!updateUserType.equalsIgnoreCase("worker") && !updateUserType.equalsIgnoreCase("admin")) {
            System.out.println("Invalid user type. Must be 'worker' or 'admin'.");
            return;
        }
        User updateUser = new User(updateUserId, updateUserName, updateUserEmail, updateUserPassword, updateUserType);
        userService.updateUser(updateUser);
        System.out.println("User updated successfully.");
    }

    private static void deleteUser(Scanner scanner, UserService userService) {
        String deleteUserId = UserInput.getStringInput(scanner, "Enter User ID to Delete: ");
        if (userService.getUserById(deleteUserId) == null) {
            System.out.println("User not found.");
            return;
        }
        userService.deleteUser(deleteUserId);
        System.out.println("User deleted successfully.");
    }
}