package fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eLogType;
import utils.GlobalClass;
import utils.UserSession;

public class ProfileFragment extends Fragment {


    private View view;
    @BindView(R.id.clCodeProfile)
    TextView clCode;
    @BindView(R.id.clNameProfile)
    TextView clName;

    public ProfileFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_screen, null);
        ButterKnife.bind(this, view);
        clCode.setText(UserSession.getLoginDetailsModel().getUserID());
        clName.setText(UserSession.getLoginDetailsModel().getClientName());
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return view;
    }
}
