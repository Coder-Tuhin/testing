package enums;

/**
 * Created by xtremsoft on 8/20/16.
 */
public enum eExpiryType {

    MONTHLY(0,"Monthly"),WEEKLY(1,"Weekly") ;
    public short value;
    public String name;

    private eExpiryType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
