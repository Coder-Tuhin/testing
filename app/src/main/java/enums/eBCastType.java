package enums;

public enum eBCastType {
    BCast(1,"BCast"), JBCast(2,"JBCast");
    public int value;
    public String name;

    private eBCastType(int value, String name) {
        this.value = value;
        this.name=name;
    }
}
