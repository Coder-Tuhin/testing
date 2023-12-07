package Structure.Response.BC;

import java.sql.Struct;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructADST extends StructBase {

    public StructInt segment;
    public StructString cPType;
    public StructString aDSName;
    public StructInt adv;
    public StructInt dec;
    public StructInt same;
    public StructLong turnOver;
    public StructLong turnOverWithStPr;

    public StructADST(){
        init();
        data= new StructValueSetter(fields);
    }
    public StructADST(byte[] bytes){
        init();
        data= new StructValueSetter(fields,bytes);
    }
    private void init() {
        segment = new StructInt("segment",0);
        cPType = new StructString("cPType",2,"");
        aDSName = new StructString("aDSName",20,"");
        adv = new StructInt("adv",0);
        dec = new StructInt("dec",0);
        same = new StructInt("same",0);
        turnOver = new StructLong("turnOver",0);
        turnOverWithStPr = new StructLong("turnOverWithStPr",0);
        fields = new BaseStructure[]{
            segment,cPType,aDSName,adv,dec,same,turnOver,turnOverWithStPr
        };
    }
}