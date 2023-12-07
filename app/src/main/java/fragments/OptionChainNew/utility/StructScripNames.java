package fragments.OptionChainNew.utility;

import Structure.BaseStructure.ExtractPacket;
import structure.BaseStructure;
import structure.StructBase;
import structure.StructByte;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

public class StructScripNames extends StructBase {

    public StructShort noOfRecs;
    public StructByte dc;
    public StructByte exch;
    public StructScripName scripName[];

    public StructScripNames() {
        init();
        data = new StructValueSetter(fields);
    }
    public void createInnerStruct(int noOfRec){
        noOfRecs.setValue(noOfRec);
        scripName = new StructScripName[noOfRec];
        for(int i=0; i < noOfRec; i++ ){
            scripName[i] = new StructScripName();
        }
    }

    public StructScripNames(byte bytes[]) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = 2;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            extract = 1;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            extract = 1;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            data = new StructValueSetter(fields, ep.getExtractData());
            scripName = new StructScripName[noOfRecs.getValue()];

            int iSize = new StructScripName().data.sizeOf();
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                scripName[i] = new StructScripName(iBytes);
            }
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className+" structure",ex);
        }
    }

    private void init() {
        className=getClass().getName();
        noOfRecs = new StructShort("NoOfRecs", 0);
        dc = new StructByte("", 0);
        exch = new StructByte("" , 0);
        fields = new BaseStructure[]{
                noOfRecs,dc,exch
        };
    }

    public void setByteData() throws Exception {
        try{
            int aCont = 0;
            byte orgData[] = new byte[data.sizeOf() + (noOfRecs.getValue() *
                    (new StructScripName()).data.sizeOf())];
            byte opData[] = data.getByteArr();
            System.arraycopy(opData, 0, orgData, aCont, opData.length);
            aCont += opData.length;
            for (StructScripName innerStruct : scripName) {
                byte ipData[] = innerStruct.data.getByteArr();
                System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
                aCont += ipData.length;
            }
            this.orgData = orgData;
        } catch (Exception ex) {
            GlobalClass.onError("Error in "+className+" structure",ex);
        }
    }
}
