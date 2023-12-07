package handler;

import android.text.SpannableString;
import android.text.TextUtils;

import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import Structure.Other.StructTrdQtyPriceTime;
import Structure.Request.RC.CancelOrderReq_CASH_Pointer;
import Structure.Request.RC.CancelOrderReq_FNO_Pointer;
import Structure.Request.RC.CancelOrderReq_SLBM_Pointer;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.RC.StructOrderSummary_Pointer;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import connection.SendDataToRCServer;
import enums.eMessageCode;
import enums.eOrderbookSpinnerItems;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;

/**
 * Created by Tapas on 28-04-2016.
 */
public class ClsOrderBook {

    private ConcurrentHashMap<Long,StructOrderReportReplyRecord_Pointer> m_hmOrderBook;
    private HashMap<Long,Long> m_hmBrokerIDExchID;
    long requestTime = 0;
    public CharSequence lastPendingOrderScrip = "";
    public String lastPendingOrderTime = "";
    private ArrayList<StructOrderSummary_Pointer> mList;

    public ClsOrderBook(){
        m_hmOrderBook = new ConcurrentHashMap<>();
        mList = new ArrayList<>();
        m_hmBrokerIDExchID = new HashMap<>();
    }

    public void AddOrder(StructOrderSummary_Pointer myOrderBook){
        mList.add(myOrderBook);
        if(myOrderBook.complete.getValue() == 1) {
            m_hmBrokerIDExchID.clear();
            m_hmOrderBook.clear();
            for (int j = 0;j<mList.size();j++){
                StructOrderSummary_Pointer myOrderBooktemp = mList.get(j);
                StructOrderReportReplyRecord_Pointer[] singleOrderResp = myOrderBooktemp.orderRequest;
                for (int i = 0; i < myOrderBooktemp.shortcNoOfRecs.getValue(); i++) {
                    addSingleOrder(singleOrderResp[i]);
                }
            }
            mList.clear();
        }
    }
    public void addSingleOrder(StructOrderReportReplyRecord_Pointer orderRow){

        if(orderRow.exchOrderID.getValue()>0) {
            StructTrdQtyPriceTime trade = GlobalClass.getClsTradeBook().getTradeQtyPriceTimeForExchOrdId(orderRow.exchOrderID.getValue());
            if (trade.exchTime > orderRow.finalexchtime) {
                orderRow.finalexchtime = trade.exchTime;
            }
            orderRow.finaTradePrice = trade.tradePrice;
            //if(UserSession.getClientResponse().getServerType() == eServerType.ITS) {
                orderRow.setFinalPendingQtyWithTrdQty(trade.tradeQty);
            //}
        }
        addOrderBook(orderRow);
    }

    public void setFinalTdQtyfromTradeResponse(StructTrdQtyPriceTime trade){
        //if(UserSession.getClientResponse().getServerType() == eServerType.ITS) {
            long key = trade.exchOrderID;
            StructOrderReportReplyRecord_Pointer orderRow = m_hmOrderBook.get(key);
            if (orderRow != null) {
                if (trade.exchTime > orderRow.finalexchtime) {
                    orderRow.finalexchtime = trade.exchTime;
                }
                orderRow.finaTradePrice = trade.tradePrice;
                orderRow.setFinalPendingQtyWithTrdQty(trade.tradeQty);
            }
        //}
    }

    public void upDateOrderBook(StructOrderReportReplyRecord_Pointer myorderBook){
        addOrderBook(myorderBook);
    }

    public void upDateSLTriggeredOrderBook(StructOrderReportReplyRecord_Pointer myorderBook){
        long key = myorderBook.exchOrderID.getValue();
        StructOrderReportReplyRecord_Pointer ordBk = m_hmOrderBook.get(key);
        if(ordBk != null) {
            ordBk.sLTriggered.setValue(myorderBook.sLTriggered.getValue());
        }
    }

    public void upDateIOCOrderBook(StructOrderReportReplyRecord_Pointer myorderBook){
        upDateOrderBook(myorderBook);
    }

    public void upDateBSEKillOrderBook(StructOrderReportReplyRecord_Pointer myorderBook){
        long key = myorderBook.exchOrderID.getValue();
        StructOrderReportReplyRecord_Pointer ordBk = m_hmOrderBook.get(key);
        ordBk.status.setValue("Cancelled");
        ordBk.finaPendingQty = 0;
    }

