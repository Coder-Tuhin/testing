package fragments.simplysave;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Structure.simplysave.SimplysaveResp;
import Structure.simplysave.bankReq;
import Structure.simplysave.withdrawReq;
import butterknife.ButterKnife;
import butterknife.BindView;
import connection.SendDataToSimplysaveServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import models.InvestModel;
import utils.Constants;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.PreferenceHandler;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 05-Jun-2017.
 */
@SuppressLint("ValidFragment")
public class WithdrawFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener{

    private static String TAG= WithdrawFragment.class.getSimpleName();
    @BindView(R.id.proceed)Button proceed;
    @BindView(R.id.withdrawRG)RadioGroup withdrawRG;
    @BindView(R.id.amountRd)RadioButton amountRd;
    @BindView(R.id.unitsRd)RadioButton unitsRd;

    @BindView(R.id.mktValue)TextView mktValue;
    @BindView(R.id.withdrawable)TextView withdrawable;
    @BindView(R.id.amountEt)EditText amountEt;

    @BindView(R.id.bank_spinner)Spinner bank_spinner;

    private InvestModel investModel;

    public  WithdrawFragment(){super();}
    public WithdrawFragment(InvestModel investModel){
       this.investModel = investModel;
    }

    private String unitamtFlag = "A";
    private String formattedDate="";
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.withdraw_screen, null);
        ButterKnife.bind(this, view);
        mktValue.setText(investModel.mktValue);
        withdrawable.setText(investModel.instawithdrableAmount);
        bankDetailReq();
        if (PreferenceHandler.getUnitWithdraw()){
            unitsRd.setVisibility(View.VISIBLE);
            withdrawRG.setOnCheckedChangeListener(this);
        }else {
            unitsRd.setVisibility(View.GONE);
        }
        proceed.setOnClickListener(this);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    private void bankDetailReq() {
        try{
            bankReq bankreq = new bankReq();
            bankreq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            bankreq.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(VenturaApplication.getPackage(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            bankreq.appVersion.setValue(pInfo.versionName);
            bankreq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.bankDetail(bankreq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isAmtShown = false,isunitShown = false;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id){
            case R.id.amountRd:
                unitamtFlag = "A";
                if (!isAmtShown){
                    isAmtShown = true;
                    showdialog(Constants.INSTANT_WITHDRAW_AMT);
                }
                amountEt.setText("");
                amountEt.setEnabled(true);
                break;
            case R.id.unitsRd:
                unitamtFlag = "U";
                if (!isunitShown){
                    isunitShown = true;
                    showdialog(Constants.INSTANT_WITHDRAW_UNIT);
                }
                amountEt.setText(investModel.allUnits);
                amountEt.setEnabled(false);
                break;
        }
    }


    private void showdialog(String msg){
        new AlertDialog.Builder(getContext())
                .setTitle("Message")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.withdrawHandler = new WithdrawHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.withdrawHandler = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.proceed:
                boolean isValid = true;
                if (TextUtils.isEmpty(amountEt.getText().toString().trim())) {
                    GlobalClass.showToast(GlobalClass.latestContext,"Please Enter amount");
                    isValid = false;
                }else {
                    double enteredAmount = Double.parseDouble(amountEt.getText().toString());
                    if(unitamtFlag.equalsIgnoreCase("A")){
                        if(enteredAmount < Double.parseDouble(investModel.minAmount)){
                            GlobalClass.showToast(GlobalClass.latestContext,"Kindly note minimum withdrawable amount should be \u20B9. 100/- and in multiples of \u20B9. 100/-.");
                            isValid = false;
                        }
                        else if(enteredAmount > investModel.doubleMktValue){
                            GlobalClass.showToast(GlobalClass.latestContext,"Kindly note amount should be less than \u20B9. " + investModel.mktValue);
                            isValid = false;
                        }
                        else if((enteredAmount%100) > 0){
                            GlobalClass.showToast(GlobalClass.latestContext,"Kindly note amount should be in multiples of \u20B9. 100");
                            isValid = false;
                        }
                    }
                    else if(unitamtFlag.equalsIgnoreCase("U")){

                        isValid = true;
                        /*if(enteredAmount < Double.parseDouble(investModel.minUnits)){
                            GlobalClass.showToast(GlobalClass.latestContext,"Kindly note Minimum withdrawable Units is "+investModel.minUnits);
                            isValid = false;
                        }*/
                    }
                }
                if(isValid) {
                    sendDatatoServer();
                }
                break;
        }
    }

    private void sendDatatoServer() {
        try {
            GlobalClass.showProgressDialog("Please wait...");
            withdrawReq withdrawReq = new withdrawReq();
            withdrawReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            // withdrawReq.amount.setValue(0);
            withdrawReq.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            if(unitamtFlag.equalsIgnoreCase("U")){
                withdrawReq.redFlag.setValue("F");
               // withdrawReq.unitAmtValue.setValue(0.00);
            }
            else{
                withdrawReq.redFlag.setValue("P");
            }
            withdrawReq.unitAmtValue.setValue(formatedPrice());
            withdrawReq.unitamtFlag.setValue(unitamtFlag);
            withdrawReq.Tpin.setValue("");
            withdrawReq.Mstatus.setValue("");
            withdrawReq.Fname.setValue("");
            withdrawReq.Lname.setValue("");
            withdrawReq.Mname.setValue("");
            withdrawReq.Cuttime.setValue("");
            withdrawReq.pangno.setValue("");
            withdrawReq.bank.setValue(bank_spinner.getSelectedItem().toString());
            withdrawReq.ip.setValue("");
            withdrawReq.oldihno.setValue("");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat(Constants.DDMMMYYYY);
            formattedDate = df.format(c.getTime());
            withdrawReq.trdate.setValue(formattedDate);
            withdrawReq.entdate.setValue(formattedDate);
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(VenturaApplication.getPackage(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            withdrawReq.appVersion.setValue(pInfo.versionName);
            withdrawReq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.sendWithDrawDetail(withdrawReq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Double formatedPrice(){
        Double value = Double.parseDouble(amountEt.getText().toString().trim());
        DecimalFormat df;
        if (unitamtFlag.equalsIgnoreCase("A")){
            df = new DecimalFormat("#.00");
        }else {
            df = new DecimalFormat("#.000");
        }
        return Double.parseDouble(df.format(value));
    }
    class WithdrawHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case WITHDRAW:
                                handleResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            case BANKDETAIL:
                                handleBankResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleBankResponse(byte[] byteArray) {
        SimplysaveResp ssr = new SimplysaveResp(byteArray);
        String response = ssr.response.getValue();
        try {
            ArrayList<String> bankList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0;i<jsonArray.length();i++){
              JSONObject jsonObj = jsonArray.optJSONObject(i);
                bankList.add(jsonObj.optString("BankName"));
            }
            ArrayAdapter<String> bankAdapter = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item);
            bankAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
            bankAdapter.addAll(bankList);
            bank_spinner.setAdapter(bankAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void handleResponse(byte[] bytes) {
        GlobalClass.dismissdialog();
        SimplysaveResp ssr = new SimplysaveResp(bytes);
        String response = ssr.response.getValue();
        String instaStatus ="";
        String msg="";
        try {
            JSONObject jsonObj = new JSONObject(response);
            String tempmsg = "";
            if (jsonObj.has("message")){
                tempmsg = jsonObj.optString("message","");
            }
           if (TextUtils.isEmpty(tempmsg) && jsonObj.has("Return_Msg")){
               tempmsg = jsonObj.optString("Return_Msg","");
           }
           try {
               instaStatus = jsonObj.optString("InstaStatus", "");
               if (instaStatus.equalsIgnoreCase("N")) {
                   msg = "Your Redemption request received on "+formattedDate+
                           " in Nippon India Liquid Fund under Folio No "+ UserSession.getLoginDetailsModel().getFolioNumber()+" is under process."+
                           " Redemption amount will be directly credited to your bank account within 1 working day.";
               }
           }catch(Exception e){
               e.printStackTrace();
           }
            if(!msg.equalsIgnoreCase("")) {
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                        getActivity()).create();

                // Setting Dialog Title
                alertDialog.setTitle("");
                // Setting Dialog Message
                alertDialog.setMessage(msg);
                // Setting Icon to Dialog
                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
            if(unitamtFlag.equalsIgnoreCase("U")) {
                PreferenceHandler.setUnitWithdraw(false);
            }
            GlobalClass.showToast(GlobalClass.latestContext,tempmsg);
            GlobalClass.fragmentManager.popBackStackImmediate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
