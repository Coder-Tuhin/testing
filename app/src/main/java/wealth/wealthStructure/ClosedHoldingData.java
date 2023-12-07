package wealth.wealthStructure;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import utils.GlobalClass;

public class ClosedHoldingData {

    private final String clientCode;
    public boolean isDataCame;

    public double totalPurValue, totalSellValue, totalGainLoss;
    public int totalQty;

    private final ArrayList<ClosedHoldingDetails> allData;
    public ClosedHoldingData(String _clCode){
        this.clientCode = _clCode;
        this.allData = new ArrayList<>();
    }

    public void setDataSummary(JSONArray respData) {
        try{
            totalPurValue = 0;
            totalSellValue = 0;
            totalGainLoss = 0;
            for(int i=0;i<respData.length();i++){
                JSONObject jsonObject = respData.getJSONObject(i);
                ClosedHoldingDetails details = new ClosedHoldingDetails();
                details.loadClosedHoldingSummary(jsonObject);
                if(details != null){
                    allData.add(details);
                    totalPurValue = totalPurValue + details.PurVal;
                    totalSellValue = totalSellValue + details.SellVal;
                    totalGainLoss = totalGainLoss + details.GainLoss;
                }
            }
            isDataCame = true;
        }catch (Exception ex){
            GlobalClass.onError("ClosedHoldingData : ",ex);
        }
    }
    public void setDataScripWise(JSONArray respData) {
        Log.d(TAG, "setDataScripWise: "+respData.toString());
        try{
            totalPurValue = 0;
            totalSellValue = 0;
            totalGainLoss = 0;
            totalQty = 0;
            for(int i=0;i<respData.length();i++){
                JSONObject jsonObject = respData.getJSONObject(i);
                ClosedHoldingDetails details = new ClosedHoldingDetails();
                details.loadClosedHoldingScripDetails(jsonObject);
                if(details != null){
                    allData.add(details);
                    totalPurValue = totalPurValue + details.PurVal;
                    totalSellValue = totalSellValue + details.SellVal;
                    totalGainLoss = totalGainLoss + details.GainLoss;
                    totalQty = totalQty + details.Qty;
                }
            }
            isDataCame = true;
        }catch (Exception ex){
            GlobalClass.onError("ClosedHoldingData : ",ex);
        }
    }

    public ArrayList<ClosedHoldingDetails> getAllData(){
        return allData;
    }
}
