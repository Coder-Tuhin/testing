package fragments.simplysave;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ventura.venturawealth.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Structure.Response.AuthRelated.ClientLoginResponse;
import Structure.simplysave.SimplysaveResp;
import Structure.simplysave.bankReq;
import butterknife.ButterKnife;
import butterknife.BindView;
import connection.Connect;
import connection.ConnectionProcess;
import connection.SendDataToSimplysaveServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eSocketClient;
import interfaces.OnAlertListener;
import utils.Constants;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import wealth.Dialogs;
import wealth.VenturaServerConnect;

/**
 * Created by XTREMSOFT on 05-Jun-2017.
 */
@SuppressLint("ValidFragment")
public class SimplysaveFragment extends Fragment implements ConnectionProcess, OnAlertListener,View.OnClickListener{

    public static Handler parkAndEarnHandler;

    private static final String SEND_REQ_TAG = "send_req_tag";

    @BindView(R.id.home_tabs)TabLayout home_tabs;
    @BindView(R.id.fragment_viewpager)ViewPager m_viewPager;

    public  SimplysaveFragment(){super();}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = null;
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);


            layout = inflater.inflate(R.layout.payment_screen, container, false);
            ButterKnife.bind(this, layout);
            setupViewPager(m_viewPager);
            GlobalClass.showProgressDialog("Please wait...");
            parkAndEarnHandler = new ParkAndEarnHandler();
            Connect.connect(GlobalClass.latestContext, this, eSocketClient.SIMPLYSAVE);

        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parkAndEarnHandler == null){
            parkAndEarnHandler = new ParkAndEarnHandler();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        parkAndEarnHandler = null;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SummaryFragment(),"SUMMARY");
        adapter.addFragment(new PassbookFragment(),"PASSBOOK");
        viewPager.setAdapter(adapter);
        home_tabs.setupWithViewPager(m_viewPager);
    }

    @Override
    public void connected() {
        GlobalClass.dismissdialog();
        gotoParkAndEarn();
    }


    private void gotoParkAndEarn(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetTaskFirstSimplySave().execute();
            }
        });

    }
    private void gotoParkAndEarnFinal(){
        if (TextUtils.isEmpty(UserSession.getLoginDetailsModel().getFolioNumber())) {
            new AlertBox(getContext(), "YES", "NO", "Would you like to Invest in" +
                    " Nippon India Liquid Fund?", SimplysaveFragment.this, SEND_REQ_TAG);
        } else {
            sendSummaryPassbookRequest();
        }
    }

    private void sendSummaryPassbookRequest() {
        requestViahandler(GlobalClass.summaryHandler);
        requestViahandler(GlobalClass.passbookHandler);
    }

    private void requestViahandler(Handler handler){
        try {
            if (handler != null) {
                Message msg = Message.obtain(handler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.SUMMARY_PASSBOOK.value);
                msg.setData(confMsgBundle);
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReqtoServer() {
        try{
            GlobalClass.showProgressDialog("Please wait...");
            bankReq bankreq = new bankReq();
            bankreq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            bankreq.follio.setValue(UserSession.getLoginDetailsModel().getMobileNo());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            bankreq.appVersion.setValue(pInfo.versionName);
            bankreq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            bankreq.authId.setValue(UserSession.getClientResponse().charAuthId.getValue());
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.follioReq(bankreq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void serverNotAvailable() {
        GlobalClass.dismissdialog();
        new AlertBox(getContext(),"Park and Earn server currently not available.",this,"");
    }

    @Override
    public void sensexNiftyCame() {

    }

    @Override
    public void onOk(String tag) {
        if (tag.equals(SEND_REQ_TAG)){
            sendReqtoServer();
        }else {
            GlobalClass.fragmentManager.popBackStackImmediate();
        }
    }


    @Override
    public void onCancel(String tag) {
        GlobalClass.fragmentManager.popBackStackImmediate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            default:
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> m_FragmentList = new ArrayList<>();
        private final List<String> m_FragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return m_FragmentList.get(position);
        }
        @Override
        public int getCount() {
            return m_FragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            m_FragmentList.add(fragment);
            m_FragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return m_FragmentTitleList.get(position);
        }
    }

    class ParkAndEarnHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case PARK_EARNREQUEST:
                                handleResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResponse(byte[] byteArray) {
        try {
            GlobalClass.dismissdialog();
            SimplysaveResp ssr = new SimplysaveResp(byteArray);

            JSONObject simplysaveResponse = new JSONObject(ssr.response.getValue());
            String folioNumber = simplysaveResponse.optString("folio");
            if (folioNumber.length()>=10){
                UserSession.getLoginDetailsModel().setFolioNumber(folioNumber);
                UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
                gotoParkAndEarn();
            }else {
                String msg = "";
                try {
                    msg = simplysaveResponse.optString("msg");
                    if(msg.equalsIgnoreCase("")){
                        msg = "Got an invalid Folionumber. Please try after sometime.";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    msg = "Got an invalid Folionumber. Please try after sometime.";
                }

                new AlertBox(getContext(),msg,this,"");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    class GetTaskFirstSimplySave extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialogS;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mDialogS = Dialogs.getProgressDialog(getActivity());
                mDialogS.setMessage("Please wait...");
                mDialogS.show();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if(UserSession.getClientResponse().isNeedAccordLogin()){
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    return clientLoginResponse.charResMsg.getValue();
                }
            } catch (Exception ie) {
                VenturaException.Print(ie);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mDialogS.dismiss();
                if(result.equalsIgnoreCase("")) {
                    gotoParkAndEarnFinal();
                }else{
                    if(result.toLowerCase().contains(Constants.WEALTH_ERR)){
                        GlobalClass.homeActivity.logoutAlert("Logout",Constants.LOGOUT_FOR_WEALTH,false);
                    }else {
                        GlobalClass.showAlertDialog(result);
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
}

