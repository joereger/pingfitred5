package com.pingfit.db;

import org.apache.log4j.Logger;

import java.sql.*;

import com.pingfit.systemprops.InstanceProperties;

//When it's time to cluster:
//http://c-jdbc.objectweb.org/


public class Db {

  Logger logger = Logger.getLogger(this.getClass().getName());

  /**
   * Get a connection object.  Don't use this.  Only here for the DatabaseToString backup code
   */
  public static Connection getConnection(){
        return com.pingfit.db.proxool.Db.getConnection();
  }
  
  /*
  * Run SQL, return a String Array
  */
  public static String[][] RunSQL(String sql, int recordstoreturn) {
    if (InstanceProperties.haveValidConfig() || InstanceProperties.haveNewConfigToTest()){
        return com.pingfit.db.proxool.Db.RunSQL(sql, recordstoreturn);
    } else {
        return new String[0][];
    }
  }
  
  /**
  * Overload to allow just sql, with no specification of # records to return
  */
  public static String[][] RunSQL(String sql) {
  		return RunSQL(sql, 50000);
  }

  //Run Update SQL, return the number of rows affected
  public static int RunSQLUpdate(String sql){
    if (InstanceProperties.haveValidConfig() || InstanceProperties.haveNewConfigToTest()){
       return com.pingfit.db.proxool.Db.RunSQLUpdate(sql);
    } else {
        return 0;
    }
  }

  public static int RunSQLInsert(String sql){
    return RunSQLInsert(sql, true);    
  }

  //Run Insert SQL, return the unique autonumber of the row inserted
  public static int RunSQLInsert(String sql, boolean isloggingon){
    if (InstanceProperties.haveValidConfig() || InstanceProperties.haveNewConfigToTest()){
       return com.pingfit.db.proxool.Db.RunSQLInsert(sql, isloggingon);
    } else {
        return 0;
    }
  }

  public static String printDriverStats() throws Exception {
        return com.pingfit.db.proxool.Db.printDriverStats();
  }

  public static void recordToSqlDebugCache(String sql){
      Logger logger = Logger.getLogger("com.pingfit.db.Db");
      logger.debug(sql);
  }


             

}