package connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.GlobalClass;
import utils.VenturaException;

public class HttpProcessNew extends AsyncTask<Void,Void,Void> {
    //private static final String BASE_URL = "http://membersim.bseindia.com/stocks/";
     private String URL = "";
     private HashMap<String, String> DATAMAP;
    private static HttpURLConnection con;
     private OnHttpResponse onHttpResponse;
    private RequestMethod METHOD;

    public HttpProcessNew(String _url, OnHttpResponse onResponse) {
        URL = _url;
        METHOD = RequestMethod.GET;
        this.onHttpResponse = onResponse;
    }

    public HttpProcessNew(String _url, RequestMethod _method,
                          HashMap<String, String> _reqDataMap, OnHttpResponse onResponse) {
        URL = _url;
        DATAMAP = _reqDataMap;
        METHOD = _method;
        this.onHttpResponse = onResponse;
    }

//    public abstract HashMap<String, String> getRequestDataMap();
//    public abstract  String getUrl();
//    public abstract  OnResponse getResponseListener();
//    public abstract  RequestMethod getRequestMethod();


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        GlobalClass.showProgressDialog("Requesting...");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL myurl = new URL(URL);
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod(METHOD.request);

            if (METHOD == RequestMethod.POST) {
                con.setRequestProperty("User-Agent", "Java client");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                byte[] _postData = getPostData(DATAMAP);
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(_postData);
                }
            }
            StringBuilder content;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            if (onHttpResponse != null) {
                onHttpResponse.onHttpResponse(content.toString());
            }

        } catch (Exception e) {
            VenturaException.Print(e);
        } finally {
            con.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public enum RequestMethod {
        GET("GET"),
        POST("POST");
        String request;
        RequestMethod(String request) {
            this.request = request;
        }
    }

    private byte[] getPostData(HashMap<String, String> _dataMap) {
        String _requestData = "";
        StringBuilder _requestbuilder = new StringBuilder();
        try {
            List<String> _keys = new ArrayList<>(_dataMap.keySet());
            for (String _key : _keys) {
                String _value = _dataMap.get(_key);
                _requestbuilder.append("&");
                _requestbuilder.append(_key + "=" + _value);
            }
            _requestData = _requestbuilder.toString();
            if (_requestData.startsWith("&")) {
                String _tempData = _requestData.replaceFirst("&","");
                _requestData = _tempData;
            }
            GlobalClass.log(URL+"       ","_POSTDATA: "+_requestData);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return _requestData.getBytes(StandardCharsets.UTF_8);
    }

    public interface OnHttpResponse {
        void onHttpResponse(String response);
    }
}
