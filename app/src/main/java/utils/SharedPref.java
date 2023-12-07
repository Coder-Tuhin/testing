package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ventura.venturawealth.VenturaApplication;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import Structure.Request.BC.StructOptionChainReqNew;
import Structure.Response.BC.StructCurrSymbol;
import chart.ChartSettingsModel;
import connection.Config;
import enums.eMsgType;
import handler.GroupDetail;
import models.CheckNotificationSetting;
import models.MPINModel;
import models.ScripAlertModel;
import models.TradeLoginModel;
import wealth.new_mutualfund.Structure.StructDYISelected;
import wealth.wealthStructure.StructDPHoldingRow;
import wealth.wealthStructure.StructFNODepositoryRow;
import wealth.wealthStructure.StructFamilyCodesDetail;

/**
 * Created by XtremsoftTechnologies on 22/02/16.
 */
public class SharedPref {

    public  SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private final String SECRET_KEY = "vXTREMWEL#";
    private final String SALTVALUE = "9754231920";

    private final String className=getClass().getName();

    public SharedPref() {
        sharedPref = VenturaApplication.getContext().getSharedPreferences(VenturaApplication.getPackage(), Context.MODE_PRIVATE);
    }
    private String encrypt(String strToEncrypt)
    {
        try
        {   /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            /* Retruns encrypted value. */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            }else {
                new String(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            }
        } catch (Exception e){
            GlobalClass.onError("Error occured during encryption: ",e);
        }
        return "";
    }

