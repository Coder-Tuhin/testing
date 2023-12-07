package wealth.mv;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsTokenDetails;
import chart.GraphFragment;
import enums.eForHandler;
import enums.eFragments;
import enums.eLogType;
import enums.eMessageCode;
import enums.eScreen;
import fragments.LatestResultFragment;
import fragments.ValuetionFragment;
import utils.Formatter;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;
import wealth.Dialogs;
import wealth.FontTextView;
import wealth.VenturaServerConnect;
import wealth.wealthStructure.ClosedHoldingData;
import wealth.wealthStructure.ClosedHoldingDetails;
import wealth.wealthStructure.StructHoldingShortLongTermDetail;
import wealth.wealthStructure.StructHoldingShortLongTermRow;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LongShortTableViewFragment extends Fragment implements View.OnClickListener {

    private String companyName;
    private int scripCode;
    private String isin;
    private String clientCode;
    private boolean isFromOpen;

    public static Handler longShortUIHandler;

    private RadioGroup holdingRG;
    private RadioButton openRd, closedRd;
    private ClosedHoldingData closedHoldingData;

    LinearLayout purDateLayout;
    LinearLayout purSellDateLay;
    View lineAfterPurSellLay;
    TextView sellPriceTitle;
    TextView sellValueTitle;
    TextView purDate;
    TextView purDate2;
    TextView sellDate;
    TextView purPrice;
    TextView sellprice;
    TextView purVal;
    TextView sellVal;
    TextView qty;
    TextView gainLoss;
    DecimalFormat decimalComaFormat = new DecimalFormat("##,##,###.##");


    public LongShortTableViewFragment() {
        // Required empty public constructor
    }

    public LongShortTableViewFragment(String _companyName, int _scripCode, String _isin, String _clientCode, boolean _isFromOpenHolding) {
        // Required empty public constructor
        companyName = _companyName;
        scripCode = _scripCode;
        isin = _isin;
        this.clientCode = _clientCode;
        isFromOpen = _isFromOpenHolding;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        super.onPause();
        longShortUIHandler = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (longShortUIHandler == null)
            longShortUIHandler = new LongShortHandler();
    }

    private View myFragmentView;
    private LinearLayout main, headerLayout;
    FrameLayout bottom_sheet;
    BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private ScrollView datascrollView;
    private HomeActivity homeActivity;
    private StructHoldingShortLongTermDetail shortLongTermDetail;
    private Typeface verdanaType, verdanaTypeBold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (myFragmentView == null) {
            myFragmentView = inflater.inflate(R.layout.fragment_long_short_table_view_fragmenr, container, false);

            main = (LinearLayout) myFragmentView.findViewById(R.id.data_layout);
            main.removeAllViews();
            headerLayout = (LinearLayout) myFragmentView.findViewById(R.id.header_layout);
            headerLayout.removeAllViews();

            datascrollView = (ScrollView) myFragmentView.findViewById(R.id.scrollView);
            verdanaType = Typeface.DEFAULT;
            verdanaTypeBold = Typeface.DEFAULT_BOLD;

            holdingRG = (RadioGroup) myFragmentView.findViewById(R.id.holdingRG);
            openRd = (RadioButton) myFragmentView.findViewById(R.id.openRd);
            closedRd = (RadioButton) myFragmentView.findViewById(R.id.closedRd);

            bottom_sheet = myFragmentView.findViewById(R.id.bottomSheet);
            bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            myFragmentView.findViewById(R.id.transparentBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            });

            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        myFragmentView.findViewById(R.id.transparentBack).setVisibility(View.GONE);
                    } else {
//                            myFragmentView.findViewById(R.id.longShortRootLayout).setForeground(new ColorDrawable(getResources().getColor(R.color.transperent)));
                        myFragmentView.findViewById(R.id.transparentBack).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        myFragmentView.findViewById(R.id.longShortRootLayout).setForeground(new ColorDrawable(getResources().getColor(R.color.transperent)));
//                    }
                }
            });

            purDate = myFragmentView.findViewById(R.id.purDate);
            sellDate = myFragmentView.findViewById(R.id.sellDate);
            purPrice = myFragmentView.findViewById(R.id.purPrice);
            sellprice = myFragmentView.findViewById(R.id.sellprice);
            purVal = myFragmentView.findViewById(R.id.purVal);
            sellVal = myFragmentView.findViewById(R.id.sellVal);
            qty = myFragmentView.findViewById(R.id.qty);
            gainLoss = myFragmentView.findViewById(R.id.gainLoss);
            purDate2 = myFragmentView.findViewById(R.id.purDate2);
            gainLoss = myFragmentView.findViewById(R.id.gainLoss);
            gainLoss = myFragmentView.findViewById(R.id.gainLoss);
            purDateLayout = myFragmentView.findViewById(R.id.purDateLayout);
            purSellDateLay = myFragmentView.findViewById(R.id.purSellDateLay);
            lineAfterPurSellLay = myFragmentView.findViewById(R.id.lineAfterPurSellLay);
            sellPriceTitle = myFragmentView.findViewById(R.id.sellPriceTitle);
            sellValueTitle = myFragmentView.findViewById(R.id.sellValueTitle);

            if (isFromOpen) {
                new GetTaskLongShort(isin).execute();
            } else {
                closedRd.setChecked(true);
                new GetClosedPortfolioTask().execute();
            }
            //new GetTaskLongShort(isin).execute();

            initHoldingRadioGroup();
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
        }
        return myFragmentView;
    }


    private void initHoldingRadioGroup() {
        holdingRG.setOnCheckedChangeListener(null);
        holdingRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try {
                    openClosedChanged();
                } catch (Exception ex) {
                    GlobalClass.onError("", ex);
                }
            }
        });
    }


    private void openClosedChanged() throws Exception {
        main.removeAllViews();
        headerLayout.removeAllViews();

        if (openRd.isChecked()) {
            if (shortLongTermDetail != null) {
                addHoldingShorLongDetail();
            } else {
                new GetTaskLongShort(isin).execute();
            }
        } else {
            if (closedHoldingData != null) {
                if (closedHoldingData != null) {
                    addClosedHoldingDetail();
                }
            } else {
                new GetClosedPortfolioTask().execute();
            }
        }
    }


    private double lastRate = 0;
    private View lsView;

    @SuppressLint({"SetTextI18n"})
    @SuppressWarnings("ConstantConditions")
    void addHoldingShorLongDetail() {//(String companyName, int scripCode) {
        try {
            main.removeAllViews();
            headerLayout.removeAllViews();
            LayoutInflater li = homeActivity.getLayoutInflater();
            View root_main = li.inflate(R.layout.long_term_short_term_latest_layout, null);
            View view = li.inflate(R.layout.long_short_term_title_layout2, null);
            headerLayout.addView(view);

            ((ImageView) view.findViewById(R.id.img_grid)).setVisibility(View.GONE);

            CardView cardView = new CardView(getContext());

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    230
            );
            cardParams.setMargins(20, 10, 20, 30);
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(25f);
            cardView.setCardElevation(8f);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.dark_183d3d));

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setBackgroundColor(getResources().getColor(R.color.dark_183d3d));

            TextView titleTotGainLoss = new TextView(getContext());
            titleTotGainLoss.setText("Overall Returns");
            titleTotGainLoss.setTextColor(getResources().getColor(R.color.white));
            titleTotGainLoss.setTextSize(15f);
            titleTotGainLoss.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            TextView totGainLoss = new TextView(getContext());
            totGainLoss.setTextSize(25f);
            totGainLoss.setTypeface(null, Typeface.BOLD);
            totGainLoss.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            linearLayout.addView(titleTotGainLoss);
            linearLayout.addView(totGainLoss);
            cardView.addView(linearLayout);

            main.addView(cardView);

            main.addView(root_main);

            view.findViewById(R.id.button_valution).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                        groupsTokenDetails.scripCode.setValue(scripCode);
                        groupsTokenDetails.scripName.setValue(companyName);
                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                        grplist.add(groupsTokenDetails);
                        Fragment m_fragment = new ValuetionFragment(scripCode, grplist, homeActivity.SELECTED_RADIO_BTN);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
                    } catch (Exception ex) {
                        VenturaException.Print(ex);
                    }
                }
            });
            view.findViewById(R.id.button_viewchart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Fragment m_fragment = GraphFragment.newInstance(scripCode, companyName);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "");
                    } catch (Exception ex) {
                        VenturaException.Print(ex);
                    }
                }
            });
            view.findViewById(R.id.button_latestresult).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                        groupsTokenDetails.scripCode.setValue(scripCode);
                        groupsTokenDetails.scripName.setValue(companyName);
                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                        grplist.add(groupsTokenDetails);
                        Fragment m_fragment = new LatestResultFragment(scripCode, grplist, homeActivity.SELECTED_RADIO_BTN);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
                    } catch (Exception ex) {
                        VenturaException.Print(ex);
                    }
                }
            });

            lsView = root_main.findViewById(R.id.lsView);

            myFragmentView.findViewById(R.id.rl_data_layout).setVisibility(View.VISIBLE);

            LinearLayout total_layout_LT = new LinearLayout(getContext());
            total_layout_LT.setOrientation(LinearLayout.VERTICAL);

            LinearLayout total_layout_ST = new LinearLayout(getContext());
            total_layout_ST.setOrientation(LinearLayout.VERTICAL);

            StructHoldingShortLongTermRow[] shortLongRows = shortLongTermDetail.getHoldingShortLongTermRows();
            FontTextView textView = ((FontTextView) view.findViewById(R.id.txt_comp_name));
            textView.setText(companyName);
            StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode, false);
            lastRate = mktWatch.getLastRate();

            boolean isLTGainLossAdded = false;
            double overallreturn = 0;
            double stGL = 0;
            double ltGL = 0;

            for (int i = 0; i < shortLongTermDetail.getNoOfRow(); i++) {
                shortLongRows[i].setLastRate(lastRate);

                overallreturn += shortLongRows[i].getGainLoss();

                View root = li.inflate(R.layout.long_short_term_data_layout2, null);
                int backColor = (i % 2 == 0) ? ScreenColor.iTableRowOneBackColor : ScreenColor.iTableRowTwoBackColor;
//                root.setBackgroundColor(backColor);

                ((FontTextView) root.findViewById(R.id.pQtyPrice)).setTypeface(verdanaType);
                ((FontTextView) root.findViewById(R.id.txt_current_value)).setTypeface(verdanaType);
                ((FontTextView) root.findViewById(R.id.sell_purchase_date)).setTypeface(verdanaType);
                ((TextView) root.findViewById(R.id.selldateTitle)).setText("Purchase: ");
                ((TextView) root.findViewById(R.id.sellTitle)).setText("Current: ");
                ((FontTextView) root.findViewById(R.id.txt_gainloss)).setTypeface(verdanaType);

                ((FontTextView) root.findViewById(R.id.pQtyPrice)).setText(shortLongRows[i].getQtyStr() + " @ ₹" + Formatter.toTwoDecimalValue(shortLongRows[i].getAvgPurchasePrice()));
                ((FontTextView) root.findViewById(R.id.txt_current_value)).setText("₹" + Formatter.toTwoDecimalValue(shortLongRows[i].getCMP()));
                ((FontTextView) root.findViewById(R.id.sell_purchase_date)).setText(shortLongRows[i].getPurchaseDate());
                ((FontTextView) root.findViewById(R.id.txt_gainloss)).setText(Formatter.convertDoubleToValue_LacCr(shortLongRows[i].getGainLoss()));

                if (shortLongRows[i].getGainLoss() < 0) {
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setTextColor(ScreenColor.negativeColor);
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setText("-₹" + ((FontTextView) root.findViewById(R.id.txt_gainloss)).getText().toString().substring(1));
                    ((View) root.findViewById(R.id.longSortTermIndicator)).setBackgroundColor(ScreenColor.negativeColor);
                } else {
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setTextColor(ScreenColor.positiveColor);
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setText("+₹" + ((FontTextView) root.findViewById(R.id.txt_gainloss)).getText());
                    ((View) root.findViewById(R.id.longSortTermIndicator)).setBackgroundColor(ScreenColor.positiveColor);
                }

                if ((shortLongRows[i].getLongOrShort() == 'L' || shortLongRows[i].getLongOrShort() == 'l')) {
                    ltGL += shortLongRows[i].getGainLoss();
                    ((LinearLayout) root_main.findViewById(R.id.data_layout_long_term)).addView(root);

                } else if ((shortLongRows[i].getLongOrShort() == 'S' || shortLongRows[i].getLongOrShort() == 's')) {
                    stGL += shortLongRows[i].getGainLoss();
                    ((LinearLayout) root_main.findViewById(R.id.data_layout_short_term)).addView(root);
                }

                int finalI = i;
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qty.setText(shortLongRows[finalI].getQtyStr());
                        purPrice.setText("₹" + Formatter.toTwoDecimalValue(shortLongRows[finalI].getAvgPurchasePrice()));
                        sellprice.setText("₹" + Formatter.toTwoDecimalValue(shortLongRows[finalI].getCMP()));
                        purVal.setText(Formatter.convertDoubleToValue_LacCr(shortLongRows[finalI].getPurchaseValue()));
                        sellVal.setText(Formatter.convertDoubleToValue_LacCr(shortLongRows[finalI].getCurrentValue()));
                        purDateLayout.setVisibility(View.VISIBLE);
                        purSellDateLay.setVisibility(View.GONE);
                        lineAfterPurSellLay.setVisibility(View.GONE);
                        purDate2.setText(shortLongRows[finalI].getPurchaseDate());
                        sellPriceTitle.setText("Current price");
                        sellValueTitle.setText("Current value");

                        if (shortLongRows[finalI].getGainLoss() < 0) {
                            gainLoss.setTextColor(ScreenColor.negativeColor);
                            gainLoss.setText("-₹" + Formatter.convertDoubleToValue_LacCr(shortLongRows[finalI].getGainLoss()).substring(1));
                        } else {
                            gainLoss.setTextColor(ScreenColor.positiveColor);
                            gainLoss.setText("+₹" + Formatter.convertDoubleToValue_LacCr(shortLongRows[finalI].getGainLoss()));
                        }

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
            }

            if(stGL<0){
                ((TextView) root_main.findViewById(R.id.shorttermtext)).setText("Short Term (Gain/Loss: " +
                        "-₹"+Formatter.convertDoubleToValue_LacCr(stGL).substring(1)
                        + ")");
            }else{
                ((TextView) root_main.findViewById(R.id.shorttermtext)).setText("Short Term (Gain/Loss: " +
                        "+₹"+Formatter.convertDoubleToValue_LacCr(stGL)
                        + ")");
            }

            if(ltGL<0){
                ((TextView) root_main.findViewById(R.id.longtermtext)).setText("Long Term (Gain/Loss: " +
                        "-₹"+Formatter.convertDoubleToValue_LacCr(ltGL).substring(1)
                        + ")");
            }else{
                ((TextView) root_main.findViewById(R.id.longtermtext)).setText("Long Term (Gain/Loss: " +
                        "+₹"+Formatter.convertDoubleToValue_LacCr(ltGL)
                        + ")");
            }

            if (overallreturn < 0) {
                totGainLoss.setTextColor(ScreenColor.negativeColor);
                totGainLoss.setText("-₹"+Formatter.convertDoubleToValue_LacCr(overallreturn).substring(1));
            } else {
                totGainLoss.setTextColor(ScreenColor.positiveColor);
                totGainLoss.setText("₹"+Formatter.convertDoubleToValue_LacCr(overallreturn));
            }

            if (((LinearLayout) root_main.findViewById(R.id.data_layout_short_term)).getChildCount() == 0) {
                ((LinearLayout) root_main.findViewById(R.id.layout_short_term)).setVisibility(View.GONE);
            }
            if (((LinearLayout) root_main.findViewById(R.id.data_layout_long_term)).getChildCount() == 0) {
                ((LinearLayout) root_main.findViewById(R.id.layout_long_term)).setVisibility(View.GONE);
            } else {
                ((LinearLayout) root_main.findViewById(R.id.title_short_term)).setVisibility(View.GONE);
            }
