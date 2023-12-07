package enums;

public enum  eNewsType {

    LIVE_NEWS(1,"LIVE"),
    STORY_NEWS(2,"STORY"),
    QTRLY_ANALISYS(3,"Qtrly Analysis"),
    RESEARCH(4,"Research");
    //RECOMMENDATION(15,"Recommendation");

    public short value;
    public String name;

    private eNewsType(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
