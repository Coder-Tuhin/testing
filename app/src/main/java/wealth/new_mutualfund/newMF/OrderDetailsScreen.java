
package wealth.new_mutualfund.newMF;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOperationMF;
import enums.eOptionMF;
import enums.eSIPType;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.StaticMessages;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.menus.MFFundTransfer;
import wealth.new_mutualfund.menus.SipMandateForInvest;
import wealth.new_mutualfund.menus.WebViewForMF;
import wealth.new_mutualfund.menus.modelclass.Mandatemodel;
import wealth.new_mutualfund.menus.modelclass.SIPModel;

public class OrderDetailsScreen extends Fragment {
    private HomeActivity homeActivity;
    public String formScreen = "";
    JSONObject jdata1;

    private static String SCHEMECODE = "schemecode";
    private static String SCHEMENAME = "schemename";
    private static String START_DATE = "start_date";
    private static String END_DATE = "end_date";
    private static String SELECTED_SIP_AMOUNT = "selected_sip_amount";
    private static String PEROID = "period";
    private static String VALID_TILL_CANCEL = "valid_till_cancel";
    int MandateCountGlobal = 0;
    String URL = "";
    String TransNo = "";
    int MandateCount = 0;


    public static OrderDetailsScreen newInstance(String schemecode, String schemename, String start_date, String end_date, String amt, String period, String validtillcancel) {
        OrderDetailsScreen fragment = new OrderDetailsScreen();
        Bundle args = new Bundle();
        args.putString(SCHEMECODE, schemecode);
        args.putString(SCHEMENAME, schemename);
        args.putString(START_DATE, start_date);
        args.putString(END_DATE, end_date);
        args.putString(SELECTED_SIP_AMOUNT, amt);
        args.putString(PEROID, period);
        args.putString(VALID_TILL_CANCEL, validtillcancel);
        fragment.setArguments(args);
        return fragment;
    }

    public static OrderDetailsScreen newInstance(String schemecode, String schemename, String start_date, String end_date, String amt, String period, String validtillcancel, JSONObject jData, String fromScreen) {
        OrderDetailsScreen fragment = new OrderDetailsScreen();
        Bundle args = new Bundle();
        args.putString(SCHEMECODE, schemecode);
        args.putString(SCHEMENAME, schemename);
        args.putString(START_DATE, start_date);
        args.putString(END_DATE, end_date);
        args.putString(SELECTED_SIP_AMOUNT, amt);
        args.putString(PEROID, period);
        args.putString(VALID_TILL_CANCEL, validtillcancel);
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(eMFJsonTag.FORMSCR.name, fromScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    public static OrderDetailsScreen newInstance() {
        return new OrderDetailsScreen();
    }

    @BindView(R.id.confirmButton)
    Button confirmButton;
    @BindView(R.id.termcondChkBox)
    CheckBox chk_terms;
    @BindView(R.id.tv_schemenane)
    TextView tv_schemenane;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.installment)
    TextView installment;
    @BindView(R.id.tv_startdate)
    TextView tv_startdate;
    @BindView(R.id.tv_enddate)
    TextView tv_enddate;
    EditText upiEditText;
    private String paymentmode = "1";
    androidx.appcompat.app.AlertDialog alertDialog;
    androidx.appcompat.app.AlertDialog alertDialog1;
    androidx.appcompat.app.AlertDialog alertDialog2;
    androidx.appcompat.app.AlertDialog alertDialogforOtpVer;
    androidx.appcompat.app.AlertDialog otpsucccessalert;

    private String schemecode, schemename, start_date, end_date, amount, period, validtillcancel;
    private String selectedConfirmationMsg = "";
    private JSONObject jschdetailData;
    private JSONObject jHoldingData;
    private SIPModel sipModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.oder_details_screen, container, false);
        ButterKnife.bind(this, mView);
        Bundle args = getArguments();
        try {
            formScreen = args.getString(eMFJsonTag.FORMSCR.name);
            if (formScreen.equalsIgnoreCase("nfo")) {
                String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
                jdata1 = new JSONObject(jsonStr);

            }
        } catch (Exception e) {
            e.printStackTrace();
            formScreen = "";
        }
        getschemedata();

