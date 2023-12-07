package models;

/**
 * Created by XTREMSOFT on 1/25/2017.
 */
public class ScripAlertModel {

    private  int scripCode;
    private AlertModel geaterCond;
    private AlertModel lessCond;

    public ScripAlertModel(int scripCode) {
        this.scripCode = scripCode;
        geaterCond = lessCond = null;
    }

    public int getScripCode() {
        return scripCode;
    }

    public AlertModel getGeaterCond() {
        return geaterCond;
    }
    public void setGeaterCond(AlertModel geaterCond) {
        this.geaterCond = geaterCond;
    }

    public AlertModel getLessCond() {
        return lessCond;
    }

    public void setLessCond(AlertModel lessCond) {
        this.lessCond = lessCond;
    }

    public void setValueForCondition(short condition, AlertModel model){
        if(condition == 0){
            geaterCond = model;
        }
        else{
            lessCond = model;
        }
    }
    public AlertModel getValueForCondition(short condition){
        if(condition == 0){
           return geaterCond;
        }
        else{
            return lessCond;
        }
    }
    public  boolean isBothNotAvailable(){
        if(geaterCond == null && lessCond == null){
            return true;
        }
        return false;
    }
}
