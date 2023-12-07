package wealth.commonstructure;

import android.util.Log;

import structure.TypeConverter;


/**
 * Created by XPC on 25-01-2016.
 */
public class StructAlertStatusResponse {
    private char[] charStatusMsg;        //1.---StatusMsg---CHAR---50byts---

    public StructAlertStatusResponse(byte[] data, int dataLength) {
        int count = 0;
        int length = 2;

        //First Two bytes -- Message Length
        byte[] msgLength = new byte[length];

        for (int i = 0; i < length; i++) {
            msgLength[i] = data[count];
            count++;
        }

        short intMsgLength = (short) TypeConverter.byteToShort(msgLength);

        if (intMsgLength == dataLength) {
            //Log.v("", "Message Length varified...");
        } else {
            Log.v("", "StructMobChangePasswordResponse :: Message Length error...");
        }

        byte[] msgCode = new byte[length];

        for (int i = 0; i < length; i++) {
            msgCode[i] = data[count];
            count++;
        }

        short intMsgCode = (short) TypeConverter.byteToShort(msgCode);

        length = 50;
        charStatusMsg = new char[50];

        for (int i = 0; i < length; i++) {
            charStatusMsg[i] = (char) data[count];
            count++;
        }
    }


    public String getStatusMsg() {
        //return new String[charStatusMsg];
        return new String(charStatusMsg).trim();
    }

    public void setStatusMsg(char[] value) {
        charStatusMsg = value;
    }
}

