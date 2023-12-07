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
import structure.StructDate;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructHoldingDateCheckingRes extends StructBase {


    public StructBoolean isNeedToUpdateDP;
    public StructBoolean isNeedToUpdateFNO;
    public StructDate updateDateDP;
    public StructDate updateDateFNO;

    public StructHoldingDateCheckingRes(byte bytes[]) {
        try {
            init(bytes.length);
            data = new StructValueSetter(fields, bytes);

        } catch (Exception e) {
            GlobalClass.onError("Error in " + className, e);
        }
    }

    private void init(int length) {

        className = getClass().getName();
        isNeedToUpdateDP = new StructBoolean("isNeedToUpdateDP", false);
        isNeedToUpdateFNO = new StructBoolean("isNeedToUpdateFNO", false);
        updateDateDP = new StructDate("updateDate", 0);
        updateDateFNO = new StructDate("updateDate", 0);
        fields = new BaseStructure[]{
                isNeedToUpdateDP,isNeedToUpdateFNO,updateDateDP,updateDateFNO
        };
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
}