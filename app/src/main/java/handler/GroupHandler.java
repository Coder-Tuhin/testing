package handler;

import android.util.Log;

import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Structure.Response.BC.StructTopGainerLoser;
import Structure.Response.BC.StructTopTraded;
import Structure.Response.Group.GroupTokensResp;
import Structure.Response.Group.GroupsResp;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;
import connection.SendDataToBCServer;
import enums.eMessageCode;
import enums.eMktMover;
import enums.eWatchs;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/17/2016.
 */
public class GroupHandler {

    //region [variables]
    private GroupDetail m_nseGroup;
    private GroupDetail m_bseGroup;
    private GroupDetail m_slbsGroup;
    private GroupDetail m_userDefineGroup;
    private GroupDetail m_mktMovers;
    private GroupsRespDetails optionChainResp = null;

    private String selectedGrpName = "";
    private String createdGrpName = "";
    public long prevGrpCode = -1;
    public boolean isDataFetchFromMemory = false;
    //endregion

    //region [constructor]
    public GroupHandler() {
        this.m_nseGroup = new GroupDetail();
        this.m_bseGroup = new GroupDetail();
        this.m_slbsGroup = new GroupDetail();
        this.m_userDefineGroup = new GroupDetail();
        this.m_mktMovers = new GroupDetail();

        for (eMktMover type : eMktMover.values()) {
            GroupsRespDetails groupsRespDetails = new GroupsRespDetails();
            groupsRespDetails.groupCode.setValue(type.value);
            groupsRespDetails.groupName.setValue(type.name);
            m_mktMovers.addSingleGroup(groupsRespDetails);
        }
    }
    //endregion

    // region [getter setter method]
    public GroupDetail getMKtMoversGroup() {
        return m_mktMovers;
    }

    public GroupDetail getNSEGroup() {
        if(m_nseGroup.hm_grpNameCode.size() == 0){
            requestForGroup(eWatchs.NSE.value);
        }
        return m_nseGroup;
    }

    public GroupDetail getBSEGroup() {
        if(m_bseGroup.hm_grpNameCode.size() == 0){
            requestForGroup(eWatchs.BSE.value);
        }
        return m_bseGroup;
    }
    public GroupDetail getSLBSGroup() {
        if(m_slbsGroup.hm_grpNameCode.size() == 0){
            requestForGroup(eWatchs.SLBS.value);
        }
        return m_slbsGroup;
    }

    public GroupDetail getUserDefineGroup() {
        if(!isDataFetchFromMemory){
            isDataFetchFromMemory = true;
            GroupDetail groupDetail = VenturaApplication.getPreference().getGroupDetail();
            if(groupDetail != null){
                m_userDefineGroup = groupDetail;
                m_userDefineGroup.setAllGroupForInitialData();
            }
        }
        m_userDefineGroup.isReqSend = false;
        if(m_userDefineGroup.hm_grpNameCode.size() == 0){
            m_userDefineGroup.isReqSend = true;
            requestForGroup(eWatchs.MKTWATCH.value);
        }
        return m_userDefineGroup;
    }

    private void requestForGroup(int segment){
        SendDataToBCServer sendDataToBCServer = new SendDataToBCServer();
        sendDataToBCServer.sendGroupRequest(segment);
    }

    public String getCreatedGrpName() {
        return createdGrpName;
    }

    public void setCreatedGrpName(String createdGrpName) {
        this.createdGrpName = createdGrpName;
    }

    public String getSelectedGrpName() {
        return selectedGrpName;
    }

    public void setSelectedGrpName(String selectedGrpName) {
        this.selectedGrpName = selectedGrpName;
    }
    public Boolean isCurrGrpEditable(){
        if(m_userDefineGroup.hm_grpNameCode.containsKey(selectedGrpName)){
            long groupCode = m_userDefineGroup.hm_grpNameCode.get(selectedGrpName);
            if(groupCode > 100){
                return true;
            }
        }
        return false;
    }
    //endregion

