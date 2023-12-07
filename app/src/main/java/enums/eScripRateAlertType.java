package enums;

/**
 * Created by XTREMSOFT on 1/27/2017.
 */
public enum eScripRateAlertType {
    HIGH_RATE(0,"Higher Rate"), LOW_RATE(1,"Lower Rate"),EVENT(2,"Percentage Event");

    public short value;
    public String name;

    private eScripRateAlertType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }

}
