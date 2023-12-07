package wealth.wealthStructure;

import org.json.JSONObject;

public class ClosedHoldingDetails {

    public String Symbol;
    public String Isin;
    public String PurDate;
    public String SellDate;

    public int ScripCode;
    public int Qty;

    public double PurPrice;
    public double PurVal;
    public double SellPrice;
    public double SellVal;
    public double GainLoss;


    public void loadClosedHoldingSummary(JSONObject jsonObject) throws Exception{

        ScripCode = jsonObject.getInt("ScripCode");
        Symbol = jsonObject.getString("Symbol");
        Symbol = Symbol.trim();
        Isin = jsonObject.getString("Isin");

        Qty = (int) jsonObject.getDouble("Qty");
        PurPrice = jsonObject.getDouble("PurPrice");
        PurVal = jsonObject.getDouble("PurVal");
        SellPrice = jsonObject.getDouble("SellPrice");
        SellVal = jsonObject.getDouble("SellVal");
        GainLoss = jsonObject.getDouble("GainLoss");
        /*{
        "ScripCode": 500009,
            "Symbol": "AMBALALSA                                         ",
            "Isin": "INE432A01017",
            "Qty": 10000.0,
            "PurPrice": 0.0,
            "PurVal": 439523.4243,
            "SellPrice": 0.0,
            "SellVal": 314604.7132,
            "GainLoss": 124918.7111
    },*/
    }

    public void loadClosedHoldingScripDetails(JSONObject jsonObject) throws Exception{

        ScripCode = jsonObject.getInt("ScripCode");
        Symbol = jsonObject.getString("Symbol");
        Symbol = Symbol.trim();
        Isin = jsonObject.getString("Isin");
        PurDate = jsonObject.getString("PurDate");

        Qty = (int) jsonObject.getDouble("Qty");
        PurPrice = jsonObject.getDouble("PurPrice");
        PurVal = jsonObject.getDouble("PurVal");
        SellDate = jsonObject.getString("SellDate");

        SellPrice = jsonObject.getDouble("SellPrice");
        SellVal = jsonObject.getDouble("SellVal");
        GainLoss = jsonObject.getDouble("GainLoss");
        /*
            {
                "ScripCode": 7488,
                "Symbol": "GMBREW                                            ",
                "Isin": "INE075D01018",
                "PurDate": "05-Apr-2019",
                "Qty": 50.0,
                "PurPrice": 643.643,
                "PurVal": 32182.15,
                "SellDate": "25-Apr-2019",
                "SellPrice": 567.2822,
                "SellVal": 28364.11,
                "GainLoss": -3818.04
            }*/
    }

    public String getCompanyName() {
        return Symbol;
    }

    public int getScripCodeForRateUpdate() {
        return ScripCode;
    }

    public String getISINNo() {
        return Isin;
    }
}
