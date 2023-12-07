package wealth.new_mutualfund;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMF_HoldingReportFor;
import utils.GlobalClass;
import utils.UserSession;
import wealth.new_mutualfund.dropdowns.MFCapitalGain;
import wealth.new_mutualfund.dropdowns.MFDashboard;
import wealth.new_mutualfund.dropdowns.MFDividenreportsum;
import wealth.new_mutualfund.dropdowns.MFHoldingReports;
import wealth.new_mutualfund.dropdowns.MFRunningSIP;
import wealth.new_mutualfund.dropdowns.MFSipMandate;

/**
 * Created by XTREMSOFT on 31-Mar-2018.
 */

public class MutualFundMenuNew extends Fragment implements AdapterView.OnItemSelectedListener {
    private HomeActivity homeActivity;
    private View mView;
    private int SELECTED_POSITION = 0;

    @BindView(R.id.mfSpinner)
    Spinner mfSpinner;
    @BindView(R.id.servicesLinear)
    LinearLayout servicesLinear;

    public static MutualFundMenuNew newInstance(){
        return new MutualFundMenuNew();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

        if (mView == null){
            mView = inflater.inflate(R.layout.new_mutualfund_screen,container,false);
            ButterKnife.bind(this,mView);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(homeActivity, R.layout.custom_spinner_item);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            spinnerAdapter.addAll(MutualFundMenus.getReports());
            mfSpinner.setAdapter(spinnerAdapter);
            mfSpinner.setOnItemSelectedListener(this);
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mfSpinner != null) {
            mfSpinner.setSelection(SELECTED_POSITION);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Fragment _tempFragment = null;
        switch (position){
            case 1:
                _tempFragment = MFHoldingReports.newInstance(eMF_HoldingReportFor.REPORT.value);
                break;
            case 2:
                _tempFragment = MFRunningSIP.newInstance();
                break;
            case 3:
                _tempFragment = MFCapitalGain.newInstance();
                break;
            case 4:
                _tempFragment = MFDividenreportsum.newInstance();
                break;
            /*case 5:
                _tempFragment = MFAssetAllocation.newInstance();
                break;
            case 5:
                _tempFragment = MFOrderBook.newInstance();
                break;*/
            case 5:
                _tempFragment = MFSipMandate.newInstance();
                break;
            default:
                _tempFragment = MFDashboard.newInstance();
                break;
        }
        homeActivity.FragmentTransaction(_tempFragment,R.id.mfBody,false);
        SELECTED_POSITION = position<0?0:position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private enum MutualFundMenus{
        DASHBOARD("Dashboard"),
        HOLDING_REPORT("Holding Report"),
        RUNNING_SIP("Running SIP"),
        CAPITAL_GL("Capital Gain/Loss"),
        DIVIDEND_SUMMARY("Dividend Summary"),
        //ASSETS_ALLOCATION("Asset Allocation"),
        //ORDER_BOOK("Order Book"),
        SIP_MANDATE("SIP Mandate");

        public String name;
        MutualFundMenus(String name) {
            this.name=name;
        }

        public static ArrayList<String> getReports(){
            ArrayList<String> mList = new ArrayList<>();
            for (MutualFundMenus b : MutualFundMenus.values()) {
                mList.add(b.name);
            }
            return mList;
        }
    }
}
