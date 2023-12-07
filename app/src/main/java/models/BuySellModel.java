package models;

import enums.eDelvIntra;

/**
 * Created by XTREMSOFT on 12/23/2016.
 */
public class BuySellModel {
    private boolean show = false;
    private String scriptName = "";
    private String quickOrderType = "" ;
    private int scriptCode = 0;
    private String orderType = "";
    private String qty = "";
    private String price = "";
    private String discQty = "";
    private String tiggerPrice = "";
    private boolean isMkt = true;
    private boolean isIOC = false;
    private boolean isStopLoss = false;
    private int exchange = -1;
    private eDelvIntra delvIntra = eDelvIntra.DELIVERY;

    public eDelvIntra isIntraDay() {
        return delvIntra;
    }

    public void setIntraDay(eDelvIntra delvIntra) {
        this.delvIntra = delvIntra;
    }

    public int getExchange() {
        return exchange;
    }

    public void setExchange(int exchange) {
        this.exchange = exchange;
    }

    public String getQuickOrderType() {
        return quickOrderType;
    }

    public void setQuickOrderType(String quickOrderType) {
        this.quickOrderType = quickOrderType;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public int getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(int scriptCode) {
        this.scriptCode = scriptCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDiscQty() {
        return discQty;
    }

    public void setDiscQty(String discQty) {
        this.discQty = discQty;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTiggerPrice() {
        return tiggerPrice;
    }

    public void setTiggerPrice(String tiggerPrice) {
        this.tiggerPrice = tiggerPrice;
    }

    public boolean isMkt() {
        return isMkt;
    }

    public void setMkt(boolean mkt) {
        isMkt = mkt;
    }

    public boolean isIOC() {
        return isIOC;
    }

    public void setIOC(boolean IOC) {
        isIOC = IOC;
    }

    public boolean isStopLoss() {
        return isStopLoss;
    }

    public void setStopLoss(boolean stopLoss) {
        isStopLoss = stopLoss;
    }
}
