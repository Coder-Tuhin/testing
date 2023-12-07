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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Response.RC.StructOCOOrdBkDet;
import Structure.Response.RC.StructOCOPosnDet;
import adapters.BracketOrderbookAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderbookSpinnerItems;
import interfaces.OnAdapterRefresh;
import interfaces.OnAlertListener;
import utils.GlobalClass;
import utils.RecyclerItemClickListener;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class BracketOrderbookView extends LinearLayout implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        OnAlertListener, OnAdapterRefresh {

    //region [Variables]
    @BindView(R.id.oderbook_recyclerView)RecyclerView oderbook_recyclerView;
    @BindView(R.id.oderbook_spinner)Spinner orderBk_spinner;
    @BindView(R.id.cancel_allOrdBk_button)Button cancelbtn;
    @BindView(R.id.noData)TextView noData;
    @BindView(R.id.dataAvailable)RelativeLayout dataAvailable;

    private BracketOrderbookAdapter orderbookAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TreeMap<String, StructOCOOrdBkDet> orderBKData;
    private StructOCOPosnDet selectedOrderBk;
    private boolean isCancel = false;

    public BracketOrderbookView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View mView = LayoutInflater.from(context).inflate(R.layout.oderbook_screen,null);
        ButterKnife.bind(this, mView);
        initialization();
        GlobalClass.bracketOrderBkUIHandler = new BracketOrderBookHandler();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        addView(mView);
    }
    //endregion

    private void initialization() {

        ArrayList<String> spinnerlist = new ArrayList<String>();
        spinnerlist.add(eOrderbookSpinnerItems.ALL.name);
        spinnerlist.add(eOrderbookSpinnerItems.PENDING.name);
        spinnerlist.add(eOrderbookSpinnerItems.FULL_EXECUTED.name);
        spinnerlist.add(eOrderbookSpinnerItems.CANCELLED.name);
        spinnerlist.add(eOrderbookSpinnerItems.OTHERS.name);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getContext(),R.layout.custom_spinner_item,spinnerlist);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        orderBk_spinner.setAdapter(dataAdapter);

        orderBk_spinner.setOnItemSelectedListener(this);
        orderBk_spinner.setSelection(dataAdapter.getPosition(eOrderbookSpinnerItems.ALL.name));

        orderBKData = GlobalClass.getClsBracketOrderBook().getTreeMap(orderBk_spinner.getSelectedItem().toString());

        orderbookAdapter = new BracketOrderbookAdapter(this,orderBKData);
        linearLayoutManager = new LinearLayoutManager(GlobalClass.latestContext);

        oderbook_recyclerView.setLayoutManager(linearLayoutManager);
        oderbook_recyclerView.setAdapter(orderbookAdapter);

        oderbook_recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), oderbook_recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        Fragment fragment = new BracketOrderbookDetailsFragment(orderbookAdapter.getOrderBKData(),
                                orderbookAdapter.getAllBrokerOrdIds(), position);
                        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment,"");
                        fragmentTransaction.addToBackStack("");
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));
        cancelbtn.setVisibility(View.GONE);
        cancelbtn.setOnClickListener(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void reloadData(String str){
        GlobalClass.dismissdialog();

        orderBKData = GlobalClass.getClsBracketOrderBook().getTreeMap(orderBk_spinner.getSelectedItem().toString());
        orderbookAdapter.refreshData(orderBKData);
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
            default:
                break;
        }
    }
    @Override
    public void onOk(String tag) {

    }

    @Override
    public void onCancel(String tag) {

    }

    @Override
    public void onMessageRefresh(boolean hasMsg) {
        if (hasMsg){
            noData.setVisibility(View.GONE);
        }else {
            noData.setVisibility(View.VISIBLE);
        }
    }

    class BracketOrderBookHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case BRACKET_ORDER_REPORT: {
                                reloadData(orderBk_spinner.getSelectedItem().toString());
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