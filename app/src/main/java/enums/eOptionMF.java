package enums;

public enum eOptionMF {

    //TOPUP | REDEEM | SWITCH | SWP | STP | PURCHASE

    TOPUP(1,"TOPUP"),
    REDEEM(2,"REDEEM"),
    SWITCH(3,"SWITCH"),
    SWP(4,"SWP"),
    STP(5,"STP"),
    GROWTH(6,"Growth"),
    DIVIDEND(7,"Dividend"),
    SIP(8,"SIP"),
    DIY(9,"DIY"),
    PURCHASE(10,"Purchase"),
    NFO(11,"NFO")
    ;

    public short value;
    public String name;

    private eOptionMF(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
