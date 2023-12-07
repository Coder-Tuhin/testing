package Structure.Response.Scrip;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import enums.eExch;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructMoney;
import structure.StructString;
import structure.StructValueSetter;
import utils.DateUtil;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/18/2016.
 */
public class StructSearchScripRow1 extends StructBase {

    public StructInt scripCode;
    public StructInt exchange;
    public StructString symbol; //10 bytes
    public StructString cpTypeSeries;//2 bytes
    public StructInt expiry;
    public StructMoney strikeRate;
    public boolean isSelected = false;
    public String formattedScripName = "";

    public StructSearchScripRow1() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructSearchScripRow1(byte[] bytes) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void init() {
        className = getClass().getName();

        scripCode = new StructInt("ScripCode", 0);
        exchange = new StructInt("exchange", 0);
        symbol = new StructString("symbol", 10, "");
        cpTypeSeries = new StructString("cpTypeSeries", 2, "");
        expiry = new StructInt("expiry", 0);
        strikeRate = new StructMoney("strikeRate", 0);

        fields = new BaseStructure[]{
                scripCode, exchange, symbol, cpTypeSeries, expiry, strikeRate
        };
    }

    public String getScripName() {

        if(!formattedScripName.equalsIgnoreCase("")){
            return formattedScripName;
        }
        String scripName = symbol.getValue();
        int intExpiry = expiry.getValue();
        if (exchange.getValue() == eExch.NSE.value) {
            scripName = "NE-" + symbol.getValue();                     //Indices
            if (!cpTypeSeries.getValue().equalsIgnoreCase("")) {
                scripName = scripName + "-" + cpTypeSeries.getValue();      //scrips
            }
        } else if (exchange.getValue() == eExch.BSE.value) {
            scripName = "BE-" + symbol.getValue();                     //Indices
        } else if (exchange.getValue() == eExch.SLBS.value) {
            scripName = "S";
            scripName = scripName + "D-" + symbol.getValue() + "-" + DateUtil.NToDDMMMYY(intExpiry) + "-" + cpTypeSeries.getValue();
        } else if (exchange.getValue() == eExch.FNO.value) {
            scripName = "N";
            String strExpDate = DateUtil.NToDDMMMYY(intExpiry);
            if (cpTypeSeries.getValue().equalsIgnoreCase("XX")) {
                scripName = scripName + "F-" + symbol.getValue() + "-" + strExpDate;
            } else {
                scripName = scripName + "O-" + symbol.getValue() + "-" + cpTypeSeries.getValue() + "-" + strExpDate + "-" + (double) (strikeRate.getValue());
            }
        } else if (exchange.getValue() == eExch.NSECURR.value) {
            scripName = "C";
            String strExpDate = DateUtil.NToDDMMMYY(intExpiry);
            if (cpTypeSeries.getValue().equalsIgnoreCase("XX")) {
                scripName = scripName + "D-" + symbol.getValue() + "-" + strExpDate;
            } else {
                scripName = scripName + "D-" + symbol.getValue() + "-" + cpTypeSeries.getValue() + "-" + strExpDate + "-" + (double) (strikeRate.getValue());
            }
        }
        formattedScripName = scripName;
        return formattedScripName;
    }

    public String getScripNameWithOutExch() {

        String scripName = symbol.getValue();
        int intExpiry = expiry.getValue();
        if (exchange.getValue() == eExch.NSE.value) {
            scripName = symbol.getValue();                     //Indices
            if (!cpTypeSeries.getValue().equalsIgnoreCase("")) {
                scripName = scripName + "-" + cpTypeSeries.getValue();      //scrips
            }
        } else if (exchange.getValue() == eExch.BSE.value) {
            scripName = symbol.getValue();                     //Indices
        } else if (exchange.getValue() == eExch.SLBS.value) {
            scripName = symbol.getValue() + "-" + DateUtil.NToDDMMMYY(intExpiry).toUpperCase() + "-" + cpTypeSeries.getValue();
        } else if (exchange.getValue() == eExch.FNO.value) {
            String strExpDate = DateUtil.NToDDMMMYY(intExpiry).toUpperCase();
            if (cpTypeSeries.getValue().equalsIgnoreCase("XX")) {
                scripName = symbol.getValue() + "-" + strExpDate;
            } else {
                scripName = symbol.getValue() + "-" + cpTypeSeries.getValue() + "-" + strExpDate + "-" + (int) (strikeRate.getValue());
            }
        } else if (exchange.getValue() == eExch.NSECURR.value) {
            String strExpDate = DateUtil.NToDDMMMYY(intExpiry).toUpperCase();
            if (cpTypeSeries.getValue().equalsIgnoreCase("XX")) {
                scripName = symbol.getValue() + "-" + strExpDate;
            } else {
                scripName = symbol.getValue() + "-" + cpTypeSeries.getValue() + "-" + strExpDate + "-" + (double) (strikeRate.getValue());
            }
        }
        return scripName;
    }

    private eExch exch = null;
    public String getExchName(){
        if(exch == null){
            exch = eExch.nameOF(exchange.getValue());
        }
        return exch.name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
}