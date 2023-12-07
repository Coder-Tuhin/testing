package enums;

public enum eLogType {

    UPIRequest(1,"UPIRequest"),
    UPIResponse(2,"UPIResponse"),
    AuthLogin(3,"AuthLogin"),
    AuthConnectFailed(4,"AuthConnectFailed"),
    BCConnectFailed(5,"BCConnectFailed"),
    RCConnectFailed(6,"RCConnectFailed"),
    NONETWORK(7,"NoNetwork"),
    NONPING(8,"NoPing"),
    CUSTOMDIALOG(9,"customdialog"),
    SCREENLOG(11,"Screen"),
    SearchConnectFailed(12,"SearchConnectFailed"),
    DEEPLINK(13,"deeplink"),
    ;

    public int value;
    public String name;
    private eLogType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}