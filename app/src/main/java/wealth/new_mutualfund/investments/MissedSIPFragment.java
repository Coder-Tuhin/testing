package wealth.new_mutualfund.investments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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

import java.text.DecimalFormat;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.ButterKnife;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.DrawableSpan;
import wealth.new_mutualfund.newMF.OneTimeFragment;


public class MissedSIPFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private HomeActivity homeActivity;
    private LinearLayout noMissedSip_ll;
    private View mView;
    JSONObject jsonData;
    MissedsipAdapter adapter;
    double totalamount = 0;
    int tag = 0;
    JSONArray jsonArraynew = null;
    String OrderNo = "";

    public static MissedSIPFragment newInstance() {
        return new MissedSIPFragment();
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
        if (mView == null) {
            mView = inflater.inflate(R.layout.missedsipscreen, container, false);
            ButterKnife.bind(this, mView);
        }
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        noMissedSip_ll = (LinearLayout) mView.findViewById(R.id.noMissedSip_ll);
        recyclerView.setVisibility(View.INVISIBLE);
        noMissedSip_ll.setVisibility(View.GONE);
        adapter = new MissedsipAdapter();
        recyclerView.setAdapter(adapter);
        new OrderBookReq().execute();
        return mView;
    }

    @Override
    public void onClick(View view) {

    }

    public class MissedsipAdapter extends RecyclerView.Adapter<MissedsipAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private Context ctx;
        private JSONArray jsonArray = new JSONArray();

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.missedsip_listscreen, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


            try {
                JSONObject jobj = jsonArray.getJSONObject(position);
                holder.SchemeName.setText(jobj.optString("SchemeName"));
                String text = " " + jobj.optString("RejectedSIP") + " ";
                SpannableString spannable = new SpannableString(text);
                spannable.setSpan(new DrawableSpan(getContext().getResources().getDrawable(R.drawable.text_underline)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.MissedSip.setText(spannable, TextView.BufferType.SPANNABLE);
                holder.amount.setText(GlobalClass.getFormattedAmountString2(Double.parseDouble(jobj.optString("Amount"))));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject jsonObject = jsonArray.optJSONObject(position);
                        String OrderNo = jsonObject.optString("OrderNo");
                        //String OrderNo = jobj.optString("OrderNo");
                        tag = 1;
                        new MissedSipRequest(OrderNo, 1).execute();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView SchemeName, MissedSip, amount;

            public MyViewHolder(View itemView) {
                super(itemView);
                SchemeName = itemView.findViewById(R.id.SchemeName);
                MissedSip = itemView.findViewById(R.id.missedinstallment);
                amount = itemView.findViewById(R.id.amount);
            }
        }

        public void refreshAdapter(JSONArray jarr) {
            jsonArray = jarr;
            jsonArraynew = jsonArray;
            notifyDataSetChanged();
        }

        public void refreshAdapter2(JSONArray jarr1, String Orderno, JSONObject jsonData) {
            jsonArray = jarr1;
            try {
                JSONArray Jarr2 = jsonData.getJSONArray("data");
                JSONObject data = Jarr2.getJSONObject(0);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String Order = jsonObject.optString("OrderNo");
                    String SchemeName = jsonObject.optString("SchemeName");
                    String FolioNo = data.optString("FolioNo");
                    if (SchemeName.equalsIgnoreCase(data.optString("SchemeName"))) {
                        if (FolioNo.equalsIgnoreCase("")) {
                            jsonArray.remove(i);
                        }
                    }
                }
                if (jsonArray.length() == 0) {
                    noMissedSip_ll.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            notifyDataSetChanged();
        }
    }

    public void ShowPopUp(JSONObject jobj) {
        LinearLayout my_linear_layout, main_layout;
        TextView SchemeName;
        JSONArray jaar = null;
        JSONObject jsonObject = null;
        TextView selectallbtn;
        Button invest;
        ImageView closebtn;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getLayoutInflater();


        View customView = layoutInflater.inflate(R.layout.misssip_popup_dialog, null);
        // reference the textview of custom_popup_dialog
        my_linear_layout = customView.findViewById(R.id.LinearLayout);
        selectallbtn = customView.findViewById(R.id.selectallbtn);
        SchemeName = customView.findViewById(R.id.SchemeName);
        invest = customView.findViewById(R.id.button_buy);
        closebtn = customView.findViewById(R.id.closebtn);


        try {
            jaar = jobj.getJSONArray("data");
            for (int i = 0; i < jaar.length(); i++) {
                jsonObject = jaar.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SchemeName.setText(jsonObject.optString("SchemeName"));
        totalamount = 0;
        double amount1 = 0;

        if (jaar.length() > 0) {
            for (int j = 0; j < jaar.length(); j++) {
                JSONObject jobj1 = jaar.optJSONObject(j);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.missedsip_sublist, null);
                TextView installment_date = (TextView) view.findViewById(R.id.installment_date);
                TextView ammount = (TextView) view.findViewById(R.id.ammount);
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(j);
                String transAmount = jsonObject.optString("TransAmount");
                amount1 = Double.parseDouble(transAmount);
//                totalamount = totalamount+amount;
                DecimalFormat formatter = new DecimalFormat("#,###");
                installment_date.setText(jobj1.optString("TransDate"));
                ammount.setText(formatter.format(amount1) + "");
                my_linear_layout.addView(view);
            }
        }
        selectallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout;
                final int childcount = my_linear_layout.getChildCount();
                for (int k = 0; k < childcount; k++) {
                    View element = my_linear_layout.getChildAt(k);
                    CheckBox chkBOx = element.findViewById(R.id.checkbox);
                    chkBOx.setChecked(true);
                }
            }
        });
        JSONObject finalJsonObject = jsonObject;


        builder.setView(customView);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        double finalAmount = amount1;
        invest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int investcount = 0;
                final int childcount = my_linear_layout.getChildCount();
                TextView amount = null;
                for (int k = 0; k < childcount; k++) {
                    View element = my_linear_layout.getChildAt(k);
                    CheckBox chkBOx = element.findViewById(R.id.checkbox);
                    amount = element.findViewById(R.id.ammount);

                    if (chkBOx.isChecked()) {
                        investcount = investcount + 1;

                    }

                }
                if (investcount > 0) {
                    alertDialog.dismiss();
                    totalamount = finalAmount * investcount;
                    tag = 0;
                    NextScrren(finalJsonObject);
                } else {
                    GlobalClass.showAlertDialog("Please select atleast one Installment");
                }
            }
        });


    }


    class MissedSipRequest extends AsyncTask<String, Void, String> {
        String OrderNo = "";
        int jsonlength, currentpos, tag;

        MissedSipRequest(String OrderNo, int jsonlength, int currentpos, int tag) {
            this.OrderNo = OrderNo;
            this.jsonlength = jsonlength;
            this.currentpos = currentpos;
            this.tag = tag;
        }

        MissedSipRequest(String OrderNo, int tag) {
            this.OrderNo = OrderNo;
            this.tag = tag;
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
                jsondata.put("OrderNo", OrderNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.MISSED_SIP.value, jsondata);
            if (jsonData != null) {
                GlobalClass.log(jsonData.toString());
                return jsonData.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    String err = "";
                    jsonData = new JSONObject(s);
                    if (!jsonData.isNull("error")) {
                        err = jsonData.getString("error");

//                        displayError(err);
                        if (!err.equalsIgnoreCase("")) {
                            noMissedSip_ll.setVisibility(View.VISIBLE);

                        } else {
                            adapter.refreshAdapter2(jsonArraynew, OrderNo, jsonData);


                        }


                    } else {
                        if (tag == 1) {
                            ShowPopUp(jsonData);
                            tag = 0;

                        } else {
                            adapter.refreshAdapter2(jsonArraynew, OrderNo, jsonData);


                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (tag == 2) {
                    if (currentpos == jsonlength - 1) {
                        GlobalClass.dismissdialog();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    GlobalClass.dismissdialog();
                }

            }

        }
    }

    private void NextScrren(JSONObject jsonData) {

        String SchemeName = jsonData.optString("SchemeName");
        String SchemeCode = jsonData.optString("AccSchemeCode");
        String TransDate = jsonData.optString("TransDate");

        try {
            OneTimeFragment ls = OneTimeFragment.newInstance(SchemeCode, SchemeName,"", eOptionMF.TOPUP.name,"",String.valueOf(totalamount));

            //LumpSumFragment ls = LumpSumFragment.newInstance(SchemeCode, SchemeName,
              //      "", true, false, String.valueOf(totalamount), OrderNo, TransDate);
            homeActivity.FragmentTransaction(ls, R.id.container_body, true);
        } catch (Exception e) {
            e.printStackTrace();
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

            if (UserSession.getClientResponse().isNeedAccordLogin()) {
                ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                    VenturaServerConnect.closeSocket();
                    if (VenturaServerConnect.connectToWealthServer(true)) {
                        try {
                            JSONObject jsondata = new JSONObject();
                            jsondata.put("clientcode", UserSession.getLoginDetailsModel().getUserID());
                            jsondata.put("transactiontype", "4,5,6,12,13");
                            JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.ORDER_BOOK.value, jsondata);
                            if (jsonData != null) {
                                GlobalClass.log(jsonData.toString());
                                return jsonData.toString();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    return clientLoginResponse.charResMsg.getValue();
                }
            } else {
                if (VenturaServerConnect.connectToWealthServer(true)) {

                    try {
                        JSONObject jsondata = new JSONObject();
                        jsondata.put("clientcode", UserSession.getLoginDetailsModel().getUserID());
                        jsondata.put("transactiontype", "4,5,6,12,13");
                        JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.ORDER_BOOK.value, jsondata);
                        if (jsonData != null) {
                            //GlobalClass.log(jsonData.toString());
                            return jsonData.toString();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                    jsonData = new JSONObject(s);
                    if (!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
//                        displayError(err);
                    } else {
                        displaydata(jsonData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!s.equalsIgnoreCase(""))
                        GlobalClass.showAlertDialog(s);
                }
            }
        }
    }

    private void displaydata(JSONObject jsonData) {
        JSONArray finalJson = new JSONArray();
        try {
            JSONArray jsonArray1 = jsonData.getJSONArray("data");
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jobj = jsonArray1.getJSONObject(i);
                int Rejectedsip = jobj.optInt("RejectedSIP");
                if (Rejectedsip > 0) {
                    finalJson.put(jobj);
                }
            }
            jsonArraynew = finalJson;
            if (jsonArraynew.length() > 0) {
                for (int j = 0; j < finalJson.length(); j++) {
                    JSONObject jobj = finalJson.optJSONObject(j);
                    String OrderNo = jobj.optString("OrderNo");
                    this.OrderNo = OrderNo;
                    new MissedSipRequest(OrderNo, finalJson.length(), j, 2).execute();
                }
            } else {
                noMissedSip_ll.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}