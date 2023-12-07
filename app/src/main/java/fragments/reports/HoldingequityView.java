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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import adapters.HoldingEquityAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.DividerItemDecoration;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.RecyclerItemClickListener;
import utils.ScreenColor;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 10/29/2016.
 */
public class HoldingequityView extends LinearLayout {
    @BindView(R.id.report_recycler)RecyclerView report_recycler;
    @BindView(R.id.noData)TextView noData;

    private HoldingEquityAdapter holdingEquityAdapter;
    private LinearLayoutManager linearLayoutManager;


    public HoldingequityView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.report_recycler,null);
        ButterKnife.bind(this, layout);
        GlobalClass.holdingEquityUIHandler = new HoldingEquityHandler();
        initialization();
        addView(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        relaodData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initialization() {

        holdingEquityAdapter = new HoldingEquityAdapter(super.getContext(),"equity");
        linearLayoutManager = new LinearLayoutManager(super.getContext());
        report_recycler.setLayoutManager(linearLayoutManager);
        report_recycler.setAdapter(holdingEquityAdapter);
        int deviderHeight = (int) getResources().getDimension(R.dimen.divider_height);
        report_recycler.addItemDecoration(new DividerItemDecoration(ScreenColor.SILVER,deviderHeight));
        report_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), report_recycler,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Fragment fragment = new HoldingEquityDetailsFragment(holdingEquityAdapter.getHolding(), position);
                FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment,"");
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void relaodData(){
        GlobalClass.dismissdialog();
        ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingEquity = GlobalClass.getClsMarginHolding().getHoldingEquity();
        holdingEquityAdapter.reloadData(holdingEquity);
        reloadViewForNoData(holdingEquity.size());
    }

    private void reloadViewForNoData(int count){
        if(count <= 0){
            noData.setVisibility(View.VISIBLE);
            report_recycler.setVisibility(View.GONE);
        }else{
            noData.setVisibility(View.GONE);
            report_recycler.setVisibility(View.VISIBLE);
        }
    }

    class HoldingEquityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case HOLDING_RESPONSE: {
                                relaodData();
                            }
                            break;
                            case LITE_MW:
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if(holdingEquityAdapter != null){
                                    holdingEquityAdapter.refreshItem(scripCode);
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
