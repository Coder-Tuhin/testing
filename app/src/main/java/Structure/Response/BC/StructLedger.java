package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructDouble;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructLedger extends StructBase {

    public StructString date;
    public StructString account;
    public StructDouble debit;
    public StructDouble credit;
    public StructDouble balance;

    public StructLedger() {
        init();
        data = new StructValueSetter(fields);
    }
    public StructLedger(byte bytes[]) {
        try {
            init();
            data = new StructValueSetter(fields, bytes);
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }
    private void init() {

        className = getClass().getName();
        date = new StructString("date", 12, "");
        account = new StructString("account",100,"");
        debit = new StructDouble("debit", 0);
        credit = new StructDouble("credit", 0);
        balance = new StructDouble("balance", 0);

        fields = new BaseStructure[]{
                date, account, debit, credit, balance
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