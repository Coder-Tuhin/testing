package enums;

public enum MF_ASSET {
    EQUITY("Equity"),
    DEBT("Debt"),
    HYBRID("Hybrid"),
    LIQUID("Liquid"),
    OTHERS("Others");

    private final String name;

    MF_ASSET(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
