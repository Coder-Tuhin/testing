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

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Other.StructBuySell;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.TSLBMHoldingsReportReplyRecord;
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
 * A simple {@link Fragment} subclass.
 */
public class SLBMHoldingDetails extends Fragment implements View.OnClickListener {

    private ArrayList<TSLBMHoldingsReportReplyRecord> holdingFOList;
    private TSLBMHoldingsReportReplyRecord selectedHolding;
    private int selectedPosition;
    private HomeActivity homeActivity;
    private NumberFormat formatter;

    public SLBMHoldingDetails(){super();}

    @SuppressLint("ValidFragment")
    public SLBMHoldingDetails (ArrayList<TSLBMHoldingsReportReplyRecord> holdingFO, int position) {
        try {
            this.holdingFOList = holdingFO;
            this.selectedPosition = position;
            selectedHolding = holdingFOList.get(selectedPosition);
            formatter = Formatter.getFormatter(eExch.SLBS.value);
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

    public static Handler holdingSLBMdetailsHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity.CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        homeActivity.findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.fragment_slbmholding_details, container, false);
        ButterKnife.bind(this,layout);
        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        holdingSLBMdetailsHandler = new HoldingSLBMDetailsHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        holdingSLBMdetailsHandler = null;
        homeActivity.findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    @BindView(R.id.scripName)
    TextView scripName;
    @BindView(R.id.bfQty)
    TextView bfQty;
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
            int bfQtyVal = selectedHolding.BfQty.getValue();
            int netQtyVal = selectedHolding.NetQty.getValue();

            String _bfQty = String.valueOf(bfQtyVal);
            String _netQty = String.valueOf(netQtyVal);
            bfQty.setText(_bfQty);
            netQty.setText(_netQty);
            netOblization.setText(selectedHolding.getBorrowLend());
            String pageNo = (selectedPosition + 1) + "/" + holdingFOList.size();
            pageCount.setText(pageNo);
            refreshLTPandOblization();
            if(selectedHolding.receivingDeliveringFlag.getValue() == 'D'){
                squareOff.setVisibility(View.GONE);
            }else{
                squareOff.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private int sqareOffVisibility(int netQty){
        return netQty == 0? View.GONE:View.VISIBLE;
    }
    private double Round(double value, DecimalFormat df){
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
        buySell.buyOrSell = eOrderType.BUY;
        buySell.modifyOrPlace = eOrderType.PLACE;
        buySell.showDepth = eShowDepth.SLBMHOLDING;
        buySell.netQty.setValue(Math.abs(netQ));

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.SLBMHOLDING,grplist,buySell,
                    homeActivity.SELECTED_RADIO_BTN,true);

        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, m_fragment, Constants.REPORTFRAGMENT_TAG);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    class HoldingSLBMDetailsHandler extends Handler {
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
            StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(selectedHolding.nSECode.getValue(),true);
            String _lastRate = "0";
            if (smw != null && smw.getLastRate() > 0){
                _lastRate = formatter.format(smw.getLastRate());
                selectedHolding.setNseRate(smw.getLastRate());
            }else{
                _lastRate = formatter.format(selectedHolding.getNseRate());
            }
            String final_lastRate = _lastRate;
            homeActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ltp.setText(final_lastRate);
                }
            });
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
}