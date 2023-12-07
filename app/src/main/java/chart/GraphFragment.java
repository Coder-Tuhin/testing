package chart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leadingbyte.stockchart.Area;
import com.leadingbyte.stockchart.AreaList;
import com.leadingbyte.stockchart.Axis;
import com.leadingbyte.stockchart.AxisRange;
import com.leadingbyte.stockchart.Crosshair;
import com.leadingbyte.stockchart.OutlineStyle;
import com.leadingbyte.stockchart.Plot;
import com.leadingbyte.stockchart.Series;
import com.leadingbyte.stockchart.Side;
import com.leadingbyte.stockchart.points.AbstractPoint;
import com.leadingbyte.stockchart.points.Point1;
import com.leadingbyte.stockchart.points.Point2;
import com.leadingbyte.stockchart.points.StockPoint;
import com.leadingbyte.stockchart.renderers.BarStockRenderer;
import com.leadingbyte.stockchart.renderers.CandlestickStockRenderer;
import com.leadingbyte.stockchart.renderers.FastLinearRenderer;
import com.leadingbyte.stockchart.renderers.HistogramRenderer;
import com.leadingbyte.stockchart.renderers.LinearRenderer;
import com.leadingbyte.stockchart.utils.PointD;
import com.leadingbyte.stockchart.utils.StockDataGenerator;
import com.leadingbyte.stockchartpro.StockChartScrollerView;
import com.leadingbyte.stockchartpro.StockChartViewPro;
import com.leadingbyte.stockchartpro.indicators.AbstractIndicator;
import com.leadingbyte.stockchartpro.indicators.AdxIndicator;
import com.leadingbyte.stockchartpro.indicators.AtrIndicator;
import com.leadingbyte.stockchartpro.indicators.BollingerBandsIndicator;
import com.leadingbyte.stockchartpro.indicators.EmaIndicator;
import com.leadingbyte.stockchartpro.indicators.EnvelopesIndicator;
import com.leadingbyte.stockchartpro.indicators.IndicatorManager;
import com.leadingbyte.stockchartpro.indicators.MacdIndicator;
import com.leadingbyte.stockchartpro.indicators.RsiIndicator;
import com.leadingbyte.stockchartpro.indicators.SmaIndicator;
import com.leadingbyte.stockchartpro.indicators.StochasticIndicator;
import com.leadingbyte.stockchartpro.misc.RoundNumbersScaleValuesProvider;
import com.leadingbyte.stockchartpro.stickers.AbstractSticker;
import com.leadingbyte.stockchartpro.stickers.FibonacciArcsSticker;
import com.leadingbyte.stockchartpro.stickers.FibonacciFansSticker;
import com.leadingbyte.stockchartpro.stickers.FibonacciRetracementSticker;
import com.leadingbyte.stockchartpro.stickers.SimpleGuideSticker;
import com.leadingbyte.stockchartpro.stickers.SpeedLinesSticker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Structure.Request.BC.StructChartReq;
import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.BC.StructMobTick_Data;

import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import connection.ReqSent;
import connection.SendDataToBCServer;
import enums.ChartTools;
import enums.ChartType;
import enums.Indicator;
import enums.Minute;
import enums.eDailyChart;
import enums.eExch;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import handler.ChartDataProcess;
import interfaces.OnAlertListener;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import view.help.ChartHelp;

import static utils.Constants.ARG_PARAM1;
import static utils.Constants.ARG_PARAM2;

