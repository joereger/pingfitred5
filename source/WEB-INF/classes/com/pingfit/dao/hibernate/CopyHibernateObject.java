package com.pingfit.dao.hibernate;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.hibernate.EntityMode;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: Joe Reger Jr
 * Date: Jun 7, 2007
 * Time: 8:38:40 AM
 */
public class CopyHibernateObject {


    public static Object shallowCopyIgnoreCertainFields(Object obj, Object objNew, ArrayList fieldstoignore){
        Logger logger = Logger.getLogger(CopyHibernateObject.class);
        logger.debug("begin copy");

        ClassMetadata meta = HibernateUtil.getSessionFactory().getClassMetadata(obj.getClass());

        String[] propertyNames = meta.getPropertyNames();
        Object[] propertyValues = meta.getPropertyValues(obj, EntityMode.POJO);
        Type[] propertyTypes = meta.getPropertyTypes();

        //All properties that are not collections or associations
        Map namedValues = new HashMap();
        for ( int i=0; i<propertyNames.length; i++ ) {
            if (!propertyTypes[i].isEntityType() && !propertyTypes[i].isCollectionType()) {
                namedValues.put( propertyNames[i], propertyValues[i] );
                logger.debug("propertyNames["+i+"]="+propertyNames[i]+" added");
            } else {
                logger.debug("propertyNames["+i+"]="+propertyNames[i]+" not added");
            }
        }

        //Add props to new object
        logger.debug("begin moving values to objNew");
        ClassMetadata metaNew = HibernateUtil.getSessionFactory().getClassMetadata(objNew.getClass());
        Iterator keyValuePairs = namedValues.entrySet().iterator();
        for (int i = 0; i < namedValues.size(); i++){
            Map.Entry mapentry = (Map.Entry) keyValuePairs.next();
            String name = (String)mapentry.getKey();
            Object value = mapentry.getValue();
            if (fieldstoignore==null || !fieldstoignore.contains(name)){
                metaNew.setPropertyValue(objNew, name, value, EntityMode.POJO);
                logger.debug("copying field "+name);
            } else {
                logger.debug("ignoring field "+name);
            }
        }
        logger.debug("end moving values to objNew");

        logger.debug("end copy");
        return objNew;
    }





}
