package wealth.new_mutualfund.newMF;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.bondstructure.StructBondAvailableBalance;
import wealth.new_mutualfund.menus.modelclass.SIPModel;

public class SIPSelectEndDateFragment extends Fragment {
    String SchemeName = "";
    public String formScreen = "";
    JSONObject jdata1;

    private static String SCHEMECODE = "schemecode";
    private static String SCHEMENAME = "schemename";
    private static String START_DATE = "start_date";
    private static String END_DATE = "end_date";
    private static String SELECTED_SIP_AMOUNT = "selected_sip_amount";
    private static String PEROID = "period";

    public static SIPSelectEndDateFragment newInstance(String schemecode, String schemename, String start_date, String end_date, String amt, String period,String SchemneName) {
        SIPSelectEndDateFragment fragment =  new SIPSelectEndDateFragment();
        Bundle args = new Bundle();
        args.putString(SCHEMECODE, schemecode);
        args.putString(SCHEMENAME, schemename);
        args.putString(START_DATE, start_date);
        args.putString(END_DATE, end_date);
        args.putString(SELECTED_SIP_AMOUNT, amt);
        args.putString(PEROID,period);
        args.putString("SchemeName",SchemneName);
        fragment.setArguments(args);
        return fragment;
    }
    public static SIPSelectEndDateFragment newInstance(String schemecode, String schemename, String start_date, String end_date, String amt, String period,String SchemneName,JSONObject jData, String fromScreen) {
        SIPSelectEndDateFragment fragment =  new SIPSelectEndDateFragment();
        Bundle args = new Bundle();
        args.putString(SCHEMECODE, schemecode);
        args.putString(SCHEMENAME, schemename);
        args.putString(START_DATE, start_date);
        args.putString(END_DATE, end_date);
        args.putString(SELECTED_SIP_AMOUNT, amt);
        args.putString(PEROID,period);
        args.putString("SchemeName",SchemneName);
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(eMFJsonTag.FORMSCR.name, fromScreen);
        fragment.setArguments(args);
        return fragment;
    }
    private HomeActivity homeActivity;
    private SIPModel sipModel;
    private JSONObject jschdetailData;
    private JSONObject jHoldingData;
    private String schemecode,schemename,start_date,end_date,amount,period;
    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    public static SIPSelectEndDateFragment newInstance(){
        return new SIPSelectEndDateFragment();
    }
    @BindView(R.id.tv_60m)
    TextView tv_60m;
    @BindView(R.id.tv_120m)
    TextView tv_120m;
    @BindView(R.id.tv_cancelled)
    TextView tv_cancelled;
    @BindView(R.id.tv_enddate)
    TextView tv_enddate;
    @BindView(R.id.sipDaySpinner)
    Spinner sipdayspinner;
    @BindView(R.id.nextbtn)
    Button startbtn;
    @BindView(R.id.schemenameTv)
    TextView schemenameTv;
    @BindView(R.id.untilcancelled_icon)
    ImageView untilcancelled_icon;
    @BindView(R.id.monofchoice_ll)
    LinearLayout monofchoice_ll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.sip_select_enddate_screen,container,false);
        ButterKnife.bind(this,mView);
        Bundle args = getArguments();
        schemecode = args.getString(SCHEMECODE,"");
        start_date = args.getString(START_DATE,"");
        end_date = args.getString(END_DATE,"");
        tv_enddate.setText("Until I Cancel");
        amount = args.getString(SELECTED_SIP_AMOUNT,"");
        schemename = args.getString(SCHEMENAME,"");
        period = args.getString(PEROID,"");
        SchemeName = args.getString("SchemeName");
        try {
            formScreen = args.getString(eMFJsonTag.FORMSCR.name);
            if(formScreen.equalsIgnoreCase("nfo")) {
                String jsonStr = args.getString(eMFJsonTag.JDATA.name, "");
                jdata1 = new JSONObject(jsonStr);

            }
        } catch (Exception e) {
            e.printStackTrace();
            formScreen = "";
        }
        schemenameTv.setText(SchemeName);
        tv_60m.setOnClickListener(_onclick);
        tv_120m.setOnClickListener(_onclick);
        tv_cancelled.setOnClickListener(_onclick);
        startbtn.setOnClickListener(_onclick);
        untilcancelled_icon.setOnClickListener(_onclick);
        tv_cancelled.setActivated(true);
        setEndDateString();
        tv_enddate.setText("Until I Cancel");
        monofchoice_ll.setVisibility(View.GONE);
        return  mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getschemedata();
    }

    private void getschemedata(){
        new SendSIPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_60m:
                case R.id.tv_120m:
                    tv_60m.setActivated(false);
                    tv_120m.setActivated(false);
                    tv_cancelled.setActivated(false);
                    monofchoice_ll.setVisibility(View.VISIBLE);
                    SetTextInSpinner((TextView) view);
                    break;
                case R.id.tv_cancelled:
                    tv_60m.setActivated(false);
                    tv_120m.setActivated(false);
                    tv_cancelled.setActivated(true);
                    tv_enddate.setText("Until I Cancel");
                    monofchoice_ll.setVisibility(View.GONE);

                    break;
                case R.id.nextbtn:
                    OpenAxisChipfundFragment();
                    break;
                case R.id.untilcancelled_icon:
                    showExitLoadWindow("What is Until I Cancel?", homeActivity.getResources().getString(R.string.untilcancel_info),
                            homeActivity.getResources().getDimension(R.dimen.text_16));
                    break;
            }

        }
    };

    private void showExitLoadWindow(String title, String value, float titleTextSize){
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.fact_sheet_exit_load_dialog_layout);

            TextView titleTv = dialog.findViewById(R.id.title);
            //titleTv.setTextSize(titleTextSize);
            titleTv.setText(title);

            TextView exit_value_tv = dialog.findViewById(R.id.exit_value_tv);
            exit_value_tv.setText(value); //exitLoadValue

            ImageView closeAlert = dialog.findViewById(R.id.closeTv);
            closeAlert.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void SetTextInSpinner(TextView tv) {
        tv.setActivated(!tv.isActivated());
        ArrayAdapter adapter = (ArrayAdapter) sipdayspinner.getAdapter();
        int position = adapter.getPosition(tv.getText().toString().trim());
        if (position>=0) sipdayspinner.setSelection(position);
    }
    private void setSpinnerData(Spinner spinner) {
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity, R.layout.item_spinner);
        categoryAdp.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categoryAdp.add("Select");
        categoryAdp.add("6 Months");
        categoryAdp.add("12 Months");
        categoryAdp.add("24 Months");
        categoryAdp.add("36 Months");
        categoryAdp.add("48 Months");
        categoryAdp.add("60 Months");
        categoryAdp.add("72 Months");
        categoryAdp.add("84 Months");
        categoryAdp.add("96 Months");
        categoryAdp.add("120 Months");
        spinner.setAdapter(categoryAdp);
        spinner.setOnItemSelectedListener(onItemSelected);
    }
    private  void OpenAxisChipfundFragment() {
        String ValidTillCancel = tv_cancelled.isActivated()?"T":"F";

        if(formScreen.equalsIgnoreCase("nfo")){
            homeActivity.FragmentTransaction(OrderDetailsScreen.newInstance(schemecode,schemename,start_date,tv_enddate.getText().toString(),amount,period,ValidTillCancel,jdata1, eFormScr.NFO.name), R.id.container_body, true);

        }else {
            homeActivity.FragmentTransaction(OrderDetailsScreen.newInstance(schemecode,schemename,start_date,tv_enddate.getText().toString(),amount,period,ValidTillCancel), R.id.container_body, true);

        }
    }

    class SendSIPReq extends AsyncTask<String, Void, String> {
        private int msgCode;
        private StructBondAvailableBalance avlBalance;
        SendSIPReq(int mCode){
            this.msgCode = mCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }
        @Override
        protected String doInBackground(String... strings) {
            try {

                if(msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.SCHEMECODE.name,schemecode);
                    jdata.put(eMFJsonTag.OPTION.name, eOptionMF.SIP.name);
                    if(formScreen.equalsIgnoreCase("nfo")){
                        jdata.put(eMFJsonTag.FOLIONO.name,jdata1.optString("FolioNo"));
                        jdata.put(eMFJsonTag.entrymode.name,"NFO");
                    }else {
                        jdata.put(eMFJsonTag.FOLIONO.name, "");
                    }
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value,jdata);
                    if (jsonData != null) return jsonData.toString();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if(s != null){ try {
                JSONObject jsonData = new JSONObject(s);
                if(!jsonData.isNull("error")){
                    String err = jsonData.getString("error");
                }else {
                    if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                        displaData(jsonData);
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            }
        }
    }
    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    private void displaData(JSONObject jsonData) {
        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedetailsdata");
            if(jschdetailDataArr.length() > 0) {
                jschdetailData = jschdetailDataArr.getJSONObject(0);
                displayschemedata();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void displayschemedata(){

        try {
            //if(efrequency == eMFFrequency.MONTHLY) {
            String multipleOf  = jschdetailData.getString("MSIPMultAmt");
            String minimumAmt = jschdetailData.getString("MSIPMinInstallmentAmt");
            _minimumAmt = StaticMethods.StringToDouble(minimumAmt);
            _multipleOf = StaticMethods.StringToDouble(multipleOf);
            //  schemecode = jschdetailData.getString("SchemeCode");
            sipModel = new SIPModel();
            sipModel.minAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMinInstallmentAmt"));
            sipModel.maxAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMaxInstallmentAmt"));
            sipModel.multipleAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMultAmt"));

            sipModel.minPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMinInstallmentNumbers"));
            sipModel.maxPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMaximumInstallmentNumbers"));
            String sipdays = jschdetailData.getString("MSIPDates");
            sipModel.populateDate(sipdays);
            setSpinnerData(sipdayspinner);
            //  initDaySpinner();
            //period.setText(sipModel.minPeriod+"");
            //period.setHint("");//("Range "+sipModel.minPeriod + " - " +sipModel.maxPeriod);
            //}
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //setEndDateString(period.getText().toString());
            setEndDateString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void setEndDateString() {
        try {
            String seldate = sipdayspinner.getSelectedItem().toString();

            String subCatArr[] = seldate.split(" ");
            int sellectedDay =Integer.parseInt(subCatArr[0]) ;

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH,1);
            cal.set(Calendar.DAY_OF_MONTH, sellectedDay);

            int dayDiff = DateUtil.compareDates(cal.getTime(),new Date());
            if(dayDiff<30){
                cal.add(Calendar.MONTH, 1);
            }
            Date _sipStartDate = cal.getTime();
            String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
            //startdate.setText(_sipStartDateStr);
            // startdate.setText(DateUtil.getSameDayNextMonthDate().replace("-","/"));
            //int _tempMonth = StaticMethods.StringToInt(_tempText);
            String ValidTillCancel = tv_cancelled.isActivated()?"T":"F";
            if(ValidTillCancel=="F"){
                cal = Calendar.getInstance();
                cal.setTime(_sipStartDate);
                cal.add(Calendar.MONTH, sellectedDay-1);
                Date _sipEndDate = cal.getTime();
                String _sipEndDateStr = SIP_SDF.format(_sipEndDate);
                _sipEndDateStr=_sipEndDateStr.replace("/","-");
                tv_enddate.setText(_sipEndDateStr);
            }else if (sipModel != null &&sellectedDay>=sipModel.minPeriod && sellectedDay<=sipModel.maxPeriod){
                //String _sipStartDateStr = sipdayspinner.getSelectedItem().toString();
                cal = Calendar.getInstance();
                cal.setTime(_sipStartDate);
                cal.add(Calendar.MONTH, sellectedDay-1);
                Date _sipEndDate = cal.getTime();
                String _sipEndDateStr = SIP_SDF.format(_sipEndDate);
                _sipEndDateStr = _sipEndDateStr.replace("/","-");
                tv_enddate.setText(_sipEndDateStr);
            }else {
                tv_enddate.setText("");
            }
            if(seldate.equalsIgnoreCase("Select") && ValidTillCancel=="T"){
                tv_enddate.setText("Until I Cancel");
            }
            try {
                String[] seldate_splited = seldate.split("\\s+");
                period = seldate_splited[0];
            }catch (Exception ex){
                period = seldate.substring(0,seldate.length()-7);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
    private SimpleDateFormat SIP_SDF = new SimpleDateFormat("dd/MMM/yyyy");
}