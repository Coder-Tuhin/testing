package view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.List;

import Structure.Other.StructBuySell;
import Structure.Request.BC.SearchScript;
import Structure.Response.BC.SearchDetails;
import Structure.Response.BC.SearchdetailsResp;
import Structure.Response.Group.GroupAddDelResp;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.Scrip.SearchScripResp;
import Structure.Response.Scrip.SearchScripRow;
import connection.ReqSent;
import connection.SendDataToBCServer;
import enums.eExch;
import enums.eExpiry;
import enums.eExpiryType;
import enums.eForHandler;
import enums.eInstType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.ePrefTAG;
import enums.eShowDepth;
import enums.eWatchs;
import interfaces.OnPopupListener;
import models.TradeLoginModel;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 11/18/2016.
 */
public class CustomPopupWindow implements RadioGroup.OnCheckedChangeListener, ReqSent,
        View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


    //region [Variables]
    private final String className = getClass().getName();
    private ArrayList<SearchScripRow> m_searchList;
    private ListView m_searchedListView;
    private AlertDialog m_alertDialog,wl_alertDialog,m_alertDialog_new;
    private LinearLayout m_fno_list;
    private RadioGroup m_rdbtn;
    private RadioButton bseRD;
    private RadioButton slbsRD;
    private Spinner m_iSpinner;
    private Spinner m_cSpinner;
    private SearchScripAdapter m_searchScripAdapter;
    private OnPopupListener m_listener;
    private TextView m_selectedGroup;
    private ListView m_groupList;
    private GroupAdapter m_groupAdapter;
    private EditText m_addNewGroupName;
    private RadioGroup m_rgExpiryType;
    private short m_expType = eExpiryType.MONTHLY.value;
    private LinearLayout expiryType;
    private View m_view;
    private EditText edit_query;
    private String grpName;
    private boolean forAdd = false;

    private LinearLayout searchLinear;
    private LinearLayout comByLinear;

    private RadioGroup searchbyRd,comByRd;
    private RadioButton name,symbol,startwith,contains;
    private boolean isSymbolType = true;
    //endregion

    //region [Constructor]
    public CustomPopupWindow(OnPopupListener listener) {
        this.m_listener = listener;
        m_searchList = new ArrayList<>();
        GlobalClass.searchScripUIHandler = new SearchScripHandler();
    }
    //endregion
    //region [Public Method]
    public void openSearchScripWindow() {
        try {
            m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.add_script_popup, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
            dialogBuilder.setView(m_view);

            searchLinear = (LinearLayout) m_view.findViewById(R.id.search_by);
           // searchLinear.setVisibility(View.GONE);
            searchbyRd = (RadioGroup) m_view.findViewById(R.id.searchbyRd);

            comByLinear = (LinearLayout) m_view.findViewById(R.id.comByLinear);
            comByRd = (RadioGroup) m_view.findViewById(R.id.comByRd);

           // searchbyRd.setOnCheckedChangeListener(this);
            name = (RadioButton) m_view.findViewById(R.id.name);
            symbol = (RadioButton) m_view.findViewById(R.id.symbol);

            comByRd.setOnCheckedChangeListener(this);
            startwith = (RadioButton) m_view.findViewById(R.id.startwith);
            contains = (RadioButton) m_view.findViewById(R.id.contains);

            // m_materialDialog = new MaterialDialog(GlobalClass.latestContext).setContentView(m_view).setCanceledOnTouchOutside(true);
            m_iSpinner = (Spinner) m_view.findViewById(R.id.spinner_Instrument).findViewById(R.id.spn);
            ArrayAdapter<CharSequence> iAdapter = ArrayAdapter.createFromResource
                    (GlobalClass.latestContext, R.array.instrument_array, R.layout.custom_spinner_item);
            iAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            m_iSpinner.setAdapter(iAdapter);
            m_iSpinner.setSelection(1);
            m_iSpinner.setOnItemSelectedListener(this);
            m_cSpinner = (Spinner) m_view.findViewById(R.id.spinner_Contract).findViewById(R.id.spn);
            ArrayAdapter<CharSequence> cAdapter = ArrayAdapter.createFromResource
                    (GlobalClass.latestContext, R.array.contract_array, R.layout.custom_spinner_item);
            cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            m_cSpinner.setAdapter(cAdapter);
            m_cSpinner.setSelection(1);
            m_searchedListView = (ListView) m_view.findViewById(R.id.searchedListView);
            m_searchScripAdapter = new SearchScripAdapter(GlobalClass.latestContext, R.layout.custom_search_item, R.id.item);
            m_searchedListView.setAdapter(m_searchScripAdapter);
            m_searchedListView.setOnItemClickListener(this);
            m_rdbtn = ((RadioGroup) m_view.findViewById(R.id.rdgroup));
            m_rdbtn.setOnCheckedChangeListener(this);
            bseRD = ((RadioButton) m_view.findViewById(R.id.bse));
            slbsRD = ((RadioButton) m_view.findViewById(R.id.slbs));
            if(!UserSession.getClientResponse().isSLBMActivated()){
                slbsRD.setVisibility(View.GONE);
            }
            m_fno_list = (LinearLayout) m_view.findViewById(R.id.fno_list);
            m_fno_list.setVisibility(View.GONE);
            expiryType = (LinearLayout) m_view.findViewById(R.id.expiry_type_layout);
            expiryType.setVisibility(View.GONE);
            m_rgExpiryType = (RadioGroup) m_view.findViewById(R.id.monthWeekExpiry);
            m_rgExpiryType.setOnCheckedChangeListener(this);
            Button search = (Button) m_view.findViewById(R.id.search);
            edit_query = (EditText) m_view.findViewById(R.id.edit_query);
            search.setOnClickListener(this);
            final ImageButton close = (ImageButton) m_view.findViewById(R.id.close);
            close.setOnClickListener(this);
            m_alertDialog = dialogBuilder.create();
            m_alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            m_alertDialog.show();
            m_alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
            //m_materialDialog.show();
        } catch (Exception e) {
            GlobalClass.onError("Error in "+className,e);
        }
    }


    public void addNewGroupWindow() {
        try {
            m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.add_group_layout,null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
            dialogBuilder.setView(m_view);
            // m_materialDialog = new MaterialDialog(GlobalClass.latestContext).setContentView(m_view).setCanceledOnTouchOutside(true);
            m_view.findViewById(R.id.btnAgOk).setOnClickListener(this);
            m_view.findViewById(R.id.btnAgCancel).setOnClickListener(this);
            m_view.findViewById(R.id.close).setOnClickListener(this);
            m_addNewGroupName = (EditText) m_view.findViewById(R.id.userGroupName);
            m_alertDialog_new = dialogBuilder.create();
            m_alertDialog_new.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            m_alertDialog_new.show();
            m_alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
        }catch (Exception e){
            GlobalClass.onError("Error in "+className,e);
        }
    }
    //endregion

    //region [Private Method]

    private void clearSearchedData() {
        m_searchList.clear();
        byNameList.clear();
        m_searchScripAdapter.refreshAdapter(searchFor);
        m_searchScripAdapter.setSelectedPosition(-1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT );
        m_searchedListView.setLayoutParams(layoutParams);
    }

    private void onSearchClick() throws Exception {
        try {
            String query = edit_query.getText().toString().trim();
            if (query.length() < Constants.MIN_SEARCH_LENGTH) {
                GlobalClass.showToast(GlobalClass.latestContext, "Enter " + Constants.MIN_SEARCH_LENGTH + " or more characters to Search");
            } else {

                short instType = eInstType.ALL.value;
                if (m_iSpinner.getSelectedItem().toString().equalsIgnoreCase("Futures")) {
                    instType = eInstType.Futures.value;
                } else if (m_iSpinner.getSelectedItem().toString().contains("Call")) {
                    instType = eInstType.Calls.value;
                } else if (m_iSpinner.getSelectedItem().toString().contains("Put")) {
                    instType = eInstType.Puts.value;
                }
                else if (m_iSpinner.getSelectedItem().toString().contains("All")) {
                    instType = eInstType.CallPut.value;
                }

                short expiry = eExpiry.ANY.value;
                if (m_cSpinner.getSelectedItem().toString().equalsIgnoreCase("near")) {
                    expiry = eExpiry.NEAR.value;
                } else if (m_cSpinner.getSelectedItem().toString().equalsIgnoreCase("next")) {
                    expiry = eExpiry.NEXT.value;
                } else if (m_cSpinner.getSelectedItem().toString().equalsIgnoreCase("far")) {
                    expiry = eExpiry.FAR.value;
                } else if (m_cSpinner.getSelectedItem().toString().equalsIgnoreCase("far next")) {
                    expiry = eExpiry.REST.value;
                }
                SearchScript searchScripReq = new SearchScript();
                String exchName = ((RadioButton) m_rdbtn.findViewById(m_rdbtn.getCheckedRadioButtonId())).getText().toString();
                int exch = GlobalClass.getExchangeCode(exchName);
                searchScripReq.intExchange.setValue(exch);
                searchScripReq.searchString.setValue(query);
                searchScripReq.shortInstrument.setValue(instType);
                searchScripReq.expiry.setValue(expiry);
                searchScripReq.expiryType.setValue(m_expType);

                short companyType = 0;
                if (comByLinear.getVisibility() == View.VISIBLE && contains.isChecked()){
                    companyType = 1;
                }
                searchScripReq.companyType.setValue(companyType);

                SendDataToBCServer sendDataToServer = new SendDataToBCServer(this);
                sendDataToServer.sendSearchScripReq(searchScripReq,isSymbolType);
                clearSearchedData();
                GlobalClass.hideKeyboard(edit_query, GlobalClass.latestContext);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void close() {
        try {
            if (m_alertDialog_new !=null){
                m_alertDialog_new.dismiss();
                m_alertDialog_new = null;
            }else {
                if (m_alertDialog != null) {
                    if (wl_alertDialog != null) {
                        wl_alertDialog.dismiss();
                        wl_alertDialog = null;
                    }
                    GlobalClass.searchScripUIHandler = null;
                    m_view = null;
                    m_alertDialog.dismiss();
                    m_alertDialog = null;
                }
            }
            if(m_listener != null){
                m_listener.onPopupClose();
            }
        }catch (Exception e){
            GlobalClass.onError("Error in "+className,e);
        }
    }

    private void addScripInGroup(String grpname) throws Exception {
        SearchScripRow gsd = getSearchScripDet();
        long grpCode = GlobalClass.groupHandler.getUserDefineGroup().hm_grpNameCode.get(grpname);
        GlobalClass.groupHandler.setSelectedGrpName(grpname);
        GroupsRespDetails grpResDetail = GlobalClass.groupHandler.getUserDefineGroup().getGrpDetailFromGrpCode(grpCode, eWatchs.MKTWATCH);
        if(grpResDetail.groupName.getValue().equalsIgnoreCase(Constants.DRIVING_WATCH) && grpResDetail.getGroupSize() >= Constants.DRIVING_WATCH_COUNT){
            GlobalClass.showToast(GlobalClass.latestContext, "You can add only " + Constants.DRIVING_WATCH_COUNT + " scrips in "+Constants.DRIVING_WATCH+" group.");
        }else if (grpResDetail.getGroupSize() >= Constants.GROUP_SCRIP_LENGTH) {
            GlobalClass.showToast(GlobalClass.latestContext, "You can add only " + Constants.GROUP_SCRIP_LENGTH + " scrips in user defined group.");
        } else if (grpResDetail.hm_grpTokenDetails.keySet().contains(gsd.scripCode)) {
            GlobalClass.showToast(GlobalClass.latestContext, gsd.scripName + Constants.ERR_ADD_SCRIP_CUSTOM_WATCHLIST);
        } else {

            int expiry = (int) GlobalClass.getExpiryFromScripName(gsd.scripName);
            SendDataToBCServer sendDataToServer = new SendDataToBCServer(this);
            sendDataToServer.addScripToGroupRequest(grpname,gsd.scripCode,expiry);

            GroupsTokenDetails tokenDetails = new GroupsTokenDetails();
            tokenDetails.scripCode.setValue(gsd.scripCode);
            tokenDetails.scripName.setValue(gsd.scripName);
            tokenDetails.isNewlyAdded = true;
            //grpResDetail.addSingleTokenDetail(tokenDetails);

            GlobalClass.groupHandler.addTokenFromAddScrip(grpCode,tokenDetails);

            try {
                GroupsRespDetails recentViewDetail = GlobalClass.groupHandler.getUserDefineGroup()
                        .getGrpDetailFromGrpCode(98, eWatchs.MKTWATCH);
                recentViewDetail.addToRecentView(tokenDetails);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            GlobalClass.notifyMktWatchScreen(eMessageCode.ADDSCRIPT_TOGROUP.value);
            GlobalClass.notifyMyStockScreen(eMessageCode.ADDSCRIPT_TOGROUP.value);
            GlobalClass.notifyDepthScreen(eMessageCode.ADDSCRIPT_TOGROUP.value,gsd.scripCode,gsd.scripName);
            GlobalClass.showToast(GlobalClass.latestContext,"Scrip added successfully");
            close();
        }
    }
/*
    private void handleScripDetailResponse(GroupsTokenDetails tokenDetails){
        String grpname = m_selectedGroup.getText().toString().trim();
        long grpCode = GlobalClass.groupHandler.getUserDefineGroup().hm_grpNameCode.get(grpname);
        GlobalClass.groupHandler.addTokenFromAddScrip(grpCode,tokenDetails);
        GlobalClass.notifyMktWatchScreen(eMessageCode.NEW_GROUPDETAILS.value);
        close();
    }
*/

    private void addToWatchlistWindow() {
        try {
            ArrayList<GroupsRespDetails> gList = GlobalClass.groupHandler.getEditableGroupStructureList();
            if (gList.size() > 0) {
                m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.popup_group_list, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
                dialogBuilder.setView(m_view);
                //wl_materialDialog = new MaterialDialog(GlobalClass.latestContext).setContentView(m_view).setCanceledOnTouchOutside(true);
                m_groupList = (ListView) m_view.findViewById(R.id.groupList);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (gList.size()*GlobalClass.latestContext.getResources().getDimension(R.dimen.item_height)));
                m_groupList.setLayoutParams(layoutParams);
                m_groupAdapter = new GroupAdapter(GlobalClass.latestContext, R.layout.group_list_item, gList);
                m_groupList.setAdapter(m_groupAdapter);
                m_selectedGroup = (TextView) m_view.findViewById(R.id.sGroupName);
               // String sGrpName = gList.get(0).groupName.getValue();//GlobalClass.groupHandler.getSelectedGrpName();
                String grpName = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.ADDGROUP_TO.name,"");
                if (grpName.equals("")) grpName = gList.get(0).groupName.getValue();
              //  String sGrpName = VenturaApplication.getPreference().getSelectedGroup();
                m_selectedGroup.setText(grpName);
                m_groupList.setOnItemClickListener(CustomPopupWindow.this);
                ImageButton close = (ImageButton) m_view.findViewById(R.id.wlClose);
                close.setOnClickListener(CustomPopupWindow.this);
                TextView txtOk = (TextView) m_view.findViewById(R.id.txtOk);
                txtOk.setOnClickListener(CustomPopupWindow.this);
                wl_alertDialog = dialogBuilder.create();
                wl_alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                wl_alertDialog.show();
                wl_alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
                // wl_materialDialog.show();
            } else {
                GlobalClass.showToast(GlobalClass.latestContext, Constants.ERR_ADD_TO_WATCHLIST);
                forAdd = true;
                addNewGroupWindow();
            }
        }catch (Exception e){
            GlobalClass.onError("Error in "+className,e);
        }

    }
    private void addNewGroup(String grpName) {
        try {
            if (grpName.equalsIgnoreCase("")) {
                GlobalClass.showToast(GlobalClass.latestContext, Constants.ADD_EMPTY_GROUP_MSG);

            } else if (GlobalClass.groupHandler.isGroupExists(grpName)) {
                GlobalClass.showToast(GlobalClass.latestContext, "Group " + grpName + " already exists.");
            } else {
                int userDGrp = GlobalClass.groupHandler.getEditableGroupStructureList().size();
                if(userDGrp < Constants.GROUP_LENGTH) {
                    String ragex = "[ a-zA-Z0-9]+";
                    if(grpName.matches(ragex)) {
                       GlobalClass.showProgressDialog("Please wait...");
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer(this);
                        sendDataToServer.addGroupRequest(grpName);
                    }else{
                        GlobalClass.showToast(GlobalClass.latestContext, Constants.GROUP_NAME_INVALID_MSG);
                    }
                }
                else{
                    GlobalClass.showToast(GlobalClass.latestContext, Constants.ADD_MAX_GROUP_MSG);
                }
            }
        } catch (Exception e) {
            GlobalClass.showToast(GlobalClass.latestContext, "Error in Creating Group");
            GlobalClass.onError("Error in: " + className, e);
            close();
        }
    }
    private void checkMPIN(String mpin) {
        try {
            if (mpin.equalsIgnoreCase("")) {
                GlobalClass.showAlertDialog(Constants.ADD_EMPTY_MPIN_MSG);
            } else {
                TradeLoginModel tradeDetails = VenturaApplication.getPreference().getTradeDetails();
                if(tradeDetails.getMpin().equals(mpin)){
                    close();
                }else{
                    GlobalClass.showAlertDialog("Incorrect MPIN");
                }

            }
        } catch (Exception e) {
            GlobalClass.showToast(GlobalClass.latestContext, "Error in checking MPIN");
            GlobalClass.onError("Error in: " + className, e);
            close();
        }
    }
    private void handleAddGroupRes(GroupAddDelResp resp) {
        try {
            GlobalClass.dismissdialog();
            if (resp.msg.getValue().equalsIgnoreCase("")) {
                String grpName = m_addNewGroupName.getText().toString().trim();
                GroupsRespDetails grpDetail = new GroupsRespDetails();
                grpDetail.groupCode = resp.groupCode;
                grpDetail.groupName.setValue(grpName);
                GlobalClass.groupHandler.getUserDefineGroup().addSingleGroup(grpDetail);
                if (forAdd) {
                    VenturaApplication.getPreference().setSelectedGroup(grpName);
                    addScripInGroup(grpName);
                    forAdd = false;
                }else{
                    GlobalClass.notifyMktWatchScreen(eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP.value);
                    GlobalClass.notifyMyStockScreen(eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP.value);
                }
            }
            else{
                GlobalClass.showToast(GlobalClass.latestContext,"AddGroup Res : " + resp.msg.getValue());
            }
            close();

        } catch (Exception e) {
            GlobalClass.showToast(GlobalClass.latestContext, "Error in Creating Group");
            GlobalClass.onError("Error in: " + className, e);
            close();
        }
    }
    private void populateSearchScripData(SearchScripResp searchScripResp) {
        try {
            if (searchScripResp.noOfRecs.getValue() == 0) {
                GlobalClass.showToast(GlobalClass.latestContext, "No Data Available");
            } else {
                m_searchList = searchScripResp.getSearchScripList();
              /*  LinearLayout.LayoutParams layoutParams = new LinearLayout
              .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((m_searchList.size())*GlobalClass
              .latestContext.getResources().getDimension(R.dimen.item_height)));
                m_searchedListView.setLayoutParams(layoutParams);*/
                m_searchScripAdapter.refreshAdapter(1);
            }
        }catch (Exception e){
            GlobalClass.onError("Error in "+className,e);
        }
    }

    private SearchScripRow getSearchScripDet() {
        if (searchFor == 2){
            SearchDetails sd = byNameList.get(m_searchScripAdapter.getSelectedPosition());
            SearchScripRow ssr = new SearchScripRow();
            ssr.scripCode = sd.scripCode.getValue();
            ssr.scripName = sd.symbol.getValue();
            return ssr;
        }else {
            return m_searchList.get(m_searchScripAdapter.getSelectedPosition());
        }
    }


    private void showMktDepth(int scripCode, String scripName,eOrderType orderType) {

        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scripCode);
        groupsTokenDetails.scripName.setValue(scripName);
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = null;
        if(orderType != eOrderType.NONE){
            buySell = new StructBuySell();
            buySell.buyOrSell = orderType;
            buySell.modifyOrPlace = eOrderType.PLACE;
            buySell.showDepth = eShowDepth.MKTWATCH;
        }
        if(!UserSession.isTradeLogin()){
           buySell = null;
        }

        //Fragment m_fragment = new MktdepthFragment(scripCode,eShowDepth.MKTWATCH,grplist,buySell);
       /* Fragment m_fragment = new MktdepthFragment(scripCode, eShowDepth.MKTWATCH,grplist,null);
        GlobalClass.fragmentTransaction(m_fragment,R.id);

        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, m_fragment);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();*/
        GlobalClass.openDepth(scripCode,eShowDepth.MKTWATCH,grplist,buySell);
        close();
    }


    //endregion

    //region [Override Method]
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        try {
            if(radioGroup == m_rdbtn) {
                if (!UserSession.getLoginDetailsModel().isClient()){
                    bseRD.setChecked(true);
                    GlobalClass.showToast(GlobalClass.latestContext,Constants.NOT_REGISTER);
                    return;
                }
                clearSearchedData();
                if (R.id.nse_fno == checkedId || R.id.nse_curr == checkedId) {
                    edit_query.setHint("Symbol Or Scripcode");
                    m_fno_list.setVisibility(View.VISIBLE);
                    isSymbolType = true;
                } else if(R.id.slbs == checkedId) {
                    edit_query.setHint("Symbol Or Scripcode");
                    m_fno_list.setVisibility(View.VISIBLE);
                    isSymbolType = true;
                }else {
                    m_fno_list.setVisibility(View.GONE);
                    if (symbol.isChecked()){
                        isSymbolType = true;
                        edit_query.setHint("Symbol Or Scripcode");
                    }else {
                        isSymbolType = false;
                        edit_query.setHint("Company Name");
                    }
                }
            } else if(radioGroup == m_rgExpiryType){
                if(R.id.rbMonthlyExpiry == checkedId){
                    m_expType = eExpiryType.MONTHLY.value;
                }else {
                    m_expType = eExpiryType.WEEKLY.value;
                }
            }else if (radioGroup == searchbyRd){
                if (R.id.symbol == checkedId){
                    ((RadioButton) m_rdbtn.getChildAt(2)).setVisibility(View.VISIBLE);
                    isSymbolType = true;
                    edit_query.setHint("Symbol Or Scripcode");
                    comByLinear.setVisibility(View.GONE);
                }else {
                    ((RadioButton) m_rdbtn.getChildAt(2)).setVisibility(View.INVISIBLE);
                    isSymbolType = false;
                    edit_query.setHint("Company Name");
                    comByLinear.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in "+className,e);
        }
    }
    @Override
    public void reqSent(int msgCode) {

    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.addToWL:
                    addToWatchlistWindow();
                    break;
                case R.id.txtSell: {
                    final SearchScripRow searchScripDet = getSearchScripDet();
                    showMktDepth(searchScripDet.scripCode, searchScripDet.scripName, eOrderType.SELL);
                }
                    break;
                case R.id.txtBuy: {
                    final SearchScripRow searchScripDet = getSearchScripDet();
                    showMktDepth(searchScripDet.scripCode, searchScripDet.scripName, eOrderType.BUY);
                }
                    break;
                case R.id.close:
                    close();
                    break;
                case R.id.btnAgCancel:
                    close();
                    break;
                case R.id.wlClose:
                    wl_alertDialog.dismiss();
                    wl_alertDialog = null;
                    break;
                case R.id.search:
                    onSearchClick();
                    break;
                case R.id.txtOk:
                    String selectedGrpName = m_selectedGroup.getText().toString().trim();
                    addScripInGroup(selectedGrpName);
                    break;
                case R.id.btnAgOk:
                    grpName = m_addNewGroupName.getText().toString().trim();
                    addNewGroup(grpName);
                    GlobalClass.log("","User Group Name: " ,""+ grpName);
                    break;

            }
        } catch (Exception e) {
            GlobalClass.onError("Error in" + className, e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            switch (adapterView.getId()) {
                case R.id.searchedListView:
                    m_searchScripAdapter.setSelectedPosition(i);
                    m_searchedListView.invalidateViews();
                    m_searchedListView.setAdapter(m_searchScripAdapter);
                    m_searchedListView.setSelection(i);
                    break;
                case R.id.groupList:
                    m_groupAdapter.setSelectedPosition(i);
                    m_groupList.invalidateViews();
                    m_groupList.setAdapter(m_groupAdapter);
                    m_groupList.setSelection(i);
                    String grpName = ((TextView) view.findViewById(R.id.groupName))
                            .getText().toString().trim();
                    m_selectedGroup.setText(grpName);
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.ADDGROUP_TO.name,grpName);
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in" + className, e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            String exchName = ((RadioButton) m_rdbtn.findViewById(m_rdbtn.getCheckedRadioButtonId())).getText().toString();
            int exch = GlobalClass.getExchangeCode(exchName);
            if ((position == 1) && (exch == eExch.FNO.value)) {
                expiryType.setVisibility(View.GONE);
            } else {
                expiryType.setVisibility(View.VISIBLE);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//endregion

    //region [inner class]
    private int searchFor = 0;

    class SearchScripAdapter extends ArrayAdapter {
        private int NO_POS = -1;
        private int selected_pos = NO_POS;
        private Context context;
        private int resource;

        public SearchScripAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            switch (searchFor){
                case 0:
                    return 0;
                case 1:
                    return m_searchList.size();
                case 2:
                    return byNameList.size();
                default:
                  return 0;
            }
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            switch (searchFor){
                case 1:
                    return position;
                case 2:
                    return byNameList.get(position);
                default:
                    return position;
            }
        }

        public void setSelectedPosition(int selected_pos) {
            this.selected_pos = selected_pos;
        }

        public int getSelectedPosition() {
            return selected_pos;
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            final View v =LayoutInflater.from(GlobalClass.latestContext).inflate(resource, null);
            v.setMinimumHeight((int) context.getResources().getDimension(R.dimen.item_height));
            try {
                if (position == selected_pos) {
                    final LinearLayout wlLayout = (LinearLayout) v.findViewById(R.id.wlLayout);
                    wlLayout.setVisibility(View.VISIBLE);
                    TextView addToWL = (TextView) v.findViewById(R.id.addToWL);
                    TextView txtSell = (TextView) v.findViewById(R.id.txtSell);
                    TextView txtBuy = (TextView) v.findViewById(R.id.txtBuy);
                    addToWL.setOnClickListener(CustomPopupWindow.this);
                    txtSell.setOnClickListener(CustomPopupWindow.this);
                    txtBuy.setOnClickListener(CustomPopupWindow.this);
                }

                TextView txtScrip = (TextView) v.findViewById(R.id.item);
                if (searchFor == 1) {
                    txtScrip.setText(m_searchList.get(position).scripName);
                }else {
                    SearchDetails sd = byNameList.get(position);
                    txtScrip.setText(sd.cName.getValue());
                }
            } catch (Exception e) {
                GlobalClass.onError("Error in "+className,e);
            }
            return v;
        }

        public void refreshAdapter(int sb){
            searchFor = sb;
            notifyDataSetChanged();
        }

    }

    public class GroupAdapter extends ArrayAdapter {
        private ArrayList<GroupsRespDetails> list;
        private Context mContext;
        private int resource;

        private int NO_POS = -1;
        private int selected_pos = NO_POS;

        public void setSelectedPosition(int selected_pos) {
            this.selected_pos = selected_pos;
        }

        public int getSelectedPosition() {
            return selected_pos;
        }

        public GroupAdapter(Context context, int resource, ArrayList<GroupsRespDetails> list) {
            super(context, resource, list);
            this.list = list;
            this.resource = resource;
            mContext = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                convertView = LayoutInflater.from(GlobalClass.latestContext).inflate(resource, null);
                GroupsRespDetails groupInfo = list.get(position);
                String grpName = groupInfo.groupName.getValue();
                TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
                groupName.setText(grpName);
                TextView numOfScrip = (TextView) convertView.findViewById(R.id.numOfScrip);
                numOfScrip.setText(groupInfo.getGroupSize() + "");
                if (selected_pos == position) {
                    convertView.setBackgroundColor(GlobalClass.latestContext.getResources().getColor(R.color.gray_color));
                }

            } catch (Exception e) {
                GlobalClass.onError("Error in "+className,e);
            }
            return convertView;
        }
    }

    class SearchScripHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt("msgCode");
                    eMessageCode emessagecode =  eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case SEARCHSCRIPT:
                            SearchScripResp response1 = (SearchScripResp) refreshBundle.getSerializable("struct");
                            populateSearchScripData(response1);

                            break;
                        case SEARCHSCRIPT_NAME:{
                            handleSearchNameData(refreshBundle.getByteArray(eForHandler.RESDATA.name));
                        }
                        break;
                        case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP: {
                            GroupAddDelResp response = (GroupAddDelResp) refreshBundle.getSerializable("struct");
                            handleAddGroupRes(response);
                        }
                        break;
                        /*case SCRIPT_DETAILS_Response: {
                            GroupsTokenDetails response = (GroupsTokenDetails) refreshBundle.getSerializable("struct");
                            handleScripDetailResponse(response);
                        }
                        break;*/
                    }
                }

            } catch (Exception ex) {
                GlobalClass.onError("Error in "+className,ex);
            }
        }
    }

   private List<SearchDetails> mList = new ArrayList<>();
   private List<SearchDetails> byNameList = new ArrayList<>();

    private void handleSearchNameData(byte[] byteArray) {
        SearchdetailsResp sdr = new SearchdetailsResp(byteArray);
        SearchDetails[] sd = sdr.sDetails;
        for (int i = 0; i<sd.length;i++){
            mList.add(sd[i]);
        }
        if (sdr.dc.getValue() == 1){
            byNameList.clear();
            byNameList.addAll(mList);
            m_searchScripAdapter.refreshAdapter(2);
           mList.clear();
        }
    }

    //endregion
}
