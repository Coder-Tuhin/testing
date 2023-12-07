package handler;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.SLBMHoldingSummary;
import Structure.Response.RC.StructHoldingSummary_Pointer;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import Structure.Response.RC.StructMarginReportReplyRecord_Pointer;
import Structure.Response.RC.StructMarginTradeSummary;
import Structure.Response.RC.StructMgnInfoRes;
import Structure.Response.RC.StructmarginTrade;
import Structure.Response.RC.TSLBMHoldingsReportReplyRecord;
import connection.SendDataToBCServer;
import connection.SendDataToRCServer;
import enums.eExch;
import enums.eExchSegment;
import enums.eMessageCode;
import enums.eServerType;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by xtremsoft on 11/24/16.
 */
public class ClsMarginHolding {
    public boolean isReqAvlHolding = true;
   // public boolean isReqAvlFO = true;
    public boolean isReqAvlMT = true;
    public boolean isReqAvlSLBM = true;
    //public boolean isHoldingRequestSend = false;
    long marginReqTime = 0;
    long holdingReqTime = 0;
    long marginTradeReqTime = 0;
    long slbmholdingReqTime = 0;

    private StructMarginReportReplyRecord_Pointer marginDetail = null;
    private StructMgnInfoRes marginInfoResp = null;

    private ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingEquity;
    private ArrayList<StructHoldingsReportReplyRecord_Pointer> holdingFNO;
    private HashMap<Integer,StructHoldingsReportReplyRecord_Pointer> holdingScripCodeList;
    private ArrayList<StructHoldingSummary_Pointer> mList;

    private List<StructmarginTrade> mMarginTradeList;
    private List<StructMarginTradeSummary> mSummaryList;

    private ArrayList<SLBMHoldingSummary> mslbmList;
    private ArrayList<TSLBMHoldingsReportReplyRecord> slbmHolding;

    private ArrayList<String> holdingScripName; // for lend order checking
    private ArrayList<String> holdingSLBSScripCode; // for Recall order checking

    private boolean isNeedMarginReload = true;

    public ClsMarginHolding() {
        holdingEquity = new ArrayList<>();
        holdingFNO = new ArrayList<>();
        holdingScripCodeList = new HashMap<>();
        mMarginTradeList = new ArrayList<>();
        mList = new ArrayList<>();
        mSummaryList = new ArrayList<>();

        mslbmList = new ArrayList<>();
        slbmHolding = new ArrayList<>();

        holdingScripName = new ArrayList<>();
        holdingSLBSScripCode = new ArrayList<>();
    }

    public void setSLBMHoldingData(SLBMHoldingSummary holding) {
        mslbmList.add(holding);
        if(holding.complete.getValue() == 1){
            slbmHolding.clear();
            holdingSLBSScripCode.clear();
            for(int j=0;j<mslbmList.size();j++){
                SLBMHoldingSummary holdingSummary = mslbmList.get(j);
                for(int i=0;i<holdingSummary.holdingData.length;i++){
                    TSLBMHoldingsReportReplyRecord slbmRow = holdingSummary.holdingData[i];
                    if(slbmRow.nSECode.getValue() > 0){
                        slbmHolding.add(slbmRow);
                        holdingSLBSScripCode.add(slbmRow.nSECode.getValue()+"");

                        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(slbmRow.nSECode.getValue(),true);
                        if (mktWatch != null) {
                            slbmRow.setNseRate(mktWatch.getLastRate());
                        }
                    }
                }
            }
            mslbmList.clear();
        }
    }
    public String isLendOrder(String symbol){
        if(holdingScripName.contains(symbol)){
            return "";
        }else{
            return "LEND order not allowed as shares not available in holding";
        }
    }

