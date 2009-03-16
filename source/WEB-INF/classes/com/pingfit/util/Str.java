package com.pingfit.util;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * User: Joe Reger Jr
 * Date: Apr 14, 2006
 * Time: 3:20:35 PM
 */
public class Str {

    public static String[] addToStringArray(String[] src, String str){
        if (src==null){
            src=new String[0];
        }

        String[] outArr = new String[src.length+1];
        for(int i=0; i < src.length; i++) {
            outArr[i]=src[i];
        }
        outArr[src.length]=str;
        return outArr;
    }


    public static String cleanForHtml(String instring){
        if (instring!=null){
            instring=instring.replaceAll("\"", "&quot;");
            instring=instring.replaceAll("<", "&lt;");
            return instring;
        }
        return "";
    }

    public static String cleanForSQL(String instring){
        if (instring!=null && !instring.equals("")) {
            instring=instring.replaceAll("'", "''");
            instring=instring.replaceAll("\\\\", "\\\\\\\\");
            return instring;
        } else {
            return "";
        }
    }


    public static String cleanForjavascript(String instring){
        if (instring!=null){
            instring=instring.replaceAll("\"", "&quot;");
            instring=instring.replaceAll("'", "\\\\'");
            //instring=instring.replaceAll("<", "&lt;");
            return instring;
        }
        return "";
    }

    public static String cleanForjavascriptAndReplaceDoubleQuoteWithSingle(String instring){
        if (instring!=null){
            instring=instring.replaceAll("\"", "\\\\'");
            instring=instring.replaceAll("'", "\\'");
            return instring;
        }
        return "";
    }



    public static String truncateString(String instring, int maxlength){
        String outstring="";
        try{
            outstring = instring.substring(0,maxlength-1);
        } catch (Exception e) {
            outstring = instring;
        }
        return outstring;
    }



    public static String prefillZeroes(String inString, int finalLength) {
        String outString = "";
        if (inString.length()<finalLength) {
            int zeroesToAdd = finalLength - inString.length();
            for(int i=1; i<=zeroesToAdd; i++){
                outString=outString + "0";
            }
        }
        return outString + inString;
    }

    /**
         * Cleans a string for proper xml presentation
         */
        public static String xmlclean(String instring){
            String xmlclean="";
            if (instring!=null) {
                if (!instring.equals("")) {

                    instring=instring.replaceAll("<", "&lt;");
                    instring=instring.replaceAll(">", "&gt;");
                    instring=instring.replaceAll("&nbsp;", " ");
                    instring=instring.replaceAll("&", "&amp;");
                    instring=instring.replaceAll("’", "'");



                    xmlclean=instring;
                } else {
                    xmlclean=" ";
                }
            } else {
                xmlclean=" ";
            }
            return xmlclean;
        }

        /**
         * Cleans a string to make it work as an xml <fieldname></fieldname>.
         * Removes spaces and any non-alphanumeric chars
         */
        public static String xmlFieldNameClean(String in) {
            String out="";

            Pattern p = Pattern.compile("\\W");
            Matcher m = p.matcher(in);
            out = m.replaceAll("");

            return out;
        }


     public static String cleanForAppendreplacement(String in){
        String out = "";
        if(in!=null){
            out=in.replaceAll("\\\\","\\\\\\\\").replaceAll("\\$", "\\\\\\$");
        }

        return out;
    }

    public static String formatForMoney(double in){
        Logger logger = Logger.getLogger(Str.class);
        try{
            NumberFormat formatter = DecimalFormat.getInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            return formatter.format(in);
        } catch (Exception ex){
            logger.error("",ex);
            return "";
        }
    }

    public static String formatForFinancialTransactionsNoCommas(double in){
        Logger logger = Logger.getLogger(Str.class);
        try{
            NumberFormat formatter = DecimalFormat.getInstance();
            formatter.setGroupingUsed(false);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            return formatter.format(in);
        } catch (Exception ex){
            logger.error("",ex);
            return "";
        }
    }

    public static String formatNoDecimals(double in){
        Logger logger = Logger.getLogger(Str.class);
        try{
            NumberFormat formatter = DecimalFormat.getInstance();
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            return formatter.format(in);
        } catch (Exception ex){
            logger.error("",ex);
            return "";
        }
    }

    public static String capitalizeFirstLetter(String in){
        if (in!=null && in.length()>=1){
            String firstletter = in.substring(0, 1);
            String firstletterCap = firstletter.toUpperCase();
            String inCap = firstletterCap + in.substring(1, in.length());
            return inCap;
        }
        return "";
    }

    public static CharSequence getCharSequence(String str){
        return str.subSequence(0, str.length());
    }

}
