package adapters;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.util.List;

import Structure.Response.RC.StructmarginTrade;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Formatter;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 10-Oct-2017.
 */
public class MarginTradeAdapter  extends RecyclerView.Adapter<MarginTradeAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private List<StructmarginTrade> mList;
    private boolean isFundAdapter;

    public MarginTradeAdapter(List<StructmarginTrade> mList,boolean isFundAdapter){
        inflater = LayoutInflater.from(GlobalClass.latestContext);
        this.mList = mList;
        this.isFundAdapter = isFundAdapter;
    }

    @Override
    public MarginTradeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.margine_trading_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MarginTradeAdapter.MyViewHolder holder, int position) {
        if (position %2 == 0){
            holder.root.setBackgroundColor(Color.rgb(25, 22, 17));
        }else {
            holder.root.setBackgroundColor(Color.BLACK);
        }
        StructmarginTrade smt = mList.get(position);
        holder.scripname.setText(smt.scripName.getValue());
        if (isFundAdapter){
           holder.qty.setText(smt.fundedStockQty.getValue()+"");
           holder.value.setText(Formatter.toTwoDecimalValue(smt.fundedStockValue.getValue())+"");
        }else {
            holder.qty.setText(smt.callateralStockQty.getValue()+"");
            holder.value.setText(Formatter.toTwoDecimalValue(smt.callateralStockValue.getValue())+"");
        }
    }

    @Override
    public int getItemCount() {
        if (mList==null)return 0;
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.scripname)TextView scripname;
        @BindView(R.id.qty)TextView qty;
        @BindView(R.id.value)TextView value;
        @BindView(R.id.root)LinearLayout root;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void refreshAdapter(List<StructmarginTrade> mList,boolean isFundAdapter){
        this.mList = mList;
        this.isFundAdapter = isFundAdapter;
        notifyDataSetChanged();
    }

}