    public String isRecallOrder(int scripCode){
        if(!holdingSLBSScripCode.contains(scripCode + "")){
            return "RECALL order not allowed as already Lent position not available";
        }
        return "";
    }
    public void setHoldingData(StructHoldingSummary_Pointer holding) {
        mList.add(holding);
        if (holding.complete.getValue() == 1) {
            ArrayList<String> currSymbolList = GlobalClass.mktDataHandler.getCurrSymbolList();
            holdingFNO.clear();
            holdingEquity.clear();
            for (int j = 0; j < mList.size(); j++) {
                StructHoldingSummary_Pointer structHoldingSummary_Pointer = mList.get(j);
                for (int i = 0; i < structHoldingSummary_Pointer.noOfRecs.getValue(); i++) {
                    StructHoldingsReportReplyRecord_Pointer holdingRow = structHoldingSummary_Pointer.holdingData[i];
                    if (holdingRow.nSECode.getValue() >= 35000) {
                        holdingRow.exchange = eExchSegment.NSEFO.value;
                        holdingFNO.add(holdingRow);
                    } else {
                        String sName = holdingRow.scripName.getValue();
                        if (sName.contains(" ")) {
                            String[] names = sName.split(" ");
                            sName = names[0];
                        }
                        holdingScripName.add(sName);
                        if(holdingRow.nSECode.getValue() >0 ) {

                            if (currSymbolList.contains(sName)) {
                                holdingRow.exchange = eExchSegment.NSECURR.value;
                                holdingRow.nSECode.setValue(holdingRow.nSECode.getValue() + GlobalClass.currScripCodeAddition);
                                holdingRow.mktlot = GlobalClass.mktDataHandler.getCurrencyStructure(sName).mktLot.getValue();
                                holdingFNO.add(holdingRow);
                            }
                            else{
                                holdingRow.exchange = eExchSegment.NSECASH.value;
                                holdingEquity.add(holdingRow);
                            }
                        }
                        else {
                            holdingRow.exchange = eExchSegment.BSECASH.value;
                            holdingEquity.add(holdingRow);
                        }
                    }
                    if (holdingRow.nSECode.getValue() > 0) {
                        holdingScripCodeList.put(holdingRow.nSECode.getValue(),holdingRow);
                        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(holdingRow.nSECode.getValue(),false);
                        if (mktWatch != null) {
                            holdingRow.setNseRate(mktWatch.getLastRate());
                        }
                    }
                    if (holdingRow.bSECode.getValue() > 0) {
                        holdingScripCodeList.put(holdingRow.bSECode.getValue(),holdingRow);
                        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(holdingRow.bSECode.getValue(),false);
                        if (mktWatch != null) {
                            holdingRow.setBseRate(mktWatch.getLastRate());
                        }
                    }
                }
            }
            mList.clear();
            if (holdingScripCodeList.size() > 0) {
                SendDataToBCServer bcadata = new SendDataToBCServer(null, eMessageCode.NEW_MULTIPLE_MARKETWATCH,new ArrayList<Integer>(holdingScripCodeList.keySet()));
                bcadata.execute();
            }
        }
    }
    public void setMaginTradeData(StructMarginTradeSummary smts) {
        mSummaryList.add(smts);
        if (smts.complete.getValue()==1){
            mMarginTradeList.clear();
            for (StructMarginTradeSummary smt: mSummaryList){
                StructmarginTrade[] smtArr = smt.margintradeData;
                mMarginTradeList = Arrays.asList(smtArr);
            }
            mSummaryList.clear();
        }
    }

    public List<StructmarginTrade> getMarginTrade() {
        if (isReqAvlMT){
            sendMarginTradeRequest();
            isReqAvlMT = false;
        }
      /*  if (mMarginTradeList.size() == 0 && !isMarginTrade) {
            isMarginTrade = true;
            sendMarginTradeRequest();
        }*/
        return mMarginTradeList;
    }

    public ArrayList<StructHoldingsReportReplyRecord_Pointer> getHoldingEquity() {
        if (isReqAvlHolding){
            sendHoldingRequest();
            isReqAvlHolding = false;
        }
        return holdingEquity;
    }
    public StructHoldingsReportReplyRecord_Pointer getHoldingEquityForScripCode(Integer scripCode) {
        return holdingScripCodeList.get(scripCode);
    }
    public void setHoldingEquityForScripCodeEDIS(Integer scripCode, int edisQty) {
        try {
            for(int i=0;i<holdingEquity.size();i++){
                StructHoldingsReportReplyRecord_Pointer _holdingReport = holdingEquity.get(i);
                if(_holdingReport.nSECode.getValue() == scripCode || _holdingReport.bSECode.getValue() == scripCode){
                    _holdingReport.pOA.setValue(_holdingReport.pOA.getValue() - edisQty);
                    if(_holdingReport.pOA.getValue() < 0){
                        _holdingReport.pOA.setValue(0);
                    }else{
                        _holdingReport.totalQty.setValue(_holdingReport.totalQty.getValue() + edisQty);
                    }

                    StructHoldingsReportReplyRecord_Pointer holdingReport = holdingScripCodeList.get(_holdingReport.nSECode.getValue());
                    if(holdingReport != null) {
                        holdingReport.pOA.setValue(holdingReport.pOA.getValue() - edisQty);
                        if (holdingReport.pOA.getValue() < 0){
                            holdingReport.pOA.setValue(0);
                        }else{
                            holdingReport.totalQty.setValue(holdingReport.totalQty.getValue() + edisQty);
                        }
                    }
                    holdingReport = holdingScripCodeList.get(_holdingReport.bSECode.getValue());
                    if(holdingReport != null) {
                        holdingReport.pOA.setValue(holdingReport.pOA.getValue() - edisQty);
                        if (holdingReport.pOA.getValue() < 0){
                            holdingReport.pOA.setValue(0);
                        }else{
                            holdingReport.totalQty.setValue(holdingReport.totalQty.getValue() + edisQty);
                        }
                    }
                }
            }
        }catch (Exception ex){
            GlobalClass.onError("setHoldingEquityForScripCodeEDIS",ex);
        }
    }
    public ArrayList<TSLBMHoldingsReportReplyRecord> getSLBMHoldingEquity() {
        if (isReqAvlSLBM){
            sendSLBMHoldingRequest();
            isReqAvlSLBM = false;
        }
        return slbmHolding;
    }
    public ArrayList<StructHoldingsReportReplyRecord_Pointer> getHoldingFNO() {

        if(UserSession.getClientResponse().getServerType() == eServerType.RC){
            return GlobalClass.getClsCFDBook().getHoldingFNO();
        }else {
            if (isReqAvlHolding) {
                sendHoldingRequest();
                isReqAvlHolding = false;
            }
            if (mList.size() > 0) {
                return new ArrayList<>();
            } else {
                return new ArrayList<>(holdingFNO);
            }
        }
    }

