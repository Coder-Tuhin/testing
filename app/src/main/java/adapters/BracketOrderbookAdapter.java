package adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Response.RC.StructOCOOrdBkDet;
import interfaces.OnAdapterRefresh;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class BracketOrderbookAdapter extends RecyclerView.Adapter<BracketOrderbookAdapter.MyViewHolder> {
    private TreeMap<String, StructOCOOrdBkDet> orderBKData;
    private LayoutInflater inflater;
    private ArrayList<String> allKeyDecending;
    private OnAdapterRefresh onAdapterRefresh;

    public BracketOrderbookAdapter(OnAdapterRefresh onAdapterRefresh, TreeMap<String, StructOCOOrdBkDet> orderBKData) {
        inflater = LayoutInflater.from(GlobalClass.homeActivity.getContext());
        this.orderBKData = orderBKData;
        allKeyDecending = new ArrayList<>(orderBKData.descendingKeySet());
        this.onAdapterRefresh = onAdapterRefresh;
    }

    public void refreshData(TreeMap<String, StructOCOOrdBkDet> orderBKData){
        this.orderBKData = orderBKData;
        allKeyDecending = new ArrayList<>(orderBKData.descendingKeySet());
        boolean hasMsg = allKeyDecending.size() > 0;
        onAdapterRefresh.onMessageRefresh(hasMsg);
        notifyDataSetChanged();
    }


    public StructOCOOrdBkDet getOrderBookForPosition(int position){
        return orderBKData.get(allKeyDecending.get(position));
    }

    public TreeMap<String, StructOCOOrdBkDet> getOrderBKData(){
        return orderBKData;
    }


    public ArrayList<String> getAllBrokerOrdIds(){
        return allKeyDecending;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.orderbook_items, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.setData(orderBKData.get(allKeyDecending.get(position)));
    }

    @Override
    public int getItemCount() {
        return orderBKData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        private View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void setData(StructOCOOrdBkDet ordRowData){

            TextView scripName = (TextView) view.findViewById(R.id.scripame_ordbook_textview);
            TextView buysell = (TextView) view.findViewById(R.id.buyorsell_ordbk_textview);
            TextView status = (TextView) view.findViewById(R.id.status_ordbk_textview);
            scripName.setText(ordRowData.scripName.getValue());

            buysell.setText(ordRowData.getBuySell() + " " +ordRowData.getOrderQtyRate());
            status.setText(ordRowData.getFinalStatus());

            scripName.setTextColor(ordRowData.getTextColor());
            buysell.setTextColor(ordRowData.getTextColor());
            status.setTextColor(ordRowData.getTextColor());

        }
    }

}
