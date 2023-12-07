package wealth.commonstructure;

/**
 * Created by Administrator on 2/18/14.
 */
public class servicesStructure {
    private short shortNoOfRow;    //1.---onOfRow---short---2bytes---
    private char[] charServiceName;//10bytes
    private String[] ServiceName;

    public servicesStructure(byte[] data, int dataLength) {
        try {
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


            byte[] noOfRow = new byte[length];

            for (int i = 0; i < length; i++) {
                noOfRow[i] = data[count];
                count++;
            }

            shortNoOfRow = (short) ModuleClass.byteToshort(noOfRow, length);
            ServiceName = new String[shortNoOfRow];

            for (int i = 0; i < shortNoOfRow; i++) {

                length = 10;
                charServiceName = new char[10];

                for (int j = 0; j < length; j++) {

                    charServiceName[j] = (char) data[count];
                    count++;
                }
                ServiceName[i] = new String(charServiceName).trim();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public String[] getServiceName() {
        return ServiceName;
    }
}
