package Structure.Request.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/16/2016.
 */

public class SearchScript extends StructBase {

    public StructInt intExchange;
    public StructString searchString;
    public StructShort shortInstrument;
    public StructShort expiry;
    public StructShort expiryType;
    public StructShort companyType;

    public SearchScript(){
        init();
        data= new StructValueSetter(fields);
    }

    private void init() {
        intExchange=new StructInt("intExchange",4);
        searchString=new StructString("searchString",20,"");
        shortInstrument = new StructShort("shortInstrument",2);
        expiry = new StructShort("expiry",2);
        expiryType = new StructShort("expiryType",2);
        companyType = new StructShort("companyType",0);
        fields = new BaseStructure[]{
                intExchange,searchString,shortInstrument,expiry,expiryType,companyType
        };
    }
}

