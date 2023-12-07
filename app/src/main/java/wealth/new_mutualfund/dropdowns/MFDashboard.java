
package wealth.new_mutualfund.dropdowns;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.InvestmentIncompanies;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.Constants;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.QuickTransactionFragment;
import wealth.new_mutualfund.Structure.MFObjectHolder;
import wealth.new_mutualfund.investments.DIYFilter;
import wealth.new_mutualfund.investments.MissedSIPFragment;
import wealth.new_mutualfund.investments.VenturaTopPicksNewGFragment;
import wealth.new_mutualfund.newMF.ChooseSIPOptionNewFragment;
import wealth.new_mutualfund.newMF.CreateWealthFragment;
import wealth.new_mutualfund.newMF.FocusOnASectorFragment;
import wealth.new_mutualfund.newMF.InvestInTopCompaniesFragmentNew;
import wealth.new_mutualfund.newMF.ParkfundFragment;
import wealth.new_mutualfund.newMF.PreserveCapitalFragment;

import static utils.Formatter.toOneDecimalPercent;
import static utils.Formatter.toNoFracValue;

public class MFDashboard extends Fragment {


    private HomeActivity homeActivity;
    private JSONObject VideoObj;

    public static MFDashboard newInstance(){
        return new MFDashboard();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @BindView(R.id.gainLossTitle)
    TextView gainLossTitle;
    @BindView(R.id.performingfunds)
    TextView performingfunds;
    @BindView(R.id.explorefunds)
    TextView explorefunds;
    @BindView(R.id.quicktransact)
    TextView quicktransact;
    @BindView(R.id.performingfunds_new)
    TextView performingfunds_new;
    @BindView(R.id.quicktransact_new)
    TextView quicktransact_new;
    @BindView(R.id.missed_sip_button)
    TextView next_sips_due_btn;
    @BindView(R.id.investmentval)
    TextView investmentval;
    @BindView(R.id.currentval)
    TextView currentval;
    @BindView(R.id.gainLossval)
    TextView gainLossval;
    /* @BindView(R.id.xirrval)
     TextView xirrval;*/
    @BindView(R.id.equityper)
    TextView equityper;
    @BindView(R.id.debtper)
    TextView debtper;
    @BindView(R.id.otherper)
    TextView otherper;
    @BindView(R.id.tv_clientname)
    TextView tv_clientname;
    @BindView(R.id.equityLinear)
    LinearLayout equityLinear;
    @BindView(R.id.debtLinear)
    LinearLayout debtLinear;
    @BindView(R.id.otherLinear)
    LinearLayout otherLinear;
    /*@BindView(R.id.ll_investment_zero)
    LinearLayout ll_investment_zero;*/
    @BindView(R.id.new_clientview)
    LinearLayout new_clientview;
    @BindView(R.id.exsistingclient_View)
    LinearLayout exsistingclient_View;

    @BindView(R.id.ll_create_wealth)
    LinearLayout ll_create_wealth;
    @BindView(R.id.ll_create_wealth_e)
    LinearLayout ll_create_wealth_e;
    @BindView(R.id.ll_preserve_capital)
    LinearLayout ll_preserve_capital;
    @BindView(R.id.ll_preserve_capital_e)
    LinearLayout ll_preserve_capital_e;
    @BindView(R.id.ll_save_tax)
    LinearLayout ll_save_tax;
    @BindView(R.id.ll_save_tax_e)
    LinearLayout ll_save_tax_e;
    @BindView(R.id.ll_park_fund)
    LinearLayout ll_park_fund;
    @BindView(R.id.ll_park_fund_e)
    LinearLayout ll_park_fund_e;
    @BindView(R.id.ll_invest_overseas)
    LinearLayout ll_invest_overseas;
    @BindView(R.id.ll_invest_overseas_e)
    LinearLayout ll_invest_overseas_e;
    @BindView(R.id.ll_focus_sector)
    LinearLayout ll_focus_sector;
    @BindView(R.id.ll_focus_sector_e)
    LinearLayout ll_focus_sector_e;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.mf_dashboard,container,false);
        ButterKnife.bind(this,mView);
        setGainLossTitle();
        performingfunds_new.setOnClickListener(onClick);
        quicktransact_new.setOnClickListener(onClick);
        if(VenturaServerConnect.mfClientType == eMFClientType.MFD_ONBOARD) {
            performingfunds.setVisibility(View.GONE);
            explorefunds.setVisibility(View.GONE);
            quicktransact.setVisibility(View.GONE);
        }else{
            tv_clientname.setText(UserSession.getLoginDetailsModel().getClientName());
            performingfunds.setOnClickListener(onClick);
            explorefunds.setOnClickListener(onClick);
            quicktransact.setOnClickListener(onClick);
            next_sips_due_btn.setOnClickListener(onClick);
            ll_create_wealth.setOnClickListener(onClick);
            ll_create_wealth_e.setOnClickListener(onClick);
            ll_preserve_capital.setOnClickListener(onClick);
            ll_preserve_capital_e.setOnClickListener(onClick);
            ll_save_tax.setOnClickListener(onClick);
            ll_park_fund_e.setOnClickListener(onClick);
            ll_save_tax_e.setOnClickListener(onClick);
            ll_park_fund.setOnClickListener(onClick);
            ll_invest_overseas.setOnClickListener(onClick);
            ll_invest_overseas_e.setOnClickListener(onClick);
            ll_focus_sector.setOnClickListener(onClick);
            ll_focus_sector_e.setOnClickListener(onClick);
        }
        if(VenturaServerConnect.mfClientType == eMFClientType.NONE){
            new DashboardReq(eMessageCodeWealth.CLIENT_SESSION.value).execute();
        }else{
            new DashboardReq(eMessageCodeWealth.DASHBOARD.value).execute();
        }
        return mView;
    }

    private void setGainLossTitle() {
        try {
            SpannableString spannable =new SpannableString("* Gain/Loss (\u20B9)");
            spannable.setSpan(
                    new ForegroundColorSpan(ScreenColor.getGREEN(getContext())),
                    2, 6,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannable.setSpan(
                    new ForegroundColorSpan(ScreenColor.getRED(getContext())),
                    7, 11,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            gainLossTitle.setText(spannable);
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
    public void openCreateWealth() {
        homeActivity.FragmentTransaction(CreateWealthFragment.newInstance(), R.id.container_body, true);
    }
    public void exploreFundsClicked() {
        //start new dev:: LIVE
        homeActivity.FragmentTransaction(DIYFilter.newInstance(), R.id.mfBody, true);
        //homeActivity.FragmentTransaction(VenturaDIYNewGFragment.newInstance(), R.id.container_body, true);
    }

    public void venturaPerformingFundsClicked() {
        //new development :: LIVE
        homeActivity.FragmentTransaction(VenturaTopPicksNewGFragment.newInstance(), R.id.container_body, true);

        //dividend: 15882, 43578
        //growth: 7885
        //homeActivity.FragmentTransaction(FactSheetPagerFragment.newInstance("15882"), R.id.container_body, true);
    }
    public void quickTransactionClicked() {
        homeActivity.FragmentTransaction(QuickTransactionFragment.newInstance(), R.id.container_body, true);
    }
    public void quickTransactionClicked_new() {
        homeActivity.FragmentTransaction(ChooseSIPOptionNewFragment.newInstance(), R.id.container_body, true);
    }

    public void nextSipsDue() {
        homeActivity.FragmentTransaction(MissedSIPFragment.newInstance(), R.id.container_body, true);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.performingfunds:
                case R.id.performingfunds_new:
                    venturaPerformingFundsClicked();
                    break;
                case R.id.explorefunds:
                    exploreFundsClicked();
                    break;
                case R.id.quicktransact:
                    quickTransactionClicked();
                    break;
                case R.id.missed_sip_button:
                    nextSipsDue();
                    break;
                case R.id.quicktransact_new:
                    quickTransactionClicked_new();
                    break;
                case R.id.ll_create_wealth:
                    openCreateWealth();
                    break;
                case R.id.ll_create_wealth_e:
                    openCreateWealth();
                    break;
                case R.id.ll_preserve_capital:
                    openpreserveCapital();
                    break;
                case R.id.ll_preserve_capital_e:
                    openpreserveCapital();
                    break;
                case R.id.ll_save_tax:
                    openSaveTax();
                    break;
                case R.id.ll_save_tax_e:
                    openSaveTax();
                    break;
                case R.id.ll_park_fund:
                    openparkfund();
                    break;
                case R.id.ll_park_fund_e:
                    openparkfund();
                    break;
                case R.id.ll_invest_overseas:
                    openInvestOverseas();
                    break;
                case R.id.ll_invest_overseas_e:
                    openInvestOverseas();
                    break;
                case R.id.ll_focus_sector:
                    openfocusonSector();
                    break;
                case R.id.ll_focus_sector_e:
                    openfocusonSector();
                    break;
            }
        }
    };

    private void openfocusonSector() {
        homeActivity.FragmentTransaction(FocusOnASectorFragment.newInstance(), R.id.container_body, true);
    }

    private void openInvestOverseas() {
        homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.FUNDOFFUNDS.name,"Fund of Funds"), R.id.container_body, true);

    }

    private void openparkfund() {
        homeActivity.FragmentTransaction(ParkfundFragment.newInstance(), R.id.container_body, true);

    }

    private void openSaveTax() {
        homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("taxsaving", InvestmentIncompanies.ELSS.name,"Save Tax"), R.id.container_body, true);
    }

    private void openpreserveCapital() {
        homeActivity.FragmentTransaction(PreserveCapitalFragment.newInstance(), R.id.container_body, true);


    }

    class DashboardReq extends AsyncTask<String, Void, String> {

        int msgCode;
        public DashboardReq(int mC){
            this.msgCode = mC;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if(msgCode == eMessageCodeWealth.CLIENT_SESSION.value){
                if(UserSession.getClientResponse().isNeedAccordLogin()) {
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                        VenturaServerConnect.closeSocket();
                        if(VenturaServerConnect.connectToWealthServer(true)) {
                            JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                            if (jsonData != null) {
                                return jsonData.toString();
                            }
                        }
                    } else {
                        return clientLoginResponse.charResMsg.getValue();
                    }
                }else{
                    if(VenturaServerConnect.connectToWealthServer(true)) {
                        JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    }
                }
            } else if(msgCode == eMessageCodeWealth.DASHBOARD.value) {
                JSONObject jsonData = (MFObjectHolder.dashBoard != null)?MFObjectHolder.dashBoard:VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.DASHBOARD.value);
                if (jsonData != null) {
                    MFObjectHolder.dashBoard = jsonData;
                    return jsonData.toString();
                }
            }else if(msgCode == eMessageCodeWealth.VIDEO_LINKS.value){
                try {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.VIDEO_LINKS.value,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        displayError(err);
                    } else {
                        if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                            String mfC = jsonData.getString("MFClientType");
                            VenturaServerConnect.mfClientID = jsonData.getString("MFBClientId");
                            VenturaServerConnect.mfBank = jsonData.optString("MFBank");
                            if(VenturaServerConnect.mfClientType == eMFClientType.NONE) {
                                if (mfC.equalsIgnoreCase(eMFClientType.MFI.name)) {
                                    VenturaServerConnect.mfClientType = eMFClientType.MFI;
                                } else {
                                    VenturaServerConnect.mfClientType = eMFClientType.MFD;
                                }
                            }
                            new DashboardReq(eMessageCodeWealth.DASHBOARD.value).execute();

                        } else if (msgCode == eMessageCodeWealth.DASHBOARD.value) {
                            displaData(jsonData);
                        }
                        else if (msgCode == eMessageCodeWealth.VIDEO_LINKS.value) {
                            GlobalClass.VideoURLS = new JSONObject();
                            GlobalClass.VideoURLS = jsonData;
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    if (s.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(s);
                    }
                }
            }
        }
    }


    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    private void displaData(JSONObject jsonData) {
        try {
            JSONArray _snapshopDataArr = jsonData.optJSONArray("SnapshotData");
            double invV=0,currV = 0, gainLossV =0, xirrV = 0;
            if (_snapshopDataArr.length()>0){
                JSONObject jdata = _snapshopDataArr.getJSONObject(0);
                if (jdata.has("PurchaseAmt")){
                    invV = jdata.optDouble("PurchaseAmt");
                }
                if (jdata.has("CurrentAmt")){
                    currV = jdata.optDouble("CurrentAmt");
                }
                if (jdata.has("NetGainLoss")){
                    gainLossV = jdata.optDouble("NetGainLoss");
                }
                if (jdata.has("XIRR")){
                    xirrV = jdata.optDouble("XIRR");
                }
            }
            if (invV>0){
                new_clientview.setVisibility(View.GONE);
                exsistingclient_View.setVisibility(View.VISIBLE);
//                ll_investment_zero.setVisibility(View.VISIBLE);

                investmentval.setText(toNoFracValue(invV));
                currentval.setText(toNoFracValue(currV));
                gainLossval.setText(toNoFracValue(gainLossV));
//                xirrval.setText(toOneDecimal(xirrV));

                if (gainLossV>=0){
                    gainLossval.setTextColor(ScreenColor.getGREEN(getContext()));
                }else {
                    gainLossval.setTextColor(ScreenColor.getRED(getContext()));
                }
                JSONArray _assetDataArr = jsonData.optJSONArray("AssetAllocationData");
                double _equity = 0,_debt = 0,_others = 0;
                for (int i = 0;i<_assetDataArr.length();i++) {
                    JSONObject jdata = _assetDataArr.getJSONObject(i);
                    String _type = jdata.optString("AssetType");
                    double _allocation = jdata.optDouble("Allocation");
                    if (_type.equalsIgnoreCase("Equity")) {
                        _equity = _allocation;
                    }else if (_type.equalsIgnoreCase("Liquid")){
                        _debt = _allocation;
                    }else {
                        _others = _others + _allocation;
                    }
                }

               /* mfDashboardProgress.setProgress((int) _equity);
                mfDashboardProgress.setSecondaryProgress((int)(_equity+_debt));*/

                equityper.setText(toOneDecimalPercent(_equity));
                debtper.setText(toOneDecimalPercent(_debt));
                otherper.setText(toOneDecimalPercent(_others));
            }else{
                new_clientview.setVisibility(View.VISIBLE);
                exsistingclient_View.setVisibility(View.GONE);
//                ll_investment_zero.setVisibility(View.GONE);
            }



//            if (_equity>0){
//                equityLinear.setVisibility(View.VISIBLE);
//                equityper.setText(ToOneDecimalPercent(_equity));
//            }else {
//                equityLinear.setVisibility(View.GONE);
//            }
//            if (_debt>0){
//                debtLinear.setVisibility(View.VISIBLE);
//                debtper.setText(ToOneDecimalPercent(_debt));
//            }else {
//                debtLinear.setVisibility(View.GONE);
//            }
//            if (_others>0){
//                otherLinear.setVisibility(View.VISIBLE);
//                otherper.setText(ToOneDecimalPercent(_others));
//            }else {
//                otherLinear.setVisibility(View.GONE);
//            }
        }
        catch (Exception ex){
            VenturaException.Print(ex);
        }

    }
}
