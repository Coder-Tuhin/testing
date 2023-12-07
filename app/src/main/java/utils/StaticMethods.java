package utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import view.ios_progress.IOSProgress;

public class StaticMethods {

    // MESSAGE DIALOG
    public static void showMessageDialog(Context ctx, String message,boolean popBack) {
       new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton("OK", getPositiveBtnListener(popBack))
                .create()
                .show();
    }

    private static DialogInterface.OnClickListener getPositiveBtnListener(final boolean popBack){
        return (dialog, which) -> {
          dialog.dismiss();
          if (popBack) GlobalClass.fragmentManager.popBackStackImmediate();
        };
    }

    //IOS PROGRESS
    private static IOSProgress iosProgress;

    public static void showIosProgress(){
        try {

            dismissIosProgress();
            iosProgress =   IOSProgress.create(GlobalClass.latestContext)
                    .setStyle(IOSProgress.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.0f)
                    .show();
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public static void dismissIosProgress(){
        try{
            if (iosProgress != null){
                iosProgress.dismiss();
                iosProgress = null;
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }


    public static int StringToInt(String str) {
        if (!TextUtils.isEmpty(str.trim())){
            try{
                str = str.replaceAll(",", "");
                return Integer.parseInt(str);
            }catch (Exception e){
                VenturaException.Print(e);
            }
        }
        return 0;
    }

    public static Double StringToDouble(String str) {
        if (!TextUtils.isEmpty(str.trim())){
            try{
                str = str.replaceAll(",", "");
                return Double.parseDouble(str);
            }catch (Exception e){
                VenturaException.Print(e);
            }
        }
        return 0.0;
    }


    public static void SetVisibleHideForScroll(ScrollView scrollView,View target){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                scrollView.setOnScrollChangeListener((view14, i, i1, i2, i3) -> {
                    View _endView = scrollView.getChildAt(scrollView.getChildCount() - 1);
                    int _diff = (_endView.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                    if (_diff <50){
                        target.setVisibility(View.GONE);
                    }else if (target.getVisibility() == View.GONE){
                        target.setVisibility(View.VISIBLE);
                    }
                });
            }else {
                scrollView.getViewTreeObserver().addOnScrollChangedListener(
                        () -> {
                            View _endView = scrollView.getChildAt(scrollView.getChildCount() - 1);
                            int _diff = (_endView.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                            if (_diff <50){
                                target.setVisibility(View.GONE);
                            }else if (target.getVisibility() == View.GONE){
                                target.setVisibility(View.VISIBLE);
                            }
                        });
            }
            target.setOnClickListener(view ->{
                View _endView = scrollView.getChildAt(scrollView.getChildCount() - 1);
                scrollView.scrollTo(0,_endView.getBottom());
            });
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
    public static int getStringToInt(String str){
        try {

            if (!TextUtils.isEmpty(str)){
                String _tempStr = str.replaceAll(",","");
                return Integer.parseInt(_tempStr);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return 0;
    }
    public static double getStringToDouble(String str){
        try {
            if (!TextUtils.isEmpty(str)){
                String _tempStr = str.replaceAll(",","");
                return Double.parseDouble(_tempStr);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return 0.00;
    }
    public static void PopupDimBehind(PopupWindow popupWindow) {
        try{
            View container;
            if (popupWindow.getBackground() == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    container = (View) popupWindow.getContentView().getParent();
                } else {
                    container = popupWindow.getContentView();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    container = (View) popupWindow.getContentView().getParent().getParent();
                } else {
                    container = (View) popupWindow.getContentView().getParent();
                }
            }
            Context context = popupWindow.getContentView().getContext();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.6f;
            assert wm != null;
            wm.updateViewLayout(container, p);
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public static JSONArray getJSONarray(String tag, JSONObject object){
        try {
            if (object.has(tag)){
                return object.optJSONArray(tag);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return new JSONArray();
    }

    public static String getString(String tag, JSONObject object){
        try {
            if (object.has(tag)){
                return object.optString(tag);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "";
    }
}
