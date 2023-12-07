package view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 11/23/2016.
 */
public class Results_DualListView extends LinearLayout {
    double divisionFactor = 5;
    ListView NameListView, ValuesListView;
    int width, titleWidth;
    LinearLayout name_main, layout_vales_title;
    String[] listviewdata;
    boolean isLeftListEnabled = true;
    boolean isRightListEnabled = true;
    HashMap<String, ArrayList> addedLatestResults;

    ArrayList<String> title;
    private SyncedScrollListener name_syncScrollListener;
    private SyncedScrollListener values_syncScrollListener;
    private boolean sorting_enable = false;
    private Stack<String> stack;
    int extraHeight;
    Context context;
    public static Vector newColumnItems = null;
    Vector<String> reqCodeVector;
    String mth[] = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public Results_DualListView(final Context context, HashMap<String, ArrayList> tokenNameCollection) {
        super(context);
        try {
            setOrientation(HORIZONTAL);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            width = width - GlobalClass.intTODP(context, 7);

            titleWidth = (int) (Math.floor(width / divisionFactor));
            extraHeight = GlobalClass.intTODP(context, 27);

            title = new ArrayList<String>();
            title.add("Sales");
            title.add("Other Income");
            title.add("PBIDT");
            title.add("Interest");
            title.add("PBDT");
            title.add("Depreciation");
            title.add("PBT");
            title.add("TAX");
            title.add("Deferred Tax");
            title.add("PAT");
            title.add("Equity");

            setTokenCollection(tokenNameCollection);


            initListViewData();

            this.context = context;
            //init 2 main LL
            name_main = new LinearLayout(context);
            //name_main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            name_main.setOrientation(VERTICAL);
            //this.addView(name_main);

            HorizontalScrollView hsv_names = new HorizontalScrollView(context);
            hsv_names.addView(name_main);
            hsv_names.setLayoutParams(new LayoutParams((2 * (int) (Math.floor(width / 6))), LayoutParams.MATCH_PARENT));
            //hsv_names.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            hsv_names.setHorizontalScrollBarEnabled(true);
            //hsv_names.setHorizontalFadingEdgeEnabled(true);
            this.addView(hsv_names);

            LinearLayout values_main = new LinearLayout(context);
            values_main.setOrientation(VERTICAL);
            values_main.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

            //HSV for Scrollable view
            HorizontalScrollView hsv = new HorizontalScrollView(context);
            hsv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            hsv.addView(values_main);
            hsv.setHorizontalScrollBarEnabled(true);
            //hsv.setHorizontalFadingEdgeEnabled(true);
            this.addView(hsv);

            //LayoutParams params_names = new LayoutParams((2 * (int) (Math.floor(width / divisionFactor))), LayoutParams.WRAP_CONTENT);
            LayoutParams params_names = new LayoutParams(LayoutParams.WRAP_CONTENT, extraHeight);
            //LayoutParams params_names = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            LinearLayout layout_name_title = new LinearLayout(context);
            //layout_name_title.setLayoutParams(params_names);
            layout_name_title.setOrientation(VERTICAL);

            TitleEditText tv = new TitleEditText(getContext(), "   ", Gravity.LEFT, (2 * (int) (Math.floor(width / divisionFactor))), Color.rgb(196, 196, 196));
            tv.setTag("Name");
            tv.setLayoutParams(params_names);
            //tv.setOnClickListener(this);
            layout_name_title.addView(tv);

            TitleEditText tv1 = new TitleEditText(getContext(), "   ", Gravity.LEFT, (2 * (int) (Math.floor(width / divisionFactor))), Color.rgb(196, 196, 196));
            tv1.setLayoutParams(params_names);
            layout_name_title.addView(tv1);

            name_main.addView(layout_name_title);

            TextView sep = new TextView(context);
            sep.setHeight(1);
            sep.setBackgroundColor(Color.LTGRAY);
            name_main.addView(sep);

            //Title List for data
            layout_vales_title = new LinearLayout(context);
            int height = layout_vales_title.getHeight();
            layout_vales_title.setOrientation(VERTICAL);
            values_main.addView(layout_vales_title);

            /*name_main.setMinimumHeight(height);
            name_main.invalidate();*/

            sep = new TextView(context);
            sep.setHeight(1);
            sep.setBackgroundColor(Color.LTGRAY);
            values_main.addView(sep);

            //LayoutParams params = new LayoutParams((int) (Math.floor(width / 3)), extraHeight);


            LinearLayout header2Layout = new LinearLayout(context);
            header2Layout.setOrientation(HORIZONTAL);
            LayoutParams params = new LayoutParams((int) (Math.floor(width / divisionFactor)), extraHeight);
            params.weight = 1;
            addHeader(params);
            for (int i = 0; i < listviewdata.length; i++) {
                String title = listviewdata[i];
                TitleEditText tty = new TitleEditText(getContext(), title, Gravity.RIGHT, (int) (Math.floor(width / 5)), Color.rgb(196, 196, 196));
                //tty.setOnClickListener(this);
                tty.setTag(listviewdata[i]);
                tty.setLayoutParams(params);
                header2Layout.addView(tty);
                //layout_vales_title.addView(tty);
            }
            layout_vales_title.addView(header2Layout);

            name_syncScrollListener = new SyncedScrollListener("NAMElist");
            values_syncScrollListener = new SyncedScrollListener("VALUELIST");

            NameListView = new ListView(context);
            NameListView.setDivider(new ColorDrawable(Color.argb(60, 255, 255, 255)));
            NameListView.setDividerHeight(1);
            //NameListView.setLayoutParams(params_names);
            NameListView.setVerticalScrollBarEnabled(false);
            NameListView.setOnScrollListener(name_syncScrollListener);
            NameListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

            ValuesListView = new ListView(context);
            ValuesListView.setDivider(new ColorDrawable(Color.argb(60, 255, 255, 255)));
            ValuesListView.setDividerHeight(1);
            ValuesListView.setVerticalScrollBarEnabled(false);
            ValuesListView.setOnScrollListener(values_syncScrollListener);
            ValuesListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
            //ValuesListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.FILL_PARENT));

            name_main.addView(NameListView);
            values_main.addView(ValuesListView);

            onCreate();

            //initListViewData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHeader(LayoutParams params){
        ArrayList<String> keySet = new ArrayList<>(addedLatestResults.keySet());
        if (keySet!= null && keySet.size()>0){
            ArrayList<String > data = addedLatestResults.get(keySet.get(0  ));
            int headerPosition = (data.size()/3);
            String headerTitle[] ={"Quarter ended","Year to Date","Year Ended"};
            if (headerPosition>0 && headerPosition<=headerTitle.length){
                LinearLayout header1Layout = new LinearLayout(context);
                header1Layout.setOrientation(HORIZONTAL);
                for (int i = 0; i < headerPosition; i++) {
                    String title = headerTitle[i];
                    TitleEditText tty1 = new TitleEditText(getContext(),title , Gravity.CENTER,(3 * (int) (Math.floor(width / 5))), Color.rgb(196, 196, 196));
                    //tty1.setOnClickListener(this);
                    tty1.setTag(listviewdata[i]);
                    tty1.setLayoutParams(params);
                    header1Layout.addView(tty1);

                }
                layout_vales_title.addView(header1Layout);
            }
        }

    }


    protected void onCreate() {
        try {
            listViewCustomAdaptor adaptor = new listViewCustomAdaptor(this.getContext(), 0, 0);
            NameListView.setAdapter(new listViewCustomAdaptor(this.getContext(), 0, 0));
            ValuesListView.setAdapter(adaptor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTokenCollection(HashMap<String, ArrayList> tokenNameCollection) {
        try {
            this.addedLatestResults = tokenNameCollection;
            Object[] tokenArr = tokenNameCollection.keySet().toArray();
            if (NameListView != null) {
                notifyDataSetChanged("setTokenCollection");
            }
            /*if (NameListView != null) {
                notifyDataSetChanged("setTokenCollection");
                *//*((Activity) getContext()).runOnUiThread(new Runnable() {
                    public void run() {
                        ((listViewCustomAdaptor) NameListView.getAdapter()).notifyDataSetChanged();
                        ((listViewCustomAdaptor) ValuesListView.getAdapter()).notifyDataSetChanged();
                    }
                });*//*
            }*/
            /*if(context instanceof LatestResults_Activity) {
                applyLastSorting();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initListViewData() {
        try {
            ArrayList<String> header = addedLatestResults.get("date_end");
            for(int i =0 ;i<header.size();i++){
                if(i ==2 || i==5 || i==8){
                    header.set(i," % var");
                } else {
                    header.set(i, GlobalClass.getLatestResultDate(header.get(i)));
                }
                GlobalClass.log("date end :" + header.get(i));
            }

            listviewdata = new String[header.size()];
            //int noOfColsAdded = 1;
            for (int i = 0; i < listviewdata.length; i++) {

                listviewdata[i] = header.get(i).toString();
                /*if (listviewdata[i].contains("+")) {
                    noOfColsAdded++;
                }*/
                Log.v("", "Column name : " + header.get(i).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyDataSetChanged(String methodName) {
        try {
            GlobalClass.log("MethodName : " + methodName);
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    ((listViewCustomAdaptor) NameListView.getAdapter()).notifyDataSetChanged();
                    ((listViewCustomAdaptor) ValuesListView.getAdapter()).notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void setStack(Stack<String> stack) {
        try {
            this.stack = stack;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshGroup(String selectedGroup) {
        try {
            notifyDataSetChanged("refreshGroup");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void pushAndShow(String id, Intent intent) {
        try {
            Window window = ((ActivityGroup) getContext()).getLocalActivityManager().startActivity(id, intent);//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY));
            if (window != null) {
                stack.push(id);
                ((ActivityGroup) getContext()).setContentView(window.getDecorView());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   /* @Override
    public void onClick(View view) {
    }*/

    private class SyncedScrollListener implements AbsListView.OnScrollListener {
        String className;

        public SyncedScrollListener(String name) {
            className = name;
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            try {
                ListView mSyncedView = ValuesListView;
                if (view == NameListView) {
                    mSyncedView = ValuesListView;
                } else if (view == ValuesListView) {
                    mSyncedView = NameListView;
                }

                if (visibleItemCount == 0) {
                    return;
                }

                View c = view.getChildAt(0);
                if (c != null) {
                    if (mSyncedView.getChildAt(0) != null) {
                        if (mSyncedView.getChildAt(0).getTop() != c.getTop())
                            mSyncedView.setSelectionFromTop(firstVisibleItem, c.getTop());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            try {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view == NameListView) {
                        ValuesListView.setOnScrollListener(values_syncScrollListener);
                    } else if (view == ValuesListView) {
                        NameListView.setOnScrollListener(name_syncScrollListener);
                    }
                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    if (view == NameListView) {
                        ValuesListView.setOnScrollListener(null);
                    } else if (view == ValuesListView) {
                        NameListView.setOnScrollListener(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class listViewCustomAdaptor extends ArrayAdapter<String> {

        public listViewCustomAdaptor(Context context, int resource, int textViewResourceId) {
            super(context, android.R.layout.simple_list_item_1);
            try {
                //Constructor calls..
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getCount() {
            try {
                return title.size();
                //return addedContractinMW.size();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

       /* public String getItem(int position) {
            try {
                return listviewdata.get(position).toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }*/

       /* public String getItem(int position) {
            try {
                return reqCodeVector.get(position).toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }*/

        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                //convertView=null;
                //System.gc();
                if (parent == NameListView) {
                    if (convertView == null) {
                        /*LayoutParams params = new LayoutParams((int) (Math.floor(width / 3)), LayoutParams.WRAP_CONTENT);
                        params.weight = 1;*/
                        convertView = new LinearLayout(getContext());
                        ((LinearLayout) convertView).setOrientation(VERTICAL);
                        MktLabel tty = new MktLabel(getContext(), "", Gravity.LEFT, (int) (Math.floor(width / divisionFactor)), Color.WHITE);
                        //convertView = new TextScroll(getContext(), "", Gravity.LEFT, (2 * (width / divisionFactor)), Color.WHITE);
                        tty.setTag("scripname");
                        tty.setSingleLine();
                        //tty.setLayoutParams(params);
                        tty.setLayoutParams(new LayoutParams(width, LayoutParams.WRAP_CONTENT));
                        //tty.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        //((LinearLayout) convertView).setOnLongClickListener((OnLongClickListener) getContext());
                        ((LinearLayout) convertView).addView(tty);
                    }
                    ((LinearLayout) convertView).setBackgroundColor(Color.BLACK);
                    //if (((TextScroll) convertView).getText().equalsIgnoreCase("")) {
                    ((LinearLayout) convertView).setTag(title.get(position));
                    //((MktLabel) convertView.findViewWithTag("scripname")).setText(addedLatestResults.get(reqCodeVector.get(position)).toString().trim());
                    ((MktLabel) convertView.findViewWithTag("scripname")).setText(title.get(position));

                    //}
                } else {
                    if (convertView == null) {
                        //Log.v("", "GetView was called for position.." + position);
                        convertView = new LinearLayout(getContext());
                        ((LinearLayout) convertView).setOrientation(LinearLayout.HORIZONTAL);

                        ArrayList<String > data = addedLatestResults.get(title.get(position).toLowerCase());
                        for (int i = 0; i < data.size(); i++) {
                            LayoutParams params = new LayoutParams((int) (Math.floor(width / 4)), LayoutParams.WRAP_CONTENT);
                            params.weight = 1;
                            String txt="";
                            if(data.get(i).equalsIgnoreCase("")){
                                data.set(i,"-");
                                txt = data.get(i);
                            } else {
                                if( i!=2 && i!=5 && i!=8) {
                                    txt = GlobalClass.getLatestResultData(data.get(i));
                                } else {
                                    txt = data.get(i);
                                }
                            }

                            MktLabel tty = new MktLabel(getContext(),"0.00", Gravity.RIGHT, (int) (Math.floor(width / 4)), Color.WHITE);
                            tty.setTag(listviewdata[i].toString().trim()+i);
                            tty.setLayoutParams(params);
                            tty.setSingleLine(true);
                            ((LinearLayout) convertView).addView(tty);
                        }
                        GlobalClass.log("Position in getView() : " + position);
                    }
                    //convertView.setTag(listviewdata[position]);
                    convertView.setBackgroundColor(Color.BLACK);
                    setInitialValues(position, convertView);
                }

                if (position % 2 == 1)
                    convertView.setBackgroundColor(Color.rgb(25, 22, 17));

                return convertView;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void setInitialValues(final int position, final View convertView) {
        try {
            final ArrayList<String > data = addedLatestResults.get(title.get(position).toLowerCase());
            //final String strTitle = title.get(position).trim();
            convertView.post(new Runnable() {
                @Override
                public void run() {
                    //   String str=convertView.getTag().toString().trim();
                    // if(str.equalsIgnoreCase(strTitle)) {
                    for(int i=0;i<data.size();i++) {
                        String txt="";
                        if(data.get(i).equalsIgnoreCase("")){
                            data.set(i,"-");
                            txt = data.get(i);
                        } else {
                            //if( i!=2 && i!=5 && i!=8) {
                            if( i!=2 && i!=5 && i!=8) {
                                if(data.get(i).equalsIgnoreCase("-")){
                                    txt = data.get(i);
                                } else {
                                    txt = GlobalClass.getLatestResultData(data.get(i));
                                }
                            } else {
                                txt = data.get(i);
                            }
                        }
                        MktLabel edit = (MktLabel) convertView.findViewWithTag(listviewdata[i].toString().trim()+i);

                        if (edit != null)
                            edit.setText(txt);
                    }
                }

                // }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MktLabel extends AppCompatTextView {
        private int textSize = 14;
        private int rowHeight = 11;

        public MktLabel(Context context, String text, int gravity, int width, int textColor) {
            super(context);

            try {
                this.setText(text);
                //this.setTextSize(textSize);
                this.setTextColor(textColor);
                this.setBackgroundResource(0);
                //this.setTypeface(Typeface.DEFAULT_BOLD);
                //this.setEnabled(false);
                if (width > 0) {
                    this.setWidth(width);
                    this.setMaxWidth(width);
                }
                this.setPadding(5, GlobalClass.intTODP(getContext(), rowHeight), 5, GlobalClass.intTODP(getContext(), rowHeight));
                this.setGravity(gravity);
                //this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
