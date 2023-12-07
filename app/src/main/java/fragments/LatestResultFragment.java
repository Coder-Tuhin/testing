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
import view.Results_DualListView;

/**
 * Created by XTREMSOFT on 11/23/2016.
 */
@SuppressLint("ValidFragment")
public class LatestResultFragment extends Fragment {
    private ArrayList<String> scripNameList;
    private int scripCode;
    private String scripName;
    ArrayList<GroupsTokenDetails> grpScripList;
    private HashMap<String,Integer> tempMap;
    private GroupsTokenDetails tokenDetails;
    private ArrayAdapter adapter;
    private Spinner latestresult_spinner;
    private View view,result_view;
    private LinearLayout linearlayout,result_linear,result_linear1;
    Results_DualListView dualListView;
    private HomeActivity.RadioButtons radioButtons;
    public static Handler latestResultHandler;


    public LatestResultFragment(){
        super();
    }

    public LatestResultFragment(int selectedScripCode, ArrayList<GroupsTokenDetails> grpScripList,HomeActivity.RadioButtons radioButtons){
        this.scripCode = selectedScripCode;
        this.grpScripList = grpScripList;
        this.radioButtons = radioButtons;
    }
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
       ((HomeActivity) getActivity()).CheckRadioButton(radioButtons);
        View view = inflater.inflate(R.layout.latestresult_screen, container, false);
        this.view = view;
        init(view);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        latestResultHandler = new LatestResultHandler();
    }

    private void init(View view) {
        tempMap = new HashMap<>();
        getScripList();
        /*result_view = (View) view.findViewById(R.id.result_view);
        result_linear = (LinearLayout) view.findViewById(R.id.result_linear);
        result_linear1 = (LinearLayout) view.findViewById(R.id.result_linear1);*/
        latestresult_spinner = (Spinner) view.findViewById(R.id.latestresult_spinner);
        adapter = new ArrayAdapter(GlobalClass.latestContext, R.layout.custom_spinner_item, scripNameList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        latestresult_spinner.setAdapter(adapter);
        latestresult_spinner.setSelection(adapter.getPosition(scripName));
        latestresult_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                scripName = adapter.getItem(i).toString();
                scripCode = tempMap.get(scripName);
                new GetLatestResults().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(scripNameList.size() == 1){
            new GetLatestResults().execute();
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
    class GetLatestResults extends AsyncTask<Object, Void, String> {
        String strResponse;
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Please wait...");/*
                result_linear.setVisibility(View.GONE);
                result_view.setVisibility(View.GONE);
                result_linear1.setVisibility(View.GONE);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                strResponse = sendLatestRequest(scripCode, scripName).trim();
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
                updateValuesFromURL(strResponse);/*
                result_linear.setVisibility(View.GONE);
                result_view.setVisibility(View.GONE);
                result_linear1.setVisibility(View.GONE);*/
                GlobalClass.dismissdialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (httpclient1 != null){
            httpclient1.getConnectionManager().shutdown();
        }
        latestResultHandler = null;
    }

    private DefaultHttpClient httpclient1;
    public String sendLatestRequest(int token, String scripname) {
        try {
             httpclient1 = new DefaultHttpClient();
            String url = "";
            if (scripname.toUpperCase().startsWith("NE")) {
                url = "http://ventura1.acesphere.com/pointercontent/DemoTabValuation.aspx?token="+token+"&Opt=LatestResults";
            } else {//if (scripname.toUpperCase().startsWith("BE")) {
                url = "http://ventura1.acesphere.com/pointercontent/DemoTabValuation.aspx?SCCode="+token+"&Opt=LatestResults";
            }

            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("User-Agent", "Profile/MIDP-2.0 Confirguration/CLDC-1.0");
            httpget.addHeader("Accept", "text/plain");

            HttpResponse response = httpclient1.execute(httpget);
            DataInputStream in = new DataInputStream(response.getEntity().getContent());
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

    private void updateValuesFromURL(String strResp) {
        try {
            GlobalClass.log("latestResult : " + strResp);
            linearlayout = (LinearLayout) view.findViewById(R.id.layout1);
            String ltt = "00:00:00";
            String ltq = "0", ltp = "0.00";
            String perChg = "0.00 %";

            String strTitle[] = {   "type",
                    "date_end",
                    "sales",
                    "other income",
                    "pbidt",
                    "interest",
                    "pbdt",
                    "depreciation",
                    "pbt",
                    "tax",
                    "deferred tax",
                    "pat",
                    "equity"};

            ArrayList<String> title = new ArrayList<String>();
            for(int i=0 ;i<strTitle.length;i++){
                title.add(strTitle[i]);
            }

            HashMap<String,ArrayList> hashMap = new HashMap<String, ArrayList>();

            if ((strResp != null) && (!strResp.equalsIgnoreCase(""))) {

                String[] respArr = strResp.split("\\t");
                String company= respArr[0];
                ((TextView) view.findViewById(R.id.latestresult_companyname)).setText(company);
                ArrayList<String> data = null;
                for(int i=1; i<respArr.length; i++){
                    if(title.contains(respArr[i].toLowerCase())){
                        data = hashMap.get(respArr[i].toLowerCase());
                        if(data == null){
                            data = new ArrayList<String>();
                            hashMap.put(respArr[i].toLowerCase(),data);
                        }
                    }else {
                        data.add(respArr[i]);
                    }
                }
                if(linearlayout.getChildCount()>0) {
                    linearlayout.removeView(dualListView);
                }
                dualListView = null;
                if (dualListView == null) {
                    dualListView = new Results_DualListView(GlobalClass.latestContext,hashMap);
                    dualListView.setTokenCollection(hashMap);
                    linearlayout.addView(dualListView);
                }
            }
            double perChange = 0;

            StaticLiteMktWatch mktWatch =  GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);            if (mktWatch != null) {
                if (mktWatch.getLw().time.getDateInNumber() > 0) {
                    ltt = ""+ DateUtil.dateFormatter(mktWatch.getLw().time.getValue(), Constants.DDMMMYYHHMMSS);
                }
                ltq = "" + mktWatch.getLw().lastQty.getValue();
                ltp = "" + Formatter.formatter.format(mktWatch.getLw().lastRate.getValue());

                perChange = mktWatch.getPerChg();
                perChg = "" + Formatter.formatter.format(perChange) + " %";
            } else {
               // BCConnection.sendAddScripRequest(strMDSelectedScrip);
            }

            if(hashMap.get("type").get(0).toString().equalsIgnoreCase("S")) {
                ((TextView) view.findViewById(R.id.consolidatedFigure)).setText("Standalone Figures:");
            } else if(hashMap.get("type").get(0).toString().equalsIgnoreCase("C")) {
                ((TextView) view.findViewById(R.id.consolidatedFigure)).setText("Consolidated Figures:");
            }

            ((TextView) view.findViewById(R.id.lblTradeDetails)).setText(Html.fromHtml(ltq + " @ <big><big><big><b>" + ltp + "</b></big></big></big>"));
            ((TextView) view.findViewById(R.id.lblPerChange)).setText(perChg);

            if (perChange<0){
                ((TextView) view.findViewById(R.id.lblPerChange)).setTextColor(ScreenColor.RED);
            }else if (perChange>0){
                ((TextView) view.findViewById(R.id.lblPerChange)).setTextColor(ScreenColor.GREEN);
            }
            ((TextView) view.findViewById(R.id.lblTradeTime)).setText(ltt);

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
            ltp = "" + Formatter.formatter.format(mktWatch.getLw().lastRate.getValue());

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
    class LatestResultHandler extends Handler {
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
