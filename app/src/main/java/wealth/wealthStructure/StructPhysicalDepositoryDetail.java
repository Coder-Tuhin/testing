package wealth.wealthStructure;


import java.util.ArrayList;
import java.util.Collections;

import wealth.commonstructure.ModuleClass;

/**
 * Created by Admin on 22/05/14.
 */

public class StructPhysicalDepositoryDetail {

    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private StructPhysicalDepositoryRow[] physicalEquityRow;
    private int noOfRowAdd = 0;

    public StructPhysicalDepositoryDetail(byte[] data, int dataLength) {

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
        length = 88;
        physicalEquityRow = new StructPhysicalDepositoryRow[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                count++;
            }
            physicalEquityRow[i] = new StructPhysicalDepositoryRow(brData, length);
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

    public StructPhysicalDepositoryRow[] getPhysicalDepositoryRows() {
        return physicalEquityRow;
    }
}
