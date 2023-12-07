package wealth.new_mutualfund.investments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMaster;
import enums.eMessageCodeWealth;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.Structure.StructDYISelected;


public class DIYFilter extends Fragment {
    private HomeActivity homeActivity;
    private View mView;

    ArrayList<String> assetTypeList;

    @BindView(R.id.fundHouserecyclerView)
    RecyclerView fundhouserecyclerView;

    @BindView(R.id.categoryrecyclerView)
    RecyclerView categoryrecyclerView;

    @BindView(R.id.rdgroup)
    RadioGroup assetRadioGroup;

    @BindView(R.id.submit)
    Button submit;

    @BindView(R.id.reset)
    Button reset;

    private DIYFilterFundHouseAdapter fundHouseAdapter;
    private DIYFilterCategoryAdapter categoryAdapter;

    private StructDYISelected dyiSelected;

    public DIYFilter() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static DIYFilter newInstance() {
        DIYFilter fragment = new DIYFilter();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeActivity = (HomeActivity) getActivity();
        try {
            ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
            ((Activity) getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_diyfilter, container, false);
            ButterKnife.bind(this,mView);
        }

        if(VenturaServerConnect.mfClientType == eMFClientType.NONE){
            new DIYFilterReq(eMessageCodeWealth.CLIENT_SESSION.value,eMaster.CATEGORY).execute();
        }else{
            initView();
        }
        return mView;
    }

