package models;

/**
 * Created by XTREMSOFT on 11/8/2016.
 */
public class Testmodel {
    String buttonnumber;
    boolean selected;

    public Testmodel(String buttonnumber,boolean selected){
        this.buttonnumber = buttonnumber;
        this.selected = selected;
    }

    public String getButtonnumber() {
        return buttonnumber;
    }

    public void setButtonnumber(String buttonnumber) {
        this.buttonnumber = buttonnumber;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
