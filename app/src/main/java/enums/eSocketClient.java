package enums;

/**
 * Created by XTREMSOFT on 11/16/2016.
 */
public enum eSocketClient {
    AUTH(1), BC(2), INTERACTIVE(3),SIMPLYSAVE(4),SEARCHENGINE(5);
    public short value;
    private eSocketClient(int value) {
        this.value = (short)value;
    }
}
