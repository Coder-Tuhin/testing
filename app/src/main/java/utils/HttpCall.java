package utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import enums.eSSOApi;
import enums.eSSOTag;

public class HttpCall {

    public HttpCallResp CallPostRequest(String url, JSONObject jsonBody){
        //String currenttime = DateUtil.dateFormatter(new Date(), eSSOTag.DATE_Patter.value);
        HttpCallResp apiResp = new HttpCallResp();
        HttpsURLConnection connection=null;
        try {
            final String mRequestBody = jsonBody.toString();

            StringBuffer strresponse = new StringBuffer();
            URL obj = new URL(url);
            connection = (HttpsURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setUseCaches(false);
            //60 sec timeout
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);

            connection.setRequestProperty(eSSOTag.ContentType.name, eSSOTag.ContentType.value);

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(mRequestBody);
                writer.flush();
                apiResp.responseCode = connection.getResponseCode();
                GlobalClass.log("Response Code : "+apiResp.responseCode);

                if (apiResp.responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        strresponse.append(inputLine);
                    }
                    in.close();
                    inputStream.close();
                    connection.disconnect();
                    GlobalClass.log(url+" :Param: "+jsonBody+ " \n:Response Data: " + strresponse.toString());
                    apiResp.responseData = strresponse.toString();
                } else {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    connection.disconnect();
                    GlobalClass.log(url+" :Param: "+jsonBody+ " :ErrResponse Data: " + errorresponse.toString());
                    apiResp.responseData = errorresponse.toString();
                }
            }
        }
        catch(Exception e){
            VenturaException.Print(e);
            GlobalClass.log(url+" :Param: "+jsonBody+ " :ErrResponse Data: " + e.toString());
        }
        finally{
            if(connection != null){
                connection.disconnect();
            }
        }
        return apiResp;
    }
}
