package fragments.OptionChainNew;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import Structure.Request.BC.StructOptionChainReqNew;
import enums.eExch;
import enums.eForHandler;
import enums.eMessageCode;
import fragments.OptionChainNew.utility.ScripNameHandler;
import utils.GlobalClass;
import utils.VenturaException;

public class OptionChainSelectionView implements View.OnClickListener{

    private View m_view;
    private AlertDialog wl_alertDialog;
    public static Handler _handler;
    private OptionSelector selector;

    private AutoCompleteTextView autocompleteTv;
    private RadioGroup _symbolRdGrp;
    private RadioGroup expTypeRdGrp;
    private CheckBox callbtn;
    private CheckBox putbtn;

    private RadioGroup expRdGrp;
    private RadioGroup strikesRdGrp;
    String selectedSymbol = "";

    public void openSelectionScreen(OptionSelector select){

        m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.aleart_screen_new, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
        dialogBuilder.setView(m_view);
        this.selector = select;
        final Button _closeBtn = m_view.findViewById(R.id.submitBtn);
        _symbolRdGrp = m_view.findViewById(R.id.symbolRdGrp);
        expTypeRdGrp = m_view.findViewById(R.id.exptypeRdGrp);
        callbtn = ((CheckBox)(m_view.findViewById(R.id.callbtn)));
        putbtn = ((CheckBox)(m_view.findViewById(R.id.putbtn)));

        expRdGrp = ((RadioGroup)(m_view.findViewById(R.id.expRdGrp)));
        strikesRdGrp = ((RadioGroup)(m_view.findViewById(R.id.strikesRdGrp)));
        autocompleteTv = m_view.findViewById(R.id.autocompleteTv);

        setPrevoiusValue();

        _closeBtn.setOnClickListener(this);
        _symbolRdGrp.setOnCheckedChangeListener(_onCheckChange);

        wl_alertDialog = dialogBuilder.create();
        wl_alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        wl_alertDialog.show();
        wl_alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
        _handler = new UIHandler();
        wl_alertDialog.setOnDismissListener(dialogInterface -> _handler = null);
    }
    private void setPrevoiusValue(){
        StructOptionChainReqNew optionChainSettings = VenturaApplication.getPreference().getOptionChainSettings();
        if(optionChainSettings != null){
            selectedSymbol = optionChainSettings.symbol.getValue();
            if(optionChainSettings.symbol.getValue().equalsIgnoreCase("NIFTY")){
                ((RadioButton)m_view.findViewById(R.id.niftyRDB)).setChecked(true);
            }
            else if(optionChainSettings.symbol.getValue().equalsIgnoreCase("BANKNIFTY")){
                ((RadioButton)m_view.findViewById(R.id.bankniftyRDB)).setChecked(true);
            }
            else{
                ((RadioButton)m_view.findViewById(R.id.othersRd)).setChecked(true);
                otherRadionBtnChecked();
            }
            if(optionChainSettings.expiryType.getValue() == 0){
                ((RadioButton)m_view.findViewById(R.id.monthlyRd)).setChecked(true);
            }
            else {
                ((RadioButton)m_view.findViewById(R.id.weeklyRd)).setChecked(true);
            }

            (callbtn).setChecked(true);
            (putbtn).setChecked(true);
            if(optionChainSettings.option.getValue() == 3){
                (putbtn).setChecked(false);
            }
            else if(optionChainSettings.option.getValue() == 4){
                (callbtn).setChecked(false);
            }
            //1 - Near  2 - Next 3 - Far
            if(optionChainSettings.expNo.getValue() == 2){
                ((RadioButton)m_view.findViewById(R.id.nextbtn)).setChecked(true);
            }
            else if(optionChainSettings.expNo.getValue() == 3){
                ((RadioButton)m_view.findViewById(R.id.farbtn)).setChecked(true);
            }
            else{
                ((RadioButton)m_view.findViewById(R.id.nearbtn)).setChecked(true);
            }

            if(optionChainSettings.strikeNo.getValue() == 3){
                ((RadioButton)m_view.findViewById(R.id.threebtn)).setChecked(true);
            }
            else if(optionChainSettings.strikeNo.getValue() == 5){
                ((RadioButton)m_view.findViewById(R.id.fivebtn)).setChecked(true);
            } else if(optionChainSettings.strikeNo.getValue() == 7){
                ((RadioButton)m_view.findViewById(R.id.sevenbtn)).setChecked(true);
            }else if(optionChainSettings.strikeNo.getValue() == 7){
                ((RadioButton)m_view.findViewById(R.id.ninebtn)).setChecked(true);
            } else{
                ((RadioButton)m_view.findViewById(R.id.allbtn)).setChecked(true);
            }
        }
    }
    private void close() {
        try {
            if (wl_alertDialog != null) {
                wl_alertDialog.dismiss();
                wl_alertDialog = null;
            }
            m_view = null;
        }catch (Exception e){
            GlobalClass.onError("Error in "+getClass().getName(),e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submitBtn: {

                StructOptionChainReqNew optReq = new StructOptionChainReqNew();
                int selectedSymbol = _symbolRdGrp.getCheckedRadioButtonId();
                if(selectedSymbol == R.id.niftyRDB){
                    optReq.symbol.setValue("NIFTY");
                }
                else if(selectedSymbol == R.id.bankniftyRDB){
                    optReq.symbol.setValue("BANKNIFTY");
                }
                else{
                    autocompleteTv = m_view.findViewById(R.id.autocompleteTv);
                    String symbol = autocompleteTv.getText().toString();
                    if(symbol.equalsIgnoreCase("")){
                        GlobalClass.showAlertDialog("Please select Symbol");
                    }
                    else {
                        optReq.symbol.setValue(symbol);
                    }
                }
                if(!optReq.symbol.getValue().equalsIgnoreCase("")){
                    if(!callbtn.isChecked() && !putbtn.isChecked()){
                        GlobalClass.showAlertDialog("Please select Option");
                    }else {
                        int expiryType = expTypeRdGrp.getCheckedRadioButtonId();
                        if (expiryType == R.id.monthlyRd) {
                            optReq.expiryType.setValue(0);
                        } else if (expiryType == R.id.weeklyRd) {
                            optReq.expiryType.setValue(1);
                        }
                        optReq.option.setValue(3);
                        if (callbtn.isChecked() && putbtn.isChecked()) {
                            optReq.option.setValue(2);
                        }
                        else if (putbtn.isChecked()) {
                            optReq.option.setValue(4);
                        }
                        int expiry = expRdGrp.getCheckedRadioButtonId();
                        if (expiry == R.id.nearbtn) {
                            optReq.expNo.setValue(1);
                        } else if (expiry == R.id.nextbtn) {
                            optReq.expNo.setValue(2);
                        } else if (expiry == R.id.farbtn) {
                            optReq.expNo.setValue(3);
                        }
                        int strike = strikesRdGrp.getCheckedRadioButtonId();
                        if (strike == R.id.threebtn) {
                            optReq.strikeNo.setValue(3);
                        } else if (strike == R.id.fivebtn) {
                            optReq.strikeNo.setValue(5);
                        } else if (strike == R.id.sevenbtn) {
                            optReq.strikeNo.setValue(7);
                        } else if (strike == R.id.ninebtn) {
                            optReq.strikeNo.setValue(9);
                        } else if(strike == R.id.allbtn){
                            optReq.strikeNo.setValue(100);
                        }
                        VenturaApplication.getPreference().setOptionChainSettings(optReq);
                        selector.onSubmit(optReq);
                        close();
                    }
                }
                break;
            }
        }
    }

    private RadioGroup.OnCheckedChangeListener _onCheckChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
            switch (radioGroup.getId()){
                case R.id.symbolRdGrp:
                    final LinearLayout _symbolSelectionLinear = m_view.findViewById(R.id.symbolSelectionLinear);
                    switch (checkId){
                        case R.id.othersRd:
                            selectedSymbol = "";
                            otherRadionBtnChecked();
                            break;
                        default:
                            m_view.findViewById(R.id.weeklyRd).setVisibility(View.VISIBLE);
                            if (_symbolSelectionLinear.getVisibility() == View.VISIBLE)
                                _symbolSelectionLinear.setVisibility(View.GONE);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void otherRadionBtnChecked(){
        final LinearLayout _symbolSelectionLinear = m_view.findViewById(R.id.symbolSelectionLinear);
        _symbolSelectionLinear.setVisibility(View.VISIBLE);
        //m_view.findViewById(R.id.weeklyRd).setVisibility(View.INVISIBLE); due to finnifty
        //((RadioButton)m_view.findViewById(R.id.monthlyRd)).setChecked(true);
        setAutoCompleteData();
    }

    private void setAutoCompleteData() {
        try{
            if (GlobalClass.scripNameHandler == null){
                GlobalClass.scripNameHandler = new ScripNameHandler();
            }
            if (GlobalClass.scripNameHandler.getScripNameList(eExch.FNO.value)!=null){
                autocompleteTv.setText(selectedSymbol);
                GlobalClass.homeActivity.runOnUiThread(() -> {
                    GlobalClass.scripNameHandler.setAutoCompleteData(autocompleteTv,eExch.FNO.value);
                });
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    int _msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                    eMessageCode _eMsgCode = eMessageCode.valueOf(_msgCode);
                    switch (_eMsgCode){
                        case SCRIP_NAMES:
                            setAutoCompleteData();
                            break;
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

}
