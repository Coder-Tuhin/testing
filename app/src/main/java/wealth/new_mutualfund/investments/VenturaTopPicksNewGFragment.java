package wealth.new_mutualfund.investments;

import android.app.Activity;
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

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMaster;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import enums.ePrefTAG;
import enums.eScreen;
import utils.Constants;
import wealth.new_mutualfund.factSheet.FactSheetPagerFragment;
import utils.GlobalClass;
import utils.UserSession;
import view.help.MutualFundHelpScreen;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.StructDYISelected;
import wealth.new_mutualfund.newMF.OneTimeFragment;
import wealth.new_mutualfund.newMF.SIPEnterAmoutFragment;

public class VenturaTopPicksNewGFragment extends Fragment {

    private HomeActivity homeActivity;
    private View mView;
    ArrayList<String> assetTypeList;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    String divGrowthOption;
    private StructDYISelected dyiSelected;
    private boolean isAllowAsset;
    private int subCatLenght, diyRespCount=1;
    private String selectedAssetType;

    @BindView(R.id.rdgroup)
    RadioGroup assetRadioGroup;
    @BindView(R.id.expandable_list)
    ExpandableListView expandable_list;
    @BindView(R.id.diySwitch)
    Switch diySwitch;
    @BindView(R.id.title)
    TextView title;

    private eScreen selectedScreen = eScreen.PERFORMINGFUNDSEQUITY;

    public VenturaTopPicksNewGFragment(){
        selectedScreen = eScreen.NONE;
    }
    public VenturaTopPicksNewGFragment(eScreen _scr){
        selectedScreen = _scr;
    }

    public static VenturaTopPicksNewGFragment newInstance() {
        VenturaTopPicksNewGFragment fragment = new VenturaTopPicksNewGFragment();
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
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

        if (mView == null){
            mView = inflater.inflate(R.layout.ventura_top_picks_new,container,false);
            ButterKnife.bind(this, mView);

            if(VenturaServerConnect.mfClientType == eMFClientType.NONE){
                new VTPNFReq(eMessageCodeWealth.CLIENT_SESSION.value,"").execute();
            }else{
                initView();
            }
        }
        return mView;
    }

    private void initView(){
        try {
            divGrowthOption = eOptionMF.GROWTH.name;
            diySwitch.setOnCheckedChangeListener(checkChange);

            new VTPNFReq(eMessageCodeWealth.ASSETTYPE_DATA.value, "").execute();
            isAllowAsset = true;
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
            listAdapter = new ExpandableListAdapter(homeActivity, listDataHeader, listDataChild);
            expandable_list.setAdapter(listAdapter);

            dyiSelected = null;//GlobalClass.sharedPref.getDYISettigs();
            if(dyiSelected == null){
                dyiSelected = new StructDYISelected();
            }
            assetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // This will get the radiobutton that has changed in its check state
                    RadioButton checkedRadioButton = group.findViewById(checkedId);
                    boolean isChecked = checkedRadioButton.isChecked();
                    if (isChecked && isAllowAsset){
                        isAllowAsset = false;
                        dyiSelected.selectedAssetType = assetTypeList.get(checkedId-1);
                        dyiSelected.clearCategory();
                        //sendDIYReq();
                        new VTPNFReq(eMessageCodeWealth.MASTER_DATA.value, "").execute();
                    }
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private CompoundButton.OnCheckedChangeListener checkChange = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String switchText = b? eOptionMF.GROWTH.name :eOptionMF.DIVIDEND.name;
            divGrowthOption = switchText;
            sendDIYReq();
        }
    };

    private void sendDIYReq(){
        //StructDYISelected dyiSettigs = GlobalClass.sharedPref.getDYISettigs();
        //String subCategoryStr = dyiSettigs.getSelectedSubCategotyValue();
        //String subCatArr[] = subCatIdList; //subCategoryStr.split(",");
        subCatLenght = subCatIdList.size();
        for (int i=0; i<subCatLenght; i++){
            new VTPNFReq(eMessageCodeWealth.VENTURA_TOP_PICKS.value, subCatIdList.get(i)).execute();
        }
    }