    /* Decryption Method */
    private String decrypt(String strToDecrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            /* Retruns decrypted value. */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }else{
                return new String(cipher.doFinal((strToDecrypt).getBytes(StandardCharsets.UTF_8)));
            }
        }
        catch (Exception e)
        {
            GlobalClass.onError("Error occured during decryption: ", e);
        }
        return "";
    }

    private void storeInSharePrefwithEnc(String tagName, String value){
        try {
            String encValue = encrypt(value);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(tagName, encValue);
            editor.apply();
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private String getSharePrefwithDec(String tagName, String defaultValue){
        try {
            if(sharedPref.contains(tagName)) {
                String prefValue = sharedPref.getString(tagName, defaultValue);
                if(!prefValue.isEmpty()){
                    String decValue = decrypt(prefValue);
                    if(decValue.isEmpty()){
                        return defaultValue;
                    }
                    return decValue;
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
            return defaultValue;
        }
        return defaultValue;
    }

    public  void storeSharedPref(String tagName, String tagValue) {
        try {
            editor = sharedPref.edit();
            editor.putString(tagName, tagValue);
            editor.apply();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    public  String getSharedPrefFromTag(String tagName, String defaultValue) {
        try {
            return sharedPref.getString(tagName, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public  void storeSharedPref(String tagName, int value) {
        try {
            editor = sharedPref.edit();
            editor.putInt(tagName, value);
            editor.apply();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    public  int getSharedPrefFromTag(String tagName, int defaultValue) {
        try {
            return sharedPref.getInt(tagName, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public void storeSharedPref(String tagName, boolean tagValue) {
        try {
            editor = sharedPref.edit();
            editor.putBoolean(tagName, tagValue);
            editor.apply();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    public boolean getSharedPrefFromTag(String tagName, boolean defaultValue) {
        try {
            return sharedPref.getBoolean(tagName, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void storeSharedPref(String tagName, long tagValue) {
        try {
            editor = sharedPref.edit();
            editor.putLong(tagName, tagValue);
            editor.apply();
        } catch (Exception e) {
            GlobalClass.onError("Error in "+className,e);
        }
    }

    public long getSharedPrefFromTag(String tagName, long defaultValue) {
        try {
            return sharedPref.getLong(tagName, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    public void clearPreference(){
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    public String getSelectedGroup(){
        return sharedPref.getString("selectedGroup","");
    }

    public void setSelectedGroup(String groupName){
        editor=sharedPref.edit();
        editor.putString("selectedGroup",groupName);
        editor.apply();
    }

    public TradeLoginModel getTradeDetails() {

        String json = sharedPref.getString("tradeloginDetailsnew", "");
        if(json!= null && !json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<TradeLoginModel>(){}.getType();
            TradeLoginModel loginDetailsModel = gson.fromJson(json, type);
            return loginDetailsModel;
        }
        return null;
    }
    public void setTradeDetails(TradeLoginModel loginDetails){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginDetails);
        editor.putString("tradeloginDetailsnew", json);//tradeDetails
        editor.apply();
    }
    public ChartSettingsModel getChartSettings() {

        String json = sharedPref.getString("chartsettings", "");
        if(json!= null && !json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ChartSettingsModel>(){}.getType();
            ChartSettingsModel chartSettings = gson.fromJson(json, type);
            return chartSettings;
        }
        return new ChartSettingsModel();
    }
    public void setChartSettings(ChartSettingsModel chartSettings){
        try {
            editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(chartSettings);
            editor.putString("chartsettings", json);
            editor.apply();
        }catch (Exception ex){ex.printStackTrace();}
    }
    public MPINModel getMPINDetails() {//used for mutiple Clientcode MPIN

        String json = sharedPref.getString("mpindetails", "");
        if(json!= null && !json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<MPINModel>(){}.getType();
            MPINModel loginDetailsModel = gson.fromJson(json, type);
            return loginDetailsModel;
        }else{
            return new MPINModel();
        }
    }
    public void setMPINDetails(MPINModel loginDetails){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginDetails);
        editor.putString("mpindetails", json);//tradeDetails
        editor.apply();
    }
    public StructOptionChainReqNew getOptionChainSettings() {

        String json = sharedPref.getString("optionchainnew", "");
        if(json!= null && !json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<StructOptionChainReqNew>(){}.getType();
            StructOptionChainReqNew loginDetailsModel = gson.fromJson(json, type);
            return loginDetailsModel;
        }
        return null;
    }
    public void setOptionChainSettings(StructOptionChainReqNew loginDetails){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginDetails);
        editor.putString("optionchainnew", json);//tradeDetails
        editor.apply();
    }
    public GroupDetail getGroupDetail() {

        String json = sharedPref.getString("groupdetail_"+UserSession.getLoginDetailsModel().getUserID(), "");
        if(json!= null && !json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<GroupDetail>(){}.getType();
            GroupDetail loginDetailsModel = gson.fromJson(json, type);
            return loginDetailsModel;
        }
        return null;
    }
    public void setGroupDetail(GroupDetail loginDetails){
        try {
            editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(loginDetails);
            editor.putString("groupdetail_" + UserSession.getLoginDetailsModel().getUserID(), json);//tradeDetails
            editor.apply();
        }catch (Exception ex){
            VenturaException.Print(ex);
        }
    }
    public Config getConnectionConfig() {
        Gson gson = new Gson();
        String json = sharedPref.getString("connectionDetail", null);
        Type type = new TypeToken<Config>() {}.getType();
        Config config = gson.fromJson(json, type);
        return config;
    }
    public void setConnectionConfig(Config config){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(config);
        editor.putString("connectionDetail", json);//Config
        editor.apply();
    }

    public StructDYISelected getDYISettigs() {

        String json = sharedPref.getString("dyisettings", "");
        if(json!= null && !json.equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<StructDYISelected>(){}.getType();
            StructDYISelected loginDetailsModel = gson.fromJson(json, type);
            return loginDetailsModel;
        }
        return new StructDYISelected();
    }
    public void setDYISettings(StructDYISelected dyiSettings){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dyiSettings);
        editor.putString("dyisettings", json);//tradeDetails
        editor.apply();
    }
    public CheckNotificationSetting getNotificationSetting(){
        Gson gson = new Gson();
        String json = sharedPref.getString("setNotificationSetting", null);
        Type type = new TypeToken<CheckNotificationSetting>() {}.getType();
        CheckNotificationSetting columnsettingModel = gson.fromJson(json, type);
        return columnsettingModel;
    }
    public void setNotificationSetting(CheckNotificationSetting columnsettingModel){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(columnsettingModel);
        editor.putString("setNotificationSetting", json);
        editor.apply();
    }

    public LinkedHashMap<Integer, ScripAlertModel> getAlertRate(){
        LinkedHashMap<Integer,ScripAlertModel> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("alert", null);
        if (json == null) return new LinkedHashMap<>();
        Type type = new TypeToken<LinkedHashMap<Integer,ScripAlertModel>>() {}.getType();
        securitylist = gson.fromJson(json, type);
        return securitylist;
    }
    public void setAlertRate(LinkedHashMap<Integer,ScripAlertModel> messagelist){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messagelist);
        editor.putString("alert", json);
        editor.apply();
    }
    public HashMap<String, StructCurrSymbol> getCurrSymbolList(){
        HashMap<String, StructCurrSymbol> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("currsymbol1", null);
        if (json == null) return new HashMap<>();
        Type type = new TypeToken<HashMap<String, StructCurrSymbol>>() {}.getType();
        securitylist = gson.fromJson(json, type);
        return securitylist;
    }
    public void setCurrencySymbol(HashMap<String, StructCurrSymbol> symbollist){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(symbollist);
        editor.putString("currsymbol1", json);
        editor.apply();
    }
    public HashMap<String, StructDPHoldingRow> getDPHoldingList(){
        HashMap<String, StructDPHoldingRow> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("dpholding", null);
        if (json == null) return null;
        Type type = new TypeToken<HashMap<String, StructDPHoldingRow>>() {}.getType();
        securitylist = gson.fromJson(json, type);
        return securitylist;
    }
    public boolean setDPHoldingList(HashMap<String, StructDPHoldingRow> symbollist){
        try {
            editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(symbollist);
            editor.putString("dpholding", json);
            editor.apply();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public StructFamilyCodesDetail getFamilyData(){
        StructFamilyCodesDetail securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("familydata_"+UserSession.getLoginDetailsModel().getUserID(), null);
        if (json == null) return null;
        Type type = new TypeToken<StructFamilyCodesDetail>() {}.getType();
        securitylist = gson.fromJson(json, type);
        return securitylist;
    }
    public boolean setFamilyData(StructFamilyCodesDetail symbollist){
        try {
            editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(symbollist);
            editor.putString("familydata_"+UserSession.getLoginDetailsModel().getUserID(), json);
            editor.apply();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public HashMap<String, StructFNODepositoryRow> getFNOHoldingList(){
        HashMap<String, StructFNODepositoryRow> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("fnoholding", null);
        if (json == null) return null;
        Type type = new TypeToken<HashMap<String, StructFNODepositoryRow>>() {}.getType();
        securitylist = gson.fromJson(json, type);
        return securitylist;
    }
    public boolean setFNOHoldingList(HashMap<String, StructFNODepositoryRow> symbollist){
        try {
            editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(symbollist);
            editor.putString("fnoholding", json);
            editor.apply();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public LinkedHashMap<String, String> getDQ(){
        LinkedHashMap<String,String> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("discloseQty", null);
        Type type = new TypeToken<LinkedHashMap<String,String>>() {}.getType();
        securitylist = gson.fromJson(json, type);
        if (securitylist == null) return new LinkedHashMap<>();
        return securitylist;
    }
    public void setDQ(LinkedHashMap<String,String> messagelist){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messagelist);
        editor.putString("discloseQty", json);
        editor.apply();
    }

    public List<String> getRoundRobin(){
        List<String> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("roundrobinlist", null);
        if (json == null) return eMsgType.getList();
        String[] favoriteItems = gson.fromJson(json, String[].class);
        securitylist = Arrays.asList(favoriteItems);
        return securitylist;
    }
    public void setRoundRobin(List<String> messagelist){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messagelist);
        editor.putString("roundrobinlist", json);
        editor.apply();
    }

    public List<String> getRemoveIds(){
        List<String> securitylist;
        Gson gson = new Gson();
        String json = sharedPref.getString("remove_ids", null);
        if (json == null) return new ArrayList<>();
        String[] favoriteItems = gson.fromJson(json, String[].class);
        securitylist = Arrays.asList(favoriteItems);
        securitylist = new ArrayList<String>(securitylist);
        return securitylist;
    }

    public void setRemoveids(List<String> mlist){
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mlist);
        editor.putString("remove_ids", json);
        editor.apply();
    }

}
