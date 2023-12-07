package enums;

public enum eACCType {

    SAVING(1,"Saving"),CURRENT(2,"Current"),NRE(3,"NRE"),NRO(4,"NRO");

    public int value;
    public String name;

    private eACCType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
