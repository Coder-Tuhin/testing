package fragments.settingstab;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.List;

import Structure.Request.BC.ShortMktWatchRequest;
import adapters.DraggableRecyclerAdapter;
import butterknife.ButterKnife;
import butterknife.BindView;
import connection.SendDataToBCServer;
import enums.eLogType;
import interfaces.OnCustomerListChangedListener;
import interfaces.OnStartDragListener;
import models.GrabberModel;
import utils.GlobalClass;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.SimpleItemTouchHelperCallback;
import utils.UserSession;
import view.DualListView;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class Columnfragment extends Fragment implements OnStartDragListener, OnCustomerListChangedListener{
    @BindView(R.id.columns_recycler)RecyclerView columns_recycler;
    private DraggableRecyclerAdapter draggableRecyclerAdapter;
    private ItemTouchHelper mItemTouchHelper;

    public  Columnfragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = null;
        if (UserSession.getLoginDetailsModel().isActiveUser()) {
            layout = inflater.inflate(R.layout.columnsetting_screen, container, false);
            ButterKnife.bind(this, layout);
            initColumnSetting();
            ObjectHolder.isNeedDisplayChange = true;
        }else {
            layout = inflater.inflate(R.layout.deactive_account, container, false);
        }
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    private void initColumnSetting() {
        draggableRecyclerAdapter = new DraggableRecyclerAdapter(this,this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(draggableRecyclerAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(columns_recycler);
        columns_recycler.setAdapter(draggableRecyclerAdapter);
    }

    private ArrayList<GrabberModel> dragList;

    @Override
    public void onNoteListChanged(List<GrabberModel> columnpages) {
        dragList = new ArrayList<>(columnpages);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onPause() {
        super.onPause();
        DualListView.setColumnTag(-1);
        if (GlobalClass.broadCastReg.shortMktWatch.getValue() == 1){
            ShortMktWatchRequest smr = new ShortMktWatchRequest();
            smr.prevGrp.setValue(GlobalClass.groupHandler.prevGrpCode);
            smr.currGrp.setValue(GlobalClass.currGrpCode);
            smr.columnTag.setValue(DualListView.getColumnTag());
            smr.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            SendDataToBCServer sdbs = new SendDataToBCServer();
            sdbs.sendsShortMarketWatchReq(smr);
            GlobalClass.groupHandler.prevGrpCode = GlobalClass.currGrpCode;
        }
         if(dragList!=null) {
             PreferenceHandler.RefactorGraberList(dragList);
         }
    }

/*
    private void updatecolumnsetting(ArrayList<GrabberModel> columnpages) {
        ColumnsettingModel columnsettingModel = new ColumnsettingModel();
        columnsettingModel.setColumnpages(columnpages);
        GlobalClass.sharedPref.setColumnsetting(columnsettingModel);
    }
*/
}
