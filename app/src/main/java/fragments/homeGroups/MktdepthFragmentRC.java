package fragments.homeGroups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.OnBalloonOutsideTouchListener;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import Structure.Other.StructBuySell;
import Structure.Other.StructMobNetPosition;
import Structure.Request.RC.ModifyOrderReq_CASH_Pointer;
import Structure.Request.RC.ModifyOrderReq_FNO_Pointer;
import Structure.Request.RC.ModifyOrderReq_SLBM_Pointer;
import Structure.Request.RC.PlaceOrderReq_CASH_Pointer;
import Structure.Request.RC.PlaceOrderReq_FNO_Pointer;
import Structure.Request.RC.StructOCOOrder;
import Structure.Request.RC.PlaceOrderReq_SLBM_Pointer;
import Structure.Response.BC.LiteMDDetails;
import Structure.Response.BC.LiteMktDepth;
import Structure.Response.BC.NewsScripOuterStructure;
import Structure.Response.BC.NewsScripResponseInnerStructure;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.BC.StructxMKTEventRes;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.Scrip.ScripDetail;
import Structure.Response.Scrip.StructSearchScripRow1;
import Structure.news.StructNewsDisclaimer;
import butterknife.BindView;
import butterknife.ButterKnife;
import chart.GraphFragment;
import chart.TradingViewChart;
import connection.SendDataToBCServer;
import connection.SendDataToRCServer;
import enums.eAbsTicks;
import enums.eConstant;
import enums.eDelvIntra;
import enums.eDivider;
import enums.eExch;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.ePrefTAG;
import enums.eProductType;
import enums.eReports;
import enums.eSSOTag;
import enums.eServerType;
import enums.eShowDepth;
import enums.eTradeFrom;
import fragments.LatestResultFragment;
import fragments.NewsScripWiseFragment;
import fragments.ValuetionFragment;
import fragments.edis.EDISProcessFragmentMktDepth;
import handler.StrucPendingOrderSummary;
import handler.StructOpenPositionSummary;
import interfaces.OnActionWatchClick;
import interfaces.OnAlertListener;
import interfaces.OnPopupListener;
import models.OrderSaveModel;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.HttpCall;
import utils.HttpCallResp;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.ScreenColor;
import utils.TubeView;
import utils.UserSession;
import utils.VenturaException;
import view.ActionWatch;
import view.AddScripToGroupPopup;
import view.BrokerageDialog;
import view.WatchNews;
import view.AlertBox;
import view.CustomPopupWindow;
import view.help.DepthHelp;
import wealth.mv.LongShortTableViewFragment;
import wealth.wealthStructure.StructDPHoldingRow;
import wealth.wealthStructure.StructFNODepositoryRow;

/**
 * Created by XTREMSOFT on 8/22/2016.
 */
