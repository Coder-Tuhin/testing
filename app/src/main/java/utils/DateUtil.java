package utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by XTREMSOFT on 11/15/2016.
 */
public class DateUtil {
    private static String className ="DateUtil";

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat DDMMMYYYY = null;

    public static SimpleDateFormat getDDMMMYYYY(){
        if(DDMMMYYYY == null){
            DDMMMYYYY = new SimpleDateFormat("ddMMMyyyy");
            DDMMMYYYY.setTimeZone(TimeZone.getTimeZone("IST"));
        }
        return DDMMMYYYY;
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public static String dateFormatter(Date date, String formatter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formatter);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (Exception pe) {
            GlobalClass.onError("Error in dateFormater method of " + className, pe);
        }
        return "";
    }
    public static Date stringTodate(String date, String formatter) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formatter);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            Date formattedDate = sdf.parse(date);
            return formattedDate;
        } catch (Exception pe) {
            GlobalClass.onError("Error in dateFormater method of " + className, pe);
        }
        return new Date();
    }
    public static String dateFormatter(int iSecs,String formatter) {
        try {
            Date oldDate=new Date(80,0,1);
            long mSeconds=oldDate.getTime()+((long)iSecs*1000);
            Date date=new Date(mSeconds);
            SimpleDateFormat sdf = new SimpleDateFormat(formatter);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (Exception pe) {
            GlobalClass.onError("Error in dateFormater method of "+className, pe);
        }
        return "";
    }

    public static int compareDatesOld(long lastPassChangeDate) {
        try {
            Calendar calendar = Calendar.getInstance();
            long currDate = calendar.getTimeInMillis();
            calendar.setTimeInMillis(lastPassChangeDate * 1000);
            lastPassChangeDate = calendar.getTimeInMillis();
            long daysDiffMilli = currDate - lastPassChangeDate;
            int dateDiff = (int) ((daysDiffMilli) / (1000 * 60 * 60 * 24));//currDate.compareTo(lastPwdChangeDate);
            return dateDiff;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long DToN(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ddMMMyy);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(strDate);

            long seconds = date.getTime() / 1000;

            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            //Date prevDate = prevDateFormat.parse("01-01-1980");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            //Log.v("", prevDate.getTime());
            //long millis = (seconds * 1000) + (prevDate.getTime() * 1000);
            //long millis = (seconds * 1000) + (31557600 * 1000 * 10);
            seconds = seconds - prevDate.getTime() / 1000;
            //SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a");
            //Log.v("", seconds);
            return seconds;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return 0;
    }


    public static long DToN(String strDate,String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(strDate);
            long seconds = date.getTime() / 1000;
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            //Date prevDate = prevDateFormat.parse("01-01-1980");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            seconds = seconds - prevDate.getTime() / 1000;
            return seconds;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return 0;
    }

    public static int numberofDays(int yr,boolean today){
        int days=0;
        try {
            Calendar now = Calendar.getInstance();
            if (!today){
                now.set(yr,1,1);
            }
            Calendar tmp = Calendar.getInstance();
            tmp.set(0,0,0);
            for (int i=1900; i < now.get(Calendar.YEAR);i++) {
                tmp.set(Calendar.YEAR, i);
                int daysForThatYear = tmp.getActualMaximum(Calendar.DAY_OF_YEAR);
                days+=daysForThatYear;
            }
            if (today){
                int daysThisYear = now.get(Calendar.DAY_OF_YEAR);
                days+=daysThisYear;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return days;
    }

    public static Date dateByDays(int days){
        Calendar cal = Calendar.getInstance();
        cal.set(1900, Calendar.JANUARY, 1);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    /*
    public static long DToN(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.DDMMMYY);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(strDate);
            long seconds = date.getTime() / 1000;
            Date prevDate=new Date(80,0,1);
            seconds = seconds - (prevDate.getTime() / 1000);
            return seconds;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return 0;
    }*/

    public static int addDaysInDate(int dwmy, int noOfCount) {
        long seconds = 0;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Now use today date.
            c.add(dwmy, noOfCount);
            seconds = c.getTime().getTime() / 1000;
            Date prevDate = new Date(80, 0, 1);
            seconds = seconds - (prevDate.getTime() / 1000);
        } catch (Exception e) {
            GlobalClass.onError("Error in getFixTimeInNumber method of " + className, e);
        }
        return (int)seconds;
    }
    public static int compareDates(long firstDate, long secondDate) {
        try {
            int dateDiff = (int) ((firstDate - secondDate) / ( 60 * 60 * 24));
            return Math.abs(dateDiff);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int compareDates(Date firstDate, Date secondDate) {
        try {
            int dateDiff = (int) ((firstDate.getTime() - secondDate.getTime()) / ( 60 * 60 * 24 * 1000));
            return dateDiff;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static String getCurrentDate() {
        try {
            Date currDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.ddMMMyy);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedDate = sdf.format(currDate);
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getCurrentDatenew() {
        try {
            Date currDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedDate = sdf.format(currDate);
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long CurrentTimeToN() {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            long seconds = cal.getTimeInMillis() / 1000;
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT+5:30");
            seconds = seconds - (int) (prevDate.getTime() / 1000);
            return seconds;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return 0;
    }

    public static long DTToN(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = sdf.parse(strDate);

            long seconds = date.getTime() / 1000;

            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");

            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");

            seconds = seconds - prevDate.getTime() / 1000;

            return seconds;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return 0;
    }
    public static short getTimeDiffInSeconds() {
        try {
            Calendar date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, 8);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            //GlobalClass.log("Time at 8am : " + date.getTimeInMillis() + " Current Time : " + System.currentTimeMillis());
            long startTime = date.getTimeInMillis()/1000;
            long currTime = System.currentTimeMillis()/1000;
            short difference = (short)(currTime - startTime);
            //GlobalClass.log("Difference : " + difference);
            return difference;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static String NToT(long seconds) {
        try {
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            long millis = (seconds * 1000) + prevDate.getTime();
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "";
    }

    public static String NToD(long seconds) {
        try {
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            long millis = (seconds * 1000) + prevDate.getTime();
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "";
    }

    public static String DateForNotification(long seconds) {
        try {
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            long millis = (seconds * 1000) + prevDate.getTime();
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "";
    }
    public static String getOnlyDateForNotification(long seconds) {
        try {
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            long millis = (seconds * 1000) + prevDate.getTime();
            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "";
    }


    public static long getFixTimeInNumber(int h,int m,int s) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,h);
            cal.set(Calendar.MINUTE,m);
            cal.set(Calendar.SECOND,s);
            cal.set(Calendar.MILLISECOND, 0);
            long seconds = cal.getTimeInMillis()/ 1000;
            Date prevDate=new Date(80,0,1);
            seconds = seconds - prevDate.getTime() / 1000;
            return seconds;
        } catch (Exception e) {
            GlobalClass.onError("Error in getFixTimeInNumber method of " + className, e);
        }
        return 0;
    }

    public static Date getFixTime(int h,int m,int s) {
        try {
            Date date = new Date();
            date.setHours(h);
            date.setMinutes(m);
            date.setSeconds(s);
            return date;
        } catch (Exception e) {
            GlobalClass.onError("Error in getFixTimeInNumber method of "+className, e);
        }
        return null;
    }

    public static int compareTimes(Date d1, Date d2) {
        //   return d1.compareTo(d2);
        int     t1;
        int     t2;
        t1 = (int) (d1.getTime() % (24*60*60*1000L));
        t2 = (int) (d2.getTime() % (24*60*60*1000L));
        return (t1 - t2);
    }

    public static long getcurrentTimeToN() {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            long seconds = cal.getTimeInMillis();
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT+5:30");
            seconds = seconds - (prevDate.getTime());
            //seconds = (prevDate.getTime() / 1000);
            return seconds;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return 0;
    }

    public static String ddMMMyyyyFormat(String strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date varDate=dateFormat.parse(strDate);
            dateFormat=new SimpleDateFormat("ddMMMyy");
            return dateFormat.format(varDate);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSameDayNextMonthDate(){
        String dateStr = getCurrentDate();
        try{
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, 1);
            dateStr = sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getSameDayNextMonthDate_2(){
        String dateStr = getCurrentDate();
        try{
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            //c.add(Calendar.MONTH, 0); // 1->0 As per Har. Previously she told me to make a gap of 30 day. Now she said it should be current month. Done on 07102020
            dateStr = sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String futureSipDate(String dateStr, String frequency){
        try{
            String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

            Date todayDate = new Date();
            String todayStr = DateUtil.dateFormatter(todayDate,"dd MMM yyyy");
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd MMM yyyy");
            Date sipDate = formatter.parse(dateStr);

            int todayYEAR = todayDate.getYear()+1900;
            int todayMONTH = todayDate.getMonth()+1;
            int todayDAY = todayDate.getDate();

            int sipMONTH = sipDate.getMonth()+1;
            int sipDAY = sipDate.getDate();

            String futDate = "";
            if (frequency.equalsIgnoreCase("Monthly")){
                if (sipDAY>todayDAY){
                    futDate = dateStr.split(" ")[0]+" "+todayStr.split(" ")[1]+" "+todayYEAR;
                }else if (sipDAY<todayDAY){
                    sipMONTH = todayMONTH + 1;
                    if (sipMONTH>12){
                        sipMONTH = sipMONTH - 12;
                        todayYEAR = todayYEAR + 1;
                    }
                    futDate = dateStr.split(" ")[0]+" "+months[sipMONTH-1]+" "+todayYEAR;
                }else {
                    futDate = todayStr;
                }
            }else{ //Frequency: Quaterly
                sipMONTH = sipMONTH + 3;
                if (sipMONTH<=todayMONTH){
                    futDate = dateStr.split(" ")[0]+" "+months[sipMONTH-1]+" "+todayYEAR;
                }else {
                    if (sipMONTH>12){
                        sipMONTH = sipMONTH - 12;
                        todayYEAR = todayYEAR + 1;
                    }
                    futDate = dateStr.split(" ")[0]+" "+months[sipMONTH-1]+" "+todayYEAR;
                }
            }
            GlobalClass.log("Future date: "+futDate);
            return futDate;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String getTodaysData(){
        try {
            Date currDate = new Date();
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            String formattedDate = sdf.format(currDate);
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getYearsBackData(int yearsBack){
        yearsBack = (-1)*yearsBack;
        String dateStr = getTodaysData();
        try{
            //java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, yearsBack);
            //c.add(Calendar.MONTH, 1);
            dateStr = sdf.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return dateStr;
    }
    public static int getDaysSince1900(Date forDate){
        try{
            Date d1 = new Date(0,0,1);
            //SimpleDateFormat sdf = new SimpleDateFormat(formmater);
            Date d2 = forDate;//sdf.parse(strDate);
            // Date d2 = new Date(94,8,27);
            //GlobalClass.log(d1+":"+d2);
            return (int) TimeUnit.DAYS.convert(d2.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    public static String  getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static String NToDDMMMYY(long seconds) {
        try {
            SimpleDateFormat prevDateFormat = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss z");
            Date prevDate = prevDateFormat.parse("January 1, 1980, 00:00:00 GMT");
            long millis = (seconds * 1000) + prevDate.getTime();

            Date date = new Date(millis);
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String formattedDate = sdf.format(date);

            return formattedDate;
        } catch (ParseException pe) {
            GlobalClass.onError("", pe);
        }
        return "";
    }
}