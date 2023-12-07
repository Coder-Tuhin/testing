package view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eForHandler;
import enums.eReports;
import fragments.homeGroups.ReportFragment;
import models.MessageModel;
import utils.GlobalClass;
import utils.VenturaException;

/**
 * Created by SUDIP on 06-12-2016.
 */
public class DialogMessages implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener ,DialogInterface.OnDismissListener{
    private View m_view;
    private RadioGroup messageRadioGrp;
    private ImageButton message_close;
    private RecyclerView message_recycler;
    private LinearLayoutManager linearLayoutManager;
    private   AlertDialog m_alertDialog;

    public DialogMessages(){
        ShowMessages();
    }

    private void ShowMessages(){
        try {
            m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.message_window, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.homeActivity.getLetestContext());
            dialogBuilder.setView(m_view);
            dialogBuilder.setCancelable(false);
            messageRadioGrp = m_view.findViewById(R.id.message_radioGrp);
            message_close = m_view.findViewById(R.id.message_close);
            message_recycler = m_view.findViewById(R.id.message_recycler);
            message_close.setOnClickListener(this);
            messageRadioGrp.setOnCheckedChangeListener(this);
            linearLayoutManager = new LinearLayoutManager(GlobalClass.latestContext);
            message_recycler.setLayoutManager(linearLayoutManager);
            message_recycler.setAdapter(new DialogMessageAdapter());
            if (m_alertDialog == null){
                m_alertDialog = dialogBuilder.create();
                m_alertDialog.show();
                m_alertDialog.setOnDismissListener(this);
                m_alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public void RefreshMessageList(){
        DialogMessageAdapter _dma = (DialogMessageAdapter) message_recycler.getAdapter();
        _dma.refreshAdapter();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        closeDialog();
        String spinnerItem = eReports.ORDERBOOK.name;
        switch (checkedId){
            case R.id.message_orderbook:
                spinnerItem = eReports.ORDERBOOK.name;
                break;
            case R.id.message_tradebook:
                spinnerItem = eReports.TRADEBOOK.name;
                break;
            case R.id.message_netposition:
                spinnerItem = eReports.NET_POSITION.name;
                break;
            default:
                break;
        }
        openReport(spinnerItem);
    }

    private void openReport(String spinnerItem) {
        Fragment currentFrag = GlobalClass.fragmentManager.findFragmentById(R.id.container_body);
        if (!(currentFrag instanceof ReportFragment)) {
            ((HomeActivity) GlobalClass.latestContext).setSpinner(spinnerItem,false);
            GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, false, "");
        }else {
            if (GlobalClass.reportHandler != null){
                ((HomeActivity) GlobalClass.latestContext).setSpinner(spinnerItem,true);
                Message msg = Message.obtain(GlobalClass.reportHandler);
                Bundle confMsgBundle = new Bundle();
                confMsgBundle.putString(eForHandler.SPINNERNAME.name, spinnerItem);
                msg.setData(confMsgBundle);
                GlobalClass.reportHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.message_close:
                closeDialog();
                break;
        }
    }



    private void closeDialog(){
        m_view = null;
        m_alertDialog.dismiss();
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        m_alertDialog = null;
        GlobalClass.DismissMessageDialog();
    }


    public class DialogMessageAdapter extends RecyclerView.Adapter<DialogMessageAdapter.MyViewHolder>{
        private LayoutInflater inflater;

        DialogMessageAdapter(){
            inflater = LayoutInflater.from(GlobalClass.homeActivity.getLetestContext());
        }

        @Override
        public DialogMessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.message_item, parent, false);
            DialogMessageAdapter.MyViewHolder holder = new DialogMessageAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(DialogMessageAdapter.MyViewHolder holder, int position) {
            try {
                MessageModel _messageModel = GlobalClass._messageList.get(position);
                if (_messageModel != null){
                    holder.message_body.setText(_messageModel.getMessage());
                    holder.message_time.setText(_messageModel.getTime());
                }
            }catch (Exception e){
                VenturaException.Print(e);
            }
        }

        @Override
        public int getItemCount() {
            return GlobalClass._messageList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.message_body)TextView message_body;
            @BindView(R.id.message_time)TextView message_time;
            @BindView(R.id.message_item)LinearLayout message_item;
            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
        public void refreshAdapter(){
            notifyDataSetChanged();
        }
    }
}
