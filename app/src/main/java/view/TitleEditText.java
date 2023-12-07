package view;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by XTREMSOFT on 11/23/2016.
 */
public class TitleEditText extends TextView {
    private int textSize = 12;

    public TitleEditText(Context context, String text, int gravity, int width, int textColor) {
        super(context);
        try {
            this.setText(text);
            //this.setTextSize(textSize);
            this.setTextColor(textColor);
            this.setBackgroundResource(0);
            this.setTypeface(Typeface.DEFAULT_BOLD);
            //this.setEnabled(false);
            if (width > 0) {
                this.setWidth(width);
                this.setMaxWidth(width);
            }
            this.setPadding(5, 2, 5, 2);
            this.setGravity(gravity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
