package enums;

/**
 * Created by XtremsoftTechnologies on 20/02/16.
 */
public enum eWatchs {
    MKTWATCH(500,"MKTWATCH"),NSE(0,"NSE"), BSE(1,"BSE"),MKTMOVERS(11,"MKTMOVERS"),
    SLBS(19,"SLBS");
    public int value;
    public String name;

    private eWatchs(int value, String name) {
        this.value = value;
        this.name=name;
    }
}

