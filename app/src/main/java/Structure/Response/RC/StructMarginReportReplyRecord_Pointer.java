package Structure.Response.RC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructDouble;
import structure.StructString;
import structure.StructValueSetter;

/**
 * Created by Xtremsoft on 11/18/2016.
 */

public class StructMarginReportReplyRecord_Pointer  extends StructBase {

    public StructDouble openingMargin;
    public StructDouble additionalMarign;
    public StructDouble marginForOpenPos;
    public StructDouble marginForPendOrder;
    public StructDouble availableMargin;
    public StructDouble ledgerBalance;
    public StructDouble t1Short;
    public StructDouble t2Short;
    public StructDouble withdrawable;
    public StructDouble mFDebit;
    public StructDouble fOBalance;
    public StructDouble fOMgnPayable;
    public StructDouble fOExcessCol;
    public StructDouble holdingsMargin;
    public StructDouble commodityMargin;
    public StructDouble slbmMargin;
    public StructDouble pledgeMargin;
    public StructString reserve;
    public StructDouble marginOfBFPosition;
    public StructDouble reversalIntrPeakMargin;
    public StructDouble reserved2;
    public StructDouble reserved3;
    public StructDouble reserved4;
    public StructDouble reserved5;
    public StructDouble reserved6;
    public StructDouble reserved7;

    public StructMarginReportReplyRecord_Pointer(){
        init(500);
        data= new StructValueSetter(fields);
    }
    public StructMarginReportReplyRecord_Pointer(byte[] bytes){
        init(bytes.length);
        data= new StructValueSetter(fields,bytes);
    }
    private void init(int length) {
        openingMargin = new StructDouble("openingMargin",0);
        additionalMarign = new StructDouble("additionalMarign",0);
        marginForOpenPos = new StructDouble("marginForOpenPos",0);
        marginForPendOrder = new StructDouble("marginForPendOrder",0);
        availableMargin = new StructDouble("availableMargin",0);
        ledgerBalance = new StructDouble("ledgerBalance",0);
        t1Short = new StructDouble("t1Short",0);
        t2Short = new StructDouble("t2Short",0);
        withdrawable = new StructDouble("withdrawable",0);
        mFDebit = new StructDouble("mFDebit",0);
        fOBalance = new StructDouble("fOBalance",0);
        fOMgnPayable = new StructDouble("fOMgnPayable",0); // need to show premium.
        fOExcessCol = new StructDouble("fOExcessCol",0);
        holdingsMargin = new StructDouble("holdingsMargin",0);
        commodityMargin = new StructDouble("junk1",0);
        slbmMargin = new StructDouble("SLBMMargin",0);
        pledgeMargin = new StructDouble("pledgeMargin",0);
        reserve = new StructString("reserve",10,"");
        marginOfBFPosition = new StructDouble("marginOfBFPosition",0);
        reversalIntrPeakMargin = new StructDouble("ReversalIntrPeakMargin",0);
        reserved2 = new StructDouble("reserved2",0);
        reserved3 = new StructDouble("reserved3",0);
        reserved4 = new StructDouble("reserved4",0);
        reserved5 = new StructDouble("reserved5",0);
        reserved6 = new StructDouble("reserved6",0);
        reserved7 = new StructDouble("reserved7",0);

        if(length > 130){
            fields = new BaseStructure[]{
                    openingMargin, additionalMarign, marginForOpenPos, marginForPendOrder, availableMargin, ledgerBalance, t1Short, t2Short, withdrawable, mFDebit,
                    fOBalance, fOMgnPayable, fOExcessCol, holdingsMargin, commodityMargin, slbmMargin,pledgeMargin,
                    reserve,marginOfBFPosition,reversalIntrPeakMargin,reserved2,reserved3,reserved4,reserved5,reserved6,reserved7
            };
        }else {
            fields = new BaseStructure[]{
                    openingMargin, additionalMarign, marginForOpenPos, marginForPendOrder, availableMargin, ledgerBalance, t1Short, t2Short, withdrawable, mFDebit,
                    fOBalance, fOMgnPayable, fOExcessCol, holdingsMargin, commodityMargin, slbmMargin
            };
        }
    }
}