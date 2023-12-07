package utils;


import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import enums.eExch;

/**
 * Created by XtremsoftTechnologies on 11/02/16.
 */
public class Formatter {

    public static NumberFormat formatter = new DecimalFormat("##,###,##,##0.00");
    public static NumberFormat threeDecimal_formatter = new DecimalFormat("#,##,##0.000");
    public static NumberFormat clk_formatter = new DecimalFormat("#,##,##0.0");
    public static final NumberFormat roundFormatter = new DecimalFormat("###,##,##,##,##0");
    public static final NumberFormat fourDigitFormatter = new DecimalFormat("###,##,##,##,##0.0000");
    public static final NumberFormat mfFormatter = new DecimalFormat("##,##,##,##0");
    public static final NumberFormat mf_one_or_not_decimal = new DecimalFormat("##,##,##,##0.#");
    private static final String className = "Formatter";

    public static NumberFormat getFormatter(int exch){
        if(exch == eExch.NSECURR.value){
            return fourDigitFormatter;
        }else{
            return formatter;
        }
    }
    public static String DecimalLessIncludingComma(String str){
        try{
            if(!str.equalsIgnoreCase("")) {
                str = str.replace(",", "");
                Double d = Double.parseDouble(str);
                return mfFormatter.format(d);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0";
    }

    public static String TwoDecimalIncludingComma(String str){
        try{
            if(!str.equalsIgnoreCase("")) {
                str = str.replace(",", "");
                Double d = Double.parseDouble(str);
                return formatter.format(d);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0.00";
    }


    public static String stringTocommaPer(String str){
        if(!str.equalsIgnoreCase("")) {
            str = str.replace(",", "");
            Double d = Double.parseDouble(str);
            return clk_formatter.format(d);
        }
        return "0";
    }

    public static double stringToDouble(String str){
        if(!str.equalsIgnoreCase("")) {
            str = str.replace(",", "");
            Double d = Double.parseDouble(str);
            return d;
        }
        return 0;
    }
    public static int stringToInt(String str){
        if(!str.equalsIgnoreCase("")) {
            str = str.replace(",", "");
            int d = Integer.parseInt(str);
            return d;
        }
        return 0;
    }
    public static String convertIntToValue(int value) {
        try {
            String finalText = "";
            if (value > 100000000) {
                finalText = "" + clk_formatter.format((double) value / (double) 10000000) + "C";
            } else if (value > 1000000) {
                finalText = "" + clk_formatter.format((double) value / (double) 100000) + "L";
            } else if (value > 10000) {
                finalText = "" + clk_formatter.format((double) value / (double) 1000) + "K";
            } else {
                finalText = "" + value;
            }
            return finalText;
        } catch (Exception e) {
            GlobalClass.onError("Error in convertIntToValue method of " + className, e);
            return "";
        }
    }

    public static String convertDoubleToValue(double value) {
        try {
            String finalText = "";
            if (value > 10000000) {
                finalText = "" + clk_formatter.format((double) value / (double) 10000000) + "C";
            } else if (value > 100000) {
                finalText = "" + clk_formatter.format((double) value / (double) 100000) + "L";
            } else if (value > 1000) {
                finalText = "" + clk_formatter.format((double) value / (double) 1000) + "K";
            } else {
                finalText = "" + formatter.format(value);
            }
            return finalText;
        } catch (Exception e) {
            GlobalClass.onError("Error in convertDoubleToValue method of " + className, e);
            return "";
        }
    }

    public static String convertDoubleToValue_LacCr(double value) {
        try {
            String finalText = "";
            if (value > 10000000 || value < -10000000) {
                finalText = "" + clk_formatter.format((double) value / (double) 10000000) + "C";
            } else if (value > 100000 || value < -100000) {
                finalText = "" + clk_formatter.format((double) value / (double) 100000) + "L";
            } else {
                finalText = "" + formatter.format(value);
            }
            return finalText;
        } catch (Exception e) {
            GlobalClass.onError("Error in convertDoubleToValue method of " + className, e);
            return "";
        }
    }

    public static int getModValue(double baseNubmer, double divisor,int multi) {
        try {
            int modValue = 0;
            modValue = convertDoubleToInt(convertDoubleToInt(baseNubmer*multi) % convertDoubleToInt((divisor*multi)));
            return modValue;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int convertDoubleToInt(double val) {
        try {
            int finalValue;
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            nf.setMinimumFractionDigits(0);
            nf.setGroupingUsed(false);
            String str = nf.format(val);
            finalValue = Integer.parseInt(str);
            return finalValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static boolean isDivisible(double baseNumber, double divisor) {
        try{
            int val1 = (int) Math.round(baseNumber*100)/ (int) Math.round(divisor*100);
            int val2 = (int) Math.round(baseNumber/divisor);
            return val1==val2;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String convertIntToValueForChart(double value) {
        try {
            NumberFormat nf = getDecimalFormat(2);
            String finalText = "";
            try {
                String givenValue = "" + nf.format(value).replaceAll(",", "").trim();
                int valLength = givenValue.replaceAll(",", "").trim().substring(0, givenValue.indexOf(".")).length();
                if (valLength > 8) {
                    finalText = "" + nf.format( value / (double) 10000000) + "C";
                } else if (valLength > 6) {
                    finalText = "" + nf.format( value / (double) 100000) + "L";
                } else if (valLength > 4) {
                    finalText = "" + nf.format( value / (double) 1000) + "K";
                } else {
                    finalText = "" + nf.format(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return finalText;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String convertIntToValueForChart(double value,int decimalPoint) {
        try {
            NumberFormat nf = getDecimalFormat(decimalPoint);
            String finalText = "";
            try {
                String givenValue = "" + nf.format(value).replaceAll(",", "").trim();
                int valLength = givenValue.replaceAll(",", "").trim().substring(0, givenValue.indexOf(".")).length();
                if (valLength > 8) {
                    finalText = "" + nf.format(value / (double) 10000000) + "C";
                } else if (valLength > 6) {
                    finalText = "" + nf.format(value / (double) 100000) + "L";
                } else if (valLength > 4) {
                    finalText = "" + nf.format(value / (double) 1000) + "K";
                } else {
                    finalText = "" + nf.format(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return finalText;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static NumberFormat getDecimalFormat(int digit) {
        NumberFormat nf = null;
        try {
            if (nf != null) {
                return nf;
            } else {
                nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(digit);
                nf.setMinimumFractionDigits(digit);
                nf.setGroupingUsed(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nf;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String toTwoDecimalValue(double value){
        return formatter.format(value);
    }

    public static int intTODP(Context context, int number) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, number, context.getResources().getDisplayMetrics());
    }
    public static String getFourDigitFormatter(double value){
        return Formatter.fourDigitFormatter.format(value);
    }

    /*NEW INTEGRATED*/
    public static String toOneDecimal(double d){
        try {
            return clk_formatter.format(d);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0.0";
    }
    public static String toThreeDecimal(double d){
        try {
            return threeDecimal_formatter.format(d);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0.000";
    }

    public static String toOneDecimalPercent(double d){
        try {
            return clk_formatter.format(d)+"%";
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0%";
    }

    public static String toNoFracValue(double d){
        try {
            return mfFormatter.format(d);
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0";
    }

    public static String mf_one_or_not_decimal(String str){
        try {
            if(!str.equalsIgnoreCase("")) {
                str = str.replace(",", "");
                Double d = Double.parseDouble(str);
                return mf_one_or_not_decimal.format(d);
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
        return "0";
    }


    public static String ConvertKMBCount(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static String ConvertKMBCountDouble(Double number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }
}
