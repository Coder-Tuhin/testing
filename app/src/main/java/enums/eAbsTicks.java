package enums;

public enum eAbsTicks {

    ABS(1,"abs"),TICKS(2,"ticks");

    public int value;
    public String name;

    private eAbsTicks(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
