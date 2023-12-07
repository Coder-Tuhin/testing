package enums;

/**
 * Created by XTREMSOFT on 25-Aug-2017.
 */
public enum  eTradeFrom {
    TAB(0,"TAB"),MKTDEPTH(1,"MKTDEPTH"),DASHBOARD(2,"DASHBOARD");
    public int value;
    public String name;

    private eTradeFrom(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
