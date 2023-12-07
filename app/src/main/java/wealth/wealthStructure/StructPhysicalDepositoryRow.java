package wealth.wealthStructure;


import wealth.commonstructure.ModuleClass;

/**
 * Created by Administrator on 9/18/13.
 */
public class StructPhysicalDepositoryRow {

    private char charCompanyName[];    //1.---CompanyName---CHAR---50bytes---
    private char tenure[];    //1.---CompanyName---CHAR---50bytes---
    private char maturityDate[];    //1.---CompanyName---CHAR---50bytes---
    private double amount;    //4.---PurchaseValue---LONG---8bytes---

    public StructPhysicalDepositoryRow(byte[] data, int dataLength) {

        int length = 50;
        int count = 0;
        charCompanyName = new char[length];
        for (int i = 0; i < length; i++) {
            charCompanyName[i] = (char) data[count];
            count++;
        }

        length = 20;
        tenure = new char[length];
        for (int i = 0; i < length; i++) {
            tenure[i] = (char) data[count];
            count++;
        }

        length = 10;
        maturityDate = new char[length];
        for (int i = 0; i < length; i++) {
            maturityDate[i] = (char) data[count];
            count++;
        }

        length = 8;
        byte[] purchaseValue = new byte[length];

        for (int i = 0; i < length; i++) {
            purchaseValue[i] = data[count];
            count++;
        }
        amount = ModuleClass.byteTodouble(purchaseValue, length);
    }

    public String getCompanyName() {
        return new String(charCompanyName).trim();
    }

    public String getTenure() {
        return new String(tenure).trim();
    }

    public String getMaturityDate() {
        return new String(maturityDate).trim();
    }

    public double getAmount() {
        return amount;
    }
}
