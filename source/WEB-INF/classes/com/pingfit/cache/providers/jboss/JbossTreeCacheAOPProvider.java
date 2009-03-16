package com.pingfit.cache.providers.jboss;

import com.pingfit.cache.providers.CacheProvider;
import com.pingfit.systemprops.WebAppRootDir;
import com.pingfit.startup.ApplicationStartup;
import org.jboss.cache.aop.TreeCacheAop;
import org.jboss.cache.PropertyConfigurator;
import org.jboss.cache.CacheException;
import org.jboss.cache.TreeCache;
import org.jboss.cache.Fqn;
import org.jboss.cache.config.Option;
import org.apache.log4j.Logger;

import javax.management.ObjectName;
import javax.management.MBeanServer;
import java.util.HashSet;
import java.util.Iterator;
import java.lang.management.ManagementFactory;

/**      
 * Implementation of the jbosscache
 */
public class JbossTreeCacheAOPProvider implements CacheProvider {

    Logger logger = Logger.getLogger(this.getClass().getName());

    private static TreeCache treeCacheAop;

    public JbossTreeCacheAOPProvider(){

    }

    public String getProviderName(){
        return "JbossTreeCacheAOPProvider";
    }

    private static void setupCache(){
        Logger logger = Logger.getLogger("com.pingfit.cache.providers.jboss.JbossTreeCacheAOPProvider");
        try{

            //Create the treecache
            treeCacheAop = new TreeCache();
            PropertyConfigurator config = new PropertyConfigurator();
            config.configure(treeCacheAop, WebAppRootDir.getWebAppRootPath()+"WEB-INF"+java.io.File.separator+"classes"+java.io.File.separator+"treecache-nondao.xml");
            treeCacheAop.startService();

            logger.debug("JBossCache Cache created: "+treeCacheAop.getClusterName());
        } catch (Exception e){
            logger.error("Error setting up cache.", e);
        }
    }

    public static TreeCache getTreeCache(){
        if (treeCacheAop == null){
            synchronized (JbossTreeCacheAOPProvider.class){
                setupCache();
            }
        }
        return treeCacheAop;
    }

    public Object get(String key, String group) {
        Logger logger = Logger.getLogger("com.pingfit.cache.providers.jboss.JbossTreeCacheAOPProvider");
        try{
            Fqn fqn = Fqn.fromString("/"+group);
            return JbossTreeCacheAOPProvider.getTreeCache().get(fqn, key);
        } catch (CacheException ex){
            logger.debug("Object not found in cache. key="+key);
            return null;
        }
    }

    public void put(String key, String group, Object obj) {
        try{
            logger.debug("put("+key+" , "+group+") called");
            Fqn fqn = Fqn.fromString("/"+group);
            JbossTreeCacheAOPProvider.getTreeCache().put(fqn, key, obj);
        }catch (Exception e){
            Logger logger = Logger.getLogger("com.pingfit.cache.providers.jboss.JbossTreeCacheAOPProvider");
            logger.error("Error putting to cache", e);
        }
    }

    public void flush() {
        try{
            Fqn fqn = Fqn.fromString("/");
            JbossTreeCacheAOPProvider.getTreeCache().remove(fqn);
        }catch (Exception e){
            Logger logger = Logger.getLogger("com.pingfit.cache.providers.jboss.JbossTreeCacheAOPProvider");
            logger.error("Error flushing from cache", e);
        }
    }

    public void flush(String group) {
        try{
            logger.debug("flush("+group+") called");
            Fqn fqn = Fqn.fromString("/"+group);
            logger.debug("pre-remove() - JbossTreeCacheAOPProvider.getTreeCache().exists("+group+")="+JbossTreeCacheAOPProvider.getTreeCache().exists(fqn));
            JbossTreeCacheAOPProvider.getTreeCache().remove(fqn);
            logger.debug("post-remove() - JbossTreeCacheAOPProvider.getTreeCache().exists("+group+")="+JbossTreeCacheAOPProvider.getTreeCache().exists(fqn));
        }catch (Exception e){
            Logger logger = Logger.getLogger("com.pingfit.cache.providers.jboss.JbossTreeCacheAOPProvider");
            logger.error("Error flushing from cache", e);
        }
    }

    public void flush(String key, String group) {
        Logger logger = Logger.getLogger("com.pingfit.cache.providers.jboss.JbossTreeCacheAOPProvider");
        try{
            logger.debug("flush("+key+", "+group+") called");
            Fqn fqn = Fqn.fromString("/"+group);
            logger.debug("pre-remove() - JbossTreeCacheAOPProvider.getTreeCache().exists("+group+")="+JbossTreeCacheAOPProvider.getTreeCache().exists(fqn));
            logger.debug("pre-remove() - JbossTreeCacheAOPProvider.getTreeCache().exists("+key+", "+group+")="+JbossTreeCacheAOPProvider.getTreeCache().exists(fqn, key));
            JbossTreeCacheAOPProvider.getTreeCache().remove(fqn, key);
            logger.debug("post-remove() - JbossTreeCacheAOPProvider.getTreeCache().exists("+group+")="+JbossTreeCacheAOPProvider.getTreeCache().exists(fqn));
            logger.debug("post-remove() - JbossTreeCacheAOPProvider.getTreeCache().exists("+key+", "+group+")="+JbossTreeCacheAOPProvider.getTreeCache().exists(fqn, key));
        }catch (Exception e){
            logger.error("Error flushing from cache", e);
        }
    }

    public String[] getKeys(){
        if (JbossTreeCacheAOPProvider.getTreeCache()!=null){
            try{
                HashSet hm = (HashSet)JbossTreeCacheAOPProvider.getTreeCache().getKeys("/");
                String[] out = new String[hm.size()];
                int i = 0;
                for (Iterator iterator = hm.iterator(); iterator.hasNext();) {
                    String s = (String) iterator.next();
                    out[i] = s;
                    i=i+1;
                }
                return out;
            } catch (CacheException ex){
                return new String[0];
            } catch (Exception ex){
                return new String[0];
            }
        }
        return new String[0];
    }

    public String[] getKeys(String group){
        if (JbossTreeCacheAOPProvider.getTreeCache()!=null){
            try{
                HashSet hm = (HashSet)JbossTreeCacheAOPProvider.getTreeCache().getKeys("/"+group);
                String[] out = new String[hm.size()];
                int i = 0;
                for (Iterator iterator = hm.iterator(); iterator.hasNext();) {
                    String s = (String) iterator.next();
                    out[i] = s;
                    i=i+1;
                }
                return out;
            } catch (CacheException ex){
                return new String[0];
            } catch (Exception ex){
                return new String[0];
            }
        }
        return new String[0];
    }

    public String getCacheStatsAsHtml() {
        StringBuffer mb = new StringBuffer();
        mb.append("JbossCacheAOPProvider<br>");
        mb.append(com.pingfit.cache.providers.jboss.CacheDumper.getHtml("/", 10));
        String[] keys = getKeys("");
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            mb.append(key + "<br/>");
        }
        return mb.toString();
    }
}