//            if (((LinearLayout) root_main.findViewById(R.id.layout_long_term)).getVisibility() == View.VISIBLE &&
//                    ((LinearLayout) root_main.findViewById(R.id.layout_short_term)).getVisibility() == View.VISIBLE) {
//                lsView.setVisibility(View.VISIBLE);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    private class GetTaskLongShort extends AsyncTask<Object, Void, String> {
        ProgressDialog mDialog;
        String isin = "";

        GetTaskLongShort(String _isin) {
            isin = _isin;
        }

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
                VenturaServerConnect.closeSocket();
                VenturaServerConnect.connectToWealthServer(false);
                shortLongTermDetail = VenturaServerConnect.getHoldingShortLongTermDetail(isin, (byte) eScreen.MKTDEPTH.value, clientCode);
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
                if (shortLongTermDetail != null && shortLongTermDetail.getNoOfRow() > 0) {
                    addHoldingShorLongDetail();
                    //new GetClosedPortfolioTask().execute();
                } else {
                    GlobalClass.showToast(getContext(), "No data found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class LongShortHandler extends Handler {
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
                                int _scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                                if (scripCode == _scripCode) {
                                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode, false);
                                    if ((mktWatch.getLastRate() != lastRate) && openRd.isChecked()) {
                                        addHoldingShorLongDetail();
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

        GetClosedPortfolioTask() {
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
                JSONArray respData = portFolio.getClosedPortFolio(clientCode, isin);
                if (respData != null) {
                    closedHoldingData = new ClosedHoldingData(clientCode);
                    closedHoldingData.setDataScripWise(respData);
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
                    //addClosedHoldingDetail();
                } else {
                    GlobalClass.showToast(getContext(), "No data found");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void addClosedHoldingDetail() {
        try {
            //TODO 2
            main.removeAllViews();
            headerLayout.removeAllViews();
            LayoutInflater li = homeActivity.getLayoutInflater();
            View root_main = li.inflate(R.layout.long_term_short_term_latest_layout, null);
            View view = li.inflate(R.layout.long_short_term_title_layout2, null);
            //main.addView(view);

            headerLayout.addView(view);
//            ((LinearLayout) view.findViewById(R.id.forList)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.img_grid)).setVisibility(View.GONE);

//            ((TextView) view.findViewById(R.id.txt_cmp)).setText("SellPrice");
//            ((TextView) view.findViewById(R.id.txt_current_value)).setText("SellVal");
//            ((TextView) view.findViewById(R.id.selldate)).setVisibility(View.VISIBLE);
//            ((TextView) view.findViewById(R.id.txt_gainloss)).setGravity(Gravity.END);

            main.addView(root_main);

            view.findViewById(R.id.button_valution).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                        groupsTokenDetails.scripCode.setValue(scripCode);
                        groupsTokenDetails.scripName.setValue(companyName);
                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                        grplist.add(groupsTokenDetails);
                        Fragment m_fragment = new ValuetionFragment(scripCode, grplist, homeActivity.SELECTED_RADIO_BTN);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.VALUATION.name);
                    } catch (Exception ex) {
                        VenturaException.Print(ex);
                    }
                }
            });
            view.findViewById(R.id.button_viewchart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Fragment m_fragment = GraphFragment.newInstance(scripCode, companyName);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "");
                    } catch (Exception ex) {
                        VenturaException.Print(ex);
                    }
                }
            });
            view.findViewById(R.id.button_latestresult).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                        groupsTokenDetails.scripCode.setValue(scripCode);
                        groupsTokenDetails.scripName.setValue(companyName);
                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                        grplist.add(groupsTokenDetails);
                        Fragment m_fragment = new LatestResultFragment(scripCode, grplist, homeActivity.SELECTED_RADIO_BTN);
                        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, eFragments.LATEST_RESULT.name);
                    } catch (Exception ex) {
                        VenturaException.Print(ex);
                    }
                }
            });

            lsView = root_main.findViewById(R.id.lsView);

            myFragmentView.findViewById(R.id.rl_data_layout).setVisibility(View.VISIBLE);

            ArrayList<ClosedHoldingDetails> closedHoldingDetailsList = closedHoldingData.getAllData();
            FontTextView textView = ((FontTextView) view.findViewById(R.id.txt_comp_name));
            textView.setText(companyName);

            View prevCurrLayout = li.inflate(R.layout.previous_current_year_layout, null);
            ImageView prevYDropDown = prevCurrLayout.findViewById(R.id.prevYDropDown);
            ImageView currYDropDown = prevCurrLayout.findViewById(R.id.currYDropDown);

            ((TextView) prevCurrLayout.findViewById(R.id.ovAllReturns)).setText("₹" + Formatter.convertDoubleToValue_LacCr(closedHoldingData.totalGainLoss));
            if (closedHoldingData.totalGainLoss < 0) {
                ((TextView) prevCurrLayout.findViewById(R.id.ovAllReturns)).setTextColor(ScreenColor.negativeColor);
            } else {
                ((TextView) prevCurrLayout.findViewById(R.id.ovAllReturns)).setTextColor(ScreenColor.positiveColor);
            }

            double currTotalPurVal = 0;
            double currTotalSellVal = 0;
            double currTotalGainLoss = 0;

            double prevTotalPurVal = 0;
            double prevTotalSellVal = 0;
            double prevTotalGainLoss = 0;

            for (int i = 0; i < closedHoldingDetailsList.size(); i++) {

                ClosedHoldingDetails closedHoldingDetails = closedHoldingDetailsList.get(i);

                View root = li.inflate(R.layout.long_short_term_data_layout2, null);

                ((FontTextView) root.findViewById(R.id.txt_current_value)).setTypeface(verdanaType);
                ((FontTextView) root.findViewById(R.id.sell_purchase_date)).setTypeface(verdanaType);
                ((FontTextView) root.findViewById(R.id.pQtyPrice)).setTypeface(verdanaType);
                ((FontTextView) root.findViewById(R.id.txt_gainloss)).setTypeface(verdanaType);

                ((FontTextView) root.findViewById(R.id.txt_current_value)).setText("₹" + String.format("%.2f", closedHoldingDetails.SellPrice));
                ((FontTextView) root.findViewById(R.id.sell_purchase_date)).setText(closedHoldingDetails.SellDate);
                ((FontTextView) root.findViewById(R.id.pQtyPrice)).setText(closedHoldingDetails.Qty + " @ ₹" + String.format("%.2f", closedHoldingDetails.PurPrice));
                ((FontTextView) root.findViewById(R.id.txt_gainloss)).setText(Formatter.convertDoubleToValue_LacCr(closedHoldingDetails.GainLoss));

                if (closedHoldingDetails.GainLoss < 0) {
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setTextColor(ScreenColor.negativeColor);
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setText("-₹" + ((FontTextView) root.findViewById(R.id.txt_gainloss)).getText().toString().substring(1));
                    ((View) root.findViewById(R.id.longSortTermIndicator)).setBackgroundColor(ScreenColor.negativeColor);
                } else {
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setTextColor(ScreenColor.positiveColor);
                    ((FontTextView) root.findViewById(R.id.txt_gainloss)).setText("+₹" + ((FontTextView) root.findViewById(R.id.txt_gainloss)).getText());
                    ((View) root.findViewById(R.id.longSortTermIndicator)).setBackgroundColor(ScreenColor.positiveColor);
                }

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        purDate.setText(closedHoldingDetails.PurDate);
                        qty.setText(closedHoldingDetails.Qty + "");
                        purPrice.setText("₹" + decimalComaFormat.format(closedHoldingDetails.PurPrice));
                        sellprice.setText("₹" + decimalComaFormat.format(closedHoldingDetails.SellPrice));
                        purVal.setText(Formatter.convertDoubleToValue_LacCr(closedHoldingDetails.PurVal));
                        sellDate.setText(closedHoldingDetails.SellDate);
                        sellVal.setText(Formatter.convertDoubleToValue_LacCr(closedHoldingDetails.SellVal));

                        purDateLayout.setVisibility(View.GONE);
                        purSellDateLay.setVisibility(View.VISIBLE);
                        lineAfterPurSellLay.setVisibility(View.VISIBLE);

                        if (closedHoldingDetails.GainLoss < 0) {
                            gainLoss.setTextColor(ScreenColor.negativeColor);
                            gainLoss.setText("-₹" + Formatter.convertDoubleToValue_LacCr(closedHoldingDetails.GainLoss).substring(1));
                        } else {
                            gainLoss.setTextColor(ScreenColor.positiveColor);
                            gainLoss.setText("+₹" + Formatter.convertDoubleToValue_LacCr(closedHoldingDetails.GainLoss));
                        }

                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });

                if (isCurrentYear(closedHoldingDetails.SellDate)) {
                    Log.d(TAG, "current year : " + closedHoldingDetails.SellDate);
                    ((LinearLayout) prevCurrLayout.findViewById(R.id.currYearDataLay)).addView(root);
                    currTotalPurVal += closedHoldingDetails.PurVal;
                    currTotalSellVal += closedHoldingDetails.SellVal;
                    currTotalGainLoss += closedHoldingDetails.GainLoss;
                } else {
                    Log.d(TAG, "prev year : " + closedHoldingDetails.SellDate);
                    ((LinearLayout) prevCurrLayout.findViewById(R.id.prevYearDataLay)).addView(root);
                    prevTotalPurVal += closedHoldingDetails.PurVal;
                    prevTotalSellVal += closedHoldingDetails.SellVal;
                    prevTotalGainLoss += closedHoldingDetails.GainLoss;
                }
            }

            if (((LinearLayout) prevCurrLayout.findViewById(R.id.prevYearDataLay)).getChildCount() < 1) {
                prevCurrLayout.findViewById(R.id.prevYearLay).setVisibility(View.GONE);
            } else prevCurrLayout.findViewById(R.id.prevYearLay).setVisibility(View.VISIBLE);

            if (((LinearLayout) prevCurrLayout.findViewById(R.id.currYearDataLay)).getChildCount() < 1) {
                prevCurrLayout.findViewById(R.id.currYearLay).setVisibility(View.GONE);
                prevCurrLayout.findViewById(R.id.prevYearDataLay).setVisibility(View.VISIBLE);
                prevYDropDown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
            } else prevCurrLayout.findViewById(R.id.currYearLay).setVisibility(View.VISIBLE);


            if ((((LinearLayout) prevCurrLayout.findViewById(R.id.currYearDataLay)).getChildCount()) > 1) {
                TextView pcl = prevCurrLayout.findViewById(R.id.currTotGLTview);
                pcl.setText(Formatter.convertDoubleToValue_LacCr(currTotalGainLoss) + ")");
            }

            if ((((LinearLayout) prevCurrLayout.findViewById(R.id.prevYearDataLay)).getChildCount()) > 1) {
                TextView ccl = prevCurrLayout.findViewById(R.id.prevTotGLTview);
                ccl.setText(Formatter.convertDoubleToValue_LacCr(prevTotalGainLoss) + ")");
            }

            RelativeLayout prevYTitleLay = prevCurrLayout.findViewById(R.id.prevYTitleLay);
            prevYTitleLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prevCurrLayout.findViewById(R.id.prevYearDataLay).getVisibility() == View.VISIBLE) {
                        prevCurrLayout.findViewById(R.id.prevYearDataLay).setVisibility(View.GONE);
                        prevYDropDown.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                    } else {
                        prevCurrLayout.findViewById(R.id.prevYearDataLay).setVisibility(View.VISIBLE);
                        prevYDropDown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                    }
                }
            });

            RelativeLayout currYTitleLay = prevCurrLayout.findViewById(R.id.currYTitleLay);
            currYTitleLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prevCurrLayout.findViewById(R.id.currYearDataLay).getVisibility() == View.VISIBLE) {
                        prevCurrLayout.findViewById(R.id.currYearDataLay).setVisibility(View.GONE);
                        currYDropDown.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                    } else {
                        prevCurrLayout.findViewById(R.id.currYearDataLay).setVisibility(View.VISIBLE);
                        currYDropDown.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                    }
                }
            });

            ((LinearLayout) root_main.findViewById(R.id.data_layout_long_term)).addView(prevCurrLayout);

            ((LinearLayout) root_main.findViewById(R.id.layout_short_term)).setVisibility(View.GONE);
            ((LinearLayout) root_main.findViewById(R.id.title_short_term)).setVisibility(View.GONE);

            if (((LinearLayout) root_main.findViewById(R.id.layout_long_term)).getVisibility() == View.VISIBLE &&
                    ((LinearLayout) root_main.findViewById(R.id.layout_short_term)).getVisibility() == View.VISIBLE) {
                lsView.setVisibility(View.VISIBLE);
            }
            (root_main.findViewById(R.id.longtermtext)).setVisibility(View.GONE);
            (root_main.findViewById(R.id.longtermdivider)).setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentYear(String dateString) {
        dateString = dateString.toLowerCase();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String currentYearDate = "01-apr-" + currentYear;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            Date dateObject1 = sdf.parse(dateString);
            Date dateObject2 = sdf.parse(currentYearDate);

            assert dateObject1 != null;
            int result = dateObject1.compareTo(dateObject2);

            return result >= 0;
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.log("isCurrentYear method error: ", e.toString());
            Toast.makeText(homeActivity, "Internal error", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public String getYear(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        try {
            Date date = inputFormat.parse(dateString);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            assert date != null;
            calendar.setTime(date);
            return calendar.get(java.util.Calendar.YEAR) + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}