package fragments.reports;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import Structure.Response.RC.StructMarginReportReplyRecord_Pointer;
import Structure.Response.RC.StructMgnInfoRes;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class MarginViewRC extends LinearLayout {

    private TextView ledgerBalance,mrginBfdPosition,valueHoldings,fundINOut,
            bookedPLMTMPL,adjustmentADMIN,netMgnAvlbTrading,pendingOrderMarginCASH,
            pendingOrderMarginFNO,pendingOrderMargin,openPositionMarginCASH,
            openPositionMarginFNO,openPositionMargin,charges,totalMarginUtilised,
            netMarginAvlCASHFNO,premiumReleased, premiumUtilized,netMarginAvlOPTION;
    ;
    private LinearLayout linearLayout;
    private RelativeLayout relative_one;

    public MarginViewRC(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.margin_screen_rc, null);
        initialization(layout);
        addView(layout);
        GlobalClass.marginUIHandler = new MarginHandlerRC();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setParam(1f,LinearLayout.HORIZONTAL);
        }else {
            setParam(0f,LinearLayout.VERTICAL);
        }
    }

    private void setParam(float ft,int orntation) {
        LayoutParams param = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,ft);
        linearLayout.setOrientation(orntation);
        relative_one.setLayoutParams(param);
    }

    private void initialization(View layout) {

        ledgerBalance = (TextView) layout.findViewById(R.id.ledger_balance_value);
        mrginBfdPosition = (TextView) layout.findViewById(R.id.margin_bfd_position_value);
        valueHoldings = (TextView) layout.findViewById(R.id.value_holding_value);
        fundINOut = (TextView) layout.findViewById(R.id.funds_inout_value);
        bookedPLMTMPL = (TextView) layout.findViewById(R.id.bookedpl_mtmpl_value);
        adjustmentADMIN = (TextView) layout.findViewById(R.id.adjustments_admin_value);
        netMgnAvlbTrading = (TextView) layout.findViewById(R.id.net_mgn_avl_trading_value);
        pendingOrderMarginCASH = (TextView) layout.findViewById(R.id.pend_ord_mgn_cash_value);
        pendingOrderMarginFNO = (TextView) layout.findViewById(R.id.pend_ord_mgn_fno_value);
        pendingOrderMargin = (TextView) layout.findViewById(R.id.pend_ord_mgn_value);
        openPositionMarginCASH = (TextView) layout.findViewById(R.id.open_position_mgn_cash_value);
        openPositionMarginFNO = (TextView) layout.findViewById(R.id.open_position_mgn_fno_value);
        openPositionMargin = (TextView) layout.findViewById(R.id.open_position_mgn_value);
        charges = (TextView) layout.findViewById(R.id.charges_value);
        totalMarginUtilised = (TextView) layout.findViewById(R.id.total_mgn_utilised_value);
        netMarginAvlCASHFNO = (TextView) layout.findViewById(R.id.net_mgn_avl_cashfuture_value);
        premiumReleased = (TextView) layout.findViewById(R.id.premium_released_value);
        premiumUtilized = (TextView) layout.findViewById(R.id.premium_utilized_value);
        netMarginAvlOPTION = (TextView) layout.findViewById(R.id.net_mgn_avl_option_value);

        linearLayout = (LinearLayout) layout.findViewById(R.id.linear);
        relative_one = (RelativeLayout) layout.findViewById(R.id.margin_relativeone);
        setMarginvalue();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (dm.widthPixels>dm.heightPixels){
            setParam(1f,LinearLayout.HORIZONTAL);
        }else {
            setParam(0f,LinearLayout.VERTICAL);
        }
    }

    private void setMarginvalue() {

        StructMgnInfoRes margininfo = GlobalClass.getClsMarginHolding().getMarginDetailRC();

        ledgerBalance.setText(""+ Formatter.formatter.format(margininfo.ledBal.getValue()));
        mrginBfdPosition.setText(""+Formatter.formatter.format(margininfo.initMgn.getValue())); //need to know
        valueHoldings.setText(""+Formatter.formatter.format(margininfo.pdhv.getValue()));
        fundINOut.setText(""+Formatter.formatter.format(margininfo.recPay.getValue()));
        bookedPLMTMPL.setText(""+Formatter.formatter.format(margininfo.pl.getValue()));
        adjustmentADMIN.setText(""+Formatter.formatter.format(margininfo.adhoc.getValue()));
        netMgnAvlbTrading.setText(""+Formatter.formatter.format(margininfo.getMarginAvlForTrading()));

        pendingOrderMarginCASH.setText(""+Formatter.formatter.format(margininfo.mbpoCash.getValue()));
        pendingOrderMarginFNO.setText(""+Formatter.formatter.format(margininfo.getMBPOFno()));
        pendingOrderMargin.setText(""+Formatter.formatter.format(margininfo.getMBPO()));

        openPositionMarginCASH.setText(""+ Formatter.formatter.format(margininfo.mbopCash.getValue()));
        openPositionMarginFNO.setText(""+Formatter.formatter.format(margininfo.getMBOPFno()));
        openPositionMargin.setText(""+Formatter.formatter.format(margininfo.getMBOP()));

        charges.setText(""+Formatter.formatter.format(margininfo.getCharges()));

        totalMarginUtilised.setText(""+Formatter.formatter.format(margininfo.getMrgnUtilised()));

        netMarginAvlCASHFNO.setText(""+Formatter.formatter.format(margininfo.AvlbMgn()));
        premiumReleased.setText(""+Formatter.formatter.format(margininfo.getPremCrToDisp()));
        //premiumUtilized.setText(""+Formatter.formatter.format(margininfo.getOptPremPend()+margininfo.getOptPremPosn()));
        netMarginAvlOPTION.setText(""+Formatter.formatter.format(margininfo.AvlbMgnOPT()));
    }

    class MarginHandlerRC extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case MARGIN: {
                                setMarginvalue();
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
