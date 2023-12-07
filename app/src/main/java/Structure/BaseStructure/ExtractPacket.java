package Structure.BaseStructure;

/**
 * Created by Admin on 08/02/2016.
 */
public class ExtractPacket {
    private byte eData[];
    private int count;
    public ExtractPacket(int packetSize) {
        eData=new byte[packetSize];
    }

    public void ePacket( byte srcData[],int srcPos,int nBytes2Copy)throws Exception
    {
        System.arraycopy(srcData, srcPos, eData, count,nBytes2Copy);
        count += nBytes2Copy;
    }
    public byte[] getExtractData()
    {
        return eData;
    }
}
