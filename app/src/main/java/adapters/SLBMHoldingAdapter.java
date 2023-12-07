package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.TSLBMHoldingsReportReplyRecord;
import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eExch;
import utils.Formatter;
import utils.GlobalClass;

public class SLBMHoldingAdapter extends RecyclerView.Adapter<SLBMHoldingAdapter.MyViewHolder> {

    ArrayList<TSLBMHoldingsReportReplyRecord> holding;
    private LayoutInflater inflater;
    private Context ctx;
    private String tag;
    private ArrayList<Integer> nseScripList;


    public SLBMHoldingAdapter(Context m_context, String tag) {
        inflater = LayoutInflater.from(m_context);
        this.ctx = m_context;
        this.tag = tag;
    }

    public ArrayList<TSLBMHoldingsReportReplyRecord> getHolding(){
        return holding;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.holdingequity_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (!tag.equalsIgnoreCase("equity")){
            holder.relativeLayout_bse.setVisibility(View.INVISIBLE);
            holder.relativeLayout_bsevalue.setVisibility(View.INVISIBLE);
        }
        holder.setValue(holding.get(position));
    }

    @Override
    public int getItemCount() {
        if (holding == null) return 0;
        return holding.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayout_bse) RelativeLayout relativeLayout_bse;
        @BindView(R.id.relativeLayout_bsevalue) RelativeLayout relativeLayout_bsevalue;

        @BindView(R.id.scrept_name_textview) TextView scripName;
        @BindView(R.id.total_vol_textview) TextView total;
        @BindView(R.id.nsc_vol_textview) TextView nseRate;
        @BindView(R.id.nse_value_total_textview) TextView nseTotal;
        @BindView(R.id.nse_textview) TextView nseTv;
        @BindView(R.id.nse_value_textview) TextView valueTV;
        @BindView(R.id.bse_vol_textview) TextView bseRate;
        @BindView(R.id.bse_value_total_textview) TextView bseTotal;
        @BindView(R.id.bse_textview) TextView bseTv;
        @BindView(R.id.bse_value_textview) TextView bseValueTV;

        private View view;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this,itemView);
        }

        public  void setValue(TSLBMHoldingsReportReplyRecord holdingRow){

            scripName.setText(holdingRow.scripName.getValue());
            total.setText(holdingRow.quantityToBeDelivered.getValue() + "");
            NumberFormat formatter = Formatter.getFormatter(eExch.SLBS.value);
            if(holdingRow.nSECode.getValue() > 0) {
                nseTv.setVisibility(View.VISIBLE);
                valueTV.setVisibility(View.VISIBLE);
                nseRate.setText("" + formatter.format(holdingRow.getNseRate()));
                nseTotal.setText("" + formatter.format(holdingRow.getNseTOTALRate()));
            }
        }
    }

    public void reloadData(ArrayList<TSLBMHoldingsReportReplyRecord> holding){
        this.holding = holding;
        loadScripList();
        notifyDataSetChanged();
    }
    private void loadScripList(){
        nseScripList = new ArrayList<>();
        for(int i=0;i<holding.size();i++){
            TSLBMHoldingsReportReplyRecord hld = holding.get(i);
            nseScripList.add(hld.nSECode.getValue());
            StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(hld.nSECode.getValue(),true);
            if (mktWatch != null) {
                hld.setNseRate(mktWatch.getLastRate());
            }
        }
    }

    public void refreshItem(int scripCode) {

        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,false);
        int position = -1;
        position = nseScripList.indexOf(scripCode);
        if(position >= 0) {
            TSLBMHoldingsReportReplyRecord hld = holding.get(position);
            hld.setNseRate(mktWatch.getLastRate());
            notifyItemChanged(position);
        }
    }
}
