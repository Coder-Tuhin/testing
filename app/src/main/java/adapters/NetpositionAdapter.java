package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Structure.Other.StructMobNetPosition;

import com.ventura.venturawealth.R;

import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import utils.Constants;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
public class NetpositionAdapter extends RecyclerView.Adapter<NetpositionAdapter.MyViewHolder> {
    ArrayList<StructMobNetPosition> allNetPosition;
    private LayoutInflater inflater;
    private Context ctx;
    private ArrayList<String> keyList;

    public NetpositionAdapter(Context m_context, ArrayList<StructMobNetPosition> allNetPosition) {
        inflater = LayoutInflater.from(m_context);
        this.allNetPosition = allNetPosition;
        this.ctx = m_context;
        generateScripCodeList();
    }

    public ArrayList<StructMobNetPosition> getAllNetPositionData(){
        return allNetPosition;
    }
    public void reloadData(ArrayList<StructMobNetPosition> allNetPosition){
        this.allNetPosition = allNetPosition;
        generateScripCodeList();
        notifyDataSetChanged();
    }
    private void generateScripCodeList(){
        keyList = new ArrayList<>();
        for (StructMobNetPosition netPosition:allNetPosition){
            keyList.add(netPosition.key);
        }
    }
    public StructMobNetPosition getNetPositionForPosition(int position){
        return allNetPosition.get(position);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.netposition_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        StructMobNetPosition structMobNetPosition = allNetPosition.get(position);
        if(structMobNetPosition != null) {
            holder.setValue(structMobNetPosition);
        }
    }


    @Override
    public int getItemCount() {
        return allNetPosition.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private double prevLastRate = 0.00;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setValue(StructMobNetPosition netPosition){
            try {
                TextView scripName = (TextView)view.findViewById(R.id.scrept_name_textview);
                TextView netQtyRate = (TextView)view.findViewById(R.id.scrept_vol_textview);
                TextView mtm = (TextView)view.findViewById(R.id.mtm_vol_textview);
                TextView booked = (TextView)view.findViewById(R.id.booked_vol_textview);
                TextView ltp = (TextView)view.findViewById(R.id.ltp_vol_textview);
                TextView ltp_title = (TextView)view.findViewById(R.id.ltp_textview);

                ltp.setVisibility(View.VISIBLE);
                ltp_title.setVisibility(View.VISIBLE);

                scripName.setText(netPosition.getFormatedScripName(true));
                netQtyRate.setText(netPosition.getNetQtyRate());
                mtm.setText(netPosition.getMTMStr());
                booked.setText(netPosition.getBookedPLStr());

                netPosition.setLtpWithTxtColor(ltp,prevLastRate);

                prevLastRate = netPosition.getLastRate();
                //ltp.setText(netPosition.getLastRateStr());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshItem(int scripCode) {

        int position = -1;
        String key = scripCode + "";
        position = keyList.contains(key)?keyList.indexOf(key):-1;
        if(position>=0) {
            StructMobNetPosition hld = GlobalClass.getClsNetPosn().getPositionForkey(key);
            allNetPosition.remove(position);
            allNetPosition.add(position, hld);
            notifyItemChanged(position);
        }
        else {
            key = scripCode + "#" + Constants.intraday;
            position = keyList.contains(key) ? keyList.indexOf(key) : -1;
            if (position >= 0) {
                StructMobNetPosition hld = GlobalClass.getClsNetPosn().getPositionForkey(key);
                allNetPosition.remove(position);
                allNetPosition.add(position, hld);
                notifyItemChanged(position);
            }
            key = scripCode + "#" + Constants.delivery;
            position = keyList.contains(key) ? keyList.indexOf(key) : -1;
            if (position >= 0) {
                StructMobNetPosition hld = GlobalClass.getClsNetPosn().getPositionForkey(key);
                allNetPosition.remove(position);
                allNetPosition.add(position, hld);
                notifyItemChanged(position);
            }
        }
    }
}
