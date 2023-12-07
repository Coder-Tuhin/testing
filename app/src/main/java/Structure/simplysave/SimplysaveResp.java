package Structure.simplysave;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 21-Aug-2017.
 */
public class SimplysaveResp extends StructBase {

    public StructString clientCode;//length=12
    public StructString response;//length=210

    public SimplysaveResp(byte[] data) {

        int count = 0;
        byte[] byteClient = new byte[12];
        System.arraycopy(data,0,byteClient,0,byteClient.length);
        clientCode = new StructString("ClientCode",12,"");
        clientCode.setValue(byteClient);

        count = count + byteClient.length;
        byte[] byteres = new byte[data.length - count];
        System.arraycopy(data,count,byteres,0,byteres.length);
        response = new StructString("Res",byteres.length,"");
        response.setValue(byteres);
    }
    public SimplysaveResp(String clientCode,String resp) {
        className = getClass().getName();

        this.clientCode = new StructString("clientCode", 12, clientCode);
        this.response = new StructString("response", resp.length(), resp);
        fields = new BaseStructure[]{
                this.clientCode,this.response
        };
        data = new StructValueSetter(fields);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
}
