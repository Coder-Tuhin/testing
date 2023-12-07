package wealth.mv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Display;
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
import wealth.VenturaServerConnect;
import wealth.wealthStructure.StructFixedDepositDetail;
import wealth.wealthStructure.StructFixedDepositRows;


/**
 * Created by Administrator on 12/21/13.
 */
public class FixedDepositMenu extends Fragment {

    int width, height;
    LinearLayout main;
    String selectedtab = "";
    Typeface verdanaType, verdanaTypeBold;
    int[] intWidthArray;
    StructFixedDepositDetail fDeposit;
    private String[] arrdeposit = {"Snapshot", "Maturity Details"};
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

            verdanaType = Typeface.DEFAULT;//Typeface.createFromAsset(getAssets(), ScreenColor.tableFontPath);
            verdanaTypeBold = Typeface.DEFAULT_BOLD;//Typeface.createFromAsset(getAssets(), ScreenColor.tableFontPath);

            Display display = mActivity.getWindowManager().getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();

            main = (LinearLayout) myFragmentView.findViewById(R.id.data_layout);
            main.removeAllViews();

            Spinner groupSpinner = (Spinner) myFragmentView.findViewById(R.id.layout_spinner).findViewById(R.id.spn);
            ArrayAdapter<String> Adapter = new ArrayAdapter(context, R.layout.custom_spinner_label, arrdeposit);
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

