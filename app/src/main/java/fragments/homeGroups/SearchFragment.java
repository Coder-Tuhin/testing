package fragments.homeGroups;

import static utils.Constants.BSE;
import static utils.MobileInfo.className;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Structure.Other.StructBuySell;
import Structure.Request.BC.SearchScript;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import Structure.Response.Scrip.StructSearchScrip;
import Structure.Response.Scrip.StructSearchScripRow1;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.Connect;
import connection.ConnectionProcess;
import connection.ReqSent;
import connection.SendDataToBCServer;
import enums.eExpiry;
import enums.eExpiryType;
import enums.eInstType;
import enums.eMessageCode;
import enums.eOrderType;
import enums.ePrefTAG;
import enums.eSearchScrip;
import enums.eShowDepth;
import enums.eSocketClient;
import enums.eWatchs;
import handler.GroupDetail;
import interfaces.OnPopupListener;
import utils.Connectivity;
import utils.Constants;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.VenturaException;
import view.AddScripToGroupPopup;
import wealth.wealthStructure.StructBondEuityDepositoryRow;

public class SearchFragment extends Fragment implements View.OnClickListener, ConnectionProcess {

    public SearchFragment() {
        // Required empty public constructor
    }

    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onSearchClick();
        }
    };
    private View searchLayout;
    @BindView(R.id.backarrow)
    ImageView backbtn_search;
    @BindView(R.id.searchedListView)
    ListView m_searchedListView;
    @BindView(R.id.edit_query)
    EditText edit_query;
    @BindView(R.id.searchscriprdgrp)
    RadioGroup searchscriprdgrp;

    @BindView(R.id.allRDbutton)
    RadioButton allRDbutton;
    @BindView(R.id.stockRDbutton)
    RadioButton stockRDbutton;
    @BindView(R.id.futureRDbutton)
    RadioButton futureRDbutton;
    @BindView(R.id.optionRDbutton)
    RadioButton optionRDbutton;
    @BindView(R.id.indicesRDbutton)
    RadioButton indicesRDbutton;
    @BindView(R.id.nseRDbutton)
    RadioButton nseRDbutton;
    @BindView(R.id.bseRDbutton)
    RadioButton bseRDbutton;
    @BindView(R.id.fnoRDbutton)
    RadioButton fnoRDbutton;
    @BindView(R.id.currRDbutton)
    RadioButton currRDbutton;
    @BindView(R.id.slbRDbutton)
    RadioButton slbRDbutton;
    @BindView(R.id.mcxRDbutton)
    RadioButton mcxRDbutton;
    @BindView(R.id.ncdexRDbutton)
    RadioButton ncdexRDbutton;


    private ArrayList<GroupsRespDetails> m_groupDetail;
    private SearchScripAdapter1 m_searchScripAdapter;
    private ArrayList<StructSearchScripRow1> m_searchList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
            ((Activity) getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

            m_groupDetail = GlobalClass.groupHandler.getUserDefineGroup().getEditableGroupStructureList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (searchLayout != null) return searchLayout;
        searchLayout = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, searchLayout);

        backbtn_search.setOnClickListener(this);
        edit_query.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                   if (edit_query.length() >= Constants.MIN_SEARCH_LENGTH) {
                       if (handler != null) handler.removeCallbacks(runnable);
                       onSearchClick();
                    }else{
                        GlobalClass.showToast(GlobalClass.latestContext, "Enter " + (Constants.MIN_SEARCH_LENGTH + 1) + " or more characters to Search");
                    }
                    return true;
                }
                return false;
            }
        });

        edit_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (handler != null) handler.removeCallbacks(runnable);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (s.toString().length() >= Constants.MIN_SEARCH_LENGTH && imm.isAcceptingText()) {
                    if (handler != null) handler.removeCallbacks(runnable);
                    handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(runnable, 1000);
                }
            }
        });
        setSelectedOptions();
        searchscriprdgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (handler != null) handler.removeCallbacks(runnable);
                onSearchClick();
            }
        });
        m_searchList = new ArrayList<>();
        GlobalClass.searchScripUIHandler = new SearchScripHandler();

        m_searchScripAdapter = new SearchScripAdapter1(GlobalClass.latestContext, R.layout.custom_search_item_new, R.id.item);
        m_searchedListView.setAdapter(m_searchScripAdapter);
        GlobalClass.groupHandler.getUserDefineGroup().sendAllGroupDetailRequest();

        return searchLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        connectToSearchEngineServer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) handler.removeCallbacks(runnable);
        if (GlobalClass.searchClient != null) {
            try {
                GlobalClass.searchClient.disconnect(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) handler.removeCallbacks(runnable);
        if (GlobalClass.searchClient != null) {
            try {
                GlobalClass.searchClient.disconnect(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.backarrow:
                    //m_fragment = new WatchFragment();
                    //GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, false, "");
                    GlobalClass.hideKeyboard(edit_query,getContext());
                    GlobalClass.fragmentManager.popBackStack();
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in" + className, e);
        }
    }

    private void setSelectedOptions() {
        if(GlobalClass.isCommodity()){
            stockRDbutton.setVisibility(View.GONE);
            indicesRDbutton.setVisibility(View.GONE);
            nseRDbutton.setVisibility(View.GONE);
            bseRDbutton.setVisibility(View.GONE);
            fnoRDbutton.setVisibility(View.GONE);
            currRDbutton.setVisibility(View.GONE);
            slbRDbutton.setVisibility(View.GONE);

            mcxRDbutton.setVisibility(View.VISIBLE);
            ncdexRDbutton.setVisibility(View.VISIBLE);
        }

        int searchSelection = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.SEARCH_SCREEN_SELECTION.name, eSearchScrip.ALL.value);
        eSearchScrip selectedOption = eSearchScrip.forValue(searchSelection);
        switch (selectedOption) {
            case ALL:
                allRDbutton.setChecked(true);
                break;
            case STOCKS:
                stockRDbutton.setChecked(true);
                break;
            case FUTURES:
                futureRDbutton.setChecked(true);
                break;
            case OPTIONS:
                optionRDbutton.setChecked(true);
                break;
            case INDICES:
                indicesRDbutton.setChecked(true);
                break;
            case NSE:
                nseRDbutton.setChecked(true);
                break;
            case BSE:
                bseRDbutton.setChecked(true);
                break;
            case FNO:
                fnoRDbutton.setChecked(true);
                break;
            case NSECUR:
                currRDbutton.setChecked(true);
                break;
            case SLBS:
                slbRDbutton.setChecked(true);
                break;
            case MCX:
                mcxRDbutton.setChecked(true);
                break;
            case NCDEX:
                ncdexRDbutton.setChecked(true);
                break;
            default:
                break;
        }
    }
    private void onSearchClick() {
        try {
            eSearchScrip selectedOption = eSearchScrip.ALL;
            switch (searchscriprdgrp.getCheckedRadioButtonId()) {
                case R.id.allRDbutton:
                    selectedOption = eSearchScrip.ALL;
                    break;
                case R.id.stockRDbutton:
                    selectedOption = eSearchScrip.STOCKS;
                    break;
                case R.id.futureRDbutton:
                    selectedOption = eSearchScrip.FUTURES;
                    break;
                case R.id.optionRDbutton:
                    selectedOption = eSearchScrip.OPTIONS;
                    break;
                case R.id.indicesRDbutton:
                    selectedOption = eSearchScrip.INDICES;
                    break;
                case R.id.nseRDbutton:
                    selectedOption = eSearchScrip.NSE;
                    break;
                case R.id.bseRDbutton:
                    selectedOption = eSearchScrip.BSE;
                    break;
                case R.id.fnoRDbutton:
                    selectedOption = eSearchScrip.FNO;
                    break;
                case R.id.currRDbutton:
                    selectedOption = eSearchScrip.NSECUR;
                    break;
                case R.id.slbRDbutton:
                    selectedOption = eSearchScrip.SLBS;
                    break;
                default:
                    break;
            }
            VenturaApplication.getPreference().storeSharedPref(ePrefTAG.SEARCH_SCREEN_SELECTION.name, selectedOption.value);

            String query = edit_query.getText().toString().trim();
            if (query.length() < Constants.MIN_SEARCH_LENGTH) {
                //GlobalClass.showToast(GlobalClass.latestContext, "Enter " + Constants.MIN_SEARCH_LENGTH + " or more characters to Search");
            } else {
                SearchScript searchScripReq = new SearchScript();
                short instType = eInstType.ALL.value;
                short expiry = eExpiry.ANY.value;
                short m_expType = eExpiryType.MONTHLY.value;
                searchScripReq.intExchange.setValue(selectedOption.value);
                searchScripReq.searchString.setValue(query);
                searchScripReq.shortInstrument.setValue(instType);
                searchScripReq.expiry.setValue(expiry);
                searchScripReq.expiryType.setValue(m_expType);

                SendDataToSearchServer sendDataToServer = new SendDataToSearchServer();
                sendDataToServer.sendSearchScripReq(searchScripReq);
                //clearSearchedData();
                //GlobalClass.hideKeyboard(edit_query, GlobalClass.latestContext);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
    private void connectToSearchEngineServer() {
        try {
            GlobalClass.showProgressDialog("Please wait connecting to Search Engine Server..." + ObjectHolder.connconfig.getSearchEngineLastDigit());
            Connect.connect(this.getContext(), this, eSocketClient.SEARCHENGINE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void connected() {
        try {
            GlobalClass.dismissdialog();
            GlobalClass.showKeyboard(edit_query, getContext());
        }catch (Exception ex){
            GlobalClass.onError("SearchFragment : ", ex);
        }
    }

    @Override
    public void serverNotAvailable() {
    }

    @Override
    public void sensexNiftyCame() {
    }

    public class SendDataToSearchServer extends AsyncTask<Object, Void, String> {
        private Context context;
        private byte m_data[];
        eMessageCode m_msgCode;

        public SendDataToSearchServer() {
            context = GlobalClass.latestContext;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                switch (m_msgCode) {
                    default:
                        sendData(m_data);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        private void sendData(byte data[]) {
            try {
                //GlobalClass.log("BC Request Sent : " +m_msgCode.value);
                if (Connectivity.IsConnectedtoInternet(context)) {
                    if (GlobalClass.searchClient != null && GlobalClass.searchClient.IsBroadcastConnected) {
                        GlobalClass.searchClient.sglrfinal = null;
                        GlobalClass.searchClient.send(data);
                        System.out.println("Search Request Send");
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        public void sendSearchScripReq(SearchScript req) {
            try {
                m_msgCode = eMessageCode.SEARCHSCRIPT;
                m_data = req.data.getByteArr((short) m_msgCode.value);
                this.execute();
            } catch (Exception e) {
                e.printStackTrace();
                GlobalClass.onError("Error in sending sendSearchScripReq request:" + getClass().getName(), e);
            }
        }
    }


    public class SearchScripHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt("msgCode");
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case SEARCHSCRIPT:
                            StructSearchScrip response1 = (StructSearchScrip) refreshBundle.getSerializable("struct");
                            populateSearchScripData(response1);
                            break;
                    }
                }
            } catch (Exception ex) {
                GlobalClass.onError("Error in " + className, ex);
            }
        }

        private void populateSearchScripData(StructSearchScrip response1) {
            try {
                if (response1.getFinalSize() <= 0) {
                    GlobalClass.showToast(GlobalClass.latestContext, "No Data Available");
                } else {
                    m_searchList = response1.getFinalScripList();
                    m_searchScripAdapter.refreshAdapter();
                    //GlobalClass.hideKeyboard(edit_query,getContext());
                }
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
        }

    }

    class SearchScripAdapter1 extends ArrayAdapter {
        //private int NO_POS = -1;
        private int selected_pos;
        Context context;
        int resource;

        public SearchScripAdapter1(Context context, int resource, int textResourceId) {
            super(context, resource, textResourceId);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return m_searchList.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
        }

        public void setSelectedPosition(int selected_pos) {
            this.selected_pos = selected_pos;
        }

        public int getSelectedPosition() {
            return selected_pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View v = LayoutInflater.from(GlobalClass.latestContext).inflate(resource, null);

            TextView txtScripName = (TextView) v.findViewById(R.id.scripnameitem);
            txtScripName.setText(m_searchList.get(position).getScripNameWithOutExch());

            TextView txtExchName = (TextView) v.findViewById(R.id.segmentitem);
            txtExchName.setText(m_searchList.get(position).getExchName());

            LinearLayout scripslay = v.findViewById(R.id.scripslay);

            TextView txtGrpName = (TextView) v.findViewById(R.id.grpnameitem);
            txtGrpName.setText(getGroupNamesForScripCode(m_searchList.get(position).scripCode.getValue()));

            TextView add = (TextView) v.findViewById(R.id.add_item);
            TextView viewDepth = (TextView) v.findViewById(R.id.view_item);

            viewDepth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMktDepth(position,eOrderType.NONE);
                }
            });
            scripslay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMktDepth(position,eOrderType.NONE);
                }
            });
            if (m_searchList.get(position).isSelected) {
                add.setBackground(getResources().getDrawable(R.drawable.button_border_selected));
            } else {
                add.setBackground(getResources().getDrawable(R.drawable.button_border));
            }
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_searchList.get(position).isSelected = !m_searchList.get(position).isSelected;
                    if (m_searchList.get(position).isSelected) {
                        GlobalClass.hideKeyboard(edit_query, context);
                        add.setBackground(getResources().getDrawable(R.drawable.button_border_selected));
                        AddScripToGroupPopup addScripToGroupPopup = new AddScripToGroupPopup(m_searchList);
                    } else {
                        add.setBackground(getResources().getDrawable(R.drawable.button_border));
                    }
                }
            });
            return v;
        }

        public void refreshAdapter() {
            this.notifyDataSetChanged();
        }
    }
    private void showMktDepth(int position, eOrderType orderType) {
        try {

            StructSearchScripRow1 ssr =  m_searchList.get(position);
            if (ssr != null) {
                GlobalClass.hideKeyboard(edit_query,getContext());
                ArrayList<GroupsTokenDetails> grpScripList = new ArrayList<>();
                GroupsTokenDetails td = new GroupsTokenDetails();
                td.scripCode.setValue(ssr.scripCode.getValue());
                td.scripName.setValue(ssr.getScripName());
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

    private String getGroupNamesForScripCode(int scripCode){
        String grpName = "";
        for(GroupsRespDetails grpDetails : m_groupDetail){
            if(grpDetails.isContainScripCode(scripCode)){
                grpName = grpName + grpDetails.groupName.getValue() + ", ";
            }
        }
        if(grpName.length()>0) grpName = grpName.substring(0,grpName.length() - 2);
        return grpName;
    }
}