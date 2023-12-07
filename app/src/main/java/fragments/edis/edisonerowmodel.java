package fragments.edis;

import org.json.JSONObject;

import utils.GlobalClass;

public class edisonerowmodel {
    //{"bod":"84","name":"BHEL","isin":"INE257A01026 "}
    private String bod;
    private String name;
    private String isin;
    private String settlement;


    private boolean isCheckBoxSelected;
    private String enterQty = "";

    public edisonerowmodel(){
        isCheckBoxSelected = true;
    }
    public edisonerowmodel(JSONObject jsonObject){
        try{
            isCheckBoxSelected = true;
            name = (jsonObject.getString("name").trim());
            isin = (jsonObject.getString("isin").trim());
            bod = (jsonObject.getString("bod").trim());
            settlement  = (jsonObject.getString("settlement").trim());
            enterQty = bod;
        }catch (Exception ex){
            GlobalClass.onError("edisonerowmodel",ex);
        }
    }

    public String getBod() {
        return bod;
    }

    public void setBod(String bod) {
        this.bod = bod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public boolean isCheckBoxSelected() {
        return isCheckBoxSelected;
    }

    public void setCheckBoxSelected(boolean checkBoxSelected) {
        isCheckBoxSelected = checkBoxSelected;
    }

    public String getEnterQty() {
        return enterQty;
    }

    public void setEnterQty(String enterQty) {
        this.enterQty = enterQty;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }
}
