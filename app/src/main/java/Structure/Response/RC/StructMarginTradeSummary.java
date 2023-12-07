package Structure.Response.RC;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11-Oct-2017.
 */
public class StructMarginTradeSummary extends StructBase {
    public StructShort noOfRecs;
    public StructmarginTrade[] margintradeData;
    public StructShort complete;


    public StructMarginTradeSummary(){
        init();
        data= new StructValueSetter(fields);
    }

    public StructMarginTradeSummary(byte[] bytes){
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            margintradeData = new StructmarginTrade[noOfRecs.getValue()];
            int iSize = new StructmarginTrade().data.sizeOf();
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                margintradeData[i] = new StructmarginTrade(iBytes);
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
