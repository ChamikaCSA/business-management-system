package services;

import entities.Supplier;

import java.util.HashMap;
import java.util.Map;

public class SupplierService {
    private Map<String, Supplier> supplierRegistry = new HashMap<>();

    public void registerSupplier(Supplier supplier) {
        supplierRegistry.put(supplier.getId(), supplier);
    }

    public Supplier getSupplierById(String id) {
        return supplierRegistry.get(id);
    }

    public Map<String, Supplier> getSupplierRegistry() {
        return supplierRegistry;
    }

    public void setSupplierRegistry(Map<String, Supplier> supplierRegistry) {
        this.supplierRegistry = supplierRegistry;
    }
}
