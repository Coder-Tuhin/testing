package fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.VenturaApplication;
import com.xtrem.chartxtrem.piechart.PieView;
import com.xtrem.chartxtrem.view.ColumnChartView;

import java.util.ArrayList;
import java.util.HashMap;

import Structure.Response.RC.StructMarginReportReplyRecord_Pointer;
import chart.ColumnChart;
import chart.PieChart;
import com.ventura.venturawealth.R;
import connection.ReqSent;
import enums.eExch;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import fragments.homeGroups.ReportFragment;
import handler.StructCBDBChartResp;
import handler.StructNetPositionSummary;
import handler.StructOrderSummary;
import handler.StructTradeSummary;
import utils.AnimationClass;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;


public class DashboardFragment extends Fragment implements ReqSent, View.OnClickListener {
    private final String className = getClass().getName();

    public static Handler reportHandler;

    private OnFragmentInteractionListener mListener;
    private LinearLayout marginsLayout,ordersLayout,tradesLayout,netPositionLayout;
    private TextView avlMargin,utlMargin,mtmMargin;
    private LayoutInflater inflater;
   // private SwipeRefreshLayout swipeRefresh;
    private ColumnChartView columnChart;
    private PieView pieChart;
    private LinearLayout pieChartLayout,columnChartLayout,mgnLayout,orderLayout,tradeLayout,npLayout;
    private TextView lastTradedScripTV,lastTradedTimeTV,lastOrderedScripTV,lastOrderTimeTV;
    private TextView marginIndicator,mgnUtilizedPer,mgnUtilized,totPenOrders,totOpenTrds, totTradeValue,
                    utilized,open,value,pending;
   // private OverrideFont dottedFont;
    private boolean showColumnChart = false;
    private boolean showPieChart = false;
    private RadioButton rbMTM,rbTrades;
    private RelativeLayout indicatorLayout;

    public DashboardFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.layout_reports, container, false);
        reportHandler = new ReportHandler();
        this.inflater = inflater;
        try {
            columnChart = (ColumnChartView)view.findViewById(R.id.columnChart);
            pieChart = (PieView) view.findViewById(R.id.pieChart);
            pieChartLayout = (LinearLayout)view.findViewById(R.id.pieChartLayout);
            columnChartLayout = (LinearLayout)view.findViewById(R.id.columnChartLayout);
            marginsLayout = (LinearLayout) view.findViewById(R.id.marginLayout);
            ordersLayout = (LinearLayout) view.findViewById(R.id.ordersLayout);
            tradesLayout = (LinearLayout) view.findViewById(R.id.tradesLayout);
            netPositionLayout = (LinearLayout) view.findViewById(R.id.netPositionLayout);
            mgnLayout = (LinearLayout) view.findViewById(R.id.mgnLayout);
            orderLayout = (LinearLayout) view.findViewById(R.id.orderLayout);
            tradeLayout = (LinearLayout) view.findViewById(R.id.tradeLayout);
            npLayout = (LinearLayout) view.findViewById(R.id.npLayout);
            view.findViewById(R.id.mgnTitle).setOnClickListener(this);
            view.findViewById(R.id.orderTitle).setOnClickListener(this);
            view.findViewById(R.id.tradeTitle).setOnClickListener(this);
            view.findViewById(R.id.npTitle).setOnClickListener(this);

            indicatorLayout = (RelativeLayout) view.findViewById(R.id.indicatorLayout);
            marginIndicator = (TextView) view.findViewById(R.id.marginIndicator);
            mgnUtilizedPer = (TextView) view.findViewById(R.id.mgnUtilizedPer);
            mgnUtilized = (TextView) view.findViewById(R.id.mgnNum);
            totPenOrders = (TextView) view.findViewById(R.id.totPenOrders);
            totOpenTrds = (TextView) view.findViewById(R.id.totOpenTrds);
            totTradeValue = (TextView) view.findViewById(R.id.totTrades);

            utilized = (TextView) view.findViewById(R.id.utilized);
            value = (TextView) view.findViewById(R.id.value);
            pending = (TextView) view.findViewById(R.id.pending);
            open = (TextView) view.findViewById(R.id.open);

            avlMargin = (TextView) view.findViewById(R.id.avlMargin);
            utlMargin = (TextView) view.findViewById(R.id.utlMargin);
            mtmMargin = (TextView) view.findViewById(R.id.mtmMargin);

            lastTradedScripTV = (TextView)view.findViewById(R.id.lastTradedScrip);
            lastTradedTimeTV = (TextView)view.findViewById(R.id.lastTradedTime);
            lastOrderedScripTV = (TextView)view.findViewById(R.id.lastOrderedScrip);
            lastOrderTimeTV = (TextView)view.findViewById(R.id.lastOrderTime);

          /*  swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(GlobalClass.refreshDashBoard.isRefresh()){
                        senReq(true);
                        int dbRefreshTime = GlobalClass.refreshDashBoard.getRefreshTime();
                        GlobalClass.refreshMargin.setRefreshTime(dbRefreshTime);
                        GlobalClass.refreshTradeBook.setRefreshTime(dbRefreshTime);
                        GlobalClass.refreshOrderBook.setRefreshTime(dbRefreshTime);
                    }else{
                        dismissSwipeRefresh();
                    }
                }
            });*/
            /*CheckBox cfdChkBox = (CheckBox) view.findViewById(R.id.cfdChkBox);
            cfdChkBox.setChecked(ObjectHolder.objNetPosn.isWithCfd());

            cfdChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ObjectHolder.objNetPosn.withCfd(b);
                    refreshNetPosition();
                }
            });*/
            marginsLayout.setOnClickListener(this);
            ordersLayout.setOnClickListener(this);
            tradesLayout.setOnClickListener(this);
            netPositionLayout.setOnClickListener(this);

            rbMTM = (RadioButton) view.findViewById(R.id.mtm);
            rbTrades = (RadioButton) view.findViewById(R.id.trades);
            RadioGroup rgValues = (RadioGroup) view.findViewById(R.id.rdGroup);
            rgValues.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i) {
                        case R.id.mtm:
                            if(showColumnChart) {
                                enableDisableChart(View.VISIBLE, View.GONE);
                            }else{
                                enableDisableChart(View.GONE, View.GONE);
                            }
                            break;
                        case R.id.trades:
                            if(showPieChart) {
                                enableDisableChart(View.GONE, View.VISIBLE);
                            }else{
                                enableDisableChart(View.GONE, View.GONE);
                            }
                            break;

                    }
                }
            });

            int mtmTradeSelection = VenturaApplication.getPreference().getSharedPrefFromTag(Constants.showMTMTradeChart,View.GONE);
            //enableDisableChart(Integer.parseInt(mtmTradeSelection[0]),Integer.parseInt(mtmTradeSelection[1]));
            if(mtmTradeSelection == View.VISIBLE){
                ((RadioButton)view.findViewById(R.id.mtm)).setChecked(true);
            }else{

                ((RadioButton)view.findViewById(R.id.trades)).setChecked(true);
            }

