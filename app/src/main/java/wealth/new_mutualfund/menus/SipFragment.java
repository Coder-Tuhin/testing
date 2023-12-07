package wealth.new_mutualfund.menus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eMFFrequency;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOperationMF;
import enums.eOptionMF;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.MySpannable;
import utils.StaticMessages;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.menus.modelclass.Mandatemodel;
import wealth.new_mutualfund.menus.modelclass.SIPModel;

/**
 * Created by XTREMSOFT on 13-Apr-2018.
 */

public class SipFragment extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    String schemeCode;
    String schemeName;
    String folioNumber = "";
    private String paymentmode = "1";

    private JSONObject jschdetailData;
    private JSONObject jHoldingData;

    private eMFFrequency efrequency;
    private SIPModel sipModel;
    private String selectedConfirmationMsg = "";
    private double selectedSipAmt=0;
    private String formScr = "";

    public static SipFragment newInstance(JSONObject jData, String fromScreen){
        SipFragment f = new SipFragment();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(eMFJsonTag.FORMSCR.name, fromScreen);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @BindView(R.id.sipPeriodLinear)
    LinearLayout sipPeriodLinear;
    @BindView(R.id.endDateLinear)
    LinearLayout endDateLinear;

    @BindView(R.id.schemeName)
    TextView schemeNametxtView;

    @BindView(R.id.folioLayout)
    LinearLayout folioLayout;

    @BindView(R.id.folioSpinner)
    Spinner folioSpinner;
    @BindView(R.id.startasipbtn)
    TextView startasipbtn;
    @BindView(R.id.navvalue)
    TextView navvalue;
    @BindView(R.id.minimum_amount)
    TextView minimum_amount;
    @BindView(R.id.multiply)
    TextView multiply;
    @BindView(R.id.exitloadvalue)
    TextView exitloadvalue;
    @BindView(R.id.frequency)
    Switch frequency;
    @BindView(R.id.startdate)
    TextView startdate;
    @BindView(R.id.period)
    EditText period;
    @BindView(R.id.enddate)
    TextView enddate;
    @BindView(R.id.sipDaySpinner)
    Spinner sipdayspinner;
    @BindView(R.id.amountEditText)
    EditText amountEditText;
    @BindView(R.id.termcondChkBox)
    CheckBox termcondChkBox;
    @BindView(R.id.validChk)
    CheckBox validChk;
    @BindView(R.id.navlinear)
    LinearLayout navlinear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.sip_screen,container,false);
            ButterKnife.bind(this,mView);
            ininValue();
            validChk.setOnCheckedChangeListener(checklistener);
            period.addTextChangedListener(watcher);
            validChk.setChecked(true);
            mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                    homeActivity.showMsgDialog("Terms & Conditions",R.string.mf_terms_conditions,false));
            mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        }
        new SIPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
        return mView;
    }

    private CompoundButton.OnCheckedChangeListener checklistener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
            int visibility = bool?View.GONE:View.VISIBLE;
            sipPeriodLinear.setVisibility(visibility);
            endDateLinear.setVisibility(visibility);
            if (bool){
                period.setText("");
                enddate.setText("");
            }else {
                period.requestFocus();
            }
        }
    };

    private void ininValue(){
        try {
            Bundle args = getArguments();
            String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
            formScr = args.getString(eMFJsonTag.FORMSCR.name, "");

            jHoldingData = new JSONObject(jsonStr);

            schemeCode = jHoldingData.getString("SchemeCode");
            schemeName = jHoldingData.getString("SchemeName");
            folioNumber = "";
            try {
                folioNumber = jHoldingData.getString("FolioNo");
            }
            catch (Exception ex){
                folioNumber = "";
                ex.printStackTrace();
            }
            //currentvalue.setText(jHoldingData.getString("CurrentAmt"));
            //totalunits.setText(jHoldingData.getString("Units"));
            initSpinner();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    AlertDialog alertDialog;
    private void initSpinner() {
        efrequency = eMFFrequency.MONTHLY;
        /*
        String type[] = {schemeName};
        ArrayAdapter spinnerAdapter = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_drop_down);
        spinnerAdapter.addAll(Arrays.asList(type));
        schemeNameSpinner.setAdapter(spinnerAdapter);
        */
        schemeNametxtView.setText(schemeName);

        if(folioNumber.equalsIgnoreCase("")){
            folioLayout.setVisibility(View.GONE);
        }
        else{
            String folioN[] = {folioNumber};
            ArrayAdapter spinnerAdapterF = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
            spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            spinnerAdapterF.addAll(Arrays.asList(folioN));
            folioSpinner.setAdapter(spinnerAdapterF);
        }
        frequency.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // do something, the isChecked will be
            efrequency = isChecked?eMFFrequency.MONTHLY:eMFFrequency.QUARTERLY;
            frequency.setText(efrequency.name);
        });
        startasipbtn.setOnClickListener(view -> {
            if(validateData()) {
                selectedConfirmationMsg = getConfirmationMsg();
                try {
                    selectedSipAmt = Double.parseDouble(amountEditText.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                selectedConfirmationMsg = getConfirmationMsg();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.payment_popup, viewGroup, false);
                LinearLayout ll_UPI = dialogView.findViewById(R.id.ll_UPI);
                EditText upiEditText = dialogView.findViewById(R.id.upiEditText);
                TextView submit = dialogView.findViewById(R.id.submit);
                RadioGroup paymentradiogrp = dialogView.findViewById(R.id.paymentradiogrp);
                Button okBtn = dialogView.findViewById(R.id.okBtn);
                okBtn.setVisibility(View.VISIBLE);
                ImageView img_cross = dialogView.findViewById(R.id.img_cross);
                img_cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                paymentradiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.netbanking:
                                paymentmode = "1";
                                okBtn.setVisibility(View.VISIBLE);
                                break;
                            case R.id.upi:
                                ll_UPI.setVisibility(View.VISIBLE);
                                okBtn.setVisibility(View.GONE);
                                paymentmode = "3";
                                TextView BankText = dialogView.findViewById(R.id.banktext);
                                BankText.setText("Kindly ensure that your UPI id is mapped to your registered bank, " + VenturaServerConnect.mfBank);
                                break;
                        }
                    }

                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (upiEditText.getText().toString().equalsIgnoreCase("")) {
                            GlobalClass.showAlertDialog("please enter and verify UPI ID");

                        } else {
                            new AddUPIReq(eMessageCodeWealth.IPO_UPI_VERIFY.value, upiEditText.getText().toString(), dialogView).execute();
                        }
                    }
                });


                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value, upiEditText.getText().toString()).execute();
                        alertDialog.dismiss();

                    }
                });
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
            }
                /*
                homeActivity.showMsgDialog("Message", getResources().getString(R.string.stampduty), (dialogInterface, i) ->
                                new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute(),
                        (dialogInterface, i) -> dialogInterface.dismiss());*/
        });
    }

    private String getConfirmationMsg(){
        String _tempConfirmation = "Are you sure you want to start a SIP of Rs. "+
                Formatter.DecimalLessIncludingComma(amountEditText.getText().toString());
        if(!validChk.isChecked()){
            _tempConfirmation = _tempConfirmation + " for "+period.getText().toString() +" months";
        }
        _tempConfirmation = _tempConfirmation + " ?";
        return _tempConfirmation;
    }

    private  void  initDaySpinner(){
        ArrayAdapter spinnerAdapterF = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
        spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
        spinnerAdapterF.addAll(sipModel.dateV);
        sipdayspinner.setAdapter(spinnerAdapterF);
        sipdayspinner.setOnItemSelectedListener(onItemSelected);

        String dayNo = DateUtil.getSameDayNextMonthDate().split("-")[0];
        int dayNoInt = Integer.parseInt(dayNo);
        int position = spinnerAdapterF.getPosition(String.valueOf(dayNoInt));
        sipdayspinner.setSelection(position);
    }

    class SIPReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String upiid = "";


        SIPReq(int mCode){
            this.msgCode = mCode;
        }
        SIPReq(int mCode,String UPIid){
            this.msgCode = mCode;
            this.upiid = UPIid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                if(msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemeCode);
                    jdata.put(eMFJsonTag.OPTION.name,eOptionMF.SIP.name);
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    jdata.put(eMFJsonTag.entrymode.name,formScr.equalsIgnoreCase(eFormScr.NFO.name)?"NFO":"FMP");
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name,UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemeCode);
                    jdata.put("TransClientType",VenturaServerConnect.mfClientType);
                    jdata.put(eMFJsonTag.OPTION.name,eOperationMF.SIP.name);//Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    jdata.put(eMFJsonTag.INVESTAMT.name,"");// only used for Purchase,TOpUP
                    jdata.put(eMFJsonTag.REDEEMOPT.name,"");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount//Redeem,SpreadOrder
                    jdata.put(eMFJsonTag.REDEEM.name,"");//Units | Amount //Redeem
                    jdata.put(eMFJsonTag.LIQUIDREDEEM.name,"");//B - Bank,L - Ledger//redeem
                    jdata.put(eMFJsonTag.WITHDRAWAL.name,"");//amount only used for SWP
                    jdata.put(eMFJsonTag.FREQUENCY.name,efrequency.name);//Monthly | Quarterly//amount only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.EXECUTIONDAY.name,"");//amount only used for SWP
                    jdata.put(eMFJsonTag.STARTDATE.name,startdate.getText());//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.ENDDATE.name,enddate.getText());//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.PERIOD.name,period.getText());// only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.TOSCHEMECODE.name,"");// only used for STP,SWITCH
                    jdata.put(eMFJsonTag.TRANSFERAMT.name,"");// only used for STP
                    jdata.put(eMFJsonTag.SWITCHAMT.name,"");// only used for SWITCH
                    jdata.put(eMFJsonTag.SWITCHUNITS.name,"");// only used for SWITCH
                    jdata.put(eMFJsonTag.REDEEMDATE.name,"");// only used for SPREADORDER
                    jdata.put(eMFJsonTag.SIPAMT.name,amountEditText.getText());// only used for SIP
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name,validChk.isChecked()?"T":"F");//date only used for SIP T/F
                    jdata.put(eMFJsonTag.ORDERNO.name,"");
                    jdata.put(eMFJsonTag.TRANSADATE.name,"");
                    jdata.put("OrderNo","");//date only used for SIP T/F
                    jdata.put("TransDate","");//date only used for SIP T/F
                    jdata.put("PaymentModeOption",paymentmode);
                    jdata.put("VPAId",upiid);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){


                    JSONArray jarr = sipresp.getJSONArray("sipdata");
                    JSONArray jarrMandate = sipresp.getJSONArray("mandatedata");
                    JSONObject jObj = jarrMandate.optJSONObject(0);
                    Mandatemodel selectedModel = new Mandatemodel(jObj);
                    String sipType = selectedModel.getSIPType();
                    //(selectedModel.getSIPType().equalsIgnoreCase(eSIPType.X_SIP.name)||
                    //selectedModel.getSIPType().equalsIgnoreCase(eSIPType.XSIP.name))? "X":"I";

                    JSONObject sipData = jarr.getJSONObject(0);

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    jdata.put(eMFJsonTag.TSRNO.name,sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name,sipData.getString("SIPSrNo"));
                    jdata.put(eMFJsonTag.SIPTYPE.name,sipType);
                    jdata.put(eMFJsonTag.SIPMANDATE.name,selectedModel.getMandateCode());
                    jdata.put("TodaysFirstOrder","Y");
                    // jdata.put(eMFJsonTag.SIPMANDATE.name,sipSpinnerBankList.getSelectedItem().toString());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();

            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(msgCode == eMessageCodeWealth.REGISTER_SIP.value && err.contains("html")){
                            displayWebView(err);
                        }
                        else{
                            if(err.contains("|")){
                                err = err.split("\\|")[1];
                            }
                            displayError(err);
                            if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                                homeActivity.onFragmentBack();
                            }
                        }
                    }
                    else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        } else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                            handleSaveInvestResp(jsonData);
                            amountEditText.setText("");
                        }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            handleSaveSIPData(jsonData);
                            homeActivity.onFragmentBack();
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    private void handleSaveSIPData(JSONObject jsonData) {

        displayError(jsonData.toString());
    }
    private void displayWebView(String err){
        homeActivity.FragmentTransaction(WebViewForMF.newInstance(err), R.id.container_body, false);
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private void displaData(JSONObject jsonData) {

        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedetailsdata");
            if(jschdetailDataArr.length() > 0) {
                jschdetailData = jschdetailDataArr.getJSONObject(0);
                displayschemedata();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    public  void makeTextViewResizable(final TextView tv, final int maxLine, final SpannableString expandText, final boolean viewMore) {
        try {

            if (tv.getTag() == null) {
                tv.setTag(tv.getText());
            }
            ViewTreeObserver vto = tv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {


                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (maxLine == 0) {
                        int lineEndIndex = tv.getLayout().getLineEnd(0);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        try {
                            int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                            String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                            tv.setText(text);
                            tv.setMovementMethod(LinkMovementMethod.getInstance());
                            tv.setText(
                                    addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                            viewMore), TextView.BufferType.SPANNABLE);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private  SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                      final int maxLine, final SpannableString spanableText, final boolean viewMore) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        try {


            String str = strSpanned.toString();

            if (str.contains(spanableText)) {


                ssb.setSpan(new MySpannable(false){
                    @Override
                    public void onClick(View widget) {
                        if (viewMore) {
                            tv.setLayoutParams(tv.getLayoutParams());
                            tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                            tv.invalidate();
                            String mystring=new String("Read Less");
                            SpannableString content = new SpannableString(mystring);
                            content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                            makeTextViewResizable(tv, -1,content, false);
                        } else {
                            tv.setLayoutParams(tv.getLayoutParams());
                            tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                            tv.invalidate();
                            String mystring=new String("Read More");
                            SpannableString content = new SpannableString(mystring);
                            content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                            makeTextViewResizable(tv, 1, content, true);
                        }
                    }
                }, str.indexOf(spanableText.toString()), str.indexOf(spanableText.toString()) + spanableText.length(), 0);

            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return ssb;


    }

    class AddUPIReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String selectedUPI;
        View DialogView;


        AddUPIReq(int mCode){
            this.msgCode = mCode;
        }
        AddUPIReq(int mCode,String upiCode,View dialogciew){
            this.msgCode = mCode;
            this.selectedUPI = upiCode;
            this.DialogView  = dialogciew;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if(msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value || msgCode == eMessageCodeWealth.BONDSAVEUPI_DETAILS.value){
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value){
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.IPO_UPI_DELETE.value || msgCode == eMessageCodeWealth.BONDDELETEUPI_DETAILS.value){
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value) {
                try{
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    displayError(e.getMessage());
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();

            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value){
                            handleUPIVerify(jsonData,DialogView);
                        }else {
                            displayError(err);
                        }
                        //UPI ID Added Successfully

                    } else {
                        if(msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value){
                            handleUPIVerify(jsonData,DialogView);
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    private void handleUPIVerify(JSONObject jsonData,View dialogview) {

        try {
            TextView UpiName = dialogview.findViewById(R.id.UPIName);
            String err = jsonData.getString("error");
            if(err.toLowerCase().contains("not available") ||
                    err.toLowerCase().contains("fail") ||
                    err.toLowerCase().contains("error") ||
                    err.toLowerCase().contains("issue")){
                UpiName.setVisibility(View.VISIBLE);
                dialogview.findViewById(R.id.okBtn).setVisibility(View.GONE);
                UpiName.setText("Invalid UPI ID.");
//                displayError("Please enter a valid UPI ID.");
            }else{
                String checkedMark =err;// + " " + "\u2713" ;
//                GlobalClass.showAlertDialog(checkedMark);
                UpiName.setVisibility(View.VISIBLE);
                UpiName.setText(checkedMark);
                dialogview.findViewById(R.id.okBtn).setVisibility(View.VISIBLE);

                //new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();
            }
            //IF SUCCESS
            //new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    private void displayschemedata(){

        try {
            if(efrequency == eMFFrequency.MONTHLY) {
                if(!jschdetailData.getString("CurrNAV").isEmpty()) {
                    navlinear.setVisibility(View.VISIBLE);
                    navvalue.setText(jschdetailData.getString("CurrNAV") + "(" + jschdetailData.getString("NAVDate") + ")");
                }else{
                    navlinear.setVisibility(View.GONE);
                    navvalue.setText("");
                }


                String multipleOf  = jschdetailData.getString("MSIPMultAmt");
                String minimumAmt = jschdetailData.getString("MSIPMinInstallmentAmt");

                _minimumAmt = StaticMethods.StringToDouble(minimumAmt);
                _multipleOf = StaticMethods.StringToDouble(multipleOf);

                multiply.setText(multipleOf);
                minimum_amount.setText(Formatter.DecimalLessIncludingComma(minimumAmt));

                schemeCode = jschdetailData.getString("SchemeCode");
                sipModel = new SIPModel();
                sipModel.minAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMinInstallmentAmt"));
                sipModel.maxAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMaxInstallmentAmt"));
                sipModel.multipleAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMultAmt"));

                sipModel.minPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMinInstallmentNumbers"));
                sipModel.maxPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMaximumInstallmentNumbers"));
                String sipdays = jschdetailData.getString("MSIPDates");
                sipModel.populateDate(sipdays);
                initDaySpinner();
                period.setText(sipModel.minPeriod+"");
                //period.setHint("");//("Range "+sipModel.minPeriod + " - " +sipModel.maxPeriod);
                exitloadvalue.setText(jschdetailData.getString("ExitLoad"));
                try {
                    String mystring=new String("Read More");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    makeTextViewResizable(exitloadvalue, 1, content, true);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private  JSONObject sipresp = null;
    private  void  handleSaveInvestResp(JSONObject jsonData){
        try{
            GlobalClass.log("SaveInvestResp : " + jsonData.toString());
            JSONArray jarrMandate = jsonData.getJSONArray("mandatedata");
            if(jarrMandate.length() == 1){
                sipresp = jsonData;
                new SIPReq(eMessageCodeWealth.REGISTER_SIP.value).execute();
            }else if(jarrMandate.length() > 0){
                //Go to mandate page for invest
                homeActivity.FragmentTransaction(SipMandateForInvest.newInstance(jsonData, eFormScr.SIP_FRAGMENT.value,
                        selectedConfirmationMsg, startdate.getText().toString().replace("/","-"), Double.parseDouble(amountEditText.getText().toString()),schemeCode,folioNumber,enddate.toString().replace("/","-"),period.toString()), R.id.container_body, false);
            } else{
                //Go to mandate page for new Mandate Creation
                /*String errMsg = "Your SIP mandate is not registered\n" +
                        "To register SIP mandate please call on 022- 6622 7312";
                displayError(errMsg);*/
                //First mandate creation
                homeActivity.FragmentTransaction(SipMandateFragment.newInstance(jsonData, DateUtil.getSameDayNextMonthDate(), 4, selectedSipAmt), R.id.container_body, true);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public boolean validateData(){
        try {
            if (sipModel ==null){
                homeActivity.showMsgDialog(StaticMessages.SOMETHING_WRONG);
                return false;
            }
            if (!validChk.isChecked()){
                int tempperiod = StaticMethods.StringToInt(period.getText().toString());
                if (tempperiod<sipModel.minPeriod|| tempperiod>sipModel.maxPeriod){
                    String ENTER_AMT = "Please enter SIP period between "+sipModel.minPeriod+" to "+sipModel.maxPeriod+" months.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
            }
            if (TextUtils.isEmpty(amountEditText.getText())){
                String ENTER_AMT = "Please enter amount to start a SIP.";
                homeActivity.showMsgDialog(ENTER_AMT);
                return false;
            }
            double amount = StaticMethods.StringToDouble(amountEditText.getText().toString());
            if (amount<_minimumAmt || (amount*100)%(_multipleOf*100) != 0){
                String MINIMUM_MULTIPLE_OF = "Entered amount should be minimum "+_minimumAmt+
                        ". And multiple of "+_multipleOf;
                homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                return false;
            }
            if(!validChk.isChecked()){
                if(TextUtils.isEmpty(period.getText().toString())) {
                    String ENTER_AMT = "Please enter SIP Period.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
                int per = StaticMethods.StringToInt(period.getText().toString());
                if (per<sipModel.minPeriod || per>sipModel.maxPeriod){
                    String MINIMUM_MULTIPLE_OF = "Entered period should be in between "+sipModel.minPeriod+
                            " and "+sipModel.maxPeriod;
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
            }
            /*
            if (amount>avlBalance.getAvailableBalance()){
                String NO_AVL_BAL = "Your current available balance is "+avlBalance.getAvailableBalance()+
                        ". Unable to process the Entered amount.";
                homeActivity.showMsgDialog(NO_AVL_BAL);
                return false;
            }*/
            if(!termcondChkBox.isChecked()){
                homeActivity.showMsgDialog(StaticMessages.CHECK_TERM_CONDITION);
                return false;
            }
            return true;
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return false;
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String _tempText = editable.toString();
            setEndDateString(_tempText);
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //setEndDateString(period.getText().toString());
            setStartDate();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void setStartDate(){
        try {
            int todayDAY = Integer.parseInt(DateUtil.getSameDayNextMonthDate().split("-")[0]);
            int selectedDay = Integer.parseInt(sipdayspinner.getSelectedItem().toString());

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = format.parse(DateUtil.getSameDayNextMonthDate());

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            if (selectedDay<todayDAY){
                cal.add(Calendar.MONTH,1);
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            }else {
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);
            }
            Date _sipStartDate = cal.getTime();
            String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
            startdate.setText(_sipStartDateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setEndDateString(String _tempText){
        try{
            int sellectedDay = Integer.parseInt(sipdayspinner.getSelectedItem().toString());

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH,1);
            cal.set(Calendar.DAY_OF_MONTH, sellectedDay);

            int dayDiff = DateUtil.compareDates(cal.getTime(),new Date());
            if(dayDiff<30){
                cal.add(Calendar.MONTH, 1);
            }
            Date _sipStartDate = cal.getTime();
            String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
            //startdate.setText(_sipStartDateStr);
            startdate.setText(DateUtil.getSameDayNextMonthDate().replace("-","/"));

            if (!TextUtils.isEmpty(_tempText)){
                int _tempMonth = StaticMethods.StringToInt(_tempText);
                if (sipModel != null &&_tempMonth>=sipModel.minPeriod && _tempMonth<=sipModel.maxPeriod){
                    //String _sipStartDateStr = sipdayspinner.getSelectedItem().toString();
                    cal = Calendar.getInstance();
                    cal.setTime(_sipStartDate);
                    cal.add(Calendar.MONTH, _tempMonth-1);
                    Date _sipEndDate = cal.getTime();
                    String _sipEndDateStr = SIP_SDF.format(_sipEndDate);
                    enddate.setText(_sipEndDateStr);

                }else {
                    enddate.setText("");
                }
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private SimpleDateFormat SIP_SDF = new SimpleDateFormat("dd/MMM/yyyy");
}
