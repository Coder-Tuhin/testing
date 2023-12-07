package wealth.new_mutualfund.factSheet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ventura.venturawealth.R;

public class GauseViewGdev {
    private Context context;
    private ImageView imageView;
    private int sliceAngel = 180/5;
    private int R_;
    private Paint textPaint;
    private int outerRadius, innerRadius;
    private int newLineTextSlice = 30;
    private int width, height;
    private Canvas canvas;

    public GauseViewGdev(ImageView imageView, Context context) {
        this.context = context;
        this.imageView = imageView;
    }

    public void initiateDrawing() {
        try {
            ViewTreeObserver vto = imageView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    width = imageView.getMeasuredWidth();
                    height = imageView.getMeasuredHeight();

                    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    textPaint.setColor(context.getResources().getColor(R.color.black_deep));
                    textPaint.setTextSize(24);

                    outerRadius = (int) (width*0.5);
                    innerRadius = (int) (outerRadius - outerRadius*0.28);
                    R_ =  innerRadius + (outerRadius-innerRadius)/2;

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(bitmap);

                    drawColoredHollow();
                    drawCoverMoon();
                    drawCenterHalfSphere();

                    setLowText();
                    setModerateLowText();
                    setModerateText();
                    setModerateHighText();
                    setHighText();
                    imageView.setImageBitmap(bitmap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    pin can be 0, 1, 2, 3, 4 for slice area 12345 resp
     */

    public void setPointer(int pin){
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(context.getResources().getColor(R.color.centerHaldfMoonColor));
        linePaint.setStrokeWidth(10);

        int angle = (int) (pin*sliceAngel + 0.5*sliceAngel);
        int R =  innerRadius;
        float x = (float) (R*Math.cos(Math.toRadians(angle)));
        float y = (float) (R*Math.sin(Math.toRadians(angle)));
        float x_ = width/2 - x;
        float y_ = height - y;
        canvas.drawLine(width/2, height, x_, y_, linePaint);
    }

    private void setLowText(){
        int angle = 10;
        float x = (float) (R_*Math.cos(Math.toRadians(angle)));
        float y = (float) (R_*Math.sin(Math.toRadians(angle)));

        float x_ = width/2 - x;
        float y_ = height - y;
        canvas.save();
        canvas.rotate(-sliceAngel*2, x_, y_);
        canvas.drawText("Low", x_, y_, textPaint);
        canvas.restore();
    }

    private void setModerateLowText(){
        int angle = sliceAngel + 5;
        float x = (float) (R_*Math.cos(Math.toRadians(angle)));
        float y = (float) (R_*Math.sin(Math.toRadians(angle)));

        float x_ = width/2 - x;
        float y_ = height - y;
        canvas.save();
        canvas.rotate((float) (-sliceAngel*1.1), x_, y_);
        canvas.drawText("Moderate", x_, y_, textPaint);
        canvas.restore();

        angle = sliceAngel + 10;
        x = (float) ((R_-newLineTextSlice)*Math.cos(Math.toRadians(angle)));
        y = (float) ((R_-newLineTextSlice)*Math.sin(Math.toRadians(angle)));

        x_ = width/2 - x;
        y_ = height - y;
        canvas.save();
        canvas.rotate((float) (-sliceAngel*1.1), x_, y_);
        canvas.drawText("Low", x_, y_, textPaint);
        canvas.restore();
    }

    private void setModerateText(){
        int angle = 2*sliceAngel + 5;
        float x = (float) (R_*Math.cos(Math.toRadians(angle)));
        float y = (float) (R_*Math.sin(Math.toRadians(angle)));

        float x_ = width/2 - x;
        float y_ = height - y;
        canvas.save();
        canvas.rotate(0, x_, y_);
        canvas.drawText("Moderate", x_, y_, textPaint);
        canvas.restore();
    }

    private void setModerateHighText(){
        int angle = 3*sliceAngel + 5;
        float x = (float) (R_*Math.cos(Math.toRadians(angle)));
        float y = (float) (R_*Math.sin(Math.toRadians(angle)));

        float x_ = width/2 - x;
        float y_ = height - y;
        canvas.save();
        canvas.rotate(sliceAngel, x_, y_);
        canvas.drawText("Moderate", x_, y_, textPaint);
        canvas.restore();

        angle = 3*sliceAngel + 10;
        x = (float) ((R_-newLineTextSlice)*Math.cos(Math.toRadians(angle)));
        y = (float) ((R_-newLineTextSlice)*Math.sin(Math.toRadians(angle)));

        x_ = width/2 - x;
        y_ = height - y;
        canvas.save();
        canvas.rotate(sliceAngel, x_, y_);
        canvas.drawText("High", x_, y_, textPaint);
        canvas.restore();
    }

    private void setHighText(){
        int angle = 4*sliceAngel + 15;
        float x = (float) (R_*Math.cos(Math.toRadians(angle)));
        float y = (float) (R_*Math.sin(Math.toRadians(angle)));

        float x_ = width/2 - x;
        float y_ = height - y;
        canvas.save();
        canvas.rotate((float) (2*sliceAngel*.9), x_, y_);
        canvas.drawText("High", x_, y_, textPaint);
        canvas.restore();
    }

    private void drawCoverMoon(){
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(context.getResources().getColor(R.color.black));

        int mRadius = innerRadius;
        int left = width/2- mRadius;
        int right = width/2 + mRadius;
        int top = height - mRadius;
        int bottom = height+mRadius;

        final RectF rect = new RectF();
        rect.set(left, top, right, bottom);

        canvas.drawArc(rect, 180, 180, true, rectPaint);

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(context.getResources().getColor(R.color.white));
        for (int i = 1; i < 5; i++) {
            int angle = i*sliceAngel;
            int R =  innerRadius;
            float x = (float) (R*Math.cos(Math.toRadians(angle)));
            float y = (float) (R*Math.sin(Math.toRadians(angle)));
            float x_ = width/2 - x;
            float y_ = height - y;
            canvas.drawLine(width/2, height, x_, y_, linePaint);
        }
    }

    private void drawColoredHollow(){
        int colorArr[] = {
                context.getResources().getColor(R.color.lowHollowColor),
                context.getResources().getColor(R.color.moderateLowHollowColor),
                context.getResources().getColor(R.color.moderateHollowColor),
                context.getResources().getColor(R.color.moderateHighHollowColor),
                context.getResources().getColor(R.color.highHollowColor)
        };

        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int mRadius = outerRadius;
        int left = width/2- mRadius;
        int right = width/2 + mRadius;
        int top = height - mRadius;
        int bottom = height+mRadius;

        final RectF rect = new RectF();
        rect.set(left, top, right, bottom);

        for (int i = 0; i <5 ; i++) {
            rectPaint.setColor(colorArr[i]);
            canvas.drawArc(rect, 180+i*sliceAngel, sliceAngel, true, rectPaint);
        }
    }


    private void drawCenterHalfSphere(){
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(context.getResources().getColor(R.color.centerHaldfMoonColor));

        int mRadius = 25;
        int left = width/2- mRadius;
        int right = width/2 + mRadius;
        int top = height - mRadius;
        int bottom = height+mRadius;

        final RectF rect = new RectF();
        rect.set(left, top, right, bottom);

        canvas.drawArc(rect, 180, 180, true, rectPaint);
    }
}
