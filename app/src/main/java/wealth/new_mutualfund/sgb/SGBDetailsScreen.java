package wealth.new_mutualfund.sgb;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.mv.MainPage;

public class SGBDetailsScreen extends Fragment {
    private final static String EG_ISSUE_DETAILS = "eg_issue_details";
    private HomeActivity homeActivity;
    private View mView;
    private JSONObject selectedEGDetails, selectedAccDetails;
    NumberFormat clk_formatter;
    double availableBal;

    @BindView(R.id.schemeName)
    TextView schemeName;
    @BindView(R.id.applyBtn)
    TextView applyBtn;
    @BindView(R.id.bit_amount_tv)
    TextView bit_amount_tv;
    @BindView(R.id.bid_price_tv)
    TextView bid_price_tv;
    @BindView(R.id.qty_tv)
    TextView qty_tv;

    @BindView(R.id.acc_holder_tv_1)
    TextView acc_holder_tv_1;
    @BindView(R.id.acc_holder_tv_2)
    TextView acc_holder_tv_2;
    @BindView(R.id.acc_holder_tv_3)
    TextView acc_holder_tv_3;

    @BindView(R.id.pan_tv_1)
    TextView pan_tv_1;
    @BindView(R.id.pan_tv_2)
    TextView pan_tv_2;
    @BindView(R.id.pan_tv_3)
    TextView pan_tv_3;

    @BindView(R.id.dp_id_tv)
    TextView dp_id_tv;
    @BindView(R.id.beneficiary_id_tv)
    TextView beneficiary_id_tv;
    @BindView(R.id.bank_name_tv)
    TextView bank_name_tv;
    @BindView(R.id.acc_no_tv)
    TextView acc_no_tv;
    @BindView(R.id.ifsc_code_tv)
    TextView ifsc_code_tv;
    @BindView(R.id.termcondclick)
    TextView termcondclick;

    @BindView(R.id.acc_layout_1)
    LinearLayout acc_layout_1;
    @BindView(R.id.acc_layout_2)
    LinearLayout acc_layout_2;
    @BindView(R.id.acc_layout_3)
    LinearLayout acc_layout_3;

    @BindView(R.id.termcondChkBox)
    CheckBox termcondChkBox;

    public static SGBDetailsScreen newInstance(JSONObject job){
        SGBDetailsScreen f = new SGBDetailsScreen();
        Bundle args = new Bundle();
        args.putString(EG_ISSUE_DETAILS, job.toString());
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
        if (mView == null){
            mView = inflater.inflate(R.layout.sgb_details_fragment,container,false);
            ButterKnife.bind(this, mView);
            initProperty();
        }
        return mView;
    }

