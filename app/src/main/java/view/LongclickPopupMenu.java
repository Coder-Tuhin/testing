package view;

import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import chart.GraphFragment;
import connection.ReqSent;
import enums.eExch;
import enums.eFragments;
import enums.eMessageCode;
import enums.eShowDepth;
import fragments.LatestResultFragment;
import fragments.ValuetionFragment;
import fragments.homeGroups.MktdepthFragmentRC;
import interfaces.OnPopupListener;
import utils.Constants;
import utils.GlobalClass;
import wealth.Dialogs;

/**
 * Created by XtremsoftTechnologies on 11/03/16.
 */
public class LongclickPopupMenu implements ReqSent {
    private final String className=getClass().getName();
    private GroupsRespDetails m_groupsRespDetails;
    private eShowDepth m_from;
 //   private Context m_context;
    private HomeActivity.RadioButtons radioButtons;
    private OnLongClickListener onLongClickListener;

    public LongclickPopupMenu(GroupsRespDetails grpResp, HomeActivity.RadioButtons radioButtons, OnLongClickListener onLongClickListener) {
        this.m_groupsRespDetails = grpResp;
        this.radioButtons = radioButtons;
        this.onLongClickListener = onLongClickListener;
    }

    public  void showItemOptions(final OnPopupListener onPopupListener, View v,
                                 final int scripCode,final String scripName, final eShowDepth depthFrom, final int index ){
        this.m_from = depthFrom;
        PopupMenu popup = new PopupMenu(GlobalClass.latestContext, v);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        GlobalClass.enableDisbleMenuOptionInScrip(popup.getMenu(),m_groupsRespDetails.groupCode.getValue(),scripCode);
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.mkt_depth:
                        showMktDepth(scripCode);
                        break;
                    case R.id.delete:
                        deleteRequest(scripCode,scripName,index);
                        break;
                    case R.id.valuation:
                        showValuation(scripCode);
                        break;
                    case R.id.latest_result:
                        showLatestResult(scripCode);
                        break;
                    case R.id.view_charts:
                        showChart(scripCode);
                        break;
                    case R.id.set_script_alert:
                        openScripRateAlert(scripCode);
                        break;
                    case R.id.marginDetails:
                        GlobalClass.showmarginDetailDialog(scripCode);
                        break;
                        default:
                            break;
                }
                return false;
            }
        });

        if (onPopupListener != null)
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                onPopupListener.onPopupClose();
            }
        });
    }


    private void showChart(int scripCode){
        ArrayList<GroupsTokenDetails> grpScripList = getScriptList();
        String scripName = "";
        for (GroupsTokenDetails gsd : grpScripList) {
            if(scripCode == gsd.scripCode.getValue()){
                scripName = gsd.scripName.getValue();
                break;
            }
        }
        Fragment m_fragment = GraphFragment.newInstance(scripCode, scripName);
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true,"");
    }

    private void showLatestResult(int scripCode) {
        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
        if (mktWatch != null && mktWatch.getSegment() == eExch.FNO.value){
            GlobalClass.showToast(GlobalClass.latestContext,"FNO scrip are not allowed");
        }else {
            Fragment m_fragment = new LatestResultFragment(scripCode, getScriptList(),radioButtons);
            GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
        }
    }

    private void showValuation(int scripCode) {
        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
        if (mktWatch != null && mktWatch.getSegment() == eExch.FNO.value){
            GlobalClass.showToast(GlobalClass.latestContext,"FNO scrip are not allowed");
        }else {
            Fragment m_fragment = new ValuetionFragment(scripCode, getScriptList(),radioButtons);
            GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
        }
    }

    private void showMktDepth(int scripCode) {

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scripCode, m_from,getScriptList(),null,
                    ((HomeActivity)GlobalClass.latestContext).SELECTED_RADIO_BTN,false);


        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, m_fragment);
        fragmentTransaction.addToBackStack(Constants.MKT_DEPTH_TAG + "");
        fragmentTransaction.commit();
    }
    private ArrayList<GroupsTokenDetails> getScriptList(){
        ArrayList<GroupsTokenDetails> grpScripList = new ArrayList<>();
        grpScripList.addAll(m_groupsRespDetails.hm_grpTokenDetails.values());
        return grpScripList;
    }
    private void deleteRequest(final int scripCode,final String scripName, final int pos) {
        AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
        m_alertBuilder.setTitle("Ventura Wealth");
        m_alertBuilder.setMessage("Are you sure you want to delete \""+scripName+"\" ?");
        m_alertBuilder.setIcon(R.drawable.ventura_icon);
        m_alertBuilder.setCancelable(true);
        m_alertBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onLongClickListener.onDelete(pos);
                        GlobalClass.groupHandler.deleteTokenFromGroup(m_groupsRespDetails.groupCode.getValue(),scripCode);
                        GlobalClass.notifyMktWatchScreen(eMessageCode.NEW_GROUPLIST.value);
                        dialog.cancel();
                    }
                });
        m_alertBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog m_alertDialog = m_alertBuilder.create();
        m_alertDialog.show();
        m_alertDialog.getButton(m_alertDialog.BUTTON_NEGATIVE).setTextColor(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
        m_alertDialog.getButton(m_alertDialog.BUTTON_POSITIVE).setTextColor(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
    }

    public void openScripRateAlert(int scripCode){
        ArrayList<GroupsTokenDetails> grpScripList = getScriptList();
        String scripName = "";
        for (GroupsTokenDetails gsd : grpScripList) {
            if(scripCode == gsd.scripCode.getValue()){
                scripName = gsd.scripName.getValue();
                break;
            }
        }

        openScriprateAlert(scripName,scripCode);
    }

    public boolean openScriprateAlert(String scripName, int scripCode) {
        try {
            SetScriptRatePopup popup_new = new SetScriptRatePopup(GlobalClass.latestContext, scripName, scripCode);
            android.app.AlertDialog.Builder builder = Dialogs.getAlertBuilder(GlobalClass.latestContext);
            builder.setView(popup_new)
                    .setCancelable(true);
            final android.app.AlertDialog alert = builder.create();
            alert.show();
            popup_new.setCloseLis(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            popup_new.setselectedScript(new SetScriptRatePopup.selectedScript() {
                @Override
                public void selectedScript() {
                    alert.dismiss();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void reqSent(int msgCode) {

    }
}
