package utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.ventura.venturawealth.R;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DatePick extends DialogFragment{

    private DatePickerDialog.OnDateSetListener listener;
    boolean selecttoday = false;

    public DatePick(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public DatePick(DatePickerDialog.OnDateSetListener listener,boolean selectToday) {
        this.listener = listener;
        this.selecttoday = selectToday;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = 1980;
        int month = 5;
        int day = 15;
        if (selecttoday){
            final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        }


        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(),
                R.style.DatePickerTheme, listener,  year, month, day);
        return mDatePickerDialog;
    }

}