package fragments.sso;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import enums.eSSOJourney;
import utils.GlobalClass;
import utils.PreferenceHandler;

public class AskGoogleAuthFragment  extends Fragment {
    TextView authenticator_text1,authenticator_text2,btn_enablenow,btn_enablelater,authenticator_text11;

    private static String clientdata = "";
    private static eSSOJourney journey = eSSOJourney.none;
    public static AskGoogleAuthFragment newInstance() {
        AskGoogleAuthFragment fragment = new AskGoogleAuthFragment();
        return fragment;
    }
    public static AskGoogleAuthFragment newInstance(String jsonObj, eSSOJourney _journey) {
        AskGoogleAuthFragment fragment = new AskGoogleAuthFragment();
        clientdata = jsonObj;
        journey = _journey;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppThemenew);
        View mView = inflater.inflate(R.layout.askggoogleauth,container,false);
        authenticator_text1 = mView.findViewById(R.id.authenticator_text1);
        authenticator_text2 = mView.findViewById(R.id.authenticator_text2);
        authenticator_text11 = mView.findViewById(R.id.authenticator_text11);

        btn_enablenow = mView.findViewById(R.id.btn_enablenow);
        btn_enablelater = mView.findViewById(R.id.btn_enablelater);
        btn_enablenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalClass.showregisterauthtag = true;
                final Fragment fragment;
                if(journey == eSSOJourney.none) {
                    fragment = Sso_validatePin.newInstance();
                }else{
                    fragment = OtpValidationFragmnet.newInstance(clientdata, journey);
                }
                GlobalClass.fragmentTransaction(fragment, R.id.layout, true, fragment.getClass().getSimpleName());
                //final Fragment fragment = CreateGoogleAuthFragment.newInstance();
                //GlobalClass.fragmentTransaction(fragment,R.id.layout,true,fragment.getClass().getSimpleName());
            }
        });
        authenticator_text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApp(getActivity(), "com.google.android.apps.authenticator2");

            }
        });
        authenticator_text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openApp(getActivity(), "com.google.android.apps.authenticator2");
                if(authenticator_text11.getVisibility() == View.GONE){
                    authenticator_text11.setVisibility(View.VISIBLE);
                }else {
                    authenticator_text11.setVisibility(View.GONE);
                }
            }
        });
        btn_enablelater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceHandler.setSSOCreateAuth("0");
                GlobalClass.showregisterauthtag = false;

                final Fragment fragment;
                if(journey == eSSOJourney.none) {
                    fragment = Sso_validatePin.newInstance();
                }else{
                    fragment = OtpValidationFragmnet.newInstance(clientdata, journey);
                }
                GlobalClass.fragmentTransaction(fragment, R.id.layout, true, fragment.getClass().getSimpleName());
            }
        });
        return mView;
    }
    public void openApp(Context context, String packageName) {

        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
            return;
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
    }
    public void OpenHomeActivity(){
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
