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
import android.widget.LinearLayout;

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

public class InvestInThreeToFiveYearsFragment extends Fragment {
    private HomeActivity homeActivity;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }


    public static InvestInThreeToFiveYearsFragment newInstance(){
        return new InvestInThreeToFiveYearsFragment();
    }
    @BindView(R.id.tv_100equity)
    ImageView tv_100equity;
    @BindView(R.id.tv_65equity)
    ImageView tv_65equity;
    @BindView(R.id.tv_3565equity)
    ImageView tv_3565equity;
    @BindView(R.id.ConservativePlay_btn)
    LinearLayout ConservativePlay_btn;
    @BindView(R.id.ModeratePlay_btn)
    LinearLayout ModeratePlay_btn;
    @BindView(R.id.Aggreshive_playbtn)
    LinearLayout Aggreshive_playbtn;

    private JSONObject VideoURLS;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.investin_three_to_five_years,container,false);
        ButterKnife.bind(this,mView);
        tv_65equity.setOnClickListener(_onclick);
        tv_100equity.setOnClickListener(_onclick);
        tv_3565equity.setOnClickListener(_onclick);
        new VideolinkRequest(eMessageCodeWealth.VIDEO_LINKS.value).execute();
        return mView;
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_65equity:
                    investInTop100Companies("65-80equity", InvestmentIncompanies.EQUITYEXP6580.name,"Invest In 65-80% Equity Exposure");
                    break;
                case R.id.tv_100equity:
                    investInTop100Companies("100equity",InvestmentIncompanies.EQUITY100.name,"Invest In 100% Equity Exposure");
                    break;
                case R.id.tv_3565equity:
                    investInTop100Companies("3565equity",InvestmentIncompanies.EQUITYEXP35.name,"Invest In 35-65% Equity Exposure");
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

    private void playbtnmethod(JSONObject videoURLS) {
        try {
            JSONArray jsonArray = videoURLS.getJSONArray("data");
            for(int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String OptionName = jsonObject.optString("OptionName");
                String Category = jsonObject.optString("Category");
                String SubCategory = jsonObject.optString("SubCategory");
                String VideoUrl = jsonObject.optString("VideoUrl");
                if(SubCategory.equalsIgnoreCase("Moderate")){
                    ModeratePlay_btn.setVisibility(View.VISIBLE);
                }
                if(SubCategory.equalsIgnoreCase("Aggressive")){
                    Aggreshive_playbtn.setVisibility(View.VISIBLE);
                }
                if(SubCategory.equalsIgnoreCase("Conservative")){
                    ConservativePlay_btn.setVisibility(View.VISIBLE);
                }
                ModeratePlay_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for(int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String OptionName = jsonObject.optString("OptionName");
                                String Category = jsonObject.optString("Category");
                                String SubCategory = jsonObject.optString("SubCategory");
                                String VideoUrl = jsonObject.optString("VideoUrl");
                                if(SubCategory.equalsIgnoreCase("Moderate")) {
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
                                if(SubCategory.equalsIgnoreCase("Aggressive")) {
                                    Uri webpage = Uri.parse(VideoUrl);
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(webIntent);
                                }
                            }

                        }catch (Exception e){

                        }

                    }
                });
                ConservativePlay_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for(int i = 0; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String OptionName = jsonObject.optString("OptionName");
                                String Category = jsonObject.optString("Category");
                                String SubCategory = jsonObject.optString("SubCategory");
                                String VideoUrl = jsonObject.optString("VideoUrl");
                                if(SubCategory.equalsIgnoreCase("Conservative")) {
                                    Uri webpage = Uri.parse(VideoUrl);
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(webIntent);
                                }
                            }

                        }catch (Exception e){

                        }
                    }
                });
               /* bluechip_playbtn.setOnClickListener(new View.OnClickListener() {
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
                });*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
}
