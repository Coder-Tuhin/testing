package enums;

public enum eProductType {

    NONE(0,"None"),
    INTRADAY(1,"Intraday"),
    DELIVERY(2,"Delivery"),
    STOPLOSS(3,"Stop Loss"),
    BRACKETORDER(4,"Bracket"),
    COVERORDER(5,"Cover")
    ;
    public int value;
    public String name;
    private eProductType(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
