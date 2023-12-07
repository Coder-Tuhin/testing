package utils;

/**
 * Created by XTREMSOFT on 8/17/2016.
 */
public class Constants {

    public static final String RS = "rs";
    public static final String LACS = "lacs";
    public static final String CR = "cr";
    public static final String NSE = "NSE";
    public static final String BSE = "BSE";
    public static final String ALWAYS_VISIBLE = "Always Visible";
    public static final String INVISIBLE = "Invisible";

    public static String intraday = "Intraday";
    public static String delivery = "Delivery";

    public static final String REPORTFRAGMENT_TAG = "reportfragment";
    public static final String WEALTH_ERR = "enter correct";

    public static final String LOGOUT_FOR_WEALTH = "Please enter correct password.\n" +
            "\n" +
            "Click here to Logout and Login again.";


    public final static String ERR_MSG_TRADESERVER_CONNECTION = "Could not connect to server. Please contact our contingency dealing desk to trade.";
    public static String ERR_MSG_SERVERNOTAVL_CONNECTION = "Oops! There appears to be a technical issue at the moment. " +
            "Please contact our contingency dealing desk to trade.";
    public final static String ERR_MSG_DISCONNECTION = "App disconnected from server.";
    public final static String ERR_MSG_UNDERMAINTANANCE = "Server under maintenance, please try after some time";

    //BC
    public static final String ADD_NEW_WATCHLIST = "Add New Watchlist";
    public static final String SLBS_NOTE = "SLBS Note";


    public static final String DDMMMYYYY="ddMMMyyyy";
    public static final String DDMMYYYY="dd/MM/yyyy";
    public static final String ddMMMyy ="ddMMMyy";
    public static final String MMMYYYY="MMMyyyy";
    public static final String HHMMSS="HH:mm:ss";
    public static final String DDMMMYYHHMMSS="ddMMMyy HH:mm:ss";
    public static final String DDMMMYYHHMM="ddMMMyy HH:mm";
    public static final String YYYY="yyyy";
    public static final String DDMMYYHHMMSS="dd/MM/yyyy HH:mm:ss";


    public static final int MIN_SEARCH_LENGTH = 2;
    public static final int GROUP_SCRIP_LENGTH =50;
    public static final int GROUP_LENGTH=10;

    public static final String MKT_DEPTH_TAG="marketdepth";

    public final static String ERR_MSG="Something went wrong, please try after sometime.";
    public final static String ERR_MSG2="We hit an unexpected issue when processing your request. Please try again later. If the issue persists, you can contact.";
    public final static String NO_INTERNET="Oops! It appears you temporarily do not have internet connectivity. Try establishing connectivity and login again.";
    public final static String NO_INTERNET2="Oops! Though it appears you have internet connectivity, we are not able to reach our Server. Usually this is resolved by switching to Airplane mode and switching back. If you still face the issue restart your device and try again.";
    public final static String ERR_ADD_TO_WATCHLIST="No custom Watchlist added yet. ";
    public final static String GROUP_NAME_INVALID_MSG="Group Name should contain only alphanumeric.";
    public final static String ADD_MAX_GROUP_MSG="Max limit of 10 Groups exceeded. Please delete few Groups before adding New ones.";
    public final static String ADD_EMPTY_GROUP_MSG="Please enter the group name.";
    public final static String ADD_EMPTY_MPIN_MSG="Please enter the MPIN.";
    public final static String ADD_EMPTY_CLIENTCODE_MSG="Please enter the valid Client code.";
    public final static String ERR_ADD_SCRIP_CUSTOM_WATCHLIST=" scrip is already added in this watchlist.";

