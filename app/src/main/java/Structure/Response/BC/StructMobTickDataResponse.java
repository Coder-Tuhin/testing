package Structure.Response.BC;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructInt;
import structure.StructShort;
import structure.StructValueSetter;
import utils.GlobalClass;

/**
 *
 * @author xtremsoft
 */
public class StructMobTickDataResponse extends StructBase {
    public StructInt token;
    public StructShort downloadComplete;
    public StructShort noOfRecords;
    public StructMobTick_Data[] structTickData;

    public StructMobTickDataResponse(byte[] bytes,boolean isNew) {
        orgData = bytes;
        try {
            init();
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            int extract = 4;
            ep.ePacket(bytes, aCount, extract);
            aCount += extract;
            extract = 2;
            ep.ePacket(bytes, aCount, extract);
            aCount+=extract;
            extract = 2;
            ep.ePacket(bytes, aCount, extract);
            aCount+=extract;
            data = new StructValueSetter(fields, ep.getExtractData());
            if(noOfRecords.getValue() > 0) {
                structTickData = new StructMobTick_Data[noOfRecords.getValue()];
                int iSize = new StructMobTick_Data(isNew).data.sizeOf();
                for (int i = 0; i < noOfRecords.getValue(); i++) {
                    byte iBytes[] = new byte[iSize];
                    System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                    aCount += iSize;
                    structTickData[i] = new StructMobTick_Data(iBytes,isNew);
                }
            }
            else{
                structTickData = new StructMobTick_Data[0];
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in structure: " + className, e);
        }

    }


    public void setStructTickData(StructMobTick_Data[] structTickData) {
        this.structTickData = structTickData;
    }

    public StructMobTickDataResponse()
    {
        init();
        data = new StructValueSetter(fields);
    }
    private void init()
    {
        className=getClass().getName();
        token = new StructInt("Token", 0);
        downloadComplete = new StructShort("DownloadComplete", 0);
        noOfRecords = new StructShort("NoOfRecs", 0);
        fields = new BaseStructure[]{
                token,downloadComplete,noOfRecords
        };
    }
    /*

    public void setByteData() throws Exception
    {
        int aCont=0;
        byte orgData[]=new byte[data.sizeOf()+(noOfRecords.getValue()*(new StructMobTick_Data()).data.sizeOf())];
        byte opData[]=data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont+=opData.length;
        for(StructMobTick_Data tickData:structTickData)
        {
            byte ipData[]=tickData.data.getByteArr();
            System.arraycopy(ipData, 0, orgData, aCont, ipData.length);
            aCont+=ipData.length;
        }
        this.orgData = orgData;
    }*/

}
