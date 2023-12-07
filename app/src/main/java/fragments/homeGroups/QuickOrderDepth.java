package fragments.homeGroups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

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
import Structure.Request.RC.PlaceOrderReq_CASH_Pointer;
import Structure.Request.RC.PlaceOrderReq_FNO_Pointer;
import Structure.Response.BC.LiteMDDetails;
import Structure.Response.BC.LiteMktDepth;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.Scrip.ScripDetail;
import butterknife.BindView;
import butterknife.ButterKnife;
import chart.GraphFragment;
import connection.SendDataToBCServer;
import connection.SendDataToRCServer;
import enums.eConstant;
import enums.eDelvIntra;
import enums.eDivider;
import enums.eExch;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.ePrefTAG;
import enums.eReports;
import enums.eShowDepth;
import enums.eTradeFrom;
import fragments.LatestResultFragment;
import fragments.ValuetionFragment;
import handler.StrucPendingOrderSummary;
import interfaces.OnAlertListener;
import interfaces.OnPopupListener;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.InputFilterMinMax;
import utils.ScreenColor;
import utils.StaticMethods;
import utils.TubeView;
import utils.UserSession;
import view.AlertBox;
import view.CustomPopupWindow;
import view.help.DepthHelp;

/**
 * Created by XTREMSOFT on 8/22/2016.
 */
@SuppressLint("ValidFragment")
public class QuickOrderDepth extends Fragment implements View.OnClickListener, OnPopupListener, AdapterView.OnItemSelectedListener, OnAlertListener, RadioGroup.OnCheckedChangeListener {

    private ArrayList noOfBuyerArr, buyQtyArr, buyRateArr, sellRateArr, sellQtyArr, noOfSellerArr;
    private String scripName = "", exchName = "";
    private ArrayList<String> scripNameList;
    private ArrayAdapter adapter;
    private final String BSE = "BE", NSE = "NE", className = getClass().getName();
    private StaticLiteMktWatch mktWatch;
    private HashMap<String, Integer> tempMap;
    private int scripCode;
    private ArrayList<GroupsTokenDetails> grpScripList;
    private eShowDepth showDepth;
    private eOrderType buySellType, placeModifyordertype;
    private StructBuySell structBuySell = null;
    private StrucPendingOrderSummary strucPendingOrderSummary;
    private StructMobNetPosition structMobNetPosition;
    private boolean isCancel = false;
    private short radioPos = 1;
    private int qtyChange = 1;
    private double priceChange = 1.00;

    private Spinner orderType, mktLimitSpinner;
    private Button btnClose, qtyMinus, qtyPlus, priceMinus, pricePlus, dqMinus,
            dqPlus, tpMinus, tpPlus, placeBtn;
    private CheckBox checkboxIOC, stopLossChk;
    private EditText qty, price, discQty, triggerPrice;
    private LinearLayout priceLayout, tPriceLayout, dqLayout;
    private TextView buysellTitle;
    private HomeActivity.RadioButtons radioButtons;

    final String placeTAG = "place";
    final String T4TTAG = "t4t";
    final String illTAG = "ILL";
    final String banTAG = "ban";

    private Timer timer;
    private int timerCount = 0;
    private boolean fromDetails = false;
    private NumberFormat formatter;

    public QuickOrderDepth(){super();}

    public QuickOrderDepth(int selectedScripCode, eShowDepth showDepth, ArrayList<GroupsTokenDetails> grpScripList,
                           StructBuySell structBS, HomeActivity.RadioButtons radioButtons, boolean fromDetails) {
        this.showDepth = showDepth;
        this.grpScripList = new ArrayList<>(grpScripList);
        this.scripCode = selectedScripCode;
        this.structBuySell = structBS;
        this.radioButtons = radioButtons;
        this.fromDetails = fromDetails;
    }


    private LayoutInflater layoutInflater;
    private View view, buySellBtn, orderLayout;
    private Button buyBtn, sellBtn;

    @BindView(R.id.depth_addscriptBtn)
    ImageButton depth_addscriptBtn;
//    @BindView(R.id.be_ne_btn)
//    ImageButton be_ne_btn;

    @BindView(R.id.button_valution)
    Button button_valution;
    @BindView(R.id.button_latestresult)
    Button button_latestresult;
    @BindView(R.id.button_viewchart)
    Button button_viewchart;

