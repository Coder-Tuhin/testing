package wealth.new_mutualfund.sgb;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.bondstructure.StructBondAvailableBalance;


public class SGBSummaryFragment extends Fragment {
    private HomeActivity homeActivity;
    private View mView;

    @BindView(R.id.schemeName)
    TextView schemeName;
     @BindView(R.id.avl_balance)
    TextView avl_balance;
    @BindView(R.id.avlbalancelayout)
    LinearLayout avlbalanceLayout;
    @BindView(R.id.applyBtn)
    TextView applyBtn;
    @BindView(R.id.bit_amount_tv)
    TextView bit_amount_tv;
    @BindView(R.id.close_date_tv)
    TextView close_date_tv;
    @BindView(R.id.bid_price_tv)
    TextView bid_price_tv;
    @BindView(R.id.qtyEditText)
    EditText qtyEditText;

    @BindView(R.id.no_issue_tv)
    TextView no_issue_tv;
    @BindView(R.id.data_layout)
    LinearLayout data_layout;

    private StructBondAvailableBalance avlBalance;
    private JSONObject selectedEGDetails;
    private int minBidQty, maxBidQty;
    NumberFormat clk_formatter;

    public static SGBSummaryFragment newInstance(){
        SGBSummaryFragment f = new SGBSummaryFragment();
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

        if (mView == null){
            mView = inflater.inflate(R.layout.scb_summary_fragment,container,false);
            ButterKnife.bind(this,mView);
            initProperty();
        }
        return mView;
    }

