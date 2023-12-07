package utils.Encriptions.AES_Encryption;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Admin
 */
public class AccordAPIResp {
    public static final int success = 1;
    public static final int failure = 0;

    private int status = failure;
    private String error = "";
    private String data = "";
    private String otherDet = "";

    public AccordAPIResp(int status, String error, String data){
        this.status = status;
        this.error = error;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOtherDet() {
        return otherDet;
    }

    public void setOtherDet(String otherDet) {
        this.otherDet = otherDet;
    }

}