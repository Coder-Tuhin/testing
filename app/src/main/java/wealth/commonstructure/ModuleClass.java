package wealth.commonstructure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.NumberFormat;

/**
 * Created by Admin on 22/05/14.
 */
public class ModuleClass {


    private static NumberFormat nf = null;

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public static byte[] intToByte(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static byte[] shortToByte(short value) {

        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }

    public static byte[] longToByte(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    }

    public static byte[] doubleToByte(double value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(value).array();
    }

    public static short byteToshort(byte[] data, int length) {
        return (short) ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static int byteToint(byte[] data, int length) {
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static long byteTolong(byte[] data, int length) {
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static double byteTodouble(byte[] data, int length) {
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }
    public static float byteTofloat(byte[] data, int length) {
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public static NumberFormat getDecimalFormat() {

        try {
            if (nf != null) {
                return nf;
            } else {
                nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(0);
                nf.setMinimumFractionDigits(0);
                nf.setGroupingUsed(true);
                return nf;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
