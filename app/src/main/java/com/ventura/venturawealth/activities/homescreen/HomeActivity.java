package com.ventura.venturawealth.activities.homescreen;

import static one.upswing.sdk.UpswingSdkKt.upswingSdkLogout;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.BaseContract;
import com.ventura.venturawealth.activities.ssologin.Sso_Login_Activity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Structure.Other.HoldingDataStatusModel;
import Structure.Response.BC.LiteIndicesWatch;
import Structure.Response.BC.StructADST;
import Structure.Response.BC.StructCustomDialog;
import Structure.news.StructNewsDisclaimer;
import adapters.QuickOrderAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import chart.GraphFragment;
import connection.Connect;
import connection.ConnectionProcess;
import connection.SendDataToBCServer;
import enums.eExchSegment;
import enums.eForHandler;
import enums.eFragments;
import enums.eIndices;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eReports;
import enums.eSSOApi;
import enums.eSSOTag;
import enums.eServerType;
import enums.eSocketClient;
import enums.eTradeFrom;
import fragments.ActivateMarginFragment;
import fragments.AlertFragment;
import fragments.DashboardFragment;
import fragments.DeactivateMarginFragment;
import fragments.LatestResultFragment;
import fragments.NFO.NFO_Fragment;
import fragments.NotificationFragment;
import fragments.OptionChainNew.OptionChain;
import fragments.ProfileFragment;
import fragments.SettingsFragment;
import fragments.Trade.TradeSummary;
import fragments.ValuetionFragment;
import fragments.backOffice.BackofficeFragment;
import fragments.edis.eDISFragment;
import fragments.excalationmatrix.ExcalationMatrixWebpage;
import fragments.fd.FixedDipositeFragment;
import fragments.holding.HoldingSummary;
import fragments.homeGroups.BseFragment;
import fragments.homeGroups.MktdepthFragmentRC;
import fragments.homeGroups.MoversFragment;
import fragments.homeGroups.NseFragment;
import fragments.homeGroups.QuickOrderDepth;
import fragments.homeGroups.ReportFragment;
import fragments.homeGroups.SlbsFragment;
import fragments.homeGroups.TradeFragment;
import fragments.homeGroups.WatchFragment;
import fragments.nps.NPSDashboardFragment;
import fragments.pledge.PledgeFragment;
import fragments.reports.HoldingEquityDetailsFragment;
import fragments.reports.HoldingFODetailsFragment;
import fragments.reports.NetpositiondetailsFragment;
import fragments.reports.OrderbookDetailsFragment;
import fragments.reports.SLBMHoldingDetails;
import fragments.reports.TradeboodetailsFragment;
import fragments.research.ResearchFragment;
import fragments.simplysave.SimplysaveFragment;
import fragments.sso.CreateGoogleAuthFragment;
import fragments.sso.sso_chnagePINFragment;
import fragments.sso.structure.SsoModel;
import handler.GroupDetail;
import interfaces.OnAlertListener;
import interfaces.OnFontChange;
import utils.Constants;
import utils.DateUtil;
import utils.ExpandableHeightListView;
import utils.Formatter;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import view.customdialog.CustomDialogScr;
import view.help.ChartHelp;
import view.help.ColumnSettingHelp;
import view.help.DepthHelp;
import view.help.HomeHelpScreen;
import view.help.MutualFundHelpScreen;
import view.help.QuickOrderHelp;
import wealth.VenturaServerConnect;
import fragments.fundtransfer.FundtransferFragment;
import wealth.mv.MainPage;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.dropdowns.MFHoldingReports;
import wealth.new_mutualfund.investments.VenturaDIYNewGFragment;
import wealth.new_mutualfund.investments.VenturaTopPicksNewGFragment;
import wealth.new_mutualfund.ipo.ApplyIPOFragment;
import wealth.new_mutualfund.bond.BondDetails;
import wealth.new_mutualfund.sgb.SGBSummaryFragment;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class    HomeActivity extends BaseActivity implements ConnectionProcess, View.OnClickListener, OnFontChange,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, OnAlertListener {

    @Override
    protected int getLayoutResource() {
        return R.layout.home_screen;
    }

    @Override
    protected BaseContract.IPresenter getPresenter() {
        return null;
    }

    @Override
    protected void onPermissionGranted() {
    }

    @Override
    public Context getContext() {
        return HomeActivity.this;
    }

    @Override
    public BaseActivity getBaseActivity() {
        return HomeActivity.this;
    }


    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView nav_view;
    @BindView(R.id.exp_listview)
    ExpandableHeightListView exp_listview;

    // wealth
    @BindView(R.id.mywealth_textview)
    TextView mywealth_textview;
    @BindView(R.id.mf_textview)
    TextView mf_textview;
    @BindView(R.id.ipo_textview)
    TextView ipo_textview;
    @BindView(R.id.bond_textview)
    TextView bond_textview;
    @BindView(R.id.sgb_textview)
    TextView sgb_textview;
    @BindView(R.id.nfo_textview)
    TextView nfo_textview;
    @BindView(R.id.startsip_textview)
    TextView startsip_textview;
    @BindView(R.id.fd_textview)
    TextView fd_textview;

    //@BindView(R.id.fund_transfer)TextView fund_transfer;
    //@BindView(R.id.nps)TextView nps;
    @BindView(R.id.home)
    TextView home;
    @BindView(R.id.home_relative)
    RelativeLayout home_relative;
    @BindView(R.id.relative)
    RelativeLayout relative;
    @BindView(R.id.tool_logo)
    ImageView imageView;
    @BindView(R.id.homeRDgroup)
    RadioGroup homeRDgroup;
    @BindView(R.id.watchRDbutton)
    RadioButton watchRDbutton;
    @BindView(R.id.tradeRDbutton)
    RadioButton tradeRDbutton;
    @BindView(R.id.moversRDbutton)
    RadioButton moversRDbutton;
    @BindView(R.id.mywealthRDbutton)
    RadioButton mywealthRDbutton;
    @BindView(R.id.optionRDbutton)
    RadioButton optionRDbutton;
    @BindView(R.id.bseRDbutton)
    RadioButton bseRDbutton;
    @BindView(R.id.nseRDbutton)
    RadioButton nseRDbutton;
    @BindView(R.id.mcxRDbutton)
    RadioButton mcxRDbutton;
    @BindView(R.id.ncdexRDbutton)
    RadioButton ncdexRDbutton;
    @BindView(R.id.slbsRDbutton)
    RadioButton slbsRDbutton;
    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView horizontal_scroll;

    @BindView(R.id.reportSpinnerLayout)
    LinearLayout reportSpinnerLayout;
    @BindView(R.id.container_body)
    RelativeLayout container_body;
    @BindView(R.id.report_spinner)
    Spinner report_spinner;
    @BindView(R.id.refresh_button)
    ImageButton refresh_button;
    //@BindView(R.id.niftyRow)
    //LinearLayout niftyRow;
    @BindView(R.id.menuExpandableList)
    ExpandableListView menuExpandableList;
    @BindView(R.id.gifimage)
    ImageView gifimage;
    @BindView(R.id.fab)
    Button fab;

    private Fragment m_fragment;
    private AlertDialog m_alertDialog;
    private QuickOrderAdapter quickOrderAdapter = new QuickOrderAdapter();
    private ExpandableListMenuAdapter elma = new ExpandableListMenuAdapter();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);

        GlobalClass.latestContext = this;
        GlobalClass.homeActivity = this;
        GlobalClass.fragmentManager = getSupportFragmentManager();

        initViews();
        initialization();
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
    }

    /**
     * Check to make sure global_tracker.xml was configured correctly (this function only needed
     * for sample apps).
     */
    private boolean checkConfiguration() {
        XmlResourceParser parser = getResources().getXml(R.xml.global_tracker);

        boolean foundTag = false;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();
                    String nameAttr = parser.getAttributeValue(null, "name");

                    foundTag = "string".equals(tagName) && "ga_trackingId".equals(nameAttr);
                }
                if (parser.getEventType() == XmlResourceParser.TEXT) {
                    if (foundTag && parser.getText().contains("REPLACE_ME")) {
                        return false;
                    }
                }
                parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void showsnackbarmsg(String msg) {
        final Snackbar snackbar = Snackbar.make(container_body, ""+msg, 5000);
        //snackbar.setActionTextColor(this.getResources().getColor(R.color.white));
        snackbar.show();
    }
    public void showMaintanceMode(boolean isShow){
        try{
            if(GlobalClass.isMaintanceMode){
                fab.setText("Maintenance Mode");
            }else{
                fab.setText("Market Closed");
            }
            fab.setVisibility(isShow?View.VISIBLE:View.GONE);
        }catch (Exception ex){
            GlobalClass.onError("",ex);
        }
    }
    private void sendScreenImageName(String name) {
    }

    private ArrayAdapter<String> reportspinnerAdapter;
    private  ArrayList<String> reportspinnerlist;

    public void addRemoveReportSpinner(boolean flag) {
        report_spinner.setOnItemSelectedListener(null);
        try {
            reportspinnerlist = eReports.getReports();
            if(GlobalClass.isEquity()) {
                if (!flag) reportspinnerlist.remove(eReports.MARGINE_TRADE.name);
                if (UserSession.getClientResponse().getServerType() == eServerType.ITS || !ObjectHolder.isOCOAllow) {
                    reportspinnerlist.remove(eReports.BRACKET_POSITIONBOOK.name);
                }
                if (!UserSession.getClientResponse().isSLBMActivated()) {
                    reportspinnerlist.remove(eReports.BOD_SECURITIES_LENT_REPORT.name);
                }
            }else if(GlobalClass.isCommodity()){
                reportspinnerlist.remove(eReports.BRACKET_POSITIONBOOK.name);
                reportspinnerlist.remove(eReports.HOLDINGE_QUITY.name);
                reportspinnerlist.remove(eReports.DERIVATIVE_NET_OBLIGATION.name);
                reportspinnerlist.remove(eReports.BOD_SECURITIES_LENT_REPORT.name);
                reportspinnerlist.remove(eReports.MARGINE_TRADE.name);
            }
        }catch (Exception ex){
            VenturaException.Print(ex);
        }
        reportspinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, reportspinnerlist);
        reportspinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        report_spinner.setAdapter(reportspinnerAdapter);
        refresh_button.setOnClickListener(this);
    }

    private void showHomeHelp() {

        if (PreferenceHandler.getLoginCount() < 4 && PreferenceHandler.isHelpScreenShowable()) {
            Dialog dialog = new HomeHelpScreen(HomeActivity.this, false, false);
            dialog.show();
        } else if (PreferenceHandler.isWhatsNewShowable(MobileInfo.getAppVersionName())) {
            Dialog dialog = new HomeHelpScreen(HomeActivity.this, false, true);
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendReqForCustomDialog();
                        }
                    }, 5000);
                }
            });
        }else{
            sendReqForCustomDialog();
        }
    }


    private void initViews() {
        try {
            boolean isIndices = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.INDICES.name, false);
            if (isIndices  || GlobalClass.isCommodity()) {
                horizontal_scroll.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.ventura_color));
                relative.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.GONE);
                horizontal_scroll.setVisibility(View.VISIBLE);
                toolbar.setBackgroundColor(getResources().getColor(R.color.black));
                relative.setVisibility(View.VISIBLE);
            }
            boolean isBSE = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.BSE.name, false);
            if (isBSE && GlobalClass.isEquity()) {
                bseRDbutton.setVisibility(View.VISIBLE);
            }else{
                bseRDbutton.setVisibility(View.GONE);
            }
            boolean isNSE = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NSE.name, false);
            if (isNSE && GlobalClass.isEquity()) {
                nseRDbutton.setVisibility(View.VISIBLE);
            }else{
                nseRDbutton.setVisibility(View.GONE);
            }
            boolean isMCX = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.MCX.name, false);
            if (isMCX && GlobalClass.isCommodity()) {
                mcxRDbutton.setVisibility(View.VISIBLE);
            }else{
                mcxRDbutton.setVisibility(View.GONE);
            }
            boolean isNCDEX = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NCDEX.name, false);
            if (isNCDEX && GlobalClass.isCommodity()) {
                ncdexRDbutton.setVisibility(View.VISIBLE);
            }else{
                ncdexRDbutton.setVisibility(View.GONE);
            }
            /*
            boolean isWaelth = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.WEALTH.name, true);
            if (isWaelth) {
                mywealthRDbutton.setVisibility(View.VISIBLE);
            }else{
                mywealthRDbutton.setVisibility(View.GONE);
            }*/
            try {
                if (!UserSession.getLoginDetailsModel().isClient()){
                    optionRDbutton.setVisibility(View.GONE);
                    slbsRDbutton.setVisibility(View.GONE);
                }else {
                    boolean isSLBS = UserSession.getClientResponse().isSLBMActivated();
                    if (isSLBS && GlobalClass.isEquity()) {
                        slbsRDbutton.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!UserSession.getLoginDetailsModel().isClient()) {
                //niftyRow.setVisibility(View.GONE);
                TextView txtNiftyTitle = (TextView) findViewById(R.id.niftytitle);
                TextView txtNiftyVal = (TextView) findViewById(R.id.niftyVal);
                TextView txtNiftyAbsChg = (TextView) findViewById(R.id.niftyAbsChg);
                TextView txtNiftyline = (TextView) findViewById(R.id.niftyline);
                TextView txtNiftyPerChg = (TextView) findViewById(R.id.niftyPerChg);

                TextView txtNiftyA = (TextView) findViewById(R.id.niftyAdv);
                TextView txtNiftyD = (TextView) findViewById(R.id.niftyDec);
                TextView txtNiftyS = (TextView) findViewById(R.id.niftySame);

                txtNiftyTitle.setVisibility(View.GONE);
                txtNiftyVal.setVisibility(View.GONE);
                txtNiftyAbsChg.setVisibility(View.GONE);
                txtNiftyline.setVisibility(View.GONE);
                txtNiftyPerChg.setVisibility(View.GONE);
                txtNiftyA.setVisibility(View.GONE);
                txtNiftyD.setVisibility(View.GONE);
                txtNiftyS.setVisibility(View.GONE);
            }
            menuExpandableList.setAdapter(elma);
            menuExpandableList.setOnGroupClickListener(onGroupClick);
            menuExpandableList.setOnChildClickListener(onChildClick);
            RefreshNavmenus();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalClass.showAlertDialog(GlobalClass.isMaintanceMode?"Morning Data Sync is going on":"Market is closed.");
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    ExpandableListView.OnChildClickListener onChildClick = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPos, int childPos, long l) {
            NavigationMenuHandler.NavMenus _navMenus = elma.getModel(groupPos);
            if (_navMenus != null && _navMenus._childList != null && _navMenus._childList.size() > childPos) {
                NavigationMenuHandler.NavChildMenus ncm = _navMenus._childList.get(childPos);
                drawer_layout.closeDrawers();
                m_fragment = null;
                Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
                switch (ncm){
                    case PAY_IN:
                    case PAY_OUT:
                    case INTERNAL:
                    case UPI:
                        if (UserSession.getClientResponse()!=null){
                            if(!(currentFragment instanceof FundtransferFragment)){
                                m_fragment = FundtransferFragment.newInstance(childPos);
                            }else {
                                FundtransferFragment ftf = (FundtransferFragment) currentFragment;
                                ftf.setSpinnerSelection(childPos);
                            }
                        }
                        break;
                    case FACEBOOK:
                        Openlink("https://www.facebook.com/venturasecurities/");
                        break;
                    case TWITTER:
                        Openlink("https://twitter.com/Ventura_Sec");
                        break;
                    case VENTURA_BLOG:
                        Openlink("https://blog.ventura1.com/");
                        break;
                    case YOUTUBE:
                        Openlink("https://www.youtube.com/channel/UC_cqLWN4uMEOAHbocqFI63w");
                        break;
                    case INSTAGRAM:
                        Openlink("https://www.instagram.com/ventura_securities/");
                        break;
                    case LINKEDINE:
                        Openlink("https://www.linkedin.com/authwall?trk=bf&trkInfo=AQEgrmFtUEl6hgAAAWv_STA4X1G54_4hRD61wu0AfxEPQYKi_kR0q1OnobNUh0lJWbe87Aqj3pI5mV-FrIT5nN30MoKstBInBlkEar60vaoladEIrspRE-5LZwak0r2Cj46H7ww=&originalReferer=&sessionRedirect=https%3A%2F%2Fwww.linkedin.com%2Fcompany%2Fventura-securities-ltd%2F");
                        break;
                        default:
                            break;
                }

                if (m_fragment != null) {
                    homeRDgroup.setVisibility(View.GONE);
                    if (UserSession.getLoginDetailsModel().isActiveUser()) {
                        home_relative.setVisibility(View.VISIBLE);
                    }
                    new Handler().postDelayed(() -> GlobalClass.fragmentTransaction(
                            m_fragment, R.id.container_body, true, ""), 250);

                    String scrName = m_fragment.getClass().getName();
                    //GlobalClass.log("MainScreenName : "+scrName);
                    sendScreenImageName(scrName); //for sending name to google analytics
                }
            }
            return false;
        }
    };

    public void Openlink(String _url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));
        startActivity(browserIntent);
    }

    public void RefreshNavmenus() {
        if (elma != null) {
            elma.RefreshAdapter(NavigationMenuHandler.getNavList());
        }
    }

    private void initialization() {

        if (UserSession.getGuestResponse() != null && !TextUtils.isEmpty(UserSession.getGuestResponse().guestValidity())) {
            showMsgDialog(UserSession.getGuestResponse().guestValidity(), false);
        }
        exp_listview.setAdapter(quickOrderAdapter);
        exp_listview.setExpanded(true);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
        };
        quickOrderAdapter.refreshesAdapter();
        assert drawer_layout != null;
        drawer_layout.addDrawerListener(toggle);
        drawer_layout.setScrimColor(getResources().getColor(android.R.color.transparent));
        toggle.syncState();
        mywealth_textview.setOnClickListener(this);
        mf_textview.setOnClickListener(this);
        ipo_textview.setOnClickListener(this);
        bond_textview.setOnClickListener(this);
        sgb_textview.setOnClickListener(this);
        nfo_textview.setOnClickListener(this);
        startsip_textview.setOnClickListener(this);
        fd_textview.setOnClickListener(this);
        home.setOnClickListener(this);
        homeRDgroup.setOnCheckedChangeListener(this);
        Glide.with(this).load(R.drawable.gifanimation).into(gifimage);

        boolean isShow = GlobalClass.isMaintanceMode;
        /*if(!isShow && (GlobalClass.broadCastReg != null)){
            isShow = !GlobalClass.broadCastReg.isNormalMKt();
        }*/
        showMaintanceMode(isShow);

        if (PreferenceHandler.isIndicesSwitchingAvl()){
            SensexVisivility();
            ProcessIndicessSwitching();
        }else {
            SensexVisivility(PreferenceHandler.getSelectedIndice());
        }
        refreshIndices(eIndices.NIFTY.value);
        refreshADST(eExchSegment.NSECASH.value);
        refreshADST(eExchSegment.BSECASH.value);
        if (UserSession.getClientResponse().getActiveUser()) {
            if(GlobalClass.tradeBCClient == null || !GlobalClass.tradeBCClient.IsBroadcastConnected) {
                GlobalClass.showProgressDialog("Please wait...");
                Connect.connect(this, this, eSocketClient.BC);
            }else{
                connected();
            }
        } else {
            horizontal_scroll.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            toolbar.setBackgroundColor(getResources().getColor(R.color.ventura_color));
            relative.setVisibility(View.GONE);
            m_fragment = null;
            m_fragment = new MainPage();
            GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, false, "");
        }
    }

    @Override
    public void onClick(View view) {
        drawer_layout.closeDrawers();
        GlobalClass.latestContext = this;
        m_fragment = null;
        Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
        switch (view.getId()) {
            case R.id.mywealth_textview:
                if (!(currentFragment instanceof MainPage)) {
                    m_fragment = new MainPage();
                }
                break;
            case R.id.mf_textview:
                if(! (currentFragment instanceof MutualFundMenuNew)){
                    m_fragment = new MutualFundMenuNew();
                }
                break;
            case R.id.ipo_textview:
                if (UserSession.getClientResponse()!=null) {
                    if (!(currentFragment instanceof ApplyIPOFragment)) {
                        m_fragment = ApplyIPOFragment.newInstance();
                    }
                }
                break;
            case R.id.bond_textview:
                if (!(currentFragment instanceof BondDetails)) {
                    m_fragment = new BondDetails();
                }
                break;
            case R.id.sgb_textview:
                if (!(currentFragment instanceof SGBSummaryFragment)) {
                    m_fragment = new SGBSummaryFragment();
                }
                break;
            case R.id.nfo_textview:
                if (!(currentFragment instanceof NFO_Fragment)) {
                    m_fragment = new NFO_Fragment();
                }
                break;
            case R.id.startsip_textview:
                if (!(currentFragment instanceof VenturaTopPicksNewGFragment)) {
                    m_fragment = new VenturaTopPicksNewGFragment();
                }
                break;
            case R.id.fd_textview:
                m_fragment = new FixedDipositeFragment();
                //Intent webview = new Intent(getActivity(), WebViewFD.class);
                //webview.putExtra("link", eSSOApi.NewUserUrl.value);
                //getActivity().startActivity(webview);
                break;

            case R.id.home:
                if (currentFragment instanceof NotificationFragment &&
                        GlobalClass.fragmentManager.getBackStackEntryCount() <= 1) {
                    GlobalClass.fragmentTransaction(new WatchFragment(), R.id.container_body,
                            false, eFragments.WATCH.name);
                } else {
                    while (GlobalClass.fragmentManager.getBackStackEntryCount() > 1)
                        GlobalClass.fragmentManager.popBackStackImmediate();
                }
                break;
            case R.id.refresh_button:
                refreshButtonClick();
                break;
            default:
                break;
        }
        if (m_fragment == null) return;
        homeRDgroup.setVisibility(View.GONE);
        if (UserSession.getLoginDetailsModel().isActiveUser()) {
            home_relative.setVisibility(View.VISIBLE);
        }
        try{
            String scrName = m_fragment.getClass().getName();
            //GlobalClass.log("MainScreenName : "+scrName);
            sendScreenImageName(scrName); //for sending name to google analytics
        }catch (Exception ex){ex.printStackTrace();}
        new Handler().postDelayed(() -> GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, ""), 250);
    }


    private ExpandableListView.OnGroupClickListener onGroupClick = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {
            if (expandableListView.isGroupExpanded(position)) {
                expandableListView.collapseGroup(position);
            } else {
                NavigationMenuHandler.NavMenus _navMenus = elma.getModel(position);
                if (_navMenus != null && _navMenus._childList == null) {
                    drawer_layout.closeDrawers();
                    m_fragment = null;
                    Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
                    switch (_navMenus) {
                        case NPS:
                            if (!(currentFragment instanceof NPSDashboardFragment)) {
                                m_fragment = new NPSDashboardFragment();
                            }
                            break;
                        case PARK_EARN:
                            if (!(currentFragment instanceof SimplysaveFragment)) {
                                m_fragment = new SimplysaveFragment();
                            }
                            break;
                        case DASHBOARD:
                            if (UserSession.isTradeLogin()) {
                                if (!(currentFragment instanceof DashboardFragment)) {
                                    m_fragment = new DashboardFragment();
                                }
                            } else {
                                m_fragment = new TradeFragment(eTradeFrom.DASHBOARD);
                            }
                            break;
                        case MY_LEDGER:
                            if (!(currentFragment instanceof BackofficeFragment)) {
                                m_fragment = new BackofficeFragment();
                            }
                            break;
                        case TRADE_SUMMARY:
                            if (!(currentFragment instanceof TradeSummary)) {
                                m_fragment = new TradeSummary();
                            }
                            break;
                        case HOLDINGS:
                            if (!(currentFragment instanceof HoldingSummary)) {
                                m_fragment = new HoldingSummary();
                            }
                            break;
                        case EDIS:
                            if (!(currentFragment instanceof eDISFragment)) {
                                m_fragment = new eDISFragment();
                            }
                            break;
                        case PLEDGE:
                            if (!(currentFragment instanceof PledgeFragment)) {
                                m_fragment = new PledgeFragment();
                            }
                            break;
                        case PROFILE:
                            if (!(currentFragment instanceof ProfileFragment)) {
                                m_fragment = new ProfileFragment();
                            }
                            break;
                        case NOTIFICATION:
                            if (PreferenceHandler.getNotificationActive()) {
                                if (!(currentFragment instanceof NotificationFragment)) {
                                    m_fragment = NotificationFragment.newInstance(0);
                                }
                            } else {
                                new AlertBox(GlobalClass.latestContext,"OK", getResources().getString(R.string.disclaimer_description),HomeActivity.this,"newsdisclaimer");
                                //showMsgDialog("Disclaimer!", R.string.disclaimer_description);
                            }
                            break;
                        case ALERTS:
                            if (!(currentFragment instanceof AlertFragment)) {
                                m_fragment = new AlertFragment();
                            }
                            break;
                        case OPT_CHAIN:
                            if (!(currentFragment instanceof OptionChain)) {
                                m_fragment = new OptionChain();
                            }
                            break;
                        case RESEARCH:
                            if (!(currentFragment instanceof ResearchFragment)) {
                                m_fragment = ResearchFragment.newInstance();
                            }
                            break;
                        case CHANGE_PIN:
                            if (!(currentFragment instanceof sso_chnagePINFragment)) {
                                m_fragment = sso_chnagePINFragment.newInstance();
                            }
                            break;
                        case SETTING:
                            if (!(currentFragment instanceof SettingsFragment)) {
                                m_fragment = new SettingsFragment(HomeActivity.this);
                            }
                            break;
                        case ACTIVATE_MARGIN_TRADING:
                            if (!(currentFragment instanceof ActivateMarginFragment)) {
                                m_fragment = ActivateMarginFragment.newInstance();
                            }
                            break;
                        case DEACTIVATE_MARGIN_TRADING:
                            if (!(currentFragment instanceof DeactivateMarginFragment)) {
                                m_fragment = DeactivateMarginFragment.newInstance();
                            }
                            break;
                        case RATE_US:
                            rateapplication();
                            break;
                        case SHARE_APP:
                            shareapplication();
                            break;
                        case HELP:
                            onHelpClick();
                            break;
                        case Escalation_Matrix:
                            if (!(currentFragment instanceof ExcalationMatrixWebpage)) {
                                m_fragment = ExcalationMatrixWebpage.newInstance();
                            }
                            break;
                        case GOOGLEAUTH:
                        case GOOGLEAUTH2:
                            if (!(currentFragment instanceof CreateGoogleAuthFragment)) {
                                m_fragment = CreateGoogleAuthFragment.newInstance();
                            }
                            break;
                        case LOGOUT:
                            logoutAlert("Logout", "Are you sure you want to Logout?",true);
                            break;
                        case EXIT:
                            showExitAlert();
                            break;
                        case AADHAAR_UPDATE:
                            if (UserSession.getClientResponse() != null) {
                                openAdharDialog();
                            }
                            break;
                        default:
                            break;
                    }
                    if (m_fragment != null) {
                        homeRDgroup.setVisibility(View.GONE);
                        if (UserSession.getLoginDetailsModel().isActiveUser()) {
                            home_relative.setVisibility(View.VISIBLE);
                        }
                        new Handler().postDelayed(() -> GlobalClass.fragmentTransaction(
                                m_fragment, R.id.container_body, true, ""), 250);

                        String scrName = m_fragment.getClass().getName();
                        //GlobalClass.log("MainScreenName : "+scrName);
                        sendScreenImageName(scrName); //for sending name to google analytics
                    }
                }
            }
            return false;
        }
    };
    private void showExitAlert(){
        try{
            /*try{
                GlobalClass.groupHandler.saveUserDefineData();
            }catch (Exception ex){
                VenturaException.Print(ex);
            }*/
            new AlertBox(GlobalClass.latestContext, "Ventura Wealth", "EXIT",
                    "Are you sure you want to exit?", true);
        }catch (Exception ex){
            VenturaException.Print(ex);
        }
    }

    private AlertDialog dialog;

    private void openAdharDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        builder.setCancelable(false);
        View view = LayoutInflater.from(this).inflate(R.layout.adhar_update, null);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        TextView adharText = (TextView) view.findViewById(R.id.adharText);
        adharText.setText("Dear Investor, As per Govt. directive, please link your Aadhaar to your" +
                " account with VENTURA by " + UserSession.getClientResponse().adharDate.getValue() + " to avoid blocking of your account." +
                " To update now click on this link.http://bit.ly/2iMAodl\n" +
                "Team Ventura.");
        final Button linkUp = (Button) view.findViewById(R.id.linkUp);
        linkUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                myWebLink.setData(Uri.parse("http://bit.ly/2iMAodl"));
                startActivity(myWebLink);
            }
        });
        Button btn = (Button) view.findViewById(R.id.remindLater);
        if (UserSession.getClientResponse().adharUpdate.getValue() == 'F')
            btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode,
                                 android.view.KeyEvent event) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                return false; // pass on to be processed as normal
            }
        });
        animateIt(btn);
        animateIt(linkUp);
    }

    public void animateIt(Button btn) {
        ObjectAnimator a = ObjectAnimator.ofInt(btn, "textColor",
                getResources().getColor(R.color.spinner_divider), getResources().getColor(R.color.gotit));
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setDuration(1000);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setEvaluator(new ArgbEvaluator());
        AnimatorSet t = new AnimatorSet();
        t.play(a);
        t.start();
    }

    private void onHelpClick() {
        Fragment mutualfundFragment = GlobalClass.fragmentManager.findFragmentById(R.id.mfBody);
        Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
        if (currentFragment instanceof WatchFragment ||
                currentFragment instanceof MoversFragment ||
                currentFragment instanceof BseFragment ||
                currentFragment instanceof NseFragment) {
            Dialog homeHelpScreen = new HomeHelpScreen(HomeActivity.this, true, false);
            homeHelpScreen.show();
        } else if ((currentFragment instanceof SettingsFragment) && ((SettingsFragment) currentFragment).selectedSettingTab == 2) {
            Dialog columnSettingHelp = new ColumnSettingHelp(GlobalClass.latestContext, true);
            columnSettingHelp.show();
        } else if ((currentFragment instanceof SettingsFragment) && ((SettingsFragment) currentFragment).selectedSettingTab == 1) {
            Dialog quickOrderHelp = new QuickOrderHelp(GlobalClass.latestContext, true);
            quickOrderHelp.show();
        } else if ((currentFragment instanceof MktdepthFragmentRC)) {
            Dialog depthHelp = new DepthHelp(GlobalClass.latestContext, true);
            depthHelp.show();
        } else if ((currentFragment instanceof GraphFragment)) {
            Dialog chartHelp = new ChartHelp(true);
            chartHelp.show();
        }else if(currentFragment instanceof VenturaTopPicksNewGFragment){
            Dialog dialog = new MutualFundHelpScreen(getActivity(),false,false);
            dialog.show();
        }else if(currentFragment instanceof VenturaDIYNewGFragment){
            Dialog dialog = new MutualFundHelpScreen(getActivity(),false,false);
            dialog.show();
        }else if(mutualfundFragment instanceof MFHoldingReports){
            Dialog dialog = new MutualFundHelpScreen(getActivity(),true,false);
            dialog.show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        m_fragment = null;
        String FragTag = "";
        switch (id) {
            case R.id.watchRDbutton:
                m_fragment = new WatchFragment();
                FragTag = eFragments.WATCH.name;
                break;
            case R.id.tradeRDbutton:
                if(GlobalClass.isMaintanceMode){
                    String msg = "This section is temporarily unavailable during our morning data sync. Please check back after some time for full access";
                    GlobalClass.showToast(getContext(),msg);
                    CheckRadioButton(RadioButtons.WATCH);
                }else {
                    if (UserSession.isTradeLogin()) {
                        String selectedTab = PreferenceHandler.getSelectedReport();
                        if (reportspinnerAdapter == null) {
                            addRemoveReportSpinner(ObjectHolder.isMarginTrade);
                        }
                        int position = reportspinnerAdapter.getPosition(selectedTab);
                        report_spinner.setSelection(position);
                        m_fragment = ReportFragment.newInstance();
                    } else {
                        m_fragment = new TradeFragment(eTradeFrom.TAB);
                        FragTag = eFragments.TRADE.name;
                    }
                }
                break;
            case R.id.optionRDbutton:
                m_fragment = new OptionChain();
                FragTag = eFragments.OPTIONCHAIN.name;
                break;
            case R.id.moversRDbutton:
                m_fragment = new MoversFragment();
                FragTag = eFragments.MOVERS.name;
                break;
            case R.id.mywealthRDbutton:
                m_fragment = new MainPage();
                FragTag = eFragments.MYWEALTH.name;
                break;
            case R.id.bseRDbutton:
                m_fragment = new BseFragment();
                FragTag = eFragments.BSE.name;
                break;
            case R.id.mcxRDbutton:
                m_fragment = new BseFragment();
                FragTag = eFragments.BSE.name;
                break;
            case R.id.ncdexRDbutton:
                m_fragment = new BseFragment();
                FragTag = eFragments.BSE.name;
                break;
            case R.id.slbsRDbutton:
                    m_fragment = new SlbsFragment();
                    FragTag = eFragments.SLBS.name;
                break;
            case R.id.nseRDbutton:
                    m_fragment = new NseFragment();
                    FragTag = eFragments.NSE.name;
                break;
        }
        if (m_fragment == null) return;
        GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, FragTag);
        try{
            String scrName = m_fragment.getClass().getName();
            //GlobalClass.log("MainScreenName : "+scrName);
            sendScreenImageName(scrName); //for sending name to google analytics
        }catch (Exception ex){ex.printStackTrace();}
    }


    @Override
    public void onBackPressed() {
        try {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers();
                return;
            }
            Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
            if (currentFragment == null) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                return;
            }
            if (!UserSession.getLoginDetailsModel().isActiveUser() && currentFragment instanceof MainPage) {
                showExitAlert();
                return;
            }
            if (currentFragment instanceof QuickOrderDepth) {
                m_fragment = null;
                m_fragment = new WatchFragment();
                GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "");
            } else if (currentFragment instanceof WatchFragment) {
               showExitAlert();
            }  else if (GlobalClass.fragmentManager.getBackStackEntryCount() == 0) {
                if (currentFragment instanceof NotificationFragment) {
                    GlobalClass.fragmentTransaction(new WatchFragment(), R.id.container_body, false, eFragments.WATCH.name);
                } else {
                    watchRDbutton.setChecked(true);
                }
            } else {
                GlobalClass.fragmentManager.popBackStackImmediate();
            }
            @SuppressLint("RestrictedApi")
            List<Fragment> fragments = GlobalClass.fragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof NseFragment && !VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NSE.name, false)) {
                    while (GlobalClass.fragmentManager.getBackStackEntryCount() > 1)
                        GlobalClass.fragmentManager.popBackStackImmediate();
                }
                if (fragment instanceof BseFragment && !VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.BSE.name, false)) {
                    while (GlobalClass.fragmentManager.getBackStackEntryCount() > 1)
                        GlobalClass.fragmentManager.popBackStackImmediate();
                }
            }
            setHomeVisibility();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHomeVisibility() {
        Fragment afterPopFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
        if (afterPopFragment instanceof TradeFragment ||
                afterPopFragment instanceof MoversFragment ||
                afterPopFragment instanceof WatchFragment ||
                afterPopFragment instanceof BseFragment ||
                afterPopFragment instanceof NseFragment ||
                afterPopFragment instanceof MktdepthFragmentRC ||
                afterPopFragment instanceof LatestResultFragment ||
                afterPopFragment instanceof ValuetionFragment ||
                afterPopFragment instanceof GraphFragment) {
            homeRDgroup.setVisibility(View.VISIBLE);
            home_relative.setVisibility(View.GONE);
            reportSpinnerLayout.setVisibility(View.GONE);
        } else if (afterPopFragment instanceof ReportFragment ||
                afterPopFragment instanceof OrderbookDetailsFragment ||
                afterPopFragment instanceof TradeboodetailsFragment ||
                afterPopFragment instanceof NetpositiondetailsFragment ||
                afterPopFragment instanceof HoldingFODetailsFragment ||
                afterPopFragment instanceof HoldingEquityDetailsFragment) {
            homeRDgroup.setVisibility(View.VISIBLE);
            home_relative.setVisibility(View.GONE);
            reportSpinnerLayout.setVisibility(View.VISIBLE);
        } else {
            homeRDgroup.setVisibility(View.GONE);
            if (UserSession.getLoginDetailsModel().isActiveUser()) {
                home_relative.setVisibility(View.VISIBLE);
            }
            reportSpinnerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GlobalClass.homeScrUiHandler = null;
        startStopBC(0);
    }

    private void startStopBC(int startStop) {
        if (GlobalClass.tradeBCClient != null) {
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.startStopReq(startStop);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalClass.homeScrUiHandler = new HomeActivityUIHandler();
        try {
            GlobalClass.latestContext = this;
            GlobalClass.homeActivity = this;
            GlobalClass.fragmentManager = getSupportFragmentManager();

            //GlobalClass.log("TestSudip\t","onResume...................");
            setHomeVisibility();

            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            startStopBC(1);
            //OpenFragmentAfrerResume();
            if (GlobalClass.clsNewsHandler != null) {
                GlobalClass.clsNewsHandler.reqnews();
            }
            if (activityResumeListiner != null) {
                activityResumeListiner.onResume();
                activityResumeListiner = null;
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }


    public interface ActivityResumeListiner {
        void onResume();
    }

    private ActivityResumeListiner activityResumeListiner;

    public void setResumeListener(ActivityResumeListiner arl) {
        this.activityResumeListiner = arl;
    }

    private void shareapplication() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check Out Ventura Wealth App : \n\nhttps://play.google.com/store/apps/details?id=com.ventura.venturawealth&hl=en";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void rateapplication() {
        String url = "details?id=" + getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://" + url)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/" + url)));
        }
    }

    public void logoutAlert(String btnName, String msg , boolean needCancelBtn) {

        final AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(this);
        m_alertBuilder.setTitle("Ventura Wealth");
        m_alertBuilder.setMessage(msg);
        m_alertBuilder.setIcon(R.drawable.ventura_icon);
        m_alertBuilder.setCancelable(false);
        m_alertBuilder.setPositiveButton(btnName,
                (dialog, id) -> {
                    dialog.cancel();
                    new Logout().execute();
                });
        if (needCancelBtn) {
            m_alertBuilder.setNegativeButton("Cancel",
                    (dialog, id) -> dialog.cancel());
        }
        if (m_alertDialog != null) {
            m_alertDialog.dismiss();
        }
        m_alertDialog = m_alertBuilder.create();
        m_alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        m_alertDialog.show();
        if (needCancelBtn){
            m_alertDialog.getButton(m_alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ventura_color));
        }
        m_alertDialog.getButton(m_alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ventura_color));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Fragment currentFrag = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
        if (GlobalClass.reportHandler != null && report_spinner.getSelectedItemPosition() >= 0) {
            /*if (report_spinner.getSelectedItemPosition() > 0) {
                GlobalClass.clsMarginHolding.clearMargin();
            }*/
            String selectedReport = report_spinner.getSelectedItem().toString();
            PreferenceHandler.setSelectedReport(selectedReport);

            Message msg = Message.obtain(GlobalClass.reportHandler);
            Bundle confMsgBundle = new Bundle();
            confMsgBundle.putString(eForHandler.SPINNERNAME.name, selectedReport);
            msg.setData(confMsgBundle);
            GlobalClass.reportHandler.sendMessage(msg);
        } else if (
                currentFrag instanceof OrderbookDetailsFragment ||
                        currentFrag instanceof TradeboodetailsFragment ||
                        currentFrag instanceof NetpositiondetailsFragment ||
                        currentFrag instanceof HoldingFODetailsFragment ||
                        currentFrag instanceof HoldingEquityDetailsFragment||
                        currentFrag instanceof SLBMHoldingDetails
                        ) {
            GlobalClass.fragmentTransaction(ReportFragment.newInstance(),
                    R.id.container_body, false, eFragments.REPORT.name);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void connected() {
        m_fragment = null;
        GlobalClass.showProgressDialog("Please wait loading groups");
        //showProgress(R.string.Loading_groups);
        GroupDetail userDefineGroup = GlobalClass.groupHandler.getUserDefineGroup();
        if(!userDefineGroup.isReqSend) {
            OpenFragmentOnAppearence();
        }
    }
    @Override
    public void sensexNiftyCame() {

    }
    @Override
    public void serverNotAvailable() {
        showMsgDialog(Constants.ERR_MSG_SERVERNOTAVL_CONNECTION, true);
    }

    @Override
    public void onOk(String tag) {
        if(tag.equalsIgnoreCase("newsdisclaimer")) {
            PreferenceHandler.setNotificationActive(true);
            StructNewsDisclaimer snd = new StructNewsDisclaimer();
            snd.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            snd.model.setValue(android.os.Build.MODEL);
            snd.IMEI.setValue(MobileInfo.getDeviceID(this));
            new SendDataToBCServer().sendNotificationAgreeReq(snd);
            m_fragment = null;
            Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
            if (!(currentFragment instanceof NotificationFragment)) {
                m_fragment = NotificationFragment.newInstance(0);
                GlobalClass.fragmentTransaction(m_fragment, R.id.container_body, true, "");
            }
        }
    }

    @Override
    public void onCancel(String tag) {

    }

    class Logout extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Loging out...");
        }

        @Override
        protected String doInBackground(String... strings) {
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.sendLogoutReq();
            UserSession.setTradeLogin(false);
            boolean _ISCLIENT = UserSession.getLoginDetailsModel().isClient();
            ObjectHolder.connconfig.setbCastServerIP("");
                CallSSOAPI_CLOSESESSION(PreferenceHandler.getSSOSessionID(),
                        PreferenceHandler.getSSOAuthToken(), PreferenceHandler.getSSORefreshToken(), _ISCLIENT);
            if(GlobalClass.isUPSwingPageOpen) {
                // for logout the upswing sdk
                upswingSdkLogout(getContext());
                callUpswingLogoutApi();
                //
            }
            PreferenceHandler.setSSOSessionID("");
            PreferenceHandler.setSSORefreshToken("");
            PreferenceHandler.setSSOAuthToken("");
            ArrayList<SsoModel> list1 = PreferenceHandler.getSsoClientDetails();
            for (int i = 0 ; i < list1.size() ; i++){
                SsoModel model1 = list1.get(i);
                String Clientcode = UserSession.getLoginDetailsModel().getUserID();
                if(model1.getClientCode().contains(Clientcode)){
                    list1.remove(model1);
                }
            }
            PreferenceHandler.setSsoClientDetails(list1);

            GlobalClass.oneTimeTryForHoldingDetail = true;
            PreferenceHandler.setHoldingStatus(new HoldingDataStatusModel());
            VenturaApplication.getPreference().setDPHoldingList(null);
            VenturaApplication.getPreference().setGroupDetail(null);
            VenturaApplication.getPreference().setFamilyData(null);

            UserSession.setClientResponse(null);
            PreferenceHandler.setPreviousLoginUser(UserSession.getLoginDetailsModel().getUserID());
            UserSession.HandleLogout();
            ObjectHolder.intializeAll();
            GlobalClass.initialiseAll();
            VenturaServerConnect.closeSocket();
            VenturaServerConnect.InitialiasedAll(true);
            PreferenceHandler.getQuickOrderList().clear();
            PreferenceHandler.setQuickOrderList();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //GlobalClass.dismissdialog();
        }
    }

    private void callUpswingLogoutApi() {
        try {
            String _url = "https://vw.ventura1.com/authrestapi/upSwingFDLogOut?ucc=" + UserSession.getLoginDetailsModel().getUserID();
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String response1 = response.toString();
            }
        } catch (Exception e) {
            GlobalClass.onError("callUpswingLogoutApi: ",e);
        }

    }
    private void CallSSOAPI_CLOSESESSION (String sessionID, String authToken,String refreshToken,boolean _ISCLIENT){
        String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        try {
            GlobalClass.showProgressDialog(Constants.pleasewait);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = eSSOApi.BASEURL.value + eSSOApi.logout.value;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("client_id", UserSession.getLoginDetailsModel().getUserID());
            jsonBody.put("session_id_to_close", sessionID);
            jsonBody.put("datetime", currenttime);
            jsonBody.put("ip", MobileInfo.getIPAddress(true));

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GlobalClass.log("LOG_VOLLEY_SUCCESS", response);
                    ParseResponse(response,_ISCLIENT);
                    VenturaException.SSOPrintLog(eSSOApi.logout.value,"RequestBody : "+mRequestBody+ " "+currenttime+" : ResponseBody : "+response + " : " +DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        String responseString =  new String(error.networkResponse.data);
                        ParseResponse(responseString,_ISCLIENT);
                        GlobalClass.log("LOG_VOLLEY_ERR", responseString);
                        VenturaException.SSOPrintLog(eSSOApi.logout.value,"RequestBody : "+mRequestBody+ " "+currenttime+" : ResponseBody : "+responseString + " : " +DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(eSSOTag.ContentType.name, eSSOTag.ContentType.value);
                    params.put(eSSOTag.session_id.name, sessionID);
                    params.put(eSSOTag.xapikey.name, eSSOTag.xapikey.value);
                    params.put(eSSOTag.Authorization.name, "Bearer " + authToken);
                    params.put(eSSOTag.Refresh_token.name, refreshToken);
                    return params;
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = new String(response.data);
                        PreferenceHandler.setSSOSessionID(response.headers.get("session_id"));
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 20000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 0;
                }
                @Override
                public void retry(VolleyError error) throws VolleyError {
                }
            });
            requestQueue.add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ParseResponse(String response,boolean _ISCLIENT) {
        try {
            GlobalClass.dismissdialog();
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.optString("status");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            PreferenceHandler.setSSOSessionID("");
            PreferenceHandler.setSSORefreshToken("");
            PreferenceHandler.setSSOAuthToken("");
            runOnUiThread(() -> {
                Class<?> cls = _ISCLIENT ? Sso_Login_Activity.class : Sso_Login_Activity.class;
                Intent intent = new Intent(HomeActivity.this, cls);
                finishAffinity();
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // finish(false);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void refreshADST(int segmentCode) {
        StructADST structAdst = GlobalClass.mktDataHandler.getADSTData(segmentCode);
        if(structAdst != null) {
            if (segmentCode == eExchSegment.NSECASH.value
                    && UserSession.getLoginDetailsModel().isClient()) {
                TextView txtNiftyA = (TextView) findViewById(R.id.niftyAdv);
                TextView txtNiftyD = (TextView) findViewById(R.id.niftyDec);
                TextView txtNiftyS = (TextView) findViewById(R.id.niftySame);

                txtNiftyA.setText("A: " + structAdst.adv.getValue());
                txtNiftyD.setText("D: " + structAdst.dec.getValue());
                txtNiftyS.setText("S: " + structAdst.same.getValue());
            }
            if (segmentCode == eExchSegment.BSECASH.value) {

                TextView txtSensexA = (TextView) findViewById(R.id.sensexAdv);
                TextView txtSensexD = (TextView) findViewById(R.id.sensexDec);
                TextView txtSensexS = (TextView) findViewById(R.id.sensexSame);

                txtSensexA.setText("A: " + structAdst.adv.getValue());
                txtSensexD.setText("D: " + structAdst.dec.getValue());
                txtSensexS.setText("S: " + structAdst.same.getValue());
            }
        }
    }
    private void refreshInicesForTimer(int indexCode){
        LiteIndicesWatch structIndex = GlobalClass.mktDataHandler.getIndicesData(indexCode);

        if(structIndex != null) {
            TextView txtSensexTitle = (TextView) findViewById(R.id.sensextitle);
            TextView txtSensexVal = (TextView) findViewById(R.id.sensexVal);
            TextView txtSensexAbsChg = (TextView) findViewById(R.id.sensexAbsChg);
            TextView txtSensexPerChg = (TextView) findViewById(R.id.sensexPerChg);

            if(indexCode == eIndices.SENSEX.value) {
                txtSensexTitle.setText(eIndices.SENSEX.name);
            }else if(indexCode == eIndices.USDINR.value) {
                txtSensexTitle.setText(eIndices.USDINR.name);
            }else if(indexCode == eIndices.NIFTYBANK.value) {
                txtSensexTitle.setText(eIndices.NIFTYBANK.name);
            }
            double sensexVal = structIndex.getIndexRate();
            txtSensexVal.setTextColor(getIndicesColor(txtSensexVal.getText().toString().trim(), sensexVal));
            txtSensexVal.setText("" + Formatter.formatter.format(sensexVal));

            double sensexAbsChg = structIndex.getAbsChg();
            double sensexPerChg = structIndex.getPerChg();
            if (sensexAbsChg >= 0) {
                txtSensexPerChg.setTextColor(ScreenColor.getGREEN(this));
                txtSensexAbsChg.setTextColor(ScreenColor.getGREEN(this));
            } else {
                txtSensexPerChg.setTextColor(ScreenColor.getRED(this));
                txtSensexAbsChg.setTextColor(ScreenColor.getRED(this));
            }
            txtSensexPerChg.setText("" + Formatter.formatter.format(sensexPerChg) + "%");
            txtSensexAbsChg.setText("" + Formatter.formatter.format(sensexAbsChg));
        }
    }

    private void refreshIndices(int indexCode) {
        try {

            LiteIndicesWatch structIndex = GlobalClass.mktDataHandler.getIndicesData(indexCode);
            if (structIndex != null) {
                double niftyVal, niftyAbsChg, niftyPerChg, sensexVal, sensexAbsChg, sensexPerChg;
                if (structIndex.token.getValue() == eIndices.NIFTY.value
                        && UserSession.getLoginDetailsModel().isClient()) {           //NIFTY

                    TextView txtNiftyVal = (TextView) findViewById(R.id.niftyVal);
                    TextView txtNiftyAbsChg = (TextView) findViewById(R.id.niftyAbsChg);
                    TextView txtNiftyPerChg = (TextView) findViewById(R.id.niftyPerChg);

                    niftyVal = structIndex.getIndexRate();
                    txtNiftyVal.setTextColor(getIndicesColor(txtNiftyVal.getText().toString().trim(), niftyVal));

                    txtNiftyVal.setText("" + Formatter.formatter.format(niftyVal));
                    niftyAbsChg = structIndex.getAbsChg();
                    niftyPerChg = structIndex.getPerChg();
                    if (niftyAbsChg >= 0) {
                        txtNiftyPerChg.setTextColor(ScreenColor.getGREEN(this));
                        txtNiftyAbsChg.setTextColor(ScreenColor.getGREEN(this));
                    } else {
                        txtNiftyPerChg.setTextColor(ScreenColor.getRED(this));
                        txtNiftyAbsChg.setTextColor(ScreenColor.getRED(this));
                    }

                    txtNiftyPerChg.setText("" + Formatter.formatter.format(niftyPerChg) + "%");
                    txtNiftyAbsChg.setText("" + Formatter.formatter.format(niftyAbsChg));
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in refreshIndices" + getClass().getName(), e);
        }
    }

//INDICES SWITCHING

    int indexOfIndices = 0;
   // private int _sensexVisibility = View.GONE;
    private Handler _switchingHandler;
    private Runnable _switchingRunnable = new Runnable() {
        @Override
        public void run() {
          //  _sensexVisibility = _sensexVisibility==View.VISIBLE? View.GONE : View.VISIBLE;
            try {
                SensexVisivility();
                if (PreferenceHandler.isIndicesSwitchingAvl()) {
                    ProcessIndicessSwitching();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };
    public void ProcessIndicessSwitching(){
        //RemoveIndicesSwitching();
        _switchingHandler = new Handler();
        _switchingHandler.postDelayed(_switchingRunnable,2000);
    }

    private void RemoveIndicesSwitching(){
        if (_switchingHandler!=null){
            _switchingHandler.removeCallbacks(_switchingRunnable);
            _switchingHandler = null;
        }
    }

    private void SensexVisivility(){
        indexOfIndices = indexOfIndices>2 ? 0:indexOfIndices;
        int indexCode = eIndices.SENSEX.value;
        if(indexOfIndices == 1){
            indexCode = eIndices.USDINR.value;
        }
        else if(indexOfIndices == 2){
            indexCode = eIndices.NIFTYBANK.value;
        }
        refreshInicesForTimer(indexCode);
        indexOfIndices++;
    }
    public void SensexVisivility(int indexCode){
        RemoveIndicesSwitching();
        refreshInicesForTimer(indexCode);
    }

    private int getIndicesColor(String prevTick, double indexVal) {

        if (getStringToDouble(prevTick) > indexVal) {
            return (ScreenColor.getRED(this));
        } else if (getStringToDouble(prevTick) < indexVal) {
            return (ScreenColor.getGREEN(this));
        } else {
            return (getResources().getColor(R.color.white));
        }
    }

    private double getStringToDouble(String str) {
        if (str.equalsIgnoreCase(""))
            return 0;
        else {
            str = str.replace(",", "");
            return Double.parseDouble(str);
        }
    }

    @Override
    public void onFontChange(int textStyle) {
        setTheme(textStyle);
    }

    //region [ Inner classes ]
    class HomeActivityUIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    final int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case INDICE_CODE_LITE_PRIME: {
                            final int indexCode = refreshBundle.getInt("indexCode");
                            if(indexCode == eIndices.NIFTY.value) {
                                refreshIndices(indexCode);
                            }
                            else if(!PreferenceHandler.isIndicesSwitchingAvl() && (indexCode == PreferenceHandler.getSelectedIndice())){
                                refreshInicesForTimer(indexCode);
                            }
                        }
                        break;
                        case ADST: {
                            final int indexCode = refreshBundle.getInt("indexCode");
                            refreshADST(indexCode);
                        }
                        break;
                        case NOTIFICATION:
                            GlobalClass.fragmentTransaction(NotificationFragment.newInstance(0), R.id.container_body, true, "");
                            break;
                        case MKT_READ:
                            break;
                        case NEW_GROUPLIST:
                            OpenFragmentOnAppearence();
                            break;
                        case CUSTOM_DIALOG:
                            final byte[] bData = refreshBundle.getByteArray(eForHandler.RESDATA.name);
                            openCustomDialog(bData);
                            break;
                        default:
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
    private void openCustomDialog(byte[] data){
        try {
            StructCustomDialog cDialog = new StructCustomDialog(data);
            GlobalClass.log(cDialog.toString());
            if ((cDialog.sl.getValue()>0) && (!cDialog.description.getValue().equalsIgnoreCase("") ||
                    !cDialog.imageLink.getValue().equalsIgnoreCase("")) ) {
                runOnUiThread(() -> {
                    GlobalClass.dismissdialog();
                    dismisProgress();
                    Dialog dialog = new CustomDialogScr(HomeActivity.this, cDialog);
                    //dialog.show();
                });
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static boolean FROM_NOTIFICATION_WHENPAUSE = false;

    private void OpenFragmentAfrerResume() {

        Fragment currentFragment = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
        //boolean fromNotification = getIntent().getBooleanExtra(StaticVariables.FROM_NOTIFICATION_WHENPAUSE,false);
        if (!(currentFragment instanceof NotificationFragment) && FROM_NOTIFICATION_WHENPAUSE) {
            FROM_NOTIFICATION_WHENPAUSE = false;
            GlobalClass.fragmentTransaction(NotificationFragment.newInstance(0),
                    R.id.container_body, false, eFragments.NOTIFICATION.name);
        }
    }


    private void OpenFragmentOnAppearence() {

        runOnUiThread(() -> {
            GlobalClass.dismissdialog();
            dismisProgress();
            if (GlobalClass.isFromNotificationClick) {
                GlobalClass.fragmentTransaction(NotificationFragment.newInstance(0), R.id.container_body, false, eFragments.NOTIFICATION.name);
            } else {
                GlobalClass.fragmentTransaction(new WatchFragment(), R.id.container_body, true, "");
            }
            GlobalClass.isFromNotificationClick = false;
            OpenActivateMargin();
            showHomeHelp();
        });
    }

    private void sendReqForCustomDialog(){
        if (UserSession.getClientResponse()!= null && UserSession.getClientResponse().isCustomDialogAvl.getValue()){
            SendDataToBCServer sendDataToRCServer = new SendDataToBCServer();
            sendDataToRCServer.sendCustomDialogReq();
        }
    }

    public void OpenActivateMargin(){

        if (UserSession.getClientResponse()!= null && UserSession.getClientResponse().isActivatemargin()){

            long currTime = (new Date().getTime())/1000;
            long saveTime = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.MARGINTRADIN_TIME.name, (long) 0);

            int dateDiff = Math.abs(DateUtil.compareDates(currTime,saveTime));
            GlobalClass.log("MarginTrading : " + currTime + " : " + saveTime + " : " + dateDiff);
            if(dateDiff > 7) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getLetestContext(), android.R.style.Theme_Translucent_NoTitleBar);
                builder.setCancelable(false);
                View aView = LayoutInflater.from(getLetestContext()).inflate(R.layout.margintrading_popup, null);
                builder.setView(aView);
                AlertDialog aadharDialog = builder.create();
                aadharDialog.show();
                final Button activateMargin = aView.findViewById(R.id.activateMargin);
                activateMargin.setOnClickListener(v -> {
                    aadharDialog.dismiss();
                    GlobalClass.fragmentTransaction(ActivateMarginFragment.newInstance(), R.id.container_body, true, "");
                });
                final Button btn = aView.findViewById(R.id.remindLater);
                btn.setOnClickListener(v -> {
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.MARGINTRADIN_TIME.name, currTime);
                    aadharDialog.dismiss();
                });
                aadharDialog.setOnKeyListener((dialog, keyCode, event) -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    return false; // pass on to be processed as normal
                });
                animateIt(btn);
                animateIt(activateMargin);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //interectionhandler.removeCallbacks(interectionRunnable);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public RadioButtons SELECTED_RADIO_BTN = RadioButtons.WATCH;

    public enum RadioButtons {
        WATCH, TRADE, OPTIONS, WEALTH, MOVER, SLBS , NSE, BSE, MCX, NCDEX
    }

    public void CheckRadioButton(RadioButtons radioButton) {
        if (radioButton != null) {
            try {
                SELECTED_RADIO_BTN = radioButton;
                homeRDgroup.setOnCheckedChangeListener(null);
                switch (SELECTED_RADIO_BTN) {
                    case TRADE:
                        tradeRDbutton.setChecked(true);
                        break;
                    case OPTIONS:
                        optionRDbutton.setChecked(true);
                        break;
                    case WEALTH:
                        mywealthRDbutton.setChecked(true);
                        break;
                    case MOVER:
                        moversRDbutton.setChecked(true);
                        break;
                    case SLBS:
                        slbsRDbutton.setChecked(true);
                        break;
                    case BSE:
                        bseRDbutton.setChecked(true);
                        break;
                    case NSE:
                        nseRDbutton.setChecked(true);
                        break;
                    case MCX:
                        mcxRDbutton.setChecked(true);
                        break;
                    case NCDEX:
                        ncdexRDbutton.setChecked(true);
                        break;
                    default:
                        watchRDbutton.setChecked(true);
                        break;
                }
                homeRDgroup.setOnCheckedChangeListener(this);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

    public int getReportSpinnerPosition(String itemName) {
        return reportspinnerAdapter.getPosition(itemName);
    }

    public void setSpinner(String name, boolean isRefreshListener) {
        try {
            if (reportspinnerAdapter != null && reportspinnerlist!=null) {
                if (reportspinnerlist.contains(name)){
                    int position = reportspinnerAdapter.getPosition(name);
                    if (!isRefreshListener) {
                        report_spinner.setSelection(position);
                        return;
                    }
                    report_spinner.setOnItemSelectedListener(null);
                    report_spinner.setSelection(position);
                    report_spinner.setOnItemSelectedListener(this);
                }
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void refreshButtonClick() {
        GlobalClass.showProgressDialog("Please wait...");
        ObjectHolder.handler = new Handler(Looper.getMainLooper());
        ObjectHolder.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isProgressNeeded = false;
                eReports ereports = eReports.fromString(report_spinner.getSelectedItem().toString());
                assert ereports != null;
                switch (ereports) {
                    case MARGINE:
                        isProgressNeeded = GlobalClass.getClsMarginHolding().sendMarginRequest(true, "2");
                        break;
                    case ORDERBOOK:
                        GlobalClass.getClsTradeBook().sendTradeBookRequest();
                        isProgressNeeded = GlobalClass.getClsOrderBook().sendOrderBookRequest();
                        break;
                    case TRADEBOOK:
                        isProgressNeeded = GlobalClass.getClsTradeBook().sendTradeBookRequest();
                        break;
                    case NET_POSITION:
                        isProgressNeeded = GlobalClass.getClsTradeBook().sendTradeBookRequest();
                        break;
                    case DERIVATIVE_NET_OBLIGATION:
                        isProgressNeeded = GlobalClass.getClsMarginHolding().sendHoldingRequest();
                        break;
                    case MARGINE_TRADE:
                        isProgressNeeded = GlobalClass.getClsMarginHolding().sendMarginTradeRequest();
                        break;
                    case HOLDINGE_QUITY:
                        isProgressNeeded = GlobalClass.getClsMarginHolding().sendHoldingRequest();
                        break;
                    default:
                        break;
                }
                if (!isProgressNeeded) {
                    GlobalClass.showToast(GlobalClass.latestContext, "Refresh will available in 10 seconds");
                }
                GlobalClass.dismissdialog();
            }
        }, 300);
        ObjectHolder.handler = null;
    }
    //endregion
}
