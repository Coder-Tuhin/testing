package utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;

import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.WebViewFD;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.activities.upswing.UpswingInitActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import Structure.Other.StructBuySell;
import Structure.Request.BC.ErrorLOG;
import Structure.Response.BC.StructRegistrationResp;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.Scrip.ScripDetail;
import connection.AuthClient;
import connection.SearchClient;
import connection.SimplysaveClient;
import connection.TradeBCClient;
import connection.TradeRCClient;
import enums.eAppType;
import enums.eExch;
import enums.eExchSegment;
import enums.eForHandler;
import enums.eFragments;
import enums.ePrefTAG;
import enums.eScreen;
import enums.eShowDepth;
import enums.eTabPosition;
import fragments.NFO.NFO_Fragment;
import fragments.homeGroups.MktdepthFragmentRC;
import fragments.OptionChainNew.utility.ScripNameHandler;
import fragments.nps.NPSDashboardFragment;
import fragments.simplysave.SimplysaveFragment;
import handler.ChartDataProcess;
import handler.ClsBracketOrderBook;
import handler.ClsBracketPositionBook;
import handler.ClsCFDBook;
import handler.ClsMarginHolding;
import handler.ClsNetPosn;
import handler.ClsNewsHandler;
import handler.ClsOrderBook;
import handler.ClsTradeBook;
import handler.GroupHandler;
import handler.MktDataHandler;
import interfaces.KeyBoadInterface;
import interfaces.OnOrderSave;
import models.MessageModel;
import one.upswing.sdk.UpswingSdk;
import view.AlertBox;
import view.DialogMessages;
import fragments.fundtransfer.FundtransferFragment;
import wealth.mv.MainPage;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.QuickTransactionFragment;
import wealth.new_mutualfund.bond.BondDetails;
import wealth.new_mutualfund.investments.DIYFilter;
import wealth.new_mutualfund.investments.MissedSIPFragment;
import wealth.new_mutualfund.investments.VenturaTopPicksNewGFragment;
import wealth.new_mutualfund.ipo.ApplyIPOFragment;
import wealth.new_mutualfund.sgb.SGBSummaryFragment;

/**
 * Created by XTREMSOFT on 10/7/2016.
 */
public class GlobalClass {
    //27Jan2023 changes
    public static boolean isLiveApk = false;//True - will not write log in logact, false - write log in logcat
    public static eAppType appType = eAppType.EQUITY; // set app type, if equity then need to connect equity servers, else need to connect commodity server
    public static boolean isEquity(){
        return appType == eAppType.EQUITY;
    }
    public static boolean isCommodity(){
        return appType == eAppType.COMMODITY;
    }
    public static String mandatebankid = "";
    public static String mandateSipType = "";
    public static int SigleMandate = 0;
    public static String mandateSIPamount = "";
    public static boolean checkPaymentstatusTag = false;
    //public static boolean isSSOTag = true; //True - SSO, false - NON SSO

    //before
    public static Context latestContext = null;
    public static FragmentManager fragmentManager;
    public static HomeActivity homeActivity;

    public static boolean reportdetailsfragment = false;
    public static boolean fromDisplaySettings = false;
    public static boolean isForceReconnect = false;
    public static boolean isSessionExpired = false;
    public static boolean oneTimeTryForHoldingDetail = true;
    public static boolean showregisterauthtag = false;
    public static boolean GoogleAuthEnabled = false;
    public static String OtpResponse = "";


    public static final int currScripCodeAddition = 10000000; // used in all book
    private static final int slbsScripCodeSubstractor = 50000000;

    //connections
    public static AuthClient authClient = null;
    public static TradeBCClient tradeBCClient = null;
    public static TradeRCClient tradeRCClient = null;
    public static SimplysaveClient simplysaveClient = null;
    public static SearchClient searchClient = null;

    //events
    public static String finalEventType = null;
    public static String event_lastrate = null;
    public static int textColor = 0;

    public static StructRegistrationResp broadCastReg = new StructRegistrationResp();

    public static long currGrpCode = -1;
    public static boolean isRCconnected = false;

