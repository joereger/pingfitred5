package com.pingfit.startup;

/**
 * User: Joe Reger Jr
 * Date: Nov 26, 2006
 * Time: 11:38:40 AM
 */
public interface UpgradeDatabaseOneVersion {

    void doPreHibernateUpgrade();
    void doPostHibernateUpgrade();

}
