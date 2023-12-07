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
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Formatter;
import utils.GlobalClass;
import utils.StaticMethods;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.FiscalDate;
import wealth.new_mutualfund.Structure.MFObjectHolder;

/**
 * Created by XTREMSOFT on 03-Apr-2018.
 */

public class MFDividenreportsum extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    private ArrayList<String> assetTypeList;
    private ArrayList<JSONObject> familyList;
    private MFDividenreportsumAdapter mfReportAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.totgd)
    TextView totgd;
    @BindView(R.id.financialYr)
    TextView financialYr;
    //@BindView(R.id.mfSpinnerAssetType)
    //Spinner spinnerAssetType;
    @BindView(R.id.mfSpinnerfamily)
    Spinner spinnerFamily;


    public static MFDividenreportsum newInstance() {
        return new MFDividenreportsum();
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
        if (mView == null) {
            mView = inflater.inflate(R.layout.mf_dividendreportsum, container, false);
            ButterKnife.bind(this, mView);
            mfReportAdapter = new MFDividenreportsumAdapter();
            recyclerView.setAdapter(mfReportAdapter);
            initFinancialSpinner();
            new DividendReq(eMessageCodeWealth.FAMILY_DATA.value).execute();
        }
        return mView;
    }

    private void initFinancialSpinner(){

        FiscalDate fcDate = new FiscalDate(new Date());
        int yearfcDate = fcDate.getFiscalYear();
        String currYr = yearfcDate + "-" + (yearfcDate +1);
        financialYr.setText(currYr);
        //String prevYr = (yearfcDate-1) + "-" + (yearfcDate);
        //String[] fyArr = new String[]{currYr,prevYr};
        //setSpinnerData(spinnerFinancialYear,fyArr);
    }

    public class MFDividenreportsumAdapter extends RecyclerView.Adapter<MFDividenreportsumAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public MFDividenreportsumAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }

        @Override
        public MFDividenreportsumAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_dividensum, parent, false);
            MFDividenreportsumAdapter.MyViewHolder holder = new MFDividenreportsumAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MFDividenreportsumAdapter.MyViewHolder holder, final int position) {
            try {
                JSONObject jData = mList.get(position);
                holder.updateJsonData(jData);

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
            @BindView(R.id.amount)
            TextView amount;

            JSONObject jData;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final PopupWindow popUp = new PopupWindow(homeActivity);
                        View viewpopup = LayoutInflater.from(homeActivity).inflate(R.layout.devident_popup, null);

                        try {
                            TextView schemeName = (TextView) viewpopup.findViewById(R.id.schemeName);
                            schemeName.setText(jData.getString("Schemename"));
                            TextView totalValue = (TextView) viewpopup.findViewById(R.id.totalValue);
                            totalValue.setText(jData.getString("Total"));

                            TextView firstDate = (TextView) viewpopup.findViewById(R.id.firstdate);
                            TextView secondDate = (TextView) viewpopup.findViewById(R.id.seconddate);
                            TextView thirdDate = (TextView) viewpopup.findViewById(R.id.thirddate);
                            TextView fourthDate = (TextView) viewpopup.findViewById(R.id.fourthdate);

                            TextView firstValue = (TextView) viewpopup.findViewById(R.id.firstvalue);
                            TextView secondValue = (TextView) viewpopup.findViewById(R.id.secondvalue);
                            TextView thirdValue = (TextView) viewpopup.findViewById(R.id.thirdvalue);
                            TextView fourthValue = (TextView) viewpopup.findViewById(R.id.fourthvalue);

                            firstDate.setText("JanMar");
                            firstValue.setText(Formatter.DecimalLessIncludingComma(jData.getString("JanMar")));
                            secondDate.setText("AprJun");
                            secondValue.setText(Formatter.DecimalLessIncludingComma(jData.getString("AprJun")));
                            thirdDate.setText("JulSep");
                            thirdValue.setText(Formatter.DecimalLessIncludingComma(jData.getString("JulSep")));
                            fourthDate.setText("OctDec");
                            fourthValue.setText(Formatter.DecimalLessIncludingComma(jData.getString("OctDec")));
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                        }
                        popUp.setContentView(viewpopup);
                        popUp.showAtLocation(viewpopup, Gravity.CENTER, 0, viewpopup.getHeight());
                        popUp.setOutsideTouchable(true);
                        viewpopup.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //schemeName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus, 0);
                                popUp.dismiss();
                            }
                        });
                        StaticMethods.PopupDimBehind(popUp);
                    }
                });
                /*
                schemeName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //schemeName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.minus, 0);
                    }
                });*/
            }
            public void updateJsonData(JSONObject jData){
                try {
                    this.jData = jData;
                    schemeName.setText(jData.getString("Schemename"));
                    amount.setText(Formatter.DecimalLessIncludingComma(jData.getString("Total")));
                }
                catch (Exception ex){ex.printStackTrace();}
            }
        }
    }

    class DividendReq extends AsyncTask<String, Void, String> {

        int msgCode;
        DividendReq(int mCode){
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
                if (msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value) {
                    assetTypeList = VenturaServerConnect.getAssetType();
                    return assetTypeList.toString();
                } else if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                    familyList = VenturaServerConnect.getFamilyMembers();
                    return familyList.toString();
                } else {
                    JSONObject selectedFamily = familyList.get(spinnerFamily.getSelectedItemPosition());
                    JSONObject jsonDataReq = new JSONObject();
                    jsonDataReq.put(eMFJsonTag.FAMILYCODE.name, selectedFamily.getString("ClientID"));
                    jsonDataReq.put(eMFJsonTag.ASSET.name, "");
                    jsonDataReq.put(eMFJsonTag.FINANCIALYR.name, financialYr.getText());
                    jsonDataReq.put(eMFJsonTag.CLIENTTYPE.name, selectedFamily.getString("Flag"));

                    String key = selectedFamily.getString("ClientID")+"_"+financialYr.getText();
                    JSONObject jsonData = MFObjectHolder.dividendSumm.get(key);
                    if(jsonData == null) {
                        jsonData=VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jsonDataReq);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.dividendSumm.put(key,jsonData);
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
                    if (msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value) {
                        displayAssetType();
                    } else if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                        displayFamilyMember();
                    } else {
                        JSONObject jsonData = new JSONObject(s);
                        if(!jsonData.isNull("error")){
                            String err = jsonData.getString("error");
                            displayError(err);
                        }
                        else {
                            displaData(jsonData);
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
    private  void displayAssetType(){
        /*
        spinnerAssetType.setOnItemSelectedListener(null);
        String[] mStringArray = new String[assetTypeList.size()];
        mStringArray = assetTypeList.toArray(mStringArray);
        setSpinnerData(spinnerAssetType,mStringArray);
        new DividendReq(eMessageCodeWealth.FAMILY_DATA.value).execute();*/
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
            //new DividendReq(eMessageCodeWealth.DIVIDENT_SUMMARY.value).execute();
            /*
            spinnerAssetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    GlobalClass.log("AssetType Selectd : " + position);
                    new DividendReq(eMessageCodeWealth.DIVIDENT_SUMMARY.value).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });*/
            spinnerFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    GlobalClass.log("FamilyType Selectd : " + position);
                    new DividendReq(eMessageCodeWealth.DIVIDENT_SUMMARY.value).execute();
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
            double dTot=0;

            ArrayList<JSONObject> jList = new ArrayList<>();
            GlobalClass.log("HoldingMF : " + jsonData.toString());
            JSONArray jsonArr = jsonData.getJSONArray("data");
            for(int i=0;i<jsonArr.length();i++){
                try {
                    JSONObject jsonD = jsonArr.getJSONObject(i);
                    String purP = jsonD.getString("OnlineFlag");
                    if(!purP.equalsIgnoreCase("")) {
                        dTot = dTot + Formatter.stringToDouble(jsonD.getString("Total"));
                        jList.add(jsonD);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if(mfReportAdapter != null){
                mfReportAdapter.reloadData(jList);
            }
            totgd.setText(Formatter.DecimalLessIncludingComma(dTot+""));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}