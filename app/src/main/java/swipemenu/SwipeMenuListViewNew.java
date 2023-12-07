package swipemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by XTREMSOFT on 20-Jun-2017.
 */
public class SwipeMenuListViewNew extends ListView implements View.OnTouchListener{
    private final GestureDetector gestureDetector;
    Context context;

    public SwipeMenuListViewNew(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
        init(context);
    }

    public SwipeMenuListViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetector(context, new GestureListener());
        init(context);
    }

    public SwipeMenuListViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureListener());
        init(context);
    }

    private void init(Context context) {
        this.setOnTouchListener(this);
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                int position = SwipeMenuListViewNew.this.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()));
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight(position);
                        } else {
                            onSwipeLeft(position);
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom(position);
                    } else {
                        onSwipeTop(position);
                    }
                    result = true;
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
    void onSwipeRight(int position) {
        this.onSwipe.swipeRight(position);
    }
    void onSwipeLeft(int position) {
        //Toast.makeText(context, "Swiped Left", Toast.LENGTH_SHORT).show();
        this.onSwipe.swipeLeft(position);
    }
    void onSwipeTop(int position) {
        //Toast.makeText(context, "Swiped Up", Toast.LENGTH_SHORT).show();
        this.onSwipe.swipeTop(position);
    }
    void onSwipeBottom(int position) {
        //Toast.makeText(context, "Swiped Down", Toast.LENGTH_SHORT).show();
        this.onSwipe.swipeBottom(position);
    }
    public interface onSwipeListener {
        void swipeRight(int position);
        void swipeTop(int position);
        void swipeBottom(int position);
        void swipeLeft(int position);
    }
    onSwipeListener onSwipe;
    public void setOnSwipeListener(onSwipeListener _onSwipe){
        onSwipe = _onSwipe;
    }
}
