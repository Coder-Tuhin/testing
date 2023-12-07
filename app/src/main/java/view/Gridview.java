package view;

import android.app.Activity;
import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import Structure.Response.BC.StaticLiteMktWatch;
import Structure.Response.Group.GroupsRespDetails;
import Structure.Response.Group.GroupsTokenDetails;

import com.ventura.venturawealth.activities.homescreen.HomeActivity;
import com.ventura.venturawealth.R;
import connection.ReqSent;
import enums.eMessageCode;
import enums.eShowDepth;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 8/19/2016.
 */
public class Gridview extends LinearLayout implements View.OnClickListener, ReqSent,OnLongClickListener {

    //region [Variables]
    private ArrayList<GroupsTokenDetails> m_grpScripList;
    private GridView m_mktGrid;
    private MktAdapter m_mktAdapter;
    private Context m_context;
    private eShowDepth m_depthFrom;
    private ArrayList<Integer> m_scripCodeList;
    private GroupsRespDetails m_groupsRespDetails;
    private final String m_className =getClass().getName();
    private HomeActivity.RadioButtons radioButtons;
    //endregion

    //region [Constructor]
    public Gridview(final Context context, final GroupsRespDetails grpRespDetail, final SwipeRefreshLayout refreshLayout,
                    eShowDepth depthFrom, HomeActivity.RadioButtons radioButtons) {
        super(context);
        try {
            addView(LayoutInflater.from(context).inflate(R.layout.grid_layout, null));
            this.m_context = context;
            this.m_depthFrom = depthFrom;
            this.m_grpScripList = new ArrayList<>();
            this.m_groupsRespDetails = grpRespDetail;
            this.radioButtons = radioButtons;
            m_grpScripList.addAll(grpRespDetail.hm_grpTokenDetails.values());

            this.m_scripCodeList = new ArrayList<>();
            m_scripCodeList.addAll(grpRespDetail.hm_grpTokenDetails.keySet());

            m_mktAdapter = new MktAdapter(context);
            m_mktGrid = (GridView) findViewById(R.id.mktGrid);
            m_mktGrid.setAdapter(m_mktAdapter);
            if (GlobalClass.gridselection<m_groupsRespDetails.hm_grpTokenDetails.size()){
                m_mktGrid.setSelection(GlobalClass.gridselection);
            }
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) m_context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            if(displaymetrics.widthPixels > displaymetrics.heightPixels){
                m_mktGrid.setNumColumns(4);
            }else {
                m_mktGrid.setNumColumns(2);
            }
            m_mktGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                    GlobalClass.gridselection = i;
                    if(refreshLayout!=null) {
                        int topRowVerticalPosition = (absListView == null || absListView.getChildCount() == 0) ?
                                0 : absListView.getChildAt(0).getTop();
                        refreshLayout.setEnabled((topRowVerticalPosition >= 0));
                    }
                }
            });
            GlobalClass.dismissdialog();

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }
    //endregion

    //region [Override Method]

    @Override
    public void onClick(View view) {

    }

    //endregion

    //region [Public Methods]
    private void showMktDepth(int scripCode) {
        ArrayList<GroupsTokenDetails> grpScripList = new ArrayList<>();
        grpScripList.addAll(m_grpScripList);
        GlobalClass.openDepth(scripCode,m_depthFrom,grpScripList,null);
//        Fragment m_fragment = new MktdepthFragment(scripCode,m_depthFrom,grpScripList,null);
//        FragmentTransaction fragmentTransaction = GlobalClass.fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.container_body, m_fragment);
//        fragmentTransaction.addToBackStack("");
//        fragmentTransaction.commit();
    }
    public void notifyDataSetChanged(final GroupsRespDetails grpResp) {
        try {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        m_grpScripList.clear();
                        m_grpScripList.addAll(grpResp.hm_grpTokenDetails.values());
                        m_scripCodeList.clear();
                        m_scripCodeList.addAll(grpResp.hm_grpTokenDetails.keySet());
                        m_mktAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        GlobalClass.onError("Error in " + m_className, e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callRefreshData(eMessageCode emessagecode, int scripCode) {
        try {
            GridView gridView = m_mktGrid;
            int start = gridView.getFirstVisiblePosition();
            int index = m_scripCodeList.indexOf(scripCode);
            int last = gridView.getLastVisiblePosition();
            if (index >= start && index <= last) {
                refreshData(emessagecode, index, scripCode);
            }
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    public void refreshData(eMessageCode emessagecode, int index, int scripCode) {
        try {
            View v = m_mktGrid.getChildAt(index - m_mktGrid.getFirstVisiblePosition());
            if (v != null) {
                //if (!isBidOffOnly) {
                StaticLiteMktWatch data = null;
                try {
                    data = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (data != null) {
                    NumberFormat formatter = data.getFormatter();
                    double absChange = data.getAbsChg(), lastRate = data.getLastRate();
                    TextView txtLtp = (TextView) v.findViewById(R.id.txtLtp);
                    if (txtLtp != null) {
                        double preval = 0;
                        try {
                            preval = formatter.parse(txtLtp.getText().toString()).doubleValue();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        ImageView arrImageView = (ImageView) v.findViewById(R.id.arrow);
                        if (preval != 0) {
                            if (preval < lastRate) {
                                arrImageView.setImageResource(R.drawable.up);

                            } else if (preval > lastRate) {
                                arrImageView.setImageResource(R.drawable.down);

                            }
                        }
                        txtLtp.setText( ""+formatter.format(lastRate) );
                    }
                    TextView txtPerChange = (TextView) v.findViewById(R.id.txtPerChange);

                    if (txtPerChange != null) {
                        double perChange = data.getPerChg();
                        String chg = formatter.format(perChange) + "%";
                        txtPerChange.setText(chg);
                        if (chg.contains("-")) {
                            txtPerChange.setTextColor(getResources().getColor(R.color.red));
                        } else if (chg.equalsIgnoreCase("0.00%")) {
                            txtPerChange.setTextColor(getResources().getColor(R.color.black));
                        } else {
                            txtPerChange.setTextColor(getResources().getColor(R.color.green1));
                        }
                    }
                    TextView txtVolume = (TextView) v.findViewById(R.id.txtVolume);//volume
                    txtVolume.setText(data.getLw().totalQty.getValue() + "");

                    TextView txtAvg = (TextView) v.findViewById(R.id.txtAvg);
                    txtAvg.setText(formatter.format(data.getAverage()));//Average

                    TextView txtChange = (TextView) v.findViewById(R.id.txtChange);
                    String strAbsChg = formatter.format(absChange);
                    if (strAbsChg.contains("-")) {
                        txtChange.setText(strAbsChg);
                    } else if (strAbsChg.equalsIgnoreCase("0.00%")) {
                        txtChange.setText(strAbsChg);
                    } else {
                        txtChange.setText("+" + strAbsChg);
                    }
                }
            }

        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    public void notifyDataSetChanged(String methodName) {
        try {
            GlobalClass.log("MethodName : " + methodName);
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    m_mktAdapter.notifyDataSetChanged();
                    m_mktGrid.invalidateViews();
                    m_mktGrid.setAdapter(m_mktAdapter);
                }
            });
        } catch (Exception e) {
            GlobalClass.onError("Error in " + m_className, e);
        }
    }

    @Override
    public void reqSent(int msgCode) {

    }

    @Override
    public void onDelete(int position) {
        m_scripCodeList.remove(position);
        m_grpScripList.remove(position);
        m_mktAdapter.notifyDataSetChanged();
    }
    //endregion

    // region [Inner Class]
    public class MktAdapter extends BaseAdapter{
        Context mContext;

        public MktAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return m_grpScripList.size();
        }

        public long getItemId(int position) {
            return position;
        }

        public Object getItem(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            try {
                ViewHolder holder;
                if (convertView == null) {
                    LayoutInflater li = LayoutInflater.from(mContext);
                    convertView = li.inflate(R.layout.grid_item, null);
                    holder = new ViewHolder();
                    holder.txtScripName = (TextView) convertView.findViewById(R.id.txtScripName);
                    holder.txtLtp = (TextView) convertView.findViewById(R.id.txtLtp);
                    holder.txtPerChange = (TextView) convertView.findViewById(R.id.txtPerChange);
                    holder.txtVolume = (TextView) convertView.findViewById(R.id.txtVolume);
                    holder.txtAvg = (TextView) convertView.findViewById(R.id.txtAvg);
                    holder.txtChange = (TextView) convertView.findViewById(R.id.txtChange);
                    holder.arrImageView = (ImageView) convertView.findViewById(R.id.arrow);
                    holder.main = (LinearLayout) convertView.findViewById(R.id.main);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                GroupsTokenDetails gsd = m_grpScripList.get(position);
                holder.txtScripName.setText(gsd.scripName.getValue());
                final int scripCode = gsd.scripCode.getValue();
                convertView.setId(position);
                holder.txtScripName.setId(position);
                holder.main.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showMktDepth(m_scripCodeList.get(position));
                    }
                });
                holder.main.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        LongclickPopupMenu longclickPopupMenu = new LongclickPopupMenu(m_groupsRespDetails,radioButtons,Gridview.this);
                        int scripCode = m_scripCodeList.get(position);
                        String scripName = m_groupsRespDetails.hm_grpTokenDetails.get(scripCode).scripName.getValue();
                        longclickPopupMenu.showItemOptions(null,view,scripCode,scripName, eShowDepth.MKTWATCH,position);
                        return false;
                    }
                });
                StaticLiteMktWatch data = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
                if (data != null) {
                    holder.setInitValue(data);
                }
            }catch (Exception e){
                GlobalClass.onError("Error in " + m_className, e);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView txtScripName, txtLtp, txtPerChange, txtChange, txtVolume, txtAvg;
            ImageView arrImageView;
            LinearLayout main;

            public void setInitValue(StaticLiteMktWatch data) {
                try {

                    NumberFormat formatter = data.getFormatter();
                    double absChange = data.getAbsChg(), lastRate = 0;
                    lastRate = data.getLastRate();
                    txtLtp.setText("" +formatter.format(lastRate));

                    double perChange = data.getPerChg();
                    String chg = formatter.format(perChange) + "%";
                    txtPerChange.setText(chg);

                    if (chg.contains("-")) {
                        txtPerChange.setTextColor(getResources().getColor(R.color.red));
                    } else if (chg.equalsIgnoreCase("0.00%")) {
                        txtPerChange.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        txtPerChange.setTextColor(getResources().getColor(R.color.green1));
                    }
                    String strAbsChg = formatter.format(absChange);
                    if (strAbsChg.contains("-")) {
                        txtChange.setText(strAbsChg);
                    } else if (strAbsChg.equalsIgnoreCase("0.00%")) {
                        txtChange.setText(strAbsChg);
                    } else {
                        txtChange.setText("+" + strAbsChg);
                    }
                    txtVolume.setText(data.getLw().totalQty.getValue() + "");//volume
                    txtAvg.setText(formatter.format(data.getAverage()));//average

                }catch (Exception e){
                    GlobalClass.onError("Error in " + m_className, e);
                }
            }
        }
    }
    //endregion
}