    private void initView(){

        dyiSelected = VenturaApplication.getPreference().getDYISettigs();
        if(dyiSelected == null){
            dyiSelected = new StructDYISelected();
        }
        fundHouseAdapter = new DIYFilterFundHouseAdapter();
        fundhouserecyclerView.setAdapter(fundHouseAdapter);

        categoryAdapter = new DIYFilterCategoryAdapter();
        categoryrecyclerView.setAdapter(categoryAdapter);
        new DIYFilterReq(eMessageCodeWealth.ASSETTYPE_DATA.value, eMaster.SUBCATEGORY).execute();
        assetRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked){
                    dyiSelected.selectedAssetType = assetTypeList.get(checkedId-1);
                    dyiSelected.clearCategory();
                    new DIYFilterReq(eMessageCodeWealth.MASTER_DATA.value, eMaster.SUBCATEGORY).execute();
                }
            }
        });
        ArrayList<JSONObject> fundhouseList = VenturaServerConnect.getFundHouse();
        if(fundhouseList != null && fundhouseList.size()>0){
            fundHouseAdapter.reloadData(fundhouseList);
        }else{
            new DIYFilterReq(eMessageCodeWealth.MASTER_DATA.value, eMaster.FUNDHOUSE).execute();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VenturaApplication.getPreference().setDYISettings(dyiSelected);
                /*
                // Changing as per Hars
                if(dyiSelected.selectedFund.size() == 0){
                    GlobalClass.showAlertDialog("Please select fund houses");
                }else*/
                if(dyiSelected.selectedCategory.size() == 0){
                    GlobalClass.showAlertDialog("Please select sub categories");
                }else {

                    //DIY :: LIVE
                    //homeActivity.onFragmentBack();

                    //NEW dEvelopement
                    homeActivity.FragmentTransaction(VenturaDIYNewGFragment.newInstance(), R.id.container_body, true);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDiySelection();
            }
        });

        resetDiySelection();
    }
    private void resetDiySelection(){
        dyiSelected.clearCategory();
        dyiSelected.clearFund();
        VenturaApplication.getPreference().setDYISettings(dyiSelected);

        fundHouseAdapter.reloadData();
        categoryAdapter.reloadData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @SuppressLint("ResourceType")
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
                rdb.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.toggle_widget_background));
                rdb.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
                param.setMargins(4, 0, 4, 0);
                rdb.setLayoutParams(param);
                assetRadioGroup.addView(rdb);
            }
            assetRadioGroup.check(selectedID);
            new DIYFilterReq(eMessageCodeWealth.MASTER_DATA.value, eMaster.SUBCATEGORY).execute();
        }
    }

    class DIYFilterReq extends AsyncTask<String, Void, String> {
        int msgCode;
        eMaster masterType;

        DIYFilterReq(int mCode, eMaster _masterType){
            this.msgCode = mCode;
            this.masterType = _masterType;
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
            else if(msgCode == eMessageCodeWealth.MASTER_DATA.value){
                try {
                    JSONObject jsonData;
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.ASSET.name,dyiSelected.selectedAssetType);
                    jdata.put(eMFJsonTag.CATEGORY.name,"");
                    jdata.put(eMFJsonTag.MASTERTYPE.name, masterType.name);
                    jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.MASTER_DATA.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.ASSETTYPE_DATA.value){
                assetTypeList = VenturaServerConnect.getAssetType();
                return "";
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
                        else if(msgCode == eMessageCodeWealth.MASTER_DATA.value) {
                            if(masterType == eMaster.SUBCATEGORY){
                                displayCategorData(jsonData);
                            }else if(masterType == eMaster.FUNDHOUSE){
                                displayFundHouseData(jsonData);
                            }
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
        }
    }

    private void displayError(String err){
        if(!err.equalsIgnoreCase("")) {
            GlobalClass.showAlertDialog(err);
        }
    }
    private  void displayCategorData(JSONObject jsonData){
        try{
            GlobalClass.log("GetSubCategory : " + jsonData.toString());
            JSONArray catlist = jsonData.getJSONArray("data");
            ArrayList<JSONObject> categoryList= new ArrayList<>();
            for (int i = 0; i < catlist.length(); i++) {
                JSONObject jdata = catlist.getJSONObject(i);
                categoryList.add(jdata);
            }
            if(categoryAdapter != null){
                categoryAdapter.reloadData(categoryList);
            }
        }catch (Exception ex){ex.printStackTrace();}
    }

    private  void displayFundHouseData(JSONObject jsonData){
        try {
            GlobalClass.log("GetFundHouse : " + jsonData.toString());
            JSONArray catlist = jsonData.getJSONArray("data");
            ArrayList<JSONObject> fundhouseList = new ArrayList<>();
            for (int i = 0; i < catlist.length(); i++) {
                JSONObject jdata = catlist.getJSONObject(i);
                fundhouseList.add(jdata);
            }
            VenturaServerConnect.setFundHouse(fundhouseList);
            if(fundHouseAdapter != null){
                fundHouseAdapter.reloadData(fundhouseList);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public class DIYFilterFundHouseAdapter extends RecyclerView.Adapter<DIYFilterFundHouseAdapter.MyViewHolder> {
        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public DIYFilterFundHouseAdapter() {
            inflater = LayoutInflater.from(homeActivity);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }
        public void reloadData(){
            this.notifyDataSetChanged();
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.listfundhouse, parent, false);
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
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.fundchkbox)
            AppCompatCheckBox fundChkBox;

            private JSONObject jData;
            public void  reloadData(JSONObject jsonD){
                this.jData = jsonD;
                try {
                    String fundName = jData.getString("Text");
                    fundChkBox.setText(fundName);
                    if(dyiSelected.isFundCheck(fundName)){
                        fundChkBox.setChecked(true);
                    }else{
                        fundChkBox.setChecked(false);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                fundChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            boolean isadd = dyiSelected.addFund(jData);
                            if(!isadd){
                                GlobalClass.showAlertDialog("You can select upto 5 fund houses");
                                fundChkBox.setChecked(false);
                            }
                        }else{
                            dyiSelected.removeFund(jData);
                        }
                    }
                });
            }
        }
    }

    public class DIYFilterCategoryAdapter extends RecyclerView.Adapter<DIYFilterCategoryAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        public DIYFilterCategoryAdapter() {
            inflater = LayoutInflater.from(homeActivity);
            mList = new ArrayList<>();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }
        public void reloadData(){
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.listfundhouse, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                JSONObject jData = mList.get(position);
                holder.reloadData(jData);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.fundchkbox)
            AppCompatCheckBox fundChkBox;

            private JSONObject jData;
            public void  reloadData(JSONObject jsonD){
                this.jData = jsonD;
                try {
                    String fundName = jData.getString("Text");
                    fundChkBox.setText(fundName);
                    if(dyiSelected.isCategoryCheck(fundName)){
                        fundChkBox.setChecked(true);
                    }else{
                        fundChkBox.setChecked(false);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                fundChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            boolean isAdd = dyiSelected.addCategory(jData);
                            if(!isAdd){
                                GlobalClass.showAlertDialog("You can select upto 2 sub categories");
                                fundChkBox.setChecked(false);
                            }
                        }else{
                            dyiSelected.removeCategory(jData);
                        }
                    }
                });
            }
        }
    }
}
