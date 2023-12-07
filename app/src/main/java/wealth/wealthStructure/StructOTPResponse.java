package wealth.wealthStructure;

import wealth.commonstructure.ModuleClass;

/**
 * Created by Nirav on 08-May-15.
 */
public class StructOTPResponse {

    private byte byteSuccess;    //1.---Success---BYTE---1byte---
    private char[] charOTPMsg;    //2.---OTPMsg---CHAR[]---50bytes---

    public StructOTPResponse(byte[] data, int dataLength) {

        int length = 2;
        int count = 0;

        byte[] msgLength = new byte[length];

        for (int i = 0; i < length; i++) {
            msgLength[i] = data[count];
            count++;
        }

        short intMsgLength = (short) ModuleClass.byteToshort(msgLength, length);

        byte[] msgCode = new byte[length];

        for (int i = 0; i < length; i++) {
            msgCode[i] = data[count];
            count++;
        }

        short intMsgCode = (short) ModuleClass.byteToshort(msgCode, length);


        byteSuccess = data[count];
        count++;

        length = 50;
        charOTPMsg = new char[length];

        for (int i = 0; i < length; i++) {
            charOTPMsg[i] = (char) data[count];
            count++;
        }
    }

    public String getOTPMsg() {
        return new String(charOTPMsg).trim();
    }

    public void setOTPMsg(char[] value) {
        charOTPMsg = value;
    }

    public byte getSuccess() {
        return byteSuccess;
    }

    public void setSuccess(byte value) {
        byteSuccess = value;
    }


}

