package wealth.new_mutualfund.newMF;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreThanThreeYearPreserve extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;

    @BindView(R.id.yesbutton)
    Button yesbutton;
    @BindView(R.id.nobutton)
    Button nobutton;

    public static MoreThanThreeYearPreserve newInstance(){
        return new MoreThanThreeYearPreserve();
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
        View mView = inflater.inflate(R.layout.morethanthreeyear_pc_screen,container,false);
        ButterKnife.bind(this,mView);
        yesbutton.setOnClickListener(this::onClick);
        nobutton.setOnClickListener(this::onClick);
        return mView;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.yesbutton:
                homeActivity.FragmentTransaction(PreserveYesFragment.newInstance(), R.id.container_body, true);

                break;
            case R.id.nobutton:
                homeActivity.FragmentTransaction(PreserveNoFragment.newInstance(), R.id.container_body, true);

                break;
        }
    }
}
