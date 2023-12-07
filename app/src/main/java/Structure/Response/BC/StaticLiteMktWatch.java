package Structure.Response.BC;

import android.graphics.Color;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.text.NumberFormat;

import enums.eDivider;
import enums.eExch;
import structure.StructMoney;
import utils.Formatter;
import utils.GlobalClass;

public class StaticLiteMktWatch {
    private StaticWatch sw;
    private LiteMktWatch lw;

    private  boolean isIndice = false;
    private float divider= eDivider.DIVIDER1.value;
    private int round = 2;

    NumberFormat formatter;

    //private boolean isLWPacketCame;
    //private boolean isSWPacketCame;

    public StaticLiteMktWatch(int token,int segment){
        sw = new StaticWatch();
        sw.token.setValue(token);
        lw = new LiteMktWatch();
        lw.token.setValue(token);

        isIndice = GlobalClass.indexScripCodeOrNot(token);
        divider = eDivider.DIVIDER1.value;
        round = 2;
        formatter = Formatter.getFormatter(segment);
        if (segment == eExch.NSECURR.value){
            divider = eDivider.DIVIDER100000.value;;
            round = 4;
        }
    }
    public LiteMktWatch getLw() {
        return lw;
    }

    public StaticWatch getSw() {
        return sw;
    }


