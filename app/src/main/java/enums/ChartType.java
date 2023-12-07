package enums;

/**
 * Created by XtremsoftTechnologies on 12/12/16.
 */

public enum ChartType {
    PRICE(0,"Price"), AREA(1,"Area"),LINEAR(1,"Linear"),VOLUME(2,"Volume"),SCROLL(3,"Scroll");

    public short value;
    public String name;

    private ChartType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}

