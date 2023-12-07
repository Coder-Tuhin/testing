package enums;

/**
 * Created by XTREMSOFT on 8/26/2016.
 */
public enum eMF_HoldingReportFor {
    REPORT(1,"Holding Report"),TOP_UP(2,"Top UP"),
    REDEMPTION(3,"Redemption"),
    SWITCH(4,"Switch");

    public short value;
    public String name;

    private eMF_HoldingReportFor(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
