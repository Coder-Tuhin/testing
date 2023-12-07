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

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Other.StructBuySell;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructOCOOrdBkDet;
import butterknife.BindView;
import butterknife.ButterKnife;
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
public class BracketOrderbookDetailsFragment extends Fragment implements View.OnClickListener, OnAlertListener {

    private TextView screptName,buySell ,qtyRate,tradedAvgValue,tradedQty,pendingQty,stopLoss,stopLossTriggered,
            triggerRate,Time,status,brokerId,exchangeId,orderTypeTxtView,pageNumberTxt, tradedQtyTitle,pendingQtyTitle ;
    private ImageButton backbtn,prevbtn,nextbtn;
    private Button modifybtn,cancelbtn;


    private TreeMap<String, StructOCOOrdBkDet> orderBKData;
    private ArrayList<String> allKeyDecending;
    private int selectedPosition = 0;
    private StructOCOOrdBkDet selectedOrderBk;
    private boolean isCancel = false;
    @BindView(R.id.order_relativeseven) RelativeLayout order_relativeseven;

    public BracketOrderbookDetailsFragment(){super();}

    public BracketOrderbookDetailsFragment(TreeMap<String, StructOCOOrdBkDet> orderBKData, ArrayList<String> allBrkoerOrdIds, int position){
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
        setvalue();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.bracketBkDetailUIHandler= new BracketOrderBookDetailHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.bracketBkDetailUIHandler = null;
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization(View layout) {

        screptName = (TextView)layout.findViewById(R.id.screptname_textview);
        buySell = (TextView)layout.findViewById(R.id.buysell_OrdD_textview);
        qtyRate = (TextView)layout.findViewById(R.id.buy_value_ordD_textview);
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

    private void setvalue() {

        /*if (selectedOrderBk.isIntraDelAllow()){
            order_relativeseven.setVisibility(View.VISIBLE);
        }else {*/
            order_relativeseven.setVisibility(View.GONE);
        //}
        isCancel = false;
        NumberFormat formatter = Formatter.getFormatter(selectedOrderBk.segment.getValue());
        if(selectedOrderBk.segment.getValue() == eExch.NSECURR.value){
            tradedQtyTitle.setText("Traded Lots:");
            pendingQtyTitle.setText("Pending Lots:");
        }
        else{
            tradedQtyTitle.setText("Traded Qty:");
            pendingQtyTitle.setText("Pending Qty:");
        }
        String strScripName = selectedOrderBk.scripName.getValue();
        strScripName = strScripName.replace("\n"," ");
        screptName.setText(strScripName);
        buySell.setText(selectedOrderBk.getBuySell());
        qtyRate.setText(selectedOrderBk.getOrderQtyRate());
        tradedAvgValue.setText("" + formatter.format(selectedOrderBk.rate.getValue()));
        tradedQty.setText("" + selectedOrderBk.qty.getValue());
        pendingQty.setText("" + 0);
        stopLoss.setText("");
        stopLossTriggered.setText("");
        triggerRate.setText("");
        String strTime = "";
        if(selectedOrderBk.entryTime.getDateInNumber() > 0){
            strTime = DateUtil.dateFormatter(selectedOrderBk.entryTime.getValue(), Constants.DDMMMYYHHMMSS);
        }
        else{
            strTime = DateUtil.dateFormatter(selectedOrderBk.openTime.getValue(), Constants.DDMMMYYHHMMSS);
        }
        Time.setText("" + strTime);
        status.setText("" + selectedOrderBk.getFinalStatus());
        brokerId.setText("" + selectedOrderBk.positionID.getValue());
        exchangeId.setText("" + selectedOrderBk.entryEOI.getValue());

        /*
        if(UserSession.getLoginDetailsModel().isIntradayDelivery()){
            orderTypeTxtView.setText("" + selectedOrderBk.getProductTypeStr());
        }*/

        modifybtn.setVisibility(View.GONE);
        cancelbtn.setVisibility(View.GONE);
        /*
        if(selectedOrderBk.finaPendingQty > 0){
            modifybtn.setVisibility(View.VISIBLE);
            cancelbtn.setVisibility(View.VISIBLE);
        }*/
        String pageNo = (selectedPosition+1)+"/"+orderBKData.size();
        pageNumberTxt.setText(pageNo);
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
                /*if (selectedOrderBk.isOrderSentToExch()){
                    Toast.makeText(GlobalClass.latestContext, Constants.ERR_ORDER_MODIFY_SENTTOEXCH,
                            Toast.LENGTH_SHORT).show();
                }else {
                    modifyBtnClick();
                }*/
                break;
            case R.id.cancel_button:
                /*if (selectedOrderBk.isOrderSentToExch()){
                    Toast.makeText(GlobalClass.latestContext, Constants.ERR_ORDER_CANCEL_SENTTOEXCH,
                            Toast.LENGTH_SHORT).show();
                }else {
                    cancelBtnClick();
                }*/
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
        groupsTokenDetails.scripName.setValue(selectedOrderBk.scripName.getValue());
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = new StructBuySell();
        if(selectedOrderBk.qty.getValue()>0){
            buySell.buyOrSell = eOrderType.BUY;
        }
        else{
            buySell.buyOrSell = eOrderType.SELL;
        }
        buySell.modifyOrPlace = eOrderType.MODIFY;
        //buySell.order = selectedOrderBk;
        buySell.showDepth = eShowDepth.ORDERBOOK;

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.ORDERBOOK,grplist,buySell, homeActivity.SELECTED_RADIO_BTN,true);
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
        //GlobalClass.clsOrderBook.cancelOrderRequest(selectedOrderBk );
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

    class BracketOrderBookDetailHandler extends Handler {
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