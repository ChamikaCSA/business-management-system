package entities;

import java.util.Date;

public class GoodsReceiveNote {
    private String id;
    private Supplier supplier;
    private Item item;
    private Date receivedDate;
    private int quantity;

    public GoodsReceiveNote() {}

    public GoodsReceiveNote(String id, Supplier supplier, Item item, Date receivedDate, int quantity) {
        this.id = id;
        this.supplier = supplier;
        this.item = item;
        this.receivedDate = receivedDate;
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

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
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
                ", receivedDate=" + receivedDate +
                ", quantity=" + quantity +
                '}';
    }
}
