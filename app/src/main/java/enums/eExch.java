package enums;

/**
 * Created by XTREMSOFT on 8/26/2016.
 */
public enum eExch {
    NONE(-1,"NA"),NSE(0,"NSE"),BSE(1,"BSE"),FNO(2,"FNO"),
    NSECURR(3,"NSE CURR"),SLBS(19,"SLBS");

    public short value;
    public String name;

    private eExch(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
    public static eExch nameOF(int _value){
        for(eExch exch : eExch.values()){
            if(exch.value == _value){
                return exch;
            }
        }
        return eExch.NONE;
    }
}
