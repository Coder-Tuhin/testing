/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fragments.research;

import java.lang.reflect.Field;

import Structure.BaseStructure.ExtractPacket;
import Structure.BaseStructure.StructBase;
import structure.BaseStructure;
import structure.StructShort;
import structure.StructValueSetter;
import utils.VenturaException;

/**
 *
 * @author Raju
 */
public class StructResearchResp extends StructBase {
    
    public StructShort noOfRecs;    //1.---cNoOfRecs---SHORT---2bytes---
    public StructShort DC;    //1.---cNoOfRecs---SHORT---2bytes---
    public StructResearch[] researchDetail;

    public StructResearchResp() {
        init();
        data = new StructValueSetter(fields);
    }

    public StructResearchResp(byte bytes[]) {
        try {
            init();
            orgData = bytes;
            int aCount = 0;
            int oPacketSize = (new StructValueSetter(fields)).sizeOf();
            ExtractPacket ep = new ExtractPacket(oPacketSize);
            ep.ePacket(bytes, aCount, oPacketSize);
            aCount += oPacketSize;
            data = new StructValueSetter(fields, ep.getExtractData());
            researchDetail = new StructResearch[noOfRecs.getValue()];
            int iSize = new StructResearch().data.sizeOf();
            for (int i = 0; i < noOfRecs.getValue(); i++) {
                byte iBytes[] = new byte[iSize];
                System.arraycopy(bytes, aCount, iBytes, 0, iBytes.length);
                aCount += iSize;
                researchDetail[i] = new StructResearch(iBytes);
            }
        } catch (Exception ex) {
            VenturaException.Print(ex);
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
                (new StructResearch()).data.sizeOf())];
        byte opData[] = data.getByteArr();
        System.arraycopy(opData, 0, orgData, aCont, opData.length);
        aCont += opData.length;
        for (StructResearch gsd : researchDetail) {
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
            VenturaException.Print(e);
        }
        return sb.toString();
    }
}