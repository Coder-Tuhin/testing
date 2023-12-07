package Structure.Request.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructFloat;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class PositionConversion_Pointer extends StructBase {

    public StructString clientCode;
    public StructChar exch;
    public StructChar exchType;
    public StructInt scripCode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructByte instrumentType;
    public StructByte cPType;
    public StructByte cALevel;
    public StructInt expiry;
    public StructFloat strikePrice;
    public StructChar buySell;
    public StructInt qty;
    public StructFloat rate;
    public StructLong exchOrderID;
    public StructLong exchTradeID;
    public StructByte orderType;


    public PositionConversion_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init(){
        clientCode = new StructString("clientCode",15,"");
        exch = new StructChar("exch",'0');
        exchType = new StructChar("exchType",'0');
        scripCode = new StructInt("scripCode",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",12,"");
        instrumentType = new StructByte("instrumentType",0);
        cPType = new StructByte("cPType",0);
        cALevel = new StructByte("cALevel",0);
        expiry = new StructInt("expiry",0);
        strikePrice = new StructFloat("strikePrice",0);
        buySell = new StructChar("buySell",'0');
        qty = new StructInt("qty",0);
        rate = new StructFloat("rate",0);
        exchOrderID = new StructLong("exchOrderID",0);
        exchTradeID = new StructLong("exchTradeID",0);
        orderType = new StructByte("orderType",0);



        fields = new BaseStructure[]{
                clientCode,exch,exchType,scripCode,scripNameLength,scripName,instrumentType,cPType,cALevel,expiry,strikePrice,buySell,qty,rate,exchOrderID,
                exchTradeID,orderType

        };
    }
}