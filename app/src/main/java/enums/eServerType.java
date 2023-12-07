package enums;

public enum eServerType {

    ITS(1,"ITS"),
    RC(2,"RC");

    public int value;
    public String name;
    private eServerType(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
