package enums;

/**
 * Created by XTREMSOFT on 08-Aug-2017.
 */
public enum eSimplysaveURL {
    SUMMARY("https://online.reliancemf.com/rmf/mowblyserver/wsapi/rmf/prod/wsapi/RedemptionSchemeDetails?"),
    WITHDRAWCLICK("https://online.reliancemf.com/rmf/mowblyserver/wsapi/rmf/prod/wsapi/Redemptionsave?"),
    INVESTCLICK("https://online.reliancemf.com/rmf/mowblyserver/wsapi/rmf/prod/wsapi/InitPurchase?");
    public String name;
    private eSimplysaveURL(String name) {
        this.name=name;
    }
}
