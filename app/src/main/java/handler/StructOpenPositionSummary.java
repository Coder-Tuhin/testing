package handler;

import java.util.ArrayList;

import Structure.BaseStructure.StructBase;
import Structure.Other.StructMobNetPosition;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructString;
import structure.StructValueSetter;

public class StructOpenPositionSummary extends StructBase {

    public StructInt scripCode;
    public StructString scripName;
    public StructInt totalPosition;

    public ArrayList<StructMobNetPosition> openPositions;

    public StructOpenPositionSummary(){
        init();
        data = new StructValueSetter(fields);
        openPositions = new ArrayList<>();
    }
    private void init(){
        className = getClass().getName();

        scripCode = new StructInt("scripCode",0);
        scripName = new StructString("scripName",50,"");
        totalPosition = new StructInt("totalPending",0);
        fields = new BaseStructure[]{
                scripCode,scripName,totalPosition
        };
    }
}
