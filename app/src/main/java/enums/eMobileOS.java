package enums;

/**
 * Created by xtremsoft on 1/25/17.
 */

public enum eMobileOS {

    ANDROID(1,"Android"), IOS(2,"IOS"), WINDOWS(3,"Windows");
    public short value;
    public String name;

    private eMobileOS(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
