package utils;

import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by SUDIP on 17-10-2016.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private GradientDrawable mDivider;
    public DividerItemDecoration(int color,int height) {
       // mDivider = context.getResources().getDrawable(R.drawable.divider);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mDivider = new GradientDrawable();
        mDivider.setColor(color);
        mDivider.setShape(GradientDrawable.RECTANGLE);
        mDivider.setSize(displaymetrics.widthPixels,height);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
