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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.MF_ASSET;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class FactSheetHoldingFragment extends Fragment {
    private String schemeCode;
    private static final String ARG_PARAM1 = "param1";

    private HomeActivity homeActivity;
    private JSONArray companyJar, sectorJar;

    @BindView(R.id.listView_company) ListView listView_company;
    @BindView(R.id.listView_sectors) ListView listView_sectors;
    @BindView(R.id.company_no_data_tv) TextView company_no_data_tv;
    @BindView(R.id.sectors_no_data_tv) TextView sectors_no_data_tv;
    @BindView(R.id.parentLayout_comp) LinearLayout parentLayout_comp;
    @BindView(R.id.parentLayout_sec) LinearLayout parentLayout_sec;
    @BindView(R.id.holding_layout) LinearLayout holding_layout;
    @BindView(R.id.sector_layout) LinearLayout sector_layout;
    @BindView(R.id.note_tv) TextView note_tv;

    public FactSheetHoldingFragment() {
        // Required empty public constructor
    }

    public static FactSheetHoldingFragment newInstance(String schmCode) {
        FactSheetHoldingFragment fragment = new FactSheetHoldingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, schmCode);
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
        View view = inflater.inflate(R.layout.fragment_fact_sheet_holding, container, false);
        ButterKnife.bind(this, view);
        visGoneCompany(View.GONE, View.VISIBLE);
        visGoneSector(View.GONE, View.VISIBLE);

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

    private void visGoneCompany(int listVisibility, int noDataVisibility){
        holding_layout.setVisibility(listVisibility);
        company_no_data_tv.setVisibility(noDataVisibility);
    }
    private void visGoneSector(int listVisibility, int noDataVisibility){
        sector_layout.setVisibility(listVisibility);
        sectors_no_data_tv.setVisibility(noDataVisibility);
    }

    private void processPortfolioData(JSONObject jsonData) {
        try {
            if (FactSheetPagerFragment.selectedAsset== MF_ASSET.DEBT || FactSheetPagerFragment.selectedAsset== MF_ASSET.LIQUID ){
                companyJar = jsonData.getJSONArray("Ratingallocation");
            }else{
                companyJar = jsonData.getJSONArray("toptenholdingcompany");
            }
            sectorJar = jsonData.getJSONArray("toptensector");
            if (companyJar.length()>0){
                visGoneCompany(View.VISIBLE, View.GONE);
                setCustomListView(WHAT.COMPANY.name, companyJar);
            }
            if (sectorJar.length()>0){
                visGoneSector(View.VISIBLE, View.GONE);
                setCustomListView(WHAT.SECTOR.name, sectorJar);
            }

            String holdingDate = jsonData.getString("HoldingDate");
            if (holdingDate.equalsIgnoreCase("")
            || holdingDate.equalsIgnoreCase("null")){
                holdingDate = "NA";
            }
            note_tv.setText("Data as on "+holdingDate);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setCustomListView(String what, JSONArray jar){
        try {
            for (int i = 0; i <jar.length() ; i++) {
                LayoutInflater inflater = LayoutInflater.from(homeActivity);
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.fact_sheet_holding_list_item, null, false);

                JSONObject job = jar.getJSONObject(i);
                TextView name_tv = convertView.findViewById(R.id.name);
                TextView value_tv = convertView.findViewById(R.id.value);
                ImageView imageView = convertView.findViewById(R.id.image);

                String name, value, direction;
                if (what.equalsIgnoreCase(WHAT.COMPANY.name)){
                    if (FactSheetPagerFragment.selectedAsset== MF_ASSET.DEBT || FactSheetPagerFragment.selectedAsset== MF_ASSET.LIQUID ){
                        name = job.optString("Compname");
                        value = Formatter.stringTocommaPer(job.optString("Holdings"));
                    }else{
                        name = job.optString("CompName");
                        value = Formatter.stringTocommaPer(job.optString("HoldPer"));
                    }

                }else {
                    name = job.optString("SecName");
                    value = Formatter.stringTocommaPer(job.optString("Holdings"));
                }
                direction = job.optString("Status");

                name_tv.setText(name);
                value_tv.setText(value);
                if (direction.equalsIgnoreCase("Decrease")){
                    imageView.setBackgroundResource(R.drawable.fact_sheet_holding_down_arrow_3);
                }else {
                    imageView.setBackgroundResource(R.drawable.fact_sheet_holding_up_arrow_3);
                }

                if (what.equalsIgnoreCase(WHAT.COMPANY.name)){
                    parentLayout_comp.addView(convertView);
                }else {
                    parentLayout_sec.addView(convertView);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    enum WHAT{
        COMPANY("C"),
        SECTOR("S");
        public String name;
        WHAT(String name) {
            this.name = name;
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    class MyAdapter extends BaseAdapter {
        private Context context;
        private JSONArray jar;
        private String what;
        public MyAdapter(Context context, JSONArray jar, String what) {
            this.context = context;
            this.jar = jar;
            this.what = what;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.fact_sheet_holding_list_item, parent, false);
            if (convertView != null){
                try {
                    JSONObject job = jar.getJSONObject(position);
                    TextView name_tv = convertView.findViewById(R.id.name);
                    TextView value_tv = convertView.findViewById(R.id.value);
                    ImageView imageView = convertView.findViewById(R.id.image);

                    String name, value, direction;
                    if (what.equalsIgnoreCase(WHAT.COMPANY.name)){
                        name = job.optString("CompName");
                        value = job.optString("HoldPer");
                    }else {
                        name = job.optString("SecName");
                        value = job.optString("Holdings");
                    }
                    direction = job.optString("Status");

                    name_tv.setText(name);
                    value_tv.setText(value);
                    if (direction.equalsIgnoreCase("Decrease")){
                        imageView.setBackgroundResource(R.drawable.fact_sheet_holding_down_arrow);
                    }else {
                        imageView.setBackgroundResource(R.drawable.fact_sheet_holding_up_arrow);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return convertView;
        }
    }

    public void setDynamicHeight(ListView mListView) {
        GlobalClass.log("Dynamic height initiated");
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);

        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            int itemH = listItem.getMeasuredHeight();
            GlobalClass.log("SetDynamic: Position: "+i+" itemHeight: "+itemH);
            height += itemH;
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}

