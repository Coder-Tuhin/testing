package wealth.mv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import utils.GlobalClass;

public class PortfolioDataCallingHandler {


    public JSONArray getClosedPortFolio(String _clientCode,String isin){

        _clientCode = "999S691";
        String _encClientCode = getEncryptedDataForSagarApi(_clientCode);
        JSONObject jobj = new JSONObject();
        String errorMsg = "";
        try {
            jobj.put("ClientCode", _encClientCode);
            jobj.put("ISIN", isin);

            String urlStr = "https://settlements.ventura1.com/api/MyWealth/GetClosedPortfolio";
            if(!isin.equalsIgnoreCase("")){
                urlStr = "https://settlements.ventura1.com/api/MyWealth/GetClosedPortfolioScripWise";
            }

            GlobalClass.log("getClosedPortFolio : " + jobj.toString());

            BufferedReader reader = null;
            StringBuilder sb;

            URL url1 = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) url1.openConnection();
            connection.setSSLSocketFactory(socketFactory());
            connection.setHostnameVerifier(new DummyHostnameVerifier());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            connection.setConnectTimeout(1 * 60 * 1000);
            connection.setReadTimeout(1 * 60 * 1000);

            connection.connect();
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jobj.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            int resCode = connection.getResponseCode();
            String resMsg = connection.getResponseMessage();
            System.out.println("response code : "+resCode+" response msg : "+resMsg);
            InputStream in;
            if (resCode != HttpsURLConnection.HTTP_OK) {
                in = connection.getErrorStream();
                reader = new BufferedReader(new InputStreamReader(in));
                sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                GlobalClass.onError("Error in shortlong api : " + sb.toString(), null);
            } else {
                in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String resultString = sb.toString();
                System.out.println("result : "+resultString);
                JSONArray jsonArr = new JSONArray(resultString);
                return jsonArr;
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            System.out.println("error msg : "+errorMsg);
            GlobalClass.onError("getLongShortData", e);
        }
        return null;
    }
    public class DummyTrustManager implements X509TrustManager {

        public DummyTrustManager() {
        }

        public boolean isClientTrusted(X509Certificate cert[]) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate cert[]) {
            return true;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

        }
    }

    public class DummyHostnameVerifier implements HostnameVerifier {

        public boolean verify(String urlHostname, String certHostname) {
            return true;
        }

        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    private SSLSocketFactory socketFactory() {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("SSL");

            sslcontext.init(new KeyManager[0],
                    new TrustManager[]{new DummyTrustManager()},
                    new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
            //out.write(logMsg);
            //out.close();
        } catch (KeyManagementException e) {
            e.printStackTrace(System.err);
            //out.write(logMsg);
            //out.close();
        }
        SSLSocketFactory factory = sslcontext.getSocketFactory();
        return factory;
    }

    public  String getEncryptedDataForSagarApi(String clCode){
        try{
            //String normalStr = "H011";
            String encryptionKey = "ARUTNEV2023";
            byte[] clearBytes = clCode.getBytes(StandardCharsets.UTF_16LE);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(encryptionKey.toCharArray(), new byte[] { 0x49, 0x76, 0x61, 0x6e, 0x20, 0x4d, 0x65, 0x64, 0x76, 0x65, 0x64, 0x65, 0x76 }, 1000, 384);
            Key secretKey = factory.generateSecret(pbeKeySpec);
            byte[] key = new byte[32];
            byte[] iv = new byte[16];
            System.arraycopy(secretKey.getEncoded(), 0, key, 0, 32);
            System.arraycopy(secretKey.getEncoded(), 32, iv, 0, 16);

            SecretKeySpec secret = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(clearBytes);

            String outPut = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                outPut = Base64.getEncoder().encodeToString(encryptedBytes);
            }else {
                outPut = new String(org.apache.mina.util.Base64.encodeBase64(encryptedBytes));
            }
            return outPut;
        }catch(Exception ex){
            GlobalClass.onError("getEncryptedDataForSagarApi", ex);
        }
        return clCode;
    }
}
