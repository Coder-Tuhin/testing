package fragments.settingstab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.List;

import Structure.Response.BC.GenotificationSettingRes;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eMsgType;
import models.CheckNotificationSetting;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 09-Nov-2017.
 */
public class AlertSetting extends Fragment implements View.OnClickListener{
    private View mView;
    private short event,cash,fno,research,news;
    private CheckNotificationSetting cns ;

    @BindView(R.id.event_yes)RadioButton event_yes;
    @BindView(R.id.event_no)RadioButton event_no;

    @BindView(R.id.cash_trade_yes)RadioButton cash_yes;
    @BindView(R.id.cash_trade_no)RadioButton cash_no;

    @BindView(R.id.fno_trade_yes)RadioButton fno_yes;
    @BindView(R.id.fno_trade_no)RadioButton fno_no;

    @BindView(R.id.research_yes)RadioButton research_yes;
    @BindView(R.id.research_no)RadioButton research_no;

    @BindView(R.id.news_yes)RadioButton news_yes;
    @BindView(R.id.news_no)RadioButton news_no;


    @BindView(R.id.cashChk)CheckBox cashChk;
    @BindView(R.id.fnoChk)CheckBox fnoChk;
    @BindView(R.id.eventChk)CheckBox eventChk;
    @BindView(R.id.researchChk)CheckBox researchChk;
    @BindView(R.id.marginChk)CheckBox marginChk;
    @BindView(R.id.scripChk)CheckBox scripChk;
    @BindView(R.id.newsChk)CheckBox newsChk;
    @BindView(R.id.tradingChk)CheckBox tradingChk;
    @BindView(R.id.ipoChk)CheckBox ipoChk;
    @BindView(R.id.bondChk)CheckBox bondChk;
    @BindView(R.id.sgbChk)CheckBox sgbChk;
    @BindView(R.id.nfoChk)CheckBox nfoChk;
    @BindView(R.id.fdChk)CheckBox fdChk;
    @BindView(R.id.npsChk)CheckBox npsChk;
    @BindView(R.id.mfChk)CheckBox mfChk;

    @BindView(R.id.othersChk)CheckBox othersChk;

    public AlertSetting(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            if (UserSession.getLoginDetailsModel().isActiveUser()) {
                mView = inflater.inflate(R.layout.alertsetting_screen, container, false);
                ButterKnife.bind(this, mView);
                init();
            }else {
                mView = inflater.inflate(R.layout.deactive_account, container, false);
            }

        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return mView;
    }

