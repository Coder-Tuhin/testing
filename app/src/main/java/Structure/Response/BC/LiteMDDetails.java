package Structure.Response.BC;


import java.lang.reflect.Field;
import structure.StructBase;
import structure.BaseStructure;
import structure.StructValueSetter;
import structure.StructInt;
import structure.StructMoney;
import structure.StructShort;
import utils.GlobalClass;

public class LiteMDDetails extends StructBase {

    public StructInt qty;
    public StructMoney price;
    public StructShort noOfOrders;

    public LiteMDDetails() { this(null);}

    public LiteMDDetails(byte[] bytes) {
        super();
        try {
            className = getClass().getName();
            qty = new StructInt("qty",0);
            price = new StructMoney("price",0);
            noOfOrders = new StructShort("noOfOrders",(short)0);

            fields = new BaseStructure[]{
                    qty,price,noOfOrders
            };
            data = new StructValueSetter(fields, bytes);
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
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