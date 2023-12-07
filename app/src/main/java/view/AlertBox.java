package view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import connection.SendDataToBCServer;
import interfaces.OnAlertListener;
import utils.GlobalClass;
import utils.VenturaException;


/**
 * Created by XtremsoftTechnologies on 03/03/16.
 */
public class AlertBox extends AlertDialog.Builder{
    public  static AlertDialog alertDialog;

    public AlertBox(Context context, String positiveBtnName, String negativeBtnName, String msg, final OnAlertListener listener, final String tag) {
        super(GlobalClass.latestContext);
        super.setTitle("Ventura Wealth");
        super.setMessage(msg);
        super.setIcon(R.drawable.ventura_icon);
        super.setCancelable(false);
        super.setPositiveButton(positiveBtnName, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        listener.onOk(tag);
                    }
                }
        ).setNegativeButton(negativeBtnName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        listener.onCancel(tag);
                    }
                });
        createDialog(true);
    }

    public AlertBox(String positiveBtnName, String negativeBtnName, String msg, final OnAlertListener listener, final String tag) {
        super(GlobalClass.latestContext);
        super.setMessage(msg);
        super.setIcon(R.drawable.ventura_icon);
        super.setCancelable(false);
        super.setPositiveButton(positiveBtnName, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onOk(tag);
                        dialog.dismiss();
                    }
                }
        ).setNegativeButton(negativeBtnName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                listener.onCancel(tag);
                dialog.dismiss();
            }
        });
        createDialog(true);
    }

    public AlertBox(Context context, String positiveBtnName, String msg, final OnAlertListener listener, final String tag) {
        super(GlobalClass.latestContext);
        super.setTitle("Ventura Wealth");
        super.setMessage(msg);
        super.setIcon(R.drawable.ventura_icon);
        super.setCancelable(false);
        super.setPositiveButton(positiveBtnName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                listener.onOk(tag);
            }
        });
        createDialog(false);
    }

    public AlertBox(Context context,String msg, final OnAlertListener listener,final String tag) {
        super(GlobalClass.latestContext);
        super.setMessage(msg);
        super.setCancelable(false);
        super.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                listener.onOk(tag);
            }
        });
        createDialog(false);
    }

    public AlertBox(Context context,String title,String pBtn, String Msg, final boolean appExitReq) {
        super(GlobalClass.latestContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            super.setMessage(Html.fromHtml(Msg,Html.FROM_HTML_MODE_LEGACY));
        } else {
            super.setMessage(Html.fromHtml(Msg));
        }
        super.setCancelable(false);
        super.setPositiveButton(pBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (appExitReq){
                 //   android.os.Process.killProcess(android.os.Process.myPid());
                    if ((GlobalClass.tradeBCClient!= null) && (GlobalClass.tradeBCClient.IsBroadcastConnected)){
                        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                        sendDataToServer.sendExitReq();
                    }else {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }
            }
        });
        if (!title.equalsIgnoreCase("")){
            super.setTitle(title);
            super.setIcon(R.drawable.ventura_icon);
            super.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            createDialog(true);
        }else {
            createDialog(false);
        }
    }

    public static void showCustomDialog(View view,Context context) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = view.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.my_dialognotepledge, viewGroup, false);

        TextView textView=dialogView.findViewById(R.id.txtnotePledge);
        TextView textView1=dialogView.findViewById(R.id.txtnotePledge1);
        ImageView imgclose=dialogView.findViewById(R.id.imgclose);
        textView.setText(context.getResources().getString(R.string.notepledge2));
        textView1.setText(context.getResources().getString(R.string.notepledge4));

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });
        //alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void createDialog(final boolean isNegetiveAvailable) {
     try{
         if (GlobalClass.latestContext!=null){
             AppCompatActivity aca = (AppCompatActivity) GlobalClass.latestContext;
             aca.runOnUiThread(() -> {
                 AlertDialog alert = super.create();
                 alert.show();
                 //if (alert.getWindow()!=null)
                     //alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                 alert.getButton(alert.BUTTON_POSITIVE).setTextColor(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
                 if (isNegetiveAvailable){
                     alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
                 }
             });
         }
     }catch (Exception e){
         VenturaException.Print(e);
     }
    }
}
