package Structure.Response.AuthRelated;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import enums.eServerType;
import structure.BaseStructure;
import structure.StructBoolean;
import structure.StructByte;
import structure.StructChar;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;
import utils.ObjectHolder;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class ClientLoginResponse extends StructBase {

    public StructChar charSuccess;
    public StructString charUserName; //need to set
    public StructChar charIsUpdateAvailable;
    public StructChar charUserRight; //need to set
    public StructString charAuthId; //need to set
    public StructInt searchPort;
    public StructBoolean isRiskDisclosureToShow;
    public StructString researve;
    public StructChar charMsg;
    private StructString bcastServerIp; //need to set
    public StructInt intPortWealth; // need to set
    public StructInt intPortBC; // need to set
    public StructInt intPortRC;
    public StructString charResMsg;
    public StructString follio; //this required for simply save... it can be handle on simply save server..
    public StructChar adharUpdate;
    public StructString adharDate;
    private StructString charWealthIP;
    public StructString mobileNumber;
    public StructString searchIP;
    private StructChar researve1; // not in used
    public StructByte clientAcType; //Equity/Comm/EquityCOMM used for fundtransfer option
    public StructChar deActivateMargin;
    public StructChar activateMargin;
    private StructByte serverType; //Boom / ITS
    public StructChar isNSECash;
    public StructChar isNSEFno;
    public StructChar isBSECash;
    public StructChar isNSEslbm;
    public StructChar isNSECds;
    public StructBoolean isCustomDialogAvl;
    public StructBoolean isNewWealthAvl;
    public StructBoolean isEDISAvl;
    public StructBoolean isPledgeAvl;
    private StructString bCastDomainName;
    private StructString tradeDomainName;
    private StructString tradeServerIP;
    private StructString wealthDomainName;

    public ClientLoginResponse(){
        try {
            init(500);
            data=new StructValueSetter(fields);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    public ClientLoginResponse(byte[] bytes){
        try {
            init(bytes.length);
            data=new StructValueSetter(fields,bytes);
            if (charWealthIP.getValue().equals("")){
                charWealthIP.setValue(bcastServerIp.getValue());
            }
            ObjectHolder.connconfig.setWealthServerIP(charWealthIP.getValue());
            ObjectHolder.connconfig.setWealthServerPort(intPortWealth.getValue());
            if(tradeServerIP.getValue().equalsIgnoreCase("")){
                tradeServerIP.setValue(bcastServerIp.getValue());
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);
        }
    }
    private void init(int length) {

        charSuccess = new StructChar("charSuccess",'N');
        charUserName = new StructString("charUserName",40,"");
        charIsUpdateAvailable = new StructChar("charIsUpdateAvailable",' ');
        charUserRight = new StructChar("charUserRight",'N');
        charAuthId = new StructString("charAuthId",100,"");
        searchPort = new StructInt("searchPort",0);
        isRiskDisclosureToShow = new StructBoolean("isRiskDisclosureToShow",false);
        researve = new StructString("researve",20,"");
        charMsg = new StructChar("charMsg",'N');
        bcastServerIp = new StructString("charServerIp",15,"");
        intPortWealth = new StructInt("charServerIp",0);
        intPortBC = new StructInt("intPortBC",0);
        intPortRC = new StructInt("intPortRC",0);
        charResMsg = new StructString("charResMsg",100,"");
        follio = new StructString("follio",20,"");
        adharUpdate = new StructChar("adharUpdate",'F');
        adharDate = new StructString("adharDate",15,"31 Dec 2017");
        charWealthIP = new StructString("charWealthIP",15,"");
        mobileNumber = new StructString("mobileNumber",15,"");
        searchIP = new StructString("searchIP", 30, "");
        researve1 = new StructChar("researve1",'T');
        clientAcType = new StructByte("clientAcType",2);
        deActivateMargin = new StructChar("deActivateMargin",'F');
        activateMargin = new StructChar("activateMarginOld",'F');
        serverType = new StructByte("ServerType",1);
        isNSECash = new StructChar("isNSECash",'F');
        isNSEFno = new StructChar("isNSEFno",'F');
        isBSECash = new StructChar("isBSECash",'F');
        isNSEslbm = new StructChar("isNSEslbm",'F');
        isNSECds = new StructChar("isNSECds",'F');
        isCustomDialogAvl = new StructBoolean("isCustomDialog",false);
        isNewWealthAvl = new StructBoolean("isNewWealthAvl",false);
        isEDISAvl = new StructBoolean("isEDISAvl",false);
        isPledgeAvl = new StructBoolean("isPledgeAvl",false);
        bCastDomainName = new StructString("bCastDomainName", 30, "");
        tradeDomainName = new StructString("tradeDomainName", 30, "");
        tradeServerIP = new StructString("TradeServerIP",15,"");
        wealthDomainName = new StructString("wealthDomainName",30,"");
        fields = new BaseStructure[]{
                charSuccess, charUserName, charIsUpdateAvailable, charUserRight, charAuthId, searchPort,
                isRiskDisclosureToShow,researve, charMsg, bcastServerIp, intPortWealth, intPortBC,
                intPortRC, charResMsg, follio,adharUpdate,adharDate,charWealthIP,
                mobileNumber,searchIP, researve1,clientAcType,deActivateMargin,activateMargin,serverType,
                isNSECash,isNSEFno,isBSECash,isNSEslbm,isNSECds,isCustomDialogAvl,isNewWealthAvl,
                isEDISAvl,isPledgeAvl,bCastDomainName,tradeDomainName,tradeServerIP,wealthDomainName
        };
    }
    public String getWealthDomainName(){
        return wealthDomainName.getValue().isEmpty()?charWealthIP.getValue() : wealthDomainName.getValue();
    }
    public boolean isNewWealthAvailable(){
        return isNewWealthAvl.getValue();
    }
    public eServerType getServerType(){
        if(serverType.getValue() == eServerType.RC.value || GlobalClass.isCommodity()){
            return eServerType.RC;
        }
        return eServerType.ITS;
    }
    public void setServerType(int value){
        serverType.setValue(value);
    }
    public boolean isBOOM(){
        return  serverType.getValue() == eServerType.RC.value;
    }
    public String getMobile(){
        return mobileNumber.getValue();
    }

    public boolean getActiveUser(){
        return charUserRight.getValue()!='F';
    }

    private static final String BYPASS = "bypass";

    public boolean isBypassedLogin(){
        return (charAuthId.getValue().equalsIgnoreCase(BYPASS) || charAuthId.getValue().equalsIgnoreCase(""));
    }
    public boolean isBypassedAPILogin(){
        return (charAuthId.getValue().equalsIgnoreCase(BYPASS));
    }

    public eClientLoginSuccess getSuccess(){
        /*if (charAuthId.getValue().equalsIgnoreCase(BYPASS)){
            return eClientLoginSuccess.SUCCESS;
        }*/
        if (charSuccess.getValue()=='1'){
            return eClientLoginSuccess.SUCCESS;
        }
        if (charSuccess.getValue()=='2'){
            return eClientLoginSuccess.SUCCESS;
        }
        if (charSuccess.getValue()=='3'){
           return eClientLoginSuccess.SYSTEM_UNDER_MAINTAINANCE;
        }
        return eClientLoginSuccess.FALSE;
    }

    public eAadharUpdate getAadharUpdate(){
        if (adharUpdate.getValue()=='C'){
            return eAadharUpdate.COMPULSARY;
        }
        if (adharUpdate.getValue()=='F'){
            return eAadharUpdate.TRUE;
        }
        return eAadharUpdate.FALSE;
    }

    public eAppUpdate getAppUpdate(){
        if (charIsUpdateAvailable.getValue()=='N'){
            return eAppUpdate.COMPULSARY;
        }
        else if(charIsUpdateAvailable.getValue() == 'T'){
            return eAppUpdate.TRUE;
        }
        return eAppUpdate.FALSE;
    }

    public boolean isSLBMActivated() {
        return (isNSEslbm.getValue() == 'T') && (serverType.getValue() == eServerType.ITS.value);
    }

    public String getUserName(String value) {
        return charUserName.getValue().equalsIgnoreCase("")?value:charUserName.getValue();
    }

    public String getFolioNumber(String value) {
        return follio.getValue().equalsIgnoreCase("")?value:follio.getValue();
    }

    public String getMobileNumber(String value) {
        return mobileNumber.getValue().equalsIgnoreCase("")?value:mobileNumber.getValue();
    }
    public String getAdharDate(String value) {
        return adharDate.getValue().equalsIgnoreCase("")?value:adharDate.getValue();
    }
    public enum eClientLoginSuccess{
        SUCCESS,
        FALSE,
        SYSTEM_UNDER_MAINTAINANCE
    }

   public enum eAadharUpdate{
        COMPULSARY,
        TRUE,
        FALSE
    }

   public enum eAppUpdate{
       COMPULSARY,
       TRUE,
       FALSE
    }

    public enum ClientAccountType {
        EQUITYCOMM(1,"Equity Commodity"),
        Equity(2,"Equity"),
        COMMODITY(3,"Commodity");

        public int value;
        public String name;

        ClientAccountType(int value, String name) {
            this.value = (short)value;
            this.name=name;
        }
    }

    public ClientAccountType getAccountType(){
        if (clientAcType.getValue() == 1){
            return ClientAccountType.EQUITYCOMM;
        }else if (clientAcType.getValue() == 3){
            return ClientAccountType.COMMODITY;
        }
        return ClientAccountType.Equity;
    }
    public boolean isActivatemargin(){
        return activateMargin.getValue() == 'T';
    }
    public boolean isDeactivatemargin(){
        return deActivateMargin.getValue() == 'T';
    }

    public void setDeactivateMargin(){
        deActivateMargin.setValue('F');
    }

    public void setActivateMargin(){
        activateMargin.setValue('F');
    }

    public String getAuthenticationId(){
        return charAuthId.getValue();
    }

    public boolean isNeedAccordLogin() {
        if(charAuthId.getValue().equalsIgnoreCase("") || charAuthId.getValue().equalsIgnoreCase(BYPASS)){
            return true;
        }
        return false;
    }
    public boolean isEDISActive() {
        return isEDISAvl.getValue();
    }
    public boolean isPledgeActive() {
        return isPledgeAvl.getValue();
    }
    public String getBCastDomainName() {
        return bCastDomainName.getValue().equalsIgnoreCase("")? bcastServerIp.getValue():bCastDomainName.getValue();
    }
    public String getTradeDomainName() {
        return tradeDomainName.getValue().equalsIgnoreCase("")?getBCastDomainName():tradeDomainName.getValue();
    }
    public void setBCastDomainName(String value) {
        bCastDomainName.setValue(value);
    }
    public void setWealthDomainName(String value) {
        wealthDomainName.setValue(value);
    }
    public void setTradeDomainName(String value) {
        tradeDomainName.setValue(value);
    }
    public void setRCPort(int value) {
        intPortRC.setValue(value);
    }
    public void setBCastServerIP(String value) {
        bcastServerIp.setValue(value);
    }
    public String getBCastServerIP() {
       return bcastServerIp.getValue();
    }
    public void setTradeServerIP(String value) {
        tradeServerIP.setValue(value);
    }
    public String getTradeServerIP() {
        return tradeServerIP.getValue();
    }
    public int getRCPort() {
        return intPortRC.getValue();
    }
    public int getBCPort() {
        return intPortBC.getValue();
    }
    public void setBCPort(int value) {
        intPortBC.setValue(value);
    }
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }
}