    private void init() {
        try {
            initroundRobbin();
            GlobalClass.alertSettingHandler = new AlertHandler();
            cns = VenturaApplication.getPreference().getNotificationSetting();
            if (cns == null){
                GlobalClass.log("ALERTFRAG:: ","1");
                requestForNotificationSettings();
            }else{
                GlobalClass.log("ALERTFRAG:: ","2");
                event = cns.event;
                cash = cns.cash;
                fno = cns.fno;
                research = cns.research;
                news = cns.news;
                if (event == 0){
                    event_no.setChecked(true);
                }else {
                    event_yes.setChecked(true);
                }
                if (cash == 0){
                    cash_no.setChecked(true);
                }else {
                    cash_yes.setChecked(true);
                }
                if (fno == 0){
                    fno_no.setChecked(true);
                }else{
                    fno_yes.setChecked(true);
                }
                if (research == 0){
                    research_no.setChecked(true);
                }else {
                    research_yes.setChecked(true);
                }
                if (news == 0){
                    news_no.setChecked(true);
                }else {
                    news_yes.setChecked(true);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initroundRobbin() {
        List<String> rr = VenturaApplication.getPreference().getRoundRobin();
        if (rr.contains(eMsgType.CASH_TRADE.name)) cashChk.setChecked(true);
        if (rr.contains(eMsgType.FNO_TRADE.name)) fnoChk.setChecked(true);
        if (rr.contains(eMsgType.EVENTS.name)) eventChk.setChecked(true);
        if (rr.contains(eMsgType.RESEARCH_CALL.name)) researchChk.setChecked(true);
        if (rr.contains(eMsgType.MARGIN_CALL.name)) marginChk.setChecked(true);
        if (rr.contains(eMsgType.SCRIP_RATE.name)) scripChk.setChecked(true);
        if (rr.contains(eMsgType.NEWS.name)) newsChk.setChecked(true);
        if (rr.contains(eMsgType.TRADING_CALL.name)) tradingChk.setChecked(true);
        if (rr.contains(eMsgType.IPO.name)) ipoChk.setChecked(true);
        if (rr.contains(eMsgType.BOND.name)) bondChk.setChecked(true);
        if (rr.contains(eMsgType.SGB.name)) sgbChk.setChecked(true);
        if (rr.contains(eMsgType.NFO.name)) nfoChk.setChecked(true);
        if (rr.contains(eMsgType.FD.name)) fdChk.setChecked(true);
        if (rr.contains(eMsgType.NPS.name)) npsChk.setChecked(true);
        if (rr.contains(eMsgType.MF.name)) mfChk.setChecked(true);
        if (rr.contains(eMsgType.OTHERS.name)) othersChk.setChecked(true);
    }

    private void setRoundRobbin(){
        List<String> rr = new ArrayList<>();
        if (cashChk.isChecked()) rr.add(eMsgType.CASH_TRADE.name);
        if (fnoChk.isChecked()) rr.add(eMsgType.FNO_TRADE.name);
        if (eventChk.isChecked()) rr.add(eMsgType.EVENTS.name);
        if (researchChk.isChecked()) rr.add(eMsgType.RESEARCH_CALL.name);
        if (marginChk.isChecked()) rr.add(eMsgType.MARGIN_CALL.name);
        if (scripChk.isChecked()) rr.add(eMsgType.SCRIP_RATE.name);
        if (newsChk.isChecked()) rr.add(eMsgType.NEWS.name);
        if (tradingChk.isChecked()) rr.add(eMsgType.TRADING_CALL.name);
        if (ipoChk.isChecked()) rr.add(eMsgType.IPO.name);
        if (bondChk.isChecked()) rr.add(eMsgType.BOND.name);
        if (sgbChk.isChecked()) rr.add(eMsgType.SGB.name);
        if (nfoChk.isChecked()) rr.add(eMsgType.NFO.name);
        if (fdChk.isChecked()) rr.add(eMsgType.FD.name);
        if (npsChk.isChecked()) rr.add(eMsgType.NPS.name);
        if (mfChk.isChecked()) rr.add(eMsgType.MF.name);

        if (othersChk.isChecked()) rr.add(eMsgType.OTHERS.name);
        VenturaApplication.getPreference().setRoundRobin(rr);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (UserSession.getLoginDetailsModel().isClient()){
                saveSetting();
                setRoundRobbin();
                if(GlobalClass.clsNewsHandler != null){
                    GlobalClass.clsNewsHandler.loadNewsData();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveSetting() {
        event = 0;
        fno = 0;
        cash = 0;
        research = 0;
        news = 0;
        if (event_yes.isChecked()){
            event = 1;
        }
        if (cash_yes.isChecked()){
            cash = 1;
        }
        if (fno_yes.isChecked()){
            fno = 1;
        }
        if (research_yes.isChecked()){
            research = 1;
        }
        if (news_yes.isChecked()){
            news = 1;
        }
        if (cns.isValueChange(event,cash,fno,research,news)){
            CheckNotificationSetting new_cnn = new CheckNotificationSetting(event,fno,cash,research,news);
            serverCall(new_cnn);
        }
        CheckNotificationSetting cns = new CheckNotificationSetting(event,fno,cash,research,news);
        VenturaApplication.getPreference().setNotificationSetting(cns);
        GlobalClass.alertSettingHandler = null;
    }
    private void requestForNotificationSettings() {
        new SendDataToBCServer().getNotificationSetting();
    }

  /*  private void requestForAlert() {
        new SendDataToBCServer().getAlertSetting();
    }
*/
    private void serverCall(CheckNotificationSetting cns) {
        new SendDataToBCServer().sendNotificationSetting(cns);
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
                       /* case SETNOTIFICATION_SETTING:
                            handleSetNotification();
                            break;*/
                        case GETNOTIFICATION_SETTING:
                            handleGetNotification(new GenotificationSettingRes(data));
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void handleGetNotification(GenotificationSettingRes sns) {
        event = sns.event.getValue();
        cash = sns.cash.getValue();
        fno = sns.fno.getValue();
        research = sns.research.getValue();
        news = sns.news.getValue();
        if (event == 0){
            event_no.setChecked(true);
        }else {
            event_yes.setChecked(true);
        }
        if (cash == 0){
            cash_no.setChecked(true);
        }else {
            cash_yes.setChecked(true);
        }
        if (fno == 0){
            fno_no.setChecked(true);
        }else{
            fno_yes.setChecked(true);
        }
        if (research == 0){
            research_no.setChecked(true);
        }else {
            research_yes.setChecked(true);
        }
        if (news == 0){
            news_no.setChecked(true);
        }else {
            news_yes.setChecked(true);
        }
        cns = new CheckNotificationSetting(event,fno,cash,research,news);
        VenturaApplication.getPreference().setNotificationSetting(cns);
    }
}
