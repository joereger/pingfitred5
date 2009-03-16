package com.pingfit.email;

import org.apache.log4j.Logger;
import org.apache.commons.mail.HtmlEmail;

//Sample Usage:

//    try{
//        HtmlEmail email = new HtmlEmail();
//        email.addTo(to);
//        email.setFrom(EmailSendThread.DEFAULTFROM);
//        email.setSubject(subject);
//        email.setHtmlMsg("<html>"+message+"</html>");
//        email.setTextMsg(message);
//
//        sendMail(email);
//    } catch (Exception e){
//        logger.error("", e);
//    }

//For HtmlEmail and image embedding see:
//http://jakarta.apache.org/commons/email/userguide.html

public class EmailSend {


    public static void sendMail(HtmlEmail htmlEmail){
        Logger logger = Logger.getLogger(EmailSend.class);
        try {
            //Kick off a thread to send the email
            EmailSendThread eThr = new EmailSendThread();
            eThr.htmlEmail = htmlEmail;
            eThr.startThread();
        }catch (Exception e) {
            logger.error("Error starting email thread.", e);
        }
    }

}
