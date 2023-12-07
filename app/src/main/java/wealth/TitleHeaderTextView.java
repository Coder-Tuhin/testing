package wealth;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Admin on 28/03/2015.
 */
public class TitleHeaderTextView extends TextView {

    public TitleHeaderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextColor(Color.GRAY);
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public TitleHeaderTextView(Context context, int width, String text, int gravity) {
        super(context);
        this.setText(text);
        this.setGravity(gravity);
        this.setTextColor(Color.GRAY);
        this.setWidth(width);
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
}
