package view.help;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.Timer;
import java.util.TimerTask;

import enums.ePrefTAG;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 30-Aug-2017.
 */
public class QuickOrderHelp extends Dialog implements View.OnClickListener{

    private boolean isHelpClick;
    private TextView spinnertext,enabletext,selecttext,ordertext;
    private ImageView spinnerarrow,enablearrow,selectarrow,orderarrow;
    private Timer timer;
    private int count = 0;

    public QuickOrderHelp(final Context context, boolean isHelpClick) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.setContentView(R.layout.quickorder_help_screen);
        this.isHelpClick = isHelpClick;
        spinnerarrow = (ImageView) this.findViewById(R.id.spinnerarrow);
        spinnertext = (TextView) this.findViewById(R.id.spinnertext);

        enablearrow = (ImageView) this.findViewById(R.id.enablearrow);
        enabletext = (TextView) this.findViewById(R.id.enabletext);

        selectarrow = (ImageView) this.findViewById(R.id.selectarrow);
        selecttext = (TextView) this.findViewById(R.id.selecttext);

        orderarrow = (ImageView) this.findViewById(R.id.orderarrow);
        ordertext = (TextView) this.findViewById(R.id.ordertext);

        ((Button) findViewById(R.id.btnGot)).setOnClickListener(this);
        this.setCancelable(false);
        try{
            if (timer != null) timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showingOrderHelpScreen();
                        }
                    });

                }
            },0,3000);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGot:
                if (!isHelpClick){
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.QUICK_ORDER.name,false);
                }
                this.dismiss();
                break;
        }
    }

    private void showingOrderHelpScreen(){
        spinnertext.setVisibility(View.INVISIBLE);
        spinnerarrow.setVisibility(View.INVISIBLE);
        enabletext.setVisibility(View.INVISIBLE);
        enablearrow.setVisibility(View.INVISIBLE);
        selecttext.setVisibility(View.INVISIBLE);
        selectarrow.setVisibility(View.INVISIBLE);
        ordertext.setVisibility(View.INVISIBLE);
        orderarrow.setVisibility(View.INVISIBLE);
        switch (count){
            case 0:
                spinnerarrow.setVisibility(View.VISIBLE);
                spinnertext.setVisibility(View.VISIBLE);
                count = 1;
                break;
            case 1:
                enabletext.setVisibility(View.VISIBLE);
                enablearrow.setVisibility(View.VISIBLE);
                count = 2;
                break;
            case 2:
                selecttext.setVisibility(View.VISIBLE);
                selectarrow.setVisibility(View.VISIBLE);
                count = 3;
                break;
            case 3:
                ordertext.setVisibility(View.VISIBLE);
                orderarrow.setVisibility(View.VISIBLE);
                count = 0;
                break;
            default:
                break;
        }
    }
}
