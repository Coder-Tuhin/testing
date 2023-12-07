
package wealth.new_mutualfund.newMF;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.QuickTransSipFragment;
import wealth.new_mutualfund.menus.MFFundTransfer;
import wealth.new_mutualfund.menus.WebViewForMF;

public class MandateStepsFragment extends Fragment {
    private String todaysfirstorder = "Y";
    private HomeActivity homeActivity;
    TextView call_assist_text;
    TextView submitbtn;
    private AlertDialog alertDialog;
    private static String COnfNo = "";
    private static int fromScreenTag = 0;
    private static double selectedSipAmount = 0;
    private static JSONObject sipResp = new JSONObject();
    public static MandateStepsFragment newInstance(String ConfNo, int tag, JSONObject JSONObject,double selectedsipAmt){
        MandateStepsFragment fragment = new MandateStepsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        COnfNo = ConfNo;
        fromScreenTag  = tag;
        sipResp = JSONObject;
        selectedSipAmount = selectedsipAmt;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.mandatesteps,container,false);
        call_assist_text = mView.findViewById(R.id.call_assist_text);
        submitbtn = mView.findViewById(R.id.submitbtn);
        call_assist_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            123);
                } else {
                    String uri = "tel:02267547042" ;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }

            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromScreenTag == 1 || fromScreenTag == 2 || fromScreenTag == 3) {
                    homeActivity.FragmentTransaction(QuickTransSipFragment.newInstance(new JSONObject(), "QuickTransact"), R.id.container_body, false);
                } else if (fromScreenTag == 4) {
                    if (!COnfNo.isEmpty()) {
                        new SIPMandateReq(eMessageCodeWealth.REGISTER_SIP.value).execute();


                    }
                }
            }
        });
        return mView;
    }

    class SIPMandateReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String UPItext;
        SIPMandateReq(int mCode){
            this.msgCode = mCode;
        }
        SIPMandateReq(int mCode,String UPItext){
            this.msgCode = mCode;
            this.UPItext = UPItext;
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
                if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){

                    JSONArray jarr = sipResp.getJSONArray("sipdata");
                    JSONObject sipData = jarr.getJSONObject(0);

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    jdata.put(eMFJsonTag.TSRNO.name, sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name, sipData.getString("SIPSrNo"));
                    jdata.put(eMFJsonTag.SIPTYPE.name, "E");
                    jdata.put(eMFJsonTag.SIPMANDATE.name, COnfNo);
                    jdata.put("TodaysFirstOrder",todaysfirstorder);


                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.REGISTER_SIP.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value){
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("TransClientType", VenturaServerConnect.mfClientType);
                    jdata.put("TransNo",UPItext);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, jdata);
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
                        if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            if(err.contains("html")){

                                displayWebView(err);

                            }else if (err.contains("UPI|")) {
                                CheckPaymentstatus(err.substring(4));
                            }

                            else if(err.contains("https")){
                                err = err.substring(4);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(err));
                                startActivity(browserIntent);
                            }
                            /*else if (err.contains("99|")) {
                                goToFundtransferPage();
                            }*/else {
                                displayError(err.replace("100|", ""));

                            }
                            if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
//                                homeActivity.onFragmentBack();
                            }
                        }else if(msgCode == eMessageCodeWealth.CHECK_PAYMENTSTATUS.value){

                            if (err.contains("PENDING")) {

                            }
                            else {
                                GlobalClass.showAlertDialog(err.replace("|", ""));
                                if(timer != null) {
                                    timer.cancel();

                                }
                                if(timer2 != null) {
                                    timer2.cancel();

                                }
                                if(alertDialog != null) {
                                    alertDialog.dismiss();
                                }
                            }

                        }else {

//                            displayError(err);
                        }
                    }else {
                        if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            displayError("Sip transaction has been successfully completed");
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


                new SIPMandateReq(eMessageCodeWealth.CHECK_PAYMENTSTATUS.value, substring).execute();

            }

            public void onFinish() {

            }
        }.start();
    }

    private void goToFundtransferPage() {
        //err = err.replaceAll("\n","");
        MFFundTransfer ls = MFFundTransfer.newInstance();
        homeActivity.FragmentTransaction(ls, R.id.container_body, false);
    }

    private void displayWebView(String err){
        homeActivity.FragmentTransaction(WebViewForMF.newInstance(err), R.id.container_body, false);
    }


    private void displayError(String err) {
        GlobalClass.showAlertDialog(err);
    }

}
