package Structure.Response.Scrip;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructInt;
import structure.StructShort;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/17/2016.
 */
public class StructSearchScrip extends StructBase {

    public short noOfRecs = 600;

    public StructInt noOfRecords;
    public StructByte downloadComplete;
    public StructInt exchangeFromClient;
    public StructSearchScripRow1[] searchScripArray;

    public ArrayList<StructSearchScripRow1> finalList;

    public StructSearchScrip(){
        init();
        data = new StructValueSetter(fields);
    }

    private void init() {
        className = getClass().getName();
        noOfRecords = new StructInt("noOfRecords", 0);
        downloadComplete = new StructByte("downloadComplete", 0);
        exchangeFromClient = new StructInt("exchangeFromClient", 0);

        fields = new BaseStructure[]{
                noOfRecords,downloadComplete,exchangeFromClient
        };
    }

    public StructSearchScrip(byte bytes[]) {
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();

            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());

            searchScripArray = new StructSearchScripRow1[noOfRecords.getValue()];
            int iSize = new StructSearchScripRow1().data.sizeOf();
            for (int i = 0; i < noOfRecords.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                searchScripArray[i] = new StructSearchScripRow1(iBytes);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }
    }

    public void setByteData() throws Exception {
        int aCont = 0;
        byte[] _orgData = new byte[data.sizeOf()
                + (noOfRecords.getValue() * (new StructSearchScripRow1()).data.sizeOf())];
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, _orgData, aCont, opData.length);
        aCont += opData.length;
        for (StructSearchScripRow1 mdC : searchScripArray) {
            if (mdC == null) {
                mdC = new StructSearchScripRow1();
            }
            byte ipData[] = mdC.data.getByteArr();
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
            sb.append(this.getClass().getSimpleName()).append(" [ ");
            for (Field f : aClassFields) {
                String fName = f.getName();
                sb.append(fName).append(" = ").append(f.get(this)).append(", ");
            }
            sb.append("]");
        } catch (Exception e) {
            GlobalClass.onError("Error in toString:", e);
        }
        return sb.toString();
    }

    public List<StructSearchScripRow1> getSearchScripList(){
        List<StructSearchScripRow1> list = Arrays.asList(searchScripArray);
        return list;
    }

    public ArrayList<StructSearchScripRow1> getFinalScripList(){
        return finalList;
    }
    public int getFinalSize(){
        return finalList.size();
    }
}
