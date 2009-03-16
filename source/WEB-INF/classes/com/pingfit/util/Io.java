package com.pingfit.util;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * User: Joe Reger Jr
 * Date: Apr 14, 2006
 * Time: 3:20:54 PM
 */
public class Io {


    /**
         * Reads the contents of a text file and puts it into a StringBuffer
         */
         public static StringBuffer textFileRead(String filename){
            StringBuffer sb = new StringBuffer();
            Logger logger = Logger.getLogger(Io.class);
            File file = new File(filename);
            char[] chars = new char[(int) file.length()];
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                for(int i = 0; i < 10000; i++) {
                    reader.read(chars);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                logger.error("", e);
            } catch (IOException e) {
                logger.error("", e);
            }

            sb.append(new String(chars));

            return sb;
        }



        /**
         * Reads the contents of a text file and puts it into a StringBuffer
         */
         public static StringBuffer textFileRead(File file){
            StringBuffer sb = new StringBuffer();
            Logger logger = Logger.getLogger(Io.class);
            //File file = new File(filename);
            char[] chars = new char[(int) file.length()];
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                for(int i = 0; i < 10000; i++) {
                    reader.read(chars);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                logger.error("", e);
            } catch (IOException e) {
                logger.error("", e);
            }

            sb.append(new String(chars));

            return sb;
        }

        public static void writeTextToFile(File file, String text){
            //file = new File("c:/temp/testwritetofile.txt");
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(text);
                out.close();
            } catch (IOException e) {
            }
        }


}
