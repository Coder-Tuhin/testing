package models;

/**
 * Created by XTREMSOFT on 3/10/2017.
 */
public class NavModel {
    private String itemName;
    private int itemIcon;

    public NavModel(String itemName, int itemIcon) {
        this.itemName = itemName;
        this.itemIcon = itemIcon;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemIcon() {
        return itemIcon;
    }
}
