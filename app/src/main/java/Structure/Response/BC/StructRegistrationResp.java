/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Structure.Response.BC;


import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 *
 * @author Raju
 */
public class StructRegistrationResp extends StructBase {

    public StructByte heartBeat;
    public StructByte shortMktWatch;
    public StructByte normalMkt;
    public StructByte maintenanceTag;

    public StructString reserve;

    public StructRegistrationResp() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructRegistrationResp(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        heartBeat = new StructByte("heartBeat", 0);
        shortMktWatch = new StructByte("shortMktWatch", 0);
        normalMkt = new StructByte("NormalMKt", 0);
        maintenanceTag = new StructByte("maintenanceTag", 0);
        reserve = new StructString("Reserve", 6, "");
        fields = new BaseStructure[]{
            heartBeat,shortMktWatch,normalMkt,maintenanceTag,reserve
        };
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

    public boolean getHeartBit(){
        if (heartBeat.getValue() == 1){
            return true;
        }
        return false;
    }

    public boolean isNormalMKt() {
        return normalMkt.getValue() ==1;
    }
    public boolean isMaintenanceMode() {
        return maintenanceTag.getValue() == 1;
    }
}