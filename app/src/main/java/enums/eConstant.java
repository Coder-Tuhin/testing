package enums;

/**
 * Created by XTREMSOFT on 4/19/2017.
 */
public enum eConstant {
    GUEST("guest"),
    SCRIPCODE("scripcode"),
    SCRIPNAME("scripname"),
    CONDITION("condition"),
    TYPE("type"),
    MSG("msg"),
    TIME("time"),
    ID("id"),
    //TIME_STAMP("timemillies"),
    READER_GRP("readergrp"),
    DISPLAY_HELP("display_help"),
    ORDER_HELP("order_help"),
    //WHATS_NEW("whatsnew"),
    //DDATA_ONTAP("ddataontap"),
    //DATE_FORMSG("dateForMsg"),
    DATE_FORDQ("dateForDq"),
    //FIRSTTIME_LOGIN("firsttimelogin"),

    RS("rs"),
    LACS("lacs"),
    COR("cor"),
    PARK("park"),
    WARNING("warning"),
    PARKREQUESTED("parkrequested"),
    ACCLOCK("acclock")
    ;

    public String name;
    private eConstant(String name) {
        this.name=name;
    }
}
