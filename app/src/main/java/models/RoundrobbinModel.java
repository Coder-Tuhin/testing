package models;

import enums.eMsgType;

/**
 * Created by XTREMSOFT on 18-Dec-2017.
 */


public class RoundrobbinModel {
    public String cash = eMsgType.CASH_TRADE.name;
    public short fno = 1;
    public short event = 1;
    public short reserch = 1;
    public short margin = 1;
    public short scrip = 1;
    public short news = 1;
    public short trading = 1;
}
