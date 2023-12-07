package view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Structure.Other.StructBuySell;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;

import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
import connection.ReqSent;
import enums.WatchColumns;
import enums.eMessageCode;
import enums.eOrderType;
import enums.ePrefTAG;
import enums.eShowDepth;
import enums.eWatchs;
import interfaces.OnPopupListener;
import models.GrabberModel;
import swipemenu.SwipeMenu;
import swipemenu.SwipeMenuCreator;
import swipemenu.SwipeMenuItem;
import swipemenu.SwipeMenuListView;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.ScreenColor;

/**
 * Created by XTREMSOFT on 8/19/2016.
 */
public class DualListView extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener,
        AdapterView.OnItemLongClickListener, ReqSent, OnPopupListener,OnLongClickListener {
    //region [Variables]
    private SwipeMenuListView m_nameListView;
    private ListView m_valuesListView;
    private NameListAdapter m_nameListAdapter;
    private ValueListAdapter m_valueListAdapter;
    private Context m_context;
    private SyncedScrollListener m_name_syncScrollListener;
    private SyncedScrollListener m_values_syncScrollListener;
    private ArrayList<Integer> m_scripCodeList;
    private ArrayList<String> m_headerList;
    private int m_width, m_height;
    private int m_labelWidth;
    private boolean longPressAvailable = true;

    private final String m_className = getClass().getName();
    private SwipeRefreshLayout m_refreshLayout;
    private eShowDepth m_from;
    private int m_columnNumber;
    private GroupsRespDetails m_groupsRespDetails;
    private List<GrabberModel> columnlist = null;
    private boolean mktWatchStyle = true;

    private LinearLayout nameLinear;
    private HorizontalScrollView valueHorizontal;
    private boolean isShortingAvailable = true;
    private eWatchs watchType;
    private HomeActivity.RadioButtons radioButtons;
    private static int COLUMN_TAG = -1;
    //endregion

    //region [Constructor]
    public DualListView(final Context context, final GroupsRespDetails groupsRespDetails, eWatchs watchType,
                        eShowDepth from, HomeActivity.RadioButtons radioButtons) {
        super(context);
        try {
            GlobalClass.showProgressDialog("Loading...");
            this.m_from = from;
            this.watchType = watchType;
            this.m_context = context;
            this.m_groupsRespDetails = groupsRespDetails;
            this.radioButtons = radioButtons;
            columnlist = PreferenceHandler.getGraberList();
            setColumnNumber();
            populateListData();
            setScreenWidthHeight();
            addView(LayoutInflater.from(context).inflate(R.layout.dual_listview, null));
            init();
            initAdapter();
            addSwipMenu();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) context).runOnUiThread(() -> {
                        performSorting();
                        GlobalClass.dismissdialog();
                        nameLinear.setVisibility(VISIBLE);
                        valueHorizontal.setVisibility(VISIBLE);
                    });
                    if (!isShortingAvailable) this.cancel();
                    isShortingAvailable = false;
                }
            },1000,2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifiList(){
        mktWatchStyle = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.WATCHSTYLE.name,true);
        columnlist = PreferenceHandler.getGraberList();
        setColumnNumber();
        setScreenWidthHeight();
        m_headerList.clear();
        valueListTitle.removeAllViews();
        for (GrabberModel grabberModel : columnlist) {
            if (grabberModel.ischecked()) {
                String columnName = grabberModel.getGrabbername();
                m_headerList.add(columnName);
                MktLabel txtTitle = new MktLabel(m_context, columnName, columnName);
                txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
                valueListTitle.addView(txtTitle);
                txtTitle.setOnClickListener(this);
            }
        }
        m_nameListAdapter.notifyDataSetChanged();
        m_valueListAdapter.notifyDataSetChanged();
        performSorting();
    }

    private void addSwipMenu() {
        /*
        m_nameListView.setOnSwipeListener(new SwipeMenuListViewNew.onSwipeListener() {
            @Override
            public void swipeRight(int position) {
                showMktDepth(m_scripCodeList.get(position), eOrderType.BUY);
            }

            @Override
            public void swipeTop(int position) {

            }

            @Override
            public void swipeBottom(int position) {

            }

            @Override
            public void swipeLeft(int position) {
                showMktDepth(m_scripCodeList.get(position), eOrderType.SELL);
            }
        });*/

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                try {
                    int buySellSize = 50, bsTextSize = 14;
                    SwipeMenuItem openItem = new SwipeMenuItem(getContext());
                    // set item background
                    openItem.setBackground(new ColorDrawable(ScreenColor.GREEN));
                    // set item width
                    openItem.setWidth(dp2px(buySellSize));
                    // set item title
                    openItem.setTitle("Buy");
                    // set item title fontsize
                    openItem.setTitleSize(bsTextSize);
                    // set item title font color
                    openItem.setTitleColor(Color.WHITE);
                    // add to menu
                    menu.addMenuItem(openItem);

                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(ScreenColor.RED));
                    // set item width
                    deleteItem.setWidth(dp2px(buySellSize));
                    deleteItem.setTitleColor(Color.WHITE);
                    // set a icon
                    // deleteItem.setIcon(R.drawable.ic_delete);
                    deleteItem.setTitle("Sell");
                    // set item title fontsize
                    deleteItem.setTitleSize(bsTextSize);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        m_nameListView.setMenuCreator(creator);
        m_nameListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        m_nameListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        m_nameListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
                m_nameListView.smoothOpenMenu(position);

            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        m_nameListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showMktDepth(m_scripCodeList.get(position), eOrderType.BUY);
                        break;
                    case 1:
                        showMktDepth(m_scripCodeList.get(position), eOrderType.SELL);
                        break;
                }
                return false;
            }
        });
    }
    //endregion

    //region [Override Method]
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            switch (adapterView.getId()) {
                case R.id.nameListView:
                    showMktDepth(m_scripCodeList.get(i), eOrderType.NONE);
                    break;
                case R.id.valueListView:
                    showMktDepth(m_scripCodeList.get(i), eOrderType.NONE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {
            case R.id.nameListView:
                if (longPressAvailable) openMenuOnLongPress(i, view);
                longPressAvailable = false;
                break;
            case R.id.valueListView:
                if (longPressAvailable) openMenuOnLongPress(i, view);
                longPressAvailable = false;
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        String sortingTag = ((TextView) view).getText().toString();
        VenturaApplication.getPreference().storeSharedPref("sortingTag", sortingTag);
        String m_tag_sort = VenturaApplication.getPreference().getSharedPrefFromTag("m_tag_sort", "");

        if (m_tag_sort.equalsIgnoreCase(sortingTag + "-")) {
            VenturaApplication.getPreference().storeSharedPref("m_tag_sort", sortingTag + "+");
        } else {
            VenturaApplication.getPreference().storeSharedPref("m_tag_sort", sortingTag + "-");
        }
        performSorting();
    }


    @Override
    public void reqSent(int msgCode) {

    }

    //endregion
    private LinearLayout valueListTitle;
    //region [Private Method]
    private void init() {
        try {
            nameLinear = (LinearLayout) findViewById(R.id.nameLinear);
            int width = getResources().getDisplayMetrics().widthPixels;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width / 3, ViewGroup.LayoutParams.MATCH_PARENT);
            nameLinear.setLayoutParams(lp);
            valueHorizontal = (HorizontalScrollView) findViewById(R.id.valueHorizontal);
            LinearLayout.LayoutParams lpOne = new LinearLayout.LayoutParams((width / 3) * 2, ViewGroup.LayoutParams.MATCH_PARENT);
            valueHorizontal.setLayoutParams(lpOne);

            mktWatchStyle = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.WATCHSTYLE.name,true);
            m_nameListView = (SwipeMenuListView) findViewById(R.id.nameListView);
            m_valuesListView = (ListView) findViewById(R.id.valueListView);
            m_name_syncScrollListener = new SyncedScrollListener();
            m_values_syncScrollListener = new SyncedScrollListener();

            findViewById(R.id.name).setOnClickListener(this);
            valueListTitle = (LinearLayout) findViewById(R.id.valueListTitle);
            m_headerList = new ArrayList<>();
            for (GrabberModel grabberModel : columnlist) {
                if (grabberModel.ischecked()) {
                    String columnName = grabberModel.getGrabbername();
                    m_headerList.add(columnName);
                    MktLabel txtTitle = new MktLabel(m_context, columnName, columnName);
                    txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
                    valueListTitle.addView(txtTitle);
                    txtTitle.setOnClickListener(this);
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void initAdapter() {
        try {
            m_nameListAdapter = new NameListAdapter();
            m_valueListAdapter = new ValueListAdapter();
            m_nameListView.setAdapter(m_nameListAdapter);
            m_valuesListView.setAdapter(m_valueListAdapter);
            m_nameListView.setOnScrollListener(m_name_syncScrollListener);
            //m_nameListView.setOnScrollListener(new SyncedScrollListener(m_valuesListView)); //m_name_syncScrollListener);
            m_nameListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
            m_valuesListView.setOnScrollListener(m_values_syncScrollListener);
            //m_valuesListView.setOnScrollListener(new SyncedScrollListener(m_nameListView));//m_values_syncScrollListener);
            m_valuesListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
            m_nameListView.setOnItemClickListener(this);
            m_valuesListView.setOnItemClickListener(this);
            m_nameListView.setOnItemLongClickListener(this);
            m_valuesListView.setOnItemLongClickListener(this);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setColumnNumber() {
        m_columnNumber = 0;
        for (int i = 0; i < columnlist.size(); i++) {
            if (columnlist.get(i).ischecked()) {
                m_columnNumber = m_columnNumber + 1;
            }
        }
    }

    private void setScreenWidthHeight() {
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) m_context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            m_height = displaymetrics.heightPixels;
            m_width = displaymetrics.widthPixels;
            if (m_width < m_height) {
                if (m_columnNumber <= 3) {
                    m_labelWidth = (int) ((2 * (m_width / 3)) / m_columnNumber);
                } else {
                    m_labelWidth = (int) ((2 * (m_width / 3)) / 3);
                }
            } else {
                if (m_columnNumber <= 5) {
                    m_labelWidth = (int) ((2 * (m_width / 3)) / m_columnNumber);
                } else {
                    m_labelWidth = (int) ((2 * (m_width / 3)) / 5);
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void populateListData() {
        try {
            m_scripCodeList = new ArrayList<>();
            m_scripCodeList.addAll(m_groupsRespDetails.hm_grpTokenDetails.keySet());
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void showMktDepth(int scripCode, eOrderType orderType) {
        ArrayList<GroupsTokenDetails> grpScripList = new ArrayList<>();
        grpScripList.addAll(m_groupsRespDetails.hm_grpTokenDetails.values());
        StructBuySell buySell = null;
        if (orderType != eOrderType.NONE) {
            buySell = new StructBuySell();
            buySell.buyOrSell = orderType;
            buySell.modifyOrPlace = eOrderType.PLACE;
            buySell.showDepth = eShowDepth.MKTWATCH;
        }
        GlobalClass.openDepth(scripCode, m_from, grpScripList, buySell);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void openMenuOnLongPress(int index, View view) {
        int scripCode = m_scripCodeList.get(index);
        String scripName = m_groupsRespDetails.hm_grpTokenDetails.get(scripCode).scripName.getValue();
        LongclickPopupMenu longclickPopupMenu = new LongclickPopupMenu(m_groupsRespDetails,radioButtons,this);
        longclickPopupMenu.showItemOptions(this, view, scripCode,scripName ,m_from,index);
    }

    private void performSorting() {
        final String tag = VenturaApplication.getPreference().getSharedPrefFromTag("sortingTag", "");
        final boolean isNormal = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.PER_CHGSETTING.name,false);
        try {
            final int multi_factor = 100;
            final ArrayList<StaticLiteMktWatch> arrayList = new ArrayList<>();
            for (int i = 0; i < m_scripCodeList.size(); i++) {
                arrayList.add(GlobalClass.mktDataHandler.getMkt5001Data(m_scripCodeList.get(i),false));
            }

            Collections.sort(arrayList, new Comparator<StaticLiteMktWatch>() {
                @Override
                public int compare(StaticLiteMktWatch lhs, StaticLiteMktWatch rhs) {
                    double factor = 0;
                    if (tag.equalsIgnoreCase("Name")) {
                        String lhsN = m_groupsRespDetails.hm_grpTokenDetails.get(lhs.getToken()).scripName.getValue();
                        String rhsN = m_groupsRespDetails.hm_grpTokenDetails.get(rhs.getToken()).scripName.getValue();
                        return lhsN.compareTo(rhsN);
                    } else if (tag.equalsIgnoreCase("Current")) {
                        factor = (lhs.getLw().lastRate.getValue() - rhs.getLw().lastRate.getValue()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("%Chg")) {
                        double absChange = lhs.getPerChg(), absChange1 = rhs.getPerChg();
                        if (!isNormal){
                            absChange = Math.abs(absChange);
                            absChange1 = Math.abs(absChange1);
                        }
                        factor = (absChange - absChange1) * multi_factor;

                    } else if (tag.equalsIgnoreCase("Qty")) {
                        factor = (lhs.getLw().totalQty.getValue() - rhs.getLw().totalQty.getValue()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("BidQty")) {
                        factor = (lhs.getLw().bidQty.getValue() - rhs.getLw().bidQty.getValue()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("Bid")) {
                        factor = (lhs.getBestBid() - rhs.getBestBid()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("Offer")) {
                        factor = (lhs.getBestASK() - rhs.getBestASK()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("OffQty")) {
                        factor = (lhs.getLw().offQty.getValue() - rhs.getLw().offQty.getValue()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("High")) {
                        factor = (lhs.getHigh() - rhs.getHigh()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("Low")) {
                        factor = (lhs.getLow() - rhs.getLow()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("Open")) {
                        factor = (lhs.getOpen() - rhs.getOpen()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("PClose")) {
                        factor = (lhs.getPClose() - rhs.getPClose()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("Value")) {
                        factor = (lhs.getLw().offRate.getValue() * lhs.getLw().totalQty.getValue() - rhs.getLw().offRate.getValue() * rhs.getLw().totalQty.getValue()) / multi_factor;
                    } else if (tag.equalsIgnoreCase("Time")) {
                        factor = (lhs.getLw().time.getValue().getTime() - rhs.getLw().time.getValue().getTime());
                    } else if (tag.equalsIgnoreCase("Change")) {
                        double absChange = lhs.getAbsChg(), absChange1 = rhs.getAbsChg();
                        factor = (absChange - absChange1) * multi_factor;
                    } else if (tag.equalsIgnoreCase("TickAvg")) {
                        factor = (lhs.getAverage() - rhs.getAverage()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("OpenInt")) {
                        factor = (lhs.getLw().openInterest.getValue() - rhs.getLw().openInterest.getValue()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("UpperCkt")) {
                        factor = (lhs.getUpperCkt() - rhs.getUpperCkt()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("LowerCkt")) {
                        factor = (lhs.getLowerCkt() - rhs.getLowerCkt()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("OIHigh")) {
                        factor = (lhs.getHighOI() - rhs.getHighOI()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("OILow")) {
                        factor = (lhs.getLowOI() - rhs.getLowOI()) * multi_factor;
                    } else if (tag.equalsIgnoreCase("Scripcode")) {
                        factor = (lhs.getToken() - rhs.getToken()) * multi_factor;
                    }
                    return (int) factor;
                }
            });

            //for descending order
            setSortingImage(tag);

            if (VenturaApplication.getPreference().getSharedPrefFromTag("m_tag_sort", "").equalsIgnoreCase(tag + "-")) {
                for (int i = 0; i < arrayList.size() / 2; i++) {
                    StaticLiteMktWatch temp = arrayList.get(i);
                    arrayList.set(i, arrayList.get(arrayList.size() - 1 - i));
                    arrayList.set(arrayList.size() - 1 - i, temp);
                }
            }
            m_scripCodeList.clear();
            for (StaticLiteMktWatch com : arrayList) {
                m_scripCodeList.add(com.getToken());
            }
            notifyDataSetChanged();
            for (int i = 0; i < m_valueListAdapter.getCount(); i++) {
                refreshData(eMessageCode.LITE_MW, i, m_scripCodeList.get(i));
            }
            for (int i = 0; i < m_valueListAdapter.getCount(); i++) {
                refreshData(eMessageCode.STATIC_MW, i, m_scripCodeList.get(i));
            }
            // sorting_enable = false;
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    private void setSortingImage(String tag) {
        try {
            LinearLayout valueListTitle = (LinearLayout) findViewById(R.id.valueListTitle);
            ((TextView) findViewById(R.id.name)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            for (int i = 0; i < valueListTitle.getChildCount(); i++) {
                ((TextView) valueListTitle.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (((TextView) valueListTitle.getChildAt(i)).getText().toString().equalsIgnoreCase(tag)) {
                    int drawablePadding = 3;
                    String m_tag_sort = VenturaApplication.getPreference().getSharedPrefFromTag("m_tag_sort", "");
                    if (m_tag_sort.equalsIgnoreCase(tag + "+")) {
                        (((TextView) valueListTitle.getChildAt(i))).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                        (((TextView) valueListTitle.getChildAt(i))).setCompoundDrawablePadding(drawablePadding);
                    } else {
                        VenturaApplication.getPreference().storeSharedPref("m_tag_sort", tag + "-");
                        (((TextView) valueListTitle.getChildAt(i))).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                        (((TextView) valueListTitle.getChildAt(i))).setCompoundDrawablePadding(drawablePadding);
                    }
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    //endregion

    //region [Public Method]
    public void notifyDataSetChanged(final GroupsRespDetails groupsRespDetailsTemp) {
        try {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        m_groupsRespDetails = groupsRespDetailsTemp;
                        populateListData();
                        m_nameListAdapter.notifyDataSetChanged();
                        m_valueListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyDataSetChanged() {
        try {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        m_nameListAdapter.notifyDataSetChanged();
                        m_valueListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    public void callRefreshData(eMessageCode emessagecode, int scripCode) {
        try {
            ListView list = m_valuesListView;
            int start = list.getFirstVisiblePosition();
            int index = m_scripCodeList.indexOf(scripCode);
            int last = list.getLastVisiblePosition();
            if (index >= start && index <= last) {
                refreshData(emessagecode, index, scripCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData(eMessageCode emessagecode, int index, int scripCode) {
        try {
            View convertView = m_valuesListView.getChildAt(index - m_valuesListView.getFirstVisiblePosition());
            if (convertView == null) return;
            StaticLiteMktWatch data = null;
            try {
                data = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (data == null) return;

            if (emessagecode == eMessageCode.LITE_MW) {

                double absChange = data.getAbsChg();
                double lastRate = data.getLastRate();
                NumberFormat formatter = data.getFormatter();

                TextView txtVal = (TextView) convertView.findViewById(WatchColumns.CURRENT.id);
                if (txtVal != null) {
                    double preval = 0;
                    try {
                        preval = formatter.parse(txtVal.getText().toString()).doubleValue();
                    } catch (Exception e) {
                        GlobalClass.onError("Error in " + m_className, e);
                    }
                    if (preval != 0) {
                        if (preval < lastRate) {
                            txtVal.setBackgroundResource(R.drawable.tick_up_bg);
                        } else if (preval > lastRate) {
                            txtVal.setBackgroundResource(R.drawable.tick_down_bg);
                        } else {
                            txtVal.setBackgroundResource(android.R.color.transparent);
                        }
                        txtVal.setTextColor(Color.WHITE);
                    }
                    txtVal.setText(formatter.format(lastRate));
                }
                double perChange = data.getPerChg();
                txtVal = (TextView) convertView.findViewById(WatchColumns.CHG.id);

                if (txtVal != null) {
                    String chg = formatter.format(perChange) + "%";
                    txtVal.setText(chg);
                    if (chg.contains("-")) {
                        txtVal.setTextColor(ScreenColor.RED);
                    } else if (chg.equalsIgnoreCase("0.00%")) {
                        txtVal.setTextColor(Color.WHITE);
                    } else {
                        txtVal.setTextColor(ScreenColor.GREEN);
                    }
                }
                txtVal = (TextView) convertView.findViewById(WatchColumns.QTY.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.convertIntToValue(data.getLw().totalQty.getValue()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.BID_QTY.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().bidQty.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.BID.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getBestBid()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OFFER.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getBestASK()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OFF_QTY.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().offQty.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.HIGH.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getHigh()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.LOW.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getLow()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.VALUE.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.convertDoubleToValue(data.getAverage() * data.getLw().totalQty.getValue()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.CHANGE.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(absChange));
                txtVal = (TextView) convertView.findViewById(WatchColumns.TICK_AVG.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getAverage()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OPEN_INT.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().openInterest.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.TIME.id);
                if (txtVal != null)
                    txtVal.setText(DateUtil.dateFormatter(data.getLw().time.getValue(), Constants.HHMMSS) + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.SCRIPCODE.id);
                if (txtVal != null)
                    txtVal.setText(data.getToken() + "");

            } else if(emessagecode == eMessageCode.STATIC_MW) {

                NumberFormat formatter = data.getFormatter();
                TextView txtVal = (TextView) convertView.findViewById(WatchColumns.HIGH.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getHigh()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.LOW.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getLow()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OPEN.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getOpen()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.P_CLOSE.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getPClose()));
                /*txtVal = (TextView) convertView.findViewById(WatchColumns.TIME.id);
                if (txtVal != null)
                    txtVal.setText(DateUtil.dateFormatter(data.getSw().time.getValue(), Constants.HHMMSS) + "");*/
                txtVal = (TextView) convertView.findViewById(WatchColumns.UPPER_CKT.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getUpperCkt()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.LOWER_CKT.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getLowerCkt()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OIHIGH.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.roundFormatter.format(data.getHighOI())+ "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.OILOW.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.roundFormatter.format(data.getLowOI()) + "");

            } else if(emessagecode == eMessageCode.OpenInt) {
                TextView txtVal = null;
                txtVal = (TextView) convertView.findViewById(WatchColumns.OPEN_INT.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().openInterest.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.OIHIGH.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.roundFormatter.format(data.getHighOI())+ "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.OILOW.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.roundFormatter.format(data.getLowOI()) + "");

            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }


    @Override
    public void onPopupClose() {
        longPressAvailable = true;
    }

    @Override
    public void onDelete(int position) {
        if (m_nameListView != null && m_valuesListView != null){
            m_scripCodeList.remove(position);
            //m_nameListView.removeViewAt(position);
            //m_valuesListView.removeViewAt(position);
            m_nameListAdapter.notifyDataSetChanged();
            m_valueListAdapter.notifyDataSetChanged();
        }
    }
    //endregion

    //region [inner class]
    class NameListAdapter extends BaseAdapter {
        @Override
        public long getItemId(int i) {
            return 0;
        }

       /* @Override
        public boolean getSwipEnableByPosition(int position) {
            return true;
        }*/

        public int getCount() {
            try {
                return m_scripCodeList.size();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        public String getItem(int position) {
            try {
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                LinearLayout child;
                if (convertView == null) {
                    convertView = new LinearLayout(m_context);
                    child = new LinearLayout(m_context);
                    child.setGravity(Gravity.CENTER_VERTICAL);
                    ((LinearLayout) convertView).addView(child);
                    child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getItemHeight()));

                    child.removeAllViews();
                    NameTextView name = new NameTextView("Name", 14);
                    child.addView(name);
                    if (mktWatchStyle) {
                        child.setOrientation(VERTICAL);
                        NameTextView subName = new NameTextView("SubName", 9);
                        child.addView(subName);
                    }
                }else if(ObjectHolder.isNeedDisplayChange){
                    ((LinearLayout) convertView).removeAllViews();
                    child = new LinearLayout(m_context);
                    child.setGravity(Gravity.CENTER_VERTICAL);
                    ((LinearLayout) convertView).addView(child);
                    child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getItemHeight()));

                    child.removeAllViews();
                    NameTextView name = new NameTextView("Name", 14);
                    child.addView(name);
                    if (mktWatchStyle) {
                        child.setOrientation(VERTICAL);
                        NameTextView subName = new NameTextView("SubName", 9);
                        child.addView(subName);
                    }
                } else{
                    child = (LinearLayout) ((LinearLayout) convertView).getChildAt(0);
                }
                child.getLayoutParams().height=getItemHeight();
                if (position % 2 == 1) {
                    child.setBackgroundColor(Color.BLACK);
                } else {
                    child.setBackgroundColor(Color.rgb(25, 22, 17));
                }
                GroupsTokenDetails groupsTokenDetails = m_groupsRespDetails.hm_grpTokenDetails.get(m_scripCodeList.get(position));
                if (groupsTokenDetails != null) {
                    if (mktWatchStyle) {
                        String[] splitArray = groupsTokenDetails.getSymbolAndDetail();
                        ((TextView) child.findViewWithTag("Name")).setText(splitArray[0]);
                        ((TextView) child.findViewWithTag("SubName")).setText(splitArray[1]);
                    } else {
                        ((TextView) child.findViewWithTag("Name")).setText(groupsTokenDetails.getScripName());
                    }
                    if(groupsTokenDetails.isNewlyAdded){
                        ((TextView) child.findViewWithTag("Name")).setTextColor(Color.CYAN);
                    }else{
                        ((TextView) child.findViewWithTag("Name")).setTextColor(ScreenColor.textColor);
                    }
                }
                return convertView;
            } catch (Exception e) {
                GlobalClass.onError("Error in " + m_className, e);
                return null;
            }
        }
    }


    class ValueListAdapter extends BaseAdapter {
        int itemHeight = (int) getResources().getDimension(R.dimen.item_height);

        @Override
        public long getItemId(int i) {
            return 0;
        }

        /*@Override
        public boolean getSwipEnableByPosition(int position) {
            return true;
        }*/

        public int getCount() {
            try {
                return m_scripCodeList.size();
            } catch (Exception e) {
                GlobalClass.log("", "", "Error in getView method of ValueListAdapter in ");
                return 0;
            }
        }

        public String getItem(int position) {
            try {
                return null;
            } catch (Exception e) {
                GlobalClass.log("", "", "Error in getView method of ValueListAdapter in ");
                return "";
            }
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = new LinearLayout(m_context);//li.inflate(R.layout.value_list_item, null);
                    for (String colName : m_headerList) {
                        MktLabel label = new MktLabel(m_context, "0.0", colName);
                        ((LinearLayout) convertView).addView(label);
                    }
                    convertView.setMinimumHeight(itemHeight);
                }else if(ObjectHolder.isNeedDisplayChange){
                    GlobalClass.log("Duallist : isNeedDisplayChange : " + ObjectHolder.isNeedDisplayChange);
                    ((LinearLayout) convertView).removeAllViews();
                    for (String colName : m_headerList) {
                        MktLabel label = new MktLabel(m_context, "0.0", colName);
                        ((LinearLayout) convertView).addView(label);
                    }
                }

                if (position % 2 == 1) {
                    convertView.setBackgroundColor(Color.BLACK);
                } else {
                    convertView.setBackgroundColor(Color.rgb(25, 22, 17));
                }
                int  scripCode =m_scripCodeList.get(position);
                setInitValue(scripCode, convertView);
                return convertView;
            } catch (Exception e) {
                GlobalClass.log("", "", "Error in getView method of ValueListAdapter in ");
                return null;
            }
        }
    }

    public void setInitValue(final int scripCode, final View convertView) {
        try {
            StaticLiteMktWatch data = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
            if (data != null) {

                NumberFormat formatter = data.getFormatter();

                double absChange = data.getAbsChg(), lastRate = data.getLastRate();

                TextView txtVal = (TextView) convertView.findViewById(WatchColumns.CURRENT.id);
                if (txtVal != null) {
                    txtVal.setText(formatter.format(lastRate));
                }
                double perChange = data.getPerChg();
                txtVal = (TextView) convertView.findViewById(WatchColumns.CHG.id);
                if (txtVal != null) {
                    String chg = formatter.format(perChange) + "%";
                    txtVal.setText(chg);
                    if (chg.contains("-")) {
                        txtVal.setTextColor(ScreenColor.RED);
                    } else if (chg.equalsIgnoreCase("0.00%")) {
                        txtVal.setTextColor(Color.WHITE);
                    } else {
                        txtVal.setTextColor(ScreenColor.GREEN);
                    }
                }
                txtVal = (TextView) convertView.findViewById(WatchColumns.QTY.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.convertIntToValue(data.getLw().totalQty.getValue()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.BID_QTY.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().bidQty.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.BID.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getBestBid()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OFFER.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getBestASK()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OFF_QTY.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().offQty.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.HIGH.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getHigh()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.LOW.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getLow()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OPEN.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getOpen()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.P_CLOSE.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getPClose()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.VALUE.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.convertDoubleToValue(data.getAverage() * data.getLw().totalQty.getValue()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.CHANGE.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(absChange));
                txtVal = (TextView) convertView.findViewById(WatchColumns.TICK_AVG.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getAverage()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OPEN_INT.id);
                if (txtVal != null)
                    txtVal.setText(data.getLw().openInterest.getValue() + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.TIME.id);
                if (txtVal != null)
                    txtVal.setText(DateUtil.dateFormatter(data.getLw().time.getValue(), Constants.HHMMSS) + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.UPPER_CKT.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getUpperCkt()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.LOWER_CKT.id);
                if (txtVal != null)
                    txtVal.setText(formatter.format(data.getLowerCkt()));
                txtVal = (TextView) convertView.findViewById(WatchColumns.OIHIGH.id);
                if (txtVal != null)
                    txtVal.setText(Formatter.roundFormatter.format(data.getHighOI()) + "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.OILOW.id);
                if (txtVal != null) txtVal.setText(Formatter.roundFormatter.format(data.getLowOI())+ "");
                txtVal = (TextView) convertView.findViewById(WatchColumns.SCRIPCODE.id);
                if (txtVal != null) txtVal.setText(data.getSw().token.getValue()+ "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class SyncedScrollListener implements SwipeMenuListView.OnScrollListener {
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            try {
                ListView mSyncedView = m_valuesListView;
                if (view == m_nameListView) {
                    mSyncedView = m_valuesListView;
                } else if (view == m_valuesListView) {
                    mSyncedView = m_nameListView;
                }
                if (m_refreshLayout != null) {
                    int topRowVerticalPosition = (mSyncedView == null || mSyncedView.getChildCount() == 0) ?
                            0 : mSyncedView.getChildAt(0).getTop();
                    m_refreshLayout.setEnabled((topRowVerticalPosition >= 0));
                }
                if (visibleItemCount == 0) {
                    return;
                }
                View c = view.getChildAt(0);
                if (c != null) {
                    if (mSyncedView.getChildAt(0) != null) {
                        if (mSyncedView.getChildAt(0).getTop() != c.getTop())
                            mSyncedView.setSelectionFromTop(firstVisibleItem, c.getTop());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            try {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view == m_nameListView) {
                        m_valuesListView.setOnScrollListener(m_values_syncScrollListener);
                    } else if (view == m_valuesListView) {
                        m_nameListView.setOnScrollListener(m_name_syncScrollListener);

                    }
                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    if (view == m_nameListView) {
                        m_valuesListView.setOnScrollListener(null);
                    } else if (view == m_valuesListView) {
                        m_nameListView.setOnScrollListener(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private int getItemHeight(){
        return (int) getResources().getDimension(R.dimen.item_height);
        //return (int) getResources().getDimension(mktWatchStyle?R.dimen.item_height_large:R.dimen.item_height);
    }

    public class MktLabel extends AppCompatTextView {
        // public String colName;

        public MktLabel(Context context, String text, String tag) {
            super(context);
            try {
                this.setText(text);
                this.setTextColor(getResources().getColor(R.color.white));
                this.setSingleLine(true);
                this.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                this.setWidth(m_labelWidth - 6);
                this.setHeight(getItemHeight());
                // this.setTag(tag);
                this.setId(WatchColumns.getID(tag));
                this.setPadding(0, 0, 8, 0);
                // colName = tag;
            } catch (Exception e) {
                GlobalClass.onError("Error in " + m_className, e);
            }
        }
    }

    public class NameTextView extends AppCompatTextView {
        public NameTextView(String tag, int txtSize) {
            super(m_context);
            try {
                this.setTextColor(ScreenColor.textColor);
                if (tag.equalsIgnoreCase("SubName"))
                    this.setTextSize(txtSize);
                this.setSingleLine(true);
                this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                this.setTag(tag);
                this.setHorizontallyScrolling(true);
                this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                this.setMarqueeRepeatLimit(1);
                this.setSelected(true);
            } catch (Exception e) {
                GlobalClass.onError("Error in " + m_className, e);
            }
        }
    }


    public static int getColumnTag() {
        if (COLUMN_TAG<0){
            ArrayList<GrabberModel> graberList = PreferenceHandler.getGraberList();
            for (GrabberModel gm:graberList){
                String columnName = gm.getGrabbername();
                if ((columnName.equalsIgnoreCase(WatchColumns.OPEN.name)
                        || columnName.equalsIgnoreCase(WatchColumns.HIGH.name)
                        || columnName.equalsIgnoreCase(WatchColumns.LOW.name)
                        || columnName.equalsIgnoreCase(WatchColumns.UPPER_CKT.name)
                        || columnName.equalsIgnoreCase(WatchColumns.LOWER_CKT.name)
                        || columnName.equalsIgnoreCase(WatchColumns.OIHIGH.name)
                        || columnName.equalsIgnoreCase(WatchColumns.OILOW.name)
                )&& gm.ischecked()) {
                    COLUMN_TAG = 1;
                    break;
                }else {
                    COLUMN_TAG = 2;
                }
            }
        }
        return COLUMN_TAG;
    }

    public static void setColumnTag(int columnTag) {
        COLUMN_TAG = columnTag;
    }
    //endregion
}