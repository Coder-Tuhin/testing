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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Other.StructBuySell;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import adapters.OrderbookAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.eOrderbookSpinnerItems;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import interfaces.OnAlertListener;
import interfaces.OnAdapterRefresh;
import utils.Constants;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.RecyclerItemClickListener;
import utils.UserSession;
import view.AlertBox;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class OrderbookView extends LinearLayout implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        OnAlertListener, OnAdapterRefresh {

    //region [Variables]
    @BindView(R.id.oderbook_recyclerView)
    RecyclerView oderbook_recyclerView;
    @BindView(R.id.oderbook_spinner)
    Spinner orderBk_spinner;
    @BindView(R.id.cancel_allOrdBk_button)
    Button cancelbtn;
    @BindView(R.id.noData)
    TextView noData;
    @BindView(R.id.dataAvailable)
    RelativeLayout dataAvailable;

    private OrderbookAdapter orderbookAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TreeMap<Integer, StructOrderReportReplyRecord_Pointer> orderBKData;
    private StructOrderReportReplyRecord_Pointer selectedOrderBk;
    private boolean isCancel = false;


    public OrderbookView(Context context) {
        super(context);
        ((HomeActivity) context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View mView = LayoutInflater.from(context).inflate(R.layout.oderbook_screen, null);
        ButterKnife.bind(this, mView);
        initialization();
        GlobalClass.orderBkUIHandler = new OrderBookHandler();
        addView(mView);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
    }

    //endregion

    private void initialization() {
        ArrayList<String> spinnerlist = new ArrayList<String>();
        spinnerlist.add(eOrderbookSpinnerItems.ALL.name);
        spinnerlist.add(eOrderbookSpinnerItems.PENDING.name);
        spinnerlist.add(eOrderbookSpinnerItems.FULL_EXECUTED.name);
        spinnerlist.add(eOrderbookSpinnerItems.CANCELLED.name);
        spinnerlist.add(eOrderbookSpinnerItems.OTHERS.name);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getContext(), R.layout.custom_spinner_item, spinnerlist);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        orderBk_spinner.setAdapter(dataAdapter);

        orderBk_spinner.setOnItemSelectedListener(this);
        orderBk_spinner.setSelection(dataAdapter.getPosition(eOrderbookSpinnerItems.PENDING.name));

        if (ObjectHolder.pendingScripCode > 0) {
            orderBKData = GlobalClass.getClsOrderBook().getTreeMap(orderBk_spinner.getSelectedItem().toString(),
                    ObjectHolder.pendingScripCode);
        } else {
            orderBKData = GlobalClass.getClsOrderBook().getTreeMap(orderBk_spinner.getSelectedItem().toString());
        }

        orderbookAdapter = new OrderbookAdapter(this, orderBKData);
        linearLayoutManager = new LinearLayoutManager(GlobalClass.latestContext);

        oderbook_recyclerView.setLayoutManager(linearLayoutManager);
        oderbook_recyclerView.setAdapter(orderbookAdapter);
        // int deviderHeiight = (int) getResources().getDimension(R.dimen.divider_height);
        // oderbook_recyclerView.addItemDecoration(new DividerItemDecoration(ObjectHolder.SILVER,deviderHeiight));

        oderbook_recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), oderbook_recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Fragment fragment = new OrderbookDetailsFragment(orderbookAdapter.getOrderBKData(),
                                orderbookAdapter.getAllBrokerOrdIds(), position);
                        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment, "");
                        fragmentTransaction.addToBackStack("");
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        selectedOrderBk = orderbookAdapter.getOrderBookForPosition(position);

                        PopupMenu popup = new PopupMenu(GlobalClass.latestContext, view);
                        popup.getMenuInflater().inflate(R.menu.orderbook_menu, popup.getMenu());

                        if (selectedOrderBk.finaPendingQty <= 0) {
                            MenuItem modiFyitem = popup.getMenu().findItem(R.id.modify);
                            MenuItem cancelitem = popup.getMenu().findItem(R.id.cancel);
                            modiFyitem.setVisible(false);
                            cancelitem.setVisible(false);
                        }
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                isCancel = false;
                                switch (menuItem.getItemId()) {
                                    case R.id.mktdepth:
                                        showMktDepth();
                                        break;
                                    case R.id.modify:
                                        if (selectedOrderBk.isOrderSentToExchModify()) {
                                            Toast.makeText(GlobalClass.latestContext, selectedOrderBk.getERR_ORDER_MODIFY_SENTTOEXCH(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            modifyBtnClick();
                                        }
                                        break;
                                    case R.id.cancel:
                                        if (selectedOrderBk.isOrderSentToExchCancel()) {
                                            Toast.makeText(GlobalClass.latestContext, selectedOrderBk.getERR_ORDER_CANCEL_SENTTOEXCH(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            cancelBtnClick();
                                        }
                                        break;
                                }
                                return false;
                            }
                        });
                    }

                }));
        cancelbtn.setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

   /* @Override
    public void onResume() {
        super.onResume();
        GlobalClass.orderBkUIHandler = new OrderBookHandler();
        if (orderBk_spinner != null){
            reloadData(orderBk_spinner.getSelectedItem().toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.orderBkUIHandler = null;
        ObjectHolder.pendingScripCode = 0;
    }*/


    private void reloadData(String str) {
        GlobalClass.dismissdialog();
        //set pending scrip = 0 if all
        if (str.equalsIgnoreCase(eOrderbookSpinnerItems.ALL.name)) {
            ObjectHolder.pendingScripCode = 0;
        }
        if (ObjectHolder.pendingScripCode > 0) {
            orderBKData = GlobalClass.getClsOrderBook().getTreeMap(orderBk_spinner.getSelectedItem().toString(), ObjectHolder.pendingScripCode);
        } else {
            orderBKData = GlobalClass.getClsOrderBook().getTreeMap(orderBk_spinner.getSelectedItem().toString());
        }
        orderbookAdapter.refreshData(orderBKData);
        loadScripCodeList();
    }

    private void loadScripCodeList() {
        try{
            //if (GlobalClass.broadCastReg.isNormalMKt()) {
                SendDataToBCServer bcadata = new SendDataToBCServer(null, eMessageCode.NEW_MULTIPLE_MARKETWATCH, orderbookAdapter.getAllScripCodeList());
                bcadata.execute();
            //}
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        reloadData(orderBk_spinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_allOrdBk_button:
                cancelAllBtnClick();
                break;
            default:
                break;
        }
    }

    private void showMktDepth() {

        int scriptCode = selectedOrderBk.scripCode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(selectedOrderBk.getFormatedScripName(false));
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.ORDERBOOK, grplist, null,
                ((HomeActivity) super.getContext()).SELECTED_RADIO_BTN, false);

        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.DEPTH.name);
    }

    private void modifyBtnClick() {
        int scriptCode = selectedOrderBk.scripCode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(selectedOrderBk.getFormatedScripName(false));
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = new StructBuySell();
        if (selectedOrderBk.buySell.getValue() == 'B') {
            buySell.buyOrSell = eOrderType.BUY;
        } else {
            buySell.buyOrSell = eOrderType.SELL;
        }
        buySell.modifyOrPlace = eOrderType.MODIFY;
        buySell.order = selectedOrderBk;
        buySell.showDepth = eShowDepth.ORDERBOOK;
        Fragment m_fragment;

//        if(UserSession.getClientResponse().getServerType() == eServerType.RC){
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.ORDERBOOK, grplist, buySell,
                ((HomeActivity) super.getContext()).SELECTED_RADIO_BTN, false);
