package enums;

/**
 * Created by XTREMSOFT on 11/11/2016.
 */
public enum eNavItems {
    NPS("NPS"),
    PAYMENT("Simply Save"),
    FUNDTRANS("Fund Transfer"),
    DASHBOARD("Reports Dashboard"),
    BACKOFFICE("Back Office"),
    FUND_TRANSFER("Fund Transfer"),
    NOTIFICATION("Notification"),
    EVENT("Event"),
    FORGOT_PASSWORD("Forgot Password"),
    CHANGE_PASSWORD("Change Password"),
    SETTINGS("Settings"),
    RATE_US("Rate Us"),
    SHARE_APP("Share App"),
    HELP("Help"),
    LOGOUT("Log Out"),
    EXIT("Exit");
    public String name;
    private eNavItems(String name) {
        this.name=name;
    }
}
