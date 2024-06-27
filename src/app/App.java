package app;

import entities.*;
import services.*;
import utils.*;

import java.util.*;

public class App {
    public static void main(String[] args) {
        ItemService itemService = new ItemService();
        SupplierService supplierService = new SupplierService();
        CustomerService customerService = new CustomerService();
        InvoiceService invoiceService = new InvoiceService();
        StockService stockService = new StockService(itemService);
        AdminService adminService = new AdminService(itemService, supplierService, customerService, invoiceService);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            try {
                System.out.println("\nWelcome to the Inventory Management System");
                System.out.println("1. Worker");
                System.out.println("2. Admin");
                System.out.println("0. Exit");
                int choice = UserInput.getIntInput(scanner, "Select role: ", 0, 2);

                switch (choice) {
                    case 1:
                        workerMenu(scanner, itemService, supplierService, customerService, invoiceService, stockService);
                        break;
                    case 2:
                        adminMenu(scanner, adminService, itemService, supplierService, customerService, invoiceService);
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
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

    private static void workerMenu(Scanner scanner, ItemService itemService, SupplierService supplierService,
                                   CustomerService customerService, InvoiceService invoiceService,
                                   StockService stockService) {
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
                System.out.println("0. Back");

                int choice = UserInput.getIntInput(scanner, "Select option: ", 0, 6);

                switch (choice) {
                    case 1:
                        Item item = new Item();
                        item.setId(UserInput.getStringInput(scanner, "Enter Item ID: "));
                        item.setName(UserInput.getStringInput(scanner, "Enter Item Name: "));
                        item.setPrice(UserInput.getDoubleInput(scanner, "Enter Item Price: ", 0, Double.MAX_VALUE));
                        item.setQuantity(UserInput.getIntInput(scanner, "Enter Item Quantity: ", 0, Integer.MAX_VALUE));
                        itemService.registerItem(item);
                        System.out.println("Item registered successfully.");
                        break;
                    case 2:
                        Supplier supplier = new Supplier();
                        supplier.setId(UserInput.getStringInput(scanner, "Enter Supplier ID: "));
                        supplier.setName(UserInput.getStringInput(scanner, "Enter Supplier Name: "));
                        supplier.setEmail(UserInput.getStringInput(scanner, "Enter Supplier Email: ", UserInput.EMAIL_REGEX));
                        supplierService.registerSupplier(supplier);
                        System.out.println("Supplier registered successfully.");
                        break;
                    case 3:
                        Customer customer = new Customer();
                        customer.setId(UserInput.getStringInput(scanner, "Enter Customer ID: "));
                        customer.setName(UserInput.getStringInput(scanner, "Enter Customer Name: "));
                        customer.setEmail(UserInput.getStringInput(scanner, "Enter Customer Email: ", UserInput.EMAIL_REGEX));
                        customerService.registerCustomer(customer);
                        System.out.println("Customer registered successfully.");
                        break;
                    case 4:
                        Invoice invoice = new Invoice();
                        invoice.setId(UUID.randomUUID().toString());
                        String customerId = UserInput.getStringInput(scanner, "Enter Customer ID for Invoice: ");
                        Customer invoiceCustomer = customerService.getCustomerById(customerId);
                        if (invoiceCustomer == null) {
                            System.out.println("Customer not found.");
                            break;
                        }
                        invoice.setCustomer(invoiceCustomer);
                        List<Item> items = new ArrayList<>();
                        String addMoreItems;
                        do {
                            String itemId = UserInput.getStringInput(scanner, "Enter Item ID: ");
                            Item invoiceItem = itemService.getItemById(itemId);
                            if (invoiceItem != null) {
                                items.add(invoiceItem);
                            } else {
                                System.out.println("Item not found.");
                            }
                            addMoreItems = UserInput.getStringInput(scanner, "Add more items? (yes/no): ");
                        } while (addMoreItems.equalsIgnoreCase("yes"));
                        invoice.setItems(items);
                        invoice.setDate(new Date());
                        invoice.setTotalAmount(items.stream().mapToDouble(Item::getPrice).sum());
                        invoiceService.registerInvoice(invoice);
                        System.out.println("Invoice created successfully.");
                        EmailSender.sendEmail(invoice.getCustomer().getEmail(), "Invoice", "Your invoice details: " + invoice);
                        break;
                    case 5:
                        String stockItemId = UserInput.getStringInput(scanner, "Enter Item ID to Update Stock: ");
                        Item stockItem = itemService.getItemById(stockItemId);
                        if (stockItem == null) {
                            System.out.println("Item not found.");
                            break;
                        }
                        int quantity = UserInput.getIntInput(scanner, "Enter Quantity to Add: ", 0, Integer.MAX_VALUE);
                        stockService.updateStock(stockItemId, quantity);
                        System.out.println("Stock updated successfully.");
                        break;
                    case 6:
                        GoodsReceiveNote grn = new GoodsReceiveNote();
                        grn.setId(UUID.randomUUID().toString());
                        String grnSupplierId = UserInput.getStringInput(scanner, "Enter Supplier ID: ");
                        Supplier grnSupplier = supplierService.getSupplierById(grnSupplierId);
                        if (grnSupplier == null) {
                            System.out.println("Supplier not found.");
                            break;
                        }
                        grn.setSupplier(grnSupplier);
                        String grnItemId = UserInput.getStringInput(scanner, "Enter Item ID: ");
                        Item grnItem = itemService.getItemById(grnItemId);
                        if (grnItem == null) {
                            System.out.println("Item not found.");
                            break;
                        }
                        grn.setItem(grnItem);
                        grn.setReceiveDate(new Date());
                        int receivedQuantity = UserInput.getIntInput(scanner, "Enter Quantity Received: ", 0, Integer.MAX_VALUE);
                        grn.setQuantity(receivedQuantity);
                        stockService.updateStock(grnItemId, receivedQuantity);
                        System.out.println("Goods Receive Note created and stock updated successfully.");
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Input error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void adminMenu(Scanner scanner, AdminService adminService, ItemService itemService,
                                  SupplierService supplierService, CustomerService customerService,
                                  InvoiceService invoiceService) {
        boolean exit = false;

        while (!exit) {
            try {
                System.out.println("\nAdmin Menu");
                System.out.println("1. Add User");
                System.out.println("2. Update User");
                System.out.println("3. Delete User");
                System.out.println("4. View Reports");
                System.out.println("5. Next Month Sales Forecasting");
                System.out.println("6. Income and Sales Analysis");
                System.out.println("0. Back");

                int choice = UserInput.getIntInput(scanner, "Select option: ", 0, 6);

                switch (choice) {
                    case 1:
                        String userId = UserInput.getStringInput(scanner, "Enter User ID: ");
                        String userName = UserInput.getStringInput(scanner, "Enter User Name: ");
                        String userEmail = UserInput.getStringInput(scanner, "Enter User Email: ", UserInput.EMAIL_REGEX);
                        String userType = UserInput.getStringInput(scanner, "Enter User Type (customer/supplier): ");
                        if (!userType.equalsIgnoreCase("customer") && !userType.equalsIgnoreCase("supplier")) {
                            System.out.println("Invalid user type. Must be 'customer' or 'supplier'.");
                            break;
                        }
                        adminService.addUser(userId, userName, userEmail, userType);
                        System.out.println("User added successfully.");
                        break;
                    case 2:
                        String updateUserId = UserInput.getStringInput(scanner, "Enter User ID to Update: ");
                        String updateUserName = UserInput.getStringInput(scanner, "Enter New User Name: ");
                        String updateUserEmail = UserInput.getStringInput(scanner, "Enter New User Email: ", UserInput.EMAIL_REGEX);
                        String updateUserType = UserInput.getStringInput(scanner, "Enter User Type (customer/supplier): ");
                        if (!updateUserType.equalsIgnoreCase("customer") && !updateUserType.equalsIgnoreCase("supplier")) {
                            System.out.println("Invalid user type. Must be 'customer' or 'supplier'.");
                            break;
                        }
                        adminService.updateUser(updateUserId, updateUserName, updateUserEmail, updateUserType);
                        System.out.println("User updated successfully.");
                        break;
                    case 3:
                        String deleteUserId = UserInput.getStringInput(scanner, "Enter User ID to Delete: ");
                        String deleteUserType = UserInput.getStringInput(scanner, "Enter User Type (customer/supplier): ");
                        if (!deleteUserType.equalsIgnoreCase("customer") && !deleteUserType.equalsIgnoreCase("supplier")) {
                            System.out.println("Invalid user type. Must be 'customer' or 'supplier'.");
                            break;
                        }
                        adminService.deleteUser(deleteUserId, deleteUserType);
                        System.out.println("User deleted successfully.");
                        break;
                    case 4:
                        adminService.viewReports();
                        break;
                    case 5:
                        adminService.nextMonthSalesForecast();
                        break;
                    case 6:
                        adminService.incomeAndSalesAnalysis();
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Input error: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
