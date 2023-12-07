package models;

/**
 * Created by XTREMSOFT on 10/7/2016.
 */
public class LoginDetailsModel {
    private String userID = "";
    private String mobileNo = "";
    private String usersecret = "";
    private String clientName = "";
    private String folioNumber = "";
    private boolean isClient = false;
    private boolean isActiveUser = true;
    private int type = 1;
    private String pan = "";
    private boolean isIntradayDelivery = false;
    private boolean isFNOIntradayDelivery = false;
    private boolean isTradeLogin = false;
    private String pin = "";


    public void initializeData(){
        isIntradayDelivery = false;
        isFNOIntradayDelivery = false;
        isTradeLogin = false;
    }

    public boolean isTradeLogin() { return isTradeLogin; }

    public void setTradeLogin(boolean tradeLogin) { isTradeLogin = tradeLogin; }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public boolean isIntradayDelivery() {
        return isIntradayDelivery;
    }

    public void setIntradayDelivery(char intradel) {
        isIntradayDelivery = intradel == 'Y';
    }

    public boolean isFNOIntradayDelivery() {
        return isFNOIntradayDelivery;
    }

    public void setFNOIntradayDelivery(char intradel) {
        isFNOIntradayDelivery = intradel == 'Y';
    }

    public String getUserID() {
        return userID.trim().toUpperCase();//"99212215";//userID.trim().toUpperCase();
    }

    public void setUserID(String userID) {
        this.userID = userID.trim();
    }

    public String getPassword() {
        return usersecret;
    }

    public void setPassword(String password) {
        this.usersecret = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        if(folioNumber.length() > 6) {
            this.folioNumber = folioNumber;
        }
    }

    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean client) {
        isClient = client;
    }

    public boolean isActiveUser() {
        return isActiveUser;
    }

    public void setActiveUser(boolean activeUser) {
        isActiveUser = activeUser;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}