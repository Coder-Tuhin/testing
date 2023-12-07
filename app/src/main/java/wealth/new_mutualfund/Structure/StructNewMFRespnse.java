package wealth.new_mutualfund.Structure;

import wealth.commonstructure.ModuleClass;

public class StructNewMFRespnse {

    private int totalJsonL;    //1.---TransId---INT---4bytes---
    private int dc;    //2.---BondId---INT---4bytes---
    private char subjson[];    //3.---TransDate---CHAR---20bytes---

    public StructNewMFRespnse(byte[] data){

        try {
            int count = 4;
            int length = 4;
            byte[] transId = new byte[length];
            for (int i = 0; i < length; i++) {
                transId[i] = data[count];
                count++;
            }
            totalJsonL = ModuleClass.byteToint(transId, length);
            length = 4;
            byte[] bondId = new byte[length];
            for (int i = 0; i < length; i++) {
                bondId[i] = data[count];
                count++;
            }
            dc = ModuleClass.byteToint(bondId, length);
            length = data.length - count;
            subjson = new char[length];
            for (int i = 0; i < length; i++) {
                subjson[i] = (char) data[count];
                count++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getTotalJsonL() {
        return totalJsonL;
    }

    public int getDc() {
        return dc;
    }

    public String getSubjson() {
        return new String(subjson);
    }
}