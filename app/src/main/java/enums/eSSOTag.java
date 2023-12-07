package enums;

/**
 * Created by xtremsoft on 8/20/16.
 */
public enum eSSOTag {
    DATE_Patter("date","yyyy-MM-dd hh:mm:ss"),
    ContentType("Content-Type","application/json"),
    session_id("session_id",""),
    Refresh_token("Refresh_token",""),
    //xapikey("x-api-key","SymChcBowZ8zntznALKzn7lYmsO02Axt3jJlhyvx"),

    xapikey("x-api-key","4rvgvuw9gR7HYuu5kSrf59t4AtyjgyiU9t47oaD4"),//Live
    Authorization("Authorization",""),
    uuid("uuid","1513bdb6-7a85-4426-9218-aab482c38001")
    ;
    public String value;
    public String name;

    private eSSOTag(String name, String value) {
        this.value = value;
        this.name=name;
    }
}