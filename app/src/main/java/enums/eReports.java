package enums;

import java.util.ArrayList;

/**
 * Created by SUDIP on 08-12-2016.
 */
public enum eReports {
    MARGINE("Margin",0),
    ORDERBOOK("Order Book",1),
    //BRACKET_ORDERBOOK("Bracket Order Book",2),
    TRADEBOOK("Trade Book",2),
    NET_POSITION("Day Position",3),
    BRACKET_POSITIONBOOK("Bracket Position Book",4),
    HOLDINGE_QUITY("Holding Equity",5),
    DERIVATIVE_NET_OBLIGATION("Derivative Net Obligation",6),
    BOD_SECURITIES_LENT_REPORT("BOD Securities Lent Report",7),
    MARGINE_TRADE("Margin Trading Fund",8),
    //CHANGE_PIN("Change PIN",9),
    TOTAL_MSG("Messages",10),
    EXCH_MSG("Exchange Messages",11);

    public String name;
    public int value;

    eReports(String name,int value){
        this.name = name;
        this.value = value;
    }

    public static ArrayList<String> getReports(){
        ArrayList<String> mList = new ArrayList<>();
        for (eReports b : eReports.values()) {
            mList.add(b.name);
        }
        return mList;
    }

    public static eReports fromString(String text) {
        eReports _tempReports = MARGINE;
        for (eReports b : eReports.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                _tempReports = b;
                break;
            }
        }
        return _tempReports;
    }
}