@SuppressLint("ValidFragment")
public class GraphFragment extends Fragment implements OnAlertListener, View.OnClickListener,
        ReqSent, CheckBox.OnCheckedChangeListener, AdapterView.OnItemClickListener {

    public static Handler uiHandler;
    private final String className = getClass().getName();
    private final PointD fTouchPoint = new PointD();

    private StructMobTick_Data lastTick;
    private StructMobTick_Data currTick;

    private Toast toast;
    private ListView studiesListView, toolsListView;
    private RelativeLayout toolsLayout;
    private LinearLayout topBarLayout;
    private AlertDialog editPopupAlert;
    private Dialog showMenuDialog;
    private CountDownTimer menuTimer;
    private Minute selectedMin;
    private eDailyChart selectedDailyChart;

    private List<StockDataGenerator.Point> dataPoints = new ArrayList<>();
    private HashMap<Indicator, AbstractIndicator> indicatorMap = new HashMap<>();
    private HashMap<Indicator, IndicatorSettingModel> indicatorSettingMap = new HashMap<>();
    public static Handler graphHandler;
    private ChartDataHolder chartDataHolder;

    private String allType[] = {"1 DAY", "7 DAY", "DAILY", "MONTHLY", "3 MONTHS", "6 MONTHS", "YEARLY", "TradingView (beta version)"};
    private String fnoType[] = {"1 DAY", "7 DAY"};

    private HomeActivity homeActivity;
    private String _scripName = "";
    private int _scripCode = 0;
    private boolean isIndices = false;
    private NumberFormat formatter;// = Formatter.getFormatter(smw.segment.getValue());

    private boolean isChartLoaded = false;
    private ChartSettingsModel chartSettingsModel;

    //today updated 24May21
    //private StructMobTick_Data currTick;
    public static GraphFragment newInstance(int _scripCode, String _scripName) {
        GraphFragment fragment = new GraphFragment();
        try {
            Bundle args = new Bundle();
            args.putInt(ARG_PARAM1, _scripCode);
            args.putString(ARG_PARAM2, _scripName);
            fragment.setArguments(args);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        storeChartSettings();
    }

    @BindView(R.id.ohclLinear)
    LinearLayout ohclLinear;
    @BindView(R.id.scripName)
    TextView scripName;
    @BindView(R.id.open)
    TextView txtOpen;
    @BindView(R.id.high)
    TextView txtHigh;
    @BindView(R.id.low)
    TextView txtLow;
    @BindView(R.id.close)
    TextView txtClose;
    @BindView(R.id.qty)
    TextView txtVolume;
    @BindView(R.id.ltp)
    TextView txtLtp;
    @BindView(R.id.absChg)
    TextView txtAbsChg;
    @BindView(R.id.perChg)
    TextView txtPerChg;
    @BindView(R.id.minSel)
    TextView minSel;
    @BindView(R.id.graphspinner)
    Spinner graphSpinner;
    @BindView(R.id.crossImage)
    ImageView crossImage;
    @BindView(R.id.stockChartView)
    StockChartViewPro chart;
    @BindView(R.id.stockChartScrollerView)
    StockChartScrollerView fScroller;
    @BindView(R.id.openLayout)
    LinearLayout openLayout;
    @BindView(R.id.highLayout)
    LinearLayout highLayout;
    @BindView(R.id.lowLayout)
    LinearLayout lowLayout;
    @BindView(R.id.closeLayout)
    LinearLayout closeLayout;
    @BindView(R.id.volumeLayout)
    LinearLayout volumeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.graph_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        chartSettingsModel = VenturaApplication.getPreference().getChartSettings();
        view.findViewById(R.id.crossHair).setOnClickListener(this);
        view.findViewById(R.id.graphSetting).setOnClickListener(this);
        initValues();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());
    }

    private void initValues() {
        homeActivity.findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        final Bundle arguments = getArguments();
        _scripCode = arguments.getInt(ARG_PARAM1);
        _scripName = arguments.getString(ARG_PARAM2);
        selectedDailyChart = eDailyChart.MAX;
        currTick = null;
        isIndices = GlobalClass.indexScripCodeOrNot(_scripCode);
        if (PreferenceHandler.getChartHelp()) {
            Dialog chartHelp = new ChartHelp(false);
            chartHelp.show();
        }
        scripName.setText(_scripName);
        if (GlobalClass.chartDataProcess != null) {
            GlobalClass.chartDataProcess.clearData(_scripCode, eMessageCode.EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA.value);
        }
        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(_scripCode, true);
        /*
        if (isIndices) {
            StructIndex index = GlobalClass.mktDataHandler
                    .getIndicesData(Indices.getIndices(_scripCode).indexCode);
            mktWatch = getMktStruct(index);
        } else {
            mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(_scripCode,true);
        }*/
        if (mktWatch != null) {
            formatter = mktWatch.getFormatter();
        }
        setMktRates(mktWatch);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(GlobalClass.latestContext, R.layout.custom_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_drop_down);

        if (mktWatch.getSegment() == eExch.FNO.value
                || mktWatch.getSegment() == eExch.NSECURR.value) {
            spinnerAdapter.addAll(Arrays.asList(fnoType));
        } else {
            spinnerAdapter.addAll(Arrays.asList(allType));
        }
        graphSpinner.setAdapter(spinnerAdapter);
        graphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == allType.length - 1) {
                    Intent intent = new Intent(getContext(),TradingViewChart.class);
                    intent.putExtra("SCRIPNAME", _scripName);
                    intent.putExtra("SCRIPCODE", _scripCode);
                    intent.putExtra("EXCHANGE", getExchangeForTradingView(_scripName));
                    startActivity(intent);
                    graphSpinner.setSelection(0);
                } else {
                    if (chart != null) chart.reset();
                    initChart();
                    GlobalClass.showProgressDialog("Loading...");
                    selectedMin = Minute.M_DEF;
                    chartDataHolder = new ChartDataHolder(_scripCode);
                    graphHandler = new GraphHandler();
                    sendChartReq(position);
                    chartSettingsModel.dropDownSelectionPOsition = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        graphSpinner.setSelection(graphSpinner.getAdapter().getCount() > chartSettingsModel.dropDownSelectionPOsition ? chartSettingsModel.dropDownSelectionPOsition : 0);
    }

    public String getExchangeForTradingView(String scripName){
        String []parts = scripName.split("-");
        if(parts[0].equals("NE")){
            return "N";
        }
        else if(parts[0].equals("BE")){
            return "B";
        }
        else{
            return "C";
        }
    }

    private void setMktRates(StaticLiteMktWatch mkt) {
        try {
            if (mkt != null) {
                double prevVal = GlobalClass.mktDataHandler.getPrevMkt5001Data(_scripCode).lastRate.getValue();
                mkt.setLtpWithTxtColor(txtLtp, prevVal);
                mkt.setChgWithColor(txtPerChg, txtAbsChg, true);
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    protected void initChart() {
        try {
            Area priceArea = chart.addArea();
            priceArea.setName(ChartType.PRICE.name);
            if (graphSpinner.getSelectedItemPosition() == 1 ||
                    graphSpinner.getSelectedItemPosition() == 2) {
                priceArea.setAxesVisible(false, true, true, false);
            } else {
                priceArea.setAxesVisible(false, false, true, false);
            }

            priceArea.setAutoHeight(true);
            priceArea.getRightAxis().setScaleValuesProvider(new RoundNumbersScaleValuesProvider(2));
            priceArea.getRightAxis().getAxisRange().setMargin(0.1, false);

            Area volumeArea = chart.addArea();
            volumeArea.setName(ChartType.VOLUME.name);
            volumeArea.setAxesVisible(false, false, true, true);
            volumeArea.setAutoHeight(false);

            Series fPriceSeries = chart.addSeries(priceArea);
            fPriceSeries.setName(ChartType.PRICE.name);
            fPriceSeries.setYAxisSide(Side.RIGHT);

            fPriceSeries.setRenderer(getCandlestickStockRenderer());

            Series fVolumeSeries = chart.addSeries(volumeArea);
            fVolumeSeries.setName(ChartType.VOLUME.name);
            fVolumeSeries.setYAxisSide(Side.RIGHT);
            fVolumeSeries.setRenderer(new HistogramRenderer());

            int VolColor = Color.rgb(110, 209, 255);
            fVolumeSeries.getRenderer().getAppearance().setPrimaryFillColor(VolColor);
            fVolumeSeries.getRenderer().getAppearance().setSecondaryFillColor(VolColor);
            fVolumeSeries.getRenderer().getAppearance().setOutlineColor(VolColor);


            Series fAreaSeries = chart.addSeries(priceArea);
            fAreaSeries.setName(ChartType.AREA.name);
            fAreaSeries.setYAxisSide(Side.RIGHT);
            fAreaSeries.setRenderer(new LinearRenderer());

            Series fLinearSeries = chart.addSeries(priceArea);
            fLinearSeries.setName(ChartType.LINEAR.name);
            fLinearSeries.setYAxisSide(Side.RIGHT);
            fLinearSeries.setRenderer(new FastLinearRenderer());

            Series fFastLinearSeries = chart.addSeries(priceArea);
            fFastLinearSeries.setName(ChartType.SCROLL.name);
            fFastLinearSeries.setVisible(false);
            fFastLinearSeries.setRenderer(new FastLinearRenderer());

            fScroller.setup(chart, fFastLinearSeries.getName());
            setVolumeChart(chartSettingsModel.isChartVolume);


            enableDisableScroller(chartSettingsModel.isScoller);

            AxisRange ar = new AxisRange();
            ar.setZoomable(true);

            ar.setMovable(true);
            ar.setMaxMinViewLength(Double.NaN, 3);


            chart.enableGlobalAxisRange(Side.BOTTOM, ar);
            chart.enableGlobalAxisRange(Side.TOP, ar);


            Axis.ILabelFormatProvider bottomLfp = new Axis.ILabelFormatProvider() {

                public String getAxisLabel(Axis sender, double value) {
                    try {
                        AbstractPoint p = chart.getSeries().get(0).getPointAtValue(value);
                        if (null != p) {
                            Date date = (Date) p.getID();
                            return getTime(date);
                            //date.getHours() + ":" + twoDigitVal(date.getMinutes());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                }

            };

            Axis vBottomAxis = volumeArea.getBottomAxis(),
                    pTopAxis = priceArea.getTopAxis(),
                    pRightAxis = priceArea.getRightAxis(),
                    vRightAxis = volumeArea.getRightAxis();

            vBottomAxis.setLabelFormatProvider(bottomLfp);


            if (graphSpinner.getSelectedItemPosition() == 1 ||
                    graphSpinner.getSelectedItemPosition() == 2) {
                Axis.ILabelFormatProvider top = new Axis.ILabelFormatProvider() {

                    public String getAxisLabel(Axis sender, double value) {
                        try {
                            AbstractPoint p = chart.getSeries().get(0).getPointAtValue(value);
                            if (null != p) {
                                Date date = (Date) p.getID();
                                return getMonth(date);
                                //date.getHours() + ":" + twoDigitVal(date.getMinutes());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return "";
                    }

                };
                pTopAxis.setLabelFormatProvider(top);
            }

            //chart.enableGlobalAxisRange(Axis.Side.RIGHT, ar);
            Axis.ILabelFormatProvider rightLfp = new Axis.ILabelFormatProvider() {

                public String getAxisLabel(Axis sender, double value) {
                    AbstractPoint p = chart.getSeries().get(1).getPointAtValue(value);
                    if (p != null) {
                        String doubleValue = Formatter.convertIntToValue((int) value);
                       /* int dvl = doubleValue.length();
                        if (dvl > rightAxisLabelWidth) {
                            setRightAxisWidth(dvl);
                            rightAxisLabelWidth = dvl;
                        }*/
                        return doubleValue;
                    }
                    return "";

                }

            };

            Axis.ILabelFormatProvider prightLfp = new Axis.ILabelFormatProvider() {
                public String getAxisLabel(Axis sender, double value) {
                    AbstractPoint p = chart.getSeries().get(0).getPointAtValue(value);
                    if (p != null && value >= 0) {
                        String doubleValue = Formatter.convertIntToValueForChart(value, 1);
                        return doubleValue;
                    }
                    return "";
                }
            };

            pRightAxis.setLabelFormatProvider(prightLfp);
            vRightAxis.setLabelFormatProvider(rightLfp);
            pRightAxis.setVisible(true);

            volumeArea.getTopAxis().setInvisibleSizeInDips(0);
            priceArea.getTopAxis().setInvisibleSizeInDips(0);

            pRightAxis.setAutoSize(true);
            vRightAxis.setAutoSize(true);
            pTopAxis.setAutoSize(false);
            vBottomAxis.setAutoSize(false);

            float fontSize = 8;//getResources().getDimension(R.dimen.graph_label_height);
            pRightAxis.getAppearance().getFont().setSizeInDips(fontSize);
            vRightAxis.getAppearance().getFont().setSizeInDips(fontSize);
            vBottomAxis.getAppearance().getFont().setSizeInDips(fontSize);
            pTopAxis.getAppearance().getFont().setSizeInDips(fontSize);
            vRightAxis.setLinesCount(4);
            crossHairSetup();

            chart.getGlobalAxisRange(Side.BOTTOM).setEventListener(new AxisRange.IEventListener() {
                @Override
                public void onMoveViewValues(AxisRange sender, double oldMaxValue,
                                             double oldMinValue, double newMaxValue, double newMinValue) {
                    //onValuesChanged(newMaxValue, newMinValue);
                    fScroller.invalidate();
                }

                @Override
                public void onZoomViewValues(AxisRange sender, double oldMaxValue,
                                             double oldMinValue, double newMaxValue, double newMinValue) {
                    fScroller.invalidate();
                }

                @Override
                public double[] onAutoValuesChanged(AxisRange sender,
                                                    double oldMaxValue, double oldMinValue, double newMaxValue,
                                                    double newMinValue) {
                    // TODO Auto-generated method stub
                    return null;
                }

            });

            fScroller.setEventListener(new StockChartScrollerView.IEventListener() {
                @Override
                public void onViewValuesChanged(double maxValue, double minValue) {
                    //onValuesChanged(maxValue,minValue);
                    GraphFragment.this.chart.invalidate();
                }
            });

            //Theme.setCurrentThemeFromResources(this, R.raw.gray);
            chart.setClickable(true);
            //removeGrid();
            setChartBackgroundColor(Color.BLACK);
            setAdapter();
            enableDisableCrosshair();
            setChart(chartSettingsModel.isChartLine, chartSettingsModel.isChartCandle, chartSettingsModel.isChartBar,
                    chartSettingsModel.isChartArea);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            uiHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOk(String tag) {
        GlobalClass.fragmentManager.popBackStackImmediate();
    }

    @Override
    public void onCancel(String tag) {
    }

    @Override
    public void reqSent(int msgCode) {
    }

    private final int PERMISSION_REQUEST_CODE = 2;

    @TargetApi(Build.VERSION_CODES.M)
    private void askDynamicPermission() {
        if (ContextCompat.checkSelfPermission(GlobalClass.latestContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    acceptedMeth();
                } else {
                    GlobalClass.showToast(GlobalClass.latestContext, "You have not accepted all permissions. Please clear cache or reinstall");
                }
            }
        }
    }

    private void acceptedMeth() {
        dissmissMenu();
        saveAsImage();
    }

    private class IndicatorSettingModel {
        public int pCount = 0;
        public double lowerCoeff = 0;
        public double upperCoef = 0;
        public double percent = 0;
        public int longMacdPeriod = 0;
        public int shortMacdPeriod = 0;
        public int signalMacdPeriod = 0;
        public int slowK = 0;
        public int slowD = 0;

        public IndicatorSettingModel() {
        }

        ;

        public IndicatorSettingModel(int pCount, double lowerCoeff, double upperCoef, double percent,
                                     int longMacdPeriod, int shortMacdPeriod, int signalMacdPeriod, int slowK, int slowD) {
            this.pCount = pCount;
            this.lowerCoeff = lowerCoeff;
            this.upperCoef = upperCoef;
            this.percent = percent;
            this.longMacdPeriod = longMacdPeriod;
            this.shortMacdPeriod = shortMacdPeriod;
            this.signalMacdPeriod = signalMacdPeriod;
            this.slowK = slowK;
            this.slowD = slowD;
        }
    }

    int incrimentMin = 1;

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.clearSticker:
                    dissmissMenu();
                    clearStickers();
                    break;
                case R.id.saveImg:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        askDynamicPermission();
                    } else {
                        acceptedMeth();
                    }
                    break;
                case R.id.showPoint:
                    dissmissMenu();
                    getChartDataPoint();
                    break;
                case R.id.showData:
                    dissmissMenu();
                    if (PreferenceHandler.getDisplayOnTap()) {
                        PreferenceHandler.setDisplayOnTap(false);
                    } else {
                        GlobalClass.showToast(GlobalClass.latestContext, "Showing touch point values");
                        PreferenceHandler.setDisplayOnTap(true);
                    }
                    break;
                case R.id.close:
                    closeEditpopup();
                    break;
                case R.id.btnEdit:
                    View convertView = (View) view.getTag();
                    Indicator ind = (Indicator) convertView.getTag();
                    openEditPopupWindow(ind);
                    break;
                case R.id.btnApply:
                    closeEditpopup();
                    dissmissMenu();
                    View conView = (View) view.getTag();
                    int pCount = (int) getValue((EditText) conView.findViewById(R.id.editPeriod));
                    double lowerCoeff = getValue((EditText) conView.findViewById(R.id.editLowerCoeff));
                    double upperCoef = getValue((EditText) conView.findViewById(R.id.editUpperCoef));
                    double percent = getValue((EditText) conView.findViewById(R.id.editPercent));
                    int longMacdPeriod = (int) getValue((EditText) conView.findViewById(R.id.editLongMacdPeriod));
                    int shortMacdPeriod = (int) getValue((EditText) conView.findViewById(R.id.editShortMacdPeriod));
                    int signalMacdPeriod = (int) getValue((EditText) conView.findViewById(R.id.editSignalMacdPeriod));
                    int slowK = (int) getValue((EditText) conView.findViewById(R.id.editSlowK));
                    int slowD = (int) getValue((EditText) conView.findViewById(R.id.editSlowD));
                    indicatorSettingMap.put((Indicator) conView.getTag(), new IndicatorSettingModel(pCount, lowerCoeff, upperCoef, percent,
                            longMacdPeriod, shortMacdPeriod, signalMacdPeriod, slowD, slowK));
                    setIndicator((Indicator) conView.getTag(), true, pCount, lowerCoeff, upperCoef, percent,
                            longMacdPeriod, shortMacdPeriod, signalMacdPeriod, slowD, slowK);
                    break;
                case R.id.indicator:
                    enableDisableStudies(View.VISIBLE, View.GONE);
                    break;
                case R.id.tools:
                    enableDisableStudies(View.GONE, View.VISIBLE);
                    break;
                case R.id.menuLayout:
                    dissmissMenu();
                    break;
                case R.id.line:
                    setChart(true, false, false, false);
                    break;
                case R.id.area:
                    setChart(false, false, false, true);
                    break;
                case R.id.candlestick:
                    setChart(false, true, false, false);
                    break;
                case R.id.bar:
                    setChart(false, false, true, false);
                    break;
                case R.id.volume:
                    dissmissMenu();
                    setVolumeChart(!chartSettingsModel.isChartVolume);
                    break;
                case R.id.scroller:
                    dissmissMenu();
                    enableDisableScroller(!chartSettingsModel.isScoller);
                    break;
                case R.id.crossHair:
                    boolean isCrossHair = !PreferenceHandler.getCrossEnable();
                    PreferenceHandler.setCrossEnable(isCrossHair);
                    enableDisableCrosshair();
                    break;
                case R.id.oneMin:
                    onMinuteClick(Minute.M1);
                    break;
                case R.id.twoMin:
                    onMinuteClick(Minute.M2);
                    break;
                case R.id.fiveMin:
                    onMinuteClick(Minute.M5);
                    break;
                case R.id.tenMin:
                    onMinuteClick(Minute.M10);
                    break;
                case R.id.fifteenMin:
                    onMinuteClick(Minute.M15);
                    break;
                case R.id.onemonth:
                    onDailyChartClick(eDailyChart.M1);
                    break;
                case R.id.twomonth:
                    onDailyChartClick(eDailyChart.M2);
                    break;
                case R.id.threemonth:
                    onDailyChartClick(eDailyChart.M3);
                    break;
                case R.id.sixmonth:
                    onDailyChartClick(eDailyChart.M6);
                    break;
                case R.id.oneyear:
                    onDailyChartClick(eDailyChart.Y1);
                    break;
                case R.id.twoyear:
                    onDailyChartClick(eDailyChart.Y2);
                    break;
                case R.id.maxyear:
                    onDailyChartClick(eDailyChart.MAX);
                    break;
                case R.id.graphSetting:
                    settingClick();
                    break;
                case R.id.dateTV:
                case R.id.open:
                case R.id.high:
                case R.id.low:
                case R.id.clo:
                case R.id.vol:
                    setDrawableRigth(view);
                    dataListAdapter.sortingAdapter(view);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void onDailyChartClick(eDailyChart dailyChart) {
        dissmissMenu();
        if (selectedDailyChart.value != dailyChart.value) {
            selectedDailyChart = dailyChart;
            rePopulateChartForMinute(selectedMin);
        }
    }

    private void onMinuteClick(Minute minute) {
        dissmissMenu();
        if (minute.value != chartSettingsModel.selectedMin) {
            rePopulateChartForMinute(minute);
        }
    }

    private void settingClick() {
        //.TODO
        StockChartViewPro.ChartState state = chart.getState();
        if (!fTouchPoint.isNaN() && state != StockChartViewPro.ChartState.MOVING
                && state != StockChartViewPro.ChartState.ZOOMING
                && state != StockChartViewPro.ChartState.BEGIN_ZOOM) {
            double x = chart.getCrosshair().getXValue();
            double y = chart.getCrosshair().getYValue();
          /*  boolean isMoved = (fTouchPoint.x != x || fTouchPoint.y != y);
            if (!isMoved) {
                showMenuScreen();
            }*/
            showMenuScreen();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try {
            int id = compoundButton.getId();
            switch (id) {
                case R.id.chkBox:
                    View view = (View) compoundButton.getTag();
                    Indicator indicator = (Indicator) view.getTag();
                    enableDisableEditButton(view);
                    setIndicator(indicator, b, 0, 0, 0, 0,
                            0, 0, 0, 0, 0);
                    break;
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.toolsList:
                dissmissMenu();
                addSticker((ChartTools) view.getTag());
                break;
        }
    }


    SimpleDateFormat sdf = new SimpleDateFormat(Constants.ddMMMyy);

    private void sendChartReq(int pos) {
        try {
            if (GlobalClass.chartDataProcess == null) {
                GlobalClass.chartDataProcess = new ChartDataProcess();
            }
            GlobalClass.chartDataProcess.setSelectectedToken(_scripCode);

            StructChartReq req = new StructChartReq();
            req.scripCode.setValue(_scripCode);
            StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(_scripCode, true);
            req.segment.setValue(smw.getSegment());
            eMessageCode msgCode = null;
            minSel.setVisibility(View.INVISIBLE);
            switch (pos) {
                case 0:
                    minSel.setVisibility(View.VISIBLE);
                    req.timeFrom.setValue(0);
                    req.timeTo.setValue(0);
                    msgCode = eMessageCode.EXCHCANCELORDER_CASHINTRADEL_BCCHARTDATA;
                    break;
                case 1:
                    minSel.setVisibility(View.VISIBLE);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -7);
                    String date = sdf.format(cal.getTime());
                    req.timeFrom.setValue((int) DateUtil.DToN(date));//9:15
                    req.timeTo.setValue(0);//curr time but upto 3:30
                    msgCode = eMessageCode.INTRADAY_CHART;
                    break;
                default:
                    req.timeFrom.setValue(DateUtil.numberofDays(2000, false));
                    req.timeTo.setValue(DateUtil.numberofDays(2018, true));
                    msgCode = eMessageCode.EOD_CHART;
                    break;
            }
            if (msgCode != null) {
                ArrayList<StructMobTick_Data> chartData = GlobalClass.chartDataProcess.getChartData(_scripCode, msgCode.value);
                if (chartData == null) {
                    SendDataToBCServer sendDataToBCServer = new SendDataToBCServer();
                    sendDataToBCServer.sendChartRequest(req, msgCode);
                } else {
                    boolean isEOD = graphSpinner.getSelectedItemPosition() > 1;
                    chartDataHolder.saveTickData(GlobalClass.chartDataProcess.getChartData(_scripCode, msgCode.value), isEOD);
                    getSaveData();
                }
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void setMinutesSelected(final Minute min) {
        Activity act = (Activity) GlobalClass.latestContext;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selectedMin = min;
                minSel.setText(" (" + min.value + " min)");
            }
        });
    }

    private void setOHLC(final double o, final double h, final double l, final double c, final double v) {

        Activity act = (Activity) GlobalClass.latestContext;

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtOpen.setText(Formatter.toTwoDecimalValue(o));
                txtHigh.setText(Formatter.toTwoDecimalValue(h));
                txtLow.setText(Formatter.toTwoDecimalValue(l));
                txtClose.setText(Formatter.toTwoDecimalValue(c));
                txtVolume.setText(((int) v) + "");
            }
        });
    }

    private void invalidateAll() {
        if (chart != null) {
            chart.clearSeriesCache();
            chart.getIndicatorManager().recalcIndicators();
            chart.invalidate();
            fScroller.recalc();
            fScroller.invalidate();
        }
    }

    private void setChartHeight(boolean isVolumeChartDisplay) {
        float vHeight = 0.25f;
        if (!isVolumeChartDisplay) {
            vHeight = 0;
        }
        chart.getAreas().get(1).setHeightInPercents(vHeight);
    }

    private int getRightAxisWidth(int ralw) {
        int w;
        if (ralw <= 2) {
            w = (int) getResources().getDimension(R.dimen.graph_left_vswidth);
        } else if (ralw <= 4) {
            w = (int) getResources().getDimension(R.dimen.graph_left_swidth);

        } else if (ralw <= 6) {
            w = (int) getResources().getDimension(R.dimen.graph_left_mwidth);

        } else {
            w = (int) getResources().getDimension(R.dimen.graph_left_lwidth);

        }
        return w;
    }

    private void setChartBackgroundColor(int color) {
        try {
            int revertColor;
            if (color == Color.BLACK) {
                revertColor = Color.WHITE;
            } else {
                revertColor = Color.BLACK;
            }

            chart.getAreas().get(1).getPlot().getAppearance().setAllColors(color);
            chart.getAreas().get(0).getPlot().getAppearance().setAllColors(color);
            chart.getAreas().get(1).getPlot().getAppearance().setOutlineColor(Color.YELLOW);
            chart.getAreas().get(0).getPlot().getAppearance().setOutlineColor(Color.YELLOW);
            // chart.getAreas().get(0).getVerticalGridAxis().getScaleAppearance().setOutlineColor(Color.RED);

            chart.getAreas().get(1).getAppearance().setOutlineColor(color);
            chart.getAreas().get(0).getAppearance().setOutlineColor(color);
            chart.getAreas().get(1).getAppearance().setFillColors(color);
            chart.getAreas().get(0).getAppearance().setFillColors(color);
            chart.getAreas().get(0).getRightAxis().getScaleAppearance().getFont().setColor(revertColor);
            chart.getAreas().get(1).getRightAxis().getScaleAppearance().getFont().setColor(revertColor);
            chart.getAreas().get(0).getTopAxis().getScaleAppearance().getFont().setColor(revertColor);
            chart.getAreas().get(1).getBottomAxis().getScaleAppearance().getFont().setColor(revertColor);
            chart.getCrosshair().getAppearance().getFont().setColor(revertColor);
            chart.invalidate();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void crossHairSetup() {
        final Crosshair ch = chart.getCrosshair();
        ch.setAuto(true);

        if (GlobalClass.indexScripCodeOrNot(_scripCode)) {
            //ch.setDrawVertical(false);
            ch.setHorizontalAxis(Side.TOP);
        }
        ch.setLabelFormatProvider(new Crosshair.ILabelFormatProvider() {
            @Override
            public String getLabel(Crosshair sender, Plot plot, double xValue, double yValue) {
                String dispVal = "";
                try {
                    if (ch.isVisible()) {
                        //TODO  'com.leadingbyte.stockchart.Area com.leadingbyte.stockchart.Plot.getArea()' on a null object reference
                        Area area = sender.getParent().getArea();
                        if (area != null) {
                            double o, h, l, c, v;
                            o = h = l = c = v = 0;
                            //String name = area.getName();
                            //if (name != null && name.equalsIgnoreCase(ChartType.VOLUME.name)) {
                            if (chart.getSeries().get(1).isVisible()) {
                                AbstractPoint p = chart.getSeries().get(1).getPointAtValue(xValue);
                                if (null != p) {
                                    double values[] = p.getValues();
                                    v = values[1];
                                    //dispVal = " Volume:" + values[1];
                                }
                            }
                            if (chart.getSeries().get(0).isVisible()) {
                                AbstractPoint p = chart.getSeries().get(0).getPointAtValue(xValue);
                                if (null != p) {
                                    double values[] = p.getValues();
                                    o = values[0];
                                    h = values[1];
                                    l = values[2];
                                    c = values[3];
                                    //setOHLC(values[0],values[1],values[2],values[3],C);
                                    //dispVal = "O:" + values[0] + " H:" + values[1] + " L:" + values[2] + " C:" + values[3];
                                }
                            } else {
                                AbstractPoint p = chart.getSeries().get(2).getPointAtValue(xValue);
                                if (null != p) {
                                    double values[] = p.getValues();
                                    c = values[0];
                                    //setOHLC(O,H,L,values[0],V);
                                    //dispVal = " Value:" + values[0];
                                }
                            }
                            setOHLC(o, h, l, c, v);
                        }

                    }
                } catch (Exception e) {
                    GlobalClass.onError("Error in crossHairSetup ", e);
                }
                return dispVal;
            }

        });

        chart.setOnTouchListener(new View.OnTouchListener() {
            private static final int MIN_CLICK_DURATION = 9000;
            private long startClickTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int e = event.getAction() & MotionEvent.ACTION_MASK;
                switch (e) {
                    case MotionEvent.ACTION_DOWN:
                        if (chart.processCrosshair(event)) {
                            Crosshair c = chart.getCrosshair();
                            fTouchPoint.set(c.getXValue(), c.getYValue());
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        StockChartViewPro.ChartState state = chart.getState();
                        if (state != StockChartViewPro.ChartState.MOVING
                                && state != StockChartViewPro.ChartState.ZOOMING
                                && state != StockChartViewPro.ChartState.BEGIN_ZOOM
                                && !chart.getCrosshair().isVisible()
                                && PreferenceHandler.getDisplayOnTap()) {
                            Area area = chart.getAreas().get(0);
                            int x = (int) event.getX();
                            double value = area.getBottomAxis().getValueByCoordinate(x, true);
                            int index = chart.getSeries().get(0).convertToArrayIndex(value);
                            if (index >= 0 && index < dataPoints.size()) {
                                StockDataGenerator.Point p = dataPoints.get(index);
                                if (toast != null && toast.getView().isShown()) {
                                    setOHLCInToast(p, toast);
                                } else {
                                    createOHLCToast(p);
                                }
                               /* if (isSmall){
                                   GlobalClass.showChart(R.id.container_body,scripName,scripCode,false);
                                }else {
                                    if (toast != null && toast.getView().isShown()) {
                                        setOHLCInToast(p, toast);
                                    } else {
                                        createOHLCToast(p);
                                    }
                                }*/
                            }
                        }
                        break;

                }

                return false;
            }
        });
    }

    private void setAxisRange() {
        if (chart.getSeries().get(0).isVisible()) {
            AxisRange priceRightAxisRange = chart.getAreas().get(0).getRightAxis().getAxisRange();
            double HH = priceRightAxisRange.getMaxOrAutoValue();
            double LL = priceRightAxisRange.getMinOrAutoValue();
            priceRightAxisRange.setAuto(false);
            priceRightAxisRange.setMaxMinValues(HH + getValueInPer(HH, LL), LL);
            //priceRightAxisRange.exp
        }
        chart.invalidate();
    }


    private void removeGrid() {
        try {
            AreaList areas = chart.getAreas();
            for (Area area : areas) {
                //area.getPlot().getVerticalGridAppearance().set
                area.setVerticalGridVisible(false);
                area.getPlot().getAppearance().setOutlineStyle(OutlineStyle.DASH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        LinearPointAdapter line = new LinearPointAdapter();
        chart.getSeries().get(0).setPointAdapter(new IPointAdapter());
        chart.getSeries().get(1).setPointAdapter(new VolumePointAdapter());
        chart.getSeries().get(2).setPointAdapter(line);
        chart.getSeries().get(3).setPointAdapter(line);
        chart.getSeries().get(4).setPointAdapter(line);
    }

    private void notifyAllChart() {
        try {
            chart.clearSeriesCache();
            chart.getIndicatorManager().recalcIndicators();
            fScroller.recalc();
            fScroller.invalidate();
            chart.invalidate();

        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private String twoDigitVal(int val) {
        String v = val + "";
        if (val == 1 || val == 2 || val == 3 || val == 4 || val == 5 || val == 6 || val == 7 || val == 8 || val == 9) {
            v = "0" + val;
        }
        return v;
    }

    private double getValue(EditText text) {
        double value = 0;
        String val = text.getText().toString().trim();
        if (!val.equalsIgnoreCase("")) {
            value = Float.parseFloat(val);
        }
        return value;
    }

    private double getValueInPer(double heigh, double low) {
        double diff = heigh - low;
        return ((diff * 10) / 100);
    }

    private void setIndicator(Indicator ind, boolean isAddOrUpdate, int pCount,
                              double upperCoeff, double lowerCoeff, double percent,
                              int longMacdPeriod, int shortMacdPeriod, int signalMacdPeriod,
                              int slowD, int slowK) {
        try {
            if (isAddOrUpdate) {
                AbstractIndicator indicator = getIndicator(ind);
                if (indicator == null) {
                    switch (ind) {
                        case EMA:
                            EmaIndicator emaInd = new EmaIndicator();
                            indicator = emaInd;
                            break;
                        case RSMA:
                            SmaIndicator smaInd = new SmaIndicator();
                            indicator = smaInd;
                            break;
                        case ATR:
                            AtrIndicator atr = new AtrIndicator();
                            indicator = atr;
                            break;
                        case ADI:
                            AdxIndicator adx = new AdxIndicator();
                            indicator = adx;
                            break;
                        case RSI:
                            RsiIndicator rsi = new RsiIndicator();
                            indicator = rsi;
                            break;
                        case BB:
                            BollingerBandsIndicator bbInd = new BollingerBandsIndicator();
                            if (upperCoeff > 0) {
                                bbInd.setLowerCoeff(upperCoeff);
                            }
                            if (lowerCoeff > 0) {
                                bbInd.setLowerCoeff(lowerCoeff);
                            }

                            indicator = bbInd;
                            BolingerSmaIndicator bsmaInd = new BolingerSmaIndicator();

                            if (pCount > 0) {
                                bsmaInd.setPeriodsCount(pCount);
                            }
                            addIndicator(bsmaInd, Indicator.BSMA);
                            break;
                        case ENVELOPES:
                            EnvelopesIndicator ei = new EnvelopesIndicator();

                            if (percent > 0) {
                                ei.setPercent(percent);
                            }
                            indicator = ei;
                            break;
                        case MACD:
                            MacdIndicator macd = new MacdIndicator();
                            if (longMacdPeriod > 0) {
                                macd.setLongMacdPeriod(longMacdPeriod);
                            }
                            if (shortMacdPeriod > 0) {
                                macd.setShortMacdPeriod(shortMacdPeriod);

                            }
                            if (signalMacdPeriod > 0) {
                                macd.setSignalPeriod(signalMacdPeriod);
                            }

                            indicator = macd;
                            break;
                        case STOCHASTIC:
                            StochasticIndicator si = new StochasticIndicator();
                            if (slowD > 0) {
                                si.setSlowD(slowD);
                            }
                            if (slowK > 0) {
                                si.setSlowK(slowK);
                            }
                            indicator = si;
                            break;

                    }
                    if (pCount > 0) {
                        indicator.setPeriodsCount(pCount);
                    }
                    addIndicator(indicator, ind);

                } else {
                    updateIndicator(ind, indicator, pCount, upperCoeff, lowerCoeff, percent,
                            longMacdPeriod, shortMacdPeriod, signalMacdPeriod, slowD, slowK);
                }
            } else {
                removeIndicator(ind);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void setIndicator(View view) {
        try {
            View convertView = (View) view.getTag();
            Indicator indicator = (Indicator) convertView.getTag();
            List<Indicator> mList = new ArrayList<>(indicatorMap.keySet());
            if (mList.contains(indicator)) {
                ((CheckBox) (view)).setChecked(true);
                convertView.findViewById(R.id.btnEdit).setVisibility(View.VISIBLE);
            } else {
                ((CheckBox) (view)).setChecked(false);
                convertView.findViewById(R.id.btnEdit).setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void addIndicator(AbstractIndicator indicator, Indicator ind) {
        try {
            IndicatorManager indicatorManager = chart.getIndicatorManager();
            indicatorManager.addIndicator(indicator, chart.getSeries().get(0));
            indicatorMap.put(ind, indicator);

            Area a = indicatorManager.findAreaByIndicator(indicator);
            if (!a.getName().equals(this.chart.getAreas().get(0).getName())) {
                a.setAutoHeight(false);
                a.setTitle(ind.name + " " + indicator.toString());
                a.setHeightInPercents(0.12f);
            }
            chart.clearSeriesCache();
            indicatorManager.recalcIndicators();
            chart.invalidate();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }


    private void updateIndicator(Indicator ind, AbstractIndicator indicator, int pCount,
                                 double upperCoeff, double lowerCoeff, double percent,
                                 int longMacdPeriod, int shortMacdPeriod, int signalMacdPeriod,
                                 int slowD, int slowK) {
        try {
            IndicatorManager indicatorManager = chart.getIndicatorManager();

            switch (ind) {
                case BB:
                    BollingerBandsIndicator bbInd = (BollingerBandsIndicator) indicator;
                    if (upperCoeff > 0) {
                        bbInd.setLowerCoeff(upperCoeff);
                    }
                    if (lowerCoeff > 0) {
                        bbInd.setLowerCoeff(lowerCoeff);
                    }
                    BolingerSmaIndicator bsmaInd = (BolingerSmaIndicator) indicatorMap.get(Indicator.BSMA);
                    if (pCount > 0) {
                        bsmaInd.setPeriodsCount(pCount);
                    }
                    break;
                case ENVELOPES:
                    EnvelopesIndicator ei = (EnvelopesIndicator) indicator;
                    if (percent > 0) {
                        ei.setPercent(percent);
                    }
                    break;
                case MACD:
                    MacdIndicator macd = (MacdIndicator) indicator;
                    if (longMacdPeriod > 0) {
                        macd.setLongMacdPeriod(longMacdPeriod);
                    }
                    if (shortMacdPeriod > 0) {
                        macd.setShortMacdPeriod(shortMacdPeriod);

                    }
                    if (signalMacdPeriod > 0) {
                        macd.setSignalPeriod(signalMacdPeriod);
                    }
                    break;
                case STOCHASTIC:
                    StochasticIndicator si = (StochasticIndicator) indicator;
                    if (slowD > 0) {
                        si.setSlowD(slowD);
                    }
                    if (slowK > 0) {
                        si.setSlowK(slowK);
                    }
                    break;
            }
            if (pCount > 0) {
                indicator.setPeriodsCount(pCount);
            }

            indicatorManager.recalcIndicators();
            chart.invalidate();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void removeIndicator(Indicator id) {
        IndicatorManager indicatorManager = chart.getIndicatorManager();
        indicatorManager.removeIndicator(indicatorMap.remove(id));
        if (id.value == Indicator.BB.value) {
            indicatorManager.removeIndicator(indicatorMap.remove(Indicator.BSMA));
        }
        if (indicatorSettingMap.containsKey(id)) {
            indicatorSettingMap.remove(id);
        }
        chart.invalidate();
    }

    private AbstractIndicator getIndicator(Indicator id) {
        return indicatorMap.get(id);
    }

    private void addSticker(ChartTools tools) {
        try {
            AbstractSticker result = null;
            switch (tools) {
                case SIMPLELINE:
                    result = new SimpleGuideSticker();
                    break;
                case SPEEDLINES:
                    result = new SpeedLinesSticker();
                    break;
                case FR:
                    result = new FibonacciRetracementSticker();
                    break;
                case FF:
                    result = new FibonacciFansSticker();
                    break;
                case FA:
                    result = new FibonacciArcsSticker();
                    break;
            }
            if (null != result) {
                result.setAreaName(chart.getSeries().get(0).getName());
                chart.letTheUserGlueSticker(result);
                chart.invalidate();
                GlobalClass.showToast(getContext(), Constants.STICKER_MSG);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void clearStickers() {
        this.chart.getStickers().clear();
        this.chart.invalidate();
    }

    private void saveAsImage() {
        try {
            final Bitmap bmp = this.chart.toBitmap();
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View view = factory.inflate(R.layout.image_dialog, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.dialogImageView);
            imageView.setImageBitmap(bmp);

            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            b.setTitle(getString(R.string.preview_image));
            b.setView(view);
            b.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String text = null;

                    try {
                        String fileName = saveBitmap(bmp);
                        text = String.format(getString(R.string.successfully_saved_fmt), fileName);
                        bmp.recycle();
                        dialog.dismiss();
                    } catch (Exception e) {
                        text = e.getMessage();
                    }

                    GlobalClass.showToast(getContext(), text);
                }
            });
            b.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        bmp.recycle();
                        dialog.dismiss();
                    } catch (Exception e) {
                        GlobalClass.onError("Error in " + getClass().getName(), e);
                    }
                }
            });

            b.show();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private String saveBitmap(Bitmap bmp) throws Exception {
        ByteArrayOutputStream bytes = null;
        FileOutputStream fo = null;
        String fileName = "";
        try {
            bytes = new ByteArrayOutputStream();
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)) {
                fileName = Environment.getExternalStorageDirectory() + File.separator
                        + DateUtil.dateFormatter(new Date(), Constants.DDMMMYYHHMMSS) + ".jpg";

                File f = new File(fileName);
                f.createNewFile();

                fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                return fileName;
            }
            throw new IOException("Unable to compress file!");
        } finally {
            if (null != bytes) bytes.close();
            if (null != fo) fo.close();
        }
    }


    private void getChartDataPoint() {
        try {
            LayoutInflater li = LayoutInflater.from(getContext());
            View v = li.inflate(R.layout.activity_show_tick_list, null);
            RelativeLayout main = (RelativeLayout) v.findViewById(R.id.main);

            TextView totalTicks = (TextView) v.findViewById(R.id.totalTicks);
            totalTicks.setText("Total number of ticks: " + dataPoints.size());
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(main)
                    .setCancelable(true);
            final AlertDialog alert = builder.create();
            alert.show();
            ListView dataListView = (ListView) v.findViewById(R.id.dataList);
            dataListAdapter = new DataListAdapter(getContext(), R.layout.tick_row_layout
                    , new ArrayList<>(dataPoints));
            dataListView.setAdapter(dataListAdapter);

            headers = (LinearLayout) v.findViewById(R.id.header_layout);
            for (int i = 0; i < headers.getChildCount(); i++) {
                View child = headers.getChildAt(i);
                child.setTag(true);
                child.setOnClickListener(this);
            }
            //  setDrawableRigth(headers.getChildAt(0));

            ((TextView) v.findViewById(R.id.popupTitle)).setText(getResources().getString(R.string.show_data));
            v.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private LinearLayout headers;
    private DataListAdapter dataListAdapter;

    private void setDrawableRigth(View view) {
        for (int i = 0; i < headers.getChildCount(); i++) {
            TextView child = (TextView) headers.getChildAt(i);
            child.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.updown, 0);
    }

    private void createOHLCToast(StockDataGenerator.Point p) {
        final View customToastroot = LayoutInflater.from(getContext()).inflate(R.layout.chart_toast, null);
        toast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
        toast.setView(customToastroot);
        toast.setGravity(Gravity.CENTER, 0, 50);
        setOHLCInToast(p, toast);
        toast.show();
        /*
        new CountDownTimer(50000, 1000) {
            @Override
            public void onFinish() {
                toast.cancel();
            }
            @Override
            public void onTick(long millisUntilFinished) {
                toast.setView(customToastroot);
                toast.setGravity(Gravity.CENTER, 0, 50);
                setOHLCInToast(p, toast);
                toast.show();
            }
        }.start();*/
    }

    private void setOHLCInToast(StockDataGenerator.Point p, Toast toast) {
        View customToastroot = toast.getView();
        StaticLiteMktWatch smw = GlobalClass.mktDataHandler.getMkt5001Data(_scripCode, true);
        ((TextView) customToastroot.findViewById(R.id.textTime)).setText("" + getTimeforToast(p.dt));
        ((TextView) customToastroot.findViewById(R.id.textVolume)).setText("Volume :" + (int) p.v);
        ((TextView) customToastroot.findViewById(R.id.textOpen)).setText("Open :" + formatter.format(p.o));
        ((TextView) customToastroot.findViewById(R.id.textHigh)).setText("High :" + formatter.format(p.h));
        ((TextView) customToastroot.findViewById(R.id.textLow)).setText("Low :" + formatter.format(p.l));
        ((TextView) customToastroot.findViewById(R.id.textClose)).setText("Close :" + formatter.format(p.c));
    }

    SimpleDateFormat dailySdf = new SimpleDateFormat("HH:mm"),
            intradaySdf = new SimpleDateFormat("ddMMM"),
            monthly = new SimpleDateFormat("MMMyy"),
            EODSdf = new SimpleDateFormat("yyyy"),
            dailyToast = new SimpleDateFormat("ddMMMyy"),
            monthToast = new SimpleDateFormat("ddMMMyy"),
            sevenDayToast = new SimpleDateFormat("ddMMM HH:mm");

    private String getTimeforToast(Date date) {
        String str = "";
        try {
            int spinnerPos = graphSpinner.getSelectedItemPosition();
            if (spinnerPos == 0) {
                str = dailySdf.format(date);
            } else if (spinnerPos == 1) {
                str = sevenDayToast.format(date);
            } else if (spinnerPos == 2) {
                str = dailyToast.format(date);
            } else if (spinnerPos == 3) {
                str = monthly.format(date);
            } else if (spinnerPos == 4) {
                str = monthToast.format(date);
            } else if (spinnerPos == 5) {
                str = monthToast.format(date);
            } else if (spinnerPos == 6) {
                str = EODSdf.format(date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private String getTime(Date date) {
        String str = "";
        try {
            int spinnerPos = graphSpinner.getSelectedItemPosition();
            if (spinnerPos == 0) {
                str = dailySdf.format(date);
            } else if (spinnerPos == 1) {
                str = dailySdf.format(date);
            } else if (spinnerPos == 2) {
                str = intradaySdf.format(date);
            } else if (spinnerPos == 3) {
                str = monthly.format(date);
            } else if (spinnerPos == 4) {
                str = monthly.format(date);
            } else if (spinnerPos == 5) {
                str = EODSdf.format(date);
            } else if (spinnerPos == 6) {
                str = EODSdf.format(date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private String getMonth(Date date) {
        String str = "";
        try {
            if (graphSpinner.getSelectedItemPosition() == 2) {
                str = EODSdf.format(date);
            } else {
                str = intradaySdf.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private ArrayList<ChartTools> getTools() {
        ArrayList<ChartTools> tools = new ArrayList<>();
        for (ChartTools tool : ChartTools.values()) {
            tools.add(tool);
        }
        return tools;
    }

    private void showMenuScreen() {
        try {

            if (showMenuDialog == null) {
                if (menuTimer == null) {
                    menuTimer = new CountDownTimer(40000, 40000) { //40000 milli seconds is total time, 1000 milli seconds is time interval
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            dissmissMenu();
                        }
                    }.start();
                }

                showMenuDialog = new Dialog(GlobalClass.latestContext, android.R.style.Theme_Translucent_NoTitleBar);
                //View view = LayoutInflater.from(this).inflate(R.layout.layout_chart_menu, null);
                View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.layout_chart_menu, null);
                showMenuDialog.setContentView(view);

                view.findViewById(R.id.line).setOnClickListener(this);
                view.findViewById(R.id.area).setOnClickListener(this);
                view.findViewById(R.id.candlestick).setOnClickListener(this);
                view.findViewById(R.id.bar).setOnClickListener(this);
                view.findViewById(R.id.volume).setOnClickListener(this);
                view.findViewById(R.id.oneMin).setOnClickListener(this);
                view.findViewById(R.id.twoMin).setOnClickListener(this);
                view.findViewById(R.id.fiveMin).setOnClickListener(this);
                view.findViewById(R.id.tenMin).setOnClickListener(this);
                view.findViewById(R.id.fifteenMin).setOnClickListener(this);
                view.findViewById(R.id.indicator).setOnClickListener(this);
                view.findViewById(R.id.tools).setOnClickListener(this);
                view.findViewById(R.id.scroller).setOnClickListener(this);
                view.findViewById(R.id.showPoint).setOnClickListener(this);
                view.findViewById(R.id.showData).setOnClickListener(this);
                view.findViewById(R.id.saveImg).setOnClickListener(this);
                view.findViewById(R.id.clearSticker).setOnClickListener(this);

                view.findViewById(R.id.onemonth).setOnClickListener(this);
                view.findViewById(R.id.twomonth).setOnClickListener(this);
                view.findViewById(R.id.threemonth).setOnClickListener(this);
                view.findViewById(R.id.sixmonth).setOnClickListener(this);
                view.findViewById(R.id.oneyear).setOnClickListener(this);
                view.findViewById(R.id.twomonth).setOnClickListener(this);
                view.findViewById(R.id.maxyear).setOnClickListener(this);

                if (graphSpinner.getSelectedItemPosition() > 1) {
                    view.findViewById(R.id.rightBar).setVisibility(View.GONE);
                }
                if (graphSpinner.getSelectedItemPosition() == 2) {
                    view.findViewById(R.id.dailyBar).setVisibility(View.VISIBLE);
                }

                int selColor = getResources().getColor(R.color.colorAccent);
                switch (selectedMin) {
                    case M1:
                        ((TextView) view.findViewById(R.id.oneMin)).setTextColor(selColor);
                        break;
                    case M2:
                        ((TextView) view.findViewById(R.id.twoMin)).setTextColor(selColor);
                        break;
                    case M5:
                        ((TextView) view.findViewById(R.id.fiveMin)).setTextColor(selColor);
                        break;
                    case M10:
                        ((TextView) view.findViewById(R.id.tenMin)).setTextColor(selColor);
                        break;
                    case M15:
                        ((TextView) view.findViewById(R.id.fifteenMin)).setTextColor(selColor);
                        break;
                }
                switch (selectedDailyChart) {
                    case M1:
                        ((TextView) view.findViewById(R.id.onemonth)).setTextColor(selColor);
                        break;
                    case M2:
                        ((TextView) view.findViewById(R.id.twomonth)).setTextColor(selColor);
                        break;
                    case M3:
                        ((TextView) view.findViewById(R.id.threemonth)).setTextColor(selColor);
                        break;
                    case M6:
                        ((TextView) view.findViewById(R.id.sixmonth)).setTextColor(selColor);
                        break;
                    case Y1:
                        ((TextView) view.findViewById(R.id.oneyear)).setTextColor(selColor);
                        break;
                    case Y2:
                        ((TextView) view.findViewById(R.id.twoyear)).setTextColor(selColor);
                        break;
                    case MAX:
                        ((TextView) view.findViewById(R.id.maxyear)).setTextColor(selColor);
                        break;
                }

                if (chartSettingsModel.isScoller) {
                    ((TextView) view.findViewById(R.id.scroller)).setTextColor(selColor);
                }
                if (PreferenceHandler.getDisplayOnTap()) {
                    ((TextView) view.findViewById(R.id.showData)).setTextColor(selColor);
                }
                /*if(isIndices){
                    view.findViewById(R.id.volume).setVisibility(View.GONE);
                }*/
                if (chartSettingsModel.isChartVolume) {
                    ((TextView) view.findViewById(R.id.volume)).setTextColor(selColor);
                }
                if (chartSettingsModel.isChartLine) {
                    ((TextView) view.findViewById(R.id.line)).setTextColor(selColor);
                } else if (chartSettingsModel.isChartArea) {
                    ((TextView) view.findViewById(R.id.area)).setTextColor(selColor);
                } else if (chartSettingsModel.isChartCandle) {
                    ((TextView) view.findViewById(R.id.candlestick)).setTextColor(selColor);
                } else if (chartSettingsModel.isChartBar) {
                    ((TextView) view.findViewById(R.id.bar)).setTextColor(selColor);
                }

                view.findViewById(R.id.menuLayout).setOnClickListener(this);

                topBarLayout = (LinearLayout) view.findViewById(R.id.topBarLayout);
                toolsLayout = (RelativeLayout) view.findViewById(R.id.toolsLayout);
                studiesListView = (ListView) view.findViewById(R.id.indicatorList);
                studiesListView.setOnItemClickListener(this);
                IndicatorAdapter indicatorAdapter = new IndicatorAdapter(GlobalClass.latestContext, R.layout.layout_chart_indicator, getIndicators());
                studiesListView.setAdapter(indicatorAdapter);
                toolsListView = (ListView) view.findViewById(R.id.toolsList);
                toolsListView.setOnItemClickListener(this);
                ToolsAdapter toolsAdapter = new ToolsAdapter(GlobalClass.latestContext, R.layout.layout_chart_tools, getTools());
                toolsListView.setAdapter(toolsAdapter);
                toolsListView.setOnItemClickListener(this);
                showMenuDialog.show();
                showMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        showMenuDialog = null;
                    }
                });
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }


    private void dissmissMenu() {
        if (showMenuDialog != null) {
            showMenuDialog.dismiss();
            if (menuTimer != null) {
                menuTimer.cancel();
                menuTimer = null;
            }
        }
    }

    private void closeEditpopup() {
        if (editPopupAlert != null) {
            editPopupAlert.dismiss();
        }
    }

    private void openEditPopupWindow(Indicator indicator) {
        try {
            if (editPopupAlert == null) {
                LayoutInflater li = LayoutInflater.from(getContext());
                View v = li.inflate(R.layout.layout_indicator_edit, null);
                v.setTag(indicator);
                TextView txtTitle = ((TextView) v.findViewById(R.id.popupTitle));
                txtTitle.setText(getResources().getString(R.string.edit_popup_title));
                txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_textsize));
                v.findViewById(R.id.close).setOnClickListener(this);

                AbstractIndicator abstractIndicator = getIndicator(indicator);
                ((EditText) v.findViewById(R.id.editPeriod)).setText(abstractIndicator.getPeriodsCount() + "");
                switch (indicator) {
                    case BB:
                        BollingerBandsIndicator bbi = (BollingerBandsIndicator) abstractIndicator;
                        v.findViewById(R.id.coeffLayout).setVisibility(View.VISIBLE);
                        ((EditText) v.findViewById(R.id.editUpperCoef)).setText(bbi.getUpperCoeff() + "");
                        ((EditText) v.findViewById(R.id.editLowerCoeff)).setText(bbi.getLowerCoeff() + "");
                        break;
                    case ENVELOPES:
                        v.findViewById(R.id.percentLayout).setVisibility(View.VISIBLE);
                        EnvelopesIndicator ei = (EnvelopesIndicator) abstractIndicator;
                        ((EditText) v.findViewById(R.id.editPercent)).setText(ei.getPercent() + "");
                        break;
                    case STOCHASTIC:
                        v.findViewById(R.id.slowLayout).setVisibility(View.VISIBLE);
                        StochasticIndicator si = (StochasticIndicator) abstractIndicator;
                        ((EditText) v.findViewById(R.id.editSlowD)).setText(si.getSlowD() + "");
                        ((EditText) v.findViewById(R.id.editSlowK)).setText(si.getSlowK() + "");
                        break;
                    case MACD:
                        v.findViewById(R.id.macdLayout).setVisibility(View.VISIBLE);
                        MacdIndicator macd = (MacdIndicator) abstractIndicator;
                        ((EditText) v.findViewById(R.id.editLongMacdPeriod)).setText(macd.getLongMacdPeriod() + "");
                        ((EditText) v.findViewById(R.id.editShortMacdPeriod)).setText(macd.getShortMacdPeriod() + "");
                        ((EditText) v.findViewById(R.id.editSignalMacdPeriod)).setText(macd.getSignalPeriod() + "");
                        break;
                }
                Button btnApply = (Button) v.findViewById(R.id.btnApply);
                btnApply.setTag(v);
                btnApply.setOnClickListener(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(v)
                        .setCancelable(true);
                editPopupAlert = builder.create();
                editPopupAlert.show();
                editPopupAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        editPopupAlert = null;
                    }
                });
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void setChart(boolean isLine, boolean isCandle, boolean isBar, boolean isArea) {
        dissmissMenu();
        boolean isPriceSeries = isCandle ? isCandle : isBar;
        chart.getSeries().get(3).setVisible(isLine);
        chart.getSeries().get(2).setVisible(isArea);
        chart.getSeries().get(0).setVisible(isPriceSeries);

        if (isCandle) {
            chart.getSeries().get(0).setRenderer(getCandlestickStockRenderer());
            enableDisableOHLC(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
        } else if (isBar) {
            chart.getSeries().get(0).setRenderer(getBarStockRenderer());
            enableDisableOHLC(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
        } else {
            enableDisableOHLC(View.GONE, View.GONE, View.GONE, View.VISIBLE);
        }
        chart.invalidate();
        storeChartSelected(isLine, isCandle, isBar, isArea);
    }

    private CandlestickStockRenderer getCandlestickStockRenderer() {
        CandlestickStockRenderer csr = new CandlestickStockRenderer();
        csr.getRiseAppearance().setAllColors(getRisingColor());
        csr.getFallAppearance().setAllColors(getFallingColor());
        csr.getNeutralAppearance().setAllColors(getRisingColor());
        return csr;
    }

    private BarStockRenderer getBarStockRenderer() {
        BarStockRenderer bsr = new BarStockRenderer();
        bsr.getRiseAppearance().setAllColors(getRisingColor());
        bsr.getFallAppearance().setAllColors(getFallingColor());

        return bsr;
    }

    private int getRisingColor() {
        return Color.rgb(73, 212, 58);
    }

    private int getFallingColor() {
        return Color.rgb(246, 67, 53);
    }

    private void setVolumeChart(boolean isVolumeChart) {
        try {
            if (isIndices) {
                isVolumeChart = false;
            } else {
                storeVolumeChart(isVolumeChart);
            }
            chart.getAreas().get(1).setVisible(isVolumeChart);
            chart.getSeries().get(1).setVisible(isVolumeChart);
            setChartHeight(isVolumeChart);
            if (isVolumeChart) {
                enableDisableVolume(View.VISIBLE);
            } else {
                enableDisableVolume(View.GONE);
            }
            chart.getAreas().get(0).getBottomAxis().setVisible(!isVolumeChart);
            chart.getAreas().get(1).getBottomAxis().setVisible(isVolumeChart);
            chart.getAreas().get(1).getTopAxis().setVisible(false);
            if (graphSpinner.getSelectedItemPosition() == 1 ||
                    graphSpinner.getSelectedItemPosition() == 2) {
                chart.getAreas().get(0).getTopAxis().setVisible(true);
            } else {
                chart.getAreas().get(0).getTopAxis().setVisible(false);
            }

            chart.getAreas().get(0).getBottomAxis().setInvisibleSizeInDips(0);
            chart.getAreas().get(1).getBottomAxis().setInvisibleSizeInDips(0);
            //          chart.getAreas().get(0).getTopAxis().setInvisibleSizeInDips(0);
            //            chart.getAreas().get(0).getTopAxis().setScaleSizeInDips(0);
            chart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date getDatefromDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.set(1900, Calendar.JANUARY, 1);
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date d = cal.getTime();
        //android.util.GlobalClass.log("Date1"," :: "+d);
        return d;
    }


    private void generateNextPoint(double o, double h, double l, double c, double v, Date date) {
        try {

            if (v != 0 || (isIndices && o != 0)) {

                StockDataGenerator.Point p = new StockDataGenerator.Point();//fStockChartDataGenerator.getNextPoint();
                p.o = o;
                p.h = h;
                p.l = l;
                p.c = c;
                p.dt = date;
                p.v = v;

                dataPoints.add(p);

                Axis a = chart.getAreas().get(0).getAxis(chart.getSeries().get(0).getYAxisSide(),
                        chart.getSeries().get(0).getYAxisVirtualId());
                a.getAxisRange().expandAutoValues(p.h, p.l);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void notifyIndicators() {
        List<Indicator> mList = new ArrayList<>(indicatorMap.keySet());
        for (Indicator indicator : mList) {
            IndicatorSettingModel ism;
            if (indicatorSettingMap.containsKey(indicator)) {
                ism = indicatorSettingMap.get(indicator);
            } else {
                ism = new IndicatorSettingModel();
            }
            setIndicator(indicator, true, ism.pCount, ism.lowerCoeff, ism.upperCoef, ism.percent,
                    ism.longMacdPeriod, ism.shortMacdPeriod, ism.signalMacdPeriod, ism.slowD, ism.slowK);
        }
    }

    private void calculateOHLC(StaticLiteMktWatch myMktWatch) {
        try {
            int strdt = myMktWatch.getLw().time.getDateInNumber();
            if (DateUtil.dateFormatter(chartDataHolder.lastTick.lastTimeInt.getValue(), Constants.DDMMMYYHHMM).equalsIgnoreCase(DateUtil.dateFormatter(strdt, Constants.DDMMMYYHHMM))) {

                if (myMktWatch.getLastRate() > chartDataHolder.lastTick.high.getValue()) {
                    chartDataHolder.lastTick.high.setValue(myMktWatch.getLastRate());
                }
                if (myMktWatch.getLastRate() < chartDataHolder.lastTick.low.getValue()) {
                    chartDataHolder.lastTick.low.setValue(myMktWatch.getLastRate());
                }
                chartDataHolder.lastTick.close.setValue(myMktWatch.getLastRate());
                chartDataHolder.lastTick.lastQty.setValue(chartDataHolder.lastTick.lastQty.getValue() + myMktWatch.getLw().lastQty.getValue());
                chartDataHolder.updateLastTick();

            } else if ((currTick != null) &&
                    (DateUtil.dateFormatter(currTick.lastTimeInt.getValue(), Constants.DDMMMYYHHMM).equalsIgnoreCase(DateUtil.dateFormatter(strdt, Constants.DDMMMYYHHMM)))) {

                if (myMktWatch.getLastRate() > currTick.high.getValue()) {
                    currTick.high.setValue(myMktWatch.getLastRate());
                }
                if (myMktWatch.getLastRate() < currTick.low.getValue()) {
                    currTick.low.setValue(myMktWatch.getLastRate());
                }
                currTick.close.setValue(myMktWatch.getLastRate());
                currTick.lastQty.setValue(currTick.lastQty.getValue() + myMktWatch.getLw().lastQty.getValue());
                chartDataHolder.updateCurrTick(currTick);
            } else {
                if (currTick != null) {
                    try {
                        chartDataHolder.addGraghStructure(currTick, true);
                    } catch (Exception e) {
                        GlobalClass.onError("Error in " + className, e);
                    }
                }
                currTick = chartDataHolder.getNewCurrentData();
                //new StructMobTick_Data();
                currTick.open.setValue(myMktWatch.getLastRate());
                currTick.high.setValue(myMktWatch.getLastRate());
                currTick.low.setValue(myMktWatch.getLastRate());
                currTick.close.setValue(myMktWatch.getLastRate());
                currTick.lastQty.setValue(myMktWatch.getLw().lastQty.getValue());
                currTick.setLastTimeI(strdt);
                chartDataHolder.updateCurrTick(currTick);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    /*
        private StructMktWatch getMktStruct(StructIndex structIndex) {
            StructMktWatch mkt = new StructMktWatch();
            mkt.token.setValue(_scripCode);
            mkt.segment.setValue(structIndex.getIndices().exch);
            mkt.lastRate.setValue(structIndex.indexValue.getValue());
            mkt.prevClose.setValue(structIndex.closingIndex.getValue());
            mkt.time.setValue(structIndex.time.getValue());
            return mkt;
        }
    */
    private void enableDisableStudies(int indVisibility, int toolsVisibility) {
        try {
            studiesListView.setVisibility(indVisibility);
            toolsLayout.setVisibility(toolsVisibility);
            if (studiesListView.isShown() || toolsListView.isShown()) {
                topBarLayout.setVisibility(View.GONE);
            } else {
                topBarLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }


    private void enableDisableCrosshair() {
        try {
            boolean isCrossHair = PreferenceHandler.getCrossEnable();
            chart.getCrosshair().setVisible(isCrossHair);
            int resource = isCrossHair ? R.mipmap.cross_active : R.mipmap.cross_inactive;
            crossImage.setImageResource(resource);
            chart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void enableDisableScroller(boolean flag) {
        try {
            chartSettingsModel.isScoller = flag;
            int visibility = flag ? View.VISIBLE : View.GONE;
            fScroller.setVisibility(visibility);
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private ArrayList<Indicator> getIndicators() {
        try {
            ArrayList<Indicator> indicators = new ArrayList<>();
            for (Indicator indicator : Indicator.values()) {
                indicators.add(indicator);
            }
            indicators.remove(indicators.indexOf(Indicator.BSMA));
            return indicators;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void enableDisableEditButton(View view) {
        try {
            View btnEdit = view.findViewById(R.id.btnEdit);
            if (btnEdit.isShown()) {
                btnEdit.setVisibility(View.INVISIBLE);
            } else {
                btnEdit.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + getClass().getName(), e);
        }
    }

    private void enableDisableOHLC(int o, int h, int l, int c) {
        openLayout.setVisibility(o);
        highLayout.setVisibility(h);
        lowLayout.setVisibility(l);
        closeLayout.setVisibility(c);
    }

    private void enableDisableVolume(int v) {
        volumeLayout.setVisibility(v);
    }

    /*
    private boolean isCandleChart() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_CANDLE, true);
    }

    private boolean isBarChart() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_BAR, true);
    }

    private boolean isVolumeChart() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_VOLUME, true);
    }

    private boolean isScroller() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_SCROLLER, true);
    }

    private boolean isLineChart() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_LINE, false);
    }

    private boolean isAreaChart() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_AREA, false);
    }

    private int getMinuteSelected() {
        return VenturaApplication.getPreference().getSharedPrefFromTag(Constants.CHART_MINUTE, Constants.DEF_CHART_MIN);
    }*/

    private void storeChartSelected(boolean isLC, boolean isCC, boolean isBC, boolean isAC) {
        chartSettingsModel.isChartLine = isLC;
        chartSettingsModel.isChartCandle = isCC;
        chartSettingsModel.isChartBar = isBC;
        chartSettingsModel.isChartArea = isAC;
        storeChartSettings();
    }

    private void storeVolumeChart(boolean isVC) {
        VenturaApplication.getPreference().storeSharedPref(Constants.CHART_VOLUME, isVC);
    }

    private void storeChartSettings() {
        VenturaApplication.getPreference().setChartSettings(chartSettingsModel);
    }

    private class IPointAdapter implements Series.IPointAdapter {

        private final StockPoint fPoint = new StockPoint();

        @Override
        public int getPointCount() {
            return dataPoints.size();
        }

        @Override
        public AbstractPoint getPointAt(int i) {
            try {
                StockDataGenerator.Point p = dataPoints.get(i);
                fPoint.setValues(p.o, p.h, p.l, p.c);
                fPoint.setID(p.dt);

            } catch (Exception e) {
                GlobalClass.onError("Error in " + getClass().getName(), e);
            }
            return fPoint;
        }
    }

    private class VolumePointAdapter implements Series.IPointAdapter {

        private final Point2 fPoint = new Point2();

        @Override
        public int getPointCount() {
            return dataPoints.size();
        }

        @Override
        public AbstractPoint getPointAt(int i) {
            try {
                StockDataGenerator.Point p = dataPoints.get(i);
                if (p.v > 0) {
                    fPoint.setValues(0.0, p.v);
                    fPoint.setID(p.dt);
                }
            } catch (Exception e) {
                GlobalClass.onError("Error in " + getClass().getName(), e);
            }
            return fPoint;
        }

    }

    private class LinearPointAdapter implements Series.IPointAdapter {

        private final Point1 fPnt = new Point1();

        @Override
        public int getPointCount() {
            return dataPoints.size();
        }

        @Override
        public AbstractPoint getPointAt(int i) {
            try {
                StockDataGenerator.Point p = dataPoints.get(i);
                fPnt.setValue(p.c);
                fPnt.setID(p.dt);
            } catch (Exception e) {
                GlobalClass.onError("Error in " + getClass().getName(), e);
            }
            return fPnt;
        }

    }

    private class DataListAdapter extends ArrayAdapter {
        private Context mContext;
        private int resource;
        private ArrayList<StockDataGenerator.Point> dp;


        public DataListAdapter(Context context, int resource, ArrayList<StockDataGenerator.Point> dataPoints) {
            super(context, resource);
            this.resource = resource;
            mContext = context;
            Collections.reverse(dataPoints);
            dp = dataPoints;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dp.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try {
                if (convertView == null) {
                    LayoutInflater li = LayoutInflater.from(mContext);
                    convertView = li.inflate(resource, null);
                }
                StockDataGenerator.Point point = dp.get(position);
                ((TextView) convertView.findViewById(R.id.date)).setText(getTimeforToast(point.dt));
                ((TextView) convertView.findViewById(R.id.open)).setText(formatter.format(point.o) + "");
                ((TextView) convertView.findViewById(R.id.high)).setText(formatter.format(point.h) + "");
                ((TextView) convertView.findViewById(R.id.low)).setText(formatter.format(point.l) + "");
                ((TextView) convertView.findViewById(R.id.close)).setText(formatter.format(point.c) + "");
                ((TextView) convertView.findViewById(R.id.volume)).setText((int) point.v + "");

            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
            return convertView;
        }


        public void sortingAdapter(final View view) {
            if (dp != null) {
                final boolean tag = (boolean) view.getTag();
                if (tag) {
                    view.setTag(false);
                } else {
                    view.setTag(true);
                }
                Collections.sort(dp, new Comparator<StockDataGenerator.Point>() {
                    @Override
                    public int compare(StockDataGenerator.Point o1, StockDataGenerator.Point o2) {
                        StockDataGenerator.Point one, two;
                        if (tag) {
                            one = o1;
                            two = o2;
                        } else {
                            one = o2;
                            two = o1;
                        }
                        switch (view.getId()) {
                            case R.id.dateTV:
                                return one.dt.compareTo(two.dt);
                            case R.id.open:
                                return (int) ((one.o - two.o) * 100);
                            case R.id.high:
                                return (int) ((one.h - two.h) * 100);
                            case R.id.low:
                                return (int) ((one.l - two.l) * 100);
                            case R.id.clo:
                                return (int) ((one.c - two.c) * 100);
                            case R.id.vol:
                                return (int) ((one.v - two.v) * 100);
                            default:
                                break;
                        }
                        return 0;
                    }
                });
                notifyDataSetChanged();
            }
        }

    }

    private class IndicatorAdapter extends ArrayAdapter {
        private Context context;
        private int resource;
        private ArrayList<Indicator> indicators;

        public IndicatorAdapter(Context context, int resource, ArrayList<Indicator> indicators) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
            this.indicators = indicators;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return indicators.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try {
                if (convertView == null) {
                    LayoutInflater li = LayoutInflater.from(context);
                    convertView = li.inflate(resource, null);
                }
                Indicator indicator = indicators.get(position);
                convertView.setTag(indicator);
                CheckBox chkBox = (CheckBox) convertView.findViewById(R.id.chkBox);
                chkBox.setText(indicator.name);
                chkBox.setTag(convertView);
                chkBox.setOnCheckedChangeListener(GraphFragment.this);
                setIndicator(chkBox);
                View btnEdit = convertView.findViewById(R.id.btnEdit);
                btnEdit.setTag(convertView);
                btnEdit.setOnClickListener(GraphFragment.this);

            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
            return convertView;
        }

    }

    private class ToolsAdapter extends ArrayAdapter {
        private Context context;
        private int resource;
        private ArrayList<ChartTools> tools;

        public ToolsAdapter(Context context, int resource, ArrayList<ChartTools> tools) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
            this.tools = tools;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tools.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try {
                if (convertView == null) {
                    LayoutInflater li = LayoutInflater.from(context);
                    convertView = li.inflate(resource, null);
                }
                ChartTools tool = tools.get(position);
                convertView.setTag(tool);
                ((TextView) convertView.findViewById(R.id.txtToolName)).setText(tool.name);

            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
            return convertView;
        }
    }

    class GraphHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {

                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                GlobalClass.dismissdialog();
                if (refreshBundle != null) {
                    int token = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                    int msgC = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    if (token == _scripCode) {
                        boolean isEOD = graphSpinner.getSelectedItemPosition() > 1;
                        chartDataHolder.saveTickData(GlobalClass.chartDataProcess.getChartData(token, msgC), isEOD);
                        getSaveData();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
                new AlertBox(getContext(), "Server is down please try after some time.", GraphFragment.this, "");
            }
        }
    }

    private void getSaveData() {
        rePopulateChartForMinute(Minute.getMinute(chartSettingsModel.selectedMin));
        graphHandler = null;
        uiHandler = new UIHandler();
    }

    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                GlobalClass.dismissdialog();
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    boolean isEOD = graphSpinner.getSelectedItemPosition() > 1;
                    if (!isEOD) {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case LITE_MW: //need to handle msgcode only no structure will came
                                StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(_scripCode, false);
                                if (mktWatch != null) {
                                    update(mktWatch);
                                    setMktRates(mktWatch);
                                }
                                break;
                            /*case INDICES:
                                StructIndex structIndex = (StructIndex) refreshBundle.getSerializable(eForHandler.RESPONSE.name);
                                Indices indices = structIndex.getIndices();
                                if (indices.scripCode == _scripCode) {
                                    StructMktWatch mkt = getMktStruct(structIndex);
                                    update(mkt);
                                    setMktRates(mkt);
                                }
                                break;*/
                        }
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
                new AlertBox(getContext(), "Server is down please try after some time.", GraphFragment.this, "");
            }
        }
    }


    private RefreshChartPopulate refreshChartPopulate;

    private void rePopulateChartForMinute(Minute intMinute) {
        refreshChartPopulate = null;
        refreshChartPopulate = new RefreshChartPopulate(intMinute);
        refreshChartPopulate.execute();
    }

    class RefreshChartPopulate extends AsyncTask<Object, Void, String> {

        private Context context;
        Minute intMinute;
        ArrayList<StructMobTick_Data> tickDataList = null;

        public RefreshChartPopulate(Minute min) {
            context = GlobalClass.latestContext;
            this.intMinute = min;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                GlobalClass.showProgressDialog("Loading...");
                dataPoints.clear();
                notifyAllChart();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                //if (selectedMin != intMinute) {
                notifyAllChart();
                isChartLoaded = false;
                setMinutesSelected(intMinute);
                chartSettingsModel.selectedMin = intMinute.value;
                if (graphSpinner.getSelectedItemPosition() == 2) {
                    tickDataList = chartDataHolder.getTickList(selectedDailyChart);
                } else if (graphSpinner.getSelectedItemPosition() == 3) {
                    tickDataList = chartDataHolder.getListForMonth(1);
                } else if (graphSpinner.getSelectedItemPosition() == 4) {
                    tickDataList = chartDataHolder.getListForMonth(3);
                } else if (graphSpinner.getSelectedItemPosition() == 5) {
                    tickDataList = chartDataHolder.getListForMonth(6);
                } else if (graphSpinner.getSelectedItemPosition() == 6) {
                    tickDataList = chartDataHolder.getListForYear();
                } else {
                    tickDataList = chartDataHolder.getListForMinute(intMinute.value);
                }
                if (graphSpinner.getSelectedItemPosition() > 0 && (tickDataList == null || tickDataList.size() < 1)) {
                    AppCompatActivity aca = (AppCompatActivity) GlobalClass.latestContext;
                    aca.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertBox(GlobalClass.latestContext,
                                    "Chart not available. Please try after Sometime.", GraphFragment.this, "");
                        }
                    });
                    return "";
                }
                int size = tickDataList.size();
                int spinPos = graphSpinner.getSelectedItemPosition();
                for (int i = 0; i < size; i++) {
                    StructMobTick_Data tick_data = tickDataList.get(i);
                    // android.util.GlobalClass.log("Time",": "+tick_data.lastTime.toString());
                    Date date = null;
                    if (spinPos > 1) {
                        date = getDatefromDays(tick_data.lastTimeInt.getValue());
                    } else {
                        date = tick_data.lastTime.getValue();
                    }
                    generateNextPoint(tick_data.open.getValue(), tick_data.high.getValue(), tick_data.low.getValue(),
                            tick_data.close.getValue(), tick_data.lastQty.getValue(), date);
                }
                lastTick = tickDataList.get(size - 1);
                setOHLC(lastTick.open.getValue(), lastTick.high.getValue(), lastTick.low.getValue(), lastTick.close.getValue(), lastTick.lastQty.getValue());
                chart.setVisibility(View.VISIBLE);
                //chart.reset();
                //setAxisRange();
                //}
            } catch (Exception e) {
                GlobalClass.onError("Error in " + className, e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                notifyAllChart();
                GlobalClass.dismissdialog();
                isChartLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void update(StaticLiteMktWatch myMktWatch) {

        int strdt = myMktWatch.getLw().time.getDateInNumber();
        int startTime = (int) DateUtil.getFixTimeInNumber(9, 14, 59);
        calculateOHLC(myMktWatch);

        try {
            //TODO    'short enums.Minute.value' on a null object reference
            if (this.selectedMin == null) {
                this.selectedMin = Minute.M1;
            }
            int selectedMin = this.selectedMin.value;
            int seconds = selectedMin * 60;

            int iElem = (int) Math.ceil((double) (lastTick.lastTimeInt.getValue() - startTime) / seconds) - 1;
            int iCurrElem = (int) Math.ceil((double) (strdt - startTime) / seconds) - 1;

            double lastRate = myMktWatch.getLastRate();
            GlobalClass.log("Chart : " + iCurrElem + " : " + iElem + " : " + myMktWatch.getLw().time.getValue());

            if (iCurrElem > iElem) {
                //if (iCurrElem>=iElem) {
                int currTickSeconds = lastTick.lastTimeInt.getValue() + seconds;
                lastTick = new StructMobTick_Data(true);
                lastTick.setLastTimeI(currTickSeconds);
                lastTick.open.setValue(lastRate);
                lastTick.high.setValue(lastRate);
                lastTick.high.setValue(lastRate);
                lastTick.low.setValue(lastRate);
                lastTick.close.setValue(lastRate);
                lastTick.lastQty.setValue(myMktWatch.getLw().lastQty.getValue());

                Date date = null;
                if (graphSpinner.getSelectedItemPosition() > 1) {
                    date = getDatefromDays(lastTick.lastTimeInt.getValue());
                } else {
                    date = lastTick.lastTime.getValue();
                }
                if (isChartLoaded) {
                    generateNextPoint(lastTick.open.getValue(), lastTick.high.getValue(), lastTick.low.getValue(),
                            lastTick.close.getValue(), lastTick.lastQty.getValue(), date);
                    chart.getGlobalAxisRange(Side.RIGHT).resetViewValues();
                    chart.getGlobalAxisRange(Side.RIGHT).resetAutoValues();
                    chart.invalidate();
                }

            } else {

                if (lastRate > lastTick.high.getValue()) {
                    lastTick.high.setValue(lastRate);
                }
                if (lastRate < lastTick.low.getValue()) {
                    lastTick.low.setValue(lastRate);
                }
                lastTick.close.setValue(lastRate);
                lastTick.lastQty.setValue(lastTick.lastQty.getValue() + myMktWatch.getLw().lastQty.getValue());

                if (isChartLoaded) {
                    StockDataGenerator.Point p = dataPoints.get(dataPoints.size() - 1);
                    p.o = lastTick.open.getValue();
                    p.h = lastTick.high.getValue();
                    p.l = lastTick.low.getValue();
                    p.c = lastTick.close.getValue();
                    p.v = lastTick.lastQty.getValue();
                    chart.invalidate();
                }
            }
            setOHLC(lastTick.open.getValue(), lastTick.high.getValue(), lastTick.low.getValue(), lastTick.close.getValue()
                    , lastTick.lastQty.getValue());
            notifyIndicators();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }
}