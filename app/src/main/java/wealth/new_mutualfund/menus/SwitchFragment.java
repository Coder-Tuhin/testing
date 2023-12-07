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
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
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
import utils.StaticMessages;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Custom.CustomArrayAdapter;
import wealth.new_mutualfund.newMF.OneTimeFragment;

public class SwitchFragment extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;
    private View mView;

    String schemeCode;
    String schemeName;
    String folioNumber = "";
    String OnlieFlag = "";

    JSONObject jschdetailData;
    JSONObject jHoldingData;

    ArrayList<JSONObject> searchData;

    public static SwitchFragment newInstance(JSONObject jData, String fromScreen) {
        SwitchFragment f = new SwitchFragment();
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

    @BindView(R.id.switchtransfertoSpinner)
    Spinner switchtransfertoSpinner;

    @BindView(R.id.termcondChkBox)
    CheckBox termcondChkBox;

    @BindView(R.id.switchnowbtn)
    TextView switchnowbtn;

    @BindView(R.id.navvalue)
    TextView navvalue;
    @BindView(R.id.minimum_amount)
    TextView minimum_amount;
    @BindView(R.id.multiply)
    TextView multiply;
    @BindView(R.id.exitloadvalue)
    TextView exitloadvalue;
    @BindView(R.id.currentvalue)
    TextView currentvalue;
    @BindView(R.id.totalunits)
    TextView totalunits;
    @BindView(R.id.searchedittext)
    EditText searchedittext;
    @BindView(R.id.searchbtn)
    LinearLayout searchbtnbtn;

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
    @BindView(R.id.toSwitch)
    TextView toSwitch;
    @BindView(R.id.toSwitchEt)
    EditText toSwitchEt;

    private RadioGroup.OnCheckedChangeListener unitAmountCheck = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            setRedeemInputLayout();
        }
    };

    private void setRedeemInputLayout(){

        String _redeemTitle = getResources().getString(R.string.Units_to_switch);
        String _redeemHint = "Enter Units";
        toSwitchEt.setText("");
        if (amountRd.isChecked()){
            _redeemTitle = getResources().getString(R.string.Amount_to_switch);
            _redeemHint = "Enter Amount";
        }
        toSwitch.setText(_redeemTitle);
        toSwitchEt.setHint(_redeemHint);
        setRedeemInputValues();
    }
    private void setRedeemInputValues(){
        double _tempRedeemValue = 0;
        if (allRd.isChecked()){
            if (amountRd.isChecked()){
                _tempRedeemValue = _currentValue;
            }else {
                _tempRedeemValue = _noOfUnits;
            }
            toSwitchEt.setText(String.valueOf(_tempRedeemValue));
            toSwitchEt.setEnabled(false);
        }else {
            toSwitchEt.setText("");
            toSwitchEt.setEnabled(true);
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
        if (mView == null) {
            mView = inflater.inflate(R.layout.switch_screen, container, false);
            ButterKnife.bind(this, mView);
            mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                    homeActivity.showMsgDialog("Terms & Conditions",R.string.mf_terms_conditions,false));
            mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        }
        ininValue();
        new SwitchReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
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


            String currVal  = jHoldingData.getString("CurrentAmt");
            String units = jHoldingData.getString("Units");
            _currentValue = StaticMethods.getStringToDouble(currVal);
            _noOfUnits = StaticMethods.getStringToDouble(units);

            currentvalue.setText(Formatter.DecimalLessIncludingComma(currVal));
            totalunits.setText(_noOfUnits +"");
            initSpinnerAndRadioGroup();
            setRedeemInputLayout();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private double _currentValue = 0;
    private double _noOfUnits = 0;

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
        }
        else{
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

        switchnowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(validateData()){
                    homeActivity.showMsgDialog("Switch Now", getConfirmationMsg(), (dialogInterface, i) ->
                            new SwitchReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute(),
                            (dialogInterface, i) -> dialogInterface.dismiss());
                }*/

                if(validateData()){
                    if(OnlieFlag.equalsIgnoreCase("MFI")){
                        new SwitchReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
                    }else {
                        new SwitchReq(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value).execute();

                    }
                    /*
                    homeActivity.showMsgDialog("Message", getResources().getString(R.string.stampduty), (DialogInterface dialogInterface, int i) -> {
                                homeActivity.showMsgDialog("Invest Now", getConfirmationMsg(), (DialogInterface dialogInterface1, int j) -> {
                                            new SwitchReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();

                                        },
                                        (dialogInterface1, j) -> {
                                            dialogInterface1.dismiss();
                                        });
                            },
                            (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            });
                    */
                }
            }
        });
        searchbtnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GlobalClass.showToast(getContext(),"Search Btn Click");
                if(searchedittext.getText().toString().trim().length()>=3){
                    new SwitchReq(eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value).execute();
                }
                else{
                    GlobalClass.showToast(getContext(),"Please enter search text atlease 3 character");
                }
            }
        });
    }



    private String getConfirmationMsg(){
        String _tempConfirmation ="";
        if (unitsRd.isChecked()){
            _tempConfirmation = "Are you sure you want to Switch of units "+
                    Formatter.TwoDecimalIncludingComma(toSwitchEt.getText().toString())+"?";
        }else {
            _tempConfirmation = "Are you sure you want to Switch of amount "+
                    Formatter.TwoDecimalIncludingComma(toSwitchEt.getText().toString())+"?";
        }
        return _tempConfirmation;
    }

    private void initSearchSpinner(ArrayList<String> list){
        if(list != null && list.size()>0) {
            CustomArrayAdapter spinnerAdapterF = new CustomArrayAdapter(homeActivity, list);
            switchtransfertoSpinner.setVisibility(View.VISIBLE);
            switchtransfertoSpinner.setAdapter(spinnerAdapterF);
        }
        else{
            switchtransfertoSpinner.setVisibility(View.GONE);
        }
    }

    class SwitchReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String AuthSrNo = "",otp = "";
        SwitchReq(int mCode,String Authsrno,String OTP) {
            this.msgCode = mCode;
            this.AuthSrNo = Authsrno;
            this.otp = OTP;
        }

        SwitchReq(int mCode){
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
                    jdata.put(eMFJsonTag.OPTION.name,eOptionMF.SWITCH.name);
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if(msgCode == eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value){
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.KEYWORD.name,searchedittext.getText().toString());
                    jdata.put(eMFJsonTag.OPTION.name,eOptionMF.SWITCH.name);
                    jdata.put(eMFJsonTag.SUBCATEGORY.name,"L");
                    jdata.put(eMFJsonTag.AMCCODE.name,jschdetailData.getString("AMC_Code"));

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){

                    JSONObject transfertO = searchData.get(switchtransfertoSpinner.getSelectedItemPosition());

                    String _tempUnit = unitsRd.isChecked()?toSwitchEt.getText().toString():"";
                    String _tempAmount = amountRd.isChecked()?toSwitchEt.getText().toString():"";

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name,UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemeCode);
                    jdata.put(eMFJsonTag.OPTION.name,eOperationMF.SWITCH.name);//Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
                    jdata.put(eMFJsonTag.FOLIONO.name,folioNumber);
                    jdata.put(eMFJsonTag.INVESTAMT.name,"");// only used for Purchase,TOpUP
                    jdata.put(eMFJsonTag.REDEEMOPT.name,"");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount//Redeem,SpreadOrder
                    jdata.put(eMFJsonTag.REDEEM.name,"");//Units | Amount //Redeem
                    jdata.put(eMFJsonTag.LIQUIDREDEEM.name,"");//B - Bank,L - Ledger//redeem
                    jdata.put(eMFJsonTag.WITHDRAWAL.name,"");//amount only used for SWP
                    jdata.put(eMFJsonTag.FREQUENCY.name,"");//Monthly | Quarterly//amount only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.EXECUTIONDAY.name,"");//amount only used for SWP
                    jdata.put(eMFJsonTag.STARTDATE.name,"");//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.ENDDATE.name,"");//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.PERIOD.name,"");// only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.TOSCHEMECODE.name,transfertO.getString("SchemeCode"));// only used for STP,SWITCH
                    jdata.put(eMFJsonTag.TRANSFERAMT.name,"");// only used for STP
                    jdata.put(eMFJsonTag.SWITCHAMT.name,_tempAmount);// only used for SWITCH
                    jdata.put(eMFJsonTag.SWITCHUNITS.name,_tempUnit);// only used for SWITCH
                    jdata.put(eMFJsonTag.REDEEMDATE.name,"");// only used for SPREADORDER
                    jdata.put(eMFJsonTag.SIPAMT.name,"");// only used for SIP
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name,"");//date only used for SIP T/F
                    jdata.put("OrderNo","");//date only used for SIP T/F
                    jdata.put("TransDate","");//date only used for SIP T/F
                    jdata.put("PaymentModeOption","");
                    jdata.put("VPAId","");
                    jdata.put("TransClientType", OnlieFlag);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if (msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("AuthSrNo", AuthSrNo);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if (msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value) {

                    JSONObject transfertO = searchData.get(switchtransfertoSpinner.getSelectedItemPosition());

                    String _tempUnit = unitsRd.isChecked()?toSwitchEt.getText().toString():"";
                    String _tempAmount = amountRd.isChecked()?toSwitchEt.getText().toString():"";
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("SchemeCode", schemeCode);

                    jdata.put("TransType", "8");

                    jdata.put("TransClientType", OnlieFlag);

                    jdata.put("InvAmount", "");// only used for Purchase,TOpUP
                    jdata.put("FolioNo", folioNumber);

                    jdata.put(eMFJsonTag.REDEEMOPT.name, "");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount
                    jdata.put(eMFJsonTag.REDEEM.name, "");//Units | Amount
                    jdata.put("ToSchemeCode", transfertO.getString("SchemeCode"));//Units | Amount
                    jdata.put("SwitchAmount", _tempAmount);//Units | Amount
                    jdata.put("SwitchUnits", _tempUnit);//Units | Amount
                    jdata.put("SIPAmount", "");//Units | Amount
                    jdata.put("StartDate", "");//Units | Amount
                    jdata.put("ValidUntilCancelled", "");//Units | Amount
                    jdata.put("EndDate", "");//Units | Amount
                    jdata.put("Frequency", "");
                    jdata.put("AccountNo", "");

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
                        String err1 = "";
                        if(msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value || msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value ) {
                            if (msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value) {
                                String[] separated = mainerr.split("\\|");
                                OTPtext = separated[0]+"|"+separated[1];
                                if(separated[2].equalsIgnoreCase("T")) {
                                    showOtpVerificationPopup("");
                                }else {
                                    new SwitchReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
                                }

                            }else if(msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value){
                                StartCountdown(otptimertext,rtaotp_et);

                            } else if (msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value) {
                                if (mainerr.contains("Invalid")) {
                                    //showOtpVerificationPopup(err);
                                    updateAlertDialog(mainerr);
                                } else {
                                    if (alertDialogforOtpVer.isShowing()) {
                                        alertDialogforOtpVer.dismiss();
                                        showOtpSuccessAlert(mainerr);
                                    }
                                }
                            }
                        }else {

                            String finalMsg;
                            if(mainerr.contains("|")){
                                finalMsg = mainerr.split("\\|")[1];
                            }else{
                                finalMsg = mainerr;
                            }
                            GlobalClass.showAlertDialog(finalMsg);
                            /*
                            if (mainerr.contains("Your Switch order is placed successfully")) {
                                if (mainerr.contains("|")) {
                                    err1 = mainerr.split("\\|")[1];
                                }
                                String err2 = mainerr.split("\\|")[2];
                                ShowalertDialog(err1, err2);

                            } else {
                                displayError(err);
                            }*/
                        }
                        //displayError(err);
                    }else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        }
                        else if(msgCode == eMessageCodeWealth.GET_SCHEME_SEARCH_DATA.value){
                            handleSearchData(jsonData);
                        }
                        else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                            handleSaveInvestResp(jsonData);
                            toSwitchEt.setText("");
                        }
                    }
                    if(msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value){
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStackImmediate();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }


        }
    }
    String OTPtext = "";
    CountDownTimer timer;

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
                new SwitchReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
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
                    new SwitchReq(eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value,Authsrno,rtaotp_et.getText().toString()).execute();
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
    AlertDialog.Builder builder;
    androidx.appcompat.app.AlertDialog alertDialogforOtpVer;
    androidx.appcompat.app.AlertDialog otpsucccessalert;

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
    private void displaData(JSONObject jsonData) {

        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedetailsdata");
            if(jschdetailDataArr.length() > 0) {
                jschdetailData = jschdetailDataArr.getJSONObject(0);

                schemeCode = jschdetailData.getString("SchemeCode");
                navvalue.setText(jschdetailData.getString("CurrNAV") + "("+jschdetailData.getString("NAVDate")+")");
                exitloadvalue.setText(jschdetailData.getString("ExitLoad"));

                String multipleOff  = jschdetailData.getString("PurchaseAmtMult");
                String minimumAmt = jschdetailData.getString("MinPurchaseAmt");
                OnlieFlag = jHoldingData.optString("OnlineFlag");
                if(OnlieFlag.equalsIgnoreCase("online")){
                    OnlieFlag = "MFI";
                }else {
                    OnlieFlag = "MFD";
                }

                _minimumAmt = StaticMethods.getStringToDouble(minimumAmt);
                _multipleOf = StaticMethods.getStringToDouble(multipleOff);

                multiply.setText(multipleOff);
                minimum_amount.setText(Formatter.DecimalLessIncludingComma(minimumAmt));
            }

        }
        catch (Exception ex){
            VenturaException.Print(ex);
        }
    }

    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    private  void  handleSaveInvestResp(JSONObject jsonData){
        try{
            GlobalClass.log("SaveInvestResp : " + jsonData.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
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
            }else{
                GlobalClass.showAlertDialog("Switch is allowed in the same Fund house only");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    public boolean validateData(){
        try{
            if (amountRd.isChecked()){
                if (TextUtils.isEmpty(toSwitchEt.getText())){
                    String ENTER_AMT = "Please enter amount to switch.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
                double entiredAmt = StaticMethods.getStringToDouble(toSwitchEt.getText().toString());
                if (partialRd.isChecked() && entiredAmt>_currentValue){
                    String MINIMUM_MULTIPLE_OF = "You have no more current value to process the request.";
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }

                if (entiredAmt<_minimumAmt && (entiredAmt*100)%(_multipleOf*100) !=0){
                    String MINIMUM_MULTIPLE_OF = "Entered amount should be minimum "+_minimumAmt+
                            ". And multiple of "+_multipleOf;
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
            }else {
                if (TextUtils.isEmpty(toSwitchEt.getText())){
                    String ENTER_AMT = "Please enter units to switch.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
                double entiredUnits = StaticMethods.getStringToDouble(toSwitchEt.getText().toString());
                if (partialRd.isChecked() && entiredUnits>_noOfUnits){
                    String MINIMUM_MULTIPLE_OF = "You have no more units to process the request.";
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }
                if (entiredUnits<=0){
                    String MINIMUM_MULTIPLE_OF = "Switch units should be more than Zero.";
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