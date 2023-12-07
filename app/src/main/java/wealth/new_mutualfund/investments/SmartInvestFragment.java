package wealth.new_mutualfund.investments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 16-Apr-2018.
 */

public class SmartInvestFragment extends Fragment {

    public static SmartInvestFragment newInstance() {
        return new SmartInvestFragment();
    }

    private HomeActivity homeActivity;
    private View mView;
    private SaveTaxAdapter mfadapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
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
            mView = inflater.inflate(R.layout.smartinvest_screen,container,false);
            ButterKnife.bind(this,mView);
            mfadapter = new SaveTaxAdapter();
            recyclerView.setAdapter(mfadapter);
            new SmartInvestReq().execute();
        }
        return mView;
    }

    public class SaveTaxAdapter extends RecyclerView.Adapter<SaveTaxAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public SaveTaxAdapter() {

            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_smartinvest, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

        }

        @Override
        public int getItemCount() {
             return mList.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.actions)
            LinearLayout actions;
            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

            }
        }
    }

    class SmartInvestReq extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject jsonData = null;
                    //VenturaServerConnect.sendReqDataMFReport(eMessageCode.SMARTINVEST_DATA.value);
            if(jsonData != null){
                return jsonData.toString();
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
                    displaData(jsonData);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        }
    }

    private void displaData(JSONObject jsonData) {

        try {
            GlobalClass.log("Dashboard : " + jsonData.toString());
            /*
            JSONArray dataarr = jsonData.getJSONArray("data");
            JSONArray assetdataarr = jsonData.getJSONArray("AssetAllocationData");

            double intV=0,currV = 0, gainLossV =0, xirrV = 0;
            for(int i=0;i<dataarr.length();i++){

                JSONObject jdata = dataarr.getJSONObject(i);
                intV = intV + jdata.getDouble("Cost");
                currV = currV + jdata.getDouble("CurrentValue");
                try {
                    gainLossV = gainLossV + jdata.getDouble("GainLoss");
                }
                catch (Exception ex){}
                xirrV = xirrV + jdata.getDouble("XIRR");
            }
            investmentval.setText(Formatter.mfFormatter.format(intV) + "");
            currentval.setText(Formatter.mfFormatter.format(currV) + "");
            gainLossval.setText(Formatter.mfFormatter.format(gainLossV) + "");
            xirrval.setText(Formatter.clk_formatter.format(xirrV)+"");

            double equityP = 0, debtP=0,otherP = 0;
            for (int i=0;i<assetdataarr.length();i++){

                JSONObject jdata = assetdataarr.getJSONObject(i);
                if(jdata.getString("AssetType").equalsIgnoreCase(eMFAssetType.EQUITY.name)){
                    equityP = jdata.getDouble("Allocation");
                }
                else if(jdata.getString("AssetType").equalsIgnoreCase(eMFAssetType.DEBT.name)){
                    debtP = jdata.getDouble("Allocation");
                }
                else{
                    otherP = otherP + jdata.getDouble("Allocation");
                }
            }
            mfDashboardProgress.setProgress((int) equityP);
            mfDashboardProgress.setSecondaryProgress((int) (equityP + debtP));

            equityper.setText(Formatter.clk_formatter.format(equityP)+ "%");
            debtper.setText(Formatter.clk_formatter.format(debtP)+"%");
            otherper.setText(Formatter.clk_formatter.format(otherP)+"%");
            */
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

}
