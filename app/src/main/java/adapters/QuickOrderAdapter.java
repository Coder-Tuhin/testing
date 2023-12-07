package adapters;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import Structure.Other.StructBuySell;
import Structure.Response.Group.GroupsTokenDetails;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;

import enums.eOrderType;
import enums.eShowDepth;
import fragments.homeGroups.QuickOrderDepth;
import interfaces.OnOrderSave;
import models.BuySellModel;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.StaticMethods;
import utils.VenturaException;

/**
 * Created by XTREMSOFT on 12/26/2016.
 */
public class QuickOrderAdapter extends BaseAdapter implements OnOrderSave{
    private ArrayList<BuySellModel> mList = new ArrayList<>();
    private LayoutInflater mLayoutInflater = null;
  //  private OnNavClick onNavClick;
    public QuickOrderAdapter() {
        mLayoutInflater = (LayoutInflater)LayoutInflater.from(GlobalClass.latestContext);
                //GlobalClass.latestContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     //   this.onNavClick = onNavClick;
        GlobalClass.onOrderSave = this;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.navigation_recycleritem, parent, false);
        }
        ((ImageView) view.findViewById(R.id.nav_item_image)).setVisibility(View.GONE);
        TextView navigation_itemtext = (TextView) view.findViewById(R.id.navigation_itemtext);
        navigation_itemtext.setText(mList.get(position).getOrderType()+" "+mList.get(position).getQty()+
                " "+mList.get(position).getScriptName()+" @"+mList.get(position).getPrice());
        navigation_itemtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMktDepth(mList.get(position).getScriptCode(),mList.get(position).getScriptName(),mList.get(position));
                ((DrawerLayout)((Activity) GlobalClass.latestContext).findViewById(R.id.drawer_layout)).closeDrawers();

               // onNavClick.onNavClick();
            }
        });
        return view;
    }

    public void refreshesAdapter(){
        ArrayList<BuySellModel> _tempList = new ArrayList<>(PreferenceHandler.getQuickOrderList().values());
        mList.clear();
        for (BuySellModel bsm : _tempList){
            if (bsm.isShow())mList.add(bsm);
        }
        notifyDataSetChanged();
    }

    private void showMktDepth(final int scripCode, String scripName, BuySellModel bsm) {
        try {
            GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
            groupsTokenDetails.scripCode.setValue(scripCode);
            groupsTokenDetails.scripName.setValue(scripName);
            final ArrayList<GroupsTokenDetails> grplist = new ArrayList<>();
            grplist.add(groupsTokenDetails);
            final StructBuySell sbs = new StructBuySell();
            int _qtyVal = bsm.getQty().length()>0? StaticMethods.StringToInt(bsm.getQty()):0;
            sbs.qty.setValue(_qtyVal);
            double _limitPrice = bsm.getPrice().length()>0?StaticMethods.StringToDouble(bsm.getPrice()):0;
            sbs.limitPrice.setValue(_limitPrice);
            int _discQtyVal = bsm.getDiscQty().length()>0?StaticMethods.StringToInt(bsm.getDiscQty()):0;
            sbs.discloseQty.setValue(_discQtyVal);
            double _tiggerPrice = bsm.getTiggerPrice().length()>0?StaticMethods.StringToDouble(bsm.getTiggerPrice()):0;
            sbs.triggerPrice.setValue(_tiggerPrice);
            sbs.fromSave = true;
            sbs.delvIntra = bsm.isIntraDay();
            eOrderType eOrderType = enums.eOrderType.BUY;
            if (bsm.getOrderType().equalsIgnoreCase("SEL"))eOrderType = enums.eOrderType.SELL;
            sbs.buyOrSell = eOrderType;
            sbs.modifyOrPlace = enums.eOrderType.PLACE;
            sbs.isIoc.setValue(bsm.isIOC());
            sbs.isMarket.setValue(bsm.isMkt());
            sbs.isStopLoss.setValue(bsm.isStopLoss());
            Fragment m_fragment = new QuickOrderDepth(scripCode,eShowDepth.MKTWATCH,grplist,
                    sbs, HomeActivity.RadioButtons.WATCH,false);
            GlobalClass.fragmentTransaction(m_fragment,R.id.container_body,false,"");
        }catch (Exception e){
            VenturaException.Print(e);
          //  e.printStackTrace();
        }
    }

    @Override
    public void onOrderSave() {
        refreshesAdapter();
    }
}