    //BC Handlers
    public static Handler loginHandler = null;
    public static GroupHandler groupHandler = new GroupHandler();
    public static MktDataHandler mktDataHandler = new MktDataHandler();
    public static Handler mktWatchUiHandler;
    public static Handler nseUiHandler;
    public static Handler bseUiHandler;
    public static Handler slbsUiHandler;
    public static Handler mktMoversUiHandler;

    public static Handler homeScrUiHandler;
    public static Handler searchScripUIHandler;
    public static Handler myStockUiHandler;

    //RC related Handler
    public static Handler RCAuthHandler = null;
    public static Handler tradeLoginHandler = null;
    public static Handler reportHandler = null;

    public static Handler marginUIHandler = null;
    public static Handler orderBkUIHandler = null;
    public static Handler orderBkDetailUIHandler = null;
    public static Handler bracketBkDetailUIHandler = null;
    public static Handler tradeBKUIHandler = null;
    public static Handler tradeBKDetailUIHandler = null;
    public static Handler marginTradeHandler = null;
    public static Handler netPositionUIHandler = null;
    public static Handler netPositionDetailUIHandler = null;
    public static Handler holdingFOUIHandler = null;
    public static Handler holdingEquityUIHandler = null;
    public static Handler holdingFOdetailsUIHandler = null;

    public static Handler changePwdHandler = null;
    public static Handler changePwdHandlerActivity = null;
    public static Handler forgotPwdHandler = null;
    public static Handler notificationHandler = null;
    public static Handler alertSettingHandler = null;
    public static Handler alertHandler = null;

    public static Handler readerWatchUiHandler;

    public static Handler summaryHandler;
    public static Handler investHandler;
    public static Handler withdrawHandler;
    public static Handler passbookHandler;
    public static Handler newsScripHandler;
    public static Handler optionChainHandler;
    public static Handler bracketOrderBkUIHandler = null;
    public static Handler bracketPositionBkUIHandler = null;
    public static Handler ledgerHandler;
    public static ScripNameHandler scripNameHandler;
    public static String topcompanyTag = "";
    public static String subcatystr = "";
    public static String header = "";

    public static int currentNotificatonTab = 0;
    public static int gridselection = 0;
    public static boolean isFromNotificationClick = false;
    public static boolean tradeFromDepth = false;
    public static boolean tradeFromDashboard = false;
    public static OnOrderSave onOrderSave;

    public static ClsNewsHandler clsNewsHandler;
    private static ClsNetPosn clsNetPosn;
    private static ClsOrderBook clsOrderBook;
    private static ClsTradeBook clsTradeBook;
    private static ClsCFDBook clsCFDBook;
    private static ClsBracketOrderBook clsBracketOrderBook;
    private static ClsBracketPositionBook clsBracketPositionBook;
    private static ClsMarginHolding clsMarginHolding;
    public static String BuyorSell = "";
    public static String MANDateCode = "";
    public static JSONArray jsonArrayClientBank;
    public static String TransNo = "";
    public static String PaymentMode = "";

    //pledge
    public static boolean mKeyboardStatus = false;
    public  static AlertDialog.Builder malertDialog;
    static int alertCounter = 0;
    public  static AlertDialog alertDialog;

    public static int getSLBS_ScripCodeForITS(int scripCode){
        return scripCode>slbsScripCodeSubstractor?(scripCode - slbsScripCodeSubstractor):scripCode;
    }
    public static int getSLBS_ScripCodeForNeutrino(int scripCode){
        return scripCode>slbsScripCodeSubstractor?scripCode:(scripCode + slbsScripCodeSubstractor);
    }
    public static ClsNetPosn getClsNetPosn() {
        if(clsNetPosn == null){
            clsNetPosn = new ClsNetPosn();
        }
        return clsNetPosn;
    }
    public static ClsOrderBook getClsOrderBook() {
        if(clsOrderBook == null){
            clsOrderBook = new ClsOrderBook();
        }
        return clsOrderBook;
    }
    public static ClsTradeBook getClsTradeBook() {
        if(clsTradeBook == null){
            clsTradeBook = new ClsTradeBook();
        }
        return clsTradeBook;
    }

