package models;

import java.util.ArrayList;

/**
 * Created by XTREMSOFT on 10/20/2016.
 */
public class DisplaysettingModel {

    public boolean showindices;
    public String apptextsize;
    public String ammount_inWealth;
    public boolean isBSE;
    public boolean isNSE;

    public DisplaysettingModel(boolean showindices, String apptextsize, String ammount_inWealth, boolean isBSE, boolean isNSE) {
        this.showindices = showindices;
        this.apptextsize = apptextsize;
        this.ammount_inWealth = ammount_inWealth;
        this.isBSE = isBSE;
        this.isNSE = isNSE;
    }

    public boolean isShowindices() {
        return showindices;
    }

    public String getApptextsize() {
        return apptextsize;
    }

    public String getAmmount_inWealth() {
        return ammount_inWealth;
    }

    public boolean isBSE() {
        return isBSE;
    }

    public boolean isNSE() {
        return isNSE;
    }
}
