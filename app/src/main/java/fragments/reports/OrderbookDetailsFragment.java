package fragments.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Other.StructBuySell;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eExch;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import interfaces.OnAlertListener;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import view.AlertBox;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
@SuppressLint("ValidFragment")
public class OrderbookDetailsFragment extends Fragment implements View.OnClickListener, OnAlertListener {

    private TextView screptName,buySell ,qtyRate,tradedAvgValue,tradedQty,pendingQty,stopLoss,stopLossTriggered,
            triggerRate,Time,status,brokerId,exchangeId,orderTypeTxtView,pageNumberTxt, tradedQtyTitle,pendingQtyTitle,
            currentRateValue;
    private ImageButton backbtn,prevbtn,nextbtn;
    private Button modifybtn,cancelbtn;


    private TreeMap<Integer, StructOrderReportReplyRecord_Pointer> orderBKData;
    private ArrayList<Integer> allKeyDecending;
    private int selectedPosition = 0;
    private StructOrderReportReplyRecord_Pointer selectedOrderBk;
    private boolean isCancel = false;
    @BindView(R.id.order_relativeseven) RelativeLayout order_relativeseven;

    public OrderbookDetailsFragment(){super();}

    public OrderbookDetailsFragment(TreeMap<Integer, StructOrderReportReplyRecord_Pointer> orderBKData, ArrayList<Integer> allBrkoerOrdIds, int position){
        this.orderBKData = orderBKData;
        this.allKeyDecending = allBrkoerOrdIds;
        this.selectedPosition = position;
        selectedOrderBk = orderBKData.get(allKeyDecending.get(selectedPosition));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.orderbook_details, container, false);
        ButterKnife.bind(this,layout);
        initialization(layout);
        selectedOrderBk = orderBKData.get(allKeyDecending.get(selectedPosition));
        setvalue();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.orderBkDetailUIHandler = new OrderBookDetailHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.orderBkDetailUIHandler = null;
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization(View layout) {

        screptName = (TextView)layout.findViewById(R.id.screptname_textview);
        buySell = (TextView)layout.findViewById(R.id.buysell_OrdD_textview);
        qtyRate = (TextView)layout.findViewById(R.id.buy_value_ordD_textview);
        currentRateValue = (TextView) layout.findViewById(R.id.current_ratevalue_textview);
        tradedAvgValue = (TextView)layout.findViewById(R.id.tradedavg_ratevalue_textview);
        tradedQty = (TextView)layout.findViewById(R.id.traded_qty_textview);
        tradedQtyTitle = (TextView)layout.findViewById(R.id.traded_qty_textTitle);
        pendingQty = (TextView)layout.findViewById(R.id.pending_qty_textview);
        pendingQtyTitle = (TextView)layout.findViewById(R.id.pending_qty_textTitle);
        stopLoss = (TextView)layout.findViewById(R.id.stop_loss_textview);
        stopLossTriggered = (TextView)layout.findViewById(R.id.stoploss_triggered_textview);
        triggerRate = (TextView)layout.findViewById(R.id.trigger_rate_textview);
        Time = (TextView)layout.findViewById(R.id.Time_textview);
        status = (TextView)layout.findViewById(R.id.status_textview);
        brokerId = (TextView)layout.findViewById(R.id.broker_id_textview);
        exchangeId = (TextView)layout.findViewById(R.id.exchange_id_textview);

        orderTypeTxtView = (TextView) layout.findViewById(R.id.order_type_textview);
        pageNumberTxt = (TextView) layout.findViewById(R.id.pagecount_textview);
        backbtn = (ImageButton) layout.findViewById(R.id.previnit_imagebutton);
        backbtn.setOnClickListener(this);
        prevbtn = (ImageButton) layout.findViewById(R.id.prevbtn_textview);
        prevbtn.setOnClickListener(this);
        nextbtn = (ImageButton) layout.findViewById(R.id.nextbtn_textview);
        nextbtn.setOnClickListener(this);
        modifybtn = (Button) layout.findViewById(R.id.modify_button);
        cancelbtn = (Button) layout.findViewById(R.id.cancel_button);
        modifybtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
    }