    private void initProperty() {
        clk_formatter = new DecimalFormat("#,##,##0.0");

        new SGBReq(eMessageCodeWealth.CLIENT_SESSION.value).execute();
        /*if(VenturaServerConnect.mfClientType == eMFClientType.NONE){
            new SGBReq(eMessageCodeWealth.CLIENT_SESSION.value).execute();
        }else{
            new SGBReq(eMessageCodeWealth.AVLBALANCE.value).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }*/
        applyBtn.setOnClickListener(v -> {
            if (isValidate()){
                JSONObject job = getProcessedJson();
                if (job != null){
                    homeActivity.FragmentTransaction(SGBDetailsScreen.newInstance(job), R.id.container_body, true);
                }else {
                    displayError("OOPs! Error code: G-SGB00");
                }
            }
        });
        qtyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!qtyEditText.getText().toString().equalsIgnoreCase("")){
                    bit_amount_tv.setText(getBidAmt());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equalsIgnoreCase("")){
                    bit_amount_tv.setText("0.0");
                }
            }
        });
    }


    private JSONObject getProcessedJson() {
        try {
            JSONObject job = selectedEGDetails;
            job.put("selectedBidQty", qtyEditText.getText());
            job.put("selectedBitAmount", bit_amount_tv.getText().toString().replace(",",""));
            return job;
        }catch (JSONException e){
            e.printStackTrace();
            displayError(e.getMessage());
            return null;
        }
    }

    private String getBidAmt() {
        String bidAmt;
        try {
            bidAmt = clk_formatter.format(Double.parseDouble(bid_price_tv.getText().toString().replace(",",""))*Integer.parseInt(qtyEditText.getText().toString()));
            return bidAmt;
        }catch (Exception e){
            e.printStackTrace();
            return "0.0";
        }
    }

    private boolean isValidate(){
        boolean isOK = true;
        String qtyStr = qtyEditText.getText().toString();
        if (!isSuccResp || bid_price_tv.getText().toString().equalsIgnoreCase("0")){
            GlobalClass.showAlertDialog("SGB data not received. Please try again later");
            isOK = false;
        }else if (qtyStr.equalsIgnoreCase("")){
            GlobalClass.showAlertDialog("Please enter Bid Qty");
            isOK = false;
        }else if (Integer.parseInt(qtyStr)<minBidQty){
            GlobalClass.showAlertDialog("Qty cannot be less than 1");
            isOK = false;
        }else if (Integer.parseInt(qtyStr)>maxBidQty){
            GlobalClass.showAlertDialog("Maximum bid quantity is "+maxBidQty +" gms");
            isOK = false;
        }
        return isOK;
    }

    class SGBReq extends AsyncTask<String, Void, String> {
        int msgCode;

        SGBReq(int mCode){
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
                if(msgCode == eMessageCodeWealth.CLIENT_SESSION.value){

                    if(UserSession.getClientResponse().isNeedAccordLogin()) {
                        ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                        if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                            VenturaServerConnect.closeSocket();
                        } else {
                            return clientLoginResponse.charResMsg.getValue();
                        }
                    }
                    if(VenturaServerConnect.connectToWealthServer(true)){
                        JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    }
                }else if(msgCode == eMessageCodeWealth.EG_ISSUE_DETAILS.value) {
                    JSONObject jdata = new JSONObject();

                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("CompId", "");

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.EG_ISSUE_DETAILS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.AVLBALANCE.value){
                    avlBalance = VenturaServerConnect.getAvailableBalance();
                    if (avlBalance==null){
                        avlBalance = new StructBondAvailableBalance((long) 0.0);
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

            if(msgCode == eMessageCodeWealth.AVLBALANCE.value){
                avl_balance.setText(GlobalClass.getFormattedAmountString(avlBalance.getAvailableBalance()));
                req();
            }else if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        if (err.contains("not exists")){
                            //displayError("Currently no issue in progress");
                            issueVisibility(View.GONE, View.VISIBLE);
                        }else {
                            displayError(err);
                        }
                    }else {
                        if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                            String mfC = jsonData.getString("MFClientType");
                            VenturaServerConnect.mfClientID = jsonData.getString("MFBClientId");
                            if (mfC.equalsIgnoreCase(eMFClientType.MFI.name)) {
                                VenturaServerConnect.mfClientType = eMFClientType.MFI;
                            } else {
                                VenturaServerConnect.mfClientType = eMFClientType.MFD;
                            }

                            new SGBReq(eMessageCodeWealth.AVLBALANCE.value).execute();
                        }else if (msgCode == eMessageCodeWealth.EG_ISSUE_DETAILS.value) {
                            GlobalClass.log("MrGoutamD", "EG_ISSUE_DETAILS: "+jsonData.toString());
                            issueVisibility(View.VISIBLE, View.GONE);
                            processEGDetailsData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    if(!s.equalsIgnoreCase("")) {
                        if (s.toLowerCase().contains(Constants.WEALTH_ERR)) {
                            GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                        } else {
                            GlobalClass.showAlertDialog(s);
                        }
                    }
                }
            }
        }
    }

    private void issueVisibility(int dataLayoutVisibility, int noIssueLayoutVisibility){
        data_layout.setVisibility(dataLayoutVisibility);
        no_issue_tv.setVisibility(noIssueLayoutVisibility);
    }

    private void req(){
        new SGBReq(eMessageCodeWealth.EG_ISSUE_DETAILS.value).execute();
    }

    private boolean isSuccResp = false;
    private void processEGDetailsData(JSONObject jsonData) {
        try {
            JSONArray jar = jsonData.getJSONArray("data");
            selectedEGDetails = jar.getJSONObject(0);
            if (selectedEGDetails != null){
                schemeName.setText(selectedEGDetails.getString("CompanyName").replace("￢ﾀﾓ","-").replace("ﾖ","-"));
                close_date_tv.setText(selectedEGDetails.getString("CloseDate"));
                bid_price_tv.setText(clk_formatter.format(Double.parseDouble(selectedEGDetails.getString("MinBidPrice"))));
                minBidQty = Integer.parseInt(selectedEGDetails.getString("MinBidQty"));
                maxBidQty = Integer.parseInt(selectedEGDetails.getString("MaxBidQty"));
                isSuccResp = true;
            }else{
                displayError("Something went wrong. Please try again later. Error code- EGNULL002");
            }
        }catch (JSONException e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
}
