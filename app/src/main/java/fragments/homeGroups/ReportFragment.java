package fragments.homeGroups;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eForHandler;
import enums.eReports;
import fragments.reports.BracketNetpositionView;
import fragments.reports.ExchMsgView;
import fragments.reports.HoldingFOView;
import fragments.reports.HoldingequityView;
import fragments.reports.MarginTradedFundView;
import fragments.reports.MarginView;
import fragments.reports.MarginViewRC;
import fragments.reports.NetpositionView;
import fragments.reports.OrderbookView;
import fragments.reports.SLBMHoldingView;
import fragments.reports.TotalMessagesView;
import fragments.reports.TradebookView;
import utils.GlobalClass;
import utils.UserSession;

public class ReportFragment extends Fragment{
    private View mView;
    private LinearLayout reportFrame;
    private String spinnerItem = eReports.MARGINE.name;
    private HomeActivity homeActivity;

    public ReportFragment(){super();}
    public static ReportFragment newInstance(){
        return new ReportFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity)
            homeActivity = (HomeActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalClass.getClsMarginHolding().isReqAvlMT = true;
        GlobalClass.tradeLoginHandler = null;
        GlobalClass.reportHandler = new ReportHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppTheme);
        homeActivity.findViewById(R.id.home_relative).setVisibility(View.GONE);
        homeActivity.findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        homeActivity.findViewById(R.id.reportSpinnerLayout).setVisibility(View.VISIBLE);
        homeActivity.CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        if (mView == null){
            mView = inflater.inflate(R.layout.report_screen, container, false);
            initialization();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setSpinner(spinnerItem,true);
        if (GlobalClass.reportHandler == null) {
            GlobalClass.reportHandler = new ReportHandler();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.reportHandler = null;
        homeActivity.findViewById(R.id.reportSpinnerLayout).setVisibility(View.GONE);
    }

    private void initialization() {
        reportFrame = mView.findViewById(R.id.report_framelayout);
        try {
            spinnerItem = ((Spinner) homeActivity.findViewById(R.id.report_spinner)).getSelectedItem().toString();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        reloadData(spinnerItem);
    }

    private void reloadData(String itemName) {
        spinnerItem = itemName;
        LinearLayout reportsView = null;
        eReports ereports = eReports.fromString(itemName);
        GlobalClass.setUIHandlerToNull();
        switch (ereports){
            case MARGINE:
                if(UserSession.getClientResponse() != null) {
                    if (UserSession.getClientResponse().isBOOM()) {
                        reportsView = new MarginViewRC(getContext());
                    } else {
                        reportsView = new MarginView(getContext());
                    }
                }else {
                    reportsView = new MarginView(getContext());
                }
                break;
            case MARGINE_TRADE:
                reportsView = new MarginTradedFundView(getContext());
                break;
            case ORDERBOOK:
                reportsView = new OrderbookView(getContext());
                break;
            /*case BRACKET_ORDERBOOK:
                reportsView = new BracketOrderbookView(getContext());
                break;*/
            case TRADEBOOK:
                reportsView = new TradebookView(getContext());
                break;
            case NET_POSITION:
                reportsView = new NetpositionView(getContext());
                break;
            case BRACKET_POSITIONBOOK:
                reportsView = new BracketNetpositionView(getContext());
                break;
            case HOLDINGE_QUITY:
                GlobalClass.getClsMarginHolding().isReqAvlHolding = true;
                reportsView = new HoldingequityView(getContext());
                break;
            case DERIVATIVE_NET_OBLIGATION:
                GlobalClass.getClsMarginHolding().isReqAvlHolding = true;
                reportsView = new HoldingFOView(getContext());
                break;
            case BOD_SECURITIES_LENT_REPORT:
                //GlobalClass.clsMarginHolding.isReqAvlHolding = true;
                reportsView = new SLBMHoldingView(getContext());
                break;
            /*case CHANGE_PIN:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.report_framelayout, sso_chnagePINFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;*/
            case TOTAL_MSG:
                reportsView = new TotalMessagesView(getContext());
                break;
            case EXCH_MSG:
                reportsView = new ExchMsgView(getContext(),reportFrame);
                break;
            default:
                break;
        }
        reportFrame.removeAllViews();
        reportFrame.addView(reportsView);
    }

    class ReportHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    String name = refreshBundle.getString(eForHandler.SPINNERNAME.name,eReports.MARGINE.name);
                    reloadData(name);
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}