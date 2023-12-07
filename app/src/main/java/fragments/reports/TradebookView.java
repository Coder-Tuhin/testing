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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import adapters.TradebookAdapter;
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
public class TradebookView extends LinearLayout {
    @BindView(R.id.report_recycler)RecyclerView report_recycler;
    @BindView(R.id.noData)TextView noData;
    TradebookAdapter tradebookAdapter;
    LinearLayoutManager linearLayoutManager;

    public TradebookView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.report_recycler,null);
        ButterKnife.bind(this, layout);
        initialization();
        addView(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        GlobalClass.tradeBKUIHandler = new TradeBookHandler();
        reloadData();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        GlobalClass.tradeBKUIHandler = null;
    }

    private void initialization() {
        TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeTreeMap = GlobalClass.getClsTradeBook().getTradeTreeMap();
        tradebookAdapter = new TradebookAdapter(super.getContext(),tradeTreeMap);
        linearLayoutManager = new LinearLayoutManager(super.getContext());
        report_recycler.setLayoutManager(linearLayoutManager);
        report_recycler.setAdapter(tradebookAdapter);
        int deviderHeight = (int) getResources().getDimension(R.dimen.divider_height);
        report_recycler.addItemDecoration(new DividerItemDecoration(ScreenColor.SILVER, deviderHeight));
        report_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),report_recycler,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Fragment fragment = new TradeboodetailsFragment(tradebookAdapter.getAlltrade(), tradebookAdapter.getAllTradeIds(), position);
                        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment,"");
                        fragmentTransaction.addToBackStack("");
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        StructTradeReportReplyRecord_Pointer selectedTrdBk = tradebookAdapter.getTradeBookForPosition(position);

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
                                        showMktDepth(selectedTrdBk);
                                        break;

                                }
                                return false;
                            }
                        });
                    }
                }));
        reloadViewForNoData(tradeTreeMap.size());
    }
    private void showMktDepth(StructTradeReportReplyRecord_Pointer trdReport) {

        int scriptCode = trdReport.scripCode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(trdReport.getFormatedScripName(false));
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.TRADEBOOK,grplist,null,
                ((HomeActivity)super.getContext()).SELECTED_RADIO_BTN,false);

        GlobalClass.fragmentTransaction(m_fragment,R.id.container_body,true, eFragments.DEPTH.name);
    }

    private void reloadData(){
        GlobalClass.dismissdialog();
        TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeTreeMap = GlobalClass.getClsTradeBook().getTradeTreeMap();
        tradebookAdapter.refreshData(tradeTreeMap);
        reloadViewForNoData(tradeTreeMap.size());
    }

    private void reloadViewForNoData(int count){
        if(count <= 0){
            noData.setVisibility(View.VISIBLE);
            report_recycler.setVisibility(View.GONE);
        } else{
            noData.setVisibility(View.GONE);
            report_recycler.setVisibility(View.VISIBLE);
        }
    }

    class TradeBookHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case TRADE_BOOK_RESP: {
                                reloadData();
                            }
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
