package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.ExtractPacket;
import structure.StructBase;
import structure.BaseStructure;
import structure.StructValueSetter;
import structure.StructInt;
import structure.StructDate;
import structure.StructShort;
import utils.GlobalClass;

public class LiteMktDepth extends StructBase {

    public short noOfRecs = 10;

    public StructInt token;
    public StructInt tBidQ;
    public StructInt tOffQ;
    public StructDate time;
    public StructShort priceDivisor;

    public LiteMDDetails structMD[];

    public LiteMktDepth() {
        super();
        init();
        data = new StructValueSetter(fields);
        structMD = new LiteMDDetails[noOfRecs];
        for (int i = 0; i < noOfRecs; i++) {
            structMD[i] = new LiteMDDetails();
        }
    }

    public LiteMktDepth(byte[] bytes) {
        super();
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            structMD = new LiteMDDetails[noOfRecs];
            int iSize = new LiteMDDetails().data.sizeOf();
            for (int i = 0; i < noOfRecs; i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                structMD[i] = new LiteMDDetails(iBytes);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    private void init() {
        className = getClass().getName();
        token = new StructInt("token",0);
        tBidQ = new StructInt("tBidQ",0);
        tOffQ = new StructInt("tOffQ",0);
        time = new StructDate("time",0);
        priceDivisor = new StructShort("priceDivisor",(short)0);

        fields = new BaseStructure[]{
                token,tBidQ,tOffQ,time,priceDivisor
        };
    }

    public void setByteData() throws Exception {
        int aCont = 0;
        byte _orgData[] = new byte[data.sizeOf() + (noOfRecs * (new LiteMDDetails()).data.sizeOf())];
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, _orgData, aCont, opData.length);
        aCont += opData.length;
        for (LiteMDDetails ineerStruct : structMD) {
            byte ipData[] = ineerStruct.data.getByteArr();
            System.arraycopy(ipData, 0, _orgData, aCont, ipData.length);
            aCont += ipData.length;
        }
        this.orgData = _orgData;
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