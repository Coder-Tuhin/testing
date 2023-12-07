package enums;

public enum eOrderbookSpinnerItems {

    ALL("All",0),
    PENDING("Pending",1),
    FULL_EXECUTED("Fully Executed",2),
    CANCELLED("Cancelled",3),
    OTHERS("Others",4);

    public String name;
    public int value;
    eOrderbookSpinnerItems(String name,int value) {
        this.name=name;
        this.value = value;
    }
}
