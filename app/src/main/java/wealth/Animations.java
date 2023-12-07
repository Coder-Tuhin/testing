package wealth;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Nirav on 28-Apr-15.
 */
public class Animations {
    static boolean blink;

    public static void blinkText(TextView txt) {
        try {

            final TextView txt1;
            txt1 = (TextView) txt;
            final long totalTimeCountInMilliseconds = 2 * 1000;      // time count for 3 minutes = 180 seconds
            final long timeBlinkInMilliseconds = 10 * 1000;
            // TODO Auto-generated method stub
            final Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int timeToBlink = 1200;    //in ms
                    try {
                        Thread.sleep(timeToBlink);
                    } catch (Exception e) {
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new CountDownTimer(totalTimeCountInMilliseconds, 500) {
                                public void onTick(long leftTimeInMilliseconds) {
                                    long seconds = leftTimeInMilliseconds / 1000;
                                    if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                        if (blink) {
                                            txt1.setVisibility(View.VISIBLE);
                                        } else {
                                            txt1.setVisibility(View.INVISIBLE);
                                        }
                                        blink = !blink;
                                    }
                                }

                                public void onFinish() {
                                    txt1.setVisibility(View.VISIBLE);
                                }
                            }.start();

                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
