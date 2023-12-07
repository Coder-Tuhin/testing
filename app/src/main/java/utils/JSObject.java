package utils;

import android.webkit.JavascriptInterface;

public class JSObject {
    @JavascriptInterface
    public String toString() { return "injectedObject"; }
}
