package entities;

import java.util.Date;
import java.util.List;

public class Invoice {
    private String id;
    private Customer customer;
    private List<Item> items;
    private Date date;
    private double totalAmount;

    public Invoice() {}

    public Invoice(String id, Customer customer, List<Item> items, Date date, double totalAmount) {
        this.id = id;
        this.customer = customer;
        this.items = items;
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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
        return "Invoice{" +
                "id='" + id + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                ", date=" + date +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
