package com.pingfit.startup;

import com.pingfit.systemprops.WebAppRootDir;
import com.pingfit.systemprops.InstanceProperties;
import com.pingfit.systemprops.SystemProperty;
import com.pingfit.dao.hibernate.HibernateUtil;
import com.pingfit.dao.hibernate.HibernateSessionQuartzCloser;
import com.pingfit.xmpp.SendXMPPMessage;
import com.pingfit.scheduledjobs.SystemStats;
import javax.servlet.*;
import org.apache.log4j.Logger;
import org.quartz.SchedulerFactory;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 * User: Joe Reger Jr
 * Date: Apr 17, 2006
 * Time: 10:50:54 AM
 */
public class ApplicationStartup implements ServletContextListener {

    private static boolean ishibernateinitialized = false;
    private static boolean iswabapprooddirdiscovered = false;
    private static boolean isdatabasereadyforapprun = false;
    private static boolean isappstarted = false;

    Logger logger = Logger.getLogger(this.getClass().getName());
    private static Scheduler scheduler = null;

    public void contextInitialized(ServletContextEvent cse) {
       System.out.println("PINGFITRED5: Application initialized");
        printBug();
       //Configure some dir stuff
        WebAppRootDir ward = new WebAppRootDir(cse.getServletContext());
        iswabapprooddirdiscovered = true;
        //Connect to database
        if (InstanceProperties.haveValidConfig()){
            //Run pre-hibernate db upgrades
            DbVersionCheck dbvcPre = new DbVersionCheck();
            dbvcPre.doCheck(DbVersionCheck.EXECUTE_PREHIBERNATE);
            //Set up hibernate
            HibernateUtil.getSession();
            ishibernateinitialized = true;
            //Run post-hibernate db upgrades
            DbVersionCheck dbvcPost = new DbVersionCheck();
            dbvcPost.doCheck(DbVersionCheck.EXECUTE_POSTHIBERNATE);
            //Check to make sure we're good to go
            if (RequiredDatabaseVersion.getHavecorrectversion()){
                isdatabasereadyforapprun = true;
                isappstarted = true;
            }
            //Configure Log4j
            //Logger.getRootLogger().setLevel();
        } else {
            logger.info("InstanceProperties.haveValidConfig()=false");
        }
        //Load SystemProps
        SystemProperty.refreshAllProps();
        //Refresh SystemStats
        SystemStats ss = new SystemStats();
        try{ss.execute(null);}catch(Exception ex){logger.error("",ex);}
        //Initialize Quartz
        initQuartz(cse.getServletContext());
        //Add Quartz listener
        try{
            SchedulerFactory schedFact = new StdSchedulerFactory();
            schedFact.getScheduler().addGlobalJobListener(new HibernateSessionQuartzCloser());
        } catch (Exception ex){logger.error("",ex);}
        //Report to log and XMPP
        logger.info("WebAppRootDir = " + WebAppRootDir.getWebAppRootPath());
        logger.info("PINGFITRED5 Application Started!  Let's make some chattiness!");
        SendXMPPMessage xmpp = new SendXMPPMessage(SendXMPPMessage.GROUP_SYSADMINS, "pingFitRed5 Application started! ("+WebAppRootDir.getUniqueContextId()+")");
        xmpp.send();
    }

    public void contextDestroyed(ServletContextEvent cse) {
        //Notify sysadmins
        SendXMPPMessage xmpp = new SendXMPPMessage(SendXMPPMessage.GROUP_SYSADMINS, "pingFitRed5 Application shut down! ("+WebAppRootDir.getUniqueContextId()+")");
        xmpp.send();
        //Shut down Hibernate
        try{
            HibernateUtil.closeSession();
            HibernateUtil.killSessionFactory();
        } catch (Exception ex){logger.error("",ex);}
        //Log it
        System.out.println("PINGFITRED5: Application shut down! ("+InstanceProperties.getInstancename()+")");
    }

