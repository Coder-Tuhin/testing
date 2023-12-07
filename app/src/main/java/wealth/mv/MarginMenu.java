package wealth.mv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import utils.GlobalClass;
import utils.ScreenColor;
import utils.UserSession;
import wealth.Dialogs;
import wealth.VenturaServerConnect;
import wealth.wealthStructure.StructOpenPositionDetail;
import wealth.wealthStructure.StructOpenPositionRow;


/**
 * Created by Tapas on 11/28/13.
 */
public class MarginMenu extends Fragment {
    int width, height;
    LinearLayout main;
    float textSize, tabletextSize, tableTitleFont;
    String selectedtab = "";
    Typeface verdanaType, verdanaTypeBold;
    StructOpenPositionDetail openP;
    View.OnTouchListener gestureListener;
    Context context;
    private String[] arrmargin = {"Equity", "Commodities", "Currency"};
    private Activity mActivity;
    private View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        try {
            myFragmentView = inflater.inflate(R.layout.myventura_report_view, container, false);
            myFragmentView.findViewById(R.id.layout_extratitle).setVisibility(View.GONE);
            myFragmentView.findViewById(R.id.colletral_spinner).setVisibility(View.GONE);

            setHasOptionsMenu(true);

            textSize = ScreenColor.menufontSize;
            tabletextSize = ScreenColor.fontSize;
            tableTitleFont = tabletextSize - 3;

            context = mActivity.getApplicationContext();

            verdanaType = Typeface.DEFAULT;
            verdanaTypeBold = Typeface.DEFAULT_BOLD;

            width = ScreenColor.getScreenWidth(context);
            height = ScreenColor.getScreenHeight(context);

            main = (LinearLayout) myFragmentView.findViewById(R.id.data_layout);
            main.removeAllViews();

            Spinner groupSpinner = (Spinner) myFragmentView.findViewById(R.id.layout_spinner).findViewById(R.id.spn);
            ArrayAdapter<String> Adapter = new ArrayAdapter(context, R.layout.custom_spinner_label, arrmargin);
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
            GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, "MyWealth"+getClass().getSimpleName()+"_MyWealth", UserSession.getLoginDetailsModel().getUserID());

