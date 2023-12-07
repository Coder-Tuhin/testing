package wealth.new_mutualfund.ipo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import Structure.Response.AuthRelated.ClientLoginResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eIPOCategory;
import enums.eLogType;
import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import fragments.BaseFragment;
import utils.Constants;
import utils.DateUtil;
import utils.Formatter;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.ScreenColor;
import utils.StaticMethods;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class ApplyIPOFragment extends BaseFragment {

    IpoApplyAdapter ipoApplyAdapter;
    LinearLayout noteLinear;
    LinearLayout noipoLinear;

    TextView notetitle,notetitle1;
    Spinner upiSpinnerNoIssue;
    RecyclerView _recyclerView;

    JSONObject upijsonData;
    private String termsconditions = "file:///android_asset/IPO.html";

    public static ApplyIPOFragment newInstance(){
        return new ApplyIPOFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.ipo_aply_screen;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (ipoApplyAdapter != null) {
                if (ipoApplyAdapter.getItemCount() > 0) {
                    ipoApplyAdapter.reloadData();
                }
            }
            if(_recyclerView.getVisibility() == View.GONE){
                if(ObjectHolder.upiArr != null){
                    initUPIIdSpinnerForNoIssue(ObjectHolder.upiArr);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        ((Activity)getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);

        GlobalClass.showProgressDialog("Requesting...");
        _recyclerView = view.findViewById(R.id.recyclerView);
        notetitle = view.findViewById(R.id.notetitle);
        notetitle1 = view.findViewById(R.id.notetitle1);
        noteLinear = view.findViewById(R.id.noteLinear);
        noipoLinear = view.findViewById(R.id.noipo);

        ipoApplyAdapter = new IpoApplyAdapter();
        _recyclerView.setAdapter(ipoApplyAdapter);
        new IPODetailsReq(eMessageCodeWealth.IPO_ISSUE_DETAILS.value).execute();

        notetitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.showMsgDialog("IPO Notes",R.string.ipo_notes,false);
            }
        });

        TextView banklist = view.findViewById(R.id.banklist);
        banklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListPDFPDF();
            }
        });
        view.findViewById(R.id.addUpiNoIPO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upijsonData == null){
                    upijsonData = new JSONObject();
                }
                GlobalClass.fragmentTransaction(AddUpiFragment.newInstance(upijsonData,0), R.id.container_body, true, "");
            }
        });
        upiSpinnerNoIssue = view.findViewById(R.id.upiSpinnerNoIPO);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

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
            progressDialog.setTitle("Downloading..");
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

    public class IpoApplyAdapter extends RecyclerView.Adapter<IpoApplyAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private ArrayList<JSONObject> mList;

        IpoApplyAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
            mList = new ArrayList();
        }
        public void reloadData(ArrayList<JSONObject> value){
            this.mList = value;
            this.notifyDataSetChanged();
        }
        public void reloadData(){
            this.notifyDataSetChanged();
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_ipoapply, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                JSONObject jData = mList.get(position);
                holder.reloadData(jData);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            //return 3;
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.scheme_name)
            TextView schemename;
            @BindView(R.id.priceband)
            TextView priceBand;
            @BindView(R.id.discountnote)
            TextView discountnote;
            @BindView(R.id.dateOpen)
            TextView dateOpen;
            @BindView(R.id.dateClose)
            TextView dateClose;
            @BindView(R.id.individualshareRd)
            RadioGroup individualshareRd;
            @BindView(R.id.indivitualRbtn)
            RadioButton indivitualRbtn;
            @BindView(R.id.shareholderRbtn)
            RadioButton shareholderRbtn;
            @BindView(R.id.policyHolderRbtn)
            RadioButton policyRadioButton;

            @BindView(R.id.retailhniRd)
            RadioGroup retailhniRd;
            @BindView(R.id.retailRbtn)
            RadioButton retailRbtn;
            @BindView(R.id.hniRbtn)
            RadioButton hniRbtn;

            @BindView(R.id.bidamount)
            TextView bidamount;
            @BindView(R.id.bidQtySpinner)
            Spinner bidQtySpinner;
            @BindView(R.id.upiSpinner)
            Spinner upiSpinner;
            @BindView(R.id.issuenote)
            TextView issuenote;
            @BindView(R.id.submit)
            TextView submit;
            @BindView(R.id.statuslayout)
            LinearLayout statuslayout;
            //@BindView(R.id.applicationformlayout)
            //LinearLayout applicationformlayout;
            @BindView(R.id.statuipo)
            TextView statuipo;
            //@BindView(R.id.appform)
            //ImageView appform;

            @BindView(R.id.termcondChkBox)
            CheckBox termcondChkBox;
            @BindView(R.id.termcondclick)
            TextView termcondclick;
            @BindView(R.id.termsipo)
            LinearLayout termsipo;
            @BindView(R.id.bidpricell)
            LinearLayout bidpricell;
            @BindView(R.id.bidprice)
            EditText bidpriceedit;


            private JSONObject ipoDetail;
            private JSONObject selectedCategory;
            private eIPOCategory selectedRetailHNI = eIPOCategory.RETAIL;;

            public void  reloadData(JSONObject jsonD) {
                this.ipoDetail = jsonD;
                try {
                    JSONArray allCategory = ipoDetail.getJSONArray("category");
                    ArrayList<String> allCat = new ArrayList<>();
                    indivitualRbtn.setVisibility(View.GONE);
                    shareholderRbtn.setVisibility(View.GONE);
                    policyRadioButton.setVisibility(View.GONE);

                    for (int i = 0; i < allCategory.length(); i++) {
                        JSONObject currCategory = allCategory.optJSONObject(i);
                        String cat = currCategory.getString("Category");
                        allCat.add(cat);
                        if(cat.equalsIgnoreCase(eIPOCategory.SHA.name)){
                            shareholderRbtn.setVisibility(View.VISIBLE);
                        }else if(cat.equalsIgnoreCase(eIPOCategory.POL.name)){
                            policyRadioButton.setVisibility(View.VISIBLE);
                        }else if(cat.equalsIgnoreCase(eIPOCategory.IND.name)){
                            indivitualRbtn.setVisibility(View.VISIBLE);
                        }
                    }
                    if(allCat.size() == 1){
                        String cat = allCat.get(0);
                        if(cat.equalsIgnoreCase(eIPOCategory.SHA.name)){
                            shareholderRbtn.setVisibility(View.VISIBLE);
                            shareholderRbtn.setChecked(true);
                            loadDataForCaegory(eIPOCategory.SHA.name);
                        }else if(cat.equalsIgnoreCase(eIPOCategory.POL.name)){
                            policyRadioButton.setVisibility(View.VISIBLE);
                            policyRadioButton.setChecked(true);
                            loadDataForCaegory(eIPOCategory.POL.name);
                        }else{
                            indivitualRbtn.setVisibility(View.VISIBLE);
                            indivitualRbtn.setChecked(true);
                            loadDataForCaegory(eIPOCategory.IND.name);
                        }
                    }else{
                        indivitualRbtn.setChecked(true);
                        loadDataForCaegory(eIPOCategory.IND.name);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            private void loadDataForCaegory(String _category){
                try {
                    closeKeyboard();
                    schemename.setText(ipoDetail.getString("CompanyName"));
                    priceBand.setText(ipoDetail.getString("PriceBand"));
                    dateOpen.setText(ipoDetail.getString("OpenDate"));
                    dateClose.setText(ipoDetail.getString("CloseDate"));

                    JSONArray allCategory = ipoDetail.getJSONArray("category");
                    for (int i = 0; i < allCategory.length(); i++) {
                        JSONObject currCategory = allCategory.optJSONObject(i);
                        String cat = currCategory.getString("Category");
                        if(cat.equalsIgnoreCase(_category)){
                            selectedCategory = currCategory;
                            break;
                        }
                    }
                    discountnote.setText("");


                    JSONArray allIssueNotes = null;
                    if(!ipoDetail.isNull("IssueNote")){
                        allIssueNotes = ipoDetail.getJSONArray("IssueNote");
                    }

                    final boolean bidApplied = selectedCategory.getBoolean("BidApplied");
                    final boolean isHNI = selectedCategory.getString("IsHNI").equalsIgnoreCase("T");
                    if(!isHNI){
                        retailhniRd.setVisibility(View.GONE);
                        bidpricell.setVisibility(View.GONE);
                    }else{
                        indivitualRbtn.setText("Individual");
                        retailhniRd.setVisibility(View.VISIBLE);
                        retailhniRd.setOnCheckedChangeListener(null);
                    }
                    if(bidApplied){
                        String[] upiIdArr = new String[1];
                        String[] bidQtyArr = new String[1];
                        String savedUPIId = selectedCategory.getString("SavedUPIId");
                        String saveBidAmount = selectedCategory.getString("SavedBidAmount");
                        String savedBidQty = selectedCategory.getString("SavedBidQty");

                        String appStatus = selectedCategory.getString("ApplicationStatus");
                        String appFormStr = selectedCategory.getString("ApplicationForm");
                        upiIdArr[0] = savedUPIId;
                        initUPIIdSpinner(upiIdArr);
                        upiSpinner.setEnabled(false);

                        bidQtyArr[0] = savedBidQty;
                        ArrayAdapter spinnerAdapterF = new ArrayAdapter(getContext(), R.layout.mf_spinner_item_orange);
                        spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
                        spinnerAdapterF.addAll((Object[]) bidQtyArr);
                        bidQtySpinner.setAdapter(spinnerAdapterF);
                        bidQtySpinner.setEnabled(false);
                        bidamount.setText(saveBidAmount);
                        String invType = selectedCategory.getString("InvType");
                        if(invType.equalsIgnoreCase("h")){
                            hniRbtn.setChecked(true);
                            bidpricell.setVisibility(View.VISIBLE);
                            //double bidPrice = Formatter.stringToDoubleselectedCategory.getString("HNIBidPrice");//Formatter.stringToDouble(saveBidAmount) / Formatter.stringToInt(savedBidQty);
                            bidpriceedit.setText(selectedCategory.getString("HNIBidPrice"));
                            bidpriceedit.setEnabled(false);
                        }else{
                            bidpricell.setVisibility(View.GONE);
                            retailRbtn.setChecked(true);
                        }
                        statuslayout.setVisibility(View.VISIBLE);
                        statuipo.setText(appStatus);
                        submit.setVisibility(View.GONE);
                        termsipo.setVisibility(View.GONE);
                        hniRbtn.setEnabled(false);
                        retailRbtn.setEnabled(false);

                    }
                    else {
                        statuslayout.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        termsipo.setVisibility(View.VISIBLE);
                        upiSpinner.setEnabled(true);
                        if(ObjectHolder.upiArr != null && ObjectHolder.upiArr.length>0){
                            initUPIIdSpinner(ObjectHolder.upiArr);
                        } else {
                            String[] upiIdArr = new String[1];
                            upiIdArr[0] = "Select";
                            initUPIIdSpinner(upiIdArr);
                        }
                        discountnote.setText(selectedCategory.getString("DiscountNote"));
                        if(allIssueNotes != null){
                            String issN = "";
                            for(int i=0;i<allIssueNotes.length();i++){
                                issN = issN + (issN.equalsIgnoreCase("") ? "":"\n\n") + allIssueNotes.getString(i);
                            }
                            issuenote.setText(issN);
                        }else{
                            issuenote.setVisibility(View.GONE);
                        }
                        if(isHNI){
                            String hniBidPrice = selectedCategory.getString("MaxBidPrice");
                            bidpriceedit.setText(hniBidPrice);
                            hniRbtn.setEnabled(true);
                            retailRbtn.setEnabled(true);
                            retailRbtn.setChecked(true);
                            retailhniRd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                    if(hniRbtn.isChecked()){
                                        bidpricell.setVisibility(View.VISIBLE);
                                        selectedRetailHNI = eIPOCategory.HNI;
                                        bidpriceedit.setSelection(bidpriceedit.getText().length());
                                        bidpriceedit.requestFocus();
                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.showSoftInput(bidpriceedit, InputMethodManager.SHOW_IMPLICIT);
                                        showKeyboard();
                                    }else{
                                        bidpricell.setVisibility(View.GONE);
                                        selectedRetailHNI = eIPOCategory.RETAIL;
                                        bidpriceedit.clearFocus();
                                        closeKeyboard();
                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(bidpriceedit.getWindowToken(), 0);
                                    }
                                    initBidQtySpinner(selectedRetailHNI);
                                }
                            });
                        }
                        bidpricell.setVisibility(View.GONE);
                        selectedRetailHNI = eIPOCategory.RETAIL;
                        bidQtySpinner.setEnabled(true);

                        initBidQtySpinner(selectedRetailHNI);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            private  void  initUPIIdSpinner(String[] arr) {
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
            private  void  initBidQtySpinner(eIPOCategory subCategory){
                try {
                    String bidQty = selectedCategory.getString("BidQty");
                    JSONArray bidQtyJsonArr;
                    if(subCategory == eIPOCategory.HNI && selectedCategory.has("ddlHNIQty")){//HNI
                        bidQtyJsonArr = selectedCategory.getJSONArray("ddlHNIQty");
                    }else{
                        if(selectedCategory.has("ddlQty")) { //Retail
                            bidQtyJsonArr = selectedCategory.getJSONArray("ddlQty");
                        }else{
                            bidQtyJsonArr = new JSONArray();
                        }
                    }
                    String[] bidQtyArr = new String[bidQtyJsonArr.length()];
                    int selectectionPosition = -1;
                    for (int i = 0; i < bidQtyJsonArr.length(); i++) {
                        bidQtyArr[i] = bidQtyJsonArr.getString(i);
                        if (bidQty.equalsIgnoreCase(bidQtyJsonArr.getString(i))) {
                            selectectionPosition = i;
                        }
                    }

                    ArrayAdapter spinnerAdapterF = new ArrayAdapter(getContext(), R.layout.mf_spinner_item_orange);
                    spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
                    spinnerAdapterF.addAll((Object[]) bidQtyArr);
                    bidQtySpinner.setAdapter(spinnerAdapterF);

                    if(!bidQty.equalsIgnoreCase("") && selectectionPosition >= 0) {
                        bidQtySpinner.setSelection(selectectionPosition);
                    }
                    bidQtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            setBidPrice();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    bidpriceedit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if(!bidpriceedit.getText().toString().isEmpty()) {
                                setBidPrice();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            private void setBidPrice(){
                try {
                    String bidQty = bidQtySpinner.getSelectedItem().toString();
                    if(!bidQty.equalsIgnoreCase("select")) {
                        String bidPriceStr = (selectedRetailHNI == eIPOCategory.HNI)?bidpriceedit.getText().toString() : selectedCategory.getString("BidPrice");
                        double bidAmount = StaticMethods.getStringToInt(bidQty) * StaticMethods.getStringToDouble(bidPriceStr);
                        bidamount.setText(Formatter.TwoDecimalIncludingComma(bidAmount + ""));
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                //itemView.setOnClickListener(view -> gotoDetailsScreen());
                itemView.findViewById(R.id.addUpi).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(upijsonData == null){
                            upijsonData = new JSONObject();
                        }
                        GlobalClass.fragmentTransaction(AddUpiFragment.newInstance(upijsonData,0), R.id.container_body, true, "");
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String selectedUPI = upiSpinner.getSelectedItem().toString();
                            String selectedBidQty = bidQtySpinner.getSelectedItem().toString();
                            String bidPrice = selectedRetailHNI == eIPOCategory.HNI ? bidpriceedit.getText().toString() : selectedCategory.getString("BidPrice");
                            boolean isError = false;
                            if (selectedUPI.equalsIgnoreCase("Select")) {
                                isError = true;
                                displayError("Please Select UPI ID or Add UPI ID");
                            } else if (selectedBidQty.equalsIgnoreCase("Select")) {
                                isError = true;
                                displayError("Please Select Bid Qty");
                            } else if(selectedRetailHNI == eIPOCategory.HNI){
                                String bidAmount = bidamount.getText().toString();
                                String maxHNILimit = selectedCategory.getString("MaxHNILimit");
                                String maxBidPrice = selectedCategory.getString("MaxBidPrice");
                                String minBidPrice = selectedCategory.getString("MinBidPrice");
                                String hBidPrice = bidpriceedit.getText().toString();
                                if(Formatter.stringToDouble(hBidPrice) > Formatter.stringToDouble(maxBidPrice) ||
                                        Formatter.stringToDouble(hBidPrice) < Formatter.stringToDouble(minBidPrice)){
                                    isError = true;
                                    displayError("Price entered is outside bid price range, enter price between "+ minBidPrice +" - "+maxBidPrice);
                                } else if(Formatter.stringToDouble(bidAmount) > Formatter.stringToDouble(maxHNILimit)){
                                    isError = true;
                                    displayError("Max Amount Limit is "+maxHNILimit);
                                }
                            }else if(selectedRetailHNI == eIPOCategory.RETAIL){
                                String bidAmount = bidamount.getText().toString();
                                String maxRetailLimit = selectedCategory.getString("MaxRetailLimit");
                                if(Formatter.stringToDouble(bidAmount) > Formatter.stringToDouble(maxRetailLimit)){
                                    isError = true;
                                    displayError("Max Limit is "+maxRetailLimit);
                                }
                            }
                            if (!isError && !termcondChkBox.isChecked()) {
                                isError = true;
                                displayError("Please accept terms and conditions");
                            }
                            if(!isError) {

                                new ApplyIPOReq(eMessageCodeWealth.IPO_ISSUED_SAVE.value, ipoDetail,
                                        selectedCategory, selectedUPI, selectedBidQty,
                                        bidamount.getText().toString(), bidPrice, selectedRetailHNI.name).execute();


                            }
                        }catch (Exception ex){
                            displayError(Constants.ERR_MSG);
                        }
                    }
                });
                schemename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (ipoDetail != null) {

                                String reportPdf = ipoDetail.getString("REPORTFILE");
                                if(!reportPdf.equalsIgnoreCase("")) {
                                    showPDFPDF(reportPdf);
                                }
                            }
                        }
                        catch (Exception xe){
                            xe.printStackTrace();
                        }
                    }
                });
                termcondclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            loadWebViewOnDialog(termsconditions, "Terms & conditions");
                        }catch (Exception ex){
                            GlobalClass.onError("IPO terms and cond click",ex);
                        }
                    }
                });
                individualshareRd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(R.id.policyHolderRbtn == checkedId){
                            loadDataForCaegory(eIPOCategory.POL.name);
                        }else if(R.id.shareholderRbtn == checkedId){
                            loadDataForCaegory(eIPOCategory.SHA.name);
                        }else{
                            loadDataForCaegory(eIPOCategory.IND.name);
                        }
                    }
                });
            }
        }
    }

    private void  showPDFPDF(String pdfUrl){
        String tempUrl = pdfUrl;
        if (!tempUrl.startsWith("http://") && !tempUrl.startsWith("https://"))
            tempUrl = "http://" + tempUrl;

        try {
            if(tempUrl.toLowerCase().contains(".pdf")){
                try {
                    //loadWebViewOnDialog(tempUrl,"Bank List");
                    //Intent webview = new Intent(GlobalClass.mainContext, WebViewLayout.class);
                    //webview.putExtra("link", tempUrl);
                    //startActivity(webview);
                    String fileName = tempUrl.substring(tempUrl.lastIndexOf('/') + 1);
                    new DownloadFile().execute(tempUrl, fileName);
                }catch (Exception e){
                    GlobalClass.onError("Error in "+getClass().getName(),e);
                }
            }else {
                loadWebViewOnDialog(tempUrl, "");
                //Intent webview = new Intent(GlobalClass.mainContext, WebViewLayout.class);
                //webview.putExtra("link", tempUrl);
                //startActivity(webview);
            }
        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }
    private void  downLoadPDF(JSONObject ipoDetail){
        try {
            String appFormStr = ipoDetail.getString("ApplicationForm");
            if(!appFormStr.equalsIgnoreCase("")) {
                if(appFormStr.toLowerCase().contains(".pdf")){
                    try {
                        //loadWebViewOnDialog(tempUrl,"Bank List");
                        //Intent webview = new Intent(GlobalClass.mainContext, WebViewLayout.class);
                        //webview.putExtra("link", tempUrl);
                        //startActivity(webview);
                        String fileName = appFormStr.substring(appFormStr.lastIndexOf('/') + 1);
                        new DownloadFile().execute(appFormStr, fileName);
                    }catch (Exception e){
                        GlobalClass.onError("Error in "+getClass().getName(),e);
                    }
                }else {
                    loadWebViewOnDialog(appFormStr, "");
                    //Intent webview = new Intent(GlobalClass.mainContext, WebViewLayout.class);
                    //webview.putExtra("link", tempUrl);
                    //startActivity(webview);
                }
            }
        }
        catch (Exception ex){
            GlobalClass.onError("downLoadPDF IPO",ex);
        }
    }
    private void loadWebViewOnDialog(String url, String title) throws Exception{
        WebView mywebView = new WebView(getActivity());
        mywebView.loadUrl(url);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ventura_icon);
        builder.setTitle(title);
        builder.setView(mywebView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialogTemp = builder.create();
        alertDialogTemp.show();
        alertDialogTemp.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ScreenColor.VENTURA);
    }

    class ApplyIPOReq extends AsyncTask<String, Void, String> {
        int msgCode;
        JSONObject ipoDetail;
        JSONObject selectedCategoryDetail;
        final String selectedUPIID;
        final String bidQtystr;
        final String bidAmountstr;
        final String bidPricestr;
        final String invType;//R - Retail, H - HNI

        ApplyIPOReq(int mCode,JSONObject ipoDetail,JSONObject selectedCategory,String selectedUPIID,String bidQtystr,
                    String bidAmountstr,String bidPrice,String invType){
            this.msgCode = mCode;
            this.ipoDetail = ipoDetail;
            this.selectedUPIID = selectedUPIID;
            this.bidQtystr = bidQtystr;
            this.bidAmountstr = bidAmountstr;
            this.selectedCategoryDetail = selectedCategory;
            this.bidPricestr = bidPrice;
            this.invType = invType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {

            if(msgCode == eMessageCodeWealth.IPO_ISSUED_SAVE.value){
                try {

                    String ipoCode = ipoDetail.getString("IPOCode");
                    String categoryCode = selectedCategoryDetail.getString("Category");

                    String bidAmt = bidAmountstr;
                    bidAmt = bidAmt.replaceAll(",","");

                    String bidPrice = bidPricestr.replaceAll(",","");
                    String invT = invType;
                    if(invType.equalsIgnoreCase(eIPOCategory.HNI.name)){
                        invT = "H";
                    }else if(invType.equalsIgnoreCase(eIPOCategory.RETAIL.name)){
                        invT = "R";
                    }
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    jdata.put(eMFJsonTag.IPOCode.name,ipoCode);
                    jdata.put(eMFJsonTag.Category.name,categoryCode);
                    jdata.put(eMFJsonTag.UPICode.name, selectedUPIID);
                    jdata.put(eMFJsonTag.BidQty.name, bidQtystr);
                    jdata.put(eMFJsonTag.Bidamt.name, bidAmt);
                    jdata.put(eMFJsonTag.InvType.name, invT);
                    jdata.put(eMFJsonTag.BidPrice.name, bidPrice);

                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(msgCode,jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
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

                        displayError(err);
                        if(msgCode == eMessageCodeWealth.IPO_ISSUED_SAVE.value){
                            reloadDataForIPOPlace();
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    private void reloadDataForIPOPlace(){
        new IPODetailsReq(eMessageCodeWealth.IPO_ISSUE_DETAILS.value).execute();
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

                        ObjectHolder.upiArr[i] = jdata.getString("UPICode");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (ipoApplyAdapter != null) {
                if(ipoApplyAdapter.getItemCount() > 0) {
                    ipoApplyAdapter.reloadData();
                }
            }
            if(_recyclerView.getVisibility() == View.GONE){
                if(ObjectHolder.upiArr != null && ObjectHolder.upiArr.length>0){
                    initUPIIdSpinnerForNoIssue(ObjectHolder.upiArr);
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
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


            //UserSession.getLoginDetailsModel().setUserID("90098995");
            //UserSession.getLoginDetailsModel().getUserID()

            if(UserSession.getClientResponse().isNeedAccordLogin()) {
                ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                    VenturaServerConnect.closeSocket();
                } else {
                    return clientLoginResponse.charResMsg.getValue();
                }
            }
            if(VenturaServerConnect.connectToWealthServer(true)) {
                if (msgCode == eMessageCodeWealth.IPO_ISSUE_DETAILS.value) {
                    try {
                        JSONObject jsonData = VenturaServerConnect.sendReqDataMFReport(msgCode);
                        if (jsonData != null) {
                            return jsonData.toString();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (msgCode == eMessageCodeWealth.IPO_UPI_DETAILS.value) {
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
                        if(msgCode != eMessageCodeWealth.IPO_UPI_DETAILS.value) {
                            if (err.contains("expired")){
                                displaySessionExit(err);
                            }else {
                                displayNoIssue();
                            }
                        }
                        //displayError(err);
                    } else {
                        if(msgCode == eMessageCodeWealth.IPO_ISSUE_DETAILS.value) {
                            displaData(jsonData);
                        }else if(msgCode == eMessageCodeWealth.IPO_UPI_DETAILS.value){
                            displaDataForUPI(jsonData);
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

    private void displaData(JSONObject jsonData) {

        try {
            GlobalClass.log("IPO Details : " + jsonData.toString());

            JSONArray dataarr = jsonData.getJSONArray("data");
            ArrayList<JSONObject> jList = new ArrayList<>();
            for(int i=0;i<dataarr.length();i++){
                JSONObject jdata = dataarr.getJSONObject(i);
                jList.add(jdata);
            }
            if(jList.size() > 0) {
                if (ipoApplyAdapter != null) {
                    ipoApplyAdapter.reloadData(jList);
                }
                noteLinear.setVisibility(View.VISIBLE);
                if(ObjectHolder.upiArr == null || ObjectHolder.upiArr.length == 0){
                    new IPODetailsReq(eMessageCodeWealth.IPO_UPI_DETAILS.value).execute();
                }
                if(!jsonData.isNull("GeneralNote")){
                    JSONArray gNoteArr = jsonData.getJSONArray("GeneralNote");
                    if(gNoteArr.length() > 0){
                        String s = "";
                        for(int i=0;i<gNoteArr.length();i++){
                            s = s + (s.equalsIgnoreCase("") ? "":"\n\n") + gNoteArr.getString(i);
                        }
                        notetitle1.setText(s);
                    }else{
                        notetitle1.setVisibility(View.GONE );
                    }
                }
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void displayNoIssue(){
        _recyclerView.setVisibility(View.GONE);
        noipoLinear.setVisibility(View.VISIBLE);
        noteLinear.setVisibility(View.VISIBLE);
        if(ObjectHolder.upiArr == null || ObjectHolder.upiArr.length == 0){
            new IPODetailsReq(eMessageCodeWealth.IPO_UPI_DETAILS.value).execute();
        }else{
            initUPIIdSpinnerForNoIssue(ObjectHolder.upiArr);
        }
    }
    private  void  initUPIIdSpinnerForNoIssue(String[] arr) {
        try {
            ArrayAdapter spinnerAdapterF = new ArrayAdapter(getContext(), R.layout.mf_spinner_item_orange);
            spinnerAdapterF.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            spinnerAdapterF.addAll((Object[]) arr);
            upiSpinnerNoIssue.setAdapter(spinnerAdapterF);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    private HomeActivity homeActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    private void displaySessionExit(String msg){
        homeActivity.showMsgDialog("", msg, true);
    }

    private boolean isKeyBoardOpen = false;
    public void showKeyboard(){
        try {
            if(!isKeyBoardOpen) {
                //InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                isKeyBoardOpen = true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void closeKeyboard(){
        try {
            if(isKeyBoardOpen) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                isKeyBoardOpen = false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}