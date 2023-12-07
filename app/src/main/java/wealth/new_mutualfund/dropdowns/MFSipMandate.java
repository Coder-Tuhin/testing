package wealth.new_mutualfund.dropdowns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.MfUtils;
import wealth.new_mutualfund.Structure.MFObjectHolder;
import wealth.new_mutualfund.menus.SipMandateFragment;

public class MFSipMandate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private HomeActivity homeActivity;
    private View mView;
    private ArrayList<Mandatemodel> bankNameList;
    private JSONObject selectedBankDetail;
    private JSONObject sipResp;

    public MFSipMandate() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MFSipMandate newInstance() {
        MFSipMandate fragment = new MFSipMandate();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.add_mandate_btn)
    LinearLayout add_mandate_btn;

    @BindView(R.id.register_a_sip)
    LinearLayout register_a_sip;

    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    JSONObject jsonDataALL;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_mfsip_mandate,container,false);
            ButterKnife.bind(this,mView);

        }
        try {
            new SIPMandateListReq(eMessageCodeWealth.GETSIPMANDATELIST_DATA.value).execute();

            sipResp = new JSONObject();
            add_mandate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeActivity.FragmentTransaction(SipMandateFragment.newInstance(sipResp, DateUtil.getSameDayNextMonthDate_2(), 1, 0), R.id.container_body, true);
                }
            });
            register_a_sip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeActivity.FragmentTransaction(SipMandateFragment.newInstance(sipResp, DateUtil.getSameDayNextMonthDate_2(), 1, 0), R.id.container_body, true);
                }
            });
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(radioGroup.getCheckedRadioButtonId() ==R.id.approved_radio){
                        handleGetSIPMandateData(jsonDataALL);
                    }else {
                        handleGetSIPMandateData(jsonDataALL);
                    }



                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return mView;
    }

    private void visibilityGone(boolean isDataAvailable){
        if (isDataAvailable){
            add_mandate_btn.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            register_a_sip.setVisibility(View.GONE);
        }else {
            add_mandate_btn.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            register_a_sip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class SIPMandateListReq extends AsyncTask<String, Void, String> {
        int msgCode;

        SIPMandateListReq(int mCode){
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
                if(msgCode == eMessageCodeWealth.GETSIPMANDATELIST_DATA.value){

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    String key = eMFJsonTag.SIPMANDATENAME.name;
                    JSONObject jsonData = MFObjectHolder.sipMandate.get(key);
                    if(jsonData == null) {
                        jsonData=VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GETSIPMANDATELIST_DATA.value,jdata);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.sipMandate.put(key, jsonData);
                        return jsonData.toString();
                    }
                }
            }
            catch (Exception ex){
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
                    jsonDataALL = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        displayError(err);
                    }
                    else {
                        if(msgCode == eMessageCodeWealth.GETSIPMANDATELIST_DATA.value){
                            handleGetSIPMandateData(jsonData);
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    private void handleGetSIPMandateData(JSONObject jsonData) {

        try {
            JSONArray banklist  = jsonData.getJSONArray("data");
            bankNameList = new ArrayList<>();
            //String[] mStringArray = new String[banklist.length()];
            for (int i = 0; i < banklist.length(); i++) {
                JSONObject jdata = banklist.getJSONObject(i);
                //mStringArray[i] = jdata.getString("MandateCode");
                Mandatemodel mandatemodel = new Mandatemodel(jdata);
                if(!mandatemodel.getMandateStatus().toLowerCase().contains("reject")) {
                    if(radiogroup.getCheckedRadioButtonId() == R.id.approved_radio){
                        if(mandatemodel.getMandateStatus().toLowerCase().contains("approved")){
                            bankNameList.add(mandatemodel);
                        }
                    }else {
                        if(!mandatemodel.getMandateStatus().toLowerCase().contains("approved")){
                            bankNameList.add(mandatemodel);
                        }
                    }

                }
            }
            if(bankNameList.size() > 0) {
                recyclerView.setAdapter(new MandateItemAdapter(bankNameList));
            }
            visibilityGone(bankNameList.size()>0);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void displaBankDetailData(JSONObject jsonData) {
        try {
            selectedBankDetail = jsonData;
            GlobalClass.log("GetBankdetail : " + jsonData.toString());
            /*
            "Id":"32",
            "AccountNo":"038801504533",
            "AccountType":"Saving ",
            "MICRCode":"400229050",
            "IFSCCode":"ICIC0000388",
            "BankName":"ICICI BANK LTD",
            "BranchName":"MINDSPACE BRANCH GR FLR EUREKA TOWERS OFF LINK RD MINDSPACE MALAD WEST",
            "BankBranchCity":"",
            "MandateAmt":"10000",
            "SIPType":"ISIP",
            "SIPMandateStartDate":"3/30/2019 12:00:00 AM",
            "SIPMandateEndDate":"3/30/2119 12:00:00 AM",
            "MandateCode":"BSE000000332955",
            "PdfFilename":"BSE000000332955.pdf",
            "UntilCancel":"T",
            "MandateStatus":"REGISTERED BY MEMBER",
            "RegistrationDate":"30 Mar 2019",
            "MandateUploadFile":"",
            "MandateUploadFilename":""
            */

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public class MandateItemAdapter extends RecyclerView.Adapter<MandateItemAdapter.MyViewHolder> {
        private int selectedIndex =-1;
        private LayoutInflater inflater;
        private ArrayList<Mandatemodel> mList;
        private Mandatemodel selectedModel;


        MandateItemAdapter(ArrayList<Mandatemodel> mList) {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            this.mList = mList;
        }


        @Override
        public MandateItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_sipmandate, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                Mandatemodel mm = mList.get(position);
                holder.bankName.setText(mm.getBankName());
                holder.accountNo.setText(mm.getAccountNo());
                holder.mandateAmt.setText(Formatter.DecimalLessIncludingComma(mm.getMandateAmt()));
                holder.sipType.setText(mm.getSIPType());
                holder.mandateStatus.setText(mm.getMandateStatus());
                holder.runningsip.setText(Formatter.DecimalLessIncludingComma(mm.getUsedAmount()));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
            //return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.holder_linear)
            LinearLayout holder_linear;
            @BindView(R.id.bankName)
            TextView bankName;
            @BindView(R.id.accountNo)
            TextView accountNo;
            @BindView(R.id.mandateAmt)
            TextView mandateAmt;
            @BindView(R.id.siptype)
            TextView sipType;
            @BindView(R.id.mandateStatus)
            TextView mandateStatus;
            @BindView(R.id.runningsip)
            TextView runningsip;


            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    private class Mandatemodel{
        String BankName = "";
        String AvailableAmt = "";
        String AccountNo = "";
        String MandateAmt = "";
        String MandateCode = "";
        String SIPMandateEndDate = "";
        String SIPMandateStartDate = "";
        String SIPType = "";
        String TransactionAmt = "";
        String MandateStatus = "";
        String UsedAmount = "";

        Mandatemodel(JSONObject jObj){
            BankName = MfUtils.getString(R.string.BankName,jObj);
            AvailableAmt = MfUtils.getString(R.string.AvailableAmt,jObj);
            AccountNo = MfUtils.getString(R.string.AccountNo,jObj);
            MandateAmt = MfUtils.getString(R.string.MandateAmt,jObj);
            MandateCode = MfUtils.getString(R.string.MandateCode,jObj);
            SIPMandateEndDate = MfUtils.getString(R.string.SIPMandateEndDate,jObj);
            SIPMandateStartDate = MfUtils.getString(R.string.SIPMandateStartDate,jObj);
            SIPType = MfUtils.getString(R.string.SIPType,jObj);
            TransactionAmt = MfUtils.getString(R.string.TransactionAmt,jObj);
            MandateStatus = MfUtils.getString(R.string.MandateStatus,jObj);
            UsedAmount = MfUtils.getString(R.string.UsedAmount,jObj);
        }

        public String getUsedAmount() {
            return UsedAmount;
        }

        public void setUsedAmount(String usedAmount) {
            UsedAmount = usedAmount;
        }

        public String getBankName() {
            return BankName;
        }

        public String getAvailableAmt() {
            return AvailableAmt;
        }

        public String getAccountNo() {
            return AccountNo;
        }

        public String getMandateAmt() {
            return MandateAmt;
        }

        public String getMandateCode() {
            return MandateCode;
        }

        public String getSIPMandateEndDate() {
            return SIPMandateEndDate;
        }

        public String getSIPMandateStartDate() {
            return SIPMandateStartDate;
        }

        public String getSIPType() {
            return SIPType;
        }

        public String getTransactionAmt() {
            return TransactionAmt;
        }

        public String getMandateStatus() {
            return MandateStatus;
        }

        public void setMandateStatus(String mandateStatus) {
            MandateStatus = mandateStatus;
        }
    }
    private ArrayList<Mandatemodel> getMandateList(JSONObject object){
        ArrayList<Mandatemodel> mList = new ArrayList<>();
        try{
            JSONArray jArr = object.optJSONArray(MfUtils.getString(R.string.mandatedata));
            for (int i =0 ;i<jArr.length();i++){
                JSONObject jObj = jArr.optJSONObject(i);
                mList.add(new Mandatemodel(jObj));
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return mList;
    }

}
