package wealth;

import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import Structure.Response.AuthRelated.ClientLoginResponse;
import enums.eMFClientType;
import enums.eMessageCodeWealth;
import utils.Connectivity;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.UserSession;
import utils.VenturaException;
import wealth.bondstructure.StructBondAvailableBalance;
import wealth.commonstructure.Structsessioncheck;
import wealth.new_mutualfund.Structure.MFObjectHolder;
import wealth.new_mutualfund.Structure.StructNewMFReq;
import wealth.new_mutualfund.Structure.StructNewMFRespnse;
import wealth.wealthStructure.HoldingDateCheckRequest;
import wealth.wealthStructure.RegistrationRequest;
import wealth.wealthStructure.StructBondEquityDepositoryDetail;
import wealth.wealthStructure.StructBondEquityDepositoryDetailNew;
import wealth.wealthStructure.StructFNODepositoryDetail;
import wealth.wealthStructure.StructFamilyCodesDetail;
import wealth.wealthStructure.StructFixedDepositDetail;
import wealth.wealthStructure.StructHoldingDateCheckingRes;
import wealth.wealthStructure.StructHoldingShortLongTermDetail;
import wealth.wealthStructure.StructMyWealth;
import wealth.wealthStructure.StructOpenPositionDetail;
import wealth.wealthStructure.StructPhysicalDepositoryDetail;

/**
 * Created by Admin on 22/05/14.
 */
public class VenturaServerConnect {

    public static String password = "";
    private static DataInputStream in_wealth = null;
    private static DataOutputStream out_wealth = null;
    private static Socket socket_wealth = null;
    public static int timeout = 10000; // 1000 = 1s --  60000
    public static eMFClientType mfClientType = eMFClientType.NONE;
    public static String mfClientID = "";
    public static String mfBank = "";

    public static Structsessioncheck sessioncheck = null;
    public static StructHoldingShortLongTermDetail holdingShortLongTermDetail = null;
    public static String rsin = "Rs.";

    public static String longOrDecimal = "long";
    private static StructMyWealth myWealthSummary = null;
    private static StructOpenPositionDetail opEquity = null, opComm = null, opCurr = null;
    private static StructFixedDepositDetail deposit = null;
    private static StructBondEquityDepositoryDetail bondDepository = null;
    private static StructPhysicalDepositoryDetail physicalDepository = null;

    private static StructBondEquityDepositoryDetailNew equityDepository = null;
    private static NumberFormat nfComma2 = null;
    private static NumberFormat nf = null;
    private static ArrayList<String> assetType = null;
    private static ArrayList<JSONObject> familyMembers = null;
    private static ArrayList<JSONObject> fundHouse = null;

