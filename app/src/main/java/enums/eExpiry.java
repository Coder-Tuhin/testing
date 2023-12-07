package enums;

/**
 * Created by xtremsoft on 8/20/16.
 */
public enum eExpiry {

    ANY(0,"Any"), NEAR(1,"Near"), NEXT(2,"Next"), FAR(3,"Far"), REST(4,"Rest");
    public short value;
    public String name;

    private eExpiry(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
