package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.InvestmentIncompanies;
import utils.GlobalClass;

public class InvestInLessThanThreeYearsFragment extends Fragment {
    private HomeActivity homeActivity;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity){
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }


    public static InvestInLessThanThreeYearsFragment newInstance(){
        return new InvestInLessThanThreeYearsFragment();
    }
    @BindView(R.id.tv_65equity)
    ImageView tv_65equity;
    @BindView(R.id.tv_35equity)
    ImageView tv_35equity;
    @BindView(R.id.tv_upto35equity)
    ImageView tv_upto35equity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.invest_in_less_thn_three_years,container,false);
        ButterKnife.bind(this,mView);
        tv_65equity.setOnClickListener(_onclick);
        tv_35equity.setOnClickListener(_onclick);
        tv_upto35equity.setOnClickListener(_onclick);
        return mView;
    }
    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_65equity:
                    investInTop100Companies("60-70", InvestmentIncompanies.EQUITYEXP65.name,"Invest in 60% - 70% Equity Exposure");
                    break;
                case R.id.tv_35equity:
                    investInTop100Companies("35-65",InvestmentIncompanies.EQUITYEXP35.name,"Invest in 35% - 65% Equity Exposure");
                    break;
                case R.id.tv_upto35equity:
                    investInTop100Companies("upto35",InvestmentIncompanies.EQUITYEXPUPTO35.name,"Invest in upto 35% Equity Exposure");
                    break;

            }

        }
    };

    private  void investInTop100Companies(String tag,String subcatystr,String header){
        GlobalClass.topcompanyTag = tag;
        GlobalClass.subcatystr = subcatystr;
        GlobalClass.header = header;

        homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance(tag,subcatystr,header), R.id.container_body, true);
    }
}