/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wealth.wealthStructure;


import wealth.commonstructure.ModuleClass;

/**
 * @author Tapas Nayak <tapas@xtremsoftindia.com at Xtremsoft Technologies>
 */
public class StructFixedDepositDetail {

    private short shortNoOfRow;    //1.---NoOfRow---SHORT---2bytes---
    private long longTotalAmount;    //2.---TotalAmount---LONG---8bytes---
    private StructFixedDepositRows[] fixedDepositRows;

    private int noOfRowAdd = 0;

    public StructFixedDepositDetail(byte[] data, int dataLength) {

        int length = 2;
        int count = 0;

        byte[] msgLength = new byte[length];

        for (int i = 0; i < length; i++) {
            msgLength[i] = data[count];
            count++;
        }

        short intMsgLength = (short) ModuleClass.byteToshort(msgLength, length);



        byte[] msgCode = new byte[length];

        for (int i = 0; i < length; i++) {
            msgCode[i] = data[count];
            count++;
        }

        short intMsgCode = (short) ModuleClass.byteToshort(msgCode, length);


        byte[] noOfRow = new byte[length];

        for (int i = 0; i < length; i++) {
            noOfRow[i] = data[count];
            count++;
        }

        shortNoOfRow = (short) ModuleClass.byteToshort(noOfRow, length);
        //GlobalClass.log("noofRow : " + shortNoOfRow);
        length = 106;
        fixedDepositRows = new StructFixedDepositRows[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            //GlobalClass.log("Br Data :: ");
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                //System.out.print(brData[j] + "\t");
                count++;
            }
            //GlobalClass.log("");
            if (brData[0] != 0) {
                fixedDepositRows[i] = new StructFixedDepositRows(brData, length);
                noOfRowAdd++;
            } else {
                break;
            }
        }
        length = 8;
        byte[] totalAmount = new byte[length];

        for (int i = 0; i < length; i++) {
            totalAmount[i] = data[count];
            count++;
        }

        longTotalAmount = ModuleClass.byteTolong(totalAmount, length);

    }

    public short getNoOfRow() {
        return shortNoOfRow;
    }

    public void setNoOfRow(short value) {
        shortNoOfRow = value;
    }

    public long getTotalAmount() {
        return longTotalAmount;
    }

    public void setTotalAmount(long value) {
        longTotalAmount = value;
    }

    public StructFixedDepositRows[] getFixedDepositRows() {
        return fixedDepositRows;
    }

}
