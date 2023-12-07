package wealth.new_mutualfund.newMF;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import enums.eOptionMF;
import utils.Formatter;
import utils.GlobalClass;
import utils.MySpannable;
import utils.StaticMessages;
import utils.StaticMethods;
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.bondstructure.StructBondAvailableBalance;
import wealth.new_mutualfund.menus.modelclass.SIPModel;

public class SIPEnterAmoutFragment extends Fragment {
    String SchemeName = "";
    public String formScreen = "";
    JSONObject jdata1;
    public static SIPEnterAmoutFragment newInstance(String schemecode,String SchemeName) {
        SIPEnterAmoutFragment itc =  new SIPEnterAmoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1,schemecode);
        bundle.putString(StaticVariables.ARG_2,SchemeName);
        itc.setArguments(bundle);
        return itc;
    }
    public static SIPEnterAmoutFragment newInstance(JSONObject jData, String fromScreen){
        SIPEnterAmoutFragment f = new SIPEnterAmoutFragment();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        args.putString(eMFJsonTag.FORMSCR.name, fromScreen);
        f.setArguments(args);
        return f;
    }



    private HomeActivity homeActivity;
    private Double avl_Balance;
    private SIPModel sipModel;
    private JSONObject jschdetailData;
    private JSONObject jHoldingData;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    public static SIPEnterAmoutFragment newInstance(){
        return new SIPEnterAmoutFragment();
    }
    String schemecode;
    @BindView(R.id.tv_2000)
    TextView tv_2000;
    @BindView(R.id.tv_5000)
    TextView tv_5000;
    @BindView(R.id.tv_10000)
    TextView tv_10000;
    @BindView(R.id.tv_amount)
    EditText amountEditText;
    @BindView(R.id.navvalue)
    TextView navvalue;
    @BindView(R.id.minimum_amount)
    TextView minimum_amount;
    @BindView(R.id.multiply)
    TextView multiply;
    @BindView(R.id.exitloadvalue)
    TextView exitloadvalue;
    @BindView(R.id.startbtn)
    Button startbtn;
    @BindView(R.id.SchemeNameTV)
    TextView SchemeNameTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.sip_enteramount_screen,container,false);
        ButterKnife.bind(this,mView);
        Bundle bundle = getArguments();
        schemecode = bundle.getString(StaticVariables.ARG_1);
        SchemeName = bundle.getString(StaticVariables.ARG_2);
        try {
            formScreen = bundle.getString(eMFJsonTag.FORMSCR.name);
            if(formScreen.equalsIgnoreCase("nfo")) {
                String jsonStr = bundle.getString(eMFJsonTag.JDATA.name, "");
                jdata1 = new JSONObject(jsonStr);
                SchemeName = jdata1.optString("SchemeName");
                schemecode = jdata1.optString("SchemeCode");
            }
        } catch (Exception e) {
            e.printStackTrace();
            formScreen = "";
        }



        SchemeNameTV.setText(SchemeName);
        tv_2000.setOnClickListener(_onclick);
        tv_5000.setOnClickListener(_onclick);
        tv_10000.setOnClickListener(_onclick);
        startbtn.setOnClickListener(_onclick);
        return  mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getschemedata();
    }

    private void getschemedata(){

        new SendSIPReq(eMessageCodeWealth.GET_SCHEME_DETAILS.value).execute();
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_2000:
                    amountEditText.setText("2000");
                    tv_2000.setActivated(true);
                    tv_5000.setActivated(false);
                    tv_10000.setActivated(false);
                    break;
                case R.id.tv_5000:
                    amountEditText.setText("5000");
                    tv_5000.setActivated(true);
                    tv_2000.setActivated(false);
                    tv_10000.setActivated(false);
                    break;
                case R.id.tv_10000:
                    amountEditText.setText("10000");
                    tv_10000.setActivated(true);
                    tv_5000.setActivated(false);
                    tv_2000.setActivated(false);
                    break;
                case R.id.startbtn:
                    OpenAxisChipfundFragment(amountEditText.getText().toString().trim());

            }

        }
    };

    public void setTexttoEdittext(TextView tv){
        amountEditText.setText(tv.getText().toString().trim());
        tv.setActivated(true);
    }
    private  void OpenAxisChipfundFragment(String amount){
        if(validateData()){
            if(formScreen.equalsIgnoreCase("nfo")){
                homeActivity.FragmentTransaction(SIPSelectStartDateFragment.newInstance(schemecode,amount,SchemeName,jdata1, eFormScr.NFO.name), R.id.container_body, true);

            }else {
                homeActivity.FragmentTransaction(SIPSelectStartDateFragment.newInstance(schemecode,amount,SchemeName), R.id.container_body, true);

            }
        }
    }
    public boolean validateData(){
        try {
            if (sipModel ==null){
                homeActivity.showMsgDialog(StaticMessages.SOMETHING_WRONG);
                return false;
            }
            if (TextUtils.isEmpty(amountEditText.getText())){
                String ENTER_AMT = "Please enter amount to start a SIP.";
                homeActivity.showMsgDialog(ENTER_AMT);
                return false;
            }
            double amount = StaticMethods.StringToDouble(amountEditText.getText().toString());
            if (amount<_minimumAmt || (amount*100)%(_multipleOf*100) != 0){
                String MINIMUM_MULTIPLE_OF = "Entered amount should be minimum "+_minimumAmt+
                        ". And multiple of "+_multipleOf;
                homeActivity.showMsgDialog(MINIMUM_MULTIPLE_OF);
                return false;
            }
            return true;
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return false;
    }


    class SendSIPReq extends AsyncTask<String, Void, String> {
        private int msgCode;
        private StructBondAvailableBalance avlBalance;
        SendSIPReq(int mCode){
            this.msgCode = mCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }
        @Override
        protected String doInBackground(String... strings) {
            try {

                if(msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    if(formScreen.equalsIgnoreCase("nfo")){
                        jdata.put(eMFJsonTag.SCHEMECODE.name,jdata1.optString("SchemeCode"));
                    }else {
                        jdata.put(eMFJsonTag.SCHEMECODE.name, schemecode);
                    }
                    jdata.put(eMFJsonTag.OPTION.name, eOptionMF.SIP.name);
                    if(formScreen.equalsIgnoreCase("nfo")){
                        jdata.put(eMFJsonTag.FOLIONO.name,jdata1.optString("FolioNo"));
                        jdata.put(eMFJsonTag.entrymode.name,"NFO");
                    }else {
                        jdata.put(eMFJsonTag.FOLIONO.name, "");
                    }
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.GET_SCHEME_DETAILS.value, jdata);
                    if (jsonData != null) return jsonData.toString();
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
//            avl_balance.setText(GlobalClass.getFormattedAmountString(avlBalance.getAvailableBalance()));
            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        GlobalClass.showAlertDialog(err);
                    }else {
                        if (msgCode == eMessageCodeWealth.GET_SCHEME_DETAILS.value) {
                            displaData(jsonData);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private double _minimumAmt = -1;
    private double _multipleOf = 0;

    private void displaData(JSONObject jsonData) {
        try {
            GlobalClass.log("GetSchedetail : " + jsonData.toString());
            JSONArray jschdetailDataArr = jsonData.getJSONArray("schemedetailsdata");
            if(jschdetailDataArr.length() > 0) {
                jschdetailData = jschdetailDataArr.getJSONObject(0);
                displayschemedata();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void displayschemedata(){

        try {
            //if(efrequency == eMFFrequency.MONTHLY) {
            navvalue.setText(jschdetailData.getString("CurrNAV") + "(" + jschdetailData.getString("NAVDate") + ")");
            exitloadvalue.setText(jschdetailData.getString("ExitLoad"));
            try {
                String mystring=new String("Read More");
                SpannableString content = new SpannableString(mystring);
                content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                makeTextViewResizable(exitloadvalue, 1, content, true);

            }catch (Exception e){
                e.printStackTrace();
            }

            String multipleOf  = jschdetailData.getString("MSIPMultAmt");
            String minimumAmt = jschdetailData.getString("MSIPMinInstallmentAmt");

            _minimumAmt = StaticMethods.StringToDouble(minimumAmt);
            _multipleOf = StaticMethods.StringToDouble(multipleOf);

            multiply.setText(multipleOf);
            minimum_amount.setText(Formatter.DecimalLessIncludingComma(minimumAmt));

            //  schemecode = jschdetailData.getString("SchemeCode");
            sipModel = new SIPModel();
            sipModel.minAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMinInstallmentAmt"));
            sipModel.maxAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMaxInstallmentAmt"));
            sipModel.multipleAmt = Formatter.stringToDouble(jschdetailData.getString("MSIPMultAmt"));

            sipModel.minPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMinInstallmentNumbers"));
            sipModel.maxPeriod = Formatter.stringToInt(jschdetailData.getString("MSIPMaximumInstallmentNumbers"));
            String sipdays = jschdetailData.getString("MSIPDates");
            sipModel.populateDate(sipdays);
            //  initDaySpinner();
            //period.setText(sipModel.minPeriod+"");
            //period.setHint("");//("Range "+sipModel.minPeriod + " - " +sipModel.maxPeriod);
            //}
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public  void makeTextViewResizable(final TextView tv, final int maxLine, final SpannableString expandText, final boolean viewMore) {
        try {

            if (tv.getTag() == null) {
                tv.setTag(tv.getText());
            }
            ViewTreeObserver vto = tv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {


                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (maxLine == 0) {
                        int lineEndIndex = tv.getLayout().getLineEnd(0);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        try {
                            int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                            String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                            tv.setText(text);
                            tv.setMovementMethod(LinkMovementMethod.getInstance());
                            tv.setText(
                                    addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                            viewMore), TextView.BufferType.SPANNABLE);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                     final int maxLine, final SpannableString spanableText, final boolean viewMore) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        try {


            String str = strSpanned.toString();

            if (str.contains(spanableText)) {


                ssb.setSpan(new MySpannable(false){
                    @Override
                    public void onClick(View widget) {
                        if (viewMore) {
                            tv.setLayoutParams(tv.getLayoutParams());
                            tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                            tv.invalidate();
                            String mystring=new String("Read Less");
                            SpannableString content = new SpannableString(mystring);
                            content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                            makeTextViewResizable(tv, -1,content, false);
                        } else {
                            tv.setLayoutParams(tv.getLayoutParams());
                            tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                            tv.invalidate();
                            String mystring=new String("Read More");
                            SpannableString content = new SpannableString(mystring);
                            content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                            makeTextViewResizable(tv, 1, content, true);
                        }
                    }
                }, str.indexOf(spanableText.toString()), str.indexOf(spanableText.toString()) + spanableText.length(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ssb;
    }
}