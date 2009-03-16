package com.pingfit.util;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.InputStream;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

public class ErrorDissect {

	public static String dissect(Throwable throwable, HttpServletRequest request, String message) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(dissect(throwable, true, message));
		buffer.append(ServletUtilsdissect(request));
		return buffer.toString();
	}

	public static String dissect(Throwable throwable) {
		return dissect(throwable, true, "");
	}

	public static String dissect(Throwable throwable, boolean includeStackTrace, String custommessage) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("<table cellpadding=0 cellspacing=0 border=1 width=100%>");

		if (throwable == null) {
		    buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
            buffer.append("<font face=arial size=-1>");
            buffer.append("<b>No Throwable (or Exception) provided</b>");
            buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");
            buffer.append("</table>");
			return buffer.toString();
		}

		buffer.append("<tr>");
	    buffer.append("<td valign=top align=left bgcolor=#ffcc00>");
		buffer.append("<font face=arial size=-1>");
		buffer.append("<b>");
		buffer.append(throwable.toString());
		buffer.append("<b>");
		buffer.append("</font>");
		buffer.append("</td>");
		buffer.append("</tr>");
		buffer.append("<tr>");
	    buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
		buffer.append("<font face=arial size=-2>");
		buffer.append("throwable class name: ").append(throwable.getClass().getName()).append("<br>");
		buffer.append("toString(): ").append(throwable.toString()).append("<br>");
		if (throwable.getMessage() != null) {
			buffer.append("getMessage(): ").append(throwable.getMessage()).append("<br>");
		}
		if (custommessage != null && !custommessage.equals("")) {
			buffer.append("custom message: ").append(custommessage).append("<br>");
		}
		buffer.append("</font>");
		buffer.append("</td>");
		buffer.append("</tr>");
		//
		// Here, include Exception-specific diagnostics
		//
		if (throwable instanceof SAXParseException) {
		    buffer.append("<tr>");
	        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
		    buffer.append("<font face=arial size=-1>");
		    buffer.append("<b>SAXParseException</b>");
		    buffer.append("</font>");
		    buffer.append("</td>");
		    buffer.append("</tr>");
		    buffer.append("<tr>");
	        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
		    buffer.append("<font face=arial size=-2>");
			SAXParseException spe = (SAXParseException)throwable;
			buffer.append("Document (line,col) where exception occurred: (" + spe.getLineNumber() + "," + spe.getColumnNumber() + ")\n");
			buffer.append("Document PublicId: ").append(spe.getPublicId()).append("<br>");
			buffer.append("Document SystemId: ").append(spe.getSystemId()).append("<br>");
			buffer.append("</font>");
		    buffer.append("</td>");
		    buffer.append("</tr>");
		}
		//
		// Dump the stack trace, if requested
		//
		if (includeStackTrace == true) {
		    buffer.append("<tr>");
	        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
		    buffer.append("<font face=arial size=-1>");
		    buffer.append("<b>Stack Trace<b>");
		    buffer.append("</font>");
		    buffer.append("</td>");
		    buffer.append("</tr>");

			buffer.append(getStackTraceAsString(throwable));

		}
		//
		// Here, trace through the "inner" or "root" causes. There are lots of ways to embed a
		// "root cause" in an exception; go through all the possibilities here and recurse as needed
		if (throwable instanceof SAXException) {
			Throwable inner = ((SAXException)throwable).getException();
			if (inner != null) {
			    buffer.append("<tr>");
                buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
                buffer.append("<font face=arial size=-1>");
                buffer.append("<b>The preceeding exception was caused by the following one<b>");
                buffer.append("</font>");
                buffer.append("</td>");
                buffer.append("</tr>");
                buffer.append("<tr>");
                buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
                buffer.append("<font face=arial size=-1>");
                buffer.append(dissect(inner, includeStackTrace, ""));
                buffer.append("</font>");
                buffer.append("</td>");
                buffer.append("</tr>");
			}
		}
		if (throwable instanceof ServletException) {
			Throwable inner = ((ServletException)throwable).getRootCause();
			if (inner != null) {
				buffer.append("<tr>");
                buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
                buffer.append("<font face=arial size=-1>");
                buffer.append("<b>The preceeding exception was caused by the following one<b>");
                buffer.append("</font>");
                buffer.append("</td>");
                buffer.append("</tr>");
                buffer.append("<tr>");
                buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
                buffer.append("<font face=arial size=-1>");
                buffer.append(dissect(inner, includeStackTrace, ""));
                buffer.append("</font>");
                buffer.append("</td>");
                buffer.append("</tr>");
			}
		} else if (throwable instanceof RemoteException) {
			Throwable inner = ((RemoteException)throwable).detail;
			if (inner != null) {
				buffer.append("<tr>");
                buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
                buffer.append("<font face=arial size=-1>");
                buffer.append("<b>The preceeding exception was caused by the following one<b>");
                buffer.append("</font>");
                buffer.append("</td>");
                buffer.append("</tr>");
                buffer.append("<tr>");
                buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
                buffer.append("<font face=arial size=-1>");
                buffer.append(dissect(inner, includeStackTrace, ""));
                buffer.append("</font>");
                buffer.append("</td>");
                buffer.append("</tr>");
			}
		}

		buffer.append("</table>");

		return buffer.toString();
	}

    

    public static String getStackTraceAsString(Throwable throwable) {


        StringBuffer buffer = new StringBuffer();


//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        PrintWriter writer = new PrintWriter(bytes, true);
//        throwable.printStackTrace(writer);
//        buffer.append("<tr>");
//        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
//        buffer.append("<font face=arial size=-1>");
//        buffer.append(bytes.toString());
//        buffer.append("</font>");
//        buffer.append("</td>");
//        buffer.append("</tr>");

        //Get the array of stacktraceelements
        StackTraceElement[] el = throwable.getStackTrace();

        for(int i=0; i<el.length; i++){
            buffer.append("<tr>");

            buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
            buffer.append("<font face=arial size=-2>");
            buffer.append(el[i].toString());
            buffer.append("</font>");
            buffer.append("</td>");

            buffer.append("</tr>");
        }



        return buffer.toString();
    }






    public static String ServletUtilsdissect(HttpServletRequest request) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("<table cellpadding=0 cellspacing=0 border=1 width=100%>");

		if (request == null) {
			buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
            buffer.append("<font face=arial size=-1>");
            buffer.append("<b>No HttpServletRequest provided</b>");
            buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");
            buffer.append("</table>");
            return buffer.toString();
		}
		Enumeration enumer = null;


		HttpSession session = request.getSession(false);
		if (session != null) {
			ServletContext servletContext = session.getServletContext();
			buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
            buffer.append("<font face=arial size=-1>");
            buffer.append("<b>Dump of Current HTTP Request</b>");
            buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");

		}

		buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
        buffer.append("<font face=arial size=-1>");
        buffer.append("<b>Parameters (Form and/or Querystring)</b>");
        buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

		buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
        buffer.append("<font face=arial size=-2>");
		buffer.append("Requested URL: ").append(request.getRequestURL().toString()).append("<br>");
		buffer.append("Unparsed QueryString: ").append(request.getQueryString()).append("<br>");
		enumer = request.getParameterNames();
		while (enumer.hasMoreElements()) {
			String name = (String)enumer.nextElement();
			String value = request.getParameter(name);
			buffer.append(name).append(": ").append(value).append("<br>");
		}
		buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
        buffer.append("<font face=arial size=-1>");
        buffer.append("<b>Request Attributes</b>");
        buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

		buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
        buffer.append("<font face=arial size=-2>");
		enumer = request.getAttributeNames();
		while (enumer.hasMoreElements()) {
			String name = (String)enumer.nextElement();
			Object value = request.getAttribute(name);
			buffer.append(name).append(": ").append(value).append("<br>");
		}
		buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
        buffer.append("<font face=arial size=-1>");
        buffer.append("<b>Other Details</b>");
        buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
        buffer.append("<font face=arial size=-2>");
        if (request.getServerName()!=null){
            buffer.append("request.getServerName()").append(": ").append(request.getServerName()).append("<br>");
        }
        if (request.getContextPath()!=null){
            buffer.append("request.getContextPath()").append(": ").append(request.getContextPath()).append("<br>");
        }
        if (request.getPathInfo()!=null){
            buffer.append("request.getPathInfo()").append(": ").append(request.getPathInfo()).append("<br>");
        }
        if (request.getPathTranslated()!=null){
            buffer.append("request.getServletPath()").append(": ").append(request.getPathTranslated()).append("<br>");
        }
        if (request.getRequestURI()!=null){
            buffer.append("request.getRequestURI()").append(": ").append(request.getRequestURI()).append("<br>");
        }
        if (request.getRequestURL()!=null){
            buffer.append("request.getRequestURL()").append(": ").append(request.getRequestURL()).append("<br>");
        }
        if (request.getQueryString()!=null){
            buffer.append("request.getQueryString()").append(": ").append(request.getQueryString()).append("<br>");
        }
        buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");


		if (session != null) {
		    buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
            buffer.append("<font face=arial size=-1>");
            buffer.append("<b>Session Attributes</b>");
            buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");

            buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
            buffer.append("<font face=arial size=-2>");
			buffer.append("Session ID: ").append(session.getId()).append("<br>");
			Enumeration valueEnum = session.getAttributeNames();
			while (valueEnum.hasMoreElements()) {
                String attributeName = (String) valueEnum.nextElement();
                buffer.append(attributeName).append(": ").append(session.getAttribute(attributeName)).append("<br>");
            }
			buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");
		}

		buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
        buffer.append("<font face=arial size=-1>");
        buffer.append("<b>HTTP Header contents</b>");
        buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
        buffer.append("<font face=arial size=-2>");
		buffer.append("Method: ").append(request.getMethod()).append("<br>");
		Enumeration e_hdr = request.getHeaderNames();
		String hdr_name = null, hdr_value = null;
		while (e_hdr.hasMoreElements()) {
			hdr_name = (String)e_hdr.nextElement();
			hdr_value = request.getHeader(hdr_name);
			buffer.append(hdr_name).append(": ").append(hdr_value).append("<br>");
		}
		buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");


		int contentLength = request.getContentLength();
		if (contentLength > 0) {
		    buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
            buffer.append("<font face=arial size=-1>");
            buffer.append("<b>HTTP Request Content</b>");
            buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");

            buffer.append("<tr>");
            buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
            buffer.append("<font face=arial size=-2>");
			buffer.append("length: ").append(contentLength).append("<br>");
			buffer.append("type: ").append(request.getContentType()).append("<br>");
			buffer.append("content:<br>");
			try {
				InputStream in = request.getInputStream();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] array = new byte[contentLength];
				int len;
				while ((len=in.read(array)) != -1) {
					bos.write(array, 0, len);
				}
				buffer.append(bos.toString("utf-8"));
			} catch (IOException e) {
				buffer.append("IOException occured while trying to read request content: " + e.getMessage());
			}
			buffer.append("</font>");
            buffer.append("</td>");
            buffer.append("</tr>");
		}



		buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=#eeeeee>");
        buffer.append("<font face=arial size=-1>");
        buffer.append("<b>Cookies</b>");
        buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td valign=top align=left bgcolor=\"#ffffff\">");
        buffer.append("<font face=arial size=-2>");
		Cookie[] cookies = request.getCookies();
		if (cookies!=null){
            for (int i = 0; i < cookies.length; i++) {
                buffer.append("Cookie Number " + (i+1) + "<br>");
                buffer.append("name : "+cookies[i].getName()+"<br>");
                buffer.append("value : "+cookies[i].getValue()+"<br>");
                buffer.append("path : "+cookies[i].getPath()+"<br>");
                buffer.append("domain : "+cookies[i].getDomain()+"<br>");
                buffer.append("maxage : "+cookies[i].getMaxAge()+"<br>");
                buffer.append("issecure : "+cookies[i].getSecure()+"<br>");
                buffer.append("version : "+cookies[i].getVersion()+"<br>");
                buffer.append("comment : "+cookies[i].getComment()+"<br>");
                buffer.append("<br><br>");
            }
        }
		buffer.append("</font>");
        buffer.append("</td>");
        buffer.append("</tr>");




		buffer.append("</table>");

		return buffer.toString();
	}







}

