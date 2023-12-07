package view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import com.ventura.venturawealth.R;

import Structure.Response.Scrip.ScripDetail;
import enums.eDelvIntra;
import enums.eExch;
import enums.eMessageCode;
import enums.eOrderType;
import models.BuySellModel;
import utils.Constants;
import utils.CustomNumberPicker2old;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 12/21/2016.
 */
public class BuySellWindowforQuickOrder extends LinearLayout implements View.OnClickListener{
  //  private Context context;
    private CustomNumberPicker2old cusNumPickQty, cusNumPickLimitPrice, cusNumPickDiscQty, cusNumPickTriggerPrice;
    private EditText editQty, editLimitPrice, editDiscQty, editTriggerPrice;
    private TextView buySellTitle;
    private Spinner intraDelSpinner,mktLimitSpinner;
    private Button closeBtn,btnPlaceOrder;
    private CheckBox iocChkBox,stopLossChkBox;
    //private int scriptCode;
    private LinearLayout dqLayout;
   // private boolean showHide,isIOC=false,isStopLoss= false;
  //  private String purchageType="",security="",spinnerItem="";
  //  private int selectedExchange;
    private BuySellModel buysell;
    private ArrayAdapter<String> ltMktAdapter;

    public BuySellWindowforQuickOrder(Context context,BuySellModel buySellModel) {
        super(context);
        this.buysell = buySellModel;
        isShow = buySellModel.isShow();
       // this.context = context;
       // this.purchageType = buySellModel.getOrderType();
      //  this.selectedExchange = buySellModel.getExchange();
       // this.spinnerItem = buySellModel.getQuickOrderType();
        //this.security = buySellModel.getScriptName();
      //  this.scriptCode = buySellModel.getScriptCode();
      //  this.showHide = buySellModel.isShow();
        addView(LayoutInflater.from(this.getContext()).inflate(R.layout.buy_sell_screen, null));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(params);

        buySellTitle = findViewById(R.id.buySellTitle);
        intraDelSpinner = findViewById(R.id.intraDel_spinner);
        mktLimitSpinner = findViewById(R.id.mktLimit_Spinner);
        setRefreshValues();
        setIntraDelSpinnerListener();
    }

    public static Handler handler;

    private void setIntraDelSpinnerListener(){
        if (buysell.getScriptCode()>0){
            ScripDetail scripDetails = GlobalClass.mktDataHandler.getScripDetailData(buysell.getScriptCode());
            if (scripDetails!=null){
                BuySellWindowforQuickOrder.handler = null;
                cusNumPickQty.setChangeVal(scripDetails.mktLot.getValue());
                cusNumPickDiscQty.setChangeVal(scripDetails.mktLot.getValue());
                cusNumPickLimitPrice.setChangeVal(scripDetails.tickSize.getValue());
                cusNumPickTriggerPrice.setChangeVal(scripDetails.tickSize.getValue());
                intraDelSpinner.setSelection(0);
                intraDelSpinner.setOnItemSelectedListener(null);
                intraDelSpinner.post(() -> intraDelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (!scripDetails.enableIntraDelForCategory() &&
                                intraDelSpinner.getSelectedItem().toString().equalsIgnoreCase(eDelvIntra.INTRADAY.name)) {
                            String msg = "Scrip is not allowed for additional Intraday Multiplier";
                            new AlertBox(GlobalClass.latestContext, "", "OK", msg, false);
                            intraDelSpinner.setSelection(0);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                }));
            }else {
                BuySellWindowforQuickOrder.handler = new BSQuickOrderWindowHandler();
            }
        }
    }

    private void setRefreshValues() {
        if(UserSession.getLoginDetailsModel().isIntradayDelivery() &&
                (buysell.getExchange() == eExch.NSE.value || buysell.getExchange() == eExch.BSE.value )) {
            ArrayList<String> intraDelArr = new ArrayList<>();
            intraDelArr.add(eDelvIntra.DELIVERY.name);
            intraDelArr.add(eDelvIntra.INTRADAY.name);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, intraDelArr);
            dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
            intraDelSpinner.setAdapter(dataAdapter);
            String intDelStr = buysell.isIntraDay().name;
            if (intraDelArr.size()>1 && intraDelArr.contains(intDelStr) ){
                intraDelSpinner.setSelection(intraDelArr.indexOf(intDelStr));
            }
        }
        else{
            intraDelSpinner.setVisibility(GONE);
        }
