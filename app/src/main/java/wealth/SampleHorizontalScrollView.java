package wealth;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.HorizontalScrollView;

/**
 * Created by Admin on 22/05/14.
 */
public class SampleHorizontalScrollView extends HorizontalScrollView {
    Button left;
    Button right;
    int maxWidth;

    public SampleHorizontalScrollView(Context context) {
        super(context);
    }

    public SampleHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SampleHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLeftRightButton(Button lft, Button rght, int width) {
        try {
            this.left = lft;
            this.right = rght;
            if (width > 0) {
                this.maxWidth = width;
            } else {
                this.maxWidth = this.getMaxScrollAmount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        // TODO Auto-generated method stub
        try {
            super.onScrollChanged(x, y, oldx, oldy);
            //int maxScroll = this.getWidth();
            if (x <= 5) {
                left.setVisibility(INVISIBLE);
                right.setVisibility(VISIBLE);
            } else if (x >= (this.maxWidth - this.getWidth())) {
                left.setVisibility(VISIBLE);
                right.setVisibility(INVISIBLE);
            } else {
                left.setVisibility(VISIBLE);
                right.setVisibility(VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
