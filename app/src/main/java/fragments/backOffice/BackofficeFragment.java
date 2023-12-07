package fragments.backOffice;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Structure.Response.BC.StructLedger;
import Structure.Response.BC.StructLedgerResp;
import butterknife.ButterKnife;
import butterknife.BindView;

import com.ventura.venturawealth.R;

import connection.SendDataToBCServer;
import enums.eForHandler;
import enums.eLedgerEntryType;
import enums.eLogType;
import enums.eMessageCode;
import models.BackofficeModel;
import utils.Constants;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import utils.VenturaException;

public class BackofficeFragment extends Fragment implements View.OnClickListener {

    private View view;
    private List<BackofficeModel> list;
    @BindView(R.id.backoffice_spinner)
    Spinner backoffice_spinner;

    @BindView(R.id.backoffice_entryspinner)
    Spinner backoffice_entryspinner;
    @BindView(R.id.backofficeList)
    LinearLayout backofficeList;

    TextView openingBalance;
    TextView closingBalance;
/*
    @BindView(R.id.entryLinearLayout)
    LinearLayout entryLinearLayout;

    @BindView(R.id.entyRadiogrp)
    RadioGroup entyRadiogrp;
    @BindView(R.id.tenRd)
    RadioButton tenRd;
    @BindView(R.id.twentyRd)
    RadioButton twentyRd;
    @BindView(R.id.oneMonthRd)
    RadioButton oneMonthRd;*/

    private eLedgerEntryType selectedEntry;

    public BackofficeFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.backoffice_screen, null);
        openingBalance = view.findViewById(R.id.openingBalance);
        closingBalance = view.findViewById(R.id.closingBalance);
        ButterKnife.bind(this, view);
        //entyRadiogrp.setOnCheckedChangeListener(this);
        selectedEntry = eLedgerEntryType.TEN;
        initSpinner();
        new SendLedgerReq().execute();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return view;
    }

    private void callFromAPI() {
        String url = "https://backoffice.ventura1.com/Financial/Service1.asmx/FinancialService?sParty="
                + UserSession.getLoginDetailsModel().getUserID();
        new DownloadXmlTask().execute(url);
    }

    @Override
    public void onResume() {
        super.onResume();
        GlobalClass.ledgerHandler = new LedgerHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.ledgerHandler = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Please wait...");
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return getResources().getString(R.string.xml_error);
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();
            if (list != null) Collections.reverse(list);
//            addData();
            addData2();
        }
    }

    private void addData() {
        double balance = 0;
        backofficeList.removeAllViews();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.backoffice_item, null);
                TextView nameTv = (TextView) listView.findViewById(R.id.name);
                TextView dateTv = (TextView) listView.findViewById(R.id.date);
                TextView amountTv = (TextView) listView.findViewById(R.id.ammount);
                TextView balanceTv = (TextView) listView.findViewById(R.id.balance);
                BackofficeModel backofficeModel = list.get(i);
                String name = backofficeModel.getAccount();
                String date = backofficeModel.getDate();
                nameTv.setText(name);
                dateTv.setText(date);
                double ammount = Double.parseDouble(backofficeModel.getAmmount());
                if (i == 0) {
                    amountTv.setText("");
//                    balanceTv.setText(formatedAmt(ammount));
                    openingBalance.setText(formatedAmt(ammount));
                } else if (i == list.size() - 1) {
                    closingBalance.setText(formatedAmt(ammount));
                } else if (i == 1) {
                    amountTv.setText(formatedAmt(ammount));
                    double bal = Double.parseDouble(list.get(i).getAmmount()) +
                            Double.parseDouble(list.get(i - 1).getAmmount());
                    balance = bal;
                    if (i < (list.size() - 1) && date.equalsIgnoreCase(list.get(i + 1).getDate())) {
                        balanceTv.setText("");
                    } else {
                        balanceTv.setText(formatedAmt(balance));
                    }
                } else {
                    amountTv.setText(formatedAmt(ammount));
                    balance = balance + ammount;
                    if (i < (list.size() - 1) && date.equalsIgnoreCase(list.get(i + 1).getDate())) {
                        balanceTv.setText("");
                        if (i == list.size() - 1) {
                            balanceTv.setText(formatedAmt(balance));
                        }
                    } else {
                        balanceTv.setText(formatedAmt(balance));
                    }
                }
                if (balanceTv.getText().toString().contains("Cr")) {
                    balanceTv.setTextColor(ScreenColor.GREEN);
                } else {
                    balanceTv.setTextColor(ScreenColor.RED);
                }
                backofficeList.addView(listView);
            }
        } else {
            GlobalClass.showAlertDialog(Constants.backofficeServer);
        }
    }

    @SuppressLint("SetTextI18n")
    private void addData2() {
        double balance = 0;
        backofficeList.removeAllViews();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                View listView = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.new_backoffice_item, null);

                BackofficeModel backofficeModel = list.get(i);
                String name = backofficeModel.getAccount();
                String date = backofficeModel.getDate();
                double ammount = Double.parseDouble(backofficeModel.getAmmount());

                TextView dateText = listView.findViewById(R.id.date);
                TextView balanceText = listView.findViewById(R.id.balance);
                TextView infoText = listView.findViewById(R.id.infoText);
