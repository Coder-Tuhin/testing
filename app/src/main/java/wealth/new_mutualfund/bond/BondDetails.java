package wealth.new_mutualfund.bond;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class BondDetails extends Fragment implements View.OnClickListener {

    private HomeActivity homeActivity;
    private View mView;

    @BindView(R.id.noissue_text)
    TextView noissue_text;

    @BindView(R.id.textvalue)
    LinearLayout textvalue;

    @BindView(R.id.linear_container)
    LinearLayout linear_container;
    public static String tenor = "",interestfreqs = "", interestrates = "",noofbondss = "",totalamounts= "",OPtID = "";
    public String ExchangeT = "";

    BondDetailsAdapter adapter;
    public static String BondID = "",BondName = "";
    public static String BondQty = "",GrandTotal = "0",UPICode = "",
            DPId = "",BeneficiaryId = "",AppNoFrom = "",AppNoTo = "",PanNumber = "",ClientCode = "";

    public static BondDetails newInstance(String bondName,String bondID,String Beneficiaryid,String dpid,String appnofrom,String appnoto,String upicode,String PanNumr,String ClientCde) {
        BondDetails bd = new BondDetails();
        BondName = bondName;
        BondID = bondID;
        BeneficiaryId = Beneficiaryid;
        DPId = dpid;
        UPICode = upicode;
        AppNoFrom = appnofrom;
        AppNoTo = appnoto;
        PanNumber = PanNumr;
        ClientCode = ClientCde;
        return bd;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

        if (mView == null) {
            mView = inflater.inflate(R.layout.bonddetails, container, false);
            ButterKnife.bind(this, mView);
            new BondIssueDetailsReq().execute();
        }


        return mView;
    }

    @Override
    public void onClick(View view) {

    }
    int row_index = -1;
    int rowtag  = 1;

    public class BondDetailsAdapter extends RecyclerView.Adapter<BondDetailsAdapter.MyViewHolder>{
        private LayoutInflater inflater;
        private Context ctx;
        private JSONArray data;
        private JSONObject jobj;
        String Exchange = "";

        BondDetailsAdapter(JSONArray data,String Exchange){
            this.data = data;
            this.notifyDataSetChanged();
            this.Exchange = Exchange;
        }

        @NonNull
        @Override
        public BondDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.bonddetails_list, parent, false);
            BondDetailsAdapter.MyViewHolder viewHolder = new BondDetailsAdapter.MyViewHolder(listItem);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull BondDetailsAdapter.MyViewHolder holder, int position) {
            try {
                JSONObject jsonObject = data.getJSONObject(position);
                holder.setValue(jsonObject,position,holder);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.length();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView totalamount,interestrate,interestfreq,tenorText;
            private LinearLayout totalLayout;
            private CheckBox checkboxlist;


            public MyViewHolder(View itemView) {
                super(itemView);
                totalamount = itemView.findViewById(R.id.totalamount);
                interestrate = itemView.findViewById(R.id.interestrate);
                interestfreq = itemView.findViewById(R.id.interestfreq);
                tenorText = itemView.findViewById(R.id.tenor);
                totalLayout = itemView.findViewById(R.id.totalLayout);
                checkboxlist = itemView.findViewById(R.id.checkboxlist);

            }
            public void setValue(JSONObject jsonObject,int position,MyViewHolder holder){
//                totaltext.setVisibility(View.VISIBLE);
                try {
                    JSONArray raw = jsonObject.getJSONArray("ColumnFieldValues");
                    String issueprice = jsonObject.optString("IssuePrice");
                    LinkedList<String> rawList = new LinkedList<>();
                    for (int i = 0; i <raw.length() ; i++) {
                        String j = raw.getString(i);
                        rawList.add(j);
                    }
                    double issuepricetemp = Double.parseDouble(issueprice);
                    tenorText.setText(rawList.get(2));
                    interestfreq.setText(rawList.get(4));
                    interestrate.setText(rawList.get(3));


                    totalLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            row_index=position;
                            notifyDataSetChanged();
                            int color = getResources().getColor(R.color.ventura_color);
                            Drawable background = view.getBackground();
                            if (background instanceof ColorDrawable)
                                color = ((ColorDrawable) background).getColor();

                            if(color != getResources().getColor(R.color.ventura_color)){
                                totalLayout.setBackgroundColor(getResources().getColor(R.color.ventura_color));
                                rowtag = 0;
                                checkboxlist.setChecked(true);
                            }else {
                                totalLayout.setBackgroundColor(getResources().getColor(R.color.black));

//                                totaltext.setVisibility(View.INVISIBLE);
                                rowtag = 1;
                                checkboxlist.setChecked(false);


                            }
                            tenor = tenorText.getText().toString();
                            interestfreqs = interestfreq.getText().toString();
                            interestrates = interestrate.getText().toString();
                            totalamounts = issueprice;
                            noofbondss = rawList.get(5);
                            OPtID = jsonObject.optString("OptId");
                            AppNoFrom = jsonObject.optString("AppNoFrom");
                            AppNoTo = jsonObject.optString("AppNoTo");
                            ExchangeT = Exchange;

                        }
                    });
                    if(row_index == position){

                        if(rowtag == 1){
                            totalLayout.setBackgroundColor(getResources().getColor(R.color.black));
                            checkboxlist.setChecked(false);
                        }else {
                            totalLayout.setBackgroundColor(getResources().getColor(R.color.ventura_color));
                            checkboxlist.setChecked(true);
                        }
                    }else {
                        totalLayout.setBackgroundColor(getResources().getColor(R.color.black));
                        checkboxlist.setChecked(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        public void refreshAdapter( JSONArray jarr) {

        }
    }

    class  BondFormDetailsReq extends AsyncTask<String, Void, String> {
        String BondID = "";
        String closeDate = "",Rating = "",BondName = "",AppNoFrom = "",AppNoTo = "";
        String Exchange = "";



        BondFormDetailsReq(String BondID,String CloseDate,String Rating,String BondName,String AppNoFrom,String AppNoTo,String exchange){
            this.BondID = BondID;
            this.closeDate = CloseDate;
            this.Rating = Rating;
            this.BondName = BondName;
            this.AppNoFrom = AppNoFrom;
            this.AppNoTo = AppNoTo;
            this.Exchange = exchange;
        }

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
                jsondata.put("BondId",BondID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.BOND_FORMDETAILS.value, jsondata);
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

                JSONArray data = new JSONArray();
                View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.bonddetails_listnew,linear_container,false);
                LinearLayout alreadyappliedlayout = layout2.findViewById(R.id.alreadyappliedlayout);
                LinearLayout totallayout = layout2.findViewById(R.id.totallayout);
                TextView msg = layout2.findViewById(R.id.msg);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String error = jsonObject.optString("error");
                    if(!error.equalsIgnoreCase("")){
                        alreadyappliedlayout.setVisibility(View.VISIBLE);
                        totallayout.setVisibility(View.GONE);
                        TextView bondNameapplied = layout2.findViewById(R.id.bondNameapplied);
                        bondNameapplied.setText(BondName);
                        msg.setText(error);
                        linear_container.addView(layout2);
                    }else {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject innerjson = jsonArray.getJSONObject(i);
                            innerjson.put("AppNoFrom",AppNoFrom);
                            innerjson.put("AppNoTo",AppNoTo);
                            data.put(innerjson);
                        }
                        alreadyappliedlayout.setVisibility(View.GONE);

                        RecyclerView recycler_bonddetails = layout2.findViewById(R.id.recycler_bonddetails);
                        TextView bondName = layout2.findViewById(R.id.bondName);
                        TextView creditRating = layout2.findViewById(R.id.creditRating);
                        TextView closedate = layout2.findViewById(R.id.closedate);
                        Button button_buy = layout2.findViewById(R.id.button_buy);
                        button_buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(rowtag == 1){
                                    GlobalClass.showAlertDialog("Please select a Bond option from the list");
                                }else {
                                    homeActivity.FragmentTransaction(BondSubmitFragment.newInstance(BondName, BondID, AppNoFrom, AppNoTo, tenor, interestfreqs, interestrates, noofbondss, totalamounts, OPtID,ExchangeT), R.id.container_body, true);
                                }

                            }
                        });

                        bondName.setText(BondName);
                        creditRating.setText("Credit Rating : " + Rating);
                        closedate.setText("Close Date : " + closeDate);

                        adapter = new BondDetailsAdapter(data,Exchange);
                        recycler_bonddetails.setAdapter(adapter);
                        linear_container.addView(layout2);
                        textvalue.setVisibility(View.VISIBLE);

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }



    private void eNACHConfirmation(){

        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setTitle("Scheme Names");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.nache_confirmation_alert_layout);
            TextView ok_button = dialog.findViewById(R.id.ok_button);

            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CreateBondList(JSONArray jarry){
        for (int i = 0;i < jarry.length() ; i++) {
            JSONObject jsonObject = jarry.optJSONObject(i);
            AppNoFrom = jsonObject.optString("AppNoFrom");
            AppNoTo = jsonObject.optString("AppNoTo");
            BondID = jsonObject.optString("BondId");
            String CloseDate = jsonObject.optString("CloseDate");
            String Rating = jsonObject.optString("Rating");
            BondName = jsonObject.optString("BondName");
            String Exchange = jsonObject.optString("Exchange");

            new BondFormDetailsReq(BondID,CloseDate,Rating,BondName,AppNoFrom,AppNoTo,Exchange).execute();
        }
    }

    class  BondIssueDetailsReq extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {
            if(UserSession.getClientResponse().isNeedAccordLogin()) {
                ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                    VenturaServerConnect.closeSocket();
                } else {
                    return clientLoginResponse.charResMsg.getValue();
                }
            }
            if(VenturaServerConnect.connectToWealthServer(true)) {


                JSONObject jsondata = new JSONObject();
                try {
                    jsondata.put("clientcode", UserSession.getLoginDetailsModel().getUserID());
                    jsondata.put("BondId", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.BOND_ISSUEDETAILS.value, jsondata);
                if (jsonData != null) {
                    GlobalClass.log(jsonData.toString());
                    return jsonData.toString();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if (s != null) {
                try {
                    JSONObject data = new JSONObject(s);
                    String error = data.optString("error");
                    if(error.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                        GlobalClass.showAlertDialog(error,true);
                        return;
                    }
                    if(error.equalsIgnoreCase("details does not exists")){
                        noissue_text.setVisibility(View.VISIBLE);
//                        NoBond_layout.setVisibility(View.VISIBLE);
                    }
                    JSONArray jsonArray = data.getJSONArray("data");
                    CreateBondList(jsonArray);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

}
