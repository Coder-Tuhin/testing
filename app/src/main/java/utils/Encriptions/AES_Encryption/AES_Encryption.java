package utils.Encriptions.AES_Encryption;


//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import android.util.Base64;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Aes encryption javac AES.java ikvmc AES.class
 */
public class AES_Encryption {

    public static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String AES_ALGORITHM = "AES";
    public static final int ENC_BITS = 256;
    public static final String CHARACTER_ENCODING = "UTF-8";
    static final String SALT = "03042015A05";
    private static Cipher ENCRYPT_CIPHER;
    private static Cipher DECRYPT_CIPHER;
    private static KeyGenerator KEYGEN;
    static byte[] key;

    static {
        try {
            fixKeyLength();
            ENCRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
            DECRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION);
            KEYGEN = KeyGenerator.getInstance(AES_ALGORITHM);
            KEYGEN.init(ENC_BITS);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {

        }
    }
    public static void fixKeyLength() {
        String errorString = "Failed manually overriding key-length permissions.";
        int newMaxKeyLength;
        try {
            if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256) {
                Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = c.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);

                c = Class.forName("javax.crypto.CryptoPermissions");
                con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = c.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);

                c = Class.forName("javax.crypto.JceSecurityManager");
                f = c.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);

                newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
            }
        } catch (Exception e) {
            throw new RuntimeException(errorString, e);
        }
        if (newMaxKeyLength < 256) {
            throw new RuntimeException(errorString); // hack failed
        }
    }

    public static String EncryptWithKey(byte[] plainText, byte[] secret) {
        try {

            SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
            ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, sk);
            return Base64.encodeToString(ENCRYPT_CIPHER.doFinal(plainText), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] DecryptWithKey(String plainText, byte[] secret)
            throws IOException, IllegalBlockSizeException,
            BadPaddingException, Exception {
        SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
        DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, sk);
        byte[] output = Base64.decode(plainText, Base64.NO_WRAP);
        return DECRYPT_CIPHER.doFinal(output);
    }

    public static byte[] generateSecureKey() throws Exception {
        SecretKey secretKey = KEYGEN.generateKey();
        return secretKey.getEncoded();
    }

    public static String byteArrToBase64(byte[] byteArr) {
        return Base64.encodeToString(byteArr, Base64.NO_WRAP);
    }

    public static byte[] base64ToByteArr(String base46String) {
        try {
            //byte[] output = Base64.decode(base46String);
            byte[] output = Base64.decode(base46String, Base64.NO_WRAP);
            return output;
        } catch (Exception e) {

            return null;
        }
    }

    public static String convertToUTF8String(byte[] byteArr) {
        try {
            String s = new String(byteArr, CHARACTER_ENCODING);
            return s;
        } catch (Exception e) {
            return "";
        }
    }

    // START MODIFICATION - 14/08/2018
    public static String getBASE64Salt() {
        try {
            byte[] saltByteArr = SALT.getBytes(CHARACTER_ENCODING);
            return byteArrToBase64(saltByteArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    // END MODIFICATION
}