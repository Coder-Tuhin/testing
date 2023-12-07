package wealth.new_mutualfund.menus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.StaticMessages;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.bondstructure.StructBondAvailableBalance;
import wealth.new_mutualfund.MfUtils;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.menus.modelclass.Mandatemodel;
import wealth.new_mutualfund.newMF.OrderDetailsScreen;


public class SipMandateForInvest extends Fragment {
    private static final String fromScr = "from_screen";
    private static final String confMsgTag = "confirmation_message";
    private HomeActivity homeActivity;
    private View mView;
    private ArrayList<JSONObject> bankNameList;
    // private JSONObject selectedBankDetail;
    private JSONObject sipResp;
    private MandateItemAdapter mandateItemAdapter;
    private String selectedConfMsg;
    private static String PASSED_SIP_DATE = "passed_sip_date";
    private static String SELECTED_SIP_AMOUNT = "selected_sip_amount";
    private String selectdSipDate;
    private double selectedSipAmt = 0;
    private String schemeCode = "";
    private String folioNumber = "";
    private String enddate = "";
    private String period = "";
    StructBondAvailableBalance avlBalance;


    public SipMandateForInvest() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SipMandateForInvest newInstance(JSONObject jData,  int fromScreenTag, String confMsg, String selectedSipDate, double selSipAmt) {
        SipMandateForInvest fragment = new SipMandateForInvest();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(fromScr, String.valueOf(fromScreenTag));
        args.putString(confMsgTag, confMsg);
        args.putString(PASSED_SIP_DATE, selectedSipDate);
        args.putDouble(SELECTED_SIP_AMOUNT, selSipAmt);
        fragment.setArguments(args);
        return fragment;
    }
    public static SipMandateForInvest newInstance(JSONObject jData,  int fromScreenTag, String confMsg, String selectedSipDate, double selSipAmt,String schemecode,String folionumber,String enddate,String period ) {
        SipMandateForInvest fragment = new SipMandateForInvest();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(fromScr, String.valueOf(fromScreenTag));
        args.putString(confMsgTag, confMsg);
        args.putString(PASSED_SIP_DATE, selectedSipDate);
        args.putDouble(SELECTED_SIP_AMOUNT, selSipAmt);
        args.putString("schemecode",schemecode);
        args.putString("folionumber",folionumber);
        args.putString("enddate",enddate);
        args.putString("period",period);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isMandateEmpty(){
        return true;
    }

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


    /*@BindView(R.id.savebtnsip)
    TextView saveBtnSIP;*/

    @BindView(R.id.add_mandate_btn)
    LinearLayout add_mandate_btn;

    @BindView(R.id.register_a_sip)
    LinearLayout register_a_sip;

    @BindView(R.id.title_tv)
    TextView title_tv;



    @BindView(R.id.header_layout)
    LinearLayout header_layout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.approved_radio)
    RadioButton approved_radio;
    @BindView(R.id.other_radio)
    RadioButton other_radio;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.nomandate_ll)
    LinearLayout nomandate_ll;
    private AlertDialog alertDialog;
    androidx.appcompat.app.AlertDialog alertDialog1;
    String URL = "";


    private void visibilityGone(boolean isDataAvailable){
        if(isDataAvailable){
            title_tv.setVisibility(View.VISIBLE);
            header_layout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            add_mandate_btn.setVisibility(View.GONE);
            register_a_sip.setVisibility(View.GONE);
        }else{
            title_tv.setVisibility(View.GONE);
            header_layout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            add_mandate_btn.setVisibility(View.GONE);
            register_a_sip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.sip_mandate_for_invest,container,false);
            ButterKnife.bind(this,mView);
        }
        try {
            //new SIPMandateReq(eMessageCodeWealth.MASTER_DATA.value).execute();
            Bundle args = getArguments();
            String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
            selectedConfMsg = args.getString(confMsgTag, "");
            selectdSipDate = args.getString(PASSED_SIP_DATE, DateUtil.dateFormatter(new Date(),"dd-MMM-yyyy"));
            selectedSipAmt = args.getDouble(SELECTED_SIP_AMOUNT, 0);
            sipResp = new JSONObject(jsonStr);
            schemeCode = args.getString("schemecode");
            folioNumber = args.getString("folionumber");
            enddate = args.getString("enddate");
            period = args.getString("period");
            ArrayList<Mandatemodel> mList = getMandateList(sipResp);
            ArrayList<Mandatemodel> mListnew = new ArrayList<>();
            for (int i = 0; i<mList.size() ; i++){
                if(approved_radio.isChecked()){
                    Mandatemodel mm = mList.get(i);
                    if(mm.getMandateStatus().equalsIgnoreCase("APPROVED")){
                        mListnew.add(mm);

                    }
                }

            }
            if(mListnew.size() <= 0) {
                other_radio.setChecked(true);
            }
            mandateItemAdapter = new MandateItemAdapter(getMandateList(sipResp));
            recyclerView.setAdapter(mandateItemAdapter);
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    mandateItemAdapter = new MandateItemAdapter(getMandateList(sipResp));
                    recyclerView.setAdapter(mandateItemAdapter);
                }
            });
