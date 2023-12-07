package Structure.Response.RC;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructEODTickDataResp extends StructBase {

    public StructInt token;
    public StructInt totalTicks;
    public StructShort noOfRecords;
    public StructEODTick_Data[] structTickData;
    public StructShort downloadComplete;


    public StructEODTickDataResp(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructEODTickDataResp(byte[] bytes){
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            structTickData = new StructEODTick_Data[noOfRecords.getValue()];
            int iSize = new StructEODTick_Data().data.sizeOf();
            for (int i = 0; i < noOfRecords.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                structTickData[i] = new StructEODTick_Data(iBytes);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }
    private void init() {

        token = new StructInt("token",0);
        totalTicks = new StructInt("totalTicks",0);
        noOfRecords = new StructShort("noOfRecords",0);

        downloadComplete = new StructShort("downloadComplete",0);


        fields = new BaseStructure[]{
                token,totalTicks,noOfRecords,downloadComplete
        };
    }
}