//        }else{
//            m_fragment = new MktdepthFragment(scriptCode, eShowDepth.ORDERBOOK,grplist,buySell,
//                    homeActivity.SELECTED_RADIO_BTN,true);
//        }
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.DEPTH.name);
    }

    private void cancelBtnClick() {
        new AlertBox(GlobalClass.latestContext, "Yes", "No", "Are you sure you want to cancel this order?", this, "cancel");
    }

    public void cancelAllBtnClick() {
        ArrayList<StructOrderReportReplyRecord_Pointer> orderBKData = GlobalClass.getClsOrderBook().getAllPendingOrderForCancel();
        if (orderBKData.size() > 0) {
            new AlertBox(GlobalClass.latestContext, "Yes", "No", "Are you sure you want to cancel all pending orders?", this,
                    "cancelall");
        } else {
            new AlertBox(GlobalClass.latestContext, "", "OK", "There are no orders under pending status.", false);
            //GlobalClass.commonApi.showDialogAlert(GlobalClass.latestContext,"There is no pending order.");
        }
    }

    public void cancelAllPendingOrder() {
        ArrayList<StructOrderReportReplyRecord_Pointer> orderBKData = GlobalClass.getClsOrderBook().getAllPendingOrderForCancel();
        if (orderBKData.size() > 0) {
            for (int i = 0; i < orderBKData.size(); i++) {
                StructOrderReportReplyRecord_Pointer _selectedOrderBk = orderBKData.get(i);
                GlobalClass.getClsOrderBook().cancelOrderRequest(_selectedOrderBk);
            }
        } else {
            GlobalClass.showAlertDialog(Constants.CANCEL_PENDQTY_MSG);
        }
    }

    private void cancelSingleOrder() {
        if (selectedOrderBk != null) {
            GlobalClass.getClsOrderBook().cancelOrderRequest(selectedOrderBk);
        }
    }

    @Override
    public void onOk(String tag) {
        if (tag.equalsIgnoreCase("cancelall")) {
            cancelAllPendingOrder();
            //requestForAllScripDetailForCancelAll();
        } else if (tag.equalsIgnoreCase("cancel")) {
            cancelSingleOrder();
        }
    }

    @Override
    public void onCancel(String tag) {

    }

    @Override
    public void onMessageRefresh(boolean hasMsg) {
        if (hasMsg) {
            noData.setVisibility(View.GONE);
        } else {
            noData.setVisibility(View.VISIBLE);
        }
    }

    class OrderBookHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case ORDER_BOOk_RESP: {
                                reloadData(orderBk_spinner.getSelectedItem().toString());
                            }
                            break;
                            case LITE_MW: {
                                if(orderbookAdapter != null) {
                                    int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                    orderbookAdapter.updateRate(scripCode);
                                }
                            }
                            break;
                            case MULTI_SCRIPTDETAILS:
                                cancelAllPendingOrder();
                                break;
                            case SCRIPT_DETAILS_Response:
                                if (isCancel) {
                                    cancelSingleOrder();
                                    isCancel = false;
                                }
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        GlobalClass.onError("TradeLoginHandler : ", ex);
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
