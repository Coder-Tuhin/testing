package utils;

import android.content.Context;

import java.util.Date;

/**
 * Created by XTREMSOFT on 4/15/2017.
 */
public class RefreshTimer {
    private int refreshTime;
    private Context context;
    private int refreshAfterSeconds;
    // private SwipeRefreshLayout refreshLayout;

    public RefreshTimer(Context context){
        refreshTime = 0;
        this.context = context;
        refreshAfterSeconds = 30;
    }
    public RefreshTimer(){
        this(null);
    }
    public boolean isRefresh(){
        boolean flag = false;
        int currTime = (int)(new Date().getTime()/1000);
        int diff = currTime - refreshTime;
        if( diff >= refreshAfterSeconds ){
            //refreshLayout.setRefreshing(true);
            refreshTime = currTime;
            flag = true;
        }else{
            // refreshLayout.setRefreshing(false);
            GlobalClass.showToast(context,"Next refresh is allowed after "
                    + (refreshAfterSeconds - diff) +" seconds.");
        }
        return flag;
    }

    public int getRefreshAfterSeconds() {
        return refreshAfterSeconds;
    }

    public void setRefreshAfterSeconds(int refreshAfterSeconds) {
        this.refreshAfterSeconds = refreshAfterSeconds;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }
}
