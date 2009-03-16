package com.pingfit.dao.hibernate;

import com.pingfit.util.Str;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.stat.SecondLevelCacheStatistics;

/**
 * Dumps data on the hibernate cache
 */
public class HibernateCacheStats {

    public static String getCacheDump(){
        StringBuffer mb = new StringBuffer();
        String[] regionNames = HibernateUtil.getSession().getSessionFactory().getStatistics().getSecondLevelCacheRegionNames();
        for (int i = 0; i < regionNames.length; i++) {
            String regionName = regionNames[i];
            mb.append(getEntriesForARegion(regionName, false));
        }
        return mb.toString();
    }

    public static String getGeneralStats(){
        StringBuffer out = new StringBuffer();
        String slowestquery = HibernateUtil.getSession().getSessionFactory().getStatistics().getQueryExecutionMaxTimeQueryString();
        long slowestqueryExecutiontime = HibernateUtil.getSession().getSessionFactory().getStatistics().getQueryExecutionMaxTime();
        out.append("<br><br>");
        out.append("<font face=arial size=-2>Slowest Query ("+slowestqueryExecutiontime+"ms): "+slowestquery+"</font>");
        return out.toString();
    }

    public static String getEntriesForARegion(String regionname, boolean expandregion){
        Logger logger = Logger.getLogger(HibernateCacheStats.class);
        logger.debug("regionname="+regionname);
        StringBuffer mb = new StringBuffer();
        try{
            //long sizeInMemory = HibernateUtil.getSession().getSessionFactory().getStatistics().getSecondLevelCacheStatistics(regionname).getSizeInMemory();
            long elementCountInMemory = -1;
            Map cacheEntries = new HashMap();
            try{
                SecondLevelCacheStatistics slcs = HibernateUtil.getSession().getSessionFactory().getStatistics().getSecondLevelCacheStatistics(regionname);              
                elementCountInMemory = HibernateUtil.getSession().getSessionFactory().getStatistics().getSecondLevelCacheStatistics(regionname).getElementCountInMemory();
                cacheEntries = HibernateUtil.getSession().getSessionFactory().getStatistics().getSecondLevelCacheStatistics(regionname).getEntries();
            } catch (Exception ex){
                logger.error("", ex);
            }


            mb.append("<br><br>");
            mb.append("<font face=arial size=+1>");
            mb.append(regionname);
            mb.append(" ( "+elementCountInMemory+" items) ");
            mb.append("(<a href=\"/sysadmin/hibernatecache.jsp?action=showhibernate&region="+regionname+"\">Expand</a>)");
            mb.append("</font>");
            if (expandregion){
                if (!regionname.equals("com.pingfit.dao.Error")){
                    mb.append("<br>");
                    mb.append("<font face=arial size=-2>");
                    Iterator keyValuePairs = cacheEntries.entrySet().iterator();
                    for (int i = 0; i < cacheEntries.size(); i++){
                        Map.Entry mapentry = (Map.Entry) keyValuePairs.next();
                        Object key = mapentry.getKey();
                        //Object value = mapentry.getValue();
                        //mb.append("<b>Key:"+key.toString()+"</b>");
                        mb.append("'"+key.toString()+"' ");
                        //mb.append(" Value:"+ripToString(value));
                    }
                    mb.append("</font>");
                } else {
                    mb.append("<br>");
                    mb.append("<font face=arial size=-2>");
                    mb.append("Individual nodes not listed for this region.");
                    mb.append("</font>");
                }
            }
        } catch (Exception ex){
            logger.error("", ex);
            mb.append("<br><br>");
            mb.append("<font face=arial size=+1>");
            mb.append(regionname);
            mb.append("</font>");
            mb.append("<br>");
            mb.append("<font face=arial size=-2>");
            mb.append("Error in region: "+ex.getMessage());
            mb.append("</font>");
        }
        return mb.toString();
    }

    private static String ripToString(Object obj){
        Logger logger = Logger.getLogger(HibernateCacheStats.class);
        logger.debug("about to ripToString");
        StringBuffer out = new StringBuffer();



        String name = obj.getClass().getName().substring(obj.getClass().getName().lastIndexOf(".")+1, obj.getClass().getName().length());
        logger.debug("name="+name);
        try{
            String id = BeanUtils.getSimpleProperty(obj, name.toLowerCase()+"id");
            out.append(id);
        } catch (Exception ex){
            logger.error("", ex);
            out.append(Str.truncateString(obj.toString().replaceAll("<", "&lt;"), 5000));
        }
        
        return out.toString();
    }



}