    public static void initQuartz(ServletContext sc){
        //If there are errors in this code, check the org.quartz.ee.servlet.QuartzInitializerServlet
        //I grabbed this code from there instead of having the app server call it from web.xml
        //Potential problem with web.xml is that I may not add my listeners quickly enough.
        //
        Logger logger = Logger.getLogger(ApplicationStartup.class);
        logger.debug("Quartz Initializing");
        String QUARTZ_FACTORY_KEY = "org.quartz.impl.StdSchedulerFactory.KEY";
		StdSchedulerFactory factory;
		try {

			String configFile = null;
			if (configFile != null) {
				factory = new StdSchedulerFactory(configFile);
			} else {
				factory = new StdSchedulerFactory();
			}

			// Should the Scheduler being started now or later
			String startOnLoad = null;
			if (startOnLoad == null || (Boolean.valueOf(startOnLoad).booleanValue())) {
				// Start now
				scheduler = factory.getScheduler();
				scheduler.start();
				logger.debug("Quartz Scheduler has been started");
			} else {
				logger.debug("Quartz Scheduler has not been started - Use scheduler.start()");
			}

			logger.debug("Quartz Scheduler Factory stored in servlet context at key: " + QUARTZ_FACTORY_KEY);
			sc.setAttribute(QUARTZ_FACTORY_KEY, factory);

		} catch (Exception e) {
			logger.error("Quartz failed to initialize", e);
		}
    }




    public static boolean getIswabapprooddirdiscovered() {
        return iswabapprooddirdiscovered;
    }

    public static boolean getIshibernateinitialized() {
        return ishibernateinitialized;
    }

    public static boolean getIsdatabasereadyforapprun() {
        return isdatabasereadyforapprun;
    }

    public static boolean getIsappstarted() {
        return isappstarted;
    }

