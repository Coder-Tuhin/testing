package enums;

public enum eLoginType {

    REGISTER(1,"REGISTER"), LOGIN(2,"LOGIN"), AUTOLOGIN(3,"AUTO"),
    NONSSO_LOGIN(4,"NONSSO_LOGIN");
    public int value;
    public String name;

    private eLoginType(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
