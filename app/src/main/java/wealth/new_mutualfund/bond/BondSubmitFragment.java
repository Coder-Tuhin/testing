package wealth.new_mutualfund.bond;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Structure.Response.AuthRelated.ClientLoginResponse;
import enums.eFragments;
import enums.eMessageCodeWealth;
import fragments.homeGroups.WatchFragment;
import utils.Constants;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.ipo.AddUpiFragment;

public class BondSubmitFragment extends Fragment  {
    private HomeActivity homeActivity;
    private View mView;
    private TextView panValue,clientcode,dptextvalue,beni_id,tenor,interestfreq,interestrate,totalamount,termcondclick;
    private EditText noofBondEditText;
    private ImageView upiimage;
    private Button button_apply;
    private Spinner mfSpinnerfamily;
    private String BeneficiaryId;
    private String DPId;
    private String ClientCode;
    private String PanNumber;
    private String ExChange;
    JSONObject upijsonData;

    public static String BondName = "",bondID = "0",appnofrom = "",GrandTotal = "0",
            appnoto = "",tenors = "",interestfreqs = "",interestrates = "",noofbonds = "",totalamounts = "",BondQty = "",OptID = "";

    public static BondSubmitFragment newInstance(String bondName,String bondID,String appnofrom,String appnoto,
                                                 String tenor,String interestfreq,String interestrate,String noofbonds
            ,String totalamount,String optId,String Exchange ) {
        BondSubmitFragment bd = new BondSubmitFragment();
        bd.BondName = bondName;
        bd.bondID = bondID;
        bd.appnofrom = appnofrom;
        bd.appnoto = appnoto;
        bd.tenors = tenor;
        bd.interestfreqs = interestfreq;
        bd.interestrates = interestrate;
        bd.noofbonds = noofbonds;
        bd.totalamounts = totalamount;
        bd.OptID = optId;
        bd.ExChange = Exchange;
        return bd;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new IPODetailsReq(eMessageCodeWealth.BONDUPI_DETAILS.value).execute();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null) {
            mView = inflater.inflate(R.layout.bondsubmit_screen, container, false);
            panValue = mView.findViewById(R.id.panValue);
            clientcode = mView.findViewById(R.id.clientcode);
            dptextvalue = mView.findViewById(R.id.dptextvalue);
            beni_id = mView.findViewById(R.id.beni_id);
            tenor = mView.findViewById(R.id.tenor);
            interestfreq = mView.findViewById(R.id.interestfreq);
            totalamount = mView.findViewById(R.id.totalamount);
            interestrate = mView.findViewById(R.id.interestrate);
            termcondclick = mView.findViewById(R.id.termcondclick);
            noofBondEditText = mView.findViewById(R.id.noofBondEditText);
            button_apply = mView.findViewById(R.id.button_apply);
            mfSpinnerfamily = mView.findViewById(R.id.mfSpinnerfamily);
            upiimage = mView.findViewById(R.id.upiimage);

            upiimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(upijsonData == null){
                        upijsonData = new JSONObject();
                    }
                    GlobalClass.fragmentTransaction(AddUpiFragment.newInstance(upijsonData,1), R.id.container_body, true, "");

                }
            });


            tenor.setText(tenors);
            interestfreq.setText(interestfreqs);
            totalamount.setText(totalamounts);
            interestrate.setText(interestrates);
            noofBondEditText.setText(noofbonds);
            termcondclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eNACHConfirmation();
                }
            });
            noofBondEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        if (noofBondEditText.getText().toString().equalsIgnoreCase("")) {
                            GrandTotal = "0";
                            double noofbondtemp = 0;
                            double issuepricetemp = Double.parseDouble(totalamounts);
                            double total = noofbondtemp * issuepricetemp;
                            int totalvalue = (int) total;
                            int noofbondint = (int) noofbondtemp;
                            BondQty = "|" + OptID + ":" + noofbondint + ":" + totalvalue + "|";
                            GrandTotal = String.valueOf(totalvalue);
                            totalamount.setText(GlobalClass.getFormattedAmountString2(totalvalue));
                            totalamount.setText(GlobalClass.getFormattedAmountString2(totalvalue));
                        } else {
                            double noofbondtemp = Double.parseDouble(noofBondEditText.getText().toString().trim());
                            double issuepricetemp = Double.parseDouble(totalamounts);
                            double total = noofbondtemp * issuepricetemp;
                            int totalvalue = (int) total;
                            int noofbondint = (int) noofbondtemp;
                            BondQty = "|" + OptID + ":" + noofbondint + ":" + totalvalue + "|";
                            GrandTotal = String.valueOf(totalvalue);
                            totalamount.setText(GlobalClass.getFormattedAmountString2(totalvalue));
                            totalamount.setText(GlobalClass.getFormattedAmountString2(totalvalue));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }


            });
            new IPODetailsReq(eMessageCodeWealth.BONDUPI_DETAILS.value).execute();
            button_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int noofbond = Integer.parseInt(noofBondEditText.getText().toString());
                    int totalmount = Integer.parseInt(totalamount.getText().toString().replaceAll(",", ""));
                    try {
                        if(noofbond < 10){
                            GlobalClass.showAlertDialog("Minimum Quantity" +noofbonds+"Bonds");
                            noofBondEditText.setText(noofbonds);
                            totalamount.setText(totalamounts);
                        }else if(totalmount > 200000){
                            GlobalClass.showAlertDialog("An application of maximum Rs. 2 lakh is allowed with UPI Id");

                        }else if(mfSpinnerfamily == null || mfSpinnerfamily.getSelectedItem().toString().equalsIgnoreCase("")){
                            GlobalClass.showAlertDialog("please select upi id");
                        }else {
                            new SubmitApplicationReq(bondID, BondQty, GrandTotal, mfSpinnerfamily.getSelectedItem().toString(), dptextvalue.getText().toString(), BeneficiaryId, appnofrom, appnoto).execute();

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        GlobalClass.showAlertDialog("please select upi id");


                    }

                }
            });


            double noofbondtemp = Double.parseDouble(noofBondEditText.getText().toString().trim());
            double issuepricetemp = Double.parseDouble(totalamounts);
            double total = noofbondtemp * issuepricetemp;
            int totalvalue = (int) total;
            int noofbondint = (int) noofbondtemp;
            BondQty = "|" + OptID + ":" + noofbondint + ":" + totalvalue + "|";
            GrandTotal = String.valueOf(totalvalue);
            totalamount.setText(GlobalClass.getFormattedAmountString2(totalvalue));
        }
        return mView;
    }
    private void eNACHConfirmation(){

        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.setTitle("Scheme Names");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.nache_confirmation_alert_layout);
            TextView ok_button = dialog.findViewById(R.id.ok_button);

            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    class IPODetailsReq extends AsyncTask<String, Void, String> {
        int msgCode;

        IPODetailsReq(int mCode){
            this.msgCode = mCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if(UserSession.getClientResponse().isNeedAccordLogin()) {
                ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                    VenturaServerConnect.closeSocket();
                } else {
                    return clientLoginResponse.charResMsg.getValue();
                }
            }
            if(VenturaServerConnect.connectToWealthServer(true)) {
                if (msgCode == eMessageCodeWealth.BONDUPI_DETAILS.value) {
                    try {
                        JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(msgCode);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (msgCode == eMessageCodeWealth.BOND_CLIENTDETAILS.value) {
                    try {
                        JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(msgCode);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();

            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    if(!jsonData.isNull("error")){
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        if(!err.equalsIgnoreCase("")){
                            displaDataForUPI(new JSONObject());
                            initUPIIdSpinner(ObjectHolder.upiArr,mfSpinnerfamily);

                            new IPODetailsReq(eMessageCodeWealth.BOND_CLIENTDETAILS.value).execute();
                        }
                        //displayError(err);
                    } else if(msgCode == eMessageCodeWealth.BOND_CLIENTDETAILS.value){
                        JSONArray jsonArray = jsonData.getJSONArray("data");
                        JSONObject jobj = jsonArray.getJSONObject(0);
                        BeneficiaryId = jobj.optString("BeneficiaryId");
                        DPId  = jobj.optString("DPId");
                        ClientCode = jobj.optString("ClientCode");
                        PanNumber = jobj.optString("PANNo");
                        dptextvalue.setText(DPId);
                        clientcode.setText(ClientCode);
                        panValue.setText(PanNumber);
                        beni_id.setText(BeneficiaryId);


                    }else {
                        if(msgCode == eMessageCodeWealth.BONDUPI_DETAILS.value){
                            displaDataForUPI(jsonData);
                            initUPIIdSpinner(ObjectHolder.upiArr,mfSpinnerfamily);
                            new IPODetailsReq(eMessageCodeWealth.BOND_CLIENTDETAILS.value).execute();
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    if(!s.equalsIgnoreCase("")) {
                        if (s.toLowerCase().contains(Constants.WEALTH_ERR)) {
                            GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                        } else {
                            GlobalClass.showAlertDialog(s);
                        }
                    }
                }
            }
        }
    }

    private void displaDataForUPI(JSONObject jsonData) {

        try {
            GlobalClass.log("UPICodes : " + jsonData.toString());
            upijsonData = jsonData;
            JSONArray dataarr = jsonData.getJSONArray("data");
            if(dataarr.length() > 0) {
                ObjectHolder.upiArr = new String[dataarr.length()];
                for (int i = 0; i < dataarr.length(); i++) {
                    try {
                        JSONObject jdata = dataarr.getJSONObject(i);
                        //{"SrNo":"2029","UPICode":"test@axis","ClientCode":"98993320"}
                    /*
                    UPIIDDetails upi = new UPIIDDetails();
                    upi.srNo = jdata.getString("SrNo");
                    upi.upiId = jdata.getString("UPICode");
                    upi.clientCode = jdata.getString("ClientCode");
                    jList.add(upi);
                    */

                        ObjectHolder.upiArr[i] = jdata.getString("UPIId");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class  SubmitApplicationReq extends AsyncTask<String, Void, String> {
        String BondID = "",BondQty = "",GrandTotal = "",UPICode = "",
                DPId = "",BeneficiaryId = "",AppNoFrom = "",AppNoTo = "";



        SubmitApplicationReq(String BondID,String BondQty,String GrandTotal,String UPICode,String DPId,String BeneficiaryId,String AppNoFrom,String AppNoTo){
            this.BondID = BondID;
            this.BondQty = BondQty;
            this.GrandTotal = GrandTotal;
            this.UPICode = UPICode;
            this.DPId = DPId;
            this.BeneficiaryId = BeneficiaryId;
            this.AppNoFrom = AppNoFrom;
            this.AppNoTo = AppNoTo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject jsondata = new JSONObject();
            try {
                jsondata.put("clientcode", UserSession.getLoginDetailsModel().getUserID());
                jsondata.put("BondId",BondID);
                jsondata.put("BondQty",BondQty);
                jsondata.put("GrandTotal",GrandTotal);
                jsondata.put("UPICode",UPICode);
                jsondata.put("DPId",DPId);
                jsondata.put("BeneficiaryId",BeneficiaryId);
                jsondata.put("AppNoFrom",AppNoFrom);
                jsondata.put("AppNoTo",AppNoTo);
                jsondata.put("Exchange",ExChange);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.BOND_SUBMITAPPLICATION.value, jsondata);
            if (jsonData != null) {
                GlobalClass.log(jsonData.toString());
                return jsonData.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if (s != null) {
                try {
                    int status = 1;
                    JSONObject jobj = new JSONObject(s);
                    String AppNo = jobj.optString("error");
                    if(AppNo.length() < 9 ){
                        GlobalClass.showAlertDialog("Your application for " + BondName + " has been generated with application no." + AppNo + ". Kindly login to your UPI app. to block the funds.In case of any query you can write to us at " + getResources().getString(R.string.hyperlinktext));
                        //finishAffinity();
                        GlobalClass.fragmentTransaction(new WatchFragment(), R.id.container_body,
                                false, eFragments.WATCH.name);

                    }else {
                        GlobalClass.showAlertDialog(AppNo);
                    }



                } catch (Exception ex) {
                    GlobalClass.showAlertDialog(Constants.ERR_MSG);
                    GlobalClass.onError("",ex);
                }
            }else {
//                GlobalClass.showAlertDialog("Error Occured");

                homeActivity.onFragmentBack();
                homeActivity.onFragmentBack();
//                homeActivity.finish();
            }

        }
    }

    private  void  initUPIIdSpinner(String[] arr, Spinner upiSpinner) {
        try {
            ArrayAdapter spinnerAdapterF = new ArrayAdapter(getContext(), R.layout.mf_spinner_item_orange);
            spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            spinnerAdapterF.addAll((Object[]) arr);
            upiSpinner.setAdapter(spinnerAdapterF);




        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }





}