    public static void printBug(){
        StringBuffer out = new StringBuffer();
        out.append("\n\n\n"+"                                .::                       .\n" +
                "                               :::::::....                `::\n" +
                "                          .::::::::::::::::::..::::::::::::::.\n" +
                "                   .:  .::::::::::::::::::::::::::::::::::::::::\n" +
                "                  ::::::::::::::::::::::::::::::::::::::::::::::::\n" +
                "                  `:::::::::::::::``::::::::::: `::::::::::::::::::\n" +
                "                   ::::::::::'zc$$$b`:',cc,`:::' :''``,c=`:::::::::::'\n" +
                "                  ::::::::'::: $$$$$$$$$$$$$c,,u,zd$$$$$c,',zc,`::::'\n" +
                "                  ::::::'z$ccd$$$$$P\" . \"$$$$$$$$$$$$.. `?$$$$\".::::\n" +
                "                 :::::::dP?$$$$$$$\" d$$$$$$$$$$$$$$$$$$$b.\"$$L,`:::\n" +
                "               :::::::::.::$$$$$$ z$$$$$$$$$$$$$$$$$$$$$$$c\"$$$$,::..\n" +
                "                `'::::::'`.$$$$$$$\"$F<$\"3$$$$$$$$$$$$$$?$$?$$P:..:''\n" +
                "                  `:::::`$$$$$$$\"?   .. ?\"$$$$$$$$$$r`\" \" <\"3$c`:\n" +
                "                    :::::.\"?:`$$F    d$$.<$$$$$$$$P    4$c $\"\"\"/\n" +
                "                     :::::::::J$ .,,$$$$P $$$$$$$$>   ,$$$F  ::::\n" +
                "                  `:::'```'',d$$c`?$$$$P\"J$$$$$$$$.\"$$$$$P db`''\n" +
                "                      f,r4b4$$$$$c ,`\"\".-$$$$$$$$$$c \"\"\"\" c$$$ b\n" +
                "                      F ,$\"d$$$$$$$$c$bd$$$$$$$ $$$$b$bJ$L$$$P P\n" +
                "                            \"?$$$$$$$$$$$$$$$P??$$$$$$$$$$$P\" '\n" +
                "                               `\"\"\"???$PFFF\"\"    \"\"\"\"\"\"\"\"\n" +
                "                            4$$$$$$cdccccc$$$bcc$$$$$$$$$$$$bc\n" +
                "                           d$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$bc\n" +
                "                          $$$$$$$$$$$??????$$$FF?????P 3$$$$$$$$$$c.\n" +
                "                         d$$$$$$$$P':::::::`?'::::::::   \"?$$$$$$$$$$c.\n" +
                "                        4$$$$$P  $ :::::::::::::::::::      \"?$$$$$$$$$c\n" +
                "                        $$$$$P  `$.::::::::::::::::::          `\"\".$$$$$c\n" +
                "                      .$$$$$$'   `$-`::::::::::::::'             c$$$$$$$\n" +
                "                     z$$$$$$'      ::::::::::::::::            .$$$$$$$$\n" +
                "                   .d$$$$$$'       `:::::::::::::::           .$$$$$$$F'\n" +
                "                  d$$$$$$$'         `::::::::::::::          ,$$$$$$P\"\n" +
                "                .$$$$$$$F            ::::::::::::::: 3c,  . 4$$$$P\"'\n" +
                "               z$$$$$$$\"           :::::::::::::: ==$$$$$c % \"?\"\n" +
                "              z$$$$$$\"           .::::::::::::::::..:3$$$$P L\n" +
                "              `?$$$\"            ::::::::::::::::::::.?::: . $\n" +
                "   .,,,,ccc$L ? \"\"             :::::::::::::::::::::::::::.c%\n" +
                " :$$ ?=?P$$$%                 ::::::::::::::::::::::::::::\n" +
                ":\"???$%==\"\"  \"d               :::::::::::::::::::::::::::::    .\n" +
                "                             `:::::::::::::::::::::::::::::::::::\n" +
                "                              ::::::::::::::::::::::::::::::::::\n" +
                "                         :   :::::::::::::::::````,,,,zcc`''''`\n" +
                "                         :::::::::::'zc,,,,,cd$$ `???\"\"\"\"\n" +
                "                         `::::::::',d$$$$$$$$$$$      =\n" +
                "                           `````` $$$$$$$$$$$$$$ $$$$$c$F\n" +
                "                                  `$$$$$$$$$$$$$ $$$$$$F\n" +
                "                                   `$$$$$$$$$$$F<$$$$$$\n" +
                "                                    `$$$$$$$$$$'$$$$$$F\n" +
                "                                     4$$$$$$$$F<$$$$$P\n" +
                "                                    z$$$$$$$$F $$$$$P\n" +
                "                                   c$$$$$$$$$ J$$$$$F\n" +
                "                                 .$$$$$$$$$$\"z$$$$$$\n" +
                "                                 $$$$$$$$$P c$$$$$$$\n" +
                "                                $$$$$$$$$\" 4$$$$$$$F\n" +
                "                               d$$$$$$$$\"  $$$$$$$$'\n" +
                "                              .$$$$$$$P    4$$$$$$$\n" +
                "                              J$$$$$$\"      $$$$$$F\n" +
                "                              $$$$$P        ?$$$$$\n" +
                "                             d$$$$F         <$$$$F\n" +
                "                             $$$P\"           $$$P\n" +
                "                            J$$P            <$$$'\n" +
                "                           .$$P             $$$$\n" +
                "                          .$$$             `$$$L\n" +
                "                         z$$$F            / \"$$$\n" +
                "                        $$$$%/           ( ;. \"?.\n" +
                "                       ;\"?\"\">            ``  `.  \\\n" +
                "                       \\.- `                   `--'" +
                "          ;                            ' "+"\n\n\n\n");
        out.append("PingFitRed5: NEW AND IMPROVED... NOW WITH MORE BUGS!"+"\n\n\n");

        System.out.print(out.toString());
    }


}
