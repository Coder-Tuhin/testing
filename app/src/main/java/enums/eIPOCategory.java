package enums;

public enum eIPOCategory {

    IND(0,"IND"), SHA(1,"SHA"),POL(2,"POL"),
    RETAIL(100,"Retail"),HNI(101,"HNI")
    ;
    public short value;
    public String name;

    private eIPOCategory(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
