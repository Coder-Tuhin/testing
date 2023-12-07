package com.ventura.venturawealth;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.core.app.ActivityCompat;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;
    private FingerprintInterface fingerInterface;
    private CancellationSignal cancellationSignal;
    // Constructor
    public FingerprintHandler(Context mContext,FingerprintInterface fingerInterface) {
        this.context = mContext;
        this.fingerInterface = fingerInterface;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void cancelAuth(){
        try {
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
        }
        catch (Exception ex){ex.printStackTrace();}
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        //this.update("Fingerprint Authentication error\n" + errString, false);
        fingerInterface.onAuthError();
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        //this.update("Fingerprint Authentication help\n" + helpString, false);
        fingerInterface.onAuthHelp();
    }


    @Override
    public void onAuthenticationFailed() {
        //this.update("Fingerprint Authentication failed.", false);
        fingerInterface.onAuthFailed();
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        //this.update("Fingerprint Authentication succeeded.", true);
        fingerInterface.onAuthSucceeded();

    }


    public void update(String e, Boolean success){
//        TextView textView = (TextView) ((Activity)context).findViewById(R.id.errorText);
//        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.icon);
//
//        textView.setText(e);
//        if(success){
//            //imageView.setBackgroundResource(R.drawable.right);
//            imageView.setImageResource(R.drawable.right);
//            textView.setTextColor(ContextCompat.getColor(GlobalClass.latestContext,R.color.green1));
//        }else{
//            textView.setTextColor(ContextCompat.getColor(GlobalClass.latestContext,R.color.red));
//            //imageView.setBackgroundResource(R.drawable.wrong);
//            imageView.setImageResource(R.drawable.wrong);
//        }
    }
}
