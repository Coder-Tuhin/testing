package fragments.NFO;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eFormScr;
import enums.eLogType;
import enums.eMFClientType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;

import enums.eOptionMF;
import utils.Constants;
import utils.GlobalClass;
import utils.StaticMethods;
import utils.UserSession;
import utils.VenturaException;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.menus.SipFragment;
import wealth.new_mutualfund.newMF.OneTimeFragment;
import wealth.new_mutualfund.newMF.SIPEnterAmoutFragment;

public class NFO_Fragment extends Fragment {
    private HomeActivity homeActivity;
    @BindView(R.id.radioGrp)
    RadioGroup radioGrp;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    View mView;
    public static NFO_Fragment newInstance() {
        NFO_Fragment fragment = new NFO_Fragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

        if (mView == null) {
            mView = inflater.inflate(R.layout.nfo_screen, container, false);
            ButterKnife.bind(this, mView);
            recyclerView.setAdapter(new NfoAdapter());
            radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    NfoAdapter nfoAdapter = (NfoAdapter) recyclerView.getAdapter();
                    nfoAdapter.ClearList();
                    sendNFOrequest();
                }
            });
            sendNFOrequest();
        }

        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return mView;
    }
    private void sendNFOrequest() {
        new NFOreq(eMessageCodeWealth.CLIENT_SESSION.value).execute();
    }

    private class NfoAdapter extends RecyclerView.Adapter<NfoAdapter.MyViewHolder> {
        private ArrayList<NFOmodel> mList;
        private LayoutInflater inflater;
        private NfoAdapter() {
            inflater = LayoutInflater.from(getContext());
            mList = new ArrayList();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_nfo, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            try {
                NFOmodel nfOmodel = mList.get(position);
                holder.schemeNameTv.setText(nfOmodel.getSchemeName());
                holder.closedatetv.setText(nfOmodel.getCloseDate());
                if(nfOmodel.getPurchase().equalsIgnoreCase("Y") ){
                    holder.oneTimeTv.setVisibility(View.VISIBLE);
                    holder.oneTimeTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                //LumpSumFragment ls = LumpSumFragment.newInstance(nfOmodel.getSchemeCode(), nfOmodel.getSchemeName(),"", false, true);

                                OneTimeFragment ls = OneTimeFragment.newInstance(nfOmodel.getSchemeCode(), nfOmodel.getSchemeName(),"", eOptionMF.NFO.name);

                                homeActivity.FragmentTransaction(ls, R.id.container_body, true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    holder.oneTimeTv.setVisibility(View.INVISIBLE);
                }
                if(nfOmodel.getSIP().equalsIgnoreCase("T")) {
                    holder.sipTv.setVisibility(View.VISIBLE);
                    holder.sipTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            homeActivity.FragmentTransaction(SIPEnterAmoutFragment.newInstance(nfOmodel.jObj,eFormScr.NFO.name), R.id.container_body, true);


                        }
                    });
                }else{
                    holder.sipTv.setVisibility(View.INVISIBLE);
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView schemeNameTv;
            TextView closedatetv;
            TextView oneTimeTv;
            TextView sipTv;
            public MyViewHolder(View itemView) {
                super(itemView);
                schemeNameTv = itemView.findViewById(R.id.schemeNameTv);
                closedatetv = itemView.findViewById(R.id.closedatetv);
                oneTimeTv = itemView.findViewById(R.id.onetimeTv);
                sipTv = itemView.findViewById(R.id.sipTv);
            }
        }

        public void RefreshAdapter(ArrayList<NFOmodel> _mList){
            this.mList.clear();
            this.mList.addAll(_mList);
            notifyDataSetChanged();
        }
        public void ClearList(){
            this.mList.clear();
            notifyDataSetChanged();
        }
    }

    private int getInstval(){
        int Instval = 1;
        switch (radioGrp.getCheckedRadioButtonId()){
            case R.id.debt:
                Instval = 50;
                break;
            case R.id.hybrid:
                Instval = 2;
                break;
            case R.id.others:
                Instval = 6;
                break;
            case R.id.liquid:
                Instval = 0;
                break;
            default:
                Instval = 1;
                break;
        }
        return Instval;
    }

    class NFOreq extends AsyncTask<String, Void, String> {
        private int msgCode = -1;

        NFOreq(int mCode) {
            this.msgCode = mCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {

                    if(UserSession.getClientResponse().isNeedAccordLogin()) {
                        ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                        if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                            VenturaServerConnect.closeSocket();
                            if(VenturaServerConnect.connectToWealthServer(true)) {
                                JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                                if (jsonData != null) {
                                    return jsonData.toString();
                                }
                            }
                        } else {
                            return clientLoginResponse.charResMsg.getValue();
                        }
                    }else{
                        if(VenturaServerConnect.connectToWealthServer(true)) {
                            JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(eMessageCodeWealth.CLIENT_SESSION.value);
                            if (jsonData != null) {
                                return jsonData.toString();
                            }
                        }
                    }

                } else if (msgCode == eMessageCodeWealth.NFO_REPORT.value) {
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.INSTNAME.name, String.valueOf(getInstval()));
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.NFO_REPORT.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
            } catch (Exception ex) {
                VenturaException.Print(ex);
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
                    if(!jsonData.isNull("error")) {
                        String err = jsonData.getString("error");
                        if(err.toLowerCase().contains(GlobalClass.sessionExpiredMsg)){
                            GlobalClass.showAlertDialog(err,true);
                            return;
                        }
                        GlobalClass.showAlertDialog(err);
                    }else {
                        if (msgCode == eMessageCodeWealth.CLIENT_SESSION.value) {
                            String mfC = jsonData.getString("MFClientType");
                            VenturaServerConnect.mfClientID = jsonData.getString("MFBClientId");
                            if (mfC.equalsIgnoreCase(eMFClientType.MFI.name)) {
                                VenturaServerConnect.mfClientType = eMFClientType.MFI;
                            } else {
                                VenturaServerConnect.mfClientType = eMFClientType.MFD;
                            }

                            new NFOreq(eMessageCodeWealth.NFO_REPORT.value).execute();
                        }else if (msgCode == eMessageCodeWealth.NFO_REPORT.value) {
                            GlobalClass.log("megh", "NFO_REPORT: "+jsonData.toString());
                            processNFOReport(jsonData);
                            //this is your data
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    if(s.toLowerCase().contains(Constants.WEALTH_ERR)){
                        GlobalClass.homeActivity.logoutAlert("Logout",Constants.LOGOUT_FOR_WEALTH,false);
                    }else {
                        GlobalClass.showAlertDialog(s);
                    }
                }
            }
        }

        private void processNFOReport(JSONObject jsonData){
            try{
                JSONArray data = StaticMethods.getJSONarray("data",jsonData);
                ArrayList<NFOmodel> mList = new ArrayList<>();
                for (int i = 0;i<data.length();i++){
                    JSONObject itemObj = data.optJSONObject(i);
                    mList.add(new NFOmodel(itemObj));
                }
                NfoAdapter _nfoA = (NfoAdapter) recyclerView.getAdapter();
                homeActivity.runOnUiThread(() -> _nfoA.RefreshAdapter(mList));
            }
            catch(Exception e){
                VenturaException.Print(e);
            }
        }

    }
    private class NFOmodel {
        private String SchemeCode = "";
        private String SchemeName = "";
        private String Type = "";
        private String CloseDate = "";
        private String Minamount = "";
        private String File = "";
        private String Purchase = "";
        private String SIP = "";
        private JSONObject jObj;

        public NFOmodel(JSONObject jObj){
            try {
                this.jObj = jObj;
                SchemeCode = jObj.optString("SchemeCode");
                SchemeName = jObj.optString("SchemeName");
                Type = jObj.optString("Type");
                CloseDate = jObj.optString("CloseDate");
                Minamount = jObj.optString("Minamount");
                File = jObj.optString("File");
                Purchase = jObj.optString("Purchase");
                SIP = jObj.optString("SIP");
            }catch (Exception e){
                VenturaException.Print(e);
            }
        }

        public String getSchemeCode() {
            return SchemeCode;
        }

        public String getSchemeName() {
            return SchemeName;
        }

        public String getType() {
            return Type;
        }

        public String getCloseDate() {
            return CloseDate;
        }

        public String getMinamount() {
            return Minamount;
        }

        public String getFile() {
            return File;
        }

        public String getPurchase() {
            return Purchase;
        }

        public String getSIP() {
            return SIP;
        }
    }
}
