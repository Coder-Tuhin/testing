package fragments.homeGroups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import java.util.ArrayList;

import Structure.Response.BC.StructxMKTEventRes;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import butterknife.ButterKnife;
import butterknife.BindView;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.ePrefTAG;
import enums.eShowDepth;
import enums.eWatchs;
import handler.GroupDetail;
import interfaces.OnPopupListener;
import utils.Formatter;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import view.CustomPopupWindow;
import view.DualListView;
import view.Gridview;

/**
 * Created by XTREMSOFT on 11-Aug-2017.
 */
public class MoversFragment extends Fragment implements View.OnClickListener, OnPopupListener,
        AdapterView.OnItemSelectedListener {
    private DualListView m_duelListView;
    private Gridview m_gridview;
    private ArrayAdapter<String> m_grpAdapter;
    private GroupDetail m_groupDetail;
    private View layout;
    private int btnWidth = 0;
    private ArrayList<StructxMKTEventRes> eventList = new ArrayList<>();
    private String prevScrip = "";
    private boolean newEventStyle = true;
    private int spinnerPosition = 0;

    @BindView(R.id.watch_spinner)
    Spinner watch_spinner;
    @BindView(R.id.searchscript_button)
    ImageButton searchscript_button;
    @BindView(R.id.body)
    LinearLayout body;
    @BindView(R.id.listgrid_button)
    ImageButton listgrid_button;
    @BindView(R.id.event_setting)
    ImageButton event_setting;
    @BindView(R.id.event_linear)
    LinearLayout event_linear;

    public MoversFragment(){super();}

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppTheme);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        ((HomeActivity)getActivity()).CheckRadioButton(HomeActivity.RadioButtons.MOVER);
        if (layout != null) return layout;
        layout = inflater.inflate(R.layout.marketwatch_screen, container, false);
        ButterKnife.bind(this, layout);
        initialization();
        btnWidth = (int) getResources().getDimension(R.dimen.item_height);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.mktMoversUiHandler = new MktWatchHandler();
        m_groupDetail = GlobalClass.groupHandler.getMKtMoversGroup();
        if (m_groupDetail != null)refreshSpinner();
        newEventStyle = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.ENENTSTYLE.name,true);
        if (m_duelListView != null) {
            m_duelListView.notifiList();
            body.removeAllViews();
            body.addView(m_duelListView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //  VenturaApplication.getPreference().setSelectedGroup(watch_spinner.getSelectedItem().toString());
        GlobalClass.mktMoversUiHandler = null;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            new DisplayView().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.listgrid_button:
                GlobalClass.gridselection = 0;
                boolean tempIsList = !PreferenceHandler.isListView();
                PreferenceHandler.setListView(tempIsList);
                new DisplayView().execute();
                // displayView();
                break;
            case R.id.searchscript_button:
                new CustomPopupWindow(this).openSearchScripWindow();
                break;
            case R.id.event_setting:
                openEventSetting();
                break;
        }
    }


    private void openEventSetting() {
        // GlobalClass.fragmentTransaction(new SettingsFragment(this),R.id.container_body,true, eFragments.SETTINGS.name);
    }


    private void initialization() {
        m_grpAdapter = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item);
        m_grpAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        watch_spinner.setAdapter(m_grpAdapter);
        listgrid_button.setOnClickListener(this);
        event_setting.setOnClickListener(this);
        searchscript_button.setOnClickListener(this);
    }

    private void refreshSpinner() {
        try {
            ArrayList<String> itemList = m_groupDetail.getGroupNameList();
            if (itemList.size() > 0) {
                m_grpAdapter.clear();
                m_grpAdapter.addAll(itemList);
                m_grpAdapter.notifyDataSetChanged();
                if (watch_spinner.getOnItemSelectedListener() == null)
                    watch_spinner.setOnItemSelectedListener(MoversFragment.this);
                watch_spinner.setSelection(spinnerPosition);
            }else {
                //ToDo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
        spinnerPosition = position;
        String selectedItem = adapterView.getSelectedItem().toString();
         new DisplayView().execute();

    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }



    class DisplayView extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Please wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            ((Activity) GlobalClass.latestContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String grpName = watch_spinner.getSelectedItem().toString();
                        GlobalClass.groupHandler.setSelectedGrpName(grpName);
                        m_duelListView = null;
                        m_gridview = null;
                        body.removeAllViews();
                        GroupsRespDetails m_grpResDetail = m_groupDetail.getGrpDetailFromGrpName(grpName, eWatchs.MKTMOVERS);
                        if (m_grpResDetail.hm_grpTokenDetails.size() > 0) {
                            if (!PreferenceHandler.isListView()) {
                                if (m_gridview == null) {
                                    m_gridview = new Gridview(GlobalClass.latestContext, m_grpResDetail, null,
                                            eShowDepth.MKTWATCH, HomeActivity.RadioButtons.MOVER);
                                    body.addView(m_gridview);
                                    listgrid_button.setImageResource(R.mipmap.icon_listview);
                                }
                            } else {
                                if (m_duelListView == null) {
                                    m_duelListView = new DualListView(GlobalClass.latestContext, m_grpResDetail, eWatchs.MKTMOVERS,
                                            eShowDepth.MKTWATCH, HomeActivity.RadioButtons.MOVER);
                                    body.addView(m_duelListView);
                                    listgrid_button.setImageResource(R.mipmap.icon_gridview);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
        }
    }

    private void refreshMktWatch(eMessageCode emessagecode, int token) {
        if (PreferenceHandler.isListView() && m_duelListView != null) {
            m_duelListView.callRefreshData(emessagecode, token);
        } else if (!PreferenceHandler.isListView()&& m_gridview != null) {
            m_gridview.callRefreshData(emessagecode, token);
        }
    }


    @Override
    public void onPopupClose() {
    }



    class MktWatchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case STATIC_MW:
                        case LITE_MW:
                        case OpenInt:
                            refreshMktWatch(emessagecode, scripCode);
                            break;
                        case NEW_GROUPLIST:
                            refreshSpinner();
                            break;
                        case BROKERCANCELORDER_CASHINTRADEL_BCADDGROUP:
                            refreshSpinner();
                            break;
                        case NEW_GROUPDETAILS:
                            new DisplayView().execute();
                            break;
                        case TOP_TRADED:
                        case TOP_GAINER:
                        case TOP_LOOSER:
                            if((m_duelListView == null) || (m_gridview == null)) {
                                new DisplayView().execute();
                            } else{
                                reloadDataForMarketMovers();
                            }
                            break;
                        case DARTEVENT:
                            setEvent(new StructxMKTEventRes(refreshBundle.getByteArray(eForHandler.RESDATA.name)));
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                GlobalClass.dismissdialog();
            }
        }
    }


    private void reloadDataForMarketMovers(){
        try {
            String grpName = watch_spinner.getSelectedItem().toString();
            GroupsRespDetails m_grpResDetail = m_groupDetail.getGrpDetailFromGrpName(grpName, eWatchs.MKTMOVERS);
            if (m_grpResDetail.hm_grpTokenDetails.size() > 0) {
                if (!PreferenceHandler.isListView()) {
                    if (m_gridview != null) {
                        m_gridview.notifyDataSetChanged(m_grpResDetail);
                    }
                } else {
                    if (m_duelListView != null) {
                        m_duelListView.notifyDataSetChanged(m_grpResDetail);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEvent(StructxMKTEventRes structxMKTEventRes) {
        if (structxMKTEventRes == null) return;
        String scrip = structxMKTEventRes.scripName.getValue();
        if (!scrip.equals(prevScrip)) {
            prevScrip = scrip;
            if (newEventStyle) {
                if (eventList.size() > 2) {
                    eventList.remove(2);
                    eventList.add(2, eventList.get(1));
                    eventList.remove(1);
                    eventList.add(1, eventList.get(0));
                    eventList.remove(0);
                    eventList.add(0,structxMKTEventRes);

                } else {
                    eventList.add(structxMKTEventRes);
                }
                if (eventList.size() > 2) {
                    event_setting.setVisibility(View.VISIBLE);
                } else {
                    event_setting.setVisibility(View.GONE);
                }

            } else {
                event_setting.setVisibility(View.GONE);
                eventList.clear();
                eventList.add(structxMKTEventRes);
            }

            if (event_linear.getVisibility() != View.VISIBLE) {
                event_linear.setVisibility(View.VISIBLE);
            }

            event_linear.removeAllViews();

            for (StructxMKTEventRes structMktEvent : eventList) {
                View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.event_item, null);
                TextView event_scriptname = (TextView) view.findViewById(R.id.event_scriptname);
                final int scriptCode = structMktEvent.token.getValue();
                final String scriptName = structMktEvent.scripName.getValue();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                        groupsTokenDetails.scripCode.setValue(scriptCode);
                        groupsTokenDetails.scripName.setValue(scriptName);
                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                        grplist.add(groupsTokenDetails);
                        GlobalClass.openDepth(scriptCode, eShowDepth.MKTWATCH, grplist, null);
                    }
                });

                String finalEventType;
                int textColor;
                if (structMktEvent.event.getValue() == 5) {
                    finalEventType = "Year High";
                    textColor = getResources().getColor(R.color.green1);

                } else if (structMktEvent.event.getValue() == 6) {
                    finalEventType = "Year Low";
                    textColor = getResources().getColor(R.color.red);
                } else if (structMktEvent.event.getValue() == 7) {
                    finalEventType = "Day High";
                    textColor = getResources().getColor(R.color.green1);
                } else if (structMktEvent.event.getValue() == 8) {
                    finalEventType = "Day Low";
                    textColor = getResources().getColor(R.color.red);
                } else {
                    finalEventType = "Event : " + structMktEvent.event.getValue();
                    textColor = Color.WHITE;
                }
                event_scriptname.setText("" + structMktEvent.scripName.getValue());
                TextView event_lastrate = (TextView) view.findViewById(R.id.event_scriptrate);
                TextView event = (TextView) view.findViewById(R.id.event);

                event_lastrate.setText("" + Formatter.formatter.format(structMktEvent.lastRate.getValue()));
                event_lastrate.setTextColor(textColor);
                event.setText(finalEventType);
                event.setTextColor(textColor);
                GlobalClass.textColor = textColor;
                GlobalClass.finalEventType = finalEventType;
                GlobalClass.event_lastrate = "" + structMktEvent.lastRate.getValue();
                event_linear.addView(view);
                LinearLayout.LayoutParams btnLp = null;
                if (eventList.size() == 1) {
                    btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, btnWidth);
                } else if (eventList.size() == 3){
                    btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            btnWidth*2);
                } else {
                    btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                }
               // btnLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                event_linear.setLayoutParams(btnLp);
            }
        }
    }
}
