package fragments.edis;

import org.json.JSONObject;

import utils.GlobalClass;

public class edisclientdatamodel {
    String exid;
    String clientid;
    String apiurl;
    String clientname;
    String segmentid;
    String returnurl;

    //{"exid":"01","clientid":"10000342","apiurl":"https:\/\/ekyc.ventura1.com:51528\/eDIS\/webnsdledis","clientname":"TAPAS  NAYAK","segmentid":"00","returnurl":"https:\/\/ekyc.ventura1.com:51528\/eDIS\/webedisres"}
    public edisclientdatamodel(){}
    public edisclientdatamodel(JSONObject jsonObject){
        try{
            exid = (jsonObject.getString("exid").trim());
            clientid = (jsonObject.getString("clientid").trim());
            apiurl = (jsonObject.getString("apiurl").trim());
            clientname = (jsonObject.getString("clientname").trim());
            segmentid = (jsonObject.getString("segmentid").trim());
            returnurl = (jsonObject.getString("returnurl").trim());
        }catch (Exception ex){
            GlobalClass.onError("edisclientdatamodel",ex);
        }
    }
}
