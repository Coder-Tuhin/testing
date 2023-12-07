package fragments.backOffice;


import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.BackofficeModel;

/**
 * Created by XTREMSOFT on 4/7/2017.
 */
public class BackofficeXmlParser {

    private List<BackofficeModel> list = new ArrayList<BackofficeModel>();
    private BackofficeModel backofficeModel;
    private String text = "";

    public List<BackofficeModel> parse(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser  parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("OPENING_BALANCE")) {
                            backofficeModel = new BackofficeModel();}
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("OPENING_BALANCE")) {
                            list.add(backofficeModel);
                        }else if (tagname.equalsIgnoreCase("DT")) {
                            backofficeModel.setDate(getDate(text));
                        }else if (tagname.equalsIgnoreCase("AC")) {
                            backofficeModel.setAccount(text);
                        }else if (tagname.equalsIgnoreCase("AMT")) {
                            backofficeModel.setAmmount(getformatedAmt(text));
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        return list;
    }


    private String getformatedAmt(String text) {
        if (text.equalsIgnoreCase(""))
            return "";
        if (text == null)
            return "";
        return formatedAmt(text);
    }

    private String formatedAmt(String text) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(Double.parseDouble(text));
    }

    private String getDate(String text) {
        if (text.equalsIgnoreCase(""))
            return "";
        if (text == null)
            return "";
        return formateddate(text);
    }

    private String formateddate(String text) {
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            Date date=sdf.parse(text);
            sdf=new SimpleDateFormat("ddMMMyy");
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return text;
    }
}
