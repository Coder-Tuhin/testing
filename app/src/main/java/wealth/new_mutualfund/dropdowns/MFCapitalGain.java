package wealth.new_mutualfund.dropdowns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.FiscalDate;
import wealth.new_mutualfund.Structure.MFObjectHolder;

/**
 * Created by XTREMSOFT on 02-Apr-2018.
 */

public class MFCapitalGain extends Fragment {
    private HomeActivity homeActivity;
    private View mView;
    private MFHoldingReportAdapter mfReportAdapter;
    private ArrayList<JSONObject> familyList;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.indexationRd)
    RadioGroup indexationRd;
    @BindView(R.id.withoutIndexSationLinear)
    LinearLayout withoutIndexSationLinear;
    @BindView(R.id.comingsoonTv)
    TextView comingsoonTv;
    @BindView(R.id.totstd)
    TextView totstd;
    @BindView(R.id.totste)
    TextView totste;
    @BindView(R.id.totltd)
    TextView totltd;
    @BindView(R.id.totlte)
    TextView totlte;

    @BindView(R.id.mfSpinnerfamily)
    Spinner spinnerFamily;
    @BindView(R.id.mfSpinnerFinancialYear)
    Spinner spinnerFinancialYear;

    public static MFCapitalGain newInstance(){
        return new MFCapitalGain();
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
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.mf_capgainloss,container,false);
            ButterKnife.bind(this,mView);
            mfReportAdapter = new MFHoldingReportAdapter();
            recyclerView.setAdapter(mfReportAdapter);
            indexationRd.setOnCheckedChangeListener(checkChange);
            initFinancialSpinner();
            new CapitalGainReq(eMessageCodeWealth.FAMILY_DATA.value).execute();
        }
        return mView;
    }

    private void initFinancialSpinner(){

        FiscalDate fcDate = new FiscalDate(new Date());
        int yearfcDate = fcDate.getFiscalYear();
        String currYr = yearfcDate + "-" + (yearfcDate +1);
        String prevYr = (yearfcDate-1) + "-" + (yearfcDate);
        String[] fyArr = new String[]{currYr,prevYr};
        setSpinnerData(spinnerFinancialYear,fyArr);

    }
    public class MFHoldingReportAdapter extends RecyclerView.Adapter<MFHoldingReportAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<StructCapitalGain> mList;

        public MFHoldingReportAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<StructCapitalGain> value){
            this.mList.clear();
            this.mList = value;
            this.notifyDataSetChanged();
        }
        public void reloadData(){
            this.mList.clear();
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_capitalgainloss, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            try {
                StructCapitalGain jData = mList.get(position);
                holder.name.setText(jData.schemeName);
                holder.shortdebt.setText(Formatter.DecimalLessIncludingComma(jData.shortDebt + ""));
                holder.shortequity.setText(Formatter.DecimalLessIncludingComma(jData.shortEquity + ""));
                if(indexationRd.getCheckedRadioButtonId() == R.id.withRbtn){
                    holder.longdebt.setText(Formatter.DecimalLessIncludingComma(jData.longDebtIndex+""));
                }else {
                    holder.longdebt.setText(Formatter.DecimalLessIncludingComma(jData.longDebt+""));
                }

                holder.longequity.setText(Formatter.DecimalLessIncludingComma(jData.longEquity +""));

                if (jData.shortDebt < 0) {
                    holder.shortdebt.setTextColor(ObjectHolder.RED);
                } else {
                    holder.shortdebt.setTextColor(ObjectHolder.GREEN);
                }
                if (jData.shortEquity < 0) {
                    holder.shortequity.setTextColor(ObjectHolder.RED);
                } else {
                    holder.shortequity.setTextColor(ObjectHolder.GREEN);
                }
                if (jData.longDebt < 0) {
                    holder.longdebt.setTextColor(ObjectHolder.RED);
                } else {
                    holder.longdebt.setTextColor(ObjectHolder.GREEN);
                }
                if (jData.longEquity < 0) {
                    holder.longequity.setTextColor(ObjectHolder.RED);
                } else {
                    holder.longequity.setTextColor(ObjectHolder.GREEN);
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {

            return mList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.name)
            TextView name;
            @BindView(R.id.shortdebt)
            TextView shortdebt;
            @BindView(R.id.shortequity)
            TextView shortequity;
            @BindView(R.id.longdebt)
            TextView longdebt;
            @BindView(R.id.longequity)
            TextView longequity;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private RadioGroup.OnCheckedChangeListener checkChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
            switch (checkId){
                case R.id.withoutRbtn:
                    comingsoonTv.setVisibility(View.GONE);
                    GlobalClass.showProgressDialog("Requesting...");
                    new CapitalGainReq(eMessageCodeWealth.CAPITAL_GAIN_LOSS.value).execute();
                    withoutIndexSationLinear.setVisibility(View.VISIBLE);
                    break;
                case R.id.withRbtn:
                    comingsoonTv.setVisibility(View.GONE);
                    GlobalClass.showProgressDialog("Requesting...");
                    new CapitalGainReq(eMessageCodeWealth.CAPITAL_GAIN_LOSS.value).execute();
                    withoutIndexSationLinear.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    class CapitalGainReq extends AsyncTask<String, Void, String> {

        int msgCode;

        CapitalGainReq(int msgC){
            this.msgCode = msgC;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            comingsoonTv.setVisibility(View.GONE);
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                    familyList = VenturaServerConnect.getFamilyMembers();

                    for (int i = 0; i < familyList.size(); i++) {
                        String name = familyList.get(i).getString("ClientName");
                        if(name.equalsIgnoreCase("All")){
                            familyList.remove(i);
                        }
                    }
                    return familyList.toString();
                }
                else if (msgCode == eMessageCodeWealth.CAPITAL_GAIN_LOSS.value) {
                    JSONObject selectedFamily = familyList.get(spinnerFamily.getSelectedItemPosition());
                    JSONObject jsonDataReq = new JSONObject();
                    jsonDataReq.put(eMFJsonTag.FAMILYCODE.name, selectedFamily.getString("ClientID"));
                    jsonDataReq.put(eMFJsonTag.FINANCIALYR.name, spinnerFinancialYear.getSelectedItem().toString());
                    jsonDataReq.put(eMFJsonTag.CLIENTTYPE.name, selectedFamily.getString("Flag"));

                    String key = selectedFamily.getString("ClientID") + "_"+spinnerFinancialYear.getSelectedItem().toString();
                    JSONObject jsonData = MFObjectHolder.capitalGain.get(key);
                    if(jsonData == null) {
                        jsonData=VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jsonDataReq);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.capitalGain.put(key,jsonData);
                        return jsonData.toString();
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();

            if(s != null) {
                if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                    displayFamilyMember();
                } else {
                    try {
                        JSONObject jsonData = new JSONObject(s);
                        if (!jsonData.isNull("error")) {
                            String err = jsonData.getString("error");
                            displayError(err);
                        } else {
                            displaData(jsonData);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    private void displayError(String err){
        comingsoonTv.setVisibility(View.VISIBLE);
        mfReportAdapter.reloadData();
        totstd.setText("");
        totste.setText("");
        totltd.setText("");
        totlte.setText("");

    }
    private  void displayFamilyMember(){

        try {
            spinnerFamily.setOnItemSelectedListener(null);
            String[] mStringArray = new String[familyList.size()];
            int selectedIndex = 0;
            for (int i = 0; i < mStringArray.length; i++) {
                JSONObject jdata = familyList.get(i);
                mStringArray[i] = jdata.getString("ClientName");
                String clientId = jdata.getString("ClientID");
                if(VenturaServerConnect.mfClientID.equalsIgnoreCase(clientId)){
                    selectedIndex = i;
                }
            }
            //mStringArray = assetTypeList.toArray(mStringArray);
            setSpinnerData(spinnerFamily, mStringArray);
            spinnerFamily.setSelection(selectedIndex);
            //new CapitalGainReq(eMessageCodeWealth.CAPITAL_GAIN_LOSS.value).execute();
            spinnerFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    new CapitalGainReq(eMessageCodeWealth.CAPITAL_GAIN_LOSS.value).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
            spinnerFinancialYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    new CapitalGainReq(eMessageCodeWealth.CAPITAL_GAIN_LOSS.value).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void setSpinnerData(Spinner spinner, String[] data){
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity,R.layout.custom_spinner_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data);
        spinner.setAdapter(categoryAdp);
    }

    private void displaData(JSONObject jsonData) {

        try {
            LinkedHashMap<String,StructCapitalGain> capitalGainHM = new LinkedHashMap<>();

            //GlobalClass.log("HoldingMF : " + jsonData.toString());
            JSONObject jsonTotal = null;
            JSONArray jsonArr = jsonData.getJSONArray("data");
            for(int i=0;i<jsonArr.length();i++){
                try {

                    JSONObject jsonD = jsonArr.getJSONObject(i);
                    String purP = jsonD.getString("PurPrice");
                    if(!purP.equalsIgnoreCase("")) {
                        String schemename = jsonD.getString("SchemeName");
                        String[] namesList = schemename.split("-");
                        String scheme = namesList [0];

                        if (scheme.toLowerCase().contains("total")) {
                            jsonTotal = jsonD;
                        }
                        else{

                            StructCapitalGain capitalGain = capitalGainHM.get(scheme);
                            if(capitalGain == null){
                                capitalGain = new StructCapitalGain();
                                capitalGain.schemeName = scheme;
                                capitalGainHM.put(scheme,capitalGain);
                            }

                            Double shortDebt = Formatter.stringToDouble(jsonD.getString("Shorttermdebt"));
                            Double shortEquity = Formatter.stringToDouble(jsonD.getString("Shorttermequity"));
                            Double longDebt = Formatter.stringToDouble(jsonD.getString("Longtermdebt"));
                            Double longDebt_index = Formatter.stringToDouble(jsonD.getString("LongTermIndexDebt"));
                            Double longEquity = Formatter.stringToDouble(jsonD.getString("LongTermGFEquity"));

                            capitalGain.shortDebt = capitalGain.shortDebt + shortDebt;
                            capitalGain.shortEquity = capitalGain.shortEquity + shortEquity;
                            capitalGain.longDebt = capitalGain.longDebt + longDebt;
                            capitalGain.longDebtIndex = capitalGain.longDebtIndex + longDebt_index;
                            capitalGain.longEquity = capitalGain.longEquity + longEquity;
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            ArrayList<StructCapitalGain> jList = new ArrayList<>(capitalGainHM.values());
            if(mfReportAdapter != null){
                mfReportAdapter.reloadData(jList);
            }
            if(jsonTotal != null) {
                totstd.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("Shorttermdebt")));
                totltd.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("Shorttermequity")));
                if(indexationRd.getCheckedRadioButtonId() == R.id.withRbtn){
                    totste.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("LongTermIndexDebt")));
                }else {
                    totste.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("Longtermdebt")));
                }

                totlte.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("TaxableGLEquity")));
                if (Formatter.stringToDouble(jsonTotal.getString("Shorttermdebt")) < 0) {
                    totstd.setTextColor(ObjectHolder.RED);
                } else {
                    totstd.setTextColor(ObjectHolder.GREEN);
                }
                if (Formatter.stringToDouble(jsonTotal.getString("Longtermdebt")) < 0) {
                    totste.setTextColor(ObjectHolder.RED);
                } else {
                    totste.setTextColor(ObjectHolder.GREEN);
                }
                if (Formatter.stringToDouble(jsonTotal.getString("Shorttermequity")) < 0) {
                    totltd.setTextColor(ObjectHolder.RED);
                } else {
                    totltd.setTextColor(ObjectHolder.GREEN);
                }
                if (Formatter.stringToDouble(jsonTotal.getString("LongTermGFEquity")) < 0) {
                    totlte.setTextColor(ObjectHolder.RED);
                } else {
                    totlte.setTextColor(ObjectHolder.GREEN);
                }
            }
            else{
                totstd.setText("");
                totste.setText("");
                totltd.setText("");
                totlte.setText("");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class StructCapitalGain{

        public String schemeName;
        public double shortDebt;
        public double shortEquity;
        public double longDebt;
        public double longDebtIndex;
        public double longEquity;

        public StructCapitalGain(){
            shortDebt = 0;
            shortEquity = 0;
            longDebt = 0;
            longEquity = 0;
            longDebtIndex = 0;
        }
    }
}