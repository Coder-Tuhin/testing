package wealth.new_mutualfund.newMF;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import view.EtRegular;
import wealth.Dialogs;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.menus.MFFundTransfer;
import wealth.new_mutualfund.menus.WebViewForMF;
import wealth.new_mutualfund.menus.modelclass.Mandatemodel;

public class CreateMandateFragmentNew extends Fragment {

    private HomeActivity homeActivity;

    private static String SCHEMECODE = "schemecode";
    private static String SCHEMENAME = "schemename";
    private static String START_DATE = "start_date";
    private static String END_DATE = "end_date";
    private static String SELECTED_SIP_AMOUNT = "selected_sip_amount";
    private static String PEROID = "period";
    private static String VALID_TILL_CANCEL = "valid_till_cancel";
    private static String BANKID = "bankid";
    private static String SIPTYPE = "SipType";
    androidx.appcompat.app.AlertDialog alertDialog1;
    String TransNo = "";
    HashMap<String,String> llist = new HashMap<>();


    androidx.appcompat.app.AlertDialog alertDialog;
    androidx.appcompat.app.AlertDialog alertDialogformandate;
    androidx.appcompat.app.AlertDialog successMandateAlert;
    private String paymentmode = "1";
    private String schemecode = "", schemename = "", start_date = "", end_date = "", amount ="100", period = "", validtillcancel ="";
    EtRegular mandateamt;
    ImageView Info_icon;
    TextView saveMandate, tv_10000, tv_25000, tv_50000, bank_tv;
    Spinner sipSpinnerBankList;
    private String selectedConfirmationMsg = "";
    private JSONObject sipresp = null;
    String URL = "";
    String BankId = "",SipType = "";


    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }
    public static CreateMandateFragmentNew newInstance(String schemecode, String schemename, String start_date, String end_date, String amt, String period, String validtillcancel) {
        CreateMandateFragmentNew fragment = new CreateMandateFragmentNew();
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


    public static CreateMandateFragmentNew newInstance(String schemecode, String schemename, String start_date, String end_date, String amt, String period, String validtillcancel,String BankID,String SIPType) {
        CreateMandateFragmentNew fragment = new CreateMandateFragmentNew();
        Bundle args = new Bundle();
        args.putString(SCHEMECODE, schemecode);
        args.putString(SCHEMENAME, schemename);
        args.putString(START_DATE, start_date);
        args.putString(END_DATE, end_date);
        args.putString(SELECTED_SIP_AMOUNT, amt);
        args.putString(PEROID, period);
        args.putString(VALID_TILL_CANCEL, validtillcancel);
        args.putString(BANKID, BankID);
        args.putString(SIPTYPE, SIPType);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.createmandatescreen, container, false);
        Bundle args = getArguments();
        try {
            schemecode = args.getString(SCHEMECODE, "");
            start_date = args.getString(START_DATE, "");
            end_date = args.getString(END_DATE, "");
            amount = args.getString(SELECTED_SIP_AMOUNT, "");
            schemename = args.getString(SCHEMENAME, "");
            validtillcancel = args.getString(VALID_TILL_CANCEL, "");
            period = args.getString(PEROID, "");
            BankId = args.getString(BANKID, "");
            SipType = args.getString(SIPTYPE, "");
        }catch (Exception e){
            e.printStackTrace();
        }

        mandateamt = mView.findViewById(R.id.mandateamt);
        saveMandate = mView.findViewById(R.id.saveMandate);
        Info_icon = mView.findViewById(R.id.Info_icon);

        sipSpinnerBankList = mView.findViewById(R.id.sipSpinnerBankList);
        tv_10000 = mView.findViewById(R.id.tv_10000);
        tv_25000 = mView.findViewById(R.id.tv_25000);
        tv_50000 = mView.findViewById(R.id.tv_50000);
        bank_tv = mView.findViewById(R.id.bank_tv);
        try {
            int amt = Integer.parseInt(amount);
            if (amt > 25000) {
                mandateamt.setText(amt + "");
            } else {
                mandateamt.setText("25000");
            }


        }catch (Exception e){
            e.printStackTrace();
            mandateamt.setText("25000");
            amount = GlobalClass.mandateSIPamount;
        }

        Info_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitLoadWindow("", homeActivity.getResources().getString(R.string.mandate_icon_info),
                        homeActivity.getResources().getDimension(R.dimen.text_16));
            }
        });

        tv_10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mandateamt.setText("10000");
            }
        });
        tv_25000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mandateamt.setText("25000");
            }
        });
        tv_50000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mandateamt.setText("50000");
            }
        });

        new SIPReq(eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value).execute();
        saveMandate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidateAmount()) {
                    String totalbanktext = "";
                    if (llist.size() > 2 && sipSpinnerBankList.getSelectedItem().toString().equalsIgnoreCase("Select Bank")) {
                        GlobalClass.showAlertDialog("Please Select Mandate Bank", false);
                    } else if (llist.size() == 2) {
                        totalbanktext = getValue(llist, bank_tv.getText().toString());
                    } else {
                        totalbanktext = getValue(llist, sipSpinnerBankList.getSelectedItem().toString());
                    }
                    if(llist.size() > 2 && sipSpinnerBankList.getSelectedItem().toString().equalsIgnoreCase("Select Bank")){
                        GlobalClass.showAlertDialog("Please Select Mandate Bank", false);
                    }else {
                        String[] bankarray = totalbanktext.split("#");
                        String Bankid = bankarray[0];
                        String Siptype = bankarray[1];
                        new SIPReq(eMessageCodeWealth.SAVE_SIPMANDATE_DATA_NEW.value,"",Bankid,Siptype).execute();
                    }

                }
            }
        });
        return mView;
    }

    private void showExitLoadWindow(String title, String value, float titleTextSize) {
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.fact_sheet_exit_load_dialog_layout);

            TextView titleTv = dialog.findViewById(R.id.title);
            //titleTv.setTextSize(titleTextSize);
            titleTv.setText(title);
            titleTv.setVisibility(View.GONE);

            TextView exit_value_tv = dialog.findViewById(R.id.exit_value_tv);
            exit_value_tv.setText(value); //exitLoadValue

            ImageView closeAlert = dialog.findViewById(R.id.closeTv);
            closeAlert.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ValidateAmount() {
        try {
            int MandateAmt = Integer.parseInt(mandateamt.getText().toString());
            int SIPAMT = Integer.parseInt(amount);
            if (MandateAmt < SIPAMT) {
                GlobalClass.showAlertDialog("Mandate amount cannot be less than SIP amount.", false);
                return false;
            } else if (MandateAmt < 5000) {
                GlobalClass.showAlertDialog("Minimum mandate amount is Rs. 5,000", false);
                return false;
            } else if (MandateAmt > 100000) {
                GlobalClass.showAlertDialog("Maximun mandate amount is Rs. 1,00,000", false);
                return false;
            } else {
                return true;
            }
        }catch (Exception e){

            e.printStackTrace();
            return true;
        }

    }

    public static <K, V> V getValue(Map<K, V> map, K key) {

        return map.get(key);
    }

    private void setSpinnerData(Spinner spinner, HashMap<String, String> data) {
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity, R.layout.sip_spn_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data.keySet());
        spinner.setAdapter(categoryAdp);
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

    class SIPReq extends AsyncTask<String, Void, String> {
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {
                GlobalClass.dismissdialog();
            }else if(msgCode == eMessageCodeWealth.SAVE_SIPMANDATE_DATA_NEW.value){
                GlobalClass.dismissdialog();
                generateLinkPopup(true);
            }else if(msgCode == eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value){
                generateMandatePopup(true);
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
                    jdata.put(eMFJsonTag.FOLIONO.name, "");
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                else if(msgCode == eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value){

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if (msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {
                    JSONObject jsonObject = sipresp;
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);
                    jdata.put("TransNo", upiid);
                    jdata.put("MandateCode", "");
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }

                } else if (msgCode == eMessageCodeWealth.GET_CLIENT_BANKDETAILS.value) {
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
                    jdata.put("EndDate", end_date);
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
                }  else if (msgCode == eMessageCodeWealth.REGISTER_SIP.value) {
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
                    jdata.put(eMFJsonTag.TSRNO.name, sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name, sipData.getString("SIPSrNo"));
                    jdata.put(eMFJsonTag.SIPTYPE.name, "N");
                    jdata.put(eMFJsonTag.SIPMANDATE.name, "");
                    jdata.put("TodaysFirstOrder", "Y");

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if (msgCode == eMessageCodeWealth.SAVE_SIPMANDATE_DATA_NEW.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("TransNo", "");
                    jdata.put("BankId", bankid);
                    jdata.put("MandateAmount", mandateamt.getText().toString());
                    jdata.put("SIPType", siptype);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.SAVE_SIPMANDATE_DATA_NEW.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if (msgCode == eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value) {

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("mandatecode", upiid);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value, jdata);
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

                    if (msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value) {

                        if (err.contains("PENDING")) {

                        } else {
                            String err1 = jsonData.getString("error");

                            String[] arrStrings = err1.split("[|]", 0);
                            for (int i = 0; i < arrStrings.length; i++) {
                                GlobalClass.log("position :" + i + "String : " + arrStrings[i]);
                                if (i == 3) {
                                    URL = arrStrings[i];

                                }
                            }

                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
                            ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
                            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.mandate_eligible, viewGroup, false);
                            Button OK_btn = dialogView.findViewById(R.id.OK_btn);
                            TextView mandatetext2 = dialogView.findViewById(R.id.mandatetext2);
                            TextView mandatetext = dialogView.findViewById(R.id.mandatetext);
                            if (URL.length() > 0) {
                                mandatetext.setText("Your payment for first installment has been successful vide transaction no. " + TransNo + ". Kindly proceed to authenticate your AutoPay Mandate.");
                                OK_btn.setText("Proceed");
                                mandatetext2.setVisibility(View.VISIBLE);
                            } else {
                                mandatetext.setText("Your payment for first installment has been successful vide transaction no." + TransNo + ".");
                                mandatetext2.setVisibility(View.GONE);
                                OK_btn.setText("OK");

                            }
                            OK_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String totalbanktext = "";
                                    if (sipSpinnerBankList.getSelectedItem().toString().equalsIgnoreCase("Select Bank")) {
                                        GlobalClass.showAlertDialog("Please Select Mandate Bank", false);
                                    } else if (llist.size() == 2) {
                                        totalbanktext = getValue(llist, bank_tv.getText().toString());
                                    } else {
                                        totalbanktext = getValue(llist, sipSpinnerBankList.getSelectedItem().toString());
                                    }
                                    if(sipSpinnerBankList.getSelectedItem().toString().equalsIgnoreCase("Select Bank")){
                                        GlobalClass.showAlertDialog("Please Select Mandate Bank", false);
                                    }else {
                                        String[] bankarray = totalbanktext.split("#");
                                        String Bankid = bankarray[0];
                                        String Siptype = bankarray[1];
                                        new SIPReq(eMessageCodeWealth.SAVE_SIPMANDATE_DATA_NEW.value,"",Bankid,Siptype).execute();
                                    }
                                }
                            });
                            builder.setView(dialogView);
                            alertDialog1 = builder.create();
                            alertDialog1.show();

                            if (timer != null) {
                                timer.cancel();

                            }
                            if (timer2 != null) {
                                timer2.cancel();

                            }
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
                        }

                    } else if(msgCode == eMessageCodeWealth.GET_CLIENT_BANK_DETAILS.value){
                        handleBankResponse(jsonData);

                    }else if(msgCode == eMessageCodeWealth.SAVE_SIPMANDATE_DATA_NEW.value){
                        handleSAVESIPMANDATEDATAResponse(jsonData);
                    } else  if(msgCode == eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value){
                        if(err.contains("UNDER PROCESSING")){
                            String responseJson = jsonData.optString("error");
                            generateMandatePopup(false);
                            ShowSuccessMandatePopUp();
                        }else {
                            new SIPReq(eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value,GlobalClass.MANDateCode).execute();
                        }
                    }else if (msgCode == eMessageCodeWealth.REGISTER_SIP.value) {
                        String err1 = jsonData.getString("error");
                        if (err.contains("html")) {
//                            CheckPaymentStatusForNetBanking(GlobalClass.TransNo);
                            displayWebView(err1);
                        } else {
                            handleSaveSIPData(jsonData);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(GlobalClass.checkPaymentstatusTag) {
            new GetTaskFirst().execute();

        }
    }

    private void handleSAVESIPMANDATEDATAResponse(JSONObject jsonData) {
        generateLinkPopup(false);
        try {
//            String SIPType = jsonData.optString("SIPType");
            String MandateCode = jsonData.optString("MandateCode");
            String Urlresponse = jsonData.optString("Response");
            GlobalClass.MANDateCode = MandateCode;
            if(Urlresponse.contains("https")){
                String URL = Urlresponse.substring(4);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(URL));
                getActivity().startActivity(i);
//                homeActivity.FragmentTransaction(WebViewForMAndate.newInstance(URL), R.id.container_body, true);

                GlobalClass.checkPaymentstatusTag =true;
            }else {
                new SIPReq(eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value,MandateCode).execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void generateLinkPopup(boolean tag){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.generatelinkpopup, viewGroup, false);
        builder.setView(dialogView);
        builder.setCancelable(false);
        if(tag) {
            if(alertDialog == null) {
                alertDialog = builder.create();
                alertDialog.show();
            }
        }else {
            alertDialog.dismiss();
        }
    }

    private void generateMandatePopup(boolean tag){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.createmandate, viewGroup, false);


        builder.setView(dialogView);
        builder.setCancelable(false);

        if(tag) {

            if(alertDialogformandate == null) {
                alertDialogformandate = builder.create();
                alertDialogformandate.show();
            }

        }else {
            alertDialogformandate.dismiss();
        }
    }
    private void ShowSuccessMandatePopUp(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(homeActivity);
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.showsuccessmandatepopup, viewGroup, false);

        Button ok_button = dialogView.findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successMandateAlert.dismiss();
                GlobalClass.fragmentTransaction(new MutualFundMenuNew(), R.id.container_body, true, "");

            }
        });

        builder.setView(dialogView);
        builder.setCancelable(false);


        if(successMandateAlert == null) {
            successMandateAlert = builder.create();
            successMandateAlert.show();
        }
    }

    CountDownTimer timer3;
    private void handleBankResponse(JSONObject jsonObject){
        try {
            JSONArray jarr = jsonObject.optJSONArray("data");
            llist = new HashMap<>();
            llist.put("Select Bank","DEFAULT|1");
            if(jarr != null){
                JSONObject obj = new JSONObject();
                try {
                    for (int i = 0 ; i < jarr.length() ; i++){
                        obj = jarr.getJSONObject(i);
                        llist.put(obj.getString("Text"),obj.getString("Value")+"#"+obj.getString("SIPType"));
                    }
                    if(llist.size() == 2){
                        sipSpinnerBankList.setVisibility(View.GONE);
                        bank_tv.setVisibility(View.VISIBLE);
                        String Value = obj.optString("Text");
                        bank_tv.setText(Value);
                    } else {
                        setSpinnerData(sipSpinnerBankList, llist);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void handleSaveSIPData(JSONObject jsonData) {
        String err = jsonData.toString();
        if (err.contains("html")) {

            displayWebView(err);

        } else if (err.contains("UPI|")) {
            String UPITransID = jsonData.optString("error");
            CheckPaymentstatus(UPITransID.substring(4));
        } else if (err.contains("https")) {
            err = err.substring(4);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
            startActivity(browserIntent);
        } /*else if (err.contains("99|")) {
            goToFundtransferPage();
        } */else {
            displayError(err.replace("100|", ""));

        }

    }

    private void goToFundtransferPage() {
        //err = err.replaceAll("\n","");
        MFFundTransfer ls = MFFundTransfer.newInstance();
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
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
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = formatter.format(date);
                timetext.setText(formatted);

            }

            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
        timer2 = new CountDownTimer(300000, 10000) {

            public void onTick(long millisUntilFinished) {
                TransNo = substring;
                new SIPReq(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, substring).execute();

            }

            public void onFinish() {

            }
        }.start();
    }


    private void displayWebView(String err) {
        homeActivity.FragmentTransaction(WebViewForMF.newInstance(err), R.id.container_body, false);
    }

    class GetTaskFirst extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
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
                mDialog.dismiss();
                if(result.equalsIgnoreCase("")) {
                    new SIPReq(eMessageCodeWealth.GET_CHECK_MANDATE_STATUS.value, GlobalClass.MANDateCode).execute();
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }




    private void displayError(String err) {
        GlobalClass.showAlertDialog(err);
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
}
