package wealth.new_mutualfund;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONObject;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFClientType;
import enums.eMF_HoldingReportFor;
import enums.eMessageCodeWealth;
import fragments.simplysave.SimplysaveFragment;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.dropdowns.MFHoldingReports;
import wealth.new_mutualfund.newMF.OneTimeFragmentNew;
import wealth.new_mutualfund.newMF.SIPEnterAmoutFragmentNew;

public class QuickTransactionFragment extends Fragment {
    private HomeActivity homeActivity;

    public static QuickTransactionFragment newInstance(){
        return new QuickTransactionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @BindView(R.id.purchase)
    FrameLayout purchase;
    @BindView(R.id.sip)
    FrameLayout sip;
    @BindView(R.id.topup)
    FrameLayout topup;
    @BindView(R.id.redeem)
    FrameLayout redeem;
    @BindView(R.id.swp)
    FrameLayout swp;
    @BindView(R.id.stp)
    FrameLayout stp;
    @BindView(R.id.switchTransaction)
    FrameLayout switchTransaction;
    @BindView(R.id.spread)
    FrameLayout spread;
    @BindView(R.id.parkEarn)
    FrameLayout parkEarn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
            ((Activity) getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        View mView = inflater.inflate(R.layout.quick_transaction,container,false);
        ButterKnife.bind(this,mView);
        purchase.setOnClickListener(onClick);
        sip.setOnClickListener(onClick);
        topup.setOnClickListener(onClick);
        redeem.setOnClickListener(onClick);
        swp.setOnClickListener(onClick);
        stp.setOnClickListener(onClick);
        switchTransaction.setOnClickListener(onClick);
        spread.setOnClickListener(onClick);
        parkEarn.setOnClickListener(onClick);


        if(VenturaServerConnect.mfClientType == eMFClientType.NONE){
            new QuickReq(eMessageCodeWealth.CLIENT_SESSION.value).execute();
        }
        return mView;
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.purchase:
                    homeActivity.FragmentTransaction(OneTimeFragmentNew.newInstance(), R.id.container_body, true);
                    break;
                case R.id.sip:
                    homeActivity.FragmentTransaction(SIPEnterAmoutFragmentNew.newInstance(),R.id.container_body,true);
                    //homeActivity.FragmentTransaction(SipFragment.newInstance(new JSONObject(), "QuickTransact"), R.id.mfBody, true);
//                    homeActivity.FragmentTransaction(QuickTransSipFragment.newInstance(new JSONObject(), "QuickTransact"), R.id.container_body, true);
                    break;
                case R.id.topup:
                    //homeActivity.FragmentTransaction(TopupFragment.newInstance(), R.id.mfBody, true);
                    homeActivity.FragmentTransaction(MFHoldingReports.newInstance(eMF_HoldingReportFor.TOP_UP.value), R.id.container_body, true);
                    break;
                case R.id.redeem:
                    //homeActivity.FragmentTransaction(RedeemptionFragment.newInstance(new JSONObject(),"Scheme name"), R.id.mfBody, true);
                    homeActivity.FragmentTransaction(MFHoldingReports.newInstance(eMF_HoldingReportFor.REDEMPTION.value), R.id.container_body, true);
                    break;
                case R.id.swp:
                    //homeActivity.FragmentTransaction(SwpFragment.newInstance(), R.id.mfBody, true);
                    break;
                case R.id.stp:
                    //homeActivity.FragmentTransaction(StpFragment.newInstance(), R.id.mfBody, true);
                    break;
                case R.id.switchTransaction:
                    //homeActivity.FragmentTransaction(SwitchFragment.newInstance(new JSONObject(),"SchemeName"), R.id.mfBody, true);
                    homeActivity.FragmentTransaction(MFHoldingReports.newInstance(eMF_HoldingReportFor.SWITCH.value), R.id.container_body, true);
                    break;
                case R.id.spread:
                    //homeActivity.FragmentTransaction(SpreadOrderFragment.newInstance(), R.id.mfBody, true);
                    break;
                case R.id.parkEarn:
                    homeActivity.FragmentTransaction(new SimplysaveFragment(), R.id.container_body, true);
                    break;
            }
        }
    };

    class QuickReq extends AsyncTask<String, Void, String> {

        int msgCode;
        public QuickReq(int mC){
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
                        displayError(err);
                    }
                    else {
                        if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                            String mfC = jsonData.getString("MFClientType");
                            VenturaServerConnect.mfClientID = jsonData.getString("MFBClientId");
                            if (mfC.equalsIgnoreCase(eMFClientType.MFI.name)) {
                                VenturaServerConnect.mfClientType = eMFClientType.MFI;
                            } else {
                                VenturaServerConnect.mfClientType = eMFClientType.MFD;
                            }

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

}
