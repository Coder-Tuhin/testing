package view.help;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import enums.ePrefTAG;
import utils.GlobalClass;
import utils.PreferenceHandler;

/**
 * Created by XTREMSOFT on 26-Mar-2018.
 */

public class ChartHelp extends Dialog implements View.OnClickListener{

    private boolean isHelpClick;

    public ChartHelp(boolean isHelpClick) {
        super(GlobalClass.latestContext,android.R.style.Theme_Translucent_NoTitleBar);
        this.setContentView(R.layout.chart_help);
        this.isHelpClick = isHelpClick;
        findViewById(R.id.root).setOnClickListener(this);
        this.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.root:
                if (!isHelpClick){
                    PreferenceHandler.setChartHelp(false);
                }
                this.dismiss();
                break;
        }
    }

}