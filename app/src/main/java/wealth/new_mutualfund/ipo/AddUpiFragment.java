package wealth.new_mutualfund.ipo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import fragments.BaseFragment;
import utils.DateUtil;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class AddUpiFragment extends BaseFragment {


    @BindView(R.id.upiEditText)
    EditText upiidEditText;

    @BindView(R.id.submit)
    TextView submit;

    @BindView(R.id.activeupilist)
    TextView activeupilist;
    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.upiusername)
    TextView upiusername;



    UPIListAdapter upiListAdapter;
    ArrayList<String> upiIDList;
    public static int TAG;

    public static AddUpiFragment newInstance(JSONObject jData,int tag){
        AddUpiFragment f = new AddUpiFragment();
        Bundle args = new Bundle();
        args.putString(eMFJsonTag.JDATA.name, jData.toString());
        TAG = tag;
        f.setArguments(args);
        return f;
    }

    public static AddUpiFragment newInstance(){

        return new AddUpiFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.add_upi_screen;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        RecyclerView _recyclerView = view.findViewById(R.id.recyclerView);
        upiListAdapter = new UPIListAdapter();
        _recyclerView.setAdapter(upiListAdapter);
        upiusername.setText("");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.onFragmentBack();
            }
        });
        activeupilist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.showUPIHandlerDialogFragment();
            }
        });
        TextView banklist = view.findViewById(R.id.banklist);
        banklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListPDFPDF();
            }
        });
        initData();
    }

    private void  showBankListPDFPDF(){
        String tempUrl = "https://ipo.ventura1.com/aceipo/Client/BankUPI/BANKUPI.PDF";
        if (!tempUrl.startsWith("http://") && !tempUrl.startsWith("https://"))
            tempUrl = "http://" + tempUrl;
        try {
            //Intent webview = new Intent(GlobalClass.mainContext, WebViewLayout.class);
            //webview.putExtra("link", tempUrl);
            //startActivity(webview);

            new DownloadFile().execute(tempUrl, "banklist_"+ DateUtil.getCurrentDate());
        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<String, Integer, String> {

        String savedFilePath = null;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            progressDialog = new ProgressDialog(homeActivity);
            progressDialog.setTitle("Downloading Bank List");
            progressDialog.setMessage("Please wait (0%)");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urlParams) {
            int count;
            String fileName = urlParams[1] + ".pdf";
            File storageDir = new File(homeActivity.getExternalFilesDir(null) + "/PDF_FOLDER/");
            boolean success = true;
            if (!storageDir.exists()) {
                success = storageDir.mkdir();
            }
            if (success) {
                File file = new File(storageDir, fileName);
                savedFilePath = file.getAbsolutePath();
                if (!file.exists()) {
                    try {
                        URL url = new URL(urlParams[0]);
                        URLConnection conexion = url.openConnection();
                        conexion.connect();
                        int lengthOfFile = conexion.getContentLength();
                        InputStream input = new BufferedInputStream(url.openStream());
                        OutputStream output = new FileOutputStream(file);
                        byte[] data = new byte[1024];
                        long total = 0;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            publishProgress((int) (total * 100 / lengthOfFile));
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            return savedFilePath;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Please wait (" + values[0] + "%)");
        }

        @Override
        protected void onPostExecute(String pdfPath) {
            super.onPostExecute(pdfPath);
            if (pdfPath != null && !pdfPath.isEmpty()) {
                progressDialog.dismiss();
                showPDFDialog(pdfPath);
            }
        }
    }
    public void showPDFDialog(String pdfPath) {
        try {
            Dialog dialog = new Dialog(homeActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            assert dialog.getWindow() != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.ipo_bank_dialog);
            ImageButton cancelBtn = dialog.findViewById(R.id.cancel_action);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            PDFView pdfView = dialog.findViewById(R.id.pdfView);
            if (pdfPath != null && FileUtils.isFileExists(FileUtils.getFileByPath(pdfPath)))
                pdfView.fromFile(FileUtils.getFileByPath(pdfPath)).defaultPage(0)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(homeActivity))
                        .load();
            else ToastUtils.showShort("FILE NOT EXISTS");
            dialog.show();
            //int height = ObjectHolder.dHeight * 4 / 5;
            //int weidth = ObjectHolder.dWidth * 4 / 5;
            //Objects.requireNonNull(dialog.getWindow()).setLayout((int) weidth, (int) height);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void  initData(){
        try {
            upiIDList = new ArrayList<>();
            if(ObjectHolder.upiArr != null) {
                for (int i = 0; i < ObjectHolder.upiArr.length; i++) {
                    try {
                        upiIDList.add(ObjectHolder.upiArr[i]);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                reloadUPIList();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void reloadUPIList(){

        try {
            upiListAdapter.reloadData(upiIDList);
            ObjectHolder.upiArr = upiIDList.toArray(new String[0]);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void validateData(){
        String upiid = upiidEditText.getText().toString().toLowerCase();
        if(!upiid.equalsIgnoreCase("")){
            if(upiIDList.contains(upiid)){
                displayError("UPI ID already exist");
            } else if(upiIDList.size()>= 3){
                displayError("You can add upto 3 UPI ID only");
            } else {
                if(submit.getText().toString().equalsIgnoreCase("verify")) {
                    if(TAG == 1){
                        new AddUPIReq(eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value).execute();
                    }else {
                        new AddUPIReq(eMessageCodeWealth.IPO_UPI_VERIFY.value).execute();
                    }
                }else{
                    if(TAG == 1){
                        new AddUPIReq(eMessageCodeWealth.BONDSAVEUPI_DETAILS.value).execute();
                    }else {
                        new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();
                    }

                }
            }
        } else{
            displayError("Please enter UPI ID");
        }
    }

    public class UPIListAdapter extends RecyclerView.Adapter<UPIListAdapter.MyViewHolder> {

        private ArrayList<String> mList;
        private LayoutInflater inflater;

        UPIListAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList();
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_delete_upi, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
        public void reloadData(ArrayList<String> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                holder.reloadData(mList.get(position));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.upiid)
            TextView upicode;
            @BindView(R.id.actionIV)
            ImageView actionIV;

            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                actionIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TAG == 1){
                            new AddUPIReq(eMessageCodeWealth.BONDDELETEUPI_DETAILS.value, upistr).execute();
                        }else {
                            new AddUPIReq(eMessageCodeWealth.IPO_UPI_DELETE.value, upistr).execute();

                        }
                    }
                });
            }

            private String upistr;
            public void  reloadData(String value){
                this.upistr = value;
                try {
                    upicode.setText(upistr);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }


    class AddUPIReq extends AsyncTask<String, Void, String> {
        int msgCode;
        String selectedUPI;

        AddUPIReq(int mCode){
            this.msgCode = mCode;
        }
        AddUPIReq(int mCode,String upiCode){
            this.msgCode = mCode;
            this.selectedUPI = upiCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if(msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value || msgCode == eMessageCodeWealth.BONDSAVEUPI_DETAILS.value){
                try {
                    String upiCode = upiidEditText.getText().toString();

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value){
                try {
                    String upiCode = upiidEditText.getText().toString();

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.IPO_UPI_DELETE.value || msgCode == eMessageCodeWealth.BONDDELETEUPI_DETAILS.value){
                try {
                    String upiCode = selectedUPI;

                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.UPICode.name, upiCode);
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(msgCode == eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value) {
                try{
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    displayError(e.getMessage());
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
                        if(msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value){
                            handleUPIVerify(jsonData);
                        }else if(msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value){
                            displayUPIAddedSuccessMSg(err);
                        }else {
                            displayError(err);
                        }
                        //UPI ID Added Successfully
                        if(err.toLowerCase().contains("success")){
                            //add this UPI ID to list..
                            if(msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value || msgCode == eMessageCodeWealth.BONDSAVEUPI_DETAILS.value){
                                upiIDList.add(upiidEditText.getText().toString());
                            }else if(msgCode == eMessageCodeWealth.IPO_UPI_DELETE.value || msgCode == eMessageCodeWealth.BONDDELETEUPI_DETAILS.value){
                                upiIDList.remove(selectedUPI);
                                GlobalClass.log("MrGoutamD (IPO_GET_IPO_HANDLER_LIST): "+jsonData.toString());
                            }
                            reloadUPIList();
                            if(msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value || msgCode == eMessageCodeWealth.BONDSAVEUPI_DETAILS.value){
                                homeActivity.onFragmentBack();
                            }
                        }
                    } else {
                        if(msgCode == eMessageCodeWealth.IPO_UPI_SAVE.value || msgCode == eMessageCodeWealth.BONDSAVEUPI_DETAILS.value) {
                            displaDataForUPI(jsonData);
                        }else if(msgCode == eMessageCodeWealth.IPO_UPI_VERIFY.value || msgCode == eMessageCodeWealth.BONDVERIFYUPI_DETAILS.value){
                            handleUPIVerify(jsonData);
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void handleUPIVerify(JSONObject jsonData) {
        try {
            String err = jsonData.getString("error");
            if(err.toLowerCase().contains("not available") ||
                    err.toLowerCase().contains("fail") ||
                    err.toLowerCase().contains("error") ||
                    err.toLowerCase().contains("issue")){
                displayError("Please enter a valid UPI ID.");
            }else{
                String checkedMark =err;// + " " + "\u2713" ;
                upiusername.setText(checkedMark);
                submit.setText("Submit");
                upiidEditText.setEnabled(true);
                //new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();
            }
            //IF SUCCESS
            //new AddUPIReq(eMessageCodeWealth.IPO_UPI_SAVE.value).execute();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void displaDataForUPI(JSONObject jsonData) {
        try {
            GlobalClass.log("UPICodes : " + jsonData.toString());

            JSONArray dataarr = jsonData.getJSONArray("data");
            ArrayList<JSONObject> jList = new ArrayList<>();
            for(int i=0;i<dataarr.length();i++){
                JSONObject jdata = dataarr.getJSONObject(i);
                jList.add(jdata);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }
    private void displayUPIAddedSuccessMSg(String msg){

        if(msg.contains("|") && msg.split("\\|").length>=2) {
            try {
            String[] strArr = msg.split("\\|");
            String apiMsg = strArr[2];
            String link = strArr[1];

            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(apiMsg);
            ForegroundColorSpan color = new ForegroundColorSpan(GlobalClass.latestContext.getResources().getColor(R.color.ventura_color));
            int indexOfCH = apiMsg.toLowerCase().indexOf("click here");
            ssBuilder.setSpan(color, indexOfCH, indexOfCH + String.valueOf("click here").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssBuilder.setSpan(new UnderlineSpan(), indexOfCH, indexOfCH + String.valueOf("click here").length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                final Dialog dialog = new Dialog(getContext());
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.ipo_upiidadd_dialog_layout);

                TextView titleTv = dialog.findViewById(R.id.title);
                titleTv.setText(ssBuilder);
                titleTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent in = new Intent(Intent.ACTION_VIEW);
                            in.setData(Uri.parse(link));
                            GlobalClass.latestContext.startActivity(in);

                            dialog.dismiss();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                ImageView closeAlert = dialog.findViewById(R.id.closeTv);
                closeAlert.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            displayError(msg);
        }
    }

    private HomeActivity homeActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

}