    public void upDateNSEnDerivKillOrderBook(StructOrderReportReplyRecord_Pointer myorderBook){
        long key = myorderBook.exchOrderID.getValue();
        StructOrderReportReplyRecord_Pointer ordBk = m_hmOrderBook.get(key);
        if(ordBk != null) {
            ordBk.finaPendingQty = 0;
            ordBk.status.setValue("Cancelled");
        }
    }

    public void addOrderBook(StructOrderReportReplyRecord_Pointer myorderBook){
        long exchOrderId = myorderBook.exchOrderID.getValue();
        long brkrOrderId = (long) myorderBook.brokerOrderID.getValue();
        if(exchOrderId == 0){
            exchOrderId = brkrOrderId;
        }
        if((m_hmBrokerIDExchID.get(exchOrderId) == null) || (m_hmBrokerIDExchID.get(exchOrderId) == 0) || (m_hmBrokerIDExchID.get(exchOrderId) == brkrOrderId)) {

            StructOrderReportReplyRecord_Pointer localOrdBk = m_hmOrderBook.get(exchOrderId);
            if (localOrdBk != null) {
                String st1 = myorderBook.status.getValue().toLowerCase();
                if (st1.contains("rejected") || st1.contains("cancelled") || st1.contains("frozen")) {
                    if (localOrdBk.qty.getValue() != myorderBook.qty.getValue()) {
                        myorderBook.qty.setValue(localOrdBk.qty.getValue());
                    }
                }
                String status = localOrdBk.status.getValue();
                String st = status.toLowerCase();
                if (st.contains("rejected") || st.contains("cancelled") || st.contains("frozen")) {
                    myorderBook.status.setValue(status);
                }
            }
            m_hmOrderBook.remove(brkrOrderId);
            m_hmOrderBook.put(exchOrderId, myorderBook);
            m_hmBrokerIDExchID.put(brkrOrderId, myorderBook.exchOrderID.getValue());
        }
    }

    public StructOrderReportReplyRecord_Pointer getOrderBookForExchOrdID(long exchOrdId){
        try {
            StructOrderReportReplyRecord_Pointer ordRow =  m_hmOrderBook.get(exchOrdId);
            return ordRow;
        }
        catch (Exception exception) {
            GlobalClass.onError("Exception ... %@",exception);
        }
        return null;
    }

    public void refreshOrderBookForTrade(StructTradeReportReplyRecord_Pointer tradeRow){
        try {
            StructOrderReportReplyRecord_Pointer ordRow =  m_hmOrderBook.get(tradeRow.exchOrderID.getValue());
            addSingleOrder(ordRow);
        }
        catch (Exception exception) {
            GlobalClass.onError("Exception ... %@",exception);
        }
    }

    public ArrayList<Long> getAllKeys(){
        return new ArrayList<>(m_hmOrderBook.keySet());
    }


