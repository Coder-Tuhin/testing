package chart;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JSApplicationBridge {
    private Context context;

    public JSApplicationBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void onIntervalChanged(String newInterval) {
        Toast toast = Toast.makeText(context, "New chart widget interval is " + newInterval, Toast.LENGTH_SHORT);
        toast.show();
    }
}
