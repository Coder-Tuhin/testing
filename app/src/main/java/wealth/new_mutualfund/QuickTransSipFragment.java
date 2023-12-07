package wealth.new_mutualfund;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eMFClientType;
import enums.eMFFrequency;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOperationMF;
import enums.eOptionMF;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.StaticMessages;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.menus.SipMandateForInvest;
import wealth.new_mutualfund.menus.SipMandateFragment;
import wealth.new_mutualfund.menus.WebViewForMF;
import wealth.new_mutualfund.menus.modelclass.Mandatemodel;
import wealth.new_mutualfund.menus.modelclass.SIPModel;

public class QuickTransSipFragment extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    private String schemeCode;
    private String schemeName;
    private String folioNumber = "";

    private JSONObject jschdetailData;
    private JSONObject jHoldingData;

    private eMFFrequency efrequency;
    private SIPModel sipModel;
    private boolean isGrowth = true;
    private String selectedConfirmationMsg = "";
    private double selectedSipAmt=0;

    public static QuickTransSipFragment newInstance(JSONObject jData, String fromScreen){
        QuickTransSipFragment f = new QuickTransSipFragment();
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
    @BindView(R.id.search_btn)
    ImageView search_btn;
    @BindView(R.id.scheme_name)
    EditText schemeNameEditText;
    @BindView(R.id.diySwitch)
    Switch diySwitch;
    AlertDialog alertDialog;
    private String paymentmode = "1";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        //if (mView == null){
        mView = inflater.inflate(R.layout.quick_transact_sip_fragment,container,false);
        ButterKnife.bind(this,mView);
        ininValue();
        validChk.setOnCheckedChangeListener(checklistener);
        period.addTextChangedListener(watcher);
        validChk.setChecked(true);
        mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                homeActivity.showMsgDialog("Terms & Conditions", R.string.mf_terms_conditions,false));
        mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        //}

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(schemeNameEditText.getText().toString().trim().length()>=3){
                    new SIPReq(eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value).execute();
                }else{
                    GlobalClass.showToast(getContext(),"Please enter atleast 3 characters for search");
                }
            }
        });
        diySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String switchText = isChecked? eOptionMF.DIVIDEND.name : eOptionMF.GROWTH.name;
                diySwitch.setText(switchText);
                isGrowth = !isChecked;
            }
        });
        schemeNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (count>3 && imm.isAcceptingText()){
                    new SIPReq(eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value).execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
            String formScr = args.getString(eMFJsonTag.FORMSCR.name, "");
            jHoldingData = new JSONObject(jsonStr);

            schemeCode = jHoldingData.optString("SchemeCode");
            schemeName = jHoldingData.optString("SchemeName");
            folioNumber = "";
            try {
                folioNumber = jHoldingData.optString("FolioNo");
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            //currentvalue.setText(jHoldingData.getString("CurrentAmt"));
            //totalunits.setText(jHoldingData.getString("Units"));
            initSpinner();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void initSpinner() {
        efrequency = eMFFrequency.MONTHLY;

        /*
        String type[] = {schemeName};
        ArrayAdapter spinnerAdapter = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_drop_down);
        spinnerAdapter.addAll(Arrays.asList(type));
        schemeNameSpinner.setAdapter(spinnerAdapter);
        */
        //schemeNametxtView.setText(schemeName);

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
            efrequency = isChecked? eMFFrequency.MONTHLY: eMFFrequency.QUARTERLY;
            frequency.setText(efrequency.name);
        });
        startasipbtn.setOnClickListener(view -> {
            if(validateData()){
                selectedConfirmationMsg = getConfirmationMsg();
                selectedSipAmt = Double.parseDouble(amountEditText.getText().toString());

                if(VenturaServerConnect.mfClientType == eMFClientType.MFD){
                    AlertDialog.Builder builder = new AlertDialog.Builder(homeActivity);
                    ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.payment_popup, viewGroup, false);
                    RadioGroup paymentradiogrp = dialogView.findViewById(R.id.paymentradiogrp);
                    paymentradiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            switch (i){
                                case R.id.netbanking:
                                    paymentmode = "1";

                                    break;
                                case R.id.upi:
                                    paymentmode = "3";

                                    break;
                            }
                        }

                    });
                    Button okBtn = dialogView.findViewById(R.id.okBtn);
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
                            alertDialog.dismiss();

                        }
                    });
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.show();

                }else {
                    paymentmode = "";
                    new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();

                }

                /*
                homeActivity.showMsgDialog("Message", getResources().getString( R.string.stampduty ), (dialogInterface, i) ->
                                new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute(),
                        (dialogInterface, i) -> dialogInterface.dismiss());*/
            }

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

        SIPReq(int mCode){
            this.msgCode = mCode;
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
                    jdata.put(eMFJsonTag.OPTION.name, eOptionMF.SIP.name);
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemeCode);
                    jdata.put("TransClientType",VenturaServerConnect.mfClientType);
                    jdata.put(eMFJsonTag.OPTION.name, eOperationMF.SIP.name);//Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
                    jdata.put(eMFJsonTag.FOLIONO.name, folioNumber);
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
                    jdata.put("PaymentModeOption",paymentmode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
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
                    // jdata.put(eMFJsonTag.SIPMANDATE.name,sipSpinnerBankList.getSelectedItem().toString());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value){
                    try {
                        JSONObject jdata = new JSONObject();
                        jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                        jdata.put(eMFJsonTag.KEYWORD.name, schemeNameEditText.getText().toString());
                        jdata.put(eMFJsonTag.OPTION.name, eOptionMF.SIP.name);
                        jdata.put(eMFJsonTag.SUBCATEGORY.name, "L");
                        jdata.put(eMFJsonTag.AMCCODE.name, "");

                        JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jdata);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception ex){
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
                        }else{
                            if (err.contains("|")) {
                                err = err.split("\\|")[1];
                            }
                            displayError(err);
                            if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                                homeActivity.onFragmentBack();
                            }
                        }
                    }else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        }else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                            handleSaveInvestResp(jsonData);
                            amountEditText.setText("");
                        }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            handleSaveSIPData(jsonData);
                            homeActivity.onFragmentBack();
                        }else if(msgCode == eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value){
                            handleSearchData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    private void handleSearchData(JSONObject jsonData){
        try{
            GlobalClass.log("SearchResp : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("sipschemedata");
            ArrayList<String> schemeNameList = new ArrayList<>();
            JSONArray selectedJsonArr = new JSONArray();
            String filterStr = isGrowth?"GROWTH":"DIVIDEND";
            if(jschdetailDataArr.length() > 0) {
                for(int i=0;i<jschdetailDataArr.length();i++){
                    JSONObject jrow = jschdetailDataArr.getJSONObject(i);
                    String schemeName = jrow.getString("SchemeName");
                    if (schemeName.toUpperCase().substring(schemeName.length()/2).contains(filterStr)){
                        schemeNameList.add(schemeName);
                        selectedJsonArr.put(jrow);
                    }
                }
                searchAlertForSchemes(schemeNameList, selectedJsonArr);
            }else{
                GlobalClass.showAlertDialog("No data found");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void searchAlertForSchemes(ArrayList<String> list, JSONArray jsonArray){
        String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Scheme Names");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_demo);

        ListView listView = dialog.findViewById(R.id.scheme_name_list);
        TextView closeAlert = dialog.findViewById(R.id.closeAlert);
        closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.dialog_demo_list_item, R.id.scheme_name_list_item, array);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), array[position], Toast.LENGTH_SHORT).show();
                schemeNameEditText.setText(array[position]);
                //new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value).execute();

                try {
                    JSONObject data = jsonArray.getJSONObject(position);
                    displaySearchData(data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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

    private void displaySearchData(JSONObject jsonData) {
        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            jschdetailData = jsonData;
            displayschemedata();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    private void displayschemedata(){

        try {
            //if(efrequency == eMFFrequency.MONTHLY) {
            navvalue.setText(jschdetailData.getString("CurrNAV") + "(" + jschdetailData.getString("NAVDate") + ")");
            exitloadvalue.setText(jschdetailData.getString("ExitLoad"));

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
            //}
        }catch (Exception ex){
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
                        selectedConfirmationMsg, startdate.getText().toString().replace("/","-"), selectedSipAmt), R.id.container_body, false);
            } else{
                //Go to mandate page for new Mandate Creation
                /*String errMsg = "Your SIP mandate is not registered\n" +
                        "To register SIP mandate please call on 022- 6622 7312";
                displayError(errMsg);*/
                //new sip creation
                homeActivity.FragmentTransaction(SipMandateFragment.newInstance(jsonData, DateUtil.getSameDayNextMonthDate(), 3, selectedSipAmt), R.id.container_body, true);
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
