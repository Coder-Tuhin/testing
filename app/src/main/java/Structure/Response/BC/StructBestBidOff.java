package Structure.Response.BC;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XtremsoftTechnologies on 12/02/16.
 */

public class StructBestBidOff extends StructBase {

    public StructInt token;
    //1.---Token---INT---4bytes---
    public StructInt tBidQ;
    //2.---TBidQ---INT---4bytes---
    public StructInt tOffQ;
    //3.---TOffQ---INT---4bytes---
    public StructInt time;
    //4.---Time---INT---4bytes---
    public StructInt exch1;
    //5.---Exch1---SHORT---2bytes---
    public StructInt qty1;
    //6.---Qty1---INT---4bytes---
    public StructMoney price1;
    //7.---Price1---INT---4bytes---
    public StructShort noOfOrders1;
    //8.---NoOfOrders1---SHORT---2bytes---
    public StructShort ordType1;
    //9.---OrdType1---SHORT---2bytes---
    public StructInt exch2;
    //10.---Exch2---SHORT---2bytes---
    public StructInt qty2;
    //11.---Qty2---INT---4bytes---
    public StructMoney price2;
    //12.---Price2---INT---4bytes---
    public StructShort noOfOrders2;
    //13.---NoOfOrders2---SHORT---2bytes---
    public StructShort ordType2;
    //14.---OrdType2---SHORT---2bytes---

    public StructBestBidOff(byte[] bytes) {
        try {
            init();
            data=new StructValueSetter(fields,bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className + " structure", ex);        }
    }

    public StructBestBidOff() {
        init();
        data=new StructValueSetter(fields);
    }

    private void init() {
        className=getClass().getName();
        token = new StructInt("Token", 0);
        tBidQ = new StructInt("TBidQ", 0);
        tOffQ = new StructInt("TOffQ", 0);
        time = new StructInt("Time", 0);
        exch1 = new StructInt("Exch1", 0);
        qty1 = new StructInt("Qty1", 0);
        price1 = new StructMoney("Price1", 0);
        noOfOrders1 = new StructShort("NoOfOrders1", 0);
        ordType1 = new StructShort("OrdType1", 0);
        exch2 = new StructInt("Exch2", 0);
        qty2 = new StructInt("Qty2", 0);
        price2 = new StructMoney("Price2", 0);
        noOfOrders2 = new StructShort("NoOfOrders2", 0);
        ordType2 = new StructShort("OrdType2", 0);
        fields = new BaseStructure[]{
                token, tBidQ, tOffQ, time, exch1, qty1,
                price1, noOfOrders1, ordType1, exch2,
                qty2, price2, noOfOrders2, ordType2,};
    }

}