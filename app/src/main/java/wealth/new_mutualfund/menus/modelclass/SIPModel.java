package wealth.new_mutualfund.menus.modelclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class SIPModel {

    public double minAmt;
    public double maxAmt;
    public double multipleAmt;

    public int minPeriod;
    public int maxPeriod;
    public String[] sipdays;
    public ArrayList dateV;


    public void populateDate(String days){

        sipdays = days.split("\\,");
        dateV = new ArrayList();

        for (int i = 0; i < sipdays.length; i++) {
            dateV.add(sipdays[i]);
        }

        /*
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        dateV = new ArrayList();
        month = month + 1;
        for (int j = month; j <= month + 2; j++) {
            if (j == month) {
                for (int i = 0; i < sipdays.length; i++) {
                    if (Integer.parseInt(sipdays[i]) > day) {
                        dateV.add(dateChanger(sipdays[i] + "/" + j + "/" + year));
                    }
                }
            } else if (j == month + 1) {
                for (int i = 0; i < sipdays.length; i++) {
                    if (month == 12) {
                        dateV.add(dateChanger(sipdays[i] + "/" + j + "/" + year + 1));
                    } else {
                        dateV.add(dateChanger(sipdays[i] + "/" + j + "/" + year));
                    }
                }

            } else {
                for (int i = 0; i < sipdays.length; i++) {
                    if (Integer.parseInt(sipdays[i]) <= day) {
                        if (month == 12) {
                            dateV.add(dateChanger(sipdays[i] + "/" + j + "/" + year + 1));
                        } else {
                            dateV.add(dateChanger(sipdays[i] + "/" + j + "/" + year));
                        }
                    }
                }
            }
        }*/
    }
}
