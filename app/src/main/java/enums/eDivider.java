package enums;

/**
 * Created by xtremsoft on 5/29/18.
 */

public enum eDivider {

    DIVIDER1(1,"1"),DIVIDER100000(100000,"100000");

    public int value;
    public String name;

    private eDivider(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
