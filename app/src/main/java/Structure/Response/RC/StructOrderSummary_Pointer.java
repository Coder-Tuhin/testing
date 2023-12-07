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

public class StructOrderSummary_Pointer  extends StructBase {

    public StructShort shortcNoOfRecs;
    public StructOrderReportReplyRecord_Pointer[] orderRequest;
    public StructShort complete;
    public StructOrderSummary_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructOrderSummary_Pointer(byte[] bytes){
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            orderRequest = new StructOrderReportReplyRecord_Pointer[shortcNoOfRecs.getValue()];
            int iSize = new StructOrderReportReplyRecord_Pointer().data.sizeOf();
            for (int i = 0; i < shortcNoOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                orderRequest[i] = new StructOrderReportReplyRecord_Pointer(iBytes);
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
        shortcNoOfRecs = new StructShort("shortcNoOfRecs",0);
        complete = new StructShort("complete",0);
        fields = new BaseStructure[]{
                shortcNoOfRecs
        };
    }
}