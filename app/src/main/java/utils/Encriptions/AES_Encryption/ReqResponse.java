package utils.Encriptions.AES_Encryption;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import utils.GlobalClass;


/**
 *
 * @author XPC17
 */
public class ReqResponse {

    public AccordAPIResp getDataFromRestGET(String restURL, RequestHeader[] requestHeaders) {
        try {

            GlobalClass.log("FinalGetURL", "","getDataFromRestGET: "+restURL+","+requestHeaders.toString());

            /*
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(new FileInputStream("fileventura.crt"));
            Certificate ca = cf.generateCertificate(caInput);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            */

            URL url = new URL(restURL);

            StringBuffer sb = new StringBuffer();
            if (restURL.toLowerCase().contains("https")) {
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setSSLSocketFactory(context.getSocketFactory());
                setRequestHeaders(requestHeaders, conn);
                try {

                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                        StringBuffer errorresponse = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String inputLine;
                        while ((inputLine = reader.readLine()) != null) {
                            errorresponse.append(inputLine);
                        }
                        reader.close();
                        return new AccordAPIResp(AccordAPIResp.failure, "Failed : error code : "
                                + conn.getResponseCode(), "");
                    }

                }catch (IOException e ){
                    e.printStackTrace();
                }


                InputStream is = null;
                is = conn.getInputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                conn.disconnect();
            } else {
                /*
                CertificateFactory cf1 = CertificateFactory.getInstance("X.509");
                InputStream caInput1 = new BufferedInputStream(new FileInputStream("fileventura.crt"));
                Certificate ca1 = cf1.generateCertificate(caInput1);

                String keyStoreType1 = KeyStore.getDefaultType();
                KeyStore keyStore1 = KeyStore.getInstance(keyStoreType1);
                keyStore1.load(null, null);
                keyStore1.setCertificateEntry("ca", ca1);
                String tmfAlgorithm1= TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf1 = TrustManagerFactory.getInstance(tmfAlgorithm1); tmf1.init(keyStore1);
                SSLContext context1 = SSLContext.getInstance("TLS");
                context1.init(null, tmf.getTrustManagers(), null);*/

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                //conn.setRequestProperty("accept-encoding", "gzip, deflate");
                //conn.setRequestProperty("accept", "text/html, application/xhtml+xml, image/jxr, /");
                //conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                conn.setRequestProperty("Content-Type", "application/json");
                setRequestHeaders(requestHeaders, conn);
                BufferedReader reader = null;
                try {

                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        StringBuffer errorresponse = new StringBuffer();

                        reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String inputLine;
                        while ((inputLine = reader.readLine()) != null) {
                            errorresponse.append(inputLine);
                        }
                        reader.close();
                        return new AccordAPIResp(AccordAPIResp.failure, "Failed : HTTP error code : "
                                + conn.getResponseCode() , "");
                    }

                }catch (IOException e){
                    e.printStackTrace();


                }finally {
                    if (reader!= null){
                        reader.close();
                    }
                }

                InputStream is = null;
                is = conn.getInputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                conn.disconnect();
            }

