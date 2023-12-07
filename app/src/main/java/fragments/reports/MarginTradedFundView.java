package fragments.reports;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import java.util.List;

import Structure.Response.RC.StructmarginTrade;
import adapters.MarginTradeAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 10-Oct-2017.
 */
public class MarginTradedFundView extends LinearLayout {
    @BindView(R.id.fundRecycler)
    RecyclerView fundRecycler;
    @BindView(R.id.colletralRecycler)
    RecyclerView colletralRecycler;

    private MarginTradeAdapter fundAdapter;
    private MarginTradeAdapter collateralAdapter;
    public MarginTradedFundView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.margin_trading_screen, null);
        ButterKnife.bind(this,layout);
        initialization();
        addView(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

    }

    private void initialization() {
        GlobalClass.marginTradeHandler = new MarginTradeHandler();
        List<StructmarginTrade> mList = GlobalClass.getClsMarginHolding().getMarginTrade();
        fundAdapter = new MarginTradeAdapter(mList,true);
        collateralAdapter = new MarginTradeAdapter(mList,false);
        fundRecycler.setAdapter(fundAdapter);
        colletralRecycler.setAdapter(collateralAdapter);
    }

    class MarginTradeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case MARGINTRADE_REQ:
                                refreshAdapter();
                            break;
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void refreshAdapter() {
        GlobalClass.dismissdialog();
        List<StructmarginTrade> mList = GlobalClass.getClsMarginHolding().getMarginTrade();
        fundAdapter.refreshAdapter(mList,true);
        collateralAdapter.refreshAdapter(mList,false);
    }
}
