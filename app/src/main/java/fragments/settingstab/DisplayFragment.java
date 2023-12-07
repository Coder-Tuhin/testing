package fragments.settingstab;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import butterknife.ButterKnife;
import butterknife.BindView;
import enums.eConstant;
import enums.eIndices;
import enums.eLogType;
import enums.ePrefTAG;
import interfaces.OnFontChange;
import utils.Constants;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.SharedPref;
import utils.UserSession;

import static com.ventura.venturawealth.R.id.nse;
import static com.ventura.venturawealth.R.id.visiblelayout;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
@SuppressLint("ValidFragment")
public class DisplayFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,CompoundButton.OnCheckedChangeListener{
    @BindView(R.id.indices_setting_textview)TextView indices_setting_textview;
    @BindView(R.id.textsize_radiogroup)RadioGroup textsize_radiogroup;
    @BindView(R.id.small_radiobutton)RadioButton small_radiobutton;
    @BindView(R.id.medium_radiobutton)RadioButton medium_radiobutton;
    @BindView(R.id.large_radiobutton)RadioButton large_radiobutton;

    @BindView(R.id.ammount_radiogroup)RadioGroup ammount_radiogroup;
    @BindView(R.id.rs_radiobutton)RadioButton rs_radiobutton;
    @BindView(R.id.lacs_radiobutton)RadioButton lacs_radiobutton;
    @BindView(R.id.cr_radiobutton)RadioButton cr_radiobutton;

    @BindView(R.id.newTextview)TextView newTextview;
    @BindView(R.id.oldTextview)TextView oldTextview;

    @BindView(R.id.chg_rd)RadioGroup chg_rd;
    @BindView(R.id.abs_chg)RadioButton abs_chg;
    @BindView(R.id.normal_chg)RadioButton normal_chg;

    @BindView(R.id.depthactionwatch_radiogroup)RadioGroup depthactionwatch_radiogroup;
    @BindView(R.id.dawOn)RadioButton dawOn;
    @BindView(R.id.dawOff)RadioButton dawOff;

    @BindView(R.id.news_radiogroup)RadioGroup news_radiogroup;
    @BindView(R.id.newsOn)RadioButton newsOn;
    @BindView(R.id.newsOff)RadioButton newsOff;

    @BindView(R.id.depthnews_radiogroup)RadioGroup depthnews_radiogroup;
    @BindView(R.id.depthnewsOn)RadioButton depthnewsOn;
    @BindView(R.id.depthnewsOff)RadioButton depthnewsOff;

    @BindView(R.id.sound_radiogroup)RadioGroup sound_radiogroup;
    @BindView(R.id.off)RadioButton off;
    @BindView(R.id.on)RadioButton on;

    @BindView(R.id.beepsound_radiogroup)RadioGroup beepsound_radiogroup;
    @BindView(R.id.beep_off)RadioButton beep_off;
    @BindView(R.id.beep_on)RadioButton beep_on;

    @BindView(R.id.vibration_radiogroup)RadioGroup vibration_radiogroup;
    @BindView(R.id.v_off)RadioButton v_off;
    @BindView(R.id.v_on)RadioButton v_on;

    @BindView(R.id.event_radiogroup)RadioGroup event_radiogroup;
    @BindView(R.id.three_line)RadioButton three_line;
    @BindView(R.id.one_line)RadioButton one_line;

    @BindView(R.id.style_rd)RadioGroup style_rd;
    @BindView(R.id.new_watchstyle)RadioButton new_watchstyle;
    @BindView(R.id.old_watchstyle)RadioButton old_watchstyle;

    //@BindView(R.id.wealthChk)CheckBox wealthChk;
    @BindView(R.id.bseChk)CheckBox bseChk;
    @BindView(R.id.nseChk)CheckBox nseChk;
    @BindView(R.id.mcxChk)CheckBox mcxChk;
    @BindView(R.id.ncdexChk)CheckBox ncdexChk;
    @BindView(R.id.tradeConfirmChk)CheckBox tradeConfirmChk;

    @BindView(R.id.searchengineChk)CheckBox searchengineChk;

    @BindView(R.id.indices)TextView indices;

