package services;

import entities.Invoice;

import java.util.HashMap;
import java.util.Map;

public class InvoiceService {
    private Map<String, Invoice> invoiceRegistry = new HashMap<>();

    public void registerInvoice(Invoice invoice) {
        invoiceRegistry.put(invoice.getId(), invoice);
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRegistry.get(id);
    }

    public Map<String, Invoice> getInvoiceRegistry() {
        return invoiceRegistry;
    }

    public void setInvoiceRegistry(Map<String, Invoice> invoiceRegistry) {
        this.invoiceRegistry = invoiceRegistry;
    }
}
