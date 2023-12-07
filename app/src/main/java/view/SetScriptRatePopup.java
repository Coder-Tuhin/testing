package view;

/**
 * Created by xtremsoft on 1/24/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;

import Structure.Request.BC.SetAlertReq;
import Structure.Response.BC.StaticLiteMktWatch;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import connection.SendDataToBCServer;
import models.AlertModel;
import models.ScripAlertModel;
import utils.DecimalDigitsInputFilter;
import utils.Formatter;
import utils.GlobalClass;
import utils.UserSession;
import wealth.Dialogs;

public class SetScriptRatePopup extends LinearLayout implements  View.OnClickListener {
    public static Handler uihandler, uihandlermkt ;

    ImageButton close;
    EditText script_value;
    private RadioGroup rdbtn;
    private selectedScript script = null;
    private Context context;
    private int token;
    private String scripname;
    private double lastRate = 0.0;
    Button btnCancel;
    private short condition = 0; // geater = 0, lesser = 1
    private TextView scripvalue;

    public SetScriptRatePopup(Context context) {
        super(context);
        init(context);
    }

    public SetScriptRatePopup(Context context, String scripname, int token) {
        super(context);
        this.scripname = scripname;
        this.token = token;
        StaticLiteMktWatch data = GlobalClass.mktDataHandler.getMkt5001Data(token,true);
        this.lastRate = data.getLastRate();
        init(context);
    }

    public SetScriptRatePopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SetScriptRatePopup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setselectedScript(selectedScript script) {
        this.script = script;
    }


    private void init(Context context) {
        try {
            this.context = context;
            uihandler = new uiHandler();
            uihandlermkt = new uiHandlermkt();
            setOrientation(VERTICAL);

            addView(LayoutInflater.from(getContext()).inflate(R.layout.set_scriprate_popup, null));
            TextView scriptxt = (TextView) findViewById(R.id.scrip_rate_text);
            scriptxt.setText("Set Scrip Rate Alert for\n" + scripname);
            scripvalue = (TextView) findViewById(R.id.scrip_rate_value);
            scripvalue.setText(Formatter.formatter.format(lastRate)+"");

            close = (ImageButton) findViewById(R.id.close);
            btnCancel = (Button)findViewById(R.id.cancel);
            btnCancel.setOnClickListener(this);


            rdbtn = ((RadioGroup) findViewById(R.id.radioCondition));
            final RadioButton radio_Greater = (RadioButton) findViewById(R.id.radioGreater);
            final RadioButton radio_Smaller = (RadioButton) findViewById(R.id.radioSmaller);

            rdbtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(radio_Greater.isChecked()){
                        condition = 0;
                    } else if(radio_Smaller.isChecked()){
                        condition = 1;
                    }
                }
            });

            findViewById(R.id.submit).setOnClickListener(this);
            script_value = (EditText) findViewById(R.id.script_value);
            script_value.setText(Formatter.formatter.format(lastRate).replaceAll(",",""));
            script_value.setSelection(script_value.getText().length());
            script_value.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCloseLis(OnClickListener lis) {
        close.setOnClickListener(lis);
    }


    @Override
    public void onClick(View view) {
        try {
            Button btn = (Button) view;
            if(btn.getText().toString().equalsIgnoreCase("cancel")){
                //close.setOnClickListener();
            } else {
                String editRate = script_value.getText().toString().trim();
                editRate = editRate.replaceAll(",","");
                Double double_editrate = Double.parseDouble(editRate);
                Double double_lastRate = Double.parseDouble(Formatter.formatter.format(lastRate).replace(",",""));
                if (TextUtils.isEmpty(editRate)) {
                    Toast.makeText(getContext(), "Enter value  to Search", Toast.LENGTH_SHORT).show();
                }else if (Double.parseDouble(editRate)<=0){
                    Toast.makeText(getContext(), "Input is not acceptable", Toast.LENGTH_SHORT).show();
                } else if (double_editrate.equals(double_lastRate)){
                    Toast.makeText(getContext(), "Can not process due to same value", Toast.LENGTH_SHORT).show();
                }else if (condition == 0 && double_editrate<double_lastRate ) {
                    Toast.makeText(getContext(), "Please check condition", Toast.LENGTH_SHORT).show();
                }else if (condition == 1 && double_editrate>double_lastRate) {
                    Toast.makeText(getContext(), "Please check condition", Toast.LENGTH_SHORT).show();
                }else{
                    float scrip_rate = Float.parseFloat(editRate);
                    String clientCode = UserSession.getLoginDetailsModel().getUserID();
                    SetAlertReq setAlertReq = new SetAlertReq();
                    setAlertReq.clientCode.setValue(clientCode);
                    setAlertReq.token.setValue(token);
                    setAlertReq.tokenRate.setValue(scrip_rate);
                    setAlertReq.condition.setValue(condition);
                    SendDataToBCServer sendDataToBCServer = new SendDataToBCServer();
                    sendDataToBCServer.sendAlert(setAlertReq);

                    AlertModel alertModel = new AlertModel();
                    alertModel.setAchive((short) 0);
                    alertModel.setCondition(condition);
                    alertModel.setTokenRate(scrip_rate);
                    alertModel.setToken(token);
                    alertModel.setScriptName(scripname);

                    LinkedHashMap<Integer,ScripAlertModel> map = VenturaApplication.getPreference().getAlertRate();
                    ScripAlertModel scripAlertModel = map.get(token);
                    if(scripAlertModel == null){
                        scripAlertModel = new ScripAlertModel(token);
                    }
                    scripAlertModel.setValueForCondition(condition,alertModel);
                    map.put(token,scripAlertModel);
                    VenturaApplication.getPreference().setAlertRate(map);

                    script.selectedScript();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface selectedScript {
        public void selectedScript();
    }


    public void refreshData(boolean flag, int scripCode) {

        if(!flag) {
            StaticLiteMktWatch data = null;
            try {
                data = GlobalClass.mktDataHandler.getMkt5001Data(scripCode,true);
                double lastRate = data.getLastRate();
                double preval = 0;
                try {
                    preval = Formatter.formatter.parse(scripvalue.getText().toString()).doubleValue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (preval != 0) {
                    if (preval < lastRate) {
                        scripvalue.setBackgroundResource(R.drawable.tick_up_bg);
                    }
                    else if (preval > lastRate) {
                        scripvalue.setBackgroundResource(R.drawable.tick_down_bg);

                    }
                    else {
                        scripvalue.setBackgroundResource(android.R.color.transparent);

                    }
                }
                scripvalue.setText(Formatter.formatter.format(lastRate));

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    class uiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    String message = refreshBundle.getString("msg");

                    AlertDialog.Builder alertBuilder = Dialogs.getAlertBuilder(context);
                    alertBuilder.setMessage(message.trim())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class uiHandlermkt extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                //a message is received; update UI text view
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int scripCode = refreshBundle.getInt("token");
                    if(scripCode == token) {
                        String flag = refreshBundle.getString("updateType");
                        if (flag.equalsIgnoreCase("mktwatch"))
                            refreshData(false, scripCode);

                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}