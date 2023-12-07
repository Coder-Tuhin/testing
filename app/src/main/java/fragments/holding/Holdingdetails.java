package fragments.holding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

public class Holdingdetails extends Fragment {

    String compName;
    String isinNumber;
    TextView compNameTextView;
    ImageView hdBackBtn;
    ImageView lDropdown;
    ImageView sDropdown;
    RelativeLayout sortTermTitle;
    RelativeLayout longTermTitle;
    LinearLayout shortTermList;
    LinearLayout longTermList;
    boolean isShowingLongTermList = true;
    boolean isShowingSortTermList = true;

    public Holdingdetails(String compName, String isinNumber) {
        this.compName = compName;
        this.isinNumber = isinNumber;
    }

    public static Holdingdetails newInstance(String compName, String isinNumber) {
        Holdingdetails fragment = new Holdingdetails(compName, isinNumber);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_holdingdetails, container, false);
        compNameTextView = view.findViewById(R.id.compNameTextView);
        hdBackBtn = view.findViewById(R.id.hdBackBtn);
        sortTermTitle = view.findViewById(R.id.sortTermTitle);
        longTermTitle = view.findViewById(R.id.longTermTitle);
        shortTermList = view.findViewById(R.id.shortTermList);
        longTermList = view.findViewById(R.id.longTermList);
        lDropdown = view.findViewById(R.id.lDropdown);
        sDropdown = view.findViewById(R.id.sDropdown);

        compNameTextView.setText(compName);

        hdBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        sortTermTitle.setOnClickListener(v -> {
            if(isShowingSortTermList){
                shortTermList.setVisibility(View.GONE);
                sDropdown.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }else{
                shortTermList.setVisibility(View.VISIBLE);
                sDropdown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
            isShowingSortTermList = !isShowingSortTermList;
        });

        longTermTitle.setOnClickListener(v -> {
            if(isShowingLongTermList){
                longTermList.setVisibility(View.GONE);
                lDropdown.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
            }else{
                longTermList.setVisibility(View.VISIBLE);
                lDropdown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
            isShowingLongTermList = !isShowingLongTermList;
        });

        getHoldingDetails();

        return view;
    }

    public void getHoldingDetails(){
        GlobalClass.showProgressDialog("Please wait ...");
        try {
            String url = "https://settlements.ventura1.com/api/MvpApi/GetMVPHoldingTradeDetails";
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("Clientcode", UserSession.getLoginDetailsModel().getUserID());
            bodyJson.put("isin", isinNumber);
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
                            GlobalClass.dismissdialog();
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

    private void handleJsonObjectResponse(JSONObject response) {
        try {
            JSONArray sortTermJsonArray = response.getJSONObject("Data").getJSONArray("Short_Term");
            JSONArray longTermJsonArray = response.getJSONObject("Data").getJSONArray("Long_Term");

            if(sortTermJsonArray.length()<1){
                sortTermTitle.setVisibility(View.GONE);
            }

            if(longTermJsonArray.length()<1){
                longTermTitle.setVisibility(View.GONE);
            }

            for(int i = 0;i<sortTermJsonArray.length();i++){
                JSONObject object = sortTermJsonArray.getJSONObject(i);
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.holding_details_list, null);
                TextView buyDate = listView.findViewById(R.id.buyDate);
                TextView sellDate = listView.findViewById(R.id.sellDate);
                TextView buyQty = listView.findViewById(R.id.buyQty);
                TextView sellQty = listView.findViewById(R.id.sellQty);
                TextView buyRate = listView.findViewById(R.id.buyRate);
                TextView sellRate = listView.findViewById(R.id.sellRate);
                TextView buyValue = listView.findViewById(R.id.buyValue);
                TextView sellValue = listView.findViewById(R.id.sellValue);

                buyDate.setText(displaydate(object.getString("BuyDate").substring(0,10)));
                sellDate.setText(displaydate(object.getString("SellDate").substring(0,10)));
                buyQty.setText(object.getString("BuyQty"));
                sellQty.setText(object.getString("SellQty"));
                buyRate.setText(object.getString("BuyRate"));
                sellRate.setText(object.getString("SellRate"));
                buyValue.setText(object.getString("BuyValue"));
                sellValue.setText(object.getString("SellValue"));

                shortTermList.addView(listView);
            }

            for(int i = 0;i<longTermJsonArray.length();i++){
                JSONObject object = longTermJsonArray.getJSONObject(i);
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.holding_details_list, null);
                TextView buyDate = listView.findViewById(R.id.buyDate);
                TextView sellDate = listView.findViewById(R.id.sellDate);
                TextView buyQty = listView.findViewById(R.id.buyQty);
                TextView sellQty = listView.findViewById(R.id.sellQty);
                TextView buyRate = listView.findViewById(R.id.buyRate);
                TextView sellRate = listView.findViewById(R.id.sellRate);
                TextView buyValue = listView.findViewById(R.id.buyValue);
                TextView sellValue = listView.findViewById(R.id.sellValue);

                buyDate.setText(displaydate(object.getString("BuyDate").substring(0,10)));
                sellDate.setText(displaydate(object.getString("SellDate").substring(0,10)));
                buyQty.setText(object.getString("BuyQty"));
                sellQty.setText(object.getString("SellQty"));
                buyRate.setText(object.getString("BuyRate"));
                sellRate.setText(object.getString("SellRate"));
                buyValue.setText(object.getString("BuyValue"));
                sellValue.setText(object.getString("SellValue"));

                longTermList.addView(listView);
            }


        }catch (Exception e){
            e.printStackTrace();
            GlobalClass.log("Holding details parsing error: "+ e.toString());
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

    private String capitalizeFirstCharacterOfMonth(String input) {
        if (input != null && input.length() >= 3) {
            String firstThreeChars = input.substring(0, 3);
            String capitalizedMonth = firstThreeChars.substring(0, 1).toUpperCase() + firstThreeChars.substring(1);

            return capitalizedMonth + input.substring(3);
        }
        return input;
    }
}