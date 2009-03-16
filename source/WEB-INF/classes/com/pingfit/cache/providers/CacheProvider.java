package com.pingfit.cache.providers;

/**
 * This interface provides a common way for me to access caches so
 * that I can switch/test/use various implementations more easily.
 */
public interface CacheProvider {
    public String getProviderName();
    public Object get(String key, String group);
    public void put(String key, String group, Object obj);
    public void flush();
    public void flush(String group);
    public void flush(String key, String group);
    public String[] getKeys();
    public String[] getKeys(String group);
    public String getCacheStatsAsHtml();
}
