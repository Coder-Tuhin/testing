package enums;

/**
 * Created by XTREMSOFT on 8/26/2016.
 */
public enum eSSOApi {
    BASEURL(100,"https://sso.ventura1.com/auth/user/v3/"),
    //BASEURL(100,"https://sso-hf.venturasecurities.com/auth/user/v3/"),
    //BASEURL(100,"https://sso-stage.ventura1.com/auth/user/v3/"),
    validateotp(0,"validateotp"),
    sendotp(1,"sendotp"),
    forgotpassword(2,"forgotpassword"),
    resendotp(3,"resendotp"),
    setpasswd(4,"setpasswd"),
    setpin(5,"setpin"),
    validatepassword(6,"validatepassword"),
    validatepin(7,"validatepin"),
    validateuser(8,"validateuser"),
    validatenewuser_by_pan(9,"validatenewuser_by_pan"),
    validatenewuser_by_dob(10,"validatenewuser_by_dob"),
    //active_sessions(11,"active_sessions"), //not used for now
    change_pin(12,"changepin"),
    forgotpin(13,"forgotpin"),
    logout(14,"logout"),
    changepassword(15,"changepassword"),
    token_validation(16,"valid_token"),
    close_session(17,"close_session"),
    biometric_validated(18,"biometric_validated"),
    updatepin(19,"updatepin"),
    NewUserUrl(20,"https://webkyc.ventura1.com/mobile.aspx?ref=bPO0NyzmSvlK5r62qQRvLQ%3d%3d&utm_source=wealth-mobile-app&utm_medium=banner&utm_campaign=webkyc-mobile-app&utm_content=sign-up"),
    PanValidationURL(21,"https://sso.ventura1.com/auth/user/pan/v3/validateuser"),//LIVE
    //PanValidationURL(21,"https://sso-hf.venturasecurities.com/auth/user/pan/v3/validateuser"),

    GoogleAuth_Code(22,"googleauth/auth_code"),
    registerGoogleAuth_Code(23,"googleauth/registration"),
    Validate_GoogleAuth_Code(24,"googleauth/validate")
    ;
    
    public String value;
    public int id;

    private eSSOApi(int id, String value) {
        this.value = value;
        this.id=id;
    }
}