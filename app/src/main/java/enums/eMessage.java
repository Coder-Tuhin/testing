package enums;

/**
 * Created by XTREMSOFT on 4/19/2017.
 */
public enum eMessage {
    INVALID_CLIENTID("Client ID is not same as you are Authenticated."),
    BLANK_PASS("Password cannot be blank"),
    BLANK_PIN("PIN cannot be blank"),
    BLANK_PAN("PAN cannot be blank"),
    BLANK_DOB("DOB cannot be blank"),
    VALID_DOB("Please enter DOB in valid format");

    public String name;
    private eMessage(String name) {
        this.name=name;
    }
}
