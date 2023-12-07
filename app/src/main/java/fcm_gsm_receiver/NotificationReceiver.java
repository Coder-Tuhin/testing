package fcm_gsm_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.activities.splash.SplashActivity;

import java.util.Date;

import enums.eMessageCode;
import utils.GlobalClass;
import utils.StaticVariables;
import utils.VenturaException;

/**
 * Created by XtremsoftTechnologies on 13/06/16.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            GlobalClass.log("NotificationReceiver : " + new Date());
            if (GlobalClass.homeScrUiHandler!=null){
                GlobalClass.sendMsg(GlobalClass.homeScrUiHandler,eMessageCode.NOTIFICATION.value);
            }else if (GlobalClass.homeActivity != null){ //Resume or Not
                //GlobalClass.homeActivity.move
                Intent launchIntent = new Intent(context, HomeActivity.class);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
               // launchIntent.putExtra(StaticVariables.FROM_NOTIFICATION_WHENPAUSE,true);
                HomeActivity.FROM_NOTIFICATION_WHENPAUSE = true;
                context.startActivity(launchIntent);
            }else {
                /*
                Intent launchIntent = new Intent();
                launchIntent.setClassName("com.ventura.venturawealth", "com.ventura.venturawealth.activities.splash.SplashActivity");

                //Intent launchIntent = new Intent(context.getApplicationContext(), SplashActivity.class);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.putExtra(StaticVariables.FROM_NOTIFICATION,true);
                context.startActivity(launchIntent);*/
                GlobalClass.log("NotificationReceiver1 : " + new Date());
                Intent notifyIntent = new Intent(VenturaApplication.getContext(), SplashActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                notifyIntent.putExtra(StaticVariables.FROM_NOTIFICATION,true);
                VenturaApplication.getContext().startActivity(notifyIntent);
                GlobalClass.log("NotificationReceiver2 : " + new Date());
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
}
