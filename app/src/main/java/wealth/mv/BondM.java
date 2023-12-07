package wealth.mv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import Structure.Response.AuthRelated.ClientLoginResponse;
import enums.eLogType;
import utils.Constants;
import utils.Formatter;
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import wealth.Dialogs;
import wealth.FontTextView;
import wealth.VenturaServerConnect;
import wealth.wealthStructure.StructBondEquityDepositoryDetail;
import wealth.wealthStructure.StructBondEuityDepositoryRow;
import wealth.wealthStructure.StructPhysicalDepositoryDetail;
import wealth.wealthStructure.StructPhysicalDepositoryRow;


/**
 * Created by Admin on 22/05/14.
 */
public class BondM extends Fragment {
    int width, height;
    TableLayout table_bonds;
    LinearLayout main;
    int[] intWidthArray;
    String[] stringTitleExtensionArray;
    Typeface verdanaType, verdanaTypeBold;
    TableRow.LayoutParams params;
    int code;
    StructBondEquityDepositoryDetail depositoryDetail;
    StructPhysicalDepositoryDetail physicalDetail;

    String selectedtab = "";
    int iNetWorthColor = Color.WHITE;
    int iParticularBackColor = Color.rgb(128, 128, 128);
    String txtTitltAm = "";
    StructBondEuityDepositoryRow[] equityrows;
    private String[] arrBonds = {"Depository", "Physical"};
    private View myFragmentView;
    private Activity mActivity;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        try {
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

            myFragmentView = inflater.inflate(R.layout.myventura_report_view, container, false);
            myFragmentView.findViewById(R.id.layout_extratitle).setVisibility(View.GONE);
            myFragmentView.findViewById(R.id.colletral_spinner).setVisibility(View.GONE);
            context = mActivity.getApplicationContext();
            setHasOptionsMenu(true);

            verdanaType = Typeface.DEFAULT;
            verdanaTypeBold = Typeface.DEFAULT_BOLD;

            width = ScreenColor.getScreenWidth(context);
            height = ScreenColor.getScreenHeight(context);

            main = (LinearLayout) myFragmentView.findViewById(R.id.data_layout);
            main.removeAllViews();

            Spinner groupSpinner = (Spinner) myFragmentView.findViewById(R.id.layout_spinner).findViewById(R.id.spn);
            ArrayAdapter<String> Adapter = new ArrayAdapter(context, R.layout.custom_spinner_label, arrBonds);
            Adapter.setDropDownViewResource(R.layout.custom_spinner_drop_down);
            groupSpinner.setAdapter(Adapter);

            groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    String selectedItem = adapterView.getSelectedItem().toString();
                    new GetTask(selectedItem).execute();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return myFragmentView;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void addTitle_Depository() {

        try {
            String[] stringTitleArray = {"Company Name", "Qty", "PurPrice", "CMP", "CurVal", "Gain/Loss"};
            intWidthArray = new int[]{ViewGroup.LayoutParams.WRAP_CONTENT, (width / 8) + 2, (width / 5), (width / 5) - 26, (width / 4) - 4, (width / 4)};

            int iTitleTextColor = ScreenColor.iTableHeaderTextColor;

            params = new TableRow.LayoutParams();
            params.span = 5;

            TableLayout table1 = (TableLayout) myFragmentView.findViewById(R.id.tb_layout_title);
            table1.removeAllViews();

            TableRow rowDayLabels0 = new TableRow(context);
            rowDayLabels0.setPadding(1, 1, 1, 1);
            rowDayLabels0.setBackgroundColor(ScreenColor.iTableHeaderBackColor);

            TableRow rowDayLabels = new TableRow(context);
            rowDayLabels.setPadding(1, 1, 1, 1);
            rowDayLabels.setBackgroundColor(ScreenColor.iTableHeaderBackColor);

            FontTextView title = new FontTextView(context, iTitleTextColor, stringTitleArray[0], Gravity.LEFT, intWidthArray[0], verdanaType);
            title.setId(R.id.title_id);
            title.setClickable(true);
            rowDayLabels0.addView(title, params);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetTitle();
                    if (view.getId() == R.id.title_id) {
                        equityrows = depositoryDetail.getSortedString(0, view.getId());
                        ((FontTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                        addRow_Depository();
                        view.setId(0);
                    } else {
                        equityrows = depositoryDetail.getSortedString(0, view.getId());
                        ((FontTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                        addRow_Depository();
                        view.setId(R.id.title_id);
                    }
                }
            });
            title = new FontTextView(context, iTitleTextColor, stringTitleArray[1], Gravity.RIGHT, intWidthArray[1], verdanaType);
            title.setId(R.id.title_id);
            title.setClickable(true);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            rowDayLabels.addView(title);

            title = new FontTextView(context, iTitleTextColor, stringTitleArray[2], Gravity.RIGHT, intWidthArray[2], verdanaType);
            title.setId(R.id.title_id);
            title.setClickable(true);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.title_id) {

                    }
                }
            });
            rowDayLabels.addView(title);

