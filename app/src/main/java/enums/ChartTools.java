package enums;

/**
 * Created by xtremsoft on 2/4/17.
 */

public enum ChartTools {

    SIMPLELINE(0,"Simple Line"),SPEEDLINES(1,"Speed Lines"),FR(2,"Fibonacci Retracement"),
    FF(3,"Fibonacci Fans"), FA(4,"Fibonacci Arcs");

    public short value;
    public String name;

    private ChartTools(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
