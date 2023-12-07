package enums;

public enum eSearchScrip {

    NSE(0, "NSE"), BSE(1, "BSE"), FNO(2, "FNO"),
    NSECUR(3,"NSECUR"),MCX(4,"MCX"),SLBS(19,"SLBS"),
    MCXCURR(5,"MCXCURR"),NCDEX(6,"NCDEX"),ALL(100,"ALL"),
    INDICES(99, "INDICES"),STOCKS(200, "STOCKS"),OPTIONS(201, "OPTIONS"),
    FUTURES(202, "FUTURES");

    public int value;
    public String name;

    private eSearchScrip(int value, String name) {
        this.value =  value;
        this.name = name;
    }

    public static eSearchScrip forValue(int v){
        for(eSearchScrip searchScrip : eSearchScrip.values()){
            if(searchScrip.value == v){
                return searchScrip;
            }
        }
        return eSearchScrip.ALL;
    }
}
