package view;

import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import Structure.Response.RC.StructTradeReportReplyRecord_Pointer;
import com.ventura.venturawealth.R;
import interfaces.OnDeleveryClick;
import utils.GlobalClass;

/**
 * Created by xtremsoft on 12/26/16.
 */
public class ConverDeliveryPopup implements View.OnClickListener{
    private View m_view;
    private OnDeleveryClick onDeleveryClick;
    private AlertDialog m_alertDialog;
    private StructTradeReportReplyRecord_Pointer tradeBkRow;

    public ConverDeliveryPopup(OnDeleveryClick onDeleveryClick,StructTradeReportReplyRecord_Pointer row){
        this.onDeleveryClick = onDeleveryClick;
        this.tradeBkRow = row;
    }
    public void openConvertDelivery(){
        m_view = LayoutInflater.from(GlobalClass.latestContext).inflate(R.layout.position_conversesion, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GlobalClass.latestContext);
        dialogBuilder.setView(m_view);
        dialogBuilder.setCancelable(false);
        TextView scrip_textview = (TextView) m_view.findViewById(R.id.scrip_textview);
        TextView ordertype_textview = (TextView) m_view.findViewById(R.id.ordertype_textview);
        TextView quantiyt = (TextView) m_view.findViewById(R.id.quantiyt);
        Button convert_delivery = (Button) m_view.findViewById(R.id.convert_delivery);

        scrip_textview.setText("" + tradeBkRow.getFormatedScripName(false));
        ordertype_textview.setText("" + tradeBkRow.getOrderTypeStr());
        quantiyt.setText("" + tradeBkRow.qty.getValue());

        String btnName = "Convert To ";
        if(tradeBkRow.orderType.getValue() == 0){
            btnName = btnName + "Delivery";
        }
        else{
            btnName = btnName + "Intraday";
        }
        convert_delivery.setText(btnName);
        ImageButton close = (ImageButton) m_view.findViewById(R.id.close);
        close.setOnClickListener(this);
        convert_delivery.setOnClickListener(this);
        m_alertDialog = dialogBuilder.create();
        m_alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.convert_delivery:
                onDeleveryClick.onDeleveryClick();
                break;
            case R.id.close:
                m_alertDialog.dismiss();
                break;
        }
    }

    public  void dismissDialog(){
        m_alertDialog.dismiss();
    }
}