//        if(selectedExchange == eExch.FNO.value || selectedExchange == eExch.NSECURR.value ){
//            intraDelSpinner.setVisibility(GONE);
//        }

        ArrayList<String> limitMktArr = new ArrayList<String>();
        limitMktArr.add(eOrderType.LIMIT.name);
        //limitMktArr.add(eOrderType.MARKET.name);
        ltMktAdapter = new ArrayAdapter<String>(getContext(),R.layout.custom_spinner_item,limitMktArr);
        ltMktAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        mktLimitSpinner.setAdapter(ltMktAdapter);
        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(this);
        btnPlaceOrder.setText("Save");
        cusNumPickQty = (CustomNumberPicker2old) findViewById(R.id.editQty);
        cusNumPickLimitPrice = (CustomNumberPicker2old) findViewById(R.id.editLimitPrc);
        cusNumPickLimitPrice.setInputType("price",buysell.getExchange());
        cusNumPickDiscQty = (CustomNumberPicker2old) findViewById(R.id.editDiscQty);
        cusNumPickTriggerPrice = (CustomNumberPicker2old) findViewById(R.id.editTriggerPrice);
        cusNumPickTriggerPrice.setInputType("price",buysell.getExchange());
        editQty = (EditText) cusNumPickQty.findViewById(R.id.edit_text);
        editLimitPrice = (EditText) cusNumPickLimitPrice.findViewById(R.id.edit_text);
        editDiscQty = (EditText) cusNumPickDiscQty.findViewById(R.id.edit_text);
        editTriggerPrice = (EditText) cusNumPickTriggerPrice.findViewById(R.id.edit_text);

        cusNumPickTriggerPrice.setVisibility(View.INVISIBLE);

        dqLayout = (LinearLayout) findViewById(R.id.dqLayout);
        iocChkBox = (CheckBox) findViewById(R.id.checkboxIOC);
        stopLossChkBox = (CheckBox) findViewById(R.id.chkStopLoss);

        stopLossChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cusNumPickTriggerPrice.setVisibility(View.VISIBLE);
                } else {
                    cusNumPickTriggerPrice.setVisibility(View.INVISIBLE);
                    editTriggerPrice.setText("");
                }
            }
        });

        iocChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    stopLossChkBox.setChecked(false);
                    stopLossChkBox.setEnabled(false);
                    dqLayout.setVisibility(View.GONE);
                    editDiscQty.setText("");
                } else {
                    dqLayout.setVisibility(View.VISIBLE);
                    if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.LIMIT.name)) {
                        stopLossChkBox.setEnabled(true);
                    }
                }
            }
        });
        closeBtn = (Button) findViewById(R.id.btnClose);
        closeBtn.setVisibility(GONE);
        firstTimeValueSet();
        hintTitleMethod();
        firstInitialisation();
    }


    private void firstTimeValueSet() {
        editQty.setText(buysell.getQty());
        editQty.setSelection(buysell.getQty().length());
        editDiscQty.setText(buysell.getDiscQty());
        editDiscQty.setSelection(buysell.getDiscQty().length());
        editLimitPrice.setText(buysell.getPrice());
        editLimitPrice.setSelection(buysell.getPrice().length());
        if (buysell.isStopLoss()){
            stopLossChkBox.setChecked(true);
            editTriggerPrice.setText(buysell.getTiggerPrice());
        }
        if (buysell.isIOC()){
            iocChkBox.setChecked(true);
        }
        if (buysell.isMkt()) {
            mktLimitSpinner.setSelection(ltMktAdapter.getPosition(eOrderType.MARKET.name));
        }
    }

    private void firstInitialisation(){
        if(buysell.getOrderType().equalsIgnoreCase("BUY")){
            buySellTitle.setText("Buy Order");
            buySellTitle.setTextColor(getResources().getColor(R.color.green1));
        }
        else {
            buySellTitle.setText("Sell Order");
            buySellTitle.setTextColor(getResources().getColor(R.color.red));
        }
        if(buysell.getExchange() == eExch.FNO.value){
            visibilityFORFNO(VISIBLE);
            dqLayout.setVisibility(GONE);
        }
        else{
            visibilityFORFNO(GONE);
        }
    }

    private void visibilityFORFNO(int visibility) {

        (cusNumPickQty.findViewById(R.id.up)).setVisibility(visibility);
        (cusNumPickQty.findViewById(R.id.down)).setVisibility(visibility);
        (cusNumPickQty.findViewById(R.id.leftSep)).setVisibility(visibility);
        (cusNumPickQty.findViewById(R.id.rightSep)).setVisibility(visibility);

        (cusNumPickLimitPrice.findViewById(R.id.up)).setVisibility(visibility);
        (cusNumPickLimitPrice.findViewById(R.id.down)).setVisibility(visibility);
        (cusNumPickLimitPrice.findViewById(R.id.leftSep)).setVisibility(visibility);
        (cusNumPickLimitPrice.findViewById(R.id.rightSep)).setVisibility(visibility);

        (cusNumPickDiscQty.findViewById(R.id.up)).setVisibility(visibility);
        (cusNumPickDiscQty.findViewById(R.id.down)).setVisibility(visibility);
        (cusNumPickDiscQty.findViewById(R.id.leftSep)).setVisibility(visibility);
        (cusNumPickDiscQty.findViewById(R.id.rightSep)).setVisibility(visibility);

        (cusNumPickTriggerPrice.findViewById(R.id.up)).setVisibility(visibility);
        (cusNumPickTriggerPrice.findViewById(R.id.down)).setVisibility(visibility);
        (cusNumPickTriggerPrice.findViewById(R.id.leftSep)).setVisibility(visibility);
        (cusNumPickTriggerPrice.findViewById(R.id.rightSep)).setVisibility(visibility);
    }
    private void hintTitleMethod() {
        editQty.setHint("Qty");
        editLimitPrice.setHint("Price");
        editDiscQty.setHint("Disc Qty");
        editTriggerPrice.setHint("Trigger Price");

        if(buysell.getExchange() == eExch.NSECURR.value) {
            cusNumPickLimitPrice.setMoneyFormat_twoDecimal(4);
            cusNumPickLimitPrice.setDecimalUpto(4);

            cusNumPickTriggerPrice.setMoneyFormat_twoDecimal(4);
            cusNumPickTriggerPrice.setDecimalUpto(4);
        }
        else{
            cusNumPickLimitPrice.setMoneyFormat_twoDecimal(2);
            cusNumPickLimitPrice.setDecimalUpto(2);

            cusNumPickTriggerPrice.setMoneyFormat_twoDecimal(2);
            cusNumPickTriggerPrice.setDecimalUpto(2);
        }
        ((TextView) cusNumPickQty.findViewById(R.id.title)).setText("Q : ");
        ((TextView) cusNumPickLimitPrice.findViewById(R.id.title)).setText("P : ");
        ((TextView) cusNumPickDiscQty.findViewById(R.id.title)).setText("DQ : ");
        ((TextView) cusNumPickTriggerPrice.findViewById(R.id.title)).setText("TP : ");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlaceOrder:
                try {
                    if (buysell.getScriptName().equalsIgnoreCase("")){
                        GlobalClass.showToast(getContext(), "Please select security");
                    }else if (TextUtils.isEmpty(editQty.getText().toString().trim())){
                        Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                    }else if (!editDiscQty.getText().toString().trim().equalsIgnoreCase("") &&
                            Long.parseLong(editDiscQty.getText().toString())>=Long.parseLong(editQty.getText().toString())){
                        GlobalClass.showToast(getContext(), Constants.ERR_DISC_QTY);
                    }else if (TextUtils.isEmpty(editLimitPrice.getText().toString().trim())){
                        GlobalClass.showToast(getContext(),"Please enter price");
                    }else if (stopLossChkBox.isChecked() && TextUtils.isEmpty(editTriggerPrice.getText().toString().trim())){
                        GlobalClass.showToast(getContext(),"Please enter tigger price");

                    }else {
                        //todo price compare with TriggerPrice for buy and sell
                        saveOrder();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void saveOrder() {
//        BuySellModel buySellModel = new BuySellModel();
//        buySellModel.setShow(showHide);
//        buySellModel.setQuickOrderType(spinnerItem);
//        buySellModel.setScriptCode(scriptCode);
//        buySellModel.setScriptName(security);
//        buySellModel.setOrderType(purchageType);
        if (!editQty.getText().toString().trim().equalsIgnoreCase(""))
            buysell.setQty(editQty.getText().toString().trim());
        if (!editLimitPrice.getText().toString().trim().equalsIgnoreCase(""))
            buysell.setPrice(editLimitPrice.getText().toString().trim());
        if (!editDiscQty.getText().toString().trim().equalsIgnoreCase(""))
            buysell.setDiscQty(editDiscQty.getText().toString().trim());
        if (!editTriggerPrice.getText().toString().trim().equalsIgnoreCase(""))
            buysell.setTiggerPrice(editTriggerPrice.getText().toString().trim());
        if (mktLimitSpinner.getSelectedItem().toString().equalsIgnoreCase(eOrderType.LIMIT.name)) {
            buysell.setMkt(false);
        }
        eDelvIntra delvIntra = intraDelSpinner.getSelectedItemPosition()>0?eDelvIntra.INTRADAY:eDelvIntra.DELIVERY;
        buysell.setIntraDay(delvIntra);
        buysell.setShow(isShow);
        buysell.setIOC(iocChkBox.isChecked());
        buysell.setStopLoss(stopLossChkBox.isChecked());
        PreferenceHandler.getQuickOrderList().put(buysell.getQuickOrderType(),buysell);
        PreferenceHandler.setQuickOrderList();
        new AlertBox(GlobalClass.latestContext,"","Ok","Order Saved successfully.",false);
        if (GlobalClass.onOrderSave != null){
            GlobalClass.onOrderSave.onOrderSave();
        }

    }

    public void saveSecurity(String security,int scripCode){
        buysell.setScriptName(security);
        buysell.setScriptCode(scripCode);
        setIntraDelSpinnerListener();
    }

    private boolean isShow;

    public void showHide(boolean showHide){
        isShow = showHide;
       // buysell.setShow(showHide);
    }

    class BSQuickOrderWindowHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(StaticVariables.MSGCODE);
                    eMessageCode eshgCode = eMessageCode.valueOf(msgCode);
                    switch (eshgCode) {
                        case SCRIPT_DETAILS_Response:
                            int token = refreshBundle.getInt(StaticVariables.SCRIPCODE);
                            if (token == buysell.getScriptCode()) {
                                setIntraDelSpinnerListener();
                            }
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

}