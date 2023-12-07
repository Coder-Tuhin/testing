package view.help;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.util.Timer;
import java.util.TimerTask;

import enums.eConstant;
import enums.ePrefTAG;
import utils.GlobalClass;

/**
 * Created by XTREMSOFT on 30-Aug-2017.
 */
public class DepthHelp extends Dialog implements View.OnClickListener {
    private Button btnPrevious, btnNext, btnGot;
    private FrameLayout helpFrame;
    private View helpOne, helpTwo;
    private LinearLayout buttonLayout;
    private boolean isHelpClick;
    private ImageView arrowOne, arrowTwo, arrowThree, arrowFour, arrowFive;
    private TextView textOne, textTwo, textThree, textFour, textFive;
    private Timer timer;
    private int count = 0;
    private Context context;

    public DepthHelp(Context context,boolean isHelpClick) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.setContentView(R.layout.help_screen);
        this.setCancelable(false);
        btnPrevious = (Button) this.findViewById(R.id.btnPrev);
        btnNext = (Button) this.findViewById(R.id.btnNext);
        btnGot = (Button) this.findViewById(R.id.btnGot);
        helpFrame = (FrameLayout) this.findViewById(R.id.helpFrame);
        buttonLayout = (LinearLayout) this.findViewById(R.id.buttonLayout);
        helpOne = LayoutInflater.from(context).inflate(R.layout.depthhelp_one, null);
        helpTwo = LayoutInflater.from(context).inflate(R.layout.depthhelp_two, null);
        arrowOne = (ImageView) helpTwo.findViewById(R.id.arrowOne);
        textOne = (TextView) helpTwo.findViewById(R.id.textOne);
        arrowTwo = (ImageView) helpTwo.findViewById(R.id.arrowTwo);
        textTwo = (TextView) helpTwo.findViewById(R.id.textTwo);
        arrowThree = (ImageView) helpTwo.findViewById(R.id.arrowThree);
        textThree = (TextView) helpTwo.findViewById(R.id.textThree);
        arrowFour = (ImageView) helpTwo.findViewById(R.id.arrowFour);
        textFour = (TextView) helpTwo.findViewById(R.id.textFour);
        arrowFive = (ImageView) helpTwo.findViewById(R.id.arrowFive);
        textFive = (TextView) helpTwo.findViewById(R.id.textFive);
        btnPrevious.setOnClickListener(this);
        btnGot.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        this.isHelpClick = isHelpClick;
        this.context = context;
        setView(helpOne,true);
    }

    @Override
    public void onClick(View view) {
        if (timer != null) timer.cancel();
        switch (view.getId()) {
            case R.id.btnPrev:
                setView(helpOne,true);
                break;
            case R.id.btnGot:
                if (!isHelpClick){
                    VenturaApplication.getPreference().storeSharedPref(ePrefTAG.DEPTH_HELP.name, false);
                }
                this.dismiss();
                break;
            case R.id.btnNext:
                setView(helpTwo,false);
                break;
        }
    }

    private void setView(View layout,boolean firstScreen) {
        helpFrame.removeAllViews();
        btnNext.setVisibility(View.INVISIBLE);
        btnPrevious.setVisibility(View.INVISIBLE);
        btnGot.setVisibility(View.VISIBLE);
        helpFrame.addView(layout);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (firstScreen){
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            btnNext.setVisibility(View.VISIBLE);
        }else {
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            btnPrevious.setVisibility(View.VISIBLE);
            try{
                if (timer != null) timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showingDepthHelpScreen();
                            }
                        });

                    }
                },0,3000);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        buttonLayout.setLayoutParams(lp);
    }

    private void showingDepthHelpScreen() {
        arrowOne.setVisibility(View.INVISIBLE);
        textOne.setVisibility(View.INVISIBLE);
        arrowTwo.setVisibility(View.INVISIBLE);
        textTwo.setVisibility(View.INVISIBLE);
        arrowThree.setVisibility(View.INVISIBLE);
        textThree.setVisibility(View.INVISIBLE);
        arrowFour.setVisibility(View.INVISIBLE);
        textFour.setVisibility(View.INVISIBLE);
        arrowFive.setVisibility(View.INVISIBLE);
        textFive.setVisibility(View.INVISIBLE);
        switch (count){
            case 0:
                arrowOne.setVisibility(View.VISIBLE);
                textOne.setVisibility(View.VISIBLE);
                count = 1;
                break;
            case 1:
                arrowTwo.setVisibility(View.VISIBLE);
                textTwo.setVisibility(View.VISIBLE);
                count = 2;
                break;
            case 2:
                arrowThree.setVisibility(View.VISIBLE);
                textThree.setVisibility(View.VISIBLE);
                count = 3;
                break;
            case 3:
                arrowFour.setVisibility(View.VISIBLE);
                textFour.setVisibility(View.VISIBLE);
                count = 4;
                break;
            case 4:
                arrowFive.setVisibility(View.VISIBLE);
                textFive.setVisibility(View.VISIBLE);
                count = 0;
                break;
            default:
                break;
        }
    }
}
