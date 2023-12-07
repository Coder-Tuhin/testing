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

public class PreserveYesFragment  extends Fragment  {
    private HomeActivity homeActivity;
    LinearLayout ll_conservativehybrid,ll_equitysavings;


    public static PreserveYesFragment newInstance(){
        return new PreserveYesFragment();
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
        View mView = inflater.inflate(R.layout.preserve_yes_screen,container,false);
        ll_conservativehybrid = mView.findViewById(R.id.ll_conservativehybrid);
        ll_equitysavings = mView.findViewById(R.id.ll_equitysavings);

        ll_conservativehybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.CONSERVATIVEHYBRID.name,"Conservative Hybrid Fund"), R.id.container_body, true);


            }
        });
        ll_equitysavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.EQUITYSAVINGS.name,"Equity Savings Fund"), R.id.container_body, true);

            }
        });
        return mView;
    }
}
