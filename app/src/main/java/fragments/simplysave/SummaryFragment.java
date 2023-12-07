package fragments.simplysave;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import org.json.JSONException;
import org.json.JSONObject;

import Structure.simplysave.SimplysaveResp;
import Structure.simplysave.homeDetaiireq;
import butterknife.ButterKnife;
import butterknife.BindView;
import connection.SendDataToSimplysaveServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import models.InvestModel;
import utils.Formatter;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 05-Jun-2017.
 */
public class SummaryFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.invest)Button invest;
    @BindView(R.id.withdraw)Button withdraw;
    @BindView(R.id.currentInvestment)TextView currentInvestment;
    @BindView(R.id.marketValue)TextView marketValue;
    private InvestModel investModel;

    private View view;

    public SummaryFragment(){super();}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.summary_screen,container,false);
        ButterKnife.bind(this, view);
        invest.setOnClickListener(this);
        withdraw.setOnClickListener(this);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.summaryHandler = new SummaryHandler();
        if(investModel != null){
            currentInvestment.setText(investModel.invested);
            marketValue.setText(investModel.mktValue);
        }
    }

    private void requestSummary(){
        if (!TextUtils.isEmpty(UserSession.getLoginDetailsModel().getFolioNumber())){
            getSummary();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.summaryHandler = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.invest:
                assert investModel!= null;
                openFragment(new InvestFragment(investModel));
                break;
            case R.id.withdraw:
                assert investModel!= null;
                openFragment(new WithdrawFragment(investModel));
              //  int index = investModel.msg1.indexOf("<%CLICKHERE%>");
              //  String msg = getResources().getString(R.string.withdrawn_note);
              /*  if (index>0){
                 msg =  investModel.msg1.substring(0,index);
                }*/
/*
                showMessageOKCancel(msg,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openFragment(new WithdrawFragment(investModel));
                            }
                        });
*/

                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setTitle("Message")
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }




    private void openFragment(Fragment fragment) {
        if (investModel == null) return;
        GlobalClass.fragmentTransaction(fragment, R.id.container_body,true,"");
    }

    private void getSummary() {
        try {
            GlobalClass.showProgressDialog("Please wait...");
            homeDetaiireq homeDetaiireq = new homeDetaiireq();
            homeDetaiireq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            homeDetaiireq.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(VenturaApplication.getPackage(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            homeDetaiireq.appVersion.setValue(pInfo.versionName);
            homeDetaiireq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.sendHomeDetails(homeDetaiireq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class SummaryHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case SIMPLYSAVEHOME:
                                handleResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            case SUMMARY_PASSBOOK:
                                requestSummary();
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

    private void handleResponse(byte[] bytes) {
        GlobalClass.dismissdialog();
        SimplysaveResp ssr = new SimplysaveResp(bytes);
        String response = ssr.response.getValue();
        try {
            JSONObject jsonObj = new JSONObject(response);
            investModel = new InvestModel();

            double invetedValue = Double.parseDouble(jsonObj.optString("InvestedAmt"));// as per balaji on 29Jan2019
            // + Double.parseDouble(jsonObj.optString("InProcessAmt"));
            double marketValuet = Double.parseDouble(jsonObj.optString("FreeAmt"));//+ Double.parseDouble(jsonObj.optString("InProcessAmt"));
            if(invetedValue < 0){
                invetedValue = 0;
            }
            if(marketValuet < 0){
                marketValuet = 0;
            }

            investModel.invested = Formatter.toTwoDecimalValue(invetedValue); //+InProcessAmt as per Balaji Sir on 29Oct18
            investModel.mktValue = Formatter.toTwoDecimalValue(marketValuet);

            investModel.doubleInvestedValue = invetedValue;
            investModel.doubleMktValue = marketValuet;

            investModel.scheme = jsonObj.optString("SchemeDescription");
            investModel.Follio = jsonObj.optString("Folio");
            investModel.bankName = jsonObj.isNull("BankName")?"":jsonObj.optString("BankName","");
            investModel.minAmount = jsonObj.optString("MinAmt");
            investModel.minUnits = jsonObj.optString("MinUnits");
            investModel.allUnits = jsonObj.optString("Totalunits");
            investModel.msg1 = jsonObj.optString("Msg1");
            investModel.instawithdrableAmount = Formatter.toTwoDecimalValue(Double.parseDouble(jsonObj.optString("Insta_Amount")));
            currentInvestment.setText(investModel.invested);
            marketValue.setText(investModel.mktValue);
        } catch (JSONException e) {
            e.printStackTrace();
            GlobalClass.showToast(getContext(),"Please try after sometime");
        }
    }
}