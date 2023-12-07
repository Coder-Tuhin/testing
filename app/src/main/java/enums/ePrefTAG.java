package enums;

/**
 * Created by XTREMSOFT on 11-Aug-2017.
 */
public enum  ePrefTAG {
    INDICES("indices"),
    //FONTSIZE("fontsize"),
    AMOUNT("amount"),
    ENENTSTYLE("eventstyle"),
    DEPTHACTIONWATCH("depthactionwatch"),
    WATCHNEWS("watchnews"),
    DEPTHNEWS("depthnews"),
    WATCHSTYLE("watchstyle"),
    PER_CHGSETTING("perchgsetting"),
    NEWS_SOUND("news_sound"),
    BEEP_SOUND("beep_sound"),
    NEWS_VIBRATION("news_vibration"),
    CLEAR_PREF("clear_pref"),
    BSE("bse"),
    NSE("nse"),
    MCX("mcx"),
    NCDEX("ncdex"),

    WEALTH("wealth"),
    TRADE_CONFIRM_POPUP("tradeconfimationpopup"),
    OLD_SEARCH_POPUP("oldsearchwindow"),
    SEARCH_SCREEN_SELECTION("searchscreenselection"),


    //ISCLIENT("isclient"),
    SECURITY("security"),
    //LOGINCOUNT("logincount"),
   // PREVTIMSTAMP("prevtimestamp"),
    //PREVDATE("prevDate"),
    COLUMN_HELP("columnhelp"),
    //CHART_HELP("charthelp"),
    QUICK_ORDER("quickorder"),
    TOKEN("token"),
    NEWS_ACTIVE("is_news"),
    LAST_NEWSTIME("last_news_time"),
    ADDGROUP_TO("addgroupto"),
    DEPTH_HELP("depth_help"),
    //MAX_MPIN_ATTAMPTS("max_mpin_attampts"),
    LAST_NOTIFICATION_TIME("last_notification_time"),
    MARGINTRADIN_TIME("margintrading_time"),
    IMEI("imeitag"),

    INVESTNOW_HELP("investnow_help"),
    HOLDING_HELP("holding_help"),
    HOLDING_MENU_HELP("holdingmenu_help"),
    MKTDEPTH_TOOLTIP_HELP("mktdepth_tooltip"),
    MKTDEPTH_TOOLTIP_PLUS("mktdepth_tooltip_plus"),
    MKTWATCH_TOOLTIP_HELP("mktwatch_tooltip"),
    DIY_HELP("diy_help");
    public String name;
    private ePrefTAG(String name) {
        this.name=name;
    }
}
