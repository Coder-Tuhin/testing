package models;

/**
 * Created by XTREMSOFT on 13-Nov-2017.
 */
public class ShortNotification {
    private String title;
    private String msg;

    public ShortNotification(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }
}
