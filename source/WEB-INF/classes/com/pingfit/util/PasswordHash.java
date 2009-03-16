package com.pingfit.util;

import java.security.MessageDigest;
import sun.misc.BASE64Encoder;
import org.apache.log4j.Logger;

/**
 * Gives passwords as a hash
 */
public class PasswordHash {



    public static String getHash(String plaintextPassword){
        Logger logger = Logger.getLogger("com.pingfit.util.PasswordHash");

        MessageDigest md = null;

        try{
          md = MessageDigest.getInstance("SHA"); //step 1
        }catch(Exception e){
            logger.error("", e);
        }

        try{
            if (plaintextPassword!=null && md!=null){
                md.update(plaintextPassword.getBytes("UTF-8")); //step 2
            }
        }catch(Exception e){
            logger.error("", e);
        }

        byte raw[] = md.digest(); //step 3
        String hash = (new BASE64Encoder()).encode(raw); //step 4
        return hash; //step 5
    }

}
