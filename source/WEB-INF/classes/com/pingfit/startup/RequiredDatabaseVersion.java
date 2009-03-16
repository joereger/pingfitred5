package com.pingfit.startup;

/**
 * User: Joe Reger Jr
 * Date: Nov 26, 2006
 * Time: 11:50:19 AM
 */
public class RequiredDatabaseVersion {

    //Set version to -1 to always use the max available
    //private static int version = -1;
    private static boolean havecorrectversion = false;
    private static int currentversion = -1;
    private static String error = "";
    private static int maxversion = -1;


//    public static int getVersion() {
//        return version;
//    }
//
//    public static void setVersion(int version) {
//        RequiredDatabaseVersion.version = version;
//    }

    public static boolean getHavecorrectversion() {
        return havecorrectversion;
    }

    public static void setHavecorrectversion(boolean havecorrectversion) {
        RequiredDatabaseVersion.havecorrectversion = havecorrectversion;
    }

    public static int getCurrentversion() {
        return currentversion;
    }

    public static void setCurrentversion(int currentversion) {
        RequiredDatabaseVersion.currentversion = currentversion;
    }

    public static String getError() {
        return error;
    }

    public static void setError(String error) {
        RequiredDatabaseVersion.error = error;
    }

    public static int getMaxversion() {
        return maxversion;
    }

    public static void setMaxversion(int maxversion) {
        RequiredDatabaseVersion.maxversion = maxversion;
    }
}
