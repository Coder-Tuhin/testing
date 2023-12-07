package enums;

/**
 * Created by XTREMSOFT on 11/16/2016.
 */
public enum  eForHandler {
    MSG_CODE("msgCode"),
    RESPONSE("response"),
    RESDATA("data"),
    TABPOSITION("tabposition"),
    SPINNERNAME("spinnerName"),
    SCRIPCODE("scripcode"),
    PARSABLE_LIST("parsablelist"),
    STRUCTURE("structure");

    public String name;
    private eForHandler(String name) {
        this.name = name;
    }
}
