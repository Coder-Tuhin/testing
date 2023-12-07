package enums;

/**
 * Created by xtremsoft on 12/1/16.
 */
public enum eOrderType {

    NONE(-1,"NONE"),
    LIMIT(0,"Limit"),MARKET(1,"Market"),
    BUY(0,"Place Buy Order"),SELL(1,"Place Sell Order"),SQUAREOFF(2,"Square Off"),
    PLACE(0,"Place Order"),MODIFY(1,"Modify Order"),CANCEL(2,"Cancel Order"),
    MODIFYBUY(0,"Modify Buy Order"),MODIFYSELL(1,"Modify Sell Order"),HOLDING(2,"Holding");
    public short value;
    public String name;

    private eOrderType(int value, String name) {

        this.value = (short)value;
        this.name=name;
    }

}
