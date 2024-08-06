package models;

import java.util.Date;

public class ScaleLicense {
    private String id;
    private String licenseType;
    private Date issuedDate;
    private Date expirationDate;
    private Customer customer;
    private String Status;

    public ScaleLicense() {
    }

    public ScaleLicense(String id, String licenseType, Date issuedDate, Date expirationDate, Customer customer, String Status) {
        this.id = id;
        this.licenseType = licenseType;
        this.issuedDate = issuedDate;
        this.expirationDate = expirationDate;
        this.customer = customer;
        this.Status = Status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    @Override
    public String toString() {
        if (id == null) {
            return "New " + getClass().getSimpleName();
        }
        return id + " : " + licenseType + " (" + issuedDate + " - " + expirationDate + ")" + " - " + customer;
    }
}
