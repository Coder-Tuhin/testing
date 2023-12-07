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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Formatter;
import utils.GlobalClass;
import utils.PreferenceHandler;
import view.TvRegular;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.MFObjectHolder;

/**
 * Created by XTREMSOFT on 03-Apr-2018.
 */

public class MFAssetAllocation extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    private MFAssetAllocationAdapter mfAdapter;

    private ArrayList<JSONObject> familyList;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.equityTitle)
    TextView equityTitle;
    @BindView(R.id.debtTitle)
    TextView debtTitle;
    @BindView(R.id.othersTitle)
    TextView othersTitle;
    @BindView(R.id.amountPerRd)
    RadioGroup amountPerRd;
    @BindView(R.id.totequity)
    TvRegular totEquity;
    @BindView(R.id.totdebt)
    TvRegular totdebt;
    @BindView(R.id.totother)
    TvRegular totother;

    @BindView(R.id.mfSpinnerfamily)
    Spinner spinnerFamily;


    public static MFAssetAllocation newInstance(){
        return new MFAssetAllocation();
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
            mView = inflater.inflate(R.layout.mf_assetallocation,container,false);
            ButterKnife.bind(this,mView);
            mfAdapter = new MFAssetAllocationAdapter();
            recyclerView.setAdapter(mfAdapter);
            amountPerRd.setOnCheckedChangeListener(checkChangeListener);
            setTitle();
        }
        new AssetAllocationReq(eMessageCodeWealth.FAMILY_DATA.value).execute();
        return mView;
    }

    private void setTitle(){
        String suffix = PreferenceHandler.getAmtPercent()?"(\u20B9)":"(%)";
        equityTitle.setText("Equity"+suffix);
        debtTitle.setText("Debt"+suffix);
        othersTitle.setText("Gold/Others"+suffix);
    }

    public class MFAssetAllocationAdapter extends RecyclerView.Adapter<MFAssetAllocationAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public MFAssetAllocationAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }

        public void reloadData(ArrayList<JSONObject> data){
            mList = data;
            this.notifyDataSetChanged();
        }

        @Override
        public MFAssetAllocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_assetallocation, parent, false);
            MFAssetAllocationAdapter.MyViewHolder holder = new MFAssetAllocationAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MFAssetAllocationAdapter.MyViewHolder holder, final int position) {

            try {
                JSONObject jData = mList.get(position);

                double other = jData.getDouble("Gold") + jData.getDouble("Others");
                holder.schemeName.setText(jData.getString("S_name"));
                holder.equity.setText(Formatter.DecimalLessIncludingComma(jData.getString("Equity")));
                holder.debt.setText(Formatter.DecimalLessIncludingComma(jData.getString("Debt")));
                holder.other.setText(Formatter.DecimalLessIncludingComma(other + ""));
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
            @BindView(R.id.schemeName)
            TextView schemeName;
            @BindView(R.id.equity)
            TextView equity;
            @BindView(R.id.debt)
            TextView debt;
            @BindView(R.id.other)
            TextView other;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private RadioGroup.OnCheckedChangeListener checkChangeListener =  new RadioGroup.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            boolean isAmt = id == R.id.amount;
            PreferenceHandler.setAmtPercent(isAmt);
            setTitle();
        }
    };

    class AssetAllocationReq extends AsyncTask<String, Void, String> {

        int msgCode;
        AssetAllocationReq(int mc){
            this.msgCode = mc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                    familyList = VenturaServerConnect.getFamilyMembers();
                    return familyList.toString();
                } else {

                    JSONObject selectedFamily = familyList.get(spinnerFamily.getSelectedItemPosition());
                    JSONObject jsonDataReq = new JSONObject();
                    jsonDataReq.put(eMFJsonTag.FAMILYCODE.name, selectedFamily.getString("ClientID"));
                    jsonDataReq.put(eMFJsonTag.CLIENTTYPE.name, selectedFamily.getString("Flag"));

                    String key = selectedFamily.getString("ClientID");
                    JSONObject jsonData = MFObjectHolder.assetAllocation.get(key);
                    if(jsonData == null) {
                        jsonData=VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jsonDataReq);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.assetAllocation.put(key,jsonData);
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

            if(s != null){
                try {
                    if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                        displayFamilyMember();
                    } else {
                        JSONObject jsonData = new JSONObject(s);
                        if (!jsonData.isNull("error")) {
                            String err = jsonData.getString("error");
                            displayError(err);
                        }
                        else {
                            displaData(jsonData, msgCode);
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        }
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
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
            //new AssetAllocationReq(eMessageCodeWealth.ASSET_ALLOCATION.value).execute();
            spinnerFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    new AssetAllocationReq(eMessageCodeWealth.ASSET_ALLOCATION.value).execute();
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
    private void displaData(JSONObject jsonData, int msgCode) {

        if (msgCode == eMessageCodeWealth.ASSET_ALLOCATION.value){
            try {
                double dTotEquity=0,dTotDebt=0,dTotOther=0;
                ArrayList<JSONObject> jList = new ArrayList<>();
                GlobalClass.log("ASSETALOCATIONMF : " + jsonData.toString());
                JSONArray jsonArr = jsonData.getJSONArray("data");
                for(int i=0;i<jsonArr.length();i++){
                    JSONObject jsonD = jsonArr.getJSONObject(i);
                    jList.add(jsonD);

                    dTotEquity = dTotEquity + jsonD.getDouble("Equity");
                    dTotDebt = dTotDebt + jsonD.getDouble("Debt");
                    dTotOther = dTotOther + jsonD.getDouble("Gold") + jsonD.getDouble("Others");
                }
                if(mfAdapter != null){
                    mfAdapter.reloadData(jList);
                }
                totEquity.setText(Formatter.DecimalLessIncludingComma(dTotEquity + ""));
                totdebt.setText(Formatter.DecimalLessIncludingComma(dTotDebt + ""));
                totother.setText(Formatter.DecimalLessIncludingComma(dTotOther + ""));
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else if(msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value){


        }
    }
}
