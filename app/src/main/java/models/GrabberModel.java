package models;

import enums.WatchColumns;

/**
 * Created by XTREMSOFT on 10/21/2016.
 */
public class GrabberModel {
    private String grabberName="";
    private boolean isChecked = true;
    public GrabberModel(String grabbername,boolean ischecked){
        this.grabberName = grabbername;
        this.isChecked = ischecked;
    }
    public String getGrabbername() {
        return grabberName;
    }
    public boolean ischecked() {
        return isChecked;
    }
    public void setchecked(boolean ischecked) {
        if (!grabberName.equals(WatchColumns.CURRENT.name) ||
                !grabberName.equals(WatchColumns.CHG.name)){
            this.isChecked = ischecked;
        }
    }
}
