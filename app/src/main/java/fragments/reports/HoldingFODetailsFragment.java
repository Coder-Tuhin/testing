package fragments.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Other.StructBuySell;
import Structure.Other.StructMobNetPosition;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import Structure.Response.RC.StructNetOblization;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eExch;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class HoldingFODetailsFragment extends Fragment implements View.OnClickListener {

    private ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingFOList;
    private StructHoldingsReportReplyRecord_Pointer selectedHolding;
    private int selectedPosition;
    private HomeActivity homeActivity;
    private NumberFormat formatter;

    public HoldingFODetailsFragment(){super();}

    @SuppressLint("ValidFragment")
    public HoldingFODetailsFragment (ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingFO, int position) {
        try {
            this.holdingFOList = holdingFO;
            this.selectedPosition = position;
            selectedHolding = holdingFOList.get(selectedPosition);
            formatter = Formatter.getFormatter(selectedHolding.exchange);

        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity.CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        homeActivity.findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.holdingfo_details, container, false);
        ButterKnife.bind(this,layout);

        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.holdingFOdetailsUIHandler = new HoldingFNODetailsHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.holdingFOdetailsUIHandler = null;
        homeActivity.findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    @BindView(R.id.scripName)
    TextView scripName;
    @BindView(R.id.bfQty)
    TextView bfQty;
    @BindView(R.id.prevClose)
    TextView prevClose;
    @BindView(R.id.totalBuy)
    TextView totalBuy;
    @BindView(R.id.totalBuyAvg)
    TextView totalBuyAvg;
    @BindView(R.id.totalSell)
    TextView totalSell;
    @BindView(R.id.totalSellAvg)
    TextView totalSellAvg;
    @BindView(R.id.netQty)
    TextView netQty;
    @BindView(R.id.ltp)
    TextView ltp;
    @BindView(R.id.netOblization)
    TextView netOblization;
    @BindView(R.id.pageCount)
    TextView pageCount;
    @BindView(R.id.squareOff)
    LinearLayout squareOff;
    @BindView(R.id.bfTitle)
    TextView bfTitle;
    @BindView(R.id.buyQtyTitle)
    TextView buyQtyTitle;
    @BindView(R.id.sellQtyTitle)
    TextView sellQtyTitle;
    @BindView(R.id.netQtyTitle)
    TextView netQtyTitle;
    @BindView(R.id.booked)
    TextView booked;
    @BindView(R.id.mtm)
    TextView mtm;

    private void initialization(View layout) {
        layout.findViewById(R.id.initPage).setOnClickListener(this);
        layout.findViewById(R.id.prevBtn).setOnClickListener(this);
        layout.findViewById(R.id.nextBtn).setOnClickListener(this);
        layout.findViewById(R.id.squareOff).setOnClickListener(this);
        setvalue();
    }

    private void setvalue() {
        try{
            String _scripName = selectedHolding.scripName.getValue();
            scripName.setText(_scripName);
            int bfQtyVal = selectedHolding.totalQty.getValue();
            if (selectedHolding.fromNetPosition) bfQtyVal = 0;
            String _bfQty = String.valueOf(bfQtyVal);
            bfQty.setText(_bfQty);
            StructMobNetPosition smnp = GlobalClass.getClsNetPosn().getPositionForScripcodeDerivativeNetObligation(selectedHolding.nSECode.getValue());
            String _totBuy = "0";
            String _totBuyAvgRate = "0";
            String _totSell = "0";
            String _totSellAvgRate = "0";
            if (smnp !=null){
                _totBuy= String.valueOf(smnp.getTotBuyQty());
                _totBuyAvgRate = formatter.format(smnp.getAvgBuyPrice());
                _totSell= String.valueOf(smnp.getTotSellQty());
                _totSellAvgRate = formatter.format(smnp.getAvgSellPrice());
            }
            totalBuy.setText(_totBuy);
            totalBuyAvg.setText(_totBuyAvgRate);
            totalSell.setText(_totSell);
            totalSellAvg.setText(_totSellAvgRate);
            String pageNo = (selectedPosition + 1) + "/" + holdingFOList.size();
            pageCount.setText(pageNo);
            refreshLTPandOblization();
            setTitles();
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void setTitles() {
        if (selectedHolding.exchange == eExch.NSECURR.value){
            bfTitle.setText("B/F Lots:");
            buyQtyTitle.setText("Today's Buy Lots:");
            sellQtyTitle.setText("Today's Sell Lots");
            netQtyTitle.setText("Net Lots:");
        }else {
            bfTitle.setText("B/F Qty:");
            buyQtyTitle.setText("Today's Buy Qty:");
            sellQtyTitle.setText("Today's Sell Qty:");
            netQtyTitle.setText("Net Qty:");
        }
    }
    private int sqareOffVisibility(int netQty){
        return netQty == 0? View.GONE:View.VISIBLE;
    }
    private double Round(double value,DecimalFormat df){
        return Double.valueOf(df.format(value));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.initPage:
                GlobalClass.fragmentManager.popBackStackImmediate();
                break;
            case R.id.prevBtn:
                prevClick();
                break;
            case R.id.nextBtn:
                nextClick();
                break;
            case R.id.squareOff:
                squareOffBtnClick();
                break;
            default:
                break;
        }
    }

    private void prevClick() {
        if (selectedPosition > 0) {
            selectedPosition = selectedPosition -1;
            selectedHolding = this.holdingFOList.get(selectedPosition);
            setvalue();
        }
    }

    private void nextClick() {
        if (selectedPosition < (holdingFOList.size() - 1)) {
            selectedPosition = selectedPosition +1;
            selectedHolding = this.holdingFOList.get(selectedPosition);
            setvalue();
        }
    }


    private void squareOffBtnClick() {

        int scriptCode = selectedHolding.nSECode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(selectedHolding.scripName.getValue());
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);
        StructBuySell buySell = new StructBuySell();
        int netQ = Integer.parseInt(netQty.getText().toString());
        if(netQ < 0){
            buySell.buyOrSell = eOrderType.BUY;
        } else{
            buySell.buyOrSell = eOrderType.SELL;
        }
        buySell.modifyOrPlace = eOrderType.PLACE;
        buySell.showDepth = eShowDepth.HOLDINGFO;
        buySell.netQty.setValue(Math.abs(netQ));
        buySell.isSquareOff.setValue(true);

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.HOLDINGFO,grplist,buySell,
                    homeActivity.SELECTED_RADIO_BTN,true);

        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, m_fragment, Constants.REPORTFRAGMENT_TAG);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }
    
    class HoldingFNODetailsHandler extends Handler {
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
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);//scripCode
                                if (selectedHolding.nSECode.getValue() == scripCode){
                                 refreshLTPandOblization();
                                }
                                break;
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshLTPandOblization() {
        try{
            StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(selectedHolding.nSECode.getValue(),false);
            String _prevClose = "0";
            String _lastRate = "0";
            if (smw != null){
                _prevClose = formatter.format(smw.getPClose());
                _lastRate = formatter.format(smw.getLastRate());
            }

            StructMobNetPosition smnp = GlobalClass.getClsNetPosn().getPositionForScripcodeDerivativeNetObligation(selectedHolding.nSECode.getValue());
            int bfQtyVal = selectedHolding.totalQty.getValue();
            if(selectedHolding.fromNetPosition){
                bfQtyVal = 0;
            }
            int _netQty = bfQtyVal;
            if (smnp !=null){
                _netQty = _netQty+smnp.getNetQty();
            }
            final StructNetOblization _netOblization = selectedHolding.getNetOblization();
            final String pClose = _prevClose;
            final String lRate = _lastRate;
            final int nQty = _netQty;
            homeActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prevClose.setText(pClose);
                    ltp.setText(lRate);
                    netQty.setText(String.valueOf(nQty));
                    netOblization.setText(_netOblization.getNetOblization());
                    booked.setText(Formatter.formatter.format(selectedHolding.getBookedPL()));
                    mtm.setText(Formatter.formatter.format(selectedHolding.getMTM()));
                    squareOff.setVisibility(sqareOffVisibility(nQty));
                }
            });
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

}