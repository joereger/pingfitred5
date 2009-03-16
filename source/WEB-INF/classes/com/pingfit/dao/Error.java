package com.pingfit.dao;

import com.pingfit.dao.hibernate.BasePersistentClass;
import com.pingfit.dao.hibernate.HibernateUtil;

import java.util.Date;

import org.apache.log4j.Logger;



public class Error extends BasePersistentClass implements java.io.Serializable {

     public static int STATUS_NEW = 1;
     public static int STATUS_OLD = 0;

    // Fields
     private int errorid;
     private String error;
     private int level;
     private Date date;
     private int status;
     private int timesseen;



    public static Error get(Integer id) {
        Logger logger = Logger.getLogger("com.pingfit.dao.Error");
        try {
            logger.debug("Error.get(" + id + ") called.");
            Error obj = (Error) HibernateUtil.getSession().get(Error.class, id);
            if (obj == null) {
                logger.debug("Error.get(" + id + ") returning new instance because hibernate returned null.");
                return new Error();
            }
            return obj;
        } catch (Exception ex) {
            logger.error("com.pingfit.dao.Error", ex);
            return new Error();
        }
    }

    // Constructors

    /** default constructor */
    public Error() {
    }




    // Property accessors


    public int getErrorid() {
        return errorid;
    }

    public void setErrorid(int errorid) {
        this.errorid = errorid;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTimesseen() {
        return timesseen;
    }

    public void setTimesseen(int timesseen) {
        this.timesseen=timesseen;
    }
}
