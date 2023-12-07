package enums;

import java.util.HashMap;
import java.util.Map;

import utils.Constants;

/**
 * Created by XtremsoftTechnologies on 23/02/16.
 */
public enum eMktMover {

    BSE_TOPTRADED(50111,"BSE Cash Top Traded"),// exchange = 1 ,msgCode = 5011
    NSECASH_TOPTRADED(50110,"NSE Cash Top Traded"),// exchange = 0 ,msgCode = 5011
    NSEFO_TOPTRADED(50112,"NSE Fut Top Traded"),// exchange = 2 ,msgCode = 5011
    BSE_BESTMOVERS(50121,"BSE Cash Best Movers"),// exchange = 1 ,msgCode = 5012
    NSECASH_BESTMOVERS(50120,"NSE Cash Best Movers"),// exchange = 0 ,msgCode = 5012
    NSEFO_BESTMOVERS(50122,"NSE Fut Best Movers"),// exchange = 2 ,msgCode = 5012
    BSE_WORSTMOVERS(50131,"BSE Cash Worst Movers"),// exchange = 1 ,msgCode = 5013
    NSECASH_WORSTMOVERS(50130,"NSE Cash Worst Movers"),// exchange = 0 ,msgCode = 5013
    NSEFO_WORSTMOVERS(50132,"NSE Fut Worst Movers");// exchange = 2 ,msgCode = 5013
    //TOPTRADED(7,"Top Traded"), BSESTMOVERS(6,"Best Movers"), WORSTMOVERS(9,"Worst Movers");
    public int value;
    public String name;
    private eMktMover(int value,String name) {
        this.value = value;
        this.name=name;
    }
    private static final Map<Integer, eMktMover> map = new HashMap<Integer, eMktMover>();
    static {
        for (eMktMover type : eMktMover.values()) {
            map.put(type.value, type);
        }
    }
    public static eMktMover valueOf(int value) {
        return map.get(value);
    }
}