package com.pingfit.dao.hibernate;

import org.quartz.JobExecutionContext;
import org.quartz.JobListener;
import org.quartz.JobExecutionException;
import org.apache.log4j.Logger;

import java.util.Date;

import com.pingfit.util.Num;
import com.pingfit.systemprops.InstanceProperties;

/**
 * User: Joe Reger Jr
 * Date: Jun 23, 2007
 * Time: 10:19:02 AM
 */
public class HibernateSessionQuartzCloser implements JobListener {

    public String getName() {
        return this.getClass().getName();
    }

    public void jobToBeExecuted(JobExecutionContext context){
        Logger logger = Logger.getLogger(this.getClass().getName());
        if (InstanceProperties.getRunScheduledTasksOnThisInstance()){
            logger.debug("jobToBeExecuted(): "+context.getJobDetail().getFullName());
        }
        synchronized(this){
            int rndDelay = Num.randomInt(30000);
            if (InstanceProperties.getRunScheduledTasksOnThisInstance()){
                logger.debug("Start waiting "+rndDelay+" millis: "+new Date().getTime()+" for: "+context.getJobDetail().getFullName());
            }
            try{wait(rndDelay);}catch(Exception ex){logger.error("",ex);}
            if (InstanceProperties.getRunScheduledTasksOnThisInstance()){
                logger.debug("End waiting "+rndDelay+" millis: "+new Date().getTime()+" for: "+context.getJobDetail().getFullName());
            }
        }
    }

    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
        
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobExecutionException) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        if (InstanceProperties.getRunScheduledTasksOnThisInstance()){
            logger.debug("jobWasExecuted(): "+context.getJobDetail().getFullName());
        }
        try{
            HibernateUtil.closeSession();
        } catch (Exception ex){
            logger.debug("Error closing hibernate session at end of quartz session");
            logger.error("",ex);
        }
    }


}
