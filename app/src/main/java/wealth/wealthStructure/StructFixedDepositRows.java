/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wealth.wealthStructure;


import wealth.commonstructure.ModuleClass;

/**
 * @author Tapas Nayak <tapas@xtremsoftindia.com at Xtremsoft Technologies>
 */
public class StructFixedDepositRows {

    //Length = 106
    private char charName[];    //1.---Name---CHAR---50bytes---
    private char charOption[];    //2.---Option---CHAR---20bytes---
    private short shortTenure;    //3.---Tenure---SHORT---2bytes---
    private int intInterestRate;    //4.---InterestRate---INT---4bytes---
    private char charIssueDate[];    //5.---IssueDate---CHAR---11bytes---
    private char charMaturityDate[];    //6.---MaturityDate---CHAR---11bytes---
    private long longAmount;    //7.---Amount---LONG---8bytes---

    public StructFixedDepositRows(byte[] data, int dataLength) {

        int length = 50;
        int count = 0;
        charName = new char[50];

        for (int i = 0; i < length; i++) {
            charName[i] = (char) data[count];
            count++;
        }
        length = 20;
        charOption = new char[20];

        for (int i = 0; i < length; i++) {
            charOption[i] = (char) data[count];
            count++;
        }
        length = 2;
        byte[] tenure = new byte[length];

        for (int i = 0; i < length; i++) {
            tenure[i] = data[count];
            count++;
        }

        shortTenure = (short) ModuleClass.byteToshort(tenure, length);
        length = 4;
        byte[] interestRate = new byte[length];

        for (int i = 0; i < length; i++) {
            interestRate[i] = data[count];
            count++;
        }

        intInterestRate = ModuleClass.byteToint(interestRate, length);
        length = 11;
        charIssueDate = new char[11];

        for (int i = 0; i < length; i++) {
            charIssueDate[i] = (char) data[count];
            count++;
        }
        length = 11;
        charMaturityDate = new char[11];

        for (int i = 0; i < length; i++) {
            charMaturityDate[i] = (char) data[count];
            count++;
        }
        length = 8;
        byte[] amount = new byte[length];

        for (int i = 0; i < length; i++) {
            amount[i] = data[count];
            count++;
        }

        longAmount = ModuleClass.byteTolong(amount, length);


    }

    public String getName() {
        return new String(charName).trim();
    }

    public void setName(char[] value) {
        charName = value;
    }

    public String getOption() {
        return new String(charOption).trim();
    }

    public void setOption(char[] value) {
        charOption = value;
    }

    public short getTenure() {
        return shortTenure;
    }

    public void setTenure(short value) {
        shortTenure = value;
    }

    public double getInterestRate() {
        return (double) ((double) intInterestRate / 100);
    }

    public void setInterestRate(int value) {
        intInterestRate = value;
    }

    public String getIssueDate() {
        return new String(charIssueDate).trim();
    }

    public void setIssueDate(char[] value) {
        charIssueDate = value;
    }

    public String getMaturityDate() {
        return new String(charMaturityDate).trim();
    }

    public void setMaturityDate(char[] value) {
        charMaturityDate = value;
    }

    public long getAmount() {
        return longAmount;
    }

    public void setAmount(long value) {
        longAmount = value;
    }
}
