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

public class PreserveNoFragment extends Fragment {
    private HomeActivity homeActivity;
    LinearLayout ll_bankingpsu,ll_glitfund,ll_conservativedebt;

    public static PreserveNoFragment newInstance(){
        return new PreserveNoFragment();
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
        View mView = inflater.inflate(R.layout.preserve_no_screen,container,false);
        ll_conservativedebt = mView.findViewById(R.id.ll_conservativedebt);
        ll_glitfund = mView.findViewById(R.id.ll_glitfund);
        ll_bankingpsu = mView.findViewById(R.id.ll_bankingpsu);

        ll_conservativedebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.CORPORATEBOND.name,"Corporate Debt Fund"), R.id.container_body, true);

            }
        });
        ll_glitfund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.GLITFUND.name,"Glit Funds"), R.id.container_body, true);

            }
        });
        ll_bankingpsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.BANKINGANDPSU.name,getResources().getString(R.string.bankingadnpsu)), R.id.container_body, true);

            }
        });
        return mView;
    }
}