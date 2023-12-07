package models;

public class OrderSaveModel {

    private int scripCode;
    private int qty;
    private String mktType;
    private boolean isStollLoss;

    public OrderSaveModel(int scripCode, int qty, String mktType,boolean isStollLoss) {
        this.scripCode = scripCode;
        this.qty = qty;
        this.mktType = mktType;
        this.isStollLoss = isStollLoss;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getMktType() {
        return mktType;
    }

    public void setMktType(String mktType) {
        this.mktType = mktType;
    }

    public int getScripCode() {
        return scripCode;
    }

    public void setScripCode(int scripCode) {
        this.scripCode = scripCode;
    }

    public boolean isStollLoss() {
        return isStollLoss;
    }

    public void setStollLoss(boolean stollLoss) {
        isStollLoss = stollLoss;
    }
}
