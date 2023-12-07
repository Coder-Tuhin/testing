package enums;

public enum eMFClientType {

    NONE(0,"none"), MFD(1,"MFD"), MFI(2,"MFI"), MFD_ONBOARD(3,"MFD_ONBOARD");
    public short value;
    public String name;

    private eMFClientType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
