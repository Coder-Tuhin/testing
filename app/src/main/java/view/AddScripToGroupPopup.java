package view;

import static utils.MobileInfo.className;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;

import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.Scrip.StructSearchScripRow1;
import connection.ReqSent;
import connection.SendDataToBCServer;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eWatchs;
import fragments.homeGroups.SearchFragment;
import utils.Constants;
import utils.GlobalClass;

public class AddScripToGroupPopup implements View.OnClickListener, AdapterView.OnItemClickListener, ReqSent {

    private View m_view;
    private ListView m_groupList;
    private GroupAdapter1 m_groupAdapter;
    private TextView m_selectedGroup;
    private AlertDialog wl_alertDialog;

    private ArrayList<StructSearchScripRow1> m_searchList;

    public AddScripToGroupPopup(ArrayList<StructSearchScripRow1> _searchList){
        this.m_searchList = _searchList;
        addToWatchlistWindow();
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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (gList.size() * GlobalClass.latestContext.getResources().getDimension(R.dimen.item_height)));
                m_groupList.setLayoutParams(layoutParams);
                m_groupAdapter = new GroupAdapter1(GlobalClass.latestContext, R.layout.group_list_item, gList);
                m_groupList.setAdapter(m_groupAdapter);
                m_selectedGroup = (TextView) m_view.findViewById(R.id.sGroupName);
                // String sGrpName = gList.get(0).groupName.getValue();//GlobalClass.groupHandler.getSelectedGrpName();
                String grpName = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.ADDGROUP_TO.name, "");
                if (grpName.equals("")) grpName = gList.get(0).groupName.getValue();
                //  String sGrpName = VenturaApplication.getPreference().getSelectedGroup();
                m_selectedGroup.setText(grpName);
                m_groupList.setOnItemClickListener(this);
                ImageButton close = (ImageButton) m_view.findViewById(R.id.wlClose);
                close.setOnClickListener(this);
                TextView txtOk = (TextView) m_view.findViewById(R.id.txtOk);
                txtOk.setOnClickListener(this);
                wl_alertDialog = dialogBuilder.create();
                wl_alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                wl_alertDialog.show();
                wl_alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
                Window window = wl_alertDialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
            } else {
                GlobalClass.showToast(GlobalClass.latestContext, Constants.ERR_ADD_TO_WATCHLIST);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.wlClose:
                    closeGroupListView();
                    break;
                case R.id.txtOk: {
                    String selectedGrpName = m_selectedGroup.getText().toString().trim();
                    boolean isOnceAdded = false;
                    for (StructSearchScripRow1 rowD : m_searchList) {
                        if (rowD.isSelected) {
                            boolean isAdded = addScripInGroup1(selectedGrpName, rowD);
                            if (!isOnceAdded) {
                                isOnceAdded = isAdded;
                            }
                        }
                    }
                    if (isOnceAdded) {
                        GlobalClass.isReloadMktWatch = true;
                        GlobalClass.notifyMktWatchScreen(eMessageCode.ADDSCRIPT_TOGROUP.value);
                        GlobalClass.showToast(GlobalClass.latestContext, "Scrip added successfully");
                        closeGroupListView();
                    }
                }
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
                case R.id.groupList:
                    m_groupAdapter.setSelectedPosition(i);
                    m_groupList.invalidateViews();
                    m_groupList.setAdapter(m_groupAdapter);
                    m_groupList.setSelection(i);
                    String grpName = ((TextView) view.findViewById(R.id.groupName))
                            .getText().toString().trim();
                    m_selectedGroup.setText(grpName);
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.ADDGROUP_TO.name, grpName);
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in" + className, e);
        }
    }

    @Override
    public void reqSent(int msgCode) {

    }

    public class GroupAdapter1 extends ArrayAdapter {
        private ArrayList<GroupsRespDetails> list;
        private Context mContext;
        private int resource;
        private int NO_POS = -1;
        int selected_pos = NO_POS;

        public void setSelectedPosition(int selected_pos) {
            this.selected_pos = selected_pos;
        }

        public int getSelectedPosition() {
            return selected_pos;
        }

        public GroupAdapter1(Context context, int resource, ArrayList<GroupsRespDetails> list) {
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
                GlobalClass.onError("Error in " + className, e);
            }
            return convertView;
        }
    }

    private boolean addScripInGroup1(String grpname, StructSearchScripRow1 gsd) throws Exception {

        long grpCode = GlobalClass.groupHandler.getUserDefineGroup().hm_grpNameCode.get(grpname);
        GlobalClass.groupHandler.setSelectedGrpName(grpname);
        GroupsRespDetails grpResDetail = GlobalClass.groupHandler.getUserDefineGroup().getGrpDetailFromGrpCode(grpCode, eWatchs.MKTWATCH);
        if (grpResDetail.getGroupSize() >= Constants.GROUP_SCRIP_LENGTH) {
            GlobalClass.showToast(GlobalClass.latestContext, "You can add only " + Constants.GROUP_SCRIP_LENGTH + " scrips in user defined group.");
        } else if (grpResDetail.hm_grpTokenDetails.keySet().contains(gsd.scripCode.getValue())) {
            GlobalClass.showToast(GlobalClass.latestContext, gsd.getScripName() + Constants.ERR_ADD_SCRIP_CUSTOM_WATCHLIST);
        } else {

            SendDataToBCServer sendDataToServer = new SendDataToBCServer(this);
            sendDataToServer.addScripToGroupRequest(grpname, gsd.scripCode.getValue(), gsd.expiry.getValue());

            GroupsTokenDetails tokenDetails = new GroupsTokenDetails();
            tokenDetails.scripCode.setValue(gsd.scripCode.getValue());
            tokenDetails.scripName.setValue(gsd.getScripName());
            tokenDetails.isNewlyAdded = true;
            GlobalClass.groupHandler.addTokenFromAddScrip(grpCode, tokenDetails);
            try {
                GroupsRespDetails recentViewDetail = GlobalClass.groupHandler.getUserDefineGroup()
                        .getGrpDetailFromGrpCode(98, eWatchs.MKTWATCH);
                recentViewDetail.addToRecentView(tokenDetails);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }
        return false;
    }
    private void closeGroupListView() {
        try {

            if (wl_alertDialog != null) {
                wl_alertDialog.dismiss();
                wl_alertDialog = null;
            }
            m_view = null;
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
}
