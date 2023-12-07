package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import interfaces.SmsListener;

/**
 * Created by XTREMSOFT on 11/17/2016.
 */
public class IncomingSMS extends BroadcastReceiver {
    private static SmsListener mListener;
    public void onReceive(Context context, Intent intent) {
        GlobalClass.log("jhdsf"," :onReceive: ","onReceive..........................");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody().toLowerCase();
                    String otp = "Your verification code for registration is: ".toLowerCase();
                    String forgot_pwd = "Dear Customer, your verification code for password retrieval is".toLowerCase();
                    if (message.contains(otp)) {
                        if(mListener != null) {
                            mListener.messageReceived(message.replace(otp, "").trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
