package enums;

public enum eScreen {
    NONE(0,"NONE"),IPO(1,"IPO"), MF(2,"MF"),MYWEALTH(3,"MYWEALTH"),
    SGB(4,"SGB"),NFO(5,"NFO"),PERFORMINGFUNDSEQUITY(6, "Equity"),
    PARKEARN(7,"PARKEARN"),QUICK_TRANSACT(8,"QUICK TRANSACTION"),
    EXPLORE_FUNDS(9,"EXPLORE FUNDS"), PERFORMINGFUNDSDEBT(10, "Debt"),
    PERFORMINGFUNDSHYBRID(11, "Hybrid"),PERFORMINGFUNDSLIQUID(12, "Liquid"),
    PERFORMINGFUNDSOTHERS(13, "Others"),BONDS(14, "Bonds"),
    LINK_URL(15, "LINK_URL"),

    FD(16,"FD"),NPS(17,"NPS"),Missed_SIP(18,"Missed SIP"),
    BANK_FD(19,"Bank FD"),

    DPHOLDING(50,"DPHOLDING"),MKTDEPTH(51,"MKTDEPTH");

    public int value;
    public String name;

    private eScreen(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }

    public static eScreen fromValue(int _value) {
        eScreen _tempReports = NONE;
        for (eScreen b : eScreen.values()) {
            if (b.value == _value) {
                _tempReports = b;
                break;
            }
        }
        return _tempReports;
    }
}
