package view.help;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ventura.venturawealth.BuildConfig;
import com.ventura.venturawealth.R;

import java.util.Arrays;
import java.util.List;

import connection.SendDataToBCServer;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 23-Aug-2017.
 */
public class HomeHelpScreen extends Dialog implements View.OnClickListener{
    private Button btnPrevious,btnNext,btnGot;
    private LinearLayout buttonLayout;
    private FrameLayout helpFrame;
    private View helpOne,helpTwo,helpThree,whatsnew;
    private int countNo;
    private boolean fromNavigation = false;
    private Context context;

    public HomeHelpScreen(Context context,boolean fromNavigation,boolean onlyHelpScreen) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        try {
            this.context = context;
            this.setContentView(R.layout.help_screen);
            this.setCancelable(false);
            btnPrevious = this.findViewById(R.id.btnPrev);
            btnNext = this.findViewById(R.id.btnNext);
            btnGot = this.findViewById(R.id.btnGot);
            buttonLayout = this.findViewById(R.id.buttonLayout);
            helpFrame = this.findViewById(R.id.helpFrame);
            whatsnew = LayoutInflater.from(context).inflate(R.layout.whatsnew_screen, null);
            if (!onlyHelpScreen){
                helpOne = LayoutInflater.from(context).inflate(R.layout.homehelp_one, null);
                helpTwo = LayoutInflater.from(context).inflate(R.layout.homehelp_two, null);
                helpThree = LayoutInflater.from(context).inflate(R.layout.homehelp_three, null);
                btnPrevious.setOnClickListener(this);
                btnGot.setOnClickListener(this);
                btnNext.setOnClickListener(this);
                countNo = fromNavigation?1:PreferenceHandler.getLoginCount();
                this.fromNavigation = fromNavigation;
                setView(countNo);
            }else {
              showWhatsNew();
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPrev:
                if(countNo>1){
                    countNo--;
                    setView(countNo);
                }
                break;
            case R.id.btnGot:
                if (!fromNavigation){
                    PreferenceHandler.setLoginCount(PreferenceHandler.getLoginCount()+1);
                }
                if (PreferenceHandler.isWhatsNewShowable(BuildConfig.VERSION_NAME)){
                    showWhatsNew();
                    return;
                }
                this.dismiss();
                break;
            case R.id.btnNext:
                if (countNo<3){
                    countNo++;
                    setView(countNo);
                }
                break;
            case R.id.gotIt:
                this.dismiss();
                //((HomeActivity)context).OpenActivateMargin();
                startPowerSaverIntent();
                break;
        }
    }

    private void showWhatsNew(){
        try {
            helpFrame.removeAllViews();
            buttonLayout.setVisibility(View.GONE);
            helpFrame.addView(whatsnew);
            (whatsnew.findViewById(R.id.gotIt)).setOnClickListener(this);
            this.setOnKeyListener((arg0, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    HomeHelpScreen.this.dismiss();
                    startPowerSaverIntent();
                }
                return true;
            });
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private void setView(int number){
        helpFrame.removeAllViews();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnNext.setVisibility(View.VISIBLE);
        btnGot.setVisibility(View.VISIBLE);
        btnPrevious.setVisibility(View.VISIBLE);
        switch (number){
            case 1:
                helpFrame.addView(helpOne);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                btnPrevious.setVisibility(View.INVISIBLE);
                break;
            case 2:
                helpFrame.addView(helpTwo);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 3:
                helpFrame.addView(helpThree);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                btnNext.setVisibility(View.INVISIBLE);
                break;
            default:
                helpFrame.addView(helpOne);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                btnPrevious.setVisibility(View.INVISIBLE);
                break;
        }
        buttonLayout.setLayoutParams(lp);
    }

    public void startPowerSaverIntent() {
        try {
            if (PreferenceHandler.isStartPowerSaverIntent()) {

                boolean foundCorrectIntent = false;
                for (final Intent intent : POWERMANAGER_INTENTS) {
                    if (isCallable(context, intent)) {
                        foundCorrectIntent = true;
                        final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                        dontShowAgain.setText("Do not show again");
                        dontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) ->
                                PreferenceHandler.setStartPowerSaverIntent(isChecked));

                        new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setTitle(Build.MANUFACTURER + " Protected Apps")
                                .setMessage(String.format("%s requires to be enabled in 'Protected Apps'" +
                                        " to function properly.%n", context.getString(R.string.app_name)))
                                .setView(dontShowAgain)
                                .setPositiveButton("Go to settings", (dialog, which) -> context.startActivity(intent))
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                        break;
                    }
                }
                if (!foundCorrectIntent) {
                    PreferenceHandler.setStartPowerSaverIntent(true);
                }
            }
        }catch (Exception ex){
            VenturaException.Print(ex);
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );
}