@SuppressLint("ValidFragment")
public class MktdepthFragmentRC extends Fragment implements View.OnClickListener, OnPopupListener,
        AdapterView.OnItemSelectedListener, OnAlertListener, RadioGroup.OnCheckedChangeListener, OnActionWatchClick {

    private ArrayList<MktDepthRowView> mktdepthrowviewArrayList;
    private String scripName = "", exchName = "";
    private ArrayList<String> scripNameList;
    private ArrayAdapter adapter;
    private final String BSE = "BE", NSE = "NE", className = getClass().getName();
    private StaticLiteMktWatch mktWatch;
    private HashMap<String, Integer> tempMap;
    private ArrayList<GroupsTokenDetails> grpScripList;
    private eShowDepth showDepth;
    private eOrderType buySellType, placeModifyordertype;
    private StructBuySell structBuySell = null;
    private StrucPendingOrderSummary strucPendingOrderSummary;
    private StructOpenPositionSummary strucOpenPositionSummary;
    private boolean isCancel = false;
    private short radioPos = 1;
    private int qtyChange = 1;
    private double priceChange = 1.00;
    public int scripCode;
    public int selectedExchange = -1;

    private Button btnClose, qtyMinus, qtyPlus, priceMinus, pricePlus, dqMinus,
            dqPlus, tpMinus, tpPlus, placeBtn;
    private TextView chargeEstimator;
    private CheckBox checkboxIOC, stopLossChk,ordsavechkbox;
    private RadioGroup bracketRadioGroup,mktLimitRG,delintraRG;
    //private LinearLayout deliveryintradayll;
    private RadioButton bracketAbsoluteRD,bracketTicksRD;
    private RadioButton limitRd,marketRd;
    private RadioButton deliveryRd,intradayRd;

    private EditText qtyEditText, priceEditText, discQtyEditText, triggerPriceEditText,
            bracketSquareET,bracketStopLossET,bracketTradingStopLossET;
    private ImageView ordsave_info_icon;
    private LinearLayout priceLayout, tPriceLayout, dqLayout,stoplosslayout,
            bracketorderLayout,bracketSquareOffLayout,bracketStopLossLayout, bracketTradingLayout;
    private TextView buysellTitle;
    private TextView bracketSquareOffTitle,bracketStopLossTitle,bracketTradingStopLossTitle,
    bracketSquareOffRS,bracketStopLossRS,bracketTradingStopLossRS;
    private HomeActivity.RadioButtons radioButtons;

    final String placeTAG = "place",T4TTAG = "t4t",illTAG = "ILL",banTAG = "ban",
            physicalTAG = "physical",surveilanceTAG = "surveilance",orderSaveTag = "ordersave";
    private Timer timer;
    private int timerCount = 0;
    private boolean fromDetails = false;
    private NumberFormat formatter;
    public static Handler mktDepthUiHandler;
    private StructDPHoldingRow selectedHoldingRow;
    private StructFNODepositoryRow selectedFNOHoldingRow = null;
    private int nseBseScripCode = 0;

    public MktdepthFragmentRC(){super();}

    public MktdepthFragmentRC(int selectedScripCode, eShowDepth showDepth, ArrayList<GroupsTokenDetails> grpScripList,
                              StructBuySell structBS, HomeActivity.RadioButtons radioButtons, boolean fromDetails) {
        this.showDepth = showDepth;
        this.grpScripList = new ArrayList<>(grpScripList);
        GlobalClass.mktDepthScripcode= this.scripCode = selectedScripCode;
        this.structBuySell = structBS;
        this.radioButtons = radioButtons;
        this.fromDetails = fromDetails;
    }
    private LayoutInflater layoutInflater;
    private View view, buySellBtn, orderLayout;
    private Button buyBtn, sellBtn;

    @BindView(R.id.depth_addscriptBtn)
    ImageButton depth_addscriptBtn;
    @BindView(R.id.button_valution)
    Button button_valution;
    @BindView(R.id.button_latestresult)
    Button button_latestresult;
    @BindView(R.id.button_viewchart)
    Button button_viewchart;
    @BindView(R.id.button_tradingview)
    Button button_tradingview;

    @BindView(R.id.oi_curr_value)
    TextView oi_curr;
    @BindView(R.id.oi_chg_value)
    TextView oi_chg;
    @BindView(R.id.oi_high_value)
    TextView oi_high;
    @BindView(R.id.oi_low_value)
    TextView oi_low;
    @BindView(R.id.total_vol_textview)
    TextView txtQty;
    @BindView(R.id.time_value_textview)
    TextView txtPerChg;
    @BindView(R.id.time_textview)
    TextView txtTime;
    @BindView(R.id.total_buy_value)
    TextView totBuy;
    @BindView(R.id.total_sell_value)
    TextView totSell;
    @BindView(R.id.upper_ckt_value)
    TextView upperCkt;
    @BindView(R.id.lower_ckt_value)
    TextView lowerCkt;
    @BindView(R.id.avg_value)
    TextView txtAvg;
    @BindView(R.id.trdExcLayout)
    LinearLayout trdExcLayout;
    @BindView(R.id.trdExc)
    TextView trdExc;
    @BindView(R.id.open_value)
    TextView txtOpen;
    @BindView(R.id.low_value)
    TextView txtLow;
    @BindView(R.id.vol_value)
    TextView txtVol;
    @BindView(R.id.weekhigh_value)
    TextView txt52WeekHigh;
    @BindView(R.id.pclose_value)
    TextView txtPClose;
    @BindView(R.id.high_value)
    TextView txtHigh;
    @BindView(R.id.val_value)
    TextView txtVal;
    @BindView(R.id.weeklow_value)
    TextView txt52WeekLow;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.buysellLinear)
    LinearLayout buysellLinear;
    @BindView(R.id.msgBtn)
    ImageButton msgBtn;
    @BindView(R.id.orderbookBtn)
    Button orderbookBtn;
    @BindView(R.id.tradebookBtn)
    Button tradebookBtn;
    @BindView(R.id.netpositionBtn)
    Button netpositionBtn;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.progressIndicator)
    ProgressBar progressIndicator;
    @BindView(R.id.totalBuyPer)
    TextView totalBuyPer;
    @BindView(R.id.totalSellPer)
    TextView totalSellPer;
    @BindView(R.id.indicatorLinear)
    LinearLayout indicatorLinear;

    @BindView(R.id.speedView)
    TubeView speedView;
    @BindView(R.id.speedViewLeftTv)
    TextView speedViewLeftTv;
    @BindView(R.id.speedViewRightTv)
    TextView speedViewRightTv;
    @BindView(R.id.speedometerLayout)
    LinearLayout speedometerLayout;

    @BindView(R.id.detailsRadiogrp)
    RadioGroup detailsRadiogrp;
    @BindView(R.id.news_Rd)
    RadioButton news_Rd;
    @BindView(R.id.detailRd)
    RadioButton detailRd;
    @BindView(R.id.orderRd)
    RadioButton orderRd;
    @BindView(R.id.qtyRd)
    RadioButton qtyRd;
    @BindView(R.id.details)
    LinearLayout details;
    @BindView(R.id.pendingLinear)
    LinearLayout pendingLinear;
    @BindView(R.id.oiLayout)
    LinearLayout oiLayout;
    @BindView(R.id.weekLinear)
    LinearLayout weekLinear;
    @BindView(R.id.detailsTopView)
    View detailsTopView;
    @BindView(R.id.buysellBottom)
    View buysellBottom;
    @BindView(R.id.buysellTop)
    View buysellTop;

    //for holidng value
    @BindView(R.id.holdingLinear1)
    LinearLayout holdingLinear1;
    @BindView(R.id.holdingLinear2)
    LinearLayout holdingLinear2;

    @BindView(R.id.hldqty_title)
    TextView hldqty_title;
    @BindView(R.id.holdcurr_title)
    TextView holdcurr_title;
    @BindView(R.id.holdpurprice_title)
    TextView holdpurprice_title;
    @BindView(R.id.holdpl_title)
    TextView holdpl_title;

    @BindView(R.id.hldqty_value)
    TextView hldqty_value;
    @BindView(R.id.holdcurr_value)
    TextView holdcurr_value;
    @BindView(R.id.holdpurprice_value)
    TextView holdpurprice_value;
    @BindView(R.id.holdpl_value)
    TextView holdpl_value;
    // for NSE BSE TRADE
    @BindView(R.id.nsebsetrade)
    LinearLayout nsebseTradeLayout;
    @BindView(R.id.nsebseTop)
    View nsebseTop;

    @BindView(R.id.bsenserate)
    Button nsebseLastRate;
    @BindView(R.id.nsebseratetitle)
    TextView nsebseLastRateTitle;
    @BindView(R.id.nsebseqty)
    TextView nsebseqty;
    @BindView(R.id.nsebseltptime)
    TextView nsebseltptime;
    @BindView(R.id.relativelay)
    RelativeLayout relativelay;

    //news related...
    @BindView(R.id.latestNews)
    TextView latestNews;
    @BindView(R.id.sharebtn)
    ImageView sharebtn;
    @BindView(R.id.latestnewslinear)
    LinearLayout latestnewslinear;
    //Events
    @BindView(R.id.event_linear)
    LinearLayout event_linear;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppTheme);
        ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).CheckRadioButton(radioButtons);
        ((HomeActivity) getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        buySellType = eOrderType.NONE;
        if (view != null){
            return view;
        }
        else {
            layoutInflater = inflater;
            view = inflater.inflate(R.layout.marketdepth_screen, container, false);
            ButterKnife.bind(this, view);

            buySellBtn = layoutInflater.inflate(R.layout.button_linear, null);
            buyBtn = (Button) buySellBtn.findViewById(R.id.button_buy);
            sellBtn = (Button) buySellBtn.findViewById(R.id.button_sell);
            buysellLinear.removeAllViews();
            buysellLinear.addView(buySellBtn);

            orderLayout = layoutInflater.inflate(R.layout.buysellwindownew1, null);

            //deliveryintradayll = (LinearLayout)orderLayout.findViewById(R.id.deliveryintradayll);
            delintraRG = (RadioGroup) orderLayout.findViewById(R.id.delintraRG);
            deliveryRd = (RadioButton) orderLayout.findViewById(R.id.deliveryRd);
            intradayRd = (RadioButton) orderLayout.findViewById(R.id.intradayRd);

            mktLimitRG = (RadioGroup) orderLayout.findViewById(R.id.mktLimitRG);
            limitRd = (RadioButton) orderLayout.findViewById(R.id.limitRd);
            marketRd = (RadioButton) orderLayout.findViewById(R.id.marketRd);

            btnClose = (Button) orderLayout.findViewById(R.id.btnClose);
            qtyMinus = (Button) orderLayout.findViewById(R.id.qtyMinus);
            qtyPlus = (Button) orderLayout.findViewById(R.id.qtyPlus);
            priceMinus = (Button) orderLayout.findViewById(R.id.priceMinus);
            pricePlus = (Button) orderLayout.findViewById(R.id.pricePlus);
            dqMinus = (Button) orderLayout.findViewById(R.id.dqMinus);
            dqPlus = (Button) orderLayout.findViewById(R.id.dqPlus);
            tpMinus = (Button) orderLayout.findViewById(R.id.tpMinus);
            tpPlus = (Button) orderLayout.findViewById(R.id.tpPlus);
            placeBtn = (Button) orderLayout.findViewById(R.id.placeBtn);
            chargeEstimator = (TextView) orderLayout.findViewById(R.id.charges_estimator);
            checkboxIOC = (CheckBox) orderLayout.findViewById(R.id.checkboxIOC);
            stopLossChk = (CheckBox) orderLayout.findViewById(R.id.stopLoss);
            ordsavechkbox = (CheckBox) orderLayout.findViewById(R.id.ordsavechkbox);
            qtyEditText = (EditText) orderLayout.findViewById(R.id.qty);
            priceEditText = (EditText) orderLayout.findViewById(R.id.price);
            discQtyEditText = (EditText) orderLayout.findViewById(R.id.discQty);
            triggerPriceEditText = (EditText) orderLayout.findViewById(R.id.triggerPrice);
            //price.setFilters(new InputFilter[]{new InputFilterMinMax()});
            //triggerPrice.setFilters(new InputFilter[]{new InputFilterMinMax()});
            priceLayout = (LinearLayout) orderLayout.findViewById(R.id.priceLayout);
            tPriceLayout = (LinearLayout) orderLayout.findViewById(R.id.tPriceLayout);
            dqLayout = (LinearLayout) orderLayout.findViewById(R.id.dqLayout);
            stoplosslayout = (LinearLayout) orderLayout.findViewById(R.id.stopLossLayout);
            buysellTitle =  orderLayout.findViewById(R.id.buysellTitle);

            bracketorderLayout = (LinearLayout) orderLayout.findViewById(R.id.bracketorder);
            bracketSquareOffLayout = (LinearLayout) orderLayout.findViewById(R.id.bracketSquareOffLayout);
            bracketStopLossLayout = (LinearLayout) orderLayout.findViewById(R.id.bracketStopLossLayout);
            bracketTradingLayout = (LinearLayout) orderLayout.findViewById(R.id.bracketTradingStopLossLayout);
            bracketRadioGroup = (RadioGroup) orderLayout.findViewById(R.id.bracketRG);
            bracketAbsoluteRD = (RadioButton) orderLayout.findViewById(R.id.absoluteRd);
            bracketTicksRD = (RadioButton) orderLayout.findViewById(R.id.ticksRd);
            bracketSquareET = (EditText)orderLayout.findViewById(R.id.bsquareoffEt);
            bracketStopLossET = (EditText)orderLayout.findViewById(R.id.bstoplossET);
            bracketTradingStopLossET = (EditText)orderLayout.findViewById(R.id.btradingSLET);

            bracketSquareOffTitle = orderLayout.findViewById(R.id.bsquareofftitle);
            bracketStopLossTitle = orderLayout.findViewById(R.id.bstoplosstitle);
            bracketTradingStopLossTitle = orderLayout.findViewById(R.id.btradingSLtitle);
            bracketSquareOffRS = orderLayout.findViewById(R.id.bsquareoffrs);
            bracketStopLossRS = orderLayout.findViewById(R.id.bstoplossrs);
            bracketTradingStopLossRS = orderLayout.findViewById(R.id.btradingSLrs);
            ordsave_info_icon = (ImageView) orderLayout.findViewById(R.id.ordsave_info_icon);

            ordsave_info_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = "Order details will be saved when next time you revisit this "
                            + ((structBuySell.scripDetails.segment.getValue() == eExch.NSE.value
                    || structBuySell.scripDetails.segment.getValue() == eExch.BSE.value)?"stock." : "contract.");
                    new AlertBox(GlobalClass.latestContext, "Ok", msg, MktdepthFragmentRC.this, orderSaveTag);
                }
            });
            init(view);
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());
            return view;
        }
    }
    private WatchNews watchNews = null;
    private ActionWatch actionWatch = null;

    @Override
    public void onResume() {
        super.onResume();
        try {
            mktDepthUiHandler = new MktDepthHandlerRC();
            GlobalClass.tradeFromDepth = false;
            GlobalClass.isMktDepthOpen = 1;
            sendMarketDepthReqToserver(true);
            /*if(isShowNews()) {
                sendNewsReqToserver();
            }*/
            refreshMktWatchData();
            refreshMD(scripCode);
            setPendingOrders();
            if (buysellBottom.getVisibility() == View.VISIBLE) {
                detailsTopView.setVisibility(View.GONE);
            }
            if(selectedHoldingRow != null){
                if(selectedHoldingRow.getEdisQtyForSendToEDIS() == -1) {
                    selectedHoldingRow.setEdisQtyForSendToEDIS(0);
                    buysellLinear.removeAllViews();
                    buysellLinear.addView(buySellBtn);
                    if (detailsRadiogrp.getVisibility() == View.GONE) {
                        buysellBottom.setVisibility(View.GONE);
                        detailsTopView.setVisibility(View.VISIBLE);
                    }
                }else if(selectedHoldingRow.getEdisQtyForSendToEDIS() == -1000){
                    showConfirmAlert(true,false);
                }
            }
            if(!GlobalClass.BuyorSell.equalsIgnoreCase("")) {
                if(GlobalClass.BuyorSell.equalsIgnoreCase(eOrderType.BUY.name)){
                    buySellType = eOrderType.BUY;
                }else {
                    buySellType = eOrderType.SELL;
                }
                GlobalClass.BuyorSell = "";
                openBuySellWindowHideBtn(buySellType,  eOrderType.PLACE);
            }
            try{
                if(GlobalClass.broadCastReg.isNormalMKt()){
                    checkOneTimeForLiveRateIssue();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.DEPTHNEWS.name,true)) {
                if (watchNews == null) {
                    watchNews = new WatchNews(latestNews, sharebtn, latestnewslinear, this.getContext(), this,ePrefTAG.DEPTHNEWS.name);
                }
                watchNews.startNewsTime();
            }
            if(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.DEPTHACTIONWATCH.name,true)) {
                if (actionWatch == null) {
                    actionWatch = new ActionWatch(event_linear, this.getContext(), this);
                }
                actionWatch.reloadConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mktDepthUiHandler = null;
        cancelTimer();
        cancelTimerForLiveRateIssue();
        if(watchNews != null) {
            watchNews.cancleTimer();
        }
    }

    @Override
    public void onActionWatchClick(GroupsTokenDetails groupsTokenDetails) {
        try{
            scripCode = groupsTokenDetails.scripCode.getValue();
            scripName = groupsTokenDetails.scripName.getValue();
            if (tempMap.values().contains(scripCode)) {
                spinner.setSelection(adapter.getPosition(scripName));
            } else {
                grpScripList.add(groupsTokenDetails);
                tempMap.put(groupsTokenDetails.scripName.getValue(), groupsTokenDetails.scripCode.getValue());
                if(!scripNameList.contains(groupsTokenDetails.scripName.getValue())) {
                    scripNameList.add(groupsTokenDetails.scripName.getValue());
                }
                adapter.notifyDataSetChanged();
                spinner.setSelection(adapter.getPosition(groupsTokenDetails.scripName.getValue()));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void init(View view) {
        try {
            mktdepthrowviewArrayList = new ArrayList<>();
            viewInit(view);
            tempMap = new HashMap<>();
            mdRequest();
            adapter = new ArrayAdapter(GlobalClass.latestContext, R.layout.custom_spinner_item, scripNameList);
            adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
            spinner.setAdapter(adapter);
            spinner.setSelection(adapter.getPosition(scripName));
            exchName = spinner.getSelectedItem().toString().split("-")[0];
            setExchName();
            button_valution.setOnClickListener(this);
            button_latestresult.setOnClickListener(this);
            button_viewchart.setOnClickListener(this);
            button_tradingview.setOnClickListener(this);
            depth_addscriptBtn.setOnClickListener(this);
            nsebseLastRate.setOnClickListener(this);
            buyBtn.setOnClickListener(this);
            sellBtn.setOnClickListener(this);
            msgBtn.setOnClickListener(this);
            orderbookBtn.setOnClickListener(this);
            tradebookBtn.setOnClickListener(this);
            netpositionBtn.setOnClickListener(this);
            speedometerLayout.setOnClickListener(this);
            indicatorLinear.setOnClickListener(this);
            latestNews.setOnClickListener(this);
            if (structBuySell != null) {
                nsebseLastRate.setEnabled(false);
                openBuySellWindowHideBtn(structBuySell.buyOrSell, structBuySell.modifyOrPlace);
            } else {
                spinner.setOnItemSelectedListener(this);
                structBuySell = new StructBuySell();
                if (VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.DEPTH_HELP.name, true)) {
                    Dialog depthHelp = new DepthHelp(GlobalClass.latestContext,false);
                    depthHelp.show();
                }
            }
            if(exchName.equalsIgnoreCase("SD")){
                buyBtn.setText("RECALL");
                sellBtn.setText("LEND");
            }
            IndicatorRepeatly();
            showHoldingValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHoldingValue(){

        holdingLinear1.setVisibility(View.GONE);
        holdingLinear2.setVisibility(View.GONE);

        if(GlobalClass.isEquity()) {
            button_valution.setEnabled(true);
            button_latestresult.setEnabled(true);
            if (selectedExchange == eExch.NSE.value || selectedExchange == eExch.BSE.value) {

                HashMap<String, StructDPHoldingRow> dpHoldingList = VenturaApplication.getPreference().getDPHoldingList();
                if (dpHoldingList != null) {
                    selectedHoldingRow = dpHoldingList.get(scripCode + "");
                    if (selectedHoldingRow == null) {
                        final ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
                        if (scripDetail != null) {
                            selectedHoldingRow = dpHoldingList.get(scripDetail.isin.getValue().toUpperCase());
                        }
                    }
                    if (selectedHoldingRow != null) {
                        holdingLinear1.setVisibility(View.VISIBLE);
                        holdingLinear2.setVisibility(View.VISIBLE);

                        hldqty_title.setText("Holding Qty: ");
                        holdcurr_title.setText("Curr Value: ");
                        holdpurprice_title.setText("Avg Pur Price: ");
                        holdpl_title.setText("PL: ");

                        String hQty = selectedHoldingRow.getHqty() + "";
                        if (!selectedHoldingRow.gethType().equalsIgnoreCase("")) {
                            hQty = hQty + " (" + selectedHoldingRow.gethType().trim() + ")";
                        }
                        hldqty_value.setText(hQty);
                        holdpurprice_value.setText(selectedHoldingRow.getPurchasePriceStr());
                        holdcurr_value.setText(selectedHoldingRow.getCurrValueStr(mktWatch.getLastRate()));

                        String currPLStr = selectedHoldingRow.getCurrPLStr(mktWatch.getLastRate());
                        if (currPLStr.startsWith("-")) {
                            holdpl_value.setText(currPLStr.substring(1));
                            holdpl_value.setTextColor(ScreenColor.RED);
                        } else {
                            holdpl_value.setText(currPLStr);
                            holdpl_value.setTextColor(ScreenColor.GREEN);
                        }

                        if (selectedHoldingRow.getIsNewWealth() == 1) {
                            SpannableString content = new SpannableString(hldqty_value.getText());
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            hldqty_value.setText(content);
                            hldqty_value.setTextColor(Color.CYAN);
                            holdcurr_value.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToShortLongScr();
                                }
                            });
                            holdpurprice_value.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToShortLongScr();
                                }
                            });
                            hldqty_value.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToShortLongScr();
                                }
                            });
                            holdpl_value.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goToShortLongScr();
                                }
                            });
                        }
                    }
                }
            } else {
                ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
                if ((scripDetail != null)) {
                    if (((scripDetail.segment.getValue() != eExch.NSE.value)
                            && (scripDetail.segment.getValue() != eExch.BSE.value)
                            && (scripDetail.nseScripCode.getValue() <= 0))) {
                        button_valution.setEnabled(false);
                        button_latestresult.setEnabled(false);
                    }
                }
                HashMap<String, StructFNODepositoryRow> dpHoldingList = VenturaApplication.getPreference().getFNOHoldingList();
                if (dpHoldingList != null && scripDetail != null) {
                    selectedFNOHoldingRow = dpHoldingList.get(scripCode + "");
                    if (selectedFNOHoldingRow != null) {
                        holdingLinear1.setVisibility(View.VISIBLE);
                        holdingLinear2.setVisibility(View.VISIBLE);

                        hldqty_title.setText("B/f Posn: ");
                        holdcurr_title.setText("PL on Prev Close:");
                        holdpurprice_title.setText("Cost:");
                        holdpl_title.setText("PL on Cost:");

                        String hQty = selectedFNOHoldingRow.getQty() + "";
                        hldqty_value.setText(hQty);
                        String sAvgCost = (selectedFNOHoldingRow.getAvgCostDouble() == 0) ? "$" : formatter.format(selectedFNOHoldingRow.getAvgCostDouble());
                        holdpurprice_value.setText(sAvgCost);

                        int mktLot = 1;
                        if (scripDetail.segment.getValue() == eExch.NSECURR.value) {
                            mktLot = scripDetail.mktLot.getValue();
                        }
                        String PLPrvCStr = selectedFNOHoldingRow.getPLonPrevCloseStr(mktWatch.getLastRateNoRound(), mktWatch.getPClose(), mktLot);
                        if (PLPrvCStr.startsWith("-")) {
                            holdcurr_value.setText(PLPrvCStr.substring(1));
                            holdcurr_value.setTextColor(ScreenColor.RED);
                        } else {
                            holdcurr_value.setText(PLPrvCStr);
                            holdcurr_value.setTextColor(ScreenColor.GREEN);
                        }
                        String currPLStr = selectedFNOHoldingRow.getPLonCostStr(mktWatch.getLastRateNoRound(), mktLot);
                        if (currPLStr.startsWith("-")) {
                            holdpl_value.setText(currPLStr.substring(1));
                            holdpl_value.setTextColor(ScreenColor.RED);
                        } else {
                            holdpl_value.setText(currPLStr);
                            holdpl_value.setTextColor(ScreenColor.GREEN);
                        }
                    }
                }
            }
        } else  if(GlobalClass.isCommodity()){
            button_valution.setVisibility(View.INVISIBLE);
            button_latestresult.setVisibility(View.INVISIBLE);

            button_valution.setEnabled(false);
            button_latestresult.setEnabled(false);
        }
    }
    private void goToShortLongScr(){
        try{
            Fragment fragment = new LongShortTableViewFragment(scripName,
                    scripCode, selectedHoldingRow.getISIN(),UserSession.getLoginDetailsModel().getUserID(),true);
            FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment,"");
            fragmentTransaction.addToBackStack("");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private boolean isShowNews(){
        boolean flag = GlobalClass.indexScripCodeOrNot(scripCode);
        if(flag){
            return false;
        } else if(scripName.contains("NIFTY")){
            return false;
        }
        return true;
    }
    private void IndicatorRepeatly() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timerCount < 4) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableDisableIndicator();
                        }
                    });
                    timerCount++;
                } else {
                    cancelTimer();
                }
            }
        }, 2500, 2500);
    }

    private void cancelTimer() {
        timerCount = 0;
        if (timer != null) timer.cancel();
    }
    private Timer timerRateIssue = null;
    private void checkOneTimeForLiveRateIssue() {
        cancelTimerForLiveRateIssue();
        timerRateIssue = new Timer();
        timerRateIssue.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    LiteMktDepth m_structMktDepth = GlobalClass.mktDataHandler.getMktDepthData(scripCode);
                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode, false);

                    if (mktWatch == null) {
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendMarketWatchForceReq(scripCode);
                    }
                    if (m_structMktDepth == null) {
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendMarketDepthForceReq(scripCode);
                    }
                    try {
                        if(mktWatch != null && m_structMktDepth != null) {
                            int timeDiff = m_structMktDepth.time.getDateInNumber() - mktWatch.getLw().time.getDateInNumber();
                            if (timeDiff > 60000) {
                                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                                sendDataToServer.sendMarketWatchForceReq(scripCode);
                            }
                            else if (timeDiff < -5000) {
                                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                                sendDataToServer.sendMarketDepthForceReq(scripCode);
                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    cancelTimerForLiveRateIssue();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }, 10000);
    }

    private void cancelTimerForLiveRateIssue() {
        if (timerRateIssue != null){
            timerRateIssue.cancel();
            timerRateIssue = null;
        }
    }

    private void setPendingOrders() {
        detailsRadiogrp.setVisibility(View.GONE);
        news_Rd.setVisibility(View.GONE);
        qtyRd.setVisibility(View.GONE);
        orderRd.setVisibility(View.GONE);
        detailRd.setVisibility(View.VISIBLE);
        detailsTopView.setVisibility(View.VISIBLE);
        if(isShowNews()){
            detailsRadiogrp.setVisibility(View.VISIBLE);
            news_Rd.setVisibility(View.VISIBLE);
        }
        if (UserSession.isTradeLogin()) {
            orderRd.setVisibility(View.GONE);
            qtyRd.setVisibility(View.GONE);

            strucPendingOrderSummary = GlobalClass.getClsOrderBook().getPendingOrdersForScripCode(scripCode);
            if (strucPendingOrderSummary.scripCode.getValue() == scripCode) {
                detailsRadiogrp.setVisibility(View.VISIBLE);
                buysellBottom.setVisibility(View.GONE);
                detailsTopView.setVisibility(View.GONE);
                orderRd.setVisibility(View.VISIBLE);
                orderRd.setText("Pend:" + strucPendingOrderSummary.totalPending.getValue());
            }
            int netQty = 0;
            if (GlobalClass.getClsNetPosn().m_allScripCode.contains(scripCode+"")) {
                strucOpenPositionSummary = GlobalClass.getClsNetPosn().getOpenPositionsForScripCode(scripCode);
                netQty = strucOpenPositionSummary.totalPosition.getValue();
                detailsRadiogrp.setVisibility(View.VISIBLE);
                buysellBottom.setVisibility(View.GONE);
                detailsTopView.setVisibility(View.GONE);
                qtyRd.setVisibility(View.VISIBLE);
                if (netQty != 0) {
                    String netText = netQty > 0 ? "+" + netQty : "" + netQty;
                    qtyRd.setText("Posn:" + netText);
                }else{
                    qtyRd.setText("Posn Sq Up");
                }
            }
            if (radioPos == 2) {
                generatePendOdr();
            } else if (radioPos == 3) {
                if (netQty != 0)
                    generatenetQty();
            }
        }
        if (detailsRadiogrp.getVisibility() == View.VISIBLE) {
            detailsRadiogrp.setOnCheckedChangeListener(MktdepthFragmentRC.this);
        } else {
            details.setVisibility(View.VISIBLE);
        }

    }

    private void viewInit(View view) {

        MktDepthRowView row0 = new MktDepthRowView(view.findViewById(R.id.noOfBuyers0),
                view.findViewById(R.id.buyQty0), view.findViewById(R.id.buyRate0),
                view.findViewById(R.id.sellRate0),view.findViewById(R.id.sellQty0), view.findViewById(R.id.noOfSellers0));

        MktDepthRowView row1 = new MktDepthRowView(view.findViewById(R.id.noOfBuyers1),
                view.findViewById(R.id.buyQty1), view.findViewById(R.id.buyRate1),
                view.findViewById(R.id.sellRate1), view.findViewById(R.id.sellQty1), view.findViewById(R.id.noOfSellers1));

        MktDepthRowView row2 = new MktDepthRowView(view.findViewById(R.id.noOfBuyers2),
                view.findViewById(R.id.buyQty2), view.findViewById(R.id.buyRate2),
                view.findViewById(R.id.sellRate2),view.findViewById(R.id.sellQty2), view.findViewById(R.id.noOfSellers2));

        MktDepthRowView row3 = new MktDepthRowView(view.findViewById(R.id.noOfBuyers3),
                view.findViewById(R.id.buyQty3), view.findViewById(R.id.buyRate3),
                view.findViewById(R.id.sellRate3), view.findViewById(R.id.sellQty3),view.findViewById(R.id.noOfSellers3));

        MktDepthRowView row4 = new MktDepthRowView(view.findViewById(R.id.noOfBuyers4),
                view.findViewById(R.id.buyQty4), view.findViewById(R.id.buyRate4),
                view.findViewById(R.id.sellRate4),  view.findViewById(R.id.sellQty4),view.findViewById(R.id.noOfSellers4));

        mktdepthrowviewArrayList.add(row0);
        mktdepthrowviewArrayList.add(row1);
        mktdepthrowviewArrayList.add(row2);
        mktdepthrowviewArrayList.add(row3);
        mktdepthrowviewArrayList.add(row4);
    }

    private void enableDisableIndicator() {
        try {
            if (indicatorLinear.getVisibility() == View.VISIBLE) {
                indicatorLinear.setVisibility(View.GONE);
                speedometerLayout.setVisibility(View.VISIBLE);
                changeIndicatorsLabelColor(R.color.white, R.color.highlight);
            } else {
                indicatorLinear.setVisibility(View.VISIBLE);
                speedometerLayout.setVisibility(View.GONE);
                changeIndicatorsLabelColor(R.color.highlight, R.color.white);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void setSpeedOMeterData(StaticLiteMktWatch mktWatch) {
        try {
            if (mktWatch.getPerChg() < 0) {
                speedView.setIndicatorColor(ScreenColor.RED);
            } else {
                speedView.setIndicatorColor(ScreenColor.GREEN);
            }
            speedView.setMinSpeed((int) mktWatch.getLowerCkt());
            speedView.setMaxSpeed((int) mktWatch.getUpperCkt());
            speedView.setSpeedAt(mktWatch.getLastRate());
            speedView.setWithEffects3D(true);
            speedViewLeftTv.setText(Formatter.toTwoDecimalValue(mktWatch.getLastRate() - mktWatch.getLowerCkt()));
            speedViewRightTv.setText(Formatter.toTwoDecimalValue(mktWatch.getUpperCkt() - mktWatch.getLastRate()));
        } catch (Exception e) {
            GlobalClass.onError("Error In " + className, e);
        }
    }

    private void changeIndicatorsLabelColor(int buySellIndicColor, int speedoMeterIndicatorColor) {
        try {
            totBuy.setTextColor(getResources().getColor(buySellIndicColor));
            totSell.setTextColor(getResources().getColor(buySellIndicColor));
            lowerCkt.setTextColor(getResources().getColor(speedoMeterIndicatorColor));
            upperCkt.setTextColor(getResources().getColor(speedoMeterIndicatorColor));
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void setBuySellIndicator(int buyQty, int sellQty) {
        try {
            int totQty = buyQty + sellQty;
            if (totQty > 0) {
                float totBuyPerF = (((float) buyQty / (float) totQty)) * 100;
                float totSellPerF = (((float) sellQty / (float) totQty)) * 100;
                totalBuyPer.setText(Formatter.toTwoDecimalValue(totBuyPerF)+"%");
                totalSellPer.setText(Formatter.toTwoDecimalValue(totSellPerF)+"%");
                progressIndicator.setProgressDrawable(getResources().getDrawable(R.drawable.progressindicator_active));
                progressIndicator.setProgress((int) totBuyPerF);
            } else {
                totalBuyPer.setText("0%");
                totalSellPer.setText("0%");
                progressIndicator.setProgressDrawable(getResources().getDrawable(R.drawable.progressindicator_inactive));
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //case R.id.be_ne_btn:
            case R.id.bsenserate:
                if (!UserSession.getLoginDetailsModel().isClient()){
                    GlobalClass.showToast(GlobalClass.latestContext,Constants.NOT_REGISTER);
                    return;
                }
                nsebseExchBTNCLICK(true);
                break;

            case R.id.depth_addscriptBtn:

                //showToolTip2();

                StructSearchScripRow1 ssr = new StructSearchScripRow1();
                ssr.isSelected = true;
                ssr.scripCode.setValue(scripCode);
                ssr.expiry.setValue((int) GlobalClass.getExpiryFromScripName(scripName));
                ssr.formattedScripName = scripName;
                ArrayList<StructSearchScripRow1> aar = new ArrayList<>();
                aar.add(ssr);
                AddScripToGroupPopup addScripToGroupPopup = new AddScripToGroupPopup(aar);


                /*
                boolean isOldSearchPopup = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.OLD_SEARCH_POPUP.name,  false);
                if(ObjectHolder.connconfig.getSearchEngineIP().equalsIgnoreCase("") || isOldSearchPopup) {
                    CustomPopupWindow customPopupWindow = new CustomPopupWindow(this);
                    customPopupWindow.openSearchScripWindow();
                }else {
                    SearchFragment m_fragment = new SearchFragment();
                    GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "searchf");
                }*/
                break;
            case R.id.button_valution:
                showValuation();
                break;
            case R.id.button_latestresult:
                showLatestResult();
                break;
            case R.id.button_viewchart:
                showChart();
                break;
            case R.id.button_tradingview:
                showTradingView();
                break;
            case R.id.button_buy:
                if(GlobalClass.isMaintanceMode){
                    String msg = "This section is temporarily unavailable during our morning data sync. Please check back after some times for full access";
                    GlobalClass.showToast(getContext(),msg);
                }
                else if (!GlobalClass.indexScripCodeOrNot(scripCode)) {
                    if(mktWatch.getSegment() == eExch.SLBS.value){
                        if(UserSession.getClientResponse().isSLBMActivated()){
                            openBuySellWindowHideBtn(eOrderType.BUY, eOrderType.PLACE);
                            /*
                            String msg = GlobalClass.clsMarginHolding.isRecallOrder(scripCode);
                            if(msg.equalsIgnoreCase("")) {
                                openBuySellWindowHideBtn(eOrderType.BUY, eOrderType.PLACE);
                            }else{
                                GlobalClass.showAlertDialog(msg);
                            }*/
                        }else{
                            GlobalClass.showAlertDialog("SLBS Segment not allow");
                        }
                    }else {
                        openBuySellWindowHideBtn(eOrderType.BUY, eOrderType.PLACE);
                    }
                }
                break;
            case R.id.button_sell:
                if(GlobalClass.isMaintanceMode){
                    String msg = "This section is temporarily unavailable during our morning data sync. Please check back after some times for full access";
                    GlobalClass.showToast(getContext(),msg);
                }
                else if (!GlobalClass.indexScripCodeOrNot(scripCode)) {
                    /*boolean isOpen = true;
                    if(selectedHoldingRow != null && selectedHoldingRow.iseDISNeedToDo()){
                        isOpen = false;
                        new AlertBox(GlobalClass.latestContext, "Yes", "No", "Are you want to eDIS for this order?", this, "edis");
                    }
                    if(isOpen) {*/
                        if (mktWatch.getSegment() == eExch.SLBS.value) {
                            if (UserSession.getClientResponse().isSLBMActivated()) {
                                openBuySellWindowHideBtn(eOrderType.SELL, eOrderType.PLACE);
                            } else {
                                GlobalClass.showAlertDialog("SLBS Segment not allow");
                            }
                        } else {
                            openBuySellWindowHideBtn(eOrderType.SELL, eOrderType.PLACE);
                        }
                    //}
                }
                break;
            case R.id.speedometerLayout:
            case R.id.indicatorLinear:
                cancelTimer();
                enableDisableIndicator();
                break;
            case R.id.msgBtn:
                openReport(eReports.TOTAL_MSG);
                break;
            case R.id.orderbookBtn:
                openReport(eReports.ORDERBOOK);
                break;
            case R.id.tradebookBtn:
                openReport(eReports.TRADEBOOK);
                break;
            case R.id.netpositionBtn:
                openReport(eReports.NET_POSITION);
                break;
            case R.id.btnClose:
                if (structBuySell.showDepth != eShowDepth.MKTWATCH) {
                    GlobalClass.fragmentManager.popBackStackImmediate();
                } else {
                    buysellLinear.removeAllViews();
                    buysellLinear.addView(buySellBtn);
                    if (detailsRadiogrp.getVisibility() == View.GONE) {
                        buysellBottom.setVisibility(View.GONE);
                        detailsTopView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.placeBtn:
                GlobalClass.DismissMessageDialog();
                if (structBuySell.netPosn != null) {
                    if (Integer.parseInt(qtyEditText.getText().toString().trim()) > structBuySell.netQty.getValue()) {
                        String msg = "Max qty allowed is " + structBuySell.netQty.getValue();
                        new AlertBox(GlobalClass.latestContext, "", "OK", msg, false);
                        //GlobalClass.showToast(GlobalClass.latestContext, "Max qty allowed is "+structBuySell.netQty.getValue());
                    } else {
                        placeBtnClick(false);
                    }
                } else {
                    placeBtnClick(false);
                }
                break;
            case R.id.charges_estimator:
                chargeEstimatorClick();
                break;
            case R.id.qtyPlus:
                setQty(qtyEditText, true);
                break;
            case R.id.qtyMinus:
                setQty(qtyEditText, false);
                break;
            case R.id.priceMinus:
                setPrice(priceEditText, false);
                break;
            case R.id.pricePlus:
                setPrice(priceEditText, true);
                break;
            case R.id.dqMinus:
                setQty(discQtyEditText, false);
                break;
            case R.id.dqPlus:
                setQty(discQtyEditText, true);
                break;
            case R.id.tpPlus:
                setPrice(triggerPriceEditText, true);
                break;
            case R.id.tpMinus:
                setPrice(triggerPriceEditText, false);
                break;
        }
    }


    private void setPrice(EditText price, boolean forIncrement) {
        double pric = 0;
        if (!price.getText().toString().equalsIgnoreCase("")) {
            pric = Double.parseDouble(price.getText().toString().replace(",", ""));
        }
        if (forIncrement) {
            price.setText(incrementValue(pric) + "");
        } else {
            price.setText(decrementValue(pric) + "");
        }
    }

    private void setQty(EditText qty, boolean forIncrement) {
        try {
            String rawQty = qty.getText().toString().isEmpty() ? "0" : qty.getText().toString();
            if (forIncrement) {
                qty.setText(incrementQty(Double.parseDouble(rawQty)));
            } else {
                qty.setText(decrementQty(Double.parseDouble(rawQty)));
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private String incrementQty(double value) {
        double val = (Math.round((double) value / (double) qtyChange) + 1) * (double) qtyChange;
        return String.format("%.0f", val);
    }

    private String decrementQty(double value) {
        double val1 = (Math.ceil((double) value / (double) qtyChange) - 1) * (double) qtyChange;
        if (val1 < 0) {
            val1 = 0;
        }
        return String.format("%.0f", val1);
    }

    private String incrementValue(double value) {
        int sub = 1;
        double val = (Math.round((double) value / (double) priceChange) + sub) * (double) priceChange;
        return String.format((selectedExchange==eExch.NSECURR.value?"%.4f":"%.2f"), val);
    }

    private String decrementValue(double value) {

        int sub = selectedExchange == eExch.NSECURR.value?1:1;
        double val = Math.ceil((double) value / (double) priceChange);
        val = val - sub;
        val = val * priceChange;
        double val1 = (Math.ceil((double) value / (double) priceChange) - sub) * (double) priceChange;
        if (val1 < 0) {
            val1 = 0;
        }
        return String.format((selectedExchange==eExch.NSECURR.value?"%.4f":"%.2f"), val1);
    }

    private void openReport(eReports ereports) {
        if (UserSession.isTradeLogin()) {
            ((HomeActivity) getActivity()).setSpinner(ereports.name,false);
            GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "mktdepth");
        } else {
            GlobalClass.fragmentTransaction(new TradeFragment(eTradeFrom.MKTDEPTH), R.id.container_body, true, "mktdepth");
        }
    }

    private void openBuySellWindowHideBtn(eOrderType bsorderType, eOrderType plcModOrdType) {
        try {
            if (UserSession.isTradeLogin()) {
                this.placeModifyordertype = plcModOrdType;
                this.buySellType = bsorderType;
                if (mktDepthUiHandler == null) {
                    mktDepthUiHandler = new MktDepthHandlerRC();
                }
                final ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
                if (scripDetail != null) {
                    if(mktWatch.getSegment() == eExch.SLBS.value) {
                        if (UserSession.getClientResponse().isSLBMActivated()) {
                            String msg = bsorderType == eOrderType.BUY?
                                    GlobalClass.getClsMarginHolding().isRecallOrder(scripCode):
                                    GlobalClass.getClsMarginHolding().isLendOrder(scripDetail.symbol.getValue());
                            if (!msg.equalsIgnoreCase("")) {
                                GlobalClass.showAlertDialog(msg);
                                return;
                            }
                        }
                    }
                    buysellLinear.removeAllViews();
                    buysellLinear.addView(orderLayout);
                    if (detailsRadiogrp.getVisibility() == View.GONE) {
                        buysellBottom.setVisibility(View.VISIBLE);
                        detailsTopView.setVisibility(View.GONE);
                    }
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, buysellTop.getBottom());
                        }
                    });
                    if (!structBuySell.fromSave) {
                        structBuySell.qty.setValue(0);
                        structBuySell.discloseQty.setValue(0);
                        structBuySell.limitPrice.setValue(0);
                        structBuySell.triggerPrice.setValue(0);
                        structBuySell.isStopLoss.setValue(false);
                        structBuySell.isMarket.setValue(false);
                    }
                    structBuySell.mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
                    qtyEditText.setText("");
                    priceEditText.setText("");
                    discQtyEditText.setText("");
                    triggerPriceEditText.setText("");
                    if(scripDetail.segment.getValue() == eExch.NSECURR.value){
                        qtyEditText.setHint("Lots");
                        discQtyEditText.setHint("Lots");
                    }else{
                        qtyEditText.setHint("Qty");
                        discQtyEditText.setHint("Qty");
                    }
                    openBuySellWindow(buySellType, scripDetail, placeModifyordertype);
                } else {
                    GlobalClass.showProgressDialog("Please wait...");
                }
            } else {
                GlobalClass.BuyorSell = bsorderType.name;
                buysellLinear.removeAllViews();
                buysellLinear.addView(buySellBtn);
                if (detailsRadiogrp.getVisibility() == View.GONE) {
                    buysellBottom.setVisibility(View.GONE );
                    detailsTopView.setVisibility(View.VISIBLE);
                }
                GlobalClass.fragmentTransaction(new TradeFragment(eTradeFrom.MKTDEPTH), R.id.container_body, true, "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void openBuySellWindow(final eOrderType orderType, final ScripDetail scripDetail, final eOrderType placeModify) {

        structBuySell.buyOrSell = orderType;
        structBuySell.modifyOrPlace = placeModify;
        structBuySell.scripDetails = scripDetail;
        structBuySell.mktWatch = mktWatch;
        structBuySell.showDepth = showDepth;
        tPriceLayout.setVisibility(View.INVISIBLE);
        priceEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        if(scripDetail.segment.getValue() == eExch.FNO.value){
            priceEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        if(scripDetail.segment.getValue() == eExch.NSECURR.value){
            qtyEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5) });
            discQtyEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5) });
            priceEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
            triggerPriceEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
            bracketSquareET.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
            bracketStopLossET.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
            bracketTradingStopLossET.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
        }
        else{
            qtyEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6) });
            discQtyEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6) });
            priceEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
            triggerPriceEditText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
            bracketSquareET.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
            bracketStopLossET.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
            bracketTradingStopLossET.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });
        }

        initIOCCheck();
        initStopLoss();
        initOrderSpinner();
        initMarketSpinner();
        finalInit();
        btnClose.setOnClickListener(this);
        placeBtn.setOnClickListener(this);
        chargeEstimator.setOnClickListener(this);
        qtyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if(qtyEditText.getText().toString().equalsIgnoreCase("")){
                        enableOrdeSaveCheckBox(false);
                    }else {
                        enableOrdeSaveCheckBox(true);
                        OrderSaveModel orderSaveModel = PreferenceHandler.getOrderSaveList().get(scripCode);
                        if (orderSaveModel != null && ordsavechkbox.isChecked()) {
                            if (!(orderSaveModel.getQty() + "").equalsIgnoreCase(qtyEditText.getText().toString())) {
                                ordsavechkbox.setChecked(false);
                            }
                        }
                    }
                } catch (Exception ex) {
                    GlobalClass.onError("", ex);
                }
            }
        });
    }
    private void enableOrdeSaveCheckBox(boolean btn){
        ordsavechkbox.setEnabled(btn);
        ordsavechkbox.setAlpha(btn?1:0.5f);
    }

    private void finalInit() {
        if (structBuySell.buyOrSell == eOrderType.BUY) {
            buysellTitle.setText("Buy Order");
            buysellTitle.setTextColor(ScreenColor.GREEN);
            placeBtn.setText("Place Buy Order");

            if(structBuySell.scripDetails.segment.getValue() == eExch.SLBS.value){
                buysellTitle.setText("Recall Order");
                placeBtn.setText("Place Recall Order");
            }

            bracketSquareOffTitle.setText("Square Off Sell\n(Take Profit)");
            bracketStopLossTitle.setText("Stop Loss Sell\n ");
            bracketTradingStopLossTitle.setText("Trailing Stop Loss");

            bracketSquareOffTitle.setTextColor(ScreenColor.RED);
            bracketStopLossTitle.setTextColor(ScreenColor.RED);
            bracketTradingStopLossTitle.setTextColor(ScreenColor.RED);

            qtyEditText.setBackgroundResource(R.drawable.border_green);
            priceEditText.setBackgroundResource(R.drawable.border_green);
            discQtyEditText.setBackgroundResource(R.drawable.border_green);
            triggerPriceEditText.setBackgroundResource(R.drawable.border_green);

        } else {
            buysellTitle.setText("Sell Order");
            buysellTitle.setTextColor(ScreenColor.RED);
            placeBtn.setText("Place Sell Order");

            if(structBuySell.scripDetails.segment.getValue() == eExch.SLBS.value){
                buysellTitle.setText("Lend Order");
                placeBtn.setText("Place Lend Order");
            }
            bracketSquareOffTitle.setText("Square Off Buy\n(Take Profit)");
            bracketStopLossTitle.setText("Stop Loss Buy\n ");
            bracketTradingStopLossTitle.setText("Trailing Stop Loss");

            bracketSquareOffTitle.setTextColor(ScreenColor.GREEN);
            bracketStopLossTitle.setTextColor(ScreenColor.GREEN);
            bracketTradingStopLossTitle.setTextColor(ScreenColor.GREEN);

            qtyEditText.setBackgroundResource(R.drawable.border_red);
            priceEditText.setBackgroundResource(R.drawable.border_red);
            discQtyEditText.setBackgroundResource(R.drawable.border_red);
            triggerPriceEditText.setBackgroundResource(R.drawable.border_red);

        }
        if (structBuySell.modifyOrPlace == eOrderType.MODIFY) {
            placeBtn.setText("Modify");
        }
        formatter = Formatter.getFormatter(structBuySell.scripDetails.segment.getValue());

        visibilityFORFNO(View.VISIBLE, true);

        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value){
            dqLayout.setVisibility(View.GONE);

            if(!UserSession.getClientResponse().isBOOM()) {
                if (!structBuySell.scripDetails.cpType.getValue().equalsIgnoreCase("xx")) {           //Market orders in FNO scrips are only enabled for Near and Next month future scrips for Ventura
                    mktLimitRG.setEnabled(false);
                    marketRd.setVisibility(View.GONE);
                } else {
                    if (structBuySell.scripDetails.isNearNext.getValue() == 1) {            //Difference cannot be more than 9weeks for Near and Next months
                        mktLimitRG.setEnabled(true);
                        marketRd.setVisibility(View.VISIBLE);
                    } else {
                        mktLimitRG.setEnabled(false);
                        marketRd.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            dqLayout.setVisibility(View.VISIBLE);
        }
        if(structBuySell.modifyOrPlace == eOrderType.MODIFY){

            if(structBuySell.showDepth == eShowDepth.BRACKETPOS){

                String quantity = structBuySell.bracketNetPosn.qty.getValue() + "";
                if(structBuySell.scripDetails.segment.getValue() == eExch.FNO.value){
                    quantity = (structBuySell.bracketNetPosn.qty.getValue()*structBuySell.scripDetails.mktLot.getValue()) + "";
                }
                qtyEditText.setText(quantity);
                qtyEditText.setEnabled(false);
                visibilityFORFNO(View.GONE, false);
                priceEditText.setText(formatter.format(structBuySell.bracketOrdDet.rate.getValue()).trim());
                marketRd.setChecked(true);
                stopLossChk.setEnabled(false);
                dqLayout.setVisibility(View.GONE);
                bracketRadioGroup.setOnCheckedChangeListener(null);
                if(structBuySell.bracketOrdDet.absTicksSL.getValue() == 1){
                    bracketAbsoluteRD.setChecked(true);
                    bracketSquareET.setText(structBuySell.bracketOrdDet.absValTP.getValue()+"");
                    bracketStopLossET.setText(structBuySell.bracketOrdDet.absValSL.getValue() + "");
                } else{
                    bracketTicksRD.setChecked(true);
                    bracketSquareET.setText(structBuySell.bracketOrdDet.ticksTP.getValue()+"");
                    bracketStopLossET.setText(structBuySell.bracketOrdDet.ticksSL.getValue() + "");
                    addTextChangeListener(eDelvIntra.BRACKETORDER.name);
                }
            }else if (structBuySell.order != null) {

                String quantity = structBuySell.order.qty.getValue() > 0 ? structBuySell.order.qty.getValue() + "" : "";
                qtyEditText.setText(quantity);
                priceEditText.setText(formatter.format(structBuySell.order.rate.getValue()).trim());
                if (structBuySell.order.atMarket.getValue() == 'Y') {
                    marketRd.setChecked(true);
                } else {
                    limitRd.setChecked(true);
                }
                if (structBuySell.order.getTiggeredRate() > 0) {
                    triggerPriceEditText.setText(formatter.format(structBuySell.order.getTiggeredRate()));
                    stopLossChk.setChecked(true);
                    tPriceLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (structBuySell.netPosn != null) {
            String quantity = structBuySell.netPosn.getNetQty() > 0 ? structBuySell.netPosn.getNetQty() + "" : Math.abs(structBuySell.netPosn.getNetQty()) + "";
            if (showDepth == eShowDepth.HOLDINGFO || showDepth == eShowDepth.HOLDINGEQUITY) quantity = "";
            qtyEditText.setText(quantity);
            if (structBuySell.mktWatch != null)
                priceEditText.setText(formatter.format(structBuySell.mktWatch.getLastRate()).trim());
        } else {
            if (structBuySell.mktWatch != null){
                String priceStr = formatter.format(structBuySell.mktWatch.getLastRate()).trim();
                priceEditText.setText(priceStr);
            }
            OrderSaveModel orderSaveModel = PreferenceHandler.getOrderSaveList().get(scripCode);
            if (orderSaveModel != null) {
                if (orderSaveModel.getMktType().equalsIgnoreCase(eOrderType.MARKET.name)) {
                    marketRd.setChecked(true);
                } else {
                    limitRd.setChecked(true);
                }
                qtyEditText.setText(orderSaveModel.getQty()+"");
                if(orderSaveModel.isStollLoss()){
                    stopLossChk.setChecked(true);
                    tPriceLayout.setVisibility(View.VISIBLE);
                }
            }else {
                if (structBuySell.isMarket.getValue()) {
                    marketRd.setChecked(true);
                } else {
                    limitRd.setChecked(true);
                }
            }
            if (structBuySell.netQty.getValue() > 0) {
                String quan = "";
                if (showDepth == eShowDepth.HOLDINGFO || showDepth == eShowDepth.SLBMHOLDING){
                    quan = String.valueOf(structBuySell.netQty.getValue());
                }
                qtyEditText.setText(quan);
            } else if (structBuySell.qty.getValue() > 0){
                String quan = "";
                if (showDepth == eShowDepth.HOLDINGFO || showDepth == eShowDepth.SLBMHOLDING){
                    quan = String.valueOf(structBuySell.qty.getValue());
                };
                qtyEditText.setText(quan);
            }
            if (structBuySell.discloseQty.getValue() > 0) {
                discQtyEditText.setText(String.valueOf(structBuySell.discloseQty.getValue()));
            }
            if (structBuySell.limitPrice.getValue() > 0) {
                priceEditText.setText(formatter.format(structBuySell.limitPrice.getValue()).trim());
            }
            if (structBuySell.isStopLoss.getValue()){
                tPriceLayout.setVisibility(View.VISIBLE);
            }
            if (structBuySell.triggerPrice.getValue() > 0) {
                triggerPriceEditText.setText(structBuySell.triggerPrice.getValue() + "");
            }
        }
    }

    private void visibilityFORFNO(int visible, boolean clickable) {
        qtyMinus.setVisibility(visible);
        qtyPlus.setVisibility(visible);
        priceMinus.setVisibility(visible);
        pricePlus.setVisibility(visible);
        dqMinus.setVisibility(visible);
        dqPlus.setVisibility(visible);
        tpMinus.setVisibility(visible);
        tpPlus.setVisibility(visible);
        if (clickable) {
            qtyMinus.setOnClickListener(this);
            qtyPlus.setOnClickListener(this);
            priceMinus.setOnClickListener(this);
            pricePlus.setOnClickListener(this);
            dqPlus.setOnClickListener(this);
            dqMinus.setOnClickListener(this);
            tpMinus.setOnClickListener(this);
            tpPlus.setOnClickListener(this);
            qtyChange = structBuySell.scripDetails.getMKtLotForPlaceOrdg();
            priceChange = (double) structBuySell.scripDetails.getTickSize();
            selectedExchange = structBuySell.scripDetails.segment.getValue();
        }
    }

    private void initIOCCheck() {

        if (structBuySell.isIoc != null) checkboxIOC.setChecked(structBuySell.isIoc.getValue());
        if(structBuySell.isIoc.getValue()){
            stopLossChk.setChecked(false);
            stopLossChk.setEnabled(false);
            dqLayout.setVisibility(View.GONE);
        }
        checkboxIOC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    stopLossChk.setChecked(false);
                    stopLossChk.setEnabled(false);
                    dqLayout.setVisibility(View.GONE);
                } else {
                    if (limitRd.isChecked()) {
                        stopLossChk.setEnabled(true);
                    }
                    if (structBuySell.scripDetails.segment.getValue() != eExch.FNO.value) {
                        dqLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initStopLoss() {
        triggerPriceEditText.setText("");
        if (structBuySell.isStopLoss != null) {
            stopLossChk.setChecked(structBuySell.isStopLoss.getValue());
        }
        stopLossChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tPriceLayout.setVisibility(View.VISIBLE);
                } else {
                    tPriceLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        try {
            ordsavechkbox.setOnCheckedChangeListener(null);
            enableOrdeSaveCheckBox(false);
            OrderSaveModel orderSaveModel = PreferenceHandler.getOrderSaveList().get(scripCode);
            if (orderSaveModel != null) {
                ordsavechkbox.setChecked(true);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        ordsavechkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    placeBtnClick(true);
                }
            }
        });
    }

    private void initMarketSpinner() {
        mktLimitRG.setOnCheckedChangeListener(null);
        mktLimitRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (limitRd.isChecked()) {
                    priceLayout.setVisibility(View.VISIBLE);
                    if (!checkboxIOC.isChecked()) {
                        stopLossChk.setEnabled(true);
                    }
                    if(stopLossChk.getVisibility() == View.GONE || stopLossChk.getVisibility() == View.INVISIBLE){
                        stopLossChk.setVisibility(View.VISIBLE);
                    }
                } else {
                    priceLayout.setVisibility(View.INVISIBLE);
                    tPriceLayout.setVisibility(View.INVISIBLE);
                    stopLossChk.setChecked(false);
                    stopLossChk.setEnabled(false);
                }
            }
        });
    }

    private void initOrderSpinner() {

        ArrayList<String> intraDelArr = new ArrayList<String>();
        if (structBuySell.modifyOrPlace == eOrderType.MODIFY) {
            if(structBuySell.showDepth == eShowDepth.BRACKETPOS){
                if (structBuySell.bracketNetPosn.productType.getValue() == eProductType.BRACKETORDER.value) {
                    intraDelArr.add(eDelvIntra.BRACKETORDER.name);
                } else {
                    intraDelArr.add(eDelvIntra.COVERORDER.name);
                }
            }else {
                if (structBuySell.order.orderType.getValue() == eDelvIntra.DELIVERY.value) {
                    intraDelArr.add(eDelvIntra.DELIVERY.name);
                    deliveryRd.setChecked(true);
                } else {
                    intraDelArr.add(eDelvIntra.INTRADAY.name);
                    intradayRd.setChecked(true);
                }
            }
        } else if (structBuySell.isSquareOff.getValue()) {
            if(structBuySell.netPosn != null) {
                if (structBuySell.netPosn.orderType == eDelvIntra.DELIVERY.value) {
                    intraDelArr.add(eDelvIntra.DELIVERY.name);
                    deliveryRd.setChecked(true);
                } else {
                    intraDelArr.add(eDelvIntra.INTRADAY.name);
                    intradayRd.setChecked(true);
                }
            }
            else if(structBuySell.showDepth == eShowDepth.HOLDINGFO ||
                    structBuySell.showDepth == eShowDepth.HOLDINGEQUITY){
                intraDelArr.add(eDelvIntra.DELIVERY.name);
            }
        }else{
            intraDelArr.clear();
            intraDelArr.add(eDelvIntra.DELIVERY.name);

            if(structBuySell.scripDetails.enableIntraDelForCategory()) {
                if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value &&
                        UserSession.getLoginDetailsModel().isFNOIntradayDelivery()) {
                    intraDelArr.add(eDelvIntra.INTRADAY.name);
                } else if ((structBuySell.scripDetails.segment.getValue() == eExch.NSE.value ||
                        structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) &&
                        UserSession.getLoginDetailsModel().isIntradayDelivery()) {
                    intraDelArr.add(eDelvIntra.INTRADAY.name);
                }
            }
            if(UserSession.getClientResponse().getServerType() == eServerType.RC
                    && ObjectHolder.isOCOAllow
                    && structBuySell.scripDetails.segment.getValue() != eExch.SLBS.value){
                intraDelArr.add(eDelvIntra.BRACKETORDER.name);
                intraDelArr.add(eDelvIntra.COVERORDER.name);
            }
        }

        if (intraDelArr.size()>1 || (intraDelArr.size()>0 && (structBuySell.isSquareOff.getValue() || structBuySell.modifyOrPlace == eOrderType.MODIFY)))  {

            deliveryRd.setVisibility(View.INVISIBLE);
            intradayRd.setVisibility(View.INVISIBLE);

            delintraRG.setVisibility(View.INVISIBLE);
            if(intraDelArr.contains(eDelvIntra.DELIVERY.name)){
                deliveryRd.setVisibility(View.VISIBLE);
                delintraRG.setVisibility(View.VISIBLE);
            }
            if(intraDelArr.contains(eDelvIntra.INTRADAY.name)){
                intradayRd.setVisibility(View.VISIBLE);
                delintraRG.setVisibility(View.VISIBLE);
            }
            visibilityFORNormaOrder();
            delintraRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    /*if (orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.BRACKETORDER.name)) {
                        visibilityFORBracketOrder();
                    } else if(orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.COVERORDER.name)){
                        visibilityFORCoverOrder();
                    } else{*/
                        visibilityFORNormaOrder();
                    //}
                }
            });

        } else {
            delintraRG.setVisibility(View.INVISIBLE);
            visibilityFORNormaOrder();
        }
    }
    private void visibilityFORBracketOrder() {

        bracketorderLayout.setVisibility(View.VISIBLE);
        bracketSquareOffLayout.setVisibility(View.VISIBLE);
        marketRd.setChecked(true);
        mktLimitRG.setEnabled(false);
        marketRd.setVisibility(View.GONE);
        checkboxIOC.setChecked(true);

        priceLayout.setVisibility(View.INVISIBLE);
        tPriceLayout.setVisibility(View.GONE);
        stopLossChk.setChecked(false);
        stoplosslayout.setVisibility(View.GONE);
        bracketRadioGroup.setOnCheckedChangeListener(null);
        bracketRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bracketRadioGroupChanged();
            }
        });
    }
    private void visibilityFORCoverOrder() {

        bracketorderLayout.setVisibility(View.VISIBLE);
        bracketSquareOffLayout.setVisibility(View.INVISIBLE);
        marketRd.setChecked(true);
        mktLimitRG.setEnabled(false);

        marketRd.setVisibility(View.GONE);
        checkboxIOC.setChecked(true);

        priceLayout.setVisibility(View.INVISIBLE);
        tPriceLayout.setVisibility(View.GONE);
        stopLossChk.setChecked(false);
        stoplosslayout.setVisibility(View.GONE);
        bracketRadioGroup.setOnCheckedChangeListener(null);
        bracketRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bracketRadioGroupChanged();
            }
        });
    }
    private void visibilityFORNormaOrder() {
        bracketSquareOffLayout.setVisibility(View.VISIBLE);
        bracketorderLayout.setVisibility(View.GONE);
        bracketRadioGroup.setOnCheckedChangeListener(null);
        if (!checkboxIOC.isChecked()) {
            stopLossChk.setEnabled(true);
        }
        if(stoplosslayout.getVisibility() == View.GONE){
            stoplosslayout.setVisibility(View.VISIBLE);
        }
        //mktLimitSpinner.setEnabled(true);
        removeTextChangeListener();
    }
    private void bracketRadioGroupChanged(){
        removeTextChangeListener();
        if(bracketTicksRD.isChecked()){
            //addTextChangeListener(orderTypeSpinner.getSelectedItem().toString());
        }
    }

    TextWatcher squareTxtWatcher;
    TextWatcher stoplossTxtWatcher;
    TextWatcher trailingSLTxtWatcher;

    private void removeTextChangeListener(){

        if(squareTxtWatcher != null){
            bracketSquareET.removeTextChangedListener(squareTxtWatcher);
        }
        if(stoplossTxtWatcher != null){
            bracketStopLossET.removeTextChangedListener(stoplossTxtWatcher);
        }
        if(trailingSLTxtWatcher != null){
            bracketTradingStopLossET.removeTextChangedListener(trailingSLTxtWatcher);
        }
        bracketSquareET.setText("");
        bracketStopLossET.setText("");
        bracketTradingStopLossET.setText("");

        bracketSquareOffRS.setText("");
        bracketStopLossRS.setText("");
        bracketTradingStopLossRS.setText("");
    }
    private void addTextChangeListener(String  type){
        if(type.equalsIgnoreCase(eDelvIntra.BRACKETORDER.name)) {

            squareTxtWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    double value = getStringToDouble(bracketSquareET.getText().toString());
                    value = value*structBuySell.scripDetails.getTickSize();
                    String str = "Rs. ";
                    if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
                        str = str + Formatter.getFourDigitFormatter(value);
                    } else{
                        str = str + Formatter.toTwoDecimalValue(value);
                    }
                    bracketSquareOffRS.setText(str);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            };
            bracketSquareET.addTextChangedListener(squareTxtWatcher);
        }
        stoplossTxtWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double value = getStringToDouble(bracketStopLossET.getText().toString());
                value = value*structBuySell.scripDetails.getTickSize();
                String str = "Rs. ";
                if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
                    str = str + Formatter.getFourDigitFormatter(value);
                }else{
                    str = str + Formatter.toTwoDecimalValue(value);
                }
                bracketStopLossRS.setText(str);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        bracketStopLossET.addTextChangedListener(stoplossTxtWatcher);
        trailingSLTxtWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double value = getStringToDouble(bracketTradingStopLossET.getText().toString());
                value = value*structBuySell.scripDetails.getTickSize();
                String str = "Rs. ";
                if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
                    str = str + Formatter.getFourDigitFormatter(value);
                }else{
                    str = str + Formatter.toTwoDecimalValue(value);
                }
                bracketTradingStopLossRS.setText(str);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        bracketTradingStopLossET.addTextChangedListener(trailingSLTxtWatcher);
    }

    private int getOrgScripName(String scripName) {

        int index = -1;
        String tempScripName = scripName;
        String scrNameT[] = tempScripName.split("-");
        if(scrNameT.length > 2){
            tempScripName = scrNameT[0]+"-" + scrNameT[1];
        }
        for (int i = 0; i < scripNameList.size(); i++) {
            String mscripName = scripNameList.get(i);
            String scrName[] = mscripName.split("-");
            if(scrName.length > 2){
                mscripName = scrName[0]+"-" + scrName[1];
            }
            if (mscripName.equalsIgnoreCase(tempScripName)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private boolean isnsebseclieck = false;
    private void nsebseExchBTNCLICK(boolean isFromClick) {
        isnsebseclieck = isFromClick;
        scripName = spinner.getSelectedItem().toString();
        String tempScripName = scripName;
        if (tempScripName.substring(0, 2).equalsIgnoreCase(BSE)) {
            tempScripName = NSE + "-" + scripName.substring(3);
        } else if (tempScripName.substring(0, 2).equalsIgnoreCase(NSE)) {
            tempScripName = BSE + "-" + scripName.substring(3);
        }
        tempScripName = tempScripName.replace("*","");
        if(isFromClick) {
            int index = getOrgScripName(tempScripName);
            if (index >= 0) {
                spinner.setSelection(index);
                nseBseScripCode = scripCode;
                StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001DataforDepth(nseBseScripCode);
                setNSEBSERate(mktWatch);
            } else {
                GlobalClass.showProgressDialog("Please wait...");
                if(nseBseScripCode > 0){
                    int tempCurrentCode = scripCode;
                    handleNseBse(nseBseScripCode,tempScripName);
                    nseBseScripCode = tempCurrentCode;
                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001DataforDepth(nseBseScripCode);
                    setNSEBSERate(mktWatch);
                }else {
                    int segment = 1;
                    if (exchName.equalsIgnoreCase(NSE)) {
                        segment = 0;
                    }
                    try {
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendNSEBSEReq(segment, scripCode);
                    } catch (Exception e) {
                        GlobalClass.onError("Error in " + className, e);
                    }
                }
            }
        }else{
            if(nseBseScripCode <= 0){
                try {
                    int segment = 1;
                    if (exchName.equalsIgnoreCase(NSE)) {
                        segment = 0;
                    }
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendNSEBSEReq(segment, scripCode);
                } catch (Exception e) {
                    GlobalClass.onError("Error in " + className, e);
                }
            }else{
                StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001DataforDepth(nseBseScripCode);
                setNSEBSERate(mktWatch);
            }
        }
    }
    private void showChart() {
        Fragment m_fragment = GraphFragment.newInstance(scripCode, scripName);
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true,"");
    }
    private void showTradingView() {
        Intent intent = new Intent(getContext(), TradingViewChart.class);
        intent.putExtra("SCRIPNAME",scripName);
        intent.putExtra("SCRIPCODE",scripCode);
        intent.putExtra("EXCHANGE",getExchangeForTradingView());
        intent.putExtra("theme","dark");
        startActivity(intent);
    }
    public String getExchangeForTradingView(){
        if(selectedExchange == eExch.BSE.value){
            return "B";
        }
        else if(selectedExchange == eExch.NSECURR.value){
            return "C";
        }
        else{
            return "N";
        }
    }
    private void showLatestResult() {
        if (mktWatch != null && mktWatch.getSegment() == eExch.FNO.value){
            ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
            if(scripDetail != null){
                if(scripDetail.nseScripCode.getValue() > 0){

                    GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                    groupsTokenDetails.scripCode.setValue(scripDetail.nseScripCode.getValue());
                    groupsTokenDetails.scripName.setValue("NE-"+scripDetail.underlying.getValue());
                    ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                    grplist.add(groupsTokenDetails);

                    Fragment m_fragment = new LatestResultFragment(scripDetail.nseScripCode.getValue() , grplist,radioButtons);
                    GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
                }else{
                    GlobalClass.showToast(GlobalClass.latestContext,"Fundamentals cannot be viewed for this scrip");
                }
            }else{
                valuationLatestResultClick = 2;
                GlobalClass.showProgressDialog("Please wait...");
            }
        }else {
            Fragment m_fragment = new LatestResultFragment(scripCode, grpScripList,radioButtons);
            GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
        }
    }
    int valuationLatestResultClick = -1;
    private void showValuation() {

        if (mktWatch != null && mktWatch.getSegment() == eExch.FNO.value){
            ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
            if(scripDetail != null){
                if(scripDetail.nseScripCode.getValue() > 0){

                    GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                    groupsTokenDetails.scripCode.setValue(scripDetail.nseScripCode.getValue());
                    groupsTokenDetails.scripName.setValue("NE-"+scripDetail.underlying.getValue());
                    ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                    grplist.add(groupsTokenDetails);

                    Fragment m_fragment = new ValuetionFragment(scripDetail.nseScripCode.getValue(), grplist,radioButtons);
                    GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
                }else{
                    GlobalClass.showToast(GlobalClass.latestContext,"Fundamentals cannot be viewed for this scrip");
                }
            }else{
                valuationLatestResultClick = 1;
                GlobalClass.showProgressDialog("Please wait...");
            }

        }else {
            Fragment m_fragment = new ValuetionFragment(scripCode, grpScripList,radioButtons);
            GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
        }
    }
    private void nseBSELayOUTVisibility(int visibility){
        nsebseTradeLayout.setVisibility(visibility);
        nsebseTop.setVisibility(visibility);
        if(visibility == View.VISIBLE){
            boolean investnow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.MKTDEPTH_TOOLTIP_HELP.name, false);
            if (!investnow) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToolTip();
                    }
                }, 1000);
            }
        }
        boolean plusnow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.MKTDEPTH_TOOLTIP_PLUS.name, false);
        if (!plusnow) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToolTip2();
                }
            }, 1000);
        }
    }

    private void showToolTip(){
        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.MKTDEPTH_TOOLTIP_HELP.name, true);
        displayTooltip();
    }
    Balloon balloon = null;
    private void displayTooltip() {
        if(balloon == null) {
            String msg = "Now you can toggle between prices on both the exchanges by clicking on this.";

            balloon = new Balloon.Builder(getContext())
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
                    .setArrowPosition(0.5f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(70)
                    .setWidthRatio(0.85f)
                    .setTextSize(15f)
                    .setTextTypeface(Typeface.SANS_SERIF)
                    .setCornerRadius(8f)
                    .setAlpha(0.95f)
                    .setText(msg)
                    .setPaddingHorizontal(10)
                    .setTextColor(ContextCompat.getColor(getContext(), R.color.white))
                    .setTextIsHtml(true)
                    .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ventura_color))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                    .setAutoDismissDuration(10000L)
                    .setLifecycleOwner(this)
                    .build();

                    balloon.setOnBalloonOutsideTouchListener(new OnBalloonOutsideTouchListener() {
                        @Override
                        public void onBalloonOutsideTouch(@NotNull View view, @NotNull MotionEvent motionEvent) {
                            balloon.dismiss();
                            balloon = null;
                        }
                    });
            balloon.showAlignTop(nsebseLastRate);
        }
    }
    private void showToolTip2(){
        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.MKTDEPTH_TOOLTIP_PLUS.name, true);
        displayTooltip2();
    }
    Balloon balloon2 = null;
    private void displayTooltip2() {
        if(balloon2 == null) {
            String msg = "You can add this scrip to any of your market watches using this '+' button";

            balloon2 = new Balloon.Builder(getContext())
                    .setArrowSize(10)
                    .setArrowOrientation(ArrowOrientation.BOTTOM)
                    .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
                    .setArrowPosition(0.95f)
                    //.setWidth(BalloonSizeSpec.WRAP)
                    .setHeight(120)
                    .setWidthRatio(0.85f)
                    .setTextSize(20f)
                    .setTextTypeface(Typeface.BOLD)
                    .setCornerRadius(8f)
                    .setAlpha(0.95f)
                    .setText(msg)
                    .setPaddingHorizontal(10)
                    .setTextColor(ContextCompat.getColor(getContext(), R.color.ventura_color))
                    .setTextIsHtml(true)
                    .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
                    .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                    .setAutoDismissDuration(15000L)
                    .setLifecycleOwner(this)
                    .build();

            balloon2.setOnBalloonOutsideTouchListener(new OnBalloonOutsideTouchListener() {
                @Override
                public void onBalloonOutsideTouch(@NotNull View view, @NotNull MotionEvent motionEvent) {
                    balloon2.dismiss();
                    balloon2 = null;
                }
            });
            balloon2.showAlignTop(depth_addscriptBtn);
        }
    }

    private void setExchName() {
        try {
            isnsebseclieck = false;
            oiLayout.setVisibility(View.GONE);
            weekLinear.setVisibility(View.VISIBLE);
            nseBSELayOUTVisibility(View.GONE);
            selectedExchange = -1;
            if(!GlobalClass.indexScripCodeOrNot(scripCode) && GlobalClass.isEquity()) {
                if (exchName.equalsIgnoreCase(NSE)) {
                    selectedExchange = eExch.NSE.value;
                    nsebseLastRateTitle.setText("BSE LTQ : ");
                    if (nseBseScripCode <= 0) {
                        nsebseExchBTNCLICK(false);
                    } else {
                        nseBSELayOUTVisibility(View.VISIBLE);
                    }
                    //nsebseLastRateTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.bse_site_color));
                    nsebseLastRate.setBackgroundResource(R.drawable.bse_btn_border);
                } else if (exchName.equalsIgnoreCase(BSE)) {
                    selectedExchange = eExch.BSE.value;
                    nsebseLastRateTitle.setText("NSE LTQ : ");
                    if (nseBseScripCode <= 0) {
                        nsebseExchBTNCLICK(false);
                    } else {
                        nseBSELayOUTVisibility(View.VISIBLE);
                    }
                    //nsebseLastRateTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.nse_site_color));
                    nsebseLastRate.setBackgroundResource(R.drawable.nse_btn_border);
                } else {
                    oiLayout.setVisibility(View.VISIBLE);
                    weekLinear.setVisibility(View.GONE);
                }
            }else if(GlobalClass.isCommodity()){
                oiLayout.setVisibility(View.VISIBLE);
                weekLinear.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    //endregion

    public void refreshMD(int scripCode) {
        try {
            LiteMktDepth m_structMktDepth = GlobalClass.mktDataHandler.getMktDepthData(scripCode);
            if (m_structMktDepth == null) {
                m_structMktDepth = new LiteMktDepth();
            }
            int diveder = eDivider.DIVIDER1.value;
            if (mktWatch.getSegment() == eExch.NSECURR.value) {  //yr high and yr low should be removed for NSECUR
                diveder = eDivider.DIVIDER100000.value;
            }
            totBuy.setText(m_structMktDepth.tBidQ.getValue() + "");
            totSell.setText(m_structMktDepth.tOffQ.getValue() + "");

            LiteMDDetails structMDArr[] = m_structMktDepth.structMD;
            if (mktdepthrowviewArrayList != null) {
                for (int i = 0; i < mktdepthrowviewArrayList.size(); i++) {
                    mktdepthrowviewArrayList.get(i).setMktDepthValue(structMDArr[i],structMDArr[i + 5],diveder);
                }
            }
            setBuySellIndicator(m_structMktDepth.tBidQ.getValue(), m_structMktDepth.tOffQ.getValue());
        } catch (Exception e) {
            GlobalClass.onError("Error in ", e);
        }
    }

    private void mdRequest() {
        try {
            getScripList();
            mktWatch = GlobalClass.mktDataHandler.getMkt5001DataforDepth(scripCode);
            if (mktWatch == null) {
                GlobalClass.mktDataHandler.sendMktWatchReq(scripCode);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    int mktDepthReqsend = 0;
    private void sendMarketDepthReqToserver(boolean isForce) {
        GlobalClass.mktDepthScripcode = scripCode;
        if((mktDepthReqsend != scripCode) || isForce) {
            try {
                mktDepthReqsend = scripCode;
                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                sendDataToServer.sendMarketDepthReq(scripCode);
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
        }
    }
    private void sendNewsReqToserver() {
        try {
            _mNews = new ArrayList<>();
            String sArr[] = scripName.split("-");
            if (sArr.length>1){
                String symbol = sArr[1];
                SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                sendDataToServer.sendMktDepthNewsReq(scripCode,symbol,0);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void getScripList() {
        try {
            tempMap.clear();
            scripNameList = new ArrayList<>();
            for (GroupsTokenDetails gsd : grpScripList) {
                scripNameList.add(gsd.scripName.getValue());
                tempMap.put(gsd.scripName.getValue(), gsd.scripCode.getValue());
                if (scripCode == gsd.scripCode.getValue()) {
                    scripName = gsd.scripName.getValue();
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void refreshMktWatchData() {
        try {
            mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            if (formatter == null){
                formatter = mktWatch.getFormatter();
            }
            if (txtQty != null) {
                txtQty.setText(Html.fromHtml(mktWatch.getLw().lastQty.getValue() + " @ <big><big><big><b>" +
                        formatter.format(mktWatch.getLastRate()) + "</b></big></big></big>"));

                txtQty.setTextColor(ScreenColor.textColor);

                double perChange = mktWatch.getPerChg();
                txtPerChg.setText(formatter.format(perChange) + "%");
                if (perChange < 0) {
                    txtPerChg.setTextColor(ScreenColor.RED);
                } else if (perChange > 0) {
                    txtPerChg.setTextColor(ScreenColor.GREEN);
                }
                String _timeVal = "" + DateUtil.dateFormatter(mktWatch.getLw().time.getValue(), Constants.DDMMMYYHHMMSS);
                txtTime.setText(_timeVal);

                lowerCkt.setText(formatter.format(mktWatch.getLowerCkt()));
                upperCkt.setText(formatter.format(mktWatch.getUpperCkt()));
                txtAvg.setText(formatter.format(mktWatch.getAverage()));
                if (TextUtils.isEmpty(mktWatch.getTrdExc())){
                    trdExcLayout.setVisibility(View.INVISIBLE);
                }else {
                    trdExcLayout.setVisibility(View.VISIBLE);
                    trdExc.setText(mktWatch.getTrdExc());
                }
                txtOpen.setText(formatter.format(mktWatch.getOpen()));
                txtLow.setText(formatter.format(mktWatch.getLow()));
                txtVol.setText(Formatter.convertIntToValue(mktWatch.getLw().totalQty.getValue()));
                txt52WeekHigh.setText(formatter.format(mktWatch.getYearHigh()));
                txtPClose.setText(formatter.format(mktWatch.getPClose()));
                txtHigh.setText(formatter.format(mktWatch.getHigh()));
                txtVal.setText(Formatter.convertDoubleToValue(mktWatch.getLw().totalQty.getValue() * mktWatch.getAverage()));
                txt52WeekLow.setText(formatter.format(mktWatch.getYearLow()));
                oi_low.setText(Formatter.roundFormatter.format(mktWatch.getLowOI()));
                oi_high.setText(Formatter.roundFormatter.format(mktWatch.getHighOI()));
                oi_curr.setText(Formatter.roundFormatter.format(mktWatch.getLw().openInterest.getValue()));

                int oiPrevValue = (mktWatch.getSw().prevOI.getValue());
                double perChgOI = ((double) (mktWatch.getLw().openInterest.getValue() - oiPrevValue)/(double)oiPrevValue)*100;

                oi_chg.setText(Formatter.roundFormatter.format((double) (mktWatch.getLw().openInterest.getValue() - oiPrevValue)) + "("+Formatter.formatter.format(perChgOI)+"%)");

                setSpeedOMeterData(mktWatch);
                refreshaNetPositionValue();
                if(selectedHoldingRow != null){
                    holdcurr_value.setText(selectedHoldingRow.getCurrValueStr(mktWatch.getLastRate()));
                    String currPLStr = selectedHoldingRow.getCurrPLStr(mktWatch.getLastRate());
                    if(currPLStr.startsWith("-")){
                        holdpl_value.setText(currPLStr.substring(1));
                        holdpl_value.setTextColor(ScreenColor.RED);
                    }else{
                        holdpl_value.setText(currPLStr);
                        holdpl_value.setTextColor(ScreenColor.GREEN);
                    }
                }else if(selectedFNOHoldingRow != null){
                    ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
                    int mktLot = 1;
                    if(scripDetail.segment.getValue() == eExch.NSECURR.value){
                        mktLot = scripDetail.mktLot.getValue();
                    }
                    String PLPrvCStr = selectedFNOHoldingRow.getPLonPrevCloseStr(mktWatch.getLastRateNoRound(),mktWatch.getPClose(),mktLot);
                    if (PLPrvCStr.startsWith("-")) {
                        holdcurr_value.setText(PLPrvCStr.substring(1));
                        holdcurr_value.setTextColor(ScreenColor.RED);
                    } else {
                        holdcurr_value.setText(PLPrvCStr);
                        holdcurr_value.setTextColor(ScreenColor.GREEN);
                    }
                    String currPLStr = selectedFNOHoldingRow.getPLonCostStr(mktWatch.getLastRateNoRound(),mktLot);
                    if (currPLStr.startsWith("-")) {
                        holdpl_value.setText(currPLStr.substring(1));
                        holdpl_value.setTextColor(ScreenColor.RED);
                    } else {
                        holdpl_value.setText(currPLStr);
                        holdpl_value.setTextColor(ScreenColor.GREEN);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPopupClose() {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        scripName = adapter.getItem(i).toString();
        scripCode = tempMap.get(scripName);
        GlobalClass.mktDepthScripcode = scripCode;
        formatter = null;
        sendMarketDepthReqToserver(false);
        refreshMktWatchData();
        refreshMD(scripCode);
        exchName = spinner.getSelectedItem().toString().split("-")[0];
        if(!isnsebseclieck) {
            nseBseScripCode = 0;
        }
        setExchName();
        setPendingOrders();
        if (radioPos == 2) {
            generatePendOdr();
        } else if (radioPos == 3) {
            generatenetQty();
        }
        else if(radioPos == 4){
            detailsRadiogrp.check(R.id.detailRd);
            radioPos = 1;
            details.setVisibility(View.VISIBLE);
            pendingLinear.setVisibility(View.GONE);
        }
        if(isShowNews()) {
            sendNewsReqToserver();
        }
        buySellType = eOrderType.NONE;
        buysellLinear.removeAllViews();
        buysellLinear.addView(buySellBtn);

        showHoldingValue();
        if (buysellBottom.getVisibility() == View.VISIBLE) {
            buysellBottom.setVisibility(View.GONE);
        }
        if(exchName.equalsIgnoreCase("SD")){
            buyBtn.setText("RECALL");
            sellBtn.setText("LEND");
        }else{
            buyBtn.setText("BUY");
            sellBtn.setText("SELL");
        }
        try{
            checkOneTimeForLiveRateIssue();
        }catch (Exception ex){ex.printStackTrace();}
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onOk(String tag) {
        if (tag.equalsIgnoreCase("cancel")) {
            cancelSingleOrder();
        } else if (tag.equalsIgnoreCase(placeTAG)) {
            sendOrderReqToServer();

            if (structBuySell.showDepth != eShowDepth.MKTWATCH) {
                if (fromDetails){
                    GlobalClass.fragmentManager.popBackStackImmediate();
                }
                GlobalClass.fragmentManager.popBackStackImmediate();
            }
        } else if (tag.equalsIgnoreCase(T4TTAG)) {
            String errorMsg = structBuySell.scripDetails.getIllequideMsg();
            if (!errorMsg.equalsIgnoreCase("")) {
                new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, illTAG);
            } else {
                    errorMsg = structBuySell.scripDetails.surveillanceMassage.getValue();
                    if (!errorMsg.equalsIgnoreCase("")) {
                        new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, surveilanceTAG);
                    }else {
                        showConfirmAlert(true,false);
                    }
            }
        }else if (tag.equalsIgnoreCase(banTAG)) {
            String errorMsg = structBuySell.scripDetails.getPhysicalDeliveryMsg();
            if (!errorMsg.equalsIgnoreCase("")) {
                new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, physicalTAG);
            } else{
                errorMsg = structBuySell.scripDetails.surveillanceMassage.getValue();
                if (!errorMsg.equalsIgnoreCase("")) {
                    new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, surveilanceTAG);
                }else{
                    showConfirmAlert(true,false);
                }
            }
        }else if (tag.equalsIgnoreCase(illTAG) || tag.equalsIgnoreCase(physicalTAG)) {
            String errorMsg = structBuySell.scripDetails.surveillanceMassage.getValue();
            if (!errorMsg.equalsIgnoreCase("")) {
                new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, surveilanceTAG);
            } else {
                showConfirmAlert(true,false);
            }
        }else if (tag.equalsIgnoreCase(surveilanceTAG)) {
            showConfirmAlert(true,false);
        }else if(tag.equalsIgnoreCase("edis")){
            Fragment m_fragment;
            m_fragment = new EDISProcessFragmentMktDepth(selectedHoldingRow,structBuySell.scripDetails.scripCode.getValue());

            FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, m_fragment);
            fragmentTransaction.addToBackStack("edis");
            fragmentTransaction.commit();
        } else if(tag.equalsIgnoreCase("watchnews")){
            PreferenceHandler.setNotificationActive(true);
            StructNewsDisclaimer snd = new StructNewsDisclaimer();
            snd.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            snd.model.setValue(android.os.Build.MODEL);
            snd.IMEI.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            new SendDataToBCServer().sendNotificationAgreeReq(snd);
            if(watchNews != null){
                watchNews.startNewsTime();
            }
        } else if(tag.equalsIgnoreCase(orderSaveTag)){
            saveOrderCredentials();
        } else{
            ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(3);
            GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
        }
    }

    private void cancelSingleOrder() {
        if (structBuySell.order != null) {
            GlobalClass.getClsOrderBook().cancelOrderRequest(structBuySell.order); // scrip Detail not need handle in server..
        }
    }

    @Override
    public void onCancel(String tag) {
        if(tag.equalsIgnoreCase("edis")){
            showConfirmAlert(false,false);
        }else if(tag.equalsIgnoreCase(orderSaveTag)){
            ordsavechkbox.setChecked(false);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id) {
            case R.id.detailRd:
                radioPos = 1;
                details.setVisibility(View.VISIBLE);
                pendingLinear.setVisibility(View.GONE);
                break;
            case R.id.orderRd:
                radioPos = 2;
                details.setVisibility(View.GONE);
                pendingLinear.setVisibility(View.VISIBLE);
                generatePendOdr();
                break;
            case R.id.qtyRd:
                radioPos = 3;
                details.setVisibility(View.GONE);
                pendingLinear.setVisibility(View.VISIBLE);
                generatenetQty();
                break;
            case R.id.news_Rd:
                radioPos = 4;
                details.setVisibility(View.GONE);
                pendingLinear.setVisibility(View.VISIBLE);
                generateNews();
                break;
            default:
                break;
        }
    }

    private void generateNews() {
        pendingLinear.removeAllViews();
        if(_mNews.size() >0) {
            int newsSize = _mNews.size()>3?3:_mNews.size();
            for (int i=0;i<newsSize;i++){

                try {
                    NewsScripResponseInnerStructure nsis = _mNews.get(i);
                    View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.notification_item, null);
                    TextView body = view.findViewById(R.id.notification_body);
                    TextView type = view.findViewById(R.id.notification_type);
                    TextView time = view.findViewById(R.id.notification_time);
                    body.setText(nsis.news.getValue());
                    type.setText("News");
                    time.setText(nsis.date.getValue());
                    pendingLinear.addView(view);
                } catch (Exception e) {
                    VenturaException.Print(e);
                }
            }
            if (_mNews.size() >= 3) {
                TextView acp = new TextView(GlobalClass.latestContext);
                acp.setText("Previous news");
                acp.setTextColor(Color.WHITE);
                acp.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                lp.setMargins(5, 5, 5, 5);
                acp.setLayoutParams(lp);

                acp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Fragment m_fragment = NewsScripWiseFragment.newInstance(scripCode, scripName);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.NEWS_SCRIP.name);
                    }
                });
                pendingLinear.addView(acp);
            }
        }
        else{
            //No recent news!
            TextView acp = new TextView(GlobalClass.latestContext);
            acp.setText("No recent news!");
            acp.setTextColor(Color.WHITE);
            acp.setGravity(Gravity.RIGHT);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            lp.setMargins(5, 5, 5, 5);
            acp.setLayoutParams(lp);
            pendingLinear.addView(acp);
        }
    }

    private void refreshaNetPositionValue(){

        if(radioPos == 3) {
            StructOpenPositionSummary openPositions = GlobalClass.getClsNetPosn().getOpenPositionsForScripCode(scripCode);
            if(openPositions.openPositions.size()>0) {
                for(int i=0;i<openPositions.openPositions.size();i++) {
                    StructMobNetPosition structMobNetPosition = openPositions.openPositions.get(i);
                    TextView mtmNetPTxtView = mtmNetPTxtViewHashMap.get(structMobNetPosition.key);
                    TextView bookedNetPTxtView = bookedNetPTxtViewHashMap.get(structMobNetPosition.key);
                    if(mtmNetPTxtView != null) {
                        mtmNetPTxtView.setText(structMobNetPosition.getMTMStr());
                        bookedNetPTxtView.setText(structMobNetPosition.getBookedPLStr());
                    }
                }
            }
        }
    }
    private HashMap<String,TextView> mtmNetPTxtViewHashMap =  null;
    private HashMap<String,TextView> bookedNetPTxtViewHashMap =  null;

    private void generatenetQty() {
        if(strucOpenPositionSummary != null) {
            pendingLinear.removeAllViews();
            mtmNetPTxtViewHashMap = new HashMap<>();
            bookedNetPTxtViewHashMap = new HashMap<>();
            ArrayList<StructMobNetPosition> openPositions = strucOpenPositionSummary.openPositions;
            for(int i=0;i<openPositions.size();i++) {

                final StructMobNetPosition structMobNetPosition = openPositions.get(i);
                View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.netposition_item, null);
                TextView scripName = view.findViewById(R.id.scrept_name_textview);
                TextView netQtyRate = view.findViewById(R.id.scrept_vol_textview);
                TextView mtmNetPTxtView = view.findViewById(R.id.mtm_vol_textview);
                TextView bookedNetPTxtView = view.findViewById(R.id.booked_vol_textview);
                View border =  view.findViewById(R.id.borderLine);
                border.setVisibility(View.VISIBLE);
                scripName.setText(structMobNetPosition.getFormatedScripName(true));
                netQtyRate.setText(structMobNetPosition.getNetQtyRate());
                mtmNetPTxtView.setText(structMobNetPosition.getMTMStr());
                bookedNetPTxtView.setText(structMobNetPosition.getBookedPLStr());

                mtmNetPTxtViewHashMap.put(structMobNetPosition.key,mtmNetPTxtView);
                bookedNetPTxtViewHashMap.put(structMobNetPosition.key,bookedNetPTxtView);
                pendingLinear.addView(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Spinner) ((Activity) getActivity()).findViewById(R.id.report_spinner)).setSelection(3);
                        GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        GlobalClass.log("Long press on squareoff");
                        //if (structMobNetPosition.getNetQty() > 0) {
                            PopupMenu popup = new PopupMenu(GlobalClass.latestContext, view);
                            popup.getMenuInflater().inflate(R.menu.depth_netpos_menu, popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    // isCancel = false;
                                    switch (menuItem.getItemId()) {
                                        case R.id.netpos:
                                            ((Spinner) ((Activity) getActivity()).findViewById(R.id.report_spinner)).setSelection(3);
                                            GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
                                            break;
                                        case R.id.squareoff:
                                            squireoffClick(structMobNetPosition);
                                            break;
                                    }
                                    return false;
                                }
                            });
                        //}
                        return false;
                    }
                });
            }
        }
    }

    private void squireoffClick(StructMobNetPosition structMobNetPosition) {

        if (structMobNetPosition.getNetQty() < 0) {
            structBuySell.buyOrSell = eOrderType.BUY;
        } else {
            structBuySell.buyOrSell = eOrderType.SELL;
        }
        structBuySell.modifyOrPlace = eOrderType.PLACE;
        structBuySell.showDepth = eShowDepth.MKTWATCH;
        structBuySell.netPosn = structMobNetPosition;
        structBuySell.netQty.setValue(Math.abs(structMobNetPosition.getNetQty()));
        structBuySell.isSquareOff.setValue(true);
        openBuySellWindowHideBtn(structBuySell.buyOrSell, structBuySell.modifyOrPlace);
    }

    private void generatePendOdr() {
        pendingLinear.removeAllViews();
        ArrayList<StructOrderReportReplyRecord_Pointer> pendingOrders = strucPendingOrderSummary.pendingOrders;
        for (final StructOrderReportReplyRecord_Pointer sorp : pendingOrders) {
            View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.orderbook_items, null);
            TextView scripName =  view.findViewById(R.id.scripame_ordbook_textview);
            TextView buysell =  view.findViewById(R.id.buyorsell_ordbk_textview);
            TextView status =  view.findViewById(R.id.status_ordbk_textview);

            scripName.setText(sorp.getFormatedScripName(true));
            buysell.setText(sorp.getBuySell() + " " + sorp.getOrderQtyRate());
            status.setText(sorp.getFinalStatus());

            scripName.setTextColor(sorp.getTextColor());
            buysell.setTextColor(sorp.getTextColor());
            status.setTextColor(sorp.getTextColor());
            pendingLinear.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sorp.finaPendingQty>0){
                        ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(1);
                        GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (sorp.finaPendingQty > 0) {
                        PopupMenu popup = new PopupMenu(GlobalClass.latestContext, view);
                        popup.getMenuInflater().inflate(R.menu.depth_order_menu, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                // isCancel = false;
                                switch (menuItem.getItemId()) {
                                    case R.id.orderbk:
                                        ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(1);
                                        GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
                                        break;
                                    case R.id.modify:
                                        if (sorp.isOrderSentToExchModify()){
                                            Toast.makeText(GlobalClass.latestContext, sorp.getERR_ORDER_MODIFY_SENTTOEXCH(),
                                                    Toast.LENGTH_SHORT).show();
                                        }else {
                                            modifyBtnClick(sorp);
                                        }
                                        break;
                                    case R.id.cancel:
                                        if (sorp.isOrderSentToExchCancel()){
                                            Toast.makeText(GlobalClass.latestContext,sorp.getERR_ORDER_CANCEL_SENTTOEXCH(),
                                                    Toast.LENGTH_SHORT).show();
                                        }else {
                                            cancelBtnClick(sorp);
                                        }

                                        break;
                                }
                                return false;
                            }
                        });
                    }
                    return false;
                }
            });
        }
    }

    private void modifyBtnClick(StructOrderReportReplyRecord_Pointer sorp) {
        if (sorp.buySell.getValue() == 'B') {
            structBuySell.buyOrSell = eOrderType.BUY;
        } else {
            structBuySell.buyOrSell = eOrderType.SELL;
        }
        structBuySell.modifyOrPlace = eOrderType.MODIFY;
        structBuySell.showDepth = eShowDepth.MKTWATCH;
        structBuySell.order = sorp;
        openBuySellWindowHideBtn(structBuySell.buyOrSell, eOrderType.MODIFY);
    }

    private void cancelBtnClick(StructOrderReportReplyRecord_Pointer sorp) {
        structBuySell.order = sorp;
        new AlertBox(GlobalClass.latestContext, "Yes", "No", "Are you sure you want to cancel this order?", this, "cancel");
    }

    private void handleScripDetailResponse() {
        GlobalClass.dismissdialog();
        showHoldingValue();
        if(valuationLatestResultClick == 1){
            showValuation();
        }else if(valuationLatestResultClick == 2){
            showLatestResult();
        }else {
            ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
            if ((scripDetail.segment.getValue() != eExch.NSE.value)
                    && (scripDetail.segment.getValue() != eExch.BSE.value)
                    && (scripDetail.nseScripCode.getValue() <= 0)) {
                button_valution.setEnabled(false);
                button_latestresult.setEnabled(false);
            }
        }
        valuationLatestResultClick = -1;
        if (buySellType != eOrderType.NONE) {
            ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
            if (scripDetail == null) {
                GlobalClass.showToast(getContext(), "Scripcode not found.");
                return;
            }
            if (isCancel) {
                isCancel = false;
                cancelSingleOrder();
            } else {
                openBuySellWindowHideBtn(buySellType, placeModifyordertype);
            }
        }
    }

    private void setNSEBSERate(StaticLiteMktWatch mktWatch){
        try {
            if (nseBseScripCode == mktWatch.getToken()) {
                nsebseqty.setText(mktWatch.getLw().lastQty.getValue()+"");
                nsebseLastRate.setText(formatter.format(mktWatch.getLastRate()));
                String _timeVal = "" + DateUtil.dateFormatter(mktWatch.getLw().time.getValue(), Constants.DDMMMYYHHMMSS);
                nsebseltptime.setText(_timeVal);
            }
        }catch (Exception ex){ex.printStackTrace();}
    }
    private void handleNseBse(int scripCode, String scriptName) {
        GlobalClass.dismissdialog();
        if(!isnsebseclieck){
            nseBseScripCode = scripCode;
            StaticLiteMktWatch _mktWatch = GlobalClass.mktDataHandler.getMkt5001DataforDepth(nseBseScripCode);
            if (_mktWatch == null) {
                GlobalClass.mktDataHandler.sendMktWatchReq(nseBseScripCode);
            }else{
                setNSEBSERate(_mktWatch);
            }
            nseBSELayOUTVisibility(View.VISIBLE);
        }else {
            this.scripCode = scripCode;
            this.scripName = scriptName;
            if (tempMap.values().contains(scripCode)) {
                spinner.setSelection(adapter.getPosition(scripName));
            } else {
                GroupsTokenDetails tokenDetail = new GroupsTokenDetails();
                tokenDetail.scripName.setValue(scriptName);
                tokenDetail.scripCode.setValue(scripCode);
                grpScripList.add(tokenDetail);
                mdRequest();
                adapter = new ArrayAdapter(GlobalClass.latestContext, R.layout.custom_spinner_item, scripNameList);
                adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getPosition(scripName));
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    private String getScripType(String scripName) {
        if (scripName.substring(0, 2).equalsIgnoreCase("BE") ||
                scripName.substring(0, 2).equalsIgnoreCase("NE"))
            return "";
        return "NSE";
    }

    private void chargeEstimatorClick(){
        try{
            String strQty, strDiscQty, strPrice, strTrgrPrice;
            strQty = qtyEditText.getText().toString().trim();
            strDiscQty = discQtyEditText.getText().toString().trim();
            strPrice = priceEditText.getText().toString().trim();
            strTrgrPrice = triggerPriceEditText.getText().toString().trim();

            int qty = getStringToInt(strQty);
            int discQty = getStringToInt(strDiscQty);
            double limitPrice = getStringToDouble(strPrice);
            double trgrPrice = getStringToDouble(strTrgrPrice);
            int isIOC = 0, isAtMkt = 0, withSL = 0;
            int seg = structBuySell.scripDetails.segment.getValue();

            if (marketRd.isChecked()) {
                isAtMkt = eOrderType.MARKET.value;
                limitPrice = 0;
                trgrPrice = 0;
            }
            if (stopLossChk.isChecked()) {
                withSL = 1;
            } else {
                withSL = 0;
                trgrPrice = 0;
            }
            if (checkboxIOC.isChecked()) {
                isIOC = 1;
                discQty = 0;
                trgrPrice = 0;
                withSL = 0;
            }
            structBuySell.mktWatch = mktWatch;
            String orderValidationMsg = structBuySell.orderValiDation(qty,discQty,seg,limitPrice,trgrPrice,isAtMkt,withSL);
            if(!orderValidationMsg.equalsIgnoreCase("")){
                GlobalClass.showAlertDialog(orderValidationMsg);
            }else{//call sagar bhai api..
                String isIntraday = "Delivery";
                try {
                    if (delintraRG.getVisibility() == View.VISIBLE) {
                        isIntraday = intradayRd.isChecked()?eDelvIntra.INTRADAY.name:eDelvIntra.DELIVERY.name;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("Clientcode",UserSession.getLoginDetailsModel().getUserID());
                jsonBody.put("SegId",seg);
                jsonBody.put("Name",structBuySell.scripDetails.symbol.getValue());
                jsonBody.put("Expiry",structBuySell.scripDetails.getExpiryDDMMMYYYY());
                jsonBody.put("Series",structBuySell.scripDetails.cpType.getValue());
                jsonBody.put("Buy_Sell",structBuySell.buyOrSell == eOrderType.BUY?"B":"S");
                jsonBody.put("Qty",qty);
                jsonBody.put("Rate",limitPrice);
                jsonBody.put("ProductType",isIntraday);
                jsonBody.put("StrikePrice",structBuySell.scripDetails.getStrikeRateForOrderPlacing() > 0?structBuySell.scripDetails.getStrikeRateForOrderPlacing():0);
                jsonBody.put("UnderlyingType",structBuySell.scripDetails.getUnderlyingTyep());
                jsonBody.put("UserKey","15be76482ccf443b41cb3ca66cb025b3e7048ac6");
                jsonBody.put("Session_id", PreferenceHandler.getSSOSessionID());
                jsonBody.put("Xapikey", eSSOTag.xapikey.value);
                jsonBody.put("Authorization", "Bearer " + PreferenceHandler.getSSOAuthToken());

                new eBrokerageChargesDetails(jsonBody).execute();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private class eBrokerageChargesDetails extends AsyncTask<String, Void, String> {

        JSONObject jBody;
        int responseCode;

        eBrokerageChargesDetails(JSONObject _jBody){
            this.jBody = _jBody;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Please wait...");
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(String... urls)  {
            try {
                HttpCall httpCall = new HttpCall();
                HttpCallResp httpCallResp = httpCall.CallPostRequest("https://settlements.ventura1.com/api/BkgsAndChgs/GetBkgsAndChgs",jBody);

                responseCode = httpCallResp.responseCode;
                return httpCallResp.responseData;
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)  {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            try {
                if(s != null && !s.equalsIgnoreCase("")) {
                    if(responseCode == 200){
                        JSONArray jsonArray = new JSONArray(s);
                        if(jsonArray.length() > 0){
                            JSONObject jsonResp = (JSONObject) jsonArray.get(0);
                            BrokerageDialog dialog = new BrokerageDialog(getContext(),jsonResp,structBuySell.scripDetails.getFormattedScripName());
                            dialog.show();
                        }else{
                            GlobalClass.showAlertDialog("Record not found.");
                        }
                    }else{
                        GlobalClass.showAlertDialog("Please try after some time.");
                    }
                }
            }catch (Exception ex){
                GlobalClass.onError("",ex);
            }
        }
    }
    private void placeBtnClick(boolean isFromOderSave) {

        /*
        if (UserSession.getLoginDetailsModel().isIntradayDelivery()
                && (structBuySell.scripDetails.segment.getValue() == eExch.NSE.value ||
                structBuySell.scripDetails.segment.getValue() == eExch.BSE.value)) {
            if (!structBuySell.scripDetails.enableIntraDelForCategory() ) {
                if (orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                    BaseActivity activity = (BaseActivity) BaseActivity.getLetestContext();
                    activity.showMsgDialog(R.string.Scrip_not_allowed_intraday, false);
                    return;
                }
            }
        }*/

        String strQty, strDiscQty, strPrice, strTrgrPrice;
        strQty = qtyEditText.getText().toString().trim();
        strDiscQty = discQtyEditText.getText().toString().trim();
        strPrice = priceEditText.getText().toString().trim();
        strTrgrPrice = triggerPriceEditText.getText().toString().trim();

        int qty = getStringToInt(strQty);
        int discQty = getStringToInt(strDiscQty);
        double limitPrice = getStringToDouble(strPrice);
        double trgrPrice = getStringToDouble(strTrgrPrice);
        int isIOC = 0, isAtMkt = 0, withSL = 0;
        int seg = structBuySell.scripDetails.segment.getValue();

        if (marketRd.isChecked()) {
            isAtMkt = eOrderType.MARKET.value;
            limitPrice = 0;
            trgrPrice = 0;
        }
        if (stopLossChk.isChecked()) {
            withSL = 1;
        } else {
            withSL = 0;
            trgrPrice = 0;
        }
        if (checkboxIOC.isChecked()) {
            isIOC = 1;
            discQty = 0;
            trgrPrice = 0;
            withSL = 0;
        }
        structBuySell.mktWatch = mktWatch;

        String orderValidationMsg = structBuySell.orderValiDation(qty,discQty,seg,limitPrice,trgrPrice,isAtMkt,withSL);
        if(!isFromOderSave) {
            if (orderValidationMsg.equalsIgnoreCase("") && delintraRG.getVisibility() == View.VISIBLE) {
                String ordTypeselection = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
                if (ordTypeselection.equalsIgnoreCase(eDelvIntra.BRACKETORDER.name)) {
                    structBuySell.abcTicks = bracketAbsoluteRD.isChecked() ? eAbsTicks.ABS : eAbsTicks.TICKS;
                    orderValidationMsg = structBuySell.validateBracketOrder(bracketSquareET.getText().toString(), bracketStopLossET.getText().toString());
                } else if (ordTypeselection.equalsIgnoreCase(eDelvIntra.COVERORDER.name)) {
                    structBuySell.abcTicks = bracketAbsoluteRD.isChecked() ? eAbsTicks.ABS : eAbsTicks.TICKS;
                    orderValidationMsg = structBuySell.validateCoverOrder(bracketStopLossET.getText().toString());
                }
            }
        }

        if (orderValidationMsg.equals("")){

            structBuySell.qty.setValue(qty);
            structBuySell.limitPrice.setValue(limitPrice);
            structBuySell.triggerPrice.setValue(trgrPrice);
            structBuySell.discloseQty.setValue(discQty);
            String errorMsg = "";
            if(!isFromOderSave) {
                String datestr = DateUtil.getCurrentDate();
                LinkedHashMap<String, String> dqList = new LinkedHashMap<>();
                if (!VenturaApplication.getPreference().getSharedPrefFromTag(eConstant.DATE_FORDQ.name, "").matches(datestr)) {
                    VenturaApplication.getPreference().storeSharedPref(eConstant.DATE_FORDQ.name, datestr);
                } else {
                    dqList = VenturaApplication.getPreference().getDQ();
                }
                //TODO set key.....
                dqList.put("", discQty + "");
                VenturaApplication.getPreference().setDQ(dqList);
                if (seg != eExch.FNO.value) {
                    errorMsg = structBuySell.scripDetails.getT4TMsg();
                    if (!errorMsg.equalsIgnoreCase("")) {
                        new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, T4TTAG);
                    } else {
                        errorMsg = structBuySell.scripDetails.getIllequideMsg();
                        if (!errorMsg.equalsIgnoreCase("")) {
                            new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, illTAG);
                        } else {
                            errorMsg = structBuySell.scripDetails.surveillanceMassage.getValue();
                            if (!errorMsg.equalsIgnoreCase("")) {
                                new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, surveilanceTAG);
                            }
                        }
                    }
                } else {
                    errorMsg = structBuySell.scripDetails.getBanScripMsg();
                    if (!errorMsg.equalsIgnoreCase("")) {
                        new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, banTAG);
                    } else {
                        errorMsg = structBuySell.scripDetails.getPhysicalDeliveryMsg();
                        if (!errorMsg.equalsIgnoreCase("")) {
                            new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, physicalTAG);
                        } else {
                            errorMsg = structBuySell.scripDetails.surveillanceMassage.getValue();
                            if (!errorMsg.equalsIgnoreCase("")) {
                                new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, surveilanceTAG);
                            }
                        }
                    }
                }
            }
            if (errorMsg.equalsIgnoreCase("")) {
                showConfirmAlert(true,isFromOderSave);
            }
        }else {
            GlobalClass.showAlertDialog(orderValidationMsg);
        }
    }
    private void saveOrderCredentials(){
        if(ordsavechkbox.isChecked()){
            OrderSaveModel orderSaveModel = new OrderSaveModel(structBuySell.scripDetails.scripCode.getValue(),
                    structBuySell.qty.getValue(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,stopLossChk.isChecked());
            PreferenceHandler.getOrderSaveList().put(structBuySell.scripDetails.scripCode.getValue(),orderSaveModel);
            PreferenceHandler.setOrderSaveList();
        }else {
            try {
                OrderSaveModel orderSaveModel = PreferenceHandler.getOrderSaveList().get(scripCode);
                if (orderSaveModel != null) {
                    if(!(orderSaveModel.getQty()+"").equalsIgnoreCase(qtyEditText.getText().toString())) {
                        PreferenceHandler.getOrderSaveList().remove(scripCode);
                        PreferenceHandler.setOrderSaveList();
                    }
                }
            } catch (Exception ex) {
                GlobalClass.onError("", ex);
            }
        }
    }

    private void showConfirmAlert(boolean checkeDis,boolean isFromOrderSave) {

        if(!isFromOrderSave){
            saveOrderCredentials();
        }
        boolean isOpen = true;
        if(!UserSession.getClientResponse().isEDISActive()) {
            checkeDis = false;
        }
        if(checkeDis && !isFromOrderSave
                && (structBuySell.buyOrSell == eOrderType.SELL)
                && (structBuySell.modifyOrPlace == eOrderType.PLACE)
                && (selectedHoldingRow != null)){
            selectedHoldingRow.setEdisQtyForSendToEDIS(0);
            boolean isIntraday = false;
            try {
               if (delintraRG.getVisibility() == View.VISIBLE) {
                    String intraDel = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
                    if (intraDel.equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                        isIntraday = true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(!isIntraday) {
                StrucPendingOrderSummary totalSell = GlobalClass.getClsOrderBook().getPendingSELLOrdersForScripCode(structBuySell.scripDetails.scripCode.getValue());
                StructOpenPositionSummary totalNet = GlobalClass.getClsNetPosn().getOpenPositionsForScripCode(structBuySell.scripDetails.scripCode.getValue());
                //int edisQty = selectedHoldingRow.getEdisQty();
                StructHoldingsReportReplyRecord_Pointer holdingReport = GlobalClass.getClsMarginHolding().getHoldingEquityForScripCode(structBuySell.scripDetails.scripCode.getValue());
                if(holdingReport != null) {
                    int edisQty = holdingReport.totalQty.getValue();// - holdingReport.pOA.getValue();
                    int enteredQty = structBuySell.qty.getValue();
                    int totalEdisRequired = enteredQty + totalSell.totalPending.getValue() +(-1 * totalNet.totalPosition.getValue()); //(totalNet.totalPosition.getValue() > 0 ? (-1 * totalNet.totalPosition.getValue()) : 0);
                    int finaleDISRequired = totalEdisRequired - edisQty;
                    int totalQty = holdingReport.totalQty.getValue() + holdingReport.pOA.getValue();
                    if (totalEdisRequired > totalQty) {
                        isOpen = false;
                        GlobalClass.showAlertDialog("Holding qty Exhausted. TotalEdisRequired" +totalEdisRequired + " :TotalQty: " +totalQty);
                    } else if (finaleDISRequired > 0) {
                        selectedHoldingRow.setEdisQtyForSendToEDIS(finaleDISRequired);
                        String conf = "This order requires eDIS for " + finaleDISRequired + " shares. Do you want to proceed?";
                        isOpen = false;
                        new AlertBox(GlobalClass.latestContext, "Yes", "No", conf, this, "edis");
                    }
                }
            }
        }
        if(isOpen) {
            String confMsg = "";
            if (structBuySell.modifyOrPlace == eOrderType.MODIFY) {
                confMsg = "MODIFY To : ";
            }
            switch (structBuySell.buyOrSell) {
                case BUY:
                case MODIFYBUY:
                    confMsg = confMsg + (structBuySell.scripDetails.segment.getValue() == eExch.SLBS.value ? "Recall" : "Buy");
                    break;
                case SELL:
                case MODIFYSELL:
                    confMsg = confMsg + (structBuySell.scripDetails.segment.getValue() == eExch.SLBS.value ? "Lend" : "Sell");
                    break;
            }
            confMsg = confMsg + " " + structBuySell.qty.getValue() + " " + structBuySell.scripDetails.getFormattedScripName();

            if (marketRd.isChecked()) {
                confMsg = confMsg + " @ " + " Market rate";
            } else {
                if(isFromOrderSave){
                    confMsg = confMsg + " @ " + " Limit rate";
                }else {
                    confMsg = confMsg + " @ " + formatter.format(structBuySell.limitPrice.getValue());
                }
            }
            if (stopLossChk.isChecked()) {
                if(isFromOrderSave){
                    confMsg = confMsg + " with Trigger price";
                }else {
                    confMsg = confMsg + " with TP @ " + formatter.format(structBuySell.triggerPrice.getValue());
                }
            }
            if(isFromOrderSave){
                confMsg = confMsg + "\n\nSave these order credentials for this ";
                if(structBuySell.scripDetails.segment.getValue() == eExch.NSE.value ||
                        structBuySell.scripDetails.segment.getValue() == eExch.BSE.value){
                    
                    confMsg = confMsg + "stock?";
                }else{
                    confMsg = confMsg + "contract?";
                }
                new AlertBox(GlobalClass.latestContext, "Confirm", "No", confMsg, this, orderSaveTag);
            }else {
                if (checkboxIOC.isChecked()) {
                    confMsg = confMsg + " with IOC";
                } else if (structBuySell.discloseQty.getValue() > 0) {
                    if (structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
                        confMsg = confMsg + " with Disc. Lots " + structBuySell.discloseQty.getValue();
                    } else {
                        confMsg = confMsg + " with Disc. Qty " + structBuySell.discloseQty.getValue();
                    }
                }
                confMsg = confMsg + " ? ";
                try {
                    if (delintraRG.getVisibility() == View.VISIBLE) {
                        String intraDel = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
                        if (intraDel.equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                            confMsg = confMsg + "\n\nNote : This is an Intraday Multiplier order and will be Cancelled/ SquaredOff at the end of the Day.";
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                new AlertBox(GlobalClass.latestContext, "Yes", "No", confMsg, this, placeTAG);
            }
        }
    }

    private int getStringToInt(String str) {
        if (str.equalsIgnoreCase("")) {
            return 0;
        } else {
            str = str.replace(",", "");
            return Integer.parseInt(str);
        }
    }

    private double getStringToDouble(String str) {
        if (str.equalsIgnoreCase(""))
            return 0;
        else {
            str = str.replace(",", "");
            return Double.parseDouble(str);
        }
    }

    private void sendOrderReqToServer() {
        if(structBuySell.modifyOrPlace == eOrderType.MODIFY){
            /*if(orderTypeSpinner.getVisibility() == View.VISIBLE && orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.BRACKETORDER.name)){
                modifyOrderBracket();
            } else if(orderTypeSpinner.getVisibility() == View.VISIBLE && orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.COVERORDER.name)){
                modifyOrderCover();
            } else {*/
                String strIsOK = structBuySell.order.canOrderBeModified(structBuySell.qty.getValue(),
                        structBuySell.discloseQty.getValue(), structBuySell.limitPrice.getValue(),
                        structBuySell.triggerPrice.getValue());
                if (strIsOK.equalsIgnoreCase("")) {
                    if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
                        modifyOrderFNO();
                    } else if (structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
                        modifyOrderCURR();
                    } else if (structBuySell.scripDetails.segment.getValue() == eExch.SLBS.value) {
                        modifyOrderSLBM();
                    }
                    else {
                        modifyOrderCash();
                    }
                } else {
                    new AlertBox(GlobalClass.latestContext, "", "OK", strIsOK, false);
                }
            //}
        } else{
            /*if(orderTypeSpinner.getVisibility() == View.VISIBLE && orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.BRACKETORDER.name)){
                placeOrderBracket();
            } else if(orderTypeSpinner.getVisibility() == View.VISIBLE && orderTypeSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.COVERORDER.name)){
                placeOrderCover();
            } else*/ if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
                placeOrderFNO();
            } else if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
                placeOrderCURR();
            } else if(structBuySell.scripDetails.segment.getValue() == eExch.SLBS.value){
                placeOrderSLBM();
            } else {
                placeOrderCash();
            }
        }
        buysellLinear.removeAllViews();
        buysellLinear.addView(buySellBtn);
    }
    private void modifyOrderBracket(){
        try {
            //set value here...on structbuysell
            structBuySell.delvIntra = eDelvIntra.BRACKETORDER;
            structBuySell.abcTicks = bracketAbsoluteRD.isChecked() ? eAbsTicks.ABS : eAbsTicks.TICKS;
            structBuySell.bracketSquareOFF.setValue(Formatter.stringToDouble(bracketSquareET.getText().toString()));
            structBuySell.bracketStopLoss.setValue(Formatter.stringToDouble(bracketStopLossET.getText().toString()));
            structBuySell.bracketTrailingSTopLoss.setValue(Formatter.stringToDouble(bracketTradingStopLossET.getText().toString()));

            StructOCOOrder ocoOrder = new StructOCOOrder();
            ocoOrder.setDataForModifyOrder(structBuySell);
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendModifyBracketOrderReq(ocoOrder);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void modifyOrderCover(){
        try {
            structBuySell.delvIntra = eDelvIntra.COVERORDER;
            structBuySell.abcTicks = bracketAbsoluteRD.isChecked() ? eAbsTicks.ABS : eAbsTicks.TICKS;
            structBuySell.bracketSquareOFF.setValue(Formatter.stringToDouble(bracketSquareET.getText().toString()));
            structBuySell.bracketStopLoss.setValue(Formatter.stringToDouble(bracketStopLossET.getText().toString()));
            structBuySell.bracketTrailingSTopLoss.setValue(Formatter.stringToDouble(bracketTradingStopLossET.getText().toString()));

            StructOCOOrder ocoOrder = new StructOCOOrder();
            ocoOrder.setDataForModifyOrder(structBuySell);

            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendModifyBracketOrderReq(ocoOrder);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void placeOrderBracket(){
        try {
            //set value here...on structbuysell
            structBuySell.delvIntra = eDelvIntra.BRACKETORDER;
            structBuySell.abcTicks = bracketAbsoluteRD.isChecked() ? eAbsTicks.ABS : eAbsTicks.TICKS;
            structBuySell.bracketSquareOFF.setValue(Formatter.stringToDouble(bracketSquareET.getText().toString()));
            structBuySell.bracketStopLoss.setValue(Formatter.stringToDouble(bracketStopLossET.getText().toString()));
            structBuySell.bracketTrailingSTopLoss.setValue(Formatter.stringToDouble(bracketTradingStopLossET.getText().toString()));

            StructOCOOrder ocoOrder = new StructOCOOrder();
            ocoOrder.setDataForPlaceOrder(structBuySell);
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendPlaceBracketOrderReq(ocoOrder);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void placeOrderCover(){

        try {
            structBuySell.delvIntra = eDelvIntra.COVERORDER;
            structBuySell.abcTicks = bracketAbsoluteRD.isChecked() ? eAbsTicks.ABS : eAbsTicks.TICKS;
            structBuySell.bracketSquareOFF.setValue(Formatter.stringToDouble(bracketSquareET.getText().toString()));
            structBuySell.bracketStopLoss.setValue(Formatter.stringToDouble(bracketStopLossET.getText().toString()));
            structBuySell.bracketTrailingSTopLoss.setValue(Formatter.stringToDouble(bracketTradingStopLossET.getText().toString()));

            StructOCOOrder ocoOrder = new StructOCOOrder();
            ocoOrder.setDataForPlaceOrder(structBuySell);

            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendPlaceBracketOrderReq(ocoOrder);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void placeOrderCash() {

        String intraDel = eDelvIntra.DELIVERY.name;
        if (UserSession.getLoginDetailsModel().isIntradayDelivery()
                && structBuySell.scripDetails.enableIntraDelForCategory()) {
            intraDel = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
        }
        PlaceOrderReq_CASH_Pointer place = new PlaceOrderReq_CASH_Pointer();
        place.setDataForPlaceCASH(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name, intraDel);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_CASH(place);
    }

    private void placeOrderFNO() {
        String intraDel = eDelvIntra.DELIVERY.name;;
        if (UserSession.getLoginDetailsModel().isFNOIntradayDelivery()
                && structBuySell.scripDetails.enableIntraDelForCategory()) {
            intraDel = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
        }
        PlaceOrderReq_FNO_Pointer place = new PlaceOrderReq_FNO_Pointer();
        place.setDataForPlaceFNO(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,intraDel);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_FNOCURR(place,eMessageCode.PLACE_ORDER_FNO);
    }

    private void placeOrderCURR() {

        PlaceOrderReq_FNO_Pointer place = new PlaceOrderReq_FNO_Pointer();
        place.setDataForPlaceCURR(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,"");

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_FNOCURR(place,eMessageCode.PLACE_ORDER_CURR);
    }
    private void placeOrderSLBM() {

        PlaceOrderReq_SLBM_Pointer place = new PlaceOrderReq_SLBM_Pointer();
        place.setDataForPlaceSLBM(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,"");

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_SLBM(place,eMessageCode.EXCHPLACEORDER_SLBS);
    }

    private void modifyOrderCash() {

        String intraDel = "";
        if (UserSession.getLoginDetailsModel().isIntradayDelivery() && structBuySell.scripDetails
                .enableIntraDelForCategory()) {
            intraDel = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
        }
        ModifyOrderReq_CASH_Pointer place = new ModifyOrderReq_CASH_Pointer();
        place.setDataForModifyCASH(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,intraDel);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendModifyOrderReq_CASH(place);
    }

    private void modifyOrderFNO() {
        String ordType = "";
        if (UserSession.getLoginDetailsModel().isFNOIntradayDelivery() && structBuySell.scripDetails
                .enableIntraDelForCategory()) {
            ordType = deliveryRd.isChecked()?eDelvIntra.DELIVERY.name:eDelvIntra.INTRADAY.name;
        }
        ModifyOrderReq_FNO_Pointer place = new ModifyOrderReq_FNO_Pointer();
        place.setDataForMOdifyFNO(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,ordType);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendModifyOrderReq_FNOCURR(place,eMessageCode.EXCHPLACEORDER_DERIV_MODIFY_ORDER_FNO);
    }

    private void modifyOrderCURR() {

        ModifyOrderReq_FNO_Pointer place = new ModifyOrderReq_FNO_Pointer();
        place.setDataForMOdifyCURR(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,"");

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendModifyOrderReq_FNOCURR(place,eMessageCode.MODIFY_ORDER_CURR);
    }
    private void modifyOrderSLBM() {

        ModifyOrderReq_SLBM_Pointer place = new ModifyOrderReq_SLBM_Pointer();
        place.setDataForMOdifySLBM(structBuySell,stopLossChk.isChecked(),checkboxIOC.isChecked(),limitRd.isChecked()?eOrderType.LIMIT.name : eOrderType.MARKET.name,"");

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendModifyOrderReq_SLBM(place,eMessageCode.EXCHMODIFYORDER_SLBS);
    }

    class MktDepthHandlerRC extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt("msgCode");
                    eMessageCode eshgCode = eMessageCode.valueOf(msgCode);
                    switch (eshgCode) {
                        case STATIC_MW:
                        case LITE_MW:
                        case OpenInt:
                            int token = refreshBundle.getInt("scripCode");
                            if (token == scripCode) {
                                refreshMktWatchData();
                            }
                            if(token == nseBseScripCode){
                                StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001DataforDepth(nseBseScripCode);
                                setNSEBSERate(mktWatch);
                            }
                            break;
                        case LITE_MD:
                            token = refreshBundle.getInt("scripCode");
                            if (token == scripCode) {
                                refreshMD(scripCode);
                            }
                            break;
                        case TRADE_EXECUTION:
                            refreshMktWatchData();
                            break;
                        case SCRIPT_DETAILS_Response:
                            handleScripDetailResponse();
                            break;
                        case NSEBSE_SCRIPTCODE:
                            token = refreshBundle.getInt("scripCode");
                            String scriptName = refreshBundle.getString("scripName");
                            if (token > 0) handleNseBse(token, scriptName);
                            break;
                        case ADDSCRIPT_TOGROUP:
                            int scripCode = refreshBundle.getInt("scripCode");
                            String scripName = refreshBundle.getString("scripName");
                            tempMap.put(scripName, scripCode);
                            scripNameList.add(scripName);
                            adapter.notifyDataSetChanged();
                            spinner.setSelection(adapter.getPosition(scripName));
                            break;
                        case REFRESHDEPTH:
                            setPendingOrders();
                            break;
                        case NEWS_SCRIP:
                            handleScripNews(refreshBundle);
                            break;
                        case DARTEVENT:
                            if(actionWatch != null) {
                                actionWatch.setEvent(new StructxMKTEventRes(refreshBundle.getByteArray(eForHandler.RESDATA.name)));
                            }
                            break;
                        /*case SET_ORDER:
                            setOrders(refreshBundle);
                            break;*/
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

    private ArrayList<NewsScripResponseInnerStructure> _mNews = new ArrayList<>();


    private void handleScripNews(Bundle refreshBundle) {
        try {
            ArrayList<NewsScripResponseInnerStructure> _tempList = new ArrayList<>();
            NewsScripOuterStructure nsos = new NewsScripOuterStructure(refreshBundle.getByteArray(eForHandler.RESDATA.name));
            for (int i = 0;i<nsos.newsArray.length;i++){
                NewsScripResponseInnerStructure nsris = nsos.newsArray[i];
                _tempList.add(nsris);
            }
            if (nsos.isDownloadCompleted()){
                _mNews.clear();
                _mNews.addAll(_tempList);
                _tempList.clear();
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private class MktDepthRowView {
        TextView noOfBuyer;
        TextView buyQty;
        TextView buyRate;
        TextView sellRate;
        TextView sellQty;
        TextView noOfSeller;
        MktDepthRowView(TextView _noOfBuyer,
        TextView _buyQty,
        TextView _buyRate,
        TextView _sellRate,
        TextView _sellQty,
        TextView _noOfSeller){
            this.noOfBuyer = _noOfBuyer;
            this.buyQty = _buyQty;
            this.buyRate = _buyRate;
            this.sellRate = _sellRate;
            this.sellQty = _sellQty;
            this.noOfSeller = _noOfSeller;
        }

        public void setMktDepthValue(LiteMDDetails liteMDDetails1, LiteMDDetails liteMDDetails5, int diveder) throws Exception{

            noOfBuyer.setText(liteMDDetails1.noOfOrders.getValue() + "");
            buyQty.setText(liteMDDetails1.qty.getValue() + "");
            buyRate.setText(formatter.format(liteMDDetails1.price.getValue()/diveder));
            sellRate.setText(formatter.format(liteMDDetails5.price.getValue()/diveder));
            sellQty.setText(liteMDDetails5.qty.getValue() + "");
            noOfSeller.setText(liteMDDetails5.noOfOrders.getValue() + "");
        }
    }
}
