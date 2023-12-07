package Structure.Response.RC;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/19/2016.
 */

public class StructTradeSummary_Pointer  extends StructBase {



    public StructShort cNoOfRecs;
    public StructTradeReportReplyRecord_Pointer[] tradeRequest;
    public StructShort complete;

    public StructTradeSummary_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructTradeSummary_Pointer(byte[] bytes){
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            tradeRequest = new StructTradeReportReplyRecord_Pointer[cNoOfRecs.getValue()];
            int iSize = new StructTradeReportReplyRecord_Pointer().data.sizeOf();
            for (int i = 0; i < cNoOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                tradeRequest[i] = new StructTradeReportReplyRecord_Pointer(iBytes);
            }
            oPacketSize = 2;
            ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            complete.setValue(ep.getExtractData());
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }
    private void init() {
        cNoOfRecs = new StructShort("cNoOfRecs",0);
        complete = new StructShort("complete",0);
        fields = new BaseStructure[]{
                cNoOfRecs
        };

    }
}