    //region [Methods]
    public void handleTopTradedResp(StructTopTraded topTraded){

        int grpCode = getGroupCodeForMktMovers(5011,topTraded.segment.getValue());
        GroupsRespDetails groupsRespDetails = m_mktMovers.getGrpDetailFromGrpCode(grpCode,eWatchs.MKTMOVERS);
        boolean isR = false;
        if(groupsRespDetails == null){
            groupsRespDetails = new GroupsRespDetails();
            //groupsRespDetails.hm_grpTokenDetails = new LinkedHashMap<>();
            groupsRespDetails.groupCode.setValue(grpCode);
            groupsRespDetails.groupName.setValue(eMktMover.valueOf(grpCode).name);
            groupsRespDetails.setGroupSize(topTraded.topTradedRow.length);
            m_mktMovers.addSingleGroup(groupsRespDetails);
            isR = true;
        }
        ArrayList<Integer> scripCodes = groupsRespDetails.updateFromTopTraded(topTraded.topTradedRow);
        if(isR  && GlobalClass.broadCastReg.shortMktWatch.getValue() == 0){
            SendDataToBCServer bcreq = new SendDataToBCServer(null, eMessageCode.NEW_MULTIPLE_MARKETWATCH,scripCodes);
            bcreq.execute();
        }
    }

    public void handleGainerResp(StructTopGainerLoser topGainer){

        int grpCode = getGroupCodeForMktMovers(5012,topGainer.segment.getValue());
        GroupsRespDetails groupsRespDetails = m_mktMovers.getGrpDetailFromGrpCode(grpCode,eWatchs.MKTMOVERS);
        boolean isR = false;

        if(groupsRespDetails == null){
            isR = true;
            groupsRespDetails = new GroupsRespDetails();
            //groupsRespDetails.hm_grpTokenDetails = new LinkedHashMap<>();
            groupsRespDetails.groupCode.setValue(grpCode);
            groupsRespDetails.groupName.setValue(eMktMover.valueOf(grpCode).name);
            groupsRespDetails.setGroupSize(topGainer.topGainerLoesr.length);
            m_mktMovers.addSingleGroup(groupsRespDetails);
        }
        ArrayList<Integer> scripCodes = groupsRespDetails.updateFromTopGainerLooser(topGainer.topGainerLoesr);
        if(isR && GlobalClass.broadCastReg.shortMktWatch.getValue() == 0){
            SendDataToBCServer bcreq = new SendDataToBCServer(null,eMessageCode.NEW_MULTIPLE_MARKETWATCH,scripCodes);
            bcreq.execute();
        }
    }

    public void handleLooserResp(StructTopGainerLoser topLooser){

        int grpCode = getGroupCodeForMktMovers(5013,topLooser.segment.getValue());
        GroupsRespDetails groupsRespDetails = m_mktMovers.getGrpDetailFromGrpCode(grpCode,eWatchs.MKTMOVERS);
        boolean isR = false;

        if(groupsRespDetails == null){
            isR = true;
            groupsRespDetails = new GroupsRespDetails();
            groupsRespDetails.groupCode.setValue(grpCode);
            groupsRespDetails.groupName.setValue(eMktMover.valueOf(grpCode).name);
            groupsRespDetails.setGroupSize(topLooser.topGainerLoesr.length);
            m_mktMovers.addSingleGroup(groupsRespDetails);
        }
        ArrayList<Integer> scripCodes = groupsRespDetails.updateFromTopGainerLooser(topLooser.topGainerLoesr);
        if(isR && GlobalClass.broadCastReg.shortMktWatch.getValue() == 0){
            SendDataToBCServer bcreq = new SendDataToBCServer(null,eMessageCode.NEW_MULTIPLE_MARKETWATCH,scripCodes);
            bcreq.execute();
        }
    }
    public void addGroupFromGroupRes(GroupsResp groupsResp){

        if((int)groupsResp.Seg.getValue() == eWatchs.BSE.value){
            m_bseGroup.addGroupFromGroupRes(groupsResp);
        }
        else if((int)groupsResp.Seg.getValue() == eWatchs.NSE.value){
            m_nseGroup.addGroupFromGroupRes(groupsResp);
        }
        else if((int)groupsResp.Seg.getValue() == eWatchs.SLBS.value){
            m_slbsGroup.addGroupFromGroupRes(groupsResp);
        }
        else {
            m_userDefineGroup.addGroupFromGroupRes(groupsResp);
            saveUserDefineData();
        }
    }


