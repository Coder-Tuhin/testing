package utils;

import Structure.Response.AuthRelated.ClientLoginResponse;
import Structure.Response.AuthRelated.GuestLoginResponse;
import models.LoginDetailsModel;

public class UserSession {
    private static LoginDetailsModel loginDetailsModel;
    private static ClientLoginResponse clr;
    private static GuestLoginResponse glr;

    public static LoginDetailsModel getLoginDetailsModel() {
        if (loginDetailsModel == null){
            loginDetailsModel = PreferenceHandler.getLoginDetails();
            loginDetailsModel.initializeData();
        }
        return loginDetailsModel;
    }

    public static void setLoginDetailsModel(LoginDetailsModel _loginDetailsModel) {
        loginDetailsModel = _loginDetailsModel;
        PreferenceHandler.setLoginDetails(loginDetailsModel);
    }

    public static void InitWithMobileNo(){
        String mobileNo = getLoginDetailsModel().getMobileNo();
        loginDetailsModel = new LoginDetailsModel();
        loginDetailsModel.setMobileNo(mobileNo);
        setLoginDetailsModel(loginDetailsModel);
    }
    public static ClientLoginResponse getClientResponse() {
        if(clr == null){
            clr = PreferenceHandler.getAuthLoginResponse();
        }
        if(clr == null){
            clr = new ClientLoginResponse();
        }
        return clr;
    }

    public static GuestLoginResponse getGuestResponse() {
        return glr;
    }

    public static void setClientResponse(ClientLoginResponse _clr) {
        getClientResponse();
        if(clr == null || _clr == null || (_clr != null && !_clr.isBypassedLogin())) {
            clr = _clr;
        }else {
            clr.charUserName.setValue(_clr.getUserName(clr.charUserName.getValue()));
            clr.charAuthId.setValue(_clr.charAuthId.getValue());
            clr.setBCastServerIP(_clr.getBCastServerIP());
            clr.intPortWealth.setValue(_clr.intPortWealth.getValue());
            clr.intPortBC.setValue(_clr.intPortBC.getValue());
            clr.intPortRC.setValue(_clr.intPortRC.getValue());
            clr.follio.setValue(_clr.getFolioNumber(clr.follio.getValue()));
            clr.setWealthDomainName(_clr.getWealthDomainName());
            clr.mobileNumber.setValue(_clr.getMobileNumber(clr.mobileNumber.getValue()));
            clr.isRiskDisclosureToShow.setValue(_clr.isRiskDisclosureToShow.getValue());
            clr.searchPort.setValue(_clr.searchPort.getValue());
            clr.searchIP.setValue(_clr.searchIP.getValue());
            clr.charSuccess.setValue(_clr.charSuccess.getValue());
            clr.charResMsg.setValue(_clr.charResMsg.getValue());
            //clr.clientAcType.setValue(_clr.clientAcType.getValue());
            clr.deActivateMargin.setValue(_clr.deActivateMargin.getValue());
            clr.activateMargin.setValue(_clr.activateMargin.getValue());
            clr.setServerType(_clr.getServerType().value);
            //clr.isNSEslbm.setValue(_clr.isNSEslbm.getValue());
            //clr.isNSECds.setValue(_clr.isNSECds.getValue());
            clr.isCustomDialogAvl.setValue(_clr.isCustomDialogAvl.getValue());
            clr.isNewWealthAvl.setValue(_clr.isNewWealthAvl.getValue());
            clr.charUserRight.setValue(_clr.charUserRight.getValue());
            clr.charIsUpdateAvailable.setValue(_clr.charIsUpdateAvailable.getValue());
            clr.charMsg.setValue(_clr.charMsg.getValue());
            clr.adharUpdate.setValue(_clr.adharUpdate.getValue());
            clr.adharDate.setValue(_clr.getAdharDate(clr.adharDate.getValue()));
            clr.isEDISAvl.setValue(_clr.isEDISAvl.getValue());
            clr.isPledgeAvl.setValue(_clr.isPledgeAvl.getValue());
            clr.setBCastDomainName(_clr.getBCastDomainName());
            clr.setTradeDomainName(_clr.getTradeDomainName());
            clr.setTradeServerIP(_clr.getTradeServerIP());
        }
        PreferenceHandler.setAuthLoginResponse(clr);
        if(clr != null) {
            getLoginDetailsModel().setActiveUser(clr.getActiveUser());
            PreferenceHandler.setLoginDetails(loginDetailsModel);
        }
    }


    public static void setGuestResponse(GuestLoginResponse _glr) {
        glr = _glr;
        //activeUser = glr.getActiveUser();
        PreferenceHandler.setAuthLoginResponse(null);
        getLoginDetailsModel().setActiveUser(glr.getActiveUser());
        PreferenceHandler.setLoginDetails(loginDetailsModel);
    }

    public static void HandleLogout(){
        InitWithMobileNo();
        glr = null;
        clr = null;
        loginDetailsModel.setTradeLogin(false);
    }

    public static boolean isTradeLogin() {
        return getLoginDetailsModel().isTradeLogin();
    }

    public static void setTradeLogin(boolean _isTradeLogin) {
        getLoginDetailsModel().setTradeLogin(_isTradeLogin);
        PreferenceHandler.setLoginDetails(loginDetailsModel);
    }
}
