package wealth.new_mutualfund.dropdowns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.StaticMethods;
import utils.UserSession;
import view.TvRegular;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.MFObjectHolder;

/**
 * Created by XTREMSOFT on 02-Apr-2018.
 */

public class MFRunningSIP extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    private MFHoldingReportAdapter mfReportAdapter;
    private ArrayList<String> assetTypeList;
    private ArrayList<JSONObject> familyMemberList;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.totpur)
    TvRegular totPur;
    @BindView(R.id.totsip)
    TvRegular totsipamt;
    @BindView(R.id.totcurramt)
    TvRegular totcurramt;
    @BindView(R.id.asondate)
    TvRegular asonDate;
    @BindView(R.id.mfSpinnerAssetType)
    Spinner spinnerAssetType;
    @BindView(R.id.mfSpinnerfamily)
    Spinner spinnerFamilyMember;

    @BindView(R.id.siptotallayout)
    LinearLayout siptotallayout;

    public static MFRunningSIP newInstance(){
        return new MFRunningSIP();
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
            mView = inflater.inflate(R.layout.mf_runningsip,container,false);
            ButterKnife.bind(this,mView);
            mfReportAdapter = new MFHoldingReportAdapter();
            recyclerView.setAdapter(mfReportAdapter);
            new RunningSIPReq(eMessageCodeWealth.FAMILY_DATA.value).execute();
            //new RunningSIPReq(eMessageCodeWealth.ASSETTYPE_DATA.value).execute();
        }
        return mView;
    }
    public class MFHoldingReportAdapter extends RecyclerView.Adapter<MFHoldingReportAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public MFHoldingReportAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_runningsip, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                JSONObject jData = mList.get(position);
                Double gainLoss = Formatter.stringToDouble(jData.getString("ProfitLoss"));
                holder.schemeName.setText(jData.getString("SchemeName"));
                holder.enddate.setText(jData.getString("EndDate"));
                holder.sippm.setText(Formatter.DecimalLessIncludingComma(jData.getString("SIPAmt")));
                holder.puramt.setText(Formatter.DecimalLessIncludingComma(jData.getString("PurchaseAmt")));
                holder.curAmt.setText(Formatter.DecimalLessIncludingComma(jData.getString("CurrentAmt")));

                String startDateStr = jData.getString("StartDate");
                String frequency = jData.getString("Freq");
                holder.sip_next_date.setText(DateUtil.futureSipDate(startDateStr, frequency));
                if (gainLoss < 0) {
                    holder.curAmt.setTextColor(ScreenColor.RED);
                } else {
                    holder.curAmt.setTextColor(ScreenColor.GREEN);
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
            @BindView(R.id.schemeName)
            TextView schemeName;
            @BindView(R.id.curAmt)
            TextView curAmt;
            @BindView(R.id.puramt)
            TextView puramt;
            @BindView(R.id.sippm)
            TextView sippm;
            @BindView(R.id.enddate)
            TextView enddate;
            @BindView(R.id.sip_next_date)
            TextView sip_next_date;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                curAmt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupWindow popUp = new PopupWindow(homeActivity);
                        View view = LayoutInflater.from(homeActivity).inflate(R.layout.sip_popup,null);
                        popUp.setContentView(view);
                        popUp.showAtLocation(v, Gravity.CENTER, 0, v.getHeight());
                        popUp.setOutsideTouchable(true);
                        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popUp.dismiss();
                            }
                        });
                        StaticMethods.PopupDimBehind(popUp);
                    }
                });
            }
        }
    }

    class RunningSIPReq extends AsyncTask<String, Void, String> {

        int msgCode;
        RunningSIPReq(int msgCode){
            this.msgCode = msgCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                if (msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value) {
                    assetTypeList = VenturaServerConnect.getAssetType();
                    return assetTypeList.toString();
                } else if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                    familyMemberList = VenturaServerConnect.getFamilyMembers();
                    return familyMemberList.toString();
                } else {

                    JSONObject selectedFamily = familyMemberList.get(spinnerFamilyMember.getSelectedItemPosition());
                    JSONObject jsonDataReq = new JSONObject();
                    jsonDataReq.put(eMFJsonTag.FAMILYCODE.name, selectedFamily.getString("ClientID"));
                    jsonDataReq.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    //jsonDataReq.put(eMFJsonTag.ASSET.name, spinnerAssetType.getSelectedItem().toString());
                    jsonDataReq.put(eMFJsonTag.ASSET.name, "");
                    jsonDataReq.put(eMFJsonTag.CLIENTTYPE.name, selectedFamily.getString("Flag"));

                    String key = selectedFamily.getString("ClientID");
                    JSONObject jsonData = MFObjectHolder.runningSIP.get(key);
                    if(jsonData == null) {
                        jsonData=VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jsonDataReq);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.runningSIP.put(key,jsonData);
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
            if (msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value) {
                displayAssetType();
            } else if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                displayFamilyMember();
            } else {

                if (s != null) {
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
        GlobalClass.showAlertDialog(err);
    }

    private  void displayAssetType(){
        spinnerAssetType.setOnItemSelectedListener(null);
        String[] mStringArray = new String[assetTypeList.size()];
        mStringArray = assetTypeList.toArray(mStringArray);
        setSpinnerData(spinnerAssetType,mStringArray);
        new RunningSIPReq(eMessageCodeWealth.FAMILY_DATA.value).execute();

        spinnerAssetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                new RunningSIPReq(eMessageCodeWealth.RUNNING_SIP.value).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
    private  void displayFamilyMember(){

        try {
            spinnerFamilyMember.setOnItemSelectedListener(null);
            String[] mStringArray = new String[familyMemberList.size()];
            int selectedIndex = 0;
            for (int i = 0; i < mStringArray.length; i++) {
                JSONObject jdata = familyMemberList.get(i);
                mStringArray[i] = jdata.getString("ClientName");
                String clientId = jdata.getString("ClientID");
                if(VenturaServerConnect.mfClientID.equalsIgnoreCase(clientId)){
                    selectedIndex = i;
                }
            }
            //mStringArray = assetTypeList.toArray(mStringArray);
            setSpinnerData(spinnerFamilyMember, mStringArray);
            spinnerFamilyMember.setSelection(selectedIndex);
            spinnerFamilyMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    new RunningSIPReq(eMessageCodeWealth.RUNNING_SIP.value).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
            //new RunningSIPReq(eMessageCodeWealth.RUNNING_SIP.value).execute();

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

            //double dTotPur=0,dTotCurrAmt=0,dTotSIP=0;

            ArrayList<JSONObject> jList = new ArrayList<>();
            GlobalClass.log("RunniSIP : " + jsonData.toString());
            JSONArray jsonArr = jsonData.getJSONArray("data");
            JSONObject jsonTotal = null;

            for(int i=0;i<jsonArr.length();i++){
                try {
                    JSONObject jsonD = jsonArr.getJSONObject(i);
                    String schName = jsonD.getString("SchemeName");
                    if(schName.equalsIgnoreCase("Grand Total")){
                        jsonTotal = jsonD;
                    }
                    else{
                        if(!jsonD.isNull("Schemecode")) {
                            jList.add(jsonD);
                        }
                    }
                    /*
                    dTotPur = dTotPur + Formatter.stringToDouble(jsonD.getString("PurchaseAmt"));
                    dTotCurrAmt = dTotCurrAmt + Formatter.stringToDouble(jsonD.getString("CurrentAmt"));
                    dTotSIP = dTotSIP + Formatter.stringToDouble(jsonD.getString("SIPAmt"));
                    */
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if(mfReportAdapter != null){
                mfReportAdapter.reloadData(jList);
            }
            if(jsonTotal != null) {
                siptotallayout.setVisibility(View.VISIBLE);
                totPur.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("PurchaseAmt")));
                totcurramt.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("CurrentAmt")));
                totsipamt.setText(Formatter.DecimalLessIncludingComma(jsonTotal.getString("SIPAmt")));

                if(Formatter.stringToDouble(jsonTotal.getString("CurrentAmt")) > Formatter.stringToDouble(jsonTotal.getString("PurchaseAmt"))){
                    totcurramt.setTextColor(ScreenColor.GREEN);
                } else {
                    totcurramt.setTextColor(ScreenColor.RED);
                }
            }
            else{
                siptotallayout.setVisibility(View.GONE);
            }
            asonDate.setText(DateUtil.getCurrentDatenew());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}