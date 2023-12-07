package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by XtremsoftTechnologies on 01/02/16.
 */
public class OverrideFont {
    private Typeface tf;
    private Context context;
    private String fontFileName;
    private int fontSize;
   public OverrideFont(Context context)
    {
        this.context=context;
        fontFileName="comic.ttf";
        fontSize=14;
        setTypeFace();
    }
    public OverrideFont(Context context, String fontFileName)
    {
        this(context);
        this.fontFileName=fontFileName;
        fontSize=12;
        setTypeFace();

    }

    public OverrideFont(Context context, int fontSize) {
        this(context);
        this.fontSize = fontSize;
        setTypeFace();
    }


    public OverrideFont(Context context, String fontFileName, int fontSize)
    {
        this(context);
        this.fontSize=fontSize;
        this.fontFileName=fontFileName;
        setTypeFace();
    }

    public void setTypeFace()
    {
        try {
            tf = Typeface.createFromAsset(context.getAssets(), fontFileName);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public Typeface getTypeFace()
    {
        return tf;
    }
    public void setFontAndSize(final View v)
    {
        overrideFonts(v);
        applyFontSize(v);
    }
    public  void overrideFonts(final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(tf);
            } else if (v instanceof EditText) {
                ((Button) v).setTypeface(tf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }
    public  void applyFontSize(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFontSize( child);
                }
            }else if (v instanceof TextView) {
                ((TextView) v).setTextSize(fontSize);
            } else if (v instanceof Button) {
                ((Button) v).setTextSize(fontSize);
            } else if (v instanceof EditText) {
                ((Button) v).setTextSize(fontSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    public String getFontFileName() {
        return fontFileName;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
