package models;

import java.util.Date;
import java.util.HashMap;

import utils.Constants;
import utils.DateUtil;

/**
 * Created by XTREMSOFT on 25-May-2018.
 */

public class MPINModel {

    private HashMap<String,TradeLoginModel> mpinforclient;
    public MPINModel(){
        mpinforclient = new HashMap<>();
    }

    public void addMPIN(String clientcode, TradeLoginModel model){
        mpinforclient.put(clientcode.toUpperCase(),model);
    }
    public TradeLoginModel getMPIN(String clientCode){
        return mpinforclient.get(clientCode.toUpperCase());
    }
    public void clearMPIN(String clientCode){
        mpinforclient.remove(clientCode.toUpperCase());
    }
}
