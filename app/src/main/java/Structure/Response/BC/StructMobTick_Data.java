package Structure.Response.BC;

/**
 * Created by XPC-11 on 09-10-2014.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBigMoney;
import structure.StructDate;
import structure.StructDouble;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 *
 * @author xtremsoft
 */
public class StructMobTick_Data extends StructBase {

    public StructInt intOpen;
    public StructShort shortHigh;
    public StructShort shortLow;
    public StructShort shortClose;
    public StructInt lastQty;
    public StructInt totalQty;
    public StructInt lastTimeInt;

    public StructDate lastTime;
    public StructDouble open,high,low,close;
    public StructMoney openM,highM,lowM,closeM;

    public StructMobTick_Data (boolean isNew){
        init();
        if(isNew){
            fields=new BaseStructure[]{
                    openM, highM, lowM, closeM,lastQty,totalQty,lastTimeInt
            };
        }
        else {
            fields = new BaseStructure[]{
                    intOpen, shortHigh, shortLow, shortClose, lastQty, totalQty, lastTimeInt
            };

        }
        data = new StructValueSetter(fields);
    }
    public StructMobTick_Data (byte bytes[],boolean isNew){
        try {
            init();

            if(isNew){
                fields=new BaseStructure[]{
                        openM, highM, lowM, closeM,lastQty,totalQty,lastTimeInt
                };
                data=new StructValueSetter(fields,bytes);
                open.setValue(openM.getValue());
                high.setValue(highM.getValue());
                low.setValue(lowM.getValue());
                close.setValue(closeM.getValue());
            }
            else{
                fields=new BaseStructure[]{
                        intOpen, shortHigh, shortLow, shortClose,lastQty,totalQty,lastTimeInt
                };
                data=new StructValueSetter(fields,bytes);

                double finalOpen = ((double)intOpen.getValue())/100;
                double finalHigh = ((double)(intOpen.getValue()+shortHigh.getValue()))/100;
                double finalLow = ((double)(intOpen.getValue() - shortLow.getValue()))/100;
                double finalClose = ((double)(intOpen.getValue() - shortClose.getValue()))/100;

                open.setValue(finalOpen);
                high.setValue(finalHigh);
                low.setValue(finalLow);
                close.setValue(finalClose);
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in "+ className,e);
        }

        lastTime.setValue(lastTimeInt.getValue());
    }

    private void init(){
        className=getClass().getName();
        intOpen=new StructInt("Open",0);
        shortHigh=new StructShort("ShortHigh",0);
        shortLow=new StructShort("ShortLow",0);
        shortClose=new StructShort("ShortClose",0);
        lastQty=new StructInt("LastQty",0);
        totalQty=new StructInt("TotalQty",0);
        lastTime=new StructDate("LastTime",0);
        lastTimeInt=new StructInt("LastTimeInt",0);

        open=new StructDouble("Open",0);
        high=new StructDouble("High",0);
        low=new StructDouble("Low",0);
        close=new StructDouble("Close",0);

        openM=new StructMoney("Open",0);
        highM=new StructMoney("High",0);
        lowM=new StructMoney("Low",0);
        closeM=new StructMoney("Close",0);
    }
    public void  setLastTimeI(int ltTime){
        lastTimeInt.setValue(ltTime);
        lastTime.setValue(ltTime);
    }

}

