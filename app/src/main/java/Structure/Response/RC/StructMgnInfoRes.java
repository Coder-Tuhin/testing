package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.StructBigMoney;
import structure.StructByte;
import structure.StructInt;
import structure.StructMoney;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructMgnInfoRes extends StructBase {

    public StructString clientCode;    //1.---ClientCode---CHAR---10bytes---
    public StructInt exchSegment;  //4
    public StructBigMoney ledBal;
    public StructBigMoney pdhv;
    public StructBigMoney recPay;
    public StructBigMoney pl;
    public StructBigMoney adhoc;
    public StructBigMoney mbpoCash;
    public StructBigMoney mbpoFno;
    public StructBigMoney mbpoOth;
    public StructBigMoney mbpo;
    public StructBigMoney mbopCash;
    public StructBigMoney mbopFno;
    public StructBigMoney mbopOth;
    public StructBigMoney mbop;

    public StructBigMoney avlBalanceMgn;
    public StructMoney mgnMult; //4
    public StructBigMoney actMgn; //8
    //total = 138
    //Newly added : 28:01:2019
    public StructMoney Charges;
    public StructMoney expMulti; //4
    public StructBigMoney OptPremPend;
    public StructBigMoney OptPremPosn;
    public StructBigMoney PremCr;
    public StructByte AddOptPrem2Mgn;

    public StructBigMoney NAM;
    public StructByte useNAM;
    public StructBigMoney initMgn;
    public StructBigMoney eqHldgCvr;

    public StructMgnInfoRes() {
        init(200);
        data = new StructValueSetter(fields);
    }

    public StructMgnInfoRes(byte[] bytes) {
        try {
            init(bytes.length);
            data = new StructValueSetter(fields, bytes);
            mbpo.setValue(mbpoCash.getValue() + mbpoFno.getValue() + mbpoOth.getValue());
            mbop.setValue(mbopCash.getValue() + mbopFno.getValue() + mbopOth.getValue());
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init(int length) {
        className = getClass().getName();
        clientCode = new StructString("ClientCode ", 10, "");
        exchSegment = new StructInt("ExchSegment ", 0);
        ledBal = new StructBigMoney("LedBal ", 0);
        pdhv = new StructBigMoney("Pdhv ", 0);
        recPay = new StructBigMoney("RecPay ", 0);
        pl = new StructBigMoney("Pl ", 0);
        adhoc = new StructBigMoney("Adhoc ", 0);
        mbpoCash = new StructBigMoney("MbpoCash ", 0);
        mbpoFno = new StructBigMoney("MbpoFno ", 0);
        mbpoOth = new StructBigMoney("MbpoOth ", 0);
        mbpo = new StructBigMoney("Mbpo ", 0);
        mbopCash = new StructBigMoney("MbopCash ", 0);
        mbopFno = new StructBigMoney("MbopFno ", 0);
        mbopOth = new StructBigMoney("MbopOth ", 0);
        mbop = new StructBigMoney("Mbop ", 0);
        avlBalanceMgn = new StructBigMoney("AvlBalanceMgn ", 0);
        mgnMult = new StructMoney("MgnMult ", 0);
        actMgn = new StructBigMoney("ActMgn", 0);

        //Newly added : 28:01:2019
        Charges = new StructMoney("Charges",0);
        expMulti = new StructMoney("Exposure Mutiplier",0);
        OptPremPend = new StructBigMoney("OptPremPend", 0);
        OptPremPosn = new StructBigMoney("OptPremPosn", 0);
        PremCr = new StructBigMoney("PremCr", 0);
        AddOptPrem2Mgn = new StructByte("AddOptPrem2Mgn", 0);

        NAM = new StructBigMoney("NAM", 0);
        useNAM = new StructByte("UseNAM", 0);
        initMgn = new StructBigMoney("InitMgn", 0);
        eqHldgCvr = new StructBigMoney("EqHldgCvr", 0);
        if (length >145){
            fields = new structure.BaseStructure[]{
                    clientCode, exchSegment, ledBal, pdhv, recPay, pl, adhoc, mbpoCash, mbpoFno, mbpoOth,
                    mbpo, mbopCash, mbopFno, mbopOth, mbop, Charges, avlBalanceMgn, mgnMult, actMgn,
                    expMulti,OptPremPend,OptPremPosn,PremCr,AddOptPrem2Mgn,NAM,useNAM,initMgn,eqHldgCvr
            };
        }else {
            fields = new structure.BaseStructure[]{
                    clientCode, exchSegment, ledBal, pdhv, recPay, pl, adhoc, mbpoCash, mbpoFno, mbpoOth,
                    mbpo, mbopCash, mbopFno, mbopOth, mbop, avlBalanceMgn, mgnMult, actMgn
            };
        }
    }

    public double getMarginAvlForTrading(){
        double availableMargin = ledBal.getValue() + recPay.getValue() + pdhv.getValue() + pl.getValue()
                + adhoc.getValue() + initMgn.getValue();
        return availableMargin;
    }
    public double getMrgnUtilised(){
        return (getMBOP() + getMBPO()+getCharges());
    }

    public double getBkdPlOrMTMPl(){
        return pl.getValue();
    }


    //Start Modification: Modified 28:01:2019. Ordered by Sriram sir
    public double AvlbMgn() {
        if (AddOptPrem2Mgn.getValue() == 1) {
            return (avlBalanceMgn.getValue() - OptPremPend.getValue() - OptPremPosn.getValue() - PremCr.getValue());
        } else {
            double dPrem = Math.max(PremCr.getValue()+OptPremPosn.getValue(),0);
            return avlBalanceMgn.getValue() - OptPremPend.getValue() - dPrem;
        }
    }
    public double AvlbMgnOPT() {
        return AvlbMgn() - getPremCrToDisp();
    }
    public double getOptPremPend(){
        return OptPremPend.getValue();
    }

    public double getOptPremPosn() {
        return OptPremPosn.getValue();
    }

    public double getPremCr() {
        return PremCr.getValue();
    }

    public boolean getItemVisibility() {
        return AddOptPrem2Mgn.getValue() == 1;
    }

    public double getCharges() {
        return Charges.getValue();
    }

    public double getMBOPFno() {

        if(AddOptPrem2Mgn.getValue() == 1){
            return (mbopFno.getValue()+getOptPremPosn()+getPremCr());
        }else{
            double iperm = getOptPremPosn()+getPremCr();
            if(iperm>0){
                iperm = iperm + mbopFno.getValue();
                return iperm;
            }
            return mbopFno.getValue();
        }
    }
    public double getMBPOFno(){
        return mbpoFno.getValue() + getOptPremPend();
    }
    public double getMBOP(){
        return (mbopCash.getValue() + mbopOth.getValue())  + getMBOPFno();
    }
    public double getMBPO(){
        return (mbpoCash.getValue() + mbpoOth.getValue())  + getMBPOFno();
    }

    public double getPremCrToDisp(){
        if(AddOptPrem2Mgn.getValue() != 1){
            double iprem = getPremCr() + getOptPremPosn();
            if(iprem<0){
                return iprem;
            }
            return 0;
        }
        return getPremCr();
    }
}
