package entities;

import java.util.Date;

public class GoodsReceiveNote {
    private String id;
    private Supplier supplier;
    private Item item;
    private Date receiveDate;
    private int quantity;

    public GoodsReceiveNote() {}

    public GoodsReceiveNote(String id, Supplier supplier, Item item, Date receiveDate, int quantity) {
        this.id = id;
        this.supplier = supplier;
        this.item = item;
        this.receiveDate = receiveDate;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "GoodsReceiveNote{" +
                "id='" + id + '\'' +
                ", supplier=" + supplier +
                ", item=" + item +
                ", receiveDate=" + receiveDate +
                ", quantity=" + quantity +
                '}';
    }
}
