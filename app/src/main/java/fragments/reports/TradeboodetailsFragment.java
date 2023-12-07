package fragments.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Response.RC.StructMarginReportReplyRecord_Pointer;
import Structure.Response.RC.StructMgnInfoRes;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import Structure.Response.Scrip.ScripDetail;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eProductTypeITS;
import enums.eServerType;
import interfaces.OnAlertListener;
import interfaces.OnDeleveryClick;
import models.MessageModel;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import view.ConverDeliveryPopup;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
@SuppressLint("ValidFragment")
public class TradeboodetailsFragment extends Fragment implements View.OnClickListener , OnAlertListener, OnDeleveryClick{


    private TextView scripName, qtyRate,time,exchangeTradeId,exchangeOrderId,buySell,pageNumberTxt,
            orderTypeTitleTxt, orderTypeTxt ;
    private ImageButton backbtn,prevbtn,nextbtn;
    private Button positionConversionBtn;
    private ConverDeliveryPopup converDeliveryPopup;

    private TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeBKData;
    private StructTradeReportReplyRecord_Pointer selectedTradbook;
    private ArrayList<Integer> allKeyDecending;
    private int selectedPosition = 0;
    private  boolean isPositionConvert = false;

    public TradeboodetailsFragment(){super();}

    public TradeboodetailsFragment(TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeBKData, ArrayList<Integer> allTradeIds, int position){
        this.tradeBKData = tradeBKData;
        this.allKeyDecending = allTradeIds;
        this.selectedPosition = position;
        selectedTradbook = tradeBKData.get(allKeyDecending.get(selectedPosition));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.tradebookdetails_screen, container, false);
        GlobalClass.reportdetailsfragment = true;
        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization(View layout) {
        scripName = (TextView) layout.findViewById(R.id.screptradtname_textview);
        buySell = (TextView) layout.findViewById(R.id.buysell_trdD_textview);
        qtyRate = (TextView) layout.findViewById(R.id.buy_sell_qty_textview);
        time = (TextView) layout.findViewById(R.id.time_textview);
        exchangeTradeId = (TextView) layout.findViewById(R.id.exchange_tradeid_textview);
        exchangeOrderId = (TextView) layout.findViewById(R.id.exchange_orderid_textview);

        orderTypeTitleTxt = (TextView) layout.findViewById(R.id.trdDetail_ordtypetitle_textview);
        orderTypeTxt = (TextView) layout.findViewById(R.id.trdDetail_ordtype_textview);
        positionConversionBtn = (Button)layout.findViewById(R.id.positionconversion_button);
        positionConversionBtn.setOnClickListener(this);
        pageNumberTxt = (TextView) layout.findViewById(R.id.pagecount_textview);
        backbtn = (ImageButton) layout.findViewById(R.id.previnit_imagebutton);
        prevbtn = (ImageButton) layout.findViewById(R.id.prevbtn_textview);
        nextbtn = (ImageButton) layout.findViewById(R.id.nextbtn_textview);

        backbtn.setOnClickListener(this);
        prevbtn.setOnClickListener(this);
        nextbtn.setOnClickListener(this);

        setValue();
    }
    private void setValue(){
        if (selectedTradbook.isIntraDelAllow() ){
            orderTypeTitleTxt.setVisibility(View.VISIBLE);
            orderTypeTxt.setVisibility(View.VISIBLE);
            positionConversionBtn.setVisibility(View.VISIBLE);
            orderTypeTxt.setText("" + selectedTradbook.getOrderTypeStr());
        }else {
            orderTypeTitleTxt.setVisibility(View.GONE);
            orderTypeTxt.setVisibility(View.GONE);
            positionConversionBtn.setVisibility(View.GONE);
        }
        if(selectedTradbook.orderType.getValue() == eProductTypeITS.COVERORDER.value ||
            selectedTradbook.orderType.getValue() == eProductTypeITS.BRACKETORDER.value){
            positionConversionBtn.setVisibility(View.GONE);
        }

        isPositionConvert = false;
        scripName.setText(""+selectedTradbook.getFormatedScripName(false));
        buySell.setText("" + selectedTradbook.getBuySell());
        qtyRate.setText(""+selectedTradbook.getTradeQtyRate());
        time.setText("" + DateUtil.dateFormatter(selectedTradbook.exchTradeTime.getValue(), Constants.DDMMMYYHHMMSS));
        exchangeTradeId.setText("" + selectedTradbook.exchTradeID.getValue());
        exchangeOrderId.setText("" + selectedTradbook.exchOrderID.getValue());
        String pageNo = (selectedPosition+1)+"/"+tradeBKData.size();
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
            case R.id.positionconversion_button:
                positionConversionBtnClick();
                break;
            default:
                break;
        }

    }

    @Override
    public void onOk(String tag) {
        if(tag.equalsIgnoreCase("convert")){
            if (converDeliveryPopup != null) converDeliveryPopup.dismissDialog();
            sendPositionConversionRequest();
        }
    }

    @Override
    public void onCancel(String tag) {

    }

    @Override
    public void onDeleveryClick() {
        confirmConversion();
    }

    private void positionConversionBtnClick() {
        converDeliveryPopup = new ConverDeliveryPopup(this,selectedTradbook);
        converDeliveryPopup.openConvertDelivery();
    }
    private void confirmConversion(){
        new AlertBox("Convert","Cancel","Are you sure you want to convert this position?",this,"convert");
    }
    private void sendPositionConversionRequest(){
        GlobalClass.tradeBKDetailUIHandler = new TradeBookDetailHandler();
        ScripDetail scripDetail = GlobalClass.mktDataHandler.getScripDetailData(selectedTradbook.scripCode.getValue());
        if(scripDetail != null) {
            GlobalClass.getClsTradeBook().sendPositionConvertion(selectedTradbook,scripDetail);
        }
        else{
            isPositionConvert = true;
        }
    }
    private void handlePositionConversionResponse(){
        //xNE_MarginReport *marginReport = [[xNeutrinoConnect sharedInstance] getMarginDetail_Pointer];
        //NSString* strMsg = [NSString stringWithFormat:@"MBOP changed to %@",[xModuleClass valueToString:[marginReport doubleMarginForOpenPos]]];
        selectedTradbook = tradeBKData.get(allKeyDecending.get(selectedPosition));
        setValue();
        GlobalClass.getClsMarginHolding().sendMarginRequest(true,"6");
    }

    private void handleMargin(){
        try {
            if(UserSession.getClientResponse().getServerType() == eServerType.RC){
                StructMgnInfoRes margininfo = GlobalClass.getClsMarginHolding().getMarginDetailRC();
                String _message = "MBOP changed to " + Formatter.formatter.format(margininfo.getMBOP());
                //  GlobalClass.log("DialogMessageTest1","   "+_message);
                MessageModel _messageModel = new MessageModel(_message, DateUtil.NToT(DateUtil.CurrentTimeToN()));
                GlobalClass.addMessages(_messageModel,false);
            }else {
                StructMarginReportReplyRecord_Pointer margininfo = GlobalClass.getClsMarginHolding().getMarginDetail();
                String _message = "MBOP changed to " + Formatter.formatter.format(margininfo.marginForOpenPos.getValue());
                //  GlobalClass.log("DialogMessageTest1","   "+_message);
                MessageModel _messageModel = new MessageModel(_message, DateUtil.NToT(DateUtil.CurrentTimeToN()));
                GlobalClass.addMessages(_messageModel,false);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        //GlobalClass.fragmentManager.popBackStackImmediate();
    }

    private void backBtnClick(){
        GlobalClass.fragmentManager.popBackStackImmediate();
    }

    private void prevBtnClick(){
        if(selectedPosition > 0){
            selectedPosition = selectedPosition - 1;
            selectedTradbook = tradeBKData.get(allKeyDecending.get(selectedPosition));
            setValue();
        }
    }
    private void nextBtnClick(){
        if(selectedPosition < (tradeBKData.size() - 1)){
            selectedPosition = selectedPosition + 1;
            selectedTradbook = tradeBKData.get(allKeyDecending.get(selectedPosition));
            setValue();
        }
    }


    class TradeBookDetailHandler extends Handler {
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
                                if(isPositionConvert) {
                                    sendPositionConversionRequest();
                                    isPositionConvert = false;
                                }
                                break;
                            case POSITION_CONVERSION:
                                handlePositionConversionResponse();
                                break;
                            case MARGIN:
                            case MARGIN_NEW:
                            case MARGIN_RC:
                                handleMargin();
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