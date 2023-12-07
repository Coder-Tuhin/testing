package enums;

public enum eMFFrequency {


    MONTHLY(1,"Monthly"), QUARTERLY(2,"Quarterly");
    public short value;
    public String name;

    private eMFFrequency(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