    private void setAssetTpe(){
        assetTypeList = VenturaServerConnect.getAssetType();
        if(assetTypeList != null){
            int selectedID = 1;
            if(selectedScreen != eScreen.NONE){
                dyiSelected.selectedAssetType = selectedScreen.name;
            }
            for(int i=0;i<assetTypeList.size(); i++){

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

            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);

        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            String childText = (String) getChild(groupPosition, childPosition);
            if(childText != null) {


                if (convertView == null) {
                    LayoutInflater infalInflater = LayoutInflater.from(this._context);
                    convertView = infalInflater.inflate(R.layout.ventura_top_picks_expandable_child_item_2, null);
                }

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
                    //TextView seek_left_tv = convertView.findViewById(R.id.seek_left_tv);
                    //TextView seek_right_tv = convertView.findViewById(R.id.seek_right_tv);
                    (convertView.findViewById(R.id.lumpsumBtn)).setVisibility(View.GONE);
                    (convertView.findViewById(R.id.sipBtn)).setVisibility(View.GONE);

                    if (lumsum.equalsIgnoreCase("Y")) {
                        (convertView.findViewById(R.id.lumpsumBtn)).setVisibility(View.VISIBLE);
                    }
                    if (sip.equalsIgnoreCase("T")) {
                        (convertView.findViewById(R.id.sipBtn)).setVisibility(View.VISIBLE);
                    }
                    schemeNameTv.setOnClickListener(v -> {
                        try {
                            homeActivity.FragmentTransaction(FactSheetPagerFragment.newInstance(schemeCode), R.id.container_body, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    (convertView.findViewById(R.id.lumpsumBtn)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                homeActivity.FragmentTransaction(OneTimeFragment.newInstance(job.optString("SchemeCode"), job.optString("SchemeName"), ""), R.id.container_body, true);
                            } catch (Exception e) {
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
                    NumberFormat clk_formatter = new DecimalFormat("#,##,##0");
                    fundSizeTv.setText("Rs. "+clk_formatter.format(Double.parseDouble(fundSize))+" Crs");
                    if (!returnThreeYrs.equalsIgnoreCase("")) {
                        returnThreeYrsTv.setText(getReturnSign(Double.parseDouble(returnThreeYrs)) + returnThreeYrs + " p.a");
                    } else {
                        returnThreeYrsTv.setText("-");
                    }
                    DecimalFormat precision = new DecimalFormat("#,##,##0.00");
                    //// dblVariable is a number variable and not a String in this case
                    precision.setRoundingMode(RoundingMode.DOWN);
                    navValueTv.setText("Rs. " +precision.format(Double.parseDouble(navValue)));
                    //seek_left_tv.setText(lowVal);
                    //seek_right_tv.setText(highVal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            if(this._listDataChild.get(this._listDataHeader.get(groupPosition)) == null){
                return 0;
            }else {
                return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
            }
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


    private void setLineGraph(ImageView progress_iv, double returnVal){
        try {
            //int lineWidth = progress_iv.getMeasuredWidth();
            int offset = 0;
            int width = 148;
            int height = 2;

            Bitmap centerBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
            Canvas canvasNew = new Canvas(centerBitmap);
            Paint painNew = new Paint();
            if (returnVal>0){
                painNew.setColor(Color.GREEN);
            }else if (returnVal<0){
                painNew.setColor(Color.RED);
            }else {
                painNew.setColor(Color.YELLOW);
            }

            canvasNew.drawColor(painNew.getColor());

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            canvas.drawLine(offset, canvas.getHeight()/2, canvas.getWidth() - offset, canvas.getHeight() /2, paint);

            if (returnVal!=-99){
                if (returnVal>0){
                    canvas.drawBitmap(centerBitmap, (float) (bitmap.getWidth()-returnVal-3), 0, painNew);
                }else if(returnVal<0){
                    canvas.drawBitmap(centerBitmap, (float) Math.abs(returnVal)+3, 0, painNew);
                }else {
                    canvas.drawBitmap(centerBitmap, bitmap.getWidth() / 2, 0, painNew);
                }
            }
            progress_iv.setImageBitmap(bitmap);

            /*Bitmap bitmap2 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(bitmap2);
            Paint paint2 = new Paint();
            paint2.setColor(Color.WHITE);
            paint2.setStyle(Paint.Style.STROKE);
            paint.setTextSize(9);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            paint2.setAntiAlias(true);
            //canvas2.drawText(avgVal+"", 0, 0, paint2);
            //progress_iv_value.setImageBitmap(bitmap2);*/
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

            if(msgCode == eMessageCodeWealth.CLIENT_SESSION.value){
                if(UserSession.getClientResponse().isNeedAccordLogin()) {
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                        VenturaServerConnect.closeSocket();
                        if(VenturaServerConnect.connectToWealthServer(true)) {
                            JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                            if (jsonData != null) {
                                return jsonData.toString();
                            }
                        }
                    } else {
                        return clientLoginResponse.charResMsg.getValue();
                    }
                }else{
                    if(VenturaServerConnect.connectToWealthServer(true)) {
                        JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    }
                }
            }
            else if(msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value){
                assetTypeList = VenturaServerConnect.getAssetType();
                return "";
            }else if (msgCode == eMessageCodeWealth.MASTER_DATA.value) {
                try {
                    StructDYISelected dyiSettigs = dyiSelected;//GlobalClass.sharedPref.getDYISettigs();
                    String assetStr = dyiSettigs.selectedAssetType;
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name, assetStr);
                    jdata.put(eMFJsonTag.MASTERTYPE.name, eMaster.CATEGORY.name);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.MASTER_DATA.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(msgCode == eMessageCodeWealth.DOITYOURSELF_DATA.value){
                try {
                    String assetStr = "";
                    //String optionStr = "";
                    String categoryStr = "";
                    //String subCategoryStr = "";
                    String amcCodeStr = "";
                    String schCodeStr = "";

                    StructDYISelected dyiSettigs = dyiSelected;//GlobalClass.sharedPref.getDYISettigs();
                    assetStr = dyiSettigs.selectedAssetType;
                    //optionStr = dyiSettigs.selectedGrowthDevedend;
                    //subCategoryStr = dyiSettigs.getSelectedSubCategotyValue();
                    amcCodeStr = dyiSettigs.getSelectedFundHouseValue();

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name, assetStr);
                    jdata.put(eMFJsonTag.OPTION.name, divGrowthOption);
                    jdata.put(eMFJsonTag.CATEGORY.name, categoryStr);
                    jdata.put(eMFJsonTag.SUBCATEGORY.name, subCatID);
                    jdata.put(eMFJsonTag.AMCCODE.name, amcCodeStr);
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

                    StructDYISelected dyiSettigs = dyiSelected;//GlobalClass.sharedPref.getDYISettigs();
                    assetStr = dyiSettigs.selectedAssetType;
                    selectedAssetType = assetStr;
                    //optionStr = dyiSettigs.selectedGrowthDevedend;
                    //subCategoryStr = dyiSettigs.getSelectedSubCategotyValue();
                    amcCodeStr = dyiSettigs.getSelectedFundHouseValue();

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name, assetStr);
                    jdata.put(eMFJsonTag.OPTION.name, divGrowthOption);
                    jdata.put(eMFJsonTag.CATEGORY.name, subCatID); //ths is not a subCatID

                    //jdata.put(eMFJsonTag.SUBCATEGORY.name, "42");
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

            if(s != null){
                try {
                    if (msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value) {
                        setAssetTpe();
                        return;
                    }
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        displayError(err);
                        if(msgCode == eMessageCodeWealth.VENTURA_TOP_PICKS.value){
                            isAllowAsset = true;
                        }
                    }else {
                        if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                            String mfC = jsonData.getString("MFClientType");
                            VenturaServerConnect.mfClientID = jsonData.getString("MFBClientId");
                            if (mfC.equalsIgnoreCase(eMFClientType.MFI.name)) {
                                VenturaServerConnect.mfClientType = eMFClientType.MFI;
                            } else {
                                VenturaServerConnect.mfClientType = eMFClientType.MFD;
                            }

                            initView();
                        }
                        else if(msgCode == eMessageCodeWealth.DOITYOURSELF_DATA.value){
                            processDIYData(jsonData);
                        }else if(msgCode == eMessageCodeWealth.VENTURA_TOP_PICKS.value){
                            processDIYData(jsonData);
                        }if (msgCode == eMessageCodeWealth.MASTER_DATA.value) {
                            GlobalClass.log("MrGoutamD (MASTER_DATA)", jsonData.toString());
                            processMasterData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    if (s.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        displayError(s);
                    }
                }
            }
            GlobalClass.dismissdialog();
        }
    }

    private List<String> subCatIdList = new ArrayList<>();
    private void processMasterData(JSONObject jsonObject) {
        try{
            subCatIdList.clear();
            JSONArray jar = jsonObject.getJSONArray("data");
            for (int i=0; i<jar.length(); i++){
                JSONObject job = jar.getJSONObject(i);
                String value = job.getString("Value");
                subCatIdList.add(value);
            }
            sendDIYReq();
        }catch (Exception e){
            e.printStackTrace();
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

                //giving sorting only for equity as per Hars by MrGoutamD on 29092020
                if (selectedAssetType.equalsIgnoreCase("Equity")){
                    listDataHeader.addAll(getEquitySortedSubCatList(listDataChild));
                }else {
                    listDataHeader.addAll(listDataChild.keySet());
                }
                listAdapter.notifyDataSetChanged();

            }
            isAllowAsset = true;
            if(respStorege.length()==0){
                listDataHeader.clear();
                listDataChild.clear();
                listAdapter.notifyDataSetChanged();
                GlobalClass.showAlertDialog("No data found");
            }else{
                boolean investnow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.INVESTNOW_HELP.name,false);
                if(!investnow) {
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.INVESTNOW_HELP.name, true);
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
        if(!err.equalsIgnoreCase("")) {
            GlobalClass.showAlertDialog(err);
        }
    }

    private List<String> getEquitySortedSubCatList(HashMap<String, List<String>> alldata){

        List<String> subCategoryList = new ArrayList<>();
        for(int i=0;i<alldata.size();i++){
            subCategoryList.add("cat"+i);
        }
        /*
        subCategoryList.add("Large Cap");
        subCategoryList.add("Large & Mid Cap");
        subCategoryList.add("Mid Cap");
        subCategoryList.add("Small Cap");
        subCategoryList.add("Multi Cap");
        subCategoryList.add("Equity - Tax Planning (ELSS)");
        subCategoryList.add("Dividend Yield");
        subCategoryList.add("International");
        subCategoryList.add("Index");
        subCategoryList.add("MNC");
        subCategoryList.add("Value");
        subCategoryList.add("Banking & Financial Services");
        subCategoryList.add("FMCG");
        subCategoryList.add("IT");
        subCategoryList.add("Pharma & Healthcare");
        subCategoryList.add("Infrastructure");
        subCategoryList.add("Contra");*/

        ArrayList<String> subCatListFromApi = new ArrayList<>();
        subCatListFromApi.addAll(alldata.keySet());

        for(String subCat : subCatListFromApi){
            if(subCat.toLowerCase().contentEquals("Large Cap".toLowerCase())){
                subCategoryList.set(0,subCat);
            }else if(subCat.toLowerCase().contentEquals("Large & Mid Cap".toLowerCase())){
                subCategoryList.set(1,subCat);
            }else if(subCat.toLowerCase().contentEquals("Mid Cap".toLowerCase())){
                subCategoryList.set(2,subCat);
            }else if(subCat.toLowerCase().contentEquals("Small Cap".toLowerCase())){
                subCategoryList.set(3,subCat);
            }else if(subCat.toLowerCase().contentEquals("Multi Cap".toLowerCase())){
                subCategoryList.set(4,subCat);
            }else if(subCat.toLowerCase().contentEquals("Tax Saving (ELSS)".toLowerCase())){
                subCategoryList.set(5,subCat);
            }else if(subCat.toLowerCase().contentEquals("Dividend Yield".toLowerCase())){
                subCategoryList.set(6,subCat);
            }else if(subCat.toLowerCase().contentEquals("International".toLowerCase())){
                subCategoryList.set(7,subCat);
            }else if(subCat.toLowerCase().contentEquals("Index".toLowerCase())){
                subCategoryList.set(8,subCat);
            }else if(subCat.toLowerCase().contentEquals("MNC".toLowerCase())){
                subCategoryList.set(9,subCat);
            }else if(subCat.toLowerCase().contentEquals("Value".toLowerCase())){
                subCategoryList.set(10,subCat);
            }else if(subCat.toLowerCase().contentEquals("Banking & Financial Services".toLowerCase())){
                subCategoryList.set(11,subCat);
            }else if(subCat.toLowerCase().contentEquals("FMCG".toLowerCase())){
                subCategoryList.set(12,subCat);
            }else if(subCat.toLowerCase().contentEquals("IT".toLowerCase())){
                subCategoryList.set(13,subCat);
            }else if(subCat.toLowerCase().contentEquals("Pharma & Healthcare".toLowerCase())){
                subCategoryList.set(14,subCat);
            }else if(subCat.toLowerCase().contentEquals("Infrastructure".toLowerCase())){
                subCategoryList.set(15,subCat);
            }else if(subCat.toLowerCase().contentEquals("Contra".toLowerCase())){
                subCategoryList.set(16,subCat);
            }else{
                subCategoryList.add(subCat);
            }
        }

        List<String> finalsubCategoryList = new ArrayList<>();
        for(String subCat : subCategoryList){
            if(subCatListFromApi.contains(subCat)){
                finalsubCategoryList.add(subCat);
            }
        }
        return finalsubCategoryList;
    }
}