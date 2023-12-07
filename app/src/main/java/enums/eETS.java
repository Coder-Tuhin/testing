package enums;

public enum eETS {

    None(0,"None"),Entry(1,"Entry"),
    TP(2,"TP"),SL(3,"SL"),
    Close(4,"Close"),All(10,"All"),;
    public int value;
    public String name;

    private eETS(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
