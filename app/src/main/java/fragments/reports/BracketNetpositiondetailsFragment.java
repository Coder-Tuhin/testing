package fragments.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.ArrayList;
import java.util.Date;

import Structure.Other.StructBuySell;
import Structure.Request.RC.StructOCOOrder;
import Structure.Request.RC.StructOrderRequest;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructOCOOrdBkDet;
import Structure.Response.RC.StructOCOPosnDet;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.SendDataToRCServer;
import enums.eAbsTicks;
import enums.eDirection;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.eProductType;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import interfaces.OnAlertListener;
import utils.Constants;
import utils.DateUtil;
import utils.GlobalClass;
import utils.UserSession;
import view.AlertBox;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
@SuppressLint("ValidFragment")
public class BracketNetpositiondetailsFragment extends Fragment implements View.OnClickListener, OnAlertListener {

    private ArrayList<StructOCOPosnDet> allNetPosition;
    private StructOCOPosnDet selectedNetPosition;
    private int selectedPosition = 0;

    public static Handler bracketnetPositionDetailUIHandler;
    TextView scripName, netQtyRate, currentRateValue, mtm, booked, entryDetails, entryTime,
            closeDetail, closeTime, positionID,
            takeProfitTitle,takeProfit, stopLoss, pageNumberTxt, orderTypeTxt,
            statusTxt,directionTxt;

    ImageButton backbtn, prevbtn, nextbtn;
    Button modifybtn,exitbtn;
    @BindView(R.id.net_position_relativeseven) RelativeLayout net_position_relativeseven;


