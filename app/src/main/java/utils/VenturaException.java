package utils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.ventura.venturawealth.BuildConfig;

public class VenturaException {
    public static void Print(Exception e){
        e.printStackTrace();
        /*
        try {
            //UserExperior.sendException(e, "");
            if (BaseActivity.getActivity().getCrashlytics() != null)
                BaseActivity.getActivity().getCrashlytics().recordException(e);
        }catch (Exception ex){ex.printStackTrace();}*/

        try {
            //UserExperior.sendException(e, "");
            if (getCrashlytics() != null)
                getCrashlytics().recordException(e);
        }catch (Exception ex){ex.printStackTrace();}
    }

    public static void PrintLog(String tag,String log){
        try {
            /*if(!log.equalsIgnoreCase("")) {
                UserExperior.logMessage(tag + log);
            }*/
            /*if(BaseActivity.getActivity().getCrashlytics() != null) {
                BaseActivity.getActivity().getCrashlytics().log(log);
                BaseActivity.getActivity().getCrashlytics().recordException(new Exception(tag));
            }*/
            if(getCrashlytics() != null) {
                getCrashlytics().log(log);
                getCrashlytics().recordException(new Exception(tag));
            }
        }catch (Exception ex){ex.printStackTrace();}
    }

    public static void SSOPrintLog(String tag,String log){
        try {

            if(getCrashlytics() != null) {
                getCrashlytics().log(log);
                getCrashlytics().recordException(new Exception(tag));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static FirebaseCrashlytics mCrashlytics;
    public static FirebaseCrashlytics getCrashlytics(){
        if(mCrashlytics == null) {

            mCrashlytics = FirebaseCrashlytics.getInstance();
            // Add some custom values and identifiers to be included in crash reports
            mCrashlytics.setCustomKey("Build Version", String.valueOf(BuildConfig.VERSION_CODE));
            String _tepuser = UserSession.getLoginDetailsModel().getUserID().isEmpty() ? "Non Login" : UserSession.getLoginDetailsModel().getUserID();
            mCrashlytics.setUserId(_tepuser);
            mCrashlytics.setCustomKey("IP", MobileInfo.getIPAddress(true));
            mCrashlytics.setCrashlyticsCollectionEnabled(true);
        }
        return mCrashlytics;
    }
}
