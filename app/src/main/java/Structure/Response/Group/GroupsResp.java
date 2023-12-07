package Structure.Response.Group;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/8/2016.
 */
public class GroupsResp extends StructBase {

    public StructShort NoOfRecs;
    public StructShort Seg;
    public GroupsRespDetails[] grpDetails;

    public GroupsResp(byte bytes[]) {
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            grpDetails = new GroupsRespDetails[NoOfRecs.getValue()];
            int iSize = new GroupsRespDetails().data.sizeOf();
            for (int i = 0; i < NoOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                grpDetails[i] = new GroupsRespDetails(iBytes);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    private void init() {
        NoOfRecs = new StructShort("NoOfRecs", 0);
        Seg = new StructShort("Seg", 0);

        fields = new BaseStructure[]{
                NoOfRecs,Seg
        };
    }
}
