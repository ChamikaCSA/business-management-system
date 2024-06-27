package services;

import entities.*;
import utils.*;

import java.util.List;
import java.util.stream.Collectors;

public class AdminService {
    private ItemService itemService;
    private SupplierService supplierService;
    private CustomerService customerService;
    private InvoiceService invoiceService;

    public AdminService(ItemService itemService, SupplierService supplierService,
                        CustomerService customerService, InvoiceService invoiceService) {
        this.itemService = itemService;
        this.supplierService = supplierService;
        this.customerService = customerService;
        this.invoiceService = invoiceService;
    }

    public void addUser(String id, String name, String email, String type) {
        if (type.equals("customer")) {
            customerService.registerCustomer(new Customer(id, name, email));
        } else if (type.equals("supplier")) {
            supplierService.registerSupplier(new Supplier(id, name, email));
        }
    }

    public void updateUser(String id, String name, String email, String type) {
        if (type.equals("customer")) {
            Customer customer = customerService.getCustomerById(id);
            if (customer != null) {
                customer.setName(name);
                customer.setEmail(email);
            }
        } else if (type.equals("supplier")) {
            Supplier supplier = supplierService.getSupplierById(id);
            if (supplier != null) {
                supplier.setName(name);
                supplier.setEmail(email);
            }
        }
    }

    public void deleteUser(String id, String type) {
        if (type.equals("customer")) {
            customerService.getCustomerRegistry().remove(id);
        } else if (type.equals("supplier")) {
            supplierService.getSupplierRegistry().remove(id);
        }
    }

    public void viewReports() {
        System.out.println("Item Registry: " + itemService.getItemRegistry());
        System.out.println("Supplier Registry: " + supplierService.getSupplierRegistry());
        System.out.println("Customer Registry: " + customerService.getCustomerRegistry());
        System.out.println("Invoice Registry: " + invoiceService.getInvoiceRegistry());
    }

    public void nextMonthSalesForecast() {
        List<Invoice> invoices = invoiceService.getInvoiceRegistry().values().stream()
                .collect(Collectors.toList());
        double totalSales = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        System.out.println("Next Month Sales Forecast: " + totalSales * 1.1); // Assume a 10% growth
    }

    public void incomeAndSalesAnalysis() {
        List<Invoice> invoices = invoiceService.getInvoiceRegistry().values().stream()
                .collect(Collectors.toList());
        double totalIncome = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        System.out.println("Total Income: " + totalIncome);
    }
}
