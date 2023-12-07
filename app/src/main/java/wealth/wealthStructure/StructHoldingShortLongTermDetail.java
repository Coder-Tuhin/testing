package wealth.wealthStructure;

import wealth.commonstructure.ModuleClass;

/**
 * Created by Administrator on 1/1/15.
 */
public class StructHoldingShortLongTermDetail {


    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private StructHoldingShortLongTermRow[] holdingShortLongTermRows;
    private int noOfRowAdd = 0;

    public StructHoldingShortLongTermDetail(byte[] data, int dataLength) {

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
        length = 56;
        holdingShortLongTermRows = new StructHoldingShortLongTermRow[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            //GlobalClass.log("Br Data :: ");
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                //System.out.print(brData[j] + "\t");
                count++;
            }
            holdingShortLongTermRows[i] = new StructHoldingShortLongTermRow(brData, length);
            noOfRowAdd++;
        }
    }

    public short getNoOfRow() {
        return shortNoOfRow;
    }

    public void setNoOfRow(short value) {
        shortNoOfRow = value;
    }

    public StructHoldingShortLongTermRow[] getHoldingShortLongTermRows() {
        return holdingShortLongTermRows;
    }

    public double[] getShortTermTotal() {
        int i = 0;
        double shortTermTotal = 0;
        double longTermTotal = 0;

        while (i < shortNoOfRow) {
            if (holdingShortLongTermRows[i].getPurchaseDate().equalsIgnoreCase("Total")) {
                if (holdingShortLongTermRows[i].getLongOrShort() == 'L' || holdingShortLongTermRows[i].getLongOrShort() == 'l') {
                    longTermTotal = holdingShortLongTermRows[i].getGainLoss();
                } else {
                    shortTermTotal = holdingShortLongTermRows[i].getGainLoss();
                }
            }
            i++;
        }
        return new double[]{shortTermTotal, longTermTotal};
    }

}
