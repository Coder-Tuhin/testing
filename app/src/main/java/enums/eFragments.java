package enums;

/**
 * Created by XTREMSOFT on 5/3/2017.
 */
public enum  eFragments {
    WATCH("watch"),
    BSE("bse"),
    NSE("nse"),
    SLBS("slbs"),
    TRADE("trade"),
    REPORT("report"),
    DEPTH("depth"),
    OPTIONCHAIN("optionchain"),
    MOVERS("movers"),
    HOME("home"),
    HOLDING_EQUITY("holdingEquity"),
    HOLDING_FO("holdingFO"),
    NETPO_DETAIL("netpoDetail"),
    ORDERBK_DETAIL("orderbkDetail"),
    TRADEBK_DETAIL("tradebkDetail"),
    CHANGE_PASS("changePass"),
    REPORT_SUMMARY("reportSummary"),
    MARGINE("margine"),
    ORDERBOOK("orderbook"),
    TRADEBOOK("tradebook"),
    NETPOSITION("netposition"),
    TOTALMSG("totalmsg"),

    NOTIFICATION("notifican"),
    NPS("nps"),
    FUNDTRANS("FundtransferFragment"),
    DASHBOARD("dashboard"),
    BACKOFFICE("backoffice"),
    PAYMENT("payment"),
    SETTINGS("setting"),
    LATEST_RESULT("latestresult"),
    VALUATION("valuation"),
    NEWS_SCRIP("newsscrip"),
    MYWEALTH("mywealth")
    ;

    public String name;

    private eFragments(String name) {
        this.name=name;
    }
}