    public final static String CANCEL_PENDQTY_MSG="Order with Status Fully Executed cannot be cancelled.";
    public final static String MODIFY_PENDQTY_MSG="Order with Status Fully Executed cannot be Modified.";
    public final static String ERR_QTY_MSG="Order Quantity cannot be less than 1";
    public final static String ERR_FNO_QTY_MSG="Order Quantity should be multiple of market lot ";
    public final static String ERR_PRICE_MSG=" should be greater than 0";
    public final static String ERR_QTY_LIMIT_MSG="Max Order Quantity allowed is ";
    public final static String ERR_NET_QTY_MSG="In square off, qty cannot be more than net qty";
    public final static String ERR_HOLDING_QTY_MSG="Quantity cannot be greater than Total Qty";
    public final static String CHK_QTY_FORMODIFY="In partially modification, modify quantity should be greater than traded quantity.";
    public final static String ERR_PRICE=" should be in multiples of ";
    public final static String ERR_LIMIT_PRICE=" not within reasonable limits.";
    public final static String ERR_SERVER_CONNECTION = "Could not connect to server. Please check your internet connection.";
    public final static String SENDINGREQ_ERR = "Error in sending request.";
    public final static String ERR_MSG_CONNECTION= "Please check your internet connection";
    public final static String ERR_MSG_SLOW_CONNECTION="Your internet connection is too slow, it may take some time";
    public final static String ERR_DISC_QTY="Disclosed qty should not be greater than Quantity";
    public final static String ERR_DISC_QTY_PER="Disclosed quantity should be atleast 10% of quantity";

    public final static String ERR_BUY_TRGR_PRICE="For buy order trigger price should be less than or equal to limit price";
    public final static String ERR_SELL_TRGR_PRICE="For sell order trigger price should be greater than or equal to limit price";

    public final static String ERR_BUY_TRGR_PRICE_SLBS="For Recall order trigger price should be less than or equal to limit price";
    public final static String ERR_SELL_TRGR_PRICE_SLBS="For Lend order trigger price should be greater than or equal to limit price";

    //region DialogMessages for order modification
    public final static String ERR_NO_CHANGE_MSG="No change in previous order and modified order";
    public final static String ERR_DISC_MODIFY_MSG="Disclosed Quantity cannot be more than Pending Quantity";
    public final static String ERR_DISCPER_MODIFY_MSG="Disclosed Quantity should be atleast 10 % of Pending Quantity";

    public final static String ALREADY_TIGGERED="BSE SL Order which is already triggered cannot be modified into a SL order.Remove Trigger Price.";
    public final static String BSE_LIMIT_ORDER="BSE Limit Order cannot be modified into a SL order.Remove Trigger Price.";
    //endregion

    //login pwd
    //for chart
    public final static String STICKER_MSG = "Start drawing the sticker";

    //Tag Used for chart
    public static final String CHART_AREA = "LA";
    public static final String CHART_LINE = "LC";
    public static final String CHART_BAR = "BC";
    public static final String CHART_CANDLE = "CC";
    public static final String CHART_MINUTE = "cMin";
    public static final String CHART_VOLUME = "volumeVisibility";
    public static final String CHART_SCROLLER = "scroller";
   // public static final String CHART_CROSSHAIR = "crosshair";

    public static final int DEF_CHART_MIN = 1;
    public static final String backofficeServer = "Server not available. Please try after some time.";
    public static final String NOT_REGISTER = "You are not currently registered with ventura. Please contact ventura";

    //region DASHBOARD(DashboardFragment)
    public static final String isMgnLayoutVisible = "isMgnLayoutVisible";
    public static final String isOrderLayoutVisible = "isOrderLayoutVisible";
    public static final String isTradeLayoutVisible = "isTradeLayoutVisible";
    public static final String isNPLayoutVisible = "isNPLayoutVisible";
    public static final String showMTMTradeChart = "showMTMTrdChart";

    public static final String DRIVING_WATCH = "Drive Watch";
    public static final int DRIVING_WATCH_COUNT = 10;

    // Fragment Agruments
    public static final String ARG_PARAM1 = "arg_param1";
    public static final String ARG_PARAM2 = "arg_param2";

    // Messages
    public static final String INSTANT_WITHDRAW_AMT = "Instant withdrawable amount will be credited to you Bank Account within 30 Minutes.";
    public static final String INSTANT_WITHDRAW_UNIT = "As you opted for redemption of 'ALL UNITS'," +
            " the amount will be credited to your bank A/C on next business day.";
    public static String pleasewait = "Please wait...";
}