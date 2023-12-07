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
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import wealth.VenturaServerConnect;

/**
 * Created by XTREMSOFT on 13-Apr-2018.
 */

public class SaveTaxFragment extends Fragment {

    public static SaveTaxFragment newInstance(){
        return new SaveTaxFragment();
    }

    private HomeActivity homeActivity;
    private View mView;
    private SaveTaxAdapter mfAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.sipSwitch)
    Switch sipSwitch;
    @BindView(R.id.sipDayTv)
    TextView sipDayTv;

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
            mView = inflater.inflate(R.layout.savetax_new,container,false);
            ButterKnife.bind(this,mView);
            mfAdapter = new SaveTaxAdapter();
            recyclerView.setAdapter(mfAdapter);
            sipSwitch.setOnCheckedChangeListener(checkChange);
            new SaveTaxReq().execute();
        }
        return mView;
    }

    public class SaveTaxAdapter extends RecyclerView.Adapter<SaveTaxAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public SaveTaxAdapter() {

            inflater = LayoutInflater.from(homeActivity);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_savetax, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            int visibility = sipSwitch.isChecked()?View.VISIBLE:View.GONE;
            holder.sipDaySpinner.setVisibility(visibility);
            try {
                JSONObject jData = mList.get(position);
                holder.reloadData(jData);
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
            @BindView(R.id.sipDaySpinner)
            Spinner sipDaySpinner;

            @BindView(R.id.schemeName)
            TextView schemename;
            @BindView(R.id.nav)
            TextView nav;
            @BindView(R.id.amount)
            TextView amount;
            @BindView(R.id.alloc)
            TextView alloc;
            @BindView(R.id.oneyr)
            TextView oneyr;
            @BindView(R.id.threeyr)
            TextView threeyr;
            @BindView(R.id.fiveyr)
            TextView fiveyr;
            private JSONObject jData;
            public void  reloadData(JSONObject jsonD){
                this.jData = jsonD;

                try {
                    //String a = jData.optString("AUM");
                    schemename.setText(jData.getString("SchemeName"));
                    nav.setText(jData.getString("CurrNAV"));
                    amount.setText(jData.getString("INCRET"));
                    alloc.setText(jData.getString("YTDRET"));
                    oneyr.setText((jData.getString("RET1YEAR")));
                    threeyr.setText((jData.getString("RET3YEAR")));
                    fiveyr.setText(jData.getString("RET5YEAR"));
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener checkChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String switchText = b?"LUMPSUM":"SIP";
            int visibility = b?View.VISIBLE:View.GONE;
            sipSwitch.setText(switchText);
            sipDayTv.setVisibility(visibility);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    };

    class SaveTaxReq extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.BUCKET_INVESTMENT_DATA.value);
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
            //double dTotstd=0,dTotste=0,dTotltd=0,dTotlte=0;
            JSONArray jsonArr = jsonData.getJSONArray("data");
            ArrayList<JSONObject> jList = new ArrayList<>();
            GlobalClass.log("HoldingMF : " + jsonData.toString());

            for(int i=0;i<jsonArr.length();i++){
                try {
                    JSONObject jsonD = jsonArr.getJSONObject(i);
                    jList.add(jsonD);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if(mfAdapter != null){
                mfAdapter.reloadData(jList);
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}