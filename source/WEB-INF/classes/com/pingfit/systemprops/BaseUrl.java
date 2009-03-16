package com.pingfit.systemprops;

/**
 * User: Joe Reger Jr
 * Date: Sep 10, 2006
 * Time: 12:54:56 PM
 */
public class BaseUrl {

    private static String baseUrl;

    public static void refresh(){
        baseUrl = null;
    }

    public static String getNoHttp() {
        if (baseUrl ==null){
            baseUrl = SystemProperty.getProp(SystemProperty.PROP_BASEURL);
        }
        return baseUrl;
    }

    public static String get(boolean makeHttpsIfSSLIsOn){
        if (makeHttpsIfSSLIsOn && SystemProperty.getProp(SystemProperty.PROP_ISSSLON).equals("1")){
            return "https://" + getNoHttp() + "/";
        } else {
            return "http://" + getNoHttp() + "/";
        }
    }

    public String getIncludinghttps(){
        return get(true);
    }

    public void setIncludinghttps(){

    }

    public String getIncludinghttp(){
        return get(false);
    }

    public void setIncludinghttp(){

    }

    public String getBase(){
        return baseUrl;
    }

    public void setBase(){

    }





}
