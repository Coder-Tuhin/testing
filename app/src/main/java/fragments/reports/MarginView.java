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

import Structure.Response.RC.StructMarginReportReplyRecord_Pointer;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
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
public class MarginView extends LinearLayout {

    private TextView openMargin,bfPositionMargin,bfPositionMarginTitle,holdingMargin,additionalMargin,mBOpenPosition,
            mBPendingOrder,commTile,commdityMargin,
            availableMargin,netOptionPrem,avlmarginopt_margin_textview,
            ledgerBalance,sDT1sales, sDT2sales, unutilizeColl,t1booked,
            mtmlossUnsettledPosition,reversalintraday;
    private LinearLayout linearLayout;
    private RelativeLayout relative_one,relative_two;

    public MarginView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.margin_screen, null);
        initialization(layout);
        addView(layout);
        GlobalClass.marginUIHandler = new MarginHandler();
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
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT,ft);
        linearLayout.setOrientation(orntation);
        relative_one.setLayoutParams(param);
        if (orntation == LinearLayout.HORIZONTAL){
            param.setMargins((int) getResources().getDimension(R.dimen.common_margine),0,0,0);
        }else{
            param.setMargins(0,(int) getResources().getDimension(R.dimen.common_margine),0,0);
        }
        relative_two.setLayoutParams(param);
    }

    private void initialization(View layout) {

        bfPositionMargin = (TextView) layout.findViewById(R.id.marginbf_margin);
        bfPositionMarginTitle = (TextView) layout.findViewById(R.id.marginbf_title);

        openMargin = (TextView) layout.findViewById(R.id.opening_margin);
        holdingMargin = (TextView) layout.findViewById(R.id.holding_margin_textview);
        additionalMargin = (TextView) layout.findViewById(R.id.additional_margin_textview);
        mBOpenPosition = (TextView) layout.findViewById(R.id.mbopen_position_textview);
        mBPendingOrder = (TextView) layout.findViewById(R.id.mbpending_order_textview);
        commTile = (TextView) layout.findViewById(R.id.comm_margin_titleview);
        commdityMargin = (TextView) layout.findViewById(R.id.comm_margin_textview);

        availableMargin = (TextView) layout.findViewById(R.id.available_margin_textview);
        netOptionPrem = (TextView) layout.findViewById(R.id.netoptionprem_margin_textview);
        avlmarginopt_margin_textview = (TextView) layout.findViewById(R.id.avlmarginopt_margin_textview);

        ledgerBalance = (TextView) layout.findViewById(R.id.ledger_balance_textview);
        sDT1sales = (TextView) layout.findViewById(R.id.sdt1sales_textview);
        sDT2sales = (TextView) layout.findViewById(R.id.sdt2sales_textview);
        unutilizeColl = (TextView) layout.findViewById(R.id.unutilizecoll_textview);
        t1booked = (TextView) layout.findViewById(R.id.t1booked_textview);
        mtmlossUnsettledPosition = (TextView) layout.findViewById(R.id.mtmloss_textview);
        reversalintraday = (TextView) layout.findViewById(R.id.reversalintradaypeak_textview);

        linearLayout = (LinearLayout) layout.findViewById(R.id.linear);
        relative_one = (RelativeLayout) layout.findViewById(R.id.margin_relativeone);
        relative_two = (RelativeLayout) layout.findViewById(R.id.margin_relativetwo);
        setMarginvalue();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (dm.widthPixels>dm.heightPixels){
            setParam(1f,LinearLayout.HORIZONTAL);
        }else {
            setParam(0f,LinearLayout.VERTICAL);
        }
    }
    private void setMarginvalue() {

        //  GlobalClass.showdialog("Please waitggg...");
        if(ObjectHolder.isCommodityAllow){
            commTile.setVisibility(View.VISIBLE);
            commdityMargin.setVisibility(View.VISIBLE);
        }
        else {
            commTile.setVisibility(View.GONE);
            commdityMargin.setVisibility(View.GONE);
        }
        if(ObjectHolder.isMarginBF){
            bfPositionMargin.setVisibility(VISIBLE);
            bfPositionMarginTitle.setVisibility(VISIBLE);
        }else{
            bfPositionMargin.setVisibility(GONE);
            bfPositionMarginTitle.setVisibility(GONE);
        }

        StructMarginReportReplyRecord_Pointer margininfo = GlobalClass.getClsMarginHolding().getMarginDetail();

        openMargin.setText(""+ Formatter.formatter.format(margininfo.openingMargin.getValue()));
        bfPositionMargin.setText(""+ Formatter.formatter.format(margininfo.marginOfBFPosition.getValue()));

        holdingMargin.setText(""+Formatter.formatter.format(margininfo.holdingsMargin.getValue()));
        additionalMargin.setText(""+Formatter.formatter.format(margininfo.additionalMarign.getValue()));
        mBOpenPosition.setText(""+Formatter.formatter.format(margininfo.marginForOpenPos.getValue()));
        mBPendingOrder.setText(""+Formatter.formatter.format(margininfo.marginForPendOrder.getValue()));
        commdityMargin.setText(""+Formatter.formatter.format(margininfo.commodityMargin.getValue()));
        availableMargin.setText(""+Formatter.formatter.format(margininfo.availableMargin.getValue()));
        netOptionPrem.setText(""+Formatter.formatter.format(margininfo.fOMgnPayable.getValue()));
        avlmarginopt_margin_textview.setText(""+Formatter.formatter.format(margininfo.fOBalance.getValue()));

        ledgerBalance.setText(""+Formatter.formatter.format(margininfo.ledgerBalance.getValue()));
        sDT1sales.setText(""+Formatter.formatter.format(margininfo.t1Short.getValue()));
        sDT2sales.setText(""+Formatter.formatter.format(margininfo.t2Short.getValue()));
        unutilizeColl.setText(""+Formatter.formatter.format(margininfo.fOExcessCol.getValue()));
        t1booked.setText(""+Formatter.formatter.format(margininfo.withdrawable.getValue()));
        mtmlossUnsettledPosition.setText(""+Formatter.formatter.format(margininfo.mFDebit.getValue()));
        reversalintraday.setText(""+Formatter.formatter.format(margininfo.reversalIntrPeakMargin.getValue()));
    }
    class MarginHandler extends Handler {
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
