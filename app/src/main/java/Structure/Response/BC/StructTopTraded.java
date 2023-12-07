package Structure.Response.BC;

/**
 * Created by xtremsoft on 8/29/16.
 */
import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 *
 * @author XtremsoftTechnologies
 */
public class StructTopTraded extends StructBase {
    private int rCount = 50;
    public StructInt segment;
    public StructTopTradedRows topTradedRow [];

    public StructTopTraded() {
        init();
        data=new StructValueSetter(fields);
    }
    public StructTopTraded(byte []bytes) {
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
            topTradedRow = new StructTopTradedRows[rCount];
            int iSize = new StructTopTradedRows().data.sizeOf();
            for (int i = 0; i < rCount; i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                topTradedRow[i] = new StructTopTradedRows(iBytes);
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className, ex);
        }
    }
    private void init()
    {
        className=getClass().getName();
        segment=new StructInt("Segment", 0);
        fields=new BaseStructure[]{
                segment
        };
    }
    public void setByteData() throws Exception {
        int aCont = 0;
        byte orgData[] = new byte[data.sizeOf() + (rCount * (new StructTopTradedRows()).data.sizeOf())];
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont += opData.length;
        for (StructTopTradedRows topTraded : topTradedRow) {
            byte ipData[] = topTraded.data.getByteArr();
            System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
            aCont += ipData.length;
        }
        this.orgData = orgData;
    }
}