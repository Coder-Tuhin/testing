package wealth.wealthStructure;

import utils.Formatter;
import wealth.commonstructure.ModuleClass;

/**
 * Created by Administrator on 1/1/15.
 */
public class StructHoldingShortLongTermRow {


    private char charPurchaseDate[];    //1.---CompanyName---CHAR---50bytes---
    private float intQty;    //2.---Qty---INT---4bytes---
    private double longAvgPurchasePrice;    //3.---AvgPurchasePrice---LONG---8bytes---
    private double longPurchaseValue;    //4.---PurchaseValue---LONG---8bytes---
    private double longCMP;    //5.---CMP---LONG---8bytes---
    private double longCurrentValue;    //6.---CurrentValue---LONG---8bytes---
    private double longGainLoss;    //7.---GainLoss---LONG---8bytes---
    private char charLongShort;    //8.---LongShort---CHAR---1bytes---

    private double lastRate = 0;

    public StructHoldingShortLongTermRow(byte[] data, int dataLength) {

        int length = 11;
        int count = 0;
        charPurchaseDate = new char[11];

        for (int i = 0; i < length; i++) {
            charPurchaseDate[i] = (char) data[count];
            count++;
        }
        length = 4;
        byte[] qty = new byte[length];

        for (int i = 0; i < length; i++) {
            qty[i] = data[count];
            count++;
        }

        intQty = ModuleClass.byteTofloat(qty, length);
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
        charLongShort = (char) data[count++];
    }

    public String getPurchaseDate() {
        return new String(charPurchaseDate).trim();
    }

    public void setPurchaseDate(char[] value) {
        charPurchaseDate = value;
    }

    public char getLongOrShort() {
        return charLongShort;
    }

    public void setLongOrShort(char value) {
        charLongShort = value;
    }

    public float getQty() {
        return intQty;
    }

    public String getQtyStr() {
        if ((intQty * 100) % 100 > 0) {
            return Formatter.toTwoDecimalValue(intQty);
        }
        return (int) Math.round(intQty) + "";
    }

    public void setQty(float value) {
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
        return lastRate > 0 ? lastRate : longCMP;
    }

    public void setCMP(long value) {
        longCMP = value;
    }

    public double getCurrentValue() {
        return lastRate > 0 ? (getQty() * lastRate) : longCurrentValue;
    }

    public void setCurrentValue(long value) {
        longCurrentValue = value;
    }

    public double getGainLoss() {
        return lastRate > 0 ? (getCurrentValue() - getPurchaseValue()) : longGainLoss;
    }

    public void setGainLoss(long value) {
        longGainLoss = value;
    }

    public double getLastRate() {
        return lastRate;
    }

    public void setLastRate(double lastRate) {
        this.lastRate = lastRate;
    }
}
