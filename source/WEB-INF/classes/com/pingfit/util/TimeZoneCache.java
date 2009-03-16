package com.pingfit.util;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.base.NeedsRefreshException;

import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * Caches Account objects using OSCache
 */
public class TimeZoneCache {

    public static GeneralCacheAdministrator admin;


    public static TimeZone get(String id){
        Logger logger = Logger.getLogger("com.pingfit.util.TimeZoneCache");
        if (admin==null){
            admin = new GeneralCacheAdministrator();
        }

        try {
            return (TimeZone) admin.getFromCache(id);
        } catch (NeedsRefreshException nre) {
            try {
                TimeZone tz = TimeZone.getTimeZone(id);
                admin.putInCache(id, tz);
                logger.debug("In id: "+id+" - REFRESHING - Returning tz.getID(): " + tz.getID());
                return tz;
            } catch (Exception ex) {
                admin.cancelUpdate(id);
                logger.error("", ex);
                logger.debug("In id: "+id+" - REFRESHING - Returning the default GMT.");
                return TimeZone.getTimeZone("GMT");
            }
        }
    }




}
