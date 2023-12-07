package fragments.settingstab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.venturawealth.BuildConfig;
import com.ventura.venturawealth.R;

import enums.eLogType;
import enums.eServerType;
import fragments.BaseFragment;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class AboutFragment extends BaseFragment {

    private TextView version_textview;
    private ImageView xtremLogoImageView;

    public static AboutFragment newInstance(){
        return new AboutFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.about_screen;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        version_textview = view.findViewById(R.id.version_textview);
        xtremLogoImageView = view.findViewById(R.id.xtremlogo);
        initialization();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

    }

    private void initialization() {
        try {
            String ip = getResources().getString(R.string.LT);
            String rcITS = UserSession.getClientResponse().getServerType() == eServerType.ITS?"I":"R";
            ip = rcITS+ip;
            String lastDigitBCast = ObjectHolder.connconfig.getBCastLastDigit();
            String lastDigitInteractive = ObjectHolder.connconfig.getInteractiveLastDigit();
            String lastDigitWealth = ObjectHolder.connconfig.getWealthLastDigit();

            version_textview.setText(ip+""+ BuildConfig.VERSION_NAME+"."+
                    "B-"+lastDigitBCast+"."+
                    "I-"+lastDigitInteractive+"."+
                    "W-"+lastDigitWealth);
            xtremLogoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://xtremsoftindia.com"));
                        startActivity(browserIntent);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
