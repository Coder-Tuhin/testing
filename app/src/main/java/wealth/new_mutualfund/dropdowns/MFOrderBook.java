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
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import view.TvRegular;
import wealth.VenturaServerConnect;

import static utils.Formatter.toThreeDecimal;
import static utils.Formatter.toNoFracValue;

public class MFOrderBook extends Fragment implements View.OnClickListener {

    private HomeActivity homeActivity;
    private View mView;

    private MFOrderBookReportAdapter mfReportAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.asondate)
    TextView asondate;
    @BindView(R.id.amountbaltxt)
    TextView amountbaltxt;
    @BindView(R.id.notxt)
    TextView notxt;
    @BindView(R.id.amounttxt)
    TextView amounttxt;
    @BindView(R.id.start_date)
    TextView start_date;
    @BindView(R.id.end_date)
    TextView end_date;
    @BindView(R.id.mfSpinnerAssetType)
    Spinner spinnerAssetType;

    JSONObject jsonData;

    // ArrayList<String> assetTypeList;

    public static MFOrderBook newInstance() {
        return new MFOrderBook();
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
            mView = inflater.inflate(R.layout.mf_orderbook, container, false);
            ButterKnife.bind(this, mView);
            mfReportAdapter = new MFOrderBookReportAdapter();
            recyclerView.setAdapter(mfReportAdapter);
            setSpinnerData(spinnerAssetType, OrderBookTransactions.getList());
        }
        new OrderBookReq().execute();

        spinnerAssetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedStr = spinnerAssetType.getSelectedItem().toString();



                amounttxt.setText("Order No.");
                start_date.setText("Transaction date");
                end_date.setText("Transaction Type");
                notxt.setText("Amount");
                amountbaltxt.setText("Units");


                if(selectedStr.equalsIgnoreCase(OrderBookTransactions.SIP.name)){
                    amountbaltxt.setText("Exec./Rej./Bal");
                    amounttxt.setText("Amount");
                    notxt.setText("No.of SIP");
                    start_date.setText("Start date");
                    end_date.setText("End date");
                }
                else if(selectedStr.equalsIgnoreCase(OrderBookTransactions.REDEMPTION.name)){
                    amountbaltxt.setText("");
                    end_date.setText("Redemption date");
                }
                else if(selectedStr.equalsIgnoreCase(OrderBookTransactions.SWP.name)){
                    amountbaltxt.setText("Exec./Rej./Bal");
                    amounttxt.setText("Amount");
                    notxt.setText("No.of SWP");
                    start_date.setText("Start date");
                    end_date.setText("End date");
                }else if(selectedStr.equalsIgnoreCase(OrderBookTransactions.STP.name)){
                    amountbaltxt.setText("Exec./Rej./Bal");
                    amounttxt.setText("Amount");
                    notxt.setText("No.of STP");
                    start_date.setText("Start date");
                    end_date.setText("End date");
                }
                DataFilter(jsonData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return mView;
    }

    public void DataFilter(JSONObject jobj) {
        String selectedStr = spinnerAssetType.getSelectedItem().toString();
        displaData(jobj, selectedStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public class MFOrderBookReportAdapter extends RecyclerView.Adapter<MFOrderBookReportAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<MFOrderBookData> mList;

        MFOrderBookReportAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }

        public void reloadData(ArrayList<MFOrderBookData> value) {
            this.mList = value;
            this.notifyDataSetChanged();
        }

        @Override
        public MFOrderBookReportAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_orderbook, parent, false);
            MFOrderBookReportAdapter.MyViewHolder holder = new MFOrderBookReportAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MFOrderBookReportAdapter.MyViewHolder holder, final int position) {
            try {
                MFOrderBookData mfod = mList.get(position);
                holder.scheme_name.setText(mfod.getSchemeName());
                holder.transactiontype.setText(mfod.getTransactionType() + "");
                holder.status.setText(mfod.getStatus());
                holder.sdate.setText(mfod.getStartDate().equalsIgnoreCase("")?mfod.getTransactionDate():"");
                holder.edate.setText(mfod.getEndDate());
                holder.amount.setText(toNoFracValue(mfod.getAmount()));
                holder.units.setText(toThreeDecimal(mfod.getUnit()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            //return 5;
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.scheme_name)
            TvRegular scheme_name;
            @BindView(R.id.transactiontype)
            TvRegular transactiontype;
            @BindView(R.id.status)
            TvRegular status;
            @BindView(R.id.sdate)
            TvRegular sdate;
            @BindView(R.id.edate)
            TvRegular edate;
            @BindView(R.id.amount)
            TvRegular amount;
            @BindView(R.id.units)
            TvRegular units;

            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }

    class OrderBookReq extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject jsondata = new JSONObject();
            try {
                jsondata.put("clientcode", UserSession.getLoginDetailsModel().getUserID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.ORDER_BOOK.value, jsondata);
            if (jsonData != null) {
                GlobalClass.log(jsonData.toString());
                return jsonData.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if (s != null) {
                try {
                    jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        displayError(err);
                    }
                    else {
                        displaData(jsonData, OrderBookTransactions.PURCHASE.name);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    private void setSpinnerData(Spinner spinner, ArrayList<String> data) {
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<>(homeActivity, R.layout.custom_spinner_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data);
        spinner.setAdapter(categoryAdp);
    }
    private void displaData(JSONObject jsonData, String selectedFilter) {
        try {
            ArrayList<MFOrderBookData> jList = new ArrayList<>();
            JSONArray jsonArr = jsonData.getJSONArray("data");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jdata = jsonArr.getJSONObject(i);
                /*
                "TransType":"Bucket Save Tax - Lumpsum",
                "TransTypeId":"11",
                "SchemeName":"HDFC DYNAMIC DEBT FUND - REGULAR PLAN- GROWTH",
                "Status":"Rejected",
                "RegistrationDate":"16 Apr 2019 12:24",
                "Units":"0.000000",
                "Amount":"1000.00"*/
                MFOrderBookData mfod = new MFOrderBookData();
                String transType = jdata.optString("TransType");
                int transTypeID = jdata.optInt("TransTypeId");

                mfod.setTransactionType(transType);
                mfod.setTransTypeId(transTypeID);
                if (jdata.has("SchemeName")) {
                    mfod.setSchemeName(jdata.optString("SchemeName"));
                }
                if (jdata.has("Status")) {
                    mfod.setStatus(jdata.optString("Status"));
                }
                if (jdata.has("RegistrationDate")) {
                    mfod.setTransactionDate(jdata.optString("RegistrationDate"));
                }
                if (jdata.has("StartDate")) {
                    mfod.setStartDate(jdata.optString("StartDate"));
                }
                if (jdata.has("EndDate")) {
                    mfod.setEndDate(jdata.optString("EndDate"));
                }
                if (jdata.has("Units")) {
                    mfod.setUnit(jdata.optDouble("Units"));
                }
                if (jdata.has("Amount")) {
                    mfod.setAmount(jdata.optDouble("Amount"));
                }
                if(!mfod.getSchemeName().equalsIgnoreCase("") && mfod.isNeedToAdd(selectedFilter)){
                    jList.add(mfod);
                }
            }
            if (mfReportAdapter != null) {
                mfReportAdapter.reloadData(jList);
            }
        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }

    private class MFOrderBookData {
        private String transactionType = "";
        private String schemeName = "";
        private String status = "";
        private String startDate = "";
        private String endDate = "";
        private double unit = 0;
        private double amount = 0;
        private int tranTypeId = 0;
        private String transDate = "";

        public int getTransTypeId() {
            return tranTypeId;
        }

        public void setTransTypeId(int transactionType) {
            this.tranTypeId = transactionType;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }
        public String getTransactionDate() {
            return transDate;
        }

        public void setTransactionDate(String transactionDt) {
            this.transDate = transactionDt;
        }

        public String getSchemeName() {
            return schemeName;
        }

        public void setSchemeName(String schemeName) {
            this.schemeName = schemeName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public double getUnit() {
            return unit;
        }

        public void setUnit(double unit) {
            this.unit = unit;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public boolean isNeedToAdd(String selectedFilter){
            /*
         '1,11,14,15,16,17,18,19'  For  'Purchase' ,
                '2'  For 'Additional Purchase' ,
                '4,5,6,12,13'  for    'SIP' ,
                '3' For 'Redemption' ,
                '7' For 'Spread Order' ,
                '8' For 'Switch Order' ,
                '9' For 'SWP' ,
                '10'For  'STP'*/
            if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.PURCHASE.name)){
                if(tranTypeId == 1 || tranTypeId == 11 || tranTypeId == 14 || tranTypeId == 15 || tranTypeId == 16 ||
                        tranTypeId == 17 || tranTypeId == 18 || tranTypeId == 19){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.ADDITIONAL_PURCHASE.name)){
                if(tranTypeId == 2){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.SIP.name)){
                if(tranTypeId == 4 || tranTypeId == 5 || tranTypeId == 6 || tranTypeId == 12 || tranTypeId == 13){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.REDEMPTION.name)){
                if(tranTypeId == 3){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.SPREAD_ORDER.name)){
                if(tranTypeId == 7){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.SWITCH_ORDER.name)){
                if(tranTypeId == 8){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.SWP.name)){
                if(tranTypeId == 9){
                    return true;
                }
            } else if(selectedFilter.equalsIgnoreCase(OrderBookTransactions.STP.name)){
                if(tranTypeId == 10){
                    return true;
                }
            }
            return false;
        }
    }

    public enum OrderBookTransactions {
        //ALL("All"),
        PURCHASE("Purchase"),
        ADDITIONAL_PURCHASE("Additional Purchase"),
        SIP("SIP"),
        REDEMPTION("Redemption"),
        SPREAD_ORDER("Spread Order"),
        SWITCH_ORDER("Switch Order"),
        SWP("SWP"),
        STP("STP");

        public String name;

        OrderBookTransactions(String name) {
            this.name = name;
        }

        public static ArrayList<String> getList() {
            ArrayList<String> mList = new ArrayList<>();
            for (OrderBookTransactions b : OrderBookTransactions.values()) {
                mList.add(b.name);
            }
            return mList;
        }
    }

    public enum OrderBookStatus {
        ALL("All"),
        EXECUTED("Executed"),
        REJECTED("Rejected");
        public String name;

        OrderBookStatus(String name) {
            this.name = name;
        }

        public static ArrayList<String> getList() {
            ArrayList<String> mList = new ArrayList<>();
            for (OrderBookStatus b : OrderBookStatus.values()) {
                mList.add(b.name);
            }
            return mList;
        }
    }

}
