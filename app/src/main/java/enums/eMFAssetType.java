package enums;

public enum eMFAssetType {
    EQUITY(1,"Equity"), DEBT(2,"Debt"), OTHER(3,"Other");
    public short value;
    public String name;

    private eMFAssetType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
