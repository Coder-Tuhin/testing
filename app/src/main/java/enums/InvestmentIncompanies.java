package enums;

public enum InvestmentIncompanies {
    LARGECAP(0,"33"),MIDCAP(1,"37"),SMALLCAP(2,"46"),FLEXICAP(2,"53"),
    EQUITY100(3,"33"), EQUITY65(4,"33,32"),EQUITYEXP65(4,"2,14,40"),EQUITYEXP6580(4,"2"),
    EQUITYEXP35(4,"14,40"),EQUITYEXP3565(4,"14"),EQUITYEXPUPTO35(4,"19"),ELSS(4,"52")
    ,OVERNIGHT(1,"44"),LIQUID(1,"34"),ULTRASHORT(1,"49"),FUNDOFFUNDS(1,"24")
    ,PHARMAANDHEALTHCARE(1,"45"),BANKINGANDFINANCIAL(1,"4"),TECHNOLOGY(1,"31"),INFRASTRUCTURE(1,"29"),
    FMCG(1,"22"),CONSERVATIVEHYBRID(1,"6"),EQUITYSAVINGS(1,"19"),CORPORATEBOND(1,"8"),
    GLITFUND(1,"25"),BANKINGANDPSU(1,"5"),LOWANDSHORTDURATION(1,"35"),MONEYMARKETFUND(1,"39");

    public short value;
    public String name;

    private InvestmentIncompanies(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
