package wealth.wealthStructure;


import wealth.commonstructure.ModuleClass;

/**
 * Created by Administrator on 9/18/13.
 */
public class StructBondEuityDepositoryRow {

    private char charCompanyName[];    //1.---CompanyName---CHAR---50bytes---
    private int intQty;    //2.---Qty---INT---4bytes---
    private double longAvgPurchasePrice;    //3.---AvgPurchasePrice---LONG---8bytes---
    private double longPurchaseValue;    //4.---PurchaseValue---LONG---8bytes---
    private double longCMP;    //5.---CMP---LONG---8bytes---
    private double longCurrentValue;    //6.---CurrentValue---LONG---8bytes---
    private double longGainLoss;    //7.---GainLoss---LONG---8bytes---
    private char charISINNo[];    //1.---CompanyName---CHAR---20bytes---
    private int bseScripCode = 0;
    private int nseScripCode = 0;
    private String holdingType = "";
    private double prevDayGainLoss;

    private float lastRate = 0;
    private float prevClose = 0;

    public StructBondEuityDepositoryRow(byte[] data, int dataLength) {

        int length = 50;
        int count = 0;
        charCompanyName = new char[50];
        for (int i = 0; i < length; i++) {
            charCompanyName[i] = (char) data[count];
            count++;
        }
        length = 4;
        byte[] qty = new byte[length];
        for (int i = 0; i < length; i++) {
            qty[i] = data[count];
            count++;
        }
        intQty = ModuleClass.byteToint(qty, length);
        length = 8;
        byte[] avgPurchasePrice = new byte[length];

        for (int i = 0; i < length; i++) {
            avgPurchasePrice[i] = data[count];
            count++;
        }

        longAvgPurchasePrice = ModuleClass.byteTodouble(avgPurchasePrice, length);
        length = 8;
        byte[] purchaseValue = new byte[length];

        for (int i = 0; i < length; i++) {
            purchaseValue[i] = data[count];
            count++;
        }

        longPurchaseValue = ModuleClass.byteTodouble(purchaseValue, length);
        length = 8;
        byte[] cMP = new byte[length];

        for (int i = 0; i < length; i++) {
            cMP[i] = data[count];
            count++;
        }

        longCMP = ModuleClass.byteTodouble(cMP, length);
        length = 8;
        byte[] currentValue = new byte[length];

        for (int i = 0; i < length; i++) {
            currentValue[i] = data[count];
            count++;
        }

        longCurrentValue = ModuleClass.byteTodouble(currentValue, length);
        length = 8;
        byte[] gainLoss = new byte[length];

        for (int i = 0; i < length; i++) {
            gainLoss[i] = data[count];
            count++;
        }
        longGainLoss = ModuleClass.byteTodouble(gainLoss, length);
        length = 20;
        charISINNo = new char[length];
        for (int i = 0; i < length; i++) {
            charISINNo[i] = (char) data[count];
            count++;
        }

        if(dataLength > 114) {
            length = 4;
            byte[] scripC = new byte[length];
            for (int i = 0; i < length; i++) {
                scripC[i] = data[count];
                count++;
            }
            bseScripCode = ModuleClass.byteToint(scripC, length);
        }
        if(dataLength > 118) {
            length = 4;
            byte[] scripC = new byte[length];
            for (int i = 0; i < length; i++) {
                scripC[i] = data[count];
                count++;
            }
            nseScripCode = ModuleClass.byteToint(scripC, length);

            length = 5;
            char hType[] = new char[length];
            for (int i = 0; i < length; i++) {
                hType[i] = (char) data[count];
                count++;
            }
            holdingType = new String(hType).trim();
        }
        if(data.length > count){
            length = 8;
            byte[] prVGL = new byte[length];
            for (int i = 0; i < length; i++) {
                prVGL[i] = data[count];
                count++;
            }
            prevDayGainLoss = ModuleClass.byteTodouble(prVGL, length);
        }
    }

    public String getCompanyName() {
        return new String(charCompanyName).trim();
    }
    public String getCompanyNameForShortLong() {
        if(nseScripCode  > 0 ){
            return "NE-"+getCompanyName();
        }else{
            return "BE-"+getCompanyName();
        }
    }

    public void setCompanyName(char[] value) {
        charCompanyName = value;
    }

    public int getQty() {
        return intQty;
    }

    public int getBseScripCode() {
        return bseScripCode;
    }
    public int getNseScripCode() {
        return nseScripCode;
    }
    public int getScripCodeForRateUpdate() {
        if (nseScripCode > 0) {
            return nseScripCode;
        } else {
            return bseScripCode;
        }
    }
    public String getHoldingType() {
        return holdingType;
    }


    public void setQty(int value) {
        intQty = value;
    }

    public double getAvgPurchasePrice() {
        return longAvgPurchasePrice;
    }

    public void setAvgPurchasePrice(long value) {
        longAvgPurchasePrice = value;
    }

    public double getPurchaseValue() {
        return longPurchaseValue;
    }

    public void setPurchaseValue(long value) {
        longPurchaseValue = value;
    }

    public double getCMP() {
        return (lastRate>0?lastRate:longCMP);
    }

    public void setCMP(double value) {
        longCMP = value;
    }
    public void setLastrate(float value) {
        lastRate = value;
    }
    public void setPrevClose(float value) {
        prevClose = value;
    }

    public double getCurrentValue() {
        return lastRate>0?(getQty()*lastRate):longCurrentValue;
    }

    public void setCurrentValue(long value) {
        longCurrentValue = value;
    }

    public double getGainLoss() {
        return (longAvgPurchasePrice>0?(lastRate>0?(getCurrentValue() - getPurchaseValue()):longGainLoss):0);
    }
    public double getPrevDayGainLoss() {
        return ((lastRate>0 && prevClose>0)?(getCurrentValue() - getPrevDayValue()):((prevDayGainLoss == longCurrentValue)?0:prevDayGainLoss));
        //return (prevDayGainLoss);
    }
    public double getPrevDayValue() {
        return  (getQty()*prevClose);
    }
    public void setGainLoss(long value) {
        longGainLoss = value;
    }

    public String getISINNo() {
        return new String(charISINNo).trim();
    }

    public void setISINNo(char[] value) {
        charISINNo = value;
    }

    public float getLastRate() {
        return lastRate;
    }
}
