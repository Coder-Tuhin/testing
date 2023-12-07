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

import android.os.CountDownTimer;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOperationMF;
import enums.eOptionMF;
import utils.Formatter;
import utils.GlobalClass;
import utils.MySpannable;
import utils.StaticMessages;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.newMF.OneTimeFragment;

public class RedeemptionFragment extends Fragment implements View.OnClickListener{

    private HomeActivity homeActivity;
    private View mView;
    private String schemeCode;
    private String schemeName;
    private String folioNumber = "";
    private JSONObject jschdetailData;
    private  JSONObject SchemeAvailableUnitsjobj;
    private JSONObject jHoldingData;
    private double _currentValue = 0;
    private double _noOfUnits = 0;
    String OnlieFlag = "";
    private String BankName = "",AccountNo = "";
    AlertDialog.Builder builder;
    androidx.appcompat.app.AlertDialog alertDialogforOtpVer;
    androidx.appcompat.app.AlertDialog otpsucccessalert;

    public static RedeemptionFragment newInstance(JSONObject jData, String fromScreen) {
        RedeemptionFragment f = new RedeemptionFragment();
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

    private void init() {
        try {
            Bundle args = getArguments();
            String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
            String formScr = args.getString(eMFJsonTag.FORMSCR.name, "");
            jHoldingData = new JSONObject(jsonStr);

            schemeCode = jHoldingData.getString("SchemeCode");
            schemeName = jHoldingData.getString("SchemeName");
            folioNumber = jHoldingData.getString("FolioNo");

            String currVal  = jHoldingData.getString("CurrentAmt");
            String units = jHoldingData.getString("Units");
            OnlieFlag = jHoldingData.optString("OnlineFlag");
            if(OnlieFlag.equalsIgnoreCase("online")){
                OnlieFlag = "MFI";
            }else {
                OnlieFlag = "MFD";
            }
            _currentValue = StaticMethods.getStringToDouble(currVal);
            _noOfUnits = StaticMethods.getStringToDouble(units);

            currentvalue.setText(Formatter.DecimalLessIncludingComma(currVal));
            totalunits.setText(_noOfUnits + "");
            avlunits.setText("");
            initSpinnerAndRadioGroup();
            setRedeemInputLayout();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void initSpinnerAndRadioGroup() {
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
        }else{
            String folioN[] = {folioNumber};
            ArrayAdapter spinnerAdapterF = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
            spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            spinnerAdapterF.addAll(Arrays.asList(folioN));
            folioSpinner.setAdapter(spinnerAdapterF);
        }

        if(VenturaServerConnect.mfClientType == eMFClientType.MFI){
            // unitamountRG.removeViewAt(1);
            amountRd.setVisibility(View.GONE);
        }
        unitamountRG.setOnCheckedChangeListener(unitAmountCheck);
        allpartialRG.setOnCheckedChangeListener(allRartialCheck);

        reddemnowbtn.setOnClickListener(view -> {
            if(validateData()){

                if(OnlieFlag.equalsIgnoreCase("MFI")){
                    new RedeemReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
                }else {
                    new RedeemReq(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value).execute();
                }


                /*homeActivity.showMsgDialog("Redeem Now", getConfirmationMsg(), (dialogInterface, i) ->
                        new RedeemReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute(), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });*/
            }
        });
    }

    private String getConfirmationMsg(){
        String _tempConfirmation ="";
        if (unitsRd.isChecked()){
            _tempConfirmation = "Are you sure you want to Redeem "+
                    (toRedeemEt.getText().toString())+" units?";
        }else {
            _tempConfirmation = "Are you sure you want to Redeem Rs. "+
                    Formatter.TwoDecimalIncludingComma(toRedeemEt.getText().toString())+"?";
        }
        return _tempConfirmation;
    }


    @BindView(R.id.schemeName)
    TextView schemeNametxtView;

    @BindView(R.id.folioLayout)
    LinearLayout folioLayout;

    @BindView(R.id.folioSpinner)
    Spinner folioSpinner;
//
//    @BindView(R.id.amountEditText)
//    EditText amountEditText;
//
//    @BindView(R.id.unitsedittext)
//    EditText unitsedittext;

    @BindView(R.id.termcondChkBox)
    CheckBox termcondChkBox;

    @BindView(R.id.redeemnowbtn)
    TextView reddemnowbtn;

    @BindView(R.id.navvalue)
    TextView navvalue;
    @BindView(R.id.exitloadvalue)
    TextView exitloadvalue;

    @BindView(R.id.currentvalue)
    TextView currentvalue;

    @BindView(R.id.totalunits)
    TextView totalunits;
    @BindView(R.id.avlunits)
    TextView avlunits;


    @BindView(R.id.unitamountRG)
    RadioGroup unitamountRG;
    @BindView(R.id.allpartialRG)
    RadioGroup allpartialRG;
    @BindView(R.id.unitsRd)
    RadioButton unitsRd;
    @BindView(R.id.amountRd)
    RadioButton amountRd;
    @BindView(R.id.allRd)
    RadioButton allRd;
    @BindView(R.id.partialRd)
    RadioButton partialRd;
    @BindView(R.id.toRedeem)
    TextView toRedeem;
    @BindView(R.id.toRedeemEt)
    EditText toRedeemEt;


    @BindView(R.id.bankdetails_ll)
    LinearLayout bankdetails_ll;
    @BindView(R.id.tv_Bank)
    TextView tv_Bank;



    private RadioGroup.OnCheckedChangeListener unitAmountCheck = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            setRedeemInputLayout();
        }
    };

