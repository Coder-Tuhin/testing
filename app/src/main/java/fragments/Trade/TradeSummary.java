package fragments.Trade;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.ventura.venturawealth.temp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import enums.eSSOTag;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;

public class TradeSummary extends Fragment {

    TextView fromDate;
    TextView toDate;
    RelativeLayout toDateLay;
    RelativeLayout fromDateLay;
    Spinner tradeSpinner;
    String TR_SEGMENT = "All";
    final Calendar c = Calendar.getInstance();
    int yearTo, monthTo, dayTo;
    int yearFrom, monthFrom, dateFrom;

    LinearLayout tradeSumList;
    TextView totalBuyValue;
    TextView totalSellValue;

    String formattedToDate;
    String formattedFromDate;

    public TradeSummary() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trade_summary, container, false);
        fromDate = view.findViewById(R.id.fromDate);
        toDate = view.findViewById(R.id.toDate);
        toDateLay = view.findViewById(R.id.toDateLay);
        fromDateLay = view.findViewById(R.id.fromDateLay);
//        tradeSpinner = view.findViewById(R.id.tradeSpinner);
        tradeSumList = view.findViewById(R.id.tradeSumList);
        totalBuyValue = view.findViewById(R.id.total_buy_value);
        totalSellValue = view.findViewById(R.id.total_sell_value);

        yearTo = c.get(Calendar.YEAR);
        monthTo = c.get(Calendar.MONTH);
        dayTo = c.get(Calendar.DAY_OF_MONTH);

        dateFrom = dayTo;
        monthFrom = monthTo;
        yearFrom = yearTo;

        if (dayTo < 6) {
            if (monthTo < 1) {
                monthFrom = 11;
                dateFrom = 1;
                yearFrom -= 1;
            } else {
                monthFrom -= 1;
                dateFrom = 1;
            }
        } else {
            dateFrom = 1;
        }

        fromDate.setText(displaydate(dateFrom + "-" + (monthFrom + 1) + "-" + yearFrom));
        toDate.setText(displaydate(dayTo + "-" + (monthTo + 1) + "-" + yearTo));
        formattedFromDate = getFormatedDate(dateFrom + "-" + (monthFrom + 1) + "-" + yearFrom);
        formattedToDate = getFormatedDate(dayTo + "-" + (monthTo + 1) + "-" + yearTo);

        implementDatePicker();
//        implementTradeSpinner();
        getTradeSumDetails();


        totalBuyValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), temp.class));
            }
        });

        return view;
    }

    public void getTradeSumDetails() {
        GlobalClass.showProgressDialog("Please wait ...");

        try {
            String url = "https://settlements.ventura1.com/api/MvpApi/GetMVPTradeSummary";
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("Clientcode", UserSession.getLoginDetailsModel().getUserID());
            bodyJson.put("FromTrdDate", formattedFromDate);
            bodyJson.put("ToTrdDate", formattedToDate);
            bodyJson.put("Segment", TR_SEGMENT);
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
        Log.d(TAG, "handleJsonArrayResponse: " + response.toString());

        try {
            JSONArray clientDetails = response.getJSONArray(0);
            JSONArray tradeDetails = response.getJSONArray(1);

            if (clientDetails.length() > 0) {
                totalBuyValue.setText("₹ " + clientDetails.getJSONObject(0).getString("BuyValue"));
                totalSellValue.setText("₹ " + clientDetails.getJSONObject(0).getString("SellValue"));
            }

            for (int i = 0; i < tradeDetails.length(); i++) {
                JSONObject object = tradeDetails.getJSONObject(i);

                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.trade_summary_list, null);
                TextView compName = listView.findViewById(R.id.compName);
                TextView exchange = listView.findViewById(R.id.exchange);
                TextView netQty = listView.findViewById(R.id.netQty);
                TextView avg = listView.findViewById(R.id.avg);
                TextView buyAvg = listView.findViewById(R.id.buyAvg);
                TextView sellAvg = listView.findViewById(R.id.sellAvg);
                TextView buyQty = listView.findViewById(R.id.buyQty);
                TextView sellQty = listView.findViewById(R.id.sellQty);

                compName.setText(object.getString("ScripName"));
                exchange.setText(object.getString("Exchange"));
                netQty.setText("Net qty. "+object.getString("NetQty"));
                avg.setText("Net avg. ₹"+object.getString("Average"));
                buyQty.setText(object.getString("BuyQty"));
                buyAvg.setText("₹"+object.getString("BuyAverage"));
                sellQty.setText(object.getString("SellQty"));
                sellAvg.setText("₹"+object.getString("SellAverage"));

                String ScripCode = object.getString("ScripCode");

                listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment m_fragment = TradeDetails.newInstance(formattedFromDate, formattedToDate, ScripCode, compName.getText().toString(),exchange.getText().toString());
                        new Handler().postDelayed(() ->
                                        getParentFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.openFrag, m_fragment)
                                                .addToBackStack(null)
                                                .commit(),
                                250);

                    }
                });

                tradeSumList.addView(listView);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    private void implementTradeSpinner() {
//
//        String[] items = {
//                "All",
//                String.valueOf(eExch.NSE),
//                String.valueOf(eExch.BSE),
//                String.valueOf(eExch.FNO),
//                String.valueOf(eExch.SLBS)
//        };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                getContext(),
//                R.layout.custom_spinner_item,
//                items
//        );
//
//        adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
//
//        tradeSpinner.setAdapter(adapter);
//
//        tradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                String seg = parentView.getItemAtPosition(position).toString();
//                TR_SEGMENT = seg.substring(0, 1).toUpperCase() + seg.substring(1).toLowerCase();
//                getTradeSumDetails();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//
//            }
//        });
//    }

    private void implementDatePicker() {
        fromDateLay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateFrom = dayOfMonth;
                                monthFrom = monthOfYear;
                                yearFrom = year;

                                String inputDateString = (dayOfMonth < 10 ? "0" : "") + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                fromDate.setText(displaydate(inputDateString));
                                formattedFromDate = getFormatedDate(inputDateString);

                                getTradeSumDetails();
                            }
                        },
                        yearFrom, monthFrom, dateFrom);

                datePickerDialog.show();
            }
        });

        toDateLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dayTo = dayOfMonth;
                                monthTo = monthOfYear;
                                yearTo = year;

                                String inputDateString = (dayOfMonth < 10 ? "0" : "") + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                toDate.setText(displaydate(inputDateString));
                                formattedToDate = getFormatedDate(inputDateString);

                                getTradeSumDetails();
                            }
                        },
                        yearTo, monthTo, dayTo);

                datePickerDialog.show();
            }
        });
    }

    public String getFormatedDate(String inputDateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yy", Locale.US);
        try {
            Date date = inputFormat.parse(inputDateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.US);
            assert date != null;

            String outputString = outputFormat.format(date);

            outputString = outputString.toLowerCase();
            return outputString;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String displaydate(String input) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
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