    public void clearMargin(){
        //marginDetail = null;
        //marginInfoResp = null;
        isNeedMarginReload = true;
    }
    public StructMgnInfoRes getMarginDetailRC() {
        if (marginInfoResp == null) {
            marginInfoResp = new StructMgnInfoRes();
        }
        if(isNeedMarginReload){
            sendMarginRequest(true,"1");
        }
        return marginInfoResp;
    }
    public StructMarginReportReplyRecord_Pointer getMarginDetail() {
        if (marginDetail == null) {
            sendMarginRequest(true,"1");
            marginDetail = new StructMarginReportReplyRecord_Pointer();
        }
        return marginDetail;
    }

    public void setMarginDetail(StructMgnInfoRes marginDetail) {
        isNeedMarginReload = false;
        this.marginInfoResp = marginDetail;
    }
    public void setMarginDetail(StructMarginReportReplyRecord_Pointer marginDetail) {
        isNeedMarginReload = false;
        this.marginDetail = marginDetail;
    }

    public boolean sendMarginRequest(boolean isForceRefresh,String a) {
        GlobalClass.log("FromRC"," :: "+a);
        Date date = new Date();
        long time = date.getTime();
        if ((time - marginReqTime > 10000 || isForceRefresh )) {
         //   isMarginRequesting  = true;
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendMarginReq();
            marginReqTime = time;
            return true;
        }
        return false;
    }

    public boolean sendSLBMHoldingRequest() {
        Date date = new Date();
        long time = date.getTime();
        if (time - slbmholdingReqTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendSLBMHoldingReq();
            slbmholdingReqTime = time;
            return true;
        }
        return false;
    }

    public boolean sendHoldingRequest() {
        Date date = new Date();
        long time = date.getTime();
        if (time - holdingReqTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendHoldingReq();
            holdingReqTime = time;
            return true;
        }
        return false;
    }

    public boolean sendMarginTradeRequest() {
        Date date = new Date();
        long time = date.getTime();
        if (time - marginTradeReqTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendMarginTradeReq();
            marginTradeReqTime = time;
            return true;
        }
        return false;
    }
    public void updateRate(StaticLiteMktWatch mktWatch) {
        StructHoldingsReportReplyRecord_Pointer holdingRow = holdingScripCodeList.get(mktWatch.getToken());
        if(holdingRow != null){
            if (mktWatch.getSegment() == eExch.BSE.value) {
                holdingRow.setBseRate(mktWatch.getLastRate());
            } else {
                holdingRow.setNseRate(mktWatch.getLastRate());
            }
            return;
        }
        if(UserSession.getClientResponse().getServerType() == eServerType.RC) {
            GlobalClass.getClsCFDBook().updateRate(mktWatch);
        }
    }
    public boolean isConatinScripCode(int value) {
        if((holdingScripCodeList.get(value) != null)){
            return true;
        }
        if(UserSession.getClientResponse() != null && UserSession.getClientResponse().getServerType() == eServerType.RC){
            return GlobalClass.getClsCFDBook().isConatinScripCode(value);
        }
        return false;
    }
}
