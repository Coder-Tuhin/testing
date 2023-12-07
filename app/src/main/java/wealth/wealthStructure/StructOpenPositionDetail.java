/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wealth.wealthStructure;


import wealth.commonstructure.ModuleClass;

/**
 * @author Tapas Nayak <tapas@xtremsoftindia.com at Xtremsoft Technologies>
 */
public class StructOpenPositionDetail {

    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private StructOpenPositionRow[] openPosition;
    private int noOfRowAdd = 0;

    public StructOpenPositionDetail(byte[] data, int dataLength) {

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
        length = 44;
        openPosition = new StructOpenPositionRow[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            //GlobalClass.log("Br Data :: ");
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                //System.out.print(brData[j] + "\t");
                count++;
            }
            openPosition[i] = new StructOpenPositionRow(brData, length);
            noOfRowAdd++;
        }
    }

    public short getNoOfRow() {
        //GlobalClass.log("noofRow get : " + shortNoOfRow);
        return shortNoOfRow;
    }

    public void setNoOfRow(short value) {
        shortNoOfRow = value;
    }

    public StructOpenPositionRow[] getOpenPositionRow() {
        return openPosition;
    }


}
