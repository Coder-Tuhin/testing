package fragments.reports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import Structure.Other.StructBuySell;
import Structure.Other.StructMobNetPosition;
import Structure.Response.Group.GroupsTokenDetails;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.eProductTypeITS;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
@SuppressLint("ValidFragment")
public class NetpositiondetailsFragment extends Fragment implements View.OnClickListener {

    private ArrayList<StructMobNetPosition> allNetPosition;
    private StructMobNetPosition selectedNetPosition;
    private int selectedPosition = 0;

    TextView screptName, totalBuyValue, currentRateValue, mtm, booked, buy, sell, buyValue,
            sellValue, pageNumberTxt, orderTypeTxt;
    ImageButton backbtn, prevbtn, nextbtn;
    Button squareoffbtn;
    @BindView(R.id.net_position_relativeseven) RelativeLayout net_position_relativeseven;


    public  NetpositiondetailsFragment(){super();}
    public NetpositiondetailsFragment(ArrayList<StructMobNetPosition> allNetPosition, int position) {
        this.allNetPosition = allNetPosition;
        this.selectedPosition = position;
        selectedNetPosition = this.allNetPosition.get(selectedPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.netpositiondetails_screen, container, false);
        ButterKnife.bind(this,layout);
        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.netPositionDetailUIHandler = new NetPositionDEetailHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.netPositionDetailUIHandler = null;
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization(View layout) {

        screptName = (TextView) layout.findViewById(R.id.screptname_textview);
        totalBuyValue = (TextView) layout.findViewById(R.id.total_buy_value_textview);
        currentRateValue = (TextView) layout.findViewById(R.id.current_ratevalue_textview);
        mtm = (TextView) layout.findViewById(R.id.mtm_textview);
        booked = (TextView) layout.findViewById(R.id.booked_textview);
        buy = (TextView) layout.findViewById(R.id.buy_textview);
        sell = (TextView) layout.findViewById(R.id.sell_textview);
        buyValue = (TextView) layout.findViewById(R.id.buy_value_textview);
        sellValue = (TextView) layout.findViewById(R.id.sell_value_textview);


        orderTypeTxt = (TextView) layout.findViewById(R.id.netoisition_ordertype_textview);

        pageNumberTxt = (TextView) layout.findViewById(R.id.pagecount_textview);
        backbtn = (ImageButton) layout.findViewById(R.id.previnit_imagebutton);
        backbtn.setOnClickListener(this);
        prevbtn = (ImageButton) layout.findViewById(R.id.prevbtn_textview);
        prevbtn.setOnClickListener(this);
        nextbtn = (ImageButton) layout.findViewById(R.id.nextbtn_textview);
        nextbtn.setOnClickListener(this);
        squareoffbtn = (Button) layout.findViewById(R.id.squareoff_button);
        squareoffbtn.setOnClickListener(this);

        setvalue();
    }

    private void setvalue() {
        try {
            if(selectedNetPosition == null){
                if(selectedPosition >= allNetPosition.size()){
                    selectedPosition = 0;
                }
                selectedNetPosition = allNetPosition.get(selectedPosition);
            }
            if (selectedNetPosition.isIntraDelAllow()) {
                net_position_relativeseven.setVisibility(View.VISIBLE);
            } else {
                net_position_relativeseven.setVisibility(View.GONE);
            }
            screptName.setText(selectedNetPosition.getFormatedScripName(false));
            totalBuyValue.setText(selectedNetPosition.getNetQtyRate());
            currentRateValue.setText(selectedNetPosition.getLastRateStr());
            mtm.setText(selectedNetPosition.getMTMStr());
            booked.setText(selectedNetPosition.getBookedPLStr());
            buy.setText("" + selectedNetPosition.getBuyQtyAvgRate());
            sell.setText("" + selectedNetPosition.getSellQtyAvgRate());
            buyValue.setText(selectedNetPosition.getTotBuyValueStr());
            sellValue.setText(selectedNetPosition.getTotSellValueStr());
            if (UserSession.getLoginDetailsModel().isIntradayDelivery()) {
                orderTypeTxt.setText("" + selectedNetPosition.getOrderTypeStr());
            }
            squareoffbtn.setVisibility(View.GONE);
            if ((selectedNetPosition.getNetQty() != 0) && (selectedNetPosition.orderType == eProductTypeITS.INTRADAY.value ||
                    selectedNetPosition.orderType == eProductTypeITS.DELIVERY.value)) {
                squareoffbtn.setVisibility(View.VISIBLE);
            }
            String pageNo = (selectedPosition + 1) + "/" + allNetPosition.size();
            pageNumberTxt.setText(pageNo);
        }catch (Exception ex){
            GlobalClass.onError("NetPosDetail : " + selectedPosition + " : " + allNetPosition.size(),ex);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.previnit_imagebutton:
                backBtnClick();
                break;
            case R.id.prevbtn_textview:
                prevBtnClick();
                break;
            case R.id.nextbtn_textview:
                nextBtnClick();
                break;
            case R.id.squareoff_button:
                squareOffBtnClick();
                break;
            default:
                break;
        }
    }

    private void backBtnClick(){
        GlobalClass.fragmentManager.popBackStackImmediate();
    }
    private void prevBtnClick(){
        if(selectedPosition > 0){
            selectedPosition = selectedPosition - 1;
            selectedNetPosition = this.allNetPosition.get(selectedPosition);
            setvalue();
        }
    }
    private void nextBtnClick(){
        if(selectedPosition < (allNetPosition.size() - 1)){
            selectedPosition = selectedPosition + 1;
            selectedNetPosition = this.allNetPosition.get(selectedPosition);
            setvalue();
        }
    }
    private void squareOffBtnClick(){

        int scriptCode = selectedNetPosition.scripCode;
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        groupsTokenDetails.scripName.setValue(selectedNetPosition.getFormatedScripName(false));
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = new StructBuySell();
        if(selectedNetPosition.getNetQty() < 0){
            buySell.buyOrSell = eOrderType.BUY;
        }
        else{
            buySell.buyOrSell = eOrderType.SELL;
        }
        buySell.modifyOrPlace = eOrderType.PLACE;
        buySell.showDepth = eShowDepth.NETPOSITION;
        buySell.netPosn = selectedNetPosition;
        buySell.netQty.setValue(Math.abs(selectedNetPosition.getNetQty()));
        buySell.isSquareOff.setValue(true);

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.NETPOSITION,grplist,buySell,
                    homeActivity.SELECTED_RADIO_BTN,true);

        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, m_fragment, Constants.REPORTFRAGMENT_TAG);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    private HomeActivity homeActivity;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    private double prevLastRate = 0.00;
    class NetPositionDEetailHandler extends Handler {
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
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if(selectedNetPosition.scripCode == scripCode){
                                    //currentRateValue.setText(selectedNetPosition.getLastRateStr());
                                    selectedNetPosition.setLtpWithTxtColor(currentRateValue,prevLastRate);
                                    prevLastRate = selectedNetPosition.getLastRate();
                                    mtm.setText(selectedNetPosition.getMTMStr());
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