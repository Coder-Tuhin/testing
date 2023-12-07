package Structure.Response.BC;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 1/25/2017.
 */
public class StructScripRateAlert extends StructBase {


    public StructShort noOfRecords;
    public StructScripAlertRows[] structAlertData;

    public StructScripRateAlert(byte[] bytes) {
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = 2;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            data = new StructValueSetter(fields, ep.getExtractData());
            if(noOfRecords.getValue() > 0) {
                structAlertData = new StructScripAlertRows[noOfRecords.getValue()];
                int iSize = new StructScripAlertRows().data.sizeOf();
                for (int i = 0; i < noOfRecords.getValue(); i++) {
                    byte iBytes[] = new byte[iSize];
                    System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                    aCount += iSize;
                    structAlertData[i] = new StructScripAlertRows(iBytes);
                }
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }

    }


    public StructScripRateAlert()
    {
        init();
        data = new StructValueSetter(fields);
    }
    private void init()
    {
        className=getClass().getName();

        noOfRecords = new StructShort("NoOfRecs", 0);
        fields = new BaseStructure[]{
                noOfRecords
        };
    }

}
