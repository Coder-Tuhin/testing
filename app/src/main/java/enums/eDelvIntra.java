package enums;

/**
 * Created by xtremsoft on 12/2/16.
 */
public enum  eDelvIntra {
    INTRADAY(0,"Intraday"),
    DELIVERY(1,"Delivery"),
    BRACKETORDER(4,"Bracket Order"),
    COVERORDER(5,"Cover Order")
    ;
    public int value;
    public String name;
    private eDelvIntra(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
