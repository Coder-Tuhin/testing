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

import java.util.ArrayList;

import Structure.Request.BC.SearchScript;
import Structure.Response.Group.GroupAddDelResp;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.Scrip.SearchScripResp;
import Structure.Response.Scrip.SearchScripRow;
import com.ventura.venturawealth.R;
import connection.ReqSent;
import connection.SendDataToBCServer;
import enums.eExch;
import enums.eExpiry;
import enums.eExpiryType;
import enums.eInstType;
import enums.eMessageCode;
import enums.eWatchs;
import interfaces.OnPopup;
import utils.Constants;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 12/22/2016.
 */
public class CustomPopupQuickOrder  implements RadioGroup.OnCheckedChangeListener, ReqSent,
        View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


    //region [Variables]

    private final String className = getClass().getName();
    private ArrayList<SearchScripRow> m_searchList;
    private ListView m_searchedListView;
    //private PopupWindow m_popupWindow;
    AlertDialog m_alertDialog,wl_alertDialog;
    private LinearLayout m_fno_list;
    private RadioGroup m_rdbtn;
    private RadioButton bse,nse,fno,curr;
    private Spinner m_iSpinner;
    private Spinner m_cSpinner;
    private SearchScripAdapter m_searchScripAdapter;
    private OnPopup m_listener;
    private TextView m_selectedGroup,txtSell;
    private ListView m_groupList;
    private GroupAdapter m_groupAdapter;
    private EditText m_addNewGroupName;
    private RadioGroup m_rgExpiryType;
    private short m_expType = eExpiryType.MONTHLY.value;
    private LinearLayout expiryType;
    private View m_view;
    private EditText edit_query;
    private String purchageType;
    private int selectedExchange;

    private LinearLayout searchLinear;
    private RadioGroup searchbyRd;
    private RadioButton name,symbol;
    private boolean isSymbolType = true;
    //endregion

    //region [Constructor]
    public CustomPopupQuickOrder(OnPopup listener, String purchageType, int exch) {
        this.m_listener = listener;
        this.purchageType = purchageType;
        this.selectedExchange = exch;
        m_searchList = new ArrayList<>();
        GlobalClass.searchScripUIHandler = new SearchScripHandler();
    }
    //endregion
    //region [Public Method]
    public void openSearchScripWindow() {
        try {
            m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.add_script_popup, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(m_view);

            searchLinear = (LinearLayout) m_view.findViewById(R.id.search_by);
            searchbyRd = (RadioGroup) m_view.findViewById(R.id.searchbyRd);

            searchbyRd.setOnCheckedChangeListener(this);
            name = (RadioButton) m_view.findViewById(R.id.name);
            symbol = (RadioButton) m_view.findViewById(R.id.symbol);

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
            bse = (RadioButton) m_view.findViewById(R.id.bse);
            nse = (RadioButton) m_view.findViewById(R.id.nse);
            fno = (RadioButton) m_view.findViewById(R.id.nse_fno);
            curr = (RadioButton) m_view.findViewById(R.id.nse_curr);
            isSymbolType = true;
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

            // searchLinear.setVisibility(View.VISIBLE);
            if (selectedExchange == eExch.NSE.value){
                nse.setChecked(true);
            }else if (selectedExchange == eExch.BSE.value){
                bse.setChecked(true);
            }else if(selectedExchange == eExch.FNO.value){
                fno.setChecked(true);
                m_fno_list.setVisibility(View.VISIBLE);
                searchLinear.setVisibility(View.GONE);
            }
            else if(selectedExchange == eExch.NSECURR.value){
                curr.setChecked(true);
                m_fno_list.setVisibility(View.VISIBLE);
                searchLinear.setVisibility(View.GONE);
            }
            m_rdbtn = ((RadioGroup) m_view.findViewById(R.id.rdgroup));
            for (int i = 0; i < m_rdbtn.getChildCount(); i++) {
                m_rdbtn.getChildAt(i).setEnabled(false);
            }
            m_rdbtn.getChildAt(1).isSelected();
            // m_rdbtn.setOnCheckedChangeListener(this);
            m_alertDialog = dialogBuilder.create();
            m_alertDialog.show();
            //m_materialDialog.show();
        } catch (Exception e) {
            GlobalClass.onError("Error in "+className,e);
        }
    }


    //endregion

    //region [Private Method]

    private void clearSearchedData() {
        m_searchList.clear();
        m_searchScripAdapter.notifyDataSetChanged();
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
                searchScripReq.intExchange.setValue(GlobalClass.getExchangeCode(exchName));
                searchScripReq.searchString.setValue(query);
                searchScripReq.shortInstrument.setValue(instType);
                searchScripReq.expiry.setValue(expiry);
                searchScripReq.expiryType.setValue(m_expType);
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
        }catch (Exception e){
            GlobalClass.onError("Error in "+className,e);
        }
    }

    private void addScripInGroup() throws Exception {
        String grpName = m_selectedGroup.getText().toString().trim();
        SearchScripRow gsd = getSearchScripDet();
        long grpCode = GlobalClass.groupHandler.getUserDefineGroup().hm_grpNameCode.get(grpName);
        GlobalClass.groupHandler.setSelectedGrpName(grpName);
        GroupsRespDetails grpResDetail = GlobalClass.groupHandler.getUserDefineGroup().getGrpDetailFromGrpCode(grpCode, eWatchs.MKTWATCH);
        if (grpResDetail.getGroupSize() >= Constants.GROUP_SCRIP_LENGTH) {
            GlobalClass.showToast(GlobalClass.latestContext, "You can add only " + Constants.GROUP_SCRIP_LENGTH + " scrips in user defined group.");
        } else if (grpResDetail.hm_grpTokenDetails.keySet().contains(gsd.scripCode)) {
            GlobalClass.showToast(GlobalClass.latestContext, gsd.scripName + Constants.ERR_ADD_SCRIP_CUSTOM_WATCHLIST);
        } else {

            int expiry = (int) GlobalClass.getExpiryFromScripName(gsd.scripName);
            SendDataToBCServer sendDataToServer = new SendDataToBCServer(this);
            sendDataToServer.addScripToGroupRequest(grpName,gsd.scripCode,expiry);

            GroupsTokenDetails tokenDetails = new GroupsTokenDetails();
            tokenDetails.scripCode.setValue(gsd.scripCode);
            tokenDetails.scripName.setValue(gsd.scripName);
            GlobalClass.groupHandler.addTokenFromAddScrip(grpCode, tokenDetails);
            GlobalClass.notifyMktWatchScreen(eMessageCode.NEW_GROUPDETAILS.value);
            GlobalClass.notifyMyStockScreen(eMessageCode.ADDSCRIPT_TOGROUP.value);
            close();

        }
    }
    private void handleScripDetailResponse(GroupsTokenDetails tokenDetails){
        String grpName = m_selectedGroup.getText().toString().trim();
        long grpCode = GlobalClass.groupHandler.getUserDefineGroup().hm_grpNameCode.get(grpName);
        GlobalClass.groupHandler.addTokenFromAddScrip(grpCode,tokenDetails);
        GlobalClass.notifyMktWatchScreen(eMessageCode.NEW_GROUPDETAILS.value);
        close();
    }

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
                String sGrpName = gList.get(0).groupName.getValue();//GlobalClass.groupHandler.getSelectedGrpName();
                m_selectedGroup.setText(sGrpName);
                m_groupList.setOnItemClickListener(CustomPopupQuickOrder.this);
                ImageButton close = (ImageButton) m_view.findViewById(R.id.wlClose);
                close.setOnClickListener(CustomPopupQuickOrder.this);
                TextView txtOk = (TextView) m_view.findViewById(R.id.txtOk);
                txtOk.setOnClickListener(CustomPopupQuickOrder.this);
                wl_alertDialog = dialogBuilder.create();
                wl_alertDialog.show();
                // wl_materialDialog.show();
            } else {
                GlobalClass.showToast(GlobalClass.latestContext, Constants.ERR_ADD_TO_WATCHLIST);
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
                SendDataToBCServer sendDataToServer = new SendDataToBCServer(this);
                sendDataToServer.addGroupRequest(grpName);
            }
        } catch (Exception e) {
            GlobalClass.showToast(GlobalClass.latestContext, "Error in Creating Group");
            GlobalClass.onError("Error in: " + className, e);
            close();
        }
    }
    private void handleAddGroupRes(GroupAddDelResp resp) {
        try {
            if (resp.msg.getValue().equalsIgnoreCase("")) {
                String grpName = m_addNewGroupName.getText().toString().trim();
                GroupsRespDetails grpDetail = new GroupsRespDetails();
                grpDetail.groupCode = resp.groupCode;
                grpDetail.groupName.setValue(grpName);
                GlobalClass.groupHandler.getUserDefineGroup().addSingleGroup(grpDetail);
                GlobalClass.notifyMktWatchScreen(eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP.value);
                GlobalClass.notifyMyStockScreen(eMessageCode.BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP.value);
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
                /*for(int i=0;i<searchScripResp.scripNames.length;i++){
                    SearchScripResp searchScripRespDet = searchScripResp.searchScripRespDets[i];
                    if(searchScripRespDet.scripCode.getValue() > 0){
                        m_searchList.add(searchScripRespDet);
                    }
                }*/
                m_searchList = searchScripResp.getSearchScripList();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (m_searchList.size()*GlobalClass.latestContext.getResources().getDimension(R.dimen.item_height)));
                m_searchedListView.setLayoutParams(layoutParams);
                m_searchScripAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            GlobalClass.onError("Error in "+className,e);
        }
    }

    private SearchScripRow getSearchScripDet() {
        return m_searchList.get(m_searchScripAdapter.getSelectedPosition());
    }
    //endregion

    //region [Override Method]
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        try {
            if(radioGroup == m_rdbtn) {
                clearSearchedData();
                if (R.id.nse_fno == checkedId) {
                    edit_query.setHint("Symbol Or Scripcode");
                    m_fno_list.setVisibility(View.VISIBLE);
                    searchLinear.setVisibility(View.GONE);
                    isSymbolType = true;
                } else {
                    m_fno_list.setVisibility(View.GONE);
                    //searchLinear.setVisibility(View.VISIBLE);
                    if (symbol.isChecked()){
                        edit_query.setHint("Symbol Or Scripcode");
                        isSymbolType = true;
                    }else {
                        edit_query.setHint("Name");
                        isSymbolType = false;
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
                    isSymbolType = true;
                    edit_query.setHint("Symbol Or Scripcode");
                }else {
                    isSymbolType = false;
                    edit_query.setHint("Name");
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
                    addScripInGroup();
                    break;
                case R.id.btnAgOk:
                    String grpName = m_addNewGroupName.getText().toString().trim();
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
                    m_selectedGroup.setText(((TextView) view.findViewById(R.id.groupName))
                            .getText().toString().trim());
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in" + className, e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1){
            expiryType.setVisibility(View.GONE);
        } else {
            expiryType.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//endregion

    //region [inner class]

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
            return m_searchList.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
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
                    close();
                    m_listener.onPopupResponse(m_searchList.get(position).scripName,m_searchList.get(position).scripCode);

                   /* final LinearLayout wlLayout = (LinearLayout) v.findViewById(R.id.wlLayout);
                    wlLayout.setVisibility(View.VISIBLE);
                    TextView addToWL = (TextView) v.findViewById(R.id.addToWL);
                    addToWL.setOnClickListener(CustomPopupQuickOrder.this);*/
                }

                TextView txtScrip = (TextView) v.findViewById(R.id.item);
                txtScrip.setText(m_searchList.get(position).scripName);

            } catch (Exception e) {
                GlobalClass.onError("Error in "+className,e);
            }
            return v;
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
                    convertView.setBackgroundColor(GlobalClass.latestContext.getResources().getColor(R.color.silver));
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
                        case SEARCHSCRIPT_NAME:{
                            SearchScripResp response = (SearchScripResp) refreshBundle.getSerializable("struct");
                            populateSearchScripData(response);
                        }
                        break;
                        case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP: {
                            GroupAddDelResp response = (GroupAddDelResp) refreshBundle.getSerializable("struct");
                            handleAddGroupRes(response);
                        }
                        break;
                        case SCRIPT_DETAILS_Response: {
                            GroupsTokenDetails response = (GroupsTokenDetails) refreshBundle.getSerializable("struct");
                            handleScripDetailResponse(response);
                        }
                        break;
                    }
                }

            } catch (Exception ex) {
                GlobalClass.onError("Error in "+className,ex);
            }
        }
    }
    //endregion
}
