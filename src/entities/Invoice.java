package entities;

import java.util.Date;
import java.util.Map;

public class Invoice {
    private String id;
    private Customer customer;
    private Map<Item, Integer> itemsMap;
    private Date date;
    private double totalAmount;

    public Invoice() {}

    public Invoice(String id, Customer customer, Map<Item, Integer> itemsMap, Date date, double totalAmount) {
        this.id = id;
        this.customer = customer;
        this.itemsMap = itemsMap;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Map<Item, Integer> getItemsMap() {
        return itemsMap;
    }

    public void setItemsMap(Map<Item, Integer> itemsMap) {
        this.itemsMap = itemsMap;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return STR."Invoice{id='\{id}\{'\''}, customer=\{customer}, itemsMap=\{itemsMap}, date=\{date}, totalAmount=\{totalAmount}\{'}'}";
    }
}
