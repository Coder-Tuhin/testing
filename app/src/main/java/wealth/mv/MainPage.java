package wealth.mv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.NumberFormat;

import com.ventura.venturawealth.R;

import Structure.Request.BC.ErrorLOG;
import Structure.Response.AuthRelated.ClientLoginResponse;
import connection.SendDataToBCServer;
import enums.eLogType;
import enums.eScreen;
import utils.Constants;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;
import wealth.new_mutualfund.MutualFundMenuNew;
import wealth.wealthStructure.StructBondEquityDepositoryDetailNew;
import wealth.wealthStructure.StructMyWealth;

@SuppressWarnings("ALL")
public class MainPage extends Fragment implements View.OnClickListener {

    private final String TAG = MainPage.class.getName();
    TextView txtNetWorthValue;
    String myBalanceValue[] = new String[5];
    String myMarginValue[] = new String[4];
    String myfixedIncome[] = new String[2];
    StructMyWealth grandTot = null;
    static int holdingSpinnerIndex;
    static boolean holdingSpinnerFlag;

    @SuppressWarnings("UseOfObsoleteCollectionType")
    private String options[] = {"Balances", "Margin", "Holdings", "Mutual Funds", "Fixed Income"};
    private String totalValue[] = new String[6];
    private Button btnValue[];
    private TextView txtTitleAmount;
    private Context context;
    private NumberFormat formatter;
    private Typeface verdanaType, verdanaTypeBold;
    private TableLayout frmlayout;
    private TableRow trPerSchmes, trImg;
    private int width, height;
    private Spinner spinner;
    private View myFragmentView;
    private boolean reqAvl = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            try {
                ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
                ((Activity) getActivity()).findViewById(R.id.home_relative).setVisibility(View.VISIBLE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (myFragmentView == null) {

                myFragmentView = inflater.inflate(R.layout.mainlayout, container, false);
                reqAvl = true;
                if (getTaskFirst == null) {
                    if (UserSession.getClientResponse().isNewWealthAvailable()) {
                        getTaskFirst = new GetTaskFirst(false);
                    } else {
                        if (UserSession.getClientResponse().isNeedAccordLogin()) {
                            getTaskFirst = new GetTaskFirst(true);
                        } else {
                            getTaskFirst = new GetTaskFirst(false);
                        }
                    }
                    getTaskFirst.execute();
                } else {
                    setUI();
                }
            }
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, "MyWealth", UserSession.getLoginDetailsModel().getUserID());

            return myFragmentView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GetTaskFirst getTaskFirst = null;

    @Override
    public void onPause() {
        super.onPause();
        reqAvl = false;
        if (getTaskFirst != null) {
            getTaskFirst.cancel(true);
        }
        GlobalClass.dismissdialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        StructBondEquityDepositoryDetailNew holdingData = VenturaServerConnect.getHoldingData();
        if (holdingData != null) {
            setUI();
        }
    }

    void setUI() {
        try {
            context = getActivity();
            formatter = VenturaServerConnect.getDecimalFormat();
            width = this.getResources().getDisplayMetrics().widthPixels;
            height = this.getResources().getDisplayMetrics().heightPixels;
            verdanaType = Typeface.DEFAULT;
            verdanaTypeBold = Typeface.DEFAULT_BOLD;
            setMyBalance();
            display();
            if (GlobalClass.deeplinkScreen > 0) {
                eScreen screenType = eScreen.fromValue(GlobalClass.deeplinkScreen);
                GlobalClass.deeplinkScreen = -1;
                try {
                    ErrorLOG log = new ErrorLOG();
                    log.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                    log.errorMsg.setValue(screenType.name);
                    log.logType.setValue(eLogType.DEEPLINK.name);
                    SendDataToBCServer sendDataToServer = new SendDataToBCServer();
                    sendDataToServer.sendAndroidLog(log);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (screenType != eScreen.NONE) {
                    GlobalClass.showScreen(screenType);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setMyBalance() {
        try {
            for (int i = 0; i < totalValue.length; i++) {
                totalValue[i] = "";
            }
            for (int i = 0; i < myBalanceValue.length; i++) {
                myBalanceValue[i] = "0";
            }
            for (int i = 0; i < myMarginValue.length; i++) {
                myMarginValue[i] = "0";
            }
            for (int i = 0; i < myfixedIncome.length; i++) {
                myfixedIncome[i] = "0";
            }
            try {
                grandTot = VenturaServerConnect.getGrandTotalValue(false);
                StructBondEquityDepositoryDetailNew holdingData = VenturaServerConnect.getHoldingData();
                if (holdingData != null) {
                    holdingData.calculateTotalGainLoss();
                    grandTot.totalHolding.setValue(holdingData.getTotalCurrentValue());
                }
                if (grandTot.isAllDataCame.getValue()) {
                    totalValue[0] = longToString((long) grandTot.totalBalace.getValue());
                    totalValue[1] = "" + longToString((long) grandTot.totalMargin.getValue());
                    totalValue[2] = "" + longToString((long) grandTot.totalHolding.getValue());
                    totalValue[3] = "" + longToString((long) grandTot.totalMF.getValue());
                    totalValue[4] = "" + longToString((long) grandTot.totalFixedIncome.getValue());
                    totalValue[5] = "" + longToString((long) grandTot.getGrandTotal());
                } else {
                    for (int i = 0; i < totalValue.length; i++) {
                        totalValue[i] = "\u003F";
                    }
                    String error = grandTot.msg.getValue();
                    if (error.toLowerCase().contains("session")) {
                        showConfirmDialog("Session expired" + ", tap on YES to refresh.");
                    } else if (grandTot.isShowPertialData.getValue()) {
                        totalValue[0] = longToString((long) grandTot.totalBalace.getValue());
                        totalValue[1] = "" + longToString((long) grandTot.totalMargin.getValue());
                        totalValue[2] = "" + longToString((long) grandTot.totalHolding.getValue());
                        totalValue[3] = "" + (grandTot.totalMF.getValue() >= 0 ? longToString((long) grandTot.totalMF.getValue()) : "\u003F");
                        totalValue[4] = "" + (grandTot.totalFixedIncome.getValue() >= 0 ? longToString((long) grandTot.totalFixedIncome.getValue()) : "\u003F");
                        totalValue[5] = "" + ("\u003F");
                    } else {
                        showErrorDialog("Something went wrong. Please try after sometime.");
                        return;
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
            try {
                myBalanceValue[0] = "" + longToString((long) (grandTot.cashBalance.getValue() + grandTot.currBalance.getValue() + grandTot.fnoBalance.getValue()));
                myBalanceValue[1] = "" + longToString((long) grandTot.commBalance.getValue());
                myBalanceValue[2] = "" + longToString((long) grandTot.fnoBalance.getValue());
                myBalanceValue[3] = "" + longToString((long) grandTot.currBalance.getValue());
                myBalanceValue[4] = "" + longToString((long) grandTot.totalBalace.getValue());
            } catch (Exception e) {
                e.getMessage();
            }
            try {
                if (grandTot.totalFixedIncome.getValue() >= 0) {
                    myfixedIncome[0] = "" + longToString((long) grandTot.bondValue.getValue());
                    myfixedIncome[1] = "" + longToString((long) grandTot.fixedDeposit.getValue());
                }
            } catch (Exception e) {
                e.getMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlertDialog errorDialog;

    private void showErrorDialog(String msg) {
        try {
            if (errorDialog != null) {
                errorDialog.dismiss();
                errorDialog = null;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            GlobalClass.fragmentManager.popBackStackImmediate();
                        }
                    });
            errorDialog = builder.create();
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showConfirmDialog(String msg) {
        try {
            if (errorDialog != null) {
                errorDialog.dismiss();
                errorDialog = null;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //GlobalClass.fragmentManager.popBackStackImmediate();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            getTaskFirst = new GetTaskFirst(true);
                            getTaskFirst.execute();
                        }
                    });
            errorDialog = builder.create();
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void display() {
        try {
            int tabBackCol = Color.BLACK;
            String txtNetV = "";
            String txtTitltAm = "";

            if (VenturaServerConnect.rsin.equals("Rs.lacs")) {
                txtNetV = "(Rs.lacs) " + totalValue[5];
                txtTitltAm = " Amount (Rs.lacs) ";

            } else if (VenturaServerConnect.rsin.equals("Rs.cr")) {
                txtNetV = "(Rs.cr) " + totalValue[5];
                txtTitltAm = " Amount (Cr) ";
            } else {
                txtNetV = "(Rs.) " + totalValue[5];
                txtTitltAm = " Amount (Rs.) ";
            }

            txtNetWorthValue = (TextView) myFragmentView.findViewById(R.id.my_wealth_value);
            txtNetWorthValue.setText(txtNetV + " ");
            txtNetWorthValue.setOnClickListener(null);
            if (totalValue[5].equalsIgnoreCase("\u003F")) {
                txtNetWorthValue.setOnClickListener(errorClickListener);
            }
            txtTitleAmount = (TextView) myFragmentView.findViewById(R.id.amount_value);
            txtTitleAmount.setTextColor(Color.GRAY);

            ((TextView) myFragmentView.findViewById(R.id.particulars)).setTextColor(Color.GRAY);
            ((TextView) myFragmentView.findViewById(R.id.btn_val_balances)).setText(totalValue[0]);
            myFragmentView.findViewById(R.id.layout_balances).setOnClickListener(null);
            if (!totalValue[0].equalsIgnoreCase("\u003F")) {
                if (Double.parseDouble(totalValue[0].replaceAll(",", "")) != 0.00) {
                    myFragmentView.findViewById(R.id.layout_balances).setOnClickListener(buttonClickListener);
                }
            } else {
                myFragmentView.findViewById(R.id.layout_balances).setOnClickListener(errorClickListener);
            }

            ((TextView) myFragmentView.findViewById(R.id.btn_val_margin)).setText(totalValue[1]);
            myFragmentView.findViewById(R.id.layout_margin).setOnClickListener(null);
            if (!totalValue[1].equalsIgnoreCase("\u003F")) {
                if (Double.parseDouble(totalValue[1].replaceAll(",", "")) > 0.00) {
                    myFragmentView.findViewById(R.id.layout_margin).setOnClickListener(buttonClickListener);
                }
            } else {
                myFragmentView.findViewById(R.id.layout_margin).setOnClickListener(errorClickListener);
            }

            ((TextView) myFragmentView.findViewById(R.id.btn_val_holdings)).setText(totalValue[2]);
            myFragmentView.findViewById(R.id.layout_holdings).setOnClickListener(null);
            if (!totalValue[2].equalsIgnoreCase("\u003F")) {
                if (Double.parseDouble(totalValue[2].replaceAll(",", "")) > 0.00) {
                    myFragmentView.findViewById(R.id.layout_holdings).setOnClickListener(buttonClickListener);
                }
            } else {
                myFragmentView.findViewById(R.id.layout_holdings).setOnClickListener(errorClickListener);
            }

            ((TextView) myFragmentView.findViewById(R.id.btn_val_mutual_funds)).setText(totalValue[3]);
            myFragmentView.findViewById(R.id.layout_mutual_funds).setOnClickListener(null);
            if (!totalValue[3].equalsIgnoreCase("\u003F")) {
                if (Double.parseDouble(totalValue[3].replaceAll(",", "")) >= 0.00) {
                    myFragmentView.findViewById(R.id.layout_mutual_funds).setOnClickListener(buttonClickListener);
                }
            } else {
                myFragmentView.findViewById(R.id.layout_mutual_funds).setOnClickListener(errorClickListener);
            }

            ((TextView) myFragmentView.findViewById(R.id.btn_val_fixed_income)).setText(totalValue[4]);
            myFragmentView.findViewById(R.id.layout_fixed_income).setOnClickListener(null);
            if (!totalValue[4].equalsIgnoreCase("\u003F")) {
                if (Double.parseDouble(totalValue[4].replaceAll(",", "")) > 0.00) {
                    myFragmentView.findViewById(R.id.layout_fixed_income).setOnClickListener(buttonClickListener);
                }
            } else {
                myFragmentView.findViewById(R.id.layout_fixed_income).setOnClickListener(errorClickListener);
            }

            if (Double.parseDouble(myBalanceValue[0].replaceAll(",", "")) != 0.00) {
                ((TextView) myFragmentView.findViewById(R.id.btn_val_cash)).setText(myBalanceValue[0]);
            }
            if (Double.parseDouble(myBalanceValue[1].replaceAll(",", "")) != 0.00) {
                ((TextView) myFragmentView.findViewById(R.id.btn_val_commodity)).setText(myBalanceValue[1]);
            }
            if (Double.parseDouble(myfixedIncome[0].replaceAll(",", "")) > 0.00) {
                myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_one).setOnClickListener(buttonClickListener);
                ((TextView) myFragmentView.findViewById(R.id.btn_val_bonds)).setText(myfixedIncome[0]);
            }
            if (Double.parseDouble(myfixedIncome[1].replaceAll(",", "")) > 0.00) {
                myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_two).setOnClickListener(buttonClickListener);
                ((TextView) myFragmentView.findViewById(R.id.btn_val_deposits)).setText(myfixedIncome[1]);
            }
            setRupee();

            String lastUpdateTime = grandTot.getLastUpdateTime();
            if (!lastUpdateTime.equalsIgnoreCase("")) {
                String textLastUpdate = "<font color=#cfcfcf>Last Update Time : </font> <font color=#FFFFFF>" + lastUpdateTime + "</font>";
                ((TextView) myFragmentView.findViewById(R.id.lastupdate_wealth)).setText(Html.fromHtml(textLastUpdate));
            } else {
                ((TextView) myFragmentView.findViewById(R.id.lastupdate_wealth)).setText("");
            }
            String nextUpdateTime = grandTot.getNextUpdateTime();
            if (!nextUpdateTime.equalsIgnoreCase("")) {
                String textNextUpdate = "<font color=#cfcfcf>Next Update Time : </font> <font color=#FFFFFF>" + nextUpdateTime + "</font>";
                ((TextView) myFragmentView.findViewById(R.id.nextupdate_wealth)).setText(Html.fromHtml(textNextUpdate));
            } else {
                ((TextView) myFragmentView.findViewById(R.id.nextupdate_wealth)).setText("");
            }
            if (!grandTot.isAllDataCame.getValue()) {
                ((TextView) myFragmentView.findViewById(R.id.errornote)).setText("Note: Something appears to have gone wrong. Tap here to refresh");
                myFragmentView.findViewById(R.id.errornote).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getTaskFirst = new GetTaskFirst(true);
                        getTaskFirst.execute();
                    }
                });
            } else {
                ((TextView) myFragmentView.findViewById(R.id.errornote)).setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRupee() {
        try {
            setMyBalance();
            String txtNetV = "";
            String txtTitltAm = "";
            if (VenturaServerConnect.rsin.equals("Rs.lacs")) {
                txtNetV = "(Rs.lacs) " + totalValue[5];
                txtTitltAm = " Amount (Rs.lacs) ";

            } else if (VenturaServerConnect.rsin.equals("Rs.cr")) {
                txtNetV = "(Rs.cr) " + totalValue[5];
                txtTitltAm = " Amount (Rs.cr) ";
            } else {
                txtNetV = "(Rs.) " + totalValue[5];
                txtTitltAm = " Amount (Rs.) ";
            }
            if (txtNetWorthValue != null) {
                txtNetWorthValue.setText(txtNetV + "  ");
            }
            if (txtTitleAmount != null) {
                txtTitleAmount.setText(txtTitltAm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    View.OnClickListener errorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                showConfirmDialog("Oops! Something appears to have gone wrong." + " Tap on YES to refresh.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                if (v instanceof LinearLayout) {
                    if (v.getId() == R.id.layout_balances) {
                        if (myFragmentView.findViewById(R.id.layout_balances_inner_detail_one).getVisibility() == View.VISIBLE) {
                            ((TextView) myFragmentView.findViewById(R.id.btn_add_balances)).setText("+");
                            myFragmentView.findViewById(R.id.layout_balances_inner_detail_one).setVisibility(View.GONE);
                            myFragmentView.findViewById(R.id.layout_balances_inner_detail_two).setVisibility(View.GONE);
                        } else {
                            ((TextView) myFragmentView.findViewById(R.id.btn_add_balances)).setText("-");
                            myFragmentView.findViewById(R.id.layout_balances_inner_detail_one).setVisibility(View.VISIBLE);
                            myFragmentView.findViewById(R.id.layout_balances_inner_detail_two).setVisibility(View.VISIBLE);
                        }
                        ((TextView) myFragmentView.findViewById(R.id.btn_add_fixed_income)).setText("+");
                        myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_one).setVisibility(View.GONE);
                        myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_two).setVisibility(View.GONE);

                    } else if (v.getId() == R.id.layout_fixed_income) {

                        if (myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_one).getVisibility() == View.VISIBLE) {
                            ((TextView) myFragmentView.findViewById(R.id.btn_add_fixed_income)).setText("+");
                            myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_one).setVisibility(View.GONE);
                            myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_two).setVisibility(View.GONE);
                        } else {
                            ((TextView) myFragmentView.findViewById(R.id.btn_add_fixed_income)).setText("-");
                            myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_one).setVisibility(View.VISIBLE);
                            myFragmentView.findViewById(R.id.layout_fixed_income_inner_detail_two).setVisibility(View.VISIBLE);
                        }
                        ((TextView) myFragmentView.findViewById(R.id.btn_add_balances)).setText("+");
                        myFragmentView.findViewById(R.id.layout_balances_inner_detail_one).setVisibility(View.GONE);
                        myFragmentView.findViewById(R.id.layout_balances_inner_detail_two).setVisibility(View.GONE);

                    } else if (v.getId() == R.id.layout_margin) {
                        callIntent(1);
                    } else if (v.getId() == R.id.layout_holdings) {
                        callIntent(2);
                    } else if (v.getId() == R.id.layout_mutual_funds) {
                        callIntent(3);
                    } else if (v.getId() == R.id.layout_fixed_income_inner_detail_one) {
                        callIntent(4);
                    } else if (v.getId() == R.id.layout_fixed_income_inner_detail_two) {
                        callIntent(5);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static String longToString(long value) {
        try {
            return VenturaServerConnect.valueToString(value);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void showDialog(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == 1) {
                VenturaServerConnect.rsin = "Rs";
            } else if (item.getItemId() == 2) {
                VenturaServerConnect.rsin = "Rs.lacs";
            } else if (item.getItemId() == 3) {
                VenturaServerConnect.rsin = "Rs.cr";
            }
            setRupee();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    void callIntent(int flag) {
        try {
            Fragment myFrag = null;
            if (flag == 1) {
                myFrag = new MarginMenu();
            } else if (flag == 2) {
                myFrag = new HoldingMenuNew();
            } else if (flag == 3) {
                myFrag = new MutualFundMenuNew();
            } else if (flag == 4) {
                myFrag = new BondM();
            } else if (flag == 5) {
                myFrag = new FixedDepositMenu();
            }
            GlobalClass.fragmentTransaction(myFrag, R.id.container_body, true, "mywealth" + flag);
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    class GetTaskFirst extends AsyncTask<Object, Void, String> {
        String str = "";
        boolean isNeedLogin = false;

        public GetTaskFirst(boolean _isNeedLogin) {
            isNeedLogin = _isNeedLogin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting Balances...");
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if (isNeedLogin) {
                    VenturaServerConnect.closeSocket();
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                        if (VenturaServerConnect.connectToWealthServer(true)) {
                            VenturaServerConnect.getGrandTotalValue(true);
                        } else {
                            return "Oops! Something went wrong. Please try after some time.";
                        }
                    } else {
                        return clientLoginResponse.charResMsg.getValue();
                    }
                } else {
                    if (VenturaServerConnect.connectToWealthServer(false)) {
                        if (!reqAvl) return "";
                        VenturaServerConnect.getGrandTotalValue(true);
                    } else {
                        return "Oops! Something went wrong. Please try after some time.";
                    }
                }
            } catch (Exception ie) {
                str = ie.toString();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result.equalsIgnoreCase("")) {
                    setUI();
                    GlobalClass.dismissdialog();
                } else {
                    GlobalClass.dismissdialog();
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}