    public static void InitialiasedAll(boolean isLogout) {
        try {
            if(isLogout) {
                myWealthSummary = null;
            }
            opEquity = opComm = opCurr = null;
            deposit = null;
            bondDepository = null;
            physicalDepository = null;
            equityDepository = null;
            sessioncheck = null;
            mfClientType = eMFClientType.NONE;
            familyMembers = null;
            MFObjectHolder.initializeall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isHostReachable(String serverAddress, int serverTCPport, int timeoutMS){
        boolean connected = false;
        //RCConnection.showToast(RCConnection.splashContext, "Shifting to " + serverAddress + " for " + timeoutMS/1000 + "seconds");
        try {
            if (Connectivity.IsConnectedtoInternet(GlobalClass.latestContext)) {
                //for testing purpose, for live please comment this 2 lines
                //serverAddress = "114.143.213.76";
                //serverTCPport = 46037;

                //serverAddress = "192.168.0.110";
                //serverTCPport = 46033;

                socket_wealth = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(serverAddress, serverTCPport);
                socket_wealth.connect(socketAddress, timeoutMS);
                GlobalClass.log("Wealth IP isHostReachable2: " + serverAddress + " Port : " + serverTCPport);
                if (socket_wealth.isConnected()) {
                    connected = true;
                    in_wealth = new DataInputStream(socket_wealth.getInputStream());
                    out_wealth = new DataOutputStream(socket_wealth.getOutputStream());
                    //VenturaException.PrintLog("Wealth Connection","Wealth IP connected: " + serverAddress + " Port : " + serverTCPport);
                }
            }
        } catch (IOException e) {
            VenturaException.Print(e);
        }
        return connected;
    }

    public static void closeSocket(){
        isWealthConnected = false;
        equityDepository = null;
        if (socket_wealth != null) {
            try {
                in_wealth.close();
                out_wealth.close();
                socket_wealth.close();
                in_wealth = null;
                out_wealth = null;
                socket_wealth = null;
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }

    //NewMF
    public static ArrayList<JSONObject> getFundHouse(){
        return fundHouse;
    }

    public static String getAllFundHouseCodes(){
        StringBuilder listString = new StringBuilder();
        listString.append("");
        try {
            for (int i = 0; i <getFundHouse().size() ; i++) {
                JSONObject job = getFundHouse().get(i);
                if (i==getFundHouse().size()-1){
                    listString.append(job.getString("Value")+"");
                }else {
                    listString.append(job.getString("Value")+",");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return listString.toString();
    }
    public static void setFundHouse(ArrayList<JSONObject> fh){
        fundHouse = fh;
    }
    public static ArrayList<JSONObject> getFamilyMembers(){
        if(familyMembers != null){
            return familyMembers;
        }
        else{
            try {
                JSONObject jsondata = new JSONObject();
                jsondata.put("clientcode",UserSession.getLoginDetailsModel().getUserID());
                sendReqWithJSON((short) eMessageCodeWealth.FAMILY_DATA.value,jsondata);
                JSONObject jsonData = getDataForMsgCode(eMessageCodeWealth.FAMILY_DATA.value);
                JSONArray jsonArr = jsonData.getJSONArray("data");
                familyMembers = new ArrayList<>();
                for(int i=0;i<jsonArr.length();i++){
                    try {
                        JSONObject jsonD = jsonArr.getJSONObject(i);
                        //String value = jsonD.getString("Value");
                        familyMembers.add(jsonD);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                return familyMembers;
            } catch (Exception ie) {
                ie.printStackTrace();
                return null;
            }
        }
    }
    public static ArrayList<String> getAssetType(){
        if(assetType != null){
            return assetType;
        }
        else{
            try {
                JSONObject jsondata = new JSONObject();
                jsondata.put("clientcode", UserSession.getLoginDetailsModel().getUserID());
                sendReqWithJSON((short) eMessageCodeWealth.ASSETTYPE_DATA.value,jsondata);
                JSONObject jsonData = getDataForMsgCode(eMessageCodeWealth.ASSETTYPE_DATA.value);
                JSONArray jsonArr = jsonData.getJSONArray("data");
                assetType = new ArrayList<>();
                for(int i=0;i<jsonArr.length();i++){
                    try {
                        JSONObject jsonD = jsonArr.getJSONObject(i);
                        String value = jsonD.getString("Value");
                        assetType.add(value);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                return assetType;
            } catch (Exception ie) {
                ie.printStackTrace();
                return null;
            }
        }
    }
    public static JSONObject sendReqDataMFReport(int msgCode){
        try {
            JSONObject jsondata = new JSONObject();
            jsondata.put("clientcode",UserSession.getLoginDetailsModel().getUserID());
            sendReqWithJSON((short) msgCode,jsondata);
            JSONObject jsonData = getDataForMsgCode(msgCode);
            return jsonData;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }
    public static JSONObject sendReqJSONDataMFReport(int msgCode,JSONObject jdata){
        try {
            sendReqWithJSON((short) msgCode,jdata);
            JSONObject jsonData = getDataForMsgCode(msgCode);
            return jsonData;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructFamilyCodesDetail FamilyCodesDetail(int msgCode){
        try {
            GlobalClass.log("Called DP Holding equity FamilyCodesDetail...1");
            boolean issend = sendRequest(msgCode,"");
            if(!issend){
                return null;
            }
            StructFamilyCodesDetail structFamilyCodesDetail = null;
            byte rData[] = new byte[0];

            boolean doBreak = false;

            while (true) {
                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                if (rData.length > 0) {
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);
                    System.arraycopy(data, 0, tempData, rData.length, dataLength);
                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);

                    dataLength = tempData.length;
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));

                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        if(structFamilyCodesDetail == null) {
                            structFamilyCodesDetail = new StructFamilyCodesDetail();
                        }
                        structFamilyCodesDetail.setStructure(cData, intMsgLength);
                        /*if(structFamilyCodesDetail.complete == 1) {
                            doBreak = true;
                        }*/
                        doBreak = true;
                    } else if (intMsgCode == 11) {
                        structFamilyCodesDetail = null;

                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }
            }
            return structFamilyCodesDetail;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }
    public static void sendReqWithJSON(short msgCode,JSONObject json){
        try{
            try {
                boolean isConnect = false;
                if(!isWealthConnected){
                    connectToWealthServer(true);
                }
                if(isWealthConnected) {
                    StructNewMFReq mfReq = new StructNewMFReq(json);
                    byte[] finalByte = mfReq.data.getByteArr(msgCode);
                    //  byte [] encypted = VenturaServerConnect.encrypt(key,data);
                    // out.write(encypted, 0, encypted.length);
                    out_wealth.write(finalByte, 0, finalByte.length);
                    out_wealth.flush();
                }
            } catch (Exception ie) {
                ie.printStackTrace();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static JSONObject getDataForMsgCode(int msgCode){
        try{
            JSONObject jsonObject = new JSONObject();
            byte rData[] = new byte[0];
            boolean doBreak = false;
            String jsonData = "";
            while (true) {

                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                //GlobalClass.log("dataLength : " + dataLength);
                if (rData.length > 0) {
                    //GlobalClass.log("datalength1 : " + dataLength + " rDataLength : " + rData.length);
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);

                    System.arraycopy(data, 0, tempData, rData.length, dataLength);

                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);

                    dataLength = tempData.length;
                    //GlobalClass.log("datalength2 : " + dataLength);
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    //GlobalClass.log("Message UnCompressed Length : " + intMsgLength);
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));
                    //GlobalClass.log("Message Code : " + intMsgCode);

                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < (intMsgLength); j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        StructNewMFRespnse mfResp = new StructNewMFRespnse(cData);
                        String strJson = mfResp.getSubjson();
                        jsonData = jsonData + strJson;
                        if(mfResp.getDc() == 1) {
                            jsonObject = new JSONObject(jsonData);
                            doBreak = true;
                        }
                    } else if (intMsgCode == 11) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);

                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                    //GlobalClass.log("in loopEnd ::" + dataLength +" : " + iterate);
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }
            }
            return jsonObject;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    //Requesting Mutual Fund...
    public static StructBondAvailableBalance getAvailableBalance() {
        StructBondAvailableBalance bondAvl = null;
        try {
            sendRequest(121,"");
            byte[] data = new byte[66];
            int length = in_wealth.read(data, 0, data.length);

            int intMsgLength = (unsignedToBytes(data[1]) * 256) + unsignedToBytes(data[0]);
            int intMsgcode = (unsignedToBytes(data[3]) * 256) + unsignedToBytes(data[2]);

            if (intMsgcode == 121) {
                byte[] cData = new byte[intMsgLength];
                for (int j = 0; j < intMsgLength; j++) {
                    cData[j] = (byte) unsignedToBytes(data[j]);
                    //System.out.print(unsignedToBytes(cData[j]) + "\t");
                }
                bondAvl = new StructBondAvailableBalance(cData, intMsgLength);
            } else if (intMsgcode == 11) {
                bondAvl = null;

                byte[] cData = new byte[intMsgLength];
                for (int j = 0; j < intMsgLength; j++) {
                    cData[j] = (byte) unsignedToBytes(data[j]);
                    //System.out.print(unsignedToBytes(cData[j]) + "\t");
                }
                sessioncheck = new Structsessioncheck(cData, intMsgLength);
            }
            return bondAvl;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructHoldingShortLongTermDetail getHoldingShortLongTermDetail(String isin,byte fromScren,String clientCode) {
        holdingShortLongTermDetail = getHoldingShortLongTermDetail(eMessageCodeWealth.SHORT_LONG_TERM_NEW.value, isin,fromScren,clientCode);
        return holdingShortLongTermDetail;
    }

    private static StructHoldingShortLongTermDetail getHoldingShortLongTermDetail(int msgCode, String isinNo, byte fromScren,String clientCode) {
        try {
            sendRequestWithISIN(msgCode, isinNo,fromScren,clientCode);
            StructHoldingShortLongTermDetail holdingShortLong = null;
            byte rData[] = new byte[0];

            boolean doBreak = false;
            while (true) {
                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                if (rData.length > 0) {
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);

                    System.arraycopy(data, 0, tempData, rData.length, dataLength);
                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);
                    dataLength = tempData.length;
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));
                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        holdingShortLong = new StructHoldingShortLongTermDetail(cData, intMsgLength);
                        doBreak = true;
                    } else if (intMsgCode == 11) {
                        holdingShortLong = null;

                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }
            }
            return holdingShortLong;
        } catch (Exception ex) {
            GlobalClass.log("Error : " + ex.getMessage());
            return null;
        }
    }

    public static StructBondEquityDepositoryDetailNew getEquityDepositoryDetail(String ClientCode,byte fromscreen) {
        try {
            ClientCode = "999S691";
            if (equityDepository != null && ClientCode.equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())) {
                return equityDepository;
            } else{
                StructBondEquityDepositoryDetailNew _equityDepository = getBondEquityDepositoryDetailNew(232, ClientCode, fromscreen);
                if(_equityDepository.clientCode.equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())){
                    equityDepository = _equityDepository;
                }
                return _equityDepository;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructBondEquityDepositoryDetailNew getHoldingData(){
        return equityDepository;
    }

    public static StructBondEquityDepositoryDetail getBondDepositoryDetail() {
        try {
            if (bondDepository != null) {
                return bondDepository;
            } else {
                bondDepository = getBondEquityDepositoryDetail(221);
                return bondDepository;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructPhysicalDepositoryDetail getPhysicalDepositoryDetail() {
        try {
            if (physicalDepository != null) {
                return physicalDepository;
            } else {
                physicalDepository = getPhysicalDepositoryDetail(223);
                return physicalDepository;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructFNODepositoryDetail getFNODepositoryDetail(int msgCode) {
        try {
            GlobalClass.log("Called FNO Holding depositiry...1");
            boolean issend = sendRequest(msgCode,"");
            if(!issend){
                return null;
            }
            StructFNODepositoryDetail bondEquity = null;
            byte rData[] = new byte[0];

            boolean doBreak = false;

            while (true) {
                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                if (rData.length > 0) {
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);
                    System.arraycopy(data, 0, tempData, rData.length, dataLength);
                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);

                    dataLength = tempData.length;
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));

                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    GlobalClass.log("Called FNO Holding depositiry...2.." + intMsgCode);
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        if(bondEquity == null) {
                            bondEquity = new StructFNODepositoryDetail();
                        }
                        bondEquity.setStructure(cData, intMsgLength);
                        if(bondEquity.complete == 1) {
                            doBreak = true;
                        }
                    } else if (intMsgCode == 11) {
                        bondEquity = null;

                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }
            }
            GlobalClass.log("Called FNO Holding depositiry...3");
            return bondEquity;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }
    public static StructBondEquityDepositoryDetailNew getBondEquityDepositoryDetailNew(int msgCode,String ClientCode,byte fromscreen) {

        try {
            GlobalClass.log("Called DP Holding equity depositiry...1");
            sendRequestWithClientcode(msgCode,ClientCode,fromscreen);
            StructBondEquityDepositoryDetailNew bondEquity = null;
            byte rData[] = new byte[0];

            boolean doBreak = false;

            while (true) {
                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                if (rData.length > 0) {
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);
                    System.arraycopy(data, 0, tempData, rData.length, dataLength);
                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);

                    dataLength = tempData.length;
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));

                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    GlobalClass.log("Called DP Holding equity depositiry...2.." + intMsgCode);
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        if(bondEquity == null) {
                            bondEquity = new StructBondEquityDepositoryDetailNew();
                            bondEquity.clientCode = ClientCode;
                        }
                        bondEquity.setStructure(cData, intMsgLength);
                        if(bondEquity.complete == 1) {
                            doBreak = true;
                        }
                    } else if (intMsgCode == 11) {
                        bondEquity = null;

                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }
            }
            GlobalClass.log("Called DP Holding equity depositiry...3");
            return bondEquity;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructBondEquityDepositoryDetail getBondEquityDepositoryDetail(int msgCode) {

        try {
            sendRequest(msgCode,"");
            StructBondEquityDepositoryDetail bondEquity = null;
            byte rData[] = new byte[0];

            boolean doBreak = false;

            while (true) {
                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                if (rData.length > 0) {
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);
                    System.arraycopy(data, 0, tempData, rData.length, dataLength);

                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);

                    dataLength = tempData.length;
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));

                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        bondEquity = new StructBondEquityDepositoryDetail(cData, intMsgLength);
                        doBreak = true;
                    } else if (intMsgCode == 11) {
                        bondEquity = null;

                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }

            }
            return bondEquity;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }

    }

    public static StructPhysicalDepositoryDetail getPhysicalDepositoryDetail(int msgCode) {

        try {
            sendRequest(msgCode,"");
            StructPhysicalDepositoryDetail bondEquity = null;
            byte rData[] = new byte[0];

            boolean doBreak = false;

            while (true) {
                byte[] data = new byte[1024];
                int dataLength = in_wealth.read(data, 0, data.length);
                if (rData.length > 0) {
                    byte tempData[] = new byte[rData.length + dataLength];
                    System.arraycopy(rData, 0, tempData, 0, rData.length);
                    System.arraycopy(data, 0, tempData, rData.length, dataLength);

                    data = new byte[tempData.length];
                    System.arraycopy(tempData, 0, data, 0, tempData.length);

                    dataLength = tempData.length;
                }
                int iterate = 0;
                while (iterate < dataLength) {
                    int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                    int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));

                    if ((dataLength - iterate) < intMsgLength) {
                        break;
                    }
                    if (intMsgCode == msgCode) {
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                        }
                        bondEquity = new StructPhysicalDepositoryDetail(cData, intMsgLength);
                        doBreak = true;
                    } else if (intMsgCode == 11) {
                        bondEquity = null;

                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                        doBreak = true;
                    }
                    iterate += intMsgLength;
                }
                if (dataLength > iterate) {
                    rData = new byte[dataLength - iterate];
                    System.arraycopy(data, iterate, rData, 0, rData.length);
                } else {
                    rData = new byte[0];
                }
                if (doBreak) {
                    doBreak = false;
                    break;
                }

            }
            return bondEquity;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructFixedDepositDetail getdeposit() {
        try {
            if (deposit != null) {
                return deposit;
            } else {
                try {
                    sendRequest(222,"");

                    byte rData[] = new byte[0];

                    boolean doBreak = false;

                    while (true) {
                        byte[] data = new byte[1024];
                        int dataLength = in_wealth.read(data, 0, data.length);
                        if (rData.length > 0) {
                            byte tempData[] = new byte[rData.length + dataLength];
                            System.arraycopy(rData, 0, tempData, 0, rData.length);
                            System.arraycopy(data, 0, tempData, rData.length, dataLength);
                            data = new byte[tempData.length];
                            System.arraycopy(tempData, 0, data, 0, tempData.length);

                            dataLength = tempData.length;
                        }
                        int iterate = 0;
                        while (iterate < dataLength) {
                            int intMsgLength = (unsignedToBytes(data[iterate + 1]) * 256) + (unsignedToBytes(data[iterate + 0]));
                            int intMsgCode = (unsignedToBytes(data[iterate + 3]) * 256) + (unsignedToBytes(data[iterate + 2]));

                            if ((dataLength - iterate) < intMsgLength) {
                                break;
                            }
                            if (intMsgCode == 222) {
                                byte[] cData = new byte[intMsgLength];
                                for (int j = 0; j < intMsgLength; j++) {
                                    cData[j] = (byte) unsignedToBytes(data[iterate + j]);
                                }
                                deposit = new StructFixedDepositDetail(cData, intMsgLength);
                                doBreak = true;
                            } else if (intMsgCode == 11) {
                                deposit = null;

                                byte[] cData = new byte[intMsgLength];
                                for (int j = 0; j < intMsgLength; j++) {
                                    cData[j] = (byte) unsignedToBytes(data[j]);
                                }
                                sessioncheck = new Structsessioncheck(cData, intMsgLength);
                                doBreak = true;
                            }

                            iterate += intMsgLength;
                        }
                        if (dataLength > iterate) {
                            rData = new byte[dataLength - iterate];
                            System.arraycopy(data, iterate, rData, 0, rData.length);
                        } else {
                            rData = new byte[0];
                        }
                        if (doBreak) {
                            doBreak = false;
                            break;
                        }
                    }
                    return deposit;
                } catch (Exception ex) {
                    GlobalClass.log("Error : " + ex.getMessage());
                    return null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }
    public static StructOpenPositionDetail getOpenPositionComm() {
        try {
            if (opComm != null) {
                return opComm;
            } else {
                opComm = getOpenProsition(202);
                return opComm;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructOpenPositionDetail getOpenPositionCurr() {
        try {
            if (opCurr != null) {
                return opCurr;
            } else {
                opCurr = getOpenProsition(203);
                return opCurr;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructOpenPositionDetail getOpenPositionEquity() {
        try {
            if (opEquity != null) {
                return opEquity;
            } else {
                opEquity = getOpenProsition(201);
                return opEquity;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructOpenPositionDetail getOpenProsition(int msgCode) {
        StructOpenPositionDetail op = null;
        try {
            sendRequest(msgCode,"");
            byte[] data = new byte[1024];
            int length = in_wealth.read(data, 0, data.length);
            int intMsgLength = (unsignedToBytes(data[1]) * 256) + unsignedToBytes(data[0]);
            int intMsgcode = (unsignedToBytes(data[3]) * 256) + unsignedToBytes(data[2]);
            if (intMsgcode == msgCode) {

                byte[] cData = new byte[intMsgLength];
                for (int j = 0; j < intMsgLength; j++) {
                    cData[j] = (byte) unsignedToBytes(data[j]);
                    //System.out.print(unsignedToBytes(cData[j]) + "\t");
                }
                op = new StructOpenPositionDetail(cData, intMsgLength);
            } else if (intMsgcode == 11) {
                op = null;

                byte[] cData = new byte[intMsgLength];
                for (int j = 0; j < intMsgLength; j++) {
                    cData[j] = (byte) unsignedToBytes(data[j]);
                    //System.out.print(unsignedToBytes(cData[j]) + "\t");
                }
                sessioncheck = new Structsessioncheck(cData, intMsgLength);

            }
            return op;
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }

    }

    public static Structsessioncheck getSessioncheck(){
        return sessioncheck;
    }

    private static void createDialog(final String msg){
        GlobalClass.showProgressDialog(msg);
    }
    public static StructMyWealth getGrandTotalValue(boolean isRefetch) {
        try {
            if ((myWealthSummary != null) && !isRefetch) {
                return myWealthSummary;
            } else {
                try {
                    GlobalClass.log("Requesting getGrandTotalValue...");
                    sendRequest(eMessageCodeWealth.MYWEALTH_SUMMARY.value,"");
                    byte[] data = new byte[1024];
                    int length = in_wealth.read(data, 0, data.length);
                    int intMsgLength = (unsignedToBytes(data[1]) * 256) + unsignedToBytes(data[0]);
                    int intMsgcode = (unsignedToBytes(data[3]) * 256) + unsignedToBytes(data[2]);

                    if (intMsgcode == eMessageCodeWealth.MYWEALTH_SUMMARY.value) {

                        byte[] cData = new byte[intMsgLength-4]; // ignore first 4 byte
                        //System.arraycopy(tempData, 0, data, 0, tempData.length);

                        for (int j = 0; j < cData.length; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j+4]);
                        }
                        myWealthSummary = new StructMyWealth(cData);
                    } else if (intMsgcode == eMessageCodeWealth.MYWEALTH_SESSIONERROR.value) {
                        myWealthSummary = null;
                        byte[] cData = new byte[intMsgLength];
                        for (int j = 0; j < intMsgLength; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j]);
                        }
                        sessioncheck = new Structsessioncheck(cData, intMsgLength);
                    }
                    if(UserSession.getClientResponse().charAuthId.getValue().equalsIgnoreCase("") && !myWealthSummary.authID.getValue().equalsIgnoreCase("")){
                        UserSession.getClientResponse().charAuthId.setValue(myWealthSummary.authID.getValue());
                        UserSession.getClientResponse().follio.setValue(myWealthSummary.folioNumber.getValue());
                        if(UserSession.getLoginDetailsModel().getFolioNumber().equalsIgnoreCase("")){
                            UserSession.getLoginDetailsModel().setFolioNumber(myWealthSummary.folioNumber.getValue());
                            UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
                        }
                    }
                    return myWealthSummary;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static StructHoldingDateCheckingRes getHoldingDataUpdateCheck(int _dateDP,int _dateFNO) {
        try {
            StructHoldingDateCheckingRes holdingDateCheckingRes = null;
                try {
                    HoldingDateCheckRequest regReq = new HoldingDateCheckRequest();
                    regReq.lastUpdateDateDP.setValue(_dateDP);
                    regReq.lastUpdateDateFNO.setValue(_dateFNO);

                    byte input[] = regReq.data.getByteArr((short) eMessageCodeWealth.CHECK_HOLDING_UPDATE.value); // length of RegistrationRequest...
                    out_wealth.write(input, 0, input.length);
                    out_wealth.flush();
                    GlobalClass.log("Sending Holding Date Check request..."+input.length);

                    byte[] data = new byte[1024];
                    int length = in_wealth.read(data, 0, data.length);
                    int intMsgLength = (unsignedToBytes(data[1]) * 256) + unsignedToBytes(data[0]);
                    int intMsgcode = (unsignedToBytes(data[3]) * 256) + unsignedToBytes(data[2]);

                    if (intMsgcode == eMessageCodeWealth.CHECK_HOLDING_UPDATE.value) {

                        byte[] cData = new byte[intMsgLength-4]; // ignore first 4 byte
                        for (int j = 0; j < cData.length; j++) {
                            cData[j] = (byte) unsignedToBytes(data[j+4]);
                        }
                        holdingDateCheckingRes = new StructHoldingDateCheckingRes(cData);
                    }
                    return holdingDateCheckingRes;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }

        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }
    private static androidx.appcompat.app.AlertDialog alertDialogWealth;
    private static void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        try {
            if (alertDialogWealth != null) {
                alertDialogWealth.dismiss();
                alertDialogWealth = null;
            }
            androidx.appcompat.app.AlertDialog.Builder alertBuilderWealth = new androidx.appcompat.app.AlertDialog.Builder(GlobalClass.latestContext);
            alertBuilderWealth.setMessage(message);
            alertBuilderWealth.setPositiveButton("OK", okListener);
            alertDialogWealth = alertBuilderWealth.create();
            alertDialogWealth.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static boolean isWealthConnected = false;

    private static void showMsgForConnectionIssue(){
        try {
            AppCompatActivity aca = (AppCompatActivity) GlobalClass.latestContext;
            aca.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMessageOKCancel("Oops! Something went wrong. Please try after some time.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GlobalClass.fragmentManager.popBackStackImmediate();
                                }
                            });
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static boolean connectToWealthServer(boolean isCheckBypass) {

        if (UserSession.getClientResponse()!=null && !isWealthConnected){
            try {
                if (isCheckBypass && UserSession.getClientResponse().isBypassedLogin()){
                    showMsgForConnectionIssue();
                    return false;
                }
                InitialiasedAll(false);
                GlobalClass.log("Wealth ip: "+ObjectHolder.connconfig.getWealthServerIP() + " : "+ObjectHolder.connconfig.getWealthServerPort());
                if(isHostReachable(ObjectHolder.connconfig.getWealthServerIP(),ObjectHolder.connconfig.getWealthServerPort(),timeout)) {
                    RegistrationRequest regReq = new RegistrationRequest();

                    regReq.userID.setValue(UserSession.getLoginDetailsModel().getUserID());
                    regReq.userID.setValue("999S691");
                    regReq.imeiNumber.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
                    regReq.panDOB.setValue(UserSession.getLoginDetailsModel().getPan());
                    //regReq.username.setValue(UserSession.getClientResponse().charUserName.getValue());
                    regReq.isPAN.setValue(UserSession.getLoginDetailsModel().getType());
                    regReq.authID.setValue(UserSession.getClientResponse().charAuthId.getValue());
                    regReq.isNewWealthAvl.setValue(UserSession.getClientResponse().isNewWealthAvailable()?'T':'N');
                    if(!isCheckBypass){
                        regReq.isNewWealthAvl.setValue('T');
                    }
                    byte input[] = regReq.data.getByteArr((short) 5); // length of RegistrationRequest...
                    out_wealth.write(input, 0, input.length);
                    out_wealth.flush();
                    GlobalClass.log("Sending  wealth Login request...");
                    isWealthConnected = true;
                }else{
                    if(isCheckBypass) {
                        showMsgForConnectionIssue();
                    }
                }
            } catch (Exception ie) {
                VenturaException.Print(ie);
            }
        }
        return isWealthConnected;
    }

    private  static String getFormatDateforAuthentication(String date){

        SimpleDateFormat INPUT_SDF = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat OUTPUT_SDF = new SimpleDateFormat("dd-MMM-yyyy");
        String output = "";
        try {
            Date inputDate = INPUT_SDF.parse(date);
            output = OUTPUT_SDF.format(inputDate);
        }catch (Exception e){
            GlobalClass.onError("Date Error in Auth : " + date, e);
        }
        return output;
    }
    public static ClientLoginResponse callAccordLogin(){
        String userId = UserSession.getLoginDetailsModel().getUserID();
        String imeiNo = MobileInfo.getDeviceID(GlobalClass.latestContext);
        String password = UserSession.getLoginDetailsModel().getPassword();
        String panOrdob = UserSession.getLoginDetailsModel().getPan();
        short isPin = (short) UserSession.getLoginDetailsModel().getType();
        String pan = "";
        String dob = "";
        if(isPin == 1){
            pan = panOrdob;
        }
        else{
            dob = getFormatDateforAuthentication(panOrdob);
        }
        ClientLoginResponse clLogin = callNewHTTPPOSTLoginIDAuthentication2ndFactor(userId, password,pan,dob,imeiNo);
        if(clLogin != null && clLogin.charSuccess.getValue() == '1'){
            UserSession.getClientResponse().charAuthId.setValue(clLogin.charAuthId.getValue());
            UserSession.getClientResponse().follio.setValue(clLogin.follio.getValue());
            UserSession.getClientResponse().charSuccess.setValue(clLogin.charSuccess.getValue());
            UserSession.getClientResponse().charUserName.setValue(clLogin.charUserName.getValue());
            if(!clLogin.mobileNumber.getValue().equalsIgnoreCase("")){
                UserSession.getClientResponse().mobileNumber.setValue(clLogin.mobileNumber.getValue());
            }
            UserSession.setClientResponse(UserSession.getClientResponse());
            UserSession.getLoginDetailsModel().setFolioNumber(clLogin.follio.getValue());
            UserSession.setLoginDetailsModel(UserSession.getLoginDetailsModel());
        }
        return clLogin;
    }

    public static boolean accordSessionCheck(String scrName) {
        boolean isSessionActive = false;
        try {
            String OPERATION_NAME = "SessionCheck";
            String url = "https://mobileapi.ventura1.com/Service.asmx" + "/" + OPERATION_NAME;
            String param = "authenid=" + UserSession.getClientResponse().getAuthenticationId() + "&Screenname=" + scrName;
            String strresponse = callPOSTHTTPMethod(url, OPERATION_NAME, param);
            Vector session = xmlData(strresponse);
            if (session != null && !session.isEmpty()) {
                if (session.size() > 0) {
                    String[] stringRowData = null;
                    for (int i = 0; i < session.size(); i++) {
                        Vector rowData = (Vector) session.get(i);
                        for (int j = 0; j < rowData.size(); j++) {
                            String colData = rowData.get(j).toString().trim();
                            if (colData!= null && colData.contains("Message")) {
                                stringRowData = new String[1];
                                stringRowData[0] = (rowData.get(j + 1).toString().trim());
                            }
                            j++;
                        }
                    }
                    if (stringRowData[0].toLowerCase().contains("success")) {
                        isSessionActive = true;
                    }
                }
            }
        } catch (Exception exception) {
            VenturaException.Print(exception);
        }
        return isSessionActive;
    }

    private static ClientLoginResponse callNewHTTPPOSTLoginIDAuthentication2ndFactor(String userId, String password, String pan, String dob,
                                                                String clientip) {
        ClientLoginResponse clLogin = new ClientLoginResponse();
        try {
            String strToLog = "";
            /*
            String OPERATION_NAME = "LoginIDAuthenticationApp_Combined";
            if(clientip.equalsIgnoreCase("")){
                clientip = "1";
            }
            String url = "https://mobileapi.ventura1.com/Service.asmx"+"/"+OPERATION_NAME;
            String param = "userid="+userId+"&password="+password+"&dob="+dob+"&pan="+pan+"&clientip="+clientip;
            //strToLog = (url + "   Param :" + param + " \n");*/

            String OPERATION_NAME = "ValidateLoginIDAuthentication";
            if(clientip.equalsIgnoreCase("")){
                clientip = "1";
            }
            String url = "https://mobileapi.ventura1.com/Service.asmx"+"/"+OPERATION_NAME;
            String param = "userid="+userId+"&clientip="+clientip;
            try {
                String strresponse = callPOSTHTTPMethod(url, OPERATION_NAME, param);
                if (!strresponse.equalsIgnoreCase("")) {
                    Vector xmlData = xmlData(strresponse);
                    if (xmlData != null && !xmlData.isEmpty()) {
                        for (Object folioV1 : xmlData) {
                            Vector folioRow = (Vector) folioV1;
                            if (!folioRow.isEmpty()) {
                                for (int j = 0; j < folioRow.size(); j++) {
                                    if (folioRow.get(j).toString().equalsIgnoreCase("Message")) {
                                        strToLog = strToLog + folioRow.get(j + 1).toString();
                                        clLogin = handleLoginResponse(folioRow.get(j + 1).toString(), clLogin);
                                    } else if (folioRow.get(j).toString().equalsIgnoreCase("Access")) {
                                        String userAccess = folioRow.get(j + 1).toString();
                                        clLogin.charUserRight.setValue(userAccess.charAt(0));
                                    } else if (folioRow.get(j).toString().equalsIgnoreCase("FolioNumber")) {
                                        clLogin.follio.setValue(folioRow.get(j + 1).toString());
                                    }
                                    j++;
                                }
                            }
                        }
                    }
                }else{
                    clLogin.charResMsg.setValue("Failed to connect! Please try after some time.");
                }
            }catch (Exception ex){VenturaException.Print(ex);}
            //VenturaException.PrintLog("AccordLoginRes: ",strToLog);
        } catch (Exception exception) {
            exception.printStackTrace();
            clLogin.charSuccess.setValue('0');
            clLogin.charResMsg.setValue("1. There was some issue, please try after some time.");
        }
        return clLogin;
    }
    private static ClientLoginResponse handleLoginResponse(String loginRes,ClientLoginResponse clLogin){
        try{
            if (!loginRes.equalsIgnoreCase("")) {
                //21/04/2020
                //<loginstatus>|<username>|<userpan>|<logid>|<sessionkey>|<usermobile>|<userlastlogin>|<IsEquity>|<IsCommodity>|<MTFlag>|<MFLoginMode>|<MTFlagDeactive>|<NSECASH>|<NSEFnO>|<BSECASH>|<NSESLBM>|<NSECDS>|<POA>
                //Successful|Mr TAPAS NAYAK|ALCPN5199G|180563809|luvKmjNZOiRxkzR7U4KkNXp5SekhsMfg|9775577010|30 Jan 2020 18:10|T|F|T|Equity|F|T|T|T| |T|T

                String[] arrLogin = loginRes.split("\\|");
                if (arrLogin[0].equalsIgnoreCase("Successful")) {
                    clLogin.charSuccess.setValue('1');
                    clLogin.charUserName.setValue(arrLogin[1]);
                    //clLogin.charPAN.setValue(arrLogin[2]);
                    clLogin.charAuthId.setValue(arrLogin[3]);
                    /*
                    clLogin.charSuccess.setValue('1');
                    clLogin.charUserName.setValue(arrLogin[1]);
                    clLogin.charPAN.setValue(arrLogin[2]);
                    clLogin.charLoginId.setValue(arrLogin[3]);
                    clLogin.charAuthId.setValue(arrLogin[4]);
                    clLogin.mobileNumber.setValue(arrLogin[5]);
                    clLogin.lastPwdModify.setValue(arrLogin[6]);
                    String isEquity = arrLogin[7];
                    String isComm = arrLogin[8];
                    if(isEquity.equalsIgnoreCase("T") && isComm.equalsIgnoreCase("T")){
                        //clientAccountType = eClientAccountType.EQUITYCOMM;
                        clLogin.clientAcType.setValue(1);
                    } else if(isComm.equalsIgnoreCase("T")){
                        //clientAccountType = eClientAccountType.COMMODITY;
                        clLogin.clientAcType.setValue(3);
                    } else{
                        //clientAccountType = eClientAccountType.Equity;
                        clLogin.clientAcType.setValue(2);
                    }
                    String isMarginTrade = arrLogin[9];
                    clLogin.activateMargin.setValue(isMarginTrade.equalsIgnoreCase("T")?'T':'F');
                    String isMFD = arrLogin[10];
                    if(!isMFD.equalsIgnoreCase("Equity")){
                        //clientAccountType = eClientAccountType.MFD_ONBOARD;
                        clLogin.clientAcType.setValue(4);
                    }
                    if(arrLogin.length>11){
                        String isDeactMarginTrade = arrLogin[11];
                        clLogin.deActivateMargin.setValue(isDeactMarginTrade.equalsIgnoreCase("T")?'T':'F');
                    }
                    if(arrLogin.length > 12){
                        //<NSECASH>|<NSEFnO>|<BSECASH>|<NSESLBM>|<NSECDS>
                        clLogin.isNSECash.setValue(arrLogin[12].equalsIgnoreCase("T")?'T':'F');
                        clLogin.isNSEFno.setValue(arrLogin[13].equalsIgnoreCase("T")?'T':'F');
                        clLogin.isBSECash.setValue(arrLogin[14].equalsIgnoreCase("T")?'T':'F');
                        clLogin.isNSEslbm.setValue(arrLogin[15].equalsIgnoreCase("T")?'T':'F');
                        clLogin.isNSECds.setValue(arrLogin[16].equalsIgnoreCase("T")?'T':'F');
                    }*/
                }  else { //0|Fail|This service is currently not activated on your login.
                    clLogin.charSuccess.setValue('0');
                    clLogin.charResMsg.setValue(arrLogin[1]);
                }
            }
        }
        catch(Exception ex){
            GlobalClass.onError("handleLoginResponse", ex);
            clLogin.charSuccess.setValue('0');
            clLogin.charResMsg.setValue("2. There was some issue, please try after some time.");
        }
        return clLogin;
    }
    public static String callPOSTHTTPMethod(String url, String operationName,String param){

        String callTime = DateUtil.getcurrentTimeToN()+"";
        int responseCode = 0;
        HttpsURLConnection conn = null;
        try {
            StringBuffer strresponse = new StringBuffer();

            URL obj = new URL(url);
            conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            //60 sec timeout
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);
            //  xModuleClass.httpURLConnection.put(url, conn);
            //}
            param = param + "&cachetime="+callTime;
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(param);
                writer.flush();
                responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        strresponse.append(inputLine);
                    }
                    in.close();
                    inputStream.close();

                } else {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    VenturaException.PrintLog("call Accord login api","callPOSTHTTPMethod: " + errorresponse.toString());
                }
            }
            conn.disconnect();
            return strresponse.toString();
        } catch(Exception e){
            VenturaException.Print(e);
            return "";
        }
        finally{
            if(conn != null){
                conn.disconnect();
            }
        }
    }
    private static Vector xmlData(String str) {

        Vector rowData = null;

        try {
            if (!str.equalsIgnoreCase("")) {
                if(str.contains("&#x0;")){
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();

                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");

                //GlobalClass.log("----------------------------");
                //GlobalClass.log(nList.getLength());
                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {

                        Element eElement = (Element) nNode;
                        //GlobalClass.log(eElement.getFirstChild().getNodeName());
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            if(!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text")){
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if(!nodeName.equalsIgnoreCase("#text")){
                                        GlobalClass.log(nodeName + " ::  " + childNode.item(j).getTextContent());
                                        data.add(nodeName);
                                        data.add(childNode.item(j).getTextContent());
                                    }
                                }
                                rowData.add(data);
                            }
                        }
                    }
                }
                return rowData;
            } else {
                rowData = null;
                return rowData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            rowData = null;
            return rowData;
        }
    }
    public static void sendRequestWithISIN(int msgCode, String isin,byte fromScreen,String _clientCode) {
        try {
            int count = 0;
            byte[] data = new byte[25+10];
            data[count++] = (byte) data.length;
            data[count++] = (byte) 0;
            data[count++] = (byte) msgCode;
            data[count++] = (byte) 0;

            char[] byteISIN = isin.trim().toCharArray();
            for (int i = 0; i < 20; i++) {
                if (i < byteISIN.length) {
                    data[count++] = (byte) byteISIN[i];
                } else {
                    data[count++] = (byte) 32;
                }
            }
            data[count++] = (byte) fromScreen;
            char[] byteClientCode = _clientCode.trim().toCharArray();
            for (int i = 0; i < 10; i++) {
                if (i < byteClientCode.length) {
                    data[count++] = (byte) byteClientCode[i];
                } else {
                    data[count++] = (byte) 32;
                }
            }
            out_wealth.write(data, 0, data.length);
            out_wealth.flush();
        } catch (Exception ex) {
            Logger.getLogger(VenturaServerConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean sendRequest(int msgCode,String msg) {
        try {
            if(!isWealthConnected){
                connectToWealthServer(false);
            }
            if(isWealthConnected) {
                if (out_wealth != null) {
                    GlobalClass.log("Wealth Request : " + msgCode);
                    if (!msg.equals("")) {
                        createDialog(msg);
                    }
                    byte[] data = new byte[4];
                    data[0] = (byte) 4;
                    data[1] = (byte) 0;
                    data[2] = (byte) msgCode;
                    data[3] = (byte) (msgCode / 256);
                    out_wealth.write(data, 0, 4);
                    out_wealth.flush();
                    if (!msg.equalsIgnoreCase("")) {
                        GlobalClass.dismissdialog();
                    }
                    return true;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            closeSocket();
        }
        return false;
    }

    public static void sendRequestWithClientcode(int msgCode,String clientcode,byte fromScreen){
        try {
            int count = 0;
            byte[] data = new byte[25];
            data[count++] = (byte) data.length;
            data[count++] = (byte) 0;
            data[count++] = (byte) msgCode;
            data[count++] = (byte) 0;

            char[] byteClientCode = clientcode.trim().toCharArray();
            for (int i = 0; i < 20; i++) {
                if (i < byteClientCode.length) {
                    data[count++] = (byte) byteClientCode[i];
                } else {
                    data[count++] = (byte) 32;
                }
            }
            data[count++] = (byte) fromScreen;

            out_wealth.write(data, 0, data.length);
            out_wealth.flush();
        } catch (Exception ex) {
            Logger.getLogger(VenturaServerConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static NumberFormat getDecimalFormatFraction2WithComma() {

        try {
            if (nfComma2 != null) {
                return nfComma2;
            } else {

                nfComma2 = new DecimalFormat("##,##,##,##,###0.00");

                if (VenturaServerConnect.longOrDecimal.equalsIgnoreCase("long") && VenturaServerConnect.rsin.equalsIgnoreCase("Rs.")) {
                    nfComma2 = new DecimalFormat("##,##,##,##,###");
                }
                return nfComma2;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static NumberFormat getDecimalFormat() {

        try {
            if (nf != null) {
                return nf;
            } else {
                nf = new DecimalFormat("##,##,##,##,##,###");
                return nf;
            }
        } catch (Exception ie) {
            ie.printStackTrace();
            return null;
        }
    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public static String valueToString(long value) {
        try {
            NumberFormat formatter = null;
            double fValue = 0.00;

            if (rsin.equals("Rs.lacs")) {
                formatter = getDecimalFormatFraction2WithComma();
                fValue = (double) ((double) value / 100000);
            } else if (rsin.equals("Rs.cr")) {
                formatter = getDecimalFormatFraction2WithComma();
                fValue = (double) ((double) value / 10000000);
            } else {
                formatter = getDecimalFormat();
                fValue = (double) value;
            }

            String str = formatter.format(fValue);

            return str;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