//            columnChart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
//                @Override
//                public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
//                    ObjectHolder.objOrderBook.setSelectedSpinnerOption(NetPosition.ALL.value);
//                    GlobalClass.displayView(PositionConstant.NET_POSITION);
//                }
//
//                @Override
//                public void onValueDeselected() {
//                }
//            });
//            pieChart.setOnPieClickListener(new PieView.OnPieClickListener() {
//                @Override
//                public void onPieClick(int index) {
//                    //GlobalClass.displayView(PositionConstant.TRADE_BOOK);
//                }
//            });
            /*pieChart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int arcIndex, SliceValue value) {
                    GlobalClass.displayView(PositionConstant.TRADE_BOOK);
                }

                @Override
                public void onValueDeselected() {

                }
            });*/

            setVisibility(Constants.isMgnLayoutVisible, mgnLayout, mgnUtilized,utilized);
            setVisibility(Constants.isOrderLayoutVisible,orderLayout,totPenOrders,pending);
            setVisibility(Constants.isTradeLayoutVisible,tradeLayout,totTradeValue,value);
            setVisibility(Constants.isNPLayoutVisible,npLayout,totOpenTrds,open);

           // dottedFont = new OverrideFont(GlobalClass.mainContext, "Bazaronite.ttf");

            refreshMargin();

            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        }catch (Exception e){
            GlobalClass.onError("Error in " + className, e);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            senReq(true);
            refreshMargin();
            refreshNetPosition();
            refreshTrades();
            refreshOrders();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        reportHandler = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void reqSent(int msgCode) {

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.marginLayout:
                    ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(0);
                    GlobalClass.fragmentTransaction(ReportFragment.newInstance(),R.id.container_body,true,"");
                    break;
                case R.id.ordersLayout:
                    ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(1);
                    GlobalClass.fragmentTransaction(ReportFragment.newInstance(),R.id.container_body,true,"");
                    break;
                case R.id.tradesLayout:
                    ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(2);
                    GlobalClass.fragmentTransaction(ReportFragment.newInstance(),R.id.container_body,true,"");
                    break;
                case R.id.netPositionLayout:
                    ((Spinner)((Activity)getActivity()).findViewById(R.id.report_spinner)).setSelection(3);
                    GlobalClass.fragmentTransaction(ReportFragment.newInstance(),R.id.container_body,true,"");
                    break;
                case R.id.mgnTitle:
                    setVisibility(mgnLayout,Constants.isMgnLayoutVisible,mgnUtilized,utilized);
                    break;
                case R.id.orderTitle:
                    setVisibility(orderLayout,Constants.isOrderLayoutVisible,totPenOrders,pending);
                    break;
                case R.id.tradeTitle:
                    setVisibility(tradeLayout,Constants.isTradeLayoutVisible,totTradeValue,value);
                    break;
                case R.id.npTitle:
                    setVisibility(npLayout,Constants.isNPLayoutVisible,totOpenTrds,open);
                    break;
            }

        }catch (Exception e){
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void setVisibility(View view,String tagConstraint,TextView titleValTV,TextView upperText){
        int visibility = view.getVisibility();
        if(titleValTV.getText().toString().equalsIgnoreCase("")){
            titleValTV.setVisibility(View.GONE);
            upperText.setVisibility(View.GONE);
        }else{
            titleValTV.setVisibility(visibility);
            upperText.setVisibility(visibility);
        }

        visibility = view.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE;
        VenturaApplication.getPreference().storeSharedPref(tagConstraint,view.getVisibility());
        view.setVisibility(visibility);

    }

    private void setVisibility(String tagConstraint,View view,TextView titleValTV,TextView upperText){
        int visibility = VenturaApplication.getPreference().getSharedPrefFromTag(tagConstraint, View.VISIBLE);
        if(titleValTV.getText().toString().equalsIgnoreCase("")){
            titleValTV.setVisibility(View.GONE);
            upperText.setVisibility(View.GONE);
        }else{
            if (visibility> 0){
                titleValTV.setVisibility(View.VISIBLE);
                upperText.setVisibility(View.VISIBLE);
            }else {
                titleValTV.setVisibility(View.GONE);
                upperText.setVisibility(View.GONE);
            }
        }
        visibility = visibility == View.VISIBLE? View.GONE:View.VISIBLE;
        view.setVisibility(visibility);

    }

    private void senReq(boolean isPDialogDisplay) {
        try {
            GlobalClass.getClsOrderBook().sendOrderBookRequest();
            GlobalClass.getClsTradeBook().sendTradeBookRequest();
            GlobalClass.getClsMarginHolding().sendMarginRequest(false,"4");
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void dismissSwipeRefresh(){
      //  swipeRefresh.setRefreshing(false);
    }

    private void refreshMargin() {
        final StructMarginReportReplyRecord_Pointer marginDetail = GlobalClass.getClsMarginHolding().getMarginDetail();
        if (marginDetail != null) {
            double marginUtilized = marginDetail.marginForPendOrder.getValue();
            double marginAvailable = marginDetail.availableMargin.getValue();
            avlMargin.setText(formatText(marginAvailable));
            utlMargin.setText(formatText(marginUtilized));
            mtmMargin.setText(formatText(marginDetail.holdingsMargin.getValue()));
            setTitleTotalVisibility(mgnLayout,mgnUtilized,utilized);
            setMarginIndicator(marginAvailable,marginUtilized);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setMarginIndicator(double marginAvlForTrading, double mrgnUtilised) {
        try {
            int totWidth = indicatorLayout.getWidth();
            if(marginAvlForTrading > 0) {
                double mgnUtilPer = mrgnUtilised / marginAvlForTrading * 100;
                //mgnUtilPer = 2;
                if (mgnUtilPer < 2) {
                    mgnUtilPer = 2;
                }
                int mgnUtilWidth = (int) (((mgnUtilPer / 100)) * totWidth);
                mgnUtilPer = mrgnUtilised / marginAvlForTrading * 100;
                marginIndicator.setMinimumWidth(mgnUtilWidth);
                mgnUtilizedPer.setText(Formatter.toTwoDecimalValue(mgnUtilPer) + "%");
                if (mgnUtilPer > 65) {
                    marginIndicator.setBackground(getResources().getDrawable(R.drawable.red_indicator_bg));
                } else {
                    marginIndicator.setBackground(getResources().getDrawable(R.drawable.blue_indicator_bg));
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("",e);
        }

    }

    private void setTitleTotalVisibility(LinearLayout detailView, TextView totalTV,TextView uppertext) {
        if(detailView.getVisibility() == View.GONE){
            totalTV.setVisibility(View.VISIBLE);
            uppertext.setVisibility(View.VISIBLE);
        }else{
            totalTV.setVisibility(View.GONE);
            uppertext.setVisibility(View.GONE);
        }
    }

    private void refreshOrders(){
            ordersLayout.removeAllViews();
            HashMap<Integer, StructOrderSummary> osMap = GlobalClass.getClsOrderBook().getOrderSummary();

            lastOrderedScripTV.setText("");
            lastOrderTimeTV.setText("");

            //CharSequence lastOrderedScrip = ObjectHolder.objOrderBook.getLastPendingOrderScrip();
            if(!GlobalClass.getClsOrderBook().lastPendingOrderScrip.toString().equalsIgnoreCase("")) {
                lastOrderedScripTV.setText(GlobalClass.getClsOrderBook().lastPendingOrderScrip);
                lastOrderTimeTV.setText(GlobalClass.getClsOrderBook().lastPendingOrderTime);
            }

            int totPenOrdersint = 0;
            for(eExch exch:eExch.values()){
                if(exch == eExch.NSE || exch == eExch.BSE || exch == eExch.FNO) {
                    StructOrderSummary orderSummary = osMap.get((int) exch.value);
                    if (orderSummary == null) {
                        orderSummary = new StructOrderSummary();
                    }
                    View view = inflater.inflate(R.layout.order_net_sam_row, null, false);
                    ((TextView) view.findViewById(R.id.segment)).setText(exch.name);
                    ((TextView) view.findViewById(R.id.val1)).setText(orderSummary.total.getValue() + "");
                    ((TextView) view.findViewById(R.id.val2)).setText(orderSummary.pending.getValue() + "");
                    ((TextView) view.findViewById(R.id.val3)).setText(orderSummary.traded.getValue() + "");
                    ordersLayout.addView(view);

                    totPenOrdersint = totPenOrdersint + orderSummary.pending.getValue();

                 /*   summaryClickListener(view.findViewById(R.id.val1), eOrderbookSpinnerItems.ALL.value,
                            Constants.ORDER_BOOK);
                    summaryClickListener(view.findViewById(R.id.val2), eOrderbookSpinnerItems.PENDING.value,
                            Constants.ORDER_BOOK);
                    summaryClickListener(view.findViewById(R.id.val3), eOrderbookSpinnerItems.FULL_EXECUTED.value,
                            Constants.ORDER_BOOK);*/
                }
            }
            totPenOrders.setText(totPenOrdersint+"");
            setTitleTotalVisibility(orderLayout,totPenOrders,pending);
            //dottedFont.overrideFonts(totPenOrders);

    }

    private void refreshTrades(){
            tradesLayout.removeAllViews();
            ArrayList<Integer> dataList = new ArrayList<>();
            HashMap<Integer, StructTradeSummary> tsMap = GlobalClass.getClsTradeBook().getTradeSummary();

            lastTradedScripTV.setText(GlobalClass.getClsTradeBook().lasTradedScrip);
            lastTradedTimeTV.setText(GlobalClass.getClsTradeBook().lastTradeTime);

            float totalValue = 0;
            int totTrades = 0;
            for(eExch exch:eExch.values()){
                if(exch == eExch.NSE || exch == eExch.BSE || exch == eExch.FNO) {
                    StructTradeSummary tradeSummary = tsMap.get((int) exch.value);
                    if (tradeSummary == null) {
                        tradeSummary = new StructTradeSummary();
                    }

                    int totalTrades = tradeSummary.total.getValue();
                    //int totalTrades = 50;
                    float total = tradeSummary.value.getValue();
                    View view = inflater.inflate(R.layout.trades_sammuary_row, null, false);
                    ((TextView) view.findViewById(R.id.segment)).setText(exch.name);
                    ((TextView) view.findViewById(R.id.val1)).setText( totalTrades+ "");
                    ((TextView) view.findViewById(R.id.val2)).setText(Formatter.toTwoDecimalValue(total));
                    tradesLayout.addView(view);

                    totalValue = totalValue + total;

                    totTrades = totTrades + totalTrades;

                    if(total > 0) {
                        //dataList.add(tradeSummary);
                        dataList.add(totalTrades);
                    }
                }
                totTradeValue.setText(totalValue + "");
                setTitleTotalVisibility(tradeLayout,totTradeValue,value);
                //dottedFont.overrideFonts(totTradeValue);
            }
            if(dataList.size() > 0) {
                showPieChart = true;
                new PieChart().setChart(pieChart, dataList, totTrades);
                pieChart.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //GlobalClass.displayView(PositionConstant.TRADE_BOOK);
                        return false;
                    }
                });
                        //new Chart().setChartData(pieChart, dataList);
                if(rbTrades.isChecked())
                    pieChartLayout.setVisibility(View.VISIBLE);
            }else{
                showPieChart = false;
                pieChartLayout.setVisibility(View.GONE);
            }

    }



    private void refreshNetPosition(){
        try {
                netPositionLayout.removeAllViews();
                HashMap<Integer, StructNetPositionSummary> npsMap = GlobalClass.getClsNetPosn().getNetSummary();
                ArrayList<StructCBDBChartResp> chartDataList = new ArrayList<>();
                int totOpenTrdsInt = 0;
                for (eExch exch : eExch.values()) {
                    if (exch == eExch.NSE || exch == eExch.BSE || exch == eExch.FNO) {
                        StructNetPositionSummary npSummary = npsMap.get((int) exch.value);
                        if (npSummary == null) {
                            npSummary = new StructNetPositionSummary();
                        }

                        float mtm = npSummary.mtm.getValue();

                        if((mtm+"").equalsIgnoreCase("NaN")){
                           mtm = 0.0f;
                        }

                        View view = inflater.inflate(R.layout.order_net_sam_row, null, false);
                        ((TextView) view.findViewById(R.id.segment)).setText(exch.name);
                        ((TextView) view.findViewById(R.id.val1)).setText(npSummary.open.getValue() + "");
                        ((TextView) view.findViewById(R.id.val2)).setText(npSummary.closed.getValue() + "");
                        ((TextView) view.findViewById(R.id.val3)).setText(Formatter.toTwoDecimalValue(mtm));
                        netPositionLayout.addView(view);
                        totOpenTrdsInt = totOpenTrdsInt + npSummary.open.getValue();
                       /* summaryClickListener(view.findViewById(R.id.val1), NetPosition.OPEN.value,
                                Constants.NET_POSITION);
                        summaryClickListener(view.findViewById(R.id.val2), NetPosition.SQUARED.value,
                                Constants.NET_POSITION);*/
                        if(mtm !=0) {
                            StructCBDBChartResp chartData = new StructCBDBChartResp();
                            chartData.mtm.setValue(npSummary.mtm.getValue());
                            chartData.setExch(exch);
                            chartDataList.add(chartData);
                        }
                    }
                }
                totOpenTrds.setText(totOpenTrdsInt+"");
                setTitleTotalVisibility(npLayout,totOpenTrds,open);
                //dottedFont.overrideFonts(totOpenTrds);
                //setLastTradedScrip_NP(dateScripNameHM);
                if(chartDataList.size() > 0) {
                    showColumnChart = true;
                    new ColumnChart().setChartData(columnChart, chartDataList);
                    if(rbMTM.isChecked())
                        columnChartLayout.setVisibility(View.VISIBLE);
                }else{
                    showColumnChart = false;
                    columnChartLayout.setVisibility(View.GONE);
                }

        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }

    private void enableDisableChart(int mtmVisibility,int tradesVisibility){
        VenturaApplication.getPreference().storeSharedPref(Constants.showMTMTradeChart,mtmVisibility);
        columnChartLayout.setVisibility(mtmVisibility);
        pieChartLayout.setVisibility(tradesVisibility);
        if(mtmVisibility == View.VISIBLE){
            AnimationClass.showChart(columnChart);
        }else if(tradesVisibility == View.VISIBLE){
            AnimationClass.showChart(pieChart);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

   /* private void summaryClickListener(View selectedOption,final int val, final int nxtPage){
        selectedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (nxtPage) {
                    case PositionConstant.ORDER_BOOK:
                        ObjectHolder.objOrderBook.setSelectedSpinnerOption(val);
                        break;
                    case PositionConstant.NET_POSITION:
                        ObjectHolder.objNetPosn.setSelectedSpinnerOption(val);
                        break;
                }
                GlobalClass.displayView(nxtPage);
            }
        });
    }*/

    private String formatText(double val){
        return Formatter.roundFormatter.format(Math.round(val));
    }

    class ReportHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                dismissSwipeRefresh();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                   // byte[] data = refreshBundle.getByteArray("orgData");
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);

                    switch (emessagecode) {
                        case TRADE_BOOK_RESP:
                            refreshTrades();
                            refreshNetPosition();
                            break;
                        case ORDER_BOOk_RESP:
                            refreshOrders();
                            break;
                        case MARGIN:
                            refreshMargin();
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                GlobalClass.onError("Error in "+className,e);
            }
        }
    }
}
