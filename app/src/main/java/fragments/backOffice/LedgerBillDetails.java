package fragments.backOffice;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.ventura.venturawealth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import enums.eSSOTag;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;

public class LedgerBillDetails extends Fragment {

    String billNo;
    LinearLayout billDetails;
    ImageView backBtn;

    public LedgerBillDetails(String billNo) {
        // Required empty public constructor
        this.billNo = billNo;
    }

    public static LedgerBillDetails newInstance(String billNo) {
        LedgerBillDetails fragment = new LedgerBillDetails(billNo);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ledger_bill_details, container, false);
        billDetails = view.findViewById(R.id.billDetails);
        backBtn = view.findViewById(R.id.lbbackBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        getBilldetails();

        return view;
    }

    public void getBilldetails(){
        GlobalClass.showProgressDialog("Please wait ...");
        try {
            String url = "https://settlements.ventura1.com/api/MvpApi/GetMVPBillDetails";
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("sbillno", billNo);
            bodyJson.put("sparty", UserSession.getLoginDetailsModel().getUserID());
            bodyJson.put("UserKey", "15be76482ccf443b41cb3ca66cb025b3e7048ac6");
            bodyJson.put("Session_id", PreferenceHandler.getSSOSessionID());
            bodyJson.put("Xapikey", eSSOTag.xapikey.value);
            bodyJson.put("Authorization", "Bearer " + PreferenceHandler.getSSOAuthToken());
            bodyJson.put("SSOVerString", "S3");

            String body = bodyJson.toString();

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, body,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Handle the response
                            GlobalClass.dismissdialog();
                            handleJsonArrayResponse(response);
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

            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            queue.add(jsonArrayRequest);


        }catch (Exception e){
            Log.e("Internal error", e.toString());
            e.printStackTrace();
        }
    }



    @SuppressLint("SetTextI18n")
    private void handleJsonArrayResponse(JSONArray response) {
        try {
            for (int i =0;i<response.length();i++){
                JSONObject object = response.getJSONObject(i);
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.ledger_billdetails, null);
                TextView compName = listView.findViewById(R.id.compName);
                TextView billdate = listView.findViewById(R.id.billdate);
                TextView buyQty = listView.findViewById(R.id.buyQty);
                TextView sellQty = listView.findViewById(R.id.sellQty);
                TextView netQty = listView.findViewById(R.id.netQty);
                TextView mktType = listView.findViewById(R.id.mktType);
                TextView netRate = listView.findViewById(R.id.netRate);
                TextView billDebit = listView.findViewById(R.id.billDebit);
                TextView billCredit = listView.findViewById(R.id.billCredit);
                TextView taxCrg = listView.findViewById(R.id.taxCrg);
                TextView billBkgs = listView.findViewById(R.id.billBkgs);
                TextView mktRate = listView.findViewById(R.id.mktRate);
                TextView billex = listView.findViewById(R.id.billex);
                TextView billBS = listView.findViewById(R.id.billBS);
                TextView billSetNo = listView.findViewById(R.id.billSetNo);
                TextView billCF = listView.findViewById(R.id.billCF);

                compName.setText(object.getString("ScripName"));
                billdate.setText(getFormatedDate(object.getString("TradeDate")));

                buyQty.setText(object.getString("BuyQty"));
                sellQty.setText(object.getString("SellQty"));
                netQty.setText(object.getString("NetQty"));
                mktType.setText(object.getString("MktType"));
                netRate.setText(object.getString("NetRate"));
                billDebit.setText(object.getString("Debit"));
                billCredit.setText(object.getString("Credit"));
                taxCrg.setText(object.getString("Tax_Charges"));
                billBkgs.setText(object.getString("Bkgs"));
                mktRate.setText(object.getString("MktRate"));
                billex.setText(object.getString("Exchange"));
                billBS.setText(object.getString("BS"));
                billSetNo.setText(object.getString("SetNo"));
                billCF.setText(object.getString("CfBf"));

                billDetails.addView(listView);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFormatedDate(String dateTimeString){
        try {
            Date parsedDate = parseDateString(dateTimeString);
            return formatDateWithOrdinal(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String formatDateWithOrdinal(Date date) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");

        int day = Integer.parseInt(dayFormat.format(date));
        String dayWithOrdinal = day + getDayOfMonthSuffix(day);

        String month = monthFormat.format(date);

        return dayWithOrdinal + " " + month;
    }

    private static String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    private static Date parseDateString(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.parse(dateString);
    }
}