    private void initProperty() {
        try {
            new SGBReq(eMessageCodeWealth.EG_DP_ACCOUNT_DDETAILS.value).execute();
            clk_formatter = new DecimalFormat("#,##,##0.0");
            Bundle args = getArguments();
            selectedEGDetails = new JSONObject(args.getString(EG_ISSUE_DETAILS, ""));

            schemeName.setText(selectedEGDetails.getString("CompanyName").replace("￢ﾀﾓ","-").replace("ﾖ","-"));
            qty_tv.setText(selectedEGDetails.getString("selectedBidQty"));
            bid_price_tv.setText(clk_formatter.format(Double.parseDouble(selectedEGDetails.getString("MinBidPrice"))));
            bit_amount_tv.setText(clk_formatter.format(Double.parseDouble(selectedEGDetails.getString("selectedBitAmount"))));
            applyBtn.setOnClickListener(v -> {
                if (isValidate()){
                    homeActivity.showMsgDialog("Confirmation", "Are you sure you want to place order?", (DialogInterface dialogInterface1, int j) -> {
                                //Toast.makeText(homeActivity, "Req sent demo", Toast.LENGTH_SHORT).show();
                                new SGBReq(eMessageCodeWealth.EG_PLACE_ORDER.value).execute();
                            },
                            (dialogInterface1, j) -> {
                                dialogInterface1.dismiss();
                            });

                }
            });

            termcondclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeActivity.showMsgDialog("Terms & Conditions", R.string.sgb_terms_conditions,false);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private boolean isValidate(){
        boolean isOK = true;
        double bidAmt = Double.parseDouble(bit_amount_tv.getText().toString().replace(",",""));
        if (!termcondChkBox.isChecked()){
            displayError("Please check the terms and conditions before you submit.");
            isOK = false;
        }else if (bidAmt>availableBal){
            displayError("Your available balance is less than the amount to be invested. Please transfer funds to your account.");
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
                if(msgCode == eMessageCodeWealth.EG_DP_ACCOUNT_DDETAILS.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.EG_DP_ACCOUNT_DDETAILS.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.EG_PLACE_ORDER.value) {
                    JSONObject jdata = new JSONObject();
                    double bidAmt = Double.parseDouble(bit_amount_tv.getText().toString().replace(",",""));
                    if (bidAmt==0){
                        return null;
                    }
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("CompId", selectedEGDetails.getString("CompId"));
                    jdata.put("BidQty", selectedEGDetails.getString("selectedBidQty"));
                    jdata.put("BidPrice", selectedEGDetails.getString("MinBidPrice"));
                    jdata.put("BidValue", String.valueOf(bidAmt));
                    jdata.put("NetAmount", String.valueOf(bidAmt));
                    jdata.put("DPId", selectedAccDetails.getString("DPId"));
                    jdata.put("BeneficiaryId", selectedAccDetails.getString("BeneficiaryId"));

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.EG_PLACE_ORDER.value,jdata);
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
                    if(!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        if(msgCode == eMessageCodeWealth.EG_PLACE_ORDER.value){
                            GlobalClass.log("MrGoutamD: ", "EG_PLACE_ORDER: "+jsonData.toString());
                            goToMainPage(err);
                        }else {
                            displayError(err);
                        }

                    }else {
                        if (msgCode == eMessageCodeWealth.EG_DP_ACCOUNT_DDETAILS.value) {
                            GlobalClass.log("MrGoutamD: ", "EG_DP_ACCOUNT_DDETAILS: "+jsonData.toString());
                            processAccDetailsData(jsonData);
                        }else if(msgCode == eMessageCodeWealth.EG_PLACE_ORDER.value){
                            GlobalClass.log("MrGoutamD: ", "EG_PLACE_ORDER: "+jsonData.toString());
                            goToMainPage(jsonData.toString());
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else{
                 displayError("Something went wrong. Please try again later. Error code - SGBD004");
             }
        }
    }

    private void goToMainPage(String msg){
        homeActivity.showMsgDialogOk(msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment fragment = new MainPage();
                homeActivity.FragmentTransaction(fragment, R.id.container_body, false);
            }
        });
    }

    private void processAccDetailsData(JSONObject jsonData) {
        try {
            JSONArray jar = jsonData.getJSONArray("data");
            selectedAccDetails = jar.getJSONObject(0);

            availableBal = Double.parseDouble(selectedAccDetails.getString("AvailableBalance"));
            String accHolderName1 = selectedAccDetails.getString("DPHolder1Name");
            String accHolderName2 = selectedAccDetails.getString("DPHolder2Name");
            String accHolderName3 = selectedAccDetails.getString("DPHolder3Name");
            if (!accHolderName1.equalsIgnoreCase("")){
                acc_layout_1.setVisibility(View.VISIBLE);
                acc_holder_tv_1.setText(accHolderName1);
                pan_tv_1.setText(selectedAccDetails.getString("DPHolder1PAN").trim());
            }
            if (!accHolderName2.equalsIgnoreCase("")){
                acc_layout_2.setVisibility(View.VISIBLE);
                acc_holder_tv_2.setText(accHolderName2);
                pan_tv_2.setText(selectedAccDetails.getString("DPHolder2PAN").trim());
            }
            if (!accHolderName3.equalsIgnoreCase("")){
                acc_layout_3.setVisibility(View.VISIBLE);
                acc_holder_tv_3.setText(accHolderName3);
                pan_tv_3.setText(selectedAccDetails.getString("DPHolder3PAN").trim());
            }

            dp_id_tv.setText(selectedAccDetails.getString("DPId").trim());
            beneficiary_id_tv.setText(selectedAccDetails.getString("BeneficiaryId").trim());
            bank_name_tv.setText(selectedAccDetails.getString("BankName").trim());
            acc_no_tv.setText(selectedAccDetails.getString("AccountNo").trim());
            ifsc_code_tv.setText(selectedAccDetails.getString("IFSCCode").trim());
        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
}
