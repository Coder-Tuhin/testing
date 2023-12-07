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
import utils.StaticVariables;

public class CreateWealthFragment extends Fragment {
    public static CreateWealthFragment newInstance(String name) {
        CreateWealthFragment itc =  new CreateWealthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticVariables.ARG_1,name);
        itc.setArguments(bundle);
        return itc;
    }


    private static String tagstring;
    private HomeActivity homeActivity;

    public static CreateWealthFragment newInstance(){
        return new CreateWealthFragment();

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

    @BindView(R.id.ll_morethnfive)
    LinearLayout ll_morethnfive;
    @BindView(R.id.ll_threetofive)
    LinearLayout ll_threetofive;
    @BindView(R.id.lessthnfive)
    LinearLayout lessthnfive;
    @BindView(R.id.name)
    TextView name1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.createwealth_screen,container,false);
        ButterKnife.bind(this,mView);
        try {
            Bundle bundle = getArguments();
            String name = bundle.getString(StaticVariables.ARG_1);
            name1.setText(name);
        }catch (Exception e){
            e.printStackTrace();
        }

        ll_morethnfive.setOnClickListener(_onclick);
        ll_threetofive.setOnClickListener(_onclick);
        lessthnfive.setOnClickListener(_onclick);
        return mView;
    }

    private View.OnClickListener _onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ll_morethnfive:
                    openmoreThnFiveFragment();
                    break;
                case R.id.ll_threetofive:
                    openThreetoFiveFragment();
                    break;
                case R.id.lessthnfive:
                    openlessThnFiveFragment();
                    break;
            }

        }
    };

    private  void openmoreThnFiveFragment(){
        homeActivity.FragmentTransaction(InvestInMoreThanFiveYearsFragment.newInstance(), R.id.container_body, true);
    }
    private  void openThreetoFiveFragment(){
        homeActivity.FragmentTransaction(InvestInThreeToFiveYearsFragment.newInstance(), R.id.container_body, true);
    }
    private  void openlessThnFiveFragment(){
        homeActivity.FragmentTransaction(InvestInLessThanThreeYearsFragment.newInstance(), R.id.container_body, true);
    }
}
