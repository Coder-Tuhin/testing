package Structure.Response.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import Structure.Request.RC.StructOrderRequest;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructOrderBookResponse extends StructBase {


    private int noOfRecs = 15;
    public StructOrderRequest orderRequest[];
    public StructShort downloadComplete;
    int iSize;

    public StructOrderBookResponse() {

        init();
        data = new StructValueSetter(fields);
    }

    public StructOrderBookResponse(byte bytes[]) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            iSize = new StructOrderRequest().data.sizeOf();//415+8
            for (int i = 0; i < noOfRecs; i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                orderRequest[i] = new StructOrderRequest(iBytes);
            }
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = 2;
            ep.ePacket(bytes, aCount, extract);
            data = new StructValueSetter(fields, ep.getExtractData());
            aCount += extract;
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        orderRequest = new StructOrderRequest[noOfRecs];
        downloadComplete = new StructShort("DownloadComplete", 0);
        fields = new BaseStructure[]{
                downloadComplete
        };
    }

    public void setByteData() throws Exception {
        int aCont = 0;
        byte orgData[] = new byte[data.sizeOf() + (noOfRecs * new StructOrderRequest().data.sizeOf())];

        for (StructOrderRequest gsd : orderRequest) {
            byte ipData[] = gsd.data.getByteArr();
            System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
            aCont += ipData.length;
        }
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont += opData.length;
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
}