package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.GlobalClass;
import utils.StaticVariables;

public class ChooseSIPoptionFragment extends Fragment {
    String SchemeName = "";

    public static ChooseSIPoptionFragment newInstance(String schemecode,String Schemename) {
        ChooseSIPoptionFragment itc =  new ChooseSIPoptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1,schemecode);
        bundle.putString(StaticVariables.ARG_2,Schemename);
        itc.setArguments(bundle);
        return itc;
    }
    private HomeActivity homeActivity;
    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }
    String schemecode;

    public static ChooseSIPoptionFragment newInstance(){
        return new ChooseSIPoptionFragment();
    }
    @BindView(R.id.tv_onetime)
    ImageView tv_onetime;
    @BindView(R.id.tv_monthly)
    ImageView tv_monthly;
    @BindView(R.id.SchemeNameTV)
    TextView SchemeNameTV;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.choose_sip_option,container,false);
        ButterKnife.bind(this,mView);
        Bundle bundle = getArguments();
        schemecode = bundle.getString(StaticVariables.ARG_1);
        SchemeName = bundle.getString(StaticVariables.ARG_2);
        SchemeNameTV.setText(SchemeName);
        tv_onetime.setOnClickListener(_onclick);
        tv_monthly.setOnClickListener(_onclick);

        return  mView;
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_onetime:
                    OpenOneTime();
                    break;
                case R.id.tv_monthly:
                    OpenMonthly();
                    break;

            }

        }
    };
    private void OpenOneTime(){
        if(SchemeName.contains("Mirae Emerging Bluechip") || SchemeName.contains("SBI Small cap")) {
            GlobalClass.showAlertDialog("AMC has restricted lumpsum (one time) investments in this scheme, we suggest you to start a SIP in this scheme.");

        }else {
            homeActivity.FragmentTransaction(OneTimeFragment.newInstance(schemecode, SchemeName, ""), R.id.container_body, true);


        }
    }
    private  void OpenMonthly(){
        homeActivity.FragmentTransaction(SIPEnterAmoutFragment.newInstance(schemecode,SchemeName), R.id.container_body, true);
    }
}

