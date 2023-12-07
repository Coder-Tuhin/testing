package enums;

public enum eIndices {

    NIFTY(200000,"NIFTY"),
    SENSEX(200100,"Sensex"),
    USDINR(200500,"USDINR"),
    NIFTYBANK(200008,"BkNfty");

    /*
    NIFTY(1,"NIFTY"),
    SENSEX(101,"Sensex"),
    USDINR(501,"USDINR"),
    NIFTYBANK(9,"BkNfty");
    */
    public int value;
    public String name;
    private eIndices(int value,String name) {
        this.value = value;
        this.name = name;
    }
}