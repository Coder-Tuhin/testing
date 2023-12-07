package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.util.ArrayList;

import Structure.Response.RC.StructOCOPosnDet;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
public class BracketNetpositionAdapter extends RecyclerView.Adapter<BracketNetpositionAdapter.MyViewHolder> {
    ArrayList<StructOCOPosnDet> allNetPosition;
    private LayoutInflater inflater;
    private Context ctx;
    private ArrayList<String> keyList;

    public BracketNetpositionAdapter(Context m_context, ArrayList<StructOCOPosnDet> allNetPosition) {
        inflater = LayoutInflater.from(m_context);
        this.allNetPosition = allNetPosition;
        this.ctx = m_context;
        generateScripCodeList();
    }

    public ArrayList<StructOCOPosnDet> getAllNetPositionData(){
        return allNetPosition;
    }
    public void reloadData(ArrayList<StructOCOPosnDet> allNetPosition){
        this.allNetPosition = allNetPosition;
        generateScripCodeList();
        notifyDataSetChanged();
    }
    private void generateScripCodeList(){
        keyList = new ArrayList<>();
        for (StructOCOPosnDet netPosition:allNetPosition){
            keyList.add(netPosition.positionID.getValue()+"");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bracketnetposition_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        StructOCOPosnDet structMobNetPosition = allNetPosition.get(position);
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
        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setValue(StructOCOPosnDet netPosition){

            TextView scripName = (TextView)view.findViewById(R.id.scrept_name_textview);
            TextView netQtyRate = (TextView)view.findViewById(R.id.scrept_vol_textview);
            TextView mtm = (TextView)view.findViewById(R.id.mtm_vol_textview);
            TextView booked = (TextView)view.findViewById(R.id.booked_vol_textview);
            TextView positionID = (TextView)view.findViewById(R.id.posid_vol_textview);

            //NumberFormat formatter = Formatter.getFormatter(netPosition.getExchange());
            scripName.setText(netPosition.getScripnameWithOrderType());
            netQtyRate.setText(netPosition.getNetQtyRate());
            mtm.setText((netPosition.mtmpl.getValue() + ""));
            booked.setText((netPosition.bkpl.getValue()+""));
            positionID.setText(netPosition.positionID.getValue() + "");
        }
    }

    public void refreshItem(int scripCode) {

        /*
        int position = -1;
        String key = scripCode + "";
        position = keyList.contains(key)?keyList.indexOf(key):-1;
        if(position>=0) {
            StructMobNetPosition hld = GlobalClass.clsNetPosn.getPositionForkey(key);
            allNetPosition.remove(position);
            allNetPosition.add(position, hld);
            notifyItemChanged(position);
        }
        else {
            key = scripCode + "#" + Constants.intraday;
            position = keyList.contains(key) ? keyList.indexOf(key) : -1;
            if (position >= 0) {
                StructMobNetPosition hld = GlobalClass.clsNetPosn.getPositionForkey(key);
                allNetPosition.remove(position);
                allNetPosition.add(position, hld);
                notifyItemChanged(position);
            }
            key = scripCode + "#" + Constants.delivery;
            position = keyList.contains(key) ? keyList.indexOf(key) : -1;
            if (position >= 0) {
                StructMobNetPosition hld = GlobalClass.clsNetPosn.getPositionForkey(key);
                allNetPosition.remove(position);
                allNetPosition.add(position, hld);
                notifyItemChanged(position);
            }
        }*/
    }
}
