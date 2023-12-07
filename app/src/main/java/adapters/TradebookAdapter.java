package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;
import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;

import com.ventura.venturawealth.R;

/**
 * Created by XTREMSOFT on 11/3/2016.
 */
public class TradebookAdapter extends RecyclerView.Adapter<TradebookAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeTreeMap;
    private Context ctx;
    private ArrayList<Integer> allKeyDecending;

    public TradebookAdapter(Context m_context, TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeTreeMap) {
        inflater = LayoutInflater.from(m_context);
        this.tradeTreeMap = tradeTreeMap;
        allKeyDecending = new ArrayList<>(tradeTreeMap.descendingKeySet());
        this.ctx = m_context;
    }
    public TreeMap<Integer, StructTradeReportReplyRecord_Pointer> getAlltrade(){
        return tradeTreeMap;
    }
    public ArrayList<Integer> getAllTradeIds(){
        return allKeyDecending;
    }

    public void refreshData(TreeMap<Integer, StructTradeReportReplyRecord_Pointer> tradeTreeMap){
        this.tradeTreeMap = tradeTreeMap;
        allKeyDecending = new ArrayList<>(tradeTreeMap.descendingKeySet());
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tradebook_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.setData(tradeTreeMap.get(allKeyDecending.get(position)));
    }

    @Override
    public int getItemCount() {
        return tradeTreeMap.size();
    }

    public StructTradeReportReplyRecord_Pointer getTradeBookForPosition(int position){
        return tradeTreeMap.get(allKeyDecending.get(position));
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
        public void setData(StructTradeReportReplyRecord_Pointer tradeRow){

            TextView scripName = (TextView) view.findViewById(R.id.screpname_trdbk_textview);
            TextView value = (TextView) view.findViewById(R.id.screpvalue_trdbk_textview);

            scripName.setText(tradeRow.getFormatedScripName(true));
            value.setText(tradeRow.getBuySell() + " " +tradeRow.getTradeQtyRate());
        }
    }
}
