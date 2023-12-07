package fragments.settingstab;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;

import com.ventura.venturawealth.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import enums.eExch;
import interfaces.OnPopup;
import models.BuySellModel;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import view.BuySellWindowforQuickOrder;
import view.CustomPopupQuickOrder;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class Quickorderfragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener,
        OnPopup, CompoundButton.OnCheckedChangeListener,Animation.AnimationListener{
    private ArrayAdapter<String> m_grpAdapter;
    private String[] spinnerItems = new String[]{"Quick BUY Order for NSE",
            "Quick SELL Order for NSE",
            "Quick BUY Order for BSE",
            "Quick SELL Order for BSE",
            "Quick BUY Order for NSE Derv",
            "Quick SELL Order for NSE Derv",
            "Quick BUY Order for NSE Curr",
            "Quick SELL Order for NSE Curr"};
    private String buySell = "";
    private int selectedExchange;
    private CustomPopupQuickOrder m_customPopupWindow;
    private BuySellWindowforQuickOrder buySellWindow;

    @BindView(R.id.ordersetting_spinner)Spinner ordersetting_spinner;
    @BindView(R.id.select_security_button)Button select_security_button;
    @BindView(R.id.scrip_textview)TextView scrip_textview;
    @BindView(R.id.enable_checkbox)CheckBox enable_checkbox;
    @BindView(R.id.buy_sell_linear)LinearLayout buy_sell_linear;

    public  Quickorderfragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = null;

            if (UserSession.getLoginDetailsModel().isActiveUser()) {
                layout = inflater.inflate(R.layout.quickorder_screen, container, false);
                ButterKnife.bind(this, layout);
                initSpinner();
                select_security_button.setOnClickListener(this);
            }else {
                layout = inflater.inflate(R.layout.deactive_account, container, false);
            }

        return layout;
    }


    private void initSpinner() {
        m_grpAdapter = new ArrayAdapter<>(GlobalClass.latestContext, R.layout.custom_spinner_item);
        m_grpAdapter.setDropDownViewResource(R.layout.custom_spinner_selecteditem);
        m_grpAdapter.addAll(spinnerItems);
        ordersetting_spinner.setAdapter(m_grpAdapter);
        ordersetting_spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_security_button:
                m_customPopupWindow = new CustomPopupQuickOrder(this,buySell,selectedExchange);
                m_customPopupWindow.openSearchScripWindow();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String spinnerText = ordersetting_spinner.getSelectedItem().toString();
        buySell = spinnerText.substring(6,9);
        if(position == 0 || position == 1){
            selectedExchange = eExch.NSE.value;
        } else if(position == 2 || position == 3){
            selectedExchange = eExch.BSE.value;
        } else if(position == 4 || position == 5){
            selectedExchange = eExch.FNO.value;
        } else if(position == 6 || position == 7){
            selectedExchange = eExch.NSECURR.value;
        }
        scrip_textview.setText("");
        enable_checkbox.setChecked(false);
        firstTimeValueSet(spinnerText);
        }

    private void firstTimeValueSet(String spinnerItem) {
        buy_sell_linear.removeAllViews();
        BuySellModel buySellModel = null;
        ArrayList<BuySellModel> mList = new ArrayList<>(PreferenceHandler.getQuickOrderList().values());
        for (BuySellModel bsm : mList){
            if (bsm.getQuickOrderType().equalsIgnoreCase(spinnerItem)){
                buySellModel = bsm;
//               buySellModel.setScriptName(bsm.getScriptName());
//               buySellModel.setShow(bsm.isShow());
//               buySellModel.setScriptCode(bsm.getScriptCode());
//               buySellModel.setExchange(bsm.getExchange());
//               buySellModel.setQuickOrderType(bsm.getQuickOrderType());
//               buySellModel.setOrderType(bsm.getOrderType());
//               buySellModel.setIntraDay(bsm.isIntraDay());
//               buySellModel.setDiscQty(bsm.getDiscQty());
//               buySellModel.setIOC(bsm.isIOC());
//               buySellModel.setMkt(bsm.isMkt());
//               buySellModel.setPrice(bsm.getPrice());
//               buySellModel.setTiggerPrice(bsm.getTiggerPrice());
//               buySellModel.setQty(bsm.getQty());
//               buySellModel.setStopLoss(bsm.isStopLoss());
               break;
            }
        }if (buySellModel==null) buySellModel = new BuySellModel();
        buySellModel.setOrderType(buySell);
        buySellModel.setExchange(selectedExchange);
        buySellModel.setQuickOrderType(spinnerItem);
        enable_checkbox.setOnCheckedChangeListener(null);
        scrip_textview.setText(buySellModel.getScriptName());
        enable_checkbox.setChecked(buySellModel.isShow());
        buySellWindow = new BuySellWindowforQuickOrder(GlobalClass.latestContext,buySellModel);
        buy_sell_linear.addView(buySellWindow);
        enable_checkbox.setOnCheckedChangeListener(this);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPopupResponse(String scripName,int scripCode) {
        scrip_textview.setText(scripName);
        buySellWindow.saveSecurity(scripName,scripCode);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        buySellWindow.showHide(b);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
