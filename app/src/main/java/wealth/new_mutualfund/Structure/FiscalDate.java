package wealth.new_mutualfund.Structure;

import java.util.Calendar;
import java.util.Date;

public class FiscalDate {

    private static final int    FIRST_FISCAL_MONTH  = Calendar.APRIL;
    private Calendar calendarDate;

    public FiscalDate(Calendar calendarDate) {
        this.calendarDate = calendarDate;
    }

    public FiscalDate(Date date) {
        this.calendarDate = Calendar.getInstance();
        this.calendarDate.setTime(date);
    }

    public int getFiscalMonth() {
        int month = calendarDate.get(Calendar.MONTH);
        int result = ((month - FIRST_FISCAL_MONTH - 1) % 12) + 1;
        if (result < 0) {
            result += 12;
        }
        return result;
    }

    public int getFiscalYear() {
        int month = calendarDate.get(Calendar.MONTH);
        int year = calendarDate.get(Calendar.YEAR);
        return (month >= FIRST_FISCAL_MONTH) ? year : year - 1;
    }

    public int getCalendarMonth() {
        return calendarDate.get(Calendar.MONTH);
    }

    public int getCalendarYear() {
        return calendarDate.get(Calendar.YEAR);
    }

    /*
    private static Calendar setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private static void displayFinancialDate(Calendar calendar) {
        FiscalDate fiscalDate = new FiscalDate(calendar);
        int year = fiscalDate.getFiscalYear();
        GlobalClass.log("Current Date : " + calendar.getTime().toString());
        GlobalClass.log("Fiscal Years : " + year + "-" + (year + 1));
        GlobalClass.log("Fiscal Month : " + fiscalDate.getFiscalMonth());
        GlobalClass.log(" ");
    }*/
}

