package services;

import entities.Customer;

import java.util.HashMap;
import java.util.Map;

public class CustomerService {
    private Map<String, Customer> customerRegistry = new HashMap<>();

    public void registerCustomer(Customer customer) {
        customerRegistry.put(customer.getId(), customer);
    }

    public Customer getCustomerById(String id) {
        return customerRegistry.get(id);
    }

    public Map<String, Customer> getCustomerRegistry() {
        return customerRegistry;
    }

    public void setCustomerRegistry(Map<String, Customer> customerRegistry) {
        this.customerRegistry = customerRegistry;
    }
}
