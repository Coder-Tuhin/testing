package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.bondstructure.StructBondAvailableBalance;
import wealth.new_mutualfund.menus.modelclass.SIPModel;

public class SIPSelectStartDateFragment extends Fragment {

    public String formScreen = "";
    JSONObject jdata1;

    public static SIPSelectStartDateFragment newInstance(String schemecode, String amt,String Schemename) {
        SIPSelectStartDateFragment itc =  new SIPSelectStartDateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1,schemecode);
        bundle.putString(StaticVariables.ARG_2,amt);
        bundle.putString(StaticVariables.ARG_3,Schemename);
        itc.setArguments(bundle);
        return itc;
    }
    public static SIPSelectStartDateFragment newInstance(String schemecode, String amt,String Schemename,JSONObject jData, String fromScreen) {
        SIPSelectStartDateFragment f = new SIPSelectStartDateFragment();
        Bundle args = new Bundle();
        args.putString(StaticVariables.ARG_1,schemecode);
        args.putString(StaticVariables.ARG_2,amt);
        args.putString(StaticVariables.ARG_3,Schemename);
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(eMFJsonTag.FORMSCR.name, fromScreen);
        f.setArguments(args);
        return f;

    }
    private HomeActivity homeActivity;
    private SIPModel sipModel;
    String startdate,enddate,period;
    String schemecode,schemename,amount;
    private JSONObject jschdetailData;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }
    @BindView(R.id.nextbtn)
    TextView nxtbtn;
    @BindView(R.id.tv_startdate)
    TextView tv_startdate;
    @BindView(R.id.tv_1)
    TextView tv_1;
    @BindView(R.id.tv_5)
    TextView tv_5;
    @BindView(R.id.tv_10)
    TextView tv_10;
    @BindView(R.id.tv_15)
    TextView tv_15;
    @BindView(R.id.tv_20)
    TextView tv_20;
    @BindView(R.id.sipDaySpinner)
    Spinner sipdayspinner;
    @BindView(R.id.SchemeNameTV)
    TextView SchemeNameTV;
    String SchemeName = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.sip_select_startdate_screen,container,false);
        ButterKnife.bind(this,mView);
        Bundle bundle = getArguments();
        schemecode = bundle.getString(StaticVariables.ARG_1);
        amount = bundle.getString(StaticVariables.ARG_2);
        SchemeName = bundle.getString(StaticVariables.ARG_3);
        try {
            formScreen = bundle.getString(eMFJsonTag.FORMSCR.name);
            if(formScreen.equalsIgnoreCase("nfo")) {
                String jsonStr = bundle.getString(eMFJsonTag.JDATA.name, "");
                jdata1 = new JSONObject(jsonStr);
                /*SchemeName = jdata1.optString("SchemeName");
                schemecode = jdata1.optString("SchemeCode");*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            formScreen = "";
        }
        SchemeNameTV.setText(SchemeName);
        //initDaySpinner();
        getschemedata();
        tv_1.setOnClickListener(_onclick);
        tv_5.setOnClickListener(_onclick);
        tv_10.setOnClickListener(_onclick);
        tv_15.setOnClickListener(_onclick);
        tv_20.setOnClickListener(_onclick);
        tv_20.setOnClickListener(_onclick);
        nxtbtn.setOnClickListener(_onclick);
        return mView;
    }

    private void getschemedata(){
        new SendSIPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tv_1.setActivated(false);
            tv_5.setActivated(false);
            tv_10.setActivated(false);
            tv_15.setActivated(false);
            tv_20.setActivated(false);
            switch (view.getId()){
                case R.id.tv_1:
                case R.id.tv_5:
                case R.id.tv_10:
                case R.id.tv_15:
                case R.id.tv_20:
                    SetTextInSpinner((TextView) view);
                    break;
                case R.id.nextbtn:
                    OpenAxisChipThreeFragment();
                    break;
            }
        }
    };

    private void SetTextInSpinner(TextView tv) {
        tv.setActivated(true);
        ArrayAdapter adapter = (ArrayAdapter) sipdayspinner.getAdapter();
        int position = adapter.getPosition(tv.getText().toString().trim());
        if (position>=0) sipdayspinner.setSelection(position);
    }

    private void OpenAxisChipThreeFragment(){
        if(formScreen.equalsIgnoreCase("nfo")){
            homeActivity.FragmentTransaction(SIPSelectEndDateFragment.newInstance(schemecode,schemename,startdate,enddate,amount,period,SchemeName,jdata1, eFormScr.NFO.name), R.id.container_body, true);
        }else {
            homeActivity.FragmentTransaction(SIPSelectEndDateFragment.newInstance(schemecode,schemename,startdate,enddate,amount,period,SchemeName), R.id.container_body, true);

        }
    }
    private  void  initDaySpinner(){
        ArrayAdapter spinnerAdapterF = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_black);
        spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
        spinnerAdapterF.addAll(sipModel.dateV);
        sipdayspinner.setAdapter(spinnerAdapterF);
        sipdayspinner.setOnItemSelectedListener(onItemSelected);
        String dayNo = DateUtil.getSameDayNextMonthDate().split("-")[0];
        int dayNoInt = Integer.parseInt(dayNo);
        int position = spinnerAdapterF.getPosition(String.valueOf(dayNoInt));
        sipdayspinner.setSelection(position);
    }
    private AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            setEndDateString(period);
            setStartDate();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    class SendSIPReq extends AsyncTask<String, Void, String> {
        private int msgCode;
        private StructBondAvailableBalance avlBalance;
        private String SipStartDate = "";
        SendSIPReq(int mCode){
            this.msgCode = mCode;
        }
        SendSIPReq(int mCode,String sipStartdate){
            this.msgCode = mCode;
            this.SipStartDate = sipStartdate;
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
//                    jdata.put(eMFJsonTag.FOLIONO.name,"");
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value,jdata);
                    if (jsonData != null) return jsonData.toString();
                }
                else if(msgCode == eMessageCodeWealth.GET_NEXT_WORKING_DAY.value){
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put("orderdate", SipStartDate);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_NEXT_WORKING_DAY.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
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

            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        GlobalClass.showAlertDialog(err);
                    }else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        }else if (msgCode == eMessageCodeWealth.GET_NEXT_WORKING_DAY.value) {
                            displayDateData(jsonData.toString());
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

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
    private void displayDateData(String datetext){
        try {
            JSONObject object = new JSONObject(datetext);
            int sipdate = object.optInt("sipdate");
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = format.parse(DateUtil.getSameDayNextMonthDate());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
//            cal.add(Calendar.MONTH,1);
            cal.set(Calendar.DAY_OF_MONTH, sipdate);
            Date _sipStartDate = cal.getTime();
            String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
            String _sipStartDateStrtemp = _sipStartDateStr;
            _sipStartDateStrtemp = _sipStartDateStrtemp.replace("/","-");
            tv_startdate.setText(_sipStartDateStrtemp);
            startdate = _sipStartDateStr;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void displayschemedata(){

        try {
            schemename = jschdetailData.getString("SchemeName");
            sipModel = new SIPModel();
            sipModel.minAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMinInstallmentAmt"));
            sipModel.maxAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMaxInstallmentAmt"));
            sipModel.multipleAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMultAmt"));

            sipModel.minPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMinInstallmentNumbers"));
            sipModel.maxPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMaximumInstallmentNumbers"));
            String sipdays = jschdetailData.getString("MSIPDates");
            sipModel.populateDate(sipdays);
            initDaySpinner();
            period= (sipModel.minPeriod+"");
            //  period.setHint("");//("Range "+sipModel.minPeriod + " - " +sipModel.maxPeriod);
            //}
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void setStartDate() {
        try {
            int todayDAY = Integer.parseInt(DateUtil.getSameDayNextMonthDate().split("-")[0]);
            int selectedDay = Integer.parseInt(sipdayspinner.getSelectedItem().toString());

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = format.parse(DateUtil.getSameDayNextMonthDate());

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            if (selectedDay<todayDAY ) {
                cal.add(Calendar.MONTH,1);
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);
                Date _sipStartDate = cal.getTime();
                String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
                String _sipStartDateStrtemp = _sipStartDateStr;
                _sipStartDateStrtemp = _sipStartDateStrtemp.replace("/","-");
                tv_startdate.setText(_sipStartDateStrtemp);
                startdate = _sipStartDateStr;
            }else
            if ( selectedDay>todayDAY) {
//                cal.add(Calendar.MONTH,-2);
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);
                Date _sipStartDate = cal.getTime();
                String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
                String _sipStartDateStrtemp = _sipStartDateStr;
                _sipStartDateStrtemp = _sipStartDateStrtemp.replace("/","-");
                tv_startdate.setText(_sipStartDateStrtemp);
                startdate = _sipStartDateStr;
            }else {
                Calendar cal1 = Calendar.getInstance();
                cal1.set(Calendar.DAY_OF_MONTH, selectedDay);
                Date _sipStartDate = cal1.getTime();
                String _sipStartDateStr = format.format(_sipStartDate);
                String _sipStartDateStrtemp = _sipStartDateStr;
                _sipStartDateStrtemp = _sipStartDateStrtemp.replace("/","-");
                new SendSIPReq(eMessageCodeWealth.GET_NEXT_WORKING_DAY.value,_sipStartDateStrtemp).execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    private void setStartDate() {
        try {
            int todayDAY = Integer.parseInt(DateUtil.getSameDayNextMonthDate().split("-")[0]);
            int selectedDay = Integer.parseInt(sipdayspinner.getSelectedItem().toString());

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = format.parse(DateUtil.getSameDayNextMonthDate());

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            if (selectedDay<todayDAY || selectedDay>todayDAY) {
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);
                Date _sipStartDate = cal.getTime();
                String _sipStartDateStr = SIP_SDF.format(_sipStartDate);
                String _sipStartDateStrtemp = _sipStartDateStr;
                _sipStartDateStrtemp = _sipStartDateStrtemp.replace("/","-");
                tv_startdate.setText(_sipStartDateStrtemp);
                startdate = _sipStartDateStr;
            }else {
                Calendar cal1 = Calendar.getInstance();
                cal1.set(Calendar.DAY_OF_MONTH, selectedDay);
                Date _sipStartDate = cal1.getTime();
                String _sipStartDateStr = format.format(_sipStartDate);
                String _sipStartDateStrtemp = _sipStartDateStr;
                _sipStartDateStrtemp = _sipStartDateStrtemp.replace("/","-");
                new SendSIPReq(eMessageCodeWealth.GET_NEXT_WORKING_DAY.value,_sipStartDateStrtemp).execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    private void setEndDateString(String _tempText) {
        try {
            int sellectedDay = Integer.parseInt(sipdayspinner.getSelectedItem().toString());

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
            tv_startdate.setText(DateUtil.getSameDayNextMonthDate().replace("-","/"));
            startdate = DateUtil.getSameDayNextMonthDate().replace("-","/");

            if (!TextUtils.isEmpty(_tempText)){
                int _tempMonth = StaticMethods.StringToInt(_tempText);
                if (sipModel != null &&_tempMonth>=sipModel.minPeriod && _tempMonth<=sipModel.maxPeriod){
                    //String _sipStartDateStr = sipdayspinner.getSelectedItem().toString();
                    cal = Calendar.getInstance();
                    cal.setTime(_sipStartDate);
                    cal.add(Calendar.MONTH, _tempMonth-1);
                    Date _sipEndDate = cal.getTime();
                    String _sipEndDateStr = SIP_SDF.format(_sipEndDate);
                    enddate = _sipEndDateStr;
                }else {
                    enddate= "";
                }
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private SimpleDateFormat SIP_SDF = new SimpleDateFormat("dd/MMM/yyyy");
}