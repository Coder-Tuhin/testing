package fragments.fd;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.upswing.UpswingInitActivity;
import com.ventura.venturawealth.WebViewFD;

import one.upswing.sdk.UpswingSdk;
import utils.GlobalClass;

public class FixedDipositeFragment extends Fragment {

    LinearLayout bankFd;
    LinearLayout corporateFd;

    public FixedDipositeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fixed_diposite, container, false);
        bankFd = view.findViewById(R.id.bankFd);
        corporateFd = view.findViewById(R.id.corporateFd);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            bankFd.setVisibility(View.GONE);
        }
        bankFd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    GlobalClass.isUPSwingPageOpen = true;
                    String partnerUID = "VNTR"; // provided by Upswing
                    UpswingSdk build = new UpswingSdk.Builder(requireContext(), partnerUID, new UpswingInitActivity())
                            .setStatusBarColor(getResources().getColor(R.color.colorPrimary))
                            .build();
                    build.initializeSdk();
                } else {
                    Toast.makeText(getContext(), "Device not supported !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        corporateFd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webview = new Intent(getActivity(), WebViewFD.class);
                //webview.putExtra("link", eSSOApi.NewUserUrl.value);
                requireActivity().startActivity(webview);
            }
        });

        return view;
    }
}