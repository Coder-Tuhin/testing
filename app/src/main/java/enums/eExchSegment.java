package enums;

/**
 * Created by xtremsoft on 8/20/16.
 */
public enum eExchSegment {
    NONE(-1,"None"), NSECASH(0,"NSE"), BSECASH(1,"BSE"),
    NSEFO(2,"FNO"), NSECURR(3,"NSE CURR"), SLBS(19,"SLBS");
    public short value;
    public String name;

    private eExchSegment(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
