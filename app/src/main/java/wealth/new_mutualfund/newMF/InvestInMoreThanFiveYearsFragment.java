package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.InvestmentIncompanies;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class InvestInMoreThanFiveYearsFragment extends Fragment {

    private HomeActivity homeActivity;

    public static InvestInMoreThanFiveYearsFragment newInstance(){
        return new InvestInMoreThanFiveYearsFragment();
    }
    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @BindView(R.id.tv_largecap)
    ImageView tv_largecap;
    @BindView(R.id.tv_midcap)
    ImageView tv_midcap;
    @BindView(R.id.tv_smallcap)
    ImageView tv_smallcap;
    @BindView(R.id.tv_flexicap)
    ImageView tv_flexicap;
    @BindView(R.id.bluechip_playbtn)
    ImageView bluechip_playbtn;
    @BindView(R.id.emerging_playbtn)
    ImageView emerging_playbtn;
    @BindView(R.id.Aggreshive_playbtn)
    ImageView Aggreshive_playbtn;
    @BindView(R.id.flexi__playbtn)
    ImageView flexi__playbtn;
    private JSONObject VideoURLS;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.investin_morethn_five_years,container,false);
        ButterKnife.bind(this,mView);
        tv_largecap.setOnClickListener(_onclick);
        tv_midcap.setOnClickListener(_onclick);
        tv_smallcap.setOnClickListener(_onclick);
        tv_flexicap.setOnClickListener(_onclick);
        new VideolinkRequest(eMessageCodeWealth.VIDEO_LINKS.value).execute();


        return mView;
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_largecap:
                    investInTop100Companies("top100", InvestmentIncompanies.LARGECAP.name,"Bluechip Stocks");
                    break;
                case R.id.tv_midcap:
                    investInTop100Companies("top250",InvestmentIncompanies.MIDCAP.name,"Emerging Bluechip");
                    break;
                case R.id.tv_smallcap:
                    investInTop100Companies("tv_top500",InvestmentIncompanies.SMALLCAP.name,"Aggressive Stocks");
                    break;
                case R.id.tv_flexicap:
                    investInTop100Companies("tv_top500",InvestmentIncompanies.FLEXICAP.name,"Diversified Stocks");
                    break;
            }

        }
    };

    private  void investInTop100Companies(String tag,String subcatystr,String header){
        homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance(tag,subcatystr,header), R.id.container_body, true);
    }

    class VideolinkRequest extends AsyncTask<String, Void, String> {
        int msgCode;

        public VideolinkRequest(int mC) {
            this.msgCode = mC;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject jdata = new JSONObject();
                jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.VIDEO_LINKS.value, jdata);
                if (jsonData != null) {
                    return jsonData.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if (s != null) {
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if (!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        displayError(err);
                    }

                    if (msgCode == eMessageCodeWealth.VIDEO_LINKS.value) {
                        VideoURLS = new JSONObject();
                        VideoURLS = jsonData;
                        playbtnmethod(VideoURLS);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private void playbtnmethod(JSONObject Videourls){
        try {
            JSONArray jsonArray = Videourls.getJSONArray("data");
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String OptionName = jsonObject.optString("OptionName");
                String Category = jsonObject.optString("Category");
                String SubCategory = jsonObject.optString("SubCategory");
                String VideoUrl = jsonObject.optString("VideoUrl");
                if(SubCategory.equalsIgnoreCase("Bluechip Stocks")) {
                    bluechip_playbtn.setVisibility(View.VISIBLE);
                }else if(SubCategory.equalsIgnoreCase("Emerging Bluechip")){
                    emerging_playbtn.setVisibility(View.VISIBLE);
                }else if(SubCategory.equalsIgnoreCase("Aggressive Stocks")){
                    Aggreshive_playbtn.setVisibility(View.VISIBLE);
                }else if(SubCategory.equalsIgnoreCase("Diversified Stocks")){
                    flexi__playbtn.setVisibility(View.VISIBLE);
                }
                bluechip_playbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for(int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String OptionName = jsonObject.optString("OptionName");
                                String Category = jsonObject.optString("Category");
                                String SubCategory = jsonObject.optString("SubCategory");
                                String VideoUrl = jsonObject.optString("VideoUrl");
                                if(SubCategory.equalsIgnoreCase("Bluechip Stocks")) {
                                    Uri webpage = Uri.parse(VideoUrl);
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(webIntent);
                                }
                            }

                        }catch (Exception e){

                        }

                    }
                });
                Aggreshive_playbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for(int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String OptionName = jsonObject.optString("OptionName");
                                String Category = jsonObject.optString("Category");
                                String SubCategory = jsonObject.optString("SubCategory");
                                String VideoUrl = jsonObject.optString("VideoUrl");
                                if(SubCategory.equalsIgnoreCase("Aggressive Stocks")) {
                                    Uri webpage = Uri.parse(VideoUrl);
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(webIntent);
                                }
                            }

                        }catch (Exception e){

                        }

                    }
                });
                flexi__playbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for(int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String OptionName = jsonObject.optString("OptionName");
                                String Category = jsonObject.optString("Category");
                                String SubCategory = jsonObject.optString("SubCategory");
                                String VideoUrl = jsonObject.optString("VideoUrl");
                                if(SubCategory.equalsIgnoreCase("Diversified Stocks")) {
                                    Uri webpage = Uri.parse(VideoUrl);
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(webIntent);
                                }
                            }

                        }catch (Exception e){

                        }

                    }
                });
                emerging_playbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for(int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String OptionName = jsonObject.optString("OptionName");
                                String Category = jsonObject.optString("Category");
                                String SubCategory = jsonObject.optString("SubCategory");
                                String VideoUrl = jsonObject.optString("VideoUrl");
                                if(SubCategory.equalsIgnoreCase("Emerging Stocks")) {
                                    Uri webpage = Uri.parse(VideoUrl);
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(webIntent);
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
