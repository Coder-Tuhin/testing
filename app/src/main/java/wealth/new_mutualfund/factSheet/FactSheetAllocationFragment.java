package wealth.new_mutualfund.factSheet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.xtrem.chartxtrem.piechart.PieView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import chart.PieChart;
import enums.MF_ASSET;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import models.StructNameValue;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class FactSheetAllocationFragment extends Fragment {
    private HomeActivity homeActivity;
    private String schemCode;
    private View view;
    private static final String ARG_PARAM1 = "param1";
    private JSONArray assetAllocJar, creditRatingJar;

    @BindView(R.id.listView_asset) ListView listView_asset;
    @BindView(R.id.listView_credit) ListView listView_credit;
    @BindView(R.id.asset_layout) LinearLayout asset_layout;
    @BindView(R.id.credit_layout) LinearLayout credit_layout;
    @BindView(R.id.marketCapNote) TextView marketCapNote;
    @BindView(R.id.credit_title) TextView credit_title;
    @BindView(R.id.marketCap_ratingTitle) TextView marketCap_ratingTitle;
    @BindView(R.id.note_date) TextView note_date;

    public FactSheetAllocationFragment() {
        // Required empty public constructor
    }

   public static FactSheetAllocationFragment newInstance(String param1) {
        FactSheetAllocationFragment fragment = new FactSheetAllocationFragment();
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
            schemCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fact_sheet_allocation, container, false);
        ButterKnife.bind(this, view);
        asset_layout.setVisibility(View.GONE);
        credit_layout.setVisibility(View.GONE);
        credit_title.setVisibility(View.GONE);

        ((TextView)view.findViewById(R.id.scheme_name)).setText(FactSheetPagerFragment.schemeName);
        new RequestAsync(eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value).execute();
        return view;
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
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemCode);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }else if(msgCode == eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value) {
                    JSONObject jdata = new JSONObject();

                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schemCode);

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
                        if (msgCode == eMessageCodeWealth.FACT_SHEET_SNAPSHOT.value) {
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_SNAPSHOT: "+jsonData.toString());

                        }else if (msgCode == eMessageCodeWealth.FACT_SHEET_PORTFOLIO.value){
                            GlobalClass.log("MrGoutamD", "FACT_SHEET_PORTFOLIO: "+jsonData.toString());
                            processPortfolioData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void processPortfolioData(JSONObject jsonData) {
        try {
            assetAllocJar = jsonData.getJSONArray("Assetallocation");
            creditRatingJar = jsonData.getJSONArray("Ratingallocationchart"); //Ratingallocation
            /*
            if (assetAllocJar.length()>0){
                asset_layout.setVisibility(View.VISIBLE);
                listView_asset.setAdapter(new MyAdapter(homeActivity, assetAllocJar));
            }
            if (creditRatingJar.length()>0){
                credit_layout.setVisibility(View.VISIBLE);
                credit_title.setVisibility(View.VISIBLE);
                listView_asset.setAdapter(new MyAdapter(homeActivity, creditRatingJar));
            }*/

            processInstrumentBreakup(assetAllocJar);
            if (FactSheetPagerFragment.selectedAsset == MF_ASSET.DEBT || FactSheetPagerFragment.selectedAsset == MF_ASSET.LIQUID){
                marketCap_ratingTitle.setText("Rating Allocation");
                JSONArray instrumentJar = cumulativeRatingData(creditRatingJar);
                processMarketCapAllocation(instrumentJar);
                marketCapNote.setVisibility(View.GONE);
            }else {
                marketCap_ratingTitle.setText("Market Cap Allocation");
                processMarketCapAllocation(jsonData.getJSONArray("marketcapallocation"));
                marketCapNote.setVisibility(View.VISIBLE);
            }

            String holdingDate = jsonData.getString("HoldingDate");
            if (holdingDate.equalsIgnoreCase("")
                    || holdingDate.equalsIgnoreCase("null")){
                holdingDate = "NA";
            }
            note_date.setText("Data as on "+holdingDate);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void processInstrumentBreakup(JSONArray instrumentJar) {
        try{
            PieView pieChart = view.findViewById(R.id.pieChart);
            LinearLayout pieIndicatorLayout = view.findViewById(R.id.pieIndicatorLayout);

            TextView asOnDate = view.findViewById(R.id.asOnDate);
            TextView instrument_tv = view.findViewById(R.id.instrument_tv);
            TextView holding_percentage_tv = view.findViewById(R.id.holding_percentage_tv);

            asOnDate.setText("Date as on "+DateUtil.getCurrentDate());
            List<StructNameValue> nameValueList = new ArrayList();
            float total = 0;
            for (int i = 0; i <instrumentJar.length() ; i++) {
                JSONObject job = instrumentJar.getJSONObject(i);
                StructNameValue obj = new StructNameValue();
                if (i==0){
                    instrument_tv.setText(job.getString("Assettype"));
                    holding_percentage_tv.setText(Formatter.stringTocommaPer(job.getString("Holdings"))+"%");
                }
                float value = job.getString("Holdings").equalsIgnoreCase("")?0:Float.parseFloat(job.getString("Holdings"));
                total += value;
                obj.setName(job.getString("Assettype"));
                obj.setValue(value);
                nameValueList.add(obj);
            }
            if (total>0){
                new PieChart().setChartView(pieChart, nameValueList, total, pieIndicatorLayout, homeActivity);
            }
        }catch (Exception e){
            e.printStackTrace();
            //displayError(e.getMessage());
        }
    }

    private void processMarketCapAllocation(JSONArray instrumentJar) {
        try{
            PieView pieChart = view.findViewById(R.id.pieChart_market_cap);
            LinearLayout pieIndicatorLayout = view.findViewById(R.id.pieIndicatorLayout_market_cap);

            TextView instrument_tv = view.findViewById(R.id.market_cap_tv);
            TextView holding_percentage_tv = view.findViewById(R.id.market_cap_percentage_tv);

            List<StructNameValue> nameValueList = new ArrayList();
            float total = 0;
            for (int i = 0; i <instrumentJar.length() ; i++) {
                JSONObject job = instrumentJar.getJSONObject(i);
                StructNameValue obj = new StructNameValue();
                String compName = "";
                if (FactSheetPagerFragment.selectedAsset == MF_ASSET.DEBT || FactSheetPagerFragment.selectedAsset == MF_ASSET.LIQUID){
                    compName = job.getString("Rating");
                }else {
                    compName = job.getString("cap");
                }
                if (i==0){
                    instrument_tv.setText(compName);
                    holding_percentage_tv.setText(Formatter.stringTocommaPer(job.getString("Holdings"))+"%");
                }
                float value = job.getString("Holdings").equalsIgnoreCase("")?0:Float.parseFloat((job.getString("Holdings")));
                total += value;
                obj.setName(compName);
                obj.setValue(value);
                nameValueList.add(obj);
            }
            if (total>0){
                new PieChart().setChartView(pieChart, nameValueList, total, pieIndicatorLayout, homeActivity);
            }
        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private JSONArray cumulativeRatingData(JSONArray jar){
        JSONArray jsonArray = new JSONArray();
        HashMap<String, JSONObject> hm = new HashMap<>();
        try {
            for (int i = 0; i <jar.length() ; i++) {
                JSONObject job = jar.getJSONObject(i);
                String ratingName = job.getString("Rating");
                double value = Double.parseDouble(job.getString("Holdper"));
                if (hm.containsKey(ratingName)){
                    JSONObject jsonObject = hm.get(ratingName);
                    double valueCum = Double.parseDouble(jsonObject.getString("Holdings")) + value;
                    jsonObject.put("Holdings", valueCum);
                    hm.put(ratingName, jsonObject);
                }else {
                    String hold = job.getString("Holdper");
                    job.put("Holdings", hold);
                    job.remove("Holdper");
                    hm.put(ratingName, job);
                }
            }

            Set<String> stringSet = hm.keySet();
            for (String s : stringSet) {
                JSONObject itemObj = hm.get(s);
                jsonArray.put(itemObj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    class MyAdapter extends BaseAdapter {
        private Context context;
        private JSONArray jar;
        public MyAdapter(Context context, JSONArray jar) {
            this.context = context;
            this.jar = jar;
        }

        @Override
        public int getCount() {
            if (jar!=null){
                return jar.length();
            }else
                return 0;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fact_sheet_asset_allocation_list_item, parent, false);
            if (convertView != null){
                try {
                    JSONObject job = jar.getJSONObject(position);
                    TextView instrument_name_tv = convertView.findViewById(R.id.instrument_name_tv);
                    TextView allocation_per_tv = convertView.findViewById(R.id.allocation_per_tv);
                    instrument_name_tv.setText(job.optString("Assettype"));
                    allocation_per_tv.setText(job.optString("Holdings"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return convertView;
        }
    }
}