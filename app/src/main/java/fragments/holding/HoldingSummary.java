package fragments.holding;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ventura.venturawealth.R;

import org.json.JSONArray;
import org.json.JSONObject;

import enums.eSSOTag;
import fragments.Trade.TradeDetails;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;

public class HoldingSummary extends Fragment {

    LinearLayout holdingsList;

    public HoldingSummary() {
        // Required empty public constructor
    }

    public static HoldingSummary newInstance() {
        HoldingSummary fragment = new HoldingSummary();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_holding_summary, container, false);

        holdingsList = view.findViewById(R.id.holdingsList);

        getHoldingSummary();
        return view;
    }

    public void getHoldingSummary(){
        GlobalClass.showProgressDialog("Please wait ...");
        try {
            String url = "https://settlements.ventura1.com/api/MvpApi/GetMVPHoldingScripwise";
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("Clientcode", UserSession.getLoginDetailsModel().getUserID());
            bodyJson.put("UserKey", "15be76482ccf443b41cb3ca66cb025b3e7048ac6");
            bodyJson.put("Session_id", PreferenceHandler.getSSOSessionID());
            bodyJson.put("Xapikey", eSSOTag.xapikey.value);
            bodyJson.put("Authorization", "Bearer " + PreferenceHandler.getSSOAuthToken());
            bodyJson.put("SSOVerString", "S3");

            String body = bodyJson.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, body,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Handle the response
                            handleJsonObjectResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors
                            GlobalClass.dismissdialog();
                            Log.e("VolleyError", error.toString());
                            Toast.makeText(getContext(), "Error: "+new String(error.networkResponse.data), Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            queue.add(jsonObjectRequest);


        }catch (Exception e){
            Log.e("Internal error", e.toString());
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleJsonObjectResponse(JSONObject response) {
        try {
            JSONArray dataArr = response.getJSONArray("data");

            for(int i =0;i<dataArr.length();i++){
                JSONObject object = dataArr.getJSONObject(i);
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.holding_summary_listview, null);
                TextView compName = listView.findViewById(R.id.compName);
                TextView currVal = listView.findViewById(R.id.currVal);
                TextView unrPL = listView.findViewById(R.id.unrPL);
                TextView pnlPer = listView.findViewById(R.id.pnlPer);
                TextView shortQty = listView.findViewById(R.id.shortQty);
                TextView ltQty = listView.findViewById(R.id.ltQty);
                TextView exTran = listView.findViewById(R.id.exTran);
                TextView holdingType = listView.findViewById(R.id.holdingType);
                TextView marPledge = listView.findViewById(R.id.marPledge);
                TextView fsPledge = listView.findViewById(R.id.fsPledge);
                TextView locQty = listView.findViewById(R.id.locQty);
                TextView plQty = listView.findViewById(R.id.plQty);
                TextView dpHolding = listView.findViewById(R.id.dpHolding);
                TextView freeQty = listView.findViewById(R.id.freeQty);
                TextView purRate = listView.findViewById(R.id.purRate);
                TextView purQty = listView.findViewById(R.id.purQty);
                TextView purAmt = listView.findViewById(R.id.purAmt);

                compName.setText(object.getString("NseSymbol"));
                currVal.setText("Current value: ₹"+object.getString("CurrentValue"));
                unrPL.setText("Unrealised PL: ₹"+object.getString("UnrealisedPL"));
                pnlPer.setText(object.getString("Pnl_Per"));
                shortQty.setText(object.getString("ShortTermQty"));
                ltQty.setText(object.getString("LongTermQty"));
                exTran.setText(object.getString("ExternallyTransferred"));
                holdingType.setText(object.getString("HoldingType"));
                marPledge.setText(object.getString("MarginPledge"));
                fsPledge.setText(object.getString("FSPledge"));
                locQty.setText(object.getString("LockedQty"));
                plQty.setText(object.getString("PledgeQty"));
                dpHolding.setText(object.getString("DpHolding"));
                freeQty.setText(object.getString("FreeQty"));
                purRate.setText("₹"+object.getString("PurRate"));
                purQty.setText(object.getString("Purqty"));
                purAmt.setText("₹"+object.getString("PurAmount"));

                String ISINNo = object.getString("ISINNo");

                listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment m_fragment = Holdingdetails.newInstance(compName.getText().toString(),ISINNo);
                        new Handler().postDelayed(() ->
                                        getParentFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.openFrag, m_fragment)
                                                .addToBackStack(null)
                                                .commit(),
                                250);
                    }
                });

                holdingsList.addView(listView);
            }

            GlobalClass.dismissdialog();

        }catch (Exception e){
            Log.e(TAG, "handleJsonObjectResponse: "+e );
            Toast.makeText(getContext(), "Internal error", Toast.LENGTH_SHORT).show();
            GlobalClass.dismissdialog();
        }
    }
}