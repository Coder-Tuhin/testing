package Structure.BaseStructure;

import java.io.Serializable;

import structure.BaseStructure;
import structure.StructValueSetter;
import structure.TypeConverter;

/**
 * Created by Admin on 08/02/2016.
 */
public class StructBase implements Serializable {
    public StructValueSetter data;
    public BaseStructure fields[];

    public  String className=getClass().getName();
    protected byte [] orgData;

    public byte[] getByteArrForInnerStruct(short iMsgCode) throws Exception{
        byte[] msgHdr = new byte[4];
        short msgLen = (short)(orgData.length+ 4);
        System.arraycopy(TypeConverter.shortToByte(msgLen), 0, msgHdr, 0, 2);
        System.arraycopy(TypeConverter.shortToByte(iMsgCode), 0, msgHdr, 2, 2);

        byte[] byteData = new byte[msgLen];
        System.arraycopy(msgHdr, 0, byteData, 0, msgHdr.length);
        System.arraycopy(orgData, 0, byteData, 4, msgLen-4);

        return byteData;
    }
    public byte[] getByteArrForInnerStruct() throws Exception{
        return orgData;
    }

}