//                TextView billNo = listView.findViewById(R.id.billNo);

                String delimiter = "BillNo:";
                int position = name.indexOf(delimiter);

                String[] billParts = name.split(delimiter);
                String[] text = name.split(delimiter);

                String billKey = "";
                String billValue = "";
                if (billParts.length == 2) {
                    billKey = delimiter.trim();
                    billValue = billParts[1].trim();
                }

                SpannableString spannableString = new SpannableString(name);
                if (position > 0) {
                    spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.centerHaldfMoonColor)), position, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                infoText.setText(spannableString);
                dateText.setText(date);
//                billNo.setText(billKey + " " + billValue);

                if (i == 0) {
                    openingBalance.setText(formatedAmt(ammount));
                    continue;
                } else if (i == list.size() - 1) {
                    closingBalance.setText(formatedAmt(ammount));
                } else if (i == 1) {
                    balanceText.setText(formatedAmt(ammount));
                    double bal = Double.parseDouble(list.get(i).getAmmount()) +
                            Double.parseDouble(list.get(i - 1).getAmmount());
                    balance = bal;
                    if (i < (list.size() - 1) && date.equalsIgnoreCase(list.get(i + 1).getDate())) {
                        balanceText.setText("");
                    } else {
                        balanceText.setText(formatedAmt(balance));
                    }
                } else {
                    balanceText.setText(formatedAmt(ammount));
                    balance = balance + ammount;
                    if (i < (list.size() - 1) && date.equalsIgnoreCase(list.get(i + 1).getDate())) {
                        balanceText.setText("");
                        if (i == list.size() - 1) {
                            balanceText.setText(formatedAmt(balance));
                        }
                    } else {
                        balanceText.setText(formatedAmt(balance));
                    }
                }
                if (balanceText.getText().toString().contains("Cr")) {
                    balanceText.setTextColor(ScreenColor.GREEN);
                } else {
                    balanceText.setTextColor(ScreenColor.RED);
                }

                String finalBillValue = billValue;
                listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!finalBillValue.equals("")) {
                            Fragment m_fragment = LedgerBillDetails.newInstance(finalBillValue);

                            new Handler().postDelayed(() ->
                                            getParentFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.openFrag ,m_fragment)
                                                    .addToBackStack(null)
                                                    .commit(),
                                    250);

