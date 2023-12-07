package fragments.reports;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import adapters.TotalMsgAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eLogType;
import interfaces.OnAdapterRefresh;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by SUDIP on 06-12-2016.
 */
public class TotalMessagesView extends LinearLayout implements AdapterView.OnItemSelectedListener, OnAdapterRefresh {
    private RecyclerView message_recycler;
    private TotalMsgAdapter totalMsgAdapter;
    private Spinner spinner;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GlobalClass.latestContext);
    @BindView(R.id.noData)TextView noData;

    public TotalMessagesView(Context context) {
        super(context);
        ((HomeActivity)context).CheckRadioButton(HomeActivity.RadioButtons.TRADE);
        View view = LayoutInflater.from(context).inflate(R.layout.totalmessages_screen,null);
        ButterKnife.bind(this, view);
        message_recycler = (RecyclerView) view.findViewById(R.id.total_message_recycler);
        spinner = (Spinner) view.findViewById(R.id.message_spinner);
        message_recycler.setLayoutManager(linearLayoutManager);
        totalMsgAdapter = new TotalMsgAdapter(this);
        message_recycler.setAdapter(totalMsgAdapter);
        initSpinner();
        addView(view);
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

    }


    private void initSpinner() {
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(GlobalClass.latestContext, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.message_type));
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        totalMsgAdapter.refreshAdapter(adapterView.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onMessageRefresh(boolean hasMsg) {
        if (hasMsg){
            noData.setVisibility(View.GONE);
        }else {
            noData.setVisibility(View.VISIBLE);
        }
    }
}
