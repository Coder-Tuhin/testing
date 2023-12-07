package adapters;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.ventura.venturawealth.R;

import interfaces.OnAdapterRefresh;
import models.MessageModel;
import utils.GlobalClass;
import utils.PreferenceHandler;

/**
 * Created by SUDIP on 06-12-2016.
 */
public class TotalMsgAdapter extends RecyclerView.Adapter<TotalMsgAdapter.MyViewHolder>{
    private List<MessageModel> messagelist;
    private LayoutInflater inflater;
    private OnAdapterRefresh onAdapterRefresh;

    public TotalMsgAdapter(OnAdapterRefresh onAdapterRefresh){
        inflater = LayoutInflater.from(GlobalClass.latestContext);
        messagelist = new ArrayList<>();
        this.onAdapterRefresh = onAdapterRefresh;
    }

    @Override
    public TotalMsgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.message_body.setText(messagelist.get(position).getMessage());
        holder.message_time.setText(messagelist.get(position).getTime());
        holder.message_body.setTextColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_body)TextView message_body;
        @BindView(R.id.message_time)TextView message_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void refreshAdapter(String type){
        messagelist.clear();
        ArrayList<MessageModel> _messageList = PreferenceHandler.getMessageList();
        if (type.equalsIgnoreCase("All")){
            messagelist.addAll(_messageList);
        }else if (type.equalsIgnoreCase("Trade") || type.equalsIgnoreCase("Placed") ||
                type.equalsIgnoreCase("Cancelled")){
            for (MessageModel messageModel : _messageList){
                if (messageModel.getMessage().toLowerCase().contains(type.toLowerCase())){
                    messagelist.add(messageModel);
                }
            }
        }else{
            for (MessageModel messageModel : _messageList){
                String msg = messageModel.getMessage().toLowerCase();
                if (!msg.contains("trade") || !msg.contains("placed") ||
                        !msg.contains("cancelled")){
                    messagelist.add(messageModel);
                }
            }
        }
        boolean hasMsg = messagelist.size() > 0;
        onAdapterRefresh.onMessageRefresh(hasMsg);
        Collections.reverse(messagelist);
        notifyDataSetChanged();
    }
}