//            avlBalance = VenturaServerConnect.getAvailableBalance();


            /*validChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        endDateLinear.setVisibility(View.GONE);
                    } else {
                        endDateLinear.setVisibility(View.VISIBLE);
                    }
                }
            });
            validChk.setVisibility(View.GONE);*/
            register_a_sip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    homeActivity.FragmentTransaction(SipMandateFragment.newInstance(sipResp, DateUtil.getSameDayNextMonthDate_2(), 4, selectedSipAmt), R.id.container_body, false);
                }
            });
            add_mandate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeActivity.FragmentTransaction(SipMandateFragment.newInstance(sipResp, selectdSipDate, 4, selectedSipAmt), R.id.container_body, false);
                }
            });
            displayBankList(sipResp);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return mView;
    }


    public boolean validateData(){
        try {
            MandateItemAdapter mia = (MandateItemAdapter) recyclerView.getAdapter();
            if (mia.selectedModel==null){
                homeActivity.showMsgDialog(StaticMessages.SELECT_MANDATE);
                return false;
            }
            return true;
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class SIPMandateForInvestReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String TransNo = "";

        SIPMandateForInvestReq(int mCode){
            this.msgCode = mCode;
        }
        SIPMandateForInvestReq(int mCode,String TransNo){
            this.msgCode = mCode;
            this.TransNo = TransNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(msgCode != eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {
                GlobalClass.showProgressDialog("Requesting...");
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                    MandateItemAdapter mia = (MandateItemAdapter) recyclerView.getAdapter();

                    Mandatemodel selectedModel = mia.selectedModel;
                    String sipType = selectedModel.getSIPType().length()>0?(selectedModel.getSIPType().charAt(0)+"") : "";

                    //(mia.selectedModel.getSIPType().equalsIgnoreCase(eSIPType.X_SIP.name)||
                    //mia.selectedModel.getSIPType().equalsIgnoreCase(eSIPType.XSIP.name))? "X":"I";
                    JSONArray jarr = sipResp.getJSONArray("sipdata");
                    JSONObject sipData = jarr.getJSONObject(0);

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    jdata.put(eMFJsonTag.TSRNO.name,sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name,sipData.getString("SIPSrNo"));
                    jdata.put(eMFJsonTag.SIPTYPE.name,sipType);
                    jdata.put(eMFJsonTag.SIPMANDATE.name,selectedModel.getMandateCode());
                    jdata.put("TodaysFirstOrder","Y");
                    GlobalClass.MANDateCode = "";

                    // jdata.put(eMFJsonTag.SIPMANDATE.name,sipSpinnerBankList.getSelectedItem().toString());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }

                }else if(msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value){
                    MandateItemAdapter mia = (MandateItemAdapter) recyclerView.getAdapter();

                    Mandatemodel selectedModel = mia.selectedModel;
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);
                    jdata.put("TransNo",TransNo);
//                    jdata.put("TransNo","202208180001400831");
//                    jdata.put("MandateCode",selectedModel.getMandateCode());
                    jdata.put("MandateCode","");
                    GlobalClass.MANDateCode = "";
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, jdata);
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

                        if(msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value){

                            if (err.contains("PENDING")) {

                            }
                            else {
                                String[] arrStrings = err.split("[|]",0);
                                for (int i = 0; i < arrStrings.length; i++) {
                                    GlobalClass.log("position :" +i+ "String : "+arrStrings[i]);
                                    if(i== 3){
                                        URL = arrStrings[i];

                                    }
                                }

                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                                ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.mandate_eligible, viewGroup, false);
                                Button OK_btn = dialogView.findViewById(R.id.OK_btn);
                                TextView mandatetext2 = dialogView.findViewById(R.id.mandatetext2);
                                TextView mandatetext = dialogView.findViewById(R.id.mandatetext);
                                if(URL.length() > 0) {
                                    mandatetext.setText("Your payment for first installment has been successful vide transaction no. " + TransNo + ". Kindly proceed to authenticate your AutoPay Mandate.");
                                    OK_btn.setText("Proceed");
                                    mandatetext2.setVisibility(View.VISIBLE);
                                }else {
                                    mandatetext.setText("Your payment for first installment has been successful vide transaction no." + TransNo+".");
                                    mandatetext2.setVisibility(View.GONE);
                                    OK_btn.setText("OK");
                                }
                                OK_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog1.dismiss();
                                        GlobalClass.fragmentTransaction(new MutualFundMenuNew(), R.id.container_body, true, "");
                                    }
                                });
                                builder.setView(dialogView);
                                alertDialog1 = builder.create();
                                alertDialog1.show();

                                if(timer != null) {
                                    timer.cancel();
                                }
                                if(timer2 != null) {
                                    timer2.cancel();

                                }
                                if(timer3 != null) {
                                    timer3.cancel();

                                }
                                if(alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                            }

                        }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value && err.contains("html")){
                            displayWebView(err);
                        }else if(msgCode == eMessageCodeWealth.REGISTER_SIP.value && err.contains("UPI|")){
                            CheckPaymentstatus(err.substring(4));

                        }else if(err.contains("https")){
                            err = err.substring(4);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
                            startActivity(browserIntent);
                        }
                        else{
                            displayError(err.replace("100|",""));
                            if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            }
                        }
                    }
                   /* else {
                        if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            handleSaveSIPData(jsonData);
                            homeActivity.onFragmentBack();
                        }
                    }*/
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    CountDownTimer timer;
    CountDownTimer timer2;
    CountDownTimer timer3;

    private void CheckPaymentstatus(String substring) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.timerpopup, viewGroup, false);
        TextView timetext = dialogView.findViewById(R.id.timetext);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        timer = new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {

                Date date = new Date(millisUntilFinished);
// formattter
                SimpleDateFormat formatter= new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = formatter.format(date );
                timetext.setText(formatted);

            }

            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
        timer2 = new CountDownTimer(300000, 10000) {

            public void onTick(long millisUntilFinished) {

                new GetTaskFirst(substring).execute();


            }

            public void onFinish() {

            }
        }.start();
    }
    private void CheckPaymentStatusForNetBanking(String substring){
        timer3 = new CountDownTimer(300000, 10000) {

            public void onTick(long millisUntilFinished) {


                new SIPMandateForInvestReq(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, substring).execute();

            }

            public void onFinish() {

            }
        }.start();

    }
    class GetTaskFirst extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialog;
        String upitransId = "";


        public GetTaskFirst(String substring) {
            upitransId = substring;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if(UserSession.getClientResponse().isNeedAccordLogin() || !VenturaServerConnect.accordSessionCheck("FundTransfer")){
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    if(clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("") && VenturaServerConnect.connectToWealthServer(true)) {
                        return "";
                    }
                    return clientLoginResponse.charResMsg.getValue();
                }else{
                    if(VenturaServerConnect.connectToWealthServer(true)) {
                        return "";
                    }
                }
            } catch (Exception ie) {
                VenturaException.Print(ie);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if(result.equalsIgnoreCase("")) {
                    new SIPMandateForInvestReq(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, upitransId).execute();


                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }


    private void displayWebView(String err){
//        CheckPaymentStatusForNetBanking(GlobalClass.TransNo);

        homeActivity.FragmentTransaction(WebViewForMF.newInstance(err), R.id.container_body, false);
    }
    private void displayError(String err){
        if(err.contains("html")){
            CheckPaymentstatus(GlobalClass.TransNo);
            displayWebView(err);

        }else if(err.contains("https")){
            err = err.substring(4);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
            startActivity(browserIntent);
        }else if (err.contains("99|")) {
            goToFundtransferPage();
        }else {
            GlobalClass.showAlertDialog(err);

        }
//        GlobalClass.showAlertDialog(err);
    }

    private void displayBankList(JSONObject jsonData){
        try {
            // sipSpinnerBankList.setOnItemSelectedListener(null);
            JSONArray banklist = jsonData.getJSONArray("mandatedata");
            bankNameList = new ArrayList<>();
            String[] mStringArray = new String[banklist.length()];
            for (int i = 0; i < banklist.length(); i++) {
                JSONObject jdata = banklist.getJSONObject(i);
                mStringArray[i] = jdata.getString("MandateCode");
                bankNameList.add(jdata);
            }
            //    setSpinnerData(sipSpinnerBankList,mStringArray);
            //   displaBankDetailData(bankNameList.get(sipSpinnerBankList.getSelectedItemPosition()));
//            sipSpinnerBankList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                    // your code here
//                    if(bankNameList.size() >= position ){
//                        displaBankDetailData(bankNameList.get(position));
//                    }
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parentView) {
//                    // your code here
//                }
//            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void displaBankDetailData(JSONObject jsonData) {
        try {
            //  selectedBankDetail = jsonData;
            GlobalClass.log("GetBankdetail : " + jsonData.toString());
            //{"PANNo":null,"AccountNo":"038801504533","AccountType":"1",
            // "BankBranchName":"MINDSPACE BRANCH GR FLR EUREKA TOWERS OFF LINK RD MINDSPACE MALAD WEST",
            // "BankBranchCity":"","BankName":"ICICI BANK LTD","MICRCode":"400229050","IFSCCode":"ICIC0000388",
            // "BankAccountHolderName":"VIJAY SURESH DAROLE","MandateAmt":null,"SIPType":null,
            // "BankId":"038801504533","ID":0,"BankExists":"1","StartDOB":null,"EndDOB":null,
            // "TotalRecords":3,"PdfFilename":null,"UntilCancel":false,"EntryBy":null,
            // "ClientSessionKey":null,"ClientCode":null,"DeviceCode":null,"LastVisitedPage":null}

            /*
            "SIPType":"X-SIP",
            "MandateCode":"2737854",
            "MandateAmt":"10000",
            "BankName":"ICICI BANK LTD",
            "AccountNo":"119601535487",
            "SIPMandateStartDate":"29 May 2019",
            "SIPMandateEndDate":"Until cancelled",
            "AvailableAmt":"10000",
            "TransactionAmt":"1000.00"
            */
            //  accno.setText(jsonData.getString("AccountNo"));
            /*
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
            */
            try {
                //  bankName.setText(jsonData.getString("BankName"));
            }
            catch (Exception ex){}
            /*
            try {
                city_bank.setText(jsonData.getString("BankBranchCity"));
            }
            catch (Exception xe){}
            try {
                branchName.setText(jsonData.getString("BankBranchName"));
            }
            catch (Exception ex){}
            try {
                ifscCode.setText(jsonData.getString("IFSCCode"));
            }
            catch (Exception ex){}
            try {
                micr_code.setText(jsonData.getString("MICRCode"));
            }
            catch (Exception ex){}
            try {
                accHolderName.setText(jsonData.getString("BankAccountHolderName"));
            }
            catch (Exception ex){}*/
            try {
                //   startdate.setText(jsonData.getString("SIPMandateStartDate"));
            }
            catch (Exception ex){}
            try {
                //  enddateshowhide.setText(jsonData.getString("SIPMandateEndDate"));
            }
            catch (Exception ex){}
            try {
                //  mandateAmt.setText(jsonData.getString("MandateAmt"));
            }
            catch (Exception ex){}
            try {
                String sipType = jsonData.getString("SIPType");
                //if(sipType.equalsIgnoreCase("X-SIP") || sipType.equalsIgnoreCase(eSIPType.XSIP.name)){
                //siptypeRadioGrp.check(R.id.xsip);
                //   siptype.setText(sipType);
                /*}
                else{
                    //siptypeRadioGrp.check(R.id.isip);
                    siptype.setText(sipType);
                }*/
            }
            catch (Exception ex){}
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void handleSaveSIPData(JSONObject jsonData) {
//        try{
//            JSONArray jarr = sipResp.getJSONArray("sipdata");
//            if(jarr.length() > 0){
//                JSONObject sipData = jarr.getJSONObject(0);
//                String msg = "Transaction No: "+sipData.getString("TSrNo")+" SIP Serial No: "+sipData.getString("SIPSrNo")+
//                        " SchemeName: "+sipData.getString("SchemeName") + " Frequency: "+sipData.getString("Frequency")+
//                        " Period: "+sipData.getString("Period")+" PurchaseAmount: "+sipData.getString("PurchaseAmount")+
//                        " StartDate: "+sipData.getString("StartDate");
//                displayError(msg);
//            }
//        }catch (Exception e){
//            VenturaException.Print(e);
//        }
        displayError(jsonData.toString());
    }

    private void setSpinnerData(Spinner spinner, String[] data){
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity,R.layout.custom_spinner_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data);
        spinner.setAdapter(categoryAdp);
    }

    public class MandateItemAdapter extends RecyclerView.Adapter<MandateItemAdapter.MyViewHolder> {
        private int selectedIndex =-1;
        private LayoutInflater inflater;
        private ArrayList<Mandatemodel> mList;
        private Mandatemodel selectedModel;

//        public Mandatemodel getSelectedModel() {
//            if (selectedIndex>=0 && mList!=null){
//                selectedModel = mList.get(selectedIndex);
//            }
//            return selectedModel;
//        }

        MandateItemAdapter(ArrayList<Mandatemodel> mList) {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            ArrayList<Mandatemodel> mListnew = new ArrayList<>();
            for (int i = 0; i<mList.size() ; i++){
                if(approved_radio.isChecked()){
                    Mandatemodel mm = mList.get(i);
                    if(mm.getMandateStatus().equalsIgnoreCase("APPROVED")){
                        mListnew.add(mm);

                    }
                }else {
                    Mandatemodel mm = mList.get(i);
                    if(!mm.getMandateStatus().equalsIgnoreCase("APPROVED")){
                        mListnew.add(mm);
                    }

                }
            }
            if(mListnew.size() <= 0 ){
                nomandate_ll.setVisibility(View.VISIBLE);

            }else {
                nomandate_ll.setVisibility(View.GONE);
            }
            this.mList = mListnew;
        }


        @Override
        public MandateItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.mandate_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                Mandatemodel mm = mList.get(position);
                holder.bankName.setText(mm.getBankName());
                holder.accountNo.setText(mm.getAccountNo());
                holder.mandateAmt.setText(Formatter.DecimalLessIncludingComma(mm.getMandateAmt()));
                holder.avlAmt.setText(Formatter.DecimalLessIncludingComma(mm.getAvailableAmt()));
                /*if (selectedIndex>=0 && position == selectedIndex){
                    holder.radioBtn.setChecked(true);
                }else {
                    holder.radioBtn.setChecked(false);
                }*/
                holder.radioBtn.setChecked(false);
                double availableAmt = Double.parseDouble(mm.getAvailableAmt().replace(",",""));
                if(availableAmt>0){
                    holder.avlAmt.setTextColor(homeActivity.getResources().getColor(R.color.green1));
                }else {
                    holder.avlAmt.setTextColor(homeActivity.getResources().getColor(R.color.red));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
            //return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.bankName)
            TextView bankName;
            @BindView(R.id.accountNo)
            TextView accountNo;
            @BindView(R.id.mandateAmt)
            TextView mandateAmt;
            @BindView(R.id.avlAmt)
            TextView avlAmt;


            @BindView(R.id.radioBtn)
            RadioButton radioBtn;

            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(view -> {
                    //selectedIndex = getAdapterPosition();
                    //selectedModel = mList.get(selectedIndex);
                    //notifyDataSetChanged();
                });
                radioBtn.setOnCheckedChangeListener((compoundButton, b) -> {
                    selectedIndex = getAdapterPosition();
                    selectedModel = mList.get(selectedIndex);
                    Mandatemodel mm = mList.get(selectedIndex);
                    if(b){
//                        radioBtn.setChecked(false);

                        new SIPMandateForInvestReq(eMessageCodeWealth.REGISTER_SIP.value).execute();




                    }
                });
            }
        }
    }
    private void goToFundtransferPage() {
        //err = err.replaceAll("\n","");
        MFFundTransfer ls = MFFundTransfer.newInstance();
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
    }

    private AlertDialog msgDialog;
    private void showConfirmationDialog(Mandatemodel selectedModel){
        AlertDialog.Builder builder = new AlertDialog.Builder(homeActivity);
        builder.setIcon(R.drawable.ventura_icon);
        builder.setTitle("Confirmation");
        builder.setMessage("Your trading balance is insufficient. Would you like to proced with your first purchase from trading account? Click on Yes to transfer funds.");
        builder.setCancelable(false);
        builder.setPositiveButton("NO", (dialog, which) -> {

            mandateItemAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("YES", (dialog, which) -> {
            if(VenturaServerConnect.mfClientType == eMFClientType.MFD){
                new SIPMandateForInvestReq(eMessageCodeWealth.REGISTER_SIP.value).execute();

            }else {

                new SIPMandateForInvestReq(eMessageCodeWealth.REGISTER_SIP.value).execute();

            }
            mandateItemAdapter.notifyDataSetChanged();
        });

        msgDialog = builder.create();
        msgDialog.show();
        msgDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ScreenColor.VENTURA);
        msgDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ScreenColor.VENTURA);
    }

    private ArrayList<Mandatemodel> getMandateList(JSONObject object){
        ArrayList<Mandatemodel> mList = new ArrayList<>();
        try{
            JSONArray jArr = object.optJSONArray(MfUtils.getString(R.string.mandatedata));
            for (int i =0 ;i<jArr.length();i++){
                JSONObject jObj = jArr.optJSONObject(i);
                mList.add(new Mandatemodel(jObj));
            }
            visibilityGone(mList.size()>0);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return mList;
    }

}