package view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.eMessageCodeWealth;
import utils.Formatter;
import utils.GlobalClass;
import wealth.new_mutualfund.menus.SipMandateForInvest;
import wealth.new_mutualfund.menus.modelclass.Mandatemodel;

public class BrokerageDialog extends Dialog implements View.OnClickListener {

    private JSONObject jsonResp;
    private BrokerageDialogAdapter brokerageDialogAdapter;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;

    private TextView dialogTitle,notebr;
    private ImageView closebrokeragebtn;

    private ArrayList<BrokerageListItem> mList;
    private ArrayList<BrokerageListItem> mList1;

    private String scripName;

    public BrokerageDialog(@NonNull Context context, JSONObject jobj, String _scripName) {
        super(context);
        try {
            this.jsonResp = jobj;
            this.mList = new ArrayList();
            this.mList1 = new ArrayList<>();
            this.scripName = _scripName;
            this.setContentView(R.layout.brokeragedialog);
            this.recyclerView1 = findViewById(R.id.brokerageRecycler1);
            this.recyclerView = findViewById(R.id.brokerageRecycler);
            this.dialogTitle = findViewById(R.id.brokeragetitle);
            this.notebr = findViewById(R.id.notebr);

            this.closebrokeragebtn = findViewById(R.id.closebrokeragebtn);
            this.setCancelable(false);
            prepareList();
            BrokerageDialogAdapter brokerageDialogAdapter = new BrokerageDialogAdapter();
            this.recyclerView.setAdapter(brokerageDialogAdapter);

            BrokerageDialogAdapter1 brokerageDialogAdapter1 = new BrokerageDialogAdapter1();
            this.recyclerView1.setAdapter(brokerageDialogAdapter1);

            this.closebrokeragebtn.setOnClickListener(this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private class BrokerageListItem{
        String title;
        String value;
        BrokerageListItem(String _title, String _value){
            this.title = _title;
            this.value = _value;
        }
    }
    private void prepareList(){
        try {
            mList.clear();
            mList1.clear();
            //String name = jsonResp.getString("Name");
            dialogTitle.setText("Charges Estimator : \n" + scripName);
            String SecondSideBkg= jsonResp.getString("SecondSideBkg");
            if(SecondSideBkg.equalsIgnoreCase("1")){
                notebr.setVisibility(View.VISIBLE);
                notebr.setText("Note : "+ jsonResp.getString("Note"));
            }else{
                notebr.setVisibility(View.GONE);
            }

            BrokerageListItem item1 = new BrokerageListItem("Qty",jsonResp.getInt("Qty")+"");
            /*
            BrokerageListItem item2 = new BrokerageListItem("Rate",Formatter.toTwoDecimalValue(jsonResp.getDouble("Rate"))+"");
            BrokerageListItem item3 = new BrokerageListItem("Brokerage",Formatter.toTwoDecimalValue(jsonResp.getDouble("Brokerage"))+"");
            BrokerageListItem item4 = new BrokerageListItem("Exchange Charges",Formatter.toTwoDecimalValue(jsonResp.getDouble("TransChgs"))+"");
            BrokerageListItem item5 = new BrokerageListItem("Securities Transaction Charges",Formatter.toTwoDecimalValue(jsonResp.getDouble("STT"))+"");
            BrokerageListItem item6 = new BrokerageListItem("Goods and Services Tax GST",Formatter.toTwoDecimalValue(jsonResp.getDouble("GST"))+"");
            BrokerageListItem item7 = new BrokerageListItem("SEBI Charges",Formatter.toTwoDecimalValue(jsonResp.getDouble("SEBIfees"))+"");
            BrokerageListItem item8 = new BrokerageListItem("Clearing Charges",Formatter.toTwoDecimalValue(jsonResp.getDouble("CMChgs"))+"");
            BrokerageListItem item9 = new BrokerageListItem("Stamp Duty",Formatter.toTwoDecimalValue(jsonResp.getDouble("StampDuty"))+"");
            */
            BrokerageListItem item2 = new BrokerageListItem("Rate",jsonResp.getString("Rate")+"");
            BrokerageListItem item3 = new BrokerageListItem("Brokerage",jsonResp.getString("Brokerage")+"");
            BrokerageListItem item4 = new BrokerageListItem("Exchange Charges",jsonResp.getString("TransChgs")+"");
            BrokerageListItem item5 = new BrokerageListItem("Securities Transaction Charges",jsonResp.getString("STT")+"");
            BrokerageListItem item6 = new BrokerageListItem("Goods and Services Tax GST",jsonResp.getString("GST")+"");
            BrokerageListItem item7 = new BrokerageListItem("SEBI Charges",jsonResp.getString("SEBIfees")+"");
            BrokerageListItem item8 = new BrokerageListItem("Clearing Charges",jsonResp.getString("CMChgs")+"");
            BrokerageListItem item9 = new BrokerageListItem("Stamp Duty",jsonResp.getString("StampDuty")+"");



            mList1.add(item1);
            mList1.add(item2);

            mList.add(item3);
            mList.add(item4);
            mList.add(item5);
            mList.add(item6);
            mList.add(item7);
            mList.add(item8);
            mList.add(item9);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
    }

    public class BrokerageDialogAdapter extends RecyclerView.Adapter<BrokerageDialogAdapter.MyViewHolder> {
        private LayoutInflater inflater;

        BrokerageDialogAdapter() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
        }

        @Override
        public BrokerageDialogAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.brokerageitem, parent, false);
            BrokerageDialogAdapter.MyViewHolder holder = new BrokerageDialogAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BrokerageDialogAdapter.MyViewHolder holder, final int position) {
            try {
                BrokerageListItem brokerageListItem = mList.get(position);
                holder.setData(brokerageListItem);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.britemtitle)
            TextView britemtitle;

            @BindView(R.id.britemvalue)
            TextView britemvalue;

            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
            public void setData(BrokerageListItem data){
                britemtitle.setText(data.title);
                britemvalue.setText(data.value);
            }
        }
    }

    public class BrokerageDialogAdapter1 extends RecyclerView.Adapter<BrokerageDialogAdapter1.MyViewHolder> {
        private LayoutInflater inflater;

        BrokerageDialogAdapter1() {
            inflater = LayoutInflater.from(GlobalClass.latestContext);
        }

        @Override
        public BrokerageDialogAdapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.brokerageitem, parent, false);
            BrokerageDialogAdapter1.MyViewHolder holder = new BrokerageDialogAdapter1.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(BrokerageDialogAdapter1.MyViewHolder holder, final int position) {
            try {
                BrokerageListItem brokerageListItem = mList1.get(position);
                holder.setData(brokerageListItem);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mList1.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.britemtitle)
            TextView britemtitle;

            @BindView(R.id.britemvalue)
            TextView britemvalue;

            public MyViewHolder(final View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
            public void setData(BrokerageListItem data){
                britemtitle.setText(data.title);
                britemvalue.setText(data.value);
            }
        }
    }
}