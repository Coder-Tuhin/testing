package enums;

public enum eAppType {

    EQUITY(1,"EQUITY"),COMMODITY(2,"COMMODITY");

    public short value;
    public String name;

    private eAppType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
