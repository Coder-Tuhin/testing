package fragments.reports;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import Structure.Other.StructMobNetPosition;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import adapters.NetpositionAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import utils.DividerItemDecoration;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.RecyclerItemClickListener;
import utils.ScreenColor;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
public class NetpositionView extends LinearLayout implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.net_position_recyclerView)RecyclerView netPos_recycler;
    @BindView(R.id.dataAvailable)LinearLayout dataAvailable;
    @BindView(R.id.noData)TextView noData;

    TextView totBookedValue,totMTMValue,totBuyValue,totSellValue;
    Spinner netPosSpn;

    private NetpositionAdapter netpositionAdapter;
    private LinearLayoutManager linearLayoutManager;

    public NetpositionView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.net_position,null);
        ButterKnife.bind(this, layout);
        initialization(layout);
        addView(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());
        GlobalClass.netPositionUIHandler = new NetPositionHandler();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initialization(View layout) {

        totBookedValue = (TextView) layout.findViewById(R.id.booked_netpos_textview);
        totMTMValue = (TextView) layout.findViewById(R.id.mtm_netpos_textview);
        totBuyValue = (TextView) layout.findViewById(R.id.totbuy_netpos_textview);
        totSellValue = (TextView) layout.findViewById(R.id.totsell_netpos_textview);

        netPosSpn = (Spinner) layout.findViewById(R.id.net_position_spinner);
        ArrayList<String> spinnerlist = new ArrayList<String>();
        spinnerlist.add("All");
        spinnerlist.add("Open");
        spinnerlist.add("Squared");
        if(UserSession.getLoginDetailsModel().isIntradayDelivery()) {
            spinnerlist.add("Delivery");
            spinnerlist.add("Intraday");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getContext(), R.layout.custom_spinner_item,spinnerlist);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        netPosSpn.setAdapter(dataAdapter);
        netPosSpn.setOnItemSelectedListener(null);
        netPosSpn.setOnItemSelectedListener(this);

        totBookedValue.setText(GlobalClass.getClsNetPosn().getTotalBookedPL());
        totMTMValue.setText(GlobalClass.getClsNetPosn().getTotalMTMPL());
        totSellValue.setText(GlobalClass.getClsNetPosn().getTotalSellValue());
        totBuyValue.setText(GlobalClass.getClsNetPosn().getTotalBuyValue());

        ArrayList<StructMobNetPosition> allNetPosition = GlobalClass.getClsNetPosn().getAllNetPosition(netPosSpn.getSelectedItem().toString());

        netpositionAdapter = new NetpositionAdapter(super.getContext(),allNetPosition);
        linearLayoutManager = new LinearLayoutManager(super.getContext());
        netPos_recycler.setLayoutManager(linearLayoutManager);
        netPos_recycler.setAdapter(netpositionAdapter);
        int deviderHeiight = (int) getResources().getDimension(R.dimen.divider_height);
        netPos_recycler.addItemDecoration(new DividerItemDecoration(ScreenColor.SILVER,deviderHeiight));
        netPos_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),netPos_recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Fragment fragment = new NetpositiondetailsFragment(netpositionAdapter.getAllNetPositionData(),position);
                FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment,"");
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
            @Override
            public void onItemLongClick(View view, int position) {
                StructMobNetPosition selectedNetPs = netpositionAdapter.getNetPositionForPosition(position);

                PopupMenu popup = new PopupMenu(GlobalClass.latestContext, view);
                popup.getMenuInflater().inflate(R.menu.orderbook_menu, popup.getMenu());

                MenuItem modiFyitem = popup.getMenu().findItem(R.id.modify);
                MenuItem cancelitem = popup.getMenu().findItem(R.id.cancel);
                modiFyitem.setVisible(false);
                cancelitem.setVisible(false);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.mktdepth:
                                showMktDepth(selectedNetPs);
                                break;

                        }
                        return false;
                    }
                });
            }
        }));
        reloadViewForNoData(GlobalClass.getClsNetPosn().getNetPositionSize());
    }

    private void showMktDepth(StructMobNetPosition trdReport) {

        int scriptCode = trdReport.scripCode;
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(trdReport.getFormatedScripName(false));
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.NETPOSITION,grplist,null,
                ((HomeActivity)super.getContext()).SELECTED_RADIO_BTN,false);

        GlobalClass.fragmentTransaction(m_fragment,R.id.container_body,true, eFragments.DEPTH.name);
    }
    private void reloadData(){

        GlobalClass.dismissdialog();
        totBookedValue.setText(GlobalClass.getClsNetPosn().getTotalBookedPL());
        totSellValue.setText(GlobalClass.getClsNetPosn().getTotalSellValue());
        totBuyValue.setText(GlobalClass.getClsNetPosn().getTotalBuyValue());
        totMTMValue.setText(GlobalClass.getClsNetPosn().getTotalMTMPL());

        ArrayList<StructMobNetPosition> allNetPosition = GlobalClass.getClsNetPosn().getAllNetPosition(netPosSpn.getSelectedItem().toString());
        netpositionAdapter.reloadData(allNetPosition);
        reloadViewForNoData(GlobalClass.getClsNetPosn().getNetPositionSize());
    }
    private void reloadViewForNoData(int count){

        if(count <= 0){
            noData.setVisibility(View.VISIBLE);
            dataAvailable.setVisibility(View.GONE);
        }
        else{
            noData.setVisibility(View.GONE);
            dataAvailable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        reloadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    class NetPositionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case LITE_MW:
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if(netpositionAdapter != null && GlobalClass.getClsNetPosn().isConatinScripCode(scripCode)){
                                    netpositionAdapter.refreshItem(scripCode);
                                    totBookedValue.setText(GlobalClass.getClsNetPosn().getTotalBookedPL());
                                    totMTMValue.setText(GlobalClass.getClsNetPosn().getTotalMTMPL());
                                }
                                break;
                            case TRADE_BOOK_RESP:
                                reloadData();
                            break;

                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        GlobalClass.onError("TradeLoginHandler : " ,ex);
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}