    private void setvalue()  {
        try {
            if (selectedOrderBk.isIntraDelAllow()) {
                order_relativeseven.setVisibility(View.VISIBLE);
            } else {
                order_relativeseven.setVisibility(View.GONE);
            }
            isCancel = false;
            NumberFormat formatter = Formatter.getFormatter(selectedOrderBk.getExchange());
            if (selectedOrderBk.getExchange() == eExch.NSECURR.value) {
                tradedQtyTitle.setText("Traded Lots:");
                pendingQtyTitle.setText("Pending Lots:");
            } else {
                tradedQtyTitle.setText("Traded Qty:");
                pendingQtyTitle.setText("Pending Qty:");
            }
            String strScripName = selectedOrderBk.getFormatedScripName(false);
            strScripName = strScripName.replace("\n", " ");
            screptName.setText(strScripName);
            buySell.setText(selectedOrderBk.getBuySell());
            qtyRate.setText(selectedOrderBk.getOrderQtyRate());
            StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(selectedOrderBk.scripCode.getValue(), false);
            if (mktWatch != null) {
                mktWatch.setLtpWithTxtColor(currentRateValue, 0.00);
            }
            tradedAvgValue.setText("" + formatter.format(selectedOrderBk.finaTradePrice));
            tradedQty.setText("" + selectedOrderBk.getFinalTradeQty());
            pendingQty.setText("" + selectedOrderBk.finaPendingQty);
            stopLoss.setText("" + selectedOrderBk.getStopLoss());
            stopLossTriggered.setText("" + selectedOrderBk.getSLTiggered());
            triggerRate.setText("" + formatter.format(selectedOrderBk.getTiggeredRate()));
            String strTime = "";
            if (selectedOrderBk.exchOrderTime.getValue() > 0) {
                strTime = DateUtil.dateFormatter(selectedOrderBk.exchOrderTime.getValue(), Constants.DDMMMYYHHMMSS);
            } else {
                strTime = DateUtil.dateFormatter(selectedOrderBk.brokerOrderTime.getValue(), Constants.DDMMMYYHHMMSS);
            }
            Time.setText("" + strTime);
            status.setText("" + selectedOrderBk.getFinalStatus());
            brokerId.setText("" + selectedOrderBk.brokerOrderID.getValue());
            exchangeId.setText("" + selectedOrderBk.exchOrderID.getValue());

            if (UserSession.getLoginDetailsModel().isIntradayDelivery()) {
                orderTypeTxtView.setText("" + selectedOrderBk.getProductTypeStr());
            }

            modifybtn.setVisibility(View.GONE);
            cancelbtn.setVisibility(View.GONE);

            if (selectedOrderBk.finaPendingQty > 0) {
                modifybtn.setVisibility(View.VISIBLE);
                cancelbtn.setVisibility(View.VISIBLE);
            }
            String pageNo = (selectedPosition + 1) + "/" + orderBKData.size();
            pageNumberTxt.setText(pageNo);
        }catch (Exception ex){
            GlobalClass.onError("",ex);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previnit_imagebutton:
                backBtnClick();
                break;
            case R.id.prevbtn_textview:
                prevBtnClick();
                break;
            case R.id.nextbtn_textview:
                nextBtnClick();
                break;
            case R.id.modify_button:
                if (selectedOrderBk.isOrderSentToExchModify()){
                    Toast.makeText(GlobalClass.latestContext, selectedOrderBk.getERR_ORDER_MODIFY_SENTTOEXCH(),
                            Toast.LENGTH_SHORT).show();
                }else {
                    modifyBtnClick();
                }
                break;
            case R.id.cancel_button:
                if (selectedOrderBk.isOrderSentToExchCancel()){
                    Toast.makeText(GlobalClass.latestContext, selectedOrderBk.getERR_ORDER_CANCEL_SENTTOEXCH(), Toast.LENGTH_SHORT).show();
                }else {
                    cancelBtnClick();
                }
                break;

            default:
                break;
        }
    }

    private void backBtnClick(){
        GlobalClass.fragmentManager.popBackStackImmediate();
    }
    private void prevBtnClick(){
        if(selectedPosition > 0){
            selectedPosition = selectedPosition - 1;
            selectedOrderBk = orderBKData.get(allKeyDecending.get(selectedPosition));
            setvalue();
        }
    }
    private void nextBtnClick(){
        if(selectedPosition < (orderBKData.size()-1)){
            selectedPosition = selectedPosition + 1;
            selectedOrderBk = orderBKData.get(allKeyDecending.get(selectedPosition));
            setvalue();
        }
    }
    private void modifyBtnClick(){
        int scriptCode = selectedOrderBk.scripCode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(selectedOrderBk.getFormatedScripName(false));
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = new StructBuySell();
        if(selectedOrderBk.buySell.getValue() == 'B'){
            buySell.buyOrSell = eOrderType.BUY;
        }
        else{
            buySell.buyOrSell = eOrderType.SELL;
        }
        buySell.modifyOrPlace = eOrderType.MODIFY;
        buySell.order = selectedOrderBk;
        buySell.showDepth = eShowDepth.ORDERBOOK;

        Fragment m_fragment;
//        if(UserSession.getClientResponse().getServerType() == eServerType.RC){
            m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.ORDERBOOK,grplist,buySell,
                    homeActivity.SELECTED_RADIO_BTN,true);
//        }else{
//            m_fragment = new MktdepthFragment(scriptCode, eShowDepth.ORDERBOOK,grplist,buySell,
//                    homeActivity.SELECTED_RADIO_BTN,true);
//        }

        GlobalClass.fragmentTransaction(m_fragment,R.id.container_body,true, eFragments.DEPTH.name);
    }
    private void cancelBtnClick(){
        new AlertBox(GlobalClass.latestContext,"Yes","No","Are you sure you want to cancel this order?",this,"cancel");
    }

    private HomeActivity homeActivity;
    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onOk(String tag) {
        cancelOrder();
    }

    @Override
    public void onCancel(String tag) {

    }
    private void cancelOrder(){
        GlobalClass.getClsOrderBook().cancelOrderRequest(selectedOrderBk );
        GlobalClass.fragmentManager.popBackStackImmediate();
      /*  ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(selectedOrderBk.scripCode.getValue());
        if(scripDetail != null) {
            GlobalClass.clsOrderBook.cancelOrderRequest(selectedOrderBk,scripDetail);
            GlobalClass.fragmentManager.popBackStackImmediate();
        }
        else{
            isCancel = true;
        }*/
    }

    private double prevLastrate = 0.00;
    class OrderBookDetailHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case SCRIPT_DETAILS_Response:
                                GlobalClass.dismissdialog();
                                if(isCancel) {
                                    cancelOrder();
                                    isCancel = false;
                                }
                                break;
                            case LITE_MW:
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if(selectedOrderBk.scripCode.getValue() == scripCode){
                                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,false);
                                    if(mktWatch != null) {
                                        mktWatch.setLtpWithTxtColor(currentRateValue,prevLastrate);
                                        prevLastrate = mktWatch.getLastRate();
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        GlobalClass.onError("TradeLoginHandler : " ,ex);
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}