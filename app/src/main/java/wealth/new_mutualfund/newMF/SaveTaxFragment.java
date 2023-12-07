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

public class SaveTaxFragment extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;



    public static SaveTaxFragment newInstance() {
        return new SaveTaxFragment();
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
        View mView = inflater.inflate(R.layout.preservecapitalscreen, container, false);
        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


        }

    }
}
