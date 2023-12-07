package wealth.wealthStructure;

import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import wealth.commonstructure.ModuleClass;

/**
 * Created by xtremsoft on 7/26/17.
 */

public class StructBondEquityDepositoryDetailNew {

    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private StructBondEuityDepositoryRow[] bondEquityRow;

    public short complete;
    public byte isNewWealth = 0;
    public String clientCode;

    private ArrayList<StructBondEuityDepositoryRow> totalRows;


    public boolean isHoldingStore = false;
    public StructBondEquityDepositoryDetailNew(){
        shortNoOfRow = 0;
        bondEquityRow = new StructBondEuityDepositoryRow[0];
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

        if (intMsgLength == dataLength) {
            //GlobalClass.log("Message Length varified... : " + intMsgLength);
        } else {
            GlobalClass.log("StructClientLoginResponse :: Message Length error...");
        }

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
        length = 127;
        if(data.length > ((shortNoOfRow*length)+(9+8))){
            length = 135;
        }
        bondEquityRow = new StructBondEuityDepositoryRow[shortNoOfRow];
        for (int i = 0; i < shortNoOfRow; i++) {
            byte[] brData = new byte[length];
            //GlobalClass.log("Br Data :: ");
            for (int j = 0; j < length; j++) {
                brData[j] = data[count];
                //System.out.print(brData[j] + "\t");
                count++;
            }
            //GlobalClass.log("");
            bondEquityRow[i] = new StructBondEuityDepositoryRow(brData, length);
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
        //GlobalClass.log("mywealthholding : " + complete + " : " + totalRows.size());
        if(complete == 1){
            shortNoOfRow = (short) totalRows.size();
            bondEquityRow = new StructBondEuityDepositoryRow[shortNoOfRow];
            HashMap<String,StructDPHoldingRow> dpHoldingRowHashMap = new HashMap<>();
            //HashMap<String, StructDPHoldingRow> savedDPHoldingList = VenturaApplication.getPreference().getDPHoldingList();
            for(int i=0;i<shortNoOfRow;i++){
                bondEquityRow[i] = totalRows.get(i);
                if(!totalRows.get(i).getISINNo().equalsIgnoreCase("") && totalRows.get(i).getQty()>0) {
                    StructDPHoldingRow dpHoldingRow = new StructDPHoldingRow(totalRows.get(i).getQty(), totalRows.get(i).getAvgPurchasePrice(),
                            totalRows.get(i).getHoldingType(),totalRows.get(i).getISINNo(),totalRows.get(i).getCompanyName(),isNewWealth);
                   /*
                    try{
                        if((savedDPHoldingList != null) && (savedDPHoldingList.get(totalRows.get(i).getISINNo().toUpperCase()) != null)){
                            StructDPHoldingRow savedRow = savedDPHoldingList.get(totalRows.get(i).getISINNo().toUpperCase());
                            dpHoldingRow.setEdisQty(savedRow.getEdisQty());
                        }
                    }catch (Exception ex){ex.printStackTrace();}*/

                    if(totalRows.get(i).getBseScripCode() > 0){
                        dpHoldingRowHashMap.put(totalRows.get(i).getBseScripCode()+"", dpHoldingRow);
                    }
                    if(totalRows.get(i).getNseScripCode() > 0){
                        dpHoldingRowHashMap.put(totalRows.get(i).getNseScripCode()+"", dpHoldingRow);
                    }
                    dpHoldingRowHashMap.put(totalRows.get(i).getISINNo().toUpperCase(), dpHoldingRow);
                }
            }
            if(clientCode.equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())) {
                isHoldingStore = VenturaApplication.getPreference().setDPHoldingList(dpHoldingRowHashMap);
            }
            GlobalClass.log("Holding data Saved : " + dpHoldingRowHashMap.size());
        }
    }

    public short getNoOfRow() {
        //GlobalClass.log("noofRow get : " + shortNoOfRow);
        return shortNoOfRow;
    }

    public void setNoOfRow(short value) {
        shortNoOfRow = value;
    }

    public StructBondEuityDepositoryRow[] getBondEquityDepositoryRows() {
        return bondEquityRow;
    }

