package utils;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import Structure.Other.HoldingDataStatusModel;
import Structure.Request.BC.ErrorLOG;
import Structure.Response.AuthRelated.ClientLoginResponse;
import enums.WatchColumns;
import enums.eIndices;
import enums.eReports;
import fragments.sso.structure.SsoModel;
import models.BuySellModel;
import models.GrabberModel;
import models.LoginDetailsModel;
import models.MPINModel;
import models.MessageModel;
import models.NotificationModel;
import models.OrderSaveModel;
import models.TradeLoginModel;

public class PreferenceHandler {
    /*
     * In boolean checking 1 used for TRUE and 0 for FALSE
     */

    //VARIABLES
    private static int _notificationActive = Integer.MIN_VALUE;
    private static int _startPowerSaveIntent = Integer.MIN_VALUE;
    private static String _versionForWhatsNew = "";
    private static String _previousDateForHelpScreen = "";
    private static int _crossEnable = Integer.MIN_VALUE;
    private static int _displayDataOnTap = Integer.MIN_VALUE;
    private static int _chartHelp = Integer.MIN_VALUE;
    private static int _unit_withdraw = Integer.MIN_VALUE;
    private static ArrayList<GrabberModel> _graberList;
    private static String _previousLoginDate = "";
    private static int _mpinRetryCount = Integer.MIN_VALUE;
    private static int _termsConditionAccept = Integer.MIN_VALUE;
    private static int _permissionAccept = Integer.MIN_VALUE;
    private static int _listGrid = Integer.MIN_VALUE;
    private static int _fontStyle = Integer.MIN_VALUE;
    private static int _loginCount = Integer.MIN_VALUE;
    private static String _selectedReport = "";
    private static int _indicesSwitching = Integer.MIN_VALUE;
    private static int _sensexVisibility = Integer.MIN_VALUE;
    private static String _callwithIP = "";

    private static String _previousLoggedinuser = "";
    private static ArrayList<MessageModel> _messageList;
    private static LinkedHashMap<String,BuySellModel> _quickOrderList;
    private static LinkedHashMap<String, NotificationModel> _notificationList;
    private static LinkedHashMap<Integer, OrderSaveModel> _orderSaveList;

    private static int _amt_percent = Integer.MIN_VALUE;
    private static int _max_customdialog_sl = Integer.MIN_VALUE;
    private static ArrayList<ErrorLOG> _androidLOGList;
    private static String _ssoauthtoken = "";
    private static String _ssorefreshtoken = "";
    private static String _ssosessionid = "";
    private static String _isaskforgoogleauth = "";
    private static String _askforgoogleauthdate = "";

    //TAGS
    private static final String NOTIFICATION = "NOTIFICATION";
    private static final String WHATS_NEW = "WHATS_NEW";
    private static final String CROSS_ENABLE = "CROSS_ENABLE";
    private static final String CHART_HELP = "CHART_HELP";
    private static final String CHART_DISPLAY_DATA_ONTAP = "CHART_DISPLAY_DATA_ONTAP";
    private static final String INSTANT_WITHDRAW_UNIT = "INSTANT_WITHDRAW_UNIT";
    private static final String WATCH_COLUMNS = "columnsetting1";
    private static final String PREVIOUS_LOGIN_DATE = "prevDate";
    private static final String MAX_MPIN_ATTAMPTS = "max_mpin_attampts";
    private static final String TERMS_CONDITION = "tc";
    private static final String CALL_BY_IP = "callbyip";

