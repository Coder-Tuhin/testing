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

public class NewFragment extends Fragment {

    private HomeActivity homeActivity;
    public static NewFragment newInstance(){
        return new NewFragment();
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

    @BindView(R.id.ll_create_wealth)
    LinearLayout ll_create_wealth;
    @BindView(R.id.ll_preserve_capital)
    LinearLayout ll_preserve_capital;
    @BindView(R.id.ll_save_tax)
    LinearLayout ll_save_tax;
    @BindView(R.id.ll_park_fund)
    LinearLayout ll_park_fund;
    @BindView(R.id.ll_invest_overseas)
    LinearLayout ll_invest_overseas;
    @BindView(R.id.ll_focus_sector)
    LinearLayout ll_focus_sector;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.new_screen,container,false);
        ButterKnife.bind(this,mView);
        ll_create_wealth.setOnClickListener(onClick);
        ll_preserve_capital.setOnClickListener(onClick);
        ll_save_tax.setOnClickListener(onClick);
        ll_park_fund.setOnClickListener(onClick);
        ll_invest_overseas.setOnClickListener(onClick);
        ll_focus_sector.setOnClickListener(onClick);
        return mView;
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ll_create_wealth:
                    ll_create_wealth.setBackgroundResource(R.drawable.bg_white_1dp);
                    openCreateWealth();
                    break;
                case R.id.ll_preserve_capital:
                    ll_preserve_capital.setBackgroundResource(R.drawable.bg_white_1dp);
                    opengrowthmoney();
                    break;
                case R.id.ll_save_tax:
                    ll_save_tax.setBackgroundResource(R.drawable.bg_white_1dp);
                    openInvestBasketOne();
                    break;
                case R.id.ll_park_fund:
                    ll_park_fund.setBackgroundResource(R.drawable.bg_white_1dp);
                    openBluechipfund();
                    break;
                case R.id.ll_invest_overseas:
                    ll_invest_overseas.setBackgroundResource(R.drawable.bg_white_1dp);
                    openInvestInTop();
                    break;
                case R.id.ll_focus_sector:
                    ll_focus_sector.setBackgroundResource(R.drawable.bg_white_1dp);
                    openInvestBasketTwo();
                     break;
            }
        }
    };

    public void openCreateWealth() {
        homeActivity.FragmentTransaction(CreateWealthFragment.newInstance(), R.id.container_body, true);
    }
    public void openBluechipfund() {
        homeActivity.FragmentTransaction(ChooseSIPoptionFragment.newInstance(), R.id.container_body, true);
    }
    public void opengrowthmoney() {
        homeActivity.FragmentTransaction(GrowthYourMoneyFragment.newInstance(), R.id.container_body, true);
    }
    public void openInvestInTop() {
        homeActivity.FragmentTransaction(InvestInTopCompaniesFragmentNew.newInstance(), R.id.container_body, true);
    }
    public void openInvestBasketOne() {
     //   homeActivity.FragmentTransaction(InvestInBasketScreen_one.newInstance(), R.id.container_body, true);
    }
    public void openInvestBasketTwo() {
        homeActivity.FragmentTransaction(InvestInThreeToFiveYearsFragment.newInstance(), R.id.container_body, true);
    }


}
