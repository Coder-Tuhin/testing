package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.Bundle;
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

public class ChooseSIPOptionNewFragment extends Fragment {

    public static ChooseSIPOptionNewFragment newInstance() {
        ChooseSIPOptionNewFragment itc = new ChooseSIPOptionNewFragment();
        Bundle bundle = new Bundle();

        itc.setArguments(bundle);
        return itc;
    }

    private HomeActivity homeActivity;

    @Override
    public void onAttach(Context context) {
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        super.onAttach(context);
    }

    String schemecode;


    @BindView(R.id.tv_onetime)
    ImageView tv_onetime;
    @BindView(R.id.tv_monthly)
    ImageView tv_monthly;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.choose_sip_new, container, false);
        ButterKnife.bind(this, mView);
        Bundle bundle = getArguments();

        tv_onetime.setOnClickListener(_onclick);
        tv_monthly.setOnClickListener(_onclick);

        return mView;
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_onetime:
                    OpenOneTime();
                    break;
                case R.id.tv_monthly:
                    OpenMonthly();
                    break;

            }

        }
    };
    private void OpenOneTime() {
        homeActivity.FragmentTransaction(OneTimeFragmentNew.newInstance(), R.id.container_body, true);
    }

    private void OpenMonthly() {
        homeActivity.FragmentTransaction(SIPEnterAmoutFragmentNew.newInstance(), R.id.container_body, true);
    }
}