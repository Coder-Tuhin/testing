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

public class Struct_Cancel_ScripDetails  extends StructBase {

    public StructShort noOfRow;
    public StructCancelScripDetailRow[] cancelDetailRow;


    public Struct_Cancel_ScripDetails(){
        init();
        data= new StructValueSetter(fields);
    }
    public Struct_Cancel_ScripDetails(byte[] bytes){
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            cancelDetailRow = new StructCancelScripDetailRow[noOfRow.getValue()];
            int iSize = new StructCancelScripDetailRow().data.sizeOf();
            for (int i = 0; i < noOfRow.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                cancelDetailRow[i] = new StructCancelScripDetailRow(iBytes);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }
    private void init() {
        noOfRow = new StructShort("noOfRow",0);

        fields = new BaseStructure[]{
                noOfRow
        };

    }
}