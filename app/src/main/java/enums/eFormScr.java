package enums;

public enum eFormScr {

    HOLDINGREPORT(0,"holdingreport"), QUICKTRANSACTION(1,"quicktransaction"),
    DIY(2,"DIY"),
    MF_MAIN_MENU(3,"mf_main_menu"),
    SIP_FRAGMENT(4,"sip_fragment"),
    NFO(5,"nfo"),
    ORDERDEATILS(6,"orderdetails");
    public short value;
    public String name;

    private eFormScr(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
