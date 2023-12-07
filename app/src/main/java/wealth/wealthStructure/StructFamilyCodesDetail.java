package wealth.wealthStructure;

import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.HashMap;

import wealth.commonstructure.ModuleClass;

/**
 * Created by xtremsoft on 7/26/17.
 */

public class StructFamilyCodesDetail {

    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private String[] familyCodes;

    public StructFamilyCodesDetail(){
    }
    public void setStructure(byte[] data, int dataLength) {

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
        length = 110;
        familyCodes = new String[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                count++;
            }
            familyCodes[i] = new String(brData).trim();
        }
    }

    public short getNoOfRow() {
        //GlobalClass.log("noofRow get : " + shortNoOfRow);
        return shortNoOfRow;
    }

    public void setNoOfRow(short value) {
        shortNoOfRow = value;
    }
    public String[] getfamilyCodes(){
        return familyCodes;
    }
}
