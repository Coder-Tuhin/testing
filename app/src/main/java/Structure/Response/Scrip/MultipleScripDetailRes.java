package Structure.Response.Scrip;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 * Created by xtremsoft on 12/6/16.
 */
public class MultipleScripDetailRes extends StructBase {

    public StructShort noOfSrip;
    public ScripDetail scripDetails[];

    public MultipleScripDetailRes() {
        init();
        data = new StructValueSetter(fields);
    }

    public MultipleScripDetailRes(byte[] bytes) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = oPacketSize;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            data = new StructValueSetter(fields, ep.getExtractData());
            scripDetails = new ScripDetail[noOfSrip.getValue()];
            int iSize = new ScripDetail().data.sizeOf();
            int byteCountForSubStructure = 60;
            for (int i = 0; i < noOfSrip.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, byteCountForSubStructure);
                aCount += byteCountForSubStructure;
                scripDetails[i] = new ScripDetail(iBytes);
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in " + className, ex);
        }
    }

    private void init() {
        className = getClass().getName();
        noOfSrip = new StructShort("NoOfScrip", 0);
        fields = new BaseStructure[]{
                noOfSrip
        };
    }
}