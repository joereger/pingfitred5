package com.pingfit.startup;

import org.apache.log4j.Logger;
import com.pingfit.db.Db;
import com.pingfit.util.ErrorDissect;
import com.pingfit.util.Num;
import com.pingfit.util.Time;

import java.util.Calendar;

/**
 * User: Joe Reger Jr
 * Date: Nov 26, 2006
 * Time: 11:37:28 AM
 */
public class DbVersionCheck {

    Logger logger = Logger.getLogger(this.getClass().getName());

    public static int EXECUTE_PREHIBERNATE = 0;
    public static int EXECUTE_POSTHIBERNATE = 1;

    public void doCheck(int execute_pre_or_post){

            //Calculate the max version that exists in the code classes
            int maxVer = 0;
            while(true){
                try{
                    //Try to create an object
                    UpgradeDatabaseOneVersion upg = (UpgradeDatabaseOneVersion)(Class.forName("com.pingfit.startup.dbversion.Version"+maxVer).newInstance());
                } catch (ClassNotFoundException ex){
                    //If class isn't found, break, but be sure to decrement the maxVer
                    //logger.debug("No class found: Class.forName(\"com.pingfit.startup.dbversion.Version"+maxVer+")");
                    maxVer = maxVer - 1;
                    break;
                } catch (Throwable e){
                    //Also decrement the maxVer if anything else happens
                    maxVer = maxVer - 1;
                    logger.error("", e);
                    break;
                }
                maxVer = maxVer + 1;
            }
            RequiredDatabaseVersion.setMaxversion(maxVer);

            //Make sure we have the database table to support the database version status
            if (!checkThatDatabaseVersionTableExists()){
                createDbVersionTable();
            }

            //Get the highest version stored in the database version table
            int currentDatabaseVersion = getMaxVersionFromDatabase();

            //Boolean to keep working
            boolean keepWorking = true;


            //Compare to the current required database version
            while (currentDatabaseVersion<=RequiredDatabaseVersion.getMaxversion() && keepWorking) {
                try{
                    //Do the upgrade
                    UpgradeDatabaseOneVersion upg = (UpgradeDatabaseOneVersion)(Class.forName("com.pingfit.startup.dbversion.Version"+ (currentDatabaseVersion+1)).newInstance());
                    System.out.println("Start upgrade database to version " + (currentDatabaseVersion+1));
                    if (execute_pre_or_post==EXECUTE_PREHIBERNATE){
                        upg.doPreHibernateUpgrade();
                        //updateDatabase((currentDatabaseVersion+1));
                    } else {
                        upg.doPostHibernateUpgrade();
                        //Note that I only update the database version in the post-hibernate run
                        updateDatabase((currentDatabaseVersion+1));
                    }
                    System.out.println("End upgrade database to version " + (currentDatabaseVersion+1));
                } catch (ClassNotFoundException ex){
                    //Class isn't found, report it and exit
                    //logger.error("",ex);
                    //ex.printStackTrace();
                    keepWorking = false;
                    //RequiredDatabaseVersion.setError(RequiredDatabaseVersion.getError() + ErrorDissect.dissect(ex));
                    //logger.debug("Error: Upgrade database to version " + (currentDatabaseVersion+1) + ".  Class not found.");
                } catch (Exception e){
                    //Some other sort of error
                    System.out.println("Reger.com UpgradeCheckAtStartup.java: Error upgrading Db:" + e.getMessage());
                    e.printStackTrace();
                    logger.error("", e);
                    keepWorking = false;
                    RequiredDatabaseVersion.setError(RequiredDatabaseVersion.getError() + ErrorDissect.dissect(e));
                    logger.debug("Error: Upgrade database to version " + (currentDatabaseVersion+1) + " had issues recorded.");
                }
                //Increment and see if we can upgrade to this version
                currentDatabaseVersion = currentDatabaseVersion + 1;
            }

            if (execute_pre_or_post==EXECUTE_POSTHIBERNATE){
                if ((currentDatabaseVersion-1)==RequiredDatabaseVersion.getMaxversion()){
                    //Successful upgrade through all versions
                    RequiredDatabaseVersion.setHavecorrectversion(true);
                    System.out.println("The correct database version is installed.  Version: " + (currentDatabaseVersion-1));
                }
            }

        }




        private static void updateDatabase(int version){
            try{
                //Update the database version
                //-----------------------------------
                //-----------------------------------
                int identity = Db.RunSQLInsert("INSERT INTO databaseversion(version, date) VALUES('"+version+"', '"+ Time.dateformatfordb(Calendar.getInstance())+"')");
                //-----------------------------------
                //-----------------------------------
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private static int getMaxVersionFromDatabase(){
            try{
                //Go to the database and see what we've got.
                //-----------------------------------
                //-----------------------------------
                String[][] rstVersion= Db.RunSQL("SELECT max(version) FROM databaseversion");
                //-----------------------------------
                //-----------------------------------
                if (rstVersion!=null && rstVersion.length>0){
                    if (Num.isinteger(rstVersion[0][0])){
                        RequiredDatabaseVersion.setCurrentversion(Integer.parseInt(rstVersion[0][0]));
                        return Integer.parseInt(rstVersion[0][0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                RequiredDatabaseVersion.setError(RequiredDatabaseVersion.getError() + ErrorDissect.dissect(e));
            }
            return -1;
        }

        private static boolean checkThatDatabaseVersionTableExists(){
            try{
                //-----------------------------------
                //-----------------------------------
                String[][] rstT = Db.RunSQL("SELECT COUNT(*) FROM databaseversion");
                //-----------------------------------
                //-----------------------------------
                if (rstT!=null && rstT.length>0){
                    return true;
                }
                return false;
            } catch (Exception e){
                return false;
            }
        }

        private static void createDbVersionTable(){
            try{
                //-----------------------------------
                //-----------------------------------
                int count = Db.RunSQLUpdate("CREATE TABLE `databaseversion` (`databaseversionid` int(11) NOT NULL auto_increment, `version` int(11) NOT NULL default '0', `date` datetime default null, PRIMARY KEY  (`databaseversionid`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
                //-----------------------------------
                //-----------------------------------
            } catch (Exception e){
                e.printStackTrace();
            }
        }



}
