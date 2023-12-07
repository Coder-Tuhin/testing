package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedHashMap;

import Structure.Response.BC.StructScripAlertRows;
import Structure.Response.BC.StructScripRateAlert;
import adapters.AlertAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import models.AlertModel;
import models.ScripAlertModel;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 1/20/2017.
 */
public class AlertFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private AlertAdapter alertAdapter;
    private LinearLayoutManager linearLayoutManager ;

    @BindView(R.id.alert_recycler)RecyclerView alert_recycler;
    @BindView(R.id.scriptrate_title)TextView scriptrate_title;
    @BindView(R.id.linear)LinearLayout linear;

    public AlertFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

            GlobalClass.alertHandler = new AlertHandler();
            mView = inflater.inflate(R.layout.alert_screen, container, false);
            ButterKnife.bind(this, mView);
            handleAlertView();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.alertHandler = null;
    }

    private void handleAlertView(){

        final LinkedHashMap<Integer, ScripAlertModel> alertRate = VenturaApplication.getPreference().getAlertRate();
        linearLayoutManager = new LinearLayoutManager(GlobalClass.latestContext);
        alert_recycler.setLayoutManager(linearLayoutManager);
        alertAdapter = new AlertAdapter(getContext());
        alert_recycler.setAdapter(alertAdapter);
        if (alertRate.size()>0){
            scriptrate_title.setText("Scrip Rate Details :");
            linear.setVisibility(View.VISIBLE);
        }
        else{
            //send request for stored alert..
            requestForAlert();
        }
    }

    private void requestForAlert() {
        new SendDataToBCServer().getAlertSetting();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            default:
                break;
        }
    }
   private class AlertHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    byte[] data = refreshBundle.getByteArray(eForHandler.RESDATA.name);
                    eMessageCode eshgCode = eMessageCode.valueOf(msgCode);
                    GlobalClass.log("ALERTFRAG:: ","3 "+eshgCode);

                    switch (eshgCode) {
                        case GETSCRIPRATE_FROMALERT:
                            StructScripRateAlert scripRateAlert = new StructScripRateAlert(data);
                            if (scripRateAlert.noOfRecords.getValue()>0)
                            hanldeGetScripRateAlert(scripRateAlert);
                            break;
                        case DELETESCRIPT_FROMALERT:
                            if (VenturaApplication.getPreference().getAlertRate().size()<=0){
                                scriptrate_title.setText("No Scrip Rate Found.");
                                linear.setVisibility(View.GONE);
                            }
                            break;
                        case NOTIFICATION:
                            alertAdapter.refreshAdapter();
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
    }

    private void hanldeGetScripRateAlert(StructScripRateAlert scripRateAlert) {
        StructScripAlertRows[] st  = scripRateAlert.structAlertData;
        LinkedHashMap<Integer, ScripAlertModel> map = new LinkedHashMap<>();
        for (int i= 0;i<st.length;i++){
            StructScripAlertRows rows = st[i];
            ScripAlertModel scripAlertModel = map.get(rows.scripCode.getValue());
            if(scripAlertModel == null) {
                scripAlertModel = new ScripAlertModel(rows.scripCode.getValue());
            }
            AlertModel alertModel = new AlertModel();
            alertModel.setToken(rows.scripCode.getValue());
            alertModel.setScriptName(rows.scripName.getValue());
            alertModel.setTokenRate(rows.scripRate.getValue());
            alertModel.setCondition(rows.condition.getValue());
            alertModel.setAchive(rows.rateAchive.getValue());
            scripAlertModel.setValueForCondition(rows.condition.getValue(),alertModel);
            map.put(scripAlertModel.getScripCode(),scripAlertModel);
        }

        if(map.size() > 0) {
            VenturaApplication.getPreference().setAlertRate(map);
            alertAdapter.refreshAdapter();
            //handleAlertView();
        }
    }
}