        schemecode = args.getString(SCHEMECODE, "");
        start_date = args.getString(START_DATE, "");
        end_date = args.getString(END_DATE, "");
        amount = args.getString(SELECTED_SIP_AMOUNT, "");
        schemename = args.getString(SCHEMENAME, "");
        validtillcancel = args.getString(VALID_TILL_CANCEL, "");
        period = args.getString(PEROID, "");
        tv_schemenane.setText(schemename);
        tv_amount.setText("Rs. " + Formatter.DecimalLessIncludingComma(amount));
        String strt_date_temp = start_date.replace("/", "-");
        tv_startdate.setText(strt_date_temp);
        tv_enddate.setText(end_date);
        if (validtillcancel == "T") {
            tv_enddate.setText("Until I Cancel");
        } else {
            tv_enddate.setText(end_date);
        }
        String subCatArr[] = start_date.split("/");
        if (subCatArr[0].endsWith("1") && !subCatArr[0].startsWith("1")) {
            installment.setText(subCatArr[0] + "st");
        } else if (subCatArr[0].endsWith("2") && !subCatArr[0].startsWith("1")) {
            installment.setText(subCatArr[0] + "nd");
        } else if (subCatArr[0].endsWith("3") && !subCatArr[0].startsWith("1")) {
            installment.setText(subCatArr[0] + "rd");
        } else {
            installment.setText(subCatArr[0] + "th");
        }

        mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                homeActivity.showMsgDialog("Terms & Conditions", R.string.mf_terms_conditions, false));
        mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        confirmButton.setOnClickListener(view -> {

            if (chk_terms.isChecked()) {

                if (ChangeSIPDate.equalsIgnoreCase("T")) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                    ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.createmandatepopup, viewGroup, false);
                    Button select_date_btn = dialogView.findViewById(R.id.select_date_btn);
                    ImageView img_cross = dialogView.findViewById(R.id.img_cross);
                    select_date_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            homeActivity.onFragmentBack();
                            homeActivity.onFragmentBack();
                            alertDialog2.dismiss();
                        }
                    });
                    img_cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog2.dismiss();
                        }
                    });
                    builder.setView(dialogView);
                    if (alertDialog2 != null) {
                        alertDialog2.dismiss();
                    }
                    alertDialog2 = builder.create();
                    alertDialog2.show();
                } else {
                    selectedConfirmationMsg = getConfirmationMsg();
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                    ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.payment_popup, viewGroup, false);
                    LinearLayout ll_UPI = dialogView.findViewById(R.id.ll_UPI);
                    EditText upiEditText = dialogView.findViewById(R.id.upiEditText);
                    TextView submit = dialogView.findViewById(R.id.submit);
                    RadioGroup paymentradiogrp = dialogView.findViewById(R.id.paymentradiogrp);
                    Button okBtn = dialogView.findViewById(R.id.okBtn);
                    TextView bank_tv = dialogView.findViewById(R.id.bank_tv);
                    okBtn.setVisibility(View.VISIBLE);
                    ImageView img_cross = dialogView.findViewById(R.id.img_cross);
                    LinearLayout bankdetails_layout = dialogView.findViewById(R.id.bankdetails_layout);
                    bankdetails_layout.setVisibility(View.VISIBLE);
                    Spinner sipSpinnerBankList = dialogView.findViewById(R.id.sipSpinnerBankList);
                    JSONArray jarr = GlobalClass.jsonArrayClientBank;
                    HashMap<String, String> llist = new HashMap<>();
                    llist.put("Select Bank", "DEFAULT|1");
                    if (jarr != null) {
                        JSONObject obj = new JSONObject();
                        try {
                            for (int i = 0; i < jarr.length(); i++) {
                                obj = jarr.getJSONObject(i);
                                llist.put(obj.getString("Text"), obj.getString("Value") + "#" + obj.getString("SIPType"));
                            }
                            if (llist.size() == 2) {
                                sipSpinnerBankList.setVisibility(View.GONE);
                                bank_tv.setVisibility(View.VISIBLE);
                                String Value = obj.optString("Text");
                                bank_tv.setText(Value);
                            } else {
                                bankdetails_layout.setVisibility(View.VISIBLE);
                                sipSpinnerBankList.setVisibility(View.VISIBLE);
                                setSpinnerData(sipSpinnerBankList, llist);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
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
                                    ll_UPI.setVisibility(View.GONE);
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
                            String totalbanktext = "";
                            if (llist.size() > 2 && sipSpinnerBankList.getSelectedItem().toString().equalsIgnoreCase("Select Bank")) {
                                GlobalClass.showAlertDialog("Please Select Payment Bank", false);
                            } else if (llist.size() == 2) {
                                totalbanktext = getValue(llist, bank_tv.getText().toString());
                            } else {
                                totalbanktext = getValue(llist, sipSpinnerBankList.getSelectedItem().toString());
                            }
                            if (llist.size() > 2 && sipSpinnerBankList.getSelectedItem().toString().equalsIgnoreCase("Select Bank")) {
                                GlobalClass.showAlertDialog("Please Select Payment Bank", false);
                            } else {

                                String[] bankarray = totalbanktext.split("#");
                                String Bankid = bankarray[0];
                                String Siptype = bankarray[1];
                                GlobalClass.mandatebankid = Bankid;
                                GlobalClass.mandateSipType = Siptype;
                                if (Siptype.equalsIgnoreCase(eSIPType.E_NACH.tag)) {
                                    //new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value, upiEditText.getText().toString(), Bankid, Siptype).execute();
                                    new SIPReq(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value, upiEditText.getText().toString(), Bankid, Siptype).execute();

                                } else {
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                                    ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.mandate_eligible, viewGroup, false);
                                    Button OK_btn = dialogView.findViewById(R.id.OK_btn);
                                    OK_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value, upiEditText.getText().toString(), Bankid, Siptype).execute();
                                            new SIPReq(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value, upiEditText.getText().toString(), Bankid, Siptype).execute();

                                            alertDialog1.dismiss();
                                        }
                                    });
                                    builder.setView(dialogView);
                                    if (alertDialog1 != null) {
                                        alertDialog1.dismiss();
                                    }
                                    alertDialog1 = builder.create();
                                    alertDialog1.show();

                                }
                            }
                            alertDialog.dismiss();
                        }
                    });
                    builder.setView(dialogView);
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            } else {
                GlobalClass.showAlertDialog("Please accept terms and conditions");
            }
        });
        return mView;

    }

    public <K, V> V getValue(Map<K, V> map, K key) {

        return map.get(key);
    }

    private void setSpinnerData(Spinner spinner, HashMap<String, String> data) {
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity, R.layout.sip_spn_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data.keySet());
        spinner.setAdapter(categoryAdp);
    }


    public boolean validateData() {
        try {
            if (sipModel == null) {
                homeActivity.showMsgDialog(StaticMessages.SOMETHING_WRONG);
                return false;
            }
            if (!chk_terms.isChecked()) {
                homeActivity.showMsgDialog(StaticMessages.CHECK_TERM_CONDITION);
                return false;
            }

            return true;
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return false;
    }

    private void getschemedata() {
        new SIPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
    }

    private String getConfirmationMsg() {
        String _tempConfirmation = "Are you sure you want to start a SIP of Rs. " +
                Formatter.DecimalLessIncludingComma(amount);
//        if(!validChk.isChecked()){
//            _tempConfirmation = _tempConfirmation + " for "+period +" months";
//        }
        _tempConfirmation = _tempConfirmation + " ?";
        return _tempConfirmation;
    }

    class AddUPIReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String selectedUPI;
        View DialogView;


        AddUPIReq(int mCode) {
            this.msgCode = mCode;
        }

        AddUPIReq(int mCode, String upiCode, View dialogciew) {
            this.msgCode = mCode;
            this.selectedUPI = upiCode;
            this.DialogView = dialogciew;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if (msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value || msgCode == eMessageCodeWealth.BONDSAVEUPI_DETAILS.value) {
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value) {
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (msgCode == eMessageCodeWealth.IPO_UPI_DELETE.value || msgCode == eMessageCodeWealth.BONDDELETEUPI_DETAILS.value) {
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (msgCode == eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value) {
                try {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } catch (Exception e) {
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

            if (s != null) {
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if (!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        if (msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value) {
                            handleUPIVerify(jsonData, DialogView);
                        } else {
                            displayError(err);
                        }
                        //UPI ID Added Successfully

                    } else {
                        if (msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value) {
                            handleUPIVerify(jsonData, DialogView);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void handleUPIVerify(JSONObject jsonData, View dialogview) {

        try {
            TextView UpiName = dialogview.findViewById(R.id.UPIName);
            String err = jsonData.getString("error");
            if (err.toLowerCase().contains("not available") ||
                    err.toLowerCase().contains("fail") ||
                    err.toLowerCase().contains("error") ||
                    err.toLowerCase().contains("issue")) {
                UpiName.setVisibility(View.VISIBLE);
                dialogview.findViewById(R.id.okBtn).setVisibility(View.GONE);
                UpiName.setText("Invalid UPI ID.");
//                displayError("Please enter a valid UPI ID.");
            } else {
                String checkedMark = err;// + " " + "\u2713" ;
//                GlobalClass.showAlertDialog(checkedMark);
                UpiName.setVisibility(View.VISIBLE);
                UpiName.setText(checkedMark);
                dialogview.findViewById(R.id.okBtn).setVisibility(View.VISIBLE);

                //new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();
            }
            //IF SUCCESS
            //new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String ChangeSIPDate = "";


    class SIPReq extends AsyncTask<String, Void, String> {
        String otp = "";
        String AuthSrNo = "";
        int msgCode;
        String upiid = "";
        String bankid = "";
        String siptype = "";

        SIPReq(int mCode) {
            this.msgCode = mCode;
        }

        SIPReq(int mCode, String UPIid) {
            this.msgCode = mCode;
            this.upiid = UPIid;
        }

        SIPReq(int mCode, String UPIid, String bankid, String siptype) {
            this.msgCode = mCode;
            this.upiid = UPIid;
            this.bankid = bankid;
            this.siptype = siptype;
        }

        SIPReq(int mCode, String Authsrno, String OTP) {
            this.msgCode = mCode;
            this.AuthSrNo = Authsrno;
            this.otp = OTP;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {
                GlobalClass.dismissdialog();
            } else {
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
                    jdata.put(eMFJsonTag.OPTION.name, eOptionMF.SIP.name);
                    if (formScreen.equalsIgnoreCase("nfo")) {
                        jdata.put(eMFJsonTag.FOLIONO.name, jdata1.optString("FolioNo"));
                        jdata.put(eMFJsonTag.entrymode.name, "NFO");
                    } else {
                        jdata.put(eMFJsonTag.FOLIONO.name, "");
                    }
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);
                    jdata.put("TransNo", upiid);
                    jdata.put("MandateCode", GlobalClass.MANDateCode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("AuthSrNo", AuthSrNo);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_CLIENT_BANKDETAILS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.CHECK_EXISTING_SIP_MANDATE.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("SIPAmount", amount.replace(",", ""));
                    jdata.put("StartDate", start_date);
                    if (validtillcancel.equalsIgnoreCase("T")) {
                        jdata.put("EndDate", "");
                    } else {
                        jdata.put("EndDate", end_date);
                    }
                    jdata.put("ValidUntilCancelled", validtillcancel);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.CHECK_EXISTING_SIP_MANDATE.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.GET_CLIENT_MANDATE_COUNT.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_CLIENT_MANDATE_COUNT.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemecode);
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);
                    jdata.put(eMFJsonTag.OPTION.name, eOperationMF.SIP.name);//Purchase | TopUp | Redeem | SpreadOrder | Switch | SWP | STP | SIP
                    jdata.put(eMFJsonTag.FOLIONO.name, "");
                    jdata.put(eMFJsonTag.INVESTAMT.name, "");// only used for Purchase,TOpUP
                    jdata.put(eMFJsonTag.REDEEMOPT.name, "");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount//Redeem,SpreadOrder
                    jdata.put(eMFJsonTag.REDEEM.name, "");//Units | Amount //Redeem
                    jdata.put(eMFJsonTag.LIQUIDREDEEM.name, "");//B - Bank,L - Ledger//redeem
                    jdata.put(eMFJsonTag.WITHDRAWAL.name, "");//amount only used for SWP
                    jdata.put(eMFJsonTag.FREQUENCY.name, "Monthly");//Monthly | Quarterly//amount only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.EXECUTIONDAY.name, "");//amount only used for SWP
                    jdata.put(eMFJsonTag.STARTDATE.name, start_date);//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.ENDDATE.name, end_date);//date only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.PERIOD.name, period);// only used for SWP,STP,SIP
                    jdata.put(eMFJsonTag.TOSCHEMECODE.name, "");// only used for STP,SWITCH
                    jdata.put(eMFJsonTag.TRANSFERAMT.name, "");// only used for STP
                    jdata.put(eMFJsonTag.SWITCHAMT.name, "");// only used for SWITCH
                    jdata.put(eMFJsonTag.SWITCHUNITS.name, "");// only used for SWITCH
                    jdata.put(eMFJsonTag.REDEEMDATE.name, "");// only used for SPREADORDER
                    jdata.put(eMFJsonTag.SIPAMT.name, amount.replace(",", ""));// only used for SIP
                    jdata.put(eMFJsonTag.VALIDUNTILCANCEL.name, validtillcancel);//date only used for SIP T/F
                    jdata.put("OrderNo", "");//date only used for SIP T/F
                    jdata.put("TransDate", "");//date only used for SIP T/F
                    jdata.put("PaymentModeOption", paymentmode);
                    GlobalClass.PaymentMode = paymentmode;
                    jdata.put("VPAId", upiid);
                    jdata.put("AccountNo", "");
                    jdata.put("MandateCount", MandateCount + "");
                    jdata.put("BankId", bankid);
                    jdata.put("MandateAmount", amount);
                    jdata.put("SIPType", siptype);
                    jdata.put("BankDetails", bankid);


                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("SchemeCode", schemecode);

                    jdata.put("TransType", "1");
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);

                    jdata.put("InvAmount", amount.replace(",", ""));// only used for Purchase,TOpUP
                    jdata.put(eMFJsonTag.FOLIONO.name, "");

                    jdata.put(eMFJsonTag.REDEEMOPT.name, "");//1 - All Units, 2 - Partial Units,3 - All Amount,4 - Partial Amount
                    jdata.put(eMFJsonTag.REDEEM.name, "");//Units | Amount
                    jdata.put("ToSchemeCode", "");//Units | Amount
                    jdata.put("SwitchAmount", "");//Units | Amount
                    jdata.put("SwitchUnits", "");//Units | Amount
                    jdata.put("SIPAmount", "");//Units | Amount
                    jdata.put("StartDate", "");//Units | Amount
                    jdata.put("ValidUntilCancelled", validtillcancel);//Units | Amount
                    jdata.put("EndDate", "");//Units | Amount
                    jdata.put("Frequency", "");
                    jdata.put("AccountNo", "");

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("AuthSrNo", AuthSrNo);
                    jdata.put("OTP", otp);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } else if (msgCode == eMessageCodeWealth.REGISTER_SIP.value) {
                    JSONArray jarr = sipresp.getJSONArray("sipdata");
                    JSONArray jarrMandate = sipresp.getJSONArray("mandatedata");
                    JSONObject jObj = jarrMandate.optJSONObject(0);
                    Mandatemodel selectedModel = new Mandatemodel(jObj);
                    String sipType = selectedModel.getSIPType().length() > 0 ? (selectedModel.getSIPType().charAt(0) + "") : "";

                    //(selectedModel.getSIPType().equalsIgnoreCase(eSIPType.X_SIP.name)||
                    //selectedModel.getSIPType().equalsIgnoreCase(eSIPType.XSIP.name))? "X":"I";

                    JSONObject sipData = jarr.getJSONObject(0);

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.TSRNO.name, sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name, sipData.getString("SIPSrNo"));
                    if (sipType.equalsIgnoreCase("")) {
                        jdata.put(eMFJsonTag.SIPTYPE.name, "N");
                    } else {
                        jdata.put(eMFJsonTag.SIPTYPE.name, sipType);
                    }
                    jdata.put(eMFJsonTag.SIPMANDATE.name, selectedModel.getMandateCode());
                    /*
                    if(GlobalClass.MANDateCode.equalsIgnoreCase("")){
                        jdata.put(eMFJsonTag.SIPTYPE.name, "N");
                        jdata.put(eMFJsonTag.SIPMANDATE.name, "");
                    }else {
                        jdata.put(eMFJsonTag.SIPTYPE.name, sipType);
                        jdata.put(eMFJsonTag.SIPMANDATE.name, selectedModel.getMandateCode());
                    }*/
                    // jdata.put(eMFJsonTag.SIPMANDATE.name,sipSpinnerBankList.getSelectedItem().toString());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value, jdata);
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
                    String err = jsonData.toString();
                    String mainerr = jsonData.optString("error");
                    if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                        displaData(jsonData);
                    } else if (msgCode == eMessageCodeWealth.RESEND_RTA_AUTH_OTP.value) {
                        StartCountdown(otptimertext, rtaotp_et);

                    } else if (msgCode == eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value) {
                        fetcchClientBankDetails(jsonData);
                    } else if (msgCode == eMessageCodeWealth.GET_CLIENT_MANDATE_COUNT.value) {
                        MandateCountGlobal = jsonData.optInt("MandateCount");
                        new SIPReq(eMessageCodeWealth.CHECK_EXISTING_SIP_MANDATE.value).execute();

//                            fetcchClientBankDetails(jsonData);
                    } else if (msgCode == eMessageCodeWealth.CHECK_EXISTING_SIP_MANDATE.value) {
                        ChangeSIPDate = jsonData.optString("ChangeSIPDate");
                    }
                    if (msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {

                        if (err.contains("PENDING")) {
                        } else {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                            ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.mandate_eligible, viewGroup, false);
                            Button OK_btn = dialogView.findViewById(R.id.OK_btn);
                            TextView mandatetext2 = dialogView.findViewById(R.id.mandatetext2);
                            TextView mandatetext = dialogView.findViewById(R.id.mandatetext);

                            mandatetext.setText("Your payment for first installment has been successful vide transaction no." + TransNo + ".");
                            mandatetext2.setVisibility(View.GONE);
                            OK_btn.setText("OK");


                            OK_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    /*if(URL.length() > 0) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                                        homeActivity.startActivity(browserIntent);
                                        alertDialog1.dismiss();
                                    }else {
                                        alertDialog1.dismiss();
                                    }*/
                                    alertDialog1.dismiss();
                                    if (GlobalClass.SigleMandate == 0) {
                                        homeActivity.FragmentTransaction(CreateMandateFragmentNew.newInstance(schemecode, schemename, start_date, end_date, amount, period, validtillcancel), R.id.container_body, true);
                                    } else {
                                        GlobalClass.fragmentTransaction(new MutualFundMenuNew(), R.id.container_body, true, "");
                                    }
                                }
                            });
                            builder.setView(dialogView);
                            if (alertDialog1 != null) {
                                alertDialog1.dismiss();
                            }
                            alertDialog1 = builder.create();
                            alertDialog1.show();
                            if (timerPaymentStatusDisplay != null) {
                                timerPaymentStatusDisplay.cancel();
                            }
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                        }

                    } else if (msgCode == eMessageCodeWealth.SAVE_INVESTMENT_DATA.value) {
                        handleSaveInvestResp(jsonData);

                    } else if (msgCode == eMessageCodeWealth.GENERATE_RTAAUTHENTICATION_OTP.value) {
                        String[] separated = mainerr.split("\\|");
                        OTPtext = separated[0] + "|" + separated[1];
                        if (separated[2].equalsIgnoreCase("T")) {
                            showOtpVerificationPopup("");
                        } else {
                            //new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value, upiid).execute();
                            new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,upiid,GlobalClass.mandatebankid,GlobalClass.mandateSipType).execute();
                        }

                    } else if (msgCode == eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value) {
                        if (mainerr.contains("Invalid")) {
                            //showOtpVerificationPopup(err);
                            updateAlertDialog(err);
                        } else {
                            if (alertDialogforOtpVer.isShowing()) {
                                alertDialogforOtpVer.dismiss();
                                showOtpSuccessAlert(mainerr);
                            }
                        }
                    } else if (msgCode == eMessageCodeWealth.REGISTER_SIP.value) {
                        handleSaveSIPData(jsonData);
                        GlobalClass.mandateSIPamount = amount.replace(",", "");

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

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
                //new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value).execute();
                new SIPReq(eMessageCodeWealth.SAVE_INVESTMENT_DATA.value,upiEditText.getText().toString(),GlobalClass.mandatebankid,GlobalClass.mandateSipType).execute();

                otpsucccessalert.dismiss();

            }
        });

        builder.setView(dialogView);
        otpsucccessalert = builder.create();
        otpsucccessalert.show();

    }

    private void fetcchClientBankDetails(JSONObject jsonData) {
        try {
            GlobalClass.jsonArrayClientBank = jsonData.getJSONArray("data");
            new SIPReq(eMessageCodeWealth.GET_CLIENT_MANDATE_COUNT.value).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displaData(JSONObject jsonData) {
        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedetailsdata");
            if (jschdetailDataArr.length() > 0) {
                jschdetailData = jschdetailDataArr.getJSONObject(0);
                displayschemedata();

            }
            new SIPReq(eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value).execute();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    private void displayschemedata() {

        try {
            //if(efrequency == eMFFrequency.MONTHLY) {

            String multipleOf = jschdetailData.getString("MSIPMultAmt");
            String minimumAmt = jschdetailData.getString("MSIPMinInstallmentAmt");

            _minimumAmt = StaticMethods.StringToDouble(minimumAmt);
            _multipleOf = StaticMethods.StringToDouble(multipleOf);


            schemecode = jschdetailData.getString("SchemeCode");
            sipModel = new SIPModel();
            sipModel.minAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMinInstallmentAmt"));
            sipModel.maxAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMaxInstallmentAmt"));
            sipModel.multipleAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMultAmt"));

            sipModel.minPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMinInstallmentNumbers"));
            sipModel.maxPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMaximumInstallmentNumbers"));
            String sipdays = jschdetailData.getString("MSIPDates");
            sipModel.populateDate(sipdays);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void displayWebView(String err) {
        homeActivity.FragmentTransaction(WebViewForMF.newInstance(err), R.id.container_body, true);
    }

    private void displayError(String err) {
        GlobalClass.showAlertDialog(err);
    }

    private void handleSaveSIPData(JSONObject jsonData) {
        String err = jsonData.optString("error");
        if (err.contains("html")) {
            displayWebView(err);

        } else if (err.contains("UPI|")) {
            CheckPaymentstatus(err.substring(4));
        } else if (err.contains("https")) {
            err = err.substring(4);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
            startActivity(browserIntent);
        }
        /*else if (err.contains("99|")) {
            goToFundtransferPage();
        }*/
        else {
            displayError(err.replace("100|", ""));
        }
    }

    private void goToFundtransferPage() {
        //err = err.replaceAll("\n","");
        MFFundTransfer ls = MFFundTransfer.newInstance();
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
    }


    CountDownTimer timerPaymentStatusDisplay;
    int timerCoutForPaymentStatus = 0;

    private void CheckPaymentstatus(String substring) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.timerpopup, viewGroup, false);
        TextView timetext = dialogView.findViewById(R.id.timetext);
        builder.setView(dialogView);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        if (timerPaymentStatusDisplay != null) {
            timerPaymentStatusDisplay.cancel();
            timerPaymentStatusDisplay = null;
        }
        timerCoutForPaymentStatus = 0;

        timerPaymentStatusDisplay = new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {

                Date date = new Date(millisUntilFinished);
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = formatter.format(date);
                timetext.setText(formatted);
                timerCoutForPaymentStatus++;
                if (timerCoutForPaymentStatus % 10 == 0) {
                    TransNo = substring;
                    new GetTaskFirst(substring).execute();
                }
            }

            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
    }

    private JSONObject sipresp = null;

    private void handleSaveInvestResp(JSONObject jsonData) {
        try {
            GlobalClass.log("SaveInvestResp : " + jsonData.toString());
            String TransNo = jsonData.optString("TransNo");
            GlobalClass.TransNo = TransNo;
            GlobalClass.MANDateCode = jsonData.optString("MandateCode");
            JSONArray jarrMandate = jsonData.getJSONArray("mandatedata");
            //JSONArray jarr = new JSONArray();
           /* for (int i = 0; i < jarrMandate.length(); i++) {
                JSONObject jsonObject = jarrMandate.optJSONObject(i);
                String MandateStatus = jsonObject.optString("MandateStatus");
                if(MandateStatus.equalsIgnoreCase("APPROVED")){
                    jarr.put(jsonObject);
                }
            }*/
            if (jarrMandate.length() == 0 || jarrMandate.length() == 1) {
                sipresp = jsonData;
                GlobalClass.SigleMandate = jarrMandate.length();

                new SIPReq(eMessageCodeWealth.REGISTER_SIP.value).execute();
            } else {
                //Go to mandate page for invest
                GlobalClass.SigleMandate = 2;
                homeActivity.FragmentTransaction(SipMandateForInvest.newInstance(jsonData, eFormScr.ORDERDEATILS.value,
                        selectedConfirmationMsg, start_date.replace("/", "-"), StaticMethods.getStringToDouble(amount)), R.id.container_body, false);
                GlobalClass.MANDateCode = "";

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                if (UserSession.getClientResponse().isNeedAccordLogin() || !VenturaServerConnect.accordSessionCheck("FundTransfer")) {
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("") && VenturaServerConnect.connectToWealthServer(true)) {
                        return "";
                    }
                    return clientLoginResponse.charResMsg.getValue();
                } else {
                    if (VenturaServerConnect.connectToWealthServer(true)) {
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
                if (result.equalsIgnoreCase("")) {
                    new SIPReq(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, upitransId).execute();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

    String OTPtext = "";
    TextView otptimertext;
    EditText rtaotp_et;

    private void updateAlertDialog(String err) {
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
        if (err.length() > 0) {
            TIL_Otp.setError(err);
        } else {
            StartCountdown(otptimertext, resend_btn);
            otp_authentication_text.setText(messege);
        }

        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend_btn.setVisibility(View.GONE);
                StartCountdown(otptimertext, resend_btn);
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rtaotp_et.getText().length() > 0) {
                    new SIPReq(eMessageCodeWealth.VALIDATE_RTA_AUTH_OTP.value, Authsrno, rtaotp_et.getText().toString()).execute();
                } else {
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

    private void StartCountdown(TextView timertext, TextView resendButton) {
        timertext.setVisibility(View.VISIBLE);
        if (timerPaymentStatusDisplay != null) {
            timerPaymentStatusDisplay.cancel();
            timerPaymentStatusDisplay = null;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat("ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerPaymentStatusDisplay = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                Date date = new Date(millisUntilFinished);
                String formatted = formatter.format(date);
                timertext.setText("Resend OTP after " + formatted + " sec");
            }

            public void onFinish() {
                timertext.setVisibility(View.INVISIBLE);
                resendButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}