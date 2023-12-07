package utils.Encriptions.AES_Encryption;

import android.content.Context;
import android.util.Log;

import utils.Encriptions.ReusableLogics;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;

public class GetClientSegmentList {
    public AccordAPIResp sendReqToAccord(String purpose, Context context){
        try{
            String clientCode = UserSession.getLoginDetailsModel().getUserID();
            String sessionKey = UserSession.getClientResponse().charAuthId.getValue();

            byte[] key = AES_Encryption.generateSecureKey();
            //GlobalClass.log("GetClientSegmentList", "generateSecureKey: "+new String(key));
            String keyStr = AES_Encryption.byteArrToBase64(key);
            //GlobalClass.log("GetClientSegmentList", "Base64key: "+keyStr);
            ReusableLogics.setKeyStr(context,keyStr);

            String salt = AES_Encryption.getBASE64Salt();
            byte[] saltBytes = salt.getBytes();
            //GlobalClass.log("GetClientSegmentList", "salt: "+salt);

            String encryptedSalt = AES_Encryption.EncryptWithKey(saltBytes, key);
            ReusableLogics.setEncryptionKey(context,encryptedSalt);
            //GlobalClass.log("GetClientSegmentList", "encryptedSalt: "+encryptedSalt);

            byte[] encryptedSaltBytes = AES_Encryption.base64ToByteArr(encryptedSalt);
            //GlobalClass.log("GetClientSegmentList", "encryptedSaltbyte: "+new String(encryptedSaltBytes));
            //GlobalClass.log("GetClientSegmentList", "authid: "+sessionKey);

            byte[] dataBytes = sessionKey.getBytes(AES_Encryption.CHARACTER_ENCODING);
            String encSessionKey = AES_Encryption.EncryptWithKey(dataBytes, encryptedSaltBytes);
            //GlobalClass.log("GetClientSegmentList", "encauthid: "+encSessionKey);
            //GlobalClass.log("GetClientSegmentList", "clientcode: "+clientCode);

            dataBytes = clientCode.getBytes(AES_Encryption.CHARACTER_ENCODING);
            String encClientCode = AES_Encryption.EncryptWithKey(dataBytes, encryptedSaltBytes);
            dataBytes = purpose.getBytes(AES_Encryption.CHARACTER_ENCODING);
            //GlobalClass.log("GetClientSegmentList", "encClientCode: "+encSessionKey);
            //GlobalClass.log("GetClientSegmentList", "purpose: "+purpose);

            String encPurpose = AES_Encryption.EncryptWithKey(dataBytes, encryptedSaltBytes);
            //GlobalClass.log("GetClientSegmentList", "encpurpose: "+encPurpose);

            RequestHeader[] requestHeader = new RequestHeader[]{
                    new RequestHeader(), new RequestHeader(), new RequestHeader(),
                    new RequestHeader(), new RequestHeader()
            };
            requestHeader[0].setKey("appkey");
            requestHeader[0].setValue(keyStr);
            requestHeader[1].setKey("sessionkey");
            requestHeader[1].setValue(encSessionKey);
            requestHeader[2].setKey("clientcode");
            requestHeader[2].setValue(encClientCode);
            requestHeader[3].setKey("devicecode");
            requestHeader[3].setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            requestHeader[4].setKey("lastvisitedpage");
            requestHeader[4].setValue(encPurpose);

            String finalURL = "https://pg.ventura1.com/HDFCUPI/GetClientSegmentList";
            ReqResponse reqResponse = new ReqResponse();
            AccordAPIResp resp = reqResponse.getDataFromRestGET(finalURL, requestHeader);

            //GlobalClass.log("GetClientSegmentList", "Resp : " + resp.getData());
            AccordAPIResp finalresp = processResponse(resp, encryptedSaltBytes);
            GlobalClass.log("GetClientSegmentList", "decrypt data: " + finalresp.getData());
            return finalresp;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return new AccordAPIResp(AccordAPIResp.failure, "Error please try after sometimes ", "");
    }
    public AccordAPIResp processResponse(AccordAPIResp response, byte[] encryptkey) {
        try {
            int status = response.getStatus();
            String data = "";
            String error = "";
            if (response.getStatus() == AccordAPIResp.success) {
                org.json.JSONObject jSONObject = new org.json.JSONObject(response.getData());
                status = Integer.parseInt(jSONObject.getString("status"));
                if (status == 1) {
                    data = jSONObject.getString("data");
                    byte[] orgdata = AES_Encryption.DecryptWithKey(data, encryptkey);
                    data = AES_Encryption.convertToUTF8String(orgdata);
                } else {
                    error = jSONObject.getString("error");
                }
            } else {
                error = response.getError();
            }
            return new AccordAPIResp(status, error, data);
        } catch (Exception e) {

            return null;
        }
    }
}