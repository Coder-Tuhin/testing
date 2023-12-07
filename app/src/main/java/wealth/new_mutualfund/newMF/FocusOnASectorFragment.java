package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import enums.InvestmentIncompanies;

public class FocusOnASectorFragment extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;
    LinearLayout ll_pharma,ll_banking,ll_technology,ll_infrastructure,ll_consumer;

    public static FocusOnASectorFragment newInstance() {
        return new FocusOnASectorFragment();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
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
        View mView = inflater.inflate(R.layout.focus_on_sector_screen, container, false);
        ll_pharma = mView.findViewById(R.id.ll_pharma);
        ll_banking = mView.findViewById(R.id.ll_banking);
        ll_technology = mView.findViewById(R.id.ll_technology);
        ll_infrastructure = mView.findViewById(R.id.ll_infrastructure);
        ll_consumer = mView.findViewById(R.id.ll_consumer);

        ll_pharma.setOnClickListener(this::onClick);
        ll_banking.setOnClickListener(this::onClick);
        ll_technology.setOnClickListener(this::onClick);
        ll_infrastructure.setOnClickListener(this::onClick);
        ll_consumer.setOnClickListener(this::onClick);
        return mView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_pharma:
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.PHARMAANDHEALTHCARE.name,"Pharma and Healthcare"), R.id.container_body, true);

                break;
            case R.id.ll_banking:
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.BANKINGANDFINANCIAL.name,"Banking and Financial"), R.id.container_body, true);

                break;
            case R.id.ll_technology:
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.TECHNOLOGY.name,"Technology"), R.id.container_body, true);

                break;
            case R.id.ll_infrastructure:
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.INFRASTRUCTURE.name,"Infrastructure"), R.id.container_body, true);

                break;
            case R.id.ll_consumer:
                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("", InvestmentIncompanies.FMCG.name,"FMCG"), R.id.container_body, true);

                break;
        }

    }
}