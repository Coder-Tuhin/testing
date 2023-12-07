package wealth.new_mutualfund.newMF;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import fragments.fundtransfer.FundtransferFragment;
import utils.Formatter;
import utils.GlobalClass;
import utils.MySpannable;
import utils.StaticMessages;
import utils.StaticMethods;
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.menus.MFFundTransfer;
import wealth.new_mutualfund.menus.WebViewForMFLumpsum;

public class OneTimeFragment extends Fragment {
    String SchemeName = "",tag = "",FolioNumber = "",OnlineFlag = "", missedsipamount = "";

    public static OneTimeFragment newInstance(String schemecode,String schemename ,String tag) {
        OneTimeFragment itc = new OneTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1, schemecode);
        bundle.putString(StaticVariables.ARG_2,schemename);
        bundle.putString("tag",tag);
        bundle.putString("FolioNumber","");
        itc.setArguments(bundle);
        return itc;
    }
    public static OneTimeFragment newInstance(String schemecode,String schemename,String FolioNumber ,String tag) {
        OneTimeFragment itc = new OneTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1, schemecode);
        bundle.putString(StaticVariables.ARG_2,schemename);
        bundle.putString("FolioNumber",FolioNumber);
        bundle.putString("tag",tag);
        itc.setArguments(bundle);
        return itc;
    }
    public static OneTimeFragment newInstance(String schemecode,String schemename,String FolioNumber ,String tag,String OnlineFlag) {
        OneTimeFragment itc = new OneTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1, schemecode);
        bundle.putString(StaticVariables.ARG_2,schemename);
        bundle.putString("FolioNumber",FolioNumber);
        bundle.putString("tag",tag);
        bundle.putString("OnlineFlag",OnlineFlag);
        itc.setArguments(bundle);
        return itc;
    }
    public static OneTimeFragment newInstance(String schemecode,String schemename,String FolioNumber ,String tag,String OnlineFlag,String missedsipamount) {
        OneTimeFragment itc = new OneTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1, schemecode);
        bundle.putString(StaticVariables.ARG_2,schemename);
        bundle.putString("FolioNumber",FolioNumber);
        bundle.putString("tag",tag);
        bundle.putString("OnlineFlag",OnlineFlag);
        bundle.putString("misssipamount",missedsipamount);
        itc.setArguments(bundle);
        return itc;
    }

    private HomeActivity homeActivity;


    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    public static OneTimeFragment newInstance() {
        return new OneTimeFragment();
    }

    @BindView(R.id.termcondChkBox)
    CheckBox termcondChkBox;
    @BindView(R.id.tv_10000)
    TextView tv_10000;
    @BindView(R.id.tv_20000)
    TextView tv_20000;
    @BindView(R.id.tv_50000)
    TextView tv_50000;
    @BindView(R.id.tv_100000)
    TextView tv_100000;
    @BindView(R.id.tv_amount)
    EditText amountEditText;
    @BindView(R.id.navvalue)
    TextView navvalue;
    @BindView(R.id.minimum_amount)
    TextView minimum_amount;
    @BindView(R.id.multiply)
    TextView multiply;
    @BindView(R.id.exitloadvalue)
    TextView exitloadvalue;
    @BindView(R.id.invetnowbtn)
    TextView invetnowbtn;
    @BindView(R.id.SchemeNameTV)
    TextView SchemeNameTV;
    String schemecode;
    private String paymentmode = "1";
    androidx.appcompat.app.AlertDialog alertDialog;
    androidx.appcompat.app.AlertDialog alertDialogforOtpVer;
    androidx.appcompat.app.AlertDialog otpsucccessalert;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.sip_one_time_invest_screen, container, false);
        ButterKnife.bind(this, mView);
        try {
            Bundle bundle = getArguments();
            schemecode = bundle.getString(StaticVariables.ARG_1);
            SchemeName = bundle.getString(StaticVariables.ARG_2);
            tag = bundle.getString("tag");
            try {
                FolioNumber = bundle.getString("FolioNumber","");
                OnlineFlag = bundle.getString("OnlineFlag","");
                if(OnlineFlag.equalsIgnoreCase("")){
                    OnlineFlag = VenturaServerConnect.mfClientType.name;
                }else {
                    if (OnlineFlag.equalsIgnoreCase("online")) {
                        OnlineFlag = "MFI";
                    } else {
                        OnlineFlag = "MFD";
                    }
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }

            try {
                missedsipamount = bundle.getString("misssipamount","");
                if(!missedsipamount.equalsIgnoreCase("")){
                    amountEditText.setText(missedsipamount);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            SchemeNameTV.setText(SchemeName);
            tv_10000.setOnClickListener(_onclick);
            tv_20000.setOnClickListener(_onclick);
            tv_50000.setOnClickListener(_onclick);
            tv_100000.setOnClickListener(_onclick);
            if(tag.equalsIgnoreCase("taxsaving")){
                tv_10000.setText("\u20B9 50,000");
                tv_20000.setText("\u20B9 75,000");
                tv_50000.setText("\u20B9 1,00,000");
                tv_100000.setText("\u20B9 1,50,000");

            }
            mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                    homeActivity.showMsgDialog("Terms & Conditions", R.string.mf_terms_conditions, false));
            mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());

            new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
            invetnowbtn.setOnClickListener(view -> {
                if(validateData()){
                    showPaymentoption();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mView;
    }

    private void showPaymentoption() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.payment_popup, viewGroup, false);
        LinearLayout ll_UPI = dialogView.findViewById(R.id.ll_UPI);
        EditText upiEditText = dialogView.findViewById(R.id.upiEditText);
        TextView titleText = dialogView.findViewById(R.id.titletext);
        titleText.setText("How would you like to make the payment?");
        TextView submit = dialogView.findViewById(R.id.submit);
        RadioGroup paymentradiogrp = dialogView.findViewById(R.id.paymentradiogrp);
        Button okBtn = dialogView.findViewById(R.id.okBtn);
        okBtn.setVisibility(View.VISIBLE);
        Spinner sipSpinnerBankList = dialogView.findViewById(R.id.sipSpinnerBankList);
        sipSpinnerBankList.setVisibility(View.GONE);



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
                switch (i){
                    case R.id.netbanking:
                        paymentmode = "1";
                        okBtn.setVisibility(View.VISIBLE);
                        ll_UPI.setVisibility(View.GONE);
                        break;
                    case R.id.upi:
                        ll_UPI.setVisibility(View.VISIBLE);
                        okBtn.setVisibility(View.GONE);
                        paymentmode = "3";
                        TextView BankText  = dialogView.findViewById(R.id.banktext);
                        BankText.setText("Kindly ensure that your UPI id is mapped to your registered bank, "+VenturaServerConnect.mfBank);
                        break;
                }
            }

        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upiEditText.getText().toString().equalsIgnoreCase("")){
                    GlobalClass.showAlertDialog("please enter and verify UPI ID");

                }else {
                    new AddUPIReq(eMessageCodeWealth.IPO_UPI_VERIFY.value,upiEditText.getText().toString(),dialogView).execute();
                }
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upiEditText.getText().length() > 0){
                    UPiidtext = upiEditText.getText().toString();
                }
                if(OnlineFlag.equalsIgnoreCase("MFI")){
                    new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,upiEditText.getText().toString()).execute();
                }else {
                    new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value,upiEditText.getText().toString()).execute();
                }
                alertDialog.dismiss();
            }
        });
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
    }
    String UPiidtext = "";

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
                String checkedMark =err;// + " " + "\u2713";
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

    private void openInsufficientPopUP() {
        homeActivity.showMsgDialog("Insufficient Funds");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  SaveInvestmentData();
    }



    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_10000:
                    if(tag.equalsIgnoreCase("taxsaving")){
                        amountEditText.setText("50,000");

                    }else {
                        amountEditText.setText("10,000");

                    }
                    tv_10000.setActivated(true);
                    tv_20000.setActivated(false);
                    tv_50000.setActivated(false);
                    tv_100000.setActivated(false);
                    break;
                case R.id.tv_20000:
                    if(tag.equalsIgnoreCase("taxsaving")){
                        amountEditText.setText("75,000");

                    }else {
                        amountEditText.setText("20,000");

                    }
                    tv_20000.setActivated(true);
                    tv_10000.setActivated(false);
                    tv_50000.setActivated(false);
                    tv_100000.setActivated(false);
                    break;
                case R.id.tv_50000:
                    if(tag.equalsIgnoreCase("taxsaving")){
                        amountEditText.setText("1,00,000");

                    }else {
                        amountEditText.setText("50,000");

                    }
                    tv_50000.setActivated(true);
                    tv_10000.setActivated(false);
                    tv_20000.setActivated(false);
                    tv_100000.setActivated(false);
                    break;
                case R.id.tv_100000:
                    if(tag.equalsIgnoreCase("taxsaving")){
                        amountEditText.setText("1,50,000");

                    }else {
                        amountEditText.setText("1,00,000");

                    }
                    tv_100000.setActivated(true);
                    tv_10000.setActivated(false);
                    tv_20000.setActivated(false);
                    tv_50000.setActivated(false);
                    //checkAvlBalence(StaticMethods.getStringToDouble(((TextView) view).getText().toString()));

                    break;