    @BindView(R.id.switching_radiogroup)RadioGroup swichingRgroup;
    @BindView(R.id.s_on)RadioButton switchon;
    @BindView(R.id.s_off)RadioButton switchoff;

    @BindView(visiblelayout)LinearLayout visibleLayout;

    @BindView(R.id.visible_radiogroup)RadioGroup visibleRgroup;
    @BindView(R.id.sensex1)RadioButton sensex1;
    @BindView(R.id.usdinr1)RadioButton usdinr1;

    @BindView(R.id.niftybank)RadioButton niftybank;

    private boolean isIndices;
    private ImageView tool_logo;
    private HorizontalScrollView horizontal_scroll;
    private Toolbar toolbar;
    private OnFontChange OnFontChange;
    private HomeActivity _homeActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity){
            _homeActivity = (HomeActivity) context;
        }
    }

    public DisplayFragment(){
        super();
    }

    public DisplayFragment(OnFontChange OnFontChange){
        this.OnFontChange = OnFontChange;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        horizontal_scroll = (HorizontalScrollView) getActivity().findViewById(R.id.horizontal_scroll);
        tool_logo = (ImageView) getActivity().findViewById(R.id.tool_logo);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        GlobalClass.fromDisplaySettings = true;
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = null;
        if (UserSession.getLoginDetailsModel().isActiveUser()){
        layout = inflater.inflate(R.layout.display_setting, container, false);
        ButterKnife.bind(this, layout);
        initialization();
        }else {
            layout = inflater.inflate(R.layout.deactive_account, container, false);
        }
        return layout;
    }
    private void initialization() {
        initExample(newTextview);
        initExample(oldTextview);
        initSettings();
        indices_setting_textview.setOnClickListener(this);
        textsize_radiogroup.setOnCheckedChangeListener(this);
        chg_rd.setOnCheckedChangeListener(this);
        ammount_radiogroup.setOnCheckedChangeListener(this);
        style_rd.setOnCheckedChangeListener(this);
        event_radiogroup.setOnCheckedChangeListener(this);
        news_radiogroup.setOnCheckedChangeListener(this);
        depthnews_radiogroup.setOnCheckedChangeListener(this);
        depthactionwatch_radiogroup.setOnCheckedChangeListener(this);
        sound_radiogroup.setOnCheckedChangeListener(this);
        beepsound_radiogroup.setOnCheckedChangeListener(this);
        vibration_radiogroup.setOnCheckedChangeListener(this);
        bseChk.setVisibility(View.GONE);
        nseChk.setVisibility(View.GONE);
        mcxChk.setVisibility(View.GONE);
        ncdexChk.setVisibility(View.GONE);

        if(GlobalClass.isEquity()) {
            bseChk.setVisibility(View.VISIBLE);
            nseChk.setVisibility(View.VISIBLE);

            bseChk.setOnCheckedChangeListener(this);
            nseChk.setOnCheckedChangeListener(this);
        }else if(GlobalClass.isCommodity()){
            mcxChk.setVisibility(View.VISIBLE);
            ncdexChk.setVisibility(View.VISIBLE);

            mcxChk.setOnCheckedChangeListener(this);
            ncdexChk.setOnCheckedChangeListener(this);
        }

        //wealthChk.setOnCheckedChangeListener(this);
        tradeConfirmChk.setOnCheckedChangeListener(this);
        searchengineChk.setOnCheckedChangeListener(this);
        swichingRgroup.setOnCheckedChangeListener(this);
        visibleRgroup.setOnCheckedChangeListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initSettings() {
        SharedPref sharedPref = VenturaApplication.getPreference();
        isIndices = sharedPref.getSharedPrefFromTag(ePrefTAG.INDICES.name, false);
        if (isIndices) {
            indices_setting_textview.setText(Constants.INVISIBLE);
            indices_setting_textview.setTextColor(getResources().getColor(R.color.white));
            indices_setting_textview.setBackground(getResources().getDrawable(R.drawable.border_without_radious));
        } else {
            indices_setting_textview.setText(Constants.ALWAYS_VISIBLE);
            indices_setting_textview.setTextColor(getResources().getColor(R.color.black));
            indices_setting_textview.setBackgroundColor(getResources().getColor(R.color.white));
        }

        switch (PreferenceHandler.getFontStyle()) {
            case R.style.FontSizeSmall:
                small_radiobutton.setChecked(true);
                break;
            case R.style.FontSizeLarge:
                large_radiobutton.setChecked(true);
                break;
            default:
                medium_radiobutton.setChecked(true);
                break;
        }

        String amount = sharedPref.getSharedPrefFromTag(ePrefTAG.AMOUNT.name, eConstant.RS.name);
        if (amount.equalsIgnoreCase(Constants.RS)) {
            rs_radiobutton.setChecked(true);
        } else if (amount.equalsIgnoreCase(Constants.LACS)) {
            lacs_radiobutton.setChecked(true);
        } else if (amount.equalsIgnoreCase(Constants.CR)) {
            cr_radiobutton.setChecked(true);
        }
        boolean watchStyle = sharedPref.getSharedPrefFromTag(ePrefTAG.WATCHSTYLE.name, true);
        if (watchStyle) {
            new_watchstyle.setChecked(true);
        } else {
            old_watchstyle.setChecked(true);
        }
        boolean eventStyle = sharedPref.getSharedPrefFromTag(ePrefTAG.ENENTSTYLE.name, true);
        if (eventStyle) {
            three_line.setChecked(true);
        } else {
            one_line.setChecked(true);
        }

        boolean depthaction = sharedPref.getSharedPrefFromTag(ePrefTAG.DEPTHACTIONWATCH.name, true);
        if (depthaction) {
            dawOn.setChecked(true);
        } else {
            dawOff.setChecked(true);
        }
        boolean watchNews = sharedPref.getSharedPrefFromTag(ePrefTAG.WATCHNEWS.name, true);
        if (watchNews) {
            newsOn.setChecked(true);
        } else {
            newsOff.setChecked(true);
        }
        boolean depthNews = sharedPref.getSharedPrefFromTag(ePrefTAG.DEPTHNEWS.name, true);
        if (depthNews) {
            depthnewsOn.setChecked(true);
        } else {
            depthnewsOff.setChecked(true);
        }

        boolean newsSound = sharedPref.getSharedPrefFromTag(ePrefTAG.NEWS_SOUND.name, true);
        if (newsSound) {
            on.setChecked(true);
        } else {
            off.setChecked(true);
        }

        boolean beepSound = sharedPref.getSharedPrefFromTag(ePrefTAG.BEEP_SOUND.name, true);
        if (beepSound) {
            beep_on.setChecked(true);
        } else {
            beep_off.setChecked(true);
        }

        boolean newsvibration = sharedPref.getSharedPrefFromTag(ePrefTAG.NEWS_VIBRATION.name, true);
        if (newsvibration) {
            v_on.setChecked(true);
        } else {
            v_off.setChecked(true);
        }
        boolean isNormal = sharedPref.getSharedPrefFromTag(ePrefTAG.PER_CHGSETTING.name, false);
        if (isNormal) {
            normal_chg.setChecked(true);
        } else {
            abs_chg.setChecked(true);
        }
        boolean isBSE = sharedPref.getSharedPrefFromTag(ePrefTAG.BSE.name, false);
        bseChk.setChecked(isBSE);

        boolean isNSE = sharedPref.getSharedPrefFromTag(ePrefTAG.NSE.name, false);
        nseChk.setChecked(isNSE);

        boolean isMCX = sharedPref.getSharedPrefFromTag(ePrefTAG.MCX.name, false);
        mcxChk.setChecked(isMCX);

        boolean isNCDEX = sharedPref.getSharedPrefFromTag(ePrefTAG.NCDEX.name, false);
        ncdexChk.setChecked(isNCDEX);

        //boolean isWaelth = sharedPref.getSharedPrefFromTag(ePrefTAG.WEALTH.name, true);
        //wealthChk.setChecked(isWaelth);

        boolean isTradeConfirmPopup = sharedPref.getSharedPrefFromTag(ePrefTAG.TRADE_CONFIRM_POPUP.name, true);
        tradeConfirmChk.setChecked(isTradeConfirmPopup);

        boolean isOldSearchPopup = sharedPref.getSharedPrefFromTag(ePrefTAG.OLD_SEARCH_POPUP.name,  false);
        searchengineChk.setChecked(isOldSearchPopup);

        if (PreferenceHandler.isIndicesSwitchingAvl()) {
            switchon.setChecked(true);
            visibleLayout.setVisibility(View.GONE);
        } else {
            switchoff.setChecked(true);
            visibleLayout.setVisibility(View.VISIBLE);
        }
        int selectedIndice  = PreferenceHandler.getSelectedIndice();
        if (selectedIndice == eIndices.SENSEX.value) {
            sensex1.setChecked(true);
        } else if (selectedIndice == eIndices.USDINR.value){
            usdinr1.setChecked(true);
        } else if (selectedIndice == eIndices.NIFTYBANK.value){
            niftybank.setChecked(true);
        }

    }
    private void initExample(TextView textView) {
        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(1);
        textView.setSelected(true);
        textView.setSingleLine(true);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.indices_setting_textview:
                if (isIndices){
                    getActivity().findViewById(R.id.relative).setVisibility(View.VISIBLE);
                    horizontal_scroll.setVisibility(View.VISIBLE);
                    tool_logo.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.black));
                    indices_setting_textview.setText(Constants.ALWAYS_VISIBLE);
                    indices_setting_textview.setTextColor(getResources().getColor(R.color.black));
                    indices_setting_textview.setBackgroundColor(getResources().getColor(R.color.white));
                    isIndices = false;
                }else {
                    getActivity().findViewById(R.id.relative).setVisibility(View.GONE);
                    horizontal_scroll.setVisibility(View.GONE);
                    tool_logo.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.ventura_color));
                    indices_setting_textview.setText(Constants.INVISIBLE);
                    indices_setting_textview.setTextColor(getResources().getColor(R.color.white));
                    indices_setting_textview.setBackground(getResources().getDrawable(R.drawable.border_without_radious));
                    isIndices = true;
                }
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.INDICES.name,isIndices);
                break;
            default:
                break;
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        switch (checkId){
            case R.id.small_radiobutton:
                OnFontChange.onFontChange(R.style.FontSizeSmall);
                PreferenceHandler.setFontStyle(R.style.FontSizeSmall);
                break;
            case R.id.medium_radiobutton:
                OnFontChange.onFontChange(R.style.FontSizeMedium);
                PreferenceHandler.setFontStyle(R.style.FontSizeMedium);
                break;
            case R.id.large_radiobutton:
                OnFontChange.onFontChange(R.style.FontSizeLarge);
                PreferenceHandler.setFontStyle(R.style.FontSizeLarge);
                break;
            case R.id.rs_radiobutton:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.AMOUNT.name,eConstant.RS.name);
                break;
            case R.id.lacs_radiobutton:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.AMOUNT.name,eConstant.LACS.name);
                break;
            case R.id.cr_radiobutton:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.AMOUNT.name,eConstant.COR.name);
                break;
            case R.id.new_watchstyle:
                ObjectHolder.isNeedDisplayChange = true;
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.WATCHSTYLE.name,true);
                break;
            case R.id.old_watchstyle:
                ObjectHolder.isNeedDisplayChange = true;
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.WATCHSTYLE.name,false);
                break;
            case R.id.three_line:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.ENENTSTYLE.name,true);
                break;
            case R.id.one_line:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.ENENTSTYLE.name,false);
                break;
            case R.id.dawOn:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.DEPTHACTIONWATCH.name,true);
                break;
            case R.id.dawOff:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.DEPTHACTIONWATCH.name,false);
                break;
            case R.id.newsOn:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.WATCHNEWS.name,true);
                break;
            case R.id.newsOff:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.WATCHNEWS.name,false);
                break;
            case R.id.depthnewsOn:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.DEPTHNEWS.name,true);
                break;
            case R.id.depthnewsOff:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.DEPTHNEWS.name,false);
                break;
            case R.id.normal_chg:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.PER_CHGSETTING.name,true);
                break;
            case R.id.abs_chg:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.PER_CHGSETTING.name,false);
                break;
            case R.id.on:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.NEWS_SOUND.name,true);
                break;
            case R.id.off:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.NEWS_SOUND.name,false);
                break;
            case R.id.beep_on:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.BEEP_SOUND.name,true);
                break;
            case R.id.beep_off:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.BEEP_SOUND.name,false);
                break;
            case R.id.v_on:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.NEWS_VIBRATION.name,true);
                break;
            case R.id.v_off:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.NEWS_VIBRATION.name,false);
                break;
            case R.id.s_on:
                visibleLayout.setVisibility(View.GONE);
                PreferenceHandler.setIndicesSwitching(true);
                _homeActivity.ProcessIndicessSwitching();
                break;
            case R.id.s_off:
                visibleLayout.setVisibility(View.VISIBLE);
                PreferenceHandler.setIndicesSwitching(false);
                _homeActivity.SensexVisivility(PreferenceHandler.getSelectedIndice());
                break;
            case R.id.sensex1:
                PreferenceHandler.setSensexVisible(eIndices.SENSEX.value);
                _homeActivity.SensexVisivility(eIndices.SENSEX.value);
                break;
            case R.id.usdinr1:
                PreferenceHandler.setSensexVisible(eIndices.USDINR.value);
                _homeActivity.SensexVisivility(eIndices.USDINR.value);
                break;
            case R.id.niftybank:
                PreferenceHandler.setSensexVisible(eIndices.NIFTYBANK.value);
                _homeActivity.SensexVisivility(eIndices.NIFTYBANK.value);
                break;
        }
        GlobalClass.showToast(getContext(),"Setting saved");
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
        switch (compoundButton.getId()){
            case R.id.wealthChk:
                if (check){
                    (getActivity().findViewById(R.id.mywealthRDbutton)).setVisibility(View.VISIBLE);
                    GlobalClass.showToast(getContext(),"Movers added to home");
                }else {
                    (getActivity().findViewById(R.id.mywealthRDbutton)).setVisibility(View.GONE);
                    GlobalClass.showToast(getContext(),"Movers removed from home");
                }
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.WEALTH.name,check);
                break;
            case R.id.bseChk:
                if (check){
                    (getActivity().findViewById(R.id.bseRDbutton)).setVisibility(View.VISIBLE);
                    GlobalClass.showToast(getContext(),"BSE added to home");
                }else {
                    (getActivity().findViewById(R.id.bseRDbutton)).setVisibility(View.GONE);
                    GlobalClass.showToast(getContext(),"BSE removed from home");
                }
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.BSE.name,check);
                break;
            case R.id.nseChk:
                if (check){
                    (getActivity().findViewById(R.id.nseRDbutton)).setVisibility(View.VISIBLE);
                    GlobalClass.showToast(getContext(),"NSE added to home");
                }else {
                    (getActivity().findViewById(R.id.nseRDbutton)).setVisibility(View.GONE);
                    GlobalClass.showToast(getContext(),"NSE removed from home");
                }
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.NSE.name,check);
                break;
            case R.id.mcxChk:
                if (check){
                    (getActivity().findViewById(R.id.mcxRDbutton)).setVisibility(View.VISIBLE);
                    GlobalClass.showToast(getContext(),"MCX added to home");
                }else {
                    (getActivity().findViewById(R.id.mcxRDbutton)).setVisibility(View.GONE);
                    GlobalClass.showToast(getContext(),"MCX removed from home");
                }
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.MCX.name,check);
                break;
            case R.id.ncdexChk:
                if (check){
                    (getActivity().findViewById(R.id.ncdexRDbutton)).setVisibility(View.VISIBLE);
                    GlobalClass.showToast(getContext(),"NCDEX added to home");
                }else {
                    (getActivity().findViewById(R.id.ncdexRDbutton)).setVisibility(View.GONE);
                    GlobalClass.showToast(getContext(),"NCDEX removed from home");
                }
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.NCDEX.name,check);
                break;
            case R.id.tradeConfirmChk:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.TRADE_CONFIRM_POPUP.name,check);
                break;
            case R.id.searchengineChk:
                VenturaApplication.getPreference().storeSharedPref(ePrefTAG.OLD_SEARCH_POPUP.name, check);
                break;

        }
    }
}