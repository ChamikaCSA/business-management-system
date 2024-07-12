package entities;

import java.util.Date;

public class Payment {
    private String id;
    private Invoice invoice;
    private double amount;
    private Date paymentDate;
    private String paymentMethod;

    public Payment() {
    }

    public Payment(String id, Invoice invoice, double amount, Date paymentDate, String paymentMethod) {
        this.id = id;
        this.invoice = invoice;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return STR."Payment{id='\{id}\{'\''}, invoice=\{invoice}, amount=\{amount}, paymentDate=\{paymentDate}, paymentMethod='\{paymentMethod}\{'\''}\{'}'}";
    }
}