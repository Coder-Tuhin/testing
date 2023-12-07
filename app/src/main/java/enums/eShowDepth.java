package enums;

/**
 * Created by XTREMSOFT on 8/19/2016.
 */
public enum eShowDepth {
    MKTWATCH(0,"MktWatch"),
    ORDERBOOK(1,"Order Book"),
    NETPOSITION(2,"NetPosition"),
    HOLDINGEQUITY(3,"HoldingEquity"),
    HOLDINGFO(4,"HoldingFNO"),
    BRACKETPOS(5,"BracketPosition"),
    SLBMHOLDING(6,"SlbmHolding"),
    TRADEBOOK(7,"Trade Book");

    public long id;
    public String name;
    private eShowDepth(long id, String name) {
        this.id = id;
        this.name=name;
    }
}