//                case R.id.investbtn:
//                    checkAvlBalence(StaticMethods.getStringToDouble(amountEditText.getText().toString().trim()));
                //                   break;
            }
        }
    };

    public void setTexttoEdittext(TextView tv) {
        amountEditText.setText(tv.getText().toString().trim());
        tv.setActivated(true);

    }


    private String getConfirmationMsg() {
        /*String _tempConfirmation = "Are you sure you want to Invest of amount "+
                Formatter.TwoDecimalIncludingComma(amountEditText.getText().toString())+"?";*/

        String _tempConfirmation = "Would you like to proceed with investment of Rs. " +
                Formatter.DecimalLessIncludingComma(amountEditText.getText().toString()) + "?";
       /* double amount = StaticMethods.getStringToDouble(amountEditText.getText().toString());
        if ((VenturaServerConnect.mfClientType != eMFClientType.MFD) && (amount>avlBalance.getAvailableBalance())){
            _tempConfirmation = "Your available balance is less than the amount to be invested. Please transfer funds to your account.";
        }*/
        return _tempConfirmation;
    }

    public boolean validateData() {
        try {

            if (!termcondChkBox.isChecked()) {
                homeActivity.showMsgDialog(StaticMessages.CHECK_TERM_CONDITION);
                return false;
            }
            if (_minimumAmt >= 0) {
                if (TextUtils.isEmpty(amountEditText.getText())) {
                    String ENTER_AMT = "Please enter amount to invest.";
                    homeActivity.showMsgDialog(ENTER_AMT);
                    return false;
                }
                double amount = StaticMethods.getStringToDouble(amountEditText.getText().toString().replaceAll(",", ""));
                if (amount < _minimumAmt || (amount * 100) % (_multipleOf * 100) != 0) {
                    String MINIMUM_MULTIPLE_OF = "Entered amount should be minimum " + _minimumAmt +
                            ". And multiple of " + _multipleOf;
                    homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                    return false;
                }

                /*if ((VenturaServerConnect.mfClientType != eMFClientType.MFD) && (amount > avlBalance.getAvailableBalance()) ||(VenturaServerConnect.mfClientType != eMFClientType.NONE)) {
                 *//*String NO_AVL_BAL = "Your available balance is less than the amount to be invested. Please transfer funds to your account.";
                    homeActivity.showMsgDialog(NO_AVL_BAL);*//*
                    GlobalClass.showNotEnoughBalanceMsg(homeActivity);
                    return false;
                }*/
                return true;
            } else {
                homeActivity.showMsgDialog(StaticMessages.SOMETHING_WRONG);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return false;
    }
    private void openTransferFundspopUp() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View alertView = getLayoutInflater().inflate(R.layout.aleart_fundtransfer, null);
            builder.setView(alertView);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            alertView.findViewById(R.id.closeAlert).setOnClickListener(v -> {
                dialog.dismiss();
            });
            alertView.findViewById(R.id.positiveBtnTv).setOnClickListener(v -> {
                homeActivity.FragmentTransaction(FundtransferFragment.newInstance(0), R.id.container_body, true);
                dialog.dismiss();
            });
            dialog.getWindow().setBackgroundDrawable(null);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSuccessPopup() {
        GlobalClass.showAlertDialog("Your Order has been place successfully , with the order no.12345");
    }


    class PurchaseFragmentForBucketInvstReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String upitext = "",AuthSrNo = "",otp = "";

        PurchaseFragmentForBucketInvstReq(int mCode,String UPIText) {
            this.msgCode = mCode;
            this.upitext = UPIText;
        }
        PurchaseFragmentForBucketInvstReq(int mCode,String Authsrno,String OTP) {
            this.msgCode = mCode;
            this.AuthSrNo = Authsrno;
            this.otp = OTP;
        }

        PurchaseFragmentForBucketInvstReq(int mCode) {
            this.msgCode = mCode;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(msgCode != eMessageCodeWealth.CHECK_PAYMENTSTATUS.value ) {
                GlobalClass.showProgressDialog("Requesting...");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemecode);
                    if(tag.equalsIgnoreCase(eOptionMF.TOPUP.name)) {
                        jdata.put(eMFJsonTag.OPTION.name, eOptionMF.TOPUP.name);
                    }else {
                        jdata.put(eMFJsonTag.OPTION.name, eOptionMF.PURCHASE.name);
                    }
                    jdata.put(eMFJsonTag.FOLIONO.name, FolioNumber);
                    if(tag.equalsIgnoreCase(eOptionMF.NFO.name)) {
                        jdata.put(eMFJsonTag.entrymode.name, eOptionMF.NFO.name);
                    }else{
                        jdata.put(eMFJsonTag.entrymode.name,"FMP");
                    }

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemecode);
                    jdata.put("TransClientType", OnlineFlag);

                    if(tag.equalsIgnoreCase(eOptionMF.TOPUP.name)) {
                        jdata.put(eMFJsonTag.OPTION.name, eOptionMF.TOPUP.name);
                    }else {
                        jdata.put(eMFJsonTag.OPTION.name, eOptionMF.PURCHASE.name);
                    }
                    jdata.put(eMFJsonTag.FOLIONO.name, FolioNumber);
                    jdata.put(eMFJsonTag.INVESTAMT.name, amountEditText.getText().toString().replaceAll(",", ""));// only used for Purchase,TOpUP
                    jdata.put(eMFJsonTag.REDEEMOPT.name, "");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount
                    jdata.put(eMFJsonTag.REDEEM.name, "");//Units | Amount
                    jdata.put(eMFJsonTag.LIQUIDREDEEM.name, "");//B - Bank,L - Ledger
                    jdata.put(eMFJsonTag.WITHDRAWAL.name, "");//amount only used for SWP
                    jdata.put(eMFJsonTag.FREQUENCY.name, "");//Monthly | Quarterly//amount only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.EXECUTIONDAY.name, "");//amount only used for SWP
                    jdata.put(eMFJsonTag.STARTDATE.name, "");//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.ENDDATE.name, "");//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.PERIOD.name, "");// only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.TOSCHEMECODE.name, "");// only used for STP,SWITCH
                    jdata.put(eMFJsonTag.TRANSFERAMT.name, "");// only used for STP
                    jdata.put(eMFJsonTag.SWITCHAMT.name, "");// only used for SWITCH
                    jdata.put(eMFJsonTag.SWITCHUNITS.name, "");// only used for SWITCH
                    jdata.put(eMFJsonTag.REDEEMDATE.name, "");// only used for SPREADORDER
                    jdata.put(eMFJsonTag.SIPAMT.name, "");// only used for SIP
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name, "");//date only used for SIP T/F
                    jdata.put(eMFJsonTag.ORDERNO.name,"");
                    jdata.put(eMFJsonTag.TRANSADATE.name,"");
                    jdata.put("PaymentModeOption",paymentmode);
                    jdata.put("VPAId",upitext);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if (msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("SchemeCode", schemecode);
                    if(tag.equalsIgnoreCase(eOptionMF.TOPUP.name)) {
                        jdata.put("TransType", "2");
                    }else {
                        jdata.put("TransType", "1");
                    }
                    jdata.put("TransClientType", OnlineFlag);

                    jdata.put("InvAmount", amountEditText.getText().toString().replaceAll(",", ""));// only used for Purchase,TOpUP
                    jdata.put("FolioNo", FolioNumber);

                    jdata.put(eMFJsonTag.REDEEMOPT.name, "");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount
                    jdata.put(eMFJsonTag.REDEEM.name, "");//Units | Amount
                    jdata.put("ToSchemeCode", "");//Units | Amount
                    jdata.put("SwitchAmount", "");//Units | Amount
                    jdata.put("SwitchUnits", "");//Units | Amount
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
                else if (msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("AuthSrNo", AuthSrNo);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value){
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("TransClientType", OnlineFlag);
                    jdata.put("TransNo",upitext);
                    jdata.put("MandateCode", "");
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();

            if (s != null) {
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if (!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        if(msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value){
                            String error = "AMC has restricted lumpsum (one time) investments in this scheme, we suggest you to start a SIP in this scheme.";
                            GlobalClass.showAlertDialog(error);
                            GlobalClass.fragmentManager.popBackStackImmediate();
                        }else if(msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value){
                            String[] separated = err.split("\\|");
                            OTPtext = separated[0]+"|"+separated[1];
                            if(separated[2].equalsIgnoreCase("T")) {
                                showOtpVerificationPopup("");
                            }else {
                                new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,UPiidtext).execute();

                            }
                        }else if(msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value){
                            StartCountdown(otptimertext,rtaotp_et);

                        }else if(msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value){
                            if(err.contains("Invalid")){
                                //showOtpVerificationPopup(err);
                                updateAlertDialog(err);
                            }else {
                                if(alertDialogforOtpVer.isShowing()){
                                    alertDialogforOtpVer.dismiss();
                                    showOtpSuccessAlert(err);
                                }
                            }
                        }else {
                            if (err.contains("html")) {
                                displayInWebView(err);
                            } else if (err.contains("https")) {
                                err = err.substring(4);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
                                startActivity(browserIntent);
                            } /*else if (err.contains("99|")) {
                                goToFundtransferPage();
                            }*/ else if (err.contains("PENDING")) {

                            } else if (err.contains("UPI|")) {
                                CheckPaymentstatus(err.substring(4));
                            } else {
                                String finalMsg;
                                if(err.contains("|")){
                                    finalMsg = err.split("\\|")[1];
                                } else{
                                    finalMsg = err;
                                }
                                GlobalClass.showAlertDialog(finalMsg);
                                if (timer != null) {
                                    timer.cancel();
                                }
                                if (timer2 != null) {
                                    timer2.cancel();
                                }
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                                GlobalClass.fragmentManager.popBackStackImmediate();
                            }
                        }
                    } else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        } else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                            handleSaveInvestResp(jsonData.toString());
                            amountEditText.setText("");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
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
                new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,UPiidtext).execute();
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
                    new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value,Authsrno,rtaotp_et.getText().toString()).execute();
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

    CountDownTimer timer;
    CountDownTimer timer2;

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
                    new PurchaseFragmentForBucketInvstReq(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, upitransId).execute();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }


    private void displayError(String err) {
        GlobalClass.showAlertDialog(err);
    }

    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    private void displaData(JSONObject jObj) {
        try {
            JSONArray schemedetailsdata = jObj.optJSONArray("schemedetailsdata");
            if (schemedetailsdata.length() >= 0) {
                JSONObject schemedetailsObj = schemedetailsdata.optJSONObject(0);
                schemecode = schemedetailsObj.getString("SchemeCode");
                navvalue.setText(schemedetailsObj.getString("CurrNAV") + "(" + schemedetailsObj.getString("NAVDate") + ")");
                String multipleOf = schemedetailsObj.getString("PurchaseAmtMult");

                String minimumAmt = "";
                if(tag.equalsIgnoreCase(eOptionMF.TOPUP.name)) {
                    minimumAmt = schemedetailsObj.getString("AddPurchaseAmtMult");
                }else {
                    minimumAmt = schemedetailsObj.getString("MinPurchaseAmt");
                }
                _minimumAmt = StaticMethods.getStringToDouble(minimumAmt);
                _multipleOf = StaticMethods.getStringToDouble(multipleOf);
                minimum_amount.setText(Formatter.DecimalLessIncludingComma(minimumAmt));
                multiply.setText(multipleOf);
                exitloadvalue.setText(schemedetailsObj.getString("ExitLoad"));
                try {
                    String mystring=new String("Read More");
                    SpannableString content = new SpannableString(mystring);
                    content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                    makeTextViewResizable(exitloadvalue, 1, content, true);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //}
        } catch (Exception ex) {
            GlobalClass.onError("",ex);
        }
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
            GlobalClass.onError("",e);
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
    private void displayInWebView(String err) {
        //err = err.replaceAll("\n","");
        WebViewForMFLumpsum ls = WebViewForMFLumpsum.newInstance(err);
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
    }
    private void goToFundtransferPage() {
        //err = err.replaceAll("\n","");
        MFFundTransfer ls = MFFundTransfer.newInstance();
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
    }

    private void handleSaveInvestResp(String jsonData) {
        try {
            GlobalClass.log("SaveInvestResp : " + jsonData);
            GlobalClass.fragmentManager.popBackStackImmediate();
//            displayError(jsonData.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}