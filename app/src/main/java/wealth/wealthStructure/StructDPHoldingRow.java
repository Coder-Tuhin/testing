package wealth.wealthStructure;

import utils.Formatter;

public class StructDPHoldingRow {
    private int hqty;
    private double purchasePrice;
    private String hType;
    private String isin;
    private String companyName;
    private byte isNewWealth;
    //private int edisQty;

    private int edisQtyRequired;


    public StructDPHoldingRow(int hqty, double purchasePrice,String holdingType,String isin,String companyName, byte isNewWealth) {
        this.hqty = hqty;
        this.purchasePrice = purchasePrice;
        this.hType = holdingType;
        this.isin = isin;
        this.companyName = companyName;
        this.isNewWealth = isNewWealth;
        //this.edisQty = 0;
        edisQtyRequired = 0;
    }

    public String getCompanyName() {
        return companyName;
    }
    public String getISIN() {
        return isin;
    }
    public String gethType() {
        return hType;
    }
    public int getHqty() {
        return hqty;
    }
    public byte getIsNewWealth() {
        return isNewWealth;
    }

    public void setHqty(int hqty) {
        this.hqty = hqty;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPurchasePriceStr() {

        return (purchasePrice == 0)? "$" : Formatter.formatter.format(purchasePrice);
    }
    public String getCurrValueStr(double ltp) {
        return Formatter.roundFormatter.format(ltp * hqty);
    }
    public String getCurrPLStr(double ltp) {
        return (purchasePrice == 0)? "$" : Formatter.roundFormatter.format(hqty * (ltp - purchasePrice));
    }

    public int getEdisQtyForSendToEDIS() {
        return (edisQtyRequired);
    }
    public void setEdisQtyForSendToEDIS(int edisQty) {
        this.edisQtyRequired = edisQty;
    }

    /*public int getEdisQty() {
        return edisQty;
    }
    public void setEdisQty(int edisQty) {
        this.edisQty = edisQty;
    }*/
}
