package handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import Structure.Response.Group.GroupTokensResp;
import Structure.Response.Group.GroupsResp;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import connection.SendDataToBCServer;
import enums.eWatchs;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 8/17/2016.
 */
public class GroupDetail {

    //region [Variables]
    public int m_segment;
    public HashMap<Long,GroupsRespDetails> hm_grpResDetails;
    public LinkedHashMap<String,Long> hm_grpNameCode; //need to fetch groupcode from groupname
    public boolean isReqSend = false;
    //endregion

    //region [Constructor]
    public GroupDetail() {
        hm_grpResDetails = new HashMap<>();
        hm_grpNameCode = new LinkedHashMap<>();
    }
    //endregion

    //region [Methods]
    public void addGroupFromGroupRes(GroupsResp groupsResp){
        m_segment = (int)groupsResp.Seg.getValue();
        for(int i = 0;i<groupsResp.grpDetails.length;i++){
            addSingleGroup(groupsResp.grpDetails[i]);
        }
    }
    public void  addSingleGroup(GroupsRespDetails grpDetail){
        long grpCode = grpDetail.groupCode.getValue();
        String grpName = grpDetail.groupName.getValue();
        if(grpCode > 0 && !(!UserSession.getLoginDetailsModel().isClient()&& grpName.contains("Currency"))) {
            hm_grpResDetails.put(grpCode, grpDetail);
            hm_grpNameCode.put(grpName, grpCode);
            //GlobalClass.log("GroupDetail ::seg : "+m_segment + " Name : " + grpDetail.groupName.getValue() + " Code : " + grpCode);
        }
    }
    public void  deleteGroup(long grpCode){
        if(hm_grpResDetails.keySet().contains(grpCode)){
            GroupsRespDetails removeGrp = hm_grpResDetails.remove(grpCode);
            hm_grpNameCode.remove(removeGrp.groupName.getValue());
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.deleteGroupRequest(removeGrp.groupName.getValue(),grpCode);
        }
    }
    public void  deleteGroup(String grpName){
        if(hm_grpNameCode.keySet().contains(grpName)){
            long grpCode = hm_grpNameCode.get(grpName);
            GroupsRespDetails removeGrp = hm_grpResDetails.remove(grpCode);
            hm_grpNameCode.remove(removeGrp.groupName.getValue());
            SendDataToBCServer sendDataToServer = new SendDataToBCServer();
            sendDataToServer.deleteGroupRequest(removeGrp.groupName.getValue(),grpCode);
        }
    }
    public void addTokensFromGroupTokenRes(GroupTokensResp grpTokensResp) {

        GroupsRespDetails grpResp = hm_grpResDetails.get(grpTokensResp.groupCode.getValue());
        if(grpResp != null){
            grpResp.addGrpTokenDetails(grpTokensResp);
        }
    }

    public void addTokenFromAddScrip(long groupCode,GroupsTokenDetails tokenDetails) {
        GroupsRespDetails grpResp = hm_grpResDetails.get(groupCode);
        if(grpResp != null){
            grpResp.addSingleTokenDetail(tokenDetails);
        }
    }
    public void deleteTokenFromGroup(long groupCode,int scripCode) {
        GroupsRespDetails grpResp = hm_grpResDetails.get(groupCode);
        if(grpResp != null){
            GroupsTokenDetails grpToken = grpResp.deleteScripFromGroup(scripCode);
            SendDataToBCServer req = new SendDataToBCServer();
            req.deleteScripFromGroupRequest(grpResp.groupName.getValue(),scripCode, GlobalClass.getExpiryFromScripName(grpToken.scripName.getValue()));
        }
    }

    public GroupsRespDetails getGrpDetailFromGrpName(String groupName, eWatchs watchs){
        long grpCode = 0;
        if (hm_grpNameCode.get(groupName) != null)
            grpCode = hm_grpNameCode.get(groupName);
        return getGrpDetailFromGrpCode(grpCode,watchs);
    }

    public void sendAllGroupDetailRequest(){
        try {
            for (Long grpCode : hm_grpNameCode.values()) {
                if (grpCode > 100) {
                    getGrpDetailFromGrpCode(grpCode, eWatchs.MKTWATCH);
                }
            }
        }catch (Exception ex){
            GlobalClass.onError("sendAllGroupDetailRequest : ", ex);
        }
    }

    public GroupsRespDetails getGrpDetailFromGrpCode(long grpCode,eWatchs watchs){
        GroupsRespDetails groupsRespDetails = hm_grpResDetails.get(grpCode);
        if(groupsRespDetails != null){
            if(groupsRespDetails.isNeedToSendRequest()){
                groupsRespDetails.hm_grpTokenDetails = new LinkedHashMap<>();
                if(watchs == eWatchs.MKTMOVERS) {
                    String strgrpCode = grpCode+"";
                    String msgCode = strgrpCode.substring(0,4);
                    String segment = strgrpCode.substring(4,5);
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendMoversReq(Integer.parseInt(segment),Integer.parseInt(msgCode));
                } else{
                    //send requst for groupTokenList for the group...
                    GlobalClass.log("BC Group Details request send : "+grpCode);
                    groupsRespDetails.isInitialDataTobeSend = false;
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendGroupDetailRequest(grpCode);
                }
            }
        }
        return groupsRespDetails;
    }

    public ArrayList<String> getGroupNameList() {
        ArrayList<String> groupNameList=new ArrayList<>();
        groupNameList.addAll(hm_grpNameCode.keySet());
        return groupNameList;
    }
    public int getIndexOfGroupName(String grpName) {
        ArrayList<String> groupNameList=new ArrayList<>();
        groupNameList.addAll(hm_grpNameCode.keySet());
        return groupNameList.indexOf(grpName);
    }
    public ArrayList<GroupsRespDetails> getEditableGroupStructureList() {
        ArrayList<GroupsRespDetails> groupNameList=new ArrayList<>();
        Collection<GroupsRespDetails> values = hm_grpResDetails.values();
        for(GroupsRespDetails grpResDetail : values){
            if(grpResDetail.groupCode.getValue() > 100){
                groupNameList.add(grpResDetail);
            }
        }
        return groupNameList;
    }
    public void setAllGroupForInitialData() {
        Collection<GroupsRespDetails> values = hm_grpResDetails.values();
        for(GroupsRespDetails grpResDetail : values){
            grpResDetail.isInitialDataTobeSend = true;
            if(grpResDetail.groupCode.getValue() < 100){
                grpResDetail.isGroupDetailRespCame = false;
            }
            grpResDetail.setAllInitialDataExpiryCheckAndColor();
        }
    }
    public void setAllGroupForInitialDataforColorChange() {
        Collection<GroupsRespDetails> values = hm_grpResDetails.values();
        for(GroupsRespDetails grpResDetail : values){
            if(grpResDetail.groupCode.getValue() > 100){
                grpResDetail.setAllInitialDataforColorChange();
            }
        }
    }
    //endregion
}