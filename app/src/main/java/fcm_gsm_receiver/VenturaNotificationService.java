package fcm_gsm_receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.activities.splash.SplashActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Structure.news.StructNotificationReq;
import connection.SendDataToBCServer;
import enums.eActivityFrom;
import enums.eConstant;
import enums.eForHandler;
import enums.eMessageCode;
import enums.eMsgType;
import enums.ePrefTAG;
import models.AlertModel;
import models.NotificationModel;
import models.ScripAlertModel;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.SharedPref;
import utils.StaticVariables;
import utils.UserSession;


public class VenturaNotificationService extends FirebaseMessagingService {

    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "com.ventura.venturawealth.ONE";
    public static final String CHANNEL_ONE_NAME = "Channel One";
    public static final String CHANNEL_TWO_ID = "com.ventura.venturawealth.TWO";
    public static final String CHANNEL_TWO_NAME = "Channel Two";
    public static final String CHANNEL_THREE_ID = "com.ventura.venturawealth.THREE";
    public static final String CHANNEL_THREE_NAME = "Channel Three";
    public static final String CHANNEL_FOUR_ID = "com.ventura.venturawealth.FOUR";
    public static final String CHANNEL_FOUR_NAME = "Channel Four";

    private String NOTIFICATION_GROUP_KEY = "com.ventura.venturawealth.GROUP";


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        GlobalClass.log("New FCM token : " + token);
        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.TOKEN.name,token);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleMessage(remoteMessage);
    }

    private void handleMessage(RemoteMessage remoteMessage){
        try {
            if (!UserSession.getLoginDetailsModel().isActiveUser()) return;
            SharedPref sharedPref = VenturaApplication.getPreference();
            if (!PreferenceHandler.getNotificationActive()) return;
            if (remoteMessage.getData().size()>0){
                final Map<String, String> data = remoteMessage.getData();
                String title = data.get(eConstant.TYPE.name);
                String msg = data.get(eConstant.MSG.name);
                String time = data.get(eConstant.TIME.name);
                String id = data.get(eConstant.ID.name);
                int scripCode = 0;
                String scripName = "";

                NotificationModel notificationModel = new NotificationModel(title,msg,time,id,scripCode,scripName);
                assert title != null;
                if (title.equalsIgnoreCase(eMsgType.EVENTS.name) ||
                        title.equalsIgnoreCase(eMsgType.SCRIP_RATE.name)){
                    scripCode =Integer.parseInt(data.get(eConstant.SCRIPCODE.name));
                    scripName =data.get(eConstant.SCRIPNAME.name);
                }

                if (GlobalClass.clsNewsHandler != null) {
                    GlobalClass.clsNewsHandler.setCurrentNews(notificationModel);
                }
                if (title.equalsIgnoreCase(eMsgType.NEWS.name)){
                    if (GlobalClass.notificationHandler == null){
                        sendNotification(VenturaApplication.getContext(), title, msg.trim());
                    }
                    sendNotificationRequestServer(eMsgType.NEWS.value,eMessageCode.NEWS_FETCH);
                }else {
                    /*if (!title.equalsIgnoreCase(eMsgType.TRADING_CALL.name)){
                        PreferenceHandler.getNotificationList().put(notificationModel.getIDForSaving(),notificationModel);
                        PreferenceHandler.setNotificationList();
                    }*/
                    if(GlobalClass.notificationHandler != null) {
                        handleNotification(VenturaApplication.getContext(), title, msg);
                    } else {
                        sendNotification(VenturaApplication.getContext(), title, msg.trim());
                    }
                }
                if (title.equalsIgnoreCase(eMsgType.SCRIP_RATE.name)){
                    short condition = Short.parseShort(data.get(eConstant.CONDITION.name));
                    LinkedHashMap<Integer, ScripAlertModel> map = sharedPref.getAlertRate();
                    ScripAlertModel scripAlertModel = map.get(scripCode);
                    AlertModel alertModel;
                    short achive = 1;
                    alertModel = scripAlertModel.getValueForCondition(condition);
                    if(alertModel != null) alertModel.setAchive(achive);
                    map.put(scripCode,scripAlertModel);
                    if ((scripAlertModel.getLessCond() != null && scripAlertModel.getLessCond().getAchive()==1) &&
                            (scripAlertModel.getGeaterCond() != null && scripAlertModel.getGeaterCond().getAchive() == 1)){
                        map.remove(scripCode);
                    }
                    sharedPref.setAlertRate(map);
                    if (GlobalClass.alertHandler != null){
                        Message mssg = Message.obtain(GlobalClass.alertHandler);
                        Bundle confMsgBundle = new Bundle();
                        confMsgBundle.putInt(eForHandler.MSG_CODE.name, eMessageCode.NOTIFICATION.value);
                        confMsgBundle.putByteArray(eForHandler.RESDATA.name,null);
                        mssg.setData(confMsgBundle);
                        GlobalClass.alertHandler.sendMessage(mssg);
                    }
                }

                if (GlobalClass.mktWatchUiHandler != null){
                    Message mssg = Message.obtain(GlobalClass.mktWatchUiHandler);
                    Bundle confMsgBundle = new Bundle();
                    confMsgBundle.putInt(eForHandler.MSG_CODE.name,eMessageCode.NOTIFICATION.value);
                    confMsgBundle.putInt(eForHandler.SCRIPCODE.name,0);
                    confMsgBundle.putString(eForHandler.RESPONSE.name,title);
                    mssg.setData(confMsgBundle);
                    GlobalClass.mktWatchUiHandler.sendMessage(mssg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendNotificationRequestServer(int msgType,eMessageCode _messageCode){
        if(GlobalClass.tradeBCClient != null){
            StructNotificationReq structNotificationReq = new StructNotificationReq();
            structNotificationReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            structNotificationReq.msgType.setValue(msgType);
            structNotificationReq.fromMsgTime.setValue(VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.LAST_NEWSTIME.name,0));
            new SendDataToBCServer().sendNotificationReq(structNotificationReq,_messageCode);
        }
    }

    private void handleNotification(Context ctx, String title, String msg) {
        if(GlobalClass.notificationHandler == null){
            return;
        }

        int currentTab = GlobalClass.currentNotificatonTab;
        switch (currentTab){
            case 1:
                if (!title.equalsIgnoreCase(eMsgType.FNO_TRADE.name)) {
                    sendNotification(ctx,title,msg);
                }else {
                    GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NEWS_FETCH.value);
                }
                break;
            case 2:
                if (!title.equalsIgnoreCase(eMsgType.CASH_TRADE.name)){
                    sendNotification(ctx,title,msg);
                }else {
                    GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NEWS_FETCH.value);
                }
                break;
            case 3:
                if (title.equalsIgnoreCase(eMsgType.EVENTS.name) ||
                        title.equalsIgnoreCase(eMsgType.MARGIN_CALL.name) ) {
                    GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NEWS_FETCH.value);
                }else {
                    sendNotification(ctx,title,msg);
                }
                break;
            case 4:
                if (!title.equalsIgnoreCase(eMsgType.RESEARCH_CALL.name)){
                    sendNotification(ctx,title,msg);
                }else {
                    GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NEWS_FETCH.value);
                }
                break;
            case 5:
                if (!title.equalsIgnoreCase(eMsgType.NEWS.name)){
                    sendNotification(ctx,title,msg);
                }else {
                    GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NEWS_FETCH.value);
                }
                break;
            case 6:
                if (!title.equalsIgnoreCase(eMsgType.TRADING_CALL.name)){
                    sendNotification(ctx,title,msg);
                }else {
                    GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NOTIFICATION_FETCH.value);
                }
                break;
            default:
                GlobalClass.sendMsg(GlobalClass.notificationHandler,eMessageCode.NEWS_FETCH.value);
                break;
        }
    }

    private AudioAttributes soundAttributes;
    public static int NotificationId = 0;

    private void sendNotification(Context ctx, String title, String msg) {
        GlobalClass.log("Notify Msg : " + msg);
        notificationList.add(title+": "+msg);

        PendingIntent pendingIntent = null;

        if (GlobalClass.homeScrUiHandler!=null){
            Intent intent = new Intent(ctx, NotificationReceiver.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(ctx, eActivityFrom.NOTIFICATION.value, intent, PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_MUTABLE);
            } else{
                pendingIntent = PendingIntent.getBroadcast(ctx, eActivityFrom.NOTIFICATION.value, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }else if (GlobalClass.homeActivity != null){ //Resume or Not
            Intent notifyIntent = new Intent(this, HomeActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            HomeActivity.FROM_NOTIFICATION_WHENPAUSE = true;
            pendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }else{
            Intent notifyIntent = new Intent(this, SplashActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            notifyIntent.putExtra(StaticVariables.FROM_NOTIFICATION,true);
            pendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }

        boolean newsSound = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NEWS_SOUND.name, true);
        boolean newsVibration = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NEWS_VIBRATION.name, true);

        String channelID = CHANNEL_ONE_ID;
        if(newsSound && !newsVibration){
            channelID = CHANNEL_TWO_ID;
        } else if(!newsSound && newsVibration){
            channelID = CHANNEL_THREE_ID;
        } else if(!newsSound && !newsVibration){
            channelID = CHANNEL_FOUR_ID;
        }

        NotificationCompat.Builder notificationBuilder;
        NotificationCompat.Builder summaryBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(ctx,channelID);
            summaryBuilder = new NotificationCompat.Builder(ctx, channelID);
        }else {
            notificationBuilder = new NotificationCompat.Builder(ctx);
            summaryBuilder = new NotificationCompat.Builder(ctx);
            notificationBuilder.setSound(null);
            if (newsSound) {
                Uri defaultSoundUri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.notification);
                notificationBuilder.setSound(defaultSoundUri);
            }
            notificationBuilder.setVibrate(newsVibration?new long[]{200,200,200}:null);
        }
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(msg);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationBuilder.setGroup(NOTIFICATION_GROUP_KEY);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        notificationBuilder.setColor(Color.argb(255, 242, 103, 34));
        notificationBuilder.setSmallIcon(R.drawable.notification_icon_new);

        NotificationManager notificationManager = getManager();
        assert notificationManager != null;

        Notification notification = notificationBuilder.build();

     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.notification_icon_new);
            notificationBuilder.setColor(Color.argb(255, 242, 103, 34));
            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(msg);
            bigText.setSummaryText(title);
            notificationBuilder.setStyle(bigText);
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationManager.notify(NotificationId++, notificationBuilder.build());
        }else {
            notificationBuilder.setSmallIcon(R.drawable.notification_icon);
            List<ShortNotification> nList = GlobalClass.notificationList;
            nList.add(new ShortNotification(title, msg));

            notificationBuilder.setNumber(nList.size());
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Ventura Wealth");
            if (nList.size() > 1)
                inboxStyle.setSummaryText("You have " + nList.size() + " Notifications.");
            for (int i = (nList.size() - 1); i >= 0; i--) {
                ShortNotification sn = nList.get(i);
                Spannable spanText = new SpannableString(sn.getTitle() + ":  " + toTitleCase(sn.getMsg()));
                spanText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, sn.getTitle().length() + 1, 0);
                inboxStyle.addLine(spanText);
            }
            notificationBuilder.setStyle(inboxStyle);
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
            notificationManager.notify(0, notificationBuilder.build());
        }*/

        summaryBuilder.setContentTitle("Ventura Wealth");
        summaryBuilder.setContentIntent(pendingIntent);
        summaryBuilder.setColor(Color.argb(255, 242, 103, 34));
        //set content text to support devices running API level < 24
        summaryBuilder.setContentText("New Messages");
        summaryBuilder.setSmallIcon(R.drawable.notification_icon_new);
        //build summary info into InboxStyle template
        summaryBuilder.setStyle(getInboxStyle());
        //specify which group this notification belongs to
        summaryBuilder.setGroup(NOTIFICATION_GROUP_KEY);
        //set this notification as the summary for the group
        summaryBuilder.setGroupSummary(true);
        Notification summaryNotification =  summaryBuilder.build();
        notificationManager.notify((int) System.currentTimeMillis(), notification);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

    private int SUMMARY_ID = 0;
    public static List<String> notificationList = new ArrayList<>();

    private NotificationCompat.Style getInboxStyle(){
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        ArrayList<String> _tempMessageList = new ArrayList<>(notificationList);
        Collections.reverse(_tempMessageList);
        for (String msg:_tempMessageList) {
            inboxStyle.addLine(msg);
        }
        inboxStyle.setBigContentTitle(_tempMessageList.size()+" New Messages");
        inboxStyle.setSummaryText("Notifications");
        return inboxStyle;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {
        Uri defaultSoundUri = Uri.parse("android.resource://" + VenturaApplication.getContext()
                .getPackageName() + "/" + R.raw.notification);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setSound(defaultSoundUri,getSoundAttributes());
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_TWO_ID,
                CHANNEL_TWO_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel2.enableLights(false);
        notificationChannel2.enableVibration(false);
        notificationChannel2.setSound(defaultSoundUri,getSoundAttributes());
        notificationChannel2.setLightColor(Color.RED);
        getManager().createNotificationChannel(notificationChannel2);

        NotificationChannel notificationChannel3 = new NotificationChannel(CHANNEL_THREE_ID,
                CHANNEL_THREE_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel3.enableLights(false);
        notificationChannel3.enableVibration(true);
        notificationChannel3.setSound(null,null);
        notificationChannel3.setLightColor(Color.RED);
        getManager().createNotificationChannel(notificationChannel3);

        NotificationChannel notificationChannel4 = new NotificationChannel(CHANNEL_FOUR_ID,
                CHANNEL_FOUR_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel4.enableLights(false);
        notificationChannel4.enableVibration(false);
        notificationChannel4.setSound(null,null);
        notificationChannel4.setLightColor(Color.RED);
        getManager().createNotificationChannel(notificationChannel4);
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AudioAttributes getSoundAttributes(){
        if (soundAttributes == null){
            soundAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
        }
        return soundAttributes;
    }


    private void initBuilderForBelowOreoVersion(Context ctx, Notification.Builder notificationBuilder) {
        boolean newsSound = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NEWS_SOUND.name, true);
        if (newsSound) {
            Uri defaultSoundUri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.notification);
            notificationBuilder.setSound(defaultSoundUri);
        } else {
            notificationBuilder.setSound(null);
        }

        boolean newsVibration = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.NEWS_VIBRATION.name, true);
        if (newsVibration) {
            notificationBuilder.setVibrate(new long[]{1000, 1000, 1000});
        } else {
            notificationBuilder.setVibrate(null);
        }
        // notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(),R.drawable.notification_icon));
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
