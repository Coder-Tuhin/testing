package adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Structure.Response.Group.GroupsTokenDetails;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.ventura.venturawealth.R;

import enums.eMsgType;
import enums.eScreen;
import enums.eShowDepth;
import models.NotificationModel;
import utils.DateUtil;
import utils.GlobalClass;
import utils.PreferenceHandler;
import view.WhatsAppImageShare;

/**
 * Created by XTREMSOFT on 1/19/2017.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{
    private List<NotificationModel> notificationList = new ArrayList<>();
    private LayoutInflater inflater;
    public NotificationAdapter() {
        inflater = LayoutInflater.from(GlobalClass.latestContext);
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notification_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NotificationModel notificationModel = notificationList.get(position);
        holder.setValue(notificationModel);
    }

    private void deleteNotification(String Id) {
        if (PreferenceHandler.getNotificationList().containsKey(Id)){
            PreferenceHandler.getNotificationList().remove(Id);
            PreferenceHandler.setNotificationList();
        }
        refreshAdapter();
    }

    private void copyText(String message) {
        ClipboardManager clipboard = (ClipboardManager) GlobalClass.latestContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Notification Copied", message);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.notification_body)TextView notification_body;
        @BindView(R.id.notification_type)TextView notification_type;
        @BindView(R.id.notification_time)TextView notification_time;
        @BindView(R.id.sharebtn) ImageView sharebtn;
        @BindView(R.id.root_layout)LinearLayout root_layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void setValue(final NotificationModel notificationModel){
            String msg = notificationModel.getMessage();
            if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.CASH_TRADE.name) || notificationModel.getTitle().equalsIgnoreCase(eMsgType.FNO_TRADE.name) ){

                String[] msgArr = msg.split(":");
                String c_code = msgArr[0];
                if (msgArr != null && msgArr.length>=2) {

                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(msg);
                    ForegroundColorSpan color;
                    if (notificationModel.getTitle().equalsIgnoreCase(eMsgType.NEWS.name)) {
                        color = new ForegroundColorSpan(Color.WHITE);
                    } else {
                        color = new ForegroundColorSpan(Color.YELLOW);
                    }
                    ssBuilder.setSpan(color, msg.indexOf(c_code), msg.indexOf(c_code) + String.valueOf(c_code).length() + 2,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Pattern patternComma = Pattern.compile(",");
                    Matcher matcherComma = patternComma.matcher(msg);
                    while (matcherComma.find()) {
                        ssBuilder.replace(matcherComma.start(), matcherComma.end(), "\n");
                    }
                    boolean isReplaceAvl = true;
                    Pattern patternDot = Pattern.compile(":");
                    Matcher matcherDot = patternDot.matcher(msg);
                    while (matcherDot.find()) {
                        if (isReplaceAvl)
                            ssBuilder.replace(matcherDot.start(), matcherDot.end(), ":\n");
                        isReplaceAvl = false;
                    }
                    notification_body.setText(ssBuilder);
                }
                else {
                    notification_body.setText(msg);
                }
            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.IPO.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.BOND.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.SGB.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.NFO.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.FD.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.NPS.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.MF.name)
            ){

                if (msg.toLowerCase().contains("click here")) {
                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(msg);
                    ForegroundColorSpan color = new ForegroundColorSpan(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
                    int indexOfCH = msg.toLowerCase().indexOf("click here");
                    ssBuilder.setSpan(color, indexOfCH, indexOfCH + String.valueOf("click here").length() ,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssBuilder.setSpan(new UnderlineSpan(), indexOfCH, indexOfCH + String.valueOf("click here").length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    notification_body.setText(ssBuilder);
                    notification_body.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.IPO.name)) {
                                GlobalClass.showScreen(eScreen.IPO);
                            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.BOND.name)) {
                                GlobalClass.showScreen(eScreen.BONDS);
                            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.SGB.name)) {
                                GlobalClass.showScreen(eScreen.SGB);
                            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.NFO.name)) {
                                GlobalClass.showScreen(eScreen.NFO);
                            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.FD.name)) {
                                GlobalClass.showScreen(eScreen.FD);
                            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.NPS.name)) {
                                GlobalClass.showScreen(eScreen.NPS);
                            }else if(notificationModel.getTitle().equalsIgnoreCase(eMsgType.MF.name)) {
                                if(notificationModel.getMessage().toLowerCase().contains("park & earn")){
                                    GlobalClass.showScreen(eScreen.PARKEARN);
                                }else if(notificationModel.getMessage().toLowerCase().contains("missed sip")){
                                    GlobalClass.showScreen(eScreen.Missed_SIP);
                                }else if(notificationModel.getMessage().toLowerCase().contains("debt")){
                                    GlobalClass.showScreen(eScreen.PERFORMINGFUNDSDEBT);
                                }else if(notificationModel.getMessage().toLowerCase().contains("performing")){
                                    GlobalClass.showScreen(eScreen.PERFORMINGFUNDSEQUITY);
                                }else if(notificationModel.getMessage().toLowerCase().contains("hybrid")){
                                    GlobalClass.showScreen(eScreen.PERFORMINGFUNDSHYBRID);
                                }else if(notificationModel.getMessage().toLowerCase().contains("liquid")){
                                    GlobalClass.showScreen(eScreen.PERFORMINGFUNDSLIQUID);
                                }else if(notificationModel.getMessage().toLowerCase().contains("overseas")){
                                    GlobalClass.showScreen(eScreen.PERFORMINGFUNDSOTHERS);
                                }else {
                                    GlobalClass.showScreen(eScreen.MF);
                                }
                            }
                        }
                    });
                }
                else {
                    notification_body.setText(msg);
                }
            }else {
                notification_body.setText(msg);
            }

            notification_type.setText(notificationModel.getTitle());
            notification_time.setText(DateUtil.DateForNotification(Long.parseLong(notificationModel.getTime())));
            sharebtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    WhatsAppImageShare shareImege = new WhatsAppImageShare();
                    shareImege.createAndShareImage(notificationModel);
                }
            });
            notification_body.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    PopupMenu popup = new PopupMenu(GlobalClass.latestContext, view);
                    popup.getMenuInflater().inflate(R.menu.notification_popup, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.share:
                                    WhatsAppImageShare shareImege = new WhatsAppImageShare();
                                    shareImege.createAndShareImage(notificationModel);
                                    break;
                                case R.id.copy:
                                    copyText(msg);
                                    break;
                                //case R.id.delete:
                                    //deleteNotification(notificationModel.getId());
                                    //break;
                            }
                            return false;
                        }
                    });
                    return false;
                }
            });

            if (notificationModel.getTitle().equalsIgnoreCase(eMsgType.EVENTS.name) ||
                    notificationModel.getTitle().equalsIgnoreCase(eMsgType.SCRIP_RATE.name)){
                notification_body.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                        groupsTokenDetails.scripCode.setValue(notificationModel.getScripCode());
                        groupsTokenDetails.scripName.setValue(notificationModel.getScripName());
                        ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
                        grplist.add(groupsTokenDetails);
                        GlobalClass.openDepth(notificationModel.getScripCode(), eShowDepth.MKTWATCH, grplist, null);
                    }
                });
            }
        }
    }


    public void refreshAdapter() {
        List<NotificationModel> total = new ArrayList<NotificationModel>
                (PreferenceHandler.getNotificationList().values());
        this.notificationList.clear();
        switch (GlobalClass.currentNotificatonTab){
            case 0:
                this.notificationList = total;
                break;
            case 1:
                this.notificationList = getList(total,eMsgType.FNO_TRADE.name,"");
                break;
            case 2:
                this.notificationList = getList(total,eMsgType.CASH_TRADE.name,"");
                break;
            case 3:
                this.notificationList = getList(total,eMsgType.EVENTS.name,eMsgType.SCRIP_RATE.name);
                break;
            case 4:
                this.notificationList = getList(total,eMsgType.RESEARCH_CALL.name,eMsgType.MARGIN_CALL.name);
                break;
            case 5:
                this.notificationList = getList(total,eMsgType.NEWS.name,"");
                break;
            case 6:
                this.notificationList = getList(total,eMsgType.TRADING_CALL.name,"");
                break;
            case 7:
                this.notificationList = getList(total,eMsgType.IPO.name,"");
                break;
            case 8:
                this.notificationList = getList(total,eMsgType.BOND.name,"");
                break;
            case 9:
                this.notificationList = getList(total,eMsgType.SGB.name,"");
                break;
            case 10:
                this.notificationList = getList(total,eMsgType.NFO.name,"");
                break;
            case 11:
                this.notificationList = getList(total,eMsgType.FD.name,"");
                break;
            case 12:
                this.notificationList = getList(total,eMsgType.NPS.name,"");
                break;
            case 13:
                this.notificationList = getList(total,eMsgType.MF.name,"");
                break;
            case 14:
                this.notificationList = getList(total,eMsgType.OTHERS.name,"");
                break;
                default:
                    break;
        }
        Collections.sort(this.notificationList, new Comparator<NotificationModel>() {
            @Override
            public int compare(NotificationModel o1, NotificationModel o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        if(GlobalClass.currentNotificatonTab == 5 && notificationList.size() > 0){
            NotificationModel lastModel = notificationList.get(notificationList.size()-1);
            NotificationModel addExtra = new NotificationModel("","Source : Livesquawk  ",
                    lastModel.getTime(),lastModel.getId(),lastModel.getScripCode(),lastModel.getScripName());
            this.notificationList.add(addExtra);
        }
        notifyDataSetChanged();
    }

    private List<NotificationModel> getList(List<NotificationModel> total,
                                            String name,String alternetName) {
        List<NotificationModel> notificationModelArrayList = new ArrayList<>();
        if (total == null) return notificationModelArrayList;
        for (NotificationModel nf : total){
            if (nf.getTitle().equalsIgnoreCase(name) ||
                    nf.getTitle().equalsIgnoreCase(alternetName)){
                notificationModelArrayList.add(nf);
            }
        }
        return notificationModelArrayList;
    }
}