    public StructBondEuityDepositoryRow[] getSortedString(int column_no, int type) {

        StructBondEuityDepositoryRow[] sorted = new StructBondEuityDepositoryRow[shortNoOfRow];
        int i = 0, count = 0;

        if (column_no == 0) {

            ArrayList<String> string_list = new ArrayList<String>();
            for (int k = 0; k < shortNoOfRow; k++) {
                string_list.add(bondEquityRow[k].getCompanyName());
            }
            if (type == 0) {
                Collections.sort(string_list, Collections.reverseOrder());
            } else {
                Collections.sort(string_list);
            }

            while (count < string_list.size()) {
                for (int j = 0; j < string_list.size(); j++) {
                    if (string_list.get(count).equalsIgnoreCase(bondEquityRow[j].getCompanyName())) {
                        if (!bondEquityRow[j].getCompanyName().equalsIgnoreCase("Total")) {
                            sorted[i] = bondEquityRow[j];
                            i++;
                            count++;
                        } else {
                            sorted[string_list.size() - 1] = bondEquityRow[j];
                            count++;
                        }

                        // GlobalClass.log("Company Name : " + string_list.get(count));
                        break;
                    }
                }
            }
        } else if (column_no == 4) {

            ArrayList<Double> string_list = new ArrayList<>();
            for (int k = 0; k < shortNoOfRow; k++) {
                string_list.add(bondEquityRow[k].getCurrentValue());
            }

            if (type == 0) {
                Collections.sort(string_list, Collections.reverseOrder());
            } else {
                Collections.sort(string_list);
            }

            while (count < string_list.size()) {

                for (int j = 0; j < string_list.size(); j++) {
                    if (string_list.get(count) == bondEquityRow[j].getCurrentValue()) {
                        if (!bondEquityRow[j].getCompanyName().equalsIgnoreCase("Total")) {
                            sorted[i] = bondEquityRow[j];
                            i++;
                            count++;
                        } else {
                            sorted[string_list.size() - 1] = bondEquityRow[j];
                            count++;
                        }
                        /*GlobalClass.log("Qty : " + string_list.get(i));*/
                        break;
                    }
                }
            }
        } else if (column_no == 5) {

            ArrayList<Double> string_list = new ArrayList<>();
            for (int k = 0; k < shortNoOfRow; k++) {
                string_list.add(bondEquityRow[k].getGainLoss());
            }

            if (type == 0) {
                Collections.sort(string_list, Collections.reverseOrder());
            } else {
                Collections.sort(string_list);
            }

            while (count < string_list.size()) {

                for (int j = 0; j < string_list.size(); j++) {
                    if (string_list.get(count) == bondEquityRow[j].getGainLoss()) {
                        if (!bondEquityRow[j].getCompanyName().equalsIgnoreCase("Total")) {
                            sorted[i] = bondEquityRow[j];
                            i++;
                            count++;
                        } else {
                            sorted[string_list.size() - 1] = bondEquityRow[j];
                            count++;
                        }

                       /* GlobalClass.log("Qty : " + string_list.get(i));*/
                        break;
                    }
                }
            }
        }else if (column_no == 6) {

            ArrayList<Double> string_list = new ArrayList<>();
            for (int k = 0; k < shortNoOfRow; k++) {
                string_list.add(bondEquityRow[k].getPrevDayGainLoss());
            }

            if (type == 0) {
                Collections.sort(string_list, Collections.reverseOrder());
            } else {
                Collections.sort(string_list);
            }

            while (count < string_list.size()) {

                for (int j = 0; j < string_list.size(); j++) {
                    if (string_list.get(count) == bondEquityRow[j].getPrevDayGainLoss()) {
                        if (!bondEquityRow[j].getCompanyName().equalsIgnoreCase("Total")) {
                            sorted[i] = bondEquityRow[j];
                            i++;
                            count++;
                        } else {
                            sorted[string_list.size() - 1] = bondEquityRow[j];
                            count++;
                        }

                        /* GlobalClass.log("Qty : " + string_list.get(i));*/
                        break;
                    }
                }
            }
        }

        return sorted;
    }

    private double totalGainLoss = 0;
    private double totalCurrentValue = 0;
    private double totalPrevDayGL = 0;

    public void calculateTotalGainLoss(){
        totalGainLoss = 0;
        totalCurrentValue = 0;
        totalPrevDayGL = 0;
        for(int i=0;i<bondEquityRow.length;i++){
            StructBondEuityDepositoryRow rows = bondEquityRow[i];
            if(!rows.getCompanyName().equalsIgnoreCase("Total")) {
                totalGainLoss = totalGainLoss + rows.getGainLoss();
                totalCurrentValue = totalCurrentValue + rows.getCurrentValue();
                totalPrevDayGL = totalPrevDayGL + rows.getPrevDayGainLoss();
            }
        }
    }
    public double getTotalGainLoss(){
        return totalGainLoss;
    }
    public double getTotalCurrentValue(){
        return totalCurrentValue;
    }
    public double getTotalPrevDayGainLoss(){
        return totalPrevDayGL;
    }
}
