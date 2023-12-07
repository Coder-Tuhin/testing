package wealth.new_mutualfund.dropdowns;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMF_HoldingReportFor;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import enums.ePrefTAG;
import utils.ScreenColor;
import wealth.new_mutualfund.factSheet.FactSheetPagerFragment;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import view.TvRegular;
import view.help.MutualFundHelpScreen;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.MFObjectHolder;
import wealth.new_mutualfund.menus.RedeemptionFragment;
import wealth.new_mutualfund.menus.StpFragment;
import wealth.new_mutualfund.menus.SwitchFragment;
import wealth.new_mutualfund.menus.SwpFragment;
import wealth.new_mutualfund.newMF.OneTimeFragment;
import wealth.new_mutualfund.newMF.SIPEnterAmoutFragment;


public class MFHoldingReports extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    private MFHoldingReportAdapter mfReportAdapter;
    private ArrayList<String> assetTypeList;
    private ArrayList<JSONObject> familyMemberList;
    private String selectedClientID;
    private int fromScreenTag;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.totcostAmt)
    TvRegular totPur;
    @BindView(R.id.totcurrentAmt)
    TvRegular totcurrentAmt;
    @BindView(R.id.totgainLoss)
    TvRegular totGainLoss;
    @BindView(R.id.totxirr)
    TvRegular totxirrval;
    @BindView(R.id.asondate)
    TvRegular asonDate;
    @BindView(R.id.fromScreenTitle)
    TvRegular fromScreenTitle;
    @BindView(R.id.mfSpinnerAssetType)
    Spinner spinnerAssetType;
    @BindView(R.id.mfSpinnerfamily)
    Spinner spinnerFamily;
    @BindView(R.id.familySpnLayout)
    LinearLayout familySpnLayout;
    @BindView(R.id.gain_loss_layout_header)
    LinearLayout gain_loss_layout_header;
    @BindView(R.id.grand_total_layout)
    LinearLayout grand_total_layout;
    @BindView(R.id.bottom_ad_layout)
    LinearLayout bottom_ad_layout;
    @BindView(R.id.asOnLayout)
    LinearLayout asOnLayout;

    private String itemButtonTitle = "Go";
    public static MFHoldingReports newInstance(int fromScreenTag){
        MFHoldingReports f = new MFHoldingReports();
        Bundle args = new Bundle();
        args.putInt(eMFJsonTag.FORMSCR.name, fromScreenTag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null){
            mView = inflater.inflate(R.layout.mf_holdingreports,container,false);
            ButterKnife.bind(this, mView);

            Bundle args = getArguments();
            fromScreenTag = args.getInt(eMFJsonTag.FORMSCR.name, 1);
            if (fromScreenTag == eMF_HoldingReportFor.REPORT.value) {
                fromScreenTitle.setVisibility(View.GONE);
            }else{
                familySpnLayout.setVisibility(View.GONE);
                asOnLayout.setVisibility(View.GONE);
                grand_total_layout.setVisibility(View.GONE);
                bottom_ad_layout.setVisibility(View.GONE);
                gain_loss_layout_header.setVisibility(View.INVISIBLE);
                if (fromScreenTag == eMF_HoldingReportFor.TOP_UP.value){
                    fromScreenTitle.setText(eMF_HoldingReportFor.TOP_UP.name);
                    itemButtonTitle = "Invest";
                }else if(fromScreenTag == eMF_HoldingReportFor.REDEMPTION.value){
                    fromScreenTitle.setText(eMF_HoldingReportFor.REDEMPTION.name);
                    itemButtonTitle = "Redeem";
                }else if(fromScreenTag == eMF_HoldingReportFor.SWITCH.value){
                    fromScreenTitle.setText(eMF_HoldingReportFor.SWITCH.name);
                    itemButtonTitle = "Switch";
                }
            }
            mfReportAdapter = new MFHoldingReportAdapter();
            recyclerView.setAdapter(mfReportAdapter);
            new HoldingReportReq(eMessageCodeWealth.FAMILY_DATA.value).execute();
            //new HoldingReportReq(eMessageCodeWealth.ASSETTYPE_DATA.value).execute();
        }
        return mView;
    }

    public class MFHoldingReportAdapter extends RecyclerView.Adapter<MFHoldingReportAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        MFHoldingReportAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList<>();
        }

        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.list_holdingreport, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            try {
                JSONObject jData = mList.get(position);
                holder.reloadData(jData);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            //return 5;
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.schemeName)
            TextView schemeName;
            @BindView(R.id.background)
            LinearLayout background;
            @BindView(R.id.gainLoss)
            TextView gainLoss;
            @BindView(R.id.noUnits)
            TextView noUnits;
            @BindView(R.id.costAmt)
            TextView costAmt;
            @BindView(R.id.currentAmt)
            TextView currentAmt;
            @BindView(R.id.xirrval)
            TextView xirrVal;
            @BindView(R.id.actionIV)
            ImageView actionIV;
            @BindView(R.id.actions)
            LinearLayout actions;
            @BindView(R.id.gain_loss_layout)
            LinearLayout gain_loss_layout;
            @BindView(R.id.go_btn)
            LinearLayout go_btn;
            @BindView(R.id.item_button)
            TextView item_button;

            private JSONObject jData;
            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                if (fromScreenTag == eMF_HoldingReportFor.REPORT.value) {
                    gain_loss_layout.setVisibility(View.VISIBLE);
                    actions.setVisibility(View.VISIBLE);
                    go_btn.setVisibility(View.GONE);
                }
                else{
                    gain_loss_layout.setVisibility(View.GONE);
                    actions.setVisibility(View.GONE);
                    go_btn.setVisibility(View.VISIBLE);
                    item_button.setText(itemButtonTitle);
                }
                schemeName.setOnClickListener(v -> {
                    try{
                        homeActivity.FragmentTransaction(FactSheetPagerFragment.newInstance(jData.getString("SchemeCode")), R.id.container_body, true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });

                item_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            PopupMenu popup = new PopupMenu(GlobalClass.homeActivity, actionIV);
                            if (fromScreenTag==eMF_HoldingReportFor.TOP_UP.value) { //2: Top-up
                                OneTimeFragment ls = OneTimeFragment.newInstance(jData.getString("SchemeCode"), jData.getString("SchemeName"),jData.optString("FolioNo"), eOptionMF.TOPUP.name,jData.optString("OnlineFlag"));
                                homeActivity.FragmentTransaction(ls, R.id.container_body, true);
                            }else if (fromScreenTag==eMF_HoldingReportFor.REDEMPTION.value) { //3: Reedim
                                homeActivity.FragmentTransaction(RedeemptionFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            }else if (fromScreenTag==eMF_HoldingReportFor.SWITCH.value) { //4: Switch
                                homeActivity.FragmentTransaction(SwitchFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            }else{
                                if(VenturaServerConnect.mfClientType == eMFClientType.MFI){
                                    if(jData.getString("OnlineFlag").equalsIgnoreCase("online")) {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus_mfi, popup.getMenu());
                                    }else {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus, popup.getMenu());

                                    }
                                }else{
                                    if(jData.getString("OnlineFlag").equalsIgnoreCase("online")) {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus_mfi, popup.getMenu());
                                    }else {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus, popup.getMenu());

                                    }
                                }popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        handleMenuClick(menuItem.getItemId());
                                        //Toast.makeText(GlobalClass.latestContext, name.getText().toString() , Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                });
                                popup.show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                actions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            PopupMenu popup = new PopupMenu(GlobalClass.homeActivity, actionIV);

                            if (fromScreenTag==eMF_HoldingReportFor.TOP_UP.value) { //2: Top-up

                                OneTimeFragment ls = OneTimeFragment.newInstance(jData.getString("SchemeCode"),
                                        jData.getString("SchemeName"),jData.optString("FolioNo"),
                                        eOptionMF.TOPUP.name, jData.optString("OnlineFlag"));
                                homeActivity.FragmentTransaction(ls, R.id.container_body, true);

                            }else if (fromScreenTag==eMF_HoldingReportFor.REDEMPTION.value) { //3: Reedim
                                homeActivity.FragmentTransaction(RedeemptionFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            }else if (fromScreenTag==eMF_HoldingReportFor.SWITCH.value) { //4: Switch
                                homeActivity.FragmentTransaction(SwitchFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            }else{
                                if(VenturaServerConnect.mfClientType == eMFClientType.MFI){
                                    if(jData.getString("OnlineFlag").equalsIgnoreCase("online")) {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus_mfi, popup.getMenu());
                                    }else {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus, popup.getMenu());
                                    }
                                }else{
                                    if(jData.getString("OnlineFlag").equalsIgnoreCase("online")) {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus_mfi, popup.getMenu());
                                    }else {
                                        popup.getMenuInflater().inflate(R.menu.holding_report_menus, popup.getMenu());

                                    }
                                }popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        handleMenuClick(menuItem.getItemId());
                                        //Toast.makeText(GlobalClass.latestContext, name.getText().toString() , Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                });
                                popup.show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

            public void reloadData(JSONObject jData){

                try {
                    this.jData = jData;
                    Double gainLossV = Formatter.stringToDouble(jData.getString("GainLoss"));
                    schemeName.setText(jData.getString("SchemeName"));
                    gainLoss.setText(Formatter.DecimalLessIncludingComma(jData.getString("GainLoss")));
                    xirrVal.setText(Formatter.toOneDecimal(jData.optDouble("XIRR")));
                    costAmt.setText(Formatter.DecimalLessIncludingComma(jData.getString("PurchaseAmt")));
                    currentAmt.setText(Formatter.DecimalLessIncludingComma(jData.getString("CurrentAmt")));
                    noUnits.setText(jData.getString("Units"));
                    if (gainLossV < 0) {
                        gainLoss.setTextColor(ScreenColor.getRED(getContext()));
                    } else {
                        gainLoss.setTextColor(ScreenColor.getGREEN(getContext()));
                    }
                    String onlineFlag = jData.getString("OnlineFlag");
                    if(onlineFlag.equalsIgnoreCase("online")){
                        schemeName.setTextColor(getResources().getColor(R.color.ventura_color));

                    }else {
                        schemeName.setTextColor(getResources().getColor(R.color.white));
                    }
                    visibleView(onlineFlag);

                    /*
                    "RowId":"RowId2",
                    "SchemeCode":"yPWlMU/CIrY=",
                    "ClientCode":"4000021186",
                    "FolioNo":"91017153923",
                    "SchemeName":"Axis Long Term Equity Fund(G)",
                    "Units":"70.064",
                    "PurchaseAmt":"3,000",
                    "CurrentAmt":"3,102.88",
                    "GainLoss":"102.88",
                    "Dividend":"0",
                    "ProfitandLoss":"102.88",
                    "AbsoluteReturn":"3.4",
                    "WeightAmt":"3000.00",
                    "XIRR":10.010528564453125,
                    "OnlineFlag":"online",
                    "Date":"22/Apr/2019"
            */
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            private void visibleView(String onlineFlag){

                boolean isOnline = onlineFlag.equalsIgnoreCase("online");
                int action = View.VISIBLE;
                int colorI = R.color.white;

                /*
                if(VenturaServerConnect.mfClientType == eMFClientType.MFI) {
                    if (!isOnline) {
                        action = View.INVISIBLE;
                        colorI = R.color.silver;
                    }
                } else if(VenturaServerConnect.mfClientType == eMFClientType.MFD){
                    if (isOnline) {
                        action = View.INVISIBLE;
                        colorI = R.color.silver;
                    }
                } else if(VenturaServerConnect.mfClientType == eMFClientType.MFD_ONBOARD){
                    action = View.INVISIBLE;
                    colorI = R.color.silver;
                }*/

                if(!VenturaServerConnect.mfClientID.equalsIgnoreCase(selectedClientID)){
                    action = View.INVISIBLE;
                }

                if (fromScreenTag!=eMF_HoldingReportFor.REPORT.value){
                    actions.setVisibility(View.GONE);
                    go_btn.setVisibility(action);
                }else{
                    actions.setVisibility(action);
                }


//                schemeName.setTextColor(getResources().getColor(colorI));
                xirrVal.setTextColor(getResources().getColor(colorI));
                costAmt.setTextColor(getResources().getColor(colorI));
                currentAmt.setTextColor(getResources().getColor(colorI));
                noUnits.setTextColor(getResources().getColor(colorI));
            }
            private void handleMenuClick(int id){
                try {
                    switch (id) {
                        case R.id.purchase:
                            homeActivity.FragmentTransaction(OneTimeFragment.newInstance(jData.optString("SchemeCode"), jData.optString("SchemeName"),jData.optString("FolioNo"), eOptionMF.TOPUP.name,jData.optString("OnlineFlag")), R.id.container_body, true);
                            break;
                        case R.id.redemption:
                            homeActivity.FragmentTransaction(RedeemptionFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            break;
                        case R.id.swtch:
                            homeActivity.FragmentTransaction(SwitchFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            break;
                        case R.id.sip:
                            homeActivity.FragmentTransaction(SIPEnterAmoutFragment.newInstance(jData.optString("SchemeCode"),jData.optString("SchemeName")), R.id.container_body, true);
                            break;
                        case R.id.stp:
                            homeActivity.FragmentTransaction(StpFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            break;
                        case R.id.swp:
                            homeActivity.FragmentTransaction(SwpFragment.newInstance(jData,eFormScr.HOLDINGREPORT.name), R.id.container_body, true);
                            break;
                        default:
                            break;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    class HoldingReportReq extends AsyncTask<String, Void, String> {

        int msgCode;
        HoldingReportReq(int msgCode){
            this.msgCode = msgCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if(msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value){
                assetTypeList = VenturaServerConnect.getAssetType();
                return "";
            }
            else if(msgCode == eMessageCodeWealth.FAMILY_DATA.value){
                familyMemberList = VenturaServerConnect.getFamilyMembers();
                return "";
            }else {
                try {
                    JSONObject selectedFamily = familyMemberList.get(spinnerFamily.getSelectedItemPosition());
                    String clinetC = selectedFamily.getString("ClientID");
                    selectedClientID = clinetC;
                    JSONObject jsonDataReq = new JSONObject();
                    jsonDataReq.put(eMFJsonTag.FAMILYCODE.name, clinetC);
                    jsonDataReq.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jsonDataReq.put(eMFJsonTag.ASSET.name, "");
                    jsonDataReq.put(eMFJsonTag.CLIENTTYPE.name, selectedFamily.getString("Flag"));

                    JSONObject jsonData = MFObjectHolder.holdingReport.get(clinetC);
                    if(jsonData == null) {
                        jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode, jsonDataReq);
                    }
                    if (jsonData != null) {
                        MFObjectHolder.holdingReport.put(clinetC, jsonData);
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if(s != null){
                try {
                    if (msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value) {
                        displayAssetType();
                    } else if (msgCode == eMessageCodeWealth.FAMILY_DATA.value) {
                        displayFamilyMember();
                    } else {
                        JSONObject jsonData = new JSONObject(s);
                        if(!jsonData.isNull("error")){
                            String err = jsonData.getString("error");
                            if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                                GlobalClass.showAlertDialog(err,true);
                                return;
                            }
                            displayError(err);
                        }
                        else {
                            displaData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private  void displayAssetType(){
        spinnerAssetType.setOnItemSelectedListener(null);
        String[] mStringArray = new String[assetTypeList.size()];
        mStringArray = assetTypeList.toArray(mStringArray);
        setSpinnerData(spinnerAssetType,mStringArray);
        new HoldingReportReq(eMessageCodeWealth.FAMILY_DATA.value).execute();
        spinnerAssetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                new HoldingReportReq(eMessageCodeWealth.HOLDING_REPOPRT.value).execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
    private  void displayFamilyMember(){

        try {
            spinnerFamily.setOnItemSelectedListener(null);
            String[] mStringArray = new String[familyMemberList.size()];
            int selectedIndex = 0;
            for (int i = 0; i < mStringArray.length; i++) {
                JSONObject jdata = familyMemberList.get(i);
                mStringArray[i] = jdata.getString("ClientName");
                String clientId = jdata.getString("ClientID");
                if(VenturaServerConnect.mfClientID.equalsIgnoreCase(clientId)){
                    selectedIndex = i;
                    selectedClientID = clientId;
                }
            }
            //mStringArray = assetTypeList.toArray(mStringArray);
            setSpinnerData(spinnerFamily, mStringArray);
            spinnerFamily.setSelection(selectedIndex);
            //new HoldingReportReq(eMessageCodeWealth.HOLDING_REPOPRT.value).execute();
            spinnerFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    new HoldingReportReq(eMessageCodeWealth.HOLDING_REPOPRT.value).execute();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
            if (fromScreenTag!=eMF_HoldingReportFor.REPORT.value){
                new HoldingReportReq(eMessageCodeWealth.HOLDING_REPOPRT.value).execute();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void setSpinnerData(Spinner spinner, String[] data){
        ArrayAdapter<String> categoryAdp = new ArrayAdapter<String>(homeActivity,R.layout.custom_spinner_item);
        categoryAdp.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        categoryAdp.addAll(data);
        spinner.setAdapter(categoryAdp);
    }
    private void displaData(JSONObject jsonData) {

        try {
            //double dTotPur=0,dTotCurrAmt=0,dTotAbs=0,dTotgainLoss=0;

            ArrayList<JSONObject> jList = new ArrayList<>();
            GlobalClass.log("HoldingMF : " + jsonData.toString());
            JSONObject totalJson = null;
            JSONArray jsonArr = jsonData.getJSONArray("data");
            for(int i=0;i<jsonArr.length();i++){

                try {
                    JSONObject jsonD = jsonArr.getJSONObject(i);

                    if(!jsonD.isNull("PurchaseAmt")) {
                        String schName = jsonD.getString("SchemeName");
                        if (schName.equalsIgnoreCase("total")) {
                            totalJson = jsonD;
                        } else {
                            jList.add(jsonD);
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if(jList.size()>0){
                boolean investnow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.HOLDING_HELP.name,false);
                if (!investnow) {
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.HOLDING_HELP.name,true);
                    Dialog dialog = new MutualFundHelpScreen(getActivity(),true,false);
                    dialog.show();
                }
            }
            if(mfReportAdapter != null){
                mfReportAdapter.reloadData(jList);
            }
            if(totalJson != null){
                totPur.setText(Formatter.DecimalLessIncludingComma(totalJson.getString("PurchaseAmt")));
                totcurrentAmt.setText(Formatter.DecimalLessIncludingComma(totalJson.getString("CurrentAmt")));
                totxirrval.setText(Formatter.toOneDecimal(totalJson.optDouble("XIRR")));
                totGainLoss.setText(Formatter.DecimalLessIncludingComma(totalJson.getString("GainLoss")));

                double gainLossV = Formatter.stringToDouble(totalJson.getString("GainLoss"));
                if (gainLossV < 0) {
                    totGainLoss.setTextColor(ScreenColor.getRED(getContext()));
                } else {
                    totGainLoss.setTextColor(ScreenColor.getGREEN(getContext()));
                }
            }
            else{
                totPur.setText("");
                totcurrentAmt.setText("");
                totxirrval.setText("");
                totGainLoss.setText("");
            }
            asonDate.setText(DateUtil.getCurrentDatenew());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}