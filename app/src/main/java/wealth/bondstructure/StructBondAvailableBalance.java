package wealth.bondstructure;

/**
 * Created by Tapas on 11/1/13.
 */


import wealth.commonstructure.ModuleClass;

/**
 * @author Tapas Nayak
 */
public class StructBondAvailableBalance {
    //length 12
    private long longAvlBalance;    //1.---AvailableBalance---LONG---8bytes---

    public StructBondAvailableBalance(long longAvlBalance) {
        this.longAvlBalance = longAvlBalance;
    }
    public StructBondAvailableBalance(byte[] data, int dataLength) {

        try {
            int length = 2;
            int count = 0;

            byte[] msgLength = new byte[length];

            for (int i = 0; i < length; i++) {
                msgLength[i] = data[count];
                count++;
            }

            short intMsgLength = (short) ModuleClass.byteToshort(msgLength, length);

            byte[] msgCode = new byte[length];

            for (int i = 0; i < length; i++) {
                msgCode[i] = data[count];
                count++;
            }

            short intMsgCode = (short) ModuleClass.byteToshort(msgCode, length);

            length = 8;
            byte[] amount = new byte[length];

            for (int i = 0; i < length; i++) {
                amount[i] = data[count];
                count++;
            }
            longAvlBalance = ModuleClass.byteTolong(amount, length);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public double getAvailableBalance() {
        return (double) longAvlBalance / 100;
    }

    public void setAvailableBalance(long value) {
        longAvlBalance = value;
    }
}
