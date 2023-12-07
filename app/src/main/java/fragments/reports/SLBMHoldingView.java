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

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.ArrayList;

import Structure.Response.RC.TSLBMHoldingsReportReplyRecord;
import adapters.SLBMHoldingAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.DividerItemDecoration;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.RecyclerItemClickListener;
import utils.ScreenColor;
import utils.UserSession;

public class SLBMHoldingView extends LinearLayout {
    @BindView(R.id.report_recycler)
    RecyclerView report_recycler;
    @BindView(R.id.noData)
    TextView noData;

    private SLBMHoldingAdapter holdingEquityAdapter;
    private LinearLayoutManager linearLayoutManager;


    public SLBMHoldingView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.report_recycler,null);
        ButterKnife.bind(this, layout);
        initialization();
        addView(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
    }

    public static Handler slbmholdingUIHandler;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (slbmholdingUIHandler == null)
            slbmholdingUIHandler = new SLBMHoldingHandler();
        relaodData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        slbmholdingUIHandler = null;
    }

    private void initialization() {
        slbmholdingUIHandler = new SLBMHoldingHandler();
        // ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingEquity = GlobalClass.clsMarginHolding.getHoldingEquity();
        holdingEquityAdapter = new SLBMHoldingAdapter(super.getContext(),"slbm");
        linearLayoutManager = new LinearLayoutManager(super.getContext());
        report_recycler.setLayoutManager(linearLayoutManager);
        report_recycler.setAdapter(holdingEquityAdapter);
        int deviderHeight = (int) getResources().getDimension(R.dimen.divider_height);
        report_recycler.addItemDecoration(new DividerItemDecoration(ScreenColor.SILVER,deviderHeight));
        report_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), report_recycler,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Fragment fragment = new SLBMHoldingDetails(holdingEquityAdapter.getHolding(), position);
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
        //  reloadViewForNoData(holdingEquity.size());
    }

    private void relaodData(){
        GlobalClass.dismissdialog();
        ArrayList<TSLBMHoldingsReportReplyRecord> holdingEquity = GlobalClass.getClsMarginHolding().getSLBMHoldingEquity();
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

    class SLBMHoldingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case SLBM_HOLDING_REPORT: {
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

