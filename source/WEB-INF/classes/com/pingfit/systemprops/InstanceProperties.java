package com.pingfit.systemprops;

import com.pingfit.util.DesEncrypter;
import com.pingfit.systemprops.WebAppRootDir;
import com.pingfit.util.Num;
import com.pingfit.util.GeneralException;
import com.pingfit.db.Db;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Holds database configuration parameters
 */
public class InstanceProperties {

    private static String dbConnectionUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static String dbMaxActive;
    private static String dbMaxIdle;
    private static String dbMinIdle;
    private static String dbMaxWait;
    private static String dbDriverName;
    private static String runScheduledTasksOnThisInstance;
    private static String instancename;
    private static String absolutepathtoexerciseimages;




    private static String passPhrase = "pupper";
    private static boolean haveValidConfig = false;
    private static boolean haveNewConfigToTest = false;
    private static boolean haveAttemptedToLoadDefaultPropsFile = false;
    private static String dbPropsInternalFilename = WebAppRootDir.getWebAppRootPath() + "conf"+File.separatorChar+"instance.props";
    private static String dbPropsExternalFilename = "pingFitRed5-"+WebAppRootDir.getUniqueContextId()+"-dbconfig.txt";

    public static void load(){
        Logger logger = Logger.getLogger(InstanceProperties.class);
        if (!haveValidConfig && !haveAttemptedToLoadDefaultPropsFile){
            try {
                boolean gotFile = false;
                //Look in the webapps directory
                try{
                    File internalFile = new File(dbPropsInternalFilename);
                    if (internalFile!=null && internalFile.exists() && internalFile.canRead() && internalFile.isFile()){
                        Properties properties = new Properties();
                        properties.load(new FileInputStream(internalFile));
                        loadPropsFile(properties);
                        gotFile = true;
                        logger.debug("Got instance props from internal file ("+dbPropsInternalFilename+")");
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                //If we don't have one in the conf directory, look to the system default dir
                if (!gotFile){
                    try{
                        File externalFile = new File("", dbPropsExternalFilename);
                        if (externalFile!=null && externalFile.exists() && externalFile.canRead() && externalFile.isFile()){
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(externalFile));
                            loadPropsFile(properties);
                            gotFile = true;
                            logger.debug("Got instance props from external file (dbPropsExternalFilename)");
                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadPropsFile(Properties properties){
        try{
            dbConnectionUrl = properties.getProperty("dbConnectionUrl", "jdbc:mysql://localhost:3306/pingfitred5?autoReconnect=true");
            dbUsername = properties.getProperty("dbUsername", "username");
            //DesEncrypter encrypter2 = new DesEncrypter(passPhrase);
            //dbPassword = encrypter2.decrypt(properties.getProperty("dbPassword", "password"));
            dbPassword = properties.getProperty("dbPassword", "password");
            dbMaxActive = properties.getProperty("dbMaxActive", "100");
            dbMaxIdle = properties.getProperty("dbMaxIdle", "15");
            dbMinIdle = properties.getProperty("dbMinIdle", "10");
            dbMaxWait = properties.getProperty("dbMaxWait", "10000");
            dbDriverName = properties.getProperty("dbDriverName", "com.mysql.jdbc.Driver");
            runScheduledTasksOnThisInstance = properties.getProperty("runScheduledTasksOnThisInstance", "0");
            instancename = properties.getProperty("instancename", "InstanceNotNamed");
            absolutepathtoexerciseimages = properties.getProperty("absolutepathtoexerciseimages", "");

            haveAttemptedToLoadDefaultPropsFile = true;
            haveNewConfigToTest = true;
        } catch (Exception e){
            e.printStackTrace();
        }

        testConfig();
    }

    public static void save() throws GeneralException {
        Logger logger = Logger.getLogger(InstanceProperties.class);
        logger.debug("save() called");
        logger.debug("haveNewConfigToTest="+haveNewConfigToTest);
        if (!haveNewConfigToTest){
            Properties properties = new Properties();
            //Make sure we test the next time around
            haveNewConfigToTest = true;

            if (dbConnectionUrl!=null){
                properties.setProperty("dbConnectionUrl", dbConnectionUrl);
            }
            if (dbUsername!=null){
                properties.setProperty("dbUsername", dbUsername);
            }
            if (dbPassword!=null){
                //DesEncrypter encrypter2 = new DesEncrypter(passPhrase);
                //String encDbPassword = encrypter2.encrypt(dbPassword);
                //properties.setProperty("dbPassword", encDbPassword);
                properties.setProperty("dbPassword", dbPassword);
            }
            if (dbMaxActive!=null){
                properties.setProperty("dbMaxActive", dbMaxActive);
            }
            if (dbMaxIdle!=null){
                properties.setProperty("dbMaxIdle", dbMaxIdle);
            }
            if (dbMinIdle!=null){
                properties.setProperty("dbMinIdle", dbMinIdle);
            }
            if (dbMaxWait!=null){
                properties.setProperty("dbMaxWait", dbMaxWait);
            }
            if (dbDriverName!=null){
                properties.setProperty("dbDriverName", dbDriverName);
            }
            if (runScheduledTasksOnThisInstance!=null){
                properties.setProperty("runScheduledTasksOnThisInstance", runScheduledTasksOnThisInstance);
            }
            if (instancename!=null){
                properties.setProperty("instancename", instancename);
            }
            if (absolutepathtoexerciseimages!=null){
                properties.setProperty("absolutepathtoexerciseimages", absolutepathtoexerciseimages);
            }

            if (testConfig()){
                logger.debug("passed testConfig()");
                boolean savedsuccessfully = false;
                String saveiomsg = "";
                try{
                    //Save to the file system in the conf directory
                    File fil = new File(dbPropsInternalFilename);
                    if (!fil.exists()){
                        fil.createNewFile();   
                    }
                    FileOutputStream fos = new FileOutputStream(fil);
                    properties.store(fos, "InstanceProperties");
                    fos.close();
                    fil = null;
                    fos = null;
                    savedsuccessfully = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    saveiomsg = saveiomsg + e.getMessage();
                }

                try{
                    //Store to default system location
                    File fil2 = new File("", dbPropsExternalFilename);
                    if (!fil2.exists()){
                        fil2.createNewFile();   
                    }
                    FileOutputStream fos2 = new FileOutputStream(fil2);
                    properties.store(fos2, "InstanceProperties for " + WebAppRootDir.getUniqueContextId());
                    fos2.close();
                    fil2 = null;
                    fos2 = null;
                    savedsuccessfully = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    saveiomsg = saveiomsg + e.getMessage();
                }

                //If neither save worked, alert
                if (!savedsuccessfully){
                    GeneralException gex = new GeneralException("File save failed: "+saveiomsg);
                    throw gex;
                }
            } else {
                haveAttemptedToLoadDefaultPropsFile = false;
                load();
                //Failed validation
                GeneralException gex = new GeneralException("The new properties failed validation.");
                throw gex;
            }
        }
    }

    public static boolean testConfig(){
        if (!haveValidConfig && haveNewConfigToTest){
            //-----------------------------------
            //-----------------------------------
            String[][] rstTest= Db.RunSQL("SELECT 1");
            //-----------------------------------
            //-----------------------------------
            if (rstTest!=null && rstTest.length>0){
                haveValidConfig = true;
            }
        }
        haveNewConfigToTest = false;
        return haveValidConfig;
    }

    public static String getDbConnectionUrl() {
        load();
        return dbConnectionUrl;
    }

    public static void setDbConnectionUrl(String dbConnectionUrl) {
        InstanceProperties.dbConnectionUrl = dbConnectionUrl;
    }

    public static String getDbUsername() {
        load();
        return dbUsername;
    }

    public static void setDbUsername(String dbUsername) {
        InstanceProperties.dbUsername = dbUsername;
    }

    public static String getDbPassword() {
        load();
        return dbPassword;
    }

    public static void setDbPassword(String dbPassword) {
        InstanceProperties.dbPassword = dbPassword;
    }

    public static int getDbMaxActive() {
        load();
        if (Num.isinteger(dbMaxActive)){
            return Integer.parseInt(dbMaxActive);
        } else {
            return 100;
        }
    }

    public static void setDbMaxActive(String dbMaxActive) {
        InstanceProperties.dbMaxActive = dbMaxActive;
    }

    public static int getDbMaxIdle() {
        load();
        if (Num.isinteger(dbMaxIdle)){
            return Integer.parseInt(dbMaxIdle);
        } else {
            return 15;
        }
    }

    public static void setDbMaxIdle(String dbMaxIdle) {
        InstanceProperties.dbMaxIdle = dbMaxIdle;
    }

    public static int getDbMinIdle() {
        load();
        if (Num.isinteger(dbMinIdle)){
            return Integer.parseInt(dbMinIdle);
        } else {
            return 10;
        }
    }

    public static void setDbMinIdle(String dbMinIdle) {
        InstanceProperties.dbMinIdle = dbMinIdle;
    }

    public static int getDbMaxWait() {
        load();
        if (Num.isinteger(dbMaxWait)){
            return Integer.parseInt(dbMaxWait);
        } else {
            return 10000;
        }
    }

    public static void setDbMaxWait(String dbMaxWait) {
        InstanceProperties.dbMaxWait = dbMaxWait;
    }

    public static boolean haveValidConfig() {
        load();
        return haveValidConfig;
    }

    public static void setHaveValidConfig(boolean haveValidConfig) {
        InstanceProperties.haveValidConfig = haveValidConfig;
    }

    public static String getDbPropsInternalFilename() {
        load();
        return dbPropsInternalFilename;
    }

    public static void setDbPropsInternalFilename(String dbPropsInternalFilename) {
        InstanceProperties.dbPropsInternalFilename = dbPropsInternalFilename;
    }

    public static String getDbDriverName(){
        load();
        return dbDriverName;
    }

    public static void setDbDriverName(String dbDriverName) {
        InstanceProperties.dbDriverName = dbDriverName;
    }

    public static boolean haveNewConfigToTest() {
        return haveNewConfigToTest;
    }

    public static void setHaveNewConfigToTest(boolean haveNewConfigToTest) {
        InstanceProperties.haveNewConfigToTest = haveNewConfigToTest;
    }

    public static boolean getRunScheduledTasksOnThisInstance(){
        load();
        if (runScheduledTasksOnThisInstance!=null && runScheduledTasksOnThisInstance.equals("1")){
            return true;
        }
        return false;
    }

    public static void setRunScheduledTasksOnThisInstance(boolean runScheduledTasksOnThisInstance){
        if (runScheduledTasksOnThisInstance){
            InstanceProperties.runScheduledTasksOnThisInstance = "1";
        } else {
            InstanceProperties.runScheduledTasksOnThisInstance = "0";
        }
    }


    public static String getInstancename() {
        load();
        return instancename;
    }

    public static void setInstancename(String instancename) {
        InstanceProperties.instancename = instancename;
    }


    public static String getAbsolutepathtoexerciseimages() {
        load();
        return absolutepathtoexerciseimages;
    }

    public static void setAbsolutepathtoexerciseimages(String absolutepathtoexerciseimages) {
        InstanceProperties.absolutepathtoexerciseimages = absolutepathtoexerciseimages;
    }
}
