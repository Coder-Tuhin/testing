package enums;

public enum eSSOJourney {

    firstlogin(0,"firstlogin"),login(1,"login"),
    forgotpin(2,"forgotpin"),unlock(3,"unlock"),
    none(-1,"none")
    ;
    /*
    Following are the journey for pan api:

    unlock --> when 3 incorrect pin attempts are done, account is locked
    forgotpin--> when user is clicking on forgot pin and account is not locked.
    firstlogin -->for firsttime user  , new user for sso.

    Following are the journey for OTP api:

    unlock--> when 3 incorrect pin attempts are done, account is locked
    forgotpin--> when user is clicking on forgot pin and account is not locked.
    firstlogin--> for firsttime user  , new user for sso.
    login --> for regular user , normal login.
    */
    public int value;
    public String name;

    private eSSOJourney(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
