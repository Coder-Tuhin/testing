package handler;

import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Structure.news.StructNotificationReq;
import connection.SendDataToBCServer;
import enums.eMsgType;
import enums.ePrefTAG;
import models.NotificationModel;
import models.RoundrobbinModel;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.SharedPref;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 30-Nov-2017.
 */

public class ClsNewsHandler {
    private ArrayList<NotificationModel> news;
    int index;
    private NotificationModel currentNews;
    private List<String> removeId;

    public ClsNewsHandler() {
        index = 0;
        news = new ArrayList<>();
        rr = VenturaApplication.getPreference().getRoundRobin();
        removeId = VenturaApplication.getPreference().getRemoveIds();
        reqnews();
        loadNewsData();
    }

    public void reqnews(){
        StructNotificationReq structNotificationReq = new StructNotificationReq();
        structNotificationReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        structNotificationReq.msgType.setValue(eMsgType.NEWS.value);
        structNotificationReq.fromMsgTime.setValue(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.LAST_NEWSTIME.name, 0));
        new SendDataToBCServer().sendNewsReq(structNotificationReq);
    }

    private List<String> rr;


    public void loadNewsData() {
        ArrayList<NotificationModel> tempNewsList = new ArrayList<>();
        SharedPref sharedPref = VenturaApplication.getPreference();
       // LinkedHashMap<String, NotificationModel> notificationList = sharedPref.getNotification();
        rr = sharedPref.getRoundRobin();
        if (PreferenceHandler.getNotificationList().size() > 0) {
            ArrayList<NotificationModel> values = new ArrayList<>(PreferenceHandler.getNotificationList().values());
            Collections.sort(values, (o1, o2) -> o2.getTime().compareTo(o1.getTime()));
            for (int i = 0; i < values.size(); i++) {
                NotificationModel model = values.get(i);
                if (rr.contains(model.getTitle())) {
                    if (tempNewsList.size() >= 7) {
                        break;
                    } else {
                        if (!removeId.contains(model.getId())) {
                            tempNewsList.add(model);
                        }
                    }
                }
            }
            Collections.reverse(tempNewsList);

            news.clear();
            news.addAll(tempNewsList);
            if (news.size() > 0) {
                currentNews = news.get(news.size() - 1);
            }
        }
    }
    public void setCurrentNews(NotificationModel model) {
        if(rr != null && rr.contains(model.getTitle())) {
            currentNews = model;
            if (news.size() > 0) {
                news.remove(0);
            }
            news.add(currentNews);
        }
    }
    public NotificationModel getCurrentNews() {
        if (currentNews == null) {
            if (news.size() > 0) {
                currentNews = news.get(news.size() - 1);
            }
        }
        return currentNews;
    }
    public NotificationModel getNewsForShow() {
        try {
            if (index >= news.size()) {
                index = 0;
            }
            if (news.size() > 0 && index < news.size()) {

                NotificationModel nm = news.get(index);
                if (nm.getTitle().equals(eMsgType.EVENTS.name) ||
                        nm.getTitle().equals(eMsgType.SCRIP_RATE.name) ||
                        nm.getTitle().equals(eMsgType.FNO_TRADE.name) ||
                        nm.getTitle().equals(eMsgType.CASH_TRADE.name)) {
                    if (nm.count == 1) { // Rate alert , trade msg should show one time
                        removeId.add(nm.getId());
                        news.remove(nm);
                        if (index > news.size()) {
                            loadNewsData();
                            if (index >= news.size()) {
                                index = 0;
                            }
                            nm = news.get(index);
                        } else {
                            nm = null;
                        }
                        VenturaApplication.getPreference().setRemoveids(removeId);
                    } else {
                        nm.count++;
                    }
                }
                index++;
                return nm;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}