package fragments.settingstab;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import enums.eLogType;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eWatchs;
import fragments.homeGroups.SearchFragment;
import interfaces.OnPopupListener;
import utils.Constants;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;
import view.CustomPopupWindow;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class WatchListsFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener, OnPopupListener{
    private LinearLayout watchlist_main,watchlist_linear,groupscript_main, scriptlist_linear;
    private Button add_group_button, add_scripts_button;
    private Spinner spinner_groups;
    private ImageButton group_back;
    private ArrayAdapter<String> adapter;
    private String group_name = "";
    private CustomPopupWindow m_customPopupWindow = null;

    public WatchListsFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (UserSession.getLoginDetailsModel().isActiveUser()){
            view = inflater.inflate(R.layout.mystock_screen, container, false);
            init(view);
            GlobalClass.myStockUiHandler = new MyStockUiHandler();
        }else {
            view = inflater.inflate(R.layout.deactive_account, container, false);
        }
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, "WatchLists_settings",UserSession.getLoginDetailsModel().getUserID());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!group_name.equalsIgnoreCase("")){
            initGroupScript(group_name);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.myStockUiHandler = null;
    }

    void init(View view) {
        try {
            watchlist_main = (LinearLayout) view.findViewById(R.id.watchlist_main);
            watchlist_linear = (LinearLayout) view.findViewById(R.id.watchlist_linear);
            groupscript_main = (LinearLayout) view.findViewById(R.id.groupscript_main);
            scriptlist_linear = (LinearLayout) view.findViewById(R.id.scriptlist_linear);
            spinner_groups = (Spinner) view.findViewById(R.id.spinner_groups);
            add_group_button = (Button) view.findViewById(R.id.add_group_button);
            add_scripts_button = (Button) view.findViewById(R.id.add_scripts_button);
            group_back = (ImageButton) view.findViewById(R.id.group_back);
            add_group_button.setOnClickListener(this);
            add_scripts_button.setOnClickListener(this);
            group_back.setOnClickListener(this);
            loadGroupList();
            spinner_groups.setOnItemSelectedListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGroupList() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<GroupsRespDetails> editableGroupStructureList =   GlobalClass.groupHandler.getUserDefineGroup().getEditableGroupStructureList();
        watchlist_linear.removeAllViews();
        for(GroupsRespDetails grpRes : editableGroupStructureList){
            View sview = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.script_group_line_view, null);
            TextView tv = (TextView) sview.findViewById(R.id.name);
            tv.setText(grpRes.groupName.getValue());
            watchlist_linear.addView(sview);
            sview.setTag("G");
            sview.findViewById(R.id.edit).setOnClickListener(this);
            sview.findViewById(R.id.close).setOnClickListener(this);
            list.add(grpRes.groupName.getValue());
        }
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_item,list);
        adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        spinner_groups.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.edit:
                try {
                    group_name = ((TextView) ((LinearLayout) view.getParent()).findViewById(R.id.name)).getText().toString().trim();
                    spinner_groups.setSelection(adapter.getPosition(group_name));
                    initGroupScript(group_name);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.close:
                final LinearLayout layout = (LinearLayout) view.getParent().getParent();
                if (layout.getTag().toString().equalsIgnoreCase("G")){
                    deleteRequest(((TextView) ((LinearLayout) view.getParent()).findViewById(R.id.name)).getText().toString().trim(),"",true);
                }else {
                   deleteRequest(group_name,layout.getTag().toString(),false);
                }
                break;
            case R.id.add_group_button:
                int grpSize = GlobalClass.groupHandler.getEditableGroupStructureList().size();
                if(grpSize < Constants.GROUP_LENGTH) {
                    m_customPopupWindow = new CustomPopupWindow(this);
                    m_customPopupWindow.addNewGroupWindow();
                } else{
                    GlobalClass.showToast(GlobalClass.latestContext, Constants.ADD_MAX_GROUP_MSG);
                }
                break;
            case R.id.add_scripts_button:
                boolean isOldSearchPopup = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.OLD_SEARCH_POPUP.name,  false);

                if(ObjectHolder.connconfig.getSearchEngineIP().equalsIgnoreCase("") || isOldSearchPopup) {
                    m_customPopupWindow = new CustomPopupWindow(this);
                    m_customPopupWindow.openSearchScripWindow();
                }else {
                    SearchFragment m_fragment = new SearchFragment();
                    GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "searchf");
                }
                break;
            case R.id.group_back:
                watchlist_main.setVisibility(View.VISIBLE);
                groupscript_main.setVisibility(View.GONE);
                group_name = "";
                break;
        }
    }

    private void deleteRequest(final String groupName, final String scripName, final boolean groupdeleteReq) {
        AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
        m_alertBuilder.setTitle("Ventura Wealth");
        m_alertBuilder.setMessage("Are you sure you want to Delete?");
        m_alertBuilder.setIcon(R.drawable.ventura_icon);
        m_alertBuilder.setCancelable(true);
        m_alertBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (groupdeleteReq){
                            GlobalClass.groupHandler.getUserDefineGroup().deleteGroup(groupName);
                            loadGroupList();
                        }else{
                            GlobalClass.groupHandler.deleteTokenFromGroup(groupName,Integer.parseInt(scripName));
                            initGroupScript(groupName);
                        }
                        dialog.cancel();
                    }
                });
        m_alertBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog m_alertDialog = m_alertBuilder.create();
        m_alertDialog.show();
        m_alertDialog.getButton(m_alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ventura_color));
        m_alertDialog.getButton(m_alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ventura_color));
    }

    private void  initGroupScript(String group_name) {
        try {
            GroupsRespDetails respDetails = GlobalClass.groupHandler.getUserDefineGroup().getGrpDetailFromGrpName(group_name, eWatchs.MKTWATCH);
            watchlist_main.setVisibility(View.GONE);
            groupscript_main.setVisibility(View.VISIBLE);
            scriptlist_linear.removeAllViews();
            ArrayList<Integer> scripList = new ArrayList<>(respDetails.hm_grpTokenDetails.keySet());
            for (int i=0;i<scripList.size();i++){
                GroupsTokenDetails tokenDetails = respDetails.hm_grpTokenDetails.get(scripList.get(i));
                GlobalClass.log("MyStock","tokenDetails",""+tokenDetails.scripCode.getValue());
                View sview = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.script_group_line_view, null);
                TextView tv = (TextView) sview.findViewById(R.id.name);
                tv.setText(tokenDetails.scripName.getValue());
                scriptlist_linear.addView(sview);
                sview.findViewById(R.id.edit).setVisibility(View.GONE);
                sview.setTag(tokenDetails.scripCode.getValue()+"");
                sview.findViewById(R.id.close).setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        group_name = adapterView.getSelectedItem().toString();
        initGroupScript(group_name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPopupClose() {
        if(!group_name.equalsIgnoreCase("")){
            initGroupScript(group_name);
        }
    }


    class MyStockUiHandler extends Handler {
        public  boolean isShow;
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt("msgCode");
                    int scripCode = refreshBundle.getInt("scripCode");
                    eMessageCode emessagecode =  eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case NEW_GROUPDETAILS:
                            initGroupScript(group_name);
                            break;
                        case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP:
                            loadGroupList();
                            break;
                        case ADDSCRIPT_TOGROUP:
                            initGroupScript(group_name);
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
