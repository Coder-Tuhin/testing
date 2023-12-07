package wealth;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.ventura.venturawealth.R;


/**
 * Created by Nirav on 7/3/14.
 */
public class CustomizedButton extends Button {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public CustomizedButton(Context context, String text, int gravity,int width, int textColor,
                            int bgColor, String parentLayout) {
        super(context);

        try {
            /*GradientDrawable gradientLoginBack = new GradientDrawable();
            gradientLoginBack.setColor(bgColor);
            gradientLoginBack.setCornerRadius(10);*/

            if (parentLayout.equalsIgnoreCase("table")) {
                //TableRow.LayoutParams params = new TableRow.LayoutParams(width, ModuleClass.intTODP(this.getContext(), 35));
                TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ModuleClass.intTODP(this.getContext(), 35));
                this.setLayoutParams(params);
            } else {
                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ModuleClass.intTODP(this.getContext(), 35));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ModuleClass.intTODP(this.getContext(), 40));
                this.setLayoutParams(params);
            }

            this.setText(text);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.setTextSize(14);      // only for jellybean and newer versions
            } else {
                this.setTextSize(10);
            }
            this.setTypeface(Typeface.DEFAULT_BOLD);

            //this.setTextColor(textColor);
            this.setTextColor(Color.WHITE);
            //this.setBackground(gradientLoginBack);
        /*int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackgroundDrawable(gradientLoginBack);
        } else {
            this.setBackground(gradientLoginBack);
        }*/
            this.setBackgroundResource(R.drawable.border);
            this.setTypeface(Typeface.DEFAULT_BOLD);
            this.setGravity(gravity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}