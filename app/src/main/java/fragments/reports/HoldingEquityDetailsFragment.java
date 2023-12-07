package fragments.reports;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Other.StructBuySell;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eLogType;
import enums.eOrderType;
import enums.eShowDepth;
import fragments.homeGroups.MktdepthFragmentRC;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;


@SuppressLint("ValidFragment")
public class HoldingEquityDetailsFragment extends Fragment implements View.OnClickListener {

    TextView scripName,totalQty,nonpoaQty,nonpoaQtyTitle,todayTrade,dpQty,vbQty,srQty,sr2Qty,nseValue,bseValue,fsqtytitle,
            fsQty,csQty,csQtyMtf,slbsTitle,slbsqty,pageNumberTxt, note;
    ImageButton backbtn,prevbtn,nextbtn;
    Button nsebtnsell,bsebtnsell,nsebtnbuy,bsebtnbuy;
    private RelativeLayout stocksRelative;


    private ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingEquityList;
    private StructHoldingsReportReplyRecord_Pointer selectedHolding;
    private int selectedPosition;


    public HoldingEquityDetailsFragment(){super();}
    public HoldingEquityDetailsFragment(ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingEquity, int position){
        this.holdingEquityList = holdingEquity;
        this.selectedPosition = position;
        selectedHolding = holdingEquityList.get(selectedPosition);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        View layout = inflater.inflate(R.layout.holdingequity_itemdetails, container, false);
        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        setvalue();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Activity)getActivity()).findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization(View layout) {

        scripName = (TextView) layout.findViewById(R.id.holding_value_textview);
        totalQty = (TextView) layout.findViewById(R.id.total_qty);
        nonpoaQty = (TextView) layout.findViewById(R.id.nonpoa_qty);
        nonpoaQtyTitle = (TextView) layout.findViewById(R.id.nonpoa_qty_title);

        todayTrade = (TextView) layout.findViewById(R.id.todaytrade_textview);
        dpQty = (TextView) layout.findViewById(R.id.dp_qty);
        vbQty = (TextView) layout.findViewById(R.id.vbqty_textview);
        srQty = (TextView) layout.findViewById(R.id.srqty_textview);
        sr2Qty = (TextView) layout.findViewById(R.id.srqty2_textview);
        nseValue = (TextView) layout.findViewById(R.id.nse_value_textview);
        bseValue = (TextView) layout.findViewById(R.id.bse_value_textview);
        fsqtytitle = (TextView) layout.findViewById(R.id.fsqtytitle);
        fsQty = (TextView) layout.findViewById(R.id.fsQty);
        csQty = (TextView) layout.findViewById(R.id.csQty);
        csQtyMtf = (TextView) layout.findViewById(R.id.csQtyMtf);

        slbsTitle = (TextView) layout.findViewById(R.id.slbs_title);
        slbsqty = (TextView) layout.findViewById(R.id.slbsqty_textview);

        note = (TextView) layout.findViewById(R.id.note);

        stocksRelative = (RelativeLayout) layout.findViewById(R.id.stocksRelative);

        pageNumberTxt = (TextView) layout.findViewById(R.id.pagecount_textview);
        backbtn = (ImageButton) layout.findViewById(R.id.previnit_imagebutton);
        prevbtn = (ImageButton) layout.findViewById(R.id.prevbtn_textview);
        nextbtn = (ImageButton) layout.findViewById(R.id.nextbtn_textview);

        nsebtnsell = (Button) layout.findViewById(R.id.sell_nse_button);
        bsebtnsell = (Button) layout.findViewById(R.id.sell_bse_button);
        nsebtnbuy = (Button) layout.findViewById(R.id.buy_nse_button);
        bsebtnbuy = (Button) layout.findViewById(R.id.buy_bse_button);

        backbtn.setOnClickListener(this);
        prevbtn.setOnClickListener(this);
        nextbtn.setOnClickListener(this);

        nsebtnsell.setOnClickListener(this);
        bsebtnsell.setOnClickListener(this);
        nsebtnbuy.setOnClickListener(this);
        bsebtnbuy.setOnClickListener(this);

        if(!UserSession.getClientResponse().isSLBMActivated()){
            slbsqty.setVisibility(View.GONE);
            slbsTitle.setVisibility(View.GONE);
        }
    }

    private void setvalue() {
        if (ObjectHolder.isMarginTrade){
            //stocksRelative.setVisibility(View.VISIBLE);
            fsQty.setVisibility(View.VISIBLE);
            fsqtytitle.setVisibility(View.VISIBLE);
            fsQty.setText(""+selectedHolding.FundedStockQty.getValue());
            note.setText(getResources().getString(R.string.holding_equitytext_two));
        }
        if(!ObjectHolder.isPOA){
            nonpoaQty.setVisibility(View.VISIBLE);
            nonpoaQtyTitle.setVisibility(View.VISIBLE);
        }
        csQty.setText(""+selectedHolding.CollateralStockQty.getValue());
        csQtyMtf.setText(""+selectedHolding.CollateralMTFStockQty.getValue());

        scripName.setText("" + selectedHolding.scripName.getValue());
        totalQty.setText("" + selectedHolding.totalQty.getValue());
        nonpoaQty.setText("" + selectedHolding.pOA.getValue());
        todayTrade.setText("" + selectedHolding.soldQty.getValue());
        dpQty.setText("" + selectedHolding.clientDPQty.getValue());
        vbQty.setText("" + selectedHolding.brokerBeniQty.getValue());
        srQty.setText("" + selectedHolding.exchRcvQty.getValue());
        sr2Qty.setText("" + selectedHolding.sr2qty.getValue());
        slbsqty.setText(""+selectedHolding.slbmqty.getValue());

        NumberFormat formatter = Formatter.getFormatter(selectedHolding.exchange);

        nseValue.setText("" + formatter.format(selectedHolding.getNseRate()));
        bseValue.setText("" + formatter.format(selectedHolding.getBseRate()));

        nsebtnsell.setVisibility(View.GONE);
        bsebtnsell.setVisibility(View.GONE);
        nsebtnbuy.setVisibility(View.GONE);
        bsebtnbuy.setVisibility(View.GONE);

        if (selectedHolding.nSECode.getValue() > 0) {
            nsebtnsell.setVisibility(View.VISIBLE);
            nsebtnbuy.setVisibility(View.VISIBLE);
        }
        if (selectedHolding.bSECode.getValue() > 0) {
            bsebtnsell.setVisibility(View.VISIBLE);
            bsebtnbuy.setVisibility(View.VISIBLE);
        }
        String pageNo = (selectedPosition + 1) + "/" + holdingEquityList.size();
        pageNumberTxt.setText(pageNo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previnit_imagebutton:
                GlobalClass.fragmentManager.popBackStackImmediate();
                break;
            case R.id.prevbtn_textview:
                prevBtnClick();
                break;
            case R.id.nextbtn_textview:
                nextBtnClick();
                break;
            case R.id.sell_nse_button:
                NSEBtnClick(eOrderType.SELL);
                break;
            case R.id.sell_bse_button:
                BSEBtnClick(eOrderType.SELL);
                break;
            case R.id.buy_nse_button:
                NSEBtnClick(eOrderType.BUY);
                break;
            case R.id.buy_bse_button:
                BSEBtnClick(eOrderType.BUY);
                break;
            default:
                break;
        }
    }

    private void backBtnClick() {
      //  GlobalClass.reportFragmentManager.popBackStackImmediate();
    }

    private void prevBtnClick() {
        if (selectedPosition > 0) {
            selectedPosition = selectedPosition - 1;
            selectedHolding = this.holdingEquityList.get(selectedPosition);
            setvalue();
        }
    }

    private void nextBtnClick() {
        if (selectedPosition < (holdingEquityList.size()-1)) {
            selectedPosition = selectedPosition + 1;
            selectedHolding = this.holdingEquityList.get(selectedPosition);
            setvalue();
        }
    }

    private void NSEBtnClick(eOrderType buysellType) {

        int scriptCode = selectedHolding.nSECode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);

        String scrip = selectedHolding.scripName.getValue();
        if (!scrip.contains("NE-")) scrip = "NE-"+scrip;
        //NE-ACC
        groupsTokenDetails.scripName.setValue(scrip);
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = new StructBuySell();
        buySell.buyOrSell = buysellType;
        buySell.modifyOrPlace = eOrderType.PLACE;
        buySell.showDepth = eShowDepth.HOLDINGEQUITY;
        buySell.netQty.setValue(selectedHolding.getNetQty());

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.HOLDINGEQUITY,grplist,buySell,
                    homeActivity.SELECTED_RADIO_BTN,true);

        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, m_fragment, Constants.REPORTFRAGMENT_TAG);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }
    private void BSEBtnClick(eOrderType buysellType) {
        int scriptCode = selectedHolding.bSECode.getValue();
        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
        groupsTokenDetails.scripCode.setValue(scriptCode);
        //BE-ACC
        String scrip = selectedHolding.scripName.getValue();
        if (!scrip.contains("BE-")) scrip = "BE-"+scrip;
        groupsTokenDetails.scripName.setValue(scrip);
        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
        grplist.add(groupsTokenDetails);

        StructBuySell buySell = new StructBuySell();
        buySell.buyOrSell = buysellType;
        buySell.modifyOrPlace = eOrderType.PLACE;
        buySell.showDepth = eShowDepth.HOLDINGEQUITY;
        buySell.netQty.setValue(selectedHolding.getNetQty());

        Fragment m_fragment;
        m_fragment = new MktdepthFragmentRC(scriptCode, eShowDepth.HOLDINGEQUITY,grplist,buySell,
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
}
