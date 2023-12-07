package enums;

/**
 * Created by XTREMSOFT on 03-Feb-2018.
 */

public enum eTagForErrorMsg {
    ALLREADYLOGIN(1,"ALREADYLOGIN"),
    RELOGIN(2,"Relogin"),
    ITSCONNECTION_FAIL(3,"Fail to connect ITS"),
    ITSERROR(4,"its error coming"),
    ORDER_EXCH_MISMATCH(5, "Order req exch mismatch")
            ;

    public short value;
    public String name;

    private eTagForErrorMsg(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
