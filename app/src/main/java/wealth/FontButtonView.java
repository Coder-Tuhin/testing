package wealth;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ventura.venturawealth.R;
import utils.Formatter;
import utils.ScreenColor;


/**
 * Created by Admin on 25/03/2015.
 */

public class FontButtonView extends Button {
    public static Typeface FONT_NAME;

    public FontButtonView(Context context) {
        super(context);
        try {
            this.setBackgroundResource(0);
            this.setTextColor(ScreenColor.textColor);
            this.setBackgroundResource(R.drawable.border);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Formatter.intTODP(context, 35));
            this.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FontButtonView(Context context, String text, int gravity, int width) {
        super(context);
        try {
            this.setText(text);
            this.setBackgroundResource(0);
            this.setGravity(gravity);
            if (width != 0) this.setWidth(width);
            this.setTextColor(ScreenColor.textColor);
            this.setBackgroundResource(R.drawable.border);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Formatter.intTODP(context, 35));
            this.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FontButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            this.setBackgroundResource(0);
            this.setTextColor(ScreenColor.textColor);
            this.setBackgroundResource(R.drawable.border);
            this.setTextSize(14);
            this.setTypeface(Typeface.DEFAULT_BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

