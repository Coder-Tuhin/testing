package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructLong;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.DateUtil;

/**
 * Created by xtremsoft on 12/5/16.
 */
public class StructOrderResMsg extends StructBase {


    public StructString msg;
    public StructLong time;

    public StructOrderResMsg(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructOrderResMsg(byte[] bytes){
        if(bytes.length >= 300) {

            msg = new StructString("msg", 300, "");
            if(bytes.length>300) {
                time = new StructLong("Time", 0);
                fields = new BaseStructure[]{
                        msg, time
                };
            }else{
                time = new StructLong("time", DateUtil.CurrentTimeToN());
                fields = new BaseStructure[]{
                        msg
                };
            }
        }
        else{
            msg = new StructString("msg", 200, "");
            time = new StructLong("time", DateUtil.CurrentTimeToN());
            fields = new BaseStructure[]{
                    msg
            };
        }
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        msg = new StructString("msg",50,"");
        time = new StructLong("Time",0);
        fields = new BaseStructure[]{
                msg,time
        };
    }
}
