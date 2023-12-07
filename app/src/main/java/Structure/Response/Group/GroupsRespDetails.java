package Structure.Response.Group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import Structure.BaseStructure.StructBase;
import Structure.Response.BC.StructTopGrainerLoserRows;
import Structure.Response.BC.StructTopTradedRows;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructLong;
import structure.StructString;
import structure.StructValueSetter;
import utils.DateUtil;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/8/2016.
 */
public class GroupsRespDetails extends StructBase {

    //region [Variables]
    public StructLong groupCode;
    public StructString groupName;
    private StructByte groupSize;
    private static String TAG = GroupsRespDetails.class.getSimpleName();
    public LinkedHashMap<Integer,GroupsTokenDetails> hm_grpTokenDetails;
    public boolean isGroupDetailRespCame = false;
    public boolean isInitialDataTobeSend = true; // used for initial data req for short mkt watch...
    //endregion

    //region [Constructor]
    public GroupsRespDetails(){
        init();
        data= new StructValueSetter(fields);
    }
    public GroupsRespDetails(byte[] bytes){
        orgData = bytes;
        init();
        data= new StructValueSetter(fields,orgData);
    }
    private void init() {
        groupCode =new StructLong("groupCode",0);
        groupName =new StructString("groupName",20,"");
        groupSize =new StructByte("groupSize",0);
        fields = new BaseStructure[]{
            groupCode, groupName, groupSize
        };
    }
    //endregion

    //region [Methods]
    public boolean isNeedToSendRequest(){
        if(hm_grpTokenDetails == null){
            return true;
        }else if(hm_grpTokenDetails.size() <= 0 && groupCode.getValue()<100 && !isGroupDetailRespCame){
            return true;
        }
        return false;
    }
    public void setGroupSize(int size){
        groupSize.setValue(size);
    }
    public void addGrpTokenDetails(GroupTokensResp grpTokenRes){
        isGroupDetailRespCame = true;
        if(hm_grpTokenDetails == null) {
            hm_grpTokenDetails = new LinkedHashMap<>();
        }else{
            //hm_grpTokenDetails.clear();
            groupSize.setValue(hm_grpTokenDetails.size());
        }
        for (int i=0;i<grpTokenRes.grpTokenDetails.length;i++){
            addSingleTokenDetail(grpTokenRes.grpTokenDetails[i]);
        }
    }
    public void addSingleTokenDetail(GroupsTokenDetails tokenDetails) {
        if(hm_grpTokenDetails == null) {
            hm_grpTokenDetails = new LinkedHashMap<>();
        }
        int scripCode = tokenDetails.scripCode.getValue();
        if(scripCode > 0 && !tokenDetails.scripName.getValue().equalsIgnoreCase("")){
          //  GlobalClass.log(TAG,"addSingleTokenDetail:: ","GrpName : " + groupName.getValue() + " scrCode : " + scripCode);
            hm_grpTokenDetails.put(scripCode,tokenDetails);
            groupSize.setValue(hm_grpTokenDetails.size());
        }
    }

    public void addToRecentView(GroupsTokenDetails tokenDetails) {
        if(hm_grpTokenDetails == null) {
            hm_grpTokenDetails = new LinkedHashMap<>();
        }
        int scripCode = tokenDetails.scripCode.getValue();

        if(scripCode > 0 && !tokenDetails.scripName.getValue().equalsIgnoreCase("")){
            if (groupSize.getValue()>=10 && hm_grpTokenDetails.size()>0){
                List<Integer> keys = new ArrayList<>(hm_grpTokenDetails.keySet());
                hm_grpTokenDetails.remove(keys.get(0));
            }
            hm_grpTokenDetails.put(scripCode,tokenDetails);
            groupSize.setValue(hm_grpTokenDetails.size());
        }
    }

    public boolean isContainScripCode(int scripCode){
        if(hm_grpTokenDetails == null) return false;
        return hm_grpTokenDetails.containsKey(scripCode);
    }

