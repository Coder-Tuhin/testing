package wealth.commonstructure;

/**
 * Created by Administrator on 3/5/14.
 */
public class Structsessioncheck {
    private char charStatusMessage[];    //1.---StatusMessage---CHAR---60bytes---;

    public Structsessioncheck(byte[] data, int dataLength) {

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

        byte[] checkstatus = new byte[length];

        for (int i = 0; i < length; i++) {
            checkstatus[i] = data[count];
            count++;
        }

        short intCheckStatus = (short) ModuleClass.byteToshort(checkstatus, length);

        length = 60;
        charStatusMessage = new char[length];

        for (int i = 0; i < length; i++) {
            charStatusMessage[i] = (char) data[count];
            count++;
        }

    }

    public String getStatus() {
        return new String(charStatusMessage).trim();
    }
}
