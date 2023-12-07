package enums;

public enum eSIPType {

    E_NACH(0,"E","E-NACH"),
    XSIP(1,"X","XSIP");

    public int value;
    public String tag;
    public String name;
    private eSIPType(int value, String Tag,String name) {
        this.value = value;
        this.tag = Tag;
        this.name=name;
    }
}
