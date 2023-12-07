package utils.Encriptions.AES_Encryption;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.content.Context;
import android.util.Log;

import com.ventura.venturawealth.activities.BaseActivity;

import utils.Encriptions.ReusableLogics;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;

public class GetBankAccList {

    public AccordAPIResp sendReqToAccord(String purpose, Context context){
        try{
            String clientCode = UserSession.getLoginDetailsModel().getUserID();
            String sessionKey = UserSession.getClientResponse().charAuthId.getValue();

            byte[] key = AES_Encryption.generateSecureKey();
            String keyStr = AES_Encryption.byteArrToBase64(key);

            ReusableLogics.setKeyStr(context,keyStr);

            String salt = AES_Encryption.getBASE64Salt();
            byte[] saltBytes = salt.getBytes();
            String encryptedSalt = AES_Encryption.EncryptWithKey(saltBytes, key);
            ReusableLogics.setEncryptionKey(context,encryptedSalt);

            byte[] encryptedSaltBytes = AES_Encryption.base64ToByteArr(encryptedSalt);
            byte[] dataBytes = sessionKey.getBytes(AES_Encryption.CHARACTER_ENCODING);
            String encSessionKey = AES_Encryption.EncryptWithKey(dataBytes, encryptedSaltBytes);
            dataBytes = clientCode.getBytes(AES_Encryption.CHARACTER_ENCODING);
            String encClientCode = AES_Encryption.EncryptWithKey(dataBytes, encryptedSaltBytes);
            dataBytes = purpose.getBytes(AES_Encryption.CHARACTER_ENCODING);
            String encPurpose = AES_Encryption.EncryptWithKey(dataBytes, encryptedSaltBytes);

            GlobalClass.log("encSessionKey", "sendReqToAccord: "+encSessionKey);

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

            String finalURL = "https://pg.ventura1.com/HDFCUPI/GetBankAccountList";
            ReqResponse reqResponse = new ReqResponse();
            AccordAPIResp resp = reqResponse.getDataFromRestGET(finalURL, requestHeader);

            GlobalClass.log("Response : " + resp.getData());
            AccordAPIResp finalresp = processResponse(resp, encryptedSaltBytes);

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