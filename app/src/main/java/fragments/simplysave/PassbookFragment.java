package fragments.simplysave;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Structure.simplysave.SimplysaveResp;
import Structure.simplysave.passbookReq;
import Structure.simplysave.passbookrefdetail;
import butterknife.ButterKnife;
import butterknife.BindView;
import connection.SendDataToSimplysaveServer;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 05-Jun-2017.
 */
public class PassbookFragment extends Fragment {
    private final String TAG = PassbookFragment.class.getSimpleName();
    @BindView(R.id.passbookRoot)LinearLayout passbookRoot;
    String dd;

    public PassbookFragment(){super();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.passbook_screen,null);
        ButterKnife.bind(this, view);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.passbookHandler = new PassbookHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.passbookHandler = null;
    }

    private void requestPassbook(){
        if (!TextUtils.isEmpty(UserSession.getLoginDetailsModel().getFolioNumber())){
            getPassbookDetails();
        }
    }


    private void getPassbookDetails(){
        try {
            GlobalClass.showProgressDialog("Please wait...");
            passbookReq passbookReq = new passbookReq();
            passbookReq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            passbookReq.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.sendPassbookDetail(passbookReq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getDetails(String refNo){
        try {
            GlobalClass.showProgressDialog("Please wait...");
            passbookrefdetail detailsreq = new passbookrefdetail();
            detailsreq.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            detailsreq.follio.setValue(UserSession.getLoginDetailsModel().getFolioNumber());
            PackageInfo pInfo = null;
            try {

                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            detailsreq.appVersion.setValue(pInfo.versionName);
            detailsreq.imei.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            detailsreq.refno.setValue(refNo);
            SendDataToSimplysaveServer sendDataToSimplysaveServer = new SendDataToSimplysaveServer();
            sendDataToSimplysaveServer.sendPassbookRefDetail(detailsreq);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class PassbookHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case PASSBOOK:
                                handleResponse(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            case PASSBOOK_REF:
                                handleResponseForREFDetail(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                break;
                            case SUMMARY_PASSBOOK:
                                requestPassbook();
                                break;
                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void handleResponseForREFDetail(byte[] byteArray) {
        GlobalClass.dismissdialog();
        SimplysaveResp ssr = new SimplysaveResp(byteArray);
        String response = ssr.response.getValue();

        try {
            JSONObject jsonob = new JSONObject(response);
            String instaStatus = jsonob.optString("InstaStatus");
            String msg = "";
            if(instaStatus.equalsIgnoreCase("failed")){
                /*msg = "Your redemption request received on " +dd+
                        " in Reliance Liquid Fund under Folio No "+ UserSession.getLoginDetailsModel().getFolioNumber()+" for Rs." +amt+
                        "/- has been rejected by your bank due to‘IMPS Failure’. " +
                        "It will now be processed as normal Redemption and the funds will be " +
                        "credited to your registered bank account within 1 working day.";*/
                msg = "Your Redemption request received on "+dd+
                        " in Nippon India Liquid Fund under Folio No "+ UserSession.getLoginDetailsModel().getFolioNumber()+" is under process."+
                        " Redemption amount will be directly credited to your bank account within 1 working day.";
            }else if(instaStatus.equalsIgnoreCase("in process")){
                msg = "Your Redemption request received on "+dd+
                        " in Nippon India Liquid Fund under Folio No "+UserSession.getLoginDetailsModel().getFolioNumber()+" is under process. "+
                        "Redemption amount will be directly credited to your bank account within the next 30 minutes";
            }else if(instaStatus.equalsIgnoreCase("success")){
                msg = "Your Redemption request received on "+dd+ "" +
                        " in Nippon India Liquid Fund under Folio No "+UserSession.getLoginDetailsModel().getFolioNumber()+" is processed successfully";
            }
            if(!msg.equalsIgnoreCase("")) {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        getActivity()).create();

                // Setting Dialog Title
                alertDialog.setTitle("");
                // Setting Dialog Message
                alertDialog.setMessage(msg);
                // Setting Icon to Dialog
                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void handleResponse(byte[] byteArray) {
        GlobalClass.dismissdialog();
        SimplysaveResp ssr = new SimplysaveResp(byteArray);
        String response = ssr.response.getValue();
        try {
            JSONArray jsonArray = new JSONArray(response);
            passbookRoot.removeAllViews();
            for (int i= 0;i< jsonArray.length();i++){
                JSONObject jsonObj = jsonArray.optJSONObject(i);
                adViewtoRoot(jsonObj.optString("Status"),jsonObj.optString("Transaction_Date"),jsonObj.optString("Transaction_type"),
                        jsonObj.optString("Amount"),jsonObj.optString("IHno"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void adViewtoRoot(String status, String date, String type, String amount,String txnNo ) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.passbook_item,null);
        int color = 0;

        if (type.equalsIgnoreCase("Withdrawal") ){

            ((TextView) view.findViewById(R.id.shortName)).setBackground(getResources().getDrawable(R.drawable.circle_red));
            ((TextView) view.findViewById(R.id.shortName)).setText("W");
            color = getResources().getColor(R.color.circle_red);

            if(!status.equalsIgnoreCase("success")) {
                ((ImageView) view.findViewById(R.id.infovalbtn)).setVisibility(View.VISIBLE);
                ImageView i = (ImageView) view.findViewById(R.id.infovalbtn);
                i.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dd = date;
                        getDetails(txnNo);
                    }
                });
            }
            else{
                ((ImageView) view.findViewById(R.id.infovalbtn)).setVisibility(View.INVISIBLE);
            }

        }else if (type.equalsIgnoreCase("Purchase")){
            ((ImageView) view.findViewById(R.id.infovalbtn)).setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.shortName)).setBackground(getResources().getDrawable(R.drawable.circle_green));
            ((TextView) view.findViewById(R.id.shortName)).setText("P");
            color = getResources().getColor(R.color.circle_green);
        }else if (type.equalsIgnoreCase("Switch In")){
            ((ImageView) view.findViewById(R.id.infovalbtn)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.shortName)).setBackground(getResources().getDrawable(R.drawable.circle_purple));
            ((TextView) view.findViewById(R.id.shortName)).setText("SW");
            color = getResources().getColor(R.color.circle_purple);
        }else {
            ((ImageView) view.findViewById(R.id.infovalbtn)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.shortName)).setBackground(getResources().getDrawable(R.drawable.circle_default));
            ((TextView) view.findViewById(R.id.shortName)).setText(type.charAt(0));
            color = getResources().getColor(R.color.circle_default);
        }



        ((TextView) view.findViewById(R.id.status)).setTextColor(color);
        ((TextView) view.findViewById(R.id.status)).setText(status);

        ((TextView) view.findViewById(R.id.date)).setText(DateUtil.ddMMMyyyyFormat(date));
        ((TextView) view.findViewById(R.id.type)).setText(type);
        ((TextView) view.findViewById(R.id.amount)).setText(Formatter.toTwoDecimalValue(Double.parseDouble(amount)));
        passbookRoot.addView(view);
    }
}