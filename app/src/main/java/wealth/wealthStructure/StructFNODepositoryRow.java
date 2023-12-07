package wealth.wealthStructure;
import utils.Formatter;
import wealth.commonstructure.ModuleClass;

/**
 * Created by Administrator on 9/18/13.
 */
public class StructFNODepositoryRow {

    private char charCompanyName[];    //1.---CompanyName---CHAR---50bytes---
    private int intQty;    //2.---Qty---INT---4bytes---
    private double avgCost;    //3.---AvgPurchasePrice---LONG---8bytes---
    private int scripCode = 0;

    public StructFNODepositoryRow(byte[] data, int dataLength) {

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

        avgCost = ModuleClass.byteTodouble(avgPurchasePrice, length);
        length = 4;
        byte[] scripC = new byte[length];
        for (int i = 0; i < length; i++) {
            scripC[i] = data[count];
            count++;
        }
        scripCode = ModuleClass.byteToint(scripC, length);
        //avgCost = Formatter.round(avgCost,2);
    }

    public String getCompanyName() {
        return new String(charCompanyName).trim();
    }

    public void setCompanyName(char[] value) {
        charCompanyName = value;
    }

    public int getQty() {
        return intQty;
    }

    public int getScripCode() {
        return scripCode;
    }

    public void setQty(int value) {
        intQty = value;
    }

    public String getAvgCost() {
        return (avgCost == 0)? "$" : Formatter.formatter.format(avgCost);
    }
    public double getAvgCostDouble(){
        return avgCost;
    }
    public String getCurrValueStr(double ltp) {
        return Formatter.roundFormatter.format(ltp * intQty);
    }
    public String getPLonPrevCloseStr(double ltp, double prevClose,int mktLot) {
        return Formatter.roundFormatter.format((ltp - prevClose) * intQty * mktLot);
    }
    public String getPLonCostStr(double ltp,int mktLot) {
        return (avgCost == 0)? "$" : Formatter.roundFormatter.format(intQty * (ltp - avgCost) * mktLot);
    }
}