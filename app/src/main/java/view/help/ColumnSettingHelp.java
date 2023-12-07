package view.help;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import enums.eConstant;
import enums.ePrefTAG;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 28-Aug-2017.
 */
public class ColumnSettingHelp extends Dialog implements View.OnClickListener{

    private boolean isHelpClick;

    public ColumnSettingHelp(Context context,boolean isHelpClick) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.setContentView(R.layout.coloumns_help_screen);
        TextView coloumntext = (TextView) this.findViewById(R.id.coloumntext);
        Animation slideDown = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);
        coloumntext.setAnimation(slideDown);
        this.isHelpClick = isHelpClick;
        ((Button) findViewById(R.id.btnGot)).setOnClickListener(this);
        this.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGot:
                if (!isHelpClick){
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.COLUMN_HELP.name,false);
                }
                this.dismiss();
                break;
        }
    }

}

