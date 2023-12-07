package wealth.mv;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import Structure.Other.StructBuySell;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import chart.GraphFragment;
import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.ePrefTAG;
import enums.eScreen;
import enums.eShowDepth;
import fragments.LatestResultFragment;
import fragments.ValuetionFragment;
import fragments.homeGroups.MktdepthFragmentRC;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import wealth.Dialogs;
import wealth.VenturaServerConnect;
import wealth.wealthStructure.ClosedHoldingData;
import wealth.wealthStructure.ClosedHoldingDetails;
import wealth.wealthStructure.StructBondEquityDepositoryDetailNew;
import wealth.wealthStructure.StructBondEuityDepositoryRow;
import wealth.wealthStructure.StructFamilyCodesDetail;

public class HoldingMenuNew extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;
    private View myFragmentView;
    private StructBondEquityDepositoryDetailNew equityDetail;
    private OpenHoldingAdapter dpHoldingAdapter = null;
    private ClosedHoldingAdapter closedHoldingAdapter = null;
    public static Handler dpHoldingUIHandler;
    private LinearLayout totalLayout,openholdingheader,closedholdingheader;
    private RecyclerView recyclerView;
    private Spinner groupSpinner;
    private LinkedHashMap<String, String> fancyCodeList;
    private TextView totCurVal, tot_GL,tota_prevDayVal;
    private TextView totCurValTitle, tot_GLTitle,tota_prevDayValTitle;

    private TextView hd_company_name, hdprevdaygl,hd_gain_loss;

    private RadioGroup holdingRG;
    private RadioButton openRd,closedRd;
    private ClosedHoldingData closedHoldingData;
    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dpHoldingUIHandler == null) {
            dpHoldingUIHandler = new DPHoldingEquityHandler();
        }
        try {
            if (GlobalClass.broadCastReg.isNormalMKt()) {
                if (dpHoldingAdapter != null && VenturaServerConnect.getHoldingData() != null) {
                    dpHoldingAdapter.reloadDataForMKTData();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dpHoldingUIHandler = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.dpholdingreport_new, container, false);
            recyclerView = myFragmentView.findViewById(R.id.recyclerView);
            totalLayout = myFragmentView.findViewById(R.id.totalRow);
            openholdingheader = myFragmentView.findViewById(R.id.openholdingheader);
            closedholdingheader = myFragmentView.findViewById(R.id.closedholdingheader);

            closedholdingheader.setVisibility(View.GONE);

            this.totCurValTitle = myFragmentView.findViewById(R.id.totCurValtitle);
            this.tot_GLTitle = myFragmentView.findViewById(R.id.totGLtitle);
            this.tota_prevDayValTitle = myFragmentView.findViewById(R.id.totPrevDayValtitle);

            this.totCurVal = myFragmentView.findViewById(R.id.totCurVal);
            this.tot_GL = myFragmentView.findViewById(R.id.totGL);
            this.tota_prevDayVal = myFragmentView.findViewById(R.id.totPrevDayVal);

            this.hd_company_name = myFragmentView.findViewById(R.id.hd_company_name);
            this.hdprevdaygl = myFragmentView.findViewById(R.id.hdprevdaygl);
            this.hd_gain_loss = myFragmentView.findViewById(R.id.hd_gain_loss);

            this.hd_company_name.setOnClickListener(this);
            this.hdprevdaygl.setOnClickListener(this);
            this.hd_gain_loss.setOnClickListener(this);

            holdingRG = (RadioGroup) myFragmentView.findViewById(R.id.holdingRG);
            openRd = (RadioButton) myFragmentView.findViewById(R.id.openRd);
            closedRd = (RadioButton) myFragmentView.findViewById(R.id.closedRd);

            groupSpinner = (Spinner) myFragmentView.findViewById(R.id.layout_spinner).findViewById(R.id.spn);
            fancyCodeList = new LinkedHashMap<>();
            fancyCodeList.put(UserSession.getLoginDetailsModel().getUserID() + " - " + UserSession.getLoginDetailsModel().getClientName(), UserSession.getLoginDetailsModel().getUserID());
            StructFamilyCodesDetail familyData = VenturaApplication.getPreference().getFamilyData();
            if (familyData != null && familyData.getfamilyCodes().length > 0) {
                for (int i = 0; i < familyData.getfamilyCodes().length; i++) {
                    String code = familyData.getfamilyCodes()[i];
                    String codeName[] = code.split(";");
                    fancyCodeList.put(codeName[0] + " - " + codeName[1], codeName[0]);
                }
            }
            addClientcodeSpinner(groupSpinner);
            if (familyData == null) {
                new GetTaskFamilyCode().execute();
            }
            ItemTouchHelper swipeHandler =   new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.RIGHT) {
                       GlobalClass.log("Swipe Right : " + viewHolder.getAdapterPosition());
                       showMktDepth(viewHolder.getAdapterPosition(),eOrderType.BUY);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        GlobalClass.log("Swipe Left : " + viewHolder.getAdapterPosition());
                        showMktDepth(viewHolder.getAdapterPosition(),eOrderType.SELL);
                    }
                    dpHoldingAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
                @Override
                public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(GlobalClass.latestContext, R.color.red))
                            .addSwipeLeftLabel("Selling...")
                            .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP,25)
                            .setSwipeLeftLabelColor(Color.WHITE)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(GlobalClass.latestContext, R.color.green1))
                            .addSwipeRightLabel("Buying...")
                            .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP,25)
                            .setSwipeRightLabelColor(Color.WHITE)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            });
            swipeHandler.attachToRecyclerView(recyclerView);
            initHoldingRadioGroup();
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        }
        return myFragmentView;
    }
    private void initHoldingRadioGroup(){
        holdingRG.setOnCheckedChangeListener(null);
        holdingRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try{
                    openClosedChanged();
                }catch (Exception ex){
                    GlobalClass.onError("",ex);
                }
            }
        });
    }

    private void openClosedChanged() throws Exception{
        if (openRd.isChecked()) {
            openholdingheader.setVisibility(View.VISIBLE);
            closedholdingheader.setVisibility(View.GONE);

            totCurValTitle.setText("Total Current Value");
            tota_prevDayValTitle.setText("Day Gain/Loss");
            //tot_GLTitle.setText("Total Gain/Loss");

            if (equityDetail != null) {
                dpHoldingAdapter = new OpenHoldingAdapter(equityDetail.getBondEquityDepositoryRows());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(GlobalClass.latestContext));
                recyclerView.setAdapter(dpHoldingAdapter);

                if (equityDetail.getNoOfRow() > 0) {
                    totalLayout.setVisibility(View.VISIBLE);
                    displayTotalRow();
                }
            }
        } else {
            openholdingheader.setVisibility(View.GONE);
            closedholdingheader.setVisibility(View.VISIBLE);

            totCurValTitle.setText("Total Pur Value");
            tota_prevDayValTitle.setText("Total Sell Value");
            //tot_GLTitle.setText("");

            if(closedHoldingData != null){
                if (closedHoldingData != null) {
                    closedHoldingAdapter = new ClosedHoldingAdapter(closedHoldingData.getAllData());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(GlobalClass.latestContext));
                    recyclerView.setAdapter(closedHoldingAdapter);

                    if (closedHoldingData.getAllData().size() > 0) {
                        totalLayout.setVisibility(View.VISIBLE);
                        displayTotalRow();
                    }
                }
            }else{
                new GetClosedPortfolioTask(groupSpinner.getSelectedItem().toString()).execute();
            }
        }
    }

    private void showMktDepth(int position,eOrderType orderType) {
        try {
            StructBondEuityDepositoryRow equityrow = dpHoldingAdapter.listData[position];
            if (equityrow != null) {
                ArrayList<GroupsTokenDetails> grpScripList = new ArrayList<>();
                GroupsTokenDetails td = new GroupsTokenDetails();
                td.scripCode.setValue(equityrow.getScripCodeForRateUpdate());
                td.scripName.setValue(equityrow.getCompanyNameForShortLong());
                grpScripList.add(td);
                StructBuySell buySell = null;
                if (orderType != eOrderType.NONE) {
                    buySell = new StructBuySell();
                    buySell.buyOrSell = orderType;
                    buySell.modifyOrPlace = eOrderType.PLACE;
                    buySell.showDepth = eShowDepth.MKTWATCH;
                }
                GlobalClass.openDepth(td.scripCode.getValue(), eShowDepth.MKTWATCH, grpScripList, buySell);
            }
        }catch (Exception ex){
            GlobalClass.onError("showMktDepth(viewHolder.getAdapterPosition(),eOrderType.BUY);",ex);
        }
    }

    private void addClientcodeSpinner(Spinner groupSpinner) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), R.layout.custom_spinner_item, new ArrayList<String>(fancyCodeList.keySet()));
            adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
            groupSpinner.setAdapter(adapter);
            groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    new GetTask(adapterView.getSelectedItem().toString()).execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    int sortType = 0;

    @Override
    public void onClick(View view) {
        if(view == hd_company_name){
            if(equityDetail != null){
                StructBondEuityDepositoryRow[] equityRows = equityDetail.getSortedString(0,sortType);
                if(dpHoldingAdapter != null){
                    dpHoldingAdapter.reloadMainData(equityRows);
                }
            }
        } else if(view == hdprevdaygl){
            if(equityDetail != null){
                StructBondEuityDepositoryRow[] equityRows = equityDetail.getSortedString(6,sortType);
                if(dpHoldingAdapter != null){
                    dpHoldingAdapter.reloadMainData(equityRows);
                }
            }
        } else if(view == hd_gain_loss){
            if(equityDetail != null){
                StructBondEuityDepositoryRow[] equityRows = equityDetail.getSortedString(5,sortType);
                if(dpHoldingAdapter != null){
                    dpHoldingAdapter.reloadMainData(equityRows);
                }
            }
        }
        if(sortType == 0)
        sortType = 1;
        else
            sortType = 0;
    }

    private class GetTaskFamilyCode extends AsyncTask<Object, Void, String> {
        ProgressDialog mDialog;
        StructFamilyCodesDetail structFamilyCodesDetail = null;
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected String doInBackground(Object... params) {
            try {
                structFamilyCodesDetail = VenturaServerConnect.FamilyCodesDetail(240);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPostExecute(String result) {
            try {
                mDialog.dismiss();
                if (!result.equalsIgnoreCase("")) {
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                } else {
                    if (structFamilyCodesDetail != null) {
                        if (structFamilyCodesDetail.getfamilyCodes().length > 0) {
                            fancyCodeList.clear();
                            fancyCodeList.put(UserSession.getLoginDetailsModel().getUserID() + " - " + UserSession.getLoginDetailsModel().getClientName(), UserSession.getLoginDetailsModel().getUserID());

                            for (int i = 0; i < structFamilyCodesDetail.getfamilyCodes().length; i++) {
                                String code = structFamilyCodesDetail.getfamilyCodes()[i];
                                String codeName[] = code.split(";");
                                fancyCodeList.put(codeName[0] + " - " + codeName[1], codeName[0]);
                            }
                            addClientcodeSpinner(groupSpinner);
                        }
                        VenturaApplication.getPreference().setFamilyData(structFamilyCodesDetail);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetTask extends AsyncTask<Object, Void, String> {
        ProgressDialog mDialog;
        String CLientCode;

        GetTask(String _CLientCode) {
            this.CLientCode = fancyCodeList.get(_CLientCode);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                totalLayout.setVisibility(View.GONE);
                if(dpHoldingAdapter != null){
                    dpHoldingAdapter.reloadMainData(new StructBondEuityDepositoryRow[0]);
                }
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected String doInBackground(Object... params) {
            try {
                GlobalClass.log("Holding Data : GetTask");
                equityDetail = VenturaServerConnect.getEquityDepositoryDetail(CLientCode, (byte) eScreen.DPHOLDING.value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPostExecute(String result) {
            try {
                mDialog.dismiss();
                if (!result.equalsIgnoreCase("")) {
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                } else {
                    if (equityDetail != null) {
                        dpHoldingAdapter = new OpenHoldingAdapter(equityDetail.getBondEquityDepositoryRows());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(GlobalClass.latestContext));
                        recyclerView.setAdapter(dpHoldingAdapter);

                        if (equityDetail.getNoOfRow() > 0) {
                            totalLayout.setVisibility(View.VISIBLE);
                            displayTotalRow();
                            if (GlobalClass.broadCastReg.isNormalMKt()) {
                                SendDataToBCServer bcadata = new SendDataToBCServer(null, eMessageCode.NEW_MULTIPLE_MARKETWATCH, dpHoldingAdapter.getScripList());
                                bcadata.execute();
                            }
                            boolean investnow = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.HOLDING_MENU_HELP.name, false);
                            if (!investnow) {
                                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.HOLDING_MENU_HELP.name, true);
                                Dialog dialog = new HoldingHelpScreen(getActivity());
                                dialog.show();
                            }
                        }
                    } else {
                        Dialogs.showSessionDialog(VenturaServerConnect.sessioncheck.getStatus(), getContext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class OpenHoldingAdapter extends RecyclerView.Adapter<OpenHoldingAdapter.ViewHolder> {

        StructBondEuityDepositoryRow[] listData;
        private ArrayList<Integer> scripList;

        public OpenHoldingAdapter(StructBondEuityDepositoryRow[] listdata) {
            this.listData = listdata;
            loadScripList();
        }

        public void reloadMainData(StructBondEuityDepositoryRow[] listdata){
            try {
                this.listData = listdata;
                loadScripList();
                notifyDataSetChanged();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            StructBondEuityDepositoryRow equityrow = listData[position];
            holder.linear_Layout.setBackgroundColor((position % 2 == 0) ? ScreenColor.iTableRowOneBackColor : ScreenColor.iTableRowTwoBackColor);
            holder.setValue(equityrow);
        }

        @Override
        public int getItemCount() {
            return listData.length-1;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private StructBondEuityDepositoryRow equityrow = null;
            private TextView txt_company_name, txt_pur_price, txt_cmp, cur_val, txt_qty, txt_gain_loss,txt_prev_day_gl;
            LinearLayout linear_Layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.linear_Layout = itemView.findViewById(R.id.linear_Layout);
                this.txt_company_name = itemView.findViewById(R.id.txt_company_name);
                this.txt_pur_price = itemView.findViewById(R.id.txt_pur_price);
                this.txt_cmp = itemView.findViewById(R.id.txt_cmp);
                this.cur_val = itemView.findViewById(R.id.cur_val);
                this.txt_qty = itemView.findViewById(R.id.txt_qty);
                this.txt_gain_loss = itemView.findViewById(R.id.txt_gain_loss);
                this.txt_prev_day_gl = itemView.findViewById(R.id.txt_prevdaygl);

                linear_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (equityrow != null && !equityrow.getCompanyName().equalsIgnoreCase("total")) {

                            Fragment fragment = new LongShortTableViewFragment(equityrow.getCompanyNameForShortLong(),
                                    equityrow.getScripCodeForRateUpdate(), equityrow.getISINNo(), equityDetail.clientCode,openholdingheader.getVisibility() == View.VISIBLE);
                            FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, fragment, "");
                            fragmentTransaction.addToBackStack("");
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.commit();
                        }
                    }
                });

                linear_Layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (equityrow != null && !equityrow.getCompanyName().equalsIgnoreCase("total")
                                && fancyCodeList.get(groupSpinner.getSelectedItem().toString()).equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())) {

                            // Initializing the popup menu and giving the reference as current context
                            PopupMenu popupMenu = new PopupMenu(getActivity(), linear_Layout);

                            // Inflating popup menu from popup_menu.xml file
                            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_holding, popupMenu.getMenu());
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    int scripCode = equityrow.getNseScripCode() > 0 ? equityrow.getNseScripCode() : equityrow.getBseScripCode();
                                    String companyName = equityrow.getNseScripCode() > 0 ? "NE-" + equityrow.getCompanyName() : "BE-" + equityrow.getCompanyName();
                                    if (menuItem.getTitle().toString().equalsIgnoreCase("VALUATION")) {
                                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                                        groupsTokenDetails.scripCode.setValue(scripCode);
                                        groupsTokenDetails.scripName.setValue(companyName);
                                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                                        grplist.add(groupsTokenDetails);
                                        Fragment m_fragment = new ValuetionFragment(equityrow.getBseScripCode(), grplist, homeActivity.SELECTED_RADIO_BTN);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
                                    } else if (menuItem.getTitle().toString().equalsIgnoreCase("CHART")) {
                                        Fragment m_fragment = GraphFragment.newInstance(scripCode, companyName);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "");
                                    } else if (menuItem.getTitle().toString().equalsIgnoreCase("LATEST RESULTS")) {
                                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                                        groupsTokenDetails.scripCode.setValue(scripCode);
                                        groupsTokenDetails.scripName.setValue(companyName);
                                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                                        grplist.add(groupsTokenDetails);
                                        Fragment m_fragment = new LatestResultFragment(equityrow.getBseScripCode(), grplist, homeActivity.SELECTED_RADIO_BTN);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
                                    } else if (menuItem.getTitle().toString().equalsIgnoreCase("Market Depth")) {
                                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                                        groupsTokenDetails.scripCode.setValue(scripCode);
                                        groupsTokenDetails.scripName.setValue(companyName);
                                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                                        grplist.add(groupsTokenDetails);

                                        Fragment m_fragment;
                                        m_fragment = new MktdepthFragmentRC(scripCode, eShowDepth.MKTWATCH, grplist, null,
                                                (homeActivity).SELECTED_RADIO_BTN, false);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.DEPTH.name);
                                    }
                                    return true;
                                }
                            });
                            // Showing the popup menu
                            popupMenu.show();
                        }
                        return false;
                    }
                });
            }

            public void setValue(StructBondEuityDepositoryRow _equityrow) {
                equityrow = _equityrow;
                if (equityrow.getCompanyName().equalsIgnoreCase("Total")) {

                } else {

                    String _companyName = equityrow.getCompanyName();
                    if (!equityrow.getHoldingType().equalsIgnoreCase("")) {
                        _companyName = _companyName + " (" + equityrow.getHoldingType() + ")";
                    }
                    txt_company_name.setText(_companyName);
                    String value = "";
                    String value1 = Formatter.toNoFracValue(equityrow.getGainLoss());// as per sriram sir 10Aug20
                    String valuePV = Formatter.toNoFracValue(equityrow.getPrevDayGainLoss());// as per sriram sir 10Aug20

                    if (equityrow.getAvgPurchasePrice() == 0) {
                        value = "$";
                        value1 = "$";
                    } else {
                        value = Formatter.toTwoDecimalValue(equityrow.getAvgPurchasePrice());
                    }
                    txt_pur_price.setText(value);
                    txt_cmp.setText(Formatter.toTwoDecimalValue(equityrow.getCMP()));
                    txt_qty.setText(equityrow.getQty()+"");
                    cur_val.setText(Formatter.toNoFracValue(equityrow.getCurrentValue()));
                    if (value1.startsWith("-")) {
                        value1 = value1.substring(1);
                        txt_gain_loss.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        txt_gain_loss.setTextColor(ScreenColor.positiveColor);
                    }
                    txt_gain_loss.setText(value1);
                    if (valuePV.startsWith("-")) {
                        valuePV = valuePV.substring(1);
                        txt_prev_day_gl.setTextColor(getResources().getColor(R.color.red));

                    } else {
                        txt_prev_day_gl.setTextColor(ScreenColor.positiveColor);
                    }
                    txt_prev_day_gl.setText(valuePV);
                }
            }
        }

        private void loadScripList() {
            scripList = new ArrayList<>();
            if (listData != null) {
                for (int i = 0; i < listData.length; i++) {
                    StructBondEuityDepositoryRow hld = listData[i];
                    if (hld.getScripCodeForRateUpdate() > 0) {
                        scripList.add(hld.getScripCodeForRateUpdate());
                    }
                }
            }
        }

        public void reloadDataForMKTData() {
            if (listData != null) {
                for (int i = 0; i < listData.length; i++) {
                    StructBondEuityDepositoryRow hld = listData[i];
                    if (hld.getScripCodeForRateUpdate() > 0) {
                        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(hld.getScripCodeForRateUpdate(), false);
                        if (mktWatch != null && hld.getLastRate()>0) {
                            hld.setLastrate(mktWatch.getLastRate());
                            hld.setPrevClose(mktWatch.getPClose());
                        }else{
                            hld.setLastrate(0);
                            hld.setPrevClose(0);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        }

        public ArrayList<Integer> getScripList() {
            return scripList;
        }

        private void refreshItem(int scripCode) {
            try {
                int position = -1;
                position = scripList.indexOf(scripCode);
                if (position != -1) {
                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode, true);
                    StructBondEuityDepositoryRow hld = listData[position];
                    if (mktWatch.getLastRate() != hld.getLastRate() && mktWatch.getLastRate()>0) {
                        hld.setLastrate(mktWatch.getLastRate());
                        hld.setPrevClose(mktWatch.getPClose());
                        notifyItemChanged(position);
                        //notifyItemChanged(listdata.getNoOfRow() - 1);
                        displayTotalRow();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void displayTotalRow(){

        if(closedholdingheader.getVisibility() == View.VISIBLE )  {
            if(closedHoldingData != null) {
                totCurVal.setText("\u20B9" + " " + Formatter.toNoFracValue(closedHoldingData.totalPurValue));
                String value1 = Formatter.toNoFracValue(closedHoldingData.totalGainLoss);// as per sriram sir 10Aug20
                if (value1.startsWith("-")) {
                    value1 = value1.substring(1);
                    tot_GL.setTextColor(Color.RED);
                } else {
                    tot_GL.setTextColor(ScreenColor.positiveColor);
                }
                tot_GL.setText("\u20B9" + " " + value1);

                String valuePV = Formatter.toNoFracValue(closedHoldingData.totalSellValue);// as per sriram sir 10Aug20
                tota_prevDayVal.setText("\u20B9" + " " + valuePV);
                tota_prevDayVal.setTextColor(Color.WHITE);
            }
        }else{
            equityDetail.calculateTotalGainLoss();
            totCurVal.setText("\u20B9" + " " + Formatter.toNoFracValue(equityDetail.getTotalCurrentValue()));
            String value1 = Formatter.toNoFracValue(equityDetail.getTotalGainLoss());// as per sriram sir 10Aug20
            if (value1.startsWith("-")) {
                value1 = value1.substring(1);
                tot_GL.setTextColor(Color.RED);
            } else {
                tot_GL.setTextColor(ScreenColor.positiveColor);
            }
            tot_GL.setText("\u20B9" + " " + value1);

            String valuePV = Formatter.toNoFracValue(equityDetail.getTotalPrevDayGainLoss());// as per sriram sir 10Aug20
            if (valuePV.startsWith("-")) {
                valuePV = valuePV.substring(1);
                tota_prevDayVal.setTextColor(Color.RED);
            } else {
                tota_prevDayVal.setTextColor(ScreenColor.positiveColor);
            }
            tota_prevDayVal.setText("\u20B9" + " " + valuePV);
        }
    }

    class DPHoldingEquityHandler extends Handler {
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
                                if(openholdingheader.getVisibility() == View.VISIBLE) {
                                    int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                    if (dpHoldingAdapter != null) {
                                        dpHoldingAdapter.refreshItem(scripCode);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        GlobalClass.onError("TradeLoginHandler : ", ex);
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetClosedPortfolioTask extends AsyncTask<Object, Void, String> {
        ProgressDialog mDialog;
        String CLientCode;

        GetClosedPortfolioTask(String _CLientCode) {
            this.CLientCode = fancyCodeList.get(_CLientCode);
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                PortfolioDataCallingHandler portFolio = new PortfolioDataCallingHandler();
                JSONArray respData = portFolio.getClosedPortFolio(CLientCode,"");
                if(respData != null) {
                    closedHoldingData = new ClosedHoldingData(CLientCode);
                    closedHoldingData.setDataSummary(respData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected void onPostExecute(String result) {
            try {
                mDialog.dismiss();

                if (closedHoldingData != null) {
                   openClosedChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ClosedHoldingAdapter extends RecyclerView.Adapter<ClosedHoldingAdapter.ViewHolder> {

        ArrayList<ClosedHoldingDetails> listData;
        //private ArrayList<Integer> scripList;

        public ClosedHoldingAdapter(ArrayList<ClosedHoldingDetails> listdata) {
            this.listData = listdata;
            //loadScripList();
        }

        public void reloadMainData(ArrayList<ClosedHoldingDetails> listdata){
            try {
                this.listData = listdata;
                //loadScripList();
                notifyDataSetChanged();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ClosedHoldingDetails equityrow = listData.get(position);
            holder.linear_Layout.setBackgroundColor((position % 2 == 0) ? ScreenColor.iTableRowOneBackColor : ScreenColor.iTableRowTwoBackColor);
            holder.setValue(equityrow);
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private ClosedHoldingDetails closedHoldingrow = null;
            private TextView txt_company_name, txt_pur_price, txt_cmp, cur_val, txt_qty, txt_gain_loss,txt_prev_day_gl;
            LinearLayout linear_Layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.linear_Layout = itemView.findViewById(R.id.linear_Layout);
                this.txt_company_name = itemView.findViewById(R.id.txt_company_name);
                this.txt_pur_price = itemView.findViewById(R.id.txt_pur_price);
                this.txt_cmp = itemView.findViewById(R.id.txt_cmp);
                this.cur_val = itemView.findViewById(R.id.cur_val);
                this.txt_qty = itemView.findViewById(R.id.txt_qty);
                this.txt_gain_loss = itemView.findViewById(R.id.txt_gain_loss);
                this.txt_prev_day_gl = itemView.findViewById(R.id.txt_prevdaygl);

                linear_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (closedHoldingrow != null && !closedHoldingrow.getCompanyName().equalsIgnoreCase("total")) {

                            Fragment fragment = new LongShortTableViewFragment(closedHoldingrow.getCompanyName(),
                                    closedHoldingrow.getScripCodeForRateUpdate(), closedHoldingrow.getISINNo(), equityDetail.clientCode,openholdingheader.getVisibility() == View.VISIBLE);
                            FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, fragment, "");
                            fragmentTransaction.addToBackStack("");
                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            fragmentTransaction.commit();
                        }
                    }
                });

                linear_Layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (closedHoldingrow != null && !closedHoldingrow.getCompanyName().equalsIgnoreCase("total")
                                && fancyCodeList.get(groupSpinner.getSelectedItem().toString()).equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())) {

                            // Initializing the popup menu and giving the reference as current context
                            PopupMenu popupMenu = new PopupMenu(getActivity(), linear_Layout);

                            // Inflating popup menu from popup_menu.xml file
                            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_holding, popupMenu.getMenu());
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    int scripCode = closedHoldingrow.getScripCodeForRateUpdate();
                                    String companyName = "BE-" + closedHoldingrow.getCompanyName();
                                    if (menuItem.getTitle().toString().equalsIgnoreCase("VALUATION")) {
                                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                                        groupsTokenDetails.scripCode.setValue(scripCode);
                                        groupsTokenDetails.scripName.setValue(companyName);
                                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                                        grplist.add(groupsTokenDetails);
                                        Fragment m_fragment = new ValuetionFragment(closedHoldingrow.getScripCodeForRateUpdate(), grplist, homeActivity.SELECTED_RADIO_BTN);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
                                    } else if (menuItem.getTitle().toString().equalsIgnoreCase("CHART")) {
                                        Fragment m_fragment = GraphFragment.newInstance(scripCode, companyName);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "");
                                    } else if (menuItem.getTitle().toString().equalsIgnoreCase("LATEST RESULTS")) {
                                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                                        groupsTokenDetails.scripCode.setValue(scripCode);
                                        groupsTokenDetails.scripName.setValue(companyName);
                                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                                        grplist.add(groupsTokenDetails);
                                        Fragment m_fragment = new LatestResultFragment(closedHoldingrow.getScripCodeForRateUpdate(), grplist, homeActivity.SELECTED_RADIO_BTN);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
                                    } else if (menuItem.getTitle().toString().equalsIgnoreCase("Market Depth")) {
                                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                                        groupsTokenDetails.scripCode.setValue(scripCode);
                                        groupsTokenDetails.scripName.setValue(companyName);
                                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                                        grplist.add(groupsTokenDetails);

                                        Fragment m_fragment;
                                        m_fragment = new MktdepthFragmentRC(scripCode, eShowDepth.MKTWATCH, grplist, null,
                                                (homeActivity).SELECTED_RADIO_BTN, false);
                                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.DEPTH.name);
                                    }
                                    return true;
                                }
                            });
                            // Showing the popup menu
                            popupMenu.show();
                        }
                        return false;
                    }
                });
            }

            public void setValue(ClosedHoldingDetails _equityrow) {
                closedHoldingrow = _equityrow;
                if (closedHoldingrow.getCompanyName().equalsIgnoreCase("Total")) {

                } else {

                    String _companyName = closedHoldingrow.getCompanyName();
                    txt_company_name.setText(_companyName);
                    String value = "";
                    String value1 = Formatter.toNoFracValue(closedHoldingrow.GainLoss);// as per sriram sir 10Aug20

                    value = Formatter.toNoFracValue(closedHoldingrow.PurVal);
                    txt_pur_price.setText(value);
                    txt_cmp.setText("");

                    //txt_cmp.setText(Formatter.toTwoDecimalValue(equityrow.getCMP()));
                    txt_qty.setText(closedHoldingrow.Qty+"");
                    cur_val.setText(Formatter.toNoFracValue(closedHoldingrow.SellVal));
                    if (value1.startsWith("-")) {
                        value1 = value1.substring(1);
                        txt_gain_loss.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        txt_gain_loss.setTextColor(ScreenColor.positiveColor);
                    }
                    txt_gain_loss.setText(value1);

                    txt_prev_day_gl.setText("");
                }
            }
        }
    }

}