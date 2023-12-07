package wealth.commonstructure;

/**
 * Created by Admin on 21/08/2014.
 */
public class StructBenificiaryDpId {

    private short shortNoOfBenificiaryRow;    //1.---onOfBenificiaryRow---short---2bytes---
    private short shortNoOfDpRow;    //1.---onOfDpRow---short---2bytes---
    private char[] charBenificiaryId; //2.---BenificiaryId--Char-20bytes
    private String[] stringBenificiaryId;
    private char[] charDpId; //2.---DpId--Char-20bytes
    private char[] charDpName; //2.---DpName--Char-20bytes
    private String[] stringDpId;
    private String[] stringDpName;
    //total length 64

    public StructBenificiaryDpId(byte[] data, int dataLength) {

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


       /* byte [] noOfRow = new byte[length];

        for(int i = 0; i < length; i++)
        {
            noOfRow [i] = data[count];
            count++;
        }

        shortNoOfBenificiaryRow = (short) ModuleClass.byteToshort(noOfRow, length);
        GlobalClass.log("noofBenificiaryRow : " + shortNoOfBenificiaryRow);
*/
        byte[] noOfRow = new byte[length];

        for (int i = 0; i < length; i++) {
            noOfRow[i] = data[count];
            count++;
        }

        shortNoOfDpRow = (short) ModuleClass.byteToshort(noOfRow, length);

        stringBenificiaryId = new String[shortNoOfDpRow];
        stringDpId = new String[shortNoOfDpRow];
        stringDpName = new String[shortNoOfDpRow];

        for (int i = 0; i < shortNoOfDpRow; i++) {

            length = 20;
            charBenificiaryId = new char[length];

            for (int j = 0; j < length; j++) {
                charBenificiaryId[j] = (char) data[count];
                count++;
            }
            stringBenificiaryId[i] = new String(charBenificiaryId).trim();
            length = 20;
            charDpId = new char[length];

            for (int j = 0; j < length; j++) {
                charDpId[j] = (char) data[count];
                count++;
            }
            stringDpId[i] = new String(charDpId).trim();


            length = 30;
            charDpName = new char[length];

            for (int j = 0; j < length; j++) {
                charDpName[j] = (char) data[count];
                count++;
            }
            stringDpName[i] = new String(charDpName).trim();


        }
    }


    public String[] getStringBenificiaryId() {
        return stringBenificiaryId;
    }

    public String[] getStringDpId() {
        return stringDpId;
    }

    public String[] getStringDpName() {
        return stringDpName;
    }
}
