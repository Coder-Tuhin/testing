package handler;

import java.util.ArrayList;

import Structure.BaseStructure.StructBase;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 4/24/2017.
 */
public class StrucPendingOrderSummary extends StructBase {
    public StructInt scripCode;
    public StructString scripName;
    public StructInt totalPending;

    public ArrayList<StructOrderReportReplyRecord_Pointer>  pendingOrders;

    public StrucPendingOrderSummary(){
        init();
        data = new StructValueSetter(fields);
        pendingOrders = new ArrayList<>();
    }

    private void init(){
        className = getClass().getName();

        scripCode = new StructInt("scripCode",0);
        scripName = new StructString("scripName",50,"");
        totalPending = new StructInt("totalPending",0);
        fields = new BaseStructure[]{
                scripCode,scripName,totalPending
        };
    }
}
