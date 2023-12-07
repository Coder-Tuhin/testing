package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructChar;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructCancelScripDetailRow extends StructBase {


    public StructChar scripName;
    public StructChar cPType;
    public StructInt expiry;
    public StructInt strikeRate;
    public StructInt underlyingType;
    public StructInt mktLot;
    public StructInt tickSize;
    public StructInt segment;
    public StructInt token;
    public StructInt qtyLimit;
    public StructShort isNearNext;
    public StructChar category;
    public StructChar series;


    public StructCancelScripDetailRow(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructCancelScripDetailRow(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        scripName = new StructChar("scripName",'0');
        cPType = new StructChar("cPType",'0');
        expiry = new StructInt("expiry",'0');
        strikeRate = new StructInt("strikeRate",0);
        underlyingType = new StructInt("underlyingType",0);
        mktLot = new StructInt("mktLot",0);
        tickSize = new StructInt("tickSize",0);
        segment = new StructInt("segment",0);
        token = new StructInt("token",0);
        qtyLimit = new StructInt("qtyLimit",0);
        isNearNext = new StructShort("isNearNext",0);
        category = new StructChar("category",'0');
        series = new StructChar("series",'0');



        fields = new BaseStructure[]{
                scripName,cPType,expiry,strikeRate,underlyingType,mktLot,tickSize,segment,token,qtyLimit,
                isNearNext,category,series
        };

    }
}