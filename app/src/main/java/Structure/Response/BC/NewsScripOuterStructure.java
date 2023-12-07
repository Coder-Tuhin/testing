package Structure.Response.BC;

import java.lang.reflect.Field;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import fragments.research.StructResearch;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

public class NewsScripOuterStructure extends StructBase {

    public StructShort noOfRecs;    //1.---cNoOfRecs---SHORT---2bytes---
    private StructShort DC;    //1.---cNoOfRecs---SHORT---2bytes---
    public NewsScripResponseInnerStructure[] newsArray;

    public NewsScripOuterStructure() {
        init();
        data = new StructValueSetter(fields);
    }

    public NewsScripOuterStructure(byte bytes[]) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            newsArray = new NewsScripResponseInnerStructure[noOfRecs.getValue()];
            int iSize = new NewsScripResponseInnerStructure().data.sizeOf();
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                newsArray[i] = new NewsScripResponseInnerStructure(iBytes);
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className+" structure",ex);
        }
    }

    private void init() {
        className=getClass().getName();
        noOfRecs = new StructShort("NoOfRecs", 0);
        DC = new StructShort("DC", 0);
        fields = new BaseStructure[]{
                noOfRecs,DC
        };
    }

    public void setByteData() throws Exception {
        int aCont = 0;
        byte orgData[] = new byte[data.sizeOf() + (noOfRecs.getValue() *
                (new NewsScripResponseInnerStructure()).data.sizeOf())];
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont += opData.length;
        for (NewsScripResponseInnerStructure gsd : newsArray) {
            byte ipData[] = gsd.data.getByteArr();
            System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
            aCont += ipData.length;
        }
        this.orgData = orgData;
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

    public boolean isDownloadCompleted(){
        return DC.getValue()==1;
    }
}