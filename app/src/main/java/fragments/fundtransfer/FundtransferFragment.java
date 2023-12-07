package fragments.fundtransfer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.BaseActivity;
import com.ventura.venturawealth.activities.homescreen.NavigationMenuHandler;

import java.util.ArrayList;

import Structure.Response.AuthRelated.ClientLoginResponse;
import enums.eLogType;
import utils.Constants;
import utils.GlobalClass;
import utils.StaticVariables;
import utils.UserSession;
import utils.VenturaException;
import wealth.Dialogs;
import wealth.VenturaServerConnect;


/**
 * Created by Administrator on 12/18/13.
 */
public class FundtransferFragment extends Fragment implements View.OnClickListener{

    Typeface verdanaType, verdanaTypeBold;
    private View layout;
    private Spinner groupSpinner;

    private int selectedPosition = 0;
    private ArrayList<String> _menus = new ArrayList<>();

    public static FundtransferFragment newInstance(int _selectedPosition){
        FundtransferFragment ftf = new FundtransferFragment();
        try {
            Bundle args = new Bundle();
            args.putInt(StaticVariables.ARG_1, _selectedPosition);
            ftf.setArguments(args);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return ftf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args  = getArguments();
        try {
            assert args!=null;
            selectedPosition = args.getInt(StaticVariables.ARG_1,0);
            for (NavigationMenuHandler.NavChildMenus ncm : NavigationMenuHandler.getFundTransferMenus()){
                _menus.add(ncm._title);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            layout = inflater.inflate(R.layout.fundtransfer, container, false);
            new GetTaskFirst().execute();
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }


    private void ViewInitialization() {
        BaseActivity _baseActivity = (BaseActivity) GlobalClass.homeActivity;
        _baseActivity.runOnUiThread(() -> {
            try {
                verdanaType = Typeface.DEFAULT;//Typeface.createFromAsset(getAssets(), ScreenColor.tableFontPath);
                verdanaTypeBold = Typeface.DEFAULT_BOLD;//Typeface.createFromAsset(getAssets(), ScreenColor.tableFontPath);
                groupSpinner = layout.findViewById(R.id.spn);
                ArrayAdapter<String> _adapter = new ArrayAdapter<>
                        (_baseActivity.getLetestContext(), R.layout.custom_spinner_item);
                _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                _adapter.addAll(_menus);
                groupSpinner.setAdapter(_adapter);
                groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        String selectedItem = adapterView.getSelectedItem().toString();
                        switch (NavigationMenuHandler.NavChildMenus.getNavChild(selectedItem)){
                            case UPI: {
                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                ft.replace(R.id.fundTranferBody, UpitransferFragment.newInstance());
                                ft.commit();
                            }
                                break;
                            /*case PAY_IN: {
                                String url = "https://secure.ventura1.com/Mobintermediate.aspx?authid="
                                        + UserSession.getClientResponse().charAuthId.getValue();
                                openURL(url);
                            }
                                break;*/
                            default: {
                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                ft.replace(R.id.fundTranferBody, TransferProcessFragment.newInstance(position));
                                ft.commit();
                            }
                                break;
                        }
                        selectedPosition = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
                setSpinnerSelection(selectedPosition);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        });
      }
    private void openURL(String url){
        try {
            GlobalClass.log("FinalURL : "+ url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            GlobalClass.latestContext.startActivity(browserIntent);
        }catch (Exception ex){
            GlobalClass.onError("",ex);
        }
    }

    public void setSpinnerSelection(int position){
        if (groupSpinner!= null){
            groupSpinner.setSelection(position);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
        }
    }

    class GetTaskFirst extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if(UserSession.getClientResponse().isNeedAccordLogin() || !VenturaServerConnect.accordSessionCheck("FundTransfer")){
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    return clientLoginResponse.charResMsg.getValue();
                }
            } catch (Exception ie) {
                VenturaException.Print(ie);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mDialog.dismiss();
                if(result.equalsIgnoreCase("")) {
                    ViewInitialization();
                }else{
                    if(result.toLowerCase().contains(Constants.WEALTH_ERR)){
                        GlobalClass.homeActivity.logoutAlert("Logout",Constants.LOGOUT_FOR_WEALTH,false);
                    }else {
                        GlobalClass.showAlertDialog(result);
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
}