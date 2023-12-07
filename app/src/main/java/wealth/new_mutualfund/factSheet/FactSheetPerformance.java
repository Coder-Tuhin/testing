package wealth.new_mutualfund.factSheet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.MF_ASSET;
import enums.eGROWTH_DIVIDEND;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

import static wealth.new_mutualfund.factSheet.FactSheetPagerFragment.schemeGrDivValue;


public class FactSheetPerformance extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private String schemeCode;
    private static final String ARG_PARAM1 = "param1";
    private HomeActivity homeActivity;
    private NumberFormat format;
    private View view;

    @BindView(R.id.parentLayout_p2p) LinearLayout parentLayout_p2p;
    @BindView(R.id.parentLayout_sip_return) LinearLayout parentLayout_sip_return;
    @BindView(R.id.dividend_data_layout) LinearLayout dividend_data_layout;
    @BindView(R.id.parent_layout_dividend_hist) LinearLayout parent_layout_dividend_hist;
    @BindView(R.id.p2p_footer_tv) TextView p2p_footer_tv;
    @BindView(R.id.p2p_quarterly_switch) Switch p2p_quarterly_switch;
    @BindView(R.id.sip_lumpsum_switch) Switch sip_lumpsum_switch;
    @BindView(R.id.sip_lumpsum_title) TextView sip_lumpsum_title;
    @BindView(R.id.p2p_quarterly_title) TextView p2p_quarterly_title;
    @BindView(R.id.p2p_footer_note_tv) TextView p2p_footer_note_tv;
    @BindView(R.id.sip_lumpsum_amount_tv) TextView sip_lumpsum_amount_tv;

    public FactSheetPerformance() {
        // Required empty public constructor
    }

    public static FactSheetPerformance newInstance(String param1) {
        FactSheetPerformance fragment = new FactSheetPerformance();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity)context;
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
        view = inflater.inflate(R.layout.fragment_fact_sheet_performance, container, false);
        ButterKnife.bind(this, view);
        format = new DecimalFormat("#,##,##0.##");

        p2p_quarterly_switch.setOnCheckedChangeListener(this);
        sip_lumpsum_switch.setOnCheckedChangeListener(this);
        new RequestAsync(eMessageCodeWealth.FACT_SHEET_PERFORMANCE.value).execute();
        new RequestAsync(eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value).execute();
        new RequestAsync(eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value).execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (p2p_quarterly_switch.isChecked()){
            p2p_quarterly_switch.setChecked(false);
        }
        if (sip_lumpsum_switch.isChecked()){
            sip_lumpsum_switch.setChecked(false);
        }
    }

    private String p2p_str = "Point To Point Returns(%)";
    private String quart_str = "Quarterly Returns(%)";
    private String sip_str = "SIP Returns(as on "+FactSheetPagerFragment.navDate+")";
    private String lumpsum_str = "Lumpsum Returns(as on "+FactSheetPagerFragment.navDate+")";
    private String selectedSipOrLumpsum = "SIP";
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        try{
            switch (buttonView.getId()){
                case R.id.p2p_quarterly_switch:
                    if (isChecked){
                        p2p_quarterly_switch.setText(""); //p2p_str
                        p2p_quarterly_title.setText(quart_str);
                        ((TextView)view.findViewById(R.id.p2p_q_col1_title)).setText("Financial Year");
                        ((TextView)view.findViewById(R.id.p2p_q_col2_title)).setText("Q1");
                        ((TextView)view.findViewById(R.id.p2p_q_col3_title)).setText("Q2");
                        ((TextView)view.findViewById(R.id.p2p_q_col4_title)).setText("Q3");
                        ((TextView)view.findViewById(R.id.p2p_q_col5_title)).setText("Q4");
                        if (quarterlyDataJar.length()>0){
                            setP2PCustomListView(quarterlyDataJar, false);
                        }
                    }else {
                        p2p_quarterly_switch.setText(""); //quart_str
                        p2p_quarterly_title.setText(p2p_str);
                        ((TextView)view.findViewById(R.id.p2p_q_col1_title)).setText("");
                        ((TextView)view.findViewById(R.id.p2p_q_col2_title)).setText("YTD");
                        ((TextView)view.findViewById(R.id.p2p_q_col3_title)).setText("Yr. 1");
                        ((TextView)view.findViewById(R.id.p2p_q_col4_title)).setText("Yr. 3");
                        ((TextView)view.findViewById(R.id.p2p_q_col5_title)).setText("Yr. 5");
                        if (pointToPointJar.length()>0){
                            setP2PCustomListView(pointToPointJar, true);
                        }
                    }

                    break;

                case R.id.sip_lumpsum_switch:
                    if (isChecked){
                        sip_lumpsum_switch.setText(""); //sip_str
                        sip_lumpsum_title.setText(getSpannable(false));//lumpsum_str
                        sip_lumpsum_amount_tv.setText("If invested Rs. 1,00,000");
                        selectedSipOrLumpsum = "Lumpsum";
                        sipLumpsumReqMechanism();
                    }else {
                        sip_lumpsum_switch.setText(""); //lumpsum_str
                        sip_lumpsum_title.setText(getSpannable(true)); //sip_str
                        sip_lumpsum_amount_tv.setText("SIP for Rs. 10,000/- per month");
                        selectedSipOrLumpsum = "SIP";
                        sipLumpsumReqMechanism();
                    }
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private String selectedYearsBack;
    private List<String> yearsArr = new ArrayList<String>();
    private void sipLumpsumReqMechanism(){
        ((TextView)view.findViewById(R.id.scheme_name)).setText(FactSheetPagerFragment.schemeName);
        yearsArr.clear();
        yearsArr.add("INC");
        yearsArr.add("5");
        yearsArr.add("10");
        yearsArr.add("15");
        yearsArr.add("20");

        if (selectedSipOrLumpsum.equalsIgnoreCase("SIP")){
            if (FactSheetPagerFragment.sipReturnJar.length()==0){
                new SipLumpsumReturnAsync(yearsArr.get(0)).execute();
            }else {
                setSIPLumpsumCustomListView(FactSheetPagerFragment.sipReturnJar);
            }
        }else {
            if (FactSheetPagerFragment.lumpsumReturnJar.length()==0){
                new SipLumpsumReturnAsync(yearsArr.get(0)).execute();
            }else {
                setSIPLumpsumCustomListView(FactSheetPagerFragment.lumpsumReturnJar);
            }
        }
    }

    class SipLumpsumReturnAsync extends AsyncTask<String, Void, String>{
        String yearsBack;
        public SipLumpsumReturnAsync(String yearsBack) {
            this.yearsBack = yearsBack;
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                selectedYearsBack = yearsBack;

                JSONObject jdata = new JSONObject();
                jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                jdata.put(eMFJsonTag.SCHEMECODE.name, schemeCode);
                jdata.put(eMFJsonTag.PERIOD.name, yearsBack);
                jdata.put("FromDate", yearsBack.equalsIgnoreCase("INC")? "01-JAN-1900":DateUtil.getYearsBackData(Integer.parseInt(yearsBack))); //For inception: 01-01-1900
                jdata.put("ToDate", DateUtil.getTodaysData());
                jdata.put("LumpsumorSIP", selectedSipOrLumpsum);
                jdata.put("Benchmark", "");
                jdata.put("Ismonthoryear",  selectedSipOrLumpsum.equalsIgnoreCase("SIP")?"Y":"Y");
                jdata.put("Amount", selectedSipOrLumpsum.equalsIgnoreCase("SIP")?"10000":"100000");

                JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.FACT_SHEET_LUMPSUM_SIP_RETURNS.value, jdata);
                if (jsonData != null) {
                    return jsonData.toString();
                }
            }catch (Exception e){
                e.printStackTrace();
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
                        if (selectedSipOrLumpsum.equalsIgnoreCase("SIP")){
                            processSipReturnData(jsonData);
                        }else {
                            processLumpsumReturnData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void processLumpsumReturnData(JSONObject jsonData) {
        try {
            jsonData.put("Years", selectedYearsBack);
            FactSheetPagerFragment.lumpsumReturnJar.put(jsonData);

            if (FactSheetPagerFragment.lumpsumReturnJar.length()<yearsArr.size()){
                new SipLumpsumReturnAsync(yearsArr.get(FactSheetPagerFragment.lumpsumReturnJar.length())).execute();
            }
            if (FactSheetPagerFragment.lumpsumReturnJar.length()==yearsArr.size()){
                GlobalClass.log("MrGoutamD (processLumpsumReturnData): "+ FactSheetPagerFragment.lumpsumReturnJar);
                setSIPLumpsumCustomListView(FactSheetPagerFragment.lumpsumReturnJar);
            }
        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private void processSipReturnData(JSONObject jsonData) {
        try {
            jsonData.put("Years", selectedYearsBack);
            FactSheetPagerFragment.sipReturnJar.put(jsonData);

            if (FactSheetPagerFragment.sipReturnJar.length()<yearsArr.size()){
                new SipLumpsumReturnAsync(yearsArr.get(FactSheetPagerFragment.sipReturnJar.length())).execute();
            }
            if (FactSheetPagerFragment.sipReturnJar.length()==yearsArr.size()){
                GlobalClass.log("MrGoutamD (processSipReturnData): "+ FactSheetPagerFragment.sipReturnJar);
                setSIPLumpsumCustomListView(FactSheetPagerFragment.sipReturnJar);
            }
        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
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
                if(msgCode == eMessageCodeWealth.FACT_SHEET_PERFORMANCE.value) {
                    JSONObject jdata = new JSONObject();

                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemeCode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.FACT_SHEET_PERFORMANCE.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value) {
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
                        if (msgCode == eMessageCodeWealth.FACT_SHEET_PERFORMANCE.value) {
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_PERFORMANCE: "+jsonData.toString());

                            //sip_str = "SIP Returns(as on "+FactSheetPagerFragment.navDate+")";
                            //lumpsum_str = "Lumpsum Returns(as on "+FactSheetPagerFragment.navDate+")";
                            sip_lumpsum_title.setText(getSpannable(true));

                            processPointToPointData(jsonData);
                        }else if (msgCode == eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value) {
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_SNAPSHOT: " + jsonData.toString());
                            processSnapshotData(jsonData);
                            if (FactSheetPagerFragment.selectedAsset == MF_ASSET.EQUITY || FactSheetPagerFragment.selectedAsset == MF_ASSET.HYBRID){
                                sipLumpsumReqMechanism();
                            }else {
                                sip_lumpsum_switch.setChecked(true);
                            }
                        }else if (msgCode == eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value) {
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_PORTFOLIO: " + jsonData.toString());
                            processPortfolioData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private Spannable getSpannable(boolean isSip){
        if (isSip){
            String value = "SIP Returns (as on "+FactSheetPagerFragment.navDate+")";
            Spannable spannable = new SpannableString(value);
            spannable.setSpan(new ForegroundColorSpan(homeActivity.getResources().getColor(R.color.white)), 12, value.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(0.6f), 12, value.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 12, value.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }else {
            String value = "Lumpsum Returns (as on "+FactSheetPagerFragment.navDate+")";
            Spannable spannable = new SpannableString(value);
            spannable.setSpan(new ForegroundColorSpan(homeActivity.getResources().getColor(R.color.white)), 16, value.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(0.6f), 16, value.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 16, value.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
    }

    private void processSnapshotData(JSONObject jsonData) {
        try {
            JSONArray divHistoryJar = jsonData.getJSONArray("Dividendhistory");
            if (divHistoryJar.length()>0 && schemeGrDivValue == eGROWTH_DIVIDEND.DIVIDEND.getValue()){
                dividend_data_layout.setVisibility(View.VISIBLE);
                setDividendHistoryCustomListView(divHistoryJar);
            }else {
                dividend_data_layout.setVisibility(View.GONE);
            }
            setAsset(jsonData);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setAsset(JSONObject jsonData){
        try {
            JSONArray jar = jsonData.getJSONArray("data");
            JSONObject job = jar.getJSONObject(0);
            String category = job.getString("Category");
            FactSheetPagerFragment.selectedAsset = getAsset(category.contains("-")?category.split("-")[0]:category);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MF_ASSET getAsset(String asset){
        asset = asset.trim();
        if (asset.equalsIgnoreCase(MF_ASSET.DEBT.getName())){
            return MF_ASSET.DEBT;
        }else if (asset.equalsIgnoreCase(MF_ASSET.HYBRID.getName())){
            return MF_ASSET.HYBRID;
        }else if (asset.equalsIgnoreCase(MF_ASSET.LIQUID.getName())){
            return MF_ASSET.LIQUID;
        }else if (asset.equalsIgnoreCase(MF_ASSET.OTHERS.getName())){
            return MF_ASSET.OTHERS;
        }
        return MF_ASSET.EQUITY;
    }

    private JSONArray pointToPointJar;
    private void processPointToPointData(JSONObject jsonData) {
        try {
            pointToPointJar = jsonData.getJSONArray("performancedetail");
            if (pointToPointJar.length()>0){
                setP2PCustomListView(pointToPointJar, true);
            }
            quarterlyDataJar = jsonData.getJSONArray("SchemeDetailsQuarter");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setDividendHistoryCustomListView(JSONArray jar){
        try {
            for (int i = 0; i <jar.length() ; i++) {
                LayoutInflater inflater = LayoutInflater.from(homeActivity);
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.fact_sheet_performace_dividend_history, null, false);
                JSONObject job = jar.getJSONObject(i);
                String recordDate = job.optString("RecordDate");
                String dividendRupee = Formatter.stringTocommaPer(job.optString("Dividendrupee"));

                ((TextView)convertView.findViewById(R.id.date)).setText(recordDate);
                ((TextView)convertView.findViewById(R.id.per_unit_tv)).setText(dividendRupee);
                parent_layout_dividend_hist.addView(convertView);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSIPLumpsumCustomListView(JSONArray jar){
        try {
            if((parentLayout_sip_return).getChildCount() > 0){
                (parentLayout_sip_return).removeAllViews();
            }

            for (int i = 0; i <jar.length(); i++) {
                LayoutInflater inflater = LayoutInflater.from(homeActivity);
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.fact_sheet_sip_return_list_item, null, false);
                JSONObject job = jar.getJSONObject(i);

                String periodStr;
                if(job.getString("Years").equalsIgnoreCase("INC")){
                    periodStr = "Since inception";
                }else if (job.getString("Years").equalsIgnoreCase("5")){
                    periodStr = "Last 5 Years";
                }else {
                    periodStr = job.getString("Years")+" Years";
                }
                String amountStr = job.getString("MarketValue");

                String xirrStr = sip_lumpsum_switch.isChecked()?job.getString("CAGR"):job.getString("XIRR");

                ((TextView)convertView.findViewById(R.id.period_tv)).setText(periodStr);
                ((TextView)convertView.findViewById(R.id.amount_tv)).setText(
                                amountStr.equalsIgnoreCase("null")
                                        ||amountStr.equalsIgnoreCase("")
                                        ||amountStr.equalsIgnoreCase("0")
                                        ||amountStr.equalsIgnoreCase("0.0")
                                        ?"-":
                        Formatter.DecimalLessIncludingComma(amountStr));
                ((TextView)convertView.findViewById(R.id.xirr_tv)).setText(
                                xirrStr.equalsIgnoreCase("null")
                                || xirrStr.equalsIgnoreCase("")
                                || xirrStr.equalsIgnoreCase("0")
                                || xirrStr.equalsIgnoreCase("0.0")
                                ?"-":
                        Formatter.stringTocommaPer(xirrStr));
                parentLayout_sip_return.addView(convertView);
            }
        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private void setP2PCustomListView(JSONArray jar, boolean isP2P){
        try {
            if((parentLayout_p2p).getChildCount() > 0){
                (parentLayout_p2p).removeAllViews();
            }

            String benchMark = "";
            for (int i = 0; i <jar.length() ; i++) {
                LayoutInflater inflater = LayoutInflater.from(homeActivity);
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.fact_sheet_p2p_performance_list_item, null, false);
                JSONObject job = jar.getJSONObject(i);
                String returnOf, yTDRet, oneYrRet, threeYrRet, fiveYrRet;

                if (isP2P){
                    returnOf = job.optString("ReturnOf");
                    if (!job.optString("YTDRet").contains("/")){
                        yTDRet = job.optString("YTDRet").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("YTDRet"));
                    }else {
                        yTDRet = job.optString("YTDRet");
                    }
                    if (!job.optString("oneYrRet").contains("/")){
                        oneYrRet = job.optString("oneYrRet").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("oneYrRet"));
                    }else {
                        oneYrRet = job.optString("oneYrRet");
                    }
                    if (!job.optString("threeYrRet").contains("/")){
                        threeYrRet = job.optString("threeYrRet").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("threeYrRet"));
                    }else {
                        threeYrRet = job.optString("threeYrRet");
                    }
                    if (!job.optString("fiveYrRet").contains("/")){
                        fiveYrRet = job.optString("fiveYrRet").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("fiveYrRet"));
                    }else {
                        fiveYrRet = job.optString("fiveYrRet");
                    }
                }else {
                    returnOf = job.optString("Date");
                    yTDRet = job.optString("Q1").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("Q1"));
                    oneYrRet = job.optString("Q2").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("Q2"));
                    threeYrRet = job.optString("Q3").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("Q3"));
                    fiveYrRet = job.optString("Q4").equalsIgnoreCase("")?"-":Formatter.stringTocommaPer(job.optString("Q4"));
                }

                if (returnOf.equalsIgnoreCase("Benchmark")){
                    benchMark = job.getString("Shemename");
                }

                ((TextView)convertView.findViewById(R.id.returnOf)).setText(returnOf);
                ((TextView)convertView.findViewById(R.id.ytd)).setText(yTDRet.equalsIgnoreCase("")?"-":yTDRet);
                ((TextView)convertView.findViewById(R.id.oneYear)).setText(oneYrRet);
                ((TextView)convertView.findViewById(R.id.threeYears)).setText(threeYrRet);
                ((TextView)convertView.findViewById(R.id.fiveYears)).setText(fiveYrRet);
                parentLayout_p2p.addView(convertView);
            }

            if(isP2P){
                p2p_footer_tv.setText("Benchmark: "+benchMark);
                if (benchMark.equalsIgnoreCase("")){
                    p2p_footer_tv.setVisibility(View.GONE);
                }
            }else{
                p2p_footer_tv.setText("Absolute Returns");
                p2p_footer_tv.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    private JSONArray quarterlyDataJar;
    private void processPortfolioData(JSONObject jsonData) {
        try {
            JSONObject tabJob = jsonData.getJSONObject("SchemeDetailsQuarter");
            quarterlyDataJar = tabJob.getJSONArray("Table");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}