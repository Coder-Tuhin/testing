package Structure.Response.AuthRelated;

import java.util.ArrayList;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class StateResponse extends StructBase {
    public StructShort shortNoOfStates;
    public StructString[] strStates;
    int stringlength = 50;
    public StateResponse(byte[] bytes){
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            strStates = new StructString[shortNoOfStates.getValue()];
            for (int i = 0; i < shortNoOfStates.getValue(); i++) {
                byte iBytes[] = new byte[stringlength];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += stringlength;
                strStates[i] = new StructString("states",stringlength,"");
                strStates[i].setValue(iBytes);
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init() {
        shortNoOfStates = new StructShort("shortNoOfStates",0);
        fields = new BaseStructure[]{
                shortNoOfStates
        };
    }


    public ArrayList<String> getList(){
        ArrayList<String> mList = new ArrayList<>();
        if (shortNoOfStates.getValue()>0){
           for (StructString state : strStates ){
                mList.add(state.getValue());
            }
        }
        return mList;
    }

}
