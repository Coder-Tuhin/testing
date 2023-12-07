package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.RC.StructHoldingsReportReplyRecord_Pointer;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.ventura.venturawealth.R;

import enums.eExch;
import utils.Formatter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/2/2016.
 */
public class HoldingEquityAdapter extends RecyclerView.Adapter<HoldingEquityAdapter.MyViewHolder> {
    ArrayList<StructHoldingsReportReplyRecord_Pointer> holding;
    private LayoutInflater inflater;
    private Context ctx;
    private String tag;
    private ArrayList<Integer> bseScripList;
    private ArrayList<Integer> nseScripList;


    public HoldingEquityAdapter(Context m_context, String tag) {
        inflater = LayoutInflater.from(m_context);
        this.ctx = m_context;
        this.tag = tag;
    }

    public ArrayList<StructHoldingsReportReplyRecord_Pointer> getHolding(){
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

        public  void setValue(StructHoldingsReportReplyRecord_Pointer holdingRow){

            scripName.setText(holdingRow.scripName.getValue());
            total.setText(holdingRow.totalQty.getValue() + "");
            NumberFormat formatter = Formatter.getFormatter(holdingRow.exchange);
            if(holdingRow.nSECode.getValue() > 0) {
                nseTv.setVisibility(View.VISIBLE);
                valueTV.setVisibility(View.VISIBLE);
                nseRate.setText("" + formatter.format(holdingRow.getNseRate()));
                nseTotal.setText("" + formatter.format(holdingRow.getNseTOTALRate()));
            }
            else{
                nseTv.setVisibility(View.GONE);
                valueTV.setVisibility(View.GONE);
                nseRate.setText("");
                nseTotal.setText("");
            }
            if(tag.equalsIgnoreCase("equity") && holdingRow.bSECode.getValue() > 0) {
                bseTv.setVisibility(View.VISIBLE);
                bseValueTV.setVisibility(View.VISIBLE);
                bseRate.setText(""+ formatter.format(holdingRow.getBseRate()));
                bseTotal.setText(""+ formatter.format(holdingRow.getBseTOTALRate()));
            }else{
                bseTv.setVisibility(View.GONE);
                bseValueTV.setVisibility(View.GONE);
                bseRate.setText("");
                bseTotal.setText("");
            }
        }
    }

    public void reloadData(ArrayList<StructHoldingsReportReplyRecord_Pointer> holding){
        this.holding = holding;
        loadScripList();
        notifyDataSetChanged();
    }
    private void loadScripList(){
        bseScripList = new ArrayList<>();
        nseScripList = new ArrayList<>();
        for(int i=0;i<holding.size();i++){
            StructHoldingsReportReplyRecord_Pointer hld = holding.get(i);
            if(hld.bSECode.getValue() > 0) {
                bseScripList.add(hld.bSECode.getValue());
            }else{
                bseScripList.add(i);
            }
            if(hld.nSECode.getValue() > 0){
                nseScripList.add(hld.nSECode.getValue());
            }else{
                nseScripList.add(i);
            }
        }
    }


    public void refreshItem(int scripCode) {

        StaticLiteMktWatch mktWatch = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
        int position = -1;
        if(mktWatch.getSegment() == eExch.BSE.value){
            position = bseScripList.indexOf(scripCode);
        }
        else{
            position = nseScripList.indexOf(scripCode);
        }
        if(position != -1) {
            StructHoldingsReportReplyRecord_Pointer hld = holding.get(position);
            if(mktWatch.getSegment() == eExch.BSE.value){
                hld.setBseRate(mktWatch.getLastRate());
            }else{
                hld.setNseRate(mktWatch.getLastRate());
            }
            notifyItemChanged(position);
        }
    }
}
