package wealth.new_mutualfund.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import wealth.new_mutualfund.menus.modelclass.SIPModel;

public class StpFragment extends Fragment {

    private HomeActivity homeActivity;
    private View mView;

    String schemeCode;
    String schemeName;
    String folioNumber = "";

    JSONObject jschdetailData;
    JSONObject jHoldingData;

    eMFFrequency efrequency;
    SIPModel sipModel;
    ArrayList<JSONObject> searchData;

    public static StpFragment newInstance(JSONObject jData, String fromScreen){
        StpFragment f = new StpFragment();
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

    @BindView(R.id.schemeName)
    TextView schemeNametxtView;

    @BindView(R.id.folioLayout)
    LinearLayout folioLayout;

    @BindView(R.id.folioSpinner)
    Spinner folioSpinner;
    @BindView(R.id.stptransfertoSpinner)
    Spinner stptransfertoSpinner;

    @BindView(R.id.startastpbtn)
    TextView startastpbtn;

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


    @BindView(R.id.searchedittext)
    EditText searchedittext;
    @BindView(R.id.searchbtn)
    LinearLayout searchbtnbtn;

    @BindView(R.id.startdate)
    TextView startdate;
    @BindView(R.id.period)
    EditText period;
    @BindView(R.id.enddate)
    TextView enddate;
    @BindView(R.id.stpdaySpinner)
    Spinner stpdaySpinner;
    @BindView(R.id.amountEditText)
    EditText amountEditText;
    @BindView(R.id.termcondChkBox)
    CheckBox termcondChkBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){

            mView = inflater.inflate(R.layout.stp_screen,container,false);
            ButterKnife.bind(this,mView);
            ininValue();
            period.addTextChangedListener(watcher);
            //new STPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
            mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                    homeActivity.showMsgDialog("Terms & Conditions",R.string.mf_terms_conditions,false));
            mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        }
        new STPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
        return mView;
    }
    private void ininValue(){
        try {
            Bundle args = getArguments();
            String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
            String formScr = args.getString(eMFJsonTag.FORMSCR.name, "");
            jHoldingData = new JSONObject(jsonStr);

            schemeCode = jHoldingData.getString("SchemeCode");
            schemeName = jHoldingData.getString("SchemeName");
            folioNumber = jHoldingData.getString("FolioNo");

            //currentvalue.setText(jHoldingData.getString("CurrentAmt"));
            //totalunits.setText(jHoldingData.getString("Units"));
            initSpinner();
        }
        catch (Exception ex){
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
        frequency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                efrequency = isChecked?eMFFrequency.MONTHLY:eMFFrequency.QUARTERLY;
                frequency.setText(efrequency.name);
            }
        });
        startastpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(validateData()){
                if(validateData()){
                    homeActivity.showMsgDialog("Start a STP", getConfirmationMsg(), (dialogInterface, i) ->
                                    new STPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute(),
                            (dialogInterface, i) -> dialogInterface.dismiss());
                }
                //}
            }
        });
        searchbtnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GlobalClass.showToast(getContext(),"Search Btn Click");
                if(searchedittext.getText().toString().trim().length()>=3){
                    new STPReq(eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value).execute();
                }
                else{
                    GlobalClass.showToast(getContext(),"Please enter search text atlease 3 character");
                }
            }
        });
    }

    private String getConfirmationMsg(){
        String _tempConfirmation = "Are you sure you want to start a STP of amount "+
                Formatter.TwoDecimalIncludingComma(amountEditText.getText().toString())+" for "+period.getText().toString() +" months?";
        return _tempConfirmation;
    }

    private void initSearchSpinner(ArrayList<String> list){
        if(list != null && list.size()>0) {
            ArrayAdapter spinnerAdapterF = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
            spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            spinnerAdapterF.addAll(list);
            stptransfertoSpinner.setAdapter(spinnerAdapterF);
            stptransfertoSpinner.setVisibility(View.VISIBLE);
        }
        else{
            stptransfertoSpinner.setVisibility(View.GONE);
        }
    }
    private  void  initDaySpinner(){
        ArrayAdapter spinnerAdapterF = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
        spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
        spinnerAdapterF.addAll(sipModel.dateV);
        stpdaySpinner.setAdapter(spinnerAdapterF);
        stpdaySpinner.setOnItemSelectedListener(onItemSelected);
    }
    class STPReq extends AsyncTask<String, Void, String> {
        int msgCode;

        STPReq(int mCode){
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
                    jdata.put(eMFJsonTag.OPTION.name,eOptionMF.STP.name);
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }

                else if(msgCode == eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value){

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.KEYWORD.name,searchedittext.getText().toString());
                    jdata.put(eMFJsonTag.OPTION.name,eOptionMF.STP.name);
                    jdata.put(eMFJsonTag.SUBCATEGORY.name,"L");
                    jdata.put(eMFJsonTag.AMCCODE.name,jschdetailData.getString("AMC_Code"));

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){

                    JSONObject transfertO = searchData.get(stptransfertoSpinner.getSelectedItemPosition());

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemeCode);
                    jdata.put(eMFJsonTag.OPTION.name,eOperationMF.STP.name);//Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
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
                    jdata.put(eMFJsonTag.TOSCHEMECODE.name,transfertO.getString("SchemeCode"));// only used for STP,SWITCH
                    jdata.put(eMFJsonTag.TRANSFERAMT.name,amountEditText.getText());// only used for STP
                    jdata.put(eMFJsonTag.SWITCHAMT.name,"");// only used for SWITCH
                    jdata.put(eMFJsonTag.SWITCHUNITS.name,"");// only used for SWITCH
                    jdata.put(eMFJsonTag.REDEEMDATE.name,"");// only used for SPREADORDER
                    jdata.put(eMFJsonTag.SIPAMT.name,"");// only used for SIP
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name,"");//date only used for SIP T/F
                    jdata.put("OrderNo","");//date only used for SIP T/F
                    jdata.put("TransDate","");//date only used for SIP T/F
                    jdata.put("PaymentModeOption","");
                    jdata.put("VPAId","");
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);


                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,jdata);
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
                        String mainerr = jsonData.getString("error");
                        String err1 = "";
                        if(mainerr.contains("|")){
                            err1 = mainerr.split("\\|")[1];

                        }
                        if(err1.contains("placed successfully")){
                            String err2 = mainerr.split("\\|")[2];
                            ShowalertDialog(err1,err2);

                        }else {
                            displayError(err1);
                        }
                    }
                    else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        } else if(msgCode == eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value){
                            handleSearchData(jsonData);
                        }
                        else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                            handleSaveInvestResp(jsonData);
                            amountEditText.setText("");
                        }
                    }
                    if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStackImmediate();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    AlertDialog.Builder builder;

    public void ShowalertDialog(String data,String Url){
        builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(data)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                            homeActivity.startActivity(browserIntent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.show();
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private void handleSearchData(JSONObject jsonData){
        try{
            GlobalClass.log("SearchResp : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedata");
            ArrayList<String> schemeNameList = new ArrayList<>();
            searchData = new ArrayList<>();
            if(jschdetailDataArr.length() > 0) {
                for(int i=0;i<jschdetailDataArr.length();i++){
                    JSONObject jrow = jschdetailDataArr.getJSONObject(i);
                    searchData.add(jrow);
                    schemeNameList.add(jrow.getString("SchemeName"));
                }
                initSearchSpinner(schemeNameList);
            }
            else{
                GlobalClass.showAlertDialog("No data found");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }



    private double _minimumAmt = -1;
    private double _multipleOf = 0;

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
    private void displayschemedata(){

        try {
            if(efrequency == eMFFrequency.MONTHLY) {
                navvalue.setText(jschdetailData.getString("CurrNAV") + "(" + jschdetailData.getString("NAVDate") + ")");
                exitloadvalue.setText(jschdetailData.getString("ExitLoad"));

                String multipleOf  = jschdetailData.getString("MSIPMultAmt");
                String minimumAmt = jschdetailData.getString("MSIPMinInstallmentAmt");

                _minimumAmt = StaticMethods.getStringToDouble(minimumAmt);
                _multipleOf = StaticMethods.getStringToDouble(multipleOf);

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
                period.setText(sipModel.minPeriod);
                //period.setHint("Range "+sipModel.minPeriod + " - " +sipModel.maxPeriod);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private  void  handleSaveInvestResp(JSONObject jsonData){
        try{
            GlobalClass.log("SaveInvestResp : " + jsonData.toString());
            JSONArray jarr = jsonData.getJSONArray("sipdata");
            if(jarr.length() > 0){
                JSONObject sipData = jarr.getJSONObject(0);
                String msg = "Transaction No: "+sipData.getString("TSrNo")+" SIP Serial No: "+sipData.getString("SIPSrNo")+
                        " SchemeName: "+sipData.getString("SchemeName") + " Frequency: "+sipData.getString("Frequency")+
                        " Period: "+sipData.getString("Period")+" PurchaseAmount: "+sipData.getString("PurchaseAmount")+
                        " StartDate: "+sipData.getString("StartDate");
                displayError(msg);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean validateData(){
        try {
            if (sipModel ==null){
                homeActivity.showMsgDialog(StaticMessages.SOMETHING_WRONG);
                return false;
            }
            int tempperiod = StaticMethods.StringToInt(period.getText().toString());
            if (tempperiod<sipModel.minPeriod|| tempperiod>sipModel.maxPeriod){
                String ENTER_AMT = "Please enter STP period between "+sipModel.minPeriod+" to "+sipModel.maxPeriod+" months.";
                homeActivity.showMsgDialog(ENTER_AMT);
                return false;
            }

            if (TextUtils.isEmpty(amountEditText.getText())){
                String ENTER_AMT = "Please enter amount to start a STP.";
                homeActivity.showMsgDialog(ENTER_AMT);
                return false;
            }
            double amount = StaticMethods.getStringToDouble(amountEditText.getText().toString());
            if (amount<_minimumAmt && (amount*100)%(_multipleOf*100) != 0){
                String MINIMUM_MULTIPLE_OF = "Entered amount should be minimum "+_minimumAmt+
                        ". And multiple of "+_multipleOf;
                homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                return false;
            }

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
            setEndDateString(period.getText().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    private void setEndDateString(String _tempText){
        try{
            try{
                int sellectedDay = Integer.parseInt(stpdaySpinner.getSelectedItem().toString());
                //Date _sipStartDate = SIP_SDF.parse(_sipStartDateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, sellectedDay);

                int dayDiff = DateUtil.compareDates(cal.getTime(),new Date());
                if(dayDiff<30){
                    cal.add(Calendar.MONTH, 1);
                }
                Date _sipStartDate = cal.getTime();
                String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
                startdate.setText(_sipStartDateStr);

                if (!TextUtils.isEmpty(_tempText)){
                    int _tempMonth = StaticMethods.getStringToInt(_tempText);
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
        }catch (Exception e){
            VenturaException.Print(e);
        }

    }
    private SimpleDateFormat SIP_SDF = new SimpleDateFormat("dd/MMM/yyyy");
}