//                            new Handler().postDelayed(() -> GlobalClass.fragmentTransaction(
//                                    m_fragment, R.id.container_body, true, ""), 250);
                        }
                    }
                });

                backofficeList.addView(listView);
            }
        } else {
            GlobalClass.showAlertDialog(Constants.backofficeServer);
        }
    }

    private String amountType = "Rs";

    private String formatedAmt(double amt) {
        try {
            DecimalFormat df = null;
            double fValue = 0.00;
            if (amountType.equalsIgnoreCase("Lacs")) {
                df = new DecimalFormat("##,##,##0.00");
                fValue = (double) ((double) amt / 100000);
            } else if (amountType.equalsIgnoreCase("Cr")) {
                df = new DecimalFormat("##,##,##0.00");
                fValue = (double) ((double) amt / 10000000);
            } else {
                df = new DecimalFormat("##,##,##0");
                fValue = (double) amt;
            }
            String str = df.format(fValue);
            if (str.equals("0")) {
                return str;
            } else if (fValue > 0) {
                str = str + " Dr";
            } else if (fValue < 0) {
                str = str + " Cr";
            }
            return str.replace("-", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void initSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item, getResources().getStringArray(R.array.backofficeAmtType));
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        backoffice_spinner.setAdapter(arrayAdapter);
        backoffice_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amountType = backoffice_spinner.getSelectedItem().toString();
//                if (list != null) addData();
                if (list != null) addData2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter arrayAdapterEntry = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item, getResources().getStringArray(R.array.backofficeEntryType));
        arrayAdapterEntry.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        backoffice_entryspinner.setAdapter(arrayAdapterEntry);
        backoffice_entryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedEntry = eLedgerEntryType.TEN;
                        new SendLedgerReq().execute();
                        break;
                    case 1:
                        selectedEntry = eLedgerEntryType.TWENTRY;
                        new SendLedgerReq().execute();
                        break;
                    case 2:
                        selectedEntry = eLedgerEntryType.LASTMONTH;
                        new SendLedgerReq().execute();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        BackofficeXmlParser xmlParser = new BackofficeXmlParser();
        StringBuilder htmlString = new StringBuilder();
        try {
            stream = downloadUrl(urlString);
            list = xmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return htmlString.toString();
    }


    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    private class SendLedgerReq extends AsyncTask<Void, Void, Void> {

        SendLedgerReq() {
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
        protected Void doInBackground(Void... voids) {
            new SendDataToBCServer().sendLedgerReq(selectedEntry.value);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class LedgerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case LEDGER_DATA:
                                handleResponse(refreshBundle.getByteArray(eForHandler.RESDATA.name));
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResponse(byte[] bytes) {
        GlobalClass.dismissdialog();
        try {
            StructLedgerResp lr = new StructLedgerResp(bytes);
            GlobalClass.log("LedgerData IsError :" + lr.isError.getValue() + " Norow:" + lr.noOfRecs.getValue());
            if (lr.isError.getValue() || lr.noOfRecs.getValue() == 0) {
                callFromAPI();
            } else {
                backoffice_entryspinner.setVisibility(View.VISIBLE);
                list = new ArrayList<>();
                for (int i = 0; i < lr.noOfRecs.getValue(); i++) {
                    BackofficeModel backofficeModel = new BackofficeModel();
                    StructLedger ledger = lr.ledgersDetail[i];
                    backofficeModel.setDate(ledger.date.getValue());
                    backofficeModel.setAccount(ledger.account.getValue());
                    if (ledger.credit.getValue() == 0 && ledger.debit.getValue() == 0) {
                        backofficeModel.setAmmount((ledger.balance.getValue()) + "");
                    } else {
                        backofficeModel.setAmmount((ledger.credit.getValue() + ledger.debit.getValue()) + "");
                    }
                    backofficeModel.setBalance(ledger.balance.getValue() + "");
                    list.add(backofficeModel);
                }
//                addData();
                addData2();
            }
        } catch (Exception e) {
            e.printStackTrace();
            GlobalClass.showToast(getContext(), "Please try after sometime");
        }
    }
}
