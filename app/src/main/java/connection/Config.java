package connection;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import utils.ObjectHolder;

public class Config {
    private String authIP = "";
    private String bCastServerIP = "";
    private String tradeServerIP = "";
    private String wealthServerIP = "";
    private int bcServerPort = 0;
    private int rcServerPort = 0;
    private int wealthServerPort = 0;
    private String searchEngineIP = "";
    private int searchEngineServerPort = 0;

    public Config(){
        try {
            authIP = VenturaApplication.getContext().getString(R.string.AUTH_IP);
        }catch (Exception ex){
            authIP = "180.179.130.232";
        }
        bCastServerIP = "";
        tradeServerIP = "";
        wealthServerIP = "";
        searchEngineIP = "";
        bcServerPort = 0;
        rcServerPort = 0;
        wealthServerPort = 0;
        searchEngineServerPort = 0;
    }
    public String getAuthIP() {
        return authIP;
    }
    public int getAuthServerPort() {
        try {
            return Integer.parseInt(VenturaApplication.getContext().getString(R.string.AUTH_PORT));
        }catch (Exception ex){
            return 46037;
        }
    }
    public void setAuthIP(String authIP) {
        this.authIP = authIP;
    }

    public String getbCastServerIP() {
        //return "127.0.0.1";
        return bCastServerIP;
    }

    public String getTradeServerIP() {
        return tradeServerIP;
    }

    public void setTradeServerIP(String tradeServerIP) {
        this.tradeServerIP = tradeServerIP;
    }

    public  void setbCastServerIP(String bCastServerIP) {
        this.bCastServerIP = bCastServerIP;
    }

    public String getWealthServerIP() {
        //return "127.0.0.1";
        return wealthServerIP;
    }

    public void setWealthServerIP(String wealthServerIP) {
        this.wealthServerIP = wealthServerIP;
    }

    public int getBcServerPort() {
        return bcServerPort;
    }

    public void setBcServerPort(int bcServerPort) {
        this.bcServerPort = bcServerPort;
    }

    public int getRcServerPort() {
        return rcServerPort;
    }

    public void setRcServerPort(int rcServerPort) {
        this.rcServerPort = rcServerPort;
    }

    public int getWealthServerPort() {
        return wealthServerPort;
    }

    public void setWealthServerPort(int wealthServerPort) {
        this.wealthServerPort = wealthServerPort;
    }

    public String getSearchEngineIP() {
        return searchEngineIP;//"114.143.213.76";
    }

    public void setSearchEngineIP(String searchEngineIP) {
        this.searchEngineIP = searchEngineIP;
    }

    public int getSearchEngineServerPort() {
        return searchEngineServerPort;//51531;
    }

    public void setSearchEngineServerPort(int searchEngineServerPort) {
        this.searchEngineServerPort = searchEngineServerPort;
    }

    public String getBCastLastDigit(){
        try {
            String[] ipDigits = getbCastServerIP().split("\\.");
            String lastDigit = "";
            if (ipDigits.length > 0) {
                if (getbCastServerIP().contains(".com")) {
                    lastDigit = ipDigits[0] + "";
                } else {
                    lastDigit = ipDigits[ipDigits.length - 1] + "";
                }
            }
            return lastDigit;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "NA";
    }
    public String getSearchEngineLastDigit(){
        try {
            String[] ipDigits = getSearchEngineIP().split("\\.");
            String lastDigit = "";
            if (ipDigits.length > 0) {
                if (getbCastServerIP().contains(".com")) {
                    lastDigit = ipDigits[0] + "";
                } else {
                    lastDigit = ipDigits[ipDigits.length - 1] + "";
                }
            }
            return lastDigit;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "NA";
    }
    public String getInteractiveLastDigit(){
        try {
            String[] ipDigits = getTradeServerIP().split("\\.");
            String lastDigit = "";
            if (ipDigits.length > 0) {
                if (getTradeServerIP().contains(".com")) {
                    lastDigit = ipDigits[0] + "";
                } else {
                    lastDigit = ipDigits[ipDigits.length - 1] + "";
                }
            }
            return lastDigit;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "NA";
    }
    public String getWealthLastDigit(){
        try {
            String[] wealthipDigits = getWealthServerIP().split("\\.");
            String lastDigitWealth = "";
            if (wealthipDigits.length > 1) {
                if (getWealthServerIP().contains(".com")) {
                    lastDigitWealth = wealthipDigits[0] + "";
                } else {
                    lastDigitWealth = wealthipDigits[wealthipDigits.length - 1] + "";
                }
            }
            return lastDigitWealth;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "NA";
    }
    public String getAuthLastDigit(){
        try {
            String[] wealthipDigits = getAuthIP().split("\\.");
            String lastDigitWealth = "";
            if (wealthipDigits.length > 1) {
                if (getAuthIP().contains(".com")) {
                    lastDigitWealth = wealthipDigits[0] + "";
                } else {
                    lastDigitWealth = wealthipDigits[wealthipDigits.length - 1] + "";
                }
            }
            return lastDigitWealth;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "NA";
    }
}
