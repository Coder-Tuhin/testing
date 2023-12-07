package adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructOrderReportReplyRecord_Pointer;

import com.ventura.venturawealth.R;
import interfaces.OnAdapterRefresh;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class OrderbookAdapter extends RecyclerView.Adapter<OrderbookAdapter.MyViewHolder> {
    private TreeMap<Integer, StructOrderReportReplyRecord_Pointer> orderBKData;
    private LayoutInflater inflater;
    private ArrayList<Integer> allKeyDecending;
    private ArrayList<Integer> allScripCodeList;

    private OnAdapterRefresh onAdapterRefresh;

    public OrderbookAdapter(OnAdapterRefresh onAdapterRefresh, TreeMap<Integer, StructOrderReportReplyRecord_Pointer> orderBKData) {
        inflater = LayoutInflater.from(GlobalClass.latestContext);
        this.orderBKData = orderBKData;
        allKeyDecending = new ArrayList<>(orderBKData.descendingKeySet());
        this.onAdapterRefresh = onAdapterRefresh;
    }

    public void refreshData(TreeMap<Integer, StructOrderReportReplyRecord_Pointer> orderBKData){
        this.orderBKData = orderBKData;
        allKeyDecending = new ArrayList<>(orderBKData.descendingKeySet());
        boolean hasMsg = allKeyDecending.size() > 0;
        onAdapterRefresh.onMessageRefresh(hasMsg);
        notifyDataSetChanged();
        setAllScripCodeList();
    }

    private void setAllScripCodeList(){
        allScripCodeList = new ArrayList<>();
        for(int i=0;i<allKeyDecending.size();i++){
            StructOrderReportReplyRecord_Pointer orderReport = orderBKData.get(allKeyDecending.get(i));
            if(!allScripCodeList.contains(orderReport.scripCode.getValue())) {
                allScripCodeList.add(orderReport.scripCode.getValue());
            }
        }
    }

    public ArrayList<Integer> getAllScripCodeList() {
        return allScripCodeList;
    }
    public void updateRate(int token) {
        try {
            if(!allScripCodeList.isEmpty()) {
                int index = allScripCodeList.indexOf(token);
                if (index != -1) {
                    for (int i = 0; i < allKeyDecending.size(); i++) {
                        StructOrderReportReplyRecord_Pointer orderReport = orderBKData.get(allKeyDecending.get(i));
                        if (token == orderReport.scripCode.getValue()) {
                            notifyItemChanged(i);
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public StructOrderReportReplyRecord_Pointer getOrderBookForPosition(int position){
        return orderBKData.get(allKeyDecending.get(position));
    }

    public TreeMap<Integer, StructOrderReportReplyRecord_Pointer> getOrderBKData(){
        return orderBKData;
    }


    public ArrayList<Integer> getAllBrokerOrdIds(){
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
        private double prevlastRate = 0.00;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
        public void setData(StructOrderReportReplyRecord_Pointer ordRowData){

            TextView scripName = (TextView) view.findViewById(R.id.scripame_ordbook_textview);
            TextView buysell = (TextView) view.findViewById(R.id.buyorsell_ordbk_textview);
            TextView ltpTextView = (TextView) view.findViewById(R.id.ltp_ordbk_textview);
            LinearLayout ltpView = (LinearLayout) view.findViewById(R.id.ltpview);


            TextView status = (TextView) view.findViewById(R.id.status_ordbk_textview);
            scripName.setText(ordRowData.getFormatedScripName(true));

            buysell.setText(ordRowData.getBuySell() + " " +ordRowData.getOrderQtyRate());
            status.setText(ordRowData.getFinalStatus());

            scripName.setTextColor(ordRowData.getTextColor());
            buysell.setTextColor(ordRowData.getTextColor());
            status.setTextColor(ordRowData.getTextColor());

            StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(ordRowData.scripCode.getValue(),false);
            if(mktWatch != null) {
                ltpView.setVisibility(View.VISIBLE);
                //NumberFormat formatter = Formatter.getFormatter(mktWatch.segment.getValue());
                try {
                    mktWatch.setLtpWithTxtColor(ltpTextView,prevlastRate);
                    prevlastRate = mktWatch.getLastRate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //ltpTextView.setText("LTP : "+formatter.format(mktWatch.getLastRate()));
            }
        }
    }

}