    public GroupsTokenDetails deleteScripFromGroup(int scripCode) {
        if(scripCode > 0){
            return hm_grpTokenDetails.remove(scripCode);
        }
        return null;
    }

    public int getGroupSize(){
        if(hm_grpTokenDetails != null && hm_grpTokenDetails.size()>0){
            return hm_grpTokenDetails.size();
        }
        return groupSize.getValue();
    }

    //used For mktMOvers
    public ArrayList<Integer> updateFromTopTraded(StructTopTradedRows topTradedRows[]){
        ArrayList<Integer> scripCodes = new ArrayList<>();
        if(hm_grpTokenDetails == null) {
            hm_grpTokenDetails = new LinkedHashMap<>();
        }
        for(int i=0;i<topTradedRows.length;i++){
            StructTopTradedRows topTradedR = topTradedRows[i];
            if(topTradedR.token.getValue() > 0) {
                scripCodes.add(topTradedR.token.getValue());
                String[] grpName = topTradedR.scripName.getValue().split("-");
                if(grpName.length>=2 && !grpName[1].equalsIgnoreCase("")) {
                    GroupsTokenDetails tokenDetails = new GroupsTokenDetails();
                    tokenDetails.scripCode = topTradedR.token;
                    tokenDetails.scripName = topTradedR.scripName;
                    hm_grpTokenDetails.put(tokenDetails.scripCode.getValue(), tokenDetails);
                }
            }
        }
        return scripCodes;
    }
    public ArrayList<Integer> updateFromTopGainerLooser(StructTopGrainerLoserRows topGainerLooser[]){
        ArrayList<Integer> scripCodes = new ArrayList<>();
        if(hm_grpTokenDetails == null) {
            hm_grpTokenDetails = new LinkedHashMap<>();
        }
        for(int i=0;i<topGainerLooser.length;i++){
            StructTopGrainerLoserRows topTradedR = topGainerLooser[i];
            if(topTradedR.token.getValue() > 0) {
                scripCodes.add(topTradedR.token.getValue());
                String[] grpName = topTradedR.scripName.getValue().split("-");
                if(grpName.length>=2 && !grpName[1].equalsIgnoreCase("")) {
                    GroupsTokenDetails tokenDetails = new GroupsTokenDetails();
                    tokenDetails.scripCode = topTradedR.token;
                    tokenDetails.scripName = topTradedR.scripName;
                    hm_grpTokenDetails.put(tokenDetails.scripCode.getValue(), tokenDetails);
                }
            }
        }
        return scripCodes;
    }

    public void setAllInitialDataforColorChange() {
        if(hm_grpTokenDetails != null) {
            Collection<GroupsTokenDetails> values = hm_grpTokenDetails.values();
            for (GroupsTokenDetails grpTokenDetail : values) {
                if (grpTokenDetail.isNewlyAdded) {
                    grpTokenDetail.isNewlyAdded = false;
                }
            }
        }
    }
    public void setAllInitialDataExpiryCheckAndColor() {
        if(hm_grpTokenDetails != null) {
            Collection<GroupsTokenDetails> values = hm_grpTokenDetails.values();
            ArrayList<GroupsTokenDetails>  arrayList = new ArrayList(values);
            long currDt = DateUtil.DToN(DateUtil.getCurrentDate());
            for (GroupsTokenDetails grpTokenDetail : arrayList) {
                grpTokenDetail.isNewlyAdded = false;
                try {
                    long exp = GlobalClass.getExpiryFromScripName(grpTokenDetail.getScripName());
                    if (exp > 0 && currDt > exp) {
                        hm_grpTokenDetails.remove(grpTokenDetail.scripCode.getValue());
                    }
                }catch (Exception ex){
                    GlobalClass.onError("InitialDataExpiryCheck : ",ex);
                }
            }
        }
    }
    //endregion
}
