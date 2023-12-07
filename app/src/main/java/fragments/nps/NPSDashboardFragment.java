package fragments.nps;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eLogType;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;

public class NPSDashboardFragment extends Fragment {
    private HomeActivity homeActivity;
    private View mView;

    @BindView(R.id.npsname)
    TextView Npsname;
    @BindView(R.id.npspran)
    TextView NpsPran;
    @BindView(R.id.npsmobileno)
    TextView NpsMobileno;
    @BindView(R.id.npsemail)
    TextView NpsEmail;
    @BindView(R.id.npsInvtval)
    TextView NpsInvstVal;
    @BindView(R.id.npsCurrVal)
    TextView NpsCurrval;
    @BindView(R.id.npsNational)
    TextView NpsNational;
    @BindView(R.id.i_icon)
    ImageView i_icon;
    @BindView(R.id.submitbtn)
    LinearLayout submitbtn;
    @BindView(R.id.CopyImage)
    ImageView CopyImage;
    @BindView(R.id.pranNote)
    TextView pranNote;


    public static NPSDashboardFragment newInstance() {
        NPSDashboardFragment f = new NPSDashboardFragment();
        Bundle args = new Bundle();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

         if (mView == null) {
            mView = inflater.inflate(R.layout.npsscreen, container, false);
            ButterKnife.bind(this, mView);
            initProperty();
            i_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExitLoadWindow();

                }
            });
            submitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Url = ObjectHolder.PranLink +NpsPran.getText().toString();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                    homeActivity.startActivity(browserIntent);
//                    GlobalClass.fragmentTransaction(NPSContributionFragment.newInstance(NpsPran.getText().toString()),R.id.container_body,true,"");
                }
            });
            CopyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("PranNo", NpsPran.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(homeActivity, "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            });


        }
        GlobalClass.addAndroidLog(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return mView;
    }

    private void initProperty()
    {
        new NPSReq(eMessageCodeWealth.CLIENT_SESSION.value).execute();
    }

    class NPSReq extends AsyncTask<String, Void, String> {
        private int msgCode = -1;

        NPSReq(int mCode) {
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
                if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                    if(UserSession.getClientResponse().isNeedAccordLogin()) {
                        ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                        if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                            VenturaServerConnect.closeSocket();
                            if(VenturaServerConnect.connectToWealthServer(true)) {
                                JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                                if (jsonData != null) {
                                    return jsonData.toString();
                                }
                            }
                        } else {
                            return clientLoginResponse.charResMsg.getValue();
                        }
                    }else{
                        if(VenturaServerConnect.connectToWealthServer(true)) {
                            JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                            if (jsonData != null) {
                                return jsonData.toString();
                            }
                        }
                    }
                } else if (msgCode == eMessageCodeWealth.NPS_REPORT.value) {
                    //VenturaServerConnect.connectToWealthServer();
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.NPS_REPORT.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }


            } catch (Exception ex) {
                VenturaException.Print(ex);
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
                    if(!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        //copy paste tghis method
                        displayError(err);
                    }else {
                        if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                            String mfC = jsonData.getString("MFClientType");
                            VenturaServerConnect.mfClientID = jsonData.getString("MFBClientId");
                            if (mfC.equalsIgnoreCase(eMFClientType.MFI.name)) {
                                VenturaServerConnect.mfClientType = eMFClientType.MFI;
                            } else {
                                VenturaServerConnect.mfClientType = eMFClientType.MFD;
                            }

                            new NPSReq(eMessageCodeWealth.NPS_REPORT.value).execute();
                        }else if (msgCode == eMessageCodeWealth.NPS_REPORT.value) {
                            GlobalClass.log("MrGoutamD", "NPS_REPORT: "+jsonData.toString());
                            processNPSReport(jsonData);
                            //this is your data
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    if(s.toLowerCase().contains(Constants.WEALTH_ERR)){
                        GlobalClass.homeActivity.logoutAlert("Logout",Constants.LOGOUT_FOR_WEALTH,false);
                    }else {
                        GlobalClass.showAlertDialog(s);
                    }
                }
            }
        }

        private void processNPSReport(JSONObject jsonData){
            try{
                JSONArray clientdata = StaticMethods.getJSONarray("clientdata",jsonData);
                JSONArray contributiondetailsdata = StaticMethods.getJSONarray("contributiondetailsdata",jsonData);

                if (clientdata!=null && clientdata.length()>0){
                    JSONObject clientdataObj = clientdata.optJSONObject(0);
                    Npsname.setText(StaticMethods.getString("SubscriberName",clientdataObj));
                    NpsPran.setText(StaticMethods.getString("PRAN",clientdataObj));
                    NpsMobileno.setText(StaticMethods.getString("Mobileno",clientdataObj));
                    NpsEmail.setText(StaticMethods.getString("Email",clientdataObj));
                }
                if (contributiondetailsdata!=null && contributiondetailsdata.length()>0){
                    JSONObject contributiondetailsdataObj = contributiondetailsdata.optJSONObject(0);
                    String InvestmentValue = StaticMethods.getString("InvestmentValue",contributiondetailsdataObj);
                    String CurrentValue = StaticMethods.getString("CurrentValue",contributiondetailsdataObj);
                    String NationalGainLoss = StaticMethods.getString("NationalGainLoss",contributiondetailsdataObj);

                    NpsInvstVal.setText(TextUtils.isEmpty(InvestmentValue)?"0": Formatter.DecimalLessIncludingComma(InvestmentValue));
                    NpsCurrval.setText(TextUtils.isEmpty(CurrentValue)?"0":Formatter.DecimalLessIncludingComma(CurrentValue));
                    NpsNational.setText(TextUtils.isEmpty(NationalGainLoss)?"0":Formatter.DecimalLessIncludingComma(NationalGainLoss));// if NationalGainLoss.startWith("-")>Red Color else Green
                    if(StaticMethods.getStringToDouble(NationalGainLoss)>0){
                        NpsNational.setTextColor(Color.parseColor("#17ae1c"));
                    }else if(StaticMethods.getStringToDouble(NationalGainLoss)== 0){
                        NpsNational.setTextColor(Color.parseColor("#17ae1c"));
                    } else{
                        NpsNational.setTextColor(Color.parseColor("#f9fc0303"));
                    }
                }

                if(NpsPran.getText().toString().length() <= 0){
                    submitbtn.setVisibility(View.GONE);
                    CopyImage.setVisibility(View.GONE);
                    pranNote.setVisibility(View.GONE);
                }else {
                    submitbtn.setVisibility(View.VISIBLE);
                    CopyImage.setVisibility(View.VISIBLE);
                    pranNote.setVisibility(View.VISIBLE);
                }
            }
            catch(Exception e){
                VenturaException.Print(e);
            }
        }

        private void displayError(String err){
            GlobalClass.showAlertDialog(err);
        }
    }

    private void showExitLoadWindow(){
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.prannotelayout);

            ImageView closeAlert = dialog.findViewById(R.id.closeTv);
            closeAlert.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void req(){
        new NPSReq(eMessageCodeWealth.NPS_REPORT.value).execute();
    }


}

