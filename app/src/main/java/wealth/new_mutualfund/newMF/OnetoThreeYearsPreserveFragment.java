package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;


import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import enums.InvestmentIncompanies;





public class OnetoThreeYearsPreserveFragment extends Fragment {
    private HomeActivity homeActivity;
    LinearLayout ll_moneymarket,ll_lowandshort;


    public static OnetoThreeYearsPreserveFragment newInstance(){
        return new OnetoThreeYearsPreserveFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.onetotwoyears_preserve,container,false);

        ll_lowandshort = mView.findViewById(R.id.ll_lowandshort);
        ll_moneymarket = mView.findViewById(R.id.ll_moneymarket);
        ll_lowandshort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.LOWANDSHORTDURATION.name,"Low and Short Duration Fund"), R.id.container_body, true);

            }
        });
        ll_moneymarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.MONEYMARKETFUND.name,"Money Market Fund"), R.id.container_body, true);

            }
        });

        ButterKnife.bind(this,mView);


        return mView;
    }
}