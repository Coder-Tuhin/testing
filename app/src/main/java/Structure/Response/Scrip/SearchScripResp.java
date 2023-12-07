package Structure.Response.Scrip;

import java.util.ArrayList;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/17/2016.
 */
public class SearchScripResp extends StructBase {

    public StructShort noOfRecs;
    public StructString[] scripNames;
    public StructInt[] scripCodes;

    public SearchScripResp(){
        init();
        data = new StructValueSetter(fields);
    }
    public SearchScripResp(byte bytes[]) {
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            scripNames = new StructString[noOfRecs.getValue()];
            int iSize = 50;
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                scripNames[i] = new StructString("scripname"+i,iSize,"");
                scripNames[i].setValue(iBytes);
            }
            scripCodes = new StructInt[noOfRecs.getValue()];
            iSize = 4;
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                scripCodes[i] = new StructInt("ScripCode"+i,0);
                scripCodes[i].setValue(iBytes);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }
    private void init() {
        noOfRecs = new StructShort("noOfRecs", 0);
        fields = new BaseStructure[]{
                noOfRecs
        };
    }

    public ArrayList<SearchScripRow> getSearchScripList(){
        ArrayList<SearchScripRow> list = new ArrayList<>();
        for(int i=0;i<scripCodes.length;i++){
            SearchScripRow row = new SearchScripRow();
            row.scripCode = scripCodes[i].getValue();
            row.scripName = scripNames[i].getValue();
            list.add(row);
        }
        return list;
    }
}
