package wealth.new_mutualfund.newMF;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import enums.ePrefTAG;
import utils.GlobalClass;
import utils.StaticVariables;
import utils.UserSession;
import view.help.MutualFundHelpScreen;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.StructDYISelected;
import wealth.new_mutualfund.factSheet.FactSheetPagerFragment;

import static java.lang.Math.abs;

public class InvestInTopCompaniesFragmentNew extends Fragment {

    public static InvestInTopCompaniesFragmentNew newInstance(String tag, String subcatystr,String header) {
        InvestInTopCompaniesFragmentNew itc =  new InvestInTopCompaniesFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1,tag);
        bundle.putString(StaticVariables.ARG_2,subcatystr);
        bundle.putString(StaticVariables.ARG_3,header);
        itc.setArguments(bundle);
        return itc;
    }
    private HomeActivity homeActivity;
    private View mView;
    ArrayList<String> assetTypeList;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    String divGrowthOption;
    private StructDYISelected dyiSelected;
    private boolean isAllowAsset, isDividend;
    private int subCatLenght, diyRespCount=1;
    NumberFormat clk_formatter;
    String tag, header;

    @BindView(R.id.expandable_list)
    ExpandableListView expandable_list;
    @BindView(R.id.diySwitch)
    Switch diySwitch;
    @BindView(R.id.tv_heder)
    TextView tv_heder;
    @BindView(R.id.Date)
    TextView Date;



    public static InvestInTopCompaniesFragmentNew newInstance() {
        InvestInTopCompaniesFragmentNew fragment = new InvestInTopCompaniesFragmentNew();
        return fragment;
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
        if (mView == null) {
            mView = inflater.inflate(R.layout.investintopcompanies_new,container,false);
            ButterKnife.bind(this, mView);
            Bundle bundle = getArguments();
            tag = bundle.getString(StaticVariables.ARG_1);
            header = bundle.getString(StaticVariables.ARG_3);
            tv_heder.setText(header);
            clk_formatter = new DecimalFormat("#,##,##0.##");
            isAllowAsset = false;
            divGrowthOption = eOptionMF.GROWTH.name;
            diySwitch.setOnCheckedChangeListener(checkChange);
            new VTPNFReq(eMessageCodeWealth.ASSETTYPE_DATA.value, "").execute();

            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
            listAdapter = new ExpandableListAdapter(homeActivity, listDataHeader, listDataChild);
            expandable_list.setAdapter(listAdapter);

            dyiSelected = VenturaApplication.getPreference().getDYISettigs();
            if(dyiSelected == null){
                dyiSelected = new StructDYISelected();
            }

            sendDIYReq();
        }
        return mView;
    }
    private CompoundButton.OnCheckedChangeListener checkChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String switchText = b? eOptionMF.GROWTH.name :eOptionMF.DIVIDEND.name;
            divGrowthOption = switchText;
            //new VTPNFReq(eMessageCodeWealth.DOITYOURSELF_DATA.value).execute();
            sendDIYReq();
        }
    };
    private void sendDIYReq() {
        Bundle bundle = getArguments();
        String subCategoryStr = bundle.getString(StaticVariables.ARG_2);

        String subCatArr[] = subCategoryStr.split(",");
        subCatLenght = subCatArr.length;

        for (int i=0; i<subCatArr.length; i++) {
            new VTPNFReq(eMessageCodeWealth.DOITYOURSELF_DATA.value, subCatArr[i]).execute();
        }
    }

    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader;
        private HashMap<String, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            String childText = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = LayoutInflater.from(this._context);
                convertView = infalInflater.inflate(R.layout.item_investincompanies, null);
            }
            try {
                JSONObject job = new JSONObject(childText);
                String schemeName = job.getString("SchemeName");
                String schemeCode = job.getString("SchemeCode");
                String fundSize = job.getString("AUM");
                String returnThreeYrs = job.getString("RET3YEAR");
                String returnFiveYrs = job.getString("RET5YEAR");
                String navValue = job.getString("CurrNAV");
                final String lumsum = job.getString("PurchaseAllowed");



                TextView schemeNameTv = convertView.findViewById(R.id.tv_schemecode);
                TextView tv_3yrs = convertView.findViewById(R.id.tv_3yrs);
                TextView tv_5yrs = convertView.findViewById(R.id.tv_5yrs);
                TextView tv_nav = convertView.findViewById(R.id.tv_nav);
                TextView tv_fundsize = convertView.findViewById(R.id.tv_fundsize);
                TextView tv_invest = convertView.findViewById(R.id.tv_invest);
                if (lumsum.equalsIgnoreCase("Y")) {
                    tv_invest.setVisibility(View.VISIBLE);
                }else{
                    tv_invest.setVisibility(View.GONE);
                }





                schemeNameTv.setOnClickListener(v -> {
                    try{
                        homeActivity.FragmentTransaction(FactSheetPagerFragment.newInstance(schemeCode), R.id.container_body, true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });

                tv_invest.setOnClickListener(v -> {
                    try{
                        if(tag.equalsIgnoreCase("taxsaving") || tag.equalsIgnoreCase("parkfunds")){

                            homeActivity.FragmentTransaction(OneTimeFragment.newInstance(schemeCode, schemeName, tag), R.id.container_body, true);




                        }else {
                            homeActivity.FragmentTransaction(ChooseSIPoptionFragment.newInstance(schemeCode,schemeName), R.id.container_body, true);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });



                schemeNameTv.setText(schemeName);
                tv_fundsize.setText(clk_formatter.format(Double.parseDouble(fundSize))+" Crs");
                if (!returnThreeYrs.equalsIgnoreCase("")){
                    tv_3yrs.setText(returnThreeYrs);
                }else {
                    tv_3yrs.setText("-");
                }
                if (!returnFiveYrs.equalsIgnoreCase("")){
                    tv_5yrs.setText(returnFiveYrs);
                }else {
                    tv_5yrs.setText("-");
                }

                tv_nav.setText(navValue);

            }catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }

        private String getReturnSign(double returnThreeYrsVal){
            if (returnThreeYrsVal>0)
                return "+";
            else {
                return "";
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerText = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater =  LayoutInflater.from(this._context);
                convertView = infalInflater.inflate(R.layout.ventura_top_picks_expandable_parent_item, null);
            }
            TextView subCategoryTV = convertView.findViewById(R.id.subCategoryTV);
            subCategoryTV.setText(headerText);
            expandable_list.expandGroup(groupPosition);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    class VTPNFReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String subCatID;

        VTPNFReq(int mCode, String subCatId) {
            this.msgCode = mCode;
            this.subCatID = subCatId;
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
            }else if(msgCode == eMessageCodeWealth.DOITYOURSELF_DATA.value){
                try {
                    String assetStr ="";
                    String categoryStr="";
                    String subCategoryStr ="";
                    String amcCodeStr ="";
                    String schCodeStr ="";
                    //String allFundHouseCodes = VenturaServerConnect.getAllFundHouseCodes();
                    amcCodeStr = "400004,400040,400005,400001,400034,400006,400009,400035,400012,400013,400014,400015,400043,400028,400047,400048,400021,400017,400019,400007,400020,400054,400033,400042,400041,400025,400044,400049,400023,400010,400024,400027,400052,400050,400029,400030,400031,400057,400032,400055";

                    if(tag=="60-70"){
                        assetStr = "Hybrid";
                    }else if(tag=="35-65"){
                        assetStr = "Hybrid";
                    }else if(tag=="upto35"){
                        assetStr = "Hybrid";
                    }else if(tag.equalsIgnoreCase("65-80equity")){
                        assetStr = "Hybrid";
                    }else if(tag.equalsIgnoreCase("3565equity")){
                        assetStr = "Hybrid";
                    }else if(header.equalsIgnoreCase("1-15 days")){
                        assetStr = "Liquid";
                    }
                    else if(header.equalsIgnoreCase("15 days - 3 months")){
                        assetStr = "Liquid";
                    }
                    else if(header.equalsIgnoreCase("More than 3 months")){
                        assetStr = "Liquid";
                    }
                    else if(header.equalsIgnoreCase("Fund of Funds")){
                        assetStr = "Others";
                    }else if(header.equalsIgnoreCase("Corporate Debt Fund")){
                        assetStr = "Debt";
                    }else if(header.equalsIgnoreCase(getResources().getString(R.string.bankingadnpsu))){
                        assetStr = "Debt";
                    }else if(header.equalsIgnoreCase("Glit Funds")){
                        assetStr = "Debt";
                    }else if(header.equalsIgnoreCase("Conservative Hybrid Fund")){
                        assetStr = "Hybrid";
                    }else if(header.equalsIgnoreCase("Equity Savings Fund")){
                        assetStr = "Hybrid";
                    }
                    else if(header.equalsIgnoreCase("Low and Short Duration Fund")){
                        assetStr = "Debt";
                    }else if(header.equalsIgnoreCase("Money Market Fund")){
                        assetStr = "Debt";
                    }else {
                        assetStr = "Equity";
                    }


                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name, assetStr);
                    jdata.put(eMFJsonTag.OPTION.name, divGrowthOption);
                    jdata.put(eMFJsonTag.CATEGORY.name, categoryStr);

                    jdata.put(eMFJsonTag.SUBCATEGORY.name, subCatID); //subCatID


                    jdata.put(eMFJsonTag.AMCCODE.name, amcCodeStr);
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schCodeStr);
                    jdata.put("SortOn", "RET3YEAR");

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.DOITYOURSELF_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } catch (Exception ex) {
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
                        // setAssetTpe();
                    }else {
                        JSONObject jsonData = new JSONObject(s);
                        if (!jsonData.isNull("error")) {
                            String err = jsonData.getString("error");
                            displayError(err);
                        } else {
                            if (msgCode == eMessageCodeWealth.DOITYOURSELF_DATA.value) {
                                processDIYData(jsonData);
                            }
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        }
    }
    String Datenav = "";
    private JSONArray respStorege = new JSONArray();
    private void processDIYData(JSONObject jsonData){
        try {
            JSONArray jarData = jsonData.getJSONArray("data");
            String minReturn = jsonData.getString("MinReturn");
            String maxReturn = jsonData.getString("MaxReturn");
            if (jarData.length()>0){
                for (int i=0; i<jarData.length(); i++){
                    JSONObject row = jarData.getJSONObject(i);
                    row.put("MinReturn", minReturn);
                    row.put("MaxReturn", maxReturn);
                    respStorege.put(row);
                }

            }
            if (diyRespCount<subCatLenght){
                diyRespCount += 1;
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        try{
            listDataHeader.clear();
            listDataChild.clear();
            //GlobalClass.log("DIY Data : " + jsonData.toString());
            //JSONArray jarData = jsonData.getJSONArray("data");
            if(respStorege.length() > 0) {
                for(int i=0;i<respStorege.length();i++){
                    JSONObject row = respStorege.getJSONObject(i);
                    String subCat = row.getString("SubCategory");
                    Datenav = row.optString("NAVDate");
                    if (!listDataChild.containsKey(subCat)){
                        List<String> list = new ArrayList<>();
                        list.add(row.toString());
                        listDataChild.put(subCat, list);
                    }else {
                        List<String> list = listDataChild.get(subCat);
                        list.add(row.toString());
                        listDataChild.put(subCat, list);
                    }
                }
                Date.setText("*Data as on " +Datenav);
                listDataHeader.addAll(listDataChild.keySet());
                listAdapter.notifyDataSetChanged();
                isAllowAsset = true;
            }

            if(respStorege.length()==0) {
                listDataHeader.clear();
                listDataChild.clear();
                listAdapter.notifyDataSetChanged();
                GlobalClass.showAlertDialog("No data found");
            }else {
                boolean investnow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.DIY_HELP.name,false);
                if(!investnow) {
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.DIY_HELP.name, true);
                    Dialog dialog = new MutualFundHelpScreen(getActivity(),false,false);
                    dialog.show();
                }
            }
            respStorege = new JSONArray();
            diyRespCount = 1;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void displayError(String err) {
        GlobalClass.showAlertDialog(err);
    }

}
