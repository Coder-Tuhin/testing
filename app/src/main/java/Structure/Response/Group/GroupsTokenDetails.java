package Structure.Response.Group;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class GroupsTokenDetails extends StructBase {

    public StructInt scripCode;
    public StructString scripName;
    public boolean isNewlyAdded = false;

    public GroupsTokenDetails(){
        init();
        data = new StructValueSetter(fields);

    }
    public GroupsTokenDetails(byte[] bytes){
        init();
        data = new StructValueSetter(fields,bytes);
        getSymbolAndDetail();
    }

    private void init() {
        scripCode=new StructInt("scripCode",0);
        scripName=new StructString("symbol",50,"");
        fields = new BaseStructure[]{
                scripCode,scripName
        };
    }

    public String getScripName(){
        return scripName.getValue();
    }
    String[] strArr = null;
    public String[] getSymbolAndDetail(){
        if(strArr != null){
            return strArr;
        }
        strArr = new String[2];
        String[] splitArray = null;
        String strScripName = scripName.getValue();
        if(strScripName.startsWith("BE-") || strScripName.startsWith("NE-")){

            String strScripNameL = strScripName.substring(strScripName.indexOf("-")+1);
            strArr[0] = strScripNameL;
            if (strScripName.startsWith("BE-")) {
                strArr[1] = " BSE";
            } else if (strScripName.startsWith("NE-")) {
                strArr[1] = " NSE";
            }
        }
        else {
            if (scripName.getValue().contains("-")) {
                splitArray = scripName.getValue().split("-");
            }
            if (splitArray != null && splitArray.length > 1) {
                strArr[0] = splitArray[1];
                String subText = "";
                if (splitArray.length > 2) {
                    for (int i = 2; i < splitArray.length; i++) {
                        subText = subText + "-" + splitArray[i];
                    }
                }
                if (subText.substring(0, 1).equals("-")) {
                    subText = subText.substring(1, subText.length());
                }
                strArr[1] = subText;
            }
        }
        return strArr;
    }


    public String getTextforReader(){
        String strScripName = scripName.getValue();
        String segment = "";
        String symbol = strScripName.substring(strScripName.indexOf("-")+1);
        if (strScripName.startsWith("BE-")) {
            segment = "BSE  ";
        } else if (strScripName.startsWith("NE-")) {
            segment = "NSE  ";
        }else {
            segment = "FNO  ";
        }
        return segment + symbol;
    }




    /*
    public GroupsTokenDetails(){
        init();
        data = new StructValueSetter(fields);
        scripName = symbol;
    }
    public GroupsTokenDetails(byte[] bytes){
        init();
        data = new StructValueSetter(fields,bytes);
        scripName = symbol;
    }

    private void init() {
        seg=new StructInt("seg",0);
        scripCode=new StructInt("scripCode",0);
        nSEBSECode=new StructInt("nSEBSECode",0);
        symbol=new StructString("symbol",10,"");
        series=new StructString("series",2,"");
        expiry=new StructInt("expiry",0);
        inst=new StructShort("inst",0);
        strikePrice=new StructInt("strikePrice",0);
        mktLot=new StructShort("mktLot",0);
        multiplier=new StructShort("multiplier",0);
        undType=new StructShort("undType",0);
        tickSize=new StructInt("tickSize",0);
        limitQty=new StructInt("limitQty",0);
        fields = new BaseStructure[]{
                seg,scripCode,nSEBSECode,symbol,series,expiry,inst,strikePrice,mktLot,multiplier,undType,tickSize,limitQty
        };
    }
    public String getExchangeSeries(){
        String str = "";
        if(seg.getValue() == eExchSegment.NSECASH.value || seg.getValue() == eExchSegment.BSECASH.value) {
            str = str + GlobalClass.getExchangeName(seg.getValue());
        }
        else if(expiry.getValue() > 0){
            str = str + DateUtil.dateFormatter(expiry.getValue(), "ddMMMyy");
        }
        if((seg.getValue() == eExchSegment.NSECASH.value) && !series.getValue().equalsIgnoreCase("")) {
            str = str + " [" + series.getValue() + "]";
        }
        else if((seg.getValue() == eExchSegment.NSEFO.value) && inst.getValue()> 0){
            str = str + " [" + GlobalClass.getInstrumentName(inst.getValue()) + "]";
        }
        if(strikePrice.getValue() > 0){
            str = str + " " + Formatter.formatter.format(((double)strikePrice.getValue())/100);
        }
        return str;
    }
    public String getFormattedScripName() {

        String scripName = "";
        try {
            switch (seg.getValue()) {
                case 0:
                    scripName = "NE-" + symbol.getValue();
                    break;
                case 1:
                    scripName = "BE-" + symbol.getValue();
                    break;
                case 2:
                    scripName = "N";
                    String cptype = GlobalClass.getInstrumentName(inst.getValue());
                    int expiryl = expiry.getValue();
                    String strikRate = Formatter.formatter.format(((double)strikePrice.getValue())/100);

                    if (cptype.equalsIgnoreCase("")) {
                        scripName = scripName + "F-" + symbol.getValue() + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy");
                    } else {
                        scripName = scripName + "O-" + symbol.getValue() + "-" + cptype + "-" + DateUtil.dateFormatter(expiryl, "ddMMMyy") + "-" + strikRate;
                    }
                    break;

            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
        return scripName;
    }

    public String getFormattedScripNameForNSEBSECode() {

        String scripName = "";
        try {
            switch (seg.getValue()) {
                case 1:
                    scripName = "NE-" + symbol.getValue();
                    break;
                case 0:
                    scripName = "BE-" + symbol.getValue();
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
        return scripName;
    }*/
}