    private static final String PERMISSION_GRANTED = "PermissionAccept";
    private static final String LOGIN_DETAILS = "loginDetails";
    private static final String AUTH_LOGIN_DETAILS = "authloginresp";
    private static final String PREVIOUS_USER = "prevuser";
    private static final String MESSAGE_LIST = "messagelist";
    private static final String LIST_GRID = "wv";
    private static final String SAVE_QUICK_ORDER = "saveQuickOrders";
    private static final String NOTIFICATION_LIST = "notification";
    private static final String ORDER_SAVE_LIST = "ordersave";
    private static final String FONT_STYLE = "fontStyle";
    private static final String LOGIN_COUNT = "logincount";
    private static final String PREVIOUS_DATE_FOR_HELPSCREEN = "prevtimestamp";
    private static final String POWERSAVE_INTENT = "skipProtectedAppCheck";
    private static final String REPORT_INTENT = "selectedreport";
    private static final String INDICES_SWITCHING = "indices_switching";
    private static final String SENSEX_VISIBILITY = "sensex_visibility";
    private static final String AMT_PERCENT = "AMT_PERCENT";
    private static final String MAX_CUSTOM_DIALOG_SL = "MAX_CUSTOM_DIALOG_SL";
    private static final String ANDROID_LOG = "ANDROID_LOG";
    private static final String HOLDING_REQ = "HOLDING_REQ1";
    private static final String SSO_AUTH = "SSO_AUTH";
    private static final String SSO_REFRESH_AUTH = "SSO_REFRESH_AUTH";
    private static final String SSO_SESSIONID = "SSO_SESSIONID";
    private static final String SsoClientDetails = "SSO_CLIENTDETAILS";
    private static final String sso_Createauth = "SSO_CREATEAUTH";
    private static final String sso_askgauthdate = "SSO_askgauthdate";

