package wealth.mv;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.ventura.venturawealth.R;

import utils.PreferenceHandler;

public class HoldingHelpScreen extends Dialog {
    public HoldingHelpScreen(@NonNull Context context) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        try {
            this.setContentView(R.layout.homehelp_four);
            this.setCancelable(true);
            Button btnGot = this.findViewById(R.id.btnGot);
            btnGot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
