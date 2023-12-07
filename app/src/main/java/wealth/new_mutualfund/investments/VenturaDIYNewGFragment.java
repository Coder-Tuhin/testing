package wealth.new_mutualfund.investments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
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
import wealth.new_mutualfund.factSheet.FactSheetPagerFragment;
import utils.GlobalClass;
import utils.UserSession;
import view.help.MutualFundHelpScreen;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.StructDYISelected;
import wealth.new_mutualfund.newMF.OneTimeFragment;
import wealth.new_mutualfund.newMF.SIPEnterAmoutFragment;

import static java.lang.Math.abs;

public class VenturaDIYNewGFragment extends Fragment {
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

    @BindView(R.id.rdgroup)
    RadioGroup assetRadioGroup;
    @BindView(R.id.expandable_list)
    ExpandableListView expandable_list;
    @BindView(R.id.diySwitch)
    Switch diySwitch;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.filter)
    ImageView filter;

    public static VenturaDIYNewGFragment newInstance() {
        VenturaDIYNewGFragment fragment = new VenturaDIYNewGFragment();

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
        if (mView == null){
            mView = inflater.inflate(R.layout.ventura_diy_new_g_screen,container,false);
            ButterKnife.bind(this, mView);

            clk_formatter = new DecimalFormat("#,##,##0");
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
            assetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // This will get the radiobutton that has changed in its check state
                    RadioButton checkedRadioButton = group.findViewById(checkedId);
                    boolean isChecked = checkedRadioButton.isChecked();
                    if (isChecked & isAllowAsset){
                        dyiSelected.selectedAssetType = assetTypeList.get(checkedId-1);
                        dyiSelected.clearCategory();

                        sendDIYReq();
                    }
                }
            });

            filter.setOnClickListener(view1 -> {
                //homeActivity.FragmentTransaction(DIYFilter.newInstance(), R.id.mfBody, true);
                homeActivity.FragmentTransaction(DIYFilter.newInstance(), R.id.container_body, true);
            });
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

    private void sendDIYReq(){
        StructDYISelected dyiSettigs = VenturaApplication.getPreference().getDYISettigs();
        String subCategoryStr = dyiSettigs.getSelectedSubCategotyValue();
        String subCatArr[] = subCategoryStr.split(",");
        subCatLenght = subCatArr.length;
        for (int i=0; i<subCatArr.length; i++){
            new VTPNFReq(eMessageCodeWealth.DOITYOURSELF_DATA.value, subCatArr[i]).execute();
        }
    }

    private void setAssetTpe(){

        assetTypeList = VenturaServerConnect.getAssetType();
        if(assetTypeList != null){
            int selectedID = 1;
            for(int i=0;i<assetTypeList.size();i++){

                String asset = assetTypeList.get(i);
                if(dyiSelected.selectedAssetType.equalsIgnoreCase(asset)){
                    selectedID = i+1;
                }
                RadioButton rdb = new RadioButton(getContext());
                rdb.setText(asset);
                rdb.setId(i+1);
                rdb.setButtonDrawable(android.R.color.transparent);
                rdb.setGravity(Gravity.CENTER);
                rdb.setBackground(ContextCompat.getDrawable(homeActivity, R.drawable.toggle_widget_background));
                rdb.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
                param.setMargins(6, 0, 6, 0);
                rdb.setLayoutParams(param);
                assetRadioGroup.addView(rdb);
            }
            assetRadioGroup.check(selectedID);
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
                convertView = infalInflater.inflate(R.layout.ventura_top_picks_expandable_child_item, null);
            }

            ImageView progress_iv = convertView.findViewById(R.id.progress_iv);

            try {
                JSONObject job = new JSONObject(childText);
                String schemeName = job.getString("SchemeName");
                String schemeCode = job.getString("SchemeCode");
                String fundSize = job.getString("AUM");
                String returnThreeYrs = job.getString("RET3YEAR");
                String navValue = job.getString("CurrNAV");
                String highVal = job.getString("MaxReturn");
                String lowVal = job.getString("MinReturn");
                final String lumsum = job.getString("PurchaseAllowed");
                final String sip = job.getString("SIP");

                LinearLayout return_layout = convertView.findViewById(R.id.return_layout);
                TextView schemeNameTv = convertView.findViewById(R.id.schemeNameTv);
                TextView fundSizeTv = convertView.findViewById(R.id.fundSizeTv);
                TextView returnThreeYrsTv = convertView.findViewById(R.id.returnThreeYrsTv);
                TextView navValueTv = convertView.findViewById(R.id.navValueTv);
                TextView seek_left_tv = convertView.findViewById(R.id.seek_left_tv);
                TextView seek_right_tv = convertView.findViewById(R.id.seek_right_tv);

                (convertView.findViewById(R.id.lumpsumBtn)).setVisibility(View.GONE);
                (convertView.findViewById(R.id.sipBtn)).setVisibility(View.GONE);

                if (lumsum.equalsIgnoreCase("Y")) {
                    (convertView.findViewById(R.id.lumpsumBtn)).setVisibility(View.VISIBLE);


                }
                if (sip.equalsIgnoreCase("T")) {
                    (convertView.findViewById(R.id.sipBtn)).setVisibility(View.VISIBLE);


                }


                schemeNameTv.setOnClickListener(v -> {
                    try{
                        homeActivity.FragmentTransaction(FactSheetPagerFragment.newInstance(schemeCode), R.id.container_body, true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });

                setLineGraph(progress_iv, highVal.equalsIgnoreCase("")?0:Float.parseFloat(highVal),
                        lowVal.equalsIgnoreCase("")?0:Float.parseFloat(lowVal), returnThreeYrs.equalsIgnoreCase("")?0:Float.parseFloat(returnThreeYrs));
                /*setLineGraph(progress_iv, highVal.equalsIgnoreCase("")?0:Float.parseFloat(highVal),
                        lowVal.equalsIgnoreCase("")?0:Float.parseFloat(lowVal), (float)0.1);*/

                (convertView.findViewById(R.id.lumpsumBtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            homeActivity.FragmentTransaction(OneTimeFragment.newInstance(job.optString("SchemeCode"), job.optString("SchemeName"), ""), R.id.container_body, true);


                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
                (convertView.findViewById(R.id.sipBtn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        homeActivity.FragmentTransaction(SIPEnterAmoutFragment.newInstance(job.optString("SchemeCode"),job.optString("SchemeName")), R.id.container_body, true);


                    }
                });
                return_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String returnOneYrs = job.getString("RET1YEAR");
                            String returnFiveYrs = job.getString("RET5YEAR");
                            showReturnWindow(returnOneYrs, returnThreeYrs, returnFiveYrs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                schemeNameTv.setText(schemeName);
                fundSizeTv.setText("Rs. "+clk_formatter.format(Double.parseDouble(fundSize))+" Crs");
                if (!returnThreeYrs.equalsIgnoreCase("")){
                    returnThreeYrsTv.setText(getReturnSign(Double.parseDouble(returnThreeYrs))+returnThreeYrs +" p.a");
                }else {
                    returnThreeYrsTv.setText("-");
                }
                DecimalFormat precision = new DecimalFormat("#,##,##0.00");
                //// dblVariable is a number variable and not a String in this case
                precision.setRoundingMode(RoundingMode.DOWN);
                navValueTv.setText(precision.format(Double.parseDouble(navValue)));
                //navValueTv.setText(navValue);
                seek_left_tv.setText(lowVal);
                seek_right_tv.setText(highVal);


            }catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }

        private void showReturnWindow(String returnOne, String returnTwo, String returnThree){
            try {
                final Dialog dialog = new Dialog(getContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.top_picks_returns_window);

                TextView closeAlert = dialog.findViewById(R.id.closeTv);
                TextView return1Y = dialog.findViewById(R.id.return1Y);
                TextView return2Y = dialog.findViewById(R.id.return2Y);
                TextView return3Y = dialog.findViewById(R.id.return3Y);

                return1Y.setText(returnOne);
                return2Y.setText(returnTwo);
                return3Y.setText(returnThree);
                closeAlert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }
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

    private void setLineGraph(ImageView progress_iv, float highVal, float lowVal, float returnValue){
        try {
            int ballRadius = 10;
            ViewTreeObserver vto = progress_iv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        progress_iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int width  = progress_iv.getMeasuredWidth();
                        int height = progress_iv.getMeasuredHeight();
                        GlobalClass.log("Goutam", "H: "+height+", W:"+width);

                        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        circlePaint.setColor(Color.GREEN);

                        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        rectPaint.setColor(Color.WHITE);

                        float fraction;
                        if (highVal == 0 && lowVal == 0){
                            fraction = 0;
                        }else {
                            fraction = (width/(highVal+abs(lowVal)));
                        }

                        if (lowVal==0){

                        }
                        float cirWid = (float) ((float) width*(abs(lowVal/(abs(lowVal)+highVal)))+fraction*abs(returnValue)-10); //when return is +ve
                        if (returnValue<0){
                            cirWid = (float) ((float) width*(abs(lowVal/(abs(lowVal)+highVal)))-fraction*abs(returnValue)+10);
                        }

                        float cirHei = (float) (height*0.5);

                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);

                        GlobalClass.log("Goutam", "cirHei: "+cirHei+", cirWid:"+cirWid);
                        canvas.drawRect(0, cirHei-1, width, cirHei+1, rectPaint);
                        if (returnValue != 0){
                            if (cirWid<ballRadius){
                                cirWid = ballRadius;
                            }
                            if (cirWid>=width){
                                cirWid = width - ballRadius;
                            }
                            canvas.drawCircle(cirWid, cirHei, ballRadius, circlePaint);
                        }
                        progress_iv.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    class VTPNFReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String subCatID;

        VTPNFReq(int mCode, String subCatId){
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
                    String assetStr = "";
                    //String optionStr = "";
                    String categoryStr = "";
                    //String subCategoryStr = "";
                    String amcCodeStr = "";
                    String schCodeStr = "";
                    String allFundHouseCodes = VenturaServerConnect.getAllFundHouseCodes();
                    StructDYISelected dyiSettigs = dyiSelected;//VenturaApplication.getPreference().getDYISettigs();
                    assetStr = dyiSettigs.selectedAssetType;
                    //optionStr = dyiSettigs.selectedGrowthDevedend;
                    //subCategoryStr = dyiSettigs.getSelectedSubCategotyValue();
                    amcCodeStr = dyiSettigs.getSelectedFundHouseValue();

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name, assetStr);
                    jdata.put(eMFJsonTag.OPTION.name, divGrowthOption);
                    jdata.put(eMFJsonTag.CATEGORY.name, categoryStr);
                    jdata.put(eMFJsonTag.SUBCATEGORY.name, subCatID); //subCatID
                    jdata.put(eMFJsonTag.AMCCODE.name, amcCodeStr.equalsIgnoreCase("")?allFundHouseCodes:amcCodeStr);
                    jdata.put(eMFJsonTag.SCHEMECODE.name, schCodeStr);
                    jdata.put("SortOn", "RET3YEAR");

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.DOITYOURSELF_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            } else if (msgCode == eMessageCodeWealth.VENTURA_TOP_PICKS.value) {
                try {
                    String assetStr = "";
                    //String optionStr = "";
                    String categoryStr = "";
                    //String subCategoryStr = "";
                    String amcCodeStr = "";
                    String schCodeStr = "";

                    StructDYISelected dyiSettigs = dyiSelected;//VenturaApplication.getPreference().getDYISettigs();
                    assetStr = dyiSettigs.selectedAssetType;
                    //optionStr = dyiSettigs.selectedGrowthDevedend;
                    //subCategoryStr = dyiSettigs.getSelectedSubCategotyValue();
                    amcCodeStr = dyiSettigs.getSelectedFundHouseValue();

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name, assetStr);
                    jdata.put(eMFJsonTag.OPTION.name, divGrowthOption);
                    jdata.put(eMFJsonTag.CATEGORY.name, "7"); //categoryStr

                    // jdata.put(eMFJsonTag.SUBCATEGORY.name, subCatID);
                    // jdata.put(eMFJsonTag.AMCCODE.name, amcCodeStr);
                    jdata.put("SortOn", "RET3YEAR");

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.VENTURA_TOP_PICKS.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception ex){
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
                        setAssetTpe();
                    }
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        displayError(err);
                    }else {
                        if(msgCode == eMessageCodeWealth.DOITYOURSELF_DATA.value){
                            processDIYData(jsonData);
                        }else if(msgCode == eMessageCodeWealth.VENTURA_TOP_PICKS.value){
                            //processTopPicksData(jsonData);
                            processDIYData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

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
        }catch (Exception e){
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
                listDataHeader.addAll(listDataChild.keySet());
                listAdapter.notifyDataSetChanged();
                isAllowAsset = true;
            }

            if(respStorege.length()==0){
                listDataHeader.clear();
                listDataChild.clear();
                listAdapter.notifyDataSetChanged();
                GlobalClass.showAlertDialog("No data found");
            }else{
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

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
}