    public static ClsCFDBook getClsCFDBook() {
        if(clsCFDBook == null){
            clsCFDBook = new ClsCFDBook();
        }
        return clsCFDBook;
    }

    public static ClsBracketOrderBook getClsBracketOrderBook() {
        if(clsBracketOrderBook == null){
            clsBracketOrderBook = new ClsBracketOrderBook();
        }
        return clsBracketOrderBook;
    }

    public static ClsBracketPositionBook getClsBracketPositionBook() {
        if(clsBracketPositionBook == null){
            clsBracketPositionBook = new ClsBracketPositionBook();
        }
        return clsBracketPositionBook;
    }

    public static ClsMarginHolding getClsMarginHolding() {
        if(clsMarginHolding == null){
            clsMarginHolding = new ClsMarginHolding();
        }
        return clsMarginHolding;
    }

    public static ChartDataProcess chartDataProcess;

    public static int mktDepthScripcode = -1;
    public static boolean isEnableSpeech = false;
    public static TextToSpeech textToSpeech;
    public static View layout;

    public static ArrayList<byte[]> listForRCRequestOnConnect = new ArrayList<>();
    public static ArrayList<MessageModel> _messageList = new ArrayList<>();
    public static DialogMessages _dialogMessages;

    public static void addMessages(MessageModel _messageModel,boolean isTradeConf){
        try{
            boolean isShowSnankBar = false;
            if(isTradeConf){
                boolean isTradeConfirmPopup = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.TRADE_CONFIRM_POPUP.name, true);
                if(!isTradeConfirmPopup){
                    isShowSnankBar = true;
                }
            }
            if(isShowSnankBar){
                BaseActivity _baseActivity = (BaseActivity) homeActivity;
                _baseActivity.runOnUiThread(() -> {
                    homeActivity.showsnackbarmsg(_messageModel.getMessage());
                });
            }else {
                _messageList.add(_messageModel);
                BaseActivity _baseActivity = (BaseActivity) homeActivity;
                _baseActivity.runOnUiThread(() -> {
                    if (GlobalClass._dialogMessages == null) {
                        GlobalClass._dialogMessages = new DialogMessages();
                    }
                    GlobalClass._dialogMessages.RefreshMessageList();
                });
            }
            PreferenceHandler.getMessageList().add(_messageModel);
            PreferenceHandler.setMessageList();
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public static void addAndroidLog(String logType, String logData,String clientCode){
        try {
            ErrorLOG log = new ErrorLOG();
            log.clientCode.setValue(clientCode);
            if (clientCode.equalsIgnoreCase("")) {
                log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            }
            log.errorMsg.setValue(logData);
            log.logType.setValue(logType);
            PreferenceHandler.getAndroidLOGList().add(log);
            PreferenceHandler.setAndroidLOGList();
        }
        catch (Exception ex){VenturaException.Print(ex);}
    }
    public static void addAndroidLogForFragment(String logType, String logData,String clientCode){
        try {
            ErrorLOG log = new ErrorLOG();
            log.clientCode.setValue(clientCode);
            if (clientCode.equalsIgnoreCase("")) {
                log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            }
            log.errorMsg.setValue(logData);
            log.logType.setValue(logType);
            PreferenceHandler.getAndroidLOGList().add(log);
            PreferenceHandler.setAndroidLOGList();

        }
        catch (Exception ex){VenturaException.Print(ex);}
    }

    public static void DismissMessageDialog(){
        _messageList.clear();
        _dialogMessages = null;
    }

    //FOR WEALTH
    public static String mfName = "mf";
    public static int selectedTabPosi = 0;
    public static String selectedTabTitle = "";
    public static Timer timer;
    public static Handler handler;
    public static TimerTask timerTask;

    public static Stack<eTabPosition> stackkk = new Stack<>();

    public static void enableDisbleMenuOptionInScrip(Menu menu, long groupCode,int scripCode){
        if(groupCode > 100){
            menu.findItem(R.id.delete).setVisible(true);
        }
        else{
            menu.findItem(R.id.delete).setVisible(false);
        }
        if (indexScripCodeOrNot(scripCode)){
            menu.findItem(R.id.marginDetails).setVisible(false);
        }else {
            menu.findItem(R.id.marginDetails).setVisible(true);
        }
    }

    public static void log(String classname,String methodname,String msg) {
        if(!isLiveApk) {
           Log.e("     " + classname + "     " + methodname, "    LOG:: " + msg);
        }
    }
    public static void onError(String msg,Exception e) {
        if(!isLiveApk) {
            System.out.println(msg);
            if(e != null) {
                VenturaException.Print(e);
            }
        }
    }
    public static void log(String msg) {
        if(!isLiveApk) {
            System.out.println(msg);
        }
    }
    public static void log(String tag,String msg) {
        if(!isLiveApk) {
            System.out.println(tag+ " : " +msg);
        }
    }

    public static void hideKeyboard(View view, Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getExpiryFromScripName(String symbol) {
        /*String[] strArr = symbol.split(" ");
        if(strArr.length >= 2){
            String strExpiry = strArr[1];
            if(strExpiry.length() >= 6) {
                return DateUtil.DToN(strExpiry);
            }
        }
        return 0;*/
        long expiry = 0;
        if (!(symbol.startsWith("NE") || symbol.startsWith("BE"))) {
            String[] data = symbol.split("-");
            if (data.length > 3) {
                expiry = DateUtil.DToN(data[data.length - 2].trim());
            } else {
                expiry = DateUtil.DToN(data[data.length - 1].trim());
            }
        }
        return expiry;
    }

    public static void notifyMktWatchScreen(int msgCode) {
        try {
            Handler handler= mktWatchUiHandler;
            if ( handler!= null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", msgCode);
                confMsgBundle.putInt("scripCode", -1);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            GlobalClass.onError("Error in GlobalClass1" , e);
        }
    }

    public static void notifyMyStockScreen(int msgCode) {
        try {
            Handler handler= myStockUiHandler;
            if ( handler!= null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", msgCode);
                confMsgBundle.putInt("scripCode", -1);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            GlobalClass.onError("Error in GlobalClass" , e);
        }
    }

    public static void notifyDepthScreen(int msgCode,int scripCode,String scripName) {
        try {
            Handler handler= MktdepthFragmentRC.mktDepthUiHandler;
            if ( handler!= null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt("msgCode", msgCode);
                confMsgBundle.putInt("scripCode", scripCode);
                confMsgBundle.putString("scripName", scripName);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            GlobalClass.onError("Error in GlobalClass" , e);
        }
    }

    public  static int getExchangeCode(String exchName)throws Exception {

        if(exchName.equalsIgnoreCase("NSE")){
            return eExchSegment.NSECASH.value;
        }else if(exchName.equalsIgnoreCase("BSE")){
            return eExchSegment.BSECASH.value;
        }else if(exchName.equalsIgnoreCase("NSE FNO") || exchName.equalsIgnoreCase("FNO")){
            return eExchSegment.NSEFO.value;
        }else if(exchName.equalsIgnoreCase("NSE CURR")){
            return eExchSegment.NSECURR.value;
        }else if(exchName.equalsIgnoreCase("SLBS")){
            return eExchSegment.SLBS.value;
        }else{
            return eExchSegment.NONE.value;
        }
    }

    public static int intTODP(Context context, int number) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, number, context.getResources().getDisplayMetrics());
    }
    public static String getLatestResultData(String value){
        try {
            String formattedValue = "";
            Double val = (Double.parseDouble(value)/10);
            if( val >= 100000){
                NumberFormat formatter = new DecimalFormat("##########0");
                formattedValue = formatter.format(val);
            } else if (val < 100000 && val >= 10000) {
                NumberFormat formatter = new DecimalFormat("###########0.0");
                formattedValue = formatter.format(val);
            } else {
                NumberFormat formatter = new DecimalFormat("###########0.00");
                formattedValue = formatter.format(val);
            }
            GlobalClass.log("Latest Value : " + formattedValue);
            return formattedValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLatestResultDate(String date) {
        try {
            String MMM[] = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            Date currDate = new Date();
            //201506
            if(!date.equalsIgnoreCase("")) {
                String yr = date.substring(2, 4);
                String mth = date.substring(4, 6);
                //GlobalClass.log("month : " + MMM[Integer.parseInt(mth)]+"year : "+yr);
                String formattedDate = MMM[Integer.parseInt(mth)] + " " + yr;
                return formattedDate;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static SpannableString getSpannableString(String text, int startPos, int endPos, float fontSize, int color){
        SpannableString ss1=  new SpannableString(text);
        if(fontSize!=0) {
            ss1.setSpan(new RelativeSizeSpan(fontSize), startPos, endPos, 0); // set size
        }
        ss1.setSpan(new ForegroundColorSpan(color), startPos, endPos, 0);// set color
        return ss1;
    }
    public static void sendMsg(Handler handler,int msgCode) {
        if (handler != null) {
            Message msg = Message.obtain(handler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putInt(eForHandler.MSG_CODE.name,msgCode);
            msg.setData(confMsgBundle);
            handler.sendMessage(msg);
        }
    }
    public static void fragmentTransaction(final Fragment fragment,final int frame,final boolean isAdd,final String TAG){
        try {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(frame, fragment,TAG);
            if (isAdd) fragmentTransaction.addToBackStack(TAG);
            //fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_right);
            //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
            dismissdialog();
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
    public static void childFragmentTransaction(final Fragment fragment,final int frame,final FragmentManager fm){
        try {
            AppCompatActivity aca = (AppCompatActivity) latestContext;
           // Fragment oldFragment = fragmentManager.findFragmentByTag(eFragments.REPORT.name);
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(frame, fragment,"");
          //fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_right);
            //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
            dismissdialog();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static CustomProgressDialog progressDialog;
    //showProgressDialog
    public static void showProgressDialog(final String msg) {
        showProgressDialog(msg,latestContext);
    }

    public static void showProgressDialog(final String msg, final Context context) {
        if (latestContext != null) {
            AppCompatActivity aca = (AppCompatActivity) latestContext;
            aca.runOnUiThread(() -> {
                try {
                    dismissdialog();
                    progressDialog = new CustomProgressDialog(aca, msg);
                    progressDialog.show();
                } catch (Exception e) {
                    VenturaException.Print(e);
                }
            });
        }
    }

    public static void dismissdialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }
    private static Toast toast;
    public static void showToast(final Context ctx, final String msg){
        try{
            final AppCompatActivity act  = (AppCompatActivity) latestContext;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View layout = LayoutInflater.from(latestContext).inflate(R.layout.custom_toast, null);
                    ((TextView) layout.findViewById(R.id.custom_toast_message)).setText(msg);
                    if (toast!=null){
                        toast.cancel();
                    }
                    toast = new Toast(latestContext);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 100);
                    toast.setView(layout);
                    toast.show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Fragment m_fragment;
    public static void openDepth(int scripCode, eShowDepth m_from, ArrayList<GroupsTokenDetails> grpScripList, StructBuySell structBS){
//        if(UserSession.getClientResponse().getServerType() == eServerType.RC){
            m_fragment = new MktdepthFragmentRC(scripCode, m_from, grpScripList, structBS,
                    ((HomeActivity) GlobalClass.homeActivity).SELECTED_RADIO_BTN, false);
       /* }else {
            m_fragment = new MktdepthFragment(scripCode, m_from, grpScripList, structBS,
                    ((HomeActivity) GlobalClass.homeActivity).SELECTED_RADIO_BTN, false);
        }*/
        fragmentTransaction(m_fragment,R.id.container_body,true, eFragments.DEPTH.name);
    }

  //  public  static boolean isNeedToReplaceMarketDepth = false;
    public static int isMktDepthOpen = -1;

    public static boolean indexScripCodeOrNot(int selectedToken){
        boolean flag = false;
        try {
            if(((selectedToken >= 200000) && (selectedToken <= 200500))){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    public static void showAlertDialog(final String data) {
        showAlertDialog(data,false);
    }

    public static void showAlertDialog(final String data,final boolean onClickExit) {
        if (latestContext!= null){
            new AlertBox(latestContext, "", "OK", data, onClickExit);
        }
    }


    private static AlertDialog marginDialog;

    public static void showmarginDetailDialog(int scripCode) {
        final ScripDetail scripDetail =  GlobalClass.mktDataHandler.getScripDetailData(scripCode);
        try {
            if (scripDetail != null){
                final AppCompatActivity aca = (AppCompatActivity) latestContext;
                aca.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder adb = new AlertDialog.Builder(aca);
                        adb.setCancelable(false);
                        adb.setTitle(scripDetail.getFormattedScripName());
                        String str = "";
                        if (scripDetail.segment.getValue() == eExch.FNO.value ||
                                scripDetail.segment.getValue() == eExch.NSECURR.value){
                            float share = scripDetail.spanMargin.getValue()+
                                    scripDetail.exposerMargin.getValue();
                            str = "Lot size = "+scripDetail.mktLot.getValue()+"\n"+
                                    "Margin/Share = "+Formatter.formatter.format(share)+"\n"+
                                      "Margin/Lot = "+Formatter.formatter.format(share*scripDetail.mktLot.getValue());
                        }else {
                            String category = scripDetail.category.getValue();
                            String marginper = Formatter.formatter.format(scripDetail.scripMargin.getValue());
                            /*
                            if (category.equals("Q")){
                                marginper = "20.00";
                            }else if (category.equals("A")){
                                marginper = "25.00";
                            }else if (category.equals("B")){
                                marginper = "33.00";
                            }else if (category.equals("C")){
                                marginper = "50.00";
                            }else {
                                marginper = "100.00";
                            }*/
                            String allowtotrade = scripDetail.isAllowtoTrade.getValue() == 1?"YES":"NO";
                            String tft = scripDetail.T4T.getValue() == 1?"YES":"NO";
                            String ill = scripDetail.Ill.getValue() == 1?"YES":"NO";

                            str = "Scrip Category = "+category+"\n"+
                                    "Scrip Margin(\u0025) = "+marginper+"\n"+
                                    "Allowed to Trade = "+allowtotrade+"\n"+
                                    "Trade for Trade = "+tft+"\n"+
                                    "Periodic call Auction Category = "+ill;

                        }
                        adb.setMessage(str);
                        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                marginDialog.dismiss();
                            }
                        });
                        marginDialog = adb.create();
                        marginDialog.show();
                        marginDialog.getButton(marginDialog.BUTTON_POSITIVE).setTextColor(ScreenColor.VENTURA);

                    }
                });


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void initialiseAll(){

        fragmentManager = null;
        homeActivity = null;

        reportdetailsfragment = false;
        fromDisplaySettings = false;

        //BC Handlers
        groupHandler = new GroupHandler();

        homeScrUiHandler = null;
        searchScripUIHandler = null;
        myStockUiHandler = null;

        //RC related Handler
        tradeLoginHandler = null;
        marginUIHandler = null;
        orderBkUIHandler = null;
        orderBkDetailUIHandler = null;
        tradeBKUIHandler = null;
        tradeBKDetailUIHandler = null;

        changePwdHandler = null;
        forgotPwdHandler = null;
        notificationHandler = null;
        alertSettingHandler = null;
        alertHandler = null;
        readerWatchUiHandler = null;

        currentNotificatonTab = 0;
        gridselection = 0 ;

       // isActiveUser = true;
        tradeFromDepth = false;
        tradeFromDashboard = false;

        onOrderSave = null;


        clsNetPosn = null;
        clsOrderBook = null;
        clsTradeBook = null;
        clsMarginHolding = null;
        chartDataProcess = null;
        clsCFDBook = null;
        clsBracketPositionBook = null;
        clsBracketOrderBook = null;

        _messageList.clear();

        isEnableSpeech = false;
        textToSpeech = null;
        layout = null;

        mfName = "mf";
        selectedTabPosi = 0;
        selectedTabTitle = "";

        timer = null;
        handler = null;
        timerTask = null;
        stackkk = new Stack<>();
    }

    public static String getFormattedAmountString(double amount){
        DecimalFormat decimalFormat = new DecimalFormat("###,##,##,##,##0.00");
        return decimalFormat.format(amount);
    }
    public static String getFormattedAmountString2(double amount){
        DecimalFormat decimalFormat = new DecimalFormat("###,##,##,##,##0");
        return decimalFormat.format(amount);
    }

    public static void openFundTransferScreen(HomeActivity context){
        context.FragmentTransaction(FundtransferFragment.newInstance(0), R.id.container_body, true);
    }

    public static void showNotEnoughBalanceMsg(HomeActivity context){
        String NO_AVL_BAL = "Your available balance is less than the amount to be invested. Please transfer funds to your account.";
        context.showMsgDialogOkWithNoTitleNoCancel(NO_AVL_BAL, (DialogInterface dialogInterface1, int j) -> {
            openFundTransferScreen(homeActivity);
        });
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void showKeyboard(final EditText ettext, Context context) {

        try{
            final AppCompatActivity act  = (AppCompatActivity) latestContext;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ettext.requestFocus();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        ettext.getWindowInsetsController().show(WindowInsetsCompat.Type.ime());
                    }else {
                        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(ettext, 0);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //added by shiva
    public static void showKeyboardPledge(View view, Context context, KeyBoadInterface keyBoadInterface) {
        if(view.requestFocus()){
            InputMethodManager imgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imgr.showSoftInput(view, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mKeyboardStatus=true;
            keyBoadInterface.HideButton();
        }
    }

    public static void hideKeyboardPledge(View view,Context context, KeyBoadInterface keyBoadInterface) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mKeyboardStatus=false;
            keyBoadInterface.ShowButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void Alert(String msg,Context context){
        malertDialog = new AlertDialog.Builder(context);
        malertDialog.setTitle("Ventura Wealth");
        malertDialog.setMessage(msg);
        malertDialog.setIcon(R.drawable.ventura_icon);
        malertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something
                alertCounter--;
                dialog.dismiss();
            }
        });
        malertDialog.setCancelable(false);
        alertDialog = malertDialog.create();
        showDialog();
    }

    public static void showDialog(){
        if(alertCounter < 1){
            alertDialog.show();
            alertCounter++;
        }
    }
    public static String getIPV4IPfromDomain(String domainName,String defaultValue){
        try {
            return defaultValue;
            /*
            InetAddress[] allAddress = InetAddress.getAllByName(domainName);
            String ipv4S = "";
            for(InetAddress inetAddress:allAddress){
                String address = inetAddress.getHostAddress();
                if(isIPV4(address)){
                    ipv4S = address;
                    break;
                }
            }
            return ipv4S.equalsIgnoreCase("")?defaultValue:ipv4S;*/
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return defaultValue;
    }
    private static boolean isIPV4(String ip){
        final String IPV4_PATTERN =
                "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        final Pattern pattern = Pattern.compile(IPV4_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
    public static void setUIHandlerToNull(){
        marginUIHandler = null;
        orderBkUIHandler = null;
        orderBkDetailUIHandler = null;
        tradeBKUIHandler = null;
        tradeBKDetailUIHandler = null;
        marginTradeHandler = null;
        netPositionUIHandler = null;
        netPositionDetailUIHandler = null;
        holdingFOUIHandler = null;
        holdingEquityUIHandler = null;
        holdingFOdetailsUIHandler = null;
    }
    //Added after New Client MF Merge
    public static JSONObject VideoURLS;

    public static void showScreen(eScreen screen) {
        final Fragment m_fragment;
        try {
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.container_body);
            switch (screen) {
                case MYWEALTH:
                    if (!(currentFragment instanceof MainPage)) {
                        m_fragment = new MainPage();
                    }else{
                        m_fragment = null;
                    }
                    break;
                case MF:
                    if (!(currentFragment instanceof MutualFundMenuNew)) {
                        m_fragment = new MutualFundMenuNew();
                    }else{
                        m_fragment = null;
                    }
                    break;
                case IPO:
                    if (UserSession.getClientResponse() != null) {
                        if (!(currentFragment instanceof ApplyIPOFragment)) {
                            m_fragment = ApplyIPOFragment.newInstance();
                        }else{
                            m_fragment = null;
                        }
                    }else
                        m_fragment = null;
                    break;
                case SGB:
                    if (!(currentFragment instanceof SGBSummaryFragment)) {
                        m_fragment = new SGBSummaryFragment();
                    }else{
                        m_fragment = null;
                    }

                    break;
                case NFO:
                    if (!(currentFragment instanceof NFO_Fragment)) {
                        m_fragment = new NFO_Fragment();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case QUICK_TRANSACT:
                    if (!(currentFragment instanceof QuickTransactionFragment)) {
                        m_fragment = new QuickTransactionFragment();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case PARKEARN:
                    if (!(currentFragment instanceof SimplysaveFragment)) {
                        m_fragment = new SimplysaveFragment();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case PERFORMINGFUNDSEQUITY:
                case PERFORMINGFUNDSDEBT:
                case PERFORMINGFUNDSHYBRID:
                case PERFORMINGFUNDSLIQUID:
                case PERFORMINGFUNDSOTHERS:

                    if (!(currentFragment instanceof VenturaTopPicksNewGFragment)) {
                        m_fragment = new VenturaTopPicksNewGFragment(screen);
                    }else{
                        m_fragment = null;
                    }
                    break;
                case EXPLORE_FUNDS:
                    if (!(currentFragment instanceof DIYFilter)) {
                        m_fragment = new DIYFilter();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case BONDS:
                    if (!(currentFragment instanceof BondDetails)) {
                        m_fragment = new BondDetails();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case NPS:
                    if (!(currentFragment instanceof NPSDashboardFragment)) {
                        m_fragment = new NPSDashboardFragment();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case Missed_SIP:
                    if (!(currentFragment instanceof MissedSIPFragment)) {
                        m_fragment = new MissedSIPFragment();
                    }else {
                        m_fragment = null;
                    }
                    break;
                case FD:
                    m_fragment = null;
                    Intent webview = new Intent(GlobalClass.latestContext, WebViewFD.class);
                    latestContext.startActivity(webview);
                    break;
                case BANK_FD:
                    m_fragment = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String partnerUID = "VNTR"; // provided by Upswing
                        UpswingSdk build = new UpswingSdk.Builder(latestContext, partnerUID, new UpswingInitActivity())
                                .setStatusBarColor(latestContext.getResources().getColor(R.color.colorPrimary))
                                .setDeviceLockedEnabled(false)
                                .build();
                        build.initializeSdk();
                    }
                    break;
                    /*
                case LINK_URL:
                    m_fragment = null;
                    try {
                        if(url.toLowerCase().contains("clientcodeventura")){
                            url = url.replace("clientcodeventura",UserSession.getLoginDetailsModel().getUserID());
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        GlobalClass.latestContext.startActivity(browserIntent);
                    }catch (Exception ex){
                        GlobalClass.onError("",ex);
                    }
                    break;*/

                default:
                    m_fragment = null;
                    break;
            }
            if (m_fragment == null) return;
            try {
                ((Activity) homeActivity).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
                ((Activity) homeActivity).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            new Handler().postDelayed(() -> fragmentTransaction(m_fragment, R.id.container_body, true, ""), 250);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
    private static void disableSSLPinnig(){
        // Disable SSL pinning for HTTPS connections
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            GlobalClass.onError("disableSSLPinnig", e);
        }
    }*/
    public static String getAMCRestrictionMsg(){
        return "AMC has restricted lumpsum (one time) investments in this scheme, we suggest you to start a SIP in this scheme.";
    }
    /*
    public static String getConfirmationMsg(String transNo){
        //Order Details, SIP Mandate for Invest, CraeteMandateNew
        //"Your payment for first installment has been successful vide transaction no." + TransNo + "."//CraeteMandateFragmentNew
        //"Your payment for first installment has been successful vide transaction no." + TransNo+"."//Order Details
        //return "Your payment for first installment has been successful vide transaction no." + TransNo+".";//SIP Mandate for Invest
        return "Your order has been placed successfully. Kindly complete the OTP authentication process in order to complete your transaction.";
    }*/
    public static String sessionExpiredMsg = "session expired";
    public static boolean isReloadMktWatch = false;
    public static int deeplinkScreen = -1;
    public static boolean isMaintanceMode = false;
    public static boolean isUPSwingPageOpen = false;

}