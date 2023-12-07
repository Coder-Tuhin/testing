package Structure.Response.Group;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructLong;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/8/2016.
 */
public class GroupTokensResp extends StructBase {

    public StructShort noOfRecs;
    public StructShort segment;
    public StructLong groupCode;
    public GroupsTokenDetails[] grpTokenDetails;
    public StructShort complete;

    public GroupTokensResp(byte[] bytes) {
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            grpTokenDetails = new GroupsTokenDetails[noOfRecs.getValue()];
            int iSize = new GroupsTokenDetails().data.sizeOf();
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                grpTokenDetails[i] = new GroupsTokenDetails(iBytes);
            }
            if(bytes.length > aCount){
                byte iBytes[] = new byte[2];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iBytes.length;
                complete.setValue(iBytes);
            }else{
                complete.setValue(1);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    private void init() {

        noOfRecs = new StructShort("noOfRecs", 0);
        segment = new StructShort("scripCode", -1);
        groupCode = new StructLong("groupCode", 0);
        complete = new StructShort("complete", 0);

        fields = new BaseStructure[]{
                noOfRecs,segment,groupCode
        };
    }
}
