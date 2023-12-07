package enums;

/**
 * Created by XTREMSOFT on 1/25/2017.
 */
public enum eActivityFrom {

    SPLASH(0,"SPLASH"),NOTIFICATION(1,"NOTIFICATION");

    public short value;
    public String name;

    private eActivityFrom(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
