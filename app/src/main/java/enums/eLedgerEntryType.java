package enums;

public enum eLedgerEntryType {

    TEN(10,"ten"),TWENTRY(20,"twentry"),LASTMONTH(0,"lastmonth");

    public int value;
    public String name;

    private eLedgerEntryType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
