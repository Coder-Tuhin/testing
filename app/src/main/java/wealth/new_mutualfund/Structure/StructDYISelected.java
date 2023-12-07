package wealth.new_mutualfund.Structure;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StructDYISelected {

    public String selectedAssetType;
    public String selectedGrowthDevedend;
    public ArrayList<String> selectedCategory;
    public ArrayList<String> selectedFund;
    public HashMap<String,String> selectedCategoryHashmap;
    public HashMap<String,String> selectedFundHashmap;


    public StructDYISelected(){
        selectedAssetType = "Equity";
        selectedGrowthDevedend = "Growth";
        selectedCategory = new ArrayList<>();
        selectedFund = new ArrayList<>();
        selectedCategoryHashmap = new HashMap<>();
        selectedFundHashmap = new HashMap<>();
    }

    public boolean addFund(JSONObject jsonD){
        try {
            String fundName = jsonD.getString("Text");
            String fundValue = jsonD.getString("Value");
            selectedFundHashmap.put(fundName, fundValue);
            if (!selectedFund.contains(fundName)) {
                if(selectedFund.size()<5) {
                    selectedFund.add(fundName);
                    return true;
                }
            }else {
                return true;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public void removeFund(JSONObject jsonD){
        try {
            String fundName = jsonD.getString("Text");
            //String fundValue = jsonD.getString("Value");
            if (selectedFund.contains(fundName)) {
                selectedFund.remove(fundName);
            }
            selectedFundHashmap.remove(fundName);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public boolean addCategory(JSONObject jsonD) {
        try {
            String categoryName = jsonD.getString("Text");
            String categoryValue = jsonD.getString("Value");
            selectedCategoryHashmap.put(categoryName, categoryValue);
            if (!selectedCategory.contains(categoryName)) {
                if(selectedCategory.size() <2) {
                    selectedCategory.add(categoryName);
                    return true;
                }
            }else {
                return true;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    public void removeCategory(JSONObject jsonD) {
        try {
            String categoryName = jsonD.getString("Text");
            //String categoryValue = jsonD.getString("Value");
            if (selectedCategory.contains(categoryName)) {
                selectedCategory.remove(categoryName);
            }
            selectedCategoryHashmap.remove(categoryName);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public boolean isFundCheck(String fundName){
        return selectedFund.contains(fundName);
    }
    public boolean isCategoryCheck(String categoryName){
        return selectedCategory.contains(categoryName);
    }

    public void clearFund(){
        selectedFund.clear();
        selectedFundHashmap.clear();
    }
    public void clearCategory(){
        selectedCategory.clear();
        selectedCategoryHashmap.clear();
    }

    public String getSelectedSubCategotyValue(){
        String category = "";
        for(int i=0;i<selectedCategory.size();i++){
            String cat = selectedCategory.get(i);
            category = category + (i>0?",":"") + selectedCategoryHashmap.get(cat);
        }
        return category;
    }
    public String getSelectedFundHouseValue(){
        String fund = "";
        for(int i=0;i<selectedFund.size();i++){
            String cat = selectedFund.get(i);
            fund = fund + (i>0?",":"") + selectedFundHashmap.get(cat);
        }
        return fund;
    }
}