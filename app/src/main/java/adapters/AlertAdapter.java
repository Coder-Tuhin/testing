package adapters;


import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import Structure.Request.BC.DeleteAlert;
import butterknife.BindView;
import butterknife.ButterKnife;
import connection.SendDataToBCServer;
import models.AlertModel;
import models.ScripAlertModel;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 1/24/2017.
 */
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder>{
    private ArrayList<AlertModel> list;
    private LayoutInflater inflater;
    private Context ctx;

    public AlertAdapter(Context ctx) {
        inflater = LayoutInflater.from(GlobalClass.latestContext);
        list = new ArrayList<>();
        setuplistData();
        this.ctx = ctx;
    }

    public void setuplistData() {
        list.clear();
        List<ScripAlertModel> mList = new ArrayList<>(VenturaApplication.getPreference().getAlertRate().values());
        for (ScripAlertModel sam : mList){
            if(sam.getGeaterCond() != null&& sam.getGeaterCond().getAchive()!=1){
                list.add(sam.getGeaterCond());
            }
            if(sam.getLessCond() != null && sam.getLessCond().getAchive()!=1){
                list.add(sam.getLessCond());
            }

        }
       /* final LinkedHashMap<Integer, ScripAlertModel> alertRate = VenturaApplication.getPreference().getAlertRate();
        ArrayList<Integer> keyList = new ArrayList<>(alertRate.keySet());
        for(int i=keyList.size()-1;i>=0;i--){
            ScripAlertModel scripAlertModel = alertRate.get(keyList.get(i));
            if(scripAlertModel.getGeaterCond() != null){
                list.add(scripAlertModel.getGeaterCond());
            }
            if(scripAlertModel.getLessCond() != null){
                list.add(scripAlertModel.getLessCond());
            }
        }*/
    }

    @Override
    public AlertAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.alert_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getScriptName());
        holder.rate.setText(list.get(position).getTokenRate()+"");
        holder.name.setTextColor(ctx.getResources().getColor(R.color.red));
        holder.rate.setTextColor(ctx.getResources().getColor(R.color.red));
     /*   if (list.get(position).getAchive() == 0){
        }else {
          //  deleteAlertMeth(list.get(position));
            *//*  holder.name.setTextColor(ctx.getResources().getColor(R.color.green1));
            holder.rate.setTextColor(ctx.getResources().getColor(R.color.green1));*//*
        }*/
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(list.get(position));
            }
        });
    }

    private void openDialog(final AlertModel alertModel) {
        final AlertDialog.Builder m_alertBuilder = new AlertDialog.Builder(ctx);
        m_alertBuilder.setMessage("Are you sure you what to delete the details?");
        m_alertBuilder.setCancelable(false);
        m_alertBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        deleteAlertMeth(alertModel);
                    }
                });
        m_alertBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
       AlertDialog m_alertDialog = m_alertBuilder.create();
        m_alertDialog.show();
        m_alertDialog.getButton(m_alertDialog.BUTTON_NEGATIVE).setTextColor(ctx.getResources().getColor(R.color.ventura_color));
        m_alertDialog.getButton(m_alertDialog.BUTTON_POSITIVE).setTextColor(ctx.getResources().getColor(R.color.ventura_color));
    }

    private void deleteAlertMeth(AlertModel alertModel) {
        DeleteAlert deleteAlert = new DeleteAlert();
        deleteAlert.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
        deleteAlert.token.setValue(alertModel.getToken());
        deleteAlert.tokenRate.setValue(alertModel.getTokenRate());
        SendDataToBCServer sendDataToServer = new SendDataToBCServer();
        sendDataToServer.deleteAlertReq(deleteAlert);
        LinkedHashMap<Integer, ScripAlertModel> alertRate = VenturaApplication.getPreference().getAlertRate();
        ScripAlertModel scripAlertModel = alertRate.get(alertModel.getToken());
        scripAlertModel.setValueForCondition(alertModel.getCondition(),null);
        if(scripAlertModel.isBothNotAvailable()){
            alertRate.remove(alertModel.getToken());
        }
        VenturaApplication.getPreference().setAlertRate(alertRate);
        setuplistData();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name)TextView name;
        @BindView(R.id.rate)TextView rate;
        @BindView(R.id.close)ImageButton close;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void refreshAdapter(){
        setuplistData();
        notifyDataSetChanged();
    }

}
