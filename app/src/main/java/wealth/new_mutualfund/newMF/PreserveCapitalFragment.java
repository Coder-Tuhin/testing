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

public class PreserveCapitalFragment extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;

    @BindView(R.id.ll_morethanthreeyear)
    LinearLayout ll_morethanthreeyear;
    @BindView(R.id.ll_onetothreeyears)
    LinearLayout ll_onetothreeyears;

    public static PreserveCapitalFragment newInstance(){
        return new PreserveCapitalFragment();
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
        View mView = inflater.inflate(R.layout.preservecapitalscreen,container,false);
        ButterKnife.bind(this,mView);
        ll_morethanthreeyear = mView.findViewById(R.id.ll_morethanthreeyear);
        ll_onetothreeyears = mView.findViewById(R.id.ll_onetothreeyears);
        ll_morethanthreeyear.setOnClickListener(this);
        ll_onetothreeyears.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ll_morethanthreeyear:
                homeActivity.FragmentTransaction(MoreThanThreeYearPreserve.newInstance(), R.id.container_body, true);

                break;
            case R.id.ll_onetothreeyears:
                homeActivity.FragmentTransaction(OnetoThreeYearsPreserveFragment.newInstance(), R.id.container_body, true);

                break;
        }

    }
}
