package models;

import structure.StructShort;

/**
 * Created by XTREMSOFT on 1/21/2017.
 */
public class CheckNotificationSetting {
    public short event;
    public short fno;
    public short cash;
    public short research;
    public short news;

    public CheckNotificationSetting(short event, short fno, short cash, short research, short news) {
        this.event = event;
        this.fno = fno;
        this.cash = cash;
        this.research = research;
        this.news = news;
    }

    public short getEvent() {
        return event;
    }
    public short getFno() {
        return fno;
    }

    public short getCash() {
        return cash;
    }
    public short getResearch() {
        return research;
    }
    public short getNews() {
        return news;
    }


    public boolean isValueChange(short levent,short lcash, short lfno, short lresearchCall, short lnews){
        boolean isChange = false;
        if(!(event == levent && cash == lcash && fno == lfno && research == lresearchCall && news == lnews) ){
            isChange = true;
            event = levent;
            fno = lfno;
            cash = lcash;
            research = lresearchCall;
            news = lnews;
        }
        return isChange;
    }
}