    public void addTokensFromGroupTokenRes(GroupTokensResp grpTokensResp) {
        long grpCode = grpTokensResp.groupCode.getValue();
        if(m_bseGroup.hm_grpResDetails.keySet().contains(grpCode)){
            m_bseGroup.addTokensFromGroupTokenRes(grpTokensResp);
        }
        else if(m_nseGroup.hm_grpResDetails.keySet().contains(grpCode)){
            m_nseGroup.addTokensFromGroupTokenRes(grpTokensResp);
        }
        else if(m_slbsGroup.hm_grpResDetails.keySet().contains(grpCode)){
            m_slbsGroup.addTokensFromGroupTokenRes(grpTokensResp);
        }
        if(m_userDefineGroup.hm_grpResDetails.keySet().contains(grpCode)){
            m_userDefineGroup.addTokensFromGroupTokenRes(grpTokensResp);
            saveUserDefineData();
        }
    }

    public void addGroup(GroupsRespDetails grpResDetail) {
        m_userDefineGroup.addSingleGroup(grpResDetail);
        saveUserDefineData();
    }
    public void deleteGroup(long groupCode) {
        m_userDefineGroup.deleteGroup(groupCode);
        saveUserDefineData();
    }
    public void addTokenFromAddScrip(long groupCode,GroupsTokenDetails tokenDetails) {
        m_userDefineGroup.addTokenFromAddScrip(groupCode,tokenDetails);
        saveUserDefineData();
        /*if(tokenDetails.isNewlyAdded){
            startTimer(tokenDetails);
        }else{
            saveUserDefineData();
        }*/
    }

    public void setChangesForScripAddColorChange(){
        m_userDefineGroup.setAllGroupForInitialDataforColorChange();
        saveUserDefineData();
    }
    /*
    Timer t = null;
    private void startTimer(GroupsTokenDetails tokenDetails){
        try {
            if(t != null){
                t.cancel();
                t = null;
            }
            if (t == null) {
                t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        tokenDetails.isNewlyAdded = false;
                        saveUserDefineData();
                    }
                };
                t.schedule(tt, 30000);
            }
        }catch (Exception ex){}
    }*/

    public void deleteTokenFromGroup(long groupCode,int scripCode) {
        GlobalClass.log("DELETE","   ::   "+groupCode);
        if(groupCode <= 0){
            groupCode = m_userDefineGroup.hm_grpNameCode.get(selectedGrpName);
        }
        m_userDefineGroup.deleteTokenFromGroup(groupCode,scripCode);
        saveUserDefineData();
    }
    public void deleteTokenFromGroup(String groupName,int scripCode) {

        long groupCode = m_userDefineGroup.hm_grpNameCode.get(groupName);
        if(groupCode <= 0){
            groupCode = m_userDefineGroup.hm_grpNameCode.get(selectedGrpName);
        }
        m_userDefineGroup.deleteTokenFromGroup(groupCode,scripCode);
        saveUserDefineData();
    }

    public boolean isGroupExists(String grpName){ // used to technicalfragment_screen case sensitive groupname
        boolean isExit = false;
        Object[] scripNameArr = m_userDefineGroup.hm_grpNameCode.keySet().toArray();
        for(int i=0;i<scripNameArr.length;i++){
            if(scripNameArr[i].toString().equalsIgnoreCase(grpName)){
                isExit = true;
            }
        }
        return isExit;
    }
    public int indexOf(ArrayList<GroupsTokenDetails> gtList, int scripCode) {
        for (int i = 0; i < gtList.size(); i++) {
            GroupsTokenDetails gsd = gtList.get(i);
            if (gsd.scripCode.getValue() == scripCode) {
                return i;
            }
        }
        return 0;
    }

    public ArrayList<GroupsRespDetails> getEditableGroupStructureList() {
        return m_userDefineGroup.getEditableGroupStructureList();
    }

    public int getGroupCodeForMktMovers(int msgCode,int segment){
        String code = msgCode +""+segment;
        int groupCode = Integer.parseInt(code);
        return groupCode;
    }
    public void setOptionChainResp(GroupTokensResp grpTokensResp){
        GroupsRespDetails grpResp = new GroupsRespDetails();
        grpResp.groupCode.setValue(grpTokensResp.groupCode.getValue());
        grpResp.groupName.setValue("OptionChain");
        if(grpResp != null){
            grpResp.addGrpTokenDetails(grpTokensResp);
        }
        this.optionChainResp = grpResp;
    }
    public GroupsRespDetails getOptionChainResp(){
        return optionChainResp;
    }

    private void saveUserDefineData(){
        VenturaApplication.getPreference().setGroupDetail(m_userDefineGroup);
    }
    //endregion
}