package wealth.new_mutualfund.newMF;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import fragments.BaseFragment;

public class GrowthYourMoneyFragment extends BaseFragment {
    private HomeActivity homeActivity;

    public static GrowthYourMoneyFragment newInstance(){
        return new GrowthYourMoneyFragment();
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.growthofmoneyscreen;
    }
}
