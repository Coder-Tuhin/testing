package com.ventura.venturawealth.activities.homescreen;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.BaseActivity;

import java.util.ArrayList;

import utils.GlobalClass;
import utils.ObjectHolder;
import utils.VenturaException;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ExpandableListMenuAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<NavigationMenuHandler.NavMenus> mList = new ArrayList<>();

    public ExpandableListMenuAdapter() {
        this.context = GlobalClass.latestContext;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.mList.get(listPosition)._childList.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.list_navmenu_child, null);
        }
        NavigationMenuHandler.NavChildMenus ncm = mList.get(listPosition)._childList.get(expandedListPosition);
        TextView title = convertView.findViewById(R.id.title);
        ImageView icon = convertView.findViewById(R.id.icon);
        title.setText(ncm._title);
        icon.setImageResource(ncm._icon);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        if (this.mList.get(listPosition)._childList == null) return 0;
        return this.mList.get(listPosition)._childList.size();
    }

    @Override
    public Object getGroup(int listPosition) {
       // return this.expandableListTitle.get(listPosition);
        if (this.mList.size()>listPosition){
            return this.mList.get(listPosition);
        }
        return null;
    }

    @Override
    public int getGroupCount() {
        return this.mList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                //LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater layoutInflater = LayoutInflater.from(this.context);
                convertView = layoutInflater.inflate(R.layout.list_navmenu, null);
            }
            NavigationMenuHandler.NavMenus _navMenus = this.mList.get(listPosition);
            TextView title = convertView.findViewById(R.id.title);
            ImageView icon = convertView.findViewById(R.id.icon);
            title.setText(_navMenus._title);
            title.setSelected(true);
            icon.setImageResource(_navMenus._icon);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
//        int _iconColor = _navMenus==NavigationMenuHandler.NavMenus.ACTIVATE_MARGIN_TRADING?
//                ObjectHolder.VENTURA:ObjectHolder.WHITE;
//        icon.setColorFilter(_iconColor);
//        if (_navMenus==NavigationMenuHandler.NavMenus.ACTIVATE_MARGIN_TRADING){
//            title.setBackgroundResource(R.drawable.activate_margin);
//            title.setText("");
//            icon.setImageResource(0);
//        }else {
//            title.setBackgroundResource(0);
//            title.setText(_navMenus._title);
//            icon.setImageResource(_navMenus._icon);
//        }
        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


    public void RefreshAdapter(ArrayList<NavigationMenuHandler.NavMenus> mList){
        this.mList.clear();
        this.mList.addAll(mList);
        notifyDataSetChanged();
    }

    public void ClearAdapter(){
        this.mList.clear();
        notifyDataSetChanged();
    }

    public NavigationMenuHandler.NavMenus getModel(int position){
        if (this.mList.size()>position){
            return this.mList.get(position);
        }
        return null;
    }


    private View getGroupView(ExpandableListView listView, int groupPosition) {
        try {
            long packedPosition = ExpandableListView.getPackedPositionForGroup(groupPosition);
            int flatPosition = listView.getFlatListPosition(packedPosition);
            int first = listView.getFirstVisiblePosition();
            return listView.getChildAt(flatPosition - first);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return null;
    }

    public void animateExpand(ExpandableListView listView, int position) {
        try {
            View convertView = getGroupView(listView,position);
            if (convertView!=null){
                RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(300);
                rotate.setFillAfter(true);
                ImageView arrow = convertView.findViewById(R.id.arrow);
                arrow.setAnimation(rotate);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    public void animateCollapse(ExpandableListView listView, int position) {
        try {
            View convertView = getGroupView(listView,position);
            if (convertView!=null){
                RotateAnimation rotate = new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(300);
                rotate.setFillAfter(true);
                ImageView arrow = convertView.findViewById(R.id.arrow);
                arrow.setAnimation(rotate);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }
}
