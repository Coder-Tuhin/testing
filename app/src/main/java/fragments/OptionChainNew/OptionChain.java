package fragments.OptionChainNew;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import Structure.Request.BC.StructOptionChainReqNew;
import Structure.Response.Group.GroupsRespDetails;
import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eShowDepth;
import enums.eWatchs;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import view.DualListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionChain extends Fragment implements OptionSelector{


    private View view;
    private ImageButton btnSettings;
    private TextView selectedSymbol;
    private LinearLayout mainOptionLinear;
    private DualListView m_duelListView;

    public OptionChain() {
        // Required empty public constructor
    }
    public static OptionChain newInstance(int sectionNumber) {

        OptionChain fragment = new OptionChain();
        /*try {
            Bundle args = new Bundle();
            args.putInt(ARG_PARAM1, sectionNumber);
            fragment.setArguments(args);
            // fragment.setSectionNo(sectionNumber);
        } catch (Exception e) {
            GlobalClass.onError("Error in ", e);
        }*/
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_option_chain, container, false);
        try {
            btnSettings = view.findViewById(R.id.search);
            selectedSymbol = view.findViewById(R.id.selectedSymbol);
            mainOptionLinear = view.findViewById(R.id.mainOptionLinear);
            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OptionChainSelectionView selectionView = new OptionChainSelectionView();
                    selectionView.openSelectionScreen(OptionChain.this);
                }
            });
            StructOptionChainReqNew optionChainSettings = VenturaApplication.getPreference().getOptionChainSettings();
            if(optionChainSettings != null){
                sendReqForOptionChain(optionChainSettings);
            }
            else{
                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        OptionChainSelectionView selectionView = new OptionChainSelectionView();
                        selectionView.openSelectionScreen(OptionChain.this);
                    }
                },1000);
            }
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.optionChainHandler = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.optionChainHandler = new UIHandler();
    }

    @Override
    public void onSubmit(StructOptionChainReqNew sectedOption) {
        //OptionChainSelectionView selection.
        sendReqForOptionChain(sectedOption);
    }

    private void sendReqForOptionChain(StructOptionChainReqNew sectedOption){
        try {
            selectedSymbol.setText(sectedOption.symbol.getValue());
            StructOptionChainReqNew optReq = new StructOptionChainReqNew();
            optReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            optReq.strikeNo.setValue(sectedOption.strikeNo.getValue());
            optReq.symbol.setValue(sectedOption.symbol.getValue());
            optReq.expNo.setValue(sectedOption.expNo.getValue());
            optReq.expiryType.setValue(sectedOption.expiryType.getValue());
            optReq.option.setValue(sectedOption.option.getValue());
            GlobalClass.optionChainHandler = null;
            GlobalClass.optionChainHandler = new UIHandler();
            new SendDataToBCServer().optionChainNewRequest(optReq);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class DisplayOptionView extends AsyncTask<String, Void, String> {
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
                        m_duelListView = null;

                        final GroupsRespDetails m_grpResDetail = GlobalClass.groupHandler.getOptionChainResp();
                        mainOptionLinear.removeAllViews();
                        if (m_grpResDetail.hm_grpTokenDetails.size() > 0) {
                            setView(m_grpResDetail);
                        }else {

                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    LayoutInflater inflater = LayoutInflater.from(GlobalClass.latestContext);
                                            //(LayoutInflater)GlobalClass.mainContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View childNodata = inflater.inflate(R.layout.nodata_available, null);
                                    mainOptionLinear.addView(childNodata);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            mainOptionLinear.getMeasuredHeight());
                                    childNodata.setLayoutParams(lp);
                                }
                            },2000);
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

        private void setView(GroupsRespDetails m_grpResDetail){
            if (m_duelListView == null) {
                m_duelListView = new DualListView(GlobalClass.latestContext, m_grpResDetail, eWatchs.MKTWATCH,
                        eShowDepth.MKTWATCH, HomeActivity.RadioButtons.WATCH);
                mainOptionLinear.addView(m_duelListView);
            }
        }
    }

    private void refreshMktWatch(eMessageCode _eMsgCode, int token) {
        if (m_duelListView != null) {
            m_duelListView.callRefreshData(_eMsgCode, token);
        }
    }

    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int _msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    eMessageCode _eMsgCode = eMessageCode.valueOf(_msgCode);
                    switch (_eMsgCode){
                        case ANALYTICS_OPTION_CHAIN_NEW:
                            new DisplayOptionView().execute();
                            break;
                        case LITE_MW:
                        case STATIC_MW:{
                            int scripCode = refreshBundle.getInt(eForHandler.SCRIPCODE.name);
                            refreshMktWatch(_eMsgCode, scripCode);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

}