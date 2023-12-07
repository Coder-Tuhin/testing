package wealth.new_mutualfund.Structure;

import org.json.JSONObject;

import structure.BaseStructure;
import structure.StructBase;
import structure.StructString;
import structure.StructValueSetter;

public class StructNewMFReq extends StructBase {

    public StructString strData;
    public StructNewMFReq(JSONObject jdata){
        String j = jdata.toString();
        strData =new StructString("JDATA",j.length(),j);
        fields = new BaseStructure[]{
                strData
        };
        data= new StructValueSetter(fields);
    }
}
