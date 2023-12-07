package fragments.Trade;

import static android.content.ContentValues.TAG;

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
import com.ventura.venturawealth.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import enums.eSSOTag;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;

public class TradeDetails extends Fragment {

    String FromDate;
    String ToDate;
    String ScripCode;
    String Exchange = "All";
    String compName;
    TextView compNameTextView;
    TextView exchange_textview;
    TextView tradeDateRange;
    String TradeExchange ;
    ImageView tdBackBtn;
    LinearLayout tradeDetailsList;
    public TradeDetails(String FromDate, String ToDate, String ScripCode, String compName, String TradeExchange) {
        this.FromDate = FromDate;
        this.ToDate = ToDate;
        this.ScripCode = ScripCode;
        this.compName = compName;
        this.TradeExchange = TradeExchange;
    }

    public static TradeDetails newInstance(String FromDate, String ToDate, String ScripCode, String compName, String TradeExchange) {
        TradeDetails fragment = new TradeDetails(FromDate,ToDate,ScripCode,compName, TradeExchange);
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
        View view = inflater.inflate(R.layout.fragment_trade_details, container, false);

        compNameTextView = view.findViewById(R.id.compNameTextView);
        exchange_textview = view.findViewById(R.id.exchange_textview);
        tradeDateRange = view.findViewById(R.id.tradeDateRange);
        tdBackBtn = view.findViewById(R.id.tdBackBtn);
        tradeDetailsList = view.findViewById(R.id.tradeDetailsList);

        tdBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        compNameTextView.setText(compName);
        exchange_textview.setText(TradeExchange);
        tradeDateRange.setText("Trade date: "+displaydate2(FromDate) + " to " + displaydate2(ToDate));

        getTradeSumDetails();
        return view;
    }

    public void getTradeSumDetails() {
        GlobalClass.showProgressDialog("Please wait ...");

        try {
            String url = "https://settlements.ventura1.com/api/MvpApi/GetMVPTradeDetails";
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("ClientCode", UserSession.getLoginDetailsModel().getUserID());
            bodyJson.put("FromTrdDate", FromDate);
            bodyJson.put("ToTrdDate", ToDate);
            bodyJson.put("ScripCode", ScripCode);
            bodyJson.put("Exchange", Exchange);
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
//                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Error: " + new String(error.networkResponse.data), Toast.LENGTH_SHORT).show();
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


        } catch (Exception e) {
            Log.e("Internal error", e.toString());
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleJsonArrayResponse(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject object = response.getJSONObject(i);
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.trade_details_list, null);
                TextView bsTextView = listView.findViewById(R.id.bsTextView);
                TextView tradeQty = listView.findViewById(R.id.tradeQty);
                TextView TradeRate = listView.findViewById(R.id.TradeRate);
                TextView tradeDate = listView.findViewById(R.id.tradeDate);

                bsTextView.setText(object.getString("BS"));
                tradeQty.setText("Qty. "+object.getString("Qty"));
                TradeRate.setText("₹"+object.getString("Rate"));
                tradeDate.setText(displaydate(object.getString("TradeDate").substring(0,10)));

                tradeDetailsList.addView(listView);
            }
        }
        catch (Exception e){
            GlobalClass.log("Trade details json parse error: ", e.toString());
            e.printStackTrace();
            Toast.makeText(getContext(), "Internal error", Toast.LENGTH_SHORT).show();
        }
    }


    private String displaydate(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yy", Locale.US);
        try {
            Date date = inputFormat.parse(input);

            SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyy", Locale.US);
            assert date != null;

            String outputString = outputFormat.format(date);
            outputString = capitalizeFirstCharacterOfMonth(outputString);

//            outputString = outputString.toLowerCase();
            return outputString;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String displaydate2(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.US);
        try {
            Date date = inputFormat.parse(input);

            SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyy", Locale.US);
            assert date != null;

            String outputString = outputFormat.format(date);
            outputString = capitalizeFirstCharacterOfMonth(outputString);

//            outputString = outputString.toLowerCase();
            return outputString;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String capitalizeFirstCharacterOfMonth(String input) {
        if (input != null && input.length() >= 3) {
            String firstThreeChars = input.substring(0, 3);
            String capitalizedMonth = firstThreeChars.substring(0, 1).toUpperCase() + firstThreeChars.substring(1);

            return capitalizedMonth + input.substring(3);
        }
        return input;
    }
}