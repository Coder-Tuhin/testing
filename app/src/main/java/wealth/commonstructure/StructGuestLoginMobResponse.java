package wealth.commonstructure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Tapas Nayak <tapas@xtremsoftindia.com at Xtremsoft Technologies>
 */
public class StructGuestLoginMobResponse {

    private char charSuccess;    //1.---Success---CHAR---1bytes---
    private char charUserName[];    //2.---UserName---CHAR---40bytes---
    private char charIsUpdateAvailable;  // 3.  ---isUpdateAvailable --CHAR--1byte---
    private char charUserRight;
    private char[] charAuthId;   // 4. --- AuthId ---CHAR ---100 bytes---

    //New Changes in Response --- 23-05-2015
    private char[] charPAN;     //5.---PAN---CHAR---10bytes---
    private char[] charLoginId;     //LoginId---CHAR---15bytes---
    private char charMsg;         //msg---CHAR---1bytes---
    private char[] charServerIp;     //ServerIP---CHAR---15bytes---
    private int intPortWealth;     //PortWealth---INT---4bytes---
    private int intPortBC;         //PortBC---INT---4bytes---
    private int intPortRC;         //PortRC---INT---4bytes---


    public StructGuestLoginMobResponse(byte[] data, int dataLength) {

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

        length = 40;
        charUserName = new char[length];

        for (int i = 0; i < length; i++) {
            charUserName[i] = (char) data[count];
            count++;
        }
        length = 1;

        charIsUpdateAvailable = (char) data[count];
        count++;

        length = 1;

        charUserRight = (char) data[count];
        count++;

        length = 100;
        charAuthId = new char[length];

        for (int i = 0; i < length; i++) {
            charAuthId[i] = (char) data[count];
            count++;
        }

        length = 10;
        charPAN = new char[length];
        for (int i = 0; i < length; i++){
            charPAN[i] = (char) data[count];
            count++;
        }

        length = 15;
        charLoginId = new char[length];
        for (int i = 0; i < length; i++){
            charLoginId[i] = (char) data[count];
            count++;

        }

        charMsg = (char)data[count];
        count++;

        length = 15;
        charServerIp = new char[length];

        for (int i = 0; i < length; i++){
            charServerIp[i] = (char) data[count];
            count++;
        }

        length = 4;
        byte[] portWealthArr = new byte[length];

        for (int i = 0; i < length; i++) {
            portWealthArr[i] = data[count];
            count++;
        }
        intPortWealth = ModuleClass.byteToint(portWealthArr, length);

        length = 4;
        byte[] portBCArr = new byte[length];

        for (int i = 0; i < length; i++) {
            portBCArr[i] = data[count];
            count++;
        }
        intPortBC = ModuleClass.byteToint(portBCArr, length);

        length = 4;
        byte[] portRCArr = new byte[length];

        for (int i = 0; i < length; i++) {
            portRCArr[i] = data[count];
            count++;
        }
        intPortRC = ModuleClass.byteToint(portRCArr, length);


    }

    public char getSuccess() {
        return charSuccess;
    }

    public void setSuccess(char value) {
        charSuccess = value;
    }

    public String getUserName() {
        return new String(charUserName).trim();
    }

    public void setUserName(char[] value) {
        charUserName = value;
    }

    public char getIsUpdateAvailable() {
        return charIsUpdateAvailable;
    }

    public void setIsUpdateAvailable(char value) {
        charIsUpdateAvailable = value;
    }

    public char getUserRight() {
        return charUserRight;
    }

    public void setIsBondDataAvailable(char value) {
        charUserRight = value;
    }

    public String getAuthId() {
        return new String(charAuthId).trim();
    }

    public String getLoginId() {
        return new String(charLoginId).trim();
    }

    public void setLoginId(char[] charLoginId) {
        this.charLoginId = charLoginId;
    }

    public String getPAN() {
        return new String(charPAN).trim();
    }

    public void setPAN(char[] charPAN) {
        this.charPAN = charPAN;
    }

    public char getMsg() {
        return charMsg;
    }

    public void setMsg(char charMsg) {
        this.charMsg = charMsg;
    }

    public String getServerIp() {
        return new String(charServerIp).trim();
    }

    public void setServerIp(char[] charServerIp) {
        this.charServerIp = charServerIp;
    }

    public int getPortWealth() {
        return intPortWealth;
    }

    public void setPortWealth(int intPortWealth) {
        this.intPortWealth = intPortWealth;
    }

    public int getPortBC() {
        return intPortBC;
    }

    public void setPortBC(int intPortBC) {
        this.intPortBC = intPortBC;
    }

    public int getPortRC() {
        return intPortRC;
    }

    public void setPortRC(int intPortRC) {
        this.intPortRC = intPortRC;
    }


}
