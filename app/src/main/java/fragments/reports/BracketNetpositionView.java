package fragments.reports;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.util.ArrayList;

import Structure.Response.RC.StructOCOPosnDet;
import adapters.BracketNetpositionAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessageCode;
import enums.eServerType;
import utils.DividerItemDecoration;
import utils.GlobalClass;
import utils.RecyclerItemClickListener;
import utils.ScreenColor;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class BracketNetpositionView extends LinearLayout implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.net_position_recyclerView)RecyclerView netPos_recycler;
    @BindView(R.id.dataAvailable)LinearLayout dataAvailable;
    @BindView(R.id.noData)TextView noData;

    TextView totBookedValue,totMTMValue;
    Spinner netPosSpn;

    private BracketNetpositionAdapter netpositionAdapter;
    private LinearLayoutManager linearLayoutManager;

    public BracketNetpositionView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View layout = LayoutInflater.from(context).inflate(R.layout.bracketnet_position,null);
        ButterKnife.bind(this, layout);
        initialization(layout);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        addView(layout);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        GlobalClass.showProgressDialog("Please wait...");
        GlobalClass.bracketPositionBkUIHandler = new BracketNetPositionHandler();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Write whatever to want to do after delay specified (1 sec)
                if(UserSession.getClientResponse().getServerType() == eServerType.RC){
                    GlobalClass.getClsBracketOrderBook().sendBracketOrderBookRequest();
                    GlobalClass.getClsBracketPositionBook().sendBracketPositionBookRequest();
                    GlobalClass.dismissdialog();
                }
            }
        }, 2000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        GlobalClass.bracketPositionBkUIHandler = null;
    }

    private void initialization(View layout) {

        totBookedValue = (TextView) layout.findViewById(R.id.booked_netpos_textview);
        totMTMValue = (TextView) layout.findViewById(R.id.mtm_netpos_textview);

        netPosSpn = (Spinner) layout.findViewById(R.id.net_position_spinner);
        ArrayList<String> spinnerlist = new ArrayList<String>();
        spinnerlist.add("All");
        spinnerlist.add("Open");
        spinnerlist.add("Close");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(super.getContext(), R.layout.custom_spinner_item,spinnerlist);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        netPosSpn.setAdapter(dataAdapter);
        netPosSpn.setSelection(1);
        netPosSpn.setOnItemSelectedListener(null);
        netPosSpn.setOnItemSelectedListener(this);

        totBookedValue.setText(GlobalClass.getClsBracketPositionBook().getTotalBookedPL());
        totMTMValue.setText(GlobalClass.getClsBracketPositionBook().getTotalMTMPL());

        ArrayList<StructOCOPosnDet> allNetPosition = GlobalClass.getClsBracketPositionBook().getAllNetPosition(netPosSpn.getSelectedItem().toString());

        netpositionAdapter = new BracketNetpositionAdapter(super.getContext(),allNetPosition);
        linearLayoutManager = new LinearLayoutManager(super.getContext());
        netPos_recycler.setLayoutManager(linearLayoutManager);
        netPos_recycler.setAdapter(netpositionAdapter);
        int deviderHeiight = (int) getResources().getDimension(R.dimen.divider_height);
        netPos_recycler.addItemDecoration(new DividerItemDecoration(ScreenColor.SILVER,deviderHeiight));
        netPos_recycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),netPos_recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Fragment fragment = new BracketNetpositiondetailsFragment(netpositionAdapter.getAllNetPositionData(),position);
                FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment,"");
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        reloadViewForNoData(GlobalClass.getClsBracketPositionBook().getNetPositionSize());
    }

    private void reloadData(){

        GlobalClass.dismissdialog();
        totBookedValue.setText(GlobalClass.getClsBracketPositionBook().getTotalBookedPL());
        totMTMValue.setText(GlobalClass.getClsBracketPositionBook().getTotalMTMPL());

        ArrayList<StructOCOPosnDet> allNetPosition = GlobalClass.getClsBracketPositionBook().getAllNetPosition(netPosSpn.getSelectedItem().toString());
        netpositionAdapter.reloadData(allNetPosition);
        reloadViewForNoData(GlobalClass.getClsBracketPositionBook().getNetPositionSize());
    }
    private void reloadViewForNoData(int count){

        if(count <= 0){
            noData.setVisibility(View.VISIBLE);
            dataAvailable.setVisibility(View.GONE);
        }
        else{
            noData.setVisibility(View.GONE);
            dataAvailable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        reloadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*
    private class SendPositionBookReq extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {





            } catch (Exception e) {
                VenturaException.Print(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }*/

    class BracketNetPositionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case BRACKET_POSITION_REPORT:
                                reloadData();
                                break;

                            default:
                                break;
                        }
                    }
                    catch (Exception ex){
                        GlobalClass.onError("TradeLoginHandler : " ,ex);
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
