package com.pingfit.email;

import com.pingfit.util.*;
import com.pingfit.systemprops.SystemProperty;
import com.pingfit.systemprops.WebAppRootDir;
import com.pingfit.systemprops.BaseUrl;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

/**
 * User: Joe Reger Jr
 * Date: Sep 10, 2006
 * Time: 11:31:16 AM
 */
public class EmailTemplateProcessor {

    public static void sendGenericEmail(String toaddress, String subject, String body){
        String emailTemplateFilenameWithoutExtension = "generic";
        String htmlTemplate = Io.textFileRead(WebAppRootDir.getWebAppRootPath() + "emailtemplates" + java.io.File.separator + emailTemplateFilenameWithoutExtension + ".html").toString();
        String txtTemplate = Io.textFileRead(WebAppRootDir.getWebAppRootPath() + "emailtemplates" + java.io.File.separator + emailTemplateFilenameWithoutExtension + ".txt").toString();
        sendMail(subject, htmlTemplate, txtTemplate, body, toaddress, "");
    }



    public static void sendMail(String subject, String htmlTemplate, String txtTemplate, String body, String toaddress, String fromaddress){
        Logger logger = Logger.getLogger(EmailTemplateProcessor.class);
        String htmlEmailHeader = Io.textFileRead(WebAppRootDir.getWebAppRootPath() + "emailtemplates" + java.io.File.separator + "emailheader.html").toString();
        String htmlEmailFooter = Io.textFileRead(WebAppRootDir.getWebAppRootPath() + "emailtemplates" + java.io.File.separator + "emailfooter.html").toString();

        try{
            HtmlEmail email = new HtmlEmail();
            boolean havetoaddress=false;
            if (toaddress!=null && !toaddress.equals("")){
                email.addTo(toaddress);
                havetoaddress = true;
            }
            if (fromaddress!=null && !fromaddress.equals("")){
                email.setFrom(fromaddress, fromaddress);
            } else {
                email.setFrom(EmailSendThread.DEFAULTFROM, "The PingFit Server");
            }
            email.setSubject(subject);
            email.setTextMsg(body);
            if (havetoaddress){
                EmailSend.sendMail(email);
            }
        } catch (Exception e){
            logger.error("", e);
        }
    }





}
