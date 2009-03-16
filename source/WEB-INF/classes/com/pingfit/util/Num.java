package com.pingfit.util;

import java.util.Random;
import java.util.Date;

/**
 * User: Joe Reger Jr
 * Date: Apr 17, 2006
 * Time: 10:55:41 AM
 */
public class Num {

    public static boolean isinteger(String str){
        if (str==null){
            return false;
        }
        try{
            Integer.parseInt(str);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean isdouble(String str){
        if (str==null){
            return false;
        }
        try{
            Double.parseDouble(str);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static long absoluteValue(long num){
        if (num < 0) {
           return -1*num;
       } else {
           return num;
       }
   }

   public static int randomInt(int max){
        //return (int)(Math.random()*(max+1));
       Random generator = new Random( (new Date()).getTime() );
       int rnd = generator.nextInt(max) + 1;
       return rnd;
    }


}