    @BindView(R.id.oi_curr_value)
    TextView oi_curr;
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
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).CheckRadioButton(radioButtons);
        buySellType = eOrderType.NONE;
        if (view != null) return view;
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.marketdepth_screen, container, false);
        ButterKnife.bind(this, view);

        buySellBtn = layoutInflater.inflate(R.layout.button_linear, null);
        buyBtn = (Button) buySellBtn.findViewById(R.id.button_buy);
        sellBtn = (Button) buySellBtn.findViewById(R.id.button_sell);
        buysellLinear.removeAllViews();
        buysellLinear.addView(buySellBtn);

        orderLayout = layoutInflater.inflate(R.layout.buysellwindownew1, null);
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
        checkboxIOC = (CheckBox) orderLayout.findViewById(R.id.checkboxIOC);
        stopLossChk = (CheckBox) orderLayout.findViewById(R.id.stopLoss);
        qty = (EditText) orderLayout.findViewById(R.id.qty);
        price = (EditText) orderLayout.findViewById(R.id.price);
        discQty = (EditText) orderLayout.findViewById(R.id.discQty);
        triggerPrice = (EditText) orderLayout.findViewById(R.id.triggerPrice);
        //price.setFilters(new InputFilter[]{new InputFilterMinMax()});
        triggerPrice.setFilters(new InputFilter[]{new InputFilterMinMax()});
        priceLayout = (LinearLayout) orderLayout.findViewById(R.id.priceLayout);
        tPriceLayout = (LinearLayout) orderLayout.findViewById(R.id.tPriceLayout);
        dqLayout = (LinearLayout) orderLayout.findViewById(R.id.dqLayout);
        buysellTitle = (TextView) orderLayout.findViewById(R.id.buysellTitle);
        init(view);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MktdepthFragmentRC.mktDepthUiHandler = new MktDepthHandler();
            GlobalClass.tradeFromDepth = false;
            GlobalClass.isMktDepthOpen = 1;
            senReqToserver();
            refreshMktWatchData();
            formatter = Formatter.getFormatter(mktWatch.getSegment());
            refreshMD(scripCode);
            setPendingOrders();
            if (buysellBottom.getVisibility() == View.VISIBLE) {
                detailsTopView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MktdepthFragmentRC.mktDepthUiHandler = null;
        cancelTimer();
    }


    private void init(View view) {
        try {
            noOfBuyerArr = new ArrayList();
            buyQtyArr = new ArrayList();
            buyRateArr = new ArrayList();
            noOfSellerArr = new ArrayList();
            sellQtyArr = new ArrayList();
            sellRateArr = new ArrayList();
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
            depth_addscriptBtn.setOnClickListener(this);
            //be_ne_btn.setOnClickListener(this);
            buyBtn.setOnClickListener(this);
            sellBtn.setOnClickListener(this);
            msgBtn.setOnClickListener(this);
            orderbookBtn.setOnClickListener(this);
            tradebookBtn.setOnClickListener(this);
            netpositionBtn.setOnClickListener(this);
            speedometerLayout.setOnClickListener(this);
            indicatorLinear.setOnClickListener(this);
            if (structBuySell != null) {
                //be_ne_btn.setEnabled(false);
                openBuySellWindowHideBtn(structBuySell.buyOrSell, structBuySell.modifyOrPlace);

            } else {
                spinner.setOnItemSelectedListener(this);
                structBuySell = new StructBuySell();
                if (VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.DEPTH_HELP.name, true)) {
                    Dialog depthHelp = new DepthHelp(GlobalClass.latestContext,false);
                    depthHelp.show();
                }
            }
            IndicatorRepeatly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void IndicatorRepeatly() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timerCount < 4) {
                    getActivity().runOnUiThread(() -> enableDisableIndicator());
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

    private void setPendingOrders() {
        detailsRadiogrp.setVisibility(View.GONE);
        detailsTopView.setVisibility(View.VISIBLE);
        if (UserSession.isTradeLogin()) {
            orderRd.setVisibility(View.GONE);
            qtyRd.setVisibility(View.GONE);

            strucPendingOrderSummary = GlobalClass.getClsOrderBook().getPendingOrdersForScripCode(scripCode);
            if (strucPendingOrderSummary.scripCode.getValue() == scripCode) {
                detailsRadiogrp.setVisibility(View.VISIBLE);
                buysellBottom.setVisibility(View.GONE);
                detailsTopView.setVisibility(View.GONE);
                orderRd.setVisibility(View.VISIBLE);
                orderRd.setText("Pend Qty: " + strucPendingOrderSummary.totalPending.getValue());
            }
            int netQty = 0;
            if (GlobalClass.getClsNetPosn().m_allScripCode.contains(scripCode)) {
                structMobNetPosition = GlobalClass.getClsNetPosn().getPositionForscripcode(scripCode);
                netQty = structMobNetPosition.getNetQty();
                if (netQty != 0) {
                    detailsRadiogrp.setVisibility(View.VISIBLE);
                    buysellBottom.setVisibility(View.GONE);
                    detailsTopView.setVisibility(View.GONE);
                    qtyRd.setVisibility(View.VISIBLE);
                    String netText = netQty > 0 ? "+" + netQty : "" + netQty;
                    qtyRd.setText("Posn: " + netText);
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
            detailsRadiogrp.setOnCheckedChangeListener(QuickOrderDepth.this);
        } else {
            details.setVisibility(View.VISIBLE);
        }

    }

    private void viewInit(View view) {
        noOfBuyerArr.add((TextView) view.findViewById(R.id.noOfBuyers0));
        noOfBuyerArr.add((TextView) view.findViewById(R.id.noOfBuyers1));
        noOfBuyerArr.add((TextView) view.findViewById(R.id.noOfBuyers2));
        noOfBuyerArr.add((TextView) view.findViewById(R.id.noOfBuyers3));
        noOfBuyerArr.add((TextView) view.findViewById(R.id.noOfBuyers4));

        buyQtyArr.add((TextView) view.findViewById(R.id.buyQty0));
        buyQtyArr.add((TextView) view.findViewById(R.id.buyQty1));
        buyQtyArr.add((TextView) view.findViewById(R.id.buyQty2));
        buyQtyArr.add((TextView) view.findViewById(R.id.buyQty3));
        buyQtyArr.add((TextView) view.findViewById(R.id.buyQty4));

        buyRateArr.add((TextView) view.findViewById(R.id.buyRate0));
        buyRateArr.add((TextView) view.findViewById(R.id.buyRate1));
        buyRateArr.add((TextView) view.findViewById(R.id.buyRate2));
        buyRateArr.add((TextView) view.findViewById(R.id.buyRate3));
        buyRateArr.add((TextView) view.findViewById(R.id.buyRate4));

        sellQtyArr.add((TextView) view.findViewById(R.id.sellQty0));
        sellQtyArr.add((TextView) view.findViewById(R.id.sellQty1));
        sellQtyArr.add((TextView) view.findViewById(R.id.sellQty2));
        sellQtyArr.add((TextView) view.findViewById(R.id.sellQty3));
        sellQtyArr.add((TextView) view.findViewById(R.id.sellQty4));

        sellRateArr.add((TextView) view.findViewById(R.id.sellRate0));
        sellRateArr.add((TextView) view.findViewById(R.id.sellRate1));
        sellRateArr.add((TextView) view.findViewById(R.id.sellRate2));
        sellRateArr.add((TextView) view.findViewById(R.id.sellRate3));
        sellRateArr.add((TextView) view.findViewById(R.id.sellRate4));

        noOfSellerArr.add((TextView) view.findViewById(R.id.noOfSellers0));
        noOfSellerArr.add((TextView) view.findViewById(R.id.noOfSellers1));
        noOfSellerArr.add((TextView) view.findViewById(R.id.noOfSellers2));
        noOfSellerArr.add((TextView) view.findViewById(R.id.noOfSellers3));
        noOfSellerArr.add((TextView) view.findViewById(R.id.noOfSellers4));
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
              //  setExchNameBTNCLICK();
                //break;
            case R.id.depth_addscriptBtn:
                CustomPopupWindow customPopupWindow = new CustomPopupWindow(this);
                customPopupWindow.openSearchScripWindow();
                break;
            case R.id.button_valution:
                showValuation();
                break;
            case R.id.button_latestresult:
                showLatestResult();
                break;
            case R.id.button_buy:
                if (!GlobalClass.indexScripCodeOrNot(scripCode)) {
                    openBuySellWindowHideBtn(eOrderType.BUY, eOrderType.PLACE);
                }
                break;
            case R.id.button_sell:
                if (!GlobalClass.indexScripCodeOrNot(scripCode)) {
                    openBuySellWindowHideBtn(eOrderType.SELL, eOrderType.PLACE);
                }
                break;
            case R.id.speedometerLayout:
                cancelTimer();
                enableDisableIndicator();
                break;
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
            case R.id.button_viewchart:
                showChart();
                break;
            case R.id.btnClose:
                if (structBuySell.showDepth != eShowDepth.MKTWATCH) {
                    GlobalClass.fragmentManager.popBackStackImmediate();
                } else {
                    structBuySell.fromSave = false;
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
                    if (Integer.parseInt(qty.getText().toString().trim()) > structBuySell.netQty.getValue()) {
                        String msg = "Max qty allowed is " + structBuySell.netQty.getValue();
                        new AlertBox(GlobalClass.latestContext, "", "OK", msg, false);
                        //GlobalClass.showToast(GlobalClass.latestContext, "Max qty allowed is "+structBuySell.netQty.getValue());
                    } else {
                        placeBtnClick();
                    }
                } else {
                    placeBtnClick();
                }
                break;
            case R.id.qtyPlus:
                setQty(qty, true);
                break;
            case R.id.qtyMinus:
                setQty(qty, false);
                break;
            case R.id.priceMinus:
                setPrice(price, false);
                break;
            case R.id.pricePlus:
                setPrice(price, true);
                break;
            case R.id.dqMinus:
                setQty(discQty, false);
                break;
            case R.id.dqPlus:
                setQty(discQty, true);
                break;
            case R.id.tpPlus:
                setPrice(triggerPrice, true);
                break;
            case R.id.tpMinus:
                setPrice(triggerPrice, false);
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
        String rawQty = qty.getText().toString().isEmpty() ? "0" : qty.getText().toString();
        if (forIncrement) {
            qty.setText(incrementQty(Double.parseDouble(rawQty)));
        } else {
            qty.setText(decrementQty(Double.parseDouble(rawQty)));
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
        double val = (Math.round((double) value / (double) priceChange) + 1) * (double) priceChange;
        return String.format("%.2f", val);
    }

    private String decrementValue(double value) {

        double val1 = (Math.ceil((double) value / (double) priceChange) - 1) * (double) priceChange;
        if (val1 < 0) {
            val1 = 0;
        }
        /*
        double val = value;
        if (val != 0) val -= priceChange;
        val = (Math.ceil(val /priceChange))*priceChange;*/
        return String.format("%.2f", val1);
    }

    private void openReport(eReports position) {
        if (UserSession.isTradeLogin()) {
            ((HomeActivity) getActivity()).setSpinner(position.name,false);
            GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
        } else {
            GlobalClass.fragmentTransaction(new TradeFragment(eTradeFrom.MKTDEPTH), R.id.container_body, true, "");
        }
    }

    private void openBuySellWindowHideBtn(eOrderType bsorderType, eOrderType plcModOrdType) {
        try {
            if (UserSession.isTradeLogin()) {
                this.placeModifyordertype = plcModOrdType;
                this.buySellType = bsorderType;
                final ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(scripCode);
                if (scripDetail != null) {
                    buysellLinear.removeAllViews();
                    buysellLinear.addView(orderLayout);
                    if (detailsRadiogrp.getVisibility() == View.GONE) {
                        buysellBottom.setVisibility(View.VISIBLE);
                        detailsTopView.setVisibility(View.GONE);
                    }
                    scrollView.post(() -> scrollView.smoothScrollTo(0, buysellTop.getBottom()));
                    if (!structBuySell.fromSave) {
                        structBuySell.qty.setValue(0);
                        structBuySell.discloseQty.setValue(0);
                        structBuySell.limitPrice.setValue(0);
                        structBuySell.triggerPrice.setValue(0);
                        structBuySell.isStopLoss.setValue(false);
                        structBuySell.isMarket.setValue(false);
                    }
                    if (MktdepthFragmentRC.mktDepthUiHandler == null)
                        MktdepthFragmentRC.mktDepthUiHandler = new MktDepthHandler();
                    structBuySell.mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
                    qty.setText("");
                    price.setText("");
                    discQty.setText("");
                    triggerPrice.setText("");
                    if(scripDetail.segment.getValue() == eExch.NSECURR.value){
                        qty.setHint("Lots");
                        discQty.setHint("Lots");
                    }
                    else{
                        qty.setHint("Qty");
                        discQty.setHint("Qty");
                    }
                    openBuySellWindow(buySellType, scripDetail, placeModifyordertype);
                } else {
                    GlobalClass.showProgressDialog("Please wait...");
                }
            } else {
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
        if(scripDetail.segment.getValue() == eExch.NSECURR.value){
            qty.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5) });
            discQty.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(5) });
            price.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
            triggerPrice.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(8) });
        }
        else{
            qty.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6) });
            discQty.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6) });
            price.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(9) });
            triggerPrice.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(9) });
        }
        initIOCCheck();
        initStopLoss();
        initOrderSpinner();
        initMarketSpinner();
        finalInit();
        btnClose.setOnClickListener(this);
        placeBtn.setOnClickListener(this);
        placeBtnClick();
    }

    private void finalInit() {
        if (structBuySell.buyOrSell == eOrderType.BUY) {
            buysellTitle.setText("Buy Order");
            buysellTitle.setTextColor(ScreenColor.GREEN);
            placeBtn.setText("Place Buy Order");
        } else {
            buysellTitle.setText("Sell Order");
            buysellTitle.setTextColor(ScreenColor.RED);
            placeBtn.setText("Place Sell Order");
        }
        if (structBuySell.modifyOrPlace == eOrderType.MODIFY) {
            placeBtn.setText("Modify");
        }

        formatter = Formatter.getFormatter(structBuySell.scripDetails.segment.getValue());

        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value){
            //|| structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
            visibilityFORFNO(View.VISIBLE, true);
            if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value )
                dqLayout.setVisibility(View.GONE);
            if (!structBuySell.scripDetails.cpType.getValue().equalsIgnoreCase("xx")) {           //Market orders in FNO scrips are only enabled for Near and Next month future scrips for Ventura
                mktLimitSpinner.setEnabled(false);
            } else {
                if (structBuySell.scripDetails.isNearNext.getValue() == 1) {            //Difference cannot be more than 9weeks for Near and Next months
                    mktLimitSpinner.setEnabled(true);
                } else {
                    mktLimitSpinner.setEnabled(false);
                }
            }
        } else {
            visibilityFORFNO(View.GONE, false);
        }
        if (structBuySell.order != null) {

            String quantity = structBuySell.order.qty.getValue() > 0 ? structBuySell.order.qty.getValue() + "" : "";
            if (showDepth == eShowDepth.HOLDINGFO || showDepth == eShowDepth.HOLDINGEQUITY) quantity = "";
            qty.setText(quantity);
            price.setText(formatter.format(structBuySell.order.rate.getValue()).trim());
            if (structBuySell.order.atMarket.getValue() == 'Y') {
                mktLimitSpinner.setSelection(1);
            } else {
                mktLimitSpinner.setSelection(0);
            }

            if (structBuySell.order.getTiggeredRate() > 0) {
                triggerPrice.setText(formatter.format(structBuySell.order.getTiggeredRate()));
                stopLossChk.setChecked(true);
                tPriceLayout.setVisibility(View.VISIBLE);
            }else {
                stopLossChk.setEnabled(false);
            }
        } else if (structBuySell.netPosn != null) {
            String quantity = structBuySell.netPosn.getNetQty() > 0 ? structBuySell.netPosn.getNetQty() + "" : Math.abs(structBuySell.netPosn.getNetQty()) + "";
            if (showDepth == eShowDepth.HOLDINGFO || showDepth == eShowDepth.HOLDINGEQUITY) quantity = "";
            qty.setText(quantity);
            if (structBuySell.mktWatch != null)
                price.setText(formatter.format(structBuySell.mktWatch.getLastRate()).trim());
        } else {
            if (structBuySell.mktWatch != null){
                String priceStr = formatter.format(structBuySell.mktWatch.getLastRate()).trim();
                price.setText(priceStr);
            }
            if (structBuySell.isMarket.getValue()) {
                mktLimitSpinner.setSelection(1);
            } else {
                mktLimitSpinner.setSelection(0);
            }
            if (structBuySell.netQty.getValue() > 0) {
                String quan = structBuySell.netQty.getValue() + "";
                if (showDepth != eShowDepth.HOLDINGFO || showDepth != eShowDepth.HOLDINGEQUITY) quan ="";
                qty.setText(quan);
            } else if (structBuySell.qty.getValue() > 0){
                String quan = structBuySell.qty.getValue()+"";
                if(structBuySell.fromSave){

                }
                else if (showDepth != eShowDepth.HOLDINGFO || showDepth != eShowDepth.HOLDINGEQUITY){
                    quan ="";
                }
                qty.setText(quan);
            }
            if (structBuySell.discloseQty.getValue() > 0) {
                discQty.setText(structBuySell.discloseQty.getValue() + "");
            }
            if (structBuySell.limitPrice.getValue() > 0) {
                price.setText(formatter.format(structBuySell.limitPrice.getValue()).trim());
            }
            if (structBuySell.isStopLoss.getValue()){
                tPriceLayout.setVisibility(View.VISIBLE);
            }
            if (structBuySell.triggerPrice.getValue() > 0) {
                triggerPrice.setText(structBuySell.triggerPrice.getValue() + "");
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
            priceChange = (double) structBuySell.scripDetails.tickSize.getValue();
        }
    }


    private void initIOCCheck() {
        if (structBuySell.isIoc != null) checkboxIOC.setChecked(structBuySell.isIoc.getValue());

        checkboxIOC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if (check) {
                    stopLossChk.setChecked(false);
                    stopLossChk.setEnabled(false);
                    dqLayout.setVisibility(View.GONE);
                } else {
                    if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.LIMIT.name)) {
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
        triggerPrice.setText("");
        if (structBuySell.isStopLoss != null)
            stopLossChk.setChecked(structBuySell.isStopLoss.getValue());
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
    }

    private void initMarketSpinner() {
        ArrayList<String> limitMktArr = new ArrayList<String>();
        limitMktArr.add(eOrderType.LIMIT.name);
        limitMktArr.add(eOrderType.MARKET.name);
        ArrayAdapter<String> ltMktAdapter = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item, limitMktArr);
        ltMktAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        mktLimitSpinner.setAdapter(ltMktAdapter);
        mktLimitSpinner.setOnItemSelectedListener(null);

        mktLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.LIMIT.name)) {
                    priceLayout.setVisibility(View.VISIBLE);
                    if (!checkboxIOC.isChecked()) {
                        stopLossChk.setEnabled(true);
                    }
                } else {
                    priceLayout.setVisibility(View.INVISIBLE);
                    tPriceLayout.setVisibility(View.INVISIBLE);
                    stopLossChk.setChecked(false);
                    stopLossChk.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initOrderSpinner() {
        if (UserSession.getLoginDetailsModel().isIntradayDelivery() && (structBuySell.scripDetails.segment.getValue() == eExch.NSE.value ||
                structBuySell.scripDetails.segment.getValue() == eExch.BSE.value)) {
            ArrayList<String> intraDelArr = new ArrayList<String>();
            if (structBuySell.modifyOrPlace == eOrderType.MODIFY) {
                if (structBuySell.order.orderType.getValue() == eDelvIntra.DELIVERY.value) {
                    intraDelArr.add(eDelvIntra.DELIVERY.name);
                } else {
                    intraDelArr.add(eDelvIntra.INTRADAY.name);
                }
                orderType.setEnabled(false);
            } else if (structBuySell.isSquareOff.getValue()) {
                if (structBuySell.netPosn.orderType == eDelvIntra.DELIVERY.value) {
                    intraDelArr.add(eDelvIntra.DELIVERY.name);
                } else {
                    intraDelArr.add(eDelvIntra.INTRADAY.name);
                }
                orderType.setEnabled(false);
            } else {
                intraDelArr.add(eDelvIntra.DELIVERY.name);
                intraDelArr.add(eDelvIntra.INTRADAY.name);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GlobalClass.latestContext,
                    R.layout.custom_spinner_item, intraDelArr);
            dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
            orderType.setAdapter(dataAdapter);
            orderType.setOnItemSelectedListener(null);
            orderType.post(() -> orderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!structBuySell.scripDetails.enableIntraDelForCategory() &&
                            orderType.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                        BaseActivity activity = (BaseActivity) GlobalClass.homeActivity;
                        activity.showMsgDialog(R.string.Scrip_not_allowed_intraday,false);
                        orderType.setSelection(0);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            }));
            String intDelStr = structBuySell.delvIntra.name;
            if (intraDelArr.size()>1 && intraDelArr.contains(intDelStr) ){
                orderType.setSelection(intraDelArr.indexOf(intDelStr));
            }

        } else {
            orderType.setVisibility(View.GONE);
        }
    }


    private int getOrgScripName(String scripName) {
        int index = -1;
        for (int i = 0; i < scripNameList.size(); i++) {
            String mscripName = scripNameList.get(i);
            if (scripName.length() > mscripName.length()) {
                scripName = scripName.substring(0, mscripName.length());
            }
            if (mscripName.contains(scripName)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void setExchNameBTNCLICK() {
        String tempScripName = scripName;
        if (tempScripName.substring(0, 2).equalsIgnoreCase(BSE)) {
            tempScripName = NSE + "-" + scripName.substring(3);
        } else if (tempScripName.substring(0, 2).equalsIgnoreCase(NSE)) {
            tempScripName = BSE + "-" + scripName.substring(3);
        }
        int index = getOrgScripName(tempScripName);
        if (index >= 0) {
            spinner.setSelection(index);
        } else {
            GlobalClass.showProgressDialog("Please wait...");
            GlobalClass.onError("setExchNameBTNCLICK", null);
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
    private void showChart() {
        Fragment m_fragment = GraphFragment.newInstance(scripCode, scripName);
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true,"");
    }

    private void showLatestResult() {
        Fragment m_fragment = new LatestResultFragment(scripCode, grpScripList,radioButtons);
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
    }

    private void showValuation() {
        Fragment m_fragment = new ValuetionFragment(scripCode, grpScripList,radioButtons);
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
    }

    private void setExchName() {
        try {
            //be_ne_btn.setVisibility(View.VISIBLE);
            oiLayout.setVisibility(View.GONE);
            weekLinear.setVisibility(View.VISIBLE);
            if (exchName.equalsIgnoreCase(NSE)) {
              //  be_ne_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_btnbe));
                //btnExchName.setText(BSE);
            } else if (exchName.equalsIgnoreCase(BSE)) {
                //be_ne_btn.setImageDrawable(getResources().getDrawable(R.drawable.img_btnne));
                //btnExchName.setText(NSE);
            } else {
                oiLayout.setVisibility(View.VISIBLE);
                //be_ne_btn.setVisibility(View.INVISIBLE);
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
                //yr_high.setVisibility(View.GONE);
                //yr_low.setVisibility(View.GONE);
            }
            totBuy.setText(Formatter.convertIntToValue(m_structMktDepth.tBidQ.getValue()));
            totSell.setText(Formatter.convertIntToValue(m_structMktDepth.tOffQ.getValue()));
            LiteMDDetails structMDArr[] = m_structMktDepth.structMD;
            if (noOfBuyerArr != null) {
                for (int i = 0; i < 5; i++) {
                    ((TextView) noOfBuyerArr.get(i)).setText(structMDArr[i].noOfOrders.getValue() + "");
                    ((TextView) buyQtyArr.get(i)).setText(structMDArr[i].qty.getValue() + "");
                    ((TextView) buyRateArr.get(i)).setText(formatter.format(structMDArr[i].price.getValue()/diveder));
                    ((TextView) sellRateArr.get(i)).setText(formatter.format(structMDArr[i + 5].price.getValue()/diveder));
                    ((TextView) sellQtyArr.get(i)).setText(structMDArr[i + 5].qty.getValue() + "");
                    ((TextView) noOfSellerArr.get(i)).setText(structMDArr[i + 5].noOfOrders.getValue() + "");
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

    private void senReqToserver() {
        try {
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.sendMarketDepthReq(scripCode);
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
                    //TODO changes by T
                    //  break;
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void refreshMktWatchData() {
        try {
            mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            if (mktWatch == null) {
                mktWatch = new StaticLiteMktWatch(scripCode,-1);
            } else{
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

                txtTime.setText("" + DateUtil.dateFormatter(mktWatch.getLw().time.getValue(), Constants.DDMMMYYHHMMSS));

                //totBuy.setText(Formatter.convertIntToValue(mktWatch.totalBidQty.getValue()));
                //totSell.setText(Formatter.convertIntToValue(mktWatch.totalAskQty.getValue()));

                lowerCkt.setText(formatter.format(mktWatch.getLowerCkt()));
                upperCkt.setText(formatter.format(mktWatch.getUpperCkt()));
                txtAvg.setText(formatter.format(mktWatch.getAverage()));
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
                setSpeedOMeterData(mktWatch);
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
        senReqToserver();
        refreshMktWatchData();
        refreshMD(scripCode);
        exchName = spinner.getSelectedItem().toString().split("-")[0];
        setExchName();
        setPendingOrders();
        if (radioPos == 2) {
            generatePendOdr();
        } else if (radioPos == 3) {
            generatenetQty();
        }
        buySellType = eOrderType.NONE;
        buysellLinear.removeAllViews();
        buysellLinear.addView(buySellBtn);

        GlobalClass.mktDataHandler.getScripDetailData(scripCode);
        if (buysellBottom.getVisibility() == View.VISIBLE) {
            buysellBottom.setVisibility(View.GONE);
        }
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
              /*  int backCount = GlobalClass.fragmentManager.getBackStackEntryCount();
                for (int i = 0; i < backCount; i++) {
                    GlobalClass.fragmentManager.popBackStackImmediate();
                }*/
            }
        } else if (tag.equalsIgnoreCase(T4TTAG)) {
            String errorMsg = structBuySell.scripDetails.getIllequideMsg();
            if (!errorMsg.equalsIgnoreCase("")) {
                new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, illTAG);
            } else {
                showConfirmAlert();
            }
        } else if (tag.equalsIgnoreCase(illTAG) || tag.equalsIgnoreCase(banTAG)) {
            showConfirmAlert();
        } else {
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

        }
    }

    private void generatenetQty() {
        pendingLinear.removeAllViews();
        View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.netposition_item, null);
        TextView scripName = (TextView) view.findViewById(R.id.scrept_name_textview);
        TextView netQtyRate = (TextView) view.findViewById(R.id.scrept_vol_textview);
        TextView mtm = (TextView) view.findViewById(R.id.mtm_vol_textview);
        TextView booked = (TextView) view.findViewById(R.id.booked_vol_textview);

        scripName.setText(structMobNetPosition.getFormatedScripName(true));
        netQtyRate.setText(structMobNetPosition.getNetQtyRate());
        mtm.setText(Formatter.formatter.format(structMobNetPosition.getMTM()));
        booked.setText(Formatter.formatter.format(structMobNetPosition.getBookedPL()));
        pendingLinear.addView(view);

        Button sqroffBtn = new Button(GlobalClass.latestContext);
        sqroffBtn.setBackground(getResources().getDrawable(R.drawable.border));
        sqroffBtn.setText("Square Off");
        sqroffBtn.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.item_height));
        //lp.setMargins(0,6,6,0);
        // lp.gravity =Gravity.RIGHT;
        sqroffBtn.setLayoutParams(lp);
        pendingLinear.addView(sqroffBtn);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(3);
                GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, true, "");
            }
        });

        sqroffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                squireoffClick();
            }
        });
    }

    private void squireoffClick() {

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
            TextView scripName = (TextView) view.findViewById(R.id.scripame_ordbook_textview);
            TextView buysell = (TextView) view.findViewById(R.id.buyorsell_ordbk_textview);
            TextView status = (TextView) view.findViewById(R.id.status_ordbk_textview);
            scripName.setText(sorp.getFormatedScripName(true));
            buysell.setText(sorp.getBuySell() + " " + sorp.getOrderQtyRate());
            status.setText(sorp.getFinalStatus());

            scripName.setTextColor(sorp.getTextColor());
            buysell.setTextColor(sorp.getTextColor());
            status.setTextColor(sorp.getTextColor());
            pendingLinear.addView(view);
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
                                        modifyBtnClick(sorp);
                                        break;
                                    case R.id.cancel:
                                        cancelBtnClick(sorp);
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


    class MktDepthHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt("msgCode");
                    eMessageCode eshgCode = eMessageCode.valueOf(msgCode);
                    switch (eshgCode) {

                        case LITE_MW:
                            int token = refreshBundle.getInt("scripCode");
                            if (token == scripCode) {
                                refreshMktWatchData();
                                setMarketWatchToBuysell();
                            }
                            break;
                        case LITE_MD:
                            token = refreshBundle.getInt("scripCode");
                            if (token == scripCode) {
                                refreshMD(scripCode);
                            }
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
                        /*case SET_ORDER:
                            setOrders(refreshBundle);
                            break;*/
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
        }
    }

    private void setMarketWatchToBuysell() {
        StaticLiteMktWatch mkt5001Data = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
        if (structBuySell.mktWatch== null&& mkt5001Data!=null){
            structBuySell.mktWatch = mkt5001Data;
            placeBtnClick();
        }
    }


    private void handleScripDetailResponse() {
        GlobalClass.dismissdialog();
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

    private void handleNseBse(int scripCode, String scriptName) {
        GlobalClass.dismissdialog();
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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }


    private String getScripType(String scripName) {
        if (scripName.substring(0, 2).equalsIgnoreCase("BE") || scripName.substring(0, 2).equalsIgnoreCase("NE"))
            return "";
        return "NSE";
    }


    private void placeBtnClick() {
        if (structBuySell.mktWatch == null){
            GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            return;
        }
        int qtyVal = StaticMethods.StringToInt(qty.getText().toString().trim());
        int discQtyVal = StaticMethods.StringToInt(discQty.getText().toString().trim());
        double limitPrice = StaticMethods.StringToDouble(price.getText().toString().trim());
        double trgrPrice = StaticMethods.StringToDouble(triggerPrice.getText().toString().trim());

        int isIOC = 0, isAtMkt = 0, withSL = 0;

        if (UserSession.getLoginDetailsModel().isIntradayDelivery()
                && (structBuySell.scripDetails.segment.getValue() == eExch.NSE.value ||
                structBuySell.scripDetails.segment.getValue() == eExch.BSE.value)) {

            if (!structBuySell.scripDetails.enableIntraDelForCategory() &&
                    orderType.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                BaseActivity activity = (BaseActivity) GlobalClass.homeActivity;
                activity.showMsgDialog(R.string.Scrip_not_allowed_intraday, false);
                return;
            }
        }


        int seg = structBuySell.scripDetails.segment.getValue();
        if (mktLimitSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(eOrderType.MARKET.name)) {
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
            discQtyVal = 0;
            trgrPrice = 0;
            withSL = 0;
        }
        String orderValidationMsg = structBuySell.orderValiDation(qtyVal,discQtyVal,seg,limitPrice,trgrPrice,isAtMkt,withSL);

        if (orderValidationMsg.equals("")){
            structBuySell.qty.setValue(qtyVal);
            structBuySell.limitPrice.setValue(limitPrice);
            structBuySell.triggerPrice.setValue(trgrPrice);
            structBuySell.discloseQty.setValue(discQtyVal);
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
            String errorMsg = "";
            if (seg != eExch.FNO.value) {
                errorMsg = structBuySell.scripDetails.getT4TMsg();
                if (!errorMsg.equalsIgnoreCase("")) {
                    new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, T4TTAG);
                } else {
                    errorMsg = structBuySell.scripDetails.getIllequideMsg();
                    if (!errorMsg.equalsIgnoreCase("")) {
                        new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, illTAG);
                    }
                }
            } else if (structBuySell.scripDetails.ban.getValue() == 1) {
                errorMsg = structBuySell.scripDetails.getBanScripMsg();
                if (!errorMsg.equalsIgnoreCase("")) {
                    new AlertBox(GlobalClass.latestContext, "Yes", "No", errorMsg, this, banTAG);
                }
            }
            if (errorMsg.equalsIgnoreCase("")) {
                showConfirmAlert();
            }
        }else {
            GlobalClass.showAlertDialog(orderValidationMsg);
        }
    }

    private void showConfirmAlert() {
        String confMsg = "";
        if (structBuySell.modifyOrPlace == eOrderType.MODIFY) {
            confMsg = "MODIFY To : ";
        }
        switch (structBuySell.buyOrSell) {
            case BUY:
                confMsg = confMsg + "Buy";
                break;
            case SELL:
                confMsg = confMsg + "Sell";
                break;
            case MODIFYBUY:
                confMsg = confMsg + "Buy";
                break;
            case MODIFYSELL:
                confMsg = confMsg + "Sell";
                break;
        }

        confMsg = confMsg + " " + structBuySell.qty.getValue() + " " + structBuySell.scripDetails.getFormattedScripName();

        if (mktLimitSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(eOrderType.MARKET.name)) {
            confMsg = confMsg + " @ " + " Market rate";
        } else {
            confMsg = confMsg + " @ " + formatter.format(structBuySell.limitPrice.getValue());
        }
        if (stopLossChk.isChecked()) {
            confMsg = confMsg + " with SL @ " + formatter.format(structBuySell.triggerPrice.getValue());
        }
        if (checkboxIOC.isChecked()) {
            confMsg = confMsg + " with IOC";
        } else if (structBuySell.discloseQty.getValue() > 0) {
            if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
                confMsg = confMsg + " with Disc. Lots " + structBuySell.discloseQty.getValue();
            }
            else {
                confMsg = confMsg + " with Disc. Qty " + structBuySell.discloseQty.getValue();
            }
        }
        confMsg = confMsg + " ? ";
        new AlertBox(GlobalClass.latestContext, "Yes", "No", confMsg, this, placeTAG);
    }






    private void sendOrderReqToServer() {
        if(structBuySell.modifyOrPlace == eOrderType.MODIFY){

        } else{
            if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
                placeOrderFNO();
            } else if(structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
                placeOrderCURR();
            } else {
                placeOrderCash();
            }
        }
        buysellLinear.removeAllViews();
        buysellLinear.addView(buySellBtn);
    }

    private void placeOrderCash() {
        char exch = 'N';
        char exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int ioc = 0;
        int intraDel = 1;
        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            exch = 'B';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
            exchType = 'D';
        }
        if (stopLossChk.isChecked()) {
            stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (checkboxIOC.isChecked()) {
            ioc = 1;
            stopLoss = 'N';
            structBuySell.discloseQty.setValue(0);
        }
        if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }
        if (UserSession.getLoginDetailsModel().isIntradayDelivery()) {
            if (orderType.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                intraDel = eDelvIntra.INTRADAY.value;
            }
        }
        PlaceOrderReq_CASH_Pointer place = new PlaceOrderReq_CASH_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exchange.setValue(exch);
        place.exchangeTYpe.setValue(exchType);
        place.scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        place.scripNameLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        place.scripName.setValue(structBuySell.scripDetails.symbol.getValue());
        place.buySell.setValue(structBuySell.getBuySell());
        place.qty.setValue(structBuySell.qty.getValue());
        place.atMarket.setValue(atMkt);
        place.limitPrice.setValue(structBuySell.limitPrice.getValue());
        place.stopLoss.setValue(stopLoss);
        place.tiggerPrice.setValue(structBuySell.triggerPrice.getValue());
        place.traderRequesterID.setValue(DateUtil.getTimeDiffInSeconds());
        place.iOCSelected.setValue(ioc);
        place.discQty.setValue(structBuySell.discloseQty.getValue());
        place.allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isIntradayDelivery() ? 'Y' : 'N');
        place.orderType.setValue(intraDel);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_CASH(place);
    }

    private void placeOrderFNO() {
        char exch = 'N';
        char exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int ioc = 0;
        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            exch = 'B';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
            exchType = 'D';
        }
        if (stopLossChk.isChecked()) {
            stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (checkboxIOC.isChecked()) {
            ioc = 1;
            stopLoss = 'N';
            structBuySell.discloseQty.setValue(0);
        }

        if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }

        PlaceOrderReq_FNO_Pointer place = new PlaceOrderReq_FNO_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(exch);
        place.exchType.setValue(exchType);
        place.scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        place.symbolLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        place.symbol.setValue(structBuySell.scripDetails.symbol.getValue());
        place.buySell.setValue(structBuySell.getBuySell());
        place.qty.setValue(structBuySell.qty.getValue());
        place.atMarket.setValue(atMkt);
        place.rate.setValue(structBuySell.limitPrice.getValue());
        place.withSL.setValue(stopLoss);
        place.triggerRate.setValue(structBuySell.triggerPrice.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        place.iOC.setValue(ioc);

        place.instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        place.expiryDate.setValue(structBuySell.scripDetails.expiry.getValue());
        place.strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        place.cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        place.cALevel.setValue(calavel);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_FNOCURR(place,eMessageCode.PLACE_ORDER_FNO);
    }

    private void placeOrderCURR() {
        char exch = 'N';
        char exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int ioc = 0;
        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            exch = 'B';
        }
        else if (structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value){
            exch = 'C';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value ||
                structBuySell.scripDetails.segment.getValue() == eExch.NSECURR.value) {
            exchType = 'D';
        }
        if (stopLossChk.isChecked()) {
            stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (checkboxIOC.isChecked()) {
            ioc = 1;
            stopLoss = 'N';
            structBuySell.discloseQty.setValue(0);
        }

        if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }

        PlaceOrderReq_FNO_Pointer place = new PlaceOrderReq_FNO_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(exch);
        place.exchType.setValue(exchType);
        place.scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        place.symbolLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        place.symbol.setValue(structBuySell.scripDetails.symbol.getValue());
        place.buySell.setValue(structBuySell.getBuySell());
        place.qty.setValue(structBuySell.qty.getValue());
        place.atMarket.setValue(atMkt);
        place.rate.setValue(structBuySell.limitPrice.getValue());
        place.withSL.setValue(stopLoss);
        place.triggerRate.setValue(structBuySell.triggerPrice.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        place.iOC.setValue(ioc);

        place.instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        place.expiryDate.setValue(structBuySell.scripDetails.expiry.getValue());
        place.strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        place.cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        place.cALevel.setValue(calavel);
        place.discQty.setValue(structBuySell.discloseQty.getValue());

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendPlaceOrderReq_FNOCURR(place,eMessageCode.PLACE_ORDER_CURR);
    }

    private void modifyOrderCash() {
        char exch = 'N';
        char exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int ioc = 0;
        int intraDel = 1;

        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            exch = 'B';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
            exchType = 'D';
        }
        if (stopLossChk.isChecked()) {
            stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (checkboxIOC.isChecked()) {
            ioc = 1;
            stopLoss = 'N';
            structBuySell.discloseQty.setValue(0);
        }
        if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }
        if (UserSession.getLoginDetailsModel().isIntradayDelivery()) {
            if (orderType.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                intraDel = eDelvIntra.INTRADAY.value;
            }
        }
        ModifyOrderReq_CASH_Pointer place = new ModifyOrderReq_CASH_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(exch);
        place.exchType.setValue(exchType);
        place.scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        place.scripNameLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        place.scripName.setValue(structBuySell.scripDetails.symbol.getValue());
        place.buySell.setValue(structBuySell.getBuySell());
        place.qty.setValue(structBuySell.qty.getValue());
        place.atMarket.setValue(atMkt);
        place.rate.setValue(structBuySell.limitPrice.getValue());
        place.withSL.setValue(stopLoss);
        place.triggerRate.setValue(structBuySell.triggerPrice.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        place.ioc.setValue(ioc);
        place.disclosedQty.setValue(structBuySell.discloseQty.getValue());
        place.allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isIntradayDelivery() ? 'Y' : 'N');
        place.orderType.setValue(intraDel);

        char slTigged = 'N';
        if(structBuySell.order.sLTriggered.getValue() != ' '){
            slTigged = structBuySell.order.sLTriggered.getValue();
        }
        place.sLTriggered.setValue(slTigged);
        place.oldQty.setValue(structBuySell.order.qty.getValue());
        place.tradedQty.setValue(structBuySell.order.getFinalTradeQty());
        place.pendingQty.setValue(structBuySell.qty.getValue() - structBuySell.order.getFinalTradeQty());
        place.exchOrderID.setValue(structBuySell.order.exchOrderID.getValue());
        place.exchOrderTime.setValue(Math.max(structBuySell.order.finalexchtime, structBuySell.order.exchOrderTime.getValue()));
        char ahStatus = 'N';
        if(structBuySell.order.aHStatus.getValue() != ' '){
            ahStatus = structBuySell.order.aHStatus.getValue();
        }
        place.aHStatus.setValue(ahStatus);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendModifyOrderReq_CASH(place);
    }

    private void modifyOrderFNO() {
        char exch = 'N';
        char exchType = 'C';
        char atMkt = 'N';
        char stopLoss = 'N';
        int ioc = 0;

        if (structBuySell.scripDetails.segment.getValue() == eExch.BSE.value) {
            exch = 'B';
        }
        if (structBuySell.scripDetails.segment.getValue() == eExch.FNO.value) {
            exchType = 'D';
        }
        if (stopLossChk.isChecked()) {
            stopLoss = 'Y';
        } else {
            structBuySell.triggerPrice.setValue(0);
        }
        if (checkboxIOC.isChecked()) {
            ioc = 1;
            stopLoss = 'N';
        }
        if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.MARKET.name)) {
            atMkt = 'Y';
            structBuySell.limitPrice.setValue(0);
            structBuySell.triggerPrice.setValue(0);
        }
        ModifyOrderReq_FNO_Pointer place = new ModifyOrderReq_FNO_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(exch);
        place.exchType.setValue(exchType);
        place.scripCode.setValue(structBuySell.scripDetails.scripCode.getValue());
        place.scripNameLength.setValue(structBuySell.scripDetails.symbol.getValue().length());
        place.scripName.setValue(structBuySell.scripDetails.symbol.getValue());
        place.buySell.setValue(structBuySell.getBuySell());
        place.qty.setValue(structBuySell.qty.getValue());
        place.atMarket.setValue(atMkt);
        place.rate.setValue(structBuySell.limitPrice.getValue());
        place.withSL.setValue(stopLoss);
        place.triggerRate.setValue(structBuySell.triggerPrice.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        place.ioc.setValue(ioc);

        char slTigged = 'N';
        if(structBuySell.order.sLTriggered.getValue() != ' '){
            slTigged = structBuySell.order.sLTriggered.getValue();
        }
        place.sLTriggered.setValue(slTigged);
        place.oldQty.setValue(structBuySell.order.qty.getValue());
        place.tradedQty.setValue(structBuySell.order.getFinalTradeQty());
        place.pendingQty.setValue(structBuySell.qty.getValue() - structBuySell.order.getFinalTradeQty());
        place.exchOrderID.setValue(structBuySell.order.exchOrderID.getValue());
        place.exchOrderTime.setValue(Math.max(structBuySell.order.finalexchtime, structBuySell.order.exchOrderTime.getValue()));
        char ahStatus = 'N';
        if(structBuySell.order.aHStatus.getValue() != ' '){
            ahStatus = structBuySell.order.aHStatus.getValue();
        }
        place.aHStatus.setValue(ahStatus);

        place.instrType.setValue(structBuySell.scripDetails.getInstrumentType());
        place.expiry.setValue(structBuySell.scripDetails.expiry.getValue());
        place.strikePrice.setValue(structBuySell.scripDetails.getStrikeRateForOrderPlacing());
        place.cPType.setValue(structBuySell.scripDetails.getCPTypeForOrderPlace());
        byte calavel = 0;
        place.cALevel.setValue(calavel);

        SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
        sendDataToRCServer.sendModifyOrderReq_FNOCURR(place,eMessageCode.EXCHPLACEORDER_DERIV_MODIFY_ORDER_FNO);
    }

}
