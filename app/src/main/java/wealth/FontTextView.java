package wealth;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import utils.ScreenColor;


public class FontTextView extends TextView {
    public static Typeface FONT_NAME;


    public FontTextView(Context context) {
        super(context);
        this.setTextColor(ScreenColor.textColor);
    }

    public FontTextView(Context context, String text) {
        super(context);
        this.setTextColor(ScreenColor.textColor);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setTextColor(ScreenColor.textColor);

    }

    public FontTextView(Context context, int color, String text, int gravity, int width, Typeface typeface) {
        super(context);
        try {
            this.setText(text);
            this.setGravity(gravity);
            this.setClickable(true);
            this.setBackgroundResource(0);
            this.setTypeface(typeface);
            this.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            this.setWidth(width);
            this.setTextColor(color);
            this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public FontTextView(Context context, String text, int width, int gravity) {
        super(context);
        try {
            this.setText(text);
            this.setGravity(gravity);
            this.setWidth(width);
            this.setTextColor(ScreenColor.textColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}