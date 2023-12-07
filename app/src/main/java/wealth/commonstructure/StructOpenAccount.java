package wealth.commonstructure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Tapas Nayak <tapas@xtremsoftindia.com at Xtremsoft Technologies>
 */
public class StructOpenAccount {

    private char charSuccess;    //1.---Success---CHAR---1bytes---

    private char[] charMsg;         //msg---CHAR---120bytes---

    public StructOpenAccount(byte[] data, int dataLength) {

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

        length = 1;

        charSuccess = (char) data[count];
        count++;

        length = 120;
        charMsg = new char[length];

        for (int i = 0; i < length; i++) {
            charMsg[i] = (char) data[count];
            count++;
        }


    }

    public char getSuccess() {
        return charSuccess;
    }

    public void setSuccess(char value) {
        charSuccess = value;
    }


    public String getMsg() {
        return new String(charMsg);
    }

    public void setMsg(char[] charMsg) {
        this.charMsg = charMsg;
    }




}