    public BracketNetpositiondetailsFragment(){super();}
    public BracketNetpositiondetailsFragment(ArrayList<StructOCOPosnDet> allNetPosition, int position) {
        this.allNetPosition = allNetPosition;
        this.selectedPosition = position;
        selectedNetPosition = this.allNetPosition.get(selectedPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.bracketnetpositiondetails_screen, container, false);
        ButterKnife.bind(this,layout);
        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        bracketnetPositionDetailUIHandler = new BracketNetPositionDEetailHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        bracketnetPositionDetailUIHandler = null;
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization(View layout) {

        scripName = (TextView) layout.findViewById(R.id.screptname_textview);
        netQtyRate = (TextView) layout.findViewById(R.id.total_buy_value_textview);
        currentRateValue = (TextView) layout.findViewById(R.id.current_ratevalue_textview);
        mtm = (TextView) layout.findViewById(R.id.mtm_textview);
        booked = (TextView) layout.findViewById(R.id.booked_textview);
        entryDetails = (TextView) layout.findViewById(R.id.entry_textview);
        entryTime = (TextView) layout.findViewById(R.id.entrytime_textview);
        closeDetail = (TextView) layout.findViewById(R.id.close_value_textview);
        closeTime = (TextView) layout.findViewById(R.id.closetime_value_textview);
        positionID = (TextView) layout.findViewById(R.id.positionid_value_textview);
        takeProfitTitle = (TextView) layout.findViewById(R.id.takeprofit_title);
        takeProfit = (TextView) layout.findViewById(R.id.takeprofit_value_textview);
        stopLoss = (TextView) layout.findViewById(R.id.stoploss_value_textview);
        orderTypeTxt = (TextView) layout.findViewById(R.id.netoisition_ordertype_textview);
        statusTxt = (TextView) layout.findViewById(R.id.status_textview);
        directionTxt = (TextView) layout.findViewById(R.id.direction_textview);


        pageNumberTxt = (TextView) layout.findViewById(R.id.pagecount_textview);
        backbtn = (ImageButton) layout.findViewById(R.id.previnit_imagebutton);
        backbtn.setOnClickListener(this);
        prevbtn = (ImageButton) layout.findViewById(R.id.prevbtn_textview);
        prevbtn.setOnClickListener(this);
        nextbtn = (ImageButton) layout.findViewById(R.id.nextbtn_textview);
        nextbtn.setOnClickListener(this);
        modifybtn = (Button) layout.findViewById(R.id.modify_button);
        modifybtn.setOnClickListener(this);
        exitbtn = (Button) layout.findViewById(R.id.exit_button);
        exitbtn.setOnClickListener(this);
        setvalue();
    }

    private void setvalue() {
        /*if (selectedNetPosition.isIntraDelAllow()){
            net_position_relativeseven.setVisibility(View.VISIBLE);
        }else {*/
            //net_position_relativeseven.setVisibility(View.GONE);
        //}
        scripName.setText(selectedNetPosition.scripName.getValue());
        netQtyRate.setText("");//selectedNetPosition.getNetQtyRate());
        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(selectedNetPosition.scripCode.getValue(),true);
        selectedNetPosition.currRate.setValue(mktWatch.getLastRate());
        currentRateValue.setText(selectedNetPosition.getCurrentRate());
        mtm.setText(selectedNetPosition.mtmpl.getValue()  +"");
        booked.setText(selectedNetPosition.bkpl.getValue() + "");
        entryDetails.setText(selectedNetPosition.getEntryDetail());
        String strTime = DateUtil.dateFormatter(selectedNetPosition.entryTime.getValue(), Constants.DDMMMYYHHMMSS);
        entryTime.setText(strTime);
        closeDetail.setText(selectedNetPosition.getCloseDetail());
        strTime = DateUtil.dateFormatter(selectedNetPosition.closeTime.getValue(), Constants.DDMMMYYHHMMSS);
        closeTime.setText(strTime);
        positionID.setText(selectedNetPosition.positionID.getValue()+"");
        takeProfit.setText(selectedNetPosition.getTakeProfitValue());
        stopLoss.setText(selectedNetPosition.getStopLossValue());
        directionTxt.setText(selectedNetPosition.direction.getValue() == eDirection.LONG.value?"Long":"Short");
        /*
        if(UserSession.getLoginDetailsModel().isIntradayDelivery()) {
            orderTypeTxt.setText("" + selectedNetPosition.getOrderTypeStr());
        }*/
        orderTypeTxt.setText("" + selectedNetPosition.getOrderTypeStr());
        statusTxt.setText(selectedNetPosition.status.getValue());

        modifybtn.setVisibility(View.GONE);
        exitbtn.setVisibility(View.GONE);

        if(selectedNetPosition.getOpenQty() != 0){
            modifybtn.setVisibility(View.VISIBLE);
            exitbtn.setVisibility(View.VISIBLE);
        }
        if(selectedNetPosition.productType.getValue() == eProductType.COVERORDER.value){
            takeProfitTitle.setVisibility(View.GONE);
            takeProfit.setVisibility(View.GONE);
        }
        String pageNo = (selectedPosition+1)+"/"+allNetPosition.size();
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
                modifyBtnClick();
                break;
            case R.id.exit_button:
                cancelBtnClick();
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
            selectedNetPosition = this.allNetPosition.get(selectedPosition);
            setvalue();
        }
    }
    private void nextBtnClick(){
        if(selectedPosition < (allNetPosition.size() - 1)){
            selectedPosition = selectedPosition + 1;
            selectedNetPosition = this.allNetPosition.get(selectedPosition);
            setvalue();
        }
    }
    private void modifyBtnClick(){

        int scriptCode = selectedNetPosition.scripCode.getValue();
        int _positionID = selectedNetPosition.positionID.getValue();
        StructOCOOrdBkDet orderForPositionID = GlobalClass.getClsBracketOrderBook().getOrderForPositionID(_positionID);
        StructOrderRequest orderRequest = GlobalClass.getClsBracketPositionBook().getOrderForPositionID(_positionID);

        if(orderForPositionID != null && orderRequest != null) {

            GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
            groupsTokenDetails.scripCode.setValue(scriptCode);
            groupsTokenDetails.scripName.setValue(selectedNetPosition.scripName.getValue());
            ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
            grplist.add(groupsTokenDetails);

            StructBuySell buySell = new StructBuySell();
            if (selectedNetPosition.direction.getValue() == eDirection.LONG.value) {
                buySell.buyOrSell = eOrderType.BUY;
            } else {
                buySell.buyOrSell = eOrderType.SELL;
            }
            buySell.modifyOrPlace = eOrderType.MODIFY;
            buySell.showDepth = eShowDepth.BRACKETPOS;
            buySell.isIoc.setValue(true);
            buySell.bracketOrdDet = orderForPositionID;
            buySell.bracketNetPosn = selectedNetPosition;
            buySell.bracketOrdReq = orderRequest;

            Fragment m_fragment;
            m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.BRACKETPOS, grplist, buySell,
                        homeActivity.SELECTED_RADIO_BTN, true);

            FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, m_fragment, Constants.REPORTFRAGMENT_TAG);
            fragmentTransaction.addToBackStack("");
            fragmentTransaction.commit();

        }else{
            GlobalClass.showAlertDialog("Order details not found please refresh order book");
        }
    }
    private void cancelBtnClick(){
        new AlertBox(GlobalClass.latestContext,"Yes","No","Are you sure you want to exit this order?",this,"cancel");
    }
    @Override
    public void onOk(String tag) {
        cancelOrder();
    }

    @Override
    public void onCancel(String tag) {

    }

    private void cancelOrder(){

        try {
            int _positionID = selectedNetPosition.positionID.getValue();
            StructOCOOrdBkDet orderForPositionID = GlobalClass.getClsBracketOrderBook().getOrderForPositionID(_positionID);
            StructOrderRequest orderRequest = GlobalClass.getClsBracketPositionBook().getOrderForPositionID(_positionID);

            if (orderRequest != null && orderForPositionID != null) {

                String clientCode = UserSession.getLoginDetailsModel().getUserID();
                StructOCOOrder ocoOrder = new StructOCOOrder();
                ocoOrder.orderRequest = orderRequest;
                ocoOrder.orderRequest.orderType.setValue((selectedNetPosition.direction.getValue() == eDirection.LONG.value) ? eOrderType.BUY.value : eOrderType.SELL.value);
                ocoOrder.orderRequest.orderPlaceReqCode.setValue(clientCode);
                ocoOrder.orderRequest.orderRequestCode.setValue(clientCode);
                ocoOrder.orderRequest.localOrderID.setValue(DateUtil.getTimeDiffInSeconds());
                ocoOrder.orderRequest.localOrderTime.setValue(new Date());
                ocoOrder.orderRequest.appID.setValue(2);
                ocoOrder.orderRequest.oldQty.setValue(ocoOrder.orderRequest.qty.getValue());
                ocoOrder.orderRequest.pendQty.setValue(ocoOrder.orderRequest.qty.getValue());
                ocoOrder.orderRequest.reqType.setValue(eOrderType.CANCEL.value);

                ocoOrder.reqType.setValue(eOrderType.CANCEL.value);
                ocoOrder.TPChgd.setValue(1);

                ocoOrder.TP.AbsTicks.setValue(eAbsTicks.ABS.value);
                ocoOrder.TP.CloseAt.setValue(orderForPositionID.closeAtTP.getValue()); //for LTP
                float tpAbsVal;
                float slAbsVal;
                if(selectedNetPosition.direction.getValue() == eDirection.LONG.value){
                    tpAbsVal = selectedNetPosition.takeProf.getValue() - selectedNetPosition.openPrice.getValue();
                    slAbsVal = selectedNetPosition.openPrice.getValue() - selectedNetPosition.stopLoss.getValue();
                }else{
                    tpAbsVal = selectedNetPosition.openPrice.getValue() - selectedNetPosition.takeProf.getValue();
                    slAbsVal = selectedNetPosition.stopLoss.getValue() - selectedNetPosition.openPrice.getValue();
                }
                ocoOrder.TP.AbsVal.setValue(orderForPositionID.absValTP.getValue() == 0?(tpAbsVal):orderForPositionID.absValTP.getValue());
                ocoOrder.TP.Ticks.setValue(orderForPositionID.ticksTP.getValue());

                ocoOrder.SL.AbsTicks.setValue(eAbsTicks.ABS.value);
                ocoOrder.SL.CloseAt.setValue(orderForPositionID.closeAtSL.getValue()); //for LTP
                ocoOrder.SL.AbsVal.setValue(orderForPositionID.absValSL.getValue() == 0?slAbsVal:orderForPositionID.absValSL.getValue());
                ocoOrder.SL.Ticks.setValue(orderForPositionID.ticksSL.getValue());

                ocoOrder.TickSize.setValue(orderForPositionID.tickSize.getIntValue());
                ocoOrder.isTrailingSL.setValue(orderForPositionID.isTrailingSL.getValue());
                ocoOrder.TrlSL.setValue(orderForPositionID.trlSL.getValue());
                /*
                OldSL.Ticks.setValue(structBuySell.bracketOrdDet.ticksSL.getValue());
                OldSL.CloseAt.setValue(structBuySell.bracketOrdDet.closeAtSL.getValue());
                OldSL.AbsTicks.setValue(structBuySell.bracketOrdDet.absTicksSL.getValue());
                OldSL.AbsVal.setValue(structBuySell.bracketOrdDet.absValSL.getValue());
                */
                ocoOrder.setByteData();

                SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
                sendDataToRCServer.sendEXITBracketOrderReq(ocoOrder);

            }else{
                GlobalClass.showAlertDialog("Order details not found please refresh order book");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private HomeActivity homeActivity;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    class BracketNetPositionDEetailHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case LITE_MW:
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if(selectedNetPosition.scripCode.getValue() == scripCode){
                                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,false);
                                    selectedNetPosition.currRate.setValue(mktWatch.getLastRate());
                                    currentRateValue.setText(selectedNetPosition.getCurrentRate()+"");
                                }
                                break;
                            case TRADE_BOOK_RESP:
                                backBtnClick();
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