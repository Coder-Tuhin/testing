package Structure.Response.RC;

import java.lang.reflect.Field;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructByte;
import structure.StructString;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructOCOOrderBook extends StructBase {

    private int noOfRecs = 50;
    public StructString clientCode;
    public StructOCOOrdBkDet orderBkDetails[];
    public StructByte downloadComplete;
    public StructByte irecv;

    int iSize;

    public StructOCOOrderBook() {
        init();
        data = new StructValueSetter(fields);
    }
    public StructOCOOrderBook(byte bytes[]) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = 10;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;

            orderBkDetails = new StructOCOOrdBkDet[noOfRecs];
            iSize = new StructOCOOrdBkDet().data.sizeOf();//415+8
            for (int i = 0; i < noOfRecs; i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                orderBkDetails[i] = new StructOCOOrdBkDet(iBytes);
            }
            extract = 2;
            ep.ePacket(bytes, aCount, extract);
            data = new StructValueSetter(fields, ep.getExtractData());
            aCount += extract;
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }
    private void init() {
        className = getClass().getName();
        clientCode = new StructString("clientCode", 10,"");
        downloadComplete = new StructByte("dc", 0);
        irecv = new StructByte("irecv", 0);

        fields = new BaseStructure[]{
                clientCode,downloadComplete,irecv
        };
    }

    public void setByteData() throws Exception {
        int aCont = 0;
        byte orgData[] = new byte[data.sizeOf() + (new StructOCOOrdBkDet().data.sizeOf()*noOfRecs)];

        byte clientC[] = clientCode.toBytes();
        System.arraycopy(clientC, 0, orgData, aCont, clientC.length);
        aCont += clientC.length;

        for (StructOCOOrdBkDet gsd : orderBkDetails) {
            byte ipData[] = gsd.data.getByteArr();
            System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
            aCont += ipData.length;
        }

        orgData[aCont++] = downloadComplete.getValue();
        orgData[aCont++] = irecv.getValue();

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
