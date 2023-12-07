package enums;

public enum eOperationMF {

    //Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
    PURCHASE(1,"Purchase"),
    TOPUP(2,"TopUp"),
    REDEEM(3,"Redeem"),
    SPREADORDER(4,"SpreadOrder"),
    SWITCH(5,"Switch"),
    SWP(6,"SWP"),
    STP(7,"STP"),
    SIP(8,"SIP")
    ;

    public short value;
    public String name;

    private eOperationMF(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }

}
