package models;

import enums.eMsgType;
import utils.DateUtil;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/19/2017.
 */
public class NotificationModel {
    private String title = "";
    private String message = "";
    private String time = "";
    private String id = "";
    private int scripCode = 0;
    private String ScripName = "";
    public int count = 0;

    public NotificationModel(String title, String message, String time,
                             String id, int scripCode, String scripName) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.id = id;
        this.scripCode = scripCode;
        ScripName = scripName;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getIDForSaving(){
        return message +"_"+DateUtil.DateForNotification(Long.parseLong(getTime()));
    }
    public String getId() {
        return id;
    }

    public int getScripCode() {
        return scripCode;
    }

    public String getScripName() {
        if(ScripName.equalsIgnoreCase("")){
            String[] has = message.split("has");
            if(has.length > 0) {
                ScripName = has[0];
            }
        }
        return ScripName;
    }
    public int getTabSelection(){
        if(title.equalsIgnoreCase(eMsgType.FNO_TRADE.name)){
            return 1;
        }else if(title.equalsIgnoreCase(eMsgType.CASH_TRADE.name)){
            return 2;
        }else if(title.equalsIgnoreCase(eMsgType.EVENTS.name) || title.equalsIgnoreCase(eMsgType.SCRIP_RATE.name) ){
            return 3;
        }else if(title.equalsIgnoreCase(eMsgType.RESEARCH_CALL.name) || title.equalsIgnoreCase(eMsgType.MARGIN_CALL.name)){
            return 4;
        }else if(title.equalsIgnoreCase(eMsgType.NEWS.name)){
            return 5;
        }else if(title.equalsIgnoreCase(eMsgType.TRADING_CALL.name)){
            return 6;
        }else if(title.equalsIgnoreCase(eMsgType.IPO.name)){
            return 7;
        }else if(title.equalsIgnoreCase(eMsgType.BOND.name)){
            return 8;
        }else if(title.equalsIgnoreCase(eMsgType.SGB.name)){
            return 9;
        }else if(title.equalsIgnoreCase(eMsgType.NFO.name)){
            return 10;
        }else if(title.equalsIgnoreCase(eMsgType.FD.name)){
            return 11;
        }else if(title.equalsIgnoreCase(eMsgType.NPS.name)){
            return 12;
        }else if(title.equalsIgnoreCase(eMsgType.MF.name)){
            return 13;
        }else if(title.equalsIgnoreCase(eMsgType.OTHERS.name)){
            return 14;
        }else{
            return 0;
        }
    }
}