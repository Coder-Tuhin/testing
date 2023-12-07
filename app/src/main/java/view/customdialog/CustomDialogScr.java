package view.customdialog;

import android.app.Dialog;
import android.content.Context;

import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.WebViewFD;

import java.io.InputStream;

import Structure.Request.BC.ErrorLOG;
import Structure.Response.BC.StructCustomDialog;
import connection.SendDataToBCServer;
import enums.eLogType;
import enums.eScreen;
import fragments.NFO.NFO_Fragment;
import fragments.nps.NPSDashboardFragment;
import fragments.simplysave.SimplysaveFragment;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import wealth.mv.MainPage;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.new_mutualfund.QuickTransactionFragment;
import wealth.new_mutualfund.investments.DIYFilter;
import wealth.new_mutualfund.investments.MissedSIPFragment;
import wealth.new_mutualfund.investments.VenturaTopPicksNewGFragment;
import wealth.new_mutualfund.ipo.ApplyIPOFragment;
import wealth.new_mutualfund.bond.BondDetails;
import wealth.new_mutualfund.sgb.SGBSummaryFragment;

/**
 * Created by XTREMSOFT on 23-Aug-2017.
 */
public class CustomDialogScr extends Dialog implements View.OnClickListener {

    private Button btnPositive, btnNegative;
    private ImageView btnCross;
    private Context context;
    private TextView titleView;
    private TextView msgView;
    private LinearLayout descriptionLayout;
    private LinearLayout imageLayout;
    private ImageView imageFromURL;

    private StructCustomDialog customDialog;

    public CustomDialogScr(Context context, StructCustomDialog customDia) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        try {
            this.context = context;
            this.setContentView(R.layout.custom_dialog);
            this.setCancelable(true);
            this.customDialog = customDia;
            btnPositive = this.findViewById(R.id.custom_dialog_positive);
            btnNegative = this.findViewById(R.id.custom_dialog_negative);
            btnCross = this.findViewById(R.id.custom_dialog_cross);

            btnPositive.setOnClickListener(this);
            btnNegative.setOnClickListener(this);
            btnCross.setOnClickListener(this);

            descriptionLayout = this.findViewById(R.id.descriptionlayout);
            imageLayout = this.findViewById(R.id.imagelayout);

            titleView = this.findViewById(R.id.custom_dialog_title);
            msgView = this.findViewById(R.id.custom_dialog_msg);
            imageFromURL = this.findViewById(R.id.imagecustomdialog);
            if(customDialog.isImageClikable.getValue()){
                imageFromURL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (!customDialog.imageClickLink.getValue().equalsIgnoreCase("")) {
                                CustomDialogScr.this.dismiss();
                                PreferenceHandler.setCustomDialogMaxSL(customDialog.sl.getValue());
                                try {
                                    ErrorLOG log = new ErrorLOG();
                                    log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                                    log.errorMsg.setValue(customDialog.sl.getValue()+"|"+customDialog.title.getValue() + "|imageclick");
                                    log.logType.setValue(eLogType.CUSTOMDIALOG.name);
                                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                                    sendDataToServer.sendAndroidLog(log);
                                }
                                catch (Exception ex){ex.printStackTrace();}
                                openURL(customDialog.imageClickLink.getValue());
                            }
                        }catch (Exception ex){
                            GlobalClass.onError("",ex);
                        }
                    }
                });
            }

            if (!customDia.isCrossBtn.getValue()) {
                btnCross.setVisibility(View.GONE);
            }
            if (customDia.positiveBtn.getValue().equalsIgnoreCase("")) {
                btnPositive.setVisibility(View.GONE);
            } else {
                btnPositive.setText(customDia.positiveBtn.getValue());
            }

            if (customDia.negativeBtn.getValue().equalsIgnoreCase("")) {
                btnNegative.setVisibility(View.GONE);
            } else {
                btnNegative.setText(customDia.negativeBtn.getValue());
            }
            if (customDia.title.getValue().equalsIgnoreCase("")) {
                titleView.setVisibility(View.GONE);
            } else {
                titleView.setText(customDia.title.getValue());
            }

            if (customDia.description.getValue().equalsIgnoreCase("")) {
                msgView.setVisibility(View.GONE);
            } else {
                msgView.setText(customDia.description.getValue());
            }

            if (!customDialog.imageLink.getValue().equalsIgnoreCase("") && customDialog.isShowImage.getValue()) {
                imageLayout.setVisibility(View.VISIBLE);
                descriptionLayout.setVisibility(View.GONE);
                new DownloadImageTask(imageFromURL).execute(customDialog.imageLink.getValue());
            } else {
                imageLayout.setVisibility(View.GONE);
                descriptionLayout.setVisibility(View.VISIBLE);
                displayDialog();
            }
            this.setCanceledOnTouchOutside(true);
            this.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    PreferenceHandler.setCustomDialogMaxSL(customDialog.sl.getValue());
                }
            });
            this.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    PreferenceHandler.setCustomDialogMaxSL(customDialog.sl.getValue());
                }
            });

        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    private void openURL(String url){
        try {
            if(!customDialog.positiveBtnLink.getValue().equalsIgnoreCase("")) {
                if (url.toLowerCase().contains("clientcodeventura")) {
                    url = url.replace("clientcodeventura", UserSession.getLoginDetailsModel().getUserID());
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                GlobalClass.latestContext.startActivity(browserIntent);
            }
        }catch (Exception ex){
            GlobalClass.onError("",ex);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.custom_dialog_cross:
                this.dismiss();
                PreferenceHandler.setCustomDialogMaxSL(customDialog.sl.getValue());
                try {
                    ErrorLOG log = new ErrorLOG();
                    log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    log.errorMsg.setValue(customDialog.sl.getValue()+"|"+customDialog.title.getValue() + "|Cross");
                    log.logType.setValue(eLogType.CUSTOMDIALOG.name);
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendAndroidLog(log);
                }
                catch (Exception ex){ex.printStackTrace();}
                break;
            case R.id.custom_dialog_negative:
                this.dismiss();
                PreferenceHandler.setCustomDialogMaxSL(customDialog.sl.getValue());
                try {
                    ErrorLOG log = new ErrorLOG();
                    log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    log.errorMsg.setValue(customDialog.sl.getValue()+"|"+customDialog.title.getValue() + "|Negative");
                    log.logType.setValue(eLogType.CUSTOMDIALOG.name);
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendAndroidLog(log);
                }
                catch (Exception ex){ex.printStackTrace();}
                break;
            case R.id.custom_dialog_positive:
                this.dismiss();
                PreferenceHandler.setCustomDialogMaxSL(customDialog.sl.getValue());
                eScreen screen = eScreen.fromValue(customDialog.openScreen.getValue());
                try {
                    ErrorLOG log = new ErrorLOG();
                    log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    log.errorMsg.setValue(customDialog.sl.getValue()+"|"+customDialog.title.getValue()+"|Positive");
                    log.logType.setValue(eLogType.CUSTOMDIALOG.name);
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendAndroidLog(log);
                }
                catch (Exception ex){ex.printStackTrace();}
                if (customDialog.openScreen.getValue() > 0) {
                    if(screen == eScreen.LINK_URL){
                        if(!customDialog.positiveBtnLink.getValue().equalsIgnoreCase("")){
                            openURL(customDialog.positiveBtnLink.getValue());
                        }
                    }else{
                        GlobalClass.showScreen(screen);
                    }
                }
                break;
        }
    }

    private void displayDialog() {
        this.show();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            displayDialog();
        }
    }
}