            return myFragmentView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addTitle_equity() {
        try {
            TableLayout table1 = (TableLayout) myFragmentView.findViewById(R.id.tb_layout_title);
            table1.removeAllViewsInLayout();

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 7;

            TableRow rowDayLabels = new TableRow(context);
            rowDayLabels.setPadding(1, 1, 1, 1);
            rowDayLabels.setBackgroundColor(ScreenColor.iTableHeaderBackColor);

            TextView r1c1 = new TextView(context);
            // title column/row
            r1c1.setText("Particulars ");
            r1c1.setPadding(0, 8, 0, 8);
            r1c1.setTextColor(ScreenColor.iTableHeaderTextColor);
            r1c1.setTypeface(verdanaTypeBold);
            r1c1.setTextSize(tableTitleFont);
            r1c1.setGravity(Gravity.LEFT);
            r1c1.setWidth(width / 2);
            rowDayLabels.addView(r1c1);

            TextView r1c2 = new TextView(context);
            r1c2.setText("Purchase ");
            r1c2.setPadding(0, 8, 0, 8);
            r1c2.setTypeface(verdanaTypeBold);
            r1c2.setTextColor(ScreenColor.iTableHeaderTextColor);
            r1c2.setTextSize(tableTitleFont);
            r1c2.setWidth(width / 4);
            r1c2.setGravity(Gravity.RIGHT);

            rowDayLabels.addView(r1c2);


            TextView r1c3 = new TextView(context);
            r1c3.setText("Sales ");
            r1c3.setPadding(0, 8, 0, 8);
            r1c3.setTypeface(verdanaTypeBold);
            r1c3.setTextColor(ScreenColor.iTableHeaderTextColor);
            r1c3.setTextSize(tableTitleFont);
            r1c3.setWidth(width / 4);
            r1c3.setPadding(0, 0, 10, 0);
            r1c3.setGravity(Gravity.RIGHT);

            rowDayLabels.addView(r1c3);
            table1.addView(rowDayLabels);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRow_equity() {

        try {
            StructOpenPositionRow[] opRows = openP.getOpenPositionRow();
            TableLayout table2 = new TableLayout(context);

            for (int i = 0; i < openP.getNoOfRow(); i++) {

                int bacColor;
                if (i % 2 == 0) {
                    bacColor = ScreenColor.iTableRowOneBackColor;
                } else {
                    bacColor = ScreenColor.iTableRowTwoBackColor;//Color.rgb(255, 239, 239);
                }

                TableRow rowHighs = new TableRow(context);
                rowHighs.setBackgroundColor(bacColor);
                rowHighs.setPadding(1, 1, 1, 1);

                StructOpenPositionRow opRow = opRows[i];

                TextView highsLabel = new TextView(context);
                highsLabel.setText(opRow.getName() + " ");
                highsLabel.setTextColor(ScreenColor.iTableRowTextColor);
                highsLabel.setTextSize(tabletextSize);
                highsLabel.setTypeface(verdanaType);
                highsLabel.setGravity(Gravity.LEFT);
                highsLabel.setWidth(width / 2);
                rowHighs.addView(highsLabel);

                String qty = "" + opRow.getQtyOnHand();

                TextView day1High = new TextView(context);
                if (qty.contains("-")) {
                    day1High.setText(" ");
                } else {
                    day1High.setText(qty + " ");
                }
                day1High.setGravity(Gravity.RIGHT);
                day1High.setTextColor(ScreenColor.iTableRowTextColor);
                day1High.setTextSize(tabletextSize);
                day1High.setTypeface(verdanaType);
                day1High.setWidth(width / 4);
                rowHighs.addView(day1High);

                TextView day2High = new TextView(context);
                if (qty.contains("-")) {
                    day2High.setText(qty.substring(1, qty.length()) + " ");
                } else {
                    day2High.setText(" ");
                }
                day2High.setGravity(Gravity.RIGHT);
                day2High.setTextColor(ScreenColor.iTableRowTextColor);
                day2High.setTextSize(tabletextSize);
                day2High.setTypeface(verdanaType);
                day2High.setWidth(width / 4);
                day2High.setPadding(0, 0, 10, 0);

                rowHighs.addView(day2High);

                table2.addView(rowHighs);
                if (ScreenColor.isGridLineShow) {
                    TableRow grid = new TableRow(context);
                    grid.setBackgroundColor(ScreenColor.iTableGridColor);
                    grid.setMinimumHeight(ScreenColor.seperatorHeight);
                    table2.addView(grid);
                }
            }
            main.addView(table2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void equityMethod() {

        try {
            main.removeAllViewsInLayout();

            if ((openP != null) && (openP.getNoOfRow() > 0)) {
                addTitle_equity();
                addRow_equity();
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

    private void commodityMethod() {

        try {
            main.removeAllViewsInLayout();
            if ((openP != null) && (openP.getNoOfRow() > 0)) {
                addTitle_equity();
                addRow_equity();
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

    public void currencyMethod() {
        try {
            main.removeAllViewsInLayout();
            if ((openP != null) && (openP.getNoOfRow() > 0)) {
                addTitle_equity();
                addRow_equity();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void setRupee(String tab) {

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
                    }else{
                        return clientLoginResponse.charResMsg.getValue();
                    }
                }
                if (flag.equals(arrmargin[0])) {
                    openP = VenturaServerConnect.getOpenPositionEquity();
                } else if (flag.equals(arrmargin[1])) {
                    openP = VenturaServerConnect.getOpenPositionComm();
                } else if (flag.equals(arrmargin[2])) {
                    openP = VenturaServerConnect.getOpenPositionCurr();
                }


            } catch (Exception ie) {
                str = ie.toString();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //mDialog.setCanceledOnTouchOutside(false);
            try {
                mDialog.dismiss();
                if(!result.equalsIgnoreCase("")){
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                }else {
                    if (flag.equals(arrmargin[0])) {

                        if (openP != null) {
                            equityMethod();
                        } else {
                            Dialogs.showSessionDialog(VenturaServerConnect.sessioncheck.getStatus(), context);
                        }
                    } else if (flag.equals(arrmargin[1])) {
                        if (openP != null) {
                            commodityMethod();
                        } else {
                            Dialogs.showSessionDialog(VenturaServerConnect.sessioncheck.getStatus(), context);
                        }
                    } else if (flag.equals(arrmargin[2])) {

                        if (openP != null) {
                            currencyMethod();
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


