package enums;

/**
 * Created by xtremsoft on 8/26/16.
 */
public enum eInstType {

    ALL(0,"All"), Futures(1,"Futures"), CallPut(2,"CE,PE"),Calls(3,"CE"), Puts(4,"PE");
    public short value;
    public String name;

    private eInstType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}