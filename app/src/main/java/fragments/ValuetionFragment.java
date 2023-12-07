package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 11/23/2016.
 */
@SuppressLint("ValidFragment")
public class ValuetionFragment extends Fragment {
    private ArrayList<String> scripNameList;
    private int scripCode;
    private String scripName;
    ArrayList<GroupsTokenDetails> grpScripList;
    private HashMap<String,Integer> tempMap;
    private GroupsTokenDetails tokenDetails;
    private ArrayAdapter adapter;
    private Spinner valuation_spinner;
    private View view,valuation_view;
    private ScrollView valuation_scrollview;
    private LinearLayout valuation_linear;
    private HomeActivity.RadioButtons radioButtons;

    public static Handler valuationHandler;

    public ValuetionFragment(){
        super();
    }

    public ValuetionFragment(int selectedScripCode, ArrayList<GroupsTokenDetails> grpScripList,HomeActivity.RadioButtons radioButtons){
        this.scripCode = selectedScripCode;
        this.grpScripList = grpScripList;
        this.radioButtons = radioButtons;
    }
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        ((HomeActivity) getActivity()).CheckRadioButton(radioButtons);
        View view = inflater.inflate(R.layout.valuation_screen, container, false);
        this.view = view;
        init(view);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        valuationHandler = new ValuetionHandler();
    }

    private void init(View view) {
        tempMap = new HashMap<>();
        getScripList();
        valuation_scrollview = (ScrollView) view.findViewById(R.id.valuation_scrollview);
        valuation_view = (View) view.findViewById(R.id.valuation_view);
        valuation_linear = (LinearLayout) view.findViewById(R.id.valuation_linear);
        valuation_spinner = (Spinner) view.findViewById(R.id.valuation_spinner);
        adapter = new ArrayAdapter(GlobalClass.latestContext, R.layout.custom_spinner_item, scripNameList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        valuation_spinner.setAdapter(adapter);
        valuation_spinner.setSelection(adapter.getPosition(scripName));
        valuation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                scripName = adapter.getItem(i).toString();
                scripCode = tempMap.get(scripName);
                new GetTokenFundamentals().execute();
                // setExchName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(scripNameList.size() == 1){
            new GetTokenFundamentals().execute();
        }
    }
    private void getScripList() {
        try {
            tempMap.clear();
            scripNameList = new ArrayList<>();
            for (GroupsTokenDetails gsd : grpScripList) {
                scripNameList.add(gsd.scripName.getValue());
                tempMap.put(gsd.scripName.getValue(),gsd.scripCode.getValue());
                if(scripCode == gsd.scripCode.getValue()){
                    scripName = gsd.scripName.getValue();
                    tokenDetails = gsd;
                }
            }
        }catch (Exception e){
        }
    }

    class GetTokenFundamentals extends AsyncTask<Object, Void, String> {
        private String strResponse;
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Please wait...");
                valuation_linear.setVisibility(View.GONE);
                valuation_view.setVisibility(View.GONE);
                valuation_scrollview.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                strResponse = sendFundamentalRequest(scripCode, scripName).trim();
                Log.v("", "Token Fundamentals Request sent to server..");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                super.onPostExecute(result);
                updateValuesFromURL(strResponse);
                valuation_linear.setVisibility(View.VISIBLE);
                valuation_view.setVisibility(View.VISIBLE);
                valuation_scrollview.setVisibility(View.VISIBLE);
                GlobalClass.dismissdialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private DefaultHttpClient httpclient1;

    public String sendFundamentalRequest(int token, String scripname) {
        try {
             httpclient1 = new DefaultHttpClient();
            String url = "";
            if (scripname.toUpperCase().startsWith("NE")) {
                url = "http://ventura1.acesphere.com/pointercontent/DemoTabValuation.aspx?SCCode=&token=" + token;
            } else {//if (scripname.toUpperCase().startsWith("BE")) {
                url = "http://ventura1.acesphere.com/pointercontent/DemoTabValuation.aspx?SCCode=" + token;
            }
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("User-Agent", "Profile/MIDP-2.0 Confirguration/CLDC-1.0");
            httpget.addHeader("Accept", "text/plain");
            HttpResponse response = httpclient1.execute(httpget);
            DataInputStream in = new DataInputStream(response.getEntity().getContent());
            //while(true){
            String strRow = in.readLine();
            Log.v("", "Response String : " + strRow);
            in.close();
            in = null;
            return strRow;
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "";
    }


    @Override
    public void onPause() {
        super.onPause();
        if (httpclient1 != null){
            httpclient1.getConnectionManager().shutdown();
        }
        valuationHandler = null;
    }

    private void updateValuesFromURL(String strResp) {
        try {

            String companyName = "";
            String LTP = "0.00";
            String equity = "0.00";
            String PE_TTM = "0.00";
            String P_BV = "0.00";
            String P_CEPS_TTM = "0.00";
            String EV_EBIDTA_TTM = "0.00";
            String MCAP_Rs = "0.00";
            String Mcap_Sales_TTM = "0.00";
            String DIV_Yield = "0.00";
            String DIV = "0.00";
            String ROCE = "0.00";
            String RONW = "0.00";
            String EPS_TTM = "0.00";
            String High_52W = "0.00";
            String Low_52W = "0.00";
            String Face_value = "0.00";
            String consolidated_figure = "0.00";
            String ltt = "00:00:00";
            String ltq = "0", ltp = "0.00";
            String perChg = "0.00 %";
            String debt = "0.00";

            if ((strResp != null) && (!strResp.equalsIgnoreCase(""))) {
                String[] respArr = strResp.split("\\t");
                companyName = respArr[0];
                LTP = respArr[1];
                equity = respArr[2];
                PE_TTM = respArr[3];
                P_BV = respArr[4];
                P_CEPS_TTM = respArr[5];
                EV_EBIDTA_TTM = respArr[6];
                MCAP_Rs = respArr[7];
                Mcap_Sales_TTM = respArr[8];
                DIV_Yield = respArr[9];
                DIV = respArr[10];
                ROCE = respArr[11];
                RONW = respArr[12];
                EPS_TTM = respArr[13];
                High_52W = respArr[14];
                Low_52W = respArr[15];
                Face_value = respArr[16];
                consolidated_figure = respArr[17];
                debt = respArr[18];
            }

            StaticLiteMktWatch mktWatch =  GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            double perChange = 0;
            if (mktWatch != null) {
                if (mktWatch.getLw().time.getDateInNumber() > 0) {
                    ltt =""+ DateUtil.dateFormatter(mktWatch.getLw().time.getValue(), Constants.DDMMMYYHHMMSS);
                }
                ltq = "" + mktWatch.getLw().lastQty.getValue();
                ltp = "" + Formatter.formatter.format(mktWatch.getLastRate());

                perChange = mktWatch.getPerChg();
                perChg = "" + Formatter.formatter.format(perChange) + " %";
            }

            if(consolidated_figure.equalsIgnoreCase("S")) {
                ((TextView) view.findViewById(R.id.consolidatedFigure)).setText("Standalone Figures:");
            }
            ((TextView) view.findViewById(R.id.valuation_company)).setText(companyName);
            ((TextView) view.findViewById(R.id.epsValue)).setText(EPS_TTM);
            ((TextView) view.findViewById(R.id.peValue)).setText(PE_TTM);
            ((TextView) view.findViewById(R.id.pcepsValue)).setText(P_CEPS_TTM);
            ((TextView) view.findViewById(R.id.pbvValue)).setText(P_BV);
            ((TextView) view.findViewById(R.id.evValue)).setText(EV_EBIDTA_TTM);
            ((TextView) view.findViewById(R.id.roceValue)).setText(ROCE);
            ((TextView) view.findViewById(R.id.ronwValue)).setText(RONW);
            ((TextView) view.findViewById(R.id.mcapsalesValue)).setText(Mcap_Sales_TTM);
            ((TextView) view.findViewById(R.id.divValue)).setText(DIV);
            ((TextView) view.findViewById(R.id.divyieldValue)).setText(DIV_Yield);
            ((TextView) view.findViewById(R.id.equityValue)).setText(equity);
            ((TextView) view.findViewById(R.id.faceValue)).setText(Face_value);
            ((TextView) view.findViewById(R.id.mcapValue)).setText(MCAP_Rs);
            ((TextView) view.findViewById(R.id.high52w)).setText(High_52W);
            ((TextView) view.findViewById(R.id.low52w)).setText(Low_52W);
            ((TextView) view.findViewById(R.id.lblTradeDetails)).setText(Html.fromHtml(ltq + " @ <big><big><big><b>" + ltp + "</b></big></big></big>"));
            ((TextView) view.findViewById(R.id.lblPerChange)).setText(perChg);
            if (perChange<0){
                ((TextView) view.findViewById(R.id.lblPerChange)).setTextColor(ScreenColor.RED);
            }else if (perChange>0){
                ((TextView) view.findViewById(R.id.lblPerChange)).setTextColor(ScreenColor.GREEN);
            }
            ((TextView) view.findViewById(R.id.lblTradeTime)).setText(ltt);
            ((TextView) view.findViewById(R.id.debtValue)).setText(debt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refreshMktWatchData(){

        String ltt = "00:00:00";
        String ltq = "0", ltp = "0.00";
        String perChg = "0.00 %";

        StaticLiteMktWatch mktWatch =  GlobalClass.mktDataHandler.getMkt5001Data(scripCode,false);
        double perChange = 0;
        if (mktWatch != null) {
            if (mktWatch.getLw().time.getDateInNumber() > 0) {
                ltt =""+ DateUtil.dateFormatter(mktWatch.getLw().time.getValue(), Constants.DDMMMYYHHMMSS);
            }
            ltq = "" + mktWatch.getLw().lastQty.getValue();
            ltp = "" + Formatter.formatter.format(mktWatch.getLastRate());

                perChange = mktWatch.getPerChg();
            perChg = "" + Formatter.formatter.format(perChange) + " %";
        }
        ((TextView) view.findViewById(R.id.lblTradeDetails)).setText(Html.fromHtml(ltq + " @ <big><big><big><b>" + ltp + "</b></big></big></big>"));
        ((TextView) view.findViewById(R.id.lblPerChange)).setText(perChg);
        if (perChange<0){
            ((TextView) view.findViewById(R.id.lblPerChange)).setTextColor(ScreenColor.RED);
        }else if (perChange>0){
            ((TextView) view.findViewById(R.id.lblPerChange)).setTextColor(ScreenColor.GREEN);
        }
        ((TextView) view.findViewById(R.id.lblTradeTime)).setText(ltt);
    }
    class ValuetionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    eMessageCode eshgCode = eMessageCode.valueOf(msgCode);
                    switch (eshgCode) {
                        case LITE_MW:
                            int token = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                            if (token == scripCode) {
                                refreshMktWatchData();
                            }
                            break;

                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
}
