package Structure.Response.RC;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

public class SLBMHoldingSummary extends StructBase {

    public StructShort noOfRecs;
    public TSLBMHoldingsReportReplyRecord[] holdingData;
    public StructShort complete;

    public SLBMHoldingSummary(){
        init();
        data= new StructValueSetter(fields);
    }
    public SLBMHoldingSummary(byte[] bytes){

        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            holdingData = new TSLBMHoldingsReportReplyRecord[noOfRecs.getValue()];
            int iSize = new TSLBMHoldingsReportReplyRecord().data.sizeOf();
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                holdingData[i] = new TSLBMHoldingsReportReplyRecord(iBytes);
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

