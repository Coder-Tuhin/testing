package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructDouble;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class TSLBMHoldingsReportReplyRecord extends StructBase {

    public StructInt bSECode;
    public StructInt nSECode;
    public StructByte scripNameLength;
    public StructString scripName;
    public StructString series;
    public StructChar receivingDeliveringFlag;
    public StructString deliveryDate;

    public StructLong tradeNo;
    public StructChar settlementType;
    public StructInt settlementNo;
    public StructString expiryDate;
    public StructString quantityToBeDelivered;
    public StructString custodialParticipantCode;

    private StructDouble lendingPrice;
    public StructDouble totalAmount;
    public StructString rollOverIndicator;

    public StructInt BfQty;
    public StructInt NetQty;
    public StructString reserved;

    public double nseRate;

    public TSLBMHoldingsReportReplyRecord(){
        init();
        data= new StructValueSetter(fields);
    }
    public TSLBMHoldingsReportReplyRecord(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
        scripName.setValue("SD-"+scripName.getValue());
        nSECode.setValue(GlobalClass.getSLBS_ScripCodeForNeutrino(nSECode.getValue()));
    }
    private void init() {

        bSECode = new StructInt("bSECode",0);
        nSECode = new StructInt("nSECode",0);
        scripNameLength = new StructByte("scripNameLength",0);
        scripName = new StructString("scripName",20,"");
        series = new StructString("scripName",2,"");
        receivingDeliveringFlag = new StructChar("ReceivingDeliveringFlag",' ');
        deliveryDate = new StructString("DeliveryDate",11,"");
        tradeNo = new StructLong("TradeNo",0);
        settlementType = new StructChar("settlementType",' ');
        settlementNo = new StructInt("settlementNo",0);
        expiryDate = new StructString("DeliveryDate",11,"");
        quantityToBeDelivered = new StructString("quantityToBeDelivered",10,"");
        custodialParticipantCode = new StructString("custodialParticipantCode",15,"");
        lendingPrice = new StructDouble("lendingprice", 0);
        totalAmount = new StructDouble("totalamount", 0);
        rollOverIndicator = new StructString("rollOverIndicator",10,"");
        BfQty = new StructInt("bfQty",0);
        NetQty = new StructInt("NetQty",0);
        reserved = new StructString("reserved",10,"");

        fields = new BaseStructure[]{
                bSECode,nSECode,scripNameLength,scripName,series,receivingDeliveringFlag,
                deliveryDate,tradeNo,settlementType,settlementNo,expiryDate,quantityToBeDelivered,
                custodialParticipantCode,lendingPrice,totalAmount,rollOverIndicator,BfQty,NetQty,reserved
        };
    }
    public double getNseTOTALRate(){
        if(nseRate > 0){
            return nseRate*NetQty.getValue();
        }/*else{
            return lendingPrice.getValue()*NetQty.getValue();
        }*/
        return 0;
    }
    public void setNseRate(double rate){
        nseRate = rate;
    }
    public double getNseRate(){
        return nseRate;
    }

    public String getBorrowLend(){
        if(receivingDeliveringFlag.getValue() == 'R'){
            return "LENT";
        }else if(receivingDeliveringFlag.getValue() == 'D'){
            return "BORROW";
        }else{
            return "" + receivingDeliveringFlag.getValue();
        }
    }
}