    public void setLw(LiteMktWatch lw) {
        this.lw = lw;
        //isLWPacketCame = true;
        try {
            if (this.lw.newHiLo.getValue() > 0) {
                if(this.lw.newHiLo.getValue() > sw.high.getValue())
                    sw.high.setValue(lw.newHiLo.getValue());
                else if(this.lw.newHiLo.getValue() < sw.low.getValue())
                    sw.low.setValue(lw.newHiLo.getValue());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setSw(StaticWatch sw) {
        this.sw = sw;
        //isSWPacketCame = true;
        isIndice = GlobalClass.indexScripCodeOrNot(sw.token.getValue());
        divider = eDivider.DIVIDER1.value;
        round = 2;
        formatter = Formatter.getFormatter(sw.segment.getValue());
        if (sw.segment.getValue() == eExch.NSECURR.value){
            divider = eDivider.DIVIDER100000.value;;
            round = 4;
        }
    }

    public int getToken(){
        return lw.token.getValue();
    }
    public int getSegment(){
        return lw.segment.getValue();
    }
    public NumberFormat getFormatter() {
        return formatter;
    }

    public String getTrdExc(){
        if (sw.lowerTrdExc.getValue()>0 && sw.higherTrdExc.getValue()>0){
            return getFormatedTrd(sw.lowerTrdExc)+" - "+getFormatedTrd(sw.higherTrdExc);
        }
        return "";
    }

    private String getFormatedTrd(StructMoney value){
        return ""+formatter.format(value.getValue()/divider);
    }
    public float getAskRateForPlaceOrder() {
        return (float)round(lw.offRate.getValue()/divider, round);
    }
    public float getBidRateForPlaceOrder() {
        return (float)round(lw.bidRate.getValue()/divider,round);
    }
    public float getLastRateForPlaceOrder() {
        return (float)round(lw.lastRate.getValue()/divider,round);
    }
    public float getPrevCloseForPlaceOrder() {
        return (float)round(lw.pClose.getValue()/divider,round);
    }
    public float getUpperCircuitForPlaceOrder() {
        return (float)round(sw.upperCircuit.getValue()/divider,round);
    }
    public float getLowerCircuitForPlaceOrder() {
        return (float)round(sw.lowerCircuit.getValue()/divider,round);
    }
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public float getLastRate(){
        return (float) Formatter.round(lw.lastRate.getValue()/divider,round);
    }
    public float getLastRateNoRound(){
        return lw.lastRate.getValue()/divider;
    }
    public float getUpperCkt(){
        return sw.upperCircuit.getValue()/divider;
    }
    public float getLowerCkt(){
        return sw.lowerCircuit.getValue()/divider;
    }
    public float getOpen(){
        return sw.openRate.getValue()/divider;
    }
    public float getLow(){
        return sw.low.getValue()/divider;
    }
    public float getHigh(){
        return sw.high.getValue()/divider;
    }
    public float getPClose(){
        return lw.pClose.getValue()/divider;
    }

    public float getAverage(){
        return lw.avgRate.getValue()/divider;
    }

    public float getYearLow(){
        return (sw.lowestEver.getValue()>0)?(((sw.low.getValue()>0 && sw.low.getValue()<sw.lowestEver.getValue())?sw.low.getValue():sw.lowestEver.getValue())/divider)
                :(sw.low.getValue()/divider);
    }
    public float getYearHigh(){
        return (sw.highestEver.getValue() > 0) ?((sw.high.getValue()>sw.highestEver.getValue()?sw.high.getValue():sw.highestEver.getValue())/divider)
                :(sw.high.getValue()/divider);
    }
    public int getHighOI(){
        return sw.dayHiOI.getValue();
    }
    public int getLowOI(){
        return sw.dayLoOI.getValue();
    }
    public float getBestBid(){
        return lw.bidRate.getValue()/divider;
    }
    public float getBestASK(){
        return lw.offRate.getValue()/divider;
    }

    public double getAbsChg(){
        double absChange = 0, lastRate = this.getLastRate(),
                prevClose = this.getPClose();

        if(isIndice){
            if ((lastRate > 0) && (prevClose > 0)) {
                absChange = (lastRate - prevClose);
            }
        }else {
            if (lastRate > 0 && prevClose > 0 && lw.totalQty.getValue() > 0) {
                absChange = (lastRate - prevClose);
            }
        }
        return absChange;
    }

    public double getPerChg(){
        double perChange = 0,prevClose = this.getPClose();
        if (prevClose > 0) {
            perChange = (getAbsChg() / prevClose) * 100.00;
        }
        return perChange;
    }

    public void setLtpWithBkgColor(TextView txtVal)throws  Exception{
        if(txtVal !=null ) {
            double preval = GlobalClass.mktDataHandler.getPrevMkt5001Data(sw.token.getValue()).lastRate.getValue();
            setLtpWithBkgColor(txtVal,preval);
        }
    }
    public void setLtpWithBkgColor(TextView txtVal, double prevVal)throws  Exception{
        if(txtVal !=null ) {
            double lastRate = lw.lastRate.getValue();
            int txtColor,txtBkgColor,tickUpDownTxtColor;
            if (prevVal != 0) {
                if (prevVal < lastRate) {
                    txtBkgColor = R.drawable.tick_up_bg;
                    txtColor = Color.WHITE;
                } else if (prevVal > lastRate) {
                    txtBkgColor = R.drawable.tick_down_bg;
                    txtColor = Color.WHITE;
                } else {
                    txtBkgColor = android.R.color.transparent;
                    txtColor = getTextColor();
                }

                txtVal.setBackgroundResource(txtBkgColor);
                txtVal.setTextColor(txtColor);
            }
            txtVal.setText(Formatter.formatter.format(lastRate));
        }
    }
    public void setLtpWithTxtColor(TextView txtVal,double prevVal)throws  Exception{
        if(txtVal !=null ) {
            double lastRate = getLastRate();
            int txtColor;
            if (prevVal != 0) {
                if (prevVal < lastRate) {
                    txtColor = getTickUpColor();
                } else if (prevVal > lastRate) {
                    txtColor = getTickDownColor();
                } else {
                    txtColor = getTextColor();
                }
                txtVal.setTextColor(txtColor);
            }
            txtVal.setText(Formatter.formatter.format(lastRate));
        }
    }
    public void setPerChgWithColor(TextView txtVal)throws Exception{
        if(txtVal!=null) {
            int color;
            String chg = Formatter.formatter.format(getPerChg()) + "%";
            txtVal.setText(chg);
            if (chg.contains("-")) {
                color = getTickDownColor();
            } else if (chg.equalsIgnoreCase("0.00%")) {
                color = getTextColor();
            } else {
                color = getTickUpColor();
            }
            txtVal.setTextColor(color);
        }
    }
    public void setChgWithColor(TextView perChg,TextView absChg,boolean isPerChgBracket)throws Exception{
        if(perChg!=null) {
            int color;
            String chg = Formatter.formatter.format(getPerChg()) + "%";
            if(isPerChgBracket){
                chg = "(" +chg+")";
            }
            perChg.setText(chg);
            if (chg.contains("-")) {
                color = getTickDownColor();
            } else if (chg.equalsIgnoreCase("0.00%")) {
                color = getTextColor();
            } else {
                color = getTickUpColor();
            }
            perChg.setTextColor(color);
            if(absChg != null){
                absChg.setText(Formatter.formatter.format(getAbsChg()));
                absChg.setTextColor(color);
            }
        }
    }

    private int getTickUpColor(){
        return GlobalClass.latestContext.getResources().getColor(R.color.green1);
    }

    private int getTickDownColor(){
        return GlobalClass.latestContext.getResources().getColor(R.color.red);
    }
    private int getTextColor(){
        return GlobalClass.latestContext.getResources().getColor(R.color.white);
    }


    public String getLastrateforReader(){
        String lastRate = getLastRate()+"";
        double perChg = getPerChg();

        String perUpDwn = "";
        if (perChg<0){
            perUpDwn = "    Down";
        }else {
            perUpDwn = "    Up";
        }
        return lastRate+"\n\n"+perUpDwn+"\n"+Formatter.formatter.format(perChg)+"  Percent";
    }

    public void setTradeExecutionRange(FnoNSE_TradeExecution mkt5001Data) {
        try{
            sw.higherTrdExc.setValue(mkt5001Data.higherTrdExc.getIntValue());
            sw.lowerTrdExc.setValue(mkt5001Data.lowerTrdExc.getIntValue());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}