    public static HoldingDataStatusModel getHoldingStatus() {
        HoldingDataStatusModel ldm = null;
        try {
            Gson gson = new Gson();
            String json = VenturaApplication.getPreference().getSharedPrefFromTag(HOLDING_REQ+UserSession.getLoginDetailsModel().getUserID(), "");
            Type type = new TypeToken<HoldingDataStatusModel>() {}.getType();
            ldm = gson.fromJson(json, type);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        if (ldm==null)ldm=new HoldingDataStatusModel();
        return ldm;
    }
    public static void setHoldingStatus(HoldingDataStatusModel loginDetails){
        Gson gson = new Gson();
        String json = gson.toJson(loginDetails);
        VenturaApplication.getPreference().storeSharedPref(HOLDING_REQ+UserSession.getLoginDetailsModel().getUserID(),json);
    }

    public static boolean isIndicesSwitchingAvl() {
        if (_indicesSwitching == Integer.MIN_VALUE) {
            _indicesSwitching = VenturaApplication.getPreference().getSharedPrefFromTag(INDICES_SWITCHING, 1);
        }
        return _indicesSwitching > 0;
    }

    public static void setIndicesSwitching(boolean bool) {
        int _tempValue = bool ? 1 : 0;
        _indicesSwitching = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(INDICES_SWITCHING, _tempValue);
    }


    public static int getSelectedIndice() {
        if (_sensexVisibility == Integer.MIN_VALUE) {
            _sensexVisibility = VenturaApplication.getPreference().getSharedPrefFromTag(SENSEX_VISIBILITY, eIndices.SENSEX.value);
        }
        return _sensexVisibility;
    }

    public static void setSensexVisible(int value) {
        _sensexVisibility = value;
        VenturaApplication.getPreference().storeSharedPref(SENSEX_VISIBILITY, value);
    }


    public static boolean isStartPowerSaverIntent() {
        if (_startPowerSaveIntent == Integer.MIN_VALUE) {
            _startPowerSaveIntent = VenturaApplication.getPreference().getSharedPrefFromTag(POWERSAVE_INTENT, 0);
        }
        return _startPowerSaveIntent > 0;
    }

    public static void setStartPowerSaverIntent(boolean checked) {
        int _tempValue = checked ? 1 : 0;
        _permissionAccept = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(POWERSAVE_INTENT, _tempValue);
    }

    public static int getLoginCount() {
        if (_loginCount == Integer.MIN_VALUE) {
            _loginCount = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(LOGIN_COUNT, 1);
        }
        return _loginCount;
    }

    public static void setLoginCount(int count) {
        _loginCount = count;
        VenturaApplication.getPreference()
                .storeSharedPref(LOGIN_COUNT, count);
    }

    public static int getFontStyle() {
        if (_fontStyle == Integer.MIN_VALUE) {
            _fontStyle = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(FONT_STYLE, R.style.FontSizeMedium);
        }
        return _fontStyle;
    }

    public static void setFontStyle(int style) {
        _fontStyle = style;
        VenturaApplication.getPreference()
                .storeSharedPref(FONT_STYLE, style);
    }
    public static LinkedHashMap<Integer, OrderSaveModel> getOrderSaveList() {
        if (_orderSaveList == null) {
            try {
                String _tempStr = VenturaApplication.getPreference()
                        .getSharedPrefFromTag(ORDER_SAVE_LIST, "");
                if (!TextUtils.isEmpty(_tempStr)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<LinkedHashMap<Integer, OrderSaveModel>>() {}.getType();
                    _orderSaveList = gson.fromJson(_tempStr, type);
                } else {
                    _orderSaveList = new LinkedHashMap<>();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
                _orderSaveList = new LinkedHashMap<>();
            }
        }
        return _orderSaveList;
    }
    public static void setOrderSaveList() {
        Gson gson = new Gson();
        if (_orderSaveList==null)_orderSaveList = new LinkedHashMap<>();
        String json = gson.toJson(_orderSaveList);
        VenturaApplication.getPreference().storeSharedPref(ORDER_SAVE_LIST, json);
    }
    public static LinkedHashMap<String, NotificationModel> getNotificationList() {
        if (_notificationList == null) {
            try {
                String _tempStr = VenturaApplication.getPreference()
                        .getSharedPrefFromTag(NOTIFICATION_LIST, "");
                if (!TextUtils.isEmpty(_tempStr)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<LinkedHashMap<String, NotificationModel>>() {}.getType();
                    _notificationList = gson.fromJson(_tempStr, type);
                } else {
                    _notificationList = new LinkedHashMap<>();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
                _notificationList = new LinkedHashMap<>();
            }
        }
        REMOVE_PREVIOUS_DAY_DATA();

        return _notificationList;
    }
    public static void setNotificationList() {
        Gson gson = new Gson();
        if (_notificationList==null)_notificationList = new LinkedHashMap<>();
        String json = gson.toJson(_notificationList);
        VenturaApplication.getPreference().storeSharedPref(NOTIFICATION_LIST, json);
    }

    public static void REMOVE_PREVIOUS_DAY_DATA() {
        Date currDate = Calendar.getInstance().getTime();
        String currDateStr = DateUtil.getDDMMMYYYY().format(currDate);
        if (!getPreviousLoginDate().equals(currDateStr)) {
            try {
                setPreviousLoginDate(currDateStr);
                setCallWithIP("");
                setUnitWithdraw(true);
                getMessageList().clear();
                setMessageList();
                getNotificationList().clear();
                setNotificationList();

                VenturaApplication.getPreference().setGroupDetail(null);

                TradeLoginModel tradeDetails = VenturaApplication.getPreference().getTradeDetails();
                if (tradeDetails != null) {
                    tradeDetails.setDayFirstLogin(true);
                    VenturaApplication.getPreference().setTradeDetails(tradeDetails);
                    MPINModel mpinDetails = VenturaApplication.getPreference().getMPINDetails();
                    mpinDetails.addMPIN(UserSession.getLoginDetailsModel().getUserID(),tradeDetails);
                    VenturaApplication.getPreference().setMPINDetails(mpinDetails);
                    //int dateDiff = Math.abs(tradeDetails.getSaveTime().compareTo(currDate));
                    /*if (dateDiff>=90){
                        VenturaApplication.getPreference().setTradeDetails(null);
                        PreferenceHandler.setMpinRetryCount(1);
                    }*/
                }
            } catch (Exception ex) {
                VenturaException.Print(ex);
            }
        }
    }
    public static LinkedHashMap<String,BuySellModel> getQuickOrderList() {
        if (_quickOrderList == null) {
            try {
                String _tempStr = VenturaApplication.getPreference()
                        .getSharedPrefFromTag(SAVE_QUICK_ORDER, "");
                if (!TextUtils.isEmpty(_tempStr)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<LinkedHashMap<String,BuySellModel>>() {}.getType();
                    _quickOrderList = gson.fromJson(_tempStr, type);
                } else {
                    _quickOrderList = new LinkedHashMap<>();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
                _quickOrderList = new LinkedHashMap<>();
            }
        }

        return _quickOrderList;
    }

    public static void setQuickOrderList() {
        Gson gson = new Gson();
        String json = gson.toJson(_quickOrderList);
        VenturaApplication.getPreference().storeSharedPref(SAVE_QUICK_ORDER, json);
    }

    public static boolean isListView() {
        if (_listGrid == Integer.MIN_VALUE) {
            _listGrid = VenturaApplication.getPreference().getSharedPrefFromTag(LIST_GRID, 1);
        }
        return _listGrid > 0;
    }

    public static void setListView(boolean isList) {
        int _tempValue = isList ? 1 : 0;
        _listGrid = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(LIST_GRID, _tempValue);
    }

    public static ArrayList<MessageModel> getMessageList() {
        if (_messageList == null) {
            try {
                String _tempStr = VenturaApplication.getPreference()
                        .getSharedPrefFromTag(MESSAGE_LIST, "");
                if (!TextUtils.isEmpty(_tempStr)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<MessageModel>>() {}.getType();
                    _messageList = gson.fromJson(_tempStr, type);
                } else {
                    _messageList = new ArrayList<>();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
                _messageList = new ArrayList<>();
            }
        }
        //BaseActivity.REMOVE_PREVIOUS_DAY_DATA();
        return _messageList;
    }

    public static void setMessageList() {
        try {
            if (_messageList == null) _messageList = new ArrayList<>();

            Gson gson = new Gson();
            String json = gson.toJson(_messageList);
            VenturaApplication.getPreference().storeSharedPref(MESSAGE_LIST, json);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static ArrayList<ErrorLOG> getAndroidLOGList() {
        if (_androidLOGList == null) {
            try {
                String _tempStr = VenturaApplication.getPreference().getSharedPrefFromTag(ANDROID_LOG, "");
                if (!TextUtils.isEmpty(_tempStr)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<ErrorLOG>>() {}.getType();
                    _androidLOGList = gson.fromJson(_tempStr, type);
                } else {
                    _androidLOGList = new ArrayList<>();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
                _androidLOGList = new ArrayList<>();
            }
        }
        return _androidLOGList;
    }

    public static void setAndroidLOGList() {
        try {
            if (_androidLOGList == null) _androidLOGList = new ArrayList<>();
            Gson gson = new Gson();
            String json = gson.toJson(_androidLOGList);
            VenturaApplication.getPreference().storeSharedPref(ANDROID_LOG, json);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static String getPreviousLoginUser() {
        if (TextUtils.isEmpty(_previousLoggedinuser)) {
            _previousLoggedinuser = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(PREVIOUS_USER, UserSession.getLoginDetailsModel().getUserID());
        }
        return _previousLoggedinuser;
    }

    public static void setPreviousLoginUser(String currentUser) {
        _previousLoggedinuser = currentUser;
        VenturaApplication.getPreference().storeSharedPref(PREVIOUS_USER, currentUser);
    }

    public static LoginDetailsModel getLoginDetails() {
        LoginDetailsModel ldm = null;
        try {
            Gson gson = new Gson();
            String json = VenturaApplication.getPreference().getSharedPrefFromTag(LOGIN_DETAILS, "");
            Type type = new TypeToken<LoginDetailsModel>() {}.getType();
            ldm = gson.fromJson(json, type);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        if (ldm==null)ldm=new LoginDetailsModel();
        return ldm;
    }
    public static void setLoginDetails(LoginDetailsModel loginDetails){
        Gson gson = new Gson();
        String json = gson.toJson(loginDetails);
        VenturaApplication.getPreference().storeSharedPref(LOGIN_DETAILS,json);
    }
    public static ClientLoginResponse getAuthLoginResponse() {
        ClientLoginResponse ldm = null;
        try {
            Gson gson = new Gson();
            String json = VenturaApplication.getPreference().getSharedPrefFromTag(AUTH_LOGIN_DETAILS, "");
            Type type = new TypeToken<ClientLoginResponse>() {}.getType();
            ldm = gson.fromJson(json, type);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return ldm;
    }
    public static void setAuthLoginResponse(ClientLoginResponse loginDetails){
        Gson gson = new Gson();
        String json = gson.toJson(loginDetails);
        VenturaApplication.getPreference().storeSharedPref(AUTH_LOGIN_DETAILS,json);
    }
    public static boolean getPermissionGranted() {
        if (_permissionAccept == Integer.MIN_VALUE) {
            _permissionAccept = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(PERMISSION_GRANTED, 0);
        }
        return _permissionAccept > 0;
    }

    public static void setPermissionGranted(boolean status) {
        int _tempValue = status ? 1 : 0;
        _permissionAccept = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(PERMISSION_GRANTED, _tempValue);
    }

    public static boolean getTermsCondition() {
        if (_termsConditionAccept == Integer.MIN_VALUE) {
            _termsConditionAccept = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(TERMS_CONDITION, 0);
        }
        return _termsConditionAccept > 0;
    }
    public static String getCallWithIP() {
        if (_callwithIP.equalsIgnoreCase("")) {
            _callwithIP = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(CALL_BY_IP, "");
        }
        return _callwithIP;
    }
    public static void setCallWithIP(String value) {
        _callwithIP = value;
        VenturaApplication.getPreference().storeSharedPref(CALL_BY_IP, _callwithIP);
    }

    public static void setTermsCondition(boolean status) {
        int _tempValue = status ? 1 : 0;
        _termsConditionAccept = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(TERMS_CONDITION, _tempValue);
    }

    public static int mpinRetryCount() {
        if (_mpinRetryCount == Integer.MIN_VALUE) {
            _mpinRetryCount = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(MAX_MPIN_ATTAMPTS, 1);
        }
        return _mpinRetryCount;
    }

    public static void setMpinRetryCount(int count) {
        _mpinRetryCount = count;
        VenturaApplication.getPreference()
                .storeSharedPref(MAX_MPIN_ATTAMPTS, count);
    }

    public static String getPreviousLoginDate() {
        if (TextUtils.isEmpty(_previousLoginDate)) {
            _previousLoginDate = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(PREVIOUS_LOGIN_DATE, "");
            if (TextUtils.isEmpty(_previousLoginDate)){
                  Date currDate = Calendar.getInstance().getTime();
                _previousLoginDate = DateUtil.getDDMMMYYYY().format(currDate);
                setPreviousLoginDate(_previousLoginDate);
            }
        }
        return _previousLoginDate;
    }

    public static void setPreviousLoginDate(String currDate) {
        _previousLoginDate = currDate;
        VenturaApplication.getPreference().storeSharedPref(PREVIOUS_LOGIN_DATE, currDate);
    }

    public static ArrayList<GrabberModel> getGraberList() {
        if (_graberList == null) {
            try {
                String _watchColumns = VenturaApplication.getPreference()
                        .getSharedPrefFromTag(WATCH_COLUMNS, "");
                if (!TextUtils.isEmpty(_watchColumns)) {
                    _graberList = new ArrayList<>();
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<GrabberModel>>() {}.getType();
                    ArrayList<GrabberModel> _graberListtemp = gson.fromJson(_watchColumns, type);
                    for (GrabberModel model : _graberListtemp){
                        if(WatchColumns.isCollumnAvl(model)){
                            _graberList.add(model);
                        }
                    }
                } else {
                    _graberList = WatchColumns.getDefaultList();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
                _graberList = WatchColumns.getDefaultList();
            }
        }

        return _graberList;
    }

    public static void setGraberList() {
        Gson gson = new Gson();
        String json = gson.toJson(_graberList);
        if (_graberList==null) _graberList = new ArrayList<>();
        VenturaApplication.getPreference().storeSharedPref(WATCH_COLUMNS, json);
    }

    public static void RefactorGraberList(ArrayList<GrabberModel> tempList) {
        if (tempList!=null){
            ArrayList<GrabberModel> graberList = getGraberList();
            graberList.clear();
            graberList.addAll(tempList);
            setGraberList();
        }
    }


    public static boolean getNotificationActive() {
        if (_notificationActive == Integer.MIN_VALUE) {
            _notificationActive = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(NOTIFICATION, 0);
        }
        return _notificationActive > 0;
    }

    public static void setNotificationActive(boolean status) {
        int _tempValue = status ? 1 : 0;
        _notificationActive = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(NOTIFICATION, _tempValue);
    }

    public static boolean isWhatsNewShowable(String currentVersion) {
        if (TextUtils.isEmpty(_versionForWhatsNew)) {
            _versionForWhatsNew = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(WHATS_NEW, "");
        }

        boolean _tempVal = !_versionForWhatsNew.equals(currentVersion);
        if (_tempVal) {
            _versionForWhatsNew = currentVersion;
            VenturaApplication.getPreference().storeSharedPref(WHATS_NEW, currentVersion);
        }
        return _tempVal;
    }

    public static boolean isHelpScreenShowable() {
        if (TextUtils.isEmpty(_previousDateForHelpScreen)) {
            _previousDateForHelpScreen = VenturaApplication.getPreference().getSharedPrefFromTag(PREVIOUS_DATE_FOR_HELPSCREEN, "");
        }
        Date currDate = Calendar.getInstance().getTime();
        String currDateStr = DateUtil.getDDMMMYYYY().format(currDate);
        boolean _tempVal = !_previousDateForHelpScreen.equals(currDateStr);
        if (_tempVal) {
            _previousDateForHelpScreen = currDateStr;
            VenturaApplication.getPreference().storeSharedPref(PREVIOUS_DATE_FOR_HELPSCREEN, currDateStr);
        }
        return _tempVal;
    }

    public static boolean getCrossEnable() {
        if (_crossEnable == Integer.MIN_VALUE) {
            _crossEnable = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(CROSS_ENABLE, 0);
        }
        return _crossEnable > 0;
    }

    public static void setCrossEnable(boolean status) {
        int _tempValue = status ? 1 : 0;
        _crossEnable = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(CROSS_ENABLE, _tempValue);
    }

    public static boolean getDisplayOnTap() {
        if (_displayDataOnTap == Integer.MIN_VALUE) {
            _displayDataOnTap = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(CHART_DISPLAY_DATA_ONTAP, 1);
        }
        return _displayDataOnTap > 0;
    }

    public static void setDisplayOnTap(boolean status) {
        int _tempValue = status ? 1 : 0;
        _displayDataOnTap = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(CHART_DISPLAY_DATA_ONTAP, _tempValue);
    }


    public static boolean getChartHelp() {
        if (_chartHelp == Integer.MIN_VALUE) {
            _chartHelp = VenturaApplication.getPreference().getSharedPrefFromTag(CHART_HELP, 1);
        }
        return _chartHelp > 0;
    }

    public static void setChartHelp(boolean status) {
        int _tempValue = status ? 1 : 0;
        _chartHelp = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(CHART_HELP, _tempValue);
    }

    public static boolean getUnitWithdraw() {
        if (_unit_withdraw == Integer.MIN_VALUE) {
            _unit_withdraw = VenturaApplication.getPreference()
                    .getSharedPrefFromTag(INSTANT_WITHDRAW_UNIT, 1);
        }
        return _unit_withdraw > 0;
    }

    public static void setUnitWithdraw(boolean status) {
        int _tempValue = status ? 1 : 0;
        _unit_withdraw = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(INSTANT_WITHDRAW_UNIT, _tempValue);
    }
    public static String getSelectedReport() {
        if (_selectedReport == "") {
            _selectedReport = VenturaApplication.getPreference().getSharedPrefFromTag(REPORT_INTENT, eReports.MARGINE.name);
        }
        return _selectedReport;
    }

    public static void setSelectedReport(String value) {
        _selectedReport = value;
        VenturaApplication.getPreference().storeSharedPref(REPORT_INTENT, _selectedReport);
    }
    public static boolean getAmtPercent(){
        if (_amt_percent == Integer.MIN_VALUE){
            _amt_percent = VenturaApplication.getPreference().getSharedPrefFromTag(AMT_PERCENT,1);
        }
        return _amt_percent > 0;
    }
    public static void setAmtPercent(boolean amtPer){
        int _tempValue = amtPer?1:0;
        _amt_percent = _tempValue;
        VenturaApplication.getPreference().storeSharedPref(AMT_PERCENT,_tempValue);
    }
    public static int getCustomDialogMaxSL() {
        if (_max_customdialog_sl == Integer.MIN_VALUE) {
            _max_customdialog_sl = VenturaApplication.getPreference().getSharedPrefFromTag(MAX_CUSTOM_DIALOG_SL, 0);
        }
        return _max_customdialog_sl;
    }

    public static void setCustomDialogMaxSL(int value) {
        _max_customdialog_sl = value;
        VenturaApplication.getPreference().storeSharedPref(MAX_CUSTOM_DIALOG_SL, value);
    }
    public static String getSSOAuthToken() {
        if (_ssoauthtoken == "") {
            _ssoauthtoken = VenturaApplication.getPreference().getSharedPrefFromTag(SSO_AUTH, "");
        }
        return _ssoauthtoken;
    }
    public static void setSSOAuthToken(String value) {
        _ssoauthtoken = value;
        VenturaApplication.getPreference().storeSharedPref(SSO_AUTH, _ssoauthtoken);
    }
    public static String getSSORefreshToken() {
        if (_ssorefreshtoken == "") {
            _ssorefreshtoken = VenturaApplication.getPreference().getSharedPrefFromTag(SSO_REFRESH_AUTH, "");
        }
        return _ssorefreshtoken;
    }
    public static void setSSORefreshToken(String value) {
        _ssorefreshtoken = value;
        VenturaApplication.getPreference().storeSharedPref(SSO_REFRESH_AUTH, _ssorefreshtoken);
    }
    public static String getSSOSessionID() {
        if (_ssosessionid == "") {
            _ssosessionid = VenturaApplication.getPreference().getSharedPrefFromTag(SSO_SESSIONID, "");
        }
        return _ssosessionid;
    }
    public static void setSSOSessionID(String value) {
        _ssosessionid = value;
        VenturaApplication.getPreference().storeSharedPref(SSO_SESSIONID, _ssosessionid);
    }
    public static ArrayList<SsoModel> getSsoClientDetails(){
        ArrayList<SsoModel> ssoModels = null;
        try {
            Gson gson = new Gson();
            String json = VenturaApplication.getPreference().getSharedPrefFromTag(SsoClientDetails, "");
            Type type = new TypeToken<ArrayList<SsoModel>>() {}.getType();
            ssoModels = gson.fromJson(json, type);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(ssoModels == null) ssoModels = new ArrayList<SsoModel>();
        return ssoModels;

    }
    public static void setSsoClientDetails(ArrayList<SsoModel> ssoModels){
        Gson gson = new Gson();
        String json = gson.toJson(ssoModels);
        VenturaApplication.getPreference().storeSharedPref(SsoClientDetails,json);
    }
    public static void setSSOCreateAuth(String value) {
        _isaskforgoogleauth = value;
        VenturaApplication.getPreference().storeSharedPref(sso_Createauth, _isaskforgoogleauth);
    }
    public static String getSSOCreateAuth() {
        if (_isaskforgoogleauth == "") {
            _isaskforgoogleauth = VenturaApplication.getPreference().getSharedPrefFromTag(sso_Createauth, "");
        }
        return _isaskforgoogleauth;
    }
    public static void setGoogleAuthAskViewDate(String value) {
        _askforgoogleauthdate = value;
        VenturaApplication.getPreference().storeSharedPref(sso_askgauthdate, _askforgoogleauthdate);
    }
    public static String getGoogleAuthAskViewDate() {
        if (_askforgoogleauthdate == "") {
            _askforgoogleauthdate = VenturaApplication.getPreference().getSharedPrefFromTag(sso_askgauthdate, "");
        }
        return _askforgoogleauthdate;
    }
    public static void initAll() {
        _notificationActive = Integer.MIN_VALUE;
        _versionForWhatsNew = "";
        _crossEnable = Integer.MIN_VALUE;
        _chartHelp = Integer.MIN_VALUE;
        _displayDataOnTap = Integer.MIN_VALUE;
    }
}
