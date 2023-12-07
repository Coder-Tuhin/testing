package Structure.Response.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructClientLoginIntraDelResp extends StructBase {

    public StructByte accountType;
    public StructByte fEUserType;
    public StructInt systemDateTime;
    public StructInt badLoginCount;
    public StructInt lastModifiedTime;
    public StructInt lastAccessedTime;
    public StructByte allowNseCash;
    public StructByte allowNseDeriv;
    public StructByte allowBseCash;
    public StructShort branchID;
    public StructByte allowTotalBuySellQty;
    public StructChar allowFastFeed; //25bytes
    public StructChar allowIntradayDelivery;
    public StructString addMsg; //100bytes

    public StructByte allowNseCurr;
    public StructByte allowNseSLBM;
    public StructByte isMarginTrade;
    public StructByte isCommodityAllow;

    public StructChar allowFNOIntradayDelivery;
    public StructChar allowCURRIntradayDelivery;
    public StructBoolean allowOCO;
    public StructByte isAdminReset;
    public StructByte isMarginBF; //was 2 byte for allowOCO we take 1 byte so new size is 1 byte
    public StructByte isPOA; //was 2 byte for allowOCO we take 1 byte so new size is 1 byte


    public StructClientLoginIntraDelResp(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructClientLoginIntraDelResp(byte[] bytes){
        init();
        data = new StructValueSetter(fields,bytes);
    }

    private void init() {
        accountType = new StructByte("accountType",0);
        fEUserType = new StructByte("fEUserType",0);
        systemDateTime = new StructInt("systemDateTime",0);
        badLoginCount = new StructInt("badLoginCount",0);
        lastModifiedTime = new StructInt("lastModifiedTime",0);
        lastAccessedTime = new StructInt("lastAccessedTime",0);
        allowNseCash = new StructByte("allowNseCash",0);
        allowNseDeriv = new StructByte("allowNseDeriv",0);
        allowBseCash = new StructByte("allowBseCash",0);
        branchID = new StructShort("branchID",0);
        allowTotalBuySellQty = new StructByte("allowTotalBuySellQty",0);
        allowFastFeed = new StructChar("allowFastFeed",'N');
        allowIntradayDelivery = new StructChar("allowIntradayDelivery",'N');
        addMsg = new StructString("addMsg",100,"");
        allowNseCurr = new StructByte("allowNseCurr",0);
        allowNseSLBM = new StructByte("allowNseSLBM",0);
        isMarginTrade = new StructByte("isMarginTrade",0);
        isCommodityAllow = new StructByte("isCommodityAllow", 0);
        allowFNOIntradayDelivery = new StructChar("allowFnoIntra", 'N');
        allowCURRIntradayDelivery = new StructChar("allowCURRIntra", 'N');
        allowOCO = new StructBoolean("AllowOCO",false);
        isAdminReset = new StructByte("isAdminReset", 0);
        isMarginBF = new StructByte("isMarginBF", 0);
        isPOA = new StructByte("isPOA", 0);
        fields = new BaseStructure[]{
                accountType, fEUserType, systemDateTime, badLoginCount, lastModifiedTime,
                lastAccessedTime, allowNseCash, allowNseDeriv,
                allowBseCash, branchID, allowTotalBuySellQty, allowFastFeed, allowIntradayDelivery,
                addMsg, allowNseCurr, allowNseSLBM,isMarginTrade, isCommodityAllow,allowFNOIntradayDelivery,
                allowCURRIntradayDelivery,allowOCO,isAdminReset,isMarginBF,isPOA
        };
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