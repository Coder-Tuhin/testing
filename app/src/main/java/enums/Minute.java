package enums;

/**
 * Created by xtremsoft on 2/4/17.
 */



public enum Minute {

    M1("M1",1),M2("M2",2),M5("M5",5),M10("M10",10),M15("M15",15),M_DEF("M_DEF",-1);

    public short value;
    public String name;

    private Minute(String name,int value) {
        this.value = (short) value;
        this.name = name;
    }

    public static Minute getMinute(int min){
        Minute minute = Minute.M1;
        for (Minute m: Minute.values()){
            if(min == m.value) {
                minute = m;
                break;
            }
        }
        return minute;
    }

}
