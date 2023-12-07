package fragments.settingstab;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import enums.eConstant;
import enums.eLogType;
import enums.eWatchs;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by Goutam on 12-04-2017.
 */
public class ReaderFragment extends Fragment implements AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener {
    protected SwitchCompat switch_compat;

    protected Spinner timerspinner;
    protected String[] spn_list = {"1","2","5","8","10","15","20","25","30","35","40","45","50","55","60"};
    protected int intTimertime;
    String readThis="";
    int coutForSpeech = 0;
    private TextView selectGrp;

    public ReaderFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (UserSession.getLoginDetailsModel().isActiveUser()) {
            //if (GlobalClass.layout == null) {
                GlobalClass.layout = inflater.inflate(R.layout.driving_watch, container, false);
                switch_compat = (SwitchCompat) GlobalClass.layout.findViewById(R.id.switch_compat);
                timerspinner = (Spinner) GlobalClass.layout.findViewById(R.id.timerspinner);
                selectGrp = (TextView) GlobalClass.layout.findViewById(R.id.selectGrp);

                ArrayAdapter<String> spn_adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_item, spn_list);
                spn_adapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
                timerspinner.setAdapter(spn_adapter);
                timerspinner.setOnItemSelectedListener(this);
                switch_compat.setSwitchPadding(40);
                switch_compat.setOnCheckedChangeListener(this);

                intTimertime = VenturaApplication.getPreference().getSharedPrefFromTag("timerWatch", 2);
                timerspinner.setSelection(spn_adapter.getPosition(intTimertime + ""));
                initSwitch();
                final String grpName = VenturaApplication.getPreference().getSharedPrefFromTag(eConstant.READER_GRP.name, "Select Group");
                selectGrp.setText(grpName);

                selectGrp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectGroup();
                    }
                });

           // }
        }else {
            GlobalClass.layout = inflater.inflate(R.layout.deactive_account, container, false);
        }
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return GlobalClass.layout;
    }

    private void selectGroup() {
        LinearLayout view = new LinearLayout(GlobalClass.latestContext);
        view.setOrientation(LinearLayout.VERTICAL);
        HashMap<Long,GroupsRespDetails> hm_grpResDetails = GlobalClass.groupHandler.getUserDefineGroup().hm_grpResDetails;
        List<GroupsRespDetails> list = new ArrayList<>(hm_grpResDetails.values());
        for (final GroupsRespDetails grp : list){
            TextView tv = new TextView(GlobalClass.latestContext);
            tv.setText(grp.groupName.getValue());
            tv.setTextSize(15);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,80);
            lp.setMargins(10,0,0,0);
            tv.setLayoutParams(lp);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.CENTER_VERTICAL);

            //tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

            view.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VenturaApplication.getPreference().storeSharedPref(eConstant.READER_GRP.name,grp.groupName.getValue());
                    selectGrp.setText(grp.groupName.getValue());
                    if (alertDialog!= null) alertDialog.dismiss();
                    if (GlobalClass.isEnableSpeech) readerSpeechContent(0);
                }
            });
        }
        AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
        m_alertBuilder.setView(view);
        alertDialog = m_alertBuilder.create();
        alertDialog.show();
    }

    private void initSwitch() {
        if (GlobalClass.isEnableSpeech){
            switch_compat.setChecked(true);
        }else {
            switch_compat.setChecked(false);
        }
    }

    private void initTextToSpeech() {
        GlobalClass.textToSpeech = new TextToSpeech(GlobalClass.latestContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    GlobalClass.textToSpeech.setLanguage(Locale.ENGLISH);
                    GlobalClass.textToSpeech.setSpeechRate(0.7f);
                    GlobalClass.textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            coutForSpeech++;
                            readerSpeechContent(coutForSpeech);
                        }
                        @Override
                        public void onError(String utteranceId) {

                        }
                        @Override
                        public void onStart(String utteranceId) {

                        }
                    });
                    startTimer();

                } else {
                    GlobalClass.log("MainActivity", "Initilization Failed!");
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalClass.readerWatchUiHandler = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_compat:
                if (isChecked) {
                    GlobalClass.isEnableSpeech = true;
                    speechOn();
                } else{
                    GlobalClass.isEnableSpeech = false;
                    stopTimer();
                }
                break;
            default:
                break;
        }

    }

    public void speechOn() {
        initTextToSpeech();
    }

    private void startTimer() {
        GlobalClass.timer = new Timer();
        GlobalClass.handler = new Handler();
        GlobalClass.timerTask = new TimerTask() {
            public void run() {
                GlobalClass.handler.postDelayed(new Runnable() {
                    public void run() {
                        readerSpeechContent(0);
                    }
                },10);
            }
        };
        GlobalClass.timer.schedule(GlobalClass.timerTask,0,60*intTimertime*1000);
    }



    private void stopTimer() {
        try{
            if (GlobalClass.timer != null){
                GlobalClass.timer.cancel();
                GlobalClass.textToSpeech.stop();
                GlobalClass.textToSpeech.shutdown();
                GlobalClass.timerTask.cancel();
                GlobalClass.handler = null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private AlertDialog alertDialog;

    public void readerSpeechContent(int count){
        if(count == 0){
            coutForSpeech = 0;
        }
        final String grpName = VenturaApplication.getPreference().getSharedPrefFromTag(eConstant.READER_GRP.name,"");
        if (GlobalClass.groupHandler.isGroupExists(grpName)){
            GroupsRespDetails grpDetailFromGrpName = GlobalClass.groupHandler.getUserDefineGroup().getGrpDetailFromGrpName(grpName, eWatchs.MKTWATCH);
            if(grpDetailFromGrpName != null){
                ArrayList<GroupsTokenDetails> values = new ArrayList<>(grpDetailFromGrpName.hm_grpTokenDetails.values());
                if (values.size() <1)
                    GlobalClass.showToast(getContext(), "Please add scrip into "+ Constants.DRIVING_WATCH + " group from home screen");
                if(count < values.size()){
                    GroupsTokenDetails grpTokenDetail = values.get(count);
                    StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(grpTokenDetail.scripCode.getValue(),true);
                    String scripName = grpTokenDetail.getTextforReader();
                    String lastRate = mktWatch.getLastrateforReader();
                    readThis = scripName + "   " + lastRate;

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
                    GlobalClass.textToSpeech.speak(readThis, TextToSpeech.QUEUE_ADD, map);
                }
            }
        }else {
           GlobalClass.showToast(GlobalClass.latestContext,"No group to read");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        intTimertime = Integer.parseInt(timerspinner.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPause() {
        super.onPause();
        VenturaApplication.getPreference().storeSharedPref("timerWatch",Integer.parseInt(intTimertime+""));
    }


}
