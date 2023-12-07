package enums;

/**
 * Created by Admin on 6/6/2016.
 */
public enum eOrderLimit {

    MAXQTY(100000,"MAXQTY"),MAXCURRQTY(10000,"MaxCurrQty"),MAXCURRIRFCQTY(1250,"IRFC");

    public int value;
    public String name;

    private eOrderLimit(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