            title = new FontTextView(context, iTitleTextColor, stringTitleArray[3], Gravity.RIGHT, intWidthArray[3], verdanaType);
            title.setId(R.id.title_id);
            title.setClickable(true);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.title_id) {

                    }
                }
            });
            rowDayLabels.addView(title);

            title = new FontTextView(context, iTitleTextColor, stringTitleArray[4], Gravity.RIGHT, intWidthArray[4], verdanaType);
            title.setClickable(true);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetTitle();
                    if (view.getId() == R.id.title_id) {
                        equityrows = depositoryDetail.getSortedString(4, view.getId());
                        ((FontTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                        addRow_Depository();
                        view.setId(0);
                    } else {
                        equityrows = depositoryDetail.getSortedString(4, view.getId());
                        ((FontTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                        addRow_Depository();
                        view.setId(R.id.title_id);
                    }
                }
            });
            rowDayLabels.addView(title);

            title = new FontTextView(context, iTitleTextColor, stringTitleArray[5], Gravity.RIGHT, intWidthArray[5], verdanaType);
            title.setId(R.id.title_id);

            title.setClickable(true);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetTitle();
                    if (view.getId() == R.id.title_id) {
                        equityrows = depositoryDetail.getSortedString(5, view.getId());
                        ((FontTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_up_float, 0);
                        addRow_Depository();
                        view.setId(0);
                    } else {
                        equityrows = depositoryDetail.getSortedString(5, view.getId());
                        ((FontTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
                        addTitle_Depository();
                        view.setId(R.id.title_id);
                    }
                }
            });
            rowDayLabels.addView(title);

            table1.addView(rowDayLabels0);
            table1.addView(rowDayLabels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void resetTitle() {
        TableLayout table1 = (TableLayout) myFragmentView.findViewById(R.id.tb_layout_title);
        if (table1.getChildCount() > 0) {
            for (int i = 0; i < table1.getChildCount(); i++) {
                TableRow row = (TableRow) table1.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    ((FontTextView) row.getChildAt(j)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                }
            }
        }
    }

    public void addRow_Depository() {

        try {

            main.removeAllViewsInLayout();
            equityrows = depositoryDetail.getBondEquityDepositoryRows();

            int iRowValueTextColor = ScreenColor.iTableRowTextColor;


            for (int i = 0; i < depositoryDetail.getNoOfRow(); i++) {

                TableLayout table2 = new TableLayout(context);

                StructBondEuityDepositoryRow equityrow = equityrows[i];

                TableRow row0 = new TableRow(context);
                row0.setPadding(1, 10, 1, 1);

                TableRow row = new TableRow(context);
                row.setPadding(1, 1, 1, 10);
                int backColor;
                if (i % 2 == 0) {
                    backColor = ScreenColor.iTableRowOneBackColor;
                } else {
                    backColor = ScreenColor.iTableRowTwoBackColor;//Color.rgb(255, 239, 239);
                }

                row0.setBackgroundColor(backColor);
                row.setBackgroundColor(backColor);

                TextView highsLabel = new TextView(context);
                highsLabel.setTypeface(verdanaType);

                if (equityrow.getCompanyName().equals("Total")) {
                    highsLabel.setText("");
                } else {
                    highsLabel.setText(equityrow.getCompanyName() + "");
                }
                highsLabel.setGravity(Gravity.LEFT);
                highsLabel.setTextColor(iRowValueTextColor);
                highsLabel.setWidth(intWidthArray[0]);
                row0.addView(highsLabel, params);

                TextView day1High = new TextView(context);
                if (equityrow.getCompanyName().equals("Total")) {
                    day1High.setText(equityrow.getCompanyName() + "");
                    day1High.setTypeface(verdanaTypeBold);
                } else {
                    day1High.setText(equityrow.getQty() + "");
                    day1High.setTypeface(verdanaType);
                }
                day1High.setGravity(Gravity.RIGHT);

                day1High.setTextColor(iRowValueTextColor);
                day1High.setWidth(intWidthArray[1]);
                row.addView(day1High);

                TextView day2High = new TextView(context);
                String value = "";
                if (equityrow.getAvgPurchasePrice() == 0) {
                    value = "$";
                } else {
                    value = Formatter.toTwoDecimalValue(equityrow.getAvgPurchasePrice());

                }
                day2High.setTextColor(iRowValueTextColor);
                //}
                if (equityrow.getCompanyName().equals("Total")) {
                    value = "";
                }
                day2High.setText(value + "");
                day2High.setTypeface(verdanaType);
                day2High.setGravity(Gravity.RIGHT);
                day2High.setWidth(intWidthArray[2]);
                row.addView(day2High);

                TextView day4High = new TextView(context);
                value = Formatter.toTwoDecimalValue(equityrow.getCMP());

                if (equityrow.getCompanyName().equals("Total")) {
                    value = "";
                }
                day4High.setTextColor(iRowValueTextColor);
                //}
                day4High.setText(value + "");
                day4High.setTypeface(verdanaType);
                day4High.setGravity(Gravity.RIGHT);

                day4High.setWidth(intWidthArray[3]);
                row.addView(day4High);

                TextView day5High = new TextView(context);
                value = Formatter.toTwoDecimalValue(equityrow.getCurrentValue());
                day5High.setText(value + "");
                day5High.setTextColor(iRowValueTextColor);
                //}
                if (equityrow.getCompanyName().equals("Total")) {
                    day5High.setTypeface(verdanaTypeBold);
                } else {
                    day5High.setTypeface(verdanaType);
                }
                day5High.setGravity(Gravity.RIGHT);

                day5High.setWidth(intWidthArray[4]);

                row.addView(day5High);


                TextView day6High = new TextView(context);
                if (equityrow.getGainLoss() == 0) {
                    value = "$";
                    day6High.setTextColor(iRowValueTextColor);
                } else {
                    value = Formatter.toTwoDecimalValue(equityrow.getGainLoss());
                    if (value.startsWith("-")) {
                        value = value.substring(1);
                        day6High.setTextColor(Color.RED);
                    } else {
                        day6High.setTextColor(ScreenColor.positiveColor);
                    }
                }

                if (equityrow.getCompanyName().equals("Total")) {
                    value = "";
                }
                day6High.setText(value + "");
                day6High.setTypeface(verdanaType);
                day6High.setGravity(Gravity.RIGHT);

                day6High.setWidth(intWidthArray[5]);
                day6High.setPadding(0, 0, 10, 0);

                row.addView(day6High);

                table2.addView(row0);
                table2.addView(row);

                if (ScreenColor.isGridLineShow) {
                    TableRow grid = new TableRow(context);
                    grid.setBackgroundColor(ScreenColor.iTableGridColor);
                    grid.setMinimumHeight(ScreenColor.seperatorHeight);
                    table2.addView(grid);
                }

                // if (!equityrow.getCompanyName().equals("Total")) {
                main.addView(table2);
                //  }else{
                // ((LinearLayout)myFragmentView.findViewById(R.id.layout_total)).removeAllViewsInLayout();
                // ((LinearLayout)myFragmentView.findViewById(R.id.layout_total)).addView(table2);
                // main.addView(table2);
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void depositoryMethod() {
        try {
            main.removeAllViewsInLayout();
            if ((depositoryDetail != null) && (depositoryDetail.getNoOfRow() > 0)) {
                addTitle_Depository();
                equityrows = depositoryDetail.getBondEquityDepositoryRows();
                addRow_Depository();
            } else {
                View v = null;
                LayoutInflater li = mActivity.getLayoutInflater();
                v = li.inflate(R.layout.nodata, null);
                main.addView(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void add_TitlePhysical() {
        try {
            stringTitleExtensionArray = new String[]{"Particulars", "Amount"};
            intWidthArray = new int[]{width / 2, width / 2};

            TableLayout table1 = (TableLayout) myFragmentView.findViewById(R.id.tb_layout_title);
            table1.removeAllViewsInLayout();

            TableRow rowTitleLabels0 = new TableRow(context);
            rowTitleLabels0.setPadding(1, 1, 1, 1);
            rowTitleLabels0.setBackgroundColor(ScreenColor.iTableHeaderBackColor);


            TextView rowTitle1 = new TextView(context);
            rowTitle1.setPadding(0, 8, 0, 8);
            rowTitle1.setText(stringTitleExtensionArray[0]);
            rowTitle1.setGravity(Gravity.LEFT);
            rowTitle1.setTextColor(ScreenColor.iTableHeaderTextColor);

            rowTitle1.setWidth(intWidthArray[0]);
            rowTitleLabels0.addView(rowTitle1);

            TextView r1c3 = new TextView(context);
            r1c3.setPadding(0, 8, 0, 8);
            r1c3.setText(stringTitleExtensionArray[1]);
            r1c3.setGravity(Gravity.RIGHT);
            r1c3.setTextColor(ScreenColor.iTableHeaderTextColor);


            r1c3.setWidth(intWidthArray[1]);
            rowTitleLabels0.addView(r1c3);

            table1.addView(rowTitleLabels0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRow_Physical(String name,String amount) {

        try {
            intWidthArray = new int[]{width / 2, width / 2};

            TableLayout table1 = new TableLayout(context);

            TableRow rowDayLabels0 = new TableRow(context);
            rowDayLabels0.setPadding(1, 10, 1, 10);


            FontTextView title = new FontTextView(context, ScreenColor.textColor, name, Gravity.LEFT, intWidthArray[0], verdanaType);
            rowDayLabels0.addView(title);

            title = new FontTextView(context, ScreenColor.textColor, amount, Gravity.RIGHT, intWidthArray[1], verdanaType);
            rowDayLabels0.addView(title);

            table1.addView(rowDayLabels0);
            if (ScreenColor.isGridLineShow) {
                TableRow grid = new TableRow(context);
                grid.setBackgroundColor(ScreenColor.iTableGridColor);
                grid.setMinimumHeight(ScreenColor.seperatorHeight);
                table1.addView(grid);
            }
            main.addView(table1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void physicalMethod() {
        try {
            main.removeAllViewsInLayout();
            add_TitlePhysical();
            if(physicalDetail != null) {
                StructPhysicalDepositoryRow[] allRows = physicalDetail.getPhysicalDepositoryRows();
                for(int i=0;i<allRows.length;i++){
                    addRow_Physical(allRows[i].getCompanyName(),Formatter.toTwoDecimalValue(allRows[i].getAmount()));
                }
            }
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
        return false;
    }

    private void setRupee(String tab) {
        try {
            if (tab.equalsIgnoreCase(arrBonds[0])) {
                depositoryMethod();
            }
            if (tab.equalsIgnoreCase(arrBonds[1])) {
                physicalMethod();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            super.onAttach(activity);
            mActivity = activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            setRupee(selectedtab);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class GetTask extends AsyncTask<Object, Void, String> {
        ProgressDialog mDialog;
        String flag = "";
        String str = "";

        GetTask(String setF) {
            flag = setF;
            selectedtab = flag;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if(UserSession.getClientResponse().isNeedAccordLogin()){
                    VenturaServerConnect.closeSocket();
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    if (clientLoginResponse.charResMsg.getValue().equalsIgnoreCase("")) {
                        VenturaServerConnect.closeSocket();
                        VenturaServerConnect.connectToWealthServer(false);
                    }else{
                        return clientLoginResponse.charResMsg.getValue();
                    }
                }
                if (flag.equals(arrBonds[0])) {
                    depositoryDetail = VenturaServerConnect.getBondDepositoryDetail();
                } else if (flag.equals(arrBonds[1])) {
                    physicalDetail = VenturaServerConnect.getPhysicalDepositoryDetail();
                }
            } catch (Exception ie) {
                str = ie.toString();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                mDialog.dismiss();

                if(!result.equalsIgnoreCase("")) {
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                }else {
                    if (flag.equals(arrBonds[0])) {
                        if (depositoryDetail != null) {
                            depositoryMethod();
                        } else {
                            Dialogs.showSessionDialog(VenturaServerConnect.sessioncheck.getStatus(), context);
                        }
                    } else if (flag.equals(arrBonds[1])) {
                        if (physicalDetail != null) {
                            physicalMethod();
                        } else {
                            Dialogs.showSessionDialog(VenturaServerConnect.sessioncheck.getStatus(), context);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
