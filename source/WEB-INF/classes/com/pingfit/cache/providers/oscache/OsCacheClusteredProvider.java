package com.pingfit.cache.providers.oscache;

import com.pingfit.cache.providers.CacheProvider;
import com.pingfit.systemprops.WebAppRootDir;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.base.NeedsRefreshException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

/**
 * OsCache cache implementation
 */
public class OsCacheClusteredProvider implements CacheProvider {

    private static GeneralCacheAdministrator admin;
    private static ArrayList<KeyGroupRelationship> groupKeyRelationships = new ArrayList<KeyGroupRelationship>();

    Logger logger = Logger.getLogger(this.getClass().getName());

    public OsCacheClusteredProvider(){

    }

    private static void setupCache(){
        Logger logger = Logger.getLogger("com.pingfit.cache.providers.oscache.OsCacheClusteredProvider");
        //Initialize the groupKeyRelationships
        groupKeyRelationships = new ArrayList<KeyGroupRelationship>();
        //Load props file
        File internalFile = new File(WebAppRootDir.getWebAppRootPath() + "/WEB-INF/classes/oscacheclustered.properties");
        if (internalFile!=null && internalFile.exists() && internalFile.canRead() && internalFile.isFile()){
            Properties properties = new Properties();
            try{
                properties.load(new FileInputStream(internalFile));
                admin = new GeneralCacheAdministrator(properties);
            } catch (Exception e){
                logger.error("Error setting up cache", e);
                admin = new GeneralCacheAdministrator();
            }
        } else {
            logger.debug("Couldn't find oscache proerties file: "+internalFile.getAbsolutePath());
            admin = new GeneralCacheAdministrator();
        }
        //Manually set some properties
        //admin.setCacheCapacity(5999);
    }

    public String getProviderName(){
        return "OsCacheClusteredProvider";
    }

    public Object get(String key, String group) {
        if (OsCacheClusteredProvider.admin ==null){
            setupCache();
        }
        try {
            return OsCacheClusteredProvider.admin.getFromCache("/"+group+"/"+key);
        } catch (NeedsRefreshException nre) {
            admin.cancelUpdate("/"+group+"/"+key);
            return null;
        } catch (Exception e){
            logger.error("Error getting from cache", e);
        }
        return null;
    }

    public void put(String key, String group, Object obj) {
        if (OsCacheClusteredProvider.admin ==null){
            setupCache();
        }
        try{
            OsCacheClusteredProvider.groupKeyRelationships.add(new KeyGroupRelationship("/"+group+"/"+key, group));
            OsCacheClusteredProvider.admin.putInCache("/"+group+"/"+key, obj, new String[]{group});
        } catch (Exception ex){
            admin.cancelUpdate("/"+group+"/"+key);
            logger.error("Error putting to cache", ex);
        }
    }

    public void flush() {
        if (OsCacheClusteredProvider.admin !=null){
            try{
                OsCacheClusteredProvider.admin.flushAll();
            } catch (Exception e){
                logger.error("Error flushing", e);
            }
        }
    }

    public void flush(String group) {
        if (OsCacheClusteredProvider.admin !=null){
            try{
                OsCacheClusteredProvider.groupKeyRelationships = new ArrayList<KeyGroupRelationship>();
                OsCacheClusteredProvider.admin.flushGroup(group);
            } catch (Exception e){
                logger.error("Error flushing", e);
            }
        }
    }

    public void flush(String key, String group) {
        if (OsCacheClusteredProvider.admin !=null){
            try{
                removeKeyFromKeyGroupMap("/"+group+"/"+key);
                OsCacheClusteredProvider.admin.flushEntry("/"+group+"/"+key);
            } catch (Exception e){
                logger.error("Error flushing", e);
            }
        }
    }

    public String[] getKeys() {
        if (OsCacheClusteredProvider.admin !=null){
            ArrayList<String> outList = new ArrayList<String>();
            for (Iterator it = OsCacheClusteredProvider.groupKeyRelationships.iterator(); it.hasNext(); ) {
                KeyGroupRelationship kgr = (KeyGroupRelationship)it.next();
                outList.add(kgr.key);
            }
            String[] out = new String[outList.size()];
            int i = 0;
            for (Iterator it = outList.iterator(); it.hasNext(); ) {
                String key = (String)it.next();
                out[i]=key;
                i=i+1;
            }
            return out;
        }
        return new String[0];
    }

    public String[] getKeys(String group) {
        if (OsCacheClusteredProvider.admin !=null){
            ArrayList<String> outList = new ArrayList<String>();
            for (Iterator it = OsCacheClusteredProvider.groupKeyRelationships.iterator(); it.hasNext(); ) {
                KeyGroupRelationship kgr = (KeyGroupRelationship)it.next();
                if (kgr.group.equals(group)){
                    outList.add(kgr.key);
                }
            }
            String[] out = new String[outList.size()];
            int i = 0;
            for (Iterator it = outList.iterator(); it.hasNext(); ) {
                String key = (String)it.next();
                out[i]=key;
                i=i+1;
            }
            return out;
        }
        return new String[0];
    }

    public String getCacheStatsAsHtml() {
        StringBuffer mb = new StringBuffer();
        mb.append("OsCacheClusteredProvider<br>");
        if (admin!=null){
            mb.append("cache.capacity: "+admin.getProperty("cache.capacity")+"<br>");
            mb.append("cache.cluster.properties: "+admin.getProperty("cache.cluster.properties")+"<br>");
            String[] keys = getKeys();
            mb.append(keys.length + " keys in cache<br>");
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                mb.append("<br>"+key);
            }
        } else {
            mb.append("Cache is null.");
        }
        return mb.toString();
    }

    private void removeKeyFromKeyGroupMap(String key){
        ArrayList<String> outList = new ArrayList<String>();
        for (Iterator it = OsCacheClusteredProvider.groupKeyRelationships.iterator(); it.hasNext(); ) {
            KeyGroupRelationship kgr = (KeyGroupRelationship)it.next();
            if (kgr.key.equals(key)){
                it.remove();
            }
        }
    }


}
