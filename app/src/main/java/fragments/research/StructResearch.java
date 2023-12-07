/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fragments.research;

import java.lang.reflect.Field;
import structure.BaseStructure;
import structure.StructBase;
import structure.StructByte;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.VenturaException;

/**
 *
 * @author Raju
 */
public class StructResearch extends StructBase{
    public StructInt msgTime;       //3. Message---CHAR--50bytes
    public StructString company;
    public StructInt scripCode;
    public StructShort msgType;       //3. Message---CHAR--50bytes
    public StructShort buysell;       //3. Message---CHAR--50bytes
    public StructByte sip;       //3. Message---CHAR--50bytes
    public StructString attachedMent;
    public StructInt SL;
    
    public StructString caption;
    
    
    public StructResearch() {
        init(450);
        data = new StructValueSetter(fields);
    }
    public StructResearch(byte bytes[]) {
        try {
            init(bytes.length);
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }
    private void init(int length) {
        
        className = getClass().getName();
        msgTime = new StructInt("msgtimw", 0);
        company = new StructString("Company", 50, "");
        scripCode = new StructInt("ScripCode", 0);
        msgType = new StructShort("msgType", 0);
        buysell = new StructShort("BuySell", -1);
        sip = new StructByte("SIP", 0);
        attachedMent = new StructString("AttachedMent", 200, "");
        SL = new StructInt("SL", 0);
        
        caption = new StructString("Caption", 500, ""); // not include in structure....
        fields = new BaseStructure[]{
            msgTime,company,scripCode,msgType,buysell,sip,attachedMent,SL
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
            VenturaException.Print(e);
        }
        return sb.toString();
    }
}
