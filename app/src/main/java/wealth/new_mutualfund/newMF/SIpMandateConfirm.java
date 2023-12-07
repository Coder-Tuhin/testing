package wealth.new_mutualfund.newMF;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.QuickTransSipFragment;

public class SIpMandateConfirm extends Fragment {
    private HomeActivity homeActivity;
    private View mView;
    @BindView(R.id.mandatenonotes)
    TextView mandatenonotes;
    @BindView(R.id.enache_notes1)
    TextView enache_notes1;
    @BindView(R.id.enache_notes3)
    TextView enache_notes3;
    @BindView(R.id.ventura_site)
    TextView ventura_site;
    @BindView(R.id.ventura_call)
    TextView ventura_call;


    @BindView(R.id.submitbtn)
    TextView submitbtn;
    private static String COnfNo = "";
    private static int fromScreenTag = 0;
    private static JSONObject sipResp = new JSONObject();

    public static SIpMandateConfirm newInstance(String ConfNo,int tag,JSONObject JSONObject){
        SIpMandateConfirm fragment = new SIpMandateConfirm();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        COnfNo = ConfNo;
        fromScreenTag  = tag;
        sipResp = JSONObject;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.sipmandateconfirm,container,false); //sip_mandate_screen, sip_mandate_screen_new
            ButterKnife.bind(this,mView);
        }

        mandatenonotes.setText("Your Mandate no. is "+ COnfNo+".");
        final SpannableStringBuilder sb = new SpannableStringBuilder(mandatenonotes.getText().toString());

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, 20, mandatenonotes.getText().toString().length()-1, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold
        mandatenonotes.setText(sb);
/*
        enache_notes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://bsestarmf@bseindia.in";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
*/
/*
        enache_notes3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://noreply@digio.in";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
*/
        ventura_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://mfcustomercare@ventura1.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        ventura_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String posted_by = "+912267547042";
                String uri = "tel:" + posted_by.trim() ;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
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
                 if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){

                    JSONArray jarr = sipResp.getJSONArray("sipdata");
                    JSONObject sipData = jarr.getJSONObject(0);

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    jdata.put(eMFJsonTag.TSRNO.name, sipData.getString("TSrNo"));
                    jdata.put(eMFJsonTag.SIPSRNO.name, sipData.getString("SIPSrNo"));
                    jdata.put(eMFJsonTag.SIPTYPE.name, "E");
                    jdata.put(eMFJsonTag.SIPMANDATE.name, COnfNo);

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
                        if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
                            if(err.contains("FAILED: X-SIP REGISTRATION FAILED, MANDATE NOT APPROVED")){
                                GlobalClass.showNotEnoughBalanceMsg(homeActivity);
                            }else {
                                displayError(err.replace("100|",""));
                                homeActivity.onFragmentBack();
                                homeActivity.onFragmentBack();
                                homeActivity.onFragmentBack();
                                homeActivity.onFragmentBack();
                                homeActivity.onFragmentBack();
                                homeActivity.onFragmentBack();



                                if(msgCode == eMessageCodeWealth.REGISTER_SIP.value){
//

//                                getActivity().onBackPressed();
                                }
                            }

                        }else {
                            displayError(err);
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
    private void displayError(String err) {
        GlobalClass.showAlertDialog(err);
    }
}
