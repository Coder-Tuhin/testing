package connection;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.InetAddress;

import lib.XtremClientHandler;
import structure.TypeConverter;
import utils.GlobalClass;
import utils.VenturaException;


public abstract class Client {

    //region [ Fileds ]
    public XtremClientHandler client;
    boolean isManualDisconnected;

    final String CLIENT = "Client";
    final String CONNECT_TO_SERVER = "Connect to Server";
    final int CORE_POOL = 8;
    final int MAX_POOL = 48;

    final String DISCONNECTED_MSG = "Ventura Wealth have been disconnected from server";
    final String CONNECTED_MSG = "Ventura Wealth connected with server";
    final String CONNECTION_STATUS = "Connection Status!";

    public boolean IsBroadcastConnected = false;
    //endregion

    //region [ Public Method ]
    public void connect( String ip, int port ){
       // InetAddress address = InetAddress.getByName(ip);
        if(client != null) {
            client.disconnect();
            GlobalClass.log("IP : " + ip + " : " + port);
            client.connect("Android Client", ip, port);
        }
    }

    public void disconnect(boolean isManualDisconnected) throws IOException {
        if (client != null) {
            this.isManualDisconnected = isManualDisconnected;
            client.disconnect();
        }
    }

    public void send( byte data[],int msgCode )throws Exception{
        if(client!=null && data!=null && data.length>0){
            client.sendData(getByteArr((short)msgCode,data));
        }
    }

    public void send(byte data[])throws Exception{
        if(client!=null && data!=null && data.length>0){
            client.sendData(data);
        }
    }

    public  byte[] getByteArr( int iMsgCode, byte orgData[] ) throws Exception {
        try {
            byte[] msgHdr = new byte[4];
            short msgLen = (short) (orgData.length + 4);
            System.arraycopy(TypeConverter.shortToByte(msgLen), 0, msgHdr, 0, 2);
            System.arraycopy(TypeConverter.shortToByte((short)iMsgCode), 0, msgHdr, 2, 2);
            byte[] byteData = new byte[msgLen];
            System.arraycopy(msgHdr, 0, byteData, 0, msgHdr.length);
            System.arraycopy(orgData, 0, byteData, 4, msgLen - 4);
            return byteData;
        }catch (Exception e){
            throw new Exception("Byte Parse Exception inside Client");
        }
    }
    //endregion
}
