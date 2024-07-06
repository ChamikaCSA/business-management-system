package entities;

import java.util.Date;

public class ScaleLicense {
    private String id;
    private Date expirationDate;
    private String scaleType;

    public ScaleLicense() {
    }

    public ScaleLicense(String id, Date expirationDate, String scaleType) {
        this.id = id;
        this.expirationDate = expirationDate;
        this.scaleType = scaleType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public String toString() {
        return STR."ScaleLicense{id='\{id}\{'\''}, expirationDate=\{expirationDate}, scaleType='\{scaleType}\{'\''}\{'}'}";
    }
}
