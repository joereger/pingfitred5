package com.pingfit.cache.providers.oscache;

/**
 * Holds a single group and key relationship
 */
public class KeyGroupRelationship {
    String key;
    String group;

    public KeyGroupRelationship(String key, String group){
        this.key = key;
        this.group = group;
    }
}
