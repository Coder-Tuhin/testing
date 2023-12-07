package wealth.new_mutualfund.menus;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpreadOrderFragment extends Fragment {
    private HomeActivity homeActivity;
    private View mView;
    @BindView(R.id.spreadSpinner)
    Spinner spreadSpinner;

    public static SpreadOrderFragment newInstance(){
        return new SpreadOrderFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.spread_order,container,false);
            ButterKnife.bind(this,mView);
            initSpinner();
            mView.findViewById(R.id.termcondclick).setOnClickListener(view ->
                    homeActivity.showMsgDialog("Terms & Conditions",R.string.mf_terms_conditions,false));
            mView.findViewById(R.id.cuttofclick).setOnClickListener(view -> homeActivity.showCutOffTimings());
        }
        return mView;
    }

    private void initSpinner() {
        String type[] = {"HDFC Equity Fund-Reg-Growth"};
        ArrayAdapter spinnerAdapter = new ArrayAdapter(homeActivity, R.layout.mf_spinner_item_orange);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_drop_down);
        spinnerAdapter.addAll(Arrays.asList(type));
        spreadSpinner.setAdapter(spinnerAdapter);
    }

}
