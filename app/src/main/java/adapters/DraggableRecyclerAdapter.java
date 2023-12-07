package adapters;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.BindView;
import enums.WatchColumns;
import interfaces.ItemTouchHelperAdapter;
import interfaces.ItemTouchHelperViewHolder;
import interfaces.OnCustomerListChangedListener;
import interfaces.OnStartDragListener;
import models.GrabberModel;
import utils.PreferenceHandler;

/**
 * Created by XTREMSOFT on 10/20/2016.
 */
public class DraggableRecyclerAdapter extends
        RecyclerView.Adapter<DraggableRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    public ArrayList<GrabberModel> grabberlist;
    private OnStartDragListener mDragStartListener = null;
    private OnCustomerListChangedListener mListChangedListener = null;

    public DraggableRecyclerAdapter(OnStartDragListener dragLlistener,
                                    OnCustomerListChangedListener listChangedListener){
        grabberlist = PreferenceHandler.getGraberList();
        mDragStartListener = dragLlistener;
        mListChangedListener = listChangedListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grabber_item, parent, false);
        return new ItemViewHolder(rowView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final GrabberModel gm = grabberlist.get(position);
        holder.dragger_textview.setText(gm.getGrabbername());
        if (gm.ischecked()){
            holder.dragger_checkbox.setChecked(true);
        }else {
            holder.dragger_checkbox.setChecked(false);
        }
        if (gm.getGrabbername().equals(WatchColumns.CHG.name)
                || gm.getGrabbername().equals(WatchColumns.CURRENT.name)){
            holder.dragger_checkbox.setEnabled(false);
        }else {
            holder.dragger_checkbox.setEnabled(true);
        }
        holder.dragger_imageview.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder);
            }
            return false;
        });
    }


    @Override
    public int getItemCount() {
        return grabberlist.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(grabberlist, fromPosition, toPosition);
        mListChangedListener.onNoteListChanged(grabberlist);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        @BindView(R.id.dragger_textview)TextView dragger_textview;
        @BindView(R.id.dragger_imageview)ImageView dragger_imageview;
        @BindView(R.id.dragger_checkbox)CheckBox dragger_checkbox;
        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            dragger_checkbox.setOnCheckedChangeListener((compoundButton, ischecked) -> {
                if (grabberlist!=null && getAdapterPosition()>=0){
                    GrabberModel gm = grabberlist.get(getAdapterPosition());
                    if (!gm.getGrabbername().equals(WatchColumns.CHG.name)
                            || !gm.getGrabbername().equals(WatchColumns.CURRENT.name)){
                        gm.setchecked(ischecked);
                        PreferenceHandler.setGraberList();
                    }
                }
            });
        }
        @Override
        public void onItemSelected() {
        }
        @Override
        public void onItemClear() {
            //itemView.setBackgroundColor(0);
        }
    }
}
