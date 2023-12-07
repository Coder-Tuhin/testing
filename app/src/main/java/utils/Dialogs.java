package utils;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ventura.venturawealth.R;

public class Dialogs {

    private static AlertDialog msgDialog;

    public static void showMsg(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getString(R.string.app_name));
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismissMsg();
            }
        });
        msgDialog = builder.create();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgDialog.show();
                msgDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ScreenColor.VENTURA);
            }
        });
    }

    private static void dismissMsg(){
        if (msgDialog != null){
            msgDialog.dismiss();
            msgDialog = null;
        }
    }



    private static AppCompatActivity getActivity(){
        return (AppCompatActivity) GlobalClass.latestContext;
    }
}
