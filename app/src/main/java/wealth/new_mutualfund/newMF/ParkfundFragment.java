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

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.InvestmentIncompanies;

public class ParkfundFragment extends Fragment implements View.OnClickListener {
        private HomeActivity homeActivity;

        LinearLayout ll_1to15days;
        LinearLayout ll_15daysto3threemonths;
        LinearLayout morethan3months;

        public static ParkfundFragment newInstance() {
                return new ParkfundFragment();
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
                View mView = inflater.inflate(R.layout.parkfundscreen, container, false);
                ll_15daysto3threemonths = mView.findViewById(R.id.ll_15daysto3threemonths);
                ll_1to15days = mView.findViewById(R.id.ll_1to15days);
                morethan3months = mView.findViewById(R.id.morethan3months);
                ll_1to15days.setOnClickListener(this::onClick);
                morethan3months.setOnClickListener(this::onClick);
                ll_15daysto3threemonths.setOnClickListener(this::onClick);
                return mView;
        }

        @Override
        public void onClick(View view) {
                switch (view.getId()) {

                        case R.id.ll_1to15days:
                                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("parkfunds", InvestmentIncompanies.OVERNIGHT.name,"1-15 days"), R.id.container_body, true);

                                break;
                        case R.id.ll_15daysto3threemonths:
                                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("parkfunds", InvestmentIncompanies.LIQUID.name,"15 days - 3 months"), R.id.container_body, true);

                                break;
                        case R.id.morethan3months:
                                homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance("parkfunds", InvestmentIncompanies.ULTRASHORT.name,"More than 3 months"), R.id.container_body, true);

                                break;
                }

        }
}