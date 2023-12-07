package view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.ArrayList;

import Structure.Response.BC.StructxMKTEventRes;
import Structure.Response.Group.GroupsTokenDetails;
import butterknife.BindView;
import enums.ePrefTAG;
import enums.eShowDepth;
import interfaces.OnActionWatchClick;
import utils.Formatter;
import utils.GlobalClass;

public class ActionWatch {
    private final ArrayList<StructxMKTEventRes> eventList = new ArrayList<>();
    private String prevScrip = "";
    private boolean newEventStyle = true;
    private int btnWidth = 0;
    private LinearLayout event_linear;
    private Context context;
    private OnActionWatchClick onActionWatchClick;

    public ActionWatch(LinearLayout _event_linear,Context _context,OnActionWatchClick _onActionWatchClick){
        this.event_linear = _event_linear;
        this.context = _context;
        this.onActionWatchClick = _onActionWatchClick;
        btnWidth = (int) context.getResources().getDimension(R.dimen.item_height);

        newEventStyle = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.ENENTSTYLE.name,true);
    }
    public void reloadConfig(){
        newEventStyle = VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.ENENTSTYLE.name,true);
    }
    public void setEvent(StructxMKTEventRes structxMKTEventRes) {
        if (structxMKTEventRes == null) return;
        String scrip = structxMKTEventRes.scripName.getValue();
        if (!scrip.equals(prevScrip)) {
            prevScrip = scrip;
            if (newEventStyle) {
                if (eventList.size() > 2) {
                    eventList.remove(2);
                    eventList.add(2, eventList.get(1));
                    eventList.remove(1);
                    eventList.add(1, eventList.get(0));
                    eventList.remove(0);
                    eventList.add(0,structxMKTEventRes);
                } else {
                    eventList.add(structxMKTEventRes);
                }
            } else {
                eventList.clear();
                eventList.add(structxMKTEventRes);
            }
            if (event_linear.getVisibility() != View.VISIBLE) {
                event_linear.setVisibility(View.VISIBLE);
            }
            event_linear.removeAllViews();

            for (StructxMKTEventRes structMktEvent : eventList) {
                View view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.event_item, null);
                TextView event_scriptname = (TextView) view.findViewById(R.id.event_scriptname);
                final int scriptCode = structMktEvent.token.getValue();
                final String scriptName = structMktEvent.scripName.getValue();
                if(onActionWatchClick != null) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GroupsTokenDetails groupsTokenDetails = new GroupsTokenDetails();
                            groupsTokenDetails.scripCode.setValue(scriptCode);
                            groupsTokenDetails.scripName.setValue(scriptName);
                            onActionWatchClick.onActionWatchClick(groupsTokenDetails);
                        }
                    });
                }
                String finalEventType;
                int textColor;
                if (structMktEvent.event.getValue() == 5) {
                    finalEventType = "Year High";
                    textColor = context.getResources().getColor(R.color.green1);
                } else if (structMktEvent.event.getValue() == 6) {
                    finalEventType = "Year Low";
                    textColor = context.getResources().getColor(R.color.red);
                } else if (structMktEvent.event.getValue() == 7) {
                    finalEventType = "Day High";
                    textColor = context.getResources().getColor(R.color.green1);
                } else if (structMktEvent.event.getValue() == 8) {
                    finalEventType = "Day Low";
                    textColor = context.getResources().getColor(R.color.red);
                } else {
                    finalEventType = "Event : " + structMktEvent.event.getValue();
                    textColor = Color.WHITE;
                }
                event_scriptname.setText("" + structMktEvent.scripName.getValue());
                TextView event_lastrate = (TextView) view.findViewById(R.id.event_scriptrate);
                TextView event = (TextView) view.findViewById(R.id.event);

                event_lastrate.setText("" + Formatter.formatter.format(structMktEvent.lastRate.getValue()));
                event_lastrate.setTextColor(textColor);
                event.setText(finalEventType);
                event.setTextColor(textColor);
                GlobalClass.textColor = textColor;
                GlobalClass.finalEventType = finalEventType;
                GlobalClass.event_lastrate = "" + structMktEvent.lastRate.getValue();
                event_linear.addView(view);
                LinearLayout.LayoutParams btnLp = null;
                if (eventList.size() == 1) {
                    btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, btnWidth);
                } else if (eventList.size() == 3){
                    btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,btnWidth*2);
                }else {
                    btnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                event_linear.setLayoutParams(btnLp);
            }
        }
    }
}