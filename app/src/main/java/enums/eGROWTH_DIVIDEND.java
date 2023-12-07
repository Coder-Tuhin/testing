package enums;

public enum eGROWTH_DIVIDEND {
    GROWTH(1),
    DIVIDEND(2);

    private int value;

    eGROWTH_DIVIDEND(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

/*
Values taken from FactSheet snapshot data
 */
