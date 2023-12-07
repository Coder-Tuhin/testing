package models;

/**
 * Created by SUDIP on 06-12-2016.
 */
public class MessageModel {
    private String message = "";
    private String time = "";

    public MessageModel(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }
    public String getTime() {
        return time;
    }
}
