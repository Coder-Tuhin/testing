package wealth.wealthStructure;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructBigMoney;
import structure.StructBoolean;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructMyWealth extends StructBase {

    public StructString clientCode;//length=15
    public StructBigMoney totalBalace;
    public StructBigMoney totalMargin;
    public StructBigMoney totalHolding;
    public StructBigMoney totalMF;
    public StructBigMoney totalIPO;
    public StructBigMoney totalFixedIncome;
    public StructBigMoney grandTotal;

    public StructBigMoney cashBalance;
    public StructBigMoney fnoBalance;
    public StructBigMoney commBalance;
    public StructBigMoney currBalance;

    public StructBigMoney fnoMargin;
    public StructBigMoney commMargin;
    public StructBigMoney currMargin;

    public StructBigMoney dpHolding;
    public StructBigMoney colletaral;

    public StructBigMoney bondValue;
    public StructBigMoney depository;
    public StructBigMoney physical;
    public StructBigMoney pendingAllotment;
    public StructBigMoney fixedDeposit;

    public StructString lastUpdateTime;
    public StructString nextUpdateTime;

    public StructBoolean isAllDataCame;
    public StructString authID;
    public StructString folioNumber;

    public StructString reserve;
    public StructString msg;

    public StructBoolean isShowPertialData;

    public StructMyWealth() {
        init(500);
        data = new StructValueSetter(fields);
    }

    public StructMyWealth(byte bytes[]) {
        try {
            init(bytes.length);
            data = new StructValueSetter(fields, bytes);
            clientCode.setValue(clientCode.getValue().toUpperCase());
        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void init(int length) {

        className = getClass().getName();
        clientCode = new StructString("ClientCode", 14, "");

        totalBalace = new StructBigMoney("totalBalace", 0);
        totalMargin = new StructBigMoney("totalMargin", 0);
        totalHolding = new StructBigMoney("totalHolding", 0);
        totalMF = new StructBigMoney("totalMF", 0);
        totalIPO = new StructBigMoney("totalIPO", 0);
        totalFixedIncome = new StructBigMoney("totalFixedIncome", 0);
        grandTotal = new StructBigMoney("grandTotal", 0);

        cashBalance = new StructBigMoney("cashBalance", 0);
        fnoBalance = new StructBigMoney("fnoBalance", 0);
        commBalance = new StructBigMoney("commBalance", 0);
        currBalance = new StructBigMoney("currBalance", 0);

        fnoMargin = new StructBigMoney("fnoMargin", 0);
        commMargin = new StructBigMoney("commMargin", 0);
        currMargin = new StructBigMoney("currMargin", 0);

        dpHolding = new StructBigMoney("dpHolding", 0);
        colletaral = new StructBigMoney("colletaral", 0);

        bondValue = new StructBigMoney("bondValue", 0);
        depository = new StructBigMoney("depository", 0);
        physical = new StructBigMoney("physical", 0);
        pendingAllotment = new StructBigMoney("pendingAllotment", 0);
        fixedDeposit = new StructBigMoney("fxedDeposit", 0);

        lastUpdateTime = new StructString("lastUpdateTime", 20, "");
        nextUpdateTime = new StructString("nextUpdateTime", 20, "");

        isAllDataCame = new StructBoolean("isalldatacame", true);
        reserve = new StructString("reserve", 70, "");
        msg = new StructString("reserve", 20, "");

        isShowPertialData = new StructBoolean("isShowPertialData", false);

        if(length > 400){
            authID = new StructString("AuthID",100,"");
            folioNumber = new StructString("Folio",20,"");

            fields = new BaseStructure[]{
                    clientCode,totalBalace,totalMargin,totalHolding,totalMF,totalIPO,totalFixedIncome,grandTotal,
                    cashBalance,fnoBalance,commBalance,currBalance,fnoMargin,commMargin,currMargin,dpHolding,
                    colletaral,bondValue,depository,physical,pendingAllotment,fixedDeposit,
                    lastUpdateTime,nextUpdateTime,isAllDataCame,reserve,msg,authID,folioNumber,isShowPertialData
            };
        } else {

            authID = new StructString("AuthID",50,"");
            folioNumber = new StructString("Folio",20,"");

            fields = new BaseStructure[]{
                    clientCode,totalBalace,totalMargin,totalHolding,totalMF,totalIPO,totalFixedIncome,grandTotal,
                    cashBalance,fnoBalance,commBalance,currBalance,fnoMargin,commMargin,currMargin,dpHolding,
                    colletaral,bondValue,depository,physical,pendingAllotment,fixedDeposit,
                    lastUpdateTime,nextUpdateTime,isAllDataCame,authID,folioNumber,msg
            };
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getNextUpdateTime(){
        if(lastUpdateTime.getValue().equalsIgnoreCase(nextUpdateTime.getValue())){
            return "";
        }
        return nextUpdateTime.getValue();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getLastUpdateTime(){
        return lastUpdateTime.getValue();
    }

    public String convertWealthDate(String dt) {
        try {
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date prevDate = prevDateFormat.parse(dt);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("HH:mm");
            String newdt = newDateFormat.format(prevDate);
            return newdt;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "";
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isTimebetween(){
        LocalTime start = LocalTime.parse( "09:10:00" );
        LocalTime stop = LocalTime.parse( "15:30:00" );
        LocalTime target = LocalTime.now();
        Boolean isTargetAfterStartAndBeforeStop = ( target.isAfter( start ) && target.isBefore( stop ) ) ;
        return isTargetAfterStartAndBeforeStop;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(this.getClass().getName());
            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(this.getClass().getSimpleName() + " [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName + " = " + f.get(this) + ", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }

    public double getGrandTotal(){
        return (totalBalace.getValue()+totalMargin.getValue()+totalHolding.getValue()+totalMF.getValue()+totalIPO.getValue()+totalFixedIncome.getValue());
    }
}