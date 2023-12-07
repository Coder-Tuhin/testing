package enums;

/**
 * Created by xtremsoft on 2/4/17.
 */



public enum eDailyChart {

    M1("1M",1),M2("2M",2),M3("3M",3),
    M6("6M",6),Y1("1Y",12),Y2("2Y",24),
    MAX("Max",30);

    public short value;
    public String name;

    private eDailyChart(String name, int value) {
        this.value = (short) value;
        this.name = name;
    }

    public static eDailyChart getMinute(int min){
        eDailyChart minute = eDailyChart.M1;
        for (eDailyChart m: eDailyChart.values()){
            if(min == m.value) {
                minute = m;
                break;
            }
        }
        return minute;
    }

}
