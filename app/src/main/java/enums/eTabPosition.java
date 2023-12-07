package enums;

/**
 * Created by xtremsoft on 7/20/17.
 */

public enum eTabPosition {

    WATCH(0), TRADE(1), MOVERS(2),BSE(3),NSE(4);
    public short value;
    private eTabPosition(int value) {
        this.value = (short)value;
    }
}