    private void setRedeemInputLayout(){
        String _redeemTitle = getResources().getString(R.string.Units_to_redeem);
        String _redeemHint = "Enter Units";
        toRedeemEt.setText("");
        if (amountRd.isChecked()){
            _redeemTitle = getResources().getString(R.string.Amount_to_redeem);
            _redeemHint = "Enter Amount";
        }
        toRedeem.setText(_redeemTitle);
        toRedeemEt.setHint(_redeemHint);
        setRedeemInputValues();
    }


    private void setRedeemInputValues(){
        double _tempRedeemValue = _noOfUnits;
        if (allRd.isChecked()){
            if (amountRd.isChecked()){
                _tempRedeemValue = _currentValue;
            }else {
                _tempRedeemValue = _noOfUnits;
            }
            toRedeemEt.setText(String.valueOf(_tempRedeemValue));
            toRedeemEt.setEnabled(false);
        }else {
            toRedeemEt.setText("");
            toRedeemEt.setEnabled(true);
        }
        if(_tempRedeemValue == 0){
            toRedeemEt.setEnabled(false);
        }
    }

    private RadioGroup.OnCheckedChangeListener allRartialCheck = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            setRedeemInputValues();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.redemption_screen,container,false);
            ButterKnife.bind(this, mView);
            mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                    homeActivity.showMsgDialog("Terms & Conditions",R.string.mf_terms_conditions,false));
            mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        }
        init();
        new RedeemReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
        return mView;
    }

    @Override
    public void onClick(View view) {
/*
        switch_icon (view.getId()) {
            case R.id.redeem_btn:
                break;
            case R.id.cancel_btn:
                GlobalClass.fragmentTransaction(new MFHoldingReports(), R.id.container_body,true,"");
                break;
            default:
                break;
        }
*/
    }

    class RedeemReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String AuthSrNo = "",otp = "";

        RedeemReq(int mCode){
            this.msgCode = mCode;
        }
        RedeemReq(int mCode,String Authsrno,String OTP) {
            this.msgCode = mCode;
            this.AuthSrNo = Authsrno;
            this.otp = OTP;
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
                    jdata.put(eMFJsonTag.OPTION.name,eOptionMF.REDEEM.name);
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    jdata.put("TransClientType",OnlieFlag);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){
                    int redeemOpt = 0;
                    if(unitamountRG.getCheckedRadioButtonId() == R.id.unitsRd ){
                        redeemOpt = (allpartialRG.getCheckedRadioButtonId() == R.id.allRd )?1:2;
                    } else {
                        redeemOpt = (allpartialRG.getCheckedRadioButtonId() == R.id.allRd )?3:4;
                    }

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name,UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemeCode);
                    jdata.put(eMFJsonTag.OPTION.name,eOperationMF.REDEEM.name);//Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    jdata.put("TransClientType",OnlieFlag);
                    jdata.put(eMFJsonTag.INVESTAMT.name,"");// only used for Purchase,TOpUP
                    jdata.put(eMFJsonTag.REDEEMOPT.name,redeemOpt+"");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount
                    jdata.put(eMFJsonTag.REDEEM.name,toRedeemEt.getText().toString().trim());//Units | Amount
                    jdata.put(eMFJsonTag.LIQUIDREDEEM.name,"B");//B - Bank,L - Ledger
                    jdata.put(eMFJsonTag.WITHDRAWAL.name,"");//amount only used for SWP
                    jdata.put(eMFJsonTag.FREQUENCY.name,"");//Monthly | Quarterly//amount only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.EXECUTIONDAY.name,"");//amount only used for SWP
                    jdata.put(eMFJsonTag.STARTDATE.name,"");//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.ENDDATE.name,"");//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.PERIOD.name,"");// only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.TOSCHEMECODE.name,"");// only used for STP,SWITCH
                    jdata.put(eMFJsonTag.TRANSFERAMT.name,"");// only used for STP
                    jdata.put(eMFJsonTag.SWITCHAMT.name,"");// only used for SWITCH
                    jdata.put(eMFJsonTag.SWITCHUNITS.name,"");// only used for SWITCH
                    jdata.put(eMFJsonTag.REDEEMDATE.name,"");// only used for SPREADORDER
                    jdata.put(eMFJsonTag.SIPAMT.name,"");// only used for SIPadministrator
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name,"");//date only used for SIP T/F
                    jdata.put(eMFJsonTag.ORDERNO.name,"");
                    jdata.put(eMFJsonTag.TRANSADATE.name,"");
                    jdata.put("PaymentModeOption","");
                    jdata.put("AccountNo",AccountNo);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if (msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("AuthSrNo", AuthSrNo);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if (msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value) {
                    int redeemOpt = 0;
                    if(unitamountRG.getCheckedRadioButtonId() == R.id.unitsRd ){
                        redeemOpt = (allpartialRG.getCheckedRadioButtonId() == R.id.allRd )?1:2;
                    } else {
                        redeemOpt = (allpartialRG.getCheckedRadioButtonId() == R.id.allRd )?3:4;
                    }

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("SchemeCode", schemeCode);
                    jdata.put("TransType", "3");
                    jdata.put("TransClientType",OnlieFlag);
                    jdata.put("InvAmount", "");// only used for Purchase,TOpUP
                    jdata.put("FolioNo", folioNumber);
                    jdata.put(eMFJsonTag.REDEEMOPT.name, redeemOpt+"");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount
                    jdata.put("Redeem", toRedeemEt.getText().toString().trim());//Units | Amount
                    jdata.put("ToSchemeCode", "");//Units | Amount
                    jdata.put("SwitchAmount", "");//Units | Amount
                    jdata.put("SwitchUnits", "");//Units | Amount
                    jdata.put("SIPAmount", "");//Units | Amount
                    jdata.put("StartDate", "");//Units | Amount
                    jdata.put("ValidUntilCancelled", "");//Units | Amount
                    jdata.put("EndDate", "");//Units | Amount
                    jdata.put("Frequency", "");
                    jdata.put("AccountNo", AccountNo);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if (msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("AuthSrNo", AuthSrNo);
                    jdata.put("OTP", otp);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value, jdata);
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
                        //String err = "";
                        if(msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value){
                            String[] separated = mainerr.split("\\|");
                            OTPtext = separated[0]+"|"+separated[1];
                            showOtpVerificationPopup("");
                        } else if(msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value){
                            StartCountdown(otptimertext,rtaotp_et);
                        } else if(msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value){
                            if(mainerr.contains("Invalid")){
                                //showOtpVerificationPopup(mainerr);
                                updateAlertDialog(mainerr);
                            }else {
                                if(alertDialogforOtpVer.isShowing()){
                                    alertDialogforOtpVer.dismiss();
                                    showOtpSuccessAlert(mainerr);
                                }
                            }
                        } else {
                            String finalMsg;
                            if(mainerr.contains("|")){
                                finalMsg = mainerr.split("\\|")[1];
                            }else{
                                finalMsg = mainerr;
                            }
                            GlobalClass.showAlertDialog(finalMsg);
                            /*
                            if (mainerr.contains("100")) {
                                GlobalClass.showAlertDialog("Your redemption order has been placed successfully and the funds will be credited to your registered bank a/c.");
                            } else {
                                GlobalClass.showAlertDialog(mainerr);
                            }*/
                        }
                    }
                    else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        } else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                            handleSaveInvestResp(jsonData);
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
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    String OTPtext = "";

    private void showOtpSuccessAlert(String msg) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.otpsuccessalert, viewGroup, false);
        TextView otpsuccesstext = dialogView.findViewById(R.id.otpsuccesstext);
        TextView ok_btn = dialogView.findViewById(R.id.ok_btn);
        otpsuccesstext.setText(msg.substring(4));
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                homeActivity.showMsgDialog("Redeem Now", getConfirmationMsg(), (dialogInterface, i) ->
                    new RedeemReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute(), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                otpsucccessalert.dismiss();*/

                new RedeemReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
                otpsucccessalert.dismiss();
            }
        });

        builder.setView(dialogView);
        otpsucccessalert = builder.create();
        otpsucccessalert.show();

    }
    TextView otptimertext;
    EditText rtaotp_et;

    private void updateAlertDialog(String err){
        TextInputLayout TIL_Otp = alertDialogforOtpVer.getWindow().findViewById(R.id.TIL_Otp);
        TIL_Otp.setError(err);
    }

    private void showOtpVerificationPopup(String err) {
        getActivity().setTheme(R.style.AppThemenew);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.rtaotpverificationpopup, viewGroup, false);
        TextView otp_authentication_text = dialogView.findViewById(R.id.otp_authentication_text);
        TextView ok_btn = dialogView.findViewById(R.id.ok_btn);
        ImageView img_cross = dialogView.findViewById(R.id.img_cross);
        TextView resend_btn = dialogView.findViewById(R.id.resend_btn);
        TextInputLayout TIL_Otp = dialogView.findViewById(R.id.TIL_Otp);
        resend_btn.setVisibility(View.GONE);
        otptimertext = dialogView.findViewById(R.id.otptimertext);
        rtaotp_et = dialogView.findViewById(R.id.rtaotp_et);
        rtaotp_et.setTransformationMethod(new PasswordTransformationMethod());
        String[] otpmesseges = OTPtext.split("\\|");
        String Authsrno = otpmesseges[0];
        String messege = otpmesseges[1];
        if(err.length()>0){
            TIL_Otp.setError(err);
        }else {
            StartCountdown(otptimertext, resend_btn);
            otp_authentication_text.setText(messege);
        }

        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend_btn.setVisibility(View.GONE);
                StartCountdown(otptimertext,resend_btn);
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rtaotp_et.getText().length()>0){
                    new RedeemReq(eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value,Authsrno,rtaotp_et.getText().toString()).execute();
                }else {
                    Toast.makeText(homeActivity, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogforOtpVer.dismiss();
            }
        });
        builder.setView(dialogView);
        alertDialogforOtpVer = builder.create();
        alertDialogforOtpVer.show();
    }

    CountDownTimer timer;
    CountDownTimer timer2;

    private void StartCountdown(TextView timertext,TextView resendButton){
        timertext.setVisibility(View.VISIBLE);
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        final SimpleDateFormat formatter= new SimpleDateFormat("ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {

                Date date = new Date(millisUntilFinished);
                String formatted = formatter.format(date );
                timertext.setText("Resend OTP after "+formatted+" sec");

            }
            public void onFinish() {
                timertext.setVisibility(View.INVISIBLE);
                resendButton.setVisibility(View.VISIBLE);

            }
        }.start();
    }



    private void displaData(JSONObject jsonData) {

        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedetailsdata");
            JSONArray SchemeAvailableUnitsjarr = jsonData.getJSONArray("SchemeAvailableUnits");
            JSONArray schemeHoldingData = jsonData.getJSONArray("schemeholdingdata");
            if(jschdetailDataArr.length() > 0) {
                jschdetailData = jschdetailDataArr.getJSONObject(0);
                schemeCode = jschdetailData.getString("SchemeCode");
                navvalue.setText(jschdetailData.getString("CurrNAV") + "("+jschdetailData.getString("NAVDate")+")");
                exitloadvalue.setText(jschdetailData.getString("ExitLoad"));
                if(SchemeAvailableUnitsjarr.length()>0){
                    SchemeAvailableUnitsjobj = SchemeAvailableUnitsjarr.getJSONObject(0);
                    BankName = SchemeAvailableUnitsjobj.optString("BankName");
                    String maskedAccountNo = SchemeAvailableUnitsjobj.optString("MaskedAccountNo");
                    AccountNo = SchemeAvailableUnitsjobj.optString("AccountNo");
                    String lastFourDigits = maskedAccountNo;   //substring containing last 4 characters
                    try {
                        if (lastFourDigits.equalsIgnoreCase("")) {
                            lastFourDigits = AccountNo.substring(AccountNo.length() - 4);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    tv_Bank.setText("Your Redemption proceeds will be credited to your, "+BankName+" A/c No. "+lastFourDigits);
                }
                try {
                    String mystring=new String("Read More");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    makeTextViewResizable(exitloadvalue, 1, content, true);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (jschdetailData.optString("VClassCode").equals("11") &&
                        VenturaServerConnect.mfClientType == eMFClientType.MFI ){
                }
            }
            JSONArray schemeAvailableUnits = jsonData.getJSONArray("SchemeAvailableUnits");
            if(schemeAvailableUnits.length() > 0){
                JSONObject schAvlUnits = schemeAvailableUnits.getJSONObject(0);
                String currVal  = schAvlUnits.getString("PresentValue");
                String units = schAvlUnits.getString("AvailableUnits");
                _currentValue = StaticMethods.getStringToDouble(currVal);
                _noOfUnits = StaticMethods.getStringToDouble(units);
                avlunits.setText(_noOfUnits+"");
                setRedeemInputLayout();
            }
            else{
                _currentValue = 0;
                _noOfUnits = 0;
                avlunits.setText("0");
                setRedeemInputLayout();
                toRedeemEt.setEnabled(false);
            }
            if(schemeHoldingData.length()>0){
                JSONObject scHoldingUnits = schemeHoldingData.getJSONObject(0);
                String PresentUnits = scHoldingUnits.getString("PresentUnits");
                totalunits.setText(StaticMethods.getStringToDouble(PresentUnits)+"");
            }
            else{
                totalunits.setText("0");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

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

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
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
    private  void  handleSaveInvestResp(JSONObject jsonData){
        try{
            GlobalClass.log("SaveInvestResp : " + jsonData.toString());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean validateData(){
        try{
            if (amountRd.isChecked()){
                if (TextUtils.isEmpty(toRedeemEt.getText())){
                    String ENTER_AMT = "Please enter amount to redeem.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
                double entiredAmt = StaticMethods.getStringToDouble(toRedeemEt.getText().toString());
                if (partialRd.isChecked() && entiredAmt>_currentValue){
                    String MINIMUM_MULTIPLE_OF = "You have no more current value to process the request.";
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
                if (entiredAmt<=0){
                    String MINIMUM_MULTIPLE_OF = "Redeem amount should be more than Zero.";
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
            }else {
                if (TextUtils.isEmpty(toRedeemEt.getText())){
                    String ENTER_AMT = "Please enter units to redeem.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
                double entiredUnits = StaticMethods.getStringToDouble(toRedeemEt.getText().toString());
                if (partialRd.isChecked() && entiredUnits>_noOfUnits){
                    String MINIMUM_MULTIPLE_OF = "You have no more units to process the request.";
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
                if (entiredUnits<=0){
                    String MINIMUM_MULTIPLE_OF = "Redeem units should be more than Zero.";
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
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

}