    public void addTitle_Snapshot() {

        try {
            String[] stringTitleArray = {"Tenure", "Int.Rate ", "Amount"};

            intWidthArray = new int[]{width / 3, width / 3, width / 3};

            TableLayout table1 = (TableLayout) myFragmentView.findViewById(R.id.tb_layout_title);
            table1.removeAllViewsInLayout();

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 3;

            TableRow rowTitleLabels0 = new TableRow(context);
            rowTitleLabels0.setPadding(1, 1, 1, 1);
            rowTitleLabels0.setBackgroundColor(ScreenColor.iTableHeaderBackColor);

            TableRow rowTitleLabels = new TableRow(context);
            rowTitleLabels.setPadding(1, 1, 1, 1);
            rowTitleLabels.setBackgroundColor(ScreenColor.iTableHeaderBackColor);

            TextView rowTitle1 = new TextView(context);
            rowTitle1.setText("Company Name");
            rowTitle1.setGravity(Gravity.LEFT);
            rowTitle1.setTextColor(ScreenColor.iTableHeaderTextColor);
            rowTitle1.setWidth(width);
            rowTitleLabels0.addView(rowTitle1, params);

            TextView r1c3 = new TextView(context);
            r1c3.setText(stringTitleArray[0]);
            r1c3.setGravity(Gravity.RIGHT);
            r1c3.setTextColor(ScreenColor.iTableHeaderTextColor);

            r1c3.setWidth(intWidthArray[0]);
            rowTitleLabels.addView(r1c3);

            TextView r1c4 = new TextView(context);
            r1c4.setText(stringTitleArray[1]);
            r1c4.setGravity(Gravity.RIGHT);
            r1c4.setTextColor(ScreenColor.iTableHeaderTextColor);
            r1c4.setWidth(intWidthArray[1]);
            rowTitleLabels.addView(r1c4);

            TextView r1c7 = new TextView(context);
            r1c7.setText(stringTitleArray[2]);
            r1c7.setGravity(Gravity.RIGHT);
            r1c7.setTextColor(ScreenColor.iTableHeaderTextColor);
            r1c7.setPadding(0, 0, 10, 0);
            r1c7.setWidth(intWidthArray[2]);
            rowTitleLabels.addView(r1c7);

            table1.addView(rowTitleLabels0);
            table1.addView(rowTitleLabels);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRow_Snapshot() {

        try {
            StructFixedDepositRows[] depositrows = fDeposit.getFixedDepositRows();

            intWidthArray = new int[]{width / 3, width / 3, width / 3};


            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 3;

            for (int i = 0; i < fDeposit.getNoOfRow(); i++) {


                TableLayout table2 = new TableLayout(context);

                StructFixedDepositRows depositrow = depositrows[i];

                TableRow row0 = new TableRow(context);
                row0.setPadding(1, 10, 1, 2);

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
                highsLabel.setText(depositrow.getName() + " ");
                highsLabel.setGravity(Gravity.LEFT);
                highsLabel.setTypeface(verdanaType);
                highsLabel.setTextColor(ScreenColor.iTableRowTextColor);
                highsLabel.setWidth(width);
                row0.addView(highsLabel, params);

                TextView day2High = new TextView(context);
                day2High.setText(depositrow.getTenure() + " ");
                day2High.setGravity(Gravity.RIGHT);
                day2High.setTypeface(verdanaType);
                day2High.setTextColor(ScreenColor.iTableRowTextColor);
                day2High.setWidth(intWidthArray[0]);
                row.addView(day2High);

                TextView day3High = new TextView(context);
                day3High.setText(Formatter.toTwoDecimalValue(depositrow.getInterestRate()) + " ");
                day3High.setGravity(Gravity.RIGHT);
                day3High.setTypeface(verdanaType);
                day3High.setTextColor(ScreenColor.iTableRowTextColor);

                day3High.setWidth(intWidthArray[1]);
                row.addView(day3High);

                TextView day6High = new TextView(context);
                day6High.setText(VenturaServerConnect.valueToString(depositrow.getAmount()) + " ");
                day6High.setGravity(Gravity.RIGHT);
                day6High.setTextColor(ScreenColor.iTableRowTextColor);

                day6High.setWidth(intWidthArray[2]);
                day6High.setPadding(0, 0, 10, 0);
                day6High.setTypeface(verdanaType);
                row.addView(day6High);

                table2.addView(row0);
                table2.addView(row);

                if (ScreenColor.isGridLineShow) {
                    TableRow grid = new TableRow(context);
                    grid.setBackgroundColor(ScreenColor.iTableGridColor);
                    grid.setMinimumHeight(ScreenColor.seperatorHeight);
                    table2.addView(grid);
                }

                main.addView(table2);
            }

            // if(table2.getChildCount() > 0){
            TableLayout table2 = new TableLayout(context);

            TableRow row0 = new TableRow(context);
            row0.setPadding(1, 10, 1, 1);
            row0.setBackgroundColor(ScreenColor.iTableTotalRowBackColor);

            TableRow row = new TableRow(context);
            row.setPadding(1, 1, 1, 10);
            row.setBackgroundColor(ScreenColor.iTableTotalRowBackColor);

            TextView highsLabel0 = new TextView(context);
            highsLabel0.setText("Total");
            highsLabel0.setTextColor(ScreenColor.iTableRowTextColor);
            highsLabel0.setWidth(intWidthArray[0]);
            row0.addView(highsLabel0);

            TextView day3High = new TextView(context);
            day3High.setText("");
            day3High.setGravity(Gravity.RIGHT);
            day3High.setTypeface(verdanaTypeBold);

            day3High.setWidth(intWidthArray[1]);
            row.addView(day3High);

            TextView highsLabel = new TextView(context);
            highsLabel.setText(" ");
            highsLabel.setTextColor(ScreenColor.iTableRowTextColor);

            highsLabel.setWidth(intWidthArray[0]);
            row.addView(highsLabel);

            TextView day6High = new TextView(context);
            day6High.setText(VenturaServerConnect.valueToString(fDeposit.getTotalAmount()) + " ");
            day6High.setGravity(Gravity.RIGHT);
            day6High.setTypeface(verdanaTypeBold);
            day6High.setTextColor(ScreenColor.iTableRowTextColor);

            day6High.setWidth(intWidthArray[2]);
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
            main.addView(table2);
            //((LinearLayout)myFragmentView.findViewById(R.id.layout_total)).addView(table2);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTitle_Fixeddeposit() {
        try {
            String[] stringTitleArray = {"Amount", "Maturity"};//" "

            intWidthArray = new int[]{width / 2, width / 2};

            TableLayout table1 = (TableLayout) myFragmentView.findViewById(R.id.tb_layout_title);
            table1.removeAllViews();

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 2;

            TableRow rowTitleLabels0 = new TableRow(context);
            rowTitleLabels0.setPadding(1, 1, 1, 1);
            rowTitleLabels0.setBackgroundColor(ScreenColor.iTableHeaderBackColor);

            TableRow rowTitleLabels = new TableRow(context);
            rowTitleLabels.setPadding(1, 1, 1, 1);
            rowTitleLabels.setBackgroundColor(ScreenColor.iTableHeaderBackColor);


            TextView rowTitle1 = new TextView(context);
            rowTitle1.setText("Company Name");
            rowTitle1.setGravity(Gravity.LEFT);
            rowTitle1.setTextColor(ScreenColor.iTableHeaderTextColor);

            rowTitle1.setWidth(width);
            rowTitleLabels0.addView(rowTitle1, params);

            TextView r1c3 = new TextView(context);
            r1c3.setText(stringTitleArray[0]);
            r1c3.setGravity(Gravity.RIGHT);
            r1c3.setTextColor(ScreenColor.iTableHeaderTextColor);


            r1c3.setWidth(intWidthArray[0]);
            rowTitleLabels.addView(r1c3);

            TextView r1c4 = new TextView(context);
            r1c4.setText(stringTitleArray[1]);
            r1c4.setGravity(Gravity.RIGHT);
            r1c4.setTextColor(ScreenColor.iTableHeaderTextColor);
            r1c4.setWidth(intWidthArray[1]);
            rowTitleLabels.addView(r1c4);

            table1.addView(rowTitleLabels0);
            table1.addView(rowTitleLabels);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRow_Deposit() {

        try {
            StructFixedDepositRows[] depositrows = fDeposit.getFixedDepositRows();

            intWidthArray = new int[]{width / 2, width / 2};


            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 2;

            for (int i = 0; i < fDeposit.getNoOfRow(); i++) {
                TableLayout table2 = new TableLayout(context);
                StructFixedDepositRows depositrow = depositrows[i];

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
                highsLabel.setText(depositrow.getName() + " ");
                highsLabel.setGravity(Gravity.LEFT);
                highsLabel.setTypeface(verdanaType);
                highsLabel.setTextColor(ScreenColor.iTableRowTextColor);
                highsLabel.setWidth(width);
                row0.addView(highsLabel, params);

                TextView day6High = new TextView(context);
                day6High.setText(VenturaServerConnect.valueToString(depositrow.getAmount()) + " ");
                day6High.setGravity(Gravity.RIGHT);
                day6High.setTextColor(ScreenColor.iTableRowTextColor);
                day6High.setWidth(intWidthArray[0]);
                day6High.setPadding(0, 0, 10, 0);
                day6High.setTypeface(verdanaType);
                row.addView(day6High);


                TextView day5High = new TextView(context);
                day5High.setText(depositrow.getMaturityDate() + " ");
                day5High.setGravity(Gravity.RIGHT);
                day5High.setTextColor(ScreenColor.iTableRowTextColor);
                day5High.setWidth(intWidthArray[1]);
                day5High.setTypeface(verdanaType);
                row.addView(day5High);

                table2.addView(row0);
                table2.addView(row);

                if (ScreenColor.isGridLineShow) {
                    TableRow grid = new TableRow(context);
                    grid.setBackgroundColor(ScreenColor.iTableGridColor);
                    grid.setMinimumHeight(ScreenColor.seperatorHeight);
                    table2.addView(grid);
                }

                main.addView(table2);
            }

            //  if(table2.getChildCount() > 0){
            TableLayout table2 = new TableLayout(context);

            TableRow row0 = new TableRow(context);
            row0.setPadding(1, 10, 1, 1);

            TableRow row = new TableRow(context);
            row.setPadding(1, 1, 1, 10);
            row.setBackgroundColor(ScreenColor.iTableTotalRowBackColor);

            TextView highsLabel = new TextView(context);
            highsLabel.setText("Total");
            highsLabel.setGravity(Gravity.LEFT);
            highsLabel.setTypeface(verdanaTypeBold);
            highsLabel.setTextColor(ScreenColor.iTableRowTextColor);
            highsLabel.setWidth(width);
            row0.addView(highsLabel, params);

            TextView day6High = new TextView(context);
            day6High.setText(VenturaServerConnect.valueToString(fDeposit.getTotalAmount()) + " ");
            day6High.setGravity(Gravity.RIGHT);
            day6High.setTypeface(verdanaTypeBold);
            day6High.setTextColor(ScreenColor.iTableRowTextColor);
            day6High.setWidth(intWidthArray[0]);
            day6High.setPadding(0, 0, 10, 0);
            row.addView(day6High);

            TextView day3High = new TextView(context);
            day3High.setText("");
            day3High.setWidth(intWidthArray[1]);
            row.addView(day3High);

            table2.addView(row0);
            table2.addView(row);

            if (ScreenColor.isGridLineShow) {
                TableRow grid = new TableRow(context);
                grid.setBackgroundColor(ScreenColor.iTableGridColor);
                grid.setMinimumHeight(ScreenColor.seperatorHeight);
                table2.addView(grid);
            }

            main.addView(table2);
            // ((LinearLayout)myFragmentView.findViewById(R.id.layout_total)).addView(table2);

            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fixeddepositMethod() {
        try {
            main.removeAllViewsInLayout();
            if ((fDeposit != null) && (fDeposit.getNoOfRow() > 0)) {
                addTitle_Snapshot();
                addRow_Snapshot();
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

    public void maturityDetailsMethod() {
        try {

            main.removeAllViewsInLayout();

            if ((fDeposit != null) && (fDeposit.getNoOfRow() > 0)) {
                addTitle_Fixeddeposit();
                addRow_Deposit();
            } else {
                LayoutInflater li = mActivity.getLayoutInflater();
                View v = li.inflate(R.layout.nodata, null);
                main.addView(v);
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
            if (tab.equalsIgnoreCase(arrdeposit[0])) {
                fixeddepositMethod();
            } else if (tab.equalsIgnoreCase(arrdeposit[1])) {
                maturityDetailsMethod();
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
                    }
                    else{
                        return clientLoginResponse.charResMsg.getValue();
                    }
                }
                if (flag.equals(arrdeposit[0])) {
                    fDeposit = VenturaServerConnect.getdeposit();
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
                if(!result.equalsIgnoreCase("")){
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                }else {
                    if (flag.equals(arrdeposit[0])) {
                        if (fDeposit != null) {
                            selectedtab = arrdeposit[0];
                            fixeddepositMethod();
                        } else {
                            Dialogs.showSessionDialog(VenturaServerConnect.sessioncheck.getStatus(), context);
                        }
                    }
                    if (flag.equals(arrdeposit[1])) {
                        if (fDeposit != null) {
                            selectedtab = arrdeposit[1];
                            maturityDetailsMethod();
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

