package enums;

public enum eMaster {

    CATEGORY(1,"Category"), USERBANKDETAILS(2,"UserBankDetails"),SUBCATEGORY(3,"SubCategory"),
    FUNDHOUSE(4,"AMCMaster")
    ;
    public short value;
    public String name;

    private eMaster(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}