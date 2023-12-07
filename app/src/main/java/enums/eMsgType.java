package enums;

import java.util.ArrayList;

/**
 * Created by XTREMSOFT on 1/19/2017.
 */
public enum eMsgType {
    CASH_TRADE(1,"Cash Trade Confirmation"),
    FNO_TRADE(2,"FNO Trade Confirmation"),
    EVENTS(3,"Event"),
    RESEARCH_CALL(4,"Research Call"),
    MARGIN_CALL(5,"Margin Call"),
    SCRIP_RATE(6,"Scrip Rate"),
    NEWS(7,"News"),
    TRADING_CALL(8,"Trading Calls"),
    OTHERS(9,"Others"),
    RESEARCH_IDEA(10,"ResearchIdea"),
    IPO(11,"IPO"),
    BOND(12,"BOND"),
    SGB(13,"SGB"),
    NFO(14,"NFO"),
    FD(15,"FD"),
    NPS(16,"NPS"),
    MF(17,"MF"),
    ;

    public short value;
    public String name;

    eMsgType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }

    public static ArrayList<String> getList(){
        ArrayList<String> mList = new ArrayList<>();
        for (eMsgType emt : eMsgType.values()){
            mList.add(emt.name);
        }
        return mList;
    }
    public static eMsgType getEnumByName(String code){
        for(eMsgType e : eMsgType.values()){
            if(e.name.equalsIgnoreCase(code)) return e;
        }
        return null;
    }
    public static eMsgType getEnumByValue(int val){
        for(eMsgType e : eMsgType.values()){
            if(e.value == val) return e;
        }
        return null;
    }
}
