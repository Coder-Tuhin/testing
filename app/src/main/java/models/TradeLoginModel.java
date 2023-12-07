package models;

import java.util.Date;

import utils.Constants;
import utils.DateUtil;

/**
 * Created by XTREMSOFT on 25-May-2018.
 */

public class TradeLoginModel {
    private String usersecret = "";
    private String pin = "";
    private short chkType = 1 ;
    private String dobPan = "";
    private String mpin = "";
    private String saveTime;
    private boolean isDayFirstLogin = false;

    public String getPassword() {
        return usersecret;
    }

    public void setPassword(String password) {
        this.usersecret = password;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public short getChkType() {
        return chkType;
    }

    public void setChkType(short chkType) {
        this.chkType = chkType;
    }

    public String getDobPan() {
        return dobPan;
    }

    public void setDobPan(String dobPan) {
        this.dobPan = dobPan;
    }

    public String getMpin() {
        return mpin;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }

    public Date getSaveDate() {
        return DateUtil.stringTodate(saveTime,Constants.ddMMMyy);
    }

    public void setSaveDate(Date saveTime) {
        this.saveTime = DateUtil.dateFormatter(saveTime,Constants.ddMMMyy);
    }
    /*
    public Date getSaveTime() {
        return DateUtil.stringTodate(saveTime,Constants.DDMMMYY);
    }

    public void setSaveTime(Date saveTime) {
        this.saveTime = DateUtil.dateFormatter(saveTime,Constants.DDMMMYY);
    }*/

    public boolean isDayFirstLogin() {
        return isDayFirstLogin;
    }

    public void setDayFirstLogin(boolean dayFirstLogin) {
        isDayFirstLogin = dayFirstLogin;
    }
}
