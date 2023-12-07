package wealth;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ventura.venturawealth.R;
import utils.ScreenColor;


public class FontEditView extends EditText {
    public static Typeface FONT_NAME;

    public FontEditView(Context context) {
        super(context);

        try {
            this.setBackgroundResource(R.drawable.border);
            this.setTextColor(ScreenColor.textColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FontEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
       /* if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "comic.ttf");
         this.setTypeface(FONT_NAME);*/
        this.setTextColor(ScreenColor.textColor);

    }
   /* public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "fonts/FontName.otf");
        this.setTypeface(FONT_NAME);
    }*/
}