            return new AccordAPIResp(AccordAPIResp.success, "", sb.toString());
        } catch (MalformedURLException e) {

            //return "";
            return new AccordAPIResp(AccordAPIResp.failure, e.getMessage() , "");

        } catch (IOException  e) {
            return new AccordAPIResp(AccordAPIResp.failure, e.getMessage() , "");
        }
    }

    public AccordAPIResp getDataFromRestPOST(String restURL, String parameters, RequestHeader[] requestHeaders) {
        try {

            URL url = new URL(restURL);
            StringBuffer sb = new StringBuffer();
            if (restURL.toLowerCase().contains("https")) {
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setSSLSocketFactory(getSocketFactory());
                conn.setDoOutput(true);
                setRequestHeaders(requestHeaders, conn);
                conn.getOutputStream().write(parameters.toString().getBytes("UTF-8"));

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    return new AccordAPIResp(AccordAPIResp.failure, "Failed :  error code : "
                            + conn.getResponseCode() , "");
                }

                InputStream is = null;
                is = conn.getInputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                conn.disconnect();

            } else {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                setRequestHeaders(requestHeaders, conn);
                conn.getOutputStream().write(parameters.getBytes("UTF-8"));

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    return new AccordAPIResp(AccordAPIResp.failure, "Failed :  error code : "
                            + conn.getResponseCode(), "");
                }

                InputStream is = null;
                is = conn.getInputStream();
                int ch;
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                conn.disconnect();
            }
            return new AccordAPIResp(AccordAPIResp.success, "", sb.toString());
        } catch (MalformedURLException e) {

            //return "";
            return new AccordAPIResp(AccordAPIResp.failure, e.getMessage() , "");

        } catch (IOException e) {

            //return "";
            return new AccordAPIResp(AccordAPIResp.failure, e.getMessage() , "");
        }

    }

    public static String decryptData(String encryptedData, byte[] key) {
        try {
            String result = "";
            byte[] resultByteArr = AES_Encryption.DecryptWithKey(encryptedData, key);
            result = AES_Encryption.convertToUTF8String(resultByteArr);
            return result;
        } catch (Exception e) {

            return "";
        }
    }

    public static String decryptKey(String encryptedData, byte[] key) {
        try {
            String result = "";
            byte[] resultByteArr = AES_Encryption.DecryptWithKey(encryptedData, key);
            result = AES_Encryption.convertToUTF8String(resultByteArr);
            return result;
        } catch (Exception e) {

            return "";
        }
    }
    //NEW
    public AccordAPIResp getRESTRequestParameters(String roId, String data, String ip, String url, byte[] authKey, String encrypKey, String purpose, String parameter, RequestHeader[] requestHeaders) {
        try {
            byte[] dataBytes = data.getBytes(AES_Encryption.CHARACTER_ENCODING);
            String encryptedData = AES_Encryption.EncryptWithKey(dataBytes, authKey);

            if (!data.equalsIgnoreCase("")) {
                url = url + "?" + parameter + "=" + encryptedData;
            }

            AccordAPIResp responseJsonData = getDataFromRestGET(url.trim(), requestHeaders);
            return responseJsonData;

        } catch (Exception e) {
            return new AccordAPIResp(AccordAPIResp.failure, e.getMessage() , "");
        }
    }

    public AccordAPIResp getRESTRequestParameters(String roId, String ip, String url, byte[] base64ToByteArr, String encrypKey, String purpose) {
        try {
            RequestHeader[] requestHeader = new RequestHeader[]{new RequestHeader(), new RequestHeader(), new RequestHeader()};
            requestHeader[0].setKey("appkey");
            requestHeader[0].setValue(encrypKey);
            requestHeader[1].setKey("employeecode");
            requestHeader[1].setValue(roId);
            requestHeader[2].setKey("ipaddress");
            requestHeader[2].setValue(ip);

            AccordAPIResp responseJsonData = getDataFromRestGET(url.trim(), requestHeader);
            return responseJsonData;

        } catch (Exception e) {

            return new AccordAPIResp(AccordAPIResp.failure, e.getMessage(), "");
        }
    }

    private void setRequestHeaders(RequestHeader[] requestHeaders, HttpURLConnection conn) {
        if (requestHeaders != null) {
            for (int i = 0; i < requestHeaders.length; i++) {
                if (!requestHeaders[i].getKey().isEmpty()) {
                    conn.setRequestProperty(requestHeaders[i].getKey(), requestHeaders[i].getValue());
                }
            }
        }
    }

    private void setRequestHeaders(RequestHeader[] requestHeaders, HttpsURLConnection conn) {
        if (requestHeaders != null) {
            for (int i = 0; i < requestHeaders.length; i++) {
                if (!requestHeaders[i].getKey().isEmpty()) {
                    conn.setRequestProperty(requestHeaders[i].getKey(), requestHeaders[i].getValue());
                }
            }
        }
    }
}