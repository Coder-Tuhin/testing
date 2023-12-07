package fragments.edis;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import enums.eLogType;
import utils.DateUtil;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;


public class eDISFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private HomeActivity homeActivity;
    private View mView;
    private eidsAdapter adapter;
    private LinearLayout edisHeader;
    private RadioGroup radioGroup;
    //private RadioButton t2btn;
    //private RadioButton t1btn;



    private RecyclerView recycler_dematholding;
    private CheckBox selectAllChkBox;
    //private Button button_edis;
    private edisclientdatamodel clientData;

    private ArrayList<edisonerowmodel> mainholdingDataList;
    private ArrayList<edisonerowmodel> holdingDataList;

    private  int selectedIndex;

    public static eDISFragment newInstance() {
        eDISFragment dh = new eDISFragment();
        return dh;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity = (HomeActivity) getActivity();
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_edis, container, false);
            holdingDataList = new ArrayList<>();
            mainholdingDataList = new ArrayList<>();
            edisHeader = mView.findViewById(R.id.edisheader);
            recycler_dematholding = mView.findViewById(R.id.recycler_dematholding);
            selectAllChkBox = mView.findViewById(R.id.checkboxlist);

            radioGroup = mView.findViewById(R.id.radiogroupedis);
            //t2btn = mView.findViewById(R.id.t2btn);
            //t1btn = mView.findViewById(R.id.t1btn);
            selectedIndex = 0;
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(radioGroup.getCheckedRadioButtonId() == R.id.t2btn){
                        selectedIndex = 0;
                    }else{
                        selectedIndex = 1;
                    }
                    reloadDataForSelectedIndex();
                }
            });

            adapter = new eidsAdapter();
            recycler_dematholding.setAdapter(adapter);
            selectAllChkBox.setOnCheckedChangeListener(this);
        }
        new eDisHoldingReq().execute();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return mView;
    }
    private void reloadDataForSelectedIndex(){
        try {
            holdingDataList.clear();
            for (edisonerowmodel row : mainholdingDataList) {
                if (selectedIndex == 0 && row.getSettlement().equalsIgnoreCase("2")) {
                    holdingDataList.add(row);
                } else if (selectedIndex == 1 && row.getSettlement().equalsIgnoreCase("1")) {
                    holdingDataList.add(row);
                }
            }
            adapter.reloadData();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void placeOrderToNSDL(){
        //adapter.retrieve_Values();
        boolean isAnyCheckBoxChecked = false;
        boolean isQtyGreater = false;
        boolean isCheckedQtyZero = false;

        for(int i=0;i<holdingDataList.size();i++){
           edisonerowmodel edisonerowmodel =  holdingDataList.get(i);
           if(edisonerowmodel.isCheckBoxSelected()){
               isAnyCheckBoxChecked = true;
               if(Integer.parseInt(edisonerowmodel.getEnterQty())<=0)
                   isCheckedQtyZero = true;
           }
           if(Integer.parseInt(edisonerowmodel.getEnterQty())>Integer.parseInt(edisonerowmodel.getBod())){
               isQtyGreater = true;
           }
        }
        if(isQtyGreater){
            GlobalClass.showAlertDialog("Entered qty must be less or equal to non POA qty.");
        }else if(isCheckedQtyZero){
            GlobalClass.showAlertDialog("Invalid Data.");
        }else if(!isAnyCheckBoxChecked){
            GlobalClass.showAlertDialog("Please select at least one scrip.");
        }else {

            Fragment m_fragment;
            m_fragment = new EDISProcessFragmentReport(holdingDataList, clientData);

            FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, m_fragment);
            fragmentTransaction.addToBackStack("edis");
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(buttonView == selectAllChkBox){
            for(int i=0;i<holdingDataList.size();i++){
                edisonerowmodel _edisonerowmodel = holdingDataList.get(i);
                _edisonerowmodel.setCheckBoxSelected(isChecked);
            }
            adapter.reloadData();
        }
    }

    public class eidsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int FOOTER_VIEW = 1;
        private LayoutInflater inflater;

        eidsAdapter(){
            inflater = getLayoutInflater();
        }
        @Override
        public int getItemViewType(int position) {
            if (position == holdingDataList.size()) {
                return FOOTER_VIEW;
            }
            return super.getItemViewType(position);
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == FOOTER_VIEW) {
                View listItem = inflater.inflate(R.layout.item_edis_footer, parent, false);
                return new FooterViewHolder(listItem);
            }else {
                View listItem = inflater.inflate(R.layout.edislist, parent, false);
                return new DataViewHolder(listItem);
            }
        }
        public void reloadData(){
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DataViewHolder){
                edisonerowmodel rowData =  holdingDataList.get(position);
                ((DataViewHolder) holder).setValue(rowData,position);
            }
        }

        @Override
        public int getItemCount() {
            if (holdingDataList == null || holdingDataList.size()==0) {
                return 0;
            }
            return holdingDataList.size()+1;
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
                itemView.findViewById(R.id.button_edis).setOnClickListener(v -> {
                        placeOrderToNSDL();
                    }
                );
            }
        }


        class DataViewHolder extends RecyclerView.ViewHolder {

            CheckBox checkBox;
            TextView stockNameTxtView;
            TextView isinTxtView;
            TextView nonpoaqtyTxtView;
            EditText nonpoareqQtyEditTxt;

            public DataViewHolder(View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.checkboxlist);
                stockNameTxtView = itemView.findViewById(R.id.stockname);
                isinTxtView = itemView.findViewById(R.id.isinno);
                nonpoaqtyTxtView = itemView.findViewById(R.id.nonpoa_quantity);
                nonpoareqQtyEditTxt = itemView.findViewById(R.id.nonPOA_request_et);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            selectAllChkBox.setOnCheckedChangeListener(null);
                            selectAllChkBox.setChecked(false);
                            selectAllChkBox.setOnCheckedChangeListener(eDISFragment.this);
                        }
                        holdingDataList.get(getAdapterPosition()).setCheckBoxSelected(isChecked);
                    }
                });
                nonpoareqQtyEditTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = nonpoareqQtyEditTxt.getText().toString().trim();
                        if(!TextUtils.isEmpty(text)){
                            holdingDataList.get(getAdapterPosition()).setEnterQty(text);
                        }
                    }
                });
            }

            public void setValue(edisonerowmodel edisrow,int _position) {
                //{"bod":"84","name":"BHEL","isin":"INE257A01026 "}
                try {
                    stockNameTxtView.setText(edisrow.getName());
                    isinTxtView.setText(edisrow.getIsin());
                    nonpoaqtyTxtView.setText(edisrow.getBod());
                    nonpoareqQtyEditTxt.setText(edisrow.getEnterQty());
                    //nonpoareqQtyEditTxt.setFilters(new InputFilter[]{ new InputFilterMinMaxInteger("1", edisrow.getBod())});
                    checkBox.setChecked(edisrow.isCheckBoxSelected());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class eDisHoldingReq extends AsyncTask<String, Void, String>  {

        eDisHoldingReq(){
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                GlobalClass.showProgressDialog("Please wait...");
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(String... urls)  {

            //strToLog = (url + "   Param :" + param + " \n");
            try {
                String url = "https://edis.ventura1.com/eDIS/getholdingdata";
                String param = "ucc="+ UserSession.getLoginDetailsModel().getUserID();
                String strresponse = sendRequest(url, param);
                GlobalClass.log("EDIS Res: "+strresponse);
                return strresponse;
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)  {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            try {
                if(s != null && !s.equalsIgnoreCase("")) {
                    JSONObject jsonObject = new JSONObject(s);
                    //{"msg":"","datajarray":[{"bod":"84","name":"BHEL","isin":"INE257A01026 "},{"bod":"150","name":"MAHABANK","isin":"INE457A01014 "},{"bod":"99","name":"RELIANCE INDUS","isin":"INE467B01029 "}],"datajson":{"exid":"01","clientid":"10000342","apiurl":"https:\/\/ekyc.ventura1.com:51528\/eDIS\/webnsdledis","clientname":"TAPAS  NAYAK","segmentid":"00","returnurl":"https:\/\/ekyc.ventura1.com:51528\/eDIS\/webedisres"},"status":1}
                    int status = jsonObject.getInt("status");
                    if(status == 1){
                        JSONArray dataArray = jsonObject.getJSONArray("datajarray");
                        JSONObject jsonclientData = jsonObject.getJSONObject("datajson");
                        clientData = new edisclientdatamodel(jsonclientData);
                        mainholdingDataList.clear();
                        boolean isT2 = false,isT1 = false;
                        for(int i=0;i<dataArray.length();i++){
                            edisonerowmodel edisrow = new edisonerowmodel(dataArray.getJSONObject(i));
                            mainholdingDataList.add(edisrow);
                            if(edisrow.getSettlement().equalsIgnoreCase("2")){
                                isT2 = true;
                            }else if(edisrow.getSettlement().equalsIgnoreCase("1")){
                                isT1 = true;
                            }
                        }
                        if(isT1 && isT2){
                            radioGroup.setVisibility(View.VISIBLE);
                            selectedIndex = 0;
                        }else if(isT2){
                            selectedIndex = 0;
                        }else if(isT1){
                            selectedIndex = 1;
                        }
                        edisHeader.setVisibility(View.VISIBLE);
                        reloadDataForSelectedIndex();


                    }else{
                        String msg = jsonObject.getString("msg");
                        GlobalClass.showAlertDialog(msg);
                    }
                }
            }catch (Exception ex){
                GlobalClass.onError("",ex);
            }
        }
    }

    private  String sendRequest(String url,String param){

        String callTime = DateUtil.getcurrentTimeToN()+"";
        int responseCode = 0;
        HttpsURLConnection conn = null;
        try {
            StringBuffer strresponse = new StringBuffer();

            URL obj = new URL(url);
            conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            //60 sec timeout
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);
            //  xModuleClass.httpURLConnection.put(url, conn);
            //}
            param = param + "&cachetime="+callTime;
            try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
                writer.write(param);
                writer.flush();
                responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        strresponse.append(inputLine);
                    }
                    in.close();
                    inputStream.close();

                } else {
                    StringBuffer errorresponse = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        errorresponse.append(inputLine);
                    }
                    reader.close();
                    GlobalClass.log("edis callPOSTHTTPMethod: " + errorresponse.toString());
                }
            }
            conn.disconnect();
            return strresponse.toString();
        } catch(Exception e){
            e.printStackTrace();
            return "";
        }
        finally{
            if(conn != null){
                conn.disconnect();
            }
        }
    }
    /*
    public String sendRequest(String url,String param){
        int responseCode = 0;
        HttpsURLConnection connection=null;
        try {
            StringBuffer strresponse = new StringBuffer();
            URL obj = new URL(url+"?"+param);
            connection = (HttpsURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//          connection.setRequestProperty("Access-Token",token);
//          connection.setRequestProperty("Content-Length",encode(String.valueOf(main.length())));
            connection.setDoOutput(true);
            //connection.getOutputStream().write(main.toString().getBytes("UTF-8"));

            responseCode = connection.getResponseCode();
            GlobalClass.log("Response Code : "+responseCode);
            if (responseCode == 200) {

                InputStream inputStream = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    strresponse.append(inputLine);
                }
                in.close();
                inputStream.close();
            } else {
                StringBuffer errorresponse = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    errorresponse.append(inputLine);
                }
                GlobalClass.log("EDIS error : " + errorresponse.toString());
                reader.close();
            }

            connection.disconnect();
            return strresponse.toString();
        }
        catch(Exception e){
            GlobalClass.log(e.toString());
            return "";
        }
        finally{
            if(connection != null){
                connection.disconnect();
            }
        }
    }*/
}