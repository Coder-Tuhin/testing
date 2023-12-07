package view;

import android.app.Dialog;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import interfaces.OnCloseClick;
import utils.GlobalClass;

public class RiskDisclosureDialog extends Dialog implements View.OnClickListener {
    private ImageView closeriskbtn;
    private TextView risktext,btn_ok_risk;
    private OnCloseClick onCloseClick;

    public RiskDisclosureDialog(@NonNull Context context,OnCloseClick _onCloseClick) {
        super(context);
        try {
            this.setContentView(R.layout.risk_disclosure);
            this.closeriskbtn = findViewById(R.id.closeriskbtn);
            this.risktext = findViewById(R.id.risktext);
            this.btn_ok_risk = findViewById(R.id.btn_ok_risk);
            this.setCancelable(false);
            this.closeriskbtn.setOnClickListener(this);
            this.btn_ok_risk.setOnClickListener(this);
            this.risktext.setMovementMethod(LinkMovementMethod.getInstance());
            this.onCloseClick = _onCloseClick;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
        onCloseClick.onCloseClick();
    }
}