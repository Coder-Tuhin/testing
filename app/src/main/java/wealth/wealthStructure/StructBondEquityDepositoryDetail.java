package wealth.wealthStructure;


import java.util.ArrayList;
import java.util.Collections;

import wealth.commonstructure.ModuleClass;

/**
 * Created by Admin on 22/05/14.
 */

public class StructBondEquityDepositoryDetail {

    private short shortNoOfRow;    //2.---onOfRow---INT---4bytes---
    private StructBondEuityDepositoryRow[] bondEquityRow;
    private int noOfRowAdd = 0;

    public StructBondEquityDepositoryDetail(byte[] data, int dataLength) {

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
        length = 114;
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
        }

        return sorted;
    }
}
