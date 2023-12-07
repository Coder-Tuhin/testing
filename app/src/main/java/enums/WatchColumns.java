package enums;

import java.util.ArrayList;

import models.GrabberModel;

public enum  WatchColumns {
    CURRENT("Current",true,1),
    CHG("%Chg",true,2),
    QTY("Qty",false,3),
    BID_QTY("BidQty",false,4),
    BID("Bid",false,5),
    OFFER("Offer",false,6),
    OPEN("Open",false,7),
    OFF_QTY("OffQty",false,8),
    HIGH("High",false,9),
    LOW("Low",false,10),
    P_CLOSE("PClose",false,11),
    CHANGE("Change",false,12),
    TICK_AVG("TickAvg",false,13),
    OPEN_INT("OpenInt",false,14),
    VALUE("Value",false,15),
    TIME("Time",false,16),
    UPPER_CKT("UpperCkt",false,19),
    LOWER_CKT("LowerCkt",false,20),
    OIHIGH("OIHigh",false,21),
    OILOW("OILow",false,22),
    SCRIPCODE("Scripcode",false,23),
    ;
    public String name;
    public boolean isChecked;
    public int id;
    WatchColumns(String name,boolean isChecked,int id){
        this.name = name;
        this.isChecked = isChecked;
        this.id = id;
    }

    public static ArrayList<GrabberModel> getDefaultList(){
        ArrayList<GrabberModel> mList = new ArrayList<>();
        for (WatchColumns wc:WatchColumns.values()){
            mList.add(new GrabberModel(wc.name,wc.isChecked));
        }
        return mList;
    }
    public static boolean isCollumnAvl(GrabberModel model){
        for (WatchColumns wc:WatchColumns.values()){
            if(model.getGrabbername().equalsIgnoreCase(wc.name)){
                return true;
            }
        }
        return false;
    }

    public static int getID(String name){
        int tempID = 0;
        for (WatchColumns wc:WatchColumns.values()){
            if (wc.name.equals(name)){
                tempID = wc.id;
                break;
            }
        }
        return tempID;
    }

}
