package Structure.Response.BC;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by xtremsoft on 8/29/16.
 */
public class StructTopGainerLoser extends StructBase {

    private int rCount = 50;
    public StructInt segment;
    public StructTopGrainerLoserRows topGainerLoesr[];

    public StructTopGainerLoser() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructTopGainerLoser(byte[] bytes) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = 4;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            data = new StructValueSetter(fields, ep.getExtractData());
            topGainerLoesr = new StructTopGrainerLoserRows[rCount];
            int iSize = new StructTopGrainerLoserRows().data.sizeOf();
            for (int i = 0; i < rCount; i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                topGainerLoesr[i] = new StructTopGrainerLoserRows(iBytes);
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        segment = new StructInt("Segment", 0);
        fields = new BaseStructure[]{
                segment
        };

    }

    public void setByteData() throws Exception {
        int aCont = 0;
        byte orgData[] = new byte[data.sizeOf()
                + (rCount * (new StructTopGrainerLoserRows()).data.sizeOf())];
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont += opData.length;
        for (StructTopGrainerLoserRows topTraded : topGainerLoesr) {
            byte ipData[] = topTraded.data.getByteArr();
            System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
            aCont += ipData.length;
        }
        this.orgData = orgData;
    }
}