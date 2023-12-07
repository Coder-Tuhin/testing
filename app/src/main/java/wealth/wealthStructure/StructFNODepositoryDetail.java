package wealth.wealthStructure;

import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import utils.PreferenceHandler;
import wealth.commonstructure.ModuleClass;

/**
 * Created by xtremsoft on 7/26/17.
 */

public class StructFNODepositoryDetail {

    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private StructFNODepositoryRow[] bondEquityRow;

    public short complete;
    public byte isNewWealth = 0;


    private ArrayList<StructFNODepositoryRow> totalRows;
    public boolean isHoldingStore = false;

    public StructFNODepositoryDetail(){
        totalRows = new ArrayList<>();
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
        length = 66;
        bondEquityRow = new StructFNODepositoryRow[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            //GlobalClass.log("Br Data :: ");
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                //System.out.print(brData[j] + "\t");
                count++;
            }
            //GlobalClass.log("");
            bondEquityRow[i] = new StructFNODepositoryRow(brData, length);
            totalRows.add(bondEquityRow[i]);
        }
        length = 2;
        noOfRow = new byte[length];
        for (int i = 0; i < length; i++) {
            noOfRow[i] = data[count];
            count++;
        }
        complete = (short) ModuleClass.byteToshort(noOfRow, length);
        if(data.length > count){
            isNewWealth = data[count];
            count++;
        }
        if(complete == 1){
            shortNoOfRow = (short) totalRows.size();
            bondEquityRow = new StructFNODepositoryRow[shortNoOfRow];
            HashMap<String,StructFNODepositoryRow> dpHoldingRowHashMap = new HashMap<>();
            for(int i=0;i<shortNoOfRow;i++){
                bondEquityRow[i] = totalRows.get(i);
                if(totalRows.get(i).getScripCode() > 0) {
                    dpHoldingRowHashMap.put(totalRows.get(i).getScripCode()+"", bondEquityRow[i]);
                }
            }
            isHoldingStore = VenturaApplication.getPreference().setFNOHoldingList(dpHoldingRowHashMap);

        }
    }

    public short getNoOfRow() {
        //GlobalClass.log("noofRow get : " + shortNoOfRow);
        return shortNoOfRow;
    }

    public void setNoOfRow(short value) {
        shortNoOfRow = value;
    }


}