    public TreeMap<Integer, StructOrderReportReplyRecord_Pointer> getTreeMap(String str, int scripCode){
        TreeMap<Integer, StructOrderReportReplyRecord_Pointer> ordBookHM = new TreeMap<>();
        ArrayList<Long> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            Long key = allKey.get(i);
            StructOrderReportReplyRecord_Pointer myOrderBook = m_hmOrderBook.get(key);
            if (str.equalsIgnoreCase(eOrderbookSpinnerItems.ALL.name)){
                ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
            }else if (str.equalsIgnoreCase(eOrderbookSpinnerItems.PENDING.name) && myOrderBook.finaPendingQty > 0){
                if (scripCode > 0){
                    if (myOrderBook.scripCode.getValue() == scripCode){
                        ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
                    }
                }else {
                    ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
                }
            }
            else if(str.equalsIgnoreCase(eOrderbookSpinnerItems.OTHERS.name)){
                if(!myOrderBook.getFinalStatus().equalsIgnoreCase(eOrderbookSpinnerItems.CANCELLED.name)
                        && !myOrderBook.getFinalStatus().equalsIgnoreCase(eOrderbookSpinnerItems.FULL_EXECUTED.name)
                        && (myOrderBook.finaPendingQty <= 0)){
                    ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
                }
            }
            else if(str.equalsIgnoreCase(myOrderBook.getFinalStatus())){
                ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
            }
        }
        return ordBookHM;
    }

    public TreeMap<Integer, StructOrderReportReplyRecord_Pointer> getTreeMap(String str){
        TreeMap<Integer, StructOrderReportReplyRecord_Pointer> ordBookHM = new TreeMap<>();
        ArrayList<Long> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            Long key = allKey.get(i);
            StructOrderReportReplyRecord_Pointer myOrderBook = m_hmOrderBook.get(key);
            if (str.equalsIgnoreCase(eOrderbookSpinnerItems.ALL.name)){
                ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
            }else if (str.equalsIgnoreCase(eOrderbookSpinnerItems.PENDING.name) && myOrderBook.finaPendingQty > 0){
                ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
            }
            else if(str.equalsIgnoreCase(eOrderbookSpinnerItems.OTHERS.name)){
                if(!myOrderBook.getFinalStatus().equalsIgnoreCase(eOrderbookSpinnerItems.CANCELLED.name)
                        && !myOrderBook.getFinalStatus().equalsIgnoreCase(eOrderbookSpinnerItems.FULL_EXECUTED.name)
                        && (myOrderBook.finaPendingQty <= 0)){
                    ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
                }
            }
            else if(str.equalsIgnoreCase(myOrderBook.getFinalStatus())){
                ordBookHM.put(myOrderBook.brokerOrderID.getValue(), myOrderBook);
            }
        }
        return ordBookHM;
    }

    public ArrayList<StructOrderReportReplyRecord_Pointer> getAllPendingOrder(){
        ArrayList<StructOrderReportReplyRecord_Pointer> ordBookHM = new ArrayList<>();
        ArrayList<Long> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            Long key = allKey.get(i);
            StructOrderReportReplyRecord_Pointer myOrderBook = m_hmOrderBook.get(key);
            if (myOrderBook.finaPendingQty > 0 && !myOrderBook.isOrderSentToExchModify()){
                ordBookHM.add(myOrderBook);
            }
        }
        return ordBookHM;
    }
    public ArrayList<StructOrderReportReplyRecord_Pointer> getAllPendingOrderForCancel(){
        ArrayList<StructOrderReportReplyRecord_Pointer> ordBookHM = new ArrayList<>();
        ArrayList<Long> allKey = getAllKeys();
        for (int i = 0; i < allKey.size() ; i++) {
            Long key = allKey.get(i);
            StructOrderReportReplyRecord_Pointer myOrderBook = m_hmOrderBook.get(key);
            if (myOrderBook.finaPendingQty > 0 && !myOrderBook.isOrderSentToExchCancel()){
                ordBookHM.add(myOrderBook);
            }
        }
        return ordBookHM;
    }
    public boolean sendOrderBookRequest(){
        Date date = new Date();
        long time = date.getTime();
        if(time - requestTime > 10000) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendOrderTradeReq(eMessageCode.ORDER_BOOK_REQ);
            requestTime = time;
            return true;
        }
        return false;
    }



    public void cancelOrderRequest(StructOrderReportReplyRecord_Pointer orderRow){
        if (orderRow.finaPendingQty>0){
            if(orderRow.exch.getValue() == 'N' && orderRow.exchType.getValue() == 'D'){
                cancelOrderRequest_FNO(orderRow);
            }else if(orderRow.exch.getValue() == 'C' && orderRow.exchType.getValue() == 'D'){
                cancelOrderRequest_CURR(orderRow);
            }else if(orderRow.exch.getValue() == 'S' && orderRow.exchType.getValue() == 'D'){
                cancelOrderRequest_SLBM(orderRow);
            }else{
                cancelOrderRequest_CASH(orderRow);
            }
        }else {
            GlobalClass.showAlertDialog(Constants.CANCEL_PENDQTY_MSG);
        }
    }


    public boolean cancelOrderRequest_CASH(StructOrderReportReplyRecord_Pointer orderRow){

        CancelOrderReq_CASH_Pointer place = new CancelOrderReq_CASH_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(orderRow.exch.getValue());
        place.exchType.setValue(orderRow.exchType.getValue());
        place.scripCode.setValue(orderRow.scripCode.getValue());
        place.scripNameLength.setValue(orderRow.scripNameLength.getValue());
        place.scripName.setValue(orderRow.scripName.getValue());
        place.buySell.setValue(orderRow.buySell.getValue());
        place.qty.setValue(orderRow.qty.getValue());
        place.atMarket.setValue(orderRow.atMarket.getValue());
        place.rate.setValue(orderRow.rate.getValue());
        place.withSL.setValue(orderRow.withSL.getValue());
        place.triggerRate.setValue(orderRow.triggerRate.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        place.allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isIntradayDelivery()?'Y':'N');
        place.orderType.setValue(orderRow.orderType.getValue());

        char slTigged = 'N';
        if(orderRow.sLTriggered.getValue() != ' '){
            slTigged = orderRow.sLTriggered.getValue();
        }
        place.sLTriggered.setValue(slTigged);
        place.tradedQty.setValue(orderRow.getFinalTradeQty());
        place.pendingQty.setValue(orderRow.finaPendingQty);
        place.exchOrderID.setValue(orderRow.exchOrderID.getValue()>0?orderRow.exchOrderID.getValue():orderRow.brokerOrderID.getValue());
        place.exchOrderTime.setValue(Math.max(orderRow.finalexchtime,orderRow.exchOrderTime.getValue()));

        char ah = 'N';
        if(orderRow.aHStatus.getValue() != ' '){
            ah = orderRow.aHStatus.getValue();
        }
        place.aHStatus.setValue(ah);

        if(place.isOrderOK()) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendCancelOrderReq_CASH(place);
            return true;
        }
        else{
            return false;
        }

    }

    private boolean cancelOrderRequest_FNO(StructOrderReportReplyRecord_Pointer orderRow){

        CancelOrderReq_FNO_Pointer place = new CancelOrderReq_FNO_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(orderRow.exch.getValue());
        place.exchType.setValue(orderRow.exchType.getValue());
        place.scripCode.setValue(orderRow.scripCode.getValue());
        place.scripNameLength.setValue(orderRow.scripNameLength.getValue());
        place.scripName.setValue(orderRow.scripName.getValue());
        place.buySell.setValue(orderRow.buySell.getValue());
        place.qty.setValue(orderRow.qty.getValue());
        place.atMarket.setValue(orderRow.atMarket.getValue());
        place.rate.setValue(orderRow.rate.getValue());
        place.withSL.setValue(orderRow.withSL.getValue());
        place.triggerRate.setValue(orderRow.triggerRate.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());

        char slTigged = 'N';
        if(orderRow.sLTriggered.getValue() != ' '){
            slTigged = orderRow.sLTriggered.getValue();
        }
        place.sLTriggered.setValue(slTigged);
        place.tradedQty.setValue(orderRow.getFinalTradeQty());
        place.pendingQty.setValue(orderRow.finaPendingQty);
        place.exchOrderID.setValue(orderRow.exchOrderID.getValue());
        place.exchOrderTime.setValue(Math.max(orderRow.finalexchtime,orderRow.exchOrderTime.getValue()));

        char ah = 'N';
        if(orderRow.aHStatus.getValue() != ' '){
            ah = orderRow.aHStatus.getValue();
        }
        place.aHStatus.setValue(ah);
        place.instrType.setValue(0);
        place.expiry.setValue(0);
        place.strikePrice.setValue(0);
        place.cPType.setValue(0);
        byte calavel = 0;
        place.cALevel.setValue(calavel);
        place.oldQty.setValue(orderRow.qty.getValue());

        place.allowIntradayDelivery.setValue(UserSession.getLoginDetailsModel().isFNOIntradayDelivery()?'Y':'N');
        place.orderType.setValue(orderRow.orderType.getValue());

        if(place.isOrderOK()) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendCancelOrderReq_FNOCURR(place,eMessageCode.CANCEL_ORDER_FNO);
            return true;
        }
        else{
            return false;
        }
    }

    private boolean cancelOrderRequest_CURR(StructOrderReportReplyRecord_Pointer orderRow){

        CancelOrderReq_FNO_Pointer place = new CancelOrderReq_FNO_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exch.setValue(orderRow.exch.getValue());
        place.exchType.setValue(orderRow.exchType.getValue());
        place.scripCode.setValue(orderRow.scripCode.getValue());
        place.scripNameLength.setValue(orderRow.scripNameLength.getValue());
        place.scripName.setValue(orderRow.scripName.getValue());
        place.buySell.setValue(orderRow.buySell.getValue());
        place.qty.setValue(orderRow.qty.getValue());
        place.atMarket.setValue(orderRow.atMarket.getValue());
        place.rate.setValue(orderRow.rate.getValue());
        place.withSL.setValue(orderRow.withSL.getValue());
        place.triggerRate.setValue(orderRow.triggerRate.getValue());
        place.tradeRequestID.setValue(DateUtil.getTimeDiffInSeconds());
        char slTigged = 'N';
        if(orderRow.sLTriggered.getValue() != ' '){
            slTigged = orderRow.sLTriggered.getValue();
        }
        place.sLTriggered.setValue(slTigged);
        place.tradedQty.setValue(orderRow.getFinalTradeQty());
        place.pendingQty.setValue(orderRow.finaPendingQty);
        place.exchOrderID.setValue(orderRow.exchOrderID.getValue());
        place.exchOrderTime.setValue(Math.max(orderRow.finalexchtime,orderRow.exchOrderTime.getValue()));

        char ah = 'N';
        if(orderRow.aHStatus.getValue() != ' '){
            ah = orderRow.aHStatus.getValue();
        }
        place.aHStatus.setValue(ah);
        place.instrType.setValue(0);
        place.expiry.setValue(0);
        place.strikePrice.setValue(0);
        place.cPType.setValue(0);
        byte calavel = 0;
        place.cALevel.setValue(calavel);
        place.oldQty.setValue(orderRow.qty.getValue());
        //place.discQty.setValue(orderRow.di);
        if(place.isOrderOK()) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendCancelOrderReq_FNOCURR(place,eMessageCode.CANCEL_ORDER_CURR);
            return true;
        }
        else{
            return false;
        }
    }
    private boolean cancelOrderRequest_SLBM(StructOrderReportReplyRecord_Pointer orderRow){

        CancelOrderReq_SLBM_Pointer place = new CancelOrderReq_SLBM_Pointer();
        place.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        place.exchange.setValue(orderRow.exch.getValue());
        place.exchangeType.setValue(orderRow.exchType.getValue());
        place.scripCode.setValue(orderRow.scripCode.getValue());
        place.scripNameLength.setValue(orderRow.scripNameLength.getValue());
        place.scripName.setValue(orderRow.scripName.getValue());
        place.buySell.setValue(orderRow.buySell.getValue());
        place.qty.setValue(orderRow.qty.getValue());
        place.atMarket.setValue(orderRow.atMarket.getValue());
        place.limitPrice.setValue(orderRow.rate.getValue());
        place.withSL.setValue(orderRow.withSL.getValue());
        place.tiggerPrice.setValue(orderRow.triggerRate.getValue());
        place.traderRequseterID.setValue(DateUtil.getTimeDiffInSeconds());
        char slTigged = 'N';
        if(orderRow.sLTriggered.getValue() != ' '){
            slTigged = orderRow.sLTriggered.getValue();
        }
        place.slTriggered.setValue(slTigged);
        place.tradeQty.setValue(orderRow.getFinalTradeQty());
        place.pendingQty.setValue(orderRow.finaPendingQty);
        place.exchOrderID.setValue(orderRow.exchOrderID.getValue());
        place.exchOrderTime.setValue(Math.max(orderRow.finalexchtime,orderRow.exchOrderTime.getValue()));

        char ah = 'N';
        if(orderRow.aHStatus.getValue() != ' '){
            ah = orderRow.aHStatus.getValue();
        }
        place.ahStatus.setValue(ah);
        place.instrType.setValue(0);
        place.expityDate.setValue(0);
        //place.discQty.setValue(orderRow.disc);
        if(place.isOrderOK()) {
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendCancelOrderReq_SLBM(place,eMessageCode.EXCHCANCELORDER_SLBS);
            return true;
        }
        else{
            return false;
        }
    }


    public HashMap<Integer, StructOrderSummary> getOrderSummary() {

        HashMap<Integer, StructOrderSummary> summaryMap = new HashMap();
        Object keys[] = m_hmOrderBook.keySet().toArray();

        int latestTime = 0;
        StructOrderReportReplyRecord_Pointer lastTradedSOR = null;

        for (Object brokerOrderId : keys) {

            StructOrderReportReplyRecord_Pointer or = m_hmOrderBook.get(brokerOrderId);

            StructOrderSummary orderSummary = summaryMap.get(or.getExchange());
            if (orderSummary != null) {
                orderSummary.pending.setValue(orderSummary.pending.getValue() + or.getPendingOrders());
                orderSummary.traded.setValue(orderSummary.traded.getValue() + or.getTradedOrders());
                orderSummary.total.setValue(orderSummary.total.getValue() + or.getNoOfOrders());
            } else {
                orderSummary = new StructOrderSummary();
                orderSummary.pending.setValue(or.getPendingOrders());
                orderSummary.traded.setValue(or.getTradedOrders());
                orderSummary.total.setValue(or.getNoOfOrders());
                orderSummary.segment.setValue(or.getExchange());
                summaryMap.put(or.getExchange(), orderSummary);
            }
            if (or.getFinalStatus().equalsIgnoreCase("Pending") && or.exchOrderTime.getValue() > latestTime) {
                latestTime = or.exchOrderTime.getValue();
                lastTradedSOR = or;
            }
            if(lastTradedSOR!=null) {
                String buySell = lastTradedSOR.getBuySell();
                int color = buySell.equalsIgnoreCase("Sell")? GlobalClass.latestContext.getResources().getColor(R.color.red)
                        : GlobalClass.latestContext.getResources().getColor(R.color.green1);
                SpannableString buySell_SSTR = GlobalClass.getSpannableString(buySell.toUpperCase(), 0, buySell.length(), 0f, color);
                lastPendingOrderScrip = TextUtils.concat("Recent: ",buySell_SSTR," "
                        , (lastTradedSOR.getFormatedScripName(false))," "
                        , lastTradedSOR.finaPendingQty + "@"
                        , (Formatter.toTwoDecimalValue(lastTradedSOR.rate.getValue())));
                lastPendingOrderTime = DateUtil.dateFormatter(lastTradedSOR.exchOrderTime.getValue(), Constants.HHMMSS);
            }
        }
        return summaryMap;
    }

    public StrucPendingOrderSummary getPendingOrdersForScripCode(int scripCode) {

        StrucPendingOrderSummary orderSummary = new StrucPendingOrderSummary();
        TreeMap<Integer, StructOrderReportReplyRecord_Pointer> treeMap = getTreeMap(eOrderbookSpinnerItems.PENDING.name, scripCode);
        for (Integer brokerOrderId : treeMap.keySet()) {
            StructOrderReportReplyRecord_Pointer or = treeMap.get(brokerOrderId);
            if (or.scripCode.getValue() == scripCode && or.finaPendingQty > 0  && !or.isOrderSentToExchModify())  {
                orderSummary.totalPending.setValue(orderSummary.totalPending.getValue() + or.finaPendingQty);
                orderSummary.scripCode.setValue(or.scripCode.getValue());
                orderSummary.scripName.setValue(or.getFormatedScripName(false));
                orderSummary.pendingOrders.add(or);
            }
        }
        return orderSummary;
    }
    public StrucPendingOrderSummary getPendingSELLOrdersForScripCode(int scripCode) {

        StrucPendingOrderSummary orderSummary = new StrucPendingOrderSummary();
        TreeMap<Integer, StructOrderReportReplyRecord_Pointer> treeMap = getTreeMap(eOrderbookSpinnerItems.PENDING.name, scripCode);
        for (Integer brokerOrderId : treeMap.keySet()) {
            StructOrderReportReplyRecord_Pointer or = treeMap.get(brokerOrderId);
            if (or.scripCode.getValue() == scripCode && or.finaPendingQty > 0  && !or.isOrderSentToExchModify() && or.buySell.getValue() == 'S')  {
                orderSummary.totalPending.setValue(orderSummary.totalPending.getValue() + or.finaPendingQty);
                orderSummary.scripCode.setValue(or.scripCode.getValue());
                orderSummary.scripName.setValue(or.getFormatedScripName(false));
                orderSummary.pendingOrders.add(or);
            }
        }
        return orderSummary;
    }
}