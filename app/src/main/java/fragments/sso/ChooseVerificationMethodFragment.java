package fragments.sso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ventura.venturawealth.R;

import enums.eSSOJourney;
import utils.GlobalClass;

public class ChooseVerificationMethodFragment extends Fragment {
    private static String clientdata = "";
    private static eSSOJourney journey;
    private Button code_verify,authenticator_code,_register_google_auth;


    public static ChooseVerificationMethodFragment newInstance(String jsonObj, eSSOJourney _journey) {
        ChooseVerificationMethodFragment fragment = new ChooseVerificationMethodFragment();
        clientdata = jsonObj;
        journey = _journey;
        return fragment;
    }

    public static ChooseVerificationMethodFragment newInstance() {
        ChooseVerificationMethodFragment fragment = new ChooseVerificationMethodFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.choose_verification_method,container,false);
        GlobalClass.showregisterauthtag = false;
        code_verify = mView.findViewById(R.id.code_verify);
        authenticator_code = mView.findViewById(R.id.authenticator_code);
        _register_google_auth = mView.findViewById(R.id._register_google_auth);
        code_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment = OtpValidationFragmnet.newInstance(clientdata, journey);
                GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
            }
        });
        authenticator_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Fragment fragment = ValidateGoogleAuthFragment.newInstance(clientdata);
                GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
            }
        });
        _register_google_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalClass.showregisterauthtag = true;
                final Fragment fragment = OtpValidationFragmnet.newInstance(clientdata, journey);
                GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
            }
        });
        enableAuthbutton(GlobalClass.GoogleAuthEnabled);
        return mView;
    }

    private void enableAuthbutton(boolean btn){
        if(btn) {
            authenticator_code.setEnabled(btn);
            authenticator_code.setAlpha(btn ? 1 : 0.5f);
            _register_google_auth.setVisibility(View.GONE);
        }else {
            authenticator_code.setEnabled(btn);
            authenticator_code.setAlpha(btn ? 1 : 0.5f);
            _register_google_auth.setVisibility(View.VISIBLE);
        }
    }
}
