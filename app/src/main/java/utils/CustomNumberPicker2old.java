package utils;

import android.content.Context;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import com.ventura.venturawealth.R;
import enums.eExch;

/**
 * Created by XTREMSOFT on 11/29/2016.
 */
public class CustomNumberPicker2old extends LinearLayout implements View.OnClickListener {
    private final long REPEAT_DELAY = 50;
    private EditText number;
    private TextView title;
    private int max = -1;
    private boolean autoIncrement = false;
    private boolean autoDecrement = false;
    private Handler repeatUpdateHandler = new Handler();
    private double changeVal = 1;
    private Format moneyFormat_twoDecimal = NumberFormat.getInstance(new Locale("en", "in"));

    public CustomNumberPicker2old(Context context) {
        super(context);
        init();
    }

    public CustomNumberPicker2old(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomNumberPicker2old(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public void setChangeVal(double changeVal) {
        this.changeVal = changeVal;
    }

    private void init() {
        try {
            LinearLayout main = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.custom_number_pickerold, null);
            main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
            addView(main);
            findViewById(R.id.down).setOnClickListener(this);
            findViewById(R.id.up).setOnClickListener(this);
            findViewById(R.id.up).setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    autoIncrement = true;
                    repeatUpdateHandler.post(new RepetitiveUpdater());
                    return false;
                }
            });
            findViewById(R.id.up).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                        autoIncrement = false;
                    }
                    return false;
                }
            });
            findViewById(R.id.down).setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    autoDecrement = true;
                    repeatUpdateHandler.post(new RepetitiveUpdater());
                    return false;
                }
            });
            findViewById(R.id.down).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
                        autoDecrement = false;
                    }
                    return false;
                }
            });
            number = (EditText) findViewById(R.id.edit_text);
            title = (TextView) findViewById(R.id.title);

            ((NumberFormat) moneyFormat_twoDecimal).setMaximumFractionDigits(0);
            ((NumberFormat) moneyFormat_twoDecimal).setMinimumFractionDigits(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMoneyFormat_twoDecimal(int max) {
        ((NumberFormat) moneyFormat_twoDecimal).setMaximumFractionDigits(max);
        ((NumberFormat) moneyFormat_twoDecimal).setMinimumFractionDigits(max);
    }

    public class DecimalDigitsInputFilter_new implements InputFilter {

        private final int decimalDigits;

        public DecimalDigitsInputFilter_new(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        @Override
        public CharSequence filter(CharSequence source,
                                   int start,
                                   int end,
                                   Spanned dest,
                                   int dstart,
                                   int dend) {


            int dotPos = -1;
            int len = dest.length();
            for (int i = 0; i < len; i++) {
                char c = dest.charAt(i);
                if (c == '.') {
                    dotPos = i;
                    break;
                }
            }
            if (dotPos >= 0) {
                if (source.equals(".")) {
                    return "";
                }
                if (dend <= dotPos) {
                    return null;
                }
                if (len - dotPos > decimalDigits) {
                    return "";
                }
            }

            return null;
        }

    }

    public void setDecimalUpto(int max) {
        number.setFilters(new InputFilter[] {
                new DecimalDigitsInputFilter_new(max)
        });
    }

    private void incrementValue() {
        double val = getValue();
        val += changeVal;
        if (max != -1) {
            if (val > max)
                val -= changeVal;
        }
        val = ((Math.floor((double) val / (double) changeVal) + 1) * (double) changeVal);
        setValue(val);
    }

    private void decrementValue() {
        double val = getValue();
        if (val != 0)
            val -= changeVal;
        val = Math.ceil((double) val / (double) changeVal) * (double) changeVal;
        setValue(val);
    }

    @Override
    public void onClick(View view) {
        double val = getValue();
        if (val >= 0) {
            if (view.getId() == R.id.down) {
                val = (Math.ceil((double) val / (double) changeVal) - 1) * (double) changeVal;
                if (val < 0) {
                    val = 0;
                }
            } else {
                val = (Math.round((double) val / (double) changeVal) + 1) * (double) changeVal;
            }
            setValue(val);
        }
    }

    public void setInputType(String inputType,int exch) {
        int maxLength = 6;
        if (inputType.equalsIgnoreCase("qty")) {
            if(exch == eExch.FNO.value) {
                maxLength = 7;
            } else {
                maxLength = 6;
            }
            number.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (inputType.equalsIgnoreCase("price")) {
            maxLength = 12;
            number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            number.setSelectAllOnFocus(true);
        }

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        number.setFilters(FilterArray);
    }


    public void setHint(String hint) {
        number.setHint(hint);
    }

    public void setTitle(String strTitle) {
        title.setText(strTitle);
    }

    public double getValue() {
        if (!number.getText().toString().equalsIgnoreCase(""))
            return Double.parseDouble(number.getText().toString().replaceAll(",", ""));
        return 0;
    }

    public void setValue(Double d) {

        number.setText(moneyFormat_twoDecimal.format(d));
        if (d == 0) {
            number.setText("");
        }
    }

    class RepetitiveUpdater implements Runnable {
        @Override
        public void run() {
            if (autoIncrement) {
                incrementValue();
                repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
            } else if (autoDecrement) {
                decrementValue();
                repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
            }
        }

    }
}
