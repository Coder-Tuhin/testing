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

public class StructHoldingSummary_Pointer extends StructBase {

    public StructShort noOfRecs;
    public StructHoldingsReportReplyRecord_Pointer[] holdingData;
    public StructShort complete;

    public StructHoldingSummary_Pointer(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructHoldingSummary_Pointer(byte[] bytes){
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            holdingData = new StructHoldingsReportReplyRecord_Pointer[noOfRecs.getValue()];
            int iSize = new StructHoldingsReportReplyRecord_Pointer(129).data.sizeOf();
            if(bytes.length > (iSize*noOfRecs.getValue()) + 10){
                iSize = new StructHoldingsReportReplyRecord_Pointer(500).data.sizeOf();
            }
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                holdingData[i] = new StructHoldingsReportReplyRecord_Pointer(iBytes);
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
        noOfRecs = new StructShort("noOfRecs",'0');
        complete = new StructShort("complete",0);

        fields = new BaseStructure[]{

                noOfRecs
      };

    }
}


