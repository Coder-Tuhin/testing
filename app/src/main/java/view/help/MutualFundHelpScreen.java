package view.help;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.ventura.venturawealth.R;

import utils.GlobalClass;
import utils.ObjectHolder;
import utils.VenturaException;

public class MutualFundHelpScreen extends Dialog implements View.OnClickListener {
    private Context context;
    private Button btnGot;
    private LinearLayout buttonLayout;
    private FrameLayout helpFrame;
    private View helpOne,helpTwo,helpThree,whatsnew;
    private int countNo;
    private boolean fromNavigation = false;



    public MutualFundHelpScreen(@NonNull Context context,boolean isholdingreportscreen,boolean isbondscreen) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        try {
            this.context = context;
            this.setContentView(R.layout.mutualfundhelpscreen);
            this.setCancelable(false);
            btnGot = this.findViewById(R.id.btnGot);
            helpFrame = this.findViewById(R.id.helpFrame);

            if (isholdingreportscreen){
                helpOne = LayoutInflater.from(context).inflate(R.layout.mutualfundhelp_two, null);
            }
            if (isbondscreen){
                helpOne = LayoutInflater.from(context).inflate(R.layout.mutualfundhelp_bond, null);
            }
            else {
                if(ObjectHolder.dWidth < 721 ){
                    helpOne = LayoutInflater.from(context).inflate(R.layout.mutualfund_help_three, null);
                }else {
                    helpOne = LayoutInflater.from(context).inflate(R.layout.mutualfundhelp_one, null);
                }
            }
            helpFrame.addView(helpOne);
            btnGot.setOnClickListener(this::onClick);

        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGot:
                this.dismiss();
                //((HomeActivity)context).OpenActivateMargin();
                break;

        }
    }
}
