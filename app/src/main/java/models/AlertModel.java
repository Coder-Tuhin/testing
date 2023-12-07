package models;

/**
 * Created by XTREMSOFT on 1/24/2017.
 */
public class AlertModel {
    private int token;
    private float tokenRate;
    private short condition;
    private String scriptName;
    private short achive;

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public float getTokenRate() {
        return tokenRate;
    }

    public void setTokenRate(float tokenRate) {
        this.tokenRate = tokenRate;
    }

    public short getCondition() {
        return condition;
    }

    public void setCondition(short condition) {
        this.condition = condition;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public short getAchive() {
        return achive;
    }

    public void setAchive(short achive) {
        this.achive = achive;
    }
}
