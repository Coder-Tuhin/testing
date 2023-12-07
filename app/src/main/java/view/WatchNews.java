package view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.Timer;
import java.util.TimerTask;

import enums.eMsgType;
import enums.ePrefTAG;
import enums.eScreen;
import fragments.NotificationFragment;
import handler.ClsNewsHandler;
import interfaces.OnAlertListener;
import models.NotificationModel;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;

public class WatchNews {

    private boolean watchNews = true;
    final TextView latestNews;
    final ImageView sharebtn;
    final LinearLayout latestnewslinear;
    final Context context;
    final OnAlertListener onAlertListener;
    final String sharepreferencetag;

    public WatchNews(TextView _latestNews, ImageView _sharebtn, LinearLayout _latestnewslinear, Context _context, OnAlertListener _onAlertListener,String tag){

        this.latestNews = _latestNews;
        this.sharebtn = _sharebtn;
        this.latestnewslinear = _latestnewslinear;
        this.context = _context;
        this.onAlertListener = _onAlertListener;
        this.sharepreferencetag = tag;

        latestNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceHandler.getNotificationActive()) {
                    if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.IPO.name) ||
                            selectTedNews.getTitle().equalsIgnoreCase(eMsgType.BOND.name)||
                            selectTedNews.getTitle().equalsIgnoreCase(eMsgType.SGB.name)||
                            selectTedNews.getTitle().equalsIgnoreCase(eMsgType.NFO.name) ||
                            selectTedNews.getTitle().equalsIgnoreCase(eMsgType.FD.name) ||
                            selectTedNews.getTitle().equalsIgnoreCase(eMsgType.NPS.name) ||
                            selectTedNews.getTitle().equalsIgnoreCase(eMsgType.MF.name)
                    ){
                        if(selectTedNews.getMessage().toLowerCase().contains("click here")){
                            if(isTooLarge(latestNews,latestNews.getText().toString(),"click here")){
                                GlobalClass.fragmentTransaction(NotificationFragment.newInstance(selectTedNews.getTabSelection()), R.id.container_body, true, "");
                            } else {
                                if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.IPO.name)){
                                    GlobalClass.showScreen(eScreen.IPO);
                                }else if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.BOND.name)){
                                    GlobalClass.showScreen(eScreen.BONDS);
                                }else if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.SGB.name)){
                                    GlobalClass.showScreen(eScreen.SGB);
                                }else if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.NFO.name)){
                                    GlobalClass.showScreen(eScreen.NFO);
                                }else if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.FD.name)) {
                                    GlobalClass.showScreen(eScreen.FD);
                                }else if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.NPS.name)) {
                                    GlobalClass.showScreen(eScreen.NPS);
                                }else if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.MF.name)) {
                                    if(selectTedNews.getMessage().toLowerCase().contains("park & earn")){
                                        GlobalClass.showScreen(eScreen.PARKEARN);
                                    }else if(selectTedNews.getMessage().toLowerCase().contains("missed sip")){
                                        GlobalClass.showScreen(eScreen.Missed_SIP);
                                    }else if(selectTedNews.getMessage().toLowerCase().contains("debt")){
                                        GlobalClass.showScreen(eScreen.PERFORMINGFUNDSDEBT);
                                    }else if(selectTedNews.getMessage().toLowerCase().contains("performing")){
                                        GlobalClass.showScreen(eScreen.PERFORMINGFUNDSEQUITY);
                                    }else if(selectTedNews.getMessage().toLowerCase().contains("hybrid")){
                                        GlobalClass.showScreen(eScreen.PERFORMINGFUNDSHYBRID);
                                    }else if(selectTedNews.getMessage().toLowerCase().contains("liquid")){
                                        GlobalClass.showScreen(eScreen.PERFORMINGFUNDSLIQUID);
                                    }else if(selectTedNews.getMessage().toLowerCase().contains("overseas")){
                                        GlobalClass.showScreen(eScreen.PERFORMINGFUNDSOTHERS);
                                    }else {
                                        GlobalClass.showScreen(eScreen.MF);
                                    }
                                }
                            }
                        }else{
                            GlobalClass.fragmentTransaction(NotificationFragment.newInstance(selectTedNews.getTabSelection()), R.id.container_body, true, "");
                        }
                    }else {
                        GlobalClass.fragmentTransaction(NotificationFragment.newInstance(selectTedNews.getTabSelection()), R.id.container_body, true, "");
                    }
                }else {
                    new AlertBox(GlobalClass.latestContext,"OK",
                            context.getResources().getString(R.string.disclaimer_description),onAlertListener,"watchnews");
                }
            }
        });
        sharebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(selectTedNews != null) {
                    WhatsAppImageShare shareImege = new WhatsAppImageShare();
                    shareImege.createAndShareImage(selectTedNews);
                }
            }
        });
    }

    public void startNewsTime(){
        latestnewslinear.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initNews();
                ObjectHolder.isNeedDisplayChange = false;
            }
        },2000);
    }

    private void initNews() {
        watchNews = VenturaApplication.getPreference().getSharedPrefFromTag(sharepreferencetag,true);
        if (!UserSession.getLoginDetailsModel().isActiveUser() || !watchNews) return;
        if (PreferenceHandler.getNotificationActive()){
            if (GlobalClass.clsNewsHandler == null) {
                GlobalClass.clsNewsHandler = new ClsNewsHandler();
            }
            newsTimer(false);
        }else {
            sharebtn.setVisibility(View.GONE);
            latestnewslinear.setVisibility(View.VISIBLE);
            latestNews.setText("Get access to ‘News’ here. Tap to view Disclaimer.");
        }
    }
    NotificationModel selectTedNews;
    public void newsTimer(boolean fromHandler){
        newsFromHandler = fromHandler;
        cancleTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (GlobalClass.clsNewsHandler != null){
                    if (newsFromHandler){
                        selectTedNews = GlobalClass.clsNewsHandler.getCurrentNews();
                        newsFromHandler = false;
                    }else {
                        selectTedNews =GlobalClass.clsNewsHandler.getNewsForShow();
                    }
                    if (selectTedNews != null){
                        setMsg(selectTedNews);
                    }
                }
            }
        },0,5000);
    }

    private void setMsg(final NotificationModel selectTedNews) {
        if (GlobalClass.homeActivity != null){
            HomeActivity aca = ((HomeActivity) GlobalClass.homeActivity);
            if (aca != null){
                aca.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (latestnewslinear != null){
                                if (latestnewslinear.getVisibility() != View.VISIBLE) {
                                    latestnewslinear.setVisibility(View.VISIBLE);
                                    sharebtn.setVisibility(View.VISIBLE);
                                }
                                if (selectTedNews.getMessage().equals(GlobalClass.clsNewsHandler.getCurrentNews().getMessage())){
                                    try {
                                        Drawable img = context.getResources().getDrawable(R.drawable.latest_news);
                                        latestNews.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                                    }catch (Exception ex){}
                                }else {
                                    latestNews.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                                }

                                if(selectTedNews.getTitle().equalsIgnoreCase(eMsgType.IPO.name) ||
                                        selectTedNews.getTitle().equalsIgnoreCase(eMsgType.BOND.name) ||
                                        selectTedNews.getTitle().equalsIgnoreCase(eMsgType.SGB.name) ||
                                        selectTedNews.getTitle().equalsIgnoreCase(eMsgType.NFO.name) ||
                                        selectTedNews.getTitle().equalsIgnoreCase(eMsgType.FD.name) ||
                                        selectTedNews.getTitle().equalsIgnoreCase(eMsgType.NPS.name) ||
                                        selectTedNews.getTitle().equalsIgnoreCase(eMsgType.MF.name)
                                ){
                                    String msg = selectTedNews.getMessage();
                                    if (msg.toLowerCase().contains("click here")) {
                                        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(msg);
                                        ForegroundColorSpan color = new ForegroundColorSpan(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
                                        int indexOfCH = msg.toLowerCase().indexOf("click here");
                                        ssBuilder.setSpan(color, indexOfCH, indexOfCH + "click here".length() ,
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        ssBuilder.setSpan(new UnderlineSpan(), indexOfCH, indexOfCH + "click here".length(),
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        latestNews.setText(ssBuilder);
                                    }
                                    else {
                                        latestNews.setText(selectTedNews.getMessage());
                                    }
                                }else {
                                    latestNews.setText(selectTedNews.getMessage());
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private Timer timer;
    public void cancleTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    private boolean newsFromHandler = false;

    private boolean isTooLarge (TextView text, String newText,String withInText) {
        if(newText.toLowerCase().contains(withInText.toLowerCase())){
            newText = newText.substring(0,newText.toLowerCase().indexOf(withInText.toLowerCase())+withInText.length());
        }
        float textWidth = text.getPaint().measureText(newText);
        //float textViewWidth = text.getMeasuredWidth()
        //GlobalClass.log("TextWidth" + textWidth + " : " + textViewWidth);
        return (textWidth >= ((text.getMeasuredWidth()-10)*3));
    }
}
