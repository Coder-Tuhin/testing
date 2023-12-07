package models;

/**
 * Created by XTREMSOFT on 1/12/2017.
 */
public class HelpModel {
    private long lastTimestamp;
    private int loginCount;

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }
}
