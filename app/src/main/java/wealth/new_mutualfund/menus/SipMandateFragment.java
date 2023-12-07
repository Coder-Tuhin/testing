package wealth.new_mutualfund.menus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eACCType;
import enums.eMFAssetType;
import enums.eMFJsonTag;
import enums.eMaster;
import enums.eMessageCodeWealth;
import utils.DateUtil;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.QuickTransSipFragment;
import wealth.new_mutualfund.Structure.MFObjectHolder;
import wealth.new_mutualfund.newMF.MandateStepsFragment;

public class SipMandateFragment extends Fragment {

    private HomeActivity homeActivity;
    private static String PASSED_SIP_DATE = "passed_sip_date";
    private static String FROM_SCREEN_TAG = "from_screen_tag";
    private static String SELECTED_SIP_AMOUNT = "selected_sip_amount";
    private View mView;
    private ArrayList<JSONObject> bankNameList;
    private JSONObject selectedBankDetail;
    private String selectdSipDate;
    private int fromScreenTag=1;
    private double selectedSipAmount = 0;
    private List<Integer> mandateCodeList = new ArrayList<>();
    private JSONObject sipResp;
    private String mandateConfNo="";
    androidx.appcompat.app.AlertDialog alertDialog;


    public static SipMandateFragment newInstance(JSONObject jData, String sipDate, int fromScreenTag, double sipAmt){

        SipMandateFragment fragment = new SipMandateFragment();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(PASSED_SIP_DATE, sipDate);
        args.putInt(FROM_SCREEN_TAG, fromScreenTag);
        args.putDouble(SELECTED_SIP_AMOUNT, sipAmt);
        fragment.setArguments(args);
        return fragment;
    }

    /*
     * fromScreenTag
     * 1=MFSipMandate
     * 2=SipFragment
     * 3=QuickTransSipFragment
     * 4=SipMandateForInvest
     *
     * For tag 1, 2, 3 navigate to QuickTransactScreen
     * For tag 4 navigate to it's homeScreen
     * */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @BindView(R.id.validChk)
    CheckBox validChk;
    @BindView(R.id.endDateLinear)
    LinearLayout endDateLinear;
    @BindView(R.id.sipSpinnerBankList)
    Spinner sipSpinnerBankList;
    @BindView(R.id.enddate)
    EditText enddateshowhide;
    @BindView(R.id.accno)
    EditText accno;
    @BindView(R.id.sipSpinnerAccType)
    Spinner sipSpinnerAccType;

    @BindView(R.id.city_edittext)
    EditText city_bank;
    @BindView(R.id.bankName)
    EditText bankName;
    @BindView(R.id.ifsc)
    EditText ifscCode;
    @BindView(R.id.micr)
    EditText micr_code;
    @BindView(R.id.branchName)
    EditText branchName;
    @BindView(R.id.accholdername)
    EditText accHolderName;
    @BindView(R.id.second_holder)
    EditText second_holder;
    @BindView(R.id.mandateamt)
    EditText mandateAmt;
    @BindView(R.id.startdate)
    TextView startdate;

    @BindView(R.id.savebtnsip)
    TextView saveBtnSIP;

    @BindView(R.id.account_type_tv)
    TextView account_type_tv;

    @BindView(R.id.siptype)
    RadioGroup siptypeRadioGrp;

