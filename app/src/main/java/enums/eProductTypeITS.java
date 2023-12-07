package enums;

public enum eProductTypeITS {

    INTRADAY(0,"Intraday"),
    DELIVERY(1,"Delivery"),
    BRACKETORDER(4,"Bracket"),
    COVERORDER(5,"Cover")
    ;
    public int value;
    public String name;
    private eProductTypeITS(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
