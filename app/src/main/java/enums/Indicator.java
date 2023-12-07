package enums;

/**
 * Created by xtremsoft on 2/4/17.
 */

public enum Indicator {
    BB(0,"Bollinger Bands"), EMA(1,"Exponential MA"), RSMA(2,"Simple MA"),
    ATR(3,"Average True Range"),ENVELOPES(4,"Envelopes"),MACD(5,"MACD"),RSI(6,"Relative Strength Index"),
    STOCHASTIC(7,"Stochastic"),ADI(8,"Average Directional Index"),BSMA(9,"BSMA");

    public short value;
    public String name;

    private Indicator(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }

}
