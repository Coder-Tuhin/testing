package enums;

public enum eDirection {

    LONG(1,"Long"),
    SHORT(2,"Short")
    ;
    public int value;
    public String name;
    private eDirection(int value, String name) {
        this.value = value;
        this.name=name;
    }
}