package fragments.research;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.WebViewLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import Structure.news.StructNotificationReq;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eMsgType;
import enums.eNewsType;
import utils.Constants;
import utils.DatePick;
import utils.DateUtil;
import utils.GlobalClass;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;

public class ResearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static Handler researchHandler;
    private static final String TAG = ResearchFragment.class.getSimpleName();
    private static final String SERVER_NOT_AVL = "Server not available. please try after sometime.";
    private HomeActivity homeActivity;
    private int _prevTextLength = 0;
    private int _currTextlength = 0;
    private int calenderClickId = 0;
    private EditText focusedEdittext;
   // private ResearchAdapter researchAdapter;
    private int SELECTED_TAB = 0;
    private boolean moreDataClicked = false;

    @BindView(R.id.research_tabs)
    TabLayout research_tabs;
    @BindView(R.id.mainBody)
    LinearLayout mainBody;
    @BindView(R.id.getMoreData)
    CardView getMoreData;
    @BindView(R.id.customSearch)
    LinearLayout customSearch;
    @BindView(R.id.getMoreDataLinear)
    LinearLayout getMoreDataLinear;
    @BindView(R.id.fetchDetails)
    TextView fetchDetails;
    @BindView(R.id.etFromDate)
    EditText etFromDate;
    @BindView(R.id.etTodate)
    EditText etTodate;
    @BindView(R.id.companyName)
    EditText companyName;
    @BindView(R.id.fromCalBtn)
    LinearLayout fromCalBtn;
    @BindView(R.id.toCalBtn)
    LinearLayout toCalBtn;

   /* @BindView(R.id.recyclerView)
    RecyclerView recyclerView;*/
  /*  @BindView(R.id.etFromDate)
    EditText etFromDate;
    @BindView(R.id.etTodate)
    EditText etTodate;
    @BindView(R.id.companyName)
    EditText companyName;
    @BindView(R.id.fromCalBtn)
    LinearLayout fromCalBtn;
    @BindView(R.id.toCalBtn)
    LinearLayout toCalBtn;
    @BindView(R.id.tabBody)
    LinearLayout tabBody;*/

    public static ResearchFragment newInstance() {
        return new ResearchFragment();
    }

    public ResearchFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = null;

            layout = inflater.inflate(R.layout.research_layout, container, false);
            ButterKnife.bind(this, layout);

            fetchDetails.setOnClickListener(onClick);
            getMoreData.setOnClickListener(onClick);
            focusedEdittext = etFromDate;
            fromCalBtn.setOnClickListener(onClick);
            toCalBtn.setOnClickListener(onClick);
            etFromDate.setOnFocusChangeListener(onFocusChange);
            etTodate.setOnFocusChangeListener(onFocusChange);
            etFromDate.addTextChangedListener(dobWatcher);
            etTodate.addTextChangedListener(dobWatcher);
            researchHandler = new ResearchHandler();
            setupViews();
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());


        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (researchHandler == null) {
            researchHandler = new ResearchHandler();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        researchHandler = null;
    }


    public void clickResearchCalender(View view) {
        calenderClickId = view.getId();
        DialogFragment newFragment = new DatePick(this,true);
        newFragment.show(homeActivity.getSupportFragmentManager(), "datePicker");
    }


    private void setupViews() {
      //  researchAdapter = new ResearchAdapter();
        //recyclerView.setAdapter(researchAdapter);
        research_tabs.addTab(research_tabs.newTab().setText(eNewsType.RESEARCH.name));
        research_tabs.addTab(research_tabs.newTab().setText(eNewsType.QTRLY_ANALISYS.name));
        research_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SELECTED_TAB = tab.getPosition();
                generateList();
                //researchAdapter.refreshAdapter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        requestResearchDetails(false);
    }

    /*@Override
    public void connected() {
        StaticMethods.dismissIosProgress();
    }


    @Override
    public void serverNotAvailable() {
        fetchDetails.setEnabled(true);
        StaticMethods.dismissIosProgress();
        StaticMethods.showMessageDialog(getContext(), SERVER_NOT_AVL, true);
    }
*/

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        int month = monthOfYear + 1;
        String monthString = month < 10 ? "0" + month : "" + month;
        String date = "" + dayString + "/" + (monthString) + "/" + year;
        switch (calenderClickId) {
            case R.id.toCalBtn:
                etTodate.setText(date);
                etFromDate.requestFocus();
                etTodate.setSelection(date.length());
                break;
            default:
                etFromDate.setText(date);
                etFromDate.requestFocus();
                etFromDate.setSelection(date.length());
                break;
        }
    }


    private class ResearchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                    switch (emessagecode) {
                        case RESEARCH_FETCH_SSO_LOGIN:
                            byte[] data = refreshBundle.getByteArray(eForHandler.RESDATA.name);
                            handleResearchResponse(data);
                            break;
                        default:
                            break;
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

    private ArrayList<StructResearch> researchList = new ArrayList<>();

    private void handleResearchResponse(byte[] data) {
        try{
           // fetchDetails.setEnabled(true);
            StructResearchResp srr = new StructResearchResp(data);
            researchList.addAll(Arrays.asList(srr.researchDetail));
            datewiseSorting();
            if (srr.DC.getValue()==1){
                StaticMethods.dismissIosProgress();
                homeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // tabBody.setVisibility(View.VISIBLE);
                      //  researchAdapter.refreshAdapter();
                        generateList();
                    }
                });
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void datewiseSorting() {
        Collections.sort(researchList,new Comparator<StructResearch>(){
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(StructResearch o1, StructResearch o2) {
                return Integer.compare(o2.msgTime.getValue(),o1.msgTime.getValue());
            }
        });
    }


    private TextWatcher dobWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            _prevTextLength = focusedEdittext.getText().length();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            _currTextlength = focusedEdittext.getText().length();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (_prevTextLength < _currTextlength) {
                if (_currTextlength == 2) {
                    focusedEdittext.append("/");
                } else if (_currTextlength == 3 && editable.charAt(2) != '/') {
                    String _tempText = editable.toString();
                    String addChar = "/" + _tempText.charAt(2);
                    String str = _tempText.substring(0, 2) + addChar;
                    focusedEdittext.setText(str);
                    focusedEdittext.setSelection(str.length());
                } else if (_currTextlength == 5) {
                    focusedEdittext.append("/");
                } else if (_currTextlength == 6 && editable.charAt(5) != '/') {
                    String _tempText = editable.toString();
                    String addChar = "/" + _tempText.charAt(5);
                    String str = _tempText.substring(0, 5) + addChar;
                    focusedEdittext.setText(str);
                    focusedEdittext.setSelection(str.length());
                }
            }

        }
    };


    private View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (view instanceof EditText) {
                focusedEdittext = (EditText) view;
            }
        }
    };

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fromCalBtn:
                case R.id.toCalBtn:
                    clickResearchCalender(view);
                    break;
                case R.id.fetchDetails:
                    if (getDateValidation())
                        customRequestResearchDetails();
                    break;

                case R.id.getMoreData:
                    requestResearchDetails(true);
                    moreDataClicked = true;
                    break;

                default:
                    break;
            }
        }
    };

    private void requestResearchDetails(boolean isOneYear) {
       // fetchDetails.setEnabled(false);
        researchList.clear();
      //  tabBody.setVisibility(View.GONE);
        StaticMethods.showIosProgress();
        StructNotificationReq structNotificationReq = new StructNotificationReq();
        structNotificationReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        structNotificationReq.msgType.setValue(eMsgType.RESEARCH_IDEA.value);

        int reqMonth = isOneYear? 12 : 4;
        int fromDate = (int) DateUtil.DToN(getDateByMonth(reqMonth), Constants.DDMMYYYY);
        structNotificationReq.fromMsgTime.setValue(fromDate);

        int toDate = (int) DateUtil.DToN(getDateByMonth(0), Constants.DDMMYYYY);
        structNotificationReq.toMsgTime.setValue(toDate);
        new SendDataToBCServer().sendResearchIdeaReq(structNotificationReq);
    }

    private void customRequestResearchDetails() {
        // fetchDetails.setEnabled(false);
        researchList.clear();
        //  tabBody.setVisibility(View.GONE);
        StaticMethods.showIosProgress();
        StructNotificationReq structNotificationReq = new StructNotificationReq();
        structNotificationReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        structNotificationReq.msgType.setValue(eMsgType.RESEARCH_IDEA.value);
        structNotificationReq.companyName.setValue(companyName.getText().toString());
        int fromDate = (int) DateUtil.DToN(etFromDate.getText().toString(), Constants.DDMMYYYY);
        structNotificationReq.fromMsgTime.setValue(fromDate);

        int toDate = (int) DateUtil.DToN(etTodate.getText().toString(), Constants.DDMMYYYY);
        structNotificationReq.toMsgTime.setValue(toDate);
        new SendDataToBCServer().sendResearchIdeaReq(structNotificationReq);
    }



    private SimpleDateFormat outputSdf = new SimpleDateFormat(Constants.DDMMYYYY);

    private String getDateByMonth(int month){
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1 * month);
            return outputSdf.format(calendar.getTime());
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "";
    }

    private final String INVALID_FROMDATE = "Please enter a valid From Date.";
    private final String INVALID_TODATE = "Please enter a valid To Date.";
    private final String INVALID_COMPANYNAME = "Company name is very small. Enter atleast 3 charecters";

    private boolean getDateValidation(){
        if (companyName.getText().length()>0 && companyName.getText().length()<3){
            StaticMethods.showMessageDialog(getContext(), INVALID_COMPANYNAME, false);
            return false;
        }
       if (companyName.getText().length()<1){
           if (etFromDate.getText().length()<10){
               StaticMethods.showMessageDialog(getContext(), INVALID_FROMDATE, false);
               return false;
           }
           if (etTodate.getText().length()<10){
               StaticMethods.showMessageDialog(getContext(), INVALID_TODATE, false);
               return false;
           }

       }
        return true;
    }

    private void generateList(){
      //  this.mList.clear();
        mainBody.removeAllViews();
        int newsType = eNewsType.RESEARCH.value;
        if (SELECTED_TAB==1){
            newsType = eNewsType.QTRLY_ANALISYS.value;
        }
        for (StructResearch sr : researchList){

            if (sr.msgType.getValue()==newsType){

                View view = getLayoutInflater().inflate(R.layout.list_research, mainBody, false);
                TextView cName = view.findViewById(R.id.cName);
                TextView date = view.findViewById(R.id.date);
                cName.setText(sr.company.getValue());
                String dt = DateUtil.NToD(sr.msgTime.getValue());
                date.setText(dt);
                final String url = sr.attachedMent.getValue();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tempUrl = url;
                        if (!tempUrl.startsWith("http://") && !tempUrl.startsWith("https://"))
                            tempUrl = "http://" + tempUrl;

                        try {
                            Intent webview = new Intent(GlobalClass.latestContext, WebViewLayout.class);
                            webview.putExtra("link", tempUrl);
                            startActivity(webview);
                        }catch (Exception e){
                            GlobalClass.onError("Error in "+getClass().getName(),e);
                        }
                    }
                });
                mainBody.addView(view);
            }
                //mList.add(sr);
        }

        if (moreDataClicked){
            customSearch.setVisibility(View.VISIBLE);
            getMoreDataLinear.setVisibility(View.GONE);
        }else {
            customSearch.setVisibility(View.GONE);
            getMoreDataLinear.setVisibility(View.VISIBLE);
        }

    }
}
