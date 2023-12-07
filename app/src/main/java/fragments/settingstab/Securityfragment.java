package fragments.settingstab;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import butterknife.ButterKnife;
import butterknife.BindView;
import enums.eLogType;
import enums.ePrefTAG;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class Securityfragment extends Fragment implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.securitysetting_spinner)Spinner securitysetting_spinner;
    @BindView(R.id.securitysetting_textview)TextView securitysetting_textview;
    private ArrayAdapter<String> dataAdapter;

    public  Securityfragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.security_screen, container, false);
        ButterKnife.bind(this, layout);
        initialization();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.security_settings));
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
    }
    private void initialization() {
        securitysetting_spinner.setAdapter(dataAdapter);
        securitysetting_spinner.setOnItemSelectedListener(this);
        securitysetting_spinner.setSelection(dataAdapter.getPosition(
                VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.SECURITY.name,60+"")));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        //if (position <5){
            securitysetting_textview.setText("minutes");
        /*}else{
            securitysetting_textview.setText("hr(s)");
        }*/
       /* if (securitysetting_spinner.getSelectedItem().toString().equalsIgnoreCase(
                VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.SECURITY.name,15+""))){
            GlobalClass.showToast(getContext(),"Setting saved");
        }*/
        VenturaApplication.getPreference().storeSharedPref(ePrefTAG.SECURITY.name,
                securitysetting_spinner.getSelectedItem().toString());
//        GlobalClass.homeActivity.delayedIdle(Integer.parseInt(
//                adapterView.getItemAtPosition(position).toString()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
