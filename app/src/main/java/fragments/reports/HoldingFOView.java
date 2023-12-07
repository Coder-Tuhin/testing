package fragments.reports;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Structure.Other.StructMobNetPosition;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import Structure.Response.RC.StructNetOblization;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import connection.SendDataToBCServer;
import enums.eExch;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.DividerItemDecoration;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.RecyclerItemClickListener;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */

public class HoldingFOView extends LinearLayout implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.report_recycler)RecyclerView report_recycler;
    @BindView(R.id.dataAvailable)LinearLayout dataAvailable;
    @BindView(R.id.noData)TextView noData;
    @BindView(R.id.fo_spinner)Spinner fo_spinner;

    TextView totBookedValue,totMTMValue,totBookedMTM,totNetObligation;
    private HoldingFoAdapter holdingFoAdapter;
    private HomeActivity homeActivity;
    private boolean isFirstLoaded;

    public HoldingFOView(Context context) {
        super(context);
        if (context instanceof HomeActivity)
            homeActivity = (HomeActivity) context;
        homeActivity.CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.holdingfo_screen,null);
        ButterKnife.bind(this, layout);
        isFirstLoaded = false;
        GlobalClass.holdingFOUIHandler = new HoldingFNOHandler();
        initialization(layout);
        addView(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isFirstLoaded = false;
        reloadData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    enum  eFOSpinnerItems {
        ALL("All",0),
        NSE_FO("Nse FO",1),
        NSE_CD("Nse CD",2);

        public String name;
        public int value;
        eFOSpinnerItems(String name,int value) {
            this.name=name;
            this.value = value;
        }
    }

    private void initialization(View layout) {

        ArrayList<String> spinnerlist = new ArrayList<String>();
        spinnerlist.add(eFOSpinnerItems.ALL.name);
        spinnerlist.add(eFOSpinnerItems.NSE_FO.name);
        spinnerlist.add(eFOSpinnerItems.NSE_CD.name);

        totBookedValue = (TextView) layout.findViewById(R.id.booked_netpos_textview);
        totMTMValue = (TextView) layout.findViewById(R.id.mtm_netpos_textview);
        totBookedMTM = (TextView) layout.findViewById(R.id.totbook_mtm_textview);
        totNetObligation = (TextView) layout.findViewById(R.id.totnet_obligation_textview);

        totNetObligation.setSelected(true);
        totNetObligation.setSingleLine(true);
        totNetObligation.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        totBookedMTM.setSelected(true);
        totBookedMTM.setSingleLine(true);
        totBookedMTM.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getContext(),R.layout.custom_spinner_item,spinnerlist);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        fo_spinner.setAdapter(dataAdapter);

        fo_spinner.setOnItemSelectedListener(this);
        fo_spinner.setSelection(dataAdapter.getPosition(eFOSpinnerItems.ALL.name));

        holdingFoAdapter = new HoldingFoAdapter();
        report_recycler.setAdapter(holdingFoAdapter);
        int deviderHeight = (int) getResources().getDimension(R.dimen.divider_height);
        report_recycler.addItemDecoration(new DividerItemDecoration(ScreenColor.SILVER, deviderHeight));
        report_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), report_recycler,new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Fragment fragment = new HoldingFODetailsFragment(holdingFoAdapter.mList,position);
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
    }

    private ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingFoList = new ArrayList<>();

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (holdingFoAdapter!=null)
            holdingFoAdapter.reloadData(holdingFoList,fo_spinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void reloadData(){
        try {
            ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingFNO = GlobalClass.getClsMarginHolding().getHoldingFNO();
            if (holdingFNO.size()>= 0){
                GlobalClass.dismissdialog();
                holdingFoList.clear();
                holdingFoList.addAll(holdingFNO);
                ArrayList<StructMobNetPosition> allNetPosition = GlobalClass.getClsNetPosn().getAllNetPosition("delivery");
                for (StructMobNetPosition smnp : allNetPosition){

                    if (!GlobalClass.getClsMarginHolding().isConatinScripCode(smnp.scripCode)){

                        boolean isAdd = false;
                        if((smnp.getExchange() == eExch.FNO.value)){
                            if(UserSession.getLoginDetailsModel().isFNOIntradayDelivery()){
                                if(smnp.isDeliveryAllow()) {
                                    isAdd = true;
                                }
                            } else {
                                isAdd = true;
                            }
                        } else if((smnp.getExchange() == eExch.NSECURR.value)) {
                            isAdd = true;
                        }
                        if(isAdd) {
                            StructHoldingsReportReplyRecord_Pointer shrrp = new StructHoldingsReportReplyRecord_Pointer(500);
                            shrrp.scripName.setValue(smnp.scripName);
                            shrrp.totalQty.setValue(smnp.getNetQty());
                            shrrp.nSECode.setValue(smnp.scripCode);
                            shrrp.setNseRate(smnp.getLastRate());
                            shrrp.mktlot = smnp.mktlot;
                            shrrp.exchange = smnp.getExchange();
                            shrrp.fromNetPosition = true;
                            holdingFoList.add(shrrp);
                        }
                    }
                }

                if(holdingFoList.size() > 0){
                    Collections.sort(holdingFoList, new Comparator<StructHoldingsReportReplyRecord_Pointer>() {
                        @Override
                        public int compare(StructHoldingsReportReplyRecord_Pointer lhs, StructHoldingsReportReplyRecord_Pointer rhs) {

                            String lhsN = lhs.scripName.getValue();
                            String rhsN = rhs.scripName.getValue();
                            return lhsN.compareTo(rhsN);
                        }
                    });
                }
                holdingFoAdapter.reloadData(holdingFoList,fo_spinner.getSelectedItem().toString());
                SendDataToBCServer bsServer = new SendDataToBCServer(null,eMessageCode.NEW_MULTIPLE_MARKETWATCH,holdingFoAdapter.getScripList());
                bsServer.execute();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFirstLoaded = true;
                        updateTotalBookMTMNetObligation(true);
                    }
                },1500);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void updateTotalBookMTMNetObligation(boolean isNeedToCalculateNetOblization){
        try {
            double mtm = 0;
            double bookedPL = 0;
            double netObligation = 0;
            double bookedMtm = 0;

            for (int i = 0; i < holdingFoList.size(); i++) {
                StructHoldingsReportReplyRecord_Pointer shrrp = holdingFoList.get(i);
                if(isNeedToCalculateNetOblization){
                    shrrp.getNetOblization();
                }
                netObligation = netObligation + shrrp.netObligation;
                mtm = mtm + shrrp.getMTM();
                bookedPL = bookedPL + shrrp.getBookedPL();
            }
            bookedMtm = mtm + bookedPL;

            if(isFirstLoaded) {
                totBookedValue.setText(Formatter.formatter.format(bookedPL));
                totMTMValue.setText(Formatter.formatter.format(mtm));
                totNetObligation.setText(Formatter.formatter.format(netObligation));
                totBookedMTM.setText(Formatter.formatter.format(bookedMtm));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void reloadViewForNoData(int count){
        if(count <= 0){
            noData.setVisibility(View.VISIBLE);
            dataAvailable.setVisibility(View.GONE);
        } else{
            noData.setVisibility(View.GONE);
            dataAvailable.setVisibility(View.VISIBLE);
        }
    }

    class HoldingFNOHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case HOLDING_RESPONSE:
                            case TRADE_BOOK_RESP:
                            case CFD_BOOK_RESP:
                                isFirstLoaded = false;
                                reloadData();
                                break;
                            case LITE_MW:
                                int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if(holdingFoList != null && holdingFoList.size()>0){
                                    holdingFoAdapter.refreshItem(scripCode);
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

    public class HoldingFoAdapter extends RecyclerView.Adapter<HoldingFoAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private ArrayList<Integer> nseScripList;
        private ArrayList<StructHoldingsReportReplyRecord_Pointer> mList = new ArrayList<>();

        public HoldingFoAdapter() {
            nseScripList = new ArrayList<>();
            inflater = LayoutInflater.from(homeActivity);
        }
        public ArrayList<Integer> getScripList(){
            return nseScripList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.holding_fo_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.setValue(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.scripName)TextView scripName;
            @BindView(R.id.totalValue)TextView totalValue;
            @BindView(R.id.netObgVal)TextView netObgVal;
            @BindView(R.id.ltpValue)TextView ltpValue;
            @BindView(R.id.qtyTitle)TextView qtyTitle;
            @BindView(R.id.bookedVal)TextView bookValue;
            @BindView(R.id.mtmValue)TextView mtmValue;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }

            public  void setValue(StructHoldingsReportReplyRecord_Pointer holdingRow){
                scripName.setText(holdingRow.scripName.getValue());
                final StructNetOblization netOblizationVal = holdingRow.getNetOblization();
                netObgVal.setText(netOblizationVal.getNetOblization());
                totalValue.setText(netOblizationVal.getNetQty());
                ltpValue.setText(netOblizationVal.getLtpValue());
                if (holdingRow.exchange == eExch.NSECURR.value){
                    qtyTitle.setText("Net Lots: ");
                }else {
                    qtyTitle.setText("Net Qty: ");
                }
                bookValue.setText(Formatter.formatter.format(holdingRow.getBookedPL()));
                mtmValue.setText(Formatter.formatter.format(holdingRow.getMTM()));
            }
        }

        public void reloadData(ArrayList<StructHoldingsReportReplyRecord_Pointer> holding,String selectedItem){
            nseScripList.clear();
            mList.clear();
            for(int i=0;i<holding.size();i++){
                StructHoldingsReportReplyRecord_Pointer hld = holding.get(i);
                if (selectedItem.equals(eFOSpinnerItems.NSE_CD.name)){
                    if (hld.exchange == eExch.NSECURR.value){
                        nseScripList.add(hld.nSECode.getValue());
                        mList.add(hld);
                    }
                }else if (selectedItem.equals(eFOSpinnerItems.NSE_FO.name)){
                    if (hld.exchange != eExch.NSECURR.value){
                        nseScripList.add(hld.nSECode.getValue());
                        mList.add(hld);
                    }
                }else {
                    nseScripList.add(hld.nSECode.getValue());
                    mList.add(hld);
                }
            }
            reloadViewForNoData(mList.size());
            notifyDataSetChanged();
        }

        public void refreshItem(int scripCode) {
            if (nseScripList!= null && nseScripList.contains(scripCode)){
                StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
                int position = nseScripList.indexOf(scripCode);
                if(position != -1) {
                    StructHoldingsReportReplyRecord_Pointer hld = mList.get(position);
                    hld.setNseRate(mktWatch.getLastRate());
                    hld.getNetOblization();
                    notifyItemChanged(position);
                    updateTotalBookMTMNetObligation(false);
                }
            }
        }
    }
}