    private int myear;
    private int mmonth;
    private int mday;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.sip_mandate_screen,container,false); //sip_mandate_screen, sip_mandate_screen_new
            ButterKnife.bind(this,mView);
        }
        try {
            Bundle args = getArguments();
            String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
            sipResp = new JSONObject(jsonStr);
            selectdSipDate = args.getString(PASSED_SIP_DATE, DateUtil.getSameDayNextMonthDate_2());
            fromScreenTag = args.getInt(FROM_SCREEN_TAG, 1);
            selectedSipAmount = args.getDouble(SELECTED_SIP_AMOUNT, 0);
            initDatePickers();
            int amtint = (int) selectedSipAmount;
            mandateAmt.setText(""+amtint);
            //setFormattedDate(selectdSipDate.replace("-", "/"), "dd/MMM/yyyy");
            setFormattedDate(DateUtil.getSameDayNextMonthDate_2().replace("-", "/"), "dd/MMM/yyyy");
            mandateCodeList.clear();

            new SIPMandateReq(eMessageCodeWealth.MASTER_DATA.value).execute();
            validChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        endDateLinear.setVisibility(View.GONE);
                    } else {
                        endDateLinear.setVisibility(View.VISIBLE);
                    }
                }
            });
            saveBtnSIP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //eNACHConfirmation("123456780");
                    if(validateData()){

                        new SIPMandateReq(eMessageCodeWealth.SAVESIPMANDATE_DATA.value).execute();

                    }
                }
            });
            startdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return mView;
    }


    private DatePickerDialog datePickerDialog;
    private void initDatePickers() {
        final Calendar c = Calendar.getInstance();
        myear = c.get(Calendar.YEAR);
        mmonth = c.get(Calendar.MONTH);
        mday = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(homeActivity, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        setFormattedDate(day+"/"+(month+1)+"/"+year, "dd/MM/yyyy");
                    }
                }, myear, mmonth, mday);

        datePickerDialog.getDatePicker().setMinDate(getMillis(DateUtil.getSameDayNextMonthDate_2()));
        if (!selectdSipDate.equalsIgnoreCase(DateUtil.getSameDayNextMonthDate_2())){
            datePickerDialog.getDatePicker().setMaxDate(getMillis(selectdSipDate));
        }
    }

    private long getMillis(String mDate){
        SimpleDateFormat sdf = null;
        long dateTimeMillis = System.currentTimeMillis() - 1000;
        Date date = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sdf = new SimpleDateFormat("dd-MMM-yyyy");
                date = sdf.parse(mDate);
                dateTimeMillis = date.getTime();
            }
        }catch (Exception e){
            dateTimeMillis = System.currentTimeMillis() - 1000;
            e.printStackTrace();
        }
        return dateTimeMillis;
    }

    private void setFormattedDate(String dateStr, String format){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                Date date1 = formatter.parse(dateStr);
                formatter = new SimpleDateFormat("dd-MMM-yy");
                String finalDate = formatter.format(date1);
                startdate.setText(finalDate);
            } catch (ParseException e) {
                startdate.setText("");
                e.printStackTrace();
            }
        }
    }

    private boolean validateData(){
        boolean isOk = true;
        try {
            if (mandateAmt.getText().toString().equalsIgnoreCase("") || Integer.parseInt(mandateAmt.getText().toString())<=0 ){
                isOk = false;
                GlobalClass.showAlertDialog("Please enter a valid amount");
            }else if(Double.parseDouble(mandateAmt.getText().toString())<selectedSipAmount){
                isOk = false;
                GlobalClass.showAlertDialog("SIP mandate amount should not be less than the SIP amount");
            }else if(startdate.getText().toString().equalsIgnoreCase("")){
                isOk = false;
                GlobalClass.showAlertDialog("Please enter a valid amount");
            }
        }catch (Exception e){
            isOk=false;
            e.printStackTrace();
        }

        return isOk;
    }


    class SIPMandateReq extends AsyncTask<String, Void, String> {
        int msgCode;

        SIPMandateReq(int mCode){
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
                if(msgCode == eMessageCodeWealth.MASTER_DATA.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name,eMFAssetType.EQUITY.name);
                    jdata.put(eMFJsonTag.MASTERTYPE.name,eMaster.USERBANKDETAILS.name);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.MASTER_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.BANKDETAIL_DATA.value){

                    JSONObject selectedBank = bankNameList.get(sipSpinnerBankList.getSelectedItemPosition());
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.BANKID.name,selectedBank.getString("Value"));
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.BANKDETAIL_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.SAVESIPMANDATE_DATA.value){
                    //String sipType = siptypeRadioGrp.getCheckedRadioButtonId() == R.id.xsip?"X":"I";

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name,UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ACCOUNTNO.name,accno.getText().toString());
                    jdata.put(eMFJsonTag.PAN.name,(selectedBankDetail.isNull("PANNo")?"null":selectedBankDetail.getString("PANNo")));
                    //jdata.put(eMFJsonTag.PAN.name, "AMEPD4522A");
                    jdata.put(eMFJsonTag.ACCOUNTTYPE.name,selectedBankDetail.getInt("AccountType")+"");
                    jdata.put(eMFJsonTag.BANK_BRANCH_NAME.name,branchName.getText().toString());
                    jdata.put(eMFJsonTag.BANK_CITY.name,city_bank.getText().toString());
                    jdata.put(eMFJsonTag.BANK_NAME.name,bankName.getText().toString());
                    jdata.put(eMFJsonTag.MICR_CODE.name,micr_code.getText().toString());
                    jdata.put(eMFJsonTag.IFSC_CODE.name,ifscCode.getText().toString());
                    jdata.put(eMFJsonTag.ACCOUNT_HOLDER_NAME.name,accHolderName.getText().toString());
                    jdata.put(eMFJsonTag.MANDATE_AMT.name,mandateAmt.getText().toString());
                    jdata.put(eMFJsonTag.SIPTYPE.name, "E"); //sipType
                    jdata.put(eMFJsonTag.BANKID.name,selectedBankDetail.getString("BankId"));
                    jdata.put(eMFJsonTag.BANK_EXISTS.name,"1");
                    jdata.put(eMFJsonTag.STARTDATE.name,startdate.getText());
                    jdata.put(eMFJsonTag.ENDDATE.name, "");
                    jdata.put(eMFJsonTag.PDF_FILE_NAME.name,"");
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name, validChk.isChecked()?"true":"false"); //ValidUntilCancelled as true or false.
                    int i = 0;
                    ///*
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVESIPMANDATE_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                    //*/
                }if(msgCode == eMessageCodeWealth.GETSIPMANDATELIST_DATA.value){

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    String key = "SIPMandate";
                    JSONObject jsonData = MFObjectHolder.sipMandate.get(key);
                    if(jsonData == null) {
                        jsonData=VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GETSIPMANDATELIST_DATA.value,jdata);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.sipMandate.put(key, jsonData);
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){

                    JSONArray jarr = sipResp.getJSONArray("sipdata");
                    JSONObject sipData = jarr.getJSONObject(0);

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    jdata.put(eMFJsonTag.TSRNO.name, sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name, sipData.getString("SIPSrNo"));
                    jdata.put(eMFJsonTag.SIPTYPE.name, "E");
                    jdata.put(eMFJsonTag.SIPMANDATE.name, mandateConfNo);
                    jdata.put("TodaysFirstOrder","Y");


                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
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

                        // because in the error message success msg is coming. Like below
                        // {"error":"\"100|MANDATE REGISTRATION DONE SUCCESSFULLY|4799153\"","status":"0"}
                        if(msgCode == eMessageCodeWealth.SAVESIPMANDATE_DATA.value){
                            handleSaveSIPMandateData(err);
                        }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            if(err.contains("html")){

                                displayWebView(err);

                            }else if(err.contains("https")){
                                err = err.substring(4);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
                                startActivity(browserIntent);
                            }/*else if (err.contains("99|")) {
                                goToFundtransferPage();
                            }*/else {
                                displayError(err.replace("100|", ""));

                            }
                            if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
//                                homeActivity.onFragmentBack();
                            }
                        }else {
                            displayError(err);
                        }
                    }else {
                        if (msgCode == eMessageCodeWealth.MASTER_DATA.value) {
                            displayBankList(jsonData);
                        }else if(msgCode == eMessageCodeWealth.BANKDETAIL_DATA.value){
                            displaBankDetailData(jsonData);
                        }else if(msgCode == eMessageCodeWealth.SAVESIPMANDATE_DATA.value){
                            handleSaveSIPMandateData(jsonData.toString());
                        }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            displayError("Sip transaction has been successfully completed");
                            homeActivity.onFragmentBack();
                        }if(msgCode == eMessageCodeWealth.GETSIPMANDATELIST_DATA.value){
                            GlobalClass.log("SIP_MANDATE_LIST: "+jsonData.toString());
                            //handleSipMandateList(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void goToFundtransferPage() {
        //err = err.replaceAll("\n","");
        MFFundTransfer ls = MFFundTransfer.newInstance();
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
    }

    private void displayWebView(String err){
        homeActivity.FragmentTransaction(WebViewForMF.newInstance(err), R.id.container_body, false);
    }

    private JSONObject newMandateObject;
    private void handleSipMandateList(JSONObject jsonData){
        try {
            JSONArray jar = jsonData.getJSONArray("data");
            if (mandateCodeList.isEmpty()){
                for(int i=0; 0<jar.length();i++){
                    JSONObject job = jar.getJSONObject(i);
                    int mandateCode = job.getInt("MandateCode");
                    if (!mandateCodeList.contains(mandateCode)){
                        mandateCodeList.add(mandateCode);
                    }
                }
            }else {
                for(int i=0; 0<jar.length();i++){
                    JSONObject job = jar.getJSONObject(i);
                    int mandateCode = job.getInt("MandateCode");
                    if (!mandateCodeList.contains(mandateCode)){
                        newMandateObject = job;
                    }
                }

                if (newMandateObject != null && newMandateObject.has("MandateCode")){
                    new SIPMandateReq(eMessageCodeWealth.REGISTER_SIP.value).execute();
                }else {
                    displayError("Either mandate creation failed or Sip transaction failed");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private void displayBankList(JSONObject jsonData){
        try {
            sipSpinnerBankList.setOnItemSelectedListener(null);
            GlobalClass.log("GetBankdetail : " + jsonData.toString());
            JSONArray banklist = jsonData.getJSONArray("data");
            bankNameList = new ArrayList<>();
            String[] mStringArray = new String[banklist.length()];
            for (int i = 0; i < banklist.length(); i++) {
                JSONObject jdata = banklist.getJSONObject(i);
                mStringArray[i] = jdata.getString("Text");
                bankNameList.add(jdata);
            }
            setSpinnerData(sipSpinnerBankList, mStringArray);
            //new SIPMandateReq(eMessageCodeWealth.BANKDETAIL_DATA.value).execute();
            sipSpinnerBankList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    selectedBankDetail = null;
                    new SIPMandateReq(eMessageCodeWealth.BANKDETAIL_DATA.value).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void displaBankDetailData(JSONObject jsonData) {
        try {
            selectedBankDetail = jsonData;
            GlobalClass.log("GetBankdetail : " + jsonData.toString());
            //{"PANNo":null,"AccountNo":"038801504533","AccountType":"1",
            // "BankBranchName":"MINDSPACE BRANCH GR FLR EUREKA TOWERS OFF LINK RD MINDSPACE MALAD WEST",
            // "BankBranchCity":"","BankName":"ICICI BANK LTD","MICRCode":"400229050","IFSCCode":"ICIC0000388",
            // "BankAccountHolderName":"VIJAY SURESH DAROLE","MandateAmt":null,"SIPType":null,
            // "BankId":"038801504533","ID":0,"BankExists":"1","StartDOB":null,"EndDOB":null,
            // "TotalRecords":3,"PdfFilename":null,"UntilCancel":false,"EntryBy":null,
            // "ClientSessionKey":null,"ClientCode":null,"DeviceCode":null,"LastVisitedPage":null}
            accno.setText(jsonData.getString("AccountNo"));
            int accType = jsonData.getInt("AccountType");
            String accTypeStr = eACCType.SAVING.name;
            if(accType == eACCType.CURRENT.value){
                accTypeStr = eACCType.CURRENT.name;
            }
            else if(accType == eACCType.NRE.value){
                accTypeStr = eACCType.NRE.name;
            }
            else if(accType == eACCType.NRO.value){
                accTypeStr = eACCType.NRO.name;
            }
            String[] accArr = new String[]{accTypeStr};
            setSpinnerData(sipSpinnerAccType,accArr);
            account_type_tv.setText(accArr[0]);
            bankName.setText(jsonData.getString("BankName"));
            city_bank.setText(jsonData.getString("BankBranchCity"));
            branchName.setText(jsonData.getString("BankBranchName"));
            ifscCode.setText(jsonData.getString("IFSCCode"));
            micr_code.setText(jsonData.getString("MICRCode"));
            accHolderName.setText(jsonData.getString("BankAccountHolderName"));

            String secondHolderName = jsonData.getString("BankAccountsecondHolderName");
            if (secondHolderName.equalsIgnoreCase("null")){
                secondHolderName = "NA";
            }
            second_holder.setText(secondHolderName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void handleSaveSIPMandateData(String jsonData) {
        try {
            String msg = jsonData.replace("100|", "");
            msg = msg.substring(1, msg.length()-1);

            if (msg.toLowerCase().contains("successfully")){
                String confNo = msg.split("\\|")[1];
                eNACHConfirmationNew(confNo);
                mandateConfNo = confNo;
            }else{
                displayError(msg);
            }
        }catch (Exception e){
            mandateConfNo = "";
            e.printStackTrace();
        }

    }
    private void eNACHConfirmationNew(String confNo){
        homeActivity.FragmentTransaction(MandateStepsFragment.newInstance(confNo,fromScreenTag,sipResp,selectedSipAmount), R.id.container_body, false);


    }

    private void eNACHConfirmation(String confNo){
        try {
            String confMsg = homeActivity.getResources().getString(R.string.sip_mandate_success_msg) + " "+confNo;

            final Dialog dialog = new Dialog(getContext());
            dialog.setTitle("Scheme Names");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.nache_confirmation_alert_layout_mandate);
            TextView sip_conf_msg = dialog.findViewById(R.id.enache_process_title);
            sip_conf_msg.setVisibility(View.VISIBLE);
            sip_conf_msg.setText(confMsg);
            TextView closeAlert = dialog.findViewById(R.id.closeAlert);
            TextView ok_button = dialog.findViewById(R.id.ok_button);
            closeAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (fromScreenTag==1 || fromScreenTag==2 || fromScreenTag==3){
                        homeActivity.FragmentTransaction(QuickTransSipFragment.newInstance(new JSONObject(), "QuickTransact"), R.id.container_body, false);
                    }else if (fromScreenTag==4){
                        if (!mandateConfNo.isEmpty()){
                            new SIPMandateReq(eMessageCodeWealth.REGISTER_SIP.value).execute();
                        }
                    }
                }
            });
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSpinnerData(Spinner spinner, String[] data){
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity,R.layout.sip_spn_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data);
        spinner.setAdapter(categoryAdp);
    }
    private  void  clearAllData(){
        mandateAmt.setText("");
        startdate.setText("");
        enddateshowhide.setText("");
    }
}
