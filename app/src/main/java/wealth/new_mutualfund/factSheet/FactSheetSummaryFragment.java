package wealth.new_mutualfund.factSheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eGROWTH_DIVIDEND;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

import static wealth.new_mutualfund.factSheet.FactSheetPagerFragment.schemeGrDivValue;

public class FactSheetSummaryFragment extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;
    private String schemeCode, fundManagerCode;
    private GauseViewGdev gauseViewGdev;
    private static final String ARG_PARAM1 = "param1";

    @BindView(R.id.fund_manager_tv) TextView fund_manager_tv;
    @BindView(R.id.exit_load_tv) TextView exit_load_tv;
    @BindView(R.id.beta_iv) ImageView beta_iv;
    @BindView(R.id.turnover_iv) ImageView turnover_iv;
    @BindView(R.id.sd_iv) ImageView sd_iv;
    @BindView(R.id.alpah_iv) ImageView alpah_iv;
    @BindView(R.id.scheme_name) TextView scheme_name;
    @BindView(R.id.nav_price_tv) TextView nav_price_tv;
    @BindView(R.id.nav_date) TextView nav_date;
    @BindView(R.id.category_tv) TextView category_tv;
    @BindView(R.id.inception_date_tv) TextView inception_date_tv;
    @BindView(R.id.week52_high_tv) TextView week52_high_tv;
    @BindView(R.id.week52_low_tv) TextView week52_low_tv;
    @BindView(R.id.min_inv_price_tv) TextView min_inv_price_tv;
    @BindView(R.id.min_add_inv_price_tv) TextView min_add_inv_price_tv;
    @BindView(R.id.min_sip_inv_pric_tv) TextView min_sip_inv_pric_tv;
    @BindView(R.id.beta_tv) TextView beta_tv;
    @BindView(R.id.turnover_tv) TextView turnover_tv;
    @BindView(R.id.sd_tv) TextView sd_tv;
    @BindView(R.id.alpha_tv) TextView alpha_tv;
    @BindView(R.id.speedView) ImageView speedView;

    public FactSheetSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity)context;
    }

    public static FactSheetSummaryFragment newInstance(String param1) {
        FactSheetSummaryFragment fragment = new FactSheetSummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            schemeCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fact_sheet_summary, container, false);
        ButterKnife.bind(this, view);

        //gauseViewGdev = new GauseViewGdev(speedView, homeActivity);
        //gauseViewGdev.initiateDrawing();

        fund_manager_tv.setPaintFlags(fund_manager_tv.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        fund_manager_tv.setOnClickListener(this);

        exit_load_tv.setPaintFlags(exit_load_tv.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        exit_load_tv.setOnClickListener(this);

        beta_iv.setOnClickListener(this);
        turnover_iv.setOnClickListener(this);
        sd_iv.setOnClickListener(this);
        alpah_iv.setOnClickListener(this);
        (view.findViewById(R.id.beta_title_tv)).setOnClickListener(this);
        (view.findViewById(R.id.turnover_title_tv)).setOnClickListener(this);
        (view.findViewById(R.id.sd_title_tv)).setOnClickListener(this);
        (view.findViewById(R.id.alpha_title_tv)).setOnClickListener(this);

        new RequestAsync(eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value).execute();
        new RequestAsync(eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value).execute();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fund_manager_tv:
                new RequestAsync(eMessageCodeWealth.FACT_SHEET_FUND_MANAGER_DETAILS.value).execute();
                break;
            case R.id.exit_load_tv:
                showExitLoadWindow("Exit Load (%)", exitLoadValue, homeActivity.getResources().getDimension(R.dimen.text_18));
                break;
            case R.id.beta_iv:
            case R.id.beta_title_tv:
                showExitLoadWindow("What is Beta?", homeActivity.getResources().getString(R.string.fact_beta),
                        homeActivity.getResources().getDimension(R.dimen.text_16));
                break;
            case R.id.turnover_iv:
            case R.id.turnover_title_tv:
                showExitLoadWindow("What is Turnover Ratio?", homeActivity.getResources().getString(R.string.fact_turn_over),
                        homeActivity.getResources().getDimension(R.dimen.text_16));
                break;
            case R.id.sd_iv:
            case R.id.sd_title_tv:
                showExitLoadWindow("What is Standard Deviation?", homeActivity.getResources().getString(R.string.fact_sd),
                        homeActivity.getResources().getDimension(R.dimen.text_16));
                break;
            case R.id.alpah_iv:
            case R.id.alpha_title_tv:
                showExitLoadWindow("What is Jenson's Alpha?", homeActivity.getResources().getString(R.string.fact_jensen_alpha),
                        homeActivity.getResources().getDimension(R.dimen.text_16));
                break;
            default:break;
        }
    }

    class RequestAsync extends AsyncTask<String, Void, String> {
        int msgCode;

        RequestAsync(int mCode){
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
                if(msgCode == eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value) {
                    JSONObject jdata = new JSONObject();

                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemeCode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value) {
                    JSONObject jdata = new JSONObject();

                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemeCode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.FACT_SHEET_FUND_MANAGER_DETAILS.value) {
                    JSONObject jdata = new JSONObject();

                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemeCode);
                    jdata.put("Fundmanagercode", fundManagerCode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.FACT_SHEET_FUND_MANAGER_DETAILS.value, jdata);
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
                        displayError(err);
                    }else {
                        if (msgCode == eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value) {
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_SNAPSHOT: "+jsonData.toString());
                            processSnapshotData(jsonData);
                        }else if (msgCode == eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value){
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_PORTFOLIO: "+jsonData.toString());
                            processPortfolioData(jsonData);
                        }else if (msgCode == eMessageCodeWealth.FACT_SHEET_FUND_MANAGER_DETAILS.value){
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_FUND_MANAGER_DETAILS: "+jsonData.toString());
                            processFundManagerData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void processFundManagerData(JSONObject jsonData) {
        try {
            JSONArray jar = jsonData.getJSONArray("basicdetails");
            if (jar.length()>0){
                JSONObject job = jar.getJSONObject(0);
                String details = job.getString("Qualification")+"\n\n"+job.getString("BasicDetails");
                showExitLoadWindow(job.getString("Initial").trim() +" "+job.getString("FundManager"), details, homeActivity.getResources().getDimension(R.dimen.text_16));
            }else {
                displayError("No data found");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void processPortfolioData(JSONObject jsonData) {
        try {
            JSONArray jar = jsonData.getJSONArray("RatiodetailsData");
            if (jar.length()>0){
                JSONObject job = jar.getJSONObject(0);

                beta_tv.setText(getFormattedRatio(job.getString("BetaRatio")));
                turnover_tv.setText(getFormattedRatio(job.getString("TurnoverRatio")));
                sd_tv.setText(getFormattedRatio(job.getString("StandardRatio")));
                alpha_tv.setText(getFormattedRatio(job.getString("JensenAlpha")));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String getFormattedRatio(String value){
        try {
            String valueFormatted = "0";
            if (value != null && !value.equalsIgnoreCase("")){
                valueFormatted = Formatter.toThreeDecimal(Double.parseDouble(value));
                if (Double.parseDouble(valueFormatted.replace(",",""))==0){
                    valueFormatted = "0";
                }
            }
            return valueFormatted;
        }catch (Exception e){
            e.printStackTrace();
            return "0";
        }
    }

    private String exitLoadValue;
    private void processSnapshotData(JSONObject jsonObject){
        try {
            JSONArray jar = jsonObject.getJSONArray("data");
            if (jar.length()>0){
                JSONObject job = jar.getJSONObject(0);
                FactSheetPagerFragment.schemeName = job.getString("SchemeName");
                FactSheetPagerFragment.navDate = job.getString("NAVDate");
                FactSheetPagerFragment.aumDate = job.getString("AUMDate");

                scheme_name.setText(FactSheetPagerFragment.schemeName);
                nav_price_tv.setText(Formatter.getFourDigitFormatter(Double.parseDouble(job.getString("NAVRs"))));
                nav_date.setText(" as on "+FactSheetPagerFragment.navDate);
                category_tv.setText("Category: "+job.getString("SubCategory"));
                inception_date_tv.setText("Inception date: "+job.getString("InceptDate"));
                week52_high_tv.setText("52 wk. H (Rs.): "+jsonObject.getString("fiftytwoweekhigh"));
                week52_low_tv.setText("52 wk. L (Rs.): "+jsonObject.getString("fiftytwoweeklow"));
                fund_manager_tv.setText(job.getString("FundManagerName1"));
                fundManagerCode = job.getString("FundManagerCode1");
                exitLoadValue = job.getString("ExitLoad");

                try {
                    schemeGrDivValue = Integer.parseInt(job.optString("Optcode"));
                }catch (Exception e){
                    schemeGrDivValue = eGROWTH_DIVIDEND.GROWTH.getValue();
                    e.printStackTrace();
                }

                /*
                min_inv_price_tv.setText(Formatter.DecimalLessIncludingComma(job.getString("MINInvt")));
                min_add_inv_price_tv.setText(Formatter.DecimalLessIncludingComma(job.getString("AdditionInvt")));
                min_sip_inv_pric_tv.setText(job.getString("SIPMininvest").equalsIgnoreCase("")?"-":Formatter.DecimalLessIncludingComma(job.getString("SIPMininvest")));
*/

                min_inv_price_tv.setText(job.getString("MINInvt").equalsIgnoreCase("")?"-":Formatter.DecimalLessIncludingComma(job.getString("MINInvt")));
                min_add_inv_price_tv.setText(job.getString("AdditionInvt").equalsIgnoreCase("")?"-":Formatter.DecimalLessIncludingComma(job.getString("AdditionInvt")));
                min_sip_inv_pric_tv.setText(job.getString("SIPMininvest").equalsIgnoreCase("")?"-":Formatter.DecimalLessIncludingComma(job.getString("SIPMininvest")));
                setRiskMeter(job.getString("Riskometer"));
            }else {
                displayError("No data found");
            }
        }catch (Exception e){
            e.printStackTrace();
            displayError("Error in data processing");
        }
    }

    private void setRiskMeter(String riskStatus){
        if (riskStatus.equalsIgnoreCase(RISK.LOW.respName)){
            //gauseViewGdev.setPointer(RISK.LOW.value);
            speedView.setBackgroundResource(R.drawable.factsheet_low);
        }else if(riskStatus.equalsIgnoreCase(RISK.MODERATE_LOW.respName) ||
                riskStatus.equalsIgnoreCase(RISK.LOW_TO_MODERATE.respName)) {
            //gauseViewGdev.setPointer(RISK.MODERATE_LOW.value);
            speedView.setBackgroundResource(R.drawable.factsheet_low_to_mod);
        }else if(riskStatus.equalsIgnoreCase(RISK.MODERATE.respName)) {
            //gauseViewGdev.setPointer(RISK.MODERATE.value);
            speedView.setBackgroundResource(R.drawable.factsheet_mod);
        }else if(riskStatus.equalsIgnoreCase(RISK.MODERATE_HIGH.respName)) {
            //gauseViewGdev.setPointer(RISK.MODERATE_HIGH.value);
            speedView.setBackgroundResource(R.drawable.factsheet_mod_high);
        }else if(riskStatus.equalsIgnoreCase(RISK.HIGH.respName)) {
            //gauseViewGdev.setPointer(RISK.HIGH.value);
            speedView.setBackgroundResource(R.drawable.factsheet_high);
        }else if(riskStatus.equalsIgnoreCase(RISK.VERY_HIGH.respName)) {
            //gauseViewGdev.setPointer(RISK.HIGH.value);
            speedView.setBackgroundResource(R.drawable.factsheet_very_high);
        }
    }

    enum RISK{

        LOW(0, "Low", "Low"),
        MODERATE_LOW(1, "Moderately Low", "Moderate Low"),
        LOW_TO_MODERATE(6, "Low to Moderate", "Low to Moderate"),
        MODERATE(2, "Moderate", "Moderate"),
        MODERATE_HIGH(3, "Moderately High", "Moderate High"),
        HIGH(4, "High", "High"),
        VERY_HIGH(5, "Very High", "Very High");

        int value;
        String respName;
        String chartName;

        RISK(int value, String respName, String chartName) {
            this.value = value;
            this.respName = respName;
            this.chartName = chartName;
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    private void showExitLoadWindow(String title, String value, float titleTextSize){
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.fact_sheet_exit_load_dialog_layout);

            TextView titleTv = dialog.findViewById(R.id.title);
            //titleTv.setTextSize(titleTextSize);
            titleTv.setText(title);

            TextView exit_value_tv = dialog.findViewById(R.id.exit_value_tv);
            exit_value_tv.setText(value); //exitLoadValue

            ImageView closeAlert = dialog.findViewById(R.id.closeTv);
            closeAlert.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}