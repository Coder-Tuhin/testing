package utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user456 on 18-Nov-2015.
 */


public class InputFilterMinMax implements InputFilter {
    private static final int MAX_NUMBER = 12;
    private static final int PRECISION = 2;
    public InputFilterMinMax() {
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source.subSequence(start, end).toString());

        if (!builder.toString().matches("(([0-9]{1})([0-9]{0,"+(MAX_NUMBER-1)+"})?)?(\\.[0-9]{0,"+PRECISION+"})?")) {
            if(source.length() == 0)
                return dest.subSequence(dstart, dend);
            return "";
        